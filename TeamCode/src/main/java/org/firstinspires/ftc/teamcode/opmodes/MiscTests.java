package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareControl.JewelServoController;

/**
 * Created by nicky on 1/2/18.
 */

@TeleOp(name = "Misc Tests")
public class MiscTests extends LinearOpMode {

    JewelServoController jewel;
    @Override
    public void runOpMode() throws InterruptedException {
        jewel = new JewelServoController(hardwareMap);
        waitForStart();
        while (opModeIsActive()) {
            jewel.lowerArm();
        }
    }
}
