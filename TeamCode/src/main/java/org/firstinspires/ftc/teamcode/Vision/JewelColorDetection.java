package org.firstinspires.ftc.teamcode.Vision;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by michaelzhou on 11/24/17.
 */

public class JewelColorDetection {
    ArrayList<PixelPosition> highReds = new ArrayList<PixelPosition>();
    ArrayList<PixelPosition> highBlues = new ArrayList<PixelPosition>();
    Bitmap bm;

    int colOn = 160;
    int colOff = 80;

    private static final String TAG = "ftc9773 Jewel";
    private static final boolean DEBUG = true;//TODO: Change this to false during competitions. For testing only.



    public JewelColorDetection(){
//        Camera camera = Camera.open(0);
        analyze();

//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        byte[] imageBytes = out.toByteArray();
//        Bundle extras = data.getExtras();
//        extras.getParcelable("data");
//        YuvImage yuvImage = new YuvImage(imageBytes, ImageFormat.NV21, width, height, null);
//        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 0, out);
//        byte[] data = parameters.flatten().getBytes();

//        YuvImage yuvImage = new YuvImage(data, ImageFormat.RGB_565, width, height, null);//RGB_565 or NV21

//        ByteArrayOutputStream output = new ByteArrayOutputStream();

//        byte[] imageBytes = output.toByteArray();
//        bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public void analyze(){


//        Camera.PreviewCallback callback = new Camera.PreviewCallback() {
//            String verdict;
//            @Override
//            public void onPreviewFrame(byte[] data, Camera camera) {
//
////                return verdict;
//            }
//        };


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
