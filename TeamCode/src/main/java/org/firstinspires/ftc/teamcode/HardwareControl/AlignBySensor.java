package org.firstinspires.ftc.teamcode.HardwareControl;

import android.widget.Button;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.util.SerialNumber;

import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.resources.ButtonStatus;

/**
 * Created by Vikesh on 10/28/2017.
 */
@TeleOp(name = "Allign Swerve Modules")
public class AlignBySensor extends LinearOpMode {
    AnalogInput brwAbsEncoder;
    AnalogInput frwAbsEncoder;
    AnalogInput flwAbsEncoder;
    AnalogInput blwAbsEncoder;

    ButtonStatus aButton = new ButtonStatus();

    SafeJsonReader coefficientsFile = new SafeJsonReader("swervePIDCoefficients");

    @Override
    public void runOpMode() throws InterruptedException {

        flwAbsEncoder = hardwareMap.analogInput.get("flwAbsEncoder");
        frwAbsEncoder = hardwareMap.analogInput.get("frwAbsEncoder");
        blwAbsEncoder = hardwareMap.analogInput.get("blwAbsEncoder");
        brwAbsEncoder = hardwareMap.analogInput.get("brwAbsEncoder");

        waitForStart();

        while(opModeIsActive()){

            aButton.recordNewValue(gamepad1.a);
            if (aButton.isJustOn()) {
                coefficientsFile.modifyDouble("flwStraightPosition", flwAbsEncoder.getVoltage() / 3.24 * 2 * Math.PI);
                coefficientsFile.modifyDouble("frwStraightPosition", frwAbsEncoder.getVoltage() / 3.24 * 2 * Math.PI);
                coefficientsFile.modifyDouble("blwStraightPosition", blwAbsEncoder.getVoltage() / 3.24 * 2 * Math.PI);
                coefficientsFile.modifyDouble("brwStraightPosition", brwAbsEncoder.getVoltage() / 3.24 * 2 * Math.PI);

                coefficientsFile.updateFile();
            }

            telemetry.addData("Front Left Position: ", flwAbsEncoder.getVoltage() / 3.24 * 2 * Math.PI);
            telemetry.addData("Front Right Position: ", frwAbsEncoder.getVoltage() / 3.24 * 2 * Math.PI);
            telemetry.addData("Back Left Position: ", blwAbsEncoder.getVoltage() / 3.24 * 2 * Math.PI);
            telemetry.addData("Back Right Position: ", brwAbsEncoder.getVoltage() / 3.24 * 2 * Math.PI);
            telemetry.update();

        }
    }
}
