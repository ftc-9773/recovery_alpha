package org.firstinspires.ftc.teamcode.opmodes;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;
import org.firstinspires.ftc.teamcode.HardwareControl.IntakeController;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
/*
 * Created by zacharye on 11/19/17.
 */

// an opmode to test teleop, based on the progress ew have made so far
// should include all of the sub- assembleys made so far
@Autonomous(name = "Auton Drive Test")
public class TestOpmode extends LinearOpMode {

    private FTCrobot ftcRobot;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Init:", "waiting...");
        telemetry.update();
        ftcRobot = new FTCrobot(hardwareMap, telemetry, gamepad1, gamepad2);

        Log.i("Start", "Is starting");

        while (!opModeIsActive()) {
            telemetry.addData("Init", "Successful!!");
            telemetry.update();
        }
        waitForStart();
        Log.i("Starting auton", "Is starging");
        for (int i = 0; i < 10; i++) {
            telemetry.addData("Cycle", i);
            telemetry.update();

            ftcRobot.myDriveWithPID.driveDist(0.6, 00, 12);
            ftcRobot.myDriveWithPID.driveDist(0.6, 90, 12);
            ftcRobot.myDriveWithPID.driveDist(0.6, 180, 12);
            ftcRobot.myDriveWithPID.driveDist(0.6, 270, 12);
        }
    }

}

