package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

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

/**
 * Created by vikesh on 12/28/17.
 */
@Autonomous(name = "Close Red Auto")
public class CloseRedAuto extends LinearOpModeCamera{

    JewelDetector myJewelDetector = new JewelDetector(this);
    JewelServoController myJewelServo;
    IntakeControllerManual myIntakeControllerManual;
    SwerveController mySwerveController;
    CubeTray myCubeTray;
    Gyro myGyro;
    SafeJsonReader mySafeJsonReader = new SafeJsonReader("CloseRedAutoParameters");
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
        double distToJewelPush = mySafeJsonReader.getDouble("distToJewelPush");
        double distBackToJewel = mySafeJsonReader.getDouble("distBackToJewel");
        double distPushLeft = mySafeJsonReader.getDouble("distPushLeft");
        double distPushRight = mySafeJsonReader.getDouble("distPushRight");
        double distToCryptobox = mySafeJsonReader.getDouble("distToCryptobox");
        double extraDistToCenter = mySafeJsonReader.getDouble("extraDistToCenter");
        double extraDistToRight = mySafeJsonReader.getDouble("extraDistToRight");
        double raiseLiftTme = mySafeJsonReader.getDouble("raiseLiftTme");
        double rotateAngle = mySafeJsonReader.getDouble("rotateAngle");
        double pushCubeBackwards = mySafeJsonReader.getInt("pushCubeBackwards");
        double distDriveAwayFromCryptobox = mySafeJsonReader.getDouble("distDriveAwayFromCryptobox");
        double angleDriveAwayFromCryptobox = mySafeJsonReader.getDouble("angleDriveAwayFromCryptobox");
        double extraDistToLeft = mySafeJsonReader.getDouble("extraDistToLeft");


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
        JewelDetector.JewelColors leftJewelcolor =  myJewelDetector.getJewelColor();
        Log.e(TAG, "Jewel color is: " + leftJewelcolor);

        // Display the readings
        telemetry.addData("Mark", mark);
        telemetry.addData("Left Jewel Color", leftJewelcolor);
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
        long tempTime;
        switch (leftJewelcolor) {
            case BLUE:
                // Go 4" to the right and subtract the distance from next move
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 500) {
                    myJewelServo.lowerArm();

                }
                myDriveWithPID.driveDist(drivingPower, 90, distPushRight);
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 500) {
                    myJewelServo.raiseArm();

                }
                distToCryptobox -= distPushRight;
                break;
            case RED:
                // Go 4" left and add an distJewelPush to next move
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 500) {
                    myJewelServo.lowerArm();

                }
                myDriveWithPID.driveDist(drivingPower, 270, distPushLeft);
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 500) {
                    myJewelServo.raiseArm();

                }
                distToCryptobox += distPushLeft;
                break;
            default:
                // Do nothing
        }

        //Drive to cryptobox

        switch (mark) {
            case LEFT:
                //Add no extra distance
                distToCryptobox += extraDistToLeft;
                rotateAngle = 360 - rotateAngle;
                angleDriveAwayFromCryptobox = 360 - angleDriveAwayFromCryptobox;
                break;
            case RIGHT:
                //Add extra distance
                distToCryptobox += extraDistToRight;
                break;
            default:
                // If it is center, or if nothing is read
                // add extra distance to center
                distToCryptobox += extraDistToCenter;
        }
        myDriveWithPID.driveDist(drivingPower, 90, distToCryptobox);

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

        myGyro.recordHeading();

        // Drive away from the box
        myDriveWithPID.driveDist(drivingPower, angleDriveAwayFromCryptobox, distDriveAwayFromCryptobox);

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


//OLD VERSION (DO NOT DELETE!!!!!)
//
//package org.firstinspires.ftc.teamcode.opmodes;
//
//import android.util.Log;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//
//import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
//import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
//import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;
//import org.firstinspires.ftc.teamcode.HardwareControl.DriveWithPID;
//import org.firstinspires.ftc.teamcode.HardwareControl.IntakeControllerManual;
//import org.firstinspires.ftc.teamcode.HardwareControl.JewelServoController;
//import org.firstinspires.ftc.teamcode.HardwareControl.RelicSystem;
//import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
//import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
//import org.firstinspires.ftc.teamcode.Vision.JewelDetector;
//import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;
//import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
//
///**
// * Created by vikesh on 12/28/17.
// */
//@Autonomous(name = "Close Red Auto")
//public class CloseRedAuto extends LinearOpModeCamera{
//
//    JewelDetector myJewelDetector = new JewelDetector(this);
//    JewelServoController myJewelServo;
//    IntakeControllerManual myIntakeControllerManual;
//    SwerveController mySwerveController;
//    CubeTray myCubeTray;
//    Gyro myGyro;
//    SafeJsonReader mySafeJsonReader = new SafeJsonReader("CloseRedAutoParameters");
//    DriveWithPID myDriveWithPID;
//    RelicSystem myRelicSystem;
//
//    char liftState;
//    char autonomouspath;
//    long[] times = {0,0,0,0,0};
//    boolean intakeIsLowered = false;
//
//    int intakeLowerTime;
//    int intakeStartDelay;
//    int intakeRunDuration;
//    double drivingPower = 0.25;
//
//    String colorString;
//    RelicRecoveryVuMark mark;
//    VumarkGlyphPattern vumarkPattern;
//
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//
//        telemetry.addData("Init", "Waiting...");
//        telemetry.update();
//
//        //init:
//        vumarkPattern = new VumarkGlyphPattern(hardwareMap);
//        myJewelServo = new JewelServoController(hardwareMap);
//        myGyro = new Gyro(hardwareMap);
//        myIntakeControllerManual = new IntakeControllerManual(hardwareMap);
//        mySwerveController = new SwerveController(hardwareMap, myGyro, telemetry);
//        myDriveWithPID = new DriveWithPID(mySwerveController, myGyro);
//        myCubeTray = new CubeTray(hardwareMap, gamepad1, gamepad2);
////        autonomouspath = chooseAutonomousPath(true, (char)1);
//        myCubeTray.setZeroFromCompStart();
//
//        telemetry.addData("Init", "Success!!");
//        telemetry.update();
//
//
//        // JSON File Reading
//        /////////////////////
//        intakeLowerTime = mySafeJsonReader.getInt("intakeLowerTime");
//        intakeRunDuration = mySafeJsonReader.getInt("intakeRunDuration");
//        intakeStartDelay = mySafeJsonReader.getInt("intakeStartDelay");
//        drivingPower = mySafeJsonReader.getDouble("drivingPower");
//        double distToJewelPush = mySafeJsonReader.getDouble("distToJewelPush");
//        double distBackToJewel = mySafeJsonReader.getDouble("distBackToJewel");
//        double distPushLeft = mySafeJsonReader.getDouble("distPushLeft");
//        double distPushRight = mySafeJsonReader.getDouble("distPushRight");
//        double distToCryptobox = mySafeJsonReader.getDouble("distToCryptobox");
//        double extraDistToCenter = mySafeJsonReader.getDouble("extraDistToCenter");
//        double extraDistToRight = mySafeJsonReader.getDouble("extraDistToRight");
//        double raiseLiftTme = mySafeJsonReader.getDouble("raiseLiftTme");
//        double rotateAngle = mySafeJsonReader.getDouble("rotateAngle");
//        double pushCubeBackwards = mySafeJsonReader.getInt("pushCubeBackwards");
//        double distDriveAwayFromCryptobox = mySafeJsonReader.getDouble("distDriveAwayFromCryptobox");
//        double angleDriveAwayFromCryptobox = mySafeJsonReader.getDouble("angleDriveAwayFromCryptobox");
//        double extraDistToLeft = mySafeJsonReader.getDouble("extraDistToLeft");
//
//
//        // Start of Autonomous:
//
//        // Read the vumark
//        while (!opModeIsActive() && !isStopRequested()) {
//            mark = vumarkPattern.getColumn();
//            telemetry.addData("vuMark", mark);
//            telemetry.addData("Init", "Success!!");
//            telemetry.update();
//        }
//
//        waitForStart();
//
//        // Read the jewel color
//        myJewelDetector.startCamera();
//        String leftJewelcolor =  myJewelDetector.getJewelColor();
//        Log.e(TAG, "Jewel color is: " + leftJewelcolor);
//
//        // Display the readings
//        telemetry.addData("Mark", mark);
//        telemetry.addData("Left Jewel Color", leftJewelcolor);
//        telemetry.update();
//
//        /*
//
//        To read the CRYPTOBOX COLUMN: read 'mark' and compare to RelicRecoveryVuMark.LEFT <- whatever you want it to be
//
//        For JEWEL COLOR: use myJewelDetector.isLeftJewelRed / blue
//
//         */
//
//        // Lower intake
//        times[0] = System.currentTimeMillis();
//        myCubeTray.setToPos(CubeTray.LiftFinalStates.LOADING);
//        while(opModeIsActive() && System.currentTimeMillis()-intakeLowerTime<times[0]){
//            myIntakeControllerManual.lowerIntake(true);
//            myCubeTray.updatePosition();
//        }
///*
//        // Run intake
//        times[1] = System.currentTimeMillis();
//        myIntakeControllerManual.lowerIntake(false);
//        while(System.currentTimeMillis()-intakeStartDelay<times[1]){}
//        times[1] = System.currentTimeMillis();
//        while(opModeIsActive()  && System.currentTimeMillis()-(intakeRunDuration)<times[1]){
//            myIntakeControllerManual.RunIntake(0,-0.7);
//        }
//        myIntakeControllerManual.RunIntake(0,1);
//        myIntakeControllerManual.RunIntake(0,0);
//*/
//        // Drive to the right
//        myDriveWithPID.driveDist(drivingPower, 90, distToJewelPush);
//        myDriveWithPID.driveDist(drivingPower, 180, distBackToJewel);
//
//        //Push the jewel
//        long tempTime;
//        switch (leftJewelcolor) {
//            case "Blue is Left":
//                // Go 4" to the right and subtract the distance from next move
//                tempTime = System.currentTimeMillis();
//                while (System.currentTimeMillis() - tempTime < 500) {
//                    myJewelServo.lowerArm();
//
//                }
//                myDriveWithPID.driveDist(drivingPower, 90, distPushRight);
//                tempTime = System.currentTimeMillis();
//                while (System.currentTimeMillis() - tempTime < 500) {
//                    myJewelServo.raiseArm();
//
//                }
//                distToCryptobox -= distPushRight;
//                break;
//            case "Red is left":
//                // Go 4" left and add an distJewelPush to next move
//                tempTime = System.currentTimeMillis();
//                while (System.currentTimeMillis() - tempTime < 500) {
//                    myJewelServo.lowerArm();
//
//                }
//                myDriveWithPID.driveDist(drivingPower, 270, distPushLeft);
//                tempTime = System.currentTimeMillis();
//                while (System.currentTimeMillis() - tempTime < 500) {
//                    myJewelServo.raiseArm();
//
//                }
//                distToCryptobox += distPushLeft;
//                break;
//            default:
//                // Do nothing
//        }
//
//        //Drive to cryptobox
//
//        switch (mark) {
//            case LEFT:
//                //Add no extra distance
//                distToCryptobox += extraDistToLeft;
//                rotateAngle = 360 - rotateAngle;
//                angleDriveAwayFromCryptobox = 360 - angleDriveAwayFromCryptobox;
//                break;
//            case RIGHT:
//                //Add extra distance
//                distToCryptobox += extraDistToRight;
//                break;
//            default:
//                // If it is center, or if nothing is read
//                // add extra distance to center
//                distToCryptobox += extraDistToCenter;
//        }
//        myDriveWithPID.driveDist(drivingPower, 90, distToCryptobox);
//
//        // Rotate robot
//        myDriveWithPID.turnRobot(rotateAngle);
//
//        // Put lift to vertical state
//        myCubeTray.setToPos(CubeTray.LiftFinalStates.LOW);
//        times[2] = System.currentTimeMillis();
//        while (System.currentTimeMillis() - times[2] < raiseLiftTme) {
//            myCubeTray.updatePosition();
//        }
//        myCubeTray.dump();
//        myCubeTray.updatePosition();
//
//        // Drive backwards
//        myDriveWithPID.driveTime(drivingPower * 1.5, 180, pushCubeBackwards);
//
//        myGyro.recordHeading();
//
//        // Drive away from the box
//        myDriveWithPID.driveDist(drivingPower, angleDriveAwayFromCryptobox, distDriveAwayFromCryptobox);
//
//        myGyro.recordHeading();
//    }
//
//
//
//    /*
//    public char chooseAutonomousPath(boolean redJewelIsLeft, char glyphPosition){
//        char autonomousPath;
//        switch (glyphPosition){
//            case 0:
//                if(redJewelIsLeft){autonomousPath = 0;}
//                else{autonomousPath = 1;}
//                break;
//            case 1:
//                if(redJewelIsLeft){autonomousPath = 2;}
//                else{autonomousPath = 3;}
//                break;
//            case 2:
//                if(redJewelIsLeft){autonomousPath = 4;}
//                else{autonomousPath = 5;}
//                break;
//            default:
//                autonomousPath = 6;
//                break;
//        }
//        return autonomousPath;
//    }
//*/
//}
