package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;
import org.firstinspires.ftc.teamcode.Vision.JewelDetector;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;
import org.firstinspires.ftc.teamcode.infrastructure.RasiActions;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.resources.ButtonStatus;
import org.firstinspires.ftc.teamcode.resources.Timer;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Vision.JewelDetector;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;
import org.firstinspires.ftc.teamcode.infrastructure.RasiActions;
import org.firstinspires.ftc.teamcode.resources.ButtonStatus;
import org.firstinspires.ftc.teamcode.resources.Timer;

/**
 * Created by vikesh on 1/26/18.
 */

@Autonomous(name = "Far Red Multi-Glyph")
public class FarRedMulti extends LinearOpModeCamera {
    private FTCrobot ftcrobot;
    private JewelDetector jewelDetector;
    private String[] rasiTag = new String[2];
    private RasiActions rasiActions;

    private JewelDetector.JewelColors jewelColors = JewelDetector.JewelColors.UNKNOWN;
    private RelicRecoveryVuMark relicRecoveryVuMark = RelicRecoveryVuMark.CENTER;

    double timeForVuforia;
    double timeForJewelDetect;

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize Classes
        telemetry.addData("Classes", "Initializing...");
        telemetry.addData("RASI Status", "Waiting...");
        telemetry.update();

        ftcrobot = new FTCrobot(hardwareMap, telemetry, gamepad1, gamepad2, this);
        jewelDetector = new JewelDetector(this);

        // Json
        SafeJsonReader AutoInitParameters = new SafeJsonReader("AutoInitParameters");
        timeForVuforia = AutoInitParameters.getDouble("timeForVuforia");
        timeForJewelDetect = AutoInitParameters.getDouble("timeForJewelDetect");

        // Rasi
        telemetry.addData("Classes", "Complete");
        telemetry.addData("RASI Status: ", "Initializing...");
        telemetry.update();

        rasiActions = new RasiActions("AutoRedFarMulti", null, this, gamepad1, gamepad2, telemetry, hardwareMap);

        telemetry.addData("Classes", "Complete");
        telemetry.addData("RASI Status: ", "Complete");
        telemetry.addData("", "");
        telemetry.addData("Vision", "Starting");
        telemetry.update();


        // Read with vision
        while (!opModeIsActive() && !isStopRequested()) {
            // initialize the vuforia section of vision routine
            // during this time, the robot checks for vuforia until either the time is up,
            // or a vumark is detected

            // we start by initializing vuforia
            Timer vuf = new Timer(timeForVuforia);
            VumarkGlyphPattern pattern = new VumarkGlyphPattern(hardwareMap);
            // create a temporary variable to store the mark value for this run
            RelicRecoveryVuMark tempMark;
            // run so long as either the
            while (!opModeIsActive() && !isStopRequested()&& !vuf.isDone()) {
                tempMark = pattern.getColumn();
                // if the vumark is known, update the official value
                Log.v(TAG, "temp mark is: " + tempMark);
                if(tempMark != RelicRecoveryVuMark.UNKNOWN) {
                    relicRecoveryVuMark = tempMark;
                    break;
                }

                // report back to the driver station
                runVisionTelemetry("Vuforia", vuf);
                if(vuf.isDone())  break;

            }
            pattern.close();

            Timer jewel = new Timer(timeForJewelDetect);
            jewelDetector.startCamera();
            JewelDetector.JewelColors tempColor;
            Log.i(TAG, "switching to Jewel camera");
            while (!opModeIsActive() && !isStopRequested() &&  !jewel.isDone()) {

                tempColor = jewelDetector.computeJewelColor();
                // if the color is known update the 'official' value
                Log.v(TAG, "temp color is: " + tempColor);

                if(tempColor != JewelDetector.JewelColors.NOT_INITIALIZED || tempColor != JewelDetector.JewelColors.UNKNOWN) {
                    jewelColors = tempColor;
                    break;
                }

                // Report back to driver station
                runVisionTelemetry("Jewel", jewel);

                if(jewel.isDone()) break;
            }
            jewelDetector.stopCamera();
        }

        waitForStart();

        // Pass RASI Tags

        rasiTag[0] = jewelColors.toString();
        rasiTag[1] = Character.toString(relicRecoveryVuMark.toString().charAt(0));

        rasiActions.rasiParser.rasiTag = rasiTag;

        telemetry.addData("rasitag[0] ", rasiTag[0]);
        telemetry.addData("rasitag[1] ", rasiTag[1]);
        telemetry.update();

        Log.e("Tag 0", rasiTag[0]);
        Log.e("Tag 1", rasiTag[1]);



        // DO EVERYTHING
        rasiActions.runRasi();
    }


    public void runVisionTelemetry(String reading, Timer timer) {
        telemetry.addData("Init", "Complete!");
        telemetry.addData("Vumark", relicRecoveryVuMark);
        telemetry.addData("Jewel", jewelColors);

        telemetry.addData("Reading " + reading, timer.timePassedInSeconds());
        telemetry.update();
    }
}

