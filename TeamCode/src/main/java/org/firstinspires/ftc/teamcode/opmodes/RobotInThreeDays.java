package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.infrastructure.RasiParser;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
@TeleOp(name = "robotinThreeDays", group = "AAA robotInThreeDays" )

public class RobotInThreeDays extends LinearOpMode {
    DcMotor  leftDriveMotorA, leftDriveMotorB;
    DcMotor rightDriveMotorA, rightDriveMotorB;

    DcMotor liftMotorA;
    DcMotor liftMotorB;

    DcMotor intakeMotor;
    DcMotor armMotor;

    Servo litkServo, ritkServo;

    Servo lDump,rDump;

    Servo sorterServo;


    SafeJsonReader config  ;// do later

    public void runOpMode(){
        // init
        leftDriveMotorA = hardwareMap.dcMotor.get("lMotorA");
        leftDriveMotorB = hardwareMap.dcMotor.get("lMotorB");

        rightDriveMotorA = hardwareMap.dcMotor.get("rMotorA");
        rightDriveMotorB = hardwareMap.dcMotor.get("rMotorB");
        //liftMotors
        liftMotorA = hardwareMap.dcMotor.get("liftMotorA");
        liftMotorB = hardwareMap.dcMotor.get("liftMotorB");

        // armMotors
        armMotor = hardwareMap.dcMotor.get("armMotor");
        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");

        // servos
        litkServo = hardwareMap.servo.get("litkServo");
        ritkServo = hardwareMap.servo.get("ritkServo" );

        lDump = hardwareMap.servo.get("lDump");
        rDump = hardwareMap.servo.get("rDump");

        sorterServo = hardwareMap.servo.get("sortServo");

        // hold lift Pos
        setLiftPower(-0.15);

        waitForStart();
        sorterServo.setPosition(0.8);
        while(opModeIsActive()){

            // driving
            setLeftPow(gamepad1.left_stick_y);
            setRightPow(gamepad1.right_stick_y);
            //
            setLiftPower(-gamepad2.left_stick_y);
            armMotor.setPower(-gamepad2.right_stick_y);

            if(gamepad2.x) intakeStore();
            else if(gamepad2.b) intakeDown();
            else if(gamepad2.a) intakeTransfer();

            if(gamepad2.left_bumper){
                rDump.setPosition(0.8);
                lDump.setPosition(0.6);
            } else {
                rDump.setPosition(0.93);
                lDump.setPosition(0.43);
            }
            if(gamepad2.right_bumper){
                intakeMotor.setPower(0.75);
            } else if(gamepad2.right_trigger > 0.2){
                intakeMotor.setPower(-gamepad2.right_trigger);
            } else {
                intakeMotor.setPower(0.0);
            }
        }



    }


    private void setLiftPower(double pow){
        liftMotorA.setPower(-pow);
        liftMotorB.setPower(pow);
    }

    void setRightPow (double pow){
        rightDriveMotorA.setPower(pow);
        rightDriveMotorB.setPower(pow);

    }
    void setLeftPow (double pow){
        leftDriveMotorA.setPower(-pow);
        leftDriveMotorB.setPower(-pow);
    }
    void intakeDown(){
        ritkServo.setPosition(0.99);
        litkServo.setPosition(0.01);

    }
    void intakeStore(){
        ritkServo.setPosition(0.65);
        litkServo.setPosition(0.35);
    }
    void intakeTransfer(){
        ritkServo.setPosition(0.45);
        litkServo.setPosition(0.55);
    }



}
