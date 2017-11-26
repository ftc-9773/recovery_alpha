package org.firstinspires.ftc.teamcode.Vision;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.graphics.Color;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by michaelzhou on 11/25/17.
 */

public class CameraActivity extends Activity{

    ArrayList<PixelPosition> highReds = new ArrayList<PixelPosition>();
    ArrayList<PixelPosition> highBlues = new ArrayList<PixelPosition>();
    Bitmap bm;

    int colOn = 160;
    int colOff = 80;

    private static final String TAG = "ftc9773 Jewel";
    private static final boolean DEBUG = true;//TODO: Change this to false during competitions. For testing only.

    public void onPreviewFrame(byte[] frame, Camera camera)
    {
//       android.hardware.Camera camera = android.hardware.Camera.open(0);
        android.hardware.Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;
        //Do your processing here... Use the byte[] called "data"
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bm.copyPixelsFromBuffer(ByteBuffer.wrap(frame));

        for(int x = bm.getWidth()/2; x<bm.getWidth(); x++){
            for(int y = bm.getHeight()/2; y<bm.getHeight(); y++){
                int pixel = bm.getPixel(x,y);
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);

                if(redValue>colOn && blueValue<colOff && greenValue<colOff){
                    highReds.add(new PixelPosition(x,y));
                }
                if(blueValue>colOn && redValue<colOff && greenValue<colOff) {
                    highBlues.add(new PixelPosition(x, y));
                }
            }
        }

        String verdict = highBlues.size()>highReds.size() ? "BLUE IS LEFT" : "RED IS LEFT";
        if(DEBUG) Log.e(TAG,verdict);
    }
}
