package org.firstinspires.ftc.teamcode.Vision;

import android.graphics.Bitmap;
import android.util.Log;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by michaelzhou on 11/19/17.
 */

public class VumarkGlyphPattern {
    VuforiaTrackable template;
    HardwareMap hardwareMap;
    ClosableVuforiaLocalizer vuforia;

    private static final String vuforiaKey = "AVnz6or/////AAAAGdJgMmsGkkibrBL0inMjc7t54jDqna5iT9Rxes8KZU9k0cZQzyVZCbu3TRLqlFWiujEO7kX8tNMrqcya8ZcZLE4qebycHhi9ZMtMjs7oeb/g1/3TLizLP7ShiVmMQoiCMNiBHqFElzNyL5t5tPk21drKY+aw7q9aHZVgvY1R+ilPd31KKAFn+K077ympaGwv+ywll9uwvvRvYUdxqDYhkAng8bUK26WoCihPDsf5rnRzY9Y/eNr8hZTZwCc6xx1a04agmXLY2JIZ9/8LmB7nRotFXxYw9xoY40DvmKIwcqV77/kDHZ5QG45lRXtSbVxUcUqL2GgojvxtFCDO7/FeTVZoU2ukbT3lA6XrSJ1QvtfX";

    private Image img = null;
    public Bitmap bm = null;

    private static final String TAG = "ftc9773_Vuforia" ;



    public VumarkGlyphPattern(HardwareMap hardwareMap){
        this.hardwareMap = hardwareMap;
        this.template = initialTemplate();


    }

    private VuforiaTrackable initialTemplate(){
        // create vuforia object;
        vuforia  = createMyVuforia();
        // make vuforia able to pass images
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        vuforia.setFrameQueueCapacity(1);

        // initialize trackables
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        relicTrackables.activate();

        return relicTemplate;
    }
    private ClosableVuforiaLocalizer createMyVuforia(){
        ClosableVuforiaLocalizer newVuforia;
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = vuforiaKey;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        newVuforia = new ClosableVuforiaLocalizer(parameters);
        return newVuforia;

    }


//    public Bitmap getBitmap() throws InterruptedException {
//        VuforiaLocalizer.CloseableFrame frame = vuforia.getFrameQueue().take();
////        for(int i = 0; i<frame.getNumImages(); i++){
//        Image image = frame.getImage(0);
////            if (image.getWidth() == imageWidth && image.getHeight() == imageHeight &&
////                    image.getFormat() == PIXEL_FORMAT.RGB565)
////            {
//        Bitmap bm = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
//        return bm;
////                bm.copyPixelsFromBuffer(image.getPixels());
////                Utils.bitmapToMat(bm, frame);
////                break;
////            }
////
////  }
//    }


    public Bitmap getBitMap (){
        Log.i(TAG,"current number of frames in the queue: "+  vuforia.getFrameQueueCapacity());

        VuforiaLocalizer.CloseableFrame frame ;
        try {
            frame = vuforia.getFrameQueue().take(); //takes the frame at the head of the queue
        }  catch (InterruptedException e) {
            Log.e (TAG, "error taking frame from queue");
            e.printStackTrace();
            return null ;
        }

        long numImages = frame.getNumImages();
        Image rgb = null;

        if(frame == null){
            Log.e(TAG, "null frame received");
        }

        for (int i = 0; i < numImages; i++) {
            if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                rgb = frame.getImage(i);
                break;
            }
        }
        if (rgb == null) return null;


        //rgb is now the Image object that weve used in the video
        Bitmap bm = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);
        bm.copyPixelsFromBuffer(rgb.getPixels());
        frame.close();

        return bm ;
    }

    public RelicRecoveryVuMark getColumn(){
        return RelicRecoveryVuMark.from(template);
    }

    public void close(){
        vuforia.close();
    }

    public void openNew(){
        this.template = initialTemplate();
    }


}
