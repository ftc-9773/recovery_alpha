package org.firstinspires.ftc.teamcode.InitialTests;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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


@TeleOp(name="IntakeTest", group="SwerveTest with angle")
//@Disabled
public class IntakeTest extends LinearOpMode {


    public DcMotor leftMotor   = null;
    public DcMotor rightMotor   = null;
    private double motorLeftPower = 0;
    private double motorRightPower = 0;
    private double forward = 0;
    private double right = 0;
    private double prevEncLeft = 0;
    private double prevEncRight = 0;
    private double currEncLeft = 0;
    private double currEncRight = 0;

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

        // Set all motors to run without encoders. Also, we are going to reset the encoder values here.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

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

            //The following is being used to read the current MotorPosition
            int leftPosition = leftMotor.getCurrentPosition() / 140;
            int rightPosition = rightMotor.getCurrentPosition() / 140;
            telemetry.addData("Left Motor Encoder Position", leftPosition);
            telemetry.addData("Right Motor Encoder Position: ", rightPosition);
            currEncLeft = leftPosition;
            currEncRight = rightPosition;
            double speedLeft = 0;
            double speedRight = 0;
            if (prevEncLeft != currEncLeft) {
                speedLeft = currEncLeft - prevEncLeft;
                prevEncLeft = currEncLeft;
                //telemetry.addData ("The current speed is: ", speedLeft);
                //telemetry.update();
            } else if (prevEncLeft == currEncLeft){
                telemetry.addData ("The motor is jammed at: ", currEncLeft);
                telemetry.update();
                telemetry.addData ("The current speed is: ", speedLeft);
                telemetry.update();
            } else {
                telemetry.addData ("Impossible, this can't happen", leftPosition);
                telemetry.addData ("The current speed is: ", speedLeft);
                telemetry.update();
            }
            if (prevEncRight != currEncRight) {
                speedRight = currEncRight - prevEncRight;
                prevEncRight = currEncRight;
                //telemetry.addData ("The current speed is: ", speedRight);
                //telemetry.update();
            } else if (prevEncRight == currEncRight) {
                telemetry.addData ("The right motor is jammed at: ", currEncRight);
                telemetry.update();
                telemetry.addData ("The current speed is: ", speedRight);
                telemetry.update();
            } else {
                telemetry.addData ("Impossible, this can't happen", rightPosition);
                telemetry.addData ("The current speed is: ", speedRight);
                telemetry.update();
            }

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

