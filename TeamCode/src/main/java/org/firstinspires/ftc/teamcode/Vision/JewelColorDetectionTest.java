package org.firstinspires.ftc.teamcode.Vision;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

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
//            for(String s : )
            String fileName = "/Users/michaelzhou/Desktop/ball images/b1.jpg";
            JewelColorDetection obj = new JewelColorDetection(fileName);
//            try{
            telemetry.addData("verdict 1: ", obj.analyze());
//            }catch (OutOfMemoryError e){
//                Log.e("ftc9773: ", "Size is too big!!!");
//            }


            String fileName2 = "/Users/michaelzhou/Desktop/ball images/b3.jpg";
            JewelColorDetection obj2 = new JewelColorDetection(fileName2);
            telemetry.addData("verdict 3: ", obj2.analyze());

            String fileName3 = "/Users/michaelzhou/Desktop/ball images/b4.jpg";
            JewelColorDetection obj3 = new JewelColorDetection(fileName3);
            telemetry.addData("verdict 4: ", obj3.analyze());

            String fileName4 = "/Users/michaelzhou/Desktop/ball images/b9.jpg";
            JewelColorDetection obj4 = new JewelColorDetection(fileName4);
            telemetry.addData("verdict 9: ", obj4.analyze());

            telemetry.update();
        }
    }
}
