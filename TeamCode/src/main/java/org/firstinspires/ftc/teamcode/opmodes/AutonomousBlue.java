package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;
import org.firstinspires.ftc.teamcode.sample_camera_opmodes.LinearDetectColor;

/**
 * Created by Vikesh on 12/16/2017.
 */
@Autonomous(name = "AutoBlue")
public class AutonomousBlue extends LinearOpMode {

    String jewelColor;
    LinearDetectColor jewelDetectColor = new LinearDetectColor();

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        
        while(opModeIsActive()) {
            jewelColor = jewelDetectColor.detectJewelColor();
            telemetry.addData("color: ", jewelColor);
        }
    }
}
