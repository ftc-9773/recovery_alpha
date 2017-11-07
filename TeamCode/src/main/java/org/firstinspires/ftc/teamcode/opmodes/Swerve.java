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

    SwerveModule module0 = new SwerveModule(hardwareMap, "swerveServo0", "swerveMotor0", "modOneDefPos");
    SwerveModule module1 = new SwerveModule(hardwareMap, "swerveServo1", "swerveMotor1", "modTwoDefPos");
    SwerveModule module2 = new SwerveModule(hardwareMap, "swerveServo2", "swerveMotor2", "modThreeDefPos");
    SwerveModule module3 = new SwerveModule(hardwareMap, "swerveServo3", "swerveMotor3", "modFourDefPos");

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        while(opModeIsActive()) {
            module0.setVector(gamepad1.left_stick_x, gamepad1.left_stick_y * (-1));
            module1.setVector(gamepad1.left_stick_x, gamepad1.left_stick_y * (-1));
            module2.setVector(gamepad1.left_stick_x, gamepad1.left_stick_y * (-1));
            module3.setVector(gamepad1.left_stick_x, gamepad1.left_stick_y * (-1));
        }
    }
}
