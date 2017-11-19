package org.firstinspires.ftc.teamcode.InitialTests;

/**
 * Created by zacharye on 11/17/17.
 */

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareControl.CubeTrayController;


@TeleOp(name = "CubeTrayTest")
public class TrayTestTeleop extends LinearOpMode {

    private static final String TAG = "ftc9773 CubeTrayTest";
    private CubeTrayController myCubeTrayController;

    @Override
    public void runOpMode() throws InterruptedException {
        Log.e(TAG, "Started initializing");

        myCubeTrayController = new CubeTrayController(hardwareMap, gamepad1,gamepad2);

        myCubeTrayController.goToStowPos();

        waitForStart();

        while (opModeIsActive()) {
            // integrated lift controlls - what would normally be used
            myCubeTrayController.updateFromGamepad();

            //allow user/tester to manually reset lift position

            if (gamepad2.right_bumper){
                myCubeTrayController.resetLiftPos();
            }
            // send telemetry back to driver station
            composeTelemetry();
            telemetry.update();
        }
    }

    private void composeTelemetry(){

        telemetry.addData("LeftFlap Position: ", myCubeTrayController.leftFlapPos);
        telemetry.addData("RightFlap Position: ", myCubeTrayController.rightFlapPos);
        telemetry.addData("LeftAngle position: ", myCubeTrayController.leftAnglePos);
        telemetry.addData("RightAngle position: ", myCubeTrayController.rightAnglePos);

        telemetry.addData("rawLiftPosition: ", myCubeTrayController.getRawLiftPos());
        telemetry.addData("scaledLiftPosition:  ", myCubeTrayController.getliftPos());

    }
}



