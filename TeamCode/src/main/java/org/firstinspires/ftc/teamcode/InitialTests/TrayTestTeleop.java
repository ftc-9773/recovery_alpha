package org.firstinspires.ftc.teamcode.InitialTests;

/**
 * Created by zacharye on 11/17/17.
 */

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTrayController;


@TeleOp(name = "CubeTrayTest")
public class TrayTestTeleop extends LinearOpMode {

    private static final String TAG = "ftc9773 CubeTrayTest";
    private CubeTray myCubeTrayController;

    @Override
    public void runOpMode() throws InterruptedException {
        Log.e(TAG, "Started initializing");


        myCubeTrayController = new CubeTray(hardwareMap, gamepad1, null);

        myCubeTrayController.setServoPos(CubeTray.TrayPositions.STOWED);

            myCubeTrayController.homeLiftVersA();



        waitForStart();
        myCubeTrayController.setServoPos(CubeTray.TrayPositions.CARRYING);

        while (opModeIsActive()) {
            // integrated lift controlls - what would normally be used
            myCubeTrayController.updateFromGamepad();

            //
            myCubeTrayController.liftMotor.setTargetPosition(myCubeTrayController.liftTargetPosition);
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

        telemetry.addData("targetPosition set: ", myCubeTrayController.liftMotor.getTargetPosition());


    }
}


