package org.firstinspires.ftc.teamcode.Vision;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by michaelzhou on 11/24/17.
 */
@Autonomous(name="Jewel Color Detection", group="Vision")
public class JewelColorDetectionTest extends LinearOpMode{

    private static final String TAG = "ftc9773 Jewel";
    private static final boolean DEBUG = true;

    @Override
    public void runOpMode() throws InterruptedException {
//        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

//        Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
//        Bitmap bm = (Bitmap) i.getExtras().get("data");
//         = (Bitmap) extras.get("data");
//        Camera camera = ;
////        camera =
//        Camera.Parameters parameters = camera.getParameters();
//        int width = parameters.getPreviewSize().width;
//        int height = parameters.getPreviewSize().height;
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        byte[] imageBytes = out.toByteArray();

//        YuvImage yuvImage = new YuvImage(imageBytes, ImageFormat.NV21, width, height, null);
//        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 0, out);
//
////        Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//        Camera camera = Camera.open(0);
//        State state Vuforia.sta
//
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
//            opts.inPreferredConfig = Bitmap.Config.RGB_656;
//            Bitmap bm = BitmapFactory.decodeByByteArray(pixelArray, 0, pixelArray.length, opts);
//
//            // m_ivCamera is a android.widget.ImageView object.
//            camera.setImageBitmap(bm);

        waitForStart();


        while(opModeIsActive()){

            JewelColorDetection obj = new JewelColorDetection();
//            telemetry.addData("verdict 1: ", obj.analyze());
//            telemetry.addData(TAG, obj);
            telemetry.update();
//            telemetry.update();
        }
    }
}
