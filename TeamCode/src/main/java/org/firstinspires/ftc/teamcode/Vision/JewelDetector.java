package org.firstinspires.ftc.teamcode.Vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.util.Log;

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
    double colOn = 160.0; // Overwritten in
    double colOff = 80.0;

    final boolean scaling = true;
    final boolean commonScaling = true;
    final int ds2 = 2;

    // for Logging
    private static final String TAG = "ftc9773_JewelDetect" ;

    // algorithmic parameters set in json
    SafeJsonReader thresholds ;
    public double redThreshold;
    public double blueThreshold;

    public double redBlueImballance;

    public double redThresholdVuf ;
    public double blueThresholdVuf;

    private boolean usingVuforiaForDetect;

    // reference to linear opmode
    LinearOpModeCamera camOp;

    // result
    JewelColors leftJewelColor;

    // Diff
    public double diff = 0;

    public JewelDetector(LinearOpModeCamera camOp){
        this.camOp = camOp;
        thresholds = new SafeJsonReader("VisionThresholds");
        redThreshold = thresholds.getDouble("RedThreshold");
        blueThreshold = thresholds.getDouble("BlueThreshold");

        blueThresholdVuf =thresholds.getDouble("BlueThresholdVuforia");
        redThresholdVuf  = thresholds.getDouble("RedThresholdVuforia");

        redBlueImballance = thresholds.getDouble("redBlueImballance");

        leftJewelColor = JewelColors.NOT_INITIALIZED ;
    }

    public void startCamera(){
        camOp.setCameraDownsampling(8);
        camOp.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }
    public void stopCamera(){
        camOp.stopCamera();
    }

    public JewelColors getJewelColor(){
        return leftJewelColor;
    }

    public JewelColors computeJewelColor(){
        if (! camOp.imageReady()) return leftJewelColor;

        Bitmap rgbImage;
        rgbImage = camOp.convertYuvImageToRgb(camOp.yuvImage, camOp.width, camOp.height, ds2);
        usingVuforiaForDetect = false;

        return computeJewelColorFromBitmap(rgbImage);
    }

    public JewelColors computeJewelColorFromVuforia(VumarkGlyphPattern vuforia){
        Bitmap rgbImage;
        rgbImage = vuforia.getBitMap();
        if(rgbImage != null){
            Log.i(TAG, "successfully got rgb bitmaps");
        }
        else {
            Log.e(TAG, "issue getting bitmaps ");
            return leftJewelColor;
        }

        usingVuforiaForDetect = true;

        return computeJewelColorFromBitmap(rgbImage);
    }



    private JewelColors computeJewelColorFromBitmap(Bitmap rgbImage){

        // get the appropriate threshold based on which mode you are using
        // allows for more freedom in tuning values
        double myblueThreshold;
        double myRedThreshold;
        if(usingVuforiaForDetect){
            myblueThreshold = blueThresholdVuf;
            myRedThreshold = redThresholdVuf;
        } else {
            myblueThreshold = blueThreshold;
            myRedThreshold = redThreshold;

        }


        // get image, rotated so (0,0) is in the bottom left of the preview window

        // only do this if an image has been returned from the camera

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
        final int startHeight = 7*heights/8;
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
            if (commonScaling) {
                int commonMin = minR;
                int commonMax = maxR;
                if (minB < commonMin) commonMin = minB;
                if (maxB > commonMax) commonMax = maxB;
                if (minG < commonMin) commonMin = minG;
                if (maxG > commonMax) commonMax = maxG;
                minR = minB = minG = commonMin;
                maxR = maxB = maxG = commonMax;
            }
            coeffR = 255.0 / ((double)(maxR - minR));
            coeffB = 255.0 / ((double)(maxB - minB));
            coeffG = 255.0 / ((double)(maxG - minG));
        }

        //TODO (at competition): Manually test this threshold value by the field in the competition.

        int redValue = 0;
        int blueValue = 0;
        int totValue = 0;


        // Start the algorithm
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

                if (usingVuforiaForDetect) {
                    if (valR - valB > redBlueImballance && valR > valG) redValue++;
                    if (valB - valR > -redBlueImballance && valB > valG) blueValue++;
                } else {
                    if (valR > colOn && valB < colOff && valG < colOff)
                        redValue++;
                    if (valB > colOn && valR < colOff && valG < colOff)
                        blueValue++;
                }

                totValue ++;
            }
        }

        diff = (redValue - blueValue);
        diff /= totValue;

        Log.e(TAG, "ColOn: "+ colOn + "ColOff: "+ colOff);
        Log.e(TAG, "Red value: "+ redValue + "   Blue Value: "+ blueValue + "  diff: " + diff);

        if(diff < -blueThreshold){
            leftJewelColor = JewelColors.BLUE ;
        }else if (diff > redThreshold) {
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

// FROM DEBUG_VISION3:

//package org.firstinspires.ftc.teamcode.Vision;
//
//        import android.graphics.Bitmap;
//        import android.graphics.Color;
//        import android.hardware.Camera;
//
//        import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
//        import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

/**
 * Created by michaelzhou on 12/28/17.
 */

// commands for adb
    /*
    cd TeamCode/src/main/java/org/firstinspires/ftc/teamcode/JSON/
    ~/Library/Android/sdk/platform-tools/adb push VisionThresholds.json /sdcard/FIRST/team9773/json18
     */

//public class JewelDetector {
//
//    public enum JewelColors {
//        RED,
//        BLUE,
//        UNKNOWN,
//        NOT_INITIALIZED
//    }
//
//    LinearOpModeCamera camOp;
//    boolean isRed, isBlue;
//    final boolean scaling = true;
//    int total = 0;
//    int colOn = 160;
//    int colOff = 80;
//    int ds2 = 2;
//    SafeJsonReader thresholds ;
//    double redThreshold = 0.1;
//    double blueThreshold = 0.1;
//    public JewelColors color;
//
//    public JewelDetector(LinearOpModeCamera camOp){
//        this.camOp = camOp;
//        isRed = false;
//        isBlue = false;
//        thresholds = new SafeJsonReader("VisionThresholds");
//        redThreshold = thresholds.getDouble("RedThreshold");
//        blueThreshold = thresholds.getDouble("BlueThreshold");
//        color = JewelColors.NOT_INITIALIZED;
//    }
//
//    public void startCamera(){
//        camOp.setCameraDownsampling(8);
//        camOp.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
//        color = JewelColors.UNKNOWN;
//    }
//
//    public JewelColors getJewelColor(){
//        return color;
//    }
//
//    public boolean isLeftJewelRed() { return isRed; }
//    public boolean isLeftJewelBlue() { return isBlue; }
//    public boolean leftJewelIsUndetermined() { return ! isRed && ! isBlue; }
//
//
//
//    public void computeJewelColor(){
//        // get image, rotated so (0,0) is in the bottom left of the preview window
//        if (! camOp.imageReady()) return;
//
//        // only do this if an image has been returned from the camera
//        Bitmap rgbImage;
//        rgbImage = camOp.convertYuvImageToRgb(camOp.yuvImage, camOp.width, camOp.height, ds2);
//
//        // do we want to scale?
//        int minR = 255;
//        int maxR = 0;
//        int minB = 255;
//        int maxB = 0;
//        int minG = 255;
//        int maxG = 0;
//
//        if (scaling) {
//            // additional downsampling of the image
//            // set to 1 to disable further downsampling
//
//            for (int x = rgbImage.getWidth() / 2; x < rgbImage.getWidth(); x++) {
//                for (int y = rgbImage.getHeight() / 2; y < rgbImage.getHeight(); y++) {
//                    int pixel = rgbImage.getPixel(x, y);
//                    if (Color.red(pixel) < minR) minR = Color.red(pixel);
//                    if (Color.red(pixel) > maxR) maxR = Color.red(pixel);
//                    if (Color.blue(pixel) < minB) minB = Color.blue(pixel);
//                    if (Color.blue(pixel) > maxB) maxB = Color.blue(pixel);
//                    if (Color.green(pixel) < minG) minG = Color.green(pixel);
//                    if (Color.green(pixel) > maxG) maxG = Color.green(pixel);
//                }
//            }
//        }
//
//        //TODO (at competition): Manually test this threshold value by the field in the competition.
//        int redValue = 0;
//        int blueValue = 0;
//        int totValue = 0;
//        double coeffR = 255.0 / ((double)(maxR - minR));
//        double coeffB = 255.0 / ((double)(maxB - minB));
//        double coeffG = 255.0 / ((double)(maxG - minG));
//
//        for (int x = rgbImage.getWidth() / 2; x < rgbImage.getWidth(); x+=10) {
//            for (int y = rgbImage.getHeight() / 2; y < rgbImage.getHeight(); y+=10) {
//                int pixel = rgbImage.getPixel(x, y);
//
//                double valR = Color.red(pixel);
//                double valB = Color.blue(pixel);
//                double valG = Color.green(pixel);
//
//                if (scaling) {
//                    valR = (valR - minR) * coeffR;
//                    valB = (valB - minB) * coeffB;
//                    valG = (valG - minG) * coeffG;
//                }
//                if(valR > colOn && valB < colOff && valG < colOff)
//                    redValue+=valR;
//                if(valB > colOn && valR < colOff && valG < colOff)
//                    blueValue+=valB;
//                totValue ++;
//            }
//        }
//
//        int diff = redValue - blueValue;
////        threshold = threshold * totValue;
//
//        if(diff < -blueThreshold*totValue){
//            color = JewelColors.BLUE;
//        }else if (diff > redThreshold*totValue) {
//            color = JewelColors.RED;
//        } else {
//            color = JewelColors.UNKNOWN;
//        }
////            else colorString = "NONE";
//
////        camOp.telemetry.addData("actual difference: ", diff);
////
//////            colorString = newR-newB < threshold ? "BLUE is left" : "RED is left";//TODO: This value may be different under dimmer conditions
////        camOp.sleep(10);
////
////
////        camOp.telemetry.addData("Jewel Color ", getJewelColor());
//    }
//
//


//}


