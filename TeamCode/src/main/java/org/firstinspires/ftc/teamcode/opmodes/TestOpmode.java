package org.firstinspires.ftc.teamcode.opmodes;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.HardwareControl.DistanceColorSensor;
import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;
/*
 * Created by zacharye on 11/19/17.
 */

// an opmode to test teleop, based on the progress ew have made so far
// should include all of the sub- assembleys made so far
@Autonomous(name = "Auton Drive Test")
public class TestOpmode extends LinearOpModeCamera {

    private FTCrobot ftcRobot;
    private DistanceColorSensor distanceSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Init:", "waiting...");
        telemetry.update();
        ftcRobot = new FTCrobot(hardwareMap, telemetry, gamepad1, gamepad2, this);


        telemetry.addData("Init", "Successful!!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Left Ultrasonic", ftcRobot.myDriveWithPID.leftUltrasonicSensor.getDistance(DistanceUnit.INCH));
            telemetry.addData("Right Ultrasonic", ftcRobot.myDriveWithPID.rightUltrasonicSensor.getDistance(DistanceUnit.INCH));
            telemetry.update();
            Thread.sleep(100);
        }
    }

}

