package org.firstinspires.ftc.teamcode.opmodes;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareControl.IntakeController;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTrayController;
/**
 * Created by zacharye on 11/19/17.
 */

// an opmode to test teleop, based on the progress ew have made so far
// should include all of the sub- assembleys made so far
/*
@TeleOp(name = "Swerve")
public class TestOpmode extends LinearOpMode {

    private static final String TAG = "ftc9773 TestOpmode";
    private static final boolean DEBUG_SWERVE = false;
    private static final boolean DEBUG_CUBE_TRAY = false ;

    private SwerveController mySwerveController;
    private IntakeController myIntakeController;
    private CubeTrayController myCubeTrayController;

    //TEST cubeTray

    @Override
    public void runOpMode() throws InterruptedException {
        Log.e(TAG, "Started initializing");

        // Create objects
        mySwerveController = new SwerveController(hardwareMap);
        myIntakeController = new IntakeController(hardwareMap);
        myCubeTrayController = new CubeTrayController(hardwareMap, gamepad2);

        waitForStart();
        while(opModeIsActive()) {

            // update the intake object
            myIntakeController.runIntake();

            // update and move swerve drive
            mySwerveController.pointDirection(gamepad1.left_stick_y * -1, gamepad1.left_stick_x * -1, gamepad1.right_stick_x);
            mySwerveController.moveRobot();
            // update cube tray
            myCubeTrayController.updateFromGamepad();

            // telemetery
            composeTelemetry();
            telemetry.update();
        }
    }

    // add all and any data needed to debug/monitor robot
    private void composeTelemetry(){

        // display gamepad values
        telemetry.addData("Gamepad x:", gamepad1.left_stick_x);
        telemetry.addData("Gamepad y:", gamepad1.left_stick_y);

        // prints values important to swerve drive
        if (DEBUG_SWERVE) {
            telemetry.addData("Front Left Heading: ", Math.toDegrees(mySwerveController.flwVector.getAngle()));
            telemetry.addData("Front Left Position: ", Math.toDegrees(mySwerveController.flwModule.currentPosition));
            telemetry.addData("Front Left Error Value: ", mySwerveController.flwModule.errorAmt);
            telemetry.addData("Front Left Servo Power: ", mySwerveController.flwModule.tellServo);
            telemetry.addData("Front Left Motor Power: ", mySwerveController.flwVector.getMagnitude());
        }

        // prints values important to debugging cube tray
        if (DEBUG_CUBE_TRAY){

            telemetry.addData("rawLiftPosition: ", myCubeTrayController.getRawLiftPos());
            telemetry.addData("scaledLiftPosition:  ", myCubeTrayController.getliftPos());
        }
    }
} */
