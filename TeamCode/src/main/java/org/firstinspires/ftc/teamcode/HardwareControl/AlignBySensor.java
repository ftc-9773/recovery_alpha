package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.util.SerialNumber;

import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
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
    double[] modPositions = new double[4];
    @Override
    public void runOpMode() throws InterruptedException {

        brwAbsEncoder = hardwareMap.get(AnalogInput.class, "brwAbsEncoder");
        frwAbsEncoder = hardwareMap.get(AnalogInput.class, "frwAbsEncoder");
        flwAbsEncoder = hardwareMap.get(AnalogInput.class, "flwAbsEncoder");
        blwAbsEncoder = hardwareMap.get(AnalogInput.class, "blwAbsEncoder");
        waitForStart();

        while(opModeIsActive()){

            modPositions[0] = ((brwAbsEncoder.getVoltage()*2*Math.PI)/3.24);
            modPositions[1] = ((frwAbsEncoder.getVoltage()*2*Math.PI)/3.24);
            modPositions[2] = ((flwAbsEncoder.getVoltage()*2*Math.PI)/3.24);
            modPositions[3] = ((blwAbsEncoder.getVoltage()*2*Math.PI)/3.24);

            telemetry.addData("position 1", "%.3f", modPositions[0]);
            telemetry.addData("position 2", "%.3f", modPositions[1]);
            telemetry.addData("position 3", "%.3f", modPositions[2]);
            telemetry.addData("position 4", "%.3f", modPositions[3]);

            telemetry.addData("json string: ", jsonio.jsonRoot.toString());

            telemetry.update();

        }
        jsonio.modifyDouble("modOneDefPos", modPositions[0]);
        jsonio.modifyDouble("modTwoDefPos", modPositions[1]);
        jsonio.modifyDouble("modThreeDefPos", modPositions[2]);
        jsonio.modifyDouble("modFourDefPos", modPositions[3]);

        jsonio.updateFile();

    }
}
