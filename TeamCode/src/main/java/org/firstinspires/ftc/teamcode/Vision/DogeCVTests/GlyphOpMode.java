package org.firstinspires.ftc.teamcode.Vision.DogeCVTests;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.detectors.GlyphDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by michaelzhou on 1/15/18.
 */

@Autonomous(name="GlyphOpMode", group="dogecv")
public class GlyphOpMode extends LinearOpMode{
    private GlyphDetector glyphDetector;
    @Override
    public void runOpMode() throws InterruptedException {
        glyphDetector = new GlyphDetector();
        glyphDetector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        glyphDetector.minScore = 1;
        glyphDetector.downScaleFactor = 0.3;
        glyphDetector.speed = GlyphDetector.GlyphDetectionSpeed.SLOW;
        glyphDetector.enable();

        waitForStart();

        while(opModeIsActive()){
            telemetry.addData("Glyph Pos X", glyphDetector.getChosenGlyphOffset());
            telemetry.addData("Glyph Pos Offest", glyphDetector.getChosenGlyphPosition().toString());
        }
    }
}
