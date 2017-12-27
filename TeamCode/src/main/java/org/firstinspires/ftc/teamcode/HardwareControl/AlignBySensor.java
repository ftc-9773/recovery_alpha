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

        double flwZeroPosition = coefficientsFile.getDouble("flwStraightPosition");
        double frwZeroPosition = coefficientsFile.getDouble("frwStraightPosition");
        double blwZeroPosition = coefficientsFile.getDouble("blwStraightPosition");
        double brwZeroPosition = coefficientsFile.getDouble("brwStraightPosition");

        waitForStart();

        while(opModeIsActive()){

            aButton.recordNewValue(gamepad1.a);
            if (aButton.isJustOn()) {
                coefficientsFile.modifyDouble("flwStraightPosition", 2*Math.PI * (1 - flwAbsEncoder.getVoltage() / 3.23));
                coefficientsFile.modifyDouble("frwStraightPosition", 2*Math.PI * (1 - frwAbsEncoder.getVoltage() / 3.23));
                coefficientsFile.modifyDouble("blwStraightPosition", 2*Math.PI * (1 - blwAbsEncoder.getVoltage() / 3.23));
                coefficientsFile.modifyDouble("brwStraightPosition", 2*Math.PI * (1 - brwAbsEncoder.getVoltage() / 3.23));

                coefficientsFile.updateFile();

                flwZeroPosition = coefficientsFile.getDouble("flwStraightPosition");
                frwZeroPosition = coefficientsFile.getDouble("frwStraightPosition");
                blwZeroPosition = coefficientsFile.getDouble("blwStraightPosition");
                brwZeroPosition = coefficientsFile.getDouble("brwStraightPosition");

            }

            telemetry.addData("Front Left Position: ", setOn2Pi( 2*Math.PI * (1 - flwAbsEncoder.getVoltage() / 3.23)) - flwZeroPosition);
            telemetry.addData("Front Right Position: ", setOn2Pi(2*Math.PI * (1 - frwAbsEncoder.getVoltage() / 3.23)) - frwZeroPosition);
            telemetry.addData("Back Left Position: ", setOn2Pi(2*Math.PI * (1 - blwAbsEncoder.getVoltage() / 3.23)) - blwZeroPosition);
            telemetry.addData("Back Right Position: ", setOn2Pi(2*Math.PI * (1 - brwAbsEncoder.getVoltage() / 3.23)) - brwZeroPosition);
            telemetry.update();

        }
    }

    private double setOn2Pi(double num) {
        if (num > 2*Math.PI) {
            return num - 2*Math.PI;
        } else if (num < 0) {
            return num + 2*Math.PI;
        } else {
            return num;
        }
    }
}

