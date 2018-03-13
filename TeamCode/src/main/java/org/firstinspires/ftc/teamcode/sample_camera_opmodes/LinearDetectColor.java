package org.firstinspires.ftc.teamcode.sample_camera_opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Vision.JewelDetector;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;
import org.firstinspires.ftc.teamcode.resources.Timer;

/**
 * TeleOp Mode
 * <p/>
 * Enables control of the robot via the gamepad
 */

@Autonomous(name = "LinearDetectColor", group = "ZZOpModeCameraPackage")
//@Disabled
public class LinearDetectColor extends LinearOpModeCamera {
    String TAG = "ftc9773_LinearDetectColor";
    RelicRecoveryVuMark mark;
    JewelDetector.JewelColors color = JewelDetector.JewelColors.NOT_INITIALIZED;

    JewelDetector detector;

    // make time constraints for detectors
    private static final int timeForVuforia = 2;
    private static final int timeForJewelDetect = 2;
//    static final int THRESHOLD = 10;//red - blue

    @Override
    public void runOpMode() {
            // initialize vumark pattern
        detector = new JewelDetector(this);


        while (!opModeIsActive() && !isStopRequested()) {
            // initialize the vuforia section of vision routine
            // during this time, the robot checks for vuforia until either the time is up,
            // or a vumark is detected

            // we start by initializing vuforia
            VumarkGlyphPattern pattern = new VumarkGlyphPattern(hardwareMap);
            // create a temporary variable to store the mark value for this run
            RelicRecoveryVuMark tempMark = RelicRecoveryVuMark.UNKNOWN;
            // run so long as either the
            Timer vuf = new Timer(timeForVuforia);
            while (!opModeIsActive() && !isStopRequested()&& !vuf.isDone()) {
                tempMark = pattern.getColumn();
                // if the vumark is known, update the official value
                Log.v(TAG, "temp mark is: " + tempMark);
                if(!tempMark.equals(RelicRecoveryVuMark.UNKNOWN )){
                    mark = tempMark;
                    break;
                }
                // report back to the driver station
                runTelemetryAndLogs();
                if(vuf.isDone() )
                    break;

            }
            pattern.close();

            detector.startCamera();
            Timer jewel = new Timer(timeForJewelDetect);
            JewelDetector.JewelColors tempColor = JewelDetector.JewelColors.NOT_INITIALIZED;
            Log.i(TAG, "switching to Jewel camera");
            while (!opModeIsActive() && !isStopRequested() &&  !jewel.isDone()) {
                tempColor = detector.computeJewelColor();
                // if the color is known update the 'official' value
                Log.v(TAG, "temp color is: " + tempColor);

                if(!(tempColor.equals(JewelDetector.JewelColors.NOT_INITIALIZED)||tempColor.equals(JewelDetector.JewelColors.UNKNOWN)))
                    color = tempColor;
                runTelemetryAndLogs();
                if(jewel.isDone() )
                    break;
            }
            detector.stopCamera();

            if (opModeIsActive()) break;
        }

        // end vision patterns
        waitForStart();



        while (opModeIsActive()) {
            runTelemetryAndLogs();
        }

    }

    private void runTelemetryAndLogs(){
        telemetry.addData("vuMark", mark);
        telemetry.addData("Jewel", color);
        telemetry.update();

        Log.i(TAG, "vuMark: "+ mark);
        Log.i(TAG, "Jewel :" + color);

    }

}

// todo: migrate to a modular system with methods like :
// startJewelCV
// getJewelColor
// close JewelCV

