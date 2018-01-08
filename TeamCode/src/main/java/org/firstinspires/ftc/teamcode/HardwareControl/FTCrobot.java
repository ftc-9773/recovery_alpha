package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.ButtonStatus;
import org.firstinspires.ftc.teamcode.infrastructure.RasiParser;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
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
    public IntakeControllerManual myManualIntakeController;
    public DriveWithPID myDriveWithPID;                      // <--
    public CubeTray myCubeTray;
    private HardwareMap hwMap;
    public RelicSystem myRelicSystem;
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
    private ButtonStatus rightBumperStatus = new ButtonStatus();
    private double rotation;

    private SafeJsonReader jsonReader;

    private double minPowerXY;
    private double minPowerRot;
    private double zeroRange;


    private static final String TAG = "9773_FTCrobot";
    private static final boolean DEBUG = false;

    private boolean disableDriving = false;
    private boolean disableLift  = false;
    private boolean disableDriverIntake = false;
    private boolean disableAutoIntake = true;
    private boolean disableRelicArm = false ;

    // INIT
    public FTCrobot(HardwareMap hwmap, Telemetry telemetry, Gamepad gamepad1, Gamepad gamepad2, LinearOpModeCamera myLinearOpModeCamera){
        this.gp1y = new ButtonStatus();
        this.hwMap = hwmap;
        this.myTelemetry = telemetry;
        // myIntakeController = new IntakeController(hwMap);
        this.myGyro = new Gyro(hwMap);
        this.mySwerveController = new SwerveController(hwMap, myGyro, telemetry);
        this.myManualIntakeController = new IntakeControllerManual(hwMap);
        this.myDriveWithPID = new DriveWithPID(mySwerveController, myGyro, myLinearOpModeCamera);
        this.myRelicSystem = new RelicSystem(myTelemetry, hwMap);
        this.myCubeTray = new CubeTray(hwmap,gamepad2,null);
        this.myGamepad1 = gamepad1;
        this.myGamepad2 = gamepad2;

        jsonReader = new SafeJsonReader("FTCRobotParameters");
        minPowerXY = jsonReader.getDouble("MinPowerXY");
        minPowerRot = jsonReader.getDouble("MinPowerRotation");
        zeroRange = jsonReader.getDouble("ZeroRange");
    }

    private double valWithThresholdAndPower3Scaling(double val, double minNonZeroVal, boolean highPrecisionMode) {
        if (val > zeroRange) {
            if (highPrecisionMode) return Math.pow(0.5*val, 3) + minNonZeroVal;
            return Math.pow(val, 3) + minNonZeroVal;
        }
        if (val < -zeroRange) {
            if (highPrecisionMode) return Math.pow(0.5*val, 3) - minNonZeroVal;
            return Math.pow(val, 3) - minNonZeroVal;
        }
        return 0.0;
    }

    public void runGamepadCommands(){

        dpaddownStatus.recordNewValue(myGamepad2.dpad_down);
        dpadupStatus.recordNewValue(myGamepad2.dpad_up);

        /////// Driving - gamepad 1 left and right joysticks & Dpad /////

        // Get current direction
        boolean highPrecisionMode = myGamepad1.left_bumper;
        double drivingRotation = valWithThresholdAndPower3Scaling(myGamepad1.right_stick_x, minPowerRot, highPrecisionMode);
        //double drivingRotation = Math.pow(myGamepad1.right_stick_x, 3);
        // Direction Lock
        if (drivingRotation != 0) {
            Log.e(TAG, "Rotation is 0");
            // Disable rotation lock if driver spins the robot
            directionLock = -1;
        } else {
            Log.e(TAG, "Checking dpad");
            if (myGamepad1.dpad_up) {
                Log.d(TAG, "Up dpad pressed");
                directionLock = 0;
            } else if (myGamepad1.dpad_right) {
                Log.d(TAG, "Right dpad pressed");
                directionLock = 90;
            } else if (myGamepad1.dpad_down) {
                Log.d(TAG, "down dpad pressed");
                directionLock = 180;
            } else if (myGamepad1.dpad_left) {
                Log.d(TAG, "Left dpad pressed");
                directionLock = 270;
            }
        }
        // compute speed. Old behaviour: set minPower and zeroZone to 0.0
        // compute for x & y
        double drivingX =   valWithThresholdAndPower3Scaling(myGamepad1.left_stick_x, minPowerXY, highPrecisionMode);
        double drivingY = - valWithThresholdAndPower3Scaling(myGamepad1.left_stick_y, minPowerXY, highPrecisionMode);
        Log.d(TAG, "driving X is " + drivingX);
        Log.d(TAG, "driving Y is " + drivingY);
        Log.d(TAG, "driving rot is " + drivingRotation);

        //double drivingX =   Math.pow(myGamepad1.left_stick_x, 3);
        //double drivingY = - Math.pow(myGamepad1.left_stick_y, 3);
        //if (highPrecisionMode) {
        //    drivingX *= 0.5;
        //    drivingY *= 0.5;
        //    drivingRotation *= 0.5;
        //}

        mySwerveController.steerSwerve(true, drivingX, drivingY, drivingRotation, directionLock);
        mySwerveController.moveRobot(highPrecisionMode);
// */

            /////// Intake - Gamepad 1 right trigger and bumper ////////
    /*   if(!disableAutoIntake) {
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
         }
    // */

        // Manual Intake Controller - Gamepad 2 Right Joystick
        if(!disableDriverIntake) {
            myManualIntakeController.RunIntake(myGamepad2.right_stick_x, -1*myGamepad2.right_stick_y);

            // Lowering Intake - Gamepad 2 Left Bumper
            leftBumperStatus.recordNewValue(myGamepad2.left_bumper);
            if (leftBumperStatus.isJustOn()) {
                time = System.currentTimeMillis();
                myManualIntakeController.lowerIntake(true);
            }
            if (System.currentTimeMillis() - 500 > time) {
                myManualIntakeController.lowerIntake(false);
            }
        }


        // cube tray
        if(!disableLift) {
            myCubeTray.updateFromGamepad();
        }


        // relic arm
        if(!disableRelicArm) {
            rightBumperStatus.recordNewValue(myGamepad1.right_bumper);
            if (dpadupStatus.isJustOn()) {
                armState = !armState  ;
            }
            if (dpaddownStatus.isJustOn()&& !armState) {
                grabState = !grabState;
            }

            // Relic arm - Gamepad 2 Left Joystick
            myRelicSystem.runSequence(myGamepad2.left_stick_y * -0.95 + 0.05, armState, grabState);
        }


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
        myTelemetry.addData("Rotation", myGamepad1.right_stick_x);


        if (mySwerveController.getFieldCentric()) {
            myTelemetry.addData("Field Centric", "On");
        } else {
            myTelemetry.addData("Field Centric", "Off");
        }

        myTelemetry.update();
    }

    public void recordGyroPosition() {
        myGyro.recordHeading();
    }
}
