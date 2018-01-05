package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;
import org.firstinspires.ftc.teamcode.HardwareControl.DriveWithPID;
import org.firstinspires.ftc.teamcode.HardwareControl.IntakeControllerManual;
import org.firstinspires.ftc.teamcode.HardwareControl.JewelServoController;
import org.firstinspires.ftc.teamcode.HardwareControl.RelicSystem;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.Vision.JewelDetector;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.resources.Timer;
import org.firstinspires.ftc.teamcode.sample_camera_opmodes.LinearDetectColor;

/**
 * Created by Vikesh on 12/16/2017.
 */

// Nicky's adb push command (from the adb directory): .\adb.exe push 'C:\Users\Nicky Eichenberger\Documents\FTC Software\recovery_alpha\TeamCode\src\main\java\org\firstinspires\ftc\teamcode\JSON\CloseBlueAutoParameters.json' /sdcard/FIRST/team9773/json18/
@Autonomous(name = "Close Blue Auto")
public class CloseBlueAuto extends LinearOpModeCamera {


    JewelDetector myJewelDetector = new JewelDetector(this);
    JewelServoController myJewelServo;
    IntakeControllerManual myIntakeControllerManual;
    SwerveController mySwerveController;
    CubeTray myCubeTray;
    Gyro myGyro;
    SafeJsonReader mySafeJsonReader = new SafeJsonReader("CloseBlueAutoParameters");
    DriveWithPID myDriveWithPID;
    RelicSystem myRelicSystem;

    char liftState;
    char autonomouspath;
    long[] times = {0,0,0,0,0};
    boolean intakeIsLowered = false;

    int intakeLowerTime;
    int intakeStartDelay;
    int intakeRunDuration;
    double drivingPower = 0.25;

    String colorString;
    RelicRecoveryVuMark mark;
    VumarkGlyphPattern vumarkPattern;


    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addData("Init", "Waiting...");
        telemetry.update();

        //init:
        vumarkPattern = new VumarkGlyphPattern(hardwareMap);
        myJewelServo = new JewelServoController(hardwareMap);
        myGyro = new Gyro(hardwareMap);
        myIntakeControllerManual = new IntakeControllerManual(hardwareMap);
        mySwerveController = new SwerveController(hardwareMap, myGyro, telemetry);
        myDriveWithPID = new DriveWithPID(mySwerveController, myGyro);
        myCubeTray = new CubeTray(hardwareMap, gamepad1, gamepad2);
//        autonomouspath = chooseAutonomousPath(true, (char)1);
        myCubeTray.setZeroFromCompStart();

        telemetry.addData("Init", "Success!!");
        telemetry.update();


        // JSON File Reading
        /////////////////////
        intakeLowerTime = mySafeJsonReader.getInt("intakeLowerTime");
        intakeRunDuration = mySafeJsonReader.getInt("intakeRunDuration");
        intakeStartDelay = mySafeJsonReader.getInt("intakeStartDelay");
        drivingPower = mySafeJsonReader.getDouble("drivingPower");
        double drivingHighPower = mySafeJsonReader.getDouble("drivingHighPower");
        double distToJewelPush = mySafeJsonReader.getDouble("distToJewelPush");
        double distBackToJewel = mySafeJsonReader.getDouble("distBackToJewel");
        double distPushLeft = mySafeJsonReader.getDouble("distPushLeft");
        double distPushRight = mySafeJsonReader.getDouble("distPushRight");
        int timePushRight = mySafeJsonReader.getInt("timePushRight");
        double distToCryptobox = mySafeJsonReader.getDouble("distToCryptobox");
        double extraDistToCenter = mySafeJsonReader.getDouble("extraDistToCenter");
        double extraDistToRight = mySafeJsonReader.getDouble("extraDistToRight");
        double raiseLiftTme = mySafeJsonReader.getDouble("raiseLiftTme");
        double rotateAngle = mySafeJsonReader.getDouble("rotateAngle");
        double pushCubeBackwards = mySafeJsonReader.getInt("pushCubeBackwards");
        double distDriveAwayFromCryptobox = mySafeJsonReader.getDouble("distDriveAwayFromCryptobox");
        double angleDriveAwayFromCryptobox = mySafeJsonReader.getDouble("angleDriveAwayFromCryptobox");
        double extraDistToLeft = mySafeJsonReader.getDouble("extraDistToLeft");
        double waitForJewelReading = mySafeJsonReader.getDouble("waitForJewelReading");


        // Start of Autonomous:

        // Read the vumark
        while (!opModeIsActive() && !isStopRequested()) {
            mark = vumarkPattern.getColumn();
            telemetry.addData("vuMark", mark);
            telemetry.addData("Init", "Success!!");
            telemetry.update();
        }

        waitForStart();

        // Read the jewel color
        myJewelDetector.startCamera();
        JewelDetector.JewelColors leftJewelColor = myJewelDetector.computeJewelColor();

        Timer time1 = new Timer(waitForJewelReading);
        while (!time1.isDone()) {
            leftJewelColor = myJewelDetector.computeJewelColor();
            if (leftJewelColor == JewelDetector.JewelColors.BLUE || leftJewelColor == JewelDetector.JewelColors.RED) {
                break;
            }
        }

        Log.e(TAG, "Jewel color is: " + leftJewelColor);

        // Display the readings
        telemetry.addData("Mark", mark);
        telemetry.addData("Left Jewel Color", leftJewelColor);
        telemetry.update();

        /*

        To read the CRYPTOBOX COLUMN: read 'mark' and compare to RelicRecoveryVuMark.LEFT <- whatever you want it to be

        For JEWEL COLOR: use myJewelDetector.isLeftJewelRed / blue

         */

        // Lower intake
        times[0] = System.currentTimeMillis();
        myCubeTray.setToPos(CubeTray.LiftFinalStates.LOADING);
        while(opModeIsActive() && System.currentTimeMillis()-intakeLowerTime<times[0]){
            myIntakeControllerManual.lowerIntake(true);
            myCubeTray.updatePosition();
        }
/*
        // Run intake
        times[1] = System.currentTimeMillis();
        myIntakeControllerManual.lowerIntake(false);
        while(System.currentTimeMillis()-intakeStartDelay<times[1]){}
        times[1] = System.currentTimeMillis();
        while(opModeIsActive()  && System.currentTimeMillis()-(intakeRunDuration)<times[1]){
            myIntakeControllerManual.RunIntake(0,-0.7);
        }
        myIntakeControllerManual.RunIntake(0,1);
        myIntakeControllerManual.RunIntake(0,0);
*/
        // Drive to the right
        myDriveWithPID.driveDist(drivingPower, 90, distToJewelPush);
        myDriveWithPID.driveDist(drivingPower, 180, distBackToJewel);

        //Push the jewel
        leftJewelColor = JewelDetector.JewelColors.RED;
        long tempTime;
        switch (leftJewelColor) {
            case RED:
                // Go 4" to the right and add the distance to next move
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 300) {
                    myJewelServo.lowerArm();

                }
                Log.i("9773CloseBlueAuto", "moving by time");
                myDriveWithPID.driveTime(drivingPower, 90, timePushRight);
                Log.i("9773CloseBlueAuto", "Done moving by time");
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 300) {
                    myJewelServo.raiseArm();

                }
                distToCryptobox += distPushRight;
                break;
            case BLUE:
                // Go 4" left and subtract the distance from next move
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 300) {
                    myJewelServo.lowerArm();

                }
                myDriveWithPID.driveDist(drivingHighPower, 270, distPushLeft);
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 300) {
                    myJewelServo.raiseArm();

                }
                distToCryptobox -= distPushLeft;
                break;
            default:
                // Do nothing
        }

        //Drive to cryptobox

        switch (mark) {
            case LEFT:
                //Add no extra distance
                distToCryptobox += extraDistToLeft;
                break;
            case RIGHT:
                //Add extra distance
                distToCryptobox += extraDistToRight;
                rotateAngle = 360 - rotateAngle;
                angleDriveAwayFromCryptobox = 360 - angleDriveAwayFromCryptobox;
                break;
            default:
                // If it is center, or if nothing is read
                // add extra distance to center
                distToCryptobox += extraDistToCenter;
        }
        myDriveWithPID.driveDist(drivingHighPower, 270, distToCryptobox);

        // Rotate robot
        myDriveWithPID.turnRobot(rotateAngle);

        // Put lift to vertical state
        myCubeTray.setToPos(CubeTray.LiftFinalStates.LOW);
        times[2] = System.currentTimeMillis();
        while (System.currentTimeMillis() - times[2] < raiseLiftTme) {
            myCubeTray.updatePosition();
        }
        myCubeTray.dump();
        myCubeTray.updatePosition();

        // Drive backwards
        myDriveWithPID.driveTime(drivingPower * 1.5, 180, pushCubeBackwards);

        // Save the current heading
        myGyro.recordHeading();

        // Drive away from the box
        myDriveWithPID.driveDist(drivingPower, angleDriveAwayFromCryptobox, distDriveAwayFromCryptobox);

        // Save the current heading
        myGyro.recordHeading();
    }



    /*
    public char chooseAutonomousPath(boolean redJewelIsLeft, char glyphPosition){
        char autonomousPath;
        switch (glyphPosition){
            case 0:
                if(redJewelIsLeft){autonomousPath = 0;}
                else{autonomousPath = 1;}
                break;
            case 1:
                if(redJewelIsLeft){autonomousPath = 2;}
                else{autonomousPath = 3;}
                break;
            case 2:
                if(redJewelIsLeft){autonomousPath = 4;}
                else{autonomousPath = 5;}
                break;
            default:
                autonomousPath = 6;
                break;
        }
        return autonomousPath;
    }
*/
}
