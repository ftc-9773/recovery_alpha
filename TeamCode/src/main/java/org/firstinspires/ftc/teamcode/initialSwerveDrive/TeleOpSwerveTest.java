package org.firstinspires.ftc.teamcode.initialSwerveDrive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by arjun on 10/7/2017.
 */

public class TeleOpSwerveTest extends LinearOpMode {
    private DcMotor swerveMotor0;
    private Servo swerveServo0;
    double gpx;
    double gpy;


    @Override
    public void runOpMode() throws InterruptedException {
        swerveServo0 = hardwareMap.get(Servo.class, "swerveServo0");
        swerveMotor0 = hardwareMap.get(DcMotor.class, "swerveMotor0");

        waitForStart();

        while(opModeIsActive()){
            gpx = gamepad1.left_stick_x;
            gpy = gamepad1.left_stick_y;

        }
    }
}
