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
    String colorString;
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
//        setCameraDownsampling(8);
//        startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        waitForStart();

        ElapsedTime timer = new ElapsedTime();

        while (opModeIsActive()) {
//            if(colorString==null){
                detector.onOffThreshold(false);//TODO: Have the camera detect the ball for a certain amount of time, or store it into counts for each color and get the greatest count

//            }
//            timer.startTime();
//            while(timer.seconds()<1.5){
//                detectJewelColor();
//            }
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

