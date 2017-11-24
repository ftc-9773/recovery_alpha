package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import android.util.Log;

import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.resources.Vector;

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

    //Gyro
    private Gyro myGyro;

    //Module Movement Vectors
    public Vector flwVector = new Vector(true, 0, 0);
    public Vector frwVector = new Vector(true, 0, 0);
    public Vector blwVector = new Vector(true, 0, 0);
    public Vector brwVector = new Vector(true, 0, 0);

    private boolean useFieldCentricOrientation = true;

    // Variables
    private boolean motorsAreForward = true;

    //INIT
    public SwerveController (HardwareMap hardwareMap, Gyro myGyro, boolean useFieldCentricOrientationDefault) {
        flwModule = new SwerveModule(hardwareMap, "flw");
        frwModule = new SwerveModule(hardwareMap, "frw");
        blwModule = new SwerveModule(hardwareMap, "blw");
        brwModule = new SwerveModule(hardwareMap, "brw");

        this.myGyro = myGyro;
        this.useFieldCentricOrientation = useFieldCentricOrientationDefault;
    }


    // Part One of Movement     -   isCartesian - if the vector is in cartesian or polar form
    //                          -   xComp_Magnitude - x component (in cartesian form) or magnitude (in polar form)
    //                          -   yComp_Angle - y component (in cartesian form) or angle (in polar form)
    // If you just want the direction, set isCartesian to false, magnitude to 1, and angle to whatever angle you want (in radians from 0 to 2pi)
    public void pointDirection(boolean isCartesian, double xComp_Magnitude, double yComp_Angle, double rotationSpeed) {  // if feld centric is added, add heading and toggle

        // Calculate movement of each module
        flwVector.set(isCartesian, xComp_Magnitude, yComp_Angle);
        frwVector.set(isCartesian, xComp_Magnitude, yComp_Angle);
        blwVector.set(isCartesian, xComp_Magnitude, yComp_Angle);
        brwVector.set(isCartesian, xComp_Magnitude, yComp_Angle);

        // TODO: add feild centric controlls
        if (useFieldCentricOrientation) {
            flwVector.shiftAngle(-myGyro.getHeading());
            frwVector.shiftAngle(-myGyro.getHeading());
            blwVector.shiftAngle(-myGyro.getHeading());
            brwVector.shiftAngle(-myGyro.getHeading());
        }

        /*
        if (feildCentric){
            flwVector.set(false, flwVector.getMagnitude(), flwVector.getAngle() - heading);
            frwVector.set(false, frwVector.getMagnitude(), frwVector.getAngle() - heading);
            blwVector.set(false, blwVector.getMagnitude(), blwVector.getAngle() - heading);
            brwVector.set(false, brwVector.getMagnitude(), brwVector.getAngle() - heading);
        }

         */


        // Scale rotation speed
        //rotationSpeed =

        // Add rotation vectors
        flwVector.addVector(false, rotationSpeed, 1.75 * Math.PI);
        frwVector.addVector(false, rotationSpeed, 1.25 * Math.PI);
        blwVector.addVector(false, rotationSpeed, 0.25 * Math.PI);
        brwVector.addVector(false, rotationSpeed, 0.75 * Math.PI);

        /// Keep velocity vectors under 1  ///

        // Find the largest motor speed
        double max = Math.max( Math.max(flwVector.getMagnitude(), frwVector.getMagnitude()),Math.max(blwVector.getMagnitude(), brwVector.getMagnitude()));


        // if greater than 1, divide by largest
        if (max > 1) {
            flwVector.set(false, flwVector.getMagnitude()/max, flwVector.getAngle());
            frwVector.set(false, frwVector.getMagnitude()/max, frwVector.getAngle());
            blwVector.set(false, blwVector.getMagnitude()/max, blwVector.getAngle());
            brwVector.set(false, brwVector.getMagnitude()/max, brwVector.getAngle());
        }


        //Write the direction and speed of each module
        flwModule.setVector(flwVector, motorsAreForward);
        frwModule.setVector(frwVector, motorsAreForward);
        blwModule.setVector(blwVector, motorsAreForward);
        brwModule.setVector(brwVector, motorsAreForward);



        /////// Choose whether to flip motor direction ///////////

        final double distAsIs = flwModule.distForwardDirection() + frwModule.distForwardDirection() + blwModule.distForwardDirection() + brwModule.distForwardDirection();
        final double distSwitched = flwModule.distReversedDirection() + frwModule.distReversedDirection() + blwModule.distReversedDirection() + brwModule.distReversedDirection();

        if (DEBUG) { Log.e(TAG, "Forward Dist: " + distAsIs + "   Backwards Dist: " + distSwitched); }

        if (distSwitched < distAsIs) {

            if (DEBUG) { Log.e(TAG, "Switched Motor Direction"); }
            //Toggle motorsAreForward
            if (motorsAreForward) {
                motorsAreForward = false;
            } else {
                motorsAreForward = true;
            }

            //Recalcuate ModuleDirection
            flwModule.setVector(flwVector, motorsAreForward);
            frwModule.setVector(frwVector, motorsAreForward);
            blwModule.setVector(blwVector, motorsAreForward);
            brwModule.setVector(brwVector, motorsAreForward);

        }

        // Point modules
        flwModule.pointModule();
        frwModule.pointModule();
        blwModule.pointModule();
        brwModule.pointModule();
    }

    // Part Two of Movement
    public void moveRobot() {
        flwModule.driveModule();
        frwModule.driveModule();
        blwModule.driveModule();
        brwModule.driveModule();
    }

    public void toggleFieldCentric () {
        if (useFieldCentricOrientation) {
            useFieldCentricOrientation = false;
        } else {
            useFieldCentricOrientation = true;
        }
    }
    public boolean getFieldCentric() { return useFieldCentricOrientation; }

    public boolean getIsTurning() { return (flwModule.getIsTurning() || frwModule.getIsTurning() || blwModule.getIsTurning() || brwModule.getIsTurning()); }

    public long getFlwEncoderCount() { return flwModule.getEncoderCount(); }
    public long getFrwEncoderCount() { return frwModule.getEncoderCount(); }
    public long getBlwEncoderCount() { return blwModule.getEncoderCount(); }
    public long getBrwEncoderCount() { return brwModule.getEncoderCount(); }

}
