package org.firstinspires.ftc.teamcode.Vision;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by michaelzhou on 11/19/17.
 */

public class VumarkGlyphPattern {

    VuforiaLocalizer vuforia;
    RelicRecoveryVuMark vuMark = null;

    public VumarkGlyphPattern(){
    }

    public void init(HardwareMap hardwareMap){
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AVnz6or/////AAAAGdJgMmsGkkibrBL0inMjc7t54jDqna5iT9Rxes8KZU9k0cZQzyVZCbu3TRLqlFWiujEO7kX8tNMrqcya8ZcZLE4qebycHhi9ZMtMjs7oeb/g1/3TLizLP7ShiVmMQoiCMNiBHqFElzNyL5t5tPk21drKY+aw7q9aHZVgvY1R+ilPd31KKAFn+K077ympaGwv+ywll9uwvvRvYUdxqDYhkAng8bUK26WoCihPDsf5rnRzY9Y/eNr8hZTZwCc6xx1a04agmXLY2JIZ9/8LmB7nRotFXxYw9xoY40DvmKIwcqV77/kDHZ5QG45lRXtSbVxUcUqL2GgojvxtFCDO7/FeTVZoU2ukbT3lA6XrSJ1QvtfX";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        relicTrackables.activate();

        vuMark = RelicRecoveryVuMark.from(relicTemplate);
    }

    public RelicRecoveryVuMark getColumn(){
        return vuMark;
    }
}
