package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.AnalogSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.util.SerialNumber;

/**
 * Created by Vikesh on 10/28/2017.
 */
@TeleOp(name = "Align By Sensor")
public class AlignBySensor extends LinearOpMode {
    AnalogInput input0;
    AnalogInput input1;
    AnalogInput input2;
    AnalogInput input3;
    @Override
    public void runOpMode() throws InterruptedException {

        input0 = hardwareMap.get(AnalogInput.class, "input0");
        input1 = hardwareMap.get(AnalogInput.class, "input1");
        input2 = hardwareMap.get(AnalogInput.class, "input2");
        input3 = hardwareMap.get(AnalogInput.class, "input3");
        waitForStart();

        while(opModeIsActive()){
            telemetry.addData("position 1", "%.3f", input0.getVoltage()/3.245);
            telemetry.addData("position 2", "%.3f", input1.getVoltage()/3.245);
            telemetry.addData("position 3", "%.3f", input2.getVoltage()/3.245);
            telemetry.addData("position 4", "%.3f", input3.getVoltage()/3.245);
            telemetry.update();
        }

    }
}
