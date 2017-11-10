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

    double xComponent;
    double yComponent;
    double conversionDir;
    double conversionMag;
    AnalogInput input;
    DcMotor swerveMotor0;
    Servo swerveServo0;

    @Override
    public void runOpMode() throws InterruptedException {

        input = hardwareMap.get(AnalogInput.class, "input0");
        swerveMotor0 = hardwareMap.get(DcMotor.class, "swerveMotor0");
        swerveServo0 = hardwareMap.get(Servo.class, "swerveServo0");

        waitForStart();

        while(opModeIsActive()) {

            xComponent = gamepad1.left_stick_x;
            yComponent = gamepad1.left_stick_y;

            if (xComponent != 0) {
                conversionDir = (Math.atan(yComponent / xComponent) * (180 / 3.141593)+90);
            } else if(yComponent < 0){
                conversionDir = 0;
            }
            else{
                conversionDir = 180;
            }

            if (xComponent < 0 && yComponent < 0) {
                conversionMag = -1 * (xComponent * xComponent + yComponent * yComponent);
            } else {
                conversionMag = (xComponent * xComponent + yComponent * yComponent);
            }

            conversionDir = (conversionDir)/360+.25;

            if(conversionDir < input.getVoltage()/3.24-0.005){
                swerveServo0.setPosition(0);
            }
            else if(conversionDir > input.getVoltage()/3.24+0.005){
                swerveServo0.setPosition(1);
            }
            else{
                swerveServo0.setPosition(.5);
            }

            telemetry.addData("position:", "%.3f", conversionDir);
            telemetry.addData("speed: ", conversionMag);
            telemetry.update();
        }

    }
}
