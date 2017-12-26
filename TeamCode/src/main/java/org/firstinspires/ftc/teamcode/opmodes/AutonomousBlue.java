package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;

/**
 * Created by Vikesh on 12/16/2017.
 */

public class AutonomousBlue extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        FTCrobot ftcRobot = new FTCrobot(hardwareMap, telemetry, gamepad1, gamepad2);
        ftcRobot.runRASI("autoblue");
    }
}
