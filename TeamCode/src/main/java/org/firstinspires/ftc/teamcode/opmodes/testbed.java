package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.teamcode.RASI.RasiExecutor;

/**
 * Created by Vikesh on 11/19/2017.
 */
@Autonomous(name = "testbed")
public class testbed extends LinearOpModeCamera {


    @Override
    public void runOpMode() throws InterruptedException{
        RasiExecutor rasiExecutor = new RasiExecutor(this, "/sdcard/FIRST/team9773/rasi18/", "opmodetest.rasi");
        rasiExecutor.runRasi();
    }
}
