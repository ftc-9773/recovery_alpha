package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Vision.JewelDetector;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;
import org.firstinspires.ftc.teamcode.infrastructure.RasiActions;
import org.firstinspires.ftc.teamcode.resources.Timer;

/**
 * Created by vikesh on 1/26/18.
 */

@Autonomous(name = "RASI'd close red auto")
public class CloseRedAutoRASId extends LinearOpModeCamera {
    private VumarkGlyphPattern vumarkGlyphPattern;
    private RelicRecoveryVuMark relicRecoveryVuMark;
    private JewelDetector jewelDetector = new JewelDetector(this);
    private String[] rasiTag = new String[2];
    private RasiActions rasiActions;
    private JewelDetector.JewelColors jewelColors;

    @Override
    public void runOpMode() throws InterruptedException {
        vumarkGlyphPattern = new VumarkGlyphPattern(hardwareMap);

        while(!opModeIsActive() && !isStopRequested()){
            relicRecoveryVuMark = vumarkGlyphPattern.getColumn();

            telemetry.addData("Initialization", "Success");
            telemetry.addData("vumark", relicRecoveryVuMark);
            telemetry.update();
        }

        //Default to center
        if (relicRecoveryVuMark == RelicRecoveryVuMark.UNKNOWN) {
            relicRecoveryVuMark = RelicRecoveryVuMark.CENTER;
        }


        waitForStart();

        jewelDetector.startCamera();
        jewelColors = jewelDetector.computeJewelColor();

        Timer myTimer = new Timer(10);
        while(!myTimer.isDone() && jewelColors != JewelDetector.JewelColors.RED && jewelColors != JewelDetector.JewelColors.BLUE){
            jewelColors = jewelDetector.computeJewelColor();
        }


        rasiTag[0] = jewelColors.toString();
        rasiTag[1] = Character.toString(relicRecoveryVuMark.toString().charAt(0));

        telemetry.addData("rasitag[0] ", rasiTag[0]);
        telemetry.addData("rasitag[1] ", rasiTag[1]);
        telemetry.update();

        rasiActions = new RasiActions("AutoFarRed", rasiTag, this, gamepad1, gamepad2, telemetry, hardwareMap);

        rasiActions.runRasi();
    }
}
