package org.firstinspires.ftc.teamcode.opmodes;

/**
 * Created by vikesh on 2/23/18.
 */

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;
import org.firstinspires.ftc.teamcode.Vision.JewelDetector;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;
import org.firstinspires.ftc.teamcode.infrastructure.RasiActions;
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

@Autonomous(name = "Close Blue Multi-Glyph", group = "Auto Paths:")
public class CloseBlueMulti extends LinearOpModeCamera {
    private FTCrobot ftcrobot;
    private VumarkGlyphPattern vumarkGlyphPattern;
    private RelicRecoveryVuMark relicRecoveryVuMark;
    private JewelDetector jewelDetector;
    private String[] rasiTag = new String[2];
    private String[] rasiFileNames = new String[] {"AutoFarRed", "AutoFarBlue"};
    private RasiActions rasiActions;
    private JewelDetector.JewelColors jewelColors;

    @Override
    public void runOpMode() throws InterruptedException {

        // Select Autonomous Path

        ftcrobot = new FTCrobot(hardwareMap, telemetry, gamepad1, gamepad2, this);
        telemetry.addData("Initialization", "Waiting...");
        telemetry.update();

        // Initialize Classes
        vumarkGlyphPattern = new VumarkGlyphPattern(hardwareMap);
        jewelDetector = new JewelDetector(this);
        rasiActions = new RasiActions("AutoBlueCloseMulti", rasiTag, this, gamepad1, gamepad2, telemetry, hardwareMap);

        // Read the Vumark
        while(!opModeIsActive() && !isStopRequested()){
            relicRecoveryVuMark = vumarkGlyphPattern.getColumn();

            telemetry.addData("Autonomous Path", "AutoBlueCloseMulti");
            telemetry.addData("Initialization", "Success");
            telemetry.addData("vumark", relicRecoveryVuMark);
            telemetry.update();
        }

        //Default to center
        if (relicRecoveryVuMark == RelicRecoveryVuMark.UNKNOWN) {
            relicRecoveryVuMark = RelicRecoveryVuMark.CENTER;
        }

        waitForStart();

        // Read Jewel Color
        jewelDetector.startCamera();
        jewelDetector.computeJewelColor();

        Timer myTimer = new Timer(2);
        while(!myTimer.isDone()){
            jewelColors = jewelDetector.computeJewelColor();
            jewelColors = jewelDetector.getJewelColor();
            telemetry.addData("Jewel Color", jewelColors);
            telemetry.update();
        }
        Log.e("Auto Jewel Color", "" + jewelColors);

        // Pass RASI Tags
        rasiTag[0] = jewelColors.toString();
        rasiTag[1] = Character.toString(relicRecoveryVuMark.toString().charAt(0));

        telemetry.addData("rasitag[0] ", rasiTag[0]);
        telemetry.addData("rasitag[1] ", rasiTag[1]);
        telemetry.update();

        rasiActions.rasiParser.rasiTag = this.rasiTag;
        Log.e("Tag 0", rasiTag[0]);
        Log.e("Tag 1", rasiTag[1]);


        // DO EVERYTHING
        rasiActions.runRasi();
    }
}

