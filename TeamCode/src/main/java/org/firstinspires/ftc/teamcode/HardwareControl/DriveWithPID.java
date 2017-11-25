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

    // New objects
    private static PIDController drivePID;
    private static PIDController turnPID;

    // Variables
    static double tempZeroPosition;

    private long flwEncoderZero = 0;
    private long frwEncoderZero = 0;
    private long blwEncoderZero = 0;
    private long brwEncoderZero = 0;
    private LinearOpMode linearOpMode = new LinearOpMode() {
        @Override
        public void runOpMode() throws InterruptedException {

        }
    }

    private double gyroAngleZero = 0;

    //Constants
    private static final double wheelDiameter = 3;
    private static final double encoderTicksPerInch = (19.8 * 28) / (wheelDiameter * Math.PI);
    private double targetTicks;

    private static  SafeJsonReader myCoefficients;

    // INIT
    public DriveWithPID (SwerveController mySwerveController, Gyro myGyro) {
        this.mySwerveController = mySwerveController;
        this.myGyro = myGyro;


        myCoefficients = new SafeJsonReader("DrivePIDParameters");
        drivePID = new PIDController(myCoefficients.getDouble("DriveKp"), myCoefficients.getDouble("DriveKi"), myCoefficients.getDouble("DriveKd"));
        turnPID = new PIDController(myCoefficients.getDouble("TurnKp"), myCoefficients.getDouble("TurnKi"), myCoefficients.getDouble("TurnKd"));
    }

    // Actual driving funftions
    public void driveStraight(boolean isCartesian, double xMag, double yAngleInDegrees, double distInches) throws InterruptedException {

        // Convert angle to radians
        if (!isCartesian) {
            yAngleInDegrees = Math.toRadians(yAngleInDegrees);
        }

        // Point in the right direction
        do {
            mySwerveController.pointDirection(isCartesian, xMag, yAngleInDegrees, 0);
        } while (mySwerveController.getIsTurning());

        //////// Drive until it has gone the right distance ////////

        // log the current position
        flwEncoderZero = mySwerveController.getFlwEncoderCount();
        frwEncoderZero = mySwerveController.getFrwEncoderCount();
        blwEncoderZero = mySwerveController.getBlwEncoderCount();
        brwEncoderZero = mySwerveController.getBrwEncoderCount();

        gyroAngleZero = myGyro.getHeading();

        if (DEBUG) { Log.e(TAG, "Finished setting heading"); }
        if (DEBUG) { Log.e(TAG, "Starting driving"); }
        targetTicks = encoderTicksPerInch * distInches;
        while (Math.abs(averageEncoderDist() - targetTicks && linearOpMode.opModeIsActive())) {
            mySwerveController.pointDirection(isCartesian, xMag, yAngleInDegrees, drivePID.getPIDCorrection(setOnNegToPosPi(myGyro.getHeading() - gyroAngleZero)));
            mySwerveController.moveRobot();
            if (DEBUG) { Log.e(TAG, "Distance so far: " + averageEncoderDist()); }
        }
        //Stop the robot
        mySwerveController.pointDirection(true, 0, 0, 0);
        mySwerveController.moveRobot();

    }

    // Helper functions
    private int averageEncoderDist() {
        long flwDist = Math.abs(mySwerveController.getFlwEncoderCount() - flwEncoderZero);
        long frwDist = Math.abs(mySwerveController.getFlwEncoderCount() - frwEncoderZero);
        long blwDist = Math.abs(mySwerveController.getFlwEncoderCount() - blwEncoderZero);
        long brwDist = Math.abs(mySwerveController.getFlwEncoderCount() - brwEncoderZero);

        return (int) (flwDist + frwDist + blwDist + brwDist) / 4;
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
