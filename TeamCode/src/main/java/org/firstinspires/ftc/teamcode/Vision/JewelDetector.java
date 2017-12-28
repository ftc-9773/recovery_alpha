package org.firstinspires.ftc.teamcode.Vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;

/**
 * Created by michaelzhou on 12/28/17.
 */

public class JewelDetector {
    LinearOpModeCamera camOp;
    boolean isRed, isBlue;
    int colOn = 160;
    int colOff = 80;
    int ds2 = 2;

    public JewelDetector(LinearOpModeCamera camOp){
        this.camOp = camOp;
        isRed = false;
        isBlue = false;
    }

    public void startCamera(){
        camOp.setCameraDownsampling(8);
        camOp.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    public String getJewelColor(){
        if (isRed) return "Red is left";
        if (isBlue) return "Blue is left";
        return "we don't know";
    }

    public boolean isLeftJewelRed() { return isRed; }
    public boolean isLeftJewelBlue() { return isBlue; }
    public boolean leftJewelIsUndetermined() { return ! isRed && ! isBlue; }

    public void onOffThreshold(boolean scaling){
        // get image, rotated so (0,0) is in the bottom left of the preview window
        if (! camOp.imageReady()) return;

        // only do this if an image has been returned from the camera
        Bitmap rgbImage;
        rgbImage = camOp.convertYuvImageToRgb(camOp.yuvImage, camOp.width, camOp.height, ds2);

        // do we want to scale?
        if (scaling) {}
        // additional downsampling of the image
        // set to 1 to disable further downsampling

        int redValue = 0;
        int blueValue = 0;
        int greenValue = 0;

        int minR = 255;
        int maxR = 0;
        int minB = 255;
        int maxB = 0;


        int redValues = 0, blueValues = 0;

        for (int x = rgbImage.getWidth() / 2; x < rgbImage.getWidth(); x++) {
            for (int y = rgbImage.getHeight() / 2; y < rgbImage.getHeight(); y++) {
                int pixel = rgbImage.getPixel(x, y);

                if(Color.red(pixel) < minR) minR = Color.red(pixel);
                if(Color.red(pixel) > maxR) maxR = Color.red(pixel);
                if(Color.blue(pixel) < minB) minB = Color.blue(pixel);
                if(Color.blue(pixel) > maxB) maxB = Color.blue(pixel);

                if(Color.red(pixel) > colOn && Color.blue(pixel) < colOff && Color.green(pixel) < colOff)
                    redValue++;
                if(Color.blue(pixel) > colOn && Color.red(pixel) < colOff && Color.green(pixel) < colOff)
                    blueValue++;

//                            if(Color.green(pixel) < minG) minG = Color.green(pixel);
//                            if(Color.green(pixel) > maxG) maxG = Color.green(pixel);
//                    redValues+=Color.red(pixel);
//                    blueValues+=Color.blue(pixel);
            }
        }

//            avgredValues = redValues / ((rgbImage.getWidth()/2)*(rgbImage.getHeight()/2));
//            avgblueValues = blueValues / ((rgbImage.getWidth()/2)*(rgbImage.getHeight()/2));

//            minRGB = Math.min(minR,minB);
//            maxRGB = Math.max(maxB,maxR);
//
//            newR = 255*(avgredValues - minRGB)/(maxRGB-minRGB);
//            newB = 255*(avgblueValues - minRGB)/(maxRGB-minRGB);

//            double threshold = newR - newB;

        //TODO (at competition): Manually test this threshold value by the field in the competition.
        double threshold = redValue - blueValue;
        if(scaling){
            for (int x = rgbImage.getWidth() / 2; x < rgbImage.getWidth(); x++) {
                for (int y = rgbImage.getHeight() / 2; y < rgbImage.getHeight(); y++) {
                    int pixel = rgbImage.getPixel(x, y);

                    if(Color.red(pixel) < minR) minR = Color.red(pixel);
                    if(Color.red(pixel) > maxR) maxR = Color.red(pixel);
                    if(Color.blue(pixel) < minB) minB = Color.blue(pixel);
                    if(Color.blue(pixel) > maxB) maxB = Color.blue(pixel);

                    if(Color.red(pixel) > colOn && Color.blue(pixel) < colOff && Color.green(pixel) < colOff)
                        redValue+=Color.red(pixel);
                    if(Color.blue(pixel) > colOn && Color.red(pixel) < colOff && Color.green(pixel) < colOff)
                        blueValue+=Color.blue(pixel);

//                            if(Color.green(pixel) < minG) minG = Color.green(pixel);
//                            if(Color.green(pixel) > maxG) maxG = Color.green(pixel);
//                    redValues+=Color.red(pixel);
//                    blueValues+=Color.blue(pixel);
                }
            }

        }



        if(threshold<0){
            isBlue = true;
            isRed = false;
        }
        else {
            isRed = true;
            isBlue = false;
        }
//            else colorString = "NONE";
        camOp.telemetry.addData("actual difference: ", threshold);

//            colorString = newR-newB < threshold ? "BLUE is left" : "RED is left";//TODO: This value may be different under dimmer conditions
        camOp.sleep(10);


        camOp.telemetry.addData("Jewel Color ", getJewelColor());
    }




}
