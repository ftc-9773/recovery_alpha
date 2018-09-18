package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.resources.ButtonStatus;
import org.firstinspires.ftc.teamcode.resources.Vector;

/**
 * Created by Vikesh on 10/28/2017.
 * Modified by Nicky on 11/7/2017
 */

/** To change PID parameters, go to swervePIDCoefficients.json under the JSON package and change the value
 *
 * To go to the dirrectory:
 * cd TeamCode/src/main/java/org/firstinspires/ftc/teamcode/JSON/
 *
 * To push the file:
 * ~/Library/Android/sdk/platform-tools/adb push swervePIDCoefficients.json /sdcard/FIRST/team9773/json18/
 *
 * To pull the file:
 *
 */


public class SwerveModule {

    public DcMotor swerveMotor;
    private CRServo swerveServo;
    private double zeroPosition;
    private AnalogInput swerveAbsEncoder;

    private Vector velocityVector = new Vector(true, 0, 0);
    private boolean motorIsForward;
    public double errorAmt;
    public double tellServo;


    private SafeJsonReader myJsonCoefficients;
    private SafeJsonReader myJsonZeroPosition;


    private String TAG = "ftc9773 SwerveModule ";
    private boolean debugHere = true;
    private final boolean DEBUG = false;

    // Motor freeze!
    private ButtonStatus freezeMotors = new ButtonStatus();

    //    PID / Error scaling STUFF   //

    // Variables
    private double proportionalCorrection;
    private double differentialCorrection;

    private long lastTime = -1;
    private double lastError;
    private long dt = 1;

    private double Kp;
    private double Ke;
    private double Kd;

    private double maxPower;
    private double isTurningThreshold;

    /*
    // Step Function constants
    private double A1; // 0.03
    private double A2; // 0.09
    private double A3; // 0.17
    private double P0; // 0.0
    private double P1; // -0.1
    private double P2; // -0.2
    private double P3; // -0.5
*/


    // For outside access
    private boolean isTurning = false;
    private double currentPosition = 0;



    //INIT
    public SwerveModule(HardwareMap hwMap, String hardwareMapTag) {
        if (hardwareMapTag == "flw") { debugHere = true; }

        TAG += hardwareMapTag;
        // Pass the hardware map

        // Set the electronics
        swerveServo = hwMap.crservo.get(hardwareMapTag + "Servo");
        swerveMotor = hwMap.dcMotor.get(hardwareMapTag + "Motor");

        //Set up motor parameters
        swerveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        swerveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        swerveMotor.setDirection(DcMotor.Direction.FORWARD);


        swerveAbsEncoder = hwMap.analogInput.get(hardwareMapTag + "AbsEncoder");

        //builds the json reader
        myJsonCoefficients = new SafeJsonReader("swervePIDCoefficients");

        // Gets coefficients for PID
        Kp = myJsonCoefficients.getDouble("Kp");
        Ke = myJsonCoefficients.getDouble("Ke");
        Kd = myJsonCoefficients.getDouble("Kd");
        isTurningThreshold = myJsonCoefficients.getDouble("isTurningThreshold");

        maxPower = myJsonCoefficients.getDouble("maxPower");
        /* Gets coefficients for step function
        A1 = myJsonCoefficients.getDouble("A1");
        A2 = myJsonCoefficients.getDouble("A2");
        A3 = myJsonCoefficients.getDouble("A3");
        P0 = myJsonCoefficients.getDouble("P0");
        P1 = myJsonCoefficients.getDouble("P1");
        P2 = myJsonCoefficients.getDouble("P2");
        P3 = myJsonCoefficients.getDouble("P3");
        */

        // Sets zero position
        myJsonZeroPosition = new SafeJsonReader("SwerveModuleZeroPositions");
        zeroPosition = myJsonZeroPosition.getDouble(hardwareMapTag + "StraightPosition");

    }


    // Private Functions:
    private double setOnTwoPI (double input) {
        while (input > 2 * Math.PI) {
            input -= 2 * Math.PI;
        }
        while (input < 0) {
            input += 2 * Math.PI;
        }
        return input;
    }

    private double setOnNegPosPI (double input) {
        while (input > Math.PI) {
            input -= 2 * Math.PI;
        }
        while (input < -1 * Math.PI) {
            input += 2 * Math.PI;
        }
        return input;
    }

    //////// PID //////////// Error scaling etc.
    // input is error
    private double calculatePDCorrection(double input) {

        //PID

        //Proportional
        proportionalCorrection = Math.pow(Math.abs(input), Ke) * Kp;
        if (errorAmt < 0) {
            proportionalCorrection *= -1;
        }

        //Differential
        if (lastTime > 0) {
            dt = System.currentTimeMillis() - lastTime;
            differentialCorrection = Kd * (input - lastError) / dt;
        } else {
            differentialCorrection = 0;
        }

        // Update last time and error
        lastTime = System.currentTimeMillis();
        lastError = input;

        Log.d(TAG,"Pe correction: " +  proportionalCorrection);
        Log.d(TAG,"D correction: " +  differentialCorrection);


        if (DEBUG) { Log.d(TAG, "Prop: " + proportionalCorrection + "   Dif:" + differentialCorrection); }


        return proportionalCorrection + differentialCorrection;

        //Step Function
        /*
        if (input < -A3) {
            return 0.5 - P3;
        } else if (input < -A2) {
            return 0.5 - P2;
        } else if (input < -A1) {
            return 0.5 - P1;
        } else if (input < 0) {
            return 0.5 - P0;
        } else if (input < A1) {
            return 0.5 + P0;
        } else if (input < A2) {
            return 0.5 + P1;
        } else if (input < A3) {
            return 0.5 + P2;
        } else {
            return 0.5 + P3;
        }
        */
    }


    /////// For Outside Control ////////

    // Writes the module direction
    public void setVector(Vector newVector) {

        // Finds the current position
        currentPosition = setOnTwoPI(2*Math.PI * (1 - swerveAbsEncoder.getVoltage()/3.23) - zeroPosition);

        //Calculates distance to move on -pi to pi
        final double forwardError = setOnNegPosPI(newVector.getAngle() - currentPosition);
        final double backwardsError = setOnNegPosPI(newVector.getAngle() - currentPosition - Math.PI);

        if (Math.abs(forwardError) < Math.abs(backwardsError)) {
            velocityVector.set(true, newVector.getX(), newVector.getY());
            motorIsForward = true;
            errorAmt = forwardError;
        } else {
            velocityVector.set(true, -newVector.getX(), -newVector.getY());
            motorIsForward = false;
            errorAmt = backwardsError;
        }

        //Log.i(TAG, "NewVector: " + newVector.getAngle() + "velocityVector" + velocityVector.getAngle());

    }


    // Rotates the Module
    public void pointModule() {
        // ** Figure out how to move the servo **

        if (velocityVector.getMagnitude() > 0) {

            //Calculate PID
            tellServo = calculatePDCorrection(errorAmt);

            //Correct onto servo's range
            if (tellServo > maxPower) {
                tellServo = maxPower;
            } else if (tellServo < -maxPower) {
                tellServo = -maxPower;
            }else if(Double.isNaN(tellServo)){
                tellServo = 0;
            }

        } else {
            tellServo = 0;
        }
        //Log.i(TAG, "Desired Position: " + velocityVector.getAngle()/Math.PI + "pi,   Error Amt: " + errorAmt + ",   Tell servo is: " + tellServo);
        swerveServo.setPower(tellServo);
/*
        if (setOnTwoPI(currentPosition - lastPosition) > isTurningThreshold) {
            isTurning = true;
        } else {
            isTurning = false;
        }
*/

        if (Math.abs(errorAmt) / dt > isTurningThreshold) {
            isTurning = true;
            //swerveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        } else {
            isTurning = false;
            //swerveMotor.setZeroPowerBehavior((DcMotor.ZeroPowerBehavior.BRAKE));
        }


        //Log.i(TAG, "AbsEncoder positional difference: " + setOnTwoPI(currentPosition - lastPosition));
    }


    // Drives the wheel
    public void driveModule() {

        // Check to see if we can freeze the motors
        freezeMotors.recordNewValue(velocityVector.getMagnitude() == 0 && !isTurning);

        // If it can, switch motors to RUN_TO_POSITION and set it to the current position
        if (freezeMotors.isJustOn()) {
            swerveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            swerveMotor.setTargetPosition(swerveMotor.getCurrentPosition());

        // If it just switched to off, switch to run using  encoder
        } else if (freezeMotors.isJustOff()) {
            swerveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        // If the motors aren't frozen, just run normally
        if (freezeMotors.isOff()) {
            if (motorIsForward) {
                swerveMotor.setPower(velocityVector.getMagnitude());
            } else {
                swerveMotor.setPower(velocityVector.getMagnitude() * -1);
            }
        }

    }

    public boolean getIsTurning() {
        return isTurning;
    }
    public double getModulePosition(){
        return currentPosition;
    }
    public long getEncoderCount() {
        return swerveMotor.getCurrentPosition();
    }
    public double getErrorAmt (){return errorAmt;}
    public boolean isPivoting() { return isTurning && velocityVector.getMagnitude() == 0;}
}
