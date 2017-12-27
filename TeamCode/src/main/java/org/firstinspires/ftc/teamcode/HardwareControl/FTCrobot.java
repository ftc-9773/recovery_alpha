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

/*
* To get to the right directory:
* cd TeamCode/src/main/java/org/firstinspires/ftc/teamcode/RASI/
*
* To push:
* ~/Library/Android/sdk/platform-tools/adb push AutonTesting.rasi /sdcard/FIRST/team9773/rasi18/
 */

public class FTCrobot {
    private SwerveController mySwerveController;
    private double directionLock = -1;
    private double stickl1x;
    private double stickl1y;
    private Gyro myGyro;
    private RasiParser opModeControl;
    private String[] currentCommand;
//    private IntakeController myIntakeController;
    private IntakeControllerManual myManualIntakeController;
    private DriveWithPID myDriveWithPID;
    public CubeTray myCubeTray;
    private HardwareMap hwMap;
    private RelicSystem myRelicSystem;
    private Telemetry myTelemetry;
    private Gamepad myGamepad1;
    private Gamepad myGamepad2;
    private ButtonStatus gp1y;
    private boolean dpadlast = false;
    private char state = 0;
    private boolean armState = true;
    private boolean grabState = true;
    private long time;
    private ButtonStatus dpadupStatus = new ButtonStatus();
    private ButtonStatus dpaddownStatus = new ButtonStatus();
    private ButtonStatus gamepad1LeftTrigger = new ButtonStatus();
    private ButtonStatus gamepad1RightTrigger = new ButtonStatus();
    private ButtonStatus leftBumperStatus = new ButtonStatus();
    private boolean highPrecisionEnabled = false;
    private double rotation;


    private static final String TAG = "9773_FTCrobot";
    private static final boolean DEBUG = true;

    // INIT
    public FTCrobot(HardwareMap hwmap, Telemetry telemetry, Gamepad gamepad1, Gamepad gamepad2){
        this.gp1y = new ButtonStatus();
        this.hwMap = hwmap;
        this.myTelemetry = telemetry;
//        myIntakeController = new IntakeController(hwMap);
        this.myManualIntakeController = new IntakeControllerManual(hwMap);
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
        gp1y.recordNewValue(myGamepad1.y);
        if (gp1y.isJustOn()){
            highPrecisionEnabled = !highPrecisionEnabled;
        }
        if(highPrecisionEnabled) {
            stickl1x = myGamepad1.left_stick_x*.793;
            stickl1y = myGamepad1.left_stick_y*.793;
            rotation = myGamepad1.right_stick_x*0.5;
        }
        else{
            stickl1x = myGamepad1.left_stick_x;
            stickl1y = myGamepad1.left_stick_y;
            rotation = myGamepad1.right_stick_x;
        }
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
        mySwerveController.steerSwerve(true, Math.pow(stickl1x,3), Math.pow(stickl1y * -1, 3), Math.pow(rotation,3), directionLock);
        Log.i(TAG, "Joystick input  X: " + Math.pow(stickl1x,3) + "   Y: " + Math.pow(stickl1y * -1, 3));

        if(mySwerveController.getMaxErrorAmt()<.15 && highPrecisionEnabled) {
            mySwerveController.moveRobot();
        }else if(!highPrecisionEnabled){
            mySwerveController.moveRobot();
        }
// */

        /////// Intake - Gamepad 1 right trigger and bumper ////////
/*
        if (myGamepad1.right_trigger > 0) {
            myIntakeController.runIntakeOut();
        } else if (myGamepad1.right_bumper) {
            myIntakeController.runIntakeIn();
        } else {
            myIntakeController.intakeOff();
        }

        // Lowering Intake - Gamepad 2 Left Bumper
        leftBumperStatus.recordNewValue(myGamepad2.left_bumper);
        if(leftBumperStatus.isJustOn()){
            time = System.currentTimeMillis();
            myIntakeController.lowerIntake(true);
        }
        if (System.currentTimeMillis()-500>time){
            myIntakeController.lowerIntake(false);
        }
// */

        // Manual Intake Controller - Gamepad 2 Right Joystick
        myManualIntakeController.RunIntake(myGamepad2.right_stick_x, myGamepad2.right_stick_y);

        // Lowering Intake - Gamepad 2 Left Bumper
        leftBumperStatus.recordNewValue(myGamepad2.left_bumper);
        if(leftBumperStatus.isJustOn()){
            time = System.currentTimeMillis();
            myManualIntakeController.lowerIntake(true);
        }
        if (System.currentTimeMillis()-500>time){
            myManualIntakeController.lowerIntake(false);
        }


        // cube tray
        //myCubeTray.updateFromGamepad();


        // relic arm
        if(dpadupStatus.isJustOn()){
            armState = !armState;
        }
        if(dpaddownStatus.isJustOn()){
            grabState = !grabState;
        }

        // Relic arm - Gamepad 2 Left Joystick
        myRelicSystem.runSequence(myGamepad2.left_stick_y*-0.95 + 0.05, armState, grabState);


        // Toggle Field Centric Mode - gamepad 1 left trigger
        if (myGamepad1.left_trigger < 0.5) { gamepad1LeftTrigger.recordNewValue(false);} else { gamepad1LeftTrigger.recordNewValue(true); }
        if (gamepad1LeftTrigger.isJustOn()) {
            mySwerveController.toggleFieldCentric();
        }

        // Reset Gyro Position
        gamepad1RightTrigger.recordNewValue(myGamepad1.right_trigger > 0.5);
        if (gamepad1RightTrigger.isJustOn()) {
            myGyro.setZeroPosition();
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

        if (mySwerveController.getFieldCentric()) {
            myTelemetry.addData("Field Centric", "On");
        } else {
            myTelemetry.addData("Field Centric", "Off");
        }

        myTelemetry.update();
    }

    public void runRASI(String filename){
        opModeControl = new RasiParser(filename);
        myTelemetry.addData("method","rasi");
        myTelemetry.update();
        int index = 0;
        while(index<opModeControl.commands.length) {
            Log.i(TAG, "Index is: " + index);
            opModeControl.loadNextCommand();
            switch (opModeControl.getParameter(0)) {
                case "drv":
                    try {
                        myDriveWithPID.driveStraight(false, Double.valueOf(opModeControl.getParameter(1)), Double.valueOf(opModeControl.getParameter(2)), Double.valueOf(opModeControl.getParameter(3)), Double.valueOf(opModeControl.getParameter(4)));
                   } catch (InterruptedException e) {
                        e.printStackTrace();
                   }
                    break;
                case "intko":
                    myManualIntakeController.RunIntake(0,-1);
                    break;
                case "intki":
                    myManualIntakeController.RunIntake(0,1);
                    break;
                case "intkl":
                    myManualIntakeController.lowerIntake(true);
                    time = System.currentTimeMillis();
                    while (time+500<System.currentTimeMillis()){}
                    myManualIntakeController.lowerIntake(false);
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
            index++;
        }
    }
}
