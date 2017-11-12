package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import android.util.Log;

import org.firstinspires.ftc.teamcode.resources.Vector;

/**
 * Created by nicky on 11/10/17.
 */

public class SwerveController {

    private final String TAG = "9773 SwerveController";
    private final boolean DEBUG = false;

    // Swerve Modules
    public SwerveModule flwModule;
    public SwerveModule frwModule;
    public SwerveModule blwModule;
    public SwerveModule brwModule;

    //Module Movement Vectors
    public Vector flwVector = new Vector(true, 0, 0);
    public Vector frwVector = new Vector(true, 0, 0);
    public Vector blwVector = new Vector(true, 0, 0);
    public Vector brwVector = new Vector(true, 0, 0);

    // Variables
    private boolean motorsAreForward = true;

    //INIT
    public SwerveController (HardwareMap hardwareMap) {
        flwModule = new SwerveModule(hardwareMap, "flw");
        frwModule = new SwerveModule(hardwareMap, "frw");
        blwModule = new SwerveModule(hardwareMap, "blw");
        brwModule = new SwerveModule(hardwareMap, "brw");
        blwModule.swerveMotor.setDirection(DcMotor.Direction.REVERSE);
        flwModule.swerveMotor.setDirection(DcMotor.Direction.REVERSE);
    }


    // Part One of Movement
    public void pointDirection(double xComp, double yComp, double rotationSpeed) {

        // Calculate movement of each module
        flwVector.set(true, xComp, yComp);
        frwVector.set(true, xComp, yComp);
        blwVector.set(true, xComp, yComp);
        brwVector.set(true, xComp, yComp);

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

}
