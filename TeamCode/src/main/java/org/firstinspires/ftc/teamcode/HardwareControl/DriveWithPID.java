package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.PIDController;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

/**
 * Created by nicky on 11/22/17.
 */

public class DriveWithPID {

    private static final String TAG = "9773_DriveWithPID";
    private static final boolean DEBUG = true;

    // Useful objects
    private static SwerveController mySwerveController;
    private static Gyro myGyro;

    // Variables
    static double tempZeroPosition;

    private long flwEncoderZero = 0;
    private long frwEncoderZero = 0;
    private long blwEncoderZero = 0;
    private long brwEncoderZero = 0;

    private double gyroAngleZero = 0;

    //Constants
    private static final double wheelDiameter = 3;
    private static final double encoderTicksPerInch = (19.8 * 28) / (wheelDiameter * Math.PI);
    private double targetTicks;

    // INIT
    public DriveWithPID (SwerveController mySwerveController, Gyro myGyro) {
        this.mySwerveController = mySwerveController;
        this.mySwerveController.useFieldCentricOrientation = true;
        this.myGyro = myGyro;
    }

    // Actual driving funftions
    public void driveStraight( double speed, double angleDegrees, double robotOrientationDegrees, double distInches) throws InterruptedException {

        // Orient Robot
        turnRobot(robotOrientationDegrees);
        if (DEBUG) { Log.i(TAG, "Finished setting heading"); }


        //////// Drive until it has gone the right distance ////////

        // log the current position
        flwEncoderZero = mySwerveController.getFlwEncoderCount();
        frwEncoderZero = mySwerveController.getFrwEncoderCount();
        blwEncoderZero = mySwerveController.getBlwEncoderCount();
        brwEncoderZero = mySwerveController.getBrwEncoderCount();

        // Calculate target distance
        targetTicks = encoderTicksPerInch * distInches;
        if (DEBUG) { Log.i(TAG, "Target Ticks: " + targetTicks); }

        // Drive
        while (averageEncoderDist() < targetTicks) {
            // While the robot has not driven far enough
            mySwerveController.steerSwerve(false , speed, Math.toRadians(angleDegrees), 0, Math.toRadians(robotOrientationDegrees));
            mySwerveController.moveRobot(true);
            if (DEBUG) { Log.i(TAG, "Distance so far: " + averageEncoderDist()); }
        }

        //Stop the robot
        mySwerveController.pointModules(true, 0, 0, 0);
        mySwerveController.moveRobot(true);
        if (DEBUG) { Log.i(TAG, "Extra Distance: " + (Math.abs(averageEncoderDist()) - targetTicks)); }
    }

    public void turnRobot (double targetAngleDegrees) throws InterruptedException {

        Log.i(TAG, "Starting turn");

        final double targetAngleRadians = Math.toRadians(targetAngleDegrees);

        // Target turning speed is 90 degrees per second - 0.0015 radians per millisecond
        double rotationSpeed = 0.6;
        double rotationSpeedStep = 0.01;
        final double MIN_TURN_SPEED = 0.0018;

        double currentAngle;
        double lastAngle = myGyro.getHeading();
        double lastTime = System.currentTimeMillis() - 1000;
        double currentTime;

        while (Math.abs(setOnNegToPosPi((myGyro.getHeading()) - targetAngleRadians)) > 0.04) {

            // Calculate rotation speed
            currentTime = System.currentTimeMillis();
            currentAngle = myGyro.getHeading();

            Log.i(TAG, "Heading: " + currentAngle + "  Target Angle: " + targetAngleRadians + "  Difference: " + Math.abs(setOnNegToPosPi((myGyro.getHeading()) - targetAngleRadians)));

            final double speed = Math.abs(setOnNegToPosPi(currentAngle - lastAngle)) / (currentTime - lastTime);
            Log.i(TAG, " Curent Angle: " + currentAngle + "  Last angle: " + lastAngle + "  Difference: " + setOnNegToPosPi(currentAngle - lastAngle) );

            if (speed < 0.0015 && rotationSpeed < 0.9) {
//                rotationSpeed += rotationSpeedStep;
            }


            if (setOnNegToPosPi(targetAngleRadians - currentAngle) > 0) {
                mySwerveController.steerSwerve(true, 0, 0, rotationSpeed, -1);
            } else {
                mySwerveController.steerSwerve(true, 0, 0, -rotationSpeed, -1);
            }
            mySwerveController.moveRobot(false);

            Log.i(TAG, "speed: " + speed + "  Rotation speed: " + rotationSpeed);
            lastAngle = currentAngle;
            lastTime = currentTime;
        }

        mySwerveController.steerSwerve(true, 0, 0, 0, -1);
        mySwerveController.moveRobot(false);
    }


    // Helper functions
    private double averageEncoderDist() {
        long flwDist = Math.abs(mySwerveController.getFlwEncoderCount() - flwEncoderZero);
        long frwDist = Math.abs(mySwerveController.getFlwEncoderCount() - frwEncoderZero);
        long blwDist = Math.abs(mySwerveController.getFlwEncoderCount() - blwEncoderZero);
        long brwDist = Math.abs(mySwerveController.getFlwEncoderCount() - brwEncoderZero);

        return ((double)(flwDist + frwDist + blwDist + brwDist) / 4);
    }

    private double setOnNegToPosPi (double num) {
        while (num > Math.PI) {
            num -= 2*Math.PI;
        }
        while (num < -Math.PI) {
            num += 2*Math.PI;
        }
        return num;
    }

    private double setOnTwoPi(double num) {
        if (num < 0) {
            return num + 2*Math.PI;
        }
        if (num > 2*Math.PI) {
            return num - 2*Math.PI;
        }
        return num;
    }
}
