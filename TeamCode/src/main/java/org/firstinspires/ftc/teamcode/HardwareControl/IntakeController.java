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

    private static double motorLeftPower = 1;
    private static double motorRightPower = 1;
    private static double minSpeed = .005;
    private static double REVERSE_DELAY = 100; // in milliseconds

    private long prevTime = -1;
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

        //Update current values
        leftCurrPosition = leftMotor.getCurrentPosition();
        rightCurrPosition = rightMotor.getCurrentPosition();
        currTime = System.currentTimeMillis();

        if (DEBUG) { Log.e(TAG, "left position: " + leftCurrPosition + "   right position: " + rightCurrPosition + "   current time : " + currTime); }

        if (gamepad1.right_trigger > 0) {

            if (DEBUG) { Log.e(TAG, "Trigger Pressed"); }

                    leftMotor.setPower(motorLeftPower);
            rightMotor.setPower(motorRightPower);

            if (isForward) {
                if (prevTime > -1 && currTime - lastTimeOff > REVERSE_DELAY) {

                    if (currTime > 0 && prevTime > 0) {
                        leftSpeed = (leftCurrPosition - leftPrevPosition) / (currTime - prevTime);
                        rightSpeed = (rightCurrPosition - rightPrevPosition) / (currTime - prevTime);

                        if (DEBUG) { Log.e(TAG, "Left speed: " + leftSpeed + "   Right speed: " + rightSpeed);

                            if (leftSpeed < minSpeed || rightSpeed < minSpeed) {
                                isForward = false;
                                rightMotor.setPower(-0.25);
                                leftMotor.setPower(-0.25);
                                leftRevPosition = leftCurrPosition - 35;
                                rightRevPosition = rightCurrPosition + 35;
                                if (DEBUG) { Log.e(TAG, "Left target: " + leftRevPosition + "  Right target: " + rightRevPosition); }
                                }
                        }

                    }
                }
            } else {
                    if (leftCurrPosition <= leftRevPosition || rightCurrPosition >= rightRevPosition) {
                    isForward = true;
                    leftMotor.setPower(motorLeftPower);
                    rightMotor.setPower(motorRightPower);
                }
            }
        } else {
            isForward = true;
            lastTimeOff = currTime;

            leftMotor.setPower(0);
            rightMotor.setPower(0);
        }
    }
}
