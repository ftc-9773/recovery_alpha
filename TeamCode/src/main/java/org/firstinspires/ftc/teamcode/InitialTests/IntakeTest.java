package org.firstinspires.ftc.teamcode.InitialTests;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.Locale;

/**
 * Created by eichen on 11/4/17.
 */


public class IntakeTest extends LinearOpMode {


    public DcMotor leftMotor   = null;
    public DcMotor rightMotor   = null;
    private double motorLeftPower = 0;
    private double motorRightPower = 0;
    private double forward = 0;
    private double right = 0;

    @Override
    public void runOpMode() {

        /* Initialize the hardware variables. */
        leftMotor   = hardwareMap.dcMotor.get("LeftIntakeMotor");
        rightMotor  = hardwareMap.dcMotor.get("RightIntakeMotor");
        leftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors

        // Set all motors to zero power
        leftMotor.setPower(0);
        rightMotor.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Set up our telemetry dashboard
        composeTelemetry();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            double stickX =  - Math.pow(gamepad1.right_stick_x,3) * .7;
            double stickY =  - gamepad1.right_stick_y;

            forward = stickY;
            right = stickX;
            motorLeftPower = forward + right;
            motorRightPower = forward - right;
            double max = Math.max(Math.abs(motorLeftPower), Math.abs(motorRightPower));
            motorLeftPower = motorLeftPower / max;
            motorRightPower = motorRightPower / max;

            leftMotor.setPower(motorLeftPower);
            rightMotor.setPower(motorRightPower);

            telemetry.update();

        }
    }

    //----------------------------------------------------------------------------------------------
    // location
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    // Telemetry Configuration
    //----------------------------------------------------------------------------------------------

    void composeTelemetry() {


        telemetry.addLine()
                .addData("motor left", new Func<String>() {
                    @Override public String value() {
                        return String.format(Locale.getDefault(), "%.3f", motorLeftPower);
                    }
                })
                .addData("motor right", new Func<String>() {
                    @Override public String value() {
                        return String.format(Locale.getDefault(), "%.3f", motorRightPower);
                    }
                });
        telemetry.addLine()
                .addData("forward", new Func<String>() {
                    @Override public String value() {
                        return String.format(Locale.getDefault(), "%.3f", forward);
                    }
                })
                .addData("right", new Func<String>() {
                    @Override public String value() {
                        return String.format(Locale.getDefault(), "%.3f", right);
                    }
                });

    }

    //----------------------------------------------------------------------------------------------
    // Formatting
    //----------------------------------------------------------------------------------------------

    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    String formatDegrees(double degrees){
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }
}

