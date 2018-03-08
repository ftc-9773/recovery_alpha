package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.AutonomousDriving.DriveWithPID;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.ButtonStatus;
import org.firstinspires.ftc.teamcode.infrastructure.RasiParser;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.resources.Vector;

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
    public ModernRoboticsI2cRangeSensor distanceSensor;
    private SwerveController mySwerveController;
    private double stickl1x;
    private double stickl1y;
    private Gyro myGyro;
    private RasiParser opModeControl;
    private String[] currentCommand;
//    private IntakeController myIntakeController;
    public IntakeControllerManual myManualIntakeController;
    public DriveWithPID myDriveWithPID;                      // <--

    public CubeTrays myCubeTray;

    private HardwareMap hwMap;
    public RelicSystem myRelicSystem;

    private Telemetry myTelemetry;
    private Gamepad myGamepad1;
    private Gamepad myGamepad2;
    private ButtonStatus gp1y;
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
    private double directionLock = -1;

    private SafeJsonReader jsonReader;

    private double minPowerXY;
    private double minPowerRot;
    private double zeroRange;
    private double xyCoefficient;
    private double rotCoefficient;
    private double highPrecisionScalingFactor;
    private double highPrecisionRotationFactor;

    public JewelKnocker jewelKnocker;

    private static final String TAG = "9773_FTCrobot";
    private static final boolean DEBUG = false;

    private static boolean DISABLE_LIFT = false;
    private static boolean DISABLE_RELIC_ARM = false;
    private static boolean USE_FIELD_CENTRIC_ROTATION = true;
    private static boolean DISABLE_DRIVER_INTAKE = false;

    private static final boolean USING_SLOT_TRAY = true;


    // INIT
    public FTCrobot(HardwareMap hwmap, Telemetry telemetry, Gamepad gamepad1, Gamepad gamepad2, LinearOpModeCamera myLinearOpModeCamera){
        jewelKnocker = new JewelKnocker(hwmap);
        this.gp1y = new ButtonStatus();
        this.hwMap = hwmap;
        this.myTelemetry = telemetry;
        // myIntakeController = new IntakeController(hwMap);
        this.distanceSensor = hwMap.get(ModernRoboticsI2cRangeSensor.class, "ultrasonicSensor");
        this.myGyro = new Gyro(hwMap);
        this.mySwerveController = new SwerveController(hwMap, myGyro, telemetry);
        this.myManualIntakeController = new IntakeControllerManual(hwMap);
        this.myDriveWithPID = new DriveWithPID(mySwerveController, myGyro, myLinearOpModeCamera);
        this.myRelicSystem = new RelicSystem(myTelemetry, hwMap, myLinearOpModeCamera);

        this.myGamepad1 = gamepad1;
        this.myGamepad2 = gamepad2;

        jsonReader = new SafeJsonReader("FTCRobotParameters");
        minPowerXY = jsonReader.getDouble("MinPowerXY");
        minPowerRot = jsonReader.getDouble("MinPowerRotation");
        zeroRange = jsonReader.getDouble("ZeroRange");
        highPrecisionScalingFactor = jsonReader.getDouble("highPrecisionScalingFactor");
        highPrecisionRotationFactor = jsonReader.getDouble("highPrecisionRotationFactor");
        xyCoefficient = (1 - minPowerXY) / Math.pow(1 - zeroRange, 3);
        rotCoefficient = (1 - minPowerRot) / Math.pow(1 - zeroRange, 3);

        if(USING_SLOT_TRAY){
            this.myCubeTray = new SlotTray(hwmap, gamepad2);

        } else {
            this.myCubeTray = new CubeTray(hwmap, gamepad2, null);
        }


    }

    // Joystick Scaling

    // This scales the XY axes from joystick inputs to create an easier driver experience
    private double scaleXYAxes (double value, boolean highPrecisionMode) {
        if (value > zeroRange) {
            if (highPrecisionMode) return (value + minPowerXY) * highPrecisionScalingFactor;
            return xyCoefficient * Math.pow(value - zeroRange, 3) + minPowerXY;
        }
        if (value < -zeroRange) {
            if (highPrecisionMode) return (value - minPowerXY) * highPrecisionScalingFactor;
            return xyCoefficient * Math.pow(value + zeroRange, 3) - minPowerXY;
        }
        return 0.0;

    }

    // This scales the rotation axis from joystick inputs to create an easier driver experience
    private double scaleRotationAxis (double value, boolean highPrecisionMode) {
        if (value > zeroRange) {
            if (highPrecisionMode) return (value + minPowerRot) * highPrecisionRotationFactor;
            return rotCoefficient * Math.pow(value - zeroRange, 3) + minPowerRot;
        }
        if (value < -zeroRange) {
            if (highPrecisionMode) return (value - minPowerRot) * highPrecisionRotationFactor;
            return rotCoefficient * Math.pow(value + zeroRange, 3) - minPowerRot;
        }
        return 0.0;

    }

    // Basically all of teleop :)
    public void runGamepadCommands(){

        dpaddownStatus.recordNewValue(myGamepad2.dpad_down);
        dpadupStatus.recordNewValue(myGamepad2.dpad_up);


        /////// Driving - gamepad 1 left and right joysticks & Dpad /////

        // Get current direction
        boolean highPrecisionMode = myGamepad1.left_bumper;

        // compute speed. To return to old behaviour: set minPower and zeroZone to 0.0
        // compute for x & y
        double drivingX =   scaleXYAxes(myGamepad1.left_stick_x, highPrecisionMode);
        double drivingY = - scaleXYAxes(myGamepad1.left_stick_y, highPrecisionMode);

        // Read the Dpad for Direction Lock
        if (myGamepad1.dpad_up) {
            directionLock = 0;
        } else if (myGamepad1.dpad_left) {
            directionLock = 270;
        } else if (myGamepad1.dpad_down) {
            directionLock = 180;
        } else if (myGamepad1.dpad_right) {
            directionLock = 90;
        }

        // Check to see if the robot is using field centric rotation
        if (USE_FIELD_CENTRIC_ROTATION && mySwerveController.getFieldCentric()) {

            // If so, turn the rotation joystick into a vector, and get its angle to use for driving direction
            Vector rotationVector = new Vector(true, myGamepad1.right_stick_x, -myGamepad1.right_stick_y);

            // Change the direction lock if the joystick is sufficiently moved
            if (rotationVector.getMagnitude() > 0.8) directionLock = Math.toDegrees(rotationVector.getAngle());

            // Actually move the robot
            mySwerveController.steerSwerve(true, drivingX, drivingY, 0, directionLock);
            mySwerveController.moveRobot(highPrecisionMode);

        } else { // Otherwise use non-field centric rotation mode

            // Rotation Axis
            double drivingRotation = scaleRotationAxis(myGamepad1.right_stick_x, highPrecisionMode);

            // Scale rotation speed if the robot is translating (moving in the x or y directions)
            if (drivingX != 0 || drivingY != 0) {
                drivingRotation /= 2;
            }

            // Disable Directon Lock if robot is rotated
            if (drivingRotation != 0) {
                directionLock = -1;
            }

            mySwerveController.steerSwerve(true, drivingX, drivingY, drivingRotation, directionLock);
            mySwerveController.moveRobot(highPrecisionMode);
        }
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
        if(!DISABLE_DRIVER_INTAKE) {
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
        if(!DISABLE_LIFT) {
            myCubeTray.updateFromGamepad();
        }


        // relic arm
        if(!DISABLE_RELIC_ARM) {
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

    public void  doTelemetry() {

        myTelemetry.addData("Gamepad x", myGamepad1.left_stick_x);
        myTelemetry.addData("Gamepad y", myGamepad1.left_stick_y);
        myTelemetry.addData("Rotation", myGamepad1.right_stick_x);


        if (mySwerveController.getFieldCentric()) {
            myTelemetry.addData("Field Centric", "On");
        } else {
            myTelemetry.addData("Field Centric", "Off");
        }

        myTelemetry.addData("X Cord", mySwerveController.myEncoderTracker.getXinInches());
        myTelemetry.addData("Y Cord", mySwerveController.myEncoderTracker.getYinInches());

        myTelemetry.update();
    }

    public void recordGyroPosition() {
        myGyro.recordHeading();
    }
}
