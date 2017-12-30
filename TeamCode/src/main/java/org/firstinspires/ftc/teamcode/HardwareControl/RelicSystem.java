package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Vikesh on 11/25/2017.
 */

public class RelicSystem {
    private HardwareMap hwMap;
    private Servo armServo;
    private Servo grabServo;
    private DcMotor extensionMotor;
    private int index = 0;
    private Telemetry telemetry;
    private long lastTime;

    public RelicSystem(Telemetry telemetry, HardwareMap hwMap){
        this.armServo = hwMap.servo.get("rlaServo");
        this.grabServo = hwMap.servo.get("rlcServo");
        this.extensionMotor = hwMap.dcMotor.get("rlMotor");
        this.hwMap = hwMap;
        this.telemetry = telemetry;
        extensionMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extensionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    public void runSequence(double power, boolean armState, boolean grabState){
        extensionMotor.setPower(power);
        telemetry.addData("Current Position", extensionMotor.getCurrentPosition());
        if(!armState) {
            armServo.setPosition(1);
        }else {
            armServo.setPosition(0.05);
            grabServo.setPosition(.69);
        }
        if (!grabState) {
            grabServo.setPosition(.35);
        }else{
            grabServo.setPosition(.69);
        }
    }
    public void runToPosition(int position){
        extensionMotor.setTargetPosition(position);
    }
}
