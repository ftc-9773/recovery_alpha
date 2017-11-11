package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.resources.Vector;

/**
 * Created by nicky on 11/10/17.
 */

public class SwerveController {

    private static final String TAG = "ftc9773 SwerveController";

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
        flwVector.addVector(false, rotationSpeed, 0.25 * Math.PI);
        frwVector.addVector(false, rotationSpeed, 0.75 * Math.PI);
        blwVector.addVector(false, rotationSpeed, 1.75 * Math.PI);
        brwVector.addVector(false, rotationSpeed, 1.25 * Math.PI);

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
        flwModule.setVector(flwVector);
        frwModule.setVector(frwVector);
        blwModule.setVector(blwVector);
        brwModule.setVector(brwVector);

        //TODO: Figure out if it is better to reverse motor direction (At some point in the near future)

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
