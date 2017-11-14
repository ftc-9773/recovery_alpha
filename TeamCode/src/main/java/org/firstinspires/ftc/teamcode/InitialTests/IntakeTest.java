package org.firstinspires.ftc.teamcode.InitialTests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.infrastructure.InstrumentDoubleArray;
import org.firstinspires.ftc.teamcode.infrastructure.Instrumentation;

import java.util.Locale;

/**
 * Created by eichen on 11/4/17.
 */


@TeleOp(name="IntakeTest", group="SwerveTest with angle")
//@Disabled
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
        leftMotor   = hardwareMap.dcMotor.get("liMotor");
        rightMotor  = hardwareMap.dcMotor.get("riMotor");
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

        //FileRW myfile = new FileRW("/sdcard/FIRST/team9773/log18/hialex.1", true);
        //myfile.fileWrite("hi alex");
        //myfile.close();

        InstrumentDoubleArray motorStats = new InstrumentDoubleArray("motorSpeed", 2, "motor1, motor2", 1e-3);
        double[] motorData = new double[2];

        // Wait for the game to start (driver presses PLAY)
        waitForStart();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            Instrumentation.nextLoopIteration();
            double stickX =  - Math.pow(gamepad1.right_stick_x,3) * .7;
            double stickY =  - gamepad1.right_stick_y;

            forward = stickY;
            right = stickX;
            motorLeftPower = forward + right;
            motorRightPower = forward - right;
            double max = Math.max(Math.abs(motorLeftPower), Math.abs(motorRightPower));
            if (max>1) {
                motorLeftPower = motorLeftPower / max;
                motorRightPower = motorRightPower / max;
            }
            leftMotor.setPower(motorLeftPower);
            rightMotor.setPower(motorRightPower);

            motorData[0] = forward;
            motorData[1] = right;
            motorStats.push(motorData);
            telemetry.update();
        }
        motorStats.close();
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

