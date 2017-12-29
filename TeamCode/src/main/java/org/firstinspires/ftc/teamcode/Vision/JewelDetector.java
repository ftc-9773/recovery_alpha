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

    LinearOpModeCamera camOp;
    int total = 0;
    int colOn = 160;
    int colOff = 80;
    int ds2 = 2;
    SafeJsonReader thresholds ;
    double redThreshold = 0.1;
    double blueThreshold = 0.1;
    public JewelColors leftJewelColor ;
//    public JewelColors rightJewelColor ;


    public JewelDetector(LinearOpModeCamera camOp){
        leftJewelColor = JewelColors.NOT_INITIALIZED ;
        this.camOp = camOp;
        thresholds = new SafeJsonReader("VisionThresholds");
        redThreshold = thresholds.getDouble("RedThreshold");
        blueThreshold = thresholds.getDouble("BlueThreshold");
    }

    public void startCamera(){
        camOp.setCameraDownsampling(8);
        camOp.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        leftJewelColor = JewelColors.UNKNOWN;

    }

    public JewelColors getJewelColor(){
        onOffThreshold(true);
        return leftJewelColor;
    }

    public boolean isLeftJewelRed() { return leftJewelColor.equals(JewelColors.RED); }
    public boolean isLeftJewelBlue() { return leftJewelColor.equals(JewelColors.BLUE);}
    public boolean leftJewelIsUndetermined() { return leftJewelColor.equals(JewelColors.UNKNOWN) ||
         leftJewelColor.equals(JewelColors.NOT_INITIALIZED); }

    public void onOffThreshold(boolean scaling){
        // get image, rotated so (0,0) is in the bottom left of the preview window
        if (! camOp.imageReady()) return;

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

        if (scaling) {
            // additional downsampling of the image
            // set to 1 to disable further downsampling

            for (int x = rgbImage.getWidth() / 2; x < rgbImage.getWidth(); x++) {
                for (int y = rgbImage.getHeight() / 2; y < rgbImage.getHeight(); y++) {
                    int pixel = rgbImage.getPixel(x, y);
                    if (Color.red(pixel) < minR) minR = Color.red(pixel);
                    if (Color.red(pixel) > maxR) maxR = Color.red(pixel);
                    if (Color.blue(pixel) < minB) minB = Color.blue(pixel);
                    if (Color.blue(pixel) > maxB) maxB = Color.blue(pixel);
                    if (Color.green(pixel) < minG) minG = Color.green(pixel);
                    if (Color.green(pixel) > maxG) maxG = Color.green(pixel);
                }
            }
        }

        //TODO (at competition): Manually test this threshold value by the field in the competition.
        int redValue = 0;
        int blueValue = 0;
        int totValue = 0;
        double coeffR = 255.0 / ((double)(maxR - minR));
        double coeffB = 255.0 / ((double)(maxB - minB));
        double coeffG = 255.0 / ((double)(maxG - minG));

        for (int x = rgbImage.getWidth() / 2; x < rgbImage.getWidth(); x+=10) {
            for (int y = rgbImage.getHeight() / 2; y < rgbImage.getHeight(); y+=10) {
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
                    redValue+=valR;
                if(valB > colOn && valR < colOff && valG < colOff)
                    blueValue+=valB;
                totValue ++;
            }
        }

        int diff = redValue - blueValue;
//        threshold = threshold * totValue;

        if(diff < -blueThreshold*totValue){
            leftJewelColor = JewelColors.BLUE ;
        }else if (diff > redThreshold*totValue) {
            leftJewelColor = JewelColors.RED ;
        } else {
            leftJewelColor = JewelColors.UNKNOWN;
        }
//            else colorString = "NONE";
        camOp.telemetry.addData("actual difference: ", diff);

//            colorString = newR-newB < threshold ? "BLUE is left" : "RED is left";//TODO: This value may be different under dimmer conditions
        camOp.sleep(10);


        camOp.telemetry.addData("Jewel Color ", getJewelColor());
    }




}
