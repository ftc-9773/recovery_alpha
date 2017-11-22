package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

/**
 * Created by nicky on 11/18/17.
 */

public class IntakeController {
    public DcMotor leftMotor;
    public DcMotor rightMotor;

    private static double LEFT_MOTOR_POWER = 1;
    private static double RIGHT_MOTOR_POWER = 1;
    private static double MIN_SPEED; // rotations per second
    private static double REVERSE_DELAY; // in milliseconds - time before the intake will be allowed to reverse again
    private static long REVERSE_DIST; // Encoder ticks
    private static double REVERSE_SPEED;

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

    private SafeJsonReader jsonParameters;

    static String TAG = "9773_IntakeController";
    static boolean DEBUG = true;

    public IntakeController(HardwareMap hardwareMap) {
        leftMotor = hardwareMap.dcMotor.get("liMotor");
        rightMotor = hardwareMap.dcMotor.get("riMotor");

        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Get JSON Values
        jsonParameters = new SafeJsonReader("IntakeControllerParameters");
        MIN_SPEED = jsonParameters.getDouble("MIN_SPEED");
        REVERSE_DELAY = jsonParameters.getDouble("REVERSE_DELAY");
        REVERSE_DIST =  jsonParameters.getInt("REVERSE_DIST");
        REVERSE_SPEED = jsonParameters.getDouble("REVERSE_SPEED");
    }

    public void runIntakeIn() {

        // Get current values
        leftCurrPosition = leftMotor.getCurrentPosition();
        rightCurrPosition = rightMotor.getCurrentPosition();
        currTime = System.currentTimeMillis();

        if (DEBUG) { Log.e(TAG, "left position: " + leftCurrPosition + "   right position: " + rightCurrPosition + "   current time : " + currTime); }

        if (DEBUG) { Log.e(TAG, "Trigger Pressed"); }

        // If the intake is in the forwards state
        if (DEBUG) { if (isForward) {  Log.e(TAG, "Is forward"); } else { Log.e(TAG, "Is backwards" ); } }
        if (isForward) {
            leftMotor.setPower(LEFT_MOTOR_POWER);
            rightMotor.setPower(RIGHT_MOTOR_POWER);
            // Make sure the it has been long enough since starting before checking to go backwards
            if (DEBUG) { Log.e(TAG, "Time since last off: " + (currTime - lastTimeOff)); }
            if (currTime - lastTimeOff > REVERSE_DELAY) {

                // Calculate speed of each wheel
                leftSpeed = (leftCurrPosition - leftPrevPosition) / (currTime - prevTime);
                rightSpeed = (rightCurrPosition - rightPrevPosition) / (currTime - prevTime);

                if (DEBUG) { Log.e(TAG, "Left speed: " + leftSpeed + "   Right speed: " + rightSpeed); }

                // Check if either speed is too low
                if (leftSpeed < MIN_SPEED || rightSpeed < MIN_SPEED) {
                    isForward = false;

                    //Reverses the motors
                    rightMotor.setPower(-0.25);
                    leftMotor.setPower(-0.25);

                    //sets target positions
                    leftRevPosition = leftCurrPosition - REVERSE_DIST;
                    rightRevPosition = rightCurrPosition - REVERSE_DIST;
                    if (DEBUG) { Log.e(TAG, "Left target: " + leftRevPosition + "  Right target: " + rightRevPosition); }
                }
            }

            // If intake is in reverse state
        } else {
            rightMotor.setPower(REVERSE_SPEED);
            leftMotor.setPower(REVERSE_SPEED);
            if (leftCurrPosition <= leftRevPosition || rightCurrPosition <= rightRevPosition) {
                isForward = true;
                leftMotor.setPower(LEFT_MOTOR_POWER);
                rightMotor.setPower(RIGHT_MOTOR_POWER);

                lastTimeOff = currTime;
            }
        }
        // Anything at the end of the function
        leftPrevPosition = leftCurrPosition;
        rightPrevPosition = rightCurrPosition;
        prevTime = currTime;
    }

    public void runIntakeOut() {
        isForward = true;
        lastTimeOff = System.currentTimeMillis();

        leftMotor.setPower(-1);
        rightMotor.setPower(-1);

        leftPrevPosition = leftMotor.getCurrentPosition();
        rightPrevPosition = rightMotor.getCurrentPosition();
        prevTime = System.currentTimeMillis();
    }

    public void intakeOff() {
        isForward = true;
        lastTimeOff = System.currentTimeMillis();

        leftMotor.setPower(0);
        rightMotor.setPower(0);

        leftPrevPosition = leftMotor.getCurrentPosition();
        rightPrevPosition = rightMotor.getCurrentPosition();
        prevTime = System.currentTimeMillis();
    }
}
