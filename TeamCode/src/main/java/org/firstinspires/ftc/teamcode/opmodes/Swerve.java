package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
/**
 * Created by Vikesh on 10/29/2017.
 */

@TeleOp(name = "Swerve")
public class Swerve extends LinearOpMode {

    private static final String TAG = "ftc9773 Swerve";
    private static final boolean ENABLEDRIVING = true ;

    private SwerveController mySwerveController;

    @Override
    public void runOpMode() throws InterruptedException {
        Log.e(TAG, "Started initializing");

        mySwerveController = new SwerveController(hardwareMap);
        waitForStart();
        while(opModeIsActive()) {

            mySwerveController.pointDirection(gamepad1.left_stick_y * -1, gamepad1.left_stick_x * -1, gamepad1.right_stick_x);

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
        }
    }
}
