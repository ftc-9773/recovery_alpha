package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
/**
 * Created by Vikesh on 10/29/2017.
 */

@TeleOp(name = "Swerve")
public class Swerve extends LinearOpMode {

    private static final String TAG = "ftc9773 Swerve";
    private static final boolean ENABLEDRIVING = false ;

    private SwerveController mySwerveController;

    @Override
    public void runOpMode() throws InterruptedException {
        Log.e(TAG, "Started initializing");

        mySwerveController = new SwerveController(hardwareMap);

        waitForStart();
        while(opModeIsActive()) {

            mySwerveController.pointDirection(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
            if (ENABLEDRIVING) {
                mySwerveController.moveRobot();
            }

            // Display gamepad values
            telemetry.addData("Gamepad x:", gamepad1.left_stick_x);
            telemetry.addData("Gamepad y:", gamepad1.left_stick_y);

            telemetry.update();
        }
    }
}
