package org.firstinspires.ftc.teamcode.Vision;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.vuforia.Frame;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.State;
import com.vuforia.ar.pl.DebugLog;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * Created by michaelzhou on 11/24/17.
 */

public class JewelColorDetection {
    ArrayList<PixelPosition> highReds = new ArrayList<PixelPosition>();
    ArrayList<PixelPosition> highBlues = new ArrayList<PixelPosition>();
    Color color = null;
//    double multiplier = 1.5;
    Bitmap bm;

    int colOn = 160;
    int colOff = 80;

    private static final String TAG = "ftc9773 SafeJasonReader";
    private static final boolean DEBUG = true;//TODO: Change this to false during competitions. For testing only.

    public JewelColorDetection(String filename){
        bm = BitmapFactory.decodeFile(filename);
    }

    public String analyze(){
        int[] argbPixels = new int[bm.getWidth() * bm.getHeight()];
        bm.getPixels(argbPixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());

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
        return verdict;
    }

//    private void saveScreenShot(int x, int y, int w, int h, String filename) {
//        Bitmap bmp = grabPixels(x, y, w, h);
//        try {
//            String path = Environment.getExternalStorageDirectory() + "/" + filename;
////            DebugLog.LOGD(path);
//
//            File file = new File(path);
//            file.createNewFile();
//
//            FileOutputStream fos = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
//
//            fos.flush();
//
//            fos.close();
//
//        } catch (Exception e) {
////            Log.d(TAG, "saveScreenShot: ");
//        }
//    }
//
//    private Bitmap grabPixels(int x, int y, int w, int h) {
//        int b[] = new int[w * (y + h)];
//        int bt[] = new int[w * h];
//        IntBuffer ib = IntBuffer.wrap(b);
//        ib.position(0);
//
//        GLES20.glReadPixels(x, 0, w, y + h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
//
//        for (int i = 0, k = 0; i < h; i++, k++) {
//            for (int j = 0; j < w; j++) {
//                int pix = b[i * w + j];
//                int pb = (pix >> 16) & 0xff;
//                int pr = (pix << 16) & 0x00ff0000;
//                int pix1 = (pix & 0xff00ff00) | pr | pb;
//                bt[(h - k - 1) * w + j] = pix1;
//            }
//        }
//
//        Bitmap sb = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
//        return sb;
//    }

//    private void transformIntoBitmap(State state){
//        Image imageRGB565 = null;
//        Frame frame = state.getFrame();
//
//        for (int i = 0; i < frame.getNumImages(); ++i) {
//            Image image = frame.getImage(i);
//            if (image.getFormat() == PIXEL_FORMAT.RGB565) {
//                imageRGB565 = image;
//                break;
//            }
//        }
//
//        if (imageRGB565 != null) {
//            ByteBuffer pixels = imageRGB565.getPixels();
//            byte[] pixelArray = new byte[pixels.remaining()];
//            pixels.get(pixelArray, 0, pixelArray.length());
//            int imageWidth = imageRGB565.getWidth();
//            int imageHeight = imageRGB565.getHeight();
//            int stride = imageRGB565.getStride();
//
//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inPreferredConfig = Bitmap.Config.RGB_565;
//            Bitmap bm = BitmapFactory.decodeByteArray(pixelArray, 0, pixelArray.length, opts);
//
//            // m_ivCamera is a android.widget.ImageView object.
//            m_ivCamera.setImageBitmap(bm);
//        }
//    }

}
