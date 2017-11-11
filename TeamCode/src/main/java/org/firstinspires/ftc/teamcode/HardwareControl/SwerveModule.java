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
 */


public class SwerveModule {

    private HardwareMap hwMap;
    private DcMotor swerveMotor;
    private Servo swerveServo;
    private AnalogInput swerveAbsEncoder;
    private Vector velocityVector = new Vector(true, 0, 0);
    public double errorAmt;
    private double zeroPosition;
    public double tellServo;
    private SafeJsonReader myJsonCoefficients;

    private static final String TAG = "ftc9773 SwerveModule";
    private boolean debug = false;


    //    PID STUFF   //

    // Variables
    private double proportionalCorrection;
    private double differentialCorrection;

    private double lastTime = -1;
    private double lastError;

    // PID Constants
    private double Kp;
    private double Kpe;
    private double Kp2;
    private double Kd;



    // For outside access
    public boolean isTurning = false;
    public double currentPosition = 0;



    //INIT
    public SwerveModule(HardwareMap hwMap, String hardwareMapTag) {
        Log.e(TAG, "Building servo " + hardwareMapTag);

        if (hardwareMapTag == "flw") { debug = true; }

        // Pass the hardware map
        this.hwMap = hwMap;

        // Set the electronics
        swerveServo = hwMap.servo.get(hardwareMapTag + "Servo");
        swerveMotor = hwMap.dcMotor.get(hardwareMapTag + "Motor");
        swerveAbsEncoder = hwMap.analogInput.get(hardwareMapTag + "AbsEncoder");

        //builds the json reader
        myJsonCoefficients = new SafeJsonReader("swervePIDCoefficients");
        Kp = myJsonCoefficients.getDouble("Kp");
        Kpe = myJsonCoefficients.getDouble("Kpe");
        Kp2 = myJsonCoefficients.getDouble("Kp2");
        Kd = myJsonCoefficients.getDouble("Kd");

        // Sets zero position
        zeroPosition = myJsonCoefficients.getDouble(hardwareMapTag + "StraightPosition") * 2 * Math.PI;
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

    private double calculatePDCorrection(double input) {

        // Proportional error
        proportionalCorrection = input * Kp * Math.pow(Math.abs(input), Kpe) * input / Math.abs(input);

        // Differential error
        if (lastTime != 0)  {
            differentialCorrection = Kd * (input - lastError) / (System.currentTimeMillis() - lastTime) / 1000;
            // Differential correction = Constant * (change in error / change in time)
        }

        lastTime = System.currentTimeMillis();
        lastError = input;

        Log.e(TAG, "input: " + input);
        Log.e(TAG, "Proportional correction: " + proportionalCorrection);
        Log.e(TAG, "Output: " + 0.5 + proportionalCorrection + differentialCorrection);


        return 0.5 + proportionalCorrection + differentialCorrection;
    }



    // Writes the module direction
    public void setVector(Vector newVector){

        velocityVector.set(true, newVector.getX(), newVector.getY());

        // Finds the current position
        currentPosition = setOnTwoPI(swerveAbsEncoder.getVoltage() / 3.245 * 2 * Math.PI - zeroPosition);
        //if (debug) { Log.e(TAG, "Current Position" + currentPosition / Math.PI); }
        //if (debug) {Log.e(TAG, "Desired Position" + velocityVector.getAngle() / Math.PI); }

        //Calculates distance to move on -pi to pi
        errorAmt = setOnNegPosPI(velocityVector.getAngle() - currentPosition);
        //if (debug) { Log.e(TAG, "Error amount:" + errorAmt / Math.PI); }
    }

    // Rotates the Module
    public void pointModule() {
        // ** Figure out how to move the servo **
        if (debug) { Log.e(TAG, "Pointing Modules"); }

        if (velocityVector.getMagnitude() > 0) {
            if (debug) { Log.e(TAG, "Inside pid stuffing"); }
            //Calculate PID
            tellServo = calculatePDCorrection(errorAmt);
            if (debug) {
                //Log.e(TAG, "Servo Distance" + tellServo);
            }

            //Correct onto servo's range
            if (tellServo > 1) {
                tellServo = 1;
            } else if (tellServo < 0) {
                tellServo = 0;
            }

        } else {
            if (debug) { Log.e(TAG, "skipped stuffing"); }
            tellServo = 0.5;
        }

        swerveServo.setPosition(tellServo);
    }

    // Drives the wheel
    public void driveModule() {
        swerveMotor.setPower(velocityVector.getMagnitude());
    }

}
