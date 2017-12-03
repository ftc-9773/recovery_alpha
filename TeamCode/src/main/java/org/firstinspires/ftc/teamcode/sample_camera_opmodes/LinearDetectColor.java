package org.firstinspires.ftc.teamcode.sample_camera_opmodes;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;

/**
 * TeleOp Mode
 * <p/>
 * Enables control of the robot via the gamepad
 */

@Autonomous(name = "LinearDetectColor", group = "ZZOpModeCameraPackage")
//@Disabled
public class LinearDetectColor extends LinearOpModeCamera {

    int ds2 = 2;  // additional downsampling of the image
    // set to 1 to disable further downsampling
    int colOn = 160;
    int colOff = 80;

    int redCount = 0;
    int blueCount = 0;

    ElapsedTime time;

    @Override
    public void runOpMode() {

        String colorString = "NONE";

        // linear OpMode, so could do stuff like this too.
        /*
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        */



        if (isCameraAvailable()) {

            setCameraDownsampling(8);
            // parameter determines how downsampled you want your images
            // 8, 4, 2, or 1.
            // higher number is more downsampled, so less resolution but faster
            // 1 is original resolution, which is detailed but slow
            // must be called before super.init sets up the camera

            telemetry.addLine("Wait for camera to finish initializing!");
            telemetry.update();
            startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);  // can take a while.
            // best started before waitForStart
            telemetry.addLine("Camera ready!");
            telemetry.update();

            while (!opModeIsActive()) {
                if (imageReady()) { // only do this if an image has been returned from the camera
                    int redValue = 0;
                    int blueValue = 0;
                    int greenValue = 0;

                    int minR = 0;
                    int maxR = 0;
                    int minB = 0;
                    int maxB = 0;
//                    int maxG = 0;
//                    int minG = 0;




                    // get image, rotated so (0,0) is in the bottom left of the preview window
                    Bitmap rgbImage;
                    rgbImage = convertYuvImageToRgb(yuvImage, width, height, ds2);
                    int redValues = 0, blueValues = 0;

                    for (int x = rgbImage.getWidth() / 2; x < rgbImage.getWidth(); x++) {
                        for (int y = rgbImage.getHeight() / 2; y < rgbImage.getHeight(); y++) {
                            int pixel = rgbImage.getPixel(x, y);

//                            telemetry.addData("ValueR: ", Color.red(pixel));
//                            telemetry.addData("ValueB: ", Color.blue(pixel));

                            if(Color.red(pixel) < minR) minR = Color.red(pixel);
                            if(Color.red(pixel) > maxR) maxR = Color.red(pixel);
                            if(Color.blue(pixel) < minB) minB = Color.blue(pixel);
                            if(Color.blue(pixel) > maxB) maxB = Color.blue(pixel);
//                            if(Color.green(pixel) < minG) minG = Color.green(pixel);
//                            if(Color.green(pixel) > maxG) maxG = Color.green(pixel);
                            redValues+=Color.red(pixel);
                            blueValues+=Color.blue(pixel);
                        }
                    }

                    int avgredValues = redValues / ((rgbImage.getWidth()/2)*(rgbImage.getHeight()/2));
                    int avgblueValues = blueValues / ((rgbImage.getWidth()/2)*(rgbImage.getHeight()/2));

                    int minRGB = 0;//TODO: Figure out why avgredValues - minRGB = 0 and avgblueValues - minRGB....
                    int maxRGB = 1;

                    int newR = ((avgredValues - minRGB)/(maxRGB-minRGB));
                    int newB = ((avgblueValues - minRGB)/(maxRGB-minRGB));

                    telemetry.addData("RED: ", newR);
                    telemetry.addData("BLUE: ", newB);

                    //Color.red & blue goes to 255
//                    telemetry.addData("Test BLUE: ", Color.blue(rgbImage.getPixel(rgbImage.getWidth() / 2, rgbImage.getHeight()/2)));
                    colorString = newR-newB > 25 ? "RED is left" : "BLUE is left";

                    telemetry.addData("Threshold:", newR-newB);
                    telemetry.addData("Color:", "Color detected is: " + colorString);
                    telemetry.update();
                    sleep(10);
                }
            }

            waitForStart();

            while (opModeIsActive()) {
                stopCamera();
            }
        }
    }
}