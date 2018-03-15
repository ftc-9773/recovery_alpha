package org.firstinspires.ftc.teamcode.RASI;

import android.util.Log;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Vikesh on 2/20/2018.
 */

public class TeamRasiCommands {
    LinearOpModeCamera linearOpModeCamera;
    Telemetry telemetry;
    public TeamRasiCommands(LinearOpModeCamera linearOpModeCamera){
        this.linearOpModeCamera = linearOpModeCamera;
        telemetry = linearOpModeCamera.telemetry;
    }
    public void LogHello(){
        Log.i("TeamRasiCommands", "Hello");
        telemetry.addData("Rasi Says... ", "Hello!");
        telemetry.update();
    }
    public void copyMe(String message){
        telemetry.addData("Rasi also says ", message);
        telemetry.update();
    }
}
