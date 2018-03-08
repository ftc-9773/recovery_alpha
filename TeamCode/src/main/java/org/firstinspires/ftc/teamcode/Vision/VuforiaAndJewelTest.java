package org.firstinspires.ftc.teamcode.Vision;

import android.graphics.Bitmap;
import android.os.Environment;

import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Created by joshua9889 on 10/14/2017.
 *
 */

public class VuforiaAndJewelTest {

    // Vuforia THings
    private String vuforiaLicenseKey = "AVnz6or/////AAAAGdJgMmsGkkibrBL0inMjc7t54jDqna5iT9Rxes8KZU9k0cZQzyVZCbu3TRLqlFWiujEO7kX8tNMrqcya8ZcZLE4qebycHhi9ZMtMjs7oeb/g1/3TLizLP7ShiVmMQoiCMNiBHqFElzNyL5t5tPk21drKY+aw7q9aHZVgvY1R+ilPd31KKAFn+K077ympaGwv+ywll9uwvvRvYUdxqDYhkAng8bUK26WoCihPDsf5rnRzY9Y/eNr8hZTZwCc6xx1a04agmXLY2JIZ9/8LmB7nRotFXxYw9xoY40DvmKIwcqV77/kDHZ5QG45lRXtSbVxUcUqL2GgojvxtFCDO7/FeTVZoU2ukbT3lA6XrSJ1QvtfX";
    private VuforiaLocalizer vuforia;
    private VuforiaTrackables relicTrackables;
    private VuforiaTrackable relicTemplate;
    private RelicRecoveryVuMark ouputVuMark = RelicRecoveryVuMark.UNKNOWN;
    VuforiaLocalizer.Parameters params;

    // Bitmap things
    private Image img = null;
    public Bitmap bm = null;

    /**
     */
    public VuforiaAndJewelTest( ) {
    }

    public void setup(VuforiaLocalizer.CameraDirection cameraDirection) {
        this.setup(cameraDirection, true);
    }

    /**
     * @param cameraDirection What camera to use
     *                        Front is Selfie Camera
     */
    public void setup(VuforiaLocalizer.CameraDirection cameraDirection, boolean display) {
        if (display) {
            params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        } else {
            params = new VuforiaLocalizer.Parameters();
        }

        params.vuforiaLicenseKey = this.vuforiaLicenseKey;
        params.cameraDirection = cameraDirection;

        this.vuforia = ClassFactory.createVuforiaLocalizer(params);
        vuforia.setFrameQueueCapacity(1);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);

        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");
        this.activateVuforia();
    }

    /**
     * Activate Vuforia Tracker
     */
    public void activateVuforia() {
        this.relicTrackables.activate();
    }

    /**
     * Disable Vuforia Tracker
     */
    public void disableVuforia() {
        this.relicTrackables.deactivate();
    }

    /**
     * @return output vumark
     */
    public RelicRecoveryVuMark getOuputVuMark() {
        return this.ouputVuMark;
    }

    /**
     * Use this method to update the current vumark
     */
    public void update() {
        // VuMark Update
        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
        if (vuMark != RelicRecoveryVuMark.UNKNOWN)
            this.ouputVuMark = vuMark;
    }

    /**
     * Used for Camera
     *
     * @param frame  Vuforia frame
     * @param format Image type
     * @return Image
     */
    public static Image getImageFromFrame(VuforiaLocalizer.CloseableFrame frame, int format) {
        if (frame != null) {
            long numImgs = frame.getNumImages();
            for (int i = 0; i < numImgs; i++) {
                if (frame.getImage(i).getFormat() == format) {
                    return frame.getImage(i);
                }//if
                Thread.yield();
            }//for

        }
        return null;
    }

    /**
     * @param downsampling How much we should reduce the image by
     * @return The Bitmap from last Vuforia Frame
     */
    // Get Bitmap from vuforia
    public Bitmap getBm(int downsampling) {
        try {
            img = getImageFromFrame(vuforia.getFrameQueue().take(), PIXEL_FORMAT.RGB565);
            Bitmap bm = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.RGB_565);
            bm.copyPixelsFromBuffer(img.getPixels());
            Bitmap scaled = Bitmap.createScaledBitmap(bm, bm.getWidth() / downsampling, bm.getHeight() / downsampling, true);
            bm.recycle();
            return scaled;
        } catch (Exception e) {
            RobotLog.a("Problem with getBm");
            return null;
        }
    }

    public static int red(int pixel) {
        return (pixel >> 16) & 0xff;
    }

    public static int green(int pixel) {
        return (pixel >> 8) & 0xff;
    }

    public static int blue(int pixel) {
        return pixel & 0xff;
    }

    public static int gray(int pixel) {
        return (red(pixel) + green(pixel) + blue(pixel));
    }

    /**
     * @param finalBitmap Save Bitmap to /root/saved_images
     */
    public static void SaveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}