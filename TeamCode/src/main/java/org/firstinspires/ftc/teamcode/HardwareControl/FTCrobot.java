package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.ButtonStatus;
import org.firstinspires.ftc.teamcode.infrastructure.RasiParser;
import org.firstinspires.ftc.teamcode.opmodes.Swerve;
import android.util.Log;

/**
 * Created by Vikesh on 11/22/2017.
 */

public class FTCrobot {
    private SwerveController mySwerveController;
    private double directionLock = -1;

    private Gyro myGyro;
    private RasiParser opModeControl;
    private String[] currentCommand;
    private IntakeController myIntakeController;
    private DriveWithPID myDriveWithPID;
    public CubeTray myCubeTray;
    private HardwareMap hwMap;
    private RelicSystem myRelicSystem;
    private Telemetry myTelemetry;
    private Gamepad myGamepad1;
    private Gamepad myGamepad2;
    private boolean dpadlast = false;
    private char state = 0;
    private boolean armState = false;
    private boolean grabState = false;
    private long time;
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
        this.myCubeTray = new CubeTray(hwmap,gamepad2,null);
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
        mySwerveController.moveRobot();


        /////// Intake - Gamepad 1 right trigger and bumper /////
        if (myGamepad1.right_trigger > 0) {
            myIntakeController.runIntakeOut();
        } else if (myGamepad1.right_bumper) {
            myIntakeController.runIntakeIn();
        } else {
            myIntakeController.intakeOff();
        }

        // cube tray
        myCubeTray.updateFromGamepad();

        if(dpadupStatus.isJustOn()){
            armState = !armState;
        }
        if(dpaddownStatus.isJustOn()){
            grabState = !grabState;
        }
        myRelicSystem.runSequence(myGamepad2.left_stick_y*-0.95 + 0.05, armState, grabState);

        ButtonStatus leftBumperStatus = new ButtonStatus();
        leftBumperStatus.recordNewValue(myGamepad2.left_bumper);

        if(leftBumperStatus.isJustOn()){
            time = System.currentTimeMillis();
            myIntakeController.lowerIntake(true);
        }
        if (System.currentTimeMillis()-500>time){
            myIntakeController.lowerIntake(false);
        }
    }

    // homes cube tray lift to top. takes cube tray position object
    public void homeLift(CubeTray.LiftFinalStates pos ){
        myCubeTray.setStartPosition(pos);
        try {
            myCubeTray.homeLiftVersA();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doTelemetry() {

        myTelemetry.addData("Gamepad x", myGamepad1.left_stick_x);
        myTelemetry.addData("Gamepad y", myGamepad1.left_stick_y);
        myTelemetry.update();
    }

    public void runRASI(String filename){
        opModeControl = new RasiParser(filename);
        myTelemetry.addData("method","rasi");
        myTelemetry.update();
        int index = 0;
        while(index<opModeControl.commands.length) {
            opModeControl.loadNextCommand();
            switch (opModeControl.getParameter(0)) {
                case "drv":
                    try {
                        myDriveWithPID.driveStraight(false, Double.valueOf(opModeControl.getParameter(1)), Double.valueOf(opModeControl.getParameter(2)), Double.valueOf(opModeControl.getParameter(3)), Double.valueOf(opModeControl.getParameter(4)));
                   } catch (InterruptedException e) {
                        //e.printStackTrace();
                   }
                    break;
                case "intko":
                    myIntakeController.runIntakeIn();
                    break;
                case "intki":
                    myIntakeController.runIntakeOut();
                    break;
                case "intkl":
                    myIntakeController.lowerIntake(true);
                    time = System.currentTimeMillis();
                    while (time+500<System.currentTimeMillis()){}
                    myIntakeController.lowerIntake(false);
                    break;
                case "ctload":
                    myCubeTray.setToPos(CubeTray.LiftFinalStates.LOADING);
                    break;
                case "ctcl":
                    myCubeTray.setToPos(CubeTray.LiftFinalStates.LOW);
                    break;
                case "ctch":
                    myCubeTray.setToPos(CubeTray.LiftFinalStates.HIGH);
                    break;
                case "ctdump":
                    myCubeTray.dump();
                    break;
                case "end":
                    index = opModeControl.commands.length;
            }
            myTelemetry.update();
        }
    }
}
