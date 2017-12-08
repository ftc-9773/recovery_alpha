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

    SafeJsonReader coefficientsFile = new SafeJsonReader("SwerveModuleZeroPositions");

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
                coefficientsFile.modifyDouble("flwStraightPosition", 2*Math.PI * (1 - flwAbsEncoder.getVoltage() / 3.24));
                coefficientsFile.modifyDouble("frwStraightPosition", 2*Math.PI * (1 - frwAbsEncoder.getVoltage() / 3.24));
                coefficientsFile.modifyDouble("blwStraightPosition", 2*Math.PI * (1 - blwAbsEncoder.getVoltage() / 3.24));
                coefficientsFile.modifyDouble("brwStraightPosition", 2*Math.PI * (1 - brwAbsEncoder.getVoltage() / 3.24));

                coefficientsFile.updateFile();
            }

            telemetry.addData("Front Left Position: ", 2*Math.PI * (1 - flwAbsEncoder.getVoltage() / 3.24));
            telemetry.addData("Front Right Position: ", 2*Math.PI * (1 - frwAbsEncoder.getVoltage() / 3.24));
            telemetry.addData("Back Left Position: ", 2*Math.PI * (1 - blwAbsEncoder.getVoltage() / 3.24));
            telemetry.addData("Back Right Position: ", 2*Math.PI * (1 - brwAbsEncoder.getVoltage() / 3.24));
            telemetry.update();

        }
    }
}
