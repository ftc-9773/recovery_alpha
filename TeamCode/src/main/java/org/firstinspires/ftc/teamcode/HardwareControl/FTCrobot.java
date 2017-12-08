package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.ButtonStatus;
import org.firstinspires.ftc.teamcode.infrastructure.controlParser;
import org.firstinspires.ftc.teamcode.opmodes.Swerve;
import android.util.Log;

/**
 * Created by Vikesh on 11/22/2017.
 */

public class FTCrobot {
    private SwerveController mySwerveController;
    private double directionLock = -1;

    private Gyro myGyro;
    private controlParser opModeControl;
    private String[] currentCommand;
    private IntakeController myIntakeController;
    private DriveWithPID myDriveWithPID;
    private HardwareMap hwMap;
    private RelicSystem myRelicSystem;
    private Telemetry myTelemetry;
    private Gamepad myGamepad1;
    private Gamepad myGamepad2;
    private boolean dpadlast = false;
    private char state = 0;
    private ButtonStatus dpadupStatus = new ButtonStatus();
    private ButtonStatus dpaddownStatus = new ButtonStatus();

    private static final String TAG = "9773_FTCrobot";
    private static final boolean DEBUG = true;

    // INIT
    public FTCrobot(HardwareMap hwmap, Telemetry telemetry, Gamepad gamepad1, Gamepad gamepad2){
        this.hwMap = hwmap;
        this.myTelemetry = telemetry;
        myIntakeController = new IntakeController(hwMap);
        this.myGyro = new Gyro(hwMap);
        this.mySwerveController = new SwerveController(hwMap, myGyro, telemetry);
        this.myDriveWithPID = new DriveWithPID(mySwerveController, myGyro);
        this.myRelicSystem = new RelicSystem(myTelemetry, hwMap);
        this.myGamepad1 = gamepad1;
        this.myGamepad2 = gamepad2;
    }

    public void runGamepadCommands(){

        dpaddownStatus.recordNewValue(myGamepad2.dpad_down);
        dpadupStatus.recordNewValue(myGamepad2.dpad_up);


        /////// Driving - gamepad 1 left and right joysticks & Dpad /////

        // Direction Lock
        double rotation = myGamepad1.right_stick_x;

        if (rotation != 0) {
            Log.e(TAG, "Rotation is 0");
            // Disable rotation lock if driver spins the robot
            directionLock = -1;
        } else {
            Log.e(TAG, "Checking dpad");
            if (myGamepad1.dpad_up) {
                Log.e(TAG, "Up dpad pressed");
                directionLock = 0;
            } else if (myGamepad1.dpad_right) {
                Log.e(TAG, "Right dpad pressed");
                directionLock = 90;
            } else if (myGamepad1.dpad_down) {
                Log.e(TAG, "down dpad pressed");
                directionLock = 180;
            } else if (myGamepad1.dpad_left) {
                Log.e(TAG, "Left dpad pressed");
                directionLock = 270;
            }
        }

        // Actual driving
        mySwerveController.steerSwerve(true, myGamepad1.left_stick_x, myGamepad1.left_stick_y * -1, rotation, directionLock);
        //mySwerveController.moveRobot();


        /////// Intake - Gamepad 1 right trigger and bumper /////
        if (myGamepad1.right_trigger > 0) {
            myIntakeController.runIntakeOut();
        } else if (myGamepad1.right_bumper) {
            myIntakeController.runIntakeIn();
        } else {
            myIntakeController.intakeOff();
        }

        //relic grabber
        if(dpaddownStatus.isJustOn() && state <= 2) {
            myRelicSystem.runSequence((-0.975*myGamepad2.left_stick_y+0.025), state);
            if(state == 3){
                state = 0;
            } else{
                state++;
            }
        }else if(dpadupStatus.isJustOn() && state >= 0) {
            myRelicSystem.runSequence((-0.975*myGamepad2.left_stick_y+0.025), state);
            if(state == 0){
                state = 3;
            } else{
                state-=1;
            }
        } else{
            myRelicSystem.runSequence((-0.975*myGamepad2.left_stick_y+0.025), (char)4);
        }
    }

    public void doTelemetry() {

        myTelemetry.addData("Gamepad x", myGamepad1.left_stick_x);
        myTelemetry.addData("Gamepad y", myGamepad1.left_stick_y);
        myTelemetry.update();
    }

    public void runRASI(String filename){
        opModeControl = new controlParser(filename);
        int index = 0;
        while(index<opModeControl.commands.length) {
            currentCommand = opModeControl.getNextCommand();
            switch (currentCommand[0]) {
                case "drv":
                    try {
                        myDriveWithPID.driveStraight(false, Double.valueOf(currentCommand[1]), Double.valueOf(currentCommand[2]), Double.valueOf(currentCommand[3]), Double.valueOf(currentCommand[4]));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "intk":
                    myIntakeController.runIntakeIn();
                    break;
                case "outk":
                    myIntakeController.runIntakeOut();
                    break;
                case "end":
                    index = opModeControl.commands.length;
            }
        }
    }
}
