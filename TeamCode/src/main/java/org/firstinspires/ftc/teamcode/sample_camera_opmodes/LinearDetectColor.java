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
                detector.onOffThreshold(true);//TODO: Have the camera detect the ball for a certain amount of time, or store it into counts for each color and get the greatest count

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



/*
* package org.firstinspires.ftc.teamcode.sample_camera_opmodes;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;

/**
 * TeleOp Mode
 * <p/>
 * Enables control of the robot via the gamepad
// */
//
//@Autonomous(name = "LinearDetectColor", group = "ZZOpModeCameraPackage")
////@Disabled
//public class LinearDetectColor extends LinearOpModeCamera {
//
//    int ds2 = 2;  // additional downsampling of the image
//    // set to 1 to disable further downsampling
//
//    String colorString;
//    RelicRecoveryVuMark mark;
////    static final int THRESHOLD = 10;//red - blue
//
//    @Override
//    public void runOpMode() {
//        VumarkGlyphPattern pattern = new VumarkGlyphPattern(hardwareMap);
//
//
//        while (!opModeIsActive()) {
//            mark = pattern.getColumn();
//            telemetry.addData("vuMark", mark);
//            telemetry.update();
//        }
//
//        setCameraDownsampling(8);
//        startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
//        waitForStart();
//
//        ElapsedTime timer = new ElapsedTime();
//
//        while (opModeIsActive()) {
////            if(colorString==null){
//            detectJewelColor();//TODO: Have the camera detect the ball for a certain amount of time, or store it into counts for each color and get the greatest count
////            }
////            timer.startTime();
////            while(timer.seconds()<1.5){
////                detectJewelColor();
////            }
//            telemetry.addData("vuMark", mark);
//            telemetry.update();
//        }
//        stopCamera();
//
//    }
//
//    public void detectJewelColor(){
//        int avgredValues = 0, avgblueValues = 0, minRGB = 0, maxRGB = 0;
//        double newR = 0.0, newB = 0.0;
//        if (imageReady()) { // only do this if an image has been returned from the camera
//            int redValue = 0;
//            int blueValue = 0;
//            int greenValue = 0;
//
//            int minR = 0;
//            int maxR = 0;
//            int minB = 0;
//            int maxB = 0;
//
//            // get image, rotated so (0,0) is in the bottom left of the preview window
//            Bitmap rgbImage;
//            rgbImage = convertYuvImageToRgb(yuvImage, width, height, ds2);
//            int redValues = 0, blueValues = 0;
//
//            for (int x = rgbImage.getWidth() / 2; x < rgbImage.getWidth(); x++) {
//                for (int y = rgbImage.getHeight() / 2; y < rgbImage.getHeight(); y++) {
//                    int pixel = rgbImage.getPixel(x, y);
//
////                            telemetry.addData("ValueR: ", Color.red(pixel));
////                            telemetry.addData("ValueB: ", Color.blue(pixel));
//
//                    if(Color.red(pixel) < minR) minR = Color.red(pixel);
//                    if(Color.red(pixel) > maxR) maxR = Color.red(pixel);
//                    if(Color.blue(pixel) < minB) minB = Color.blue(pixel);
//                    if(Color.blue(pixel) > maxB) maxB = Color.blue(pixel);
////                            if(Color.green(pixel) < minG) minG = Color.green(pixel);
////                            if(Color.green(pixel) > maxG) maxG = Color.green(pixel);
//                    redValues+=Color.red(pixel);
//                    blueValues+=Color.blue(pixel);
//                }
//            }
//
//            avgredValues = redValues / ((rgbImage.getWidth()/2)*(rgbImage.getHeight()/2));
//            avgblueValues = blueValues / ((rgbImage.getWidth()/2)*(rgbImage.getHeight()/2));
//
//            minRGB = Math.min(minR,minB);
//            maxRGB = Math.max(maxB,maxR);
////
//            newR = 255*(avgredValues - minRGB)/(maxRGB-minRGB);
//            newB = 255*(avgblueValues - minRGB)/(maxRGB-minRGB);
//
//            double threshold = newR - newB;
//            if(threshold<6) colorString = "BLUE is left";
//            else if(threshold>=25) colorString = "RED is left";
//            else colorString = "NONE";
//
////            colorString = newR-newB < threshold ? "BLUE is left" : "RED is left";//TODO: This value may be different under dimmer conditions
//            sleep(10);
//        }
//
//        telemetry.addData("actual difference: ", newR-newB);
//        telemetry.addData("Jewel Color ", colorString);
//    }
//}
//
//// todo: migrate to a modular system with methods like :
//// startJewelCV
//// getJewelColor
//// close JewelCV