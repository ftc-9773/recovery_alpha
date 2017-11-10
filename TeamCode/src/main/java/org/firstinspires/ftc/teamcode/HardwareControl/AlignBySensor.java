package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.AnalogSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.util.SerialNumber;

import org.firstinspires.ftc.teamcode.JSON.jsonIO;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Vikesh on 10/28/2017.
 */
@TeleOp(name = "Align By Sensor")
public class AlignBySensor extends LinearOpMode {
    AnalogInput brwAbsEncoder;
    AnalogInput frwAbsEncoder;
    AnalogInput flwAbsEncoder;
    AnalogInput blwAbsEncoder;
    SafeJsonReader jsonio = new SafeJsonReader("positions");
    double[] positions = new double[4];
    @Override
    public void runOpMode() throws InterruptedException {

        brwAbsEncoder = hardwareMap.get(AnalogInput.class, "brwAbsEncoder");
        frwAbsEncoder = hardwareMap.get(AnalogInput.class, "frwAbsEncoder");
        flwAbsEncoder = hardwareMap.get(AnalogInput.class, "flwAbsEncoder");
        blwAbsEncoder = hardwareMap.get(AnalogInput.class, "blwAbsEncoder");
        waitForStart();

        while(opModeIsActive()){
            telemetry.addData("position 1", "%.3f", positions[0]);
            telemetry.addData("position 2", "%.3f", positions[1]);
            telemetry.addData("position 3", "%.3f", positions[2]);
            telemetry.addData("position 4", "%.3f", positions[3]);
            telemetry.addData("modOnePos: ", "%.3f", jsonio.getDouble("modOneDefPos"));
            telemetry.update();
            positions[0] = (brwAbsEncoder.getVoltage()/3.24);
            positions[1] = (frwAbsEncoder.getVoltage()/3.24);
            positions[2] = (flwAbsEncoder.getVoltage()/3.24);
            positions[3] = (blwAbsEncoder.getVoltage()/3.24);

            jsonio.modifyDouble("modOneDefPos", 0.5);
            jsonio.modifyDouble("modTwoDefPos", 0.6);
            jsonio.modifyDouble("modThreeDefPos", positions[2]);
            jsonio.modifyDouble("modFourDefPos", positions[3]);
        }
        jsonio.updateFile();

    }
}
