package org.firstinspires.ftc.teamcode.AutonomousDriving;

import android.util.Log;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.HardwareControl.DistanceColorSensor;
import org.firstinspires.ftc.teamcode.HardwareControl.IntakeControllerManual;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.PIDController;
import org.firstinspires.ftc.teamcode.infrastructure.PIDWithBaseValue;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

/**
 * Created by nicky on 11/22/17.
 */

public class DriveWithPID {

    private static final String TAG = "9773_DriveWithPID";
    private static final boolean DEBUG = false;

    // Useful objects
    private static SwerveController mySwerveController;
    private static Gyro myGyro;

    private static IntakeControllerManual myIntakeController;

    // Variables
    static double tempZeroPosition;

    private long flwEncoderZero = 0;
    private long frwEncoderZero = 0;
    private long blwEncoderZero = 0;
    private long brwEncoderZero = 0;

    private LinearOpModeCamera myOpMode;

    private double gyroAngleZero = 0;

    //Constants
    private static final double wheelDiameter = 3;
    private static final double encoderTicksPerInch = (19.8 * 28) / (wheelDiameter * Math.PI);
    private double targetTicks;


    // Turning PID
    PIDController turningPID;
    private double baseSpeed;
    private double errorThreshold;
    private double speedThreshold;


    // Sensors:
    DistanceColorSensor backSensor;
    DistanceColorSensor frontColorSensor;

    ModernRoboticsI2cRangeSensor ultrasonicSensor;


    // INIT
    public DriveWithPID (SwerveController mySwerveController, Gyro myGyro, IntakeControllerManual myIntakeController, LinearOpModeCamera myOpMode, HardwareMap hwMap) {
        this.myOpMode = myOpMode;
        this.mySwerveController = mySwerveController;
        this.mySwerveController.useFieldCentricOrientation = true;
        this.myGyro = myGyro;
        this.myIntakeController = myIntakeController;

        // Make the turning pid
        SafeJsonReader turningPIDCoefficients = new SafeJsonReader("TurningPIDCoefficients");
        double Kp = turningPIDCoefficients.getDouble("Kp");
        double Ki = turningPIDCoefficients.getDouble("Ki");
        double Kd = turningPIDCoefficients.getDouble("Kd");
        baseSpeed = turningPIDCoefficients.getDouble("min");
        turningPID = new PIDController(Kp, Ki, Kd);
        errorThreshold = turningPIDCoefficients.getDouble("errorThreshold");
        speedThreshold = turningPIDCoefficients.getDouble("speedThreshold");

        backSensor = hwMap.i2cDevice.get("backColorSensor");

    }

    // Actual driving funftions

    // Driving
    public void driveDist(double speed, double angleDegrees, double distInches, double headingDegrees) throws InterruptedException {

        // Orient Robot
//        turnRobot(robotOrientationDegrees);
//        if (DEBUG) { Log.i(TAG, "Finished setting heading"); }


        //////// Drive until it has gone the right distance ////////

        // Zero the encoders
        zeroEncoders();

        // Calculate target distance
        double targetTicks = encoderTicksPerInch * distInches;
        if (DEBUG) { Log.i(TAG, "Target Ticks: " + targetTicks); }

        // Drive
        while (!myOpMode.isStopRequested() && averageEncoderDist() < targetTicks) {
            // While the robot has not driven far enough
            mySwerveController.steerSwerve(false , speed, Math.toRadians(angleDegrees), 0, headingDegrees);
            mySwerveController.moveRobot(true);
            if (DEBUG) { Log.i(TAG, "Distance so far: " + averageEncoderDist()); }
        }

        //Stop the robot
        mySwerveController.pointModules(true, 0, 0, 0);
        mySwerveController.moveRobot(false);
        if (DEBUG) { Log.i(TAG, "Extra Distance: " + (Math.abs(averageEncoderDist()) - targetTicks)); }
    }

    public void driveIntake (double speed, double angleDegrees, double maxDistInches, double headingDegrees, double intakePower) {

        // Calculate target distance
        double maxTicks = encoderTicksPerInch * maxDistInches;

        while (!myOpMode.isStopRequested()) {
            mySwerveController.steerSwerve(false, speed, Math.toRadians(angleDegrees), 0, headingDegrees);
            mySwerveController.moveRobot(true);

            if (averageEncoderDist() >= targetTicks) break;
            if ()
        }

    }

    //Alais
    public void driveDist(double speed, double angleDegrees, double distInches) throws InterruptedException {
        driveDist(speed, angleDegrees, distInches, -1);
    }

    // Drive for time
    public void driveTime(double speed, double angleDegrees, double timeSeconds) {

        // Point modules
        double zeroTime = System.currentTimeMillis();

        // Drive
        while (System.currentTimeMillis() - zeroTime < timeSeconds * 1000) {
            mySwerveController.steerSwerve(false, speed, Math.toRadians(angleDegrees), 0, -1);
            mySwerveController.moveRobot(true);
        }

    }

    public void driveByLeftUltraonicDis (double speed, double targetUltrasonicDist, double distForward) {

    }

    // Turn the Robot
    public void turnRobot (double targetAngleDegrees) {
        final double targetAngleRad = Math.toRadians(targetAngleDegrees);

        // For calculating rotational speed:
        double lastHeading;
        double currentHeading = myGyro.getHeading();

        double lastTime;
        double currentTime = System.currentTimeMillis();

        // For turning PID
        double error;

        boolean firstTime = true;
        while (!myOpMode.isStopRequested()) {

            // update time and headings:
            lastHeading = currentHeading;
            currentHeading = myGyro.getHeading();

            lastTime = currentTime;
            currentTime = System.currentTimeMillis();

            error = setOnNegToPosPi(targetAngleRad - currentHeading);

            double rotation = turningPID.getPIDCorrection(error);
            Log.e("Error: ", "" + error);

            Log.e("First Rotation: ", "" + rotation);

            if (rotation > 0) {
                rotation += baseSpeed;
            } else if (rotation < 0) {
                rotation -= baseSpeed;
            }
            Log.e("Second Rotation: ", "" + rotation);

            mySwerveController.steerSwerve(true,0,0, rotation, -1);
            boolean log = mySwerveController.moveRobot(true);



            // Check to see if it's time to exit
            // Calculate speed
            double speed;
            if (currentTime == lastTime || firstTime) {
                speed = 0.003;
            } else {
                speed = Math.abs(error) / (currentTime - lastTime);
            }

            if (speed < speedThreshold && error < errorThreshold) {
                break;
            }

            firstTime = false;
        }
        mySwerveController.stopRobot();
    }

    // Helper functions
    private double averageEncoderDist() {
        long flwDist = Math.abs(mySwerveController.getFlwEncoderCount() - flwEncoderZero);
        long frwDist = Math.abs(mySwerveController.getFrwEncoderCount() - frwEncoderZero);
        long blwDist = Math.abs(mySwerveController.getBlwEncoderCount() - blwEncoderZero);
        long brwDist = Math.abs(mySwerveController.getBrwEncoderCount() - brwEncoderZero);

        return ((double)(flwDist + frwDist + blwDist + brwDist) / 4);
    }

    private void zeroEncoders() {
        flwEncoderZero = mySwerveController.getFlwEncoderCount();
        frwEncoderZero = mySwerveController.getFrwEncoderCount();
        blwEncoderZero = mySwerveController.getBlwEncoderCount();
        brwEncoderZero = mySwerveController.getBrwEncoderCount();
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
