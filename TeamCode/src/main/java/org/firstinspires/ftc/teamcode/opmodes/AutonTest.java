package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.teamcode.infrastructure.RasiActions;

/**
 * Created by vikesh on 1/7/18.
 */

/* to push the rasi file:
cd TeamCode/src/main/java/org/firstinspires/ftc/teamcode/RASI/

~/Library/Android/sdk/platform-tools/adb push RASI/AutonTesting.rasi /sdcard/FIRST/team9773/rasi18
 */
@Autonomous(name = "RASI shakedown")
public class AutonTest extends LinearOpModeCamera{
    private RasiActions rasiActions;
    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addData("Init", "Waiting...");
        telemetry.update();

        String[] rasiTags = {"C"};
        rasiActions = new RasiActions("AutonTesting", rasiTags, this, gamepad1, gamepad2, telemetry, hardwareMap);

        telemetry.addData("Init", "Complete");
        telemetry.update();
        waitForStart();
        rasiActions.runRasi();
    }
}
