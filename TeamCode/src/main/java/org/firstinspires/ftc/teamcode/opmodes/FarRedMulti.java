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
    private String[] rasiTag = new String[2];
    private RasiActions rasiActions;

    private JewelDetector.JewelColors jewelColors = JewelDetector.JewelColors.BLUE;
    private RelicRecoveryVuMark relicRecoveryVuMark = RelicRecoveryVuMark.CENTER;

    private double timeForVuforia;
    private double timeForJewelDetect;

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize Classes
        telemetry.addData("Classes", "Initializing...");
        telemetry.addData("RASI Status", "Waiting...");
        telemetry.update();

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

        /*
        // Read with vision
        JewelDetector detector = new JewelDetector(this);


        while (!opModeIsActive() && !isStopRequested()) {
            // initialize the vuforia section of vision routine
            // during this time, the robot checks for vuforia until either the time is up,
            // or a vumark is detected

            // we start by initializing vuforia
            VumarkGlyphPattern pattern = new VumarkGlyphPattern(hardwareMap);

            // create a temporary variable to store the mark value for this run
            RelicRecoveryVuMark tempMark;

            // run so long as either the timer is done or vuforia reads something;
            Timer vuforiaTimer = new Timer(timeForVuforia);
            while (!opModeIsActive() && !isStopRequested() && !vuforiaTimer.isDone()) {
                tempMark = pattern.getColumn();

                // if the vumark is known, update the official value and exit the loop
                if (tempMark != RelicRecoveryVuMark.UNKNOWN) {
                    relicRecoveryVuMark = tempMark;
                    break;
                }
                // report back to the driver station
                runVisionTelemetry("Vuforia", vuforiaTimer);
            }
            pattern.close();

            detector.startCamera();
            Timer jewelTimer = new Timer(timeForJewelDetect);
            JewelDetector.JewelColors tempColor;
            Log.i(TAG, "switching to Jewel camera");

            while (!opModeIsActive() && !isStopRequested() &&  !jewelTimer.isDone()) {
                tempColor = detector.computeJewelColor();

                // if the color is known update the 'official' value
                if(tempColor == JewelDetector.JewelColors.BLUE || tempColor == JewelDetector.JewelColors.RED) jewelColors = tempColor;
                runVisionTelemetry( "Jewel", jewelTimer);
                if(jewelTimer.isDone() )
                    break;
            }
            detector.stopCamera();
            Thread.sleep(100);
        }

*/

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

/*
ctload;
jwlarmd;
jwlarmc;
wait, 0.5;
RED:jwlarmr;
BLUE:jwlarml;
wait,0.25;
jwlarmu;
drvintkl, 1.0, 330, 25;
drvintake, 0.75, 330, 15;
turn, 270;
drvd, 1.0, 90, 42;
intki;
wait, 0.4;
turn, 270;
intks;
R:drvleftultra, 0.5, 17;
C:drvleftultra, 0.5, 23;
L:drvleftultra, 0.5, 29;
ctlow;
drvt, 0.5, 90, 1;

ctout;
wait, 1;
drvd, 1, 270, 3;
end;
 */