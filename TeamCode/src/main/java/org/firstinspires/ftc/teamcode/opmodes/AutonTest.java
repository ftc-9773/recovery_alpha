package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.teamcode.infrastructure.RasiActions;

/**
 * Created by vikesh on 1/7/18.
 */
@Autonomous(name = "RASI shakedown")
public class AutonTest extends LinearOpModeCamera{
    private RasiActions rasiActions;
    @Override
    public void runOpMode() throws InterruptedException {
        String[] rasiTags = {"C"};
        rasiActions = new RasiActions("testOpMode", rasiTags, this, gamepad1, gamepad2, telemetry, hardwareMap);
        waitForStart();
        rasiActions.runRasi();
    }
}
