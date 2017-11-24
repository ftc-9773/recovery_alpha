package org.firstinspires.ftc.teamcode.Vision;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by michaelzhou on 11/24/17.
 */
@Autonomous(name="Jewel Color Detection", group="Vision")
public class JewelColorDetectionTest extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        while(opModeIsActive()){
            String fileName = "/Users/";
            JewelColorDetection obj = new JewelColorDetection(fileName);
            telemetry.addData("verdict: ", obj.analyze());
            telemetry.update();
        }
    }
}
