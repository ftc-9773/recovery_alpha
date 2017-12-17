package org.firstinspires.ftc.teamcode.opmodes;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareControl.IntakeController;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
/*
 * Created by zacharye on 11/19/17.
 */

// an opmode to test teleop, based on the progress ew have made so far
// should include all of the sub- assembleys made so far
@TeleOp(name = "TestOpMode")
@Disabled

public class TestOpmode extends LinearOpMode {

    private static final String TAG = "ftc9773 TestOpmode";
    private static final boolean DEBUG_SWERVE = false;
    private static final boolean DEBUG_CUBE_TRAY = false;
    private static final double  intakePowerTol = .2 ;

    // private SwerveController mySwerveController;
    private IntakeController myIntakeController;
    private CubeTray myCubeTrayController;
    private SwerveController mySwerveController ;

    // util stuff
    private Gyro myGyro;



    //TEST cubeTray

    @Override
    public void runOpMode() throws InterruptedException {
        Log.e(TAG, "Started initializing");

        // Create objects
        myGyro = new Gyro(hardwareMap);
        myIntakeController = new IntakeController(hardwareMap);
        myCubeTrayController = new CubeTray(hardwareMap, gamepad2, null);
        mySwerveController = new SwerveController(hardwareMap, myGyro, telemetry);

        //initialise processes
        myGyro.setZeroPosition();
        myCubeTrayController.setStartPosition(CubeTray.LiftFinalStates.LOADING);



        waitForStart();
        while (opModeIsActive()) {

            // move swerve drive
            mySwerveController.pointModules(true, gamepad1.left_stick_x,gamepad1.left_stick_y,gamepad1.right_stick_x);
            mySwerveController.moveRobot();

            // update the intake object
            if (gamepad2.right_stick_y < 0 - intakePowerTol){
                myIntakeController.runIntakeIn();
            } else if (gamepad2.right_stick_y > 0 + intakePowerTol){
                myIntakeController.runIntakeOut();
            } else {
                myIntakeController.intakeOff();
            }

            // update cube tray
            myCubeTrayController.updateFromGamepad();

            // telemetery
            composeTelemetry();
            telemetry.update();
        }
    }

    // add all and any data needed to debug/monitor robot
    private void composeTelemetry() {

        // display gamepad values
        telemetry.addData("Gamepad x:", gamepad1.left_stick_x);
        telemetry.addData("Gamepad y:", gamepad1.left_stick_y);

        // prints values important to swerve drive
        if (DEBUG_SWERVE) {
            // telemetry.addData("Front Left Heading: ", Math.toDegrees(mySwerveController.flwVector.getAngle()));
            // telemetry.addData("Front Left Position: ", Math.toDegrees(mySwerveController.flwModule.currentPosition));
            //  telemetry.addData("Front Left Error Value: ", mySwerveController.flwModule.errorAmt);
            //  telemetry.addData("Front Left Servo Power: ", mySwerveController.flwModule.tellServo);
            //  telemetry.addData("Front Left Motor Power: ", mySwerveController.flwVector.getMagnitude());
        }

        // prints values important to debugging cube tray
        if (DEBUG_CUBE_TRAY) {

            telemetry.addData("rawLiftPosition: ", myCubeTrayController.getRawLiftPos());
            telemetry.addData("scaledLiftPosition:  ", myCubeTrayController.getliftPos());
        }
    }
}

