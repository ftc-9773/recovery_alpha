package org.firstinspires.ftc.teamcode.AutonomousDriving;

import android.util.Log;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTrays;
import org.firstinspires.ftc.teamcode.HardwareControl.DistanceColorSensor;
import org.firstinspires.ftc.teamcode.HardwareControl.IntakeControllerManual;
import org.firstinspires.ftc.teamcode.HardwareControl.RelicSystem;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.PIDController;
import org.firstinspires.ftc.teamcode.infrastructure.PIDWithBaseValue;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.resources.Timer;
import org.firstinspires.ftc.teamcode.resources.Vector;

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
    private static CubeTrays myCubeTray;
    private static RelicSystem myRelicSystem;

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
    private static double distOffset;

    // Turning PID
    PIDController turningPID;
    PIDController drivingPID;
    private double baseSpeed;
    private double errorThreshold;
    private double speedThreshold;
    private double intakedelay;


    // Sensors:
    public DistanceColorSensor backColorSensor;
    public DistanceColorSensor frontColorSensor;

    public ModernRoboticsI2cRangeSensor leftUltrasonicSensor;
    public ModernRoboticsI2cRangeSensor rightUltrasonicSensor;


    // INIT
    public DriveWithPID (SwerveController mySwerveController, Gyro myGyro, IntakeControllerManual myIntakeController, LinearOpModeCamera myOpMode, CubeTrays myCubeTray, RelicSystem myRelicSystem, HardwareMap hwMap) {
        this.myOpMode = myOpMode;
        this.mySwerveController = mySwerveController;
        this.mySwerveController.useFieldCentricOrientation = true;
        this.myGyro = myGyro;
        this.myIntakeController = myIntakeController;
        this.myCubeTray = myCubeTray;
        this.myRelicSystem = myRelicSystem;

        // Make the turning pid
        SafeJsonReader turningPIDCoefficients = new SafeJsonReader("TurningPIDCoefficients");
        intakedelay = turningPIDCoefficients.getDouble("intakedelay");
        double Kp = turningPIDCoefficients.getDouble("Kp");
        double Ki = turningPIDCoefficients.getDouble("Ki");
        double Kd = turningPIDCoefficients.getDouble("Kd");
        baseSpeed = turningPIDCoefficients.getDouble("min");
        turningPID = new PIDController(Kp, Ki, Kd);
        errorThreshold = turningPIDCoefficients.getDouble("errorThreshold");
        speedThreshold = turningPIDCoefficients.getDouble("speedThreshold");

        double driveKp = turningPIDCoefficients.getDouble("driveKp");
        double driveKi = turningPIDCoefficients.getDouble("driveKi");
        double driveKd = turningPIDCoefficients.getDouble("driveKd");
        distOffset = turningPIDCoefficients.getDouble("distOffset");
        drivingPID = new PIDController(driveKp, driveKi, driveKd);

        // Sensors
        backColorSensor = new DistanceColorSensor(hwMap, "backColorSensor");
        frontColorSensor = new DistanceColorSensor(hwMap, "frontColorSensor");

        leftUltrasonicSensor = hwMap.get(ModernRoboticsI2cRangeSensor.class, "leftUltrasonicSensor");
        rightUltrasonicSensor = hwMap.get(ModernRoboticsI2cRangeSensor.class, "rightUltrasonicSensor");
    }

    public void driveDistDumb (double speed, double angleDegrees, double distInches, double headingDegrees) throws InterruptedException {
        //////// Drive until it has gone the right distance ////////

        // Zero the encoders
        zeroEncoders();

        // Calculate target distance
        double targetTicks = encoderTicksPerInch * distInches - distOffset;
        if (DEBUG) { Log.i(TAG, "Target Ticks: " + targetTicks); }

        // Drive
        while (!myOpMode.isStopRequested() && averageEncoderDist() < targetTicks) {
            // While the robot has not driven far enough

            mySwerveController.steerSwerve(false , speed, Math.toRadians(angleDegrees), 0, headingDegrees);
            mySwerveController.moveRobot(true);
            if (DEBUG) { Log.i(TAG, "Distance so far: " + averageEncoderDist()); }

            // Update Cube tray
            myCubeTray.updatePosition();
        }

        //Stop the robot
        mySwerveController.stopRobot();

    }
    // Actual driving funftions

    // Driving
    public void driveDist(double speed, double angleDegrees, double distInches, double headingDegrees) throws InterruptedException {

        //////// Drive until it has gone the right distance ////////

        // Zero the encoders
        zeroEncoders();

        // Calculate target distance
        double targetTicks = encoderTicksPerInch * distInches - distOffset;
        if (DEBUG) { Log.i(TAG, "Target Ticks: " + targetTicks); }

        // Drive
        while (!myOpMode.isStopRequested() && averageEncoderDist() < targetTicks) {
            // While the robot has not driven far enough

            final double error = (targetTicks - averageEncoderDist());
            double motorPower = drivingPID.getPIDCorrection(error);
            if (motorPower > speed) motorPower = speed;

            mySwerveController.steerSwerve(false , motorPower, Math.toRadians(angleDegrees), 0, headingDegrees);
            mySwerveController.moveRobot(true);
            if (DEBUG) { Log.i(TAG, "Distance so far: " + averageEncoderDist()); }

            // Update Cube tray
            myCubeTray.updatePosition();
        }

        //Stop the robot
        mySwerveController.stopRobot();
        if (DEBUG) { Log.i(TAG, "Extra Distance: " + (Math.abs(averageEncoderDist()) - targetTicks)); }
    }

    public void driveDistStopIntake(double speed, double angleDegrees, double distInches, double headingDegrees) throws InterruptedException {

        Timer myTimer = new Timer(60000);
        boolean wasCubeIn = false;
        myIntakeController.RunIntake(0, -1);
        //////// Drive until it has gone the right distance ////////

        // Zero the encoders
        zeroEncoders();

        // Calculate target distance
        double targetTicks = encoderTicksPerInch * distInches - distOffset;
        if (DEBUG) { Log.i(TAG, "Target Ticks: " + targetTicks); }

        // Drive
        while (!myOpMode.isStopRequested() && averageEncoderDist() < targetTicks) {
            // While the robot has not driven far enough

            // Drive
            final double error = (targetTicks - averageEncoderDist());
            double motorPower = drivingPID.getPIDCorrection(error);
            if (motorPower > speed) motorPower = speed;

            mySwerveController.steerSwerve(false , motorPower, Math.toRadians(angleDegrees), 0, headingDegrees);
            mySwerveController.moveRobot(true);

            if (DEBUG) { Log.i(TAG, "Distance so far: " + averageEncoderDist()); }

            if (backColorSensor.getDistance(DistanceUnit.INCH) < 2.35 && frontColorSensor.getDistance(DistanceUnit.INCH) < 2.35 && !wasCubeIn){
                wasCubeIn = true;
                myTimer = new Timer(intakedelay);
            }

            if(wasCubeIn && myTimer.isDone()){
                myIntakeController.RunIntake(0,0);
            }
            // Update Cube tray
            myCubeTray.updatePosition();
        }


        //Stop the robot
        mySwerveController.stopRobot();
        myIntakeController.RunIntake(0,0);
        if (DEBUG) { Log.i(TAG, "Extra Distance: " + (Math.abs(averageEncoderDist()) - targetTicks)); }
    }



    public void driveIntake (double speed, double angleDegrees, double maxDistInches, double headingDegrees, double intakePower, double backOffset) {


        // Calculate target distance
        zeroEncoders();
        double maxTicks = encoderTicksPerInch * maxDistInches;

        myIntakeController.RunIntake(0, -intakePower);

        Timer stopDelay = new Timer(6000);

        while (!myOpMode.isStopRequested() && averageEncoderDist() < maxTicks) {

            // Turn on intake

            mySwerveController.steerSwerve(false, speed, Math.toRadians(angleDegrees), 0, headingDegrees);
            mySwerveController.moveRobot(true);

            // Check Break conditions
            if (averageEncoderDist() >= maxTicks) break;
            if (backColorSensor.getDistance(DistanceUnit.INCH) < 2.35 && frontColorSensor.getDistance(DistanceUnit.INCH) < 2.35) stopDelay = new Timer(0.4);

            if (stopDelay.isDone()) break;

            // Update Cube tray
            myCubeTray.updatePosition();
        }

        mySwerveController.stopRobot();

        //Stop intake
        myIntakeController.RunIntake(0, 0);

        Timer myTimer = new Timer(intakedelay);
        while (!myOpMode.isStopRequested() && !myTimer.isDone()) {} //chill

        //Drive Back
        double targetBack = averageEncoderDist() + backOffset * encoderTicksPerInch;
        zeroEncoders();

        // restart the intake in 0.5 seconds
        myTimer = new Timer(0.68);

        // Drive back with intake onn
        while (!myOpMode.isStopRequested() && averageEncoderDist() <= targetBack) {

            final double error = (targetBack - averageEncoderDist()); // /100 is to make the coefficients more readable
            double motorPower = drivingPID.getPIDCorrection(error);
            if (motorPower > speed) motorPower = speed;

            mySwerveController.steerSwerve(false, -motorPower, Math.toRadians(angleDegrees), 0, headingDegrees);
            mySwerveController.moveRobot(true);


            if (myTimer.isDone()) myIntakeController.RunIntake(0, -intakePower);

            // Update Cube tray
            myCubeTray.updatePosition();
        }
        myIntakeController.RunIntake(0, 0);
        mySwerveController.stopRobot();

        Log.i(TAG, "Overshoot: " + (targetBack - averageEncoderDist() - distOffset));
    }


    public void driveDropIntake(double speed, double angleDegrees, double distInches) throws InterruptedException {
        //////// Drive until it has gone the right distance ////////

        // Zero the encoders
        zeroEncoders();

        // Calculate target distance
        double targetTicks = encoderTicksPerInch * distInches - distOffset;
        if (DEBUG) { Log.i(TAG, "Target Ticks: " + targetTicks); }


        myRelicSystem.extensionMotor.setPower(1);
        boolean hasExtended = false;
        boolean hasRetracted = false;

        // Drive
        while (!myOpMode.isStopRequested() && averageEncoderDist() < targetTicks) {
            // While the robot has not driven far enough

            final double error = (targetTicks - averageEncoderDist());
            double motorPower = drivingPID.getPIDCorrection(error);
            if (motorPower > speed) motorPower = speed;

            mySwerveController.steerSwerve(false , motorPower, Math.toRadians(angleDegrees), 0, -1);
            mySwerveController.moveRobot(true);
            if (DEBUG) { Log.i(TAG, "Distance so far: " + averageEncoderDist()); }

            if (myRelicSystem.extensionMotor.getCurrentPosition() > 650) {
                hasExtended = true;
                myRelicSystem.extensionMotor.setPower(-1);
            }
            if (myRelicSystem.extensionMotor.getCurrentPosition() < 20){
                hasRetracted = true;
                myRelicSystem.extensionMotor.setPower(0);
            }

            // Update Cube tray
            myCubeTray.updatePosition();
        }

        //Stop the robot
        mySwerveController.stopRobot();

        if (!hasExtended) {
            while (myRelicSystem.extensionMotor.getCurrentPosition() < 650) {}
            myRelicSystem.extensionMotor.setPower(-1);
        }
        if (!hasRetracted) {
            while (myRelicSystem.extensionMotor.getCurrentPosition() > 20) {}
            myRelicSystem.extensionMotor.setPower(0);
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

            // Update Cube tray
            myCubeTray.updatePosition();

        }

    }

    public void driveByLeftUltraonicDis (double speed, double targetUltrasonicDist, double distForward) throws InterruptedException {

        final double xDist = targetUltrasonicDist - leftUltrasonicSensor.getDistance(DistanceUnit.INCH) * Math.cos(getClosestQuadrantal() - myGyro.getHeading());
        Vector distanceVector = new Vector(true, xDist, distForward);
        Log.i(TAG, "xDist: " + xDist + "  Dist forward: " + distForward);
        driveDist(speed, Math.toDegrees(distanceVector.getAngle()), distanceVector.getMagnitude(), -1);
    }

    public void driveByRightUltrasonicDis (double speed, double targetUltrasonicDist, double distForward) throws InterruptedException {
        final double xDist = targetUltrasonicDist - rightUltrasonicSensor.getDistance(DistanceUnit.INCH) * Math.cos(getClosestQuadrantal() - myGyro.getHeading());
        Vector distanceVector = new Vector(true, -xDist, distForward);
        Log.i(TAG, "xDist: " + -xDist + "  Dist forward: " + distForward);
        driveDist(speed, Math.toDegrees(distanceVector.getAngle()), distanceVector.getMagnitude(), -1);

    }

    public void driveLeftUltrasonicFast (double speed, double targetUltrasonicDist, double distForward) throws InterruptedException {
        boolean negativeDist = false;
        double driveangle = getClosestQuadrantal();
        double robotangle = Math.abs(Math.toDegrees(myGyro.getHeading())-driveangle);
        double yDist = leftUltrasonicSensor.getDistance(DistanceUnit.INCH) * Math.cos(driveangle - myGyro.getHeading()) - targetUltrasonicDist;
        Log.i("yDist", Double.toString(yDist));

        if (Math.abs(yDist) < 0.75) {
            return;
        }

        if(yDist < 0) {
            yDist *= -1;
            negativeDist = true;
        }
        yDist = Math.cos(Math.toRadians(robotangle))*yDist;

        Vector movementVector;
        if(negativeDist) {
            movementVector = new Vector(false, (getClosestQuadrantal() + 90)%360, yDist);
        }
        else{
            movementVector = new Vector(false, (getClosestQuadrantal() + 270)%360, yDist);
        }

        movementVector.addVector(false, distForward, getClosestQuadrantal());
        driveDistDumb(speed, Math.toDegrees(movementVector.getAngle()), movementVector.getMagnitude(), -1);

    }

    public void driveByRightUltrasonicDist (double speed, double targetUltrasonicDist) throws InterruptedException {
        boolean negativeDist = false;
        double driveangle = getClosestQuadrantal();

        double robotangle = Math.abs(Math.toDegrees(myGyro.getHeading())-driveangle);
        double yDist = rightUltrasonicSensor.getDistance(DistanceUnit.INCH) * Math.cos(driveangle - myGyro.getHeading()) - targetUltrasonicDist;
        Log.i("yDist", Double.toString(yDist));

        if (Math.abs(yDist) < 0.75) {
            return;
        }

        if(yDist < 0) {
            yDist *= -1;
            negativeDist = true;
        }
        yDist = Math.cos(Math.toRadians(robotangle))*yDist;
        if(negativeDist) {
            driveDist(speed, (getClosestQuadrantal() + 270)%360, yDist);
        }
        else{
            driveDist(speed, (getClosestQuadrantal() + 90)%360, yDist);
        }

    }


    public boolean isStuck(){
        if(backColorSensor.getDistance(DistanceUnit.INCH) < 2.35){
            return true;
        }
        else{
            return false;
        }
    }

    public void driveByLeftUltraonicDis (double speed, double targetUltrasonicDist) throws InterruptedException {
        boolean negativeDist = false;
        double driveangle = getClosestQuadrantal();
        double robotangle = Math.abs(Math.toDegrees(myGyro.getHeading())-driveangle);
        double yDist = leftUltrasonicSensor.getDistance(DistanceUnit.INCH) - targetUltrasonicDist;
        Log.i("Ultrasonic Dist", Double.toString(leftUltrasonicSensor.getDistance(DistanceUnit.INCH)));
        Log.i("yDist", Double.toString(yDist));

        if(yDist < 0) {
            yDist *= -1;
            negativeDist = true;
        }

        yDist = Math.cos(Math.toRadians(robotangle))*yDist;
        if(negativeDist) {
            driveDist(speed, (getClosestQuadrantal() + 90)%360, yDist);
        }

        else{
            driveDist(speed, (getClosestQuadrantal() + 270)%360, yDist);
        }
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
            //Log.e("Error: ", "" + error);

            //Log.e("First Rotation: ", "" + rotation);

            if (rotation > 0) {
                rotation += baseSpeed;
            } else if (rotation < 0) {
                rotation -= baseSpeed;
            }
            //Log.e("Second Rotation: ", "" + rotation);

            mySwerveController.steerSwerve(true,0,0, rotation, -1);
            mySwerveController.moveRobot(true);



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

            // Update Cube tray
            myCubeTray.updatePosition();

        }
        mySwerveController.stopRobot();
    }

    public void  quickTurn(double targetAngleDegrees) {

        double targetAngleRad = Math.toRadians(targetAngleDegrees);

        boolean turningLeft = (targetAngleRad < myGyro.getHeading());

        while (!myOpMode.isStopRequested()) {

            double currentHeading = myGyro.getHeading();
            double error = setOnNegToPosPi(targetAngleRad - currentHeading);

            double rotation = turningPID.getPIDCorrection(error);
            if (rotation > 0) {
                rotation += baseSpeed;
            } else if (rotation < 0) {
                rotation -= baseSpeed;
            }

            mySwerveController.steerSwerve(true,0,0, rotation, -1);
            mySwerveController.moveRobot(true);

            if (turningLeft && targetAngleRad > currentHeading) break;
            if (!turningLeft && targetAngleRad < currentHeading) break;
        }
    }

    // Non-driving related
    public void unStickCubes () {
        if (frontColorSensor.getDistance(DistanceUnit.INCH)  < 2.35) {
            Timer myTimer = new Timer(1);
            myIntakeController.RunIntake(0, 1);
            while (!myTimer.isDone()) {}
            myIntakeController.RunIntake(0,0);
        }
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
    private double getClosestQuadrantal(){
        double gyroangle = Math.toDegrees(myGyro.getHeading());
        Log.i("DPID gyroAngle", Double.toString(gyroangle));
        if (gyroangle < 45 || gyroangle > 315){
            gyroangle = 0;
        }else if(gyroangle < 135 && gyroangle > 45){
            gyroangle = 90;
        }else if(gyroangle < 225 && gyroangle > 125){
            gyroangle = 180;
        }else if(gyroangle < 315 && gyroangle > 225){
            gyroangle = 270;
        }
        else {
            gyroangle = 0;
        }
        Log.i("DPID ClosestQuad", Double.toString(gyroangle));
        return gyroangle;
    }
}
