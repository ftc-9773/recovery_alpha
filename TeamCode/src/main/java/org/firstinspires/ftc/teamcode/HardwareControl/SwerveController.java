package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.HardwareMap;
import android.util.Log;

import org.firstinspires.ftc.teamcode.PositionTracking.EncoderTracking;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.PIDController;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.resources.Vector;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.PositionTracking.CoordinateSystem;

/**
 * Created by nicky on 11/10/17.
 */

public class SwerveController {

    private static final String TAG = "9773 SwerveController";
    private static final boolean DEBUG = false;

    // Swerve Modules
    public SwerveModule flwModule;
    public SwerveModule frwModule;
    public SwerveModule blwModule;
    public SwerveModule brwModule;

    private boolean DRIVING_ENABLED = true;

    //Gyro
    private Gyro myGyro;

    //Module Movement Vectors
    public Vector flwVector = new Vector(true, 0, 0);
    public Vector frwVector = new Vector(true, 0, 0);
    public Vector blwVector = new Vector(true, 0, 0);
    public Vector brwVector = new Vector(true, 0, 0);

    //Orientation tracking variables
    public boolean useFieldCentricOrientation = true;
    private SafeJsonReader myPIDCoefficients;
    private PIDController turningPID;

    // Position Tracking
    public EncoderTracking myEncoderTracker;

    // Variables
    private boolean motorsAreForward = true;

    // Helper Functions
    private double negToPosPi (double num) {
        if (num < -Math.PI) {
            return num + 2*Math.PI;
        } else if (num > Math.PI) {
            return num - 2*Math.PI;
        } else {
            return num;
        }
    }

    //INIT
    public SwerveController (HardwareMap hardwareMap, Gyro myGyro, Telemetry telemetry) {
        flwModule = new SwerveModule(hardwareMap, "flw");
        frwModule = new SwerveModule(hardwareMap, "frw");
        blwModule = new SwerveModule(hardwareMap, "blw");
        brwModule = new SwerveModule(hardwareMap, "brw");

        this.myGyro = myGyro;

        myPIDCoefficients = new SafeJsonReader("RobotTurningPIDCoefficients");
        double Kp = myPIDCoefficients.getDouble("Kp");
        double Ki = myPIDCoefficients.getDouble("Ki");
        double Kd = myPIDCoefficients.getDouble("Kd");
        double Ke = myPIDCoefficients.getDouble("Ke");

        if (DEBUG)  Log.e(TAG, "Coefficients: " + Kp + " " + Ki + " " + Kd);
        turningPID = new PIDController(Kp, Ke, Ki, Kd);

        myEncoderTracker = new EncoderTracking(flwModule, frwModule, blwModule, brwModule, myGyro);
    }


    // Part One of Movement     -   isCartesian - if the vector is in cartesian or polar form
    //                          -   xComp_Magnitude - x component (in cartesian form) or magnitude (in polar form)
    //                          -   yComp_Angle - y component (in cartesian form) or angle (in polar form)
    // If you just want the direction, set isCartesian to false, magnitude to 1, and angle to whatever angle you want (in radians from 0 to 2pi)

    public double steerSwerve(boolean isCartesian, double xMag, double yAng, double rotation, double directionLock) {
        // direction lock  - in Degrees


        if (DEBUG) Log.d(TAG, "Rotation: " + rotation + "  DirectionLock: " + directionLock);

        // Check to make sure rotation is off before doing directionLock
        if (directionLock != -1 && useFieldCentricOrientation) {
            // Calculate Error
            double error = negToPosPi(Math.toRadians(directionLock) - myGyro.getHeading());
            rotation = turningPID.getPIDCorrection(error);
            if (DEBUG) Log.e(TAG, "true error: " + error + "  rotation: " + rotation);
        }

        //Have pointModules do the brunt work
        pointModules(isCartesian, xMag, yAng, rotation);
        return rotation;
    }

    public void pointModules(boolean isCartesian, double xComp_Magnitude, double yComp_Angle, double rotationSpeed) {

        // Calculate movement of each module
        Vector tempVector = new Vector(isCartesian, xComp_Magnitude, yComp_Angle);
        flwVector.set(true, tempVector.getX(), tempVector.getY());
        frwVector.set(true, tempVector.getX(), tempVector.getY());
        blwVector.set(true, tempVector.getX(), tempVector.getY());
        brwVector.set(true, tempVector.getX(), tempVector.getY());

        // For position tracking

        if (useFieldCentricOrientation) {
            double gyroHeading = myGyro.getHeading();
            flwVector.shiftAngle(-gyroHeading);
            frwVector.shiftAngle(-gyroHeading);
            blwVector.shiftAngle(-gyroHeading);
            brwVector.shiftAngle(-gyroHeading);
        }

        // Scale rotation speed
        rotationSpeed = Math.pow(rotationSpeed,3) * 1.5;

        // Add rotation vectors
        flwVector.addVector(false, rotationSpeed, 0.25 * Math.PI);
        frwVector.addVector(false, rotationSpeed, 0.75 * Math.PI);
        blwVector.addVector(false, rotationSpeed, 1.75 * Math.PI);
        brwVector.addVector(false, rotationSpeed, 1.25 * Math.PI);
/*
        Log.i("flwErrorAmt", Double.toString(flwModule.getErrorAmt()));
        Log.i("frwErrorAmt", Double.toString(frwModule.getErrorAmt()));
        Log.i("blwErrorAmt", Double.toString(blwModule.getErrorAmt()));
        Log.i("brwErrorAmt", Double.toString(brwModule.getErrorAmt()));

        Log.i("BRW Direction", Double.toString(brwVector.getAngle()));
        Log.i("FLW Direction", Double.toString(flwVector.getAngle()));
        Log.i("FRW Direction", Double.toString(frwVector.getAngle()));
        Log.i("BLW Direction", Double.toString(blwVector.getAngle()));

        Log.i("BRW module position", Double.toString(brwModule.getModulePosition()));
        Log.i("FLW module position", Double.toString(flwModule.getModulePosition()));
        Log.i("FRW module position", Double.toString(frwModule.getModulePosition()));
        Log.i("BLW module position", Double.toString(blwModule.getModulePosition()));
        */
        /// Keep velocity vectors under 1  ///

        // Find the largest motor speed
        double max = Math.max( Math.max(flwVector.getMagnitude(), frwVector.getMagnitude()),
                Math.max(blwVector.getMagnitude(), brwVector.getMagnitude()));


        // if greater than 1, divide by largest
        if (max > 1) {
            flwVector.set(false, flwVector.getMagnitude()/max, flwVector.getAngle());
            frwVector.set(false, frwVector.getMagnitude()/max, frwVector.getAngle());
            blwVector.set(false, blwVector.getMagnitude()/max, blwVector.getAngle());
            brwVector.set(false, brwVector.getMagnitude()/max, brwVector.getAngle());
        }

        //Write the direction and speed of each module
        flwModule.setVector(flwVector);
        frwModule.setVector(frwVector);
        blwModule.setVector(blwVector);
        brwModule.setVector(brwVector);

//        Log.i(TAG, "Rotation: " + rotationSpeed + "   Vector Magnitude: " + flwVector.getMagnitude());

        //Log.i(TAG, "FLW: " + flwVector.getAngle()/Math.PI + "   FRW: " + frwVector.getAngle()/Math.PI + "   BLW: " + blwVector.getAngle()/Math.PI + "   BRW: " + brwVector.getAngle()/Math.PI);
        // Point modules
        flwModule.pointModule();
        frwModule.pointModule();
        blwModule.pointModule();
        brwModule.pointModule();
    }

    // Part Two of Movement
    public void moveRobot(boolean highPrecisionMode) {

        if (highPrecisionMode) {
            boolean allModulesStopped = (flwVector.getMagnitude() == 0) && (frwVector.getMagnitude() == 0) && (blwVector.getMagnitude() == 0) && (brwVector.getMagnitude() == 0);
            if (getIsTurning() && !allModulesStopped) {
                return;
            }
        }

        if(DRIVING_ENABLED) {
            flwModule.driveModule();
            frwModule.driveModule();
            blwModule.driveModule();
            brwModule.driveModule();
        }

        myEncoderTracker.updatePosition();
    }

    public void toggleFieldCentric () {
        useFieldCentricOrientation = ! useFieldCentricOrientation;
    }
    public boolean getFieldCentric() { return useFieldCentricOrientation; }

    public boolean getIsTurning() { return (flwModule.getIsTurning() || frwModule.getIsTurning() || blwModule.getIsTurning() || brwModule.getIsTurning()); }

    public long getFlwEncoderCount() { return flwModule.getEncoderCount(); }
    public long getFrwEncoderCount() { return frwModule.getEncoderCount(); }
    public long getBlwEncoderCount() { return blwModule.getEncoderCount(); }
    public long getBrwEncoderCount() { return brwModule.getEncoderCount(); }
    public double getMaxErrorAmt(){
        double biggest;
        double biggest1;
        double biggest2;
        if (Math.abs(flwModule.getErrorAmt())>Math.abs(frwModule.getErrorAmt())){
            biggest1 = Math.abs(flwModule.getErrorAmt());
        }
        else {
            biggest1 = Math.abs(frwModule.getErrorAmt());
        }
        if (Math.abs(blwModule.getErrorAmt())>Math.abs(brwModule.getErrorAmt())){
            biggest2 = Math.abs(blwModule.getErrorAmt());
        }
        else {
            biggest2 = Math.abs(brwModule.getErrorAmt());
        }
        if (biggest1>biggest2){
            biggest = biggest1;
        }
        else{
            biggest = biggest2;
        }
        return biggest;
    }
}
