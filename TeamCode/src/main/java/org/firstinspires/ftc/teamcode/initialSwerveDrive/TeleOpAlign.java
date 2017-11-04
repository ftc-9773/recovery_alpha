package org.firstinspires.ftc.teamcode.initialSwerveDrive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.infrastructure.ButtonStatus;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

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
    private ButtonStatus buttonX = new ButtonStatus();
    private ButtonStatus buttonY = new ButtonStatus();
    private SafeJsonReader swerveInfo;
    private double incrementServo0;
    private double incrementServo1;
    private double incrementServo2;
    private double incrementServo3;

    private void setServo0(double pos) {
        pos += incrementServo0;
        if (pos > 1.0) {
            pos = 1.0;
        } else if (pos < 0) {
            pos = 0;
        }
        swerveServo0.setPosition(pos);
    }

    private void setServo1(double pos) {
        pos += incrementServo1;
        if (pos > 1.0) {
            pos = 1.0;
        } else if (pos < 0) {
            pos = 0;
        }
        swerveServo1.setPosition(pos);
    }

    private void setServo2(double pos) {
        pos += incrementServo2;
        if (pos > 1.0) {
            pos = 1.0;
        } else if (pos < 0) {
            pos = 0;
        }
        swerveServo2.setPosition(pos);
    }

    private void setServo3(double pos) {
        pos += incrementServo3;
        if (pos > 1.0) {
            pos = 1.0;
        } else if (pos < 0) {
            pos = 0;
        }
        swerveServo3.setPosition(pos);
    }

    private void readServoInfo() {
        swerveInfo = new SafeJsonReader("swerveInfo");
        incrementServo0 = swerveInfo.getDouble("increment0");
        incrementServo1 = swerveInfo.getDouble("increment1");
        incrementServo2 = swerveInfo.getDouble("increment2");
        incrementServo3 = swerveInfo.getDouble("increment3");
    }


    private void setAllServo(double pos) {
        setServo0(pos);
        setServo1(pos);
        setServo2(pos);
        setServo3(pos);
    }

    private double angleIncrement(double degrees) {
        final double  factor = 1.0 / (7.5 * 360.0);
        return degrees * factor;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        swerveServo0 = hardwareMap.get(Servo.class, "swerveServo0");
        swerveServo1 = hardwareMap.get(Servo.class, "swerveServo1");
        swerveServo2 = hardwareMap.get(Servo.class, "swerveServo2");
        swerveServo3 = hardwareMap.get(Servo.class, "swerveServo3");
        readServoInfo();

        telemetry.addData("Say", "Hello Driver, incr0 is %f", incrementServo0);
        telemetry.update();

        double pos = 0.5;
        double increment = 0.001;
        double neutral = 0.1;
        setAllServo(pos);

        waitForStart();
        while (opModeIsActive()) {
            double dir = gamepad1.left_stick_x;
            boolean x = gamepad1.x;
            boolean y = gamepad1.y;
            buttonX.recordNewValue(x);
            buttonY.recordNewValue(y);
            if (buttonX.isJustOn()) {
                pos = 0.5;
            } else if (buttonY.isJustOn()) {
                pos += angleIncrement(2*360);
            } else {
                if (dir > neutral) {
                    pos += increment;
                } else if (dir < -neutral) {
                    pos -= increment;
                }
            }
            setAllServo(pos);
            // Send telemetry message to signify robot running;
            telemetry.addData("hi Nicky, my pos is " ,  "%.2f", pos);
            telemetry.addData("hi Alex, my dir is " ,  "%.2f", dir);

            telemetry.update();

        }
    }
}
