package org.firstinspires.ftc.teamcode.Vision.DogeCVTests;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.detectors.JewelDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by michaelzhou on 1/7/18.
 */
@Autonomous(name="JewelOpMode", group="dogecv")

public class JewelOpMode extends LinearOpMode{
JewelDetector detector;
    @Override
    public void runOpMode() throws InterruptedException {
        detector = new JewelDetector();
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());

        detector.areaWeight = 0.02;
        detector.detectionMode = JewelDetector.JewelDetectionMode.MAX_AREA; // PERFECT_AREA
        //detector.perfectArea = 6500; <- Needed for PERFECT_AREA
        detector.debugContours = true;
        detector.maxDiffrence = 15;
        detector.ratioWeight = 15;
        detector.minArea = 700;

        detector.enable();

        waitForStart();
        while(opModeIsActive()){
            telemetry.addData("Current Order", "Jewel Order: " + detector.getCurrentOrder().toString()); // Current Result
            telemetry.addData("Last Order", "Jewel Order: " + detector.getLastOrder().toString()); // Last Known Result
            telemetry.update();
        }
        detector.disable();
    }
}
