package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.HardwareControl.SwerveModule;
import org.firstinspires.ftc.teamcode.resources.Vector;

/**
 * Created by Vikesh on 10/29/2017.
 */

@TeleOp(name = "Swerve")
public class Swerve extends LinearOpMode {

    private static final String TAG = "ftc9773 Swerve";

    @Override
    public void runOpMode() throws InterruptedException {

        Log.e(TAG, "Started initializing");

        SwerveModule module0 = new SwerveModule(hardwareMap, "flw", "modOneDefPos");

        SwerveModule module1 = new SwerveModule(hardwareMap, "frw", "modTwoDefPos");
        SwerveModule module2 = new SwerveModule(hardwareMap, "blw", "modThreeDefPos");
        SwerveModule module3 = new SwerveModule(hardwareMap, "brw", "modFourDefPos");


        // encoders for swerve drive
        AnalogInput input0 = hardwareMap.get(AnalogInput.class, "flwAbsEncoder");
        AnalogInput input1 = hardwareMap.get(AnalogInput.class, "frwAbsEncoder");
        AnalogInput input2 = hardwareMap.get(AnalogInput.class, "blwAbsEncoder");
        AnalogInput input3 = hardwareMap.get(AnalogInput.class, "brwAbsEncoder");

        Log.e(TAG, "Initialized the four encoders");

        waitForStart();
        while(opModeIsActive()) {
            Log.e(TAG, "Module 0");
            module0.setVector(gamepad1.left_stick_x, gamepad1.left_stick_y * (-1));
            Log.e(TAG, "Module 1");
            module1.setVector(gamepad1.left_stick_x, gamepad1.left_stick_y * (-1));
            Log.e(TAG, "Module 2");
            module2.setVector(gamepad1.left_stick_x, gamepad1.left_stick_y * (-1));
            Log.e(TAG, "Module 3");
            module3.setVector(gamepad1.left_stick_x, gamepad1.left_stick_y * (-1));

            telemetry.addData("position 1", "%.3f", input0.getVoltage()/3.245);
            telemetry.addData("position 2", "%.3f", input1.getVoltage()/3.245);
            telemetry.addData("position 3", "%.3f", input2.getVoltage()/3.245);
            telemetry.addData("position 4", "%.3f", input3.getVoltage()/3.245);

            telemetry.addData("Gamepad x:", gamepad1.left_stick_x);
            telemetry.addData("Gamepad y:", gamepad1.left_stick_y);

            telemetry.update();
        }
    }
}
