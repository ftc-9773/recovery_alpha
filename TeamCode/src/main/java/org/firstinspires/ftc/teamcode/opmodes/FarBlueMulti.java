package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Vision.JewelDetector;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;
import org.firstinspires.ftc.teamcode.infrastructure.RasiActions;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.resources.Timer;

/**
 * Created by nicky on 3/14/18.
 */

@Autonomous(name = "Far Blue Multi-Glyph", group = "Auto Paths:")
public class FarBlueMulti extends LinearOpModeCamera {
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

        rasiActions = new RasiActions("AutoBlueFarMulti", null, this, gamepad1, gamepad2, telemetry, hardwareMap);

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

        // End features
        rasiActions.ftcRobot.mySwerveController.stopRobot();
        rasiActions.ftcRobot.myGyro.recordHeading();
    }


    public void runVisionTelemetry(String reading, Timer timer) {
        telemetry.addData("Init", "Complete!");
        telemetry.addData("Vumark", relicRecoveryVuMark);
        telemetry.addData("Jewel", jewelColors);

        telemetry.addData("Reading " + reading, timer.timePassedInSeconds());
        telemetry.update();
    }

}

// Works reasonably well

/*
ctload;
RED: jwlarmd;
RED: jwlarmcl;
RED: wait, 1;
RED: jwlarml;
RED: wait, 0.5;
RED: jwlarmtempu;

BLUE: jwlarmd;
BLUE: jwlarmcr;
BLUE: wait, 1;
BLUE: jwlarmr;
BLUE: wait, 0.5;
BLUE: jwlarmtempu;

drvdropintk, 0.8, 20, 29;
jwlarmu;
drvintake, 0.8, 20, 13, 0;
drvd, 1.0, 270, 32;
intko;
wait, 0.6;
intki;
turn, 90;
intks;
R: drvrightultra, 1, 31;
C: drvrightultra, 1, 25;
L: drvrightultra, 1, 19;
turn, 60;
ctlow;
drvt, 0.65, 260, 1.2;
ctout;
wait, 0.15;
drvddumb, 0.4, 60, 5;
wait, 0.15;
R: drvd, 1, 180, 6;
L: drvd, 1, 0, 6;
stopdump;
ctload;
drvd, 1, 80, 30;
drvintake, 0.5, 55, 30, 3;
gyrolg;
drvd, 1, 263, 16;
L: ctlow;
C: ctlow;
R: cthigh;
gyrolg;
drvt, 0.65, 230, 1.5;
gyrolg;
ctout;
drvddumb, 0.7, 60, 1;
gyrolg;
wait, 0.3;
gyrolg;
drvddumb, 0.5, 60, 4;
gyrolg;
end;
*/