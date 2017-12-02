package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
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

    private HardwareMap hwMap;
    public DcMotor swerveMotor;
    private Servo swerveServo;
    private double zeroPosition;
    private AnalogInput swerveAbsEncoder;

    private Vector velocityVector = new Vector(true, 0, 0);
    private boolean motorsAreForward;
    public double errorAmt;
    public double tellServo;


    private SafeJsonReader myJsonCoefficients;


    private static final String TAG = "ftc9773 SwerveModule";
    private boolean debugHere = true;
    private final boolean DEBUG = false;


    //    PID / Error scaling STUFF   //

    // Variables
    private double proportionalCorrection;
    private double differentialCorrection;

    private long lastTime = -1;
    private double lastError;

    private double Kp;
    private double Ke;
    private double Kd;

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
    public boolean isTurning = false;
    public double currentPosition = 0;



    //INIT
    public SwerveModule(HardwareMap hwMap, String hardwareMapTag) {
        Log.d(TAG, "Building servo " + hardwareMapTag);

        if (hardwareMapTag == "flw") { debugHere = true; }

        // Pass the hardware map
        this.hwMap = hwMap;

        // Set the electronics
        swerveServo = hwMap.servo.get(hardwareMapTag + "Servo");
        swerveMotor = hwMap.dcMotor.get(hardwareMapTag + "Motor");

        //Set up motor parameters
        swerveMotor.setDirection(DcMotor.Direction.FORWARD);


        swerveAbsEncoder = hwMap.analogInput.get(hardwareMapTag + "AbsEncoder");

        //builds the json reader
        myJsonCoefficients = new SafeJsonReader("swervePIDCoefficients");

        // Gets coefficients for PID
        Kp = myJsonCoefficients.getDouble("Kp");
        Ke = myJsonCoefficients.getDouble("Ke");
        Kd = myJsonCoefficients.getDouble("Kd");

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
        zeroPosition = myJsonCoefficients.getDouble(hardwareMapTag + "StraightPosition");
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
            differentialCorrection = Kd * (input - lastError) / (System.currentTimeMillis() - lastTime);
        } else {
            differentialCorrection = 0;
        }

        // Update last time and error
        lastTime = System.currentTimeMillis();
        lastError = input;

        if (DEBUG) { Log.d(TAG, "Prop: " + proportionalCorrection + "   Dif:" + differentialCorrection); }

        return 0.5 - proportionalCorrection - differentialCorrection;

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
    public void setVector(Vector newVector, boolean motorsAreForward) {

        this.motorsAreForward = motorsAreForward;

        if (motorsAreForward) {
            velocityVector.set(true, newVector.getX(), newVector.getY());
        } else {
            velocityVector.set(true, -newVector.getX(), -newVector.getY());
        }

        // Finds the current position
        currentPosition = setOnTwoPI(2*Math.PI * (1 - swerveAbsEncoder.getVoltage()/3.24) - zeroPosition);

        //Calculates distance to move on -pi to pi
        errorAmt = setOnNegPosPI(velocityVector.getAngle() - currentPosition);
    }

    // returns distance for servo to turn with motor as is
    public double distForwardDirection () {
        return Math.abs(errorAmt);
    }

    // returns distance for servos to turn with motor direction switched
    public double distReversedDirection () {

        return Math.abs(setOnTwoPI(errorAmt) - Math.PI);
    }


    // Rotates the Module
    public void pointModule() {
        // ** Figure out how to move the servo **

        if (velocityVector.getMagnitude() > 0) {

            //Calculate PID
            tellServo = calculatePDCorrection(errorAmt);

            //Correct onto servo's range
            if (tellServo > 1) {
                tellServo = 1;
            } else if (tellServo < 0) {
                tellServo = 0;
            }

        } else {
            tellServo = 0.5;
        }
        swerveServo.setPosition(tellServo);

        if (Math.abs(tellServo - 0.5) < 0.04) {
            isTurning = false;
        }  else {
            isTurning = true;
        }
    }


    // Drives the wheel
    public void driveModule() {
        if (motorsAreForward) {
            swerveMotor.setPower(velocityVector.getMagnitude());
        } else {
            swerveMotor.setPower(velocityVector.getMagnitude() * -1);
        }
    }

    public boolean getIsTurning() {
        return isTurning;
    }

    public long getEncoderCount() {
        return swerveMotor.getCurrentPosition();
    }
}
