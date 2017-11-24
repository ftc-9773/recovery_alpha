package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.infrastructure.controlParser;

/**
 * Created by Vikesh on 11/19/2017.
 */
@Autonomous(name = "testbed")
public class testbed extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException {
        String[] command = new String[4];
        controlParser control = new controlParser("test");
        command = control.getNextCommand();

        waitForStart();

        while(opModeIsActive()) {
            switch (command[0]) {
                case "m1":
                    telemetry.addData("Command Type: ", "m1");
                    break;
                case "m2":
                    telemetry.addData("Command Type: ", "m2");
                    break;
                default:
                    telemetry.addData("Error: ", "No data found");
                    break;
            }
            telemetry.update();
        }
    }
}
