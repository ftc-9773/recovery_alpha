package org.firstinspires.ftc.teamcode.InitialTests;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by michaelzhou on 11/5/17.
 */
@Autonomous(name="ColorSensorTest", group="Color Detection")
public class ColorSensorTest extends LinearOpMode{
    ColorSensor colorSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        colorSensor.enableLed(true);

        telemetry.addData("About to start", "...");
        waitForStart();
        while(opModeIsActive()){
            telemetry.addData("Red: ", colorSensor.red());
            telemetry.addData("Blue: ", colorSensor.blue());

            if(colorSensor.red()>colorSensor.blue()){
                telemetry.addData("Color Detected: ", "RED");
            } else if(colorSensor.blue()>colorSensor.red()){
                //WATCH OUT! SOMETIMES MAY MISDETECT IF SENSOR NOT PLACED CLOSE ENOUGH!!!!
                telemetry.addData("Color Detected: ", "BLUE");
            } else {
                telemetry.addData("Color Detected: ", "NOT DETECTED");
            }
            telemetry.update();
        }

        colorSensor.enableLed(false);
    }

}