package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by nicky on 11/18/17.
 */

public class IntakeController {
    public DcMotor leftMotor;
    public DcMotor rightMotor;

    private static double LEFT_MOTOR_POWER = 1;
    private static double RIGHT_MOTOR_POWER = 1;
    private static double MIN_SPEED = .005;
    private static double REVERSE_DELAY = 100; // in milliseconds - time before the intake will be allowed to reverse again

    private long prevTime = 0;
    private long currTime = 0;
    private long leftPrevPosition = 0;
    private long leftCurrPosition = 0;
    private long rightPrevPosition = 0;
    private long rightCurrPosition = 0;
    private double leftSpeed;
    private double rightSpeed;
    private long lastTimeOff = 0;

    private long leftRevPosition;
    private long rightRevPosition;

    private boolean isForward = true;
    private Gamepad gamepad1;

    static String TAG = "9773_IntakeController";
    static boolean DEBUG = true;

    public IntakeController(HardwareMap hardwareMap, Gamepad gamepad1) {
        leftMotor = hardwareMap.dcMotor.get("liMotor");
        rightMotor = hardwareMap.dcMotor.get("riMotor");

        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.gamepad1 = gamepad1;
    }

    public void runIntake() {

        // Get current values
        leftCurrPosition = leftMotor.getCurrentPosition();
        rightCurrPosition = rightMotor.getCurrentPosition();
        currTime = System.currentTimeMillis();

        if (DEBUG) { Log.e(TAG, "left position: " + leftCurrPosition + "   right position: " + rightCurrPosition + "   current time : " + currTime); }

        // If the trigger is pressed, run the intake
        if (gamepad1.right_trigger > 0) {

            if (DEBUG) { Log.e(TAG, "Trigger Pressed"); }

            leftMotor.setPower(LEFT_MOTOR_POWER);
            rightMotor.setPower(RIGHT_MOTOR_POWER);

            // If the intake is in the forwards state
            if (isForward) {

                // Make sure the it has been long enough since starting before checking to go backwards
                if (currTime - lastTimeOff > REVERSE_DELAY) {

                    // Calculate speed of each wheel
                    leftSpeed = (leftCurrPosition - leftPrevPosition) / (currTime - prevTime);
                    rightSpeed = Math.abs((rightCurrPosition - rightPrevPosition) / (currTime - prevTime));

                    if (DEBUG) { Log.e(TAG, "Left speed: " + leftSpeed + "   Right speed: " + rightSpeed); }

                        // Check if either speed is too low
                        if (leftSpeed < MIN_SPEED || rightSpeed < MIN_SPEED) {
                            isForward = false;

                            //Reverses the motors
                            rightMotor.setPower(-0.25);
                            leftMotor.setPower(-0.25);

                            //sets target positions
                            leftRevPosition = leftCurrPosition - 35;
                            rightRevPosition = rightCurrPosition + 35;
                            if (DEBUG) { Log.e(TAG, "Left target: " + leftRevPosition + "  Right target: " + rightRevPosition); }
                        }
                }

            // If intake is in reverse state
            } else {
                if (leftCurrPosition <= leftRevPosition || rightCurrPosition >= rightRevPosition) {
                    isForward = true;
                    leftMotor.setPower(LEFT_MOTOR_POWER);
                    rightMotor.setPower(RIGHT_MOTOR_POWER);

                    lastTimeOff = currTime;
                }
            }

        // If the trigger is not pressed, set motor power to 0 and reset motor direction
        } else {
            isForward = true;
            lastTimeOff = currTime;

            leftMotor.setPower(0);
            rightMotor.setPower(0);
        }

        // ANything at the end of the function
        leftPrevPosition = leftCurrPosition;
        rightPrevPosition = rightCurrPosition;
        prevTime = currTime;
    }
}
