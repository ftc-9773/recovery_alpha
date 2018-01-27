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
 * Created by nicky on 1/2/18.
 */

@Autonomous(name = "Red Pit-side Auto")
public class FarRedAuto extends LinearOpModeCamera {


    JewelDetector myJewelDetector = new JewelDetector(this);
    JewelServoController myJewelServo;
    IntakeControllerManual myIntakeControllerManual;
    SwerveController mySwerveController;
    CubeTray myCubeTray;
    Gyro myGyro;
    SafeJsonReader mySafeJsonReader = new SafeJsonReader("FarRedAutoParameters");
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
        myDriveWithPID = new DriveWithPID(mySwerveController, myGyro, this);
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
        double distSidewaysToCryptobox = mySafeJsonReader.getDouble("distSidewaysToCryptobox");
        double angleLeftColumn = mySafeJsonReader.getDouble("angleLeftColumn");
        double distToLeftColumn = mySafeJsonReader.getDouble("distToLeftColumn");
        double angleCenterColumn = mySafeJsonReader.getDouble("angleCenterColumn");
        double distToCenterColumn = mySafeJsonReader.getDouble("distToCenterColumn");
        double angleRightColumn = mySafeJsonReader.getDouble("angleRightColumn");
        double distToRightColumn = mySafeJsonReader.getDouble("distToRightcolumn");
        int raiseLiftTime = mySafeJsonReader.getInt("raiseLiftTime");
        double timePushCubeBackwards = mySafeJsonReader.getDouble("timePushCubeBackwards");
        double distDriveAwayFromCryptobox = mySafeJsonReader.getDouble("distDriveAwayFromCryptobox");

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
        leftJewelcolor = JewelDetector.JewelColors.RED;
        long tempTime;
        switch (leftJewelcolor) {
            case BLUE:
                // Go 4" to the right and subtract the distance from next move
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 500) {
                   // myJewelServo.lowerArm();

                }
                myDriveWithPID.driveDist(drivingPower, 90, distPushRight);
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 500) {
                   // myJewelServo.raiseArm();

                }
                distSidewaysToCryptobox -= distPushRight;
                break;
            case RED:
                // Go 4" left and add an distJewelPush to next move
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 500) {
                 //   myJewelServo.lowerArm();

                }
                myDriveWithPID.driveDist(drivingPower, 270, distPushLeft);
                tempTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - tempTime < 500) {
                  //  myJewelServo.raiseArm();

                }
                distSidewaysToCryptobox += distPushLeft;
                break;
            default:
                // Do nothing
        }

        // Drive sideways towards the cryptobox
        myDriveWithPID.driveDist(drivingPower, 90, distSidewaysToCryptobox);

        // Figure out the corect drive dists and angles
        double drivingAngle;
        double drivingDist;
        switch (mark) {
            case LEFT:
                drivingAngle = angleLeftColumn;
                drivingDist = distToLeftColumn;
                break;
            case RIGHT:
                drivingAngle = angleRightColumn;
                drivingDist = distToRightColumn;
                break;
            default:
                drivingAngle = angleCenterColumn;
                drivingDist = distToCenterColumn;
        }

        // Turn to correct angle
        myDriveWithPID.turnRobot(drivingAngle);

        // Drive to the collumn
        myDriveWithPID.driveDist(drivingPower, 0, drivingDist);

        // Raise lift and drop cube
        myCubeTray.setToPos(CubeTray.LiftFinalStates.LOW);
        tempTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - tempTime < raiseLiftTime) {
            myCubeTray.updatePosition();
        }
        myCubeTray.dump();
        myCubeTray.updatePosition();

        // Drive backwards for time
        myDriveWithPID.driveTime(drivingPower*1.5, 90, timePushCubeBackwards);

        myGyro.recordHeading();

        // Drive away from cryptobox
        myDriveWithPID.driveDist(drivingPower*1.5, drivingAngle, distDriveAwayFromCryptobox);

        myGyro.recordHeading();
    }
}
