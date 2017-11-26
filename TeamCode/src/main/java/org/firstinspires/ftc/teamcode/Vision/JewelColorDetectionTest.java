package org.firstinspires.ftc.teamcode.Vision;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by michaelzhou on 11/24/17.
 */
@Autonomous(name="Jewel Color Detection", group="Vision")
public class JewelColorDetectionTest extends LinearOpMode{

    private static final String TAG = "ftc9773 Jewel";
    private static final boolean DEBUG = true;

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        while(opModeIsActive()){

            JewelColorDetection obj = new JewelColorDetection();
//            telemetry.addData("verdict 1: ", obj.analyze());
            telemetry.addData(TAG, obj);
            telemetry.update();
//            telemetry.update();
        }
    }
}
