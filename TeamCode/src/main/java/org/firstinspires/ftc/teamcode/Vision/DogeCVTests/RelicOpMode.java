package org.firstinspires.ftc.teamcode.Vision.DogeCVTests;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.detectors.GenericDetector;
import com.disnodeteam.dogecv.filters.LeviColorFilter;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.opencv.core.Size;

/**
 * Created by michaelzhou on 1/7/18.
 */

public class RelicOpMode extends LinearOpMode{
    GenericDetector genericDetector;

    @Override
    public void runOpMode() throws InterruptedException {
        genericDetector = new GenericDetector();
        genericDetector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        genericDetector.colorFilter = new LeviColorFilter(LeviColorFilter.ColorPreset.YELLOW);
        //genericDetector.colorFilter = new HSVColorFilter(new Scalar(30,200,200), new Scalar(15,50,50));
        genericDetector.debugContours = false;
        genericDetector.minArea = 700;
        genericDetector.perfectRatio = 1.8;
        genericDetector.stretch = true;
        genericDetector.stretchKernal = new Size(2,50);
        genericDetector.enable();

        waitForStart();
        while (opModeIsActive()){
            telemetry.addData("Location", genericDetector.getLocation().toString());
            telemetry.addData("Rect", genericDetector.getRect().toString());
        }
        genericDetector.disable();
    }
}
