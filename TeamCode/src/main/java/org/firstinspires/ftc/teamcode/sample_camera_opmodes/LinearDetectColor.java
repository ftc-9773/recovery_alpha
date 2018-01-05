package org.firstinspires.ftc.teamcode.sample_camera_opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Vision.JewelDetector;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;

/**
 * TeleOp Mode
 * <p/>
 * Enables control of the robot via the gamepad
 */

@Autonomous(name = "LinearDetectColor", group = "ZZOpModeCameraPackage")
//@Disabled
public class LinearDetectColor extends LinearOpModeCamera {
    RelicRecoveryVuMark mark;
//    static final int THRESHOLD = 10;//red - blue

    @Override
    public void runOpMode() {
        VumarkGlyphPattern pattern = new VumarkGlyphPattern(hardwareMap);
        JewelDetector detector = new JewelDetector(this);
        while (!opModeIsActive()) {
            mark = pattern.getColumn();
            telemetry.addData("vuMark", mark);
            telemetry.update();
        }
        detector.startCamera();
        waitForStart();
        ElapsedTime timer = new ElapsedTime();

        while (opModeIsActive()) {

            detector.computeJewelColor();
            telemetry.addData("vuMark", mark);
            telemetry.update();
        }
        stopCamera();

    }
}

// todo: migrate to a modular system with methods like :
// startJewelCV
// getJewelColor
// close JewelCV

