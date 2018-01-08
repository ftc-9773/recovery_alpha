package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;

/**
 * Created by Vikesh on 11/25/2017.
 */
@TeleOp(name = "BasicTeleOp")
@Disabled

public class BasicTeleOp extends LinearOpModeCamera{
    private FTCrobot ftcRobot;
    @Override
    public void runOpMode() throws InterruptedException {
        ftcRobot = new FTCrobot(hardwareMap, telemetry, gamepad1, gamepad2, this);
        waitForStart();
        while(opModeIsActive()){
            ftcRobot.runGamepadCommands();
            telemetry.update();
        }
    }
}
