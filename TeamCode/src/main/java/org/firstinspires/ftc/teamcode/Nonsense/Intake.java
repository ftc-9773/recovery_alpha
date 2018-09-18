package org.firstinspires.ftc.teamcode.Nonsense;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake extends AbstractIntake{
    HardwareMap hwmp;
    Servo rightIntakeServo, leftIntakeServo;
    DcMotor intakeMotor, armMotor;

    public Intake(String ritkName, String lintkName, String amName, HardwareMap hwmp){
        this.hwmp = hwmp;
        this.rightIntakeServo = hwmp.servo.get(ritkName);
        this.leftIntakeServo = hwmp.servo.get(lintkName);
        this.armMotor = hwmp.dcMotor.get(amName);
    }

    public void intakeOn(double pow){
        intakeMotor.setPower(pow);
    }
    public void intakeOn(){
        intakeMotor.setPower(0.75);
    }
    public void setPower(double pow){
        intakeMotor.setPower(pow);
    }
    public void intakeOff(){
        intakeMotor.setPower(0);
    }
    public void transferState(){
        this.setState(intakeStates.TRANSFER);
    }
    public void storeState(){
        this.setState(intakeStates.STORE);
    }
    public void intakeState(){
        this.setState(intakeStates.INTAKE);
    }
    public void setArmMotor(float pow){this.armMotor.setPower(pow);}

    public void setState(intakeStates state){
        switch(state){
            case STORE:
                rightIntakeServo.setPosition(0.99);
                leftIntakeServo.setPosition(0.01);
            case INTAKE:
                rightIntakeServo.setPosition(0.65);
                leftIntakeServo.setPosition(0.35);
            case TRANSFER:
                leftIntakeServo.setPosition(0.5);
                rightIntakeServo.setPosition(0.5);
        }
    }
}