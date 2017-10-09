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

    private void setServo(double pos) {
        swerveServo0.setPosition(pos);
        swerveServo1.setPosition(pos);
        swerveServo2.setPosition(pos);
        swerveServo3.setPosition(pos);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        swerveServo0 = hardwareMap.get(Servo.class, "swerveServo0");
        swerveServo1 = hardwareMap.get(Servo.class, "swerveServo1");
        swerveServo2 = hardwareMap.get(Servo.class, "swerveServo2");
        swerveServo3 = hardwareMap.get(Servo.class, "swerveServo3");

        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();

        double pos = 0.5;
        double increment = 0.001;
        double neutral = 0.1;
        setServo(pos);

        waitForStart();
        while (opModeIsActive()) {
            double dir = gamepad1.left_stick_x;
            if (dir>0) {
                pos += increment;
                if (pos>1.0) { pos = 1.0; }
            } else if (dir < 0) {
                pos -= increment;
                if (pos<0) { pos = 0; }
            }
            setServo(pos);
            // Send telemetry message to signify robot running;
            telemetry.addData("hi Nicky, my pos is " ,  "%.2f", pos);
            telemetry.addData("hi Alex, my dir is " ,  "%.2f", dir);
            telemetry.update();

        }
    }
}
