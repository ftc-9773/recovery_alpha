package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.resources.ButtonStatus;

/**
 * Created by nicky on 11/11/17.
 */


@TeleOp(name = "Voltage Test")
public class VoltageTest extends LinearOpMode {

    AnalogInput flwAbsEncoder;
    Servo flwServo;

    ButtonStatus buttonA = new ButtonStatus();
    ButtonStatus buttonY = new ButtonStatus();

    ButtonStatus buttonX = new ButtonStatus();
    ButtonStatus buttonB = new ButtonStatus();

    double servoPower = 0.5;
    double servoIncrement = 0.1;

    @Override
    public void runOpMode() throws InterruptedException {
        flwAbsEncoder = hardwareMap.analogInput.get("flwAbsEncoder");
        flwServo = hardwareMap.servo.get("flwServo");


        waitForStart();

        while (opModeIsActive()) {

            buttonA.recordNewValue(gamepad1.a);
            buttonY.recordNewValue(gamepad1.y);
            buttonX.recordNewValue(gamepad1.x);
            buttonB.recordNewValue(gamepad1.b);

            if (buttonA.isJustOn()) { servoPower -= servoIncrement; }
            if (buttonY.isJustOn()) { servoPower += servoIncrement; }
            if (buttonX.isJustOn()) { servoIncrement /= 10; }
            if (buttonB.isJustOn()) { servoIncrement *= 10; }

            flwServo.setPosition(servoPower);

            telemetry.addData("Servo Power: ", servoPower);
            telemetry.addData("Servo Increment: ", servoIncrement);

            telemetry.addData("Encoder Voltage (front left) :", flwAbsEncoder.getVersion());
            telemetry.update();
        }
    }

    }
