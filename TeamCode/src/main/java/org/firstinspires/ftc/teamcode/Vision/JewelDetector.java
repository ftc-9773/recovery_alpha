package org.firstinspires.ftc.teamcode.Vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

/**
 * Created by michaelzhou on 12/28/17.
 */

// commands for adb
    /*
    cd TeamCode/src/main/java/org/firstinspires/ftc/teamcode/JSON/
    ~/Library/Android/sdk/platform-tools/adb push VisionThresholds.json /sdcard/FIRST/team9773/json18
     */

public class JewelDetector {

    public enum JewelColors {
        RED,
        BLUE,
        UNKNOWN,
        NOT_INITIALIZED
    }

    // algorithmic parameters (set in class)
    final double colOn = 160.0;
    final double colOff = 80.0;
    final boolean scaling = true;
    final int ds2 = 2;

    // algorithmic parameters set in json
    SafeJsonReader thresholds ;
    double redThreshold = 0.1;
    double blueThreshold = 0.1;

    // reference to linear opmode
    LinearOpModeCamera camOp;

    // result
    JewelColors leftJewelColor;

    public JewelDetector(LinearOpModeCamera camOp){
        this.camOp = camOp;
        thresholds = new SafeJsonReader("VisionThresholds");
        redThreshold = thresholds.getDouble("RedThreshold");
        blueThreshold = thresholds.getDouble("BlueThreshold");
        leftJewelColor = JewelColors.NOT_INITIALIZED ;
    }

    public void startCamera(){
        camOp.setCameraDownsampling(8);
        camOp.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    public JewelColors getJewelColor(){
        return leftJewelColor;
    }

    public JewelColors computeJewelColor(){
        // get image, rotated so (0,0) is in the bottom left of the preview window
        if (! camOp.imageReady()) return leftJewelColor;

        // only do this if an image has been returned from the camera
        Bitmap rgbImage;
        rgbImage = camOp.convertYuvImageToRgb(camOp.yuvImage, camOp.width, camOp.height, ds2);

        // do we want to scale?
        int minR = 255;
        int maxR = 0;
        int minB = 255;
        int maxB = 0;
        int minG = 255;
        int maxG = 0;
        double coeffR = 1.0;
        double coeffB = 1.0;
        double coeffG = 1.0;

        final int steps = 1;
        final int width = rgbImage.getWidth();
        final int heights = rgbImage.getHeight();
        final int startHeight = 3*heights/4;
        final int startWidth = 3*width/4;

        if (scaling) {
            // additional downsampling of the image
            // set to 1 to disable further downsampling
            for (int x = startWidth; x < width; x+=steps) {
                for (int y = startHeight; y < heights; y+=steps) {
                    int pixel = rgbImage.getPixel(x, y);
                    int valR = Color.red(pixel);
                    if (valR < minR) minR = valR;
                    if (valR > maxR) maxR = valR;
                    int valB = Color.blue(pixel);
                    if (valB < minB) minB = valB;
                    if (valB > maxB) maxB = valB;
                    int valG = Color.green(pixel);
                    if (valG < minG) minG = valG;
                    if (valG > maxG) maxG = valG;
                }
            }
            coeffR = 255.0 / ((double)(maxR - minR));
            coeffB = 255.0 / ((double)(maxB - minB));
            coeffG = 255.0 / ((double)(maxG - minG));
        }

        //TODO (at competition): Manually test this threshold value by the field in the competition.
        int redValue = 0;
        int blueValue = 0;
        int totValue = 0;

        for (int x = startWidth; x < width; x+=steps) {
            for (int y = startHeight; y < heights; y+=steps) {
                int pixel = rgbImage.getPixel(x, y);
                double valR = Color.red(pixel);
                double valB = Color.blue(pixel);
                double valG = Color.green(pixel);
                if (scaling) {
                    valR = (valR - minR) * coeffR;
                    valB = (valB - minB) * coeffB;
                    valG = (valG - minG) * coeffG;
                }
                if(valR > colOn && valB < colOff && valG < colOff)
                    redValue++;
                if(valB > colOn && valR < colOff && valG < colOff)
                    blueValue++;
                totValue ++;
            }
        }

        int diff = redValue - blueValue;
        if(diff < -blueThreshold*totValue){
            leftJewelColor = JewelColors.BLUE ;
        }else if (diff > redThreshold*totValue) {
            leftJewelColor = JewelColors.RED ;
        } else {
            leftJewelColor = JewelColors.UNKNOWN;
        }

        // telemetry or log
        //camOp.telemetry.addData("actual difference: ", diff);
        //camOp.telemetry.addData("tot values: ", totValue);
        //double fraction = (double)diff/(double)totValue;
        //camOp.telemetry.addData("relative difference: ", fraction);
        //camOp.telemetry.addData("Jewel Color ", getJewelColor());

        return leftJewelColor;
    }
}
