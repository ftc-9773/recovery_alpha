package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.resources.Vector;

/**
 * Created by nicky on 11/10/17.
 */

public class SwerveController {

    private static final String TAG = "ftc9773 SwerveController";

    // Swerve Modules
    private SwerveModule flwModule;
    private SwerveModule frwModule;
    private SwerveModule blwModule;
    private SwerveModule brwModule;

    //Module Movement Vectors
    private Vector flwVector = new Vector(true, 0, 0);
    private Vector frwVector = new Vector(true, 0, 0);
    private Vector blwVector = new Vector(true, 0, 0);
    private Vector brwVector = new Vector(true, 0, 0);

    // Variables
    private boolean motorsAreForward = true;

    //INIT
    public SwerveController (HardwareMap hardwareMap) {
        flwModule = new SwerveModule(hardwareMap, "flw", "modOneDefPos");
        frwModule = new SwerveModule(hardwareMap, "frw", "modTwoDefPos");
        blwModule = new SwerveModule(hardwareMap, "blw", "modThreeDefPos");
        brwModule = new SwerveModule(hardwareMap, "brw", "modFourDefPos");
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

        //TODO: Reset magnitudes to be from -1 to 1

        //Set the direction of each module
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
