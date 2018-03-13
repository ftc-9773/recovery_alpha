package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Vikesh on 11/25/2017.
 */

public class RelicSystem {
    private HardwareMap hwMap;
    private Servo armServo;
    private Servo grabServo;
    public DcMotor extensionMotor;
    private int index = 0;
    private Telemetry telemetry;
    private long lastTime;
    private LinearOpModeCamera linearOpMode;

    public RelicSystem(Telemetry telemetry, HardwareMap hwMap, LinearOpModeCamera linearOpModeCamera){
        this.linearOpMode = linearOpModeCamera;
        this.armServo = hwMap.servo.get("rlaServo");
        this.grabServo = hwMap.servo.get("rlcServo");
        this.extensionMotor = hwMap.dcMotor.get("rlMotor");
        this.hwMap = hwMap;
        this.telemetry = telemetry;
        extensionMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extensionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
        telemetry.update();
    }
    public void runToPosition(int position){
        Log.i("current arm position",Integer.toString(extensionMotor.getCurrentPosition()));
        if(position < extensionMotor.getCurrentPosition()) {
            while (extensionMotor.getCurrentPosition() > position && linearOpMode.opModeIsActive()) {
                extensionMotor.setPower(-0.5);
            }
        }
        else if(position > extensionMotor.getCurrentPosition()) {
            while (extensionMotor.getCurrentPosition() < position && linearOpMode.opModeIsActive()) {
                extensionMotor.setPower(1.0);
            }
        }
        extensionMotor.setPower(0);
    }
}
