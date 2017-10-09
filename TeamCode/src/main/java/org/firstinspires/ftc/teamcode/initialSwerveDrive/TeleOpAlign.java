package org.firstinspires.ftc.teamcode.initialSwerveDrive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import java.lang.*;

/**
 * Created by Vikesh on 10/8/2017.
 */
@TeleOp(name="TeleOpAlign")
public class TeleOpAlign extends LinearOpMode{

    private Servo swerveServo0;
    private Servo swerveServo1;
    private Servo swerveServo2;
    private Servo swerveServo3;

    @Override
    public void runOpMode() throws InterruptedException {
        swerveServo0 = hardwareMap.get(Servo.class, "swerveServo0");
        swerveServo1 = hardwareMap.get(Servo.class, "swerveServo1");
        swerveServo2 = hardwareMap.get(Servo.class, "swerveServo2");
        swerveServo3 = hardwareMap.get(Servo.class, "swerveServo3");

        waitForStart();
        while (opModeIsActive()) {
            swerveServo0.setPosition(.5);
            swerveServo1.setPosition(.5);
            swerveServo2.setPosition(.5);
            swerveServo3.setPosition(.5);
        }
    }
}
