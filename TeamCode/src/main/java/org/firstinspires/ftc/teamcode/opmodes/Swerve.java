package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.HardwareControl.IntakeController;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;

/**
 * Created by Vikesh on 10/29/2017.
 */

@TeleOp(name = "swerveOpMode")
public class Swerve extends LinearOpMode {

    private static final String TAG = "ftc9773 Swerve";
    private static final boolean DEBUG = false;

    private static final boolean ENABLEDRIVING = true;

    private SwerveController mySwerveController;
    private IntakeController myIntakeController;
    private Gyro myGyro;

    //TEST cubeTray

    @Override
    public void runOpMode() throws InterruptedException {
        Log.e(TAG, "Started initializing");

        // Create objects
        myGyro = new Gyro(hardwareMap);
        mySwerveController = new SwerveController(hardwareMap, myGyro, true);
        myIntakeController = new IntakeController(hardwareMap);


        myGyro.setZeroPosition();

        waitForStart();
        while (opModeIsActive()) {


            double timeStart = System.currentTimeMillis();

            // Intake
            if (gamepad1.right_trigger > 0) {
                myIntakeController.runIntakeIn();
            } else if (gamepad1.right_bumper) {
                myIntakeController.runIntakeOut();
            } else {
                myIntakeController.intakeOff();
            }

            int positionLock = -1;
            if (gamepad1.dpad_up) {
                positionLock = 0;
            } else if (gamepad1.dpad_right) {
                positionLock = 1;
            } else if (gamepad1.dpad_down) {
                positionLock = 2;
            } else if (gamepad1.dpad_left) {
                positionLock = 3;
            }

            mySwerveController.steerSwerve(true, gamepad1.left_stick_x, gamepad1.left_stick_y * -1, gamepad1.right_stick_x, positionLock);

            if (ENABLEDRIVING) {
                mySwerveController.moveRobot();
            }

            // Display gamepad values
            telemetry.addData("Gamepad x:", gamepad1.left_stick_x);
            telemetry.addData("Gamepad y:", gamepad1.left_stick_y);

            telemetry.addData("Front Left Heading: ", Math.toDegrees(mySwerveController.flwVector.getAngle()));
            telemetry.addData("Front Left Position: ", Math.toDegrees(mySwerveController.flwModule.currentPosition));
            telemetry.addData("Front Left Error Value: ", mySwerveController.flwModule.errorAmt);
            telemetry.addData("Front Left Servo Power: ", mySwerveController.flwModule.tellServo);
            telemetry.addData("Front Left Motor Power: ", mySwerveController.flwVector.getMagnitude());

            telemetry.update();

            if (DEBUG) { Log.e(TAG, "Time Loop End : " + (System.currentTimeMillis() - timeStart)); }
        }
    }
}
