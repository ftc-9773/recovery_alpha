package org.firstinspires.ftc.teamcode.Nonsense;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
/**
 * Implements AbstractIntake for the Robot in Three days robot
 *
 * @author Cadence
 * @version 1.0
 * */
public class Intake extends AbstractIntake{
    HardwareMap hwmp;
    Servo rightIntakeServo, leftIntakeServo;
    DcMotor intakeMotor, armMotor;

    /**
     * @param ritkName name of the right servo that lifts the intake
     * @param lintkName name of the left servo that lifts the intake
     * @param amName name of the motor that controls the extension arm for the intake
     * @param intakeName name of the motor that controls the spinny bit at the end of the intake
     * @param hwmp the hardware map defined in the opmode
     * */
    public Intake(String ritkName, String lintkName, String amName,String intakeName, HardwareMap hwmp){
        this.hwmp = hwmp;
        this.rightIntakeServo = hwmp.servo.get(ritkName);
        this.leftIntakeServo = hwmp.servo.get(lintkName);
        this.armMotor = hwmp.dcMotor.get(amName);
        this.intakeMotor = hwmp.dcMotor.get(intakeName);
        //this.intakeMotor.setPower(0);
    }

    /**
     * Sets the intake motor to a power between 1 and -1
     * @param pow the power to be set
     * */
    public void intakeOn(double pow){
        intakeMotor.setPower(pow);
    }
    /**
     * Turns intake on to the normal operating power (0.75)
     * */
    public void intakeOn(){
        intakeMotor.setPower(0.75);
    }

    public void setPower(double pow){
        intakeMotor.setPower(pow);
    }
    /**
     * Turns the intake off
     * */
    public void intakeOff(){
        intakeMotor.setPower(0.0);
    }
    /**
     * Move the intake to the position to transfer minerals
     * */
    public void transferState(){
        this.setState(intakeStates.TRANSFER);
    }
    /**
     * Move the intake to a position where it starts play.
     * */
    public void storeState(){
        this.setState(intakeStates.STORE);
    }
    /**
     * Move the intake to a position to intake minerals
     * */
    public void intakeState(){
        this.setState(intakeStates.INTAKE);
    }
    /**
     * Set the power of the arm motor.
     * /Question Perhaps a servo would be better?
     * */
    public void setArmMotor(float pow){
        this.armMotor.setPower(pow);
    }
    /**
     * Set a specific state
     * */
    private void setState(intakeStates state){
        switch(state){
            case STORE:
                this.rightIntakeServo.setPosition(0.93);
                this.leftIntakeServo.setPosition(0.07);
                break;
            case TRANSFER:
                this.leftIntakeServo.setPosition(0.5);
                this.rightIntakeServo.setPosition(0.5);
                break;
            case INTAKE:
                this.rightIntakeServo.setPosition(0.65);
                this.leftIntakeServo.setPosition(0.35);
                break;

        }
    }
}
