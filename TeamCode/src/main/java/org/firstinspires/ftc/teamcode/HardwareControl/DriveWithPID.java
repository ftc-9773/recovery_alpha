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
        this.myGyro = myGyro;
    }

    // Actual driving funftions
    public void driveStraight(boolean isCartesian, double xMag, double yAngleDegrees, double robotOrientationDegrees, double distInches) throws InterruptedException {

        double yAngleRadians;
        if (isCartesian) {
            yAngleRadians = Math.toRadians(yAngleDegrees);
        }else{
            yAngleRadians = yAngleDegrees;
        }

        // Orient the robot correctly
        do {
            mySwerveController.pointModules(false, 0, 0, 1);
        } while (mySwerveController.getIsTurning());

        double rotation;
        do {
            rotation = mySwerveController.steerSwerve(true, 0, 0, 0, Math.toRadians(robotOrientationDegrees));
            if (DEBUG){ Log.i(TAG, "Rotation is: " + rotation); }
        } while (rotation > 0.05);

        if (DEBUG) { Log.i(TAG, "Reached pointing modules for driving"); }


        // Point in the right direction
        do {
            mySwerveController.pointModules(isCartesian, xMag, yAngleRadians, 0);
        } while (mySwerveController.getIsTurning());

        if (DEBUG) { Log.i(TAG, "Finished setting heading"); }

        //////// Drive until it has gone the right distance ////////

        // log the current position
        flwEncoderZero = mySwerveController.getFlwEncoderCount();
        frwEncoderZero = mySwerveController.getFrwEncoderCount();
        blwEncoderZero = mySwerveController.getBlwEncoderCount();
        brwEncoderZero = mySwerveController.getBrwEncoderCount();

        targetTicks = encoderTicksPerInch * distInches;
        if (DEBUG) { Log.i(TAG, "Target Ticks: " + targetTicks); }

        while (Math.abs(averageEncoderDist()) < targetTicks) {

            // While the robot has not driven far enough
            mySwerveController.steerSwerve(isCartesian, xMag, yAngleRadians, 0, robotOrientationDegrees);
            mySwerveController.moveRobot();

            if (DEBUG) { Log.i(TAG, "Distance so far: " + averageEncoderDist()); }
        }

        //Stop the robot
        mySwerveController.pointModules(true, 0, 0, 0);
        mySwerveController.moveRobot();
        if (DEBUG) { Log.i(TAG, "Extra Distance: " + (Math.abs(averageEncoderDist()) - targetTicks)); }
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
            num -= Math.PI;
        }
        while (num < Math.PI) {
            num += Math.PI;
        }
        return num;
    }
}
