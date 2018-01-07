package org.firstinspires.ftc.teamcode.Vision.DogeCVTests;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.detectors.CryptoboxDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by michaelzhou on 1/7/18.
 */

@Autonomous(name="CryptoboxOpMode", group="dogecv")

public class CryptoboxOpMode extends LinearOpMode{
    CryptoboxDetector cryptoboxDetector;
    @Override
    public void runOpMode() throws InterruptedException {
        cryptoboxDetector = new CryptoboxDetector();
        cryptoboxDetector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        cryptoboxDetector.rotateMat = false;
        cryptoboxDetector.enable();

        waitForStart();
        while(opModeIsActive()){
            telemetry.addData("isCryptoBoxDetected", cryptoboxDetector.isCryptoBoxDetected());
            telemetry.addData("isColumnDetected ",  cryptoboxDetector.isColumnDetected());

            telemetry.addData("Column Left ",  cryptoboxDetector.getCryptoBoxLeftPosition());
            telemetry.addData("Column Center ",  cryptoboxDetector.getCryptoBoxCenterPosition());
            telemetry.addData("Column Right ", cryptoboxDetector.getCryptoBoxRightPosition());

            telemetry.update();
        }
        cryptoboxDetector.disable();
    }
}
