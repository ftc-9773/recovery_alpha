package org.firstinspires.ftc.teamcode.opmodes;

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
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.resources.ButtonStatus;
import org.firstinspires.ftc.teamcode.resources.Timer;

/**
 * Created by vikesh on 1/26/18.
 */

@Autonomous(name = "RASI Auto")
public class RasiAuto extends LinearOpModeCamera {
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

        int fileNameIndex = 0;
        ButtonStatus down = new ButtonStatus();
        ButtonStatus up = new ButtonStatus();

        SafeJsonReader jsonReader = new SafeJsonReader("RasiAutoFileNames.json");

        String[] rasiFileNames = new String [jsonReader.getInt("NumberOfPaths")];

        for (int i = 0; i <= jsonReader.getInt("NumberOfPaths"); i++) {
            rasiFileNames[i-1] = jsonReader.getString("Path" + Integer.toString(i));
        }

        while(!gamepad1.y && !opModeIsActive() && !isStopRequested()) {
            down.recordNewValue(gamepad1.dpad_down);
            up.recordNewValue(gamepad1.dpad_up);

            if (down.isJustOn()) fileNameIndex --;
            if (up.isJustOn()) fileNameIndex ++;
            if (fileNameIndex > rasiFileNames.length-1) fileNameIndex = 0;
            if (fileNameIndex < 0) fileNameIndex = rasiFileNames.length-1;
            telemetry.addData("Choose an Autonomous Path", "");
            telemetry.addData("Current Path", rasiFileNames[fileNameIndex]);
            telemetry.addData("Start", "PRESS Y BEFORE START");
            telemetry.update();
        }

        telemetry.addData("Autonomous Path", rasiFileNames[fileNameIndex]);
        telemetry.addData("Initialization", "Waiting...");
        telemetry.update();

        // Initialize Classes
        vumarkGlyphPattern = new VumarkGlyphPattern(hardwareMap);
        jewelDetector = new JewelDetector(this);
        rasiActions = new RasiActions(rasiFileNames[fileNameIndex], rasiTag, this, gamepad1, gamepad2, telemetry, hardwareMap);

        // Read the Vumark
        while(!opModeIsActive() && !isStopRequested()){
            relicRecoveryVuMark = vumarkGlyphPattern.getColumn();

            telemetry.addData("Autonomous Path", rasiFileNames[fileNameIndex]);
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

        Timer myTimer = new Timer(2);
        while(!myTimer.isDone()){
            jewelColors = jewelDetector.computeJewelColor() ;
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
