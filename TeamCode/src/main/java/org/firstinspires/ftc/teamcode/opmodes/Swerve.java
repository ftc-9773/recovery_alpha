package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.HardwareControl.SwerveModule;
import org.firstinspires.ftc.teamcode.resources.cartesianVector;
import org.firstinspires.ftc.teamcode.resources.polarVector;

/**
 * Created by Vikesh on 10/29/2017.
 */
@TeleOp(name = "Swerve")
public class Swerve extends LinearOpMode {

    cartesianVector mod0Vector = new cartesianVector();
    polarVector out1Vector= new polarVector();
    double direction;

    @Override
    public void runOpMode() throws InterruptedException {
        AnalogInput input1 = hardwareMap.get(AnalogInput.class, "input1");
        Servo swerveServo1 = hardwareMap.get(Servo.class, "swerveServo1");

        waitForStart();

        while(opModeIsActive()) {
            mod0Vector.set(gamepad1.left_stick_x, -1*gamepad1.left_stick_y);
            out1Vector = mod0Vector.cartToPolar();
            direction = out1Vector.direction / 360 + .25;
            
            if (direction < (input1.getVoltage() / 3.24) - 0.02) {
                swerveServo1.setPosition(.75);
                telemetry.addData("servo Position: ","1.0");
            } else if (direction < (input1.getVoltage() / 3.24) - 0.02) {
                swerveServo1.setPosition(.25);
                telemetry.addData("servo Position: ","0.0");
            } else {
                swerveServo1.setPosition(.5);
                telemetry.addData("servo Position: ",".5");
            }
            telemetry.addData("gamepad x: ", gamepad1.left_stick_x);
            telemetry.addData("gamepad y: ", gamepad1.left_stick_y);
            telemetry.addData("target direction: ", "%.3f", out1Vector.direction);
            telemetry.update();
        }

    }
}
