package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorREVColorDistance;

/**
 * Created by Robocracy on 12/10/17.
 */

public class IntakeControllerManual {
    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private SensorREVColorDistance front;

    private static double LEFT_MOTOR_POWER = 1;
    private static double RIGHT_MOTOR_POWER = 1;

    public IntakeControllerManual(HardwareMap hardwareMap) {
        leftMotor = hardwareMap.dcMotor.get("liMotor");
        rightMotor = hardwareMap.dcMotor.get("riMotor");


        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    public void RunIntake(double stickX, double stickY) {
        // transfo of stick
        stickX =  - Math.pow(stickX,3) * .7;
        stickY =  - stickY;
        double forward = stickY;
        if (forward == 0) {
            leftMotor.setPower(0);
            rightMotor.setPower(0);
            return; }
        double right = stickX;
        double motorLeftPower = (forward + right);
        double motorRightPower = (forward - right);
        double max = Math.max(Math.abs(motorLeftPower), Math.abs(motorRightPower));
        if (max>1) {
            motorLeftPower = motorLeftPower / max;
            motorRightPower = motorRightPower / max;
        }
        leftMotor.setPower(motorLeftPower);
        rightMotor.setPower(motorRightPower);
    }

    public void lowerIntake(boolean stopOrStart){
        if (stopOrStart) {

        }
        else{
        }
    }

}
