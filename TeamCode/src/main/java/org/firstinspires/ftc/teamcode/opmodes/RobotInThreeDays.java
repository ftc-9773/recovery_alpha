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

        // leftDriveMotors
        leftDriveMotorA = hardwareMap.dcMotor.get("lMotorA"); // leftDriveMotor A
        leftDriveMotorB = hardwareMap.dcMotor.get("lMotorB"); // leftDriveMotor B
        // rightDriveMotors
        rightDriveMotorA = hardwareMap.dcMotor.get("rMotorA"); // rightDriveMotor A
        rightDriveMotorB = hardwareMap.dcMotor.get("rMotorB"); // rightDriveMotor B
        //liftMotors
        liftMotorA = hardwareMap.dcMotor.get("liftMotorA"); // liftMotor A
        liftMotorB = hardwareMap.dcMotor.get("liftMotorB"); // liftMotor B

        // armMotors
        armMotor = hardwareMap.dcMotor.get("armMotor"); // armMotor
        intakeMotor = hardwareMap.dcMotor.get("intakeMotor"); // intakeMotor

        // servos
        litkServo = hardwareMap.servo.get("litkServo"); // leftIntakeServo
        ritkServo = hardwareMap.servo.get("ritkServo" ); // rightIntakeServo

        lDump = hardwareMap.servo.get("lDump"); // leftDump (Cubes)
        rDump = hardwareMap.servo.get("rDump"); // rightDump (Balls)

        sorterServo = hardwareMap.servo.get("sortServo");

        // hold lift Pos
        setLiftPower(-0.15);

        waitForStart();
        sorterServo.setPosition(1);
        while(opModeIsActive()){

            // driving
            setLeftPow(gamepad1.left_stick_y);
            setRightPow(gamepad1.right_stick_y);
            //
            setLiftPower(-gamepad2.left_stick_y);
            armMotor.setPower(-gamepad2.right_stick_y);



            //Open Gates
            if(gamepad2.left_bumper){
                rDump.setPosition(0.5);
                lDump.setPosition(0.5);
            } else {
                rDump.setPosition(0.0);
                lDump.setPosition(1.0);
            }
            if(gamepad2.left_trigger > 0.2){
                sorterServo.setPosition(.85); //One direction
            }
            else {
                sorterServo.setPosition(.5); //Stationary
            }

            //Set intake state
            if(gamepad2.y) intakeStore();
            else if(gamepad2.a) intakeDown();
            else if(gamepad2.x) intakeTransfer();

            //Set the correct value for the intake
            if(gamepad2.right_bumper){
                intakeOn(0.75);
            } else if(gamepad2.right_trigger > 0.2){
                intakeOn(-gamepad2.right_trigger);
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
    void intakeStore(){
        ritkServo.setPosition(0.99);
        litkServo.setPosition(0.01);

    }
    void intakeDown(){
        ritkServo.setPosition(0.65);
        litkServo.setPosition(0.35);
    }
    void intakeTransfer(){
        ritkServo.setPosition(0.5);
        litkServo.setPosition(0.5);
    }
    void intakeOn(double pow){
        intakeMotor.setPower(pow);
    }



}
