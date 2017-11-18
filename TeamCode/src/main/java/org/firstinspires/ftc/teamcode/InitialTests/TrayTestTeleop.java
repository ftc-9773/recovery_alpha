package org.firstinspires.ftc.teamcode.InitialTests;

/**
 * Created by zacharye on 11/17/17.
 */

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;


@TeleOp(name = "CubeTrayTest")
public class TrayTestTeleop extends LinearOpMode {

    private static final String TAG = "ftc9773 CubeTrayTest";
    private CubeTray myCubeTray;

    @Override
    public void runOpMode() throws InterruptedException {
        Log.e(TAG, "Started initializing");

        myCubeTray = new CubeTray(hardwareMap, gamepad1,gamepad2);

        myCubeTray.goToStowPos();

        waitForStart();

        while (opModeIsActive()) {
            // integrated lift controlls - what would normally be used
            myCubeTray.updateFromGamepad();

            //allow user/tester to manually reset lift position

            if (gamepad2.right_bumper){
                myCubeTray.resetLiftPos();
            }
            // send telemetry back to driver station
            composeTelemetry();
            telemetry.update();
        }
    }

    private void composeTelemetry(){

        telemetry.addData("LeftFlap Position: ", myCubeTray.leftFlapPos);
        telemetry.addData("RightFlap Position: ", myCubeTray.rightFlapPos);
        telemetry.addData("LeftAngle position: ", myCubeTray.leftAnglePos);
        telemetry.addData("RightAngle position: ", myCubeTray.rightAnglePos);

        telemetry.addData("rawLiftPosition: ", myCubeTray.getRawLiftPos());
        telemetry.addData("scaledLiftPosition:  ", myCubeTray.getliftPos());

    }
}



