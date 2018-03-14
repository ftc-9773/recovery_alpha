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

@Autonomous(name = "Far Red Multi-Glyph", group = "Auto Paths:")
public class FarRedMulti extends LinearOpModeCamera {
    private String[] rasiTag = new String[2];
    private RasiActions rasiActions;

    private JewelDetector.JewelColors jewelColors = JewelDetector.JewelColors.UNKNOWN;
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
                if (vuforiaTimer.isDone()) break;
                if(isStarted() || isStopRequested()) break;
            }
            if(isStarted() || isStopRequested()) break;
            pattern.close();
            if(isStarted() || isStopRequested()) break;

            detector.startCamera();
            Timer jewelTimer = new Timer(timeForJewelDetect);
            JewelDetector.JewelColors tempColor;
            Log.i(TAG, "switching to Jewel camera");

            while (!opModeIsActive() && !isStopRequested() &&  !jewelTimer.isDone()) {
                tempColor = detector.computeJewelColor();

                // if the color is known update the 'official' value
                if(tempColor == JewelDetector.JewelColors.BLUE || tempColor == JewelDetector.JewelColors.RED){
                    jewelColors = tempColor;
                    break;
                }
                runVisionTelemetry( "Jewel", jewelTimer);

                if(jewelTimer.isDone() ) break;
                if(isStarted() || isStopRequested()) break;
            }
            if(isStarted() || isStopRequested()) break;
            detector.stopCamera();
            if(isStarted() || isStopRequested()) break;

            if (opModeIsActive()) break;
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

// Known to work decently:
/*
ctload;
jwlarmd;
jwlarmc;
wait, 0.75;
RED:jwlarmr;
BLUE:jwlarml;
wait, 0.15;
jwlarmtempu;
drvdropintk, 1.0, 340, 33;
jwlarmu;
drvintake, 1.0, 340, 15, 0;
drvd, 1.0, 90, 33;
intko;
wait, 0.6;
intki;
turn, 270;
intks;
ctlow;
R: drvleftultra, 1, 15;
C: drvleftultra, 1, 19;
L: drvleftultra, 1, 30;
turn 270;
drvt, 0.8, 90, 1;
ctout;
wait, 0.5;
drvddumb, 0.5, 270, 5;
stopdump;
drvleftultra, 1, 36, 0;
ctload;
turn, 300;
drvintake, 0.75, 300, 68, -6;
ctlow;
L: cthigh;
turn, 270;
R: drvleftultra, 1, 31;
C: drvleftultra, 1, 31;
L: drvleftultra, 1, 31;
gyrolg;
drvt, 0.8, 90, 1;
gyrolg;
ctout;
wait 0.5;
gyrolg;
drvddumb, 0.5, 270, 5;
gyrolg;
stopdump;
end; */