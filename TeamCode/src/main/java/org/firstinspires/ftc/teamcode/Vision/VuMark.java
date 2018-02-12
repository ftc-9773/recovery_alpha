package org.firstinspires.ftc.teamcode.Vision;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by michaelzhou on 12/3/17.
 */

public class VuMark {

    public static final String TAG = "Vuforia VuMark Sample";
    public RelicRecoveryVuMark vuMark = null;
    VuforiaLocalizer vuforia;
    VuforiaTrackable relicTemplate;

    public VuMark(HardwareMap hardwareMap){
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // OR...  Do Not Activate the Camera Monitor View, to save power
        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        // extra product key:     Ad54BSb/////AAAAmQqsuGySKkaas2pwy3WFR1BP1zrD+DNVo8iKeVUDbl7CZAyzar+W28H4L1/HuV3MQeOFZOr+PZj0LS8rTySfQ4gMkPwQBj28FY1RAcGFx1Ko96/IfphInce7UiJqO5k6L0HqePdMJ7Wm8YTnu20yKyNAu958c0FRWCKNh1kddNKEH3ud5XACkpY0HUoEJRfU7B4jejLt/wbjcPz2oeWGiwIxblxTLeeHmRmQoH18TMfbNz7taOmXMuwWRhSaE75aXAbjsSABkBWtJA6HyGaZg6uDjoZCUa5JT91+Rjuy+VnhKD5/cEHiUVg9Oj2LM2oN19hsZEKEe+CQGHMWj+GVyKf3PIb4TpisdghW7LbLJ1Xu

        parameters.vuforiaLicenseKey = "AVnz6or/////AAAAGdJgMmsGkkibrBL0inMjc7t54jDqna5iT9Rxes8KZU9k0cZQzyVZCbu3TRLqlFWiujEO7kX8tNMrqcya8ZcZLE4qebycHhi9ZMtMjs7oeb/g1/3TLizLP7ShiVmMQoiCMNiBHqFElzNyL5t5tPk21drKY+aw7q9aHZVgvY1R+ilPd31KKAFn+K077ympaGwv+ywll9uwvvRvYUdxqDYhkAng8bUK26WoCihPDsf5rnRzY9Y/eNr8hZTZwCc6xx1a04agmXLY2JIZ9/8LmB7nRotFXxYw9xoY40DvmKIwcqV77/kDHZ5QG45lRXtSbVxUcUqL2GgojvxtFCDO7/FeTVZoU2ukbT3lA6XrSJ1QvtfX";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
         relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        relicTrackables.activate();

        vuMark = RelicRecoveryVuMark.from(relicTemplate);

    }

//    public RelicRecoveryVuMark getColumnFromPattern(){
////        if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
////
////               /* Found an instance of the template. In the actual game, you will probably
////                * loop until this condition occurs, then move on to act accordingly depending
////                * on which VuMark was visible. */
//////            telemetry.addData("VuMark", "%s visible", vuMark);
////
////               /* For fun, we also exhibit the navigational pose. In the Relic Recovery game,
////                * it is perhaps unlikely that you will actually need to act on this pose information, but
////                * we illustrate it nevertheless, for completeness. */
//////            OpenGLMatrix pose = ((VuforiaTrackableDefaultListener)relicTemplate.getListener()).getPose();
////
////                /* We further illustrate how to decompose the pose into useful rotational and
////                 * translational components */
//////            if (pose != null) {
//////                VectorF trans = pose.getTranslation();
//////                Orientation rot = Orientation.getOrientation(pose, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
//////
//////                // Extract the X, Y, and Z components of the offset of the target relative to the robot
//////                double tX = trans.get(0);
//////                double tY = trans.get(1);
//////                double tZ = trans.get(2);
//////
//////                // Extract the rotational components of the target relative to the robot
//////                double rX = rot.firstAngle;
//////                double rY = rot.secondAngle;
//////                double rZ = rot.thirdAngle;
//////            }
////        }
////        return vuMark;
//    }

   
}
