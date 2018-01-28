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
import org.firstinspires.ftc.teamcode.resources.Timer;

/**
 * Created by vikesh on 12/28/17.
 */
@Autonomous(name = "Peekskill Close Blue Auto")
public class NewCloseBlueAuto extends LinearOpModeCamera{

    JewelDetector myJewelDetector = new JewelDetector(this);
    JewelServoController myJewelServo;
    IntakeControllerManual myIntakeControllerManual;
    SwerveController mySwerveController;
    CubeTray myCubeTray;
    Gyro myGyro;
    SafeJsonReader mySafeJsonReader = new SafeJsonReader("NewCloseBlueAutoParameters");
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
        double distBack = mySafeJsonReader.getDouble("distBack");
        double distToCryptobox = mySafeJsonReader.getDouble("distToCryptobox");
        double extraDistToCenter = mySafeJsonReader.getDouble("extraDistToCenter");
        double extraDistToRight = mySafeJsonReader.getDouble("extraDistToRight");
        double rotateAngle = mySafeJsonReader.getDouble("rotateAngle");
        double pushCubeBackwards = mySafeJsonReader.getInt("pushCubeBackwards");
        double distDriveAwayFromCryptobox = mySafeJsonReader.getDouble("distDriveAwayFromCryptobox");
        double angleDriveAwayFromCryptobox = mySafeJsonReader.getDouble("angleDriveAwayFromCryptobox");
        double extraDistToLeft = mySafeJsonReader.getDouble("extraDistToLeft");
        double readJewelMaxTime = mySafeJsonReader.getDouble("readJewelMaxTime");


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
        JewelDetector.JewelColors leftJewelColor =  myJewelDetector.getJewelColor();

        /*

        To read the CRYPTOBOX COLUMN: read 'mark' and compare to RelicRecoveryVuMark.LEFT <- whatever you want it to be

        For JEWEL COLOR: use myJewelDetector.isLeftJewelRed / blue

         */

        // Lower intake
        myRelicSystem.runSequence(.75, false, false);
        Timer myTimer = new Timer(intakeLowerTime);
        while(opModeIsActive() && !myTimer.isDone()){}
        myRelicSystem.runSequence(-.75, false, false);

        //Detect Jewels
        Timer timer1 = new Timer(readJewelMaxTime);
        while (timer1.isDone()) {
            leftJewelColor = myJewelDetector.getJewelColor();
            if (leftJewelColor == JewelDetector.JewelColors.BLUE || leftJewelColor == JewelDetector.JewelColors.RED) {
                break;
            }
        }
        Log.e(TAG, "Jewel color is: " + leftJewelColor);

        // Display the readings
        telemetry.addData("Mark", mark);
        telemetry.addData("Left Jewel Color", leftJewelColor);
        telemetry.update();

        // Get Jewel servo to correct position
        myJewelServo.setToCenterPos();
        Timer myTimer1 = new Timer(0.25);
        while(opModeIsActive() && !myTimer1.isDone()){}

        //Move lift ot Jewel position
        myCubeTray.setToPos(CubeTray.LiftFinalStates.JEWEL);
        Timer myTimer2 = new Timer(2);
        while(opModeIsActive()&& !myTimer2.isDone()){myCubeTray.updatePosition();}

        //Drive to Jewels
        myDriveWithPID.driveDist(drivingPower, 180, distToJewelPush);

        //Push the jewel
        long tempTime;
        switch (leftJewelColor) {
            case BLUE:
                //move jewel servo left
                myJewelServo.setToRightPos();
                break;
            case RED:
                //move jewel servo right
                myJewelServo.setToLeftPos();
                break;
            default:
                //just sit there
        }

        //drive back a little
        myDriveWithPID.driveDist(drivingPower, 0, distBack);

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

        // eject cube
        myIntakeControllerManual.RunIntake(0,-1);

        //Drive a little forward
        myDriveWithPID.driveTime(drivingPower * 1.5, 18, pushCubeBackwards);

        myGyro.recordHeading();

        // Drive away from the box
        myDriveWithPID.driveDist(drivingPower, angleDriveAwayFromCryptobox, distDriveAwayFromCryptobox);

        myGyro.recordHeading();
    }
}
