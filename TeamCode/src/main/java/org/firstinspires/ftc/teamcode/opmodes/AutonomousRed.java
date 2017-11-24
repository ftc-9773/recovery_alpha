package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;

/**
 * Created by Vikesh on 11/22/2017.
 */
@Autonomous(name = "AutonomousRed")
public class AutonomousRed extends LinearOpMode {
    FTCrobot ftcRobot;
    @Override
    public void runOpMode() throws InterruptedException {
        ftcRobot = new FTCrobot(hardwareMap);
        ftcRobot.runRASI("autored");
    }
}
