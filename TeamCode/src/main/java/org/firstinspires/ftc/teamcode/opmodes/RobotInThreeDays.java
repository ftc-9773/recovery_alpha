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

    boolean notTank ;



    SafeJsonReader config  ;// do later

    public void runOpMode(){
        boolean val = false;
        // init
        leftDriveMotorA = hardwareMap.dcMotor.get("lMotorA");
        leftDriveMotorB = hardwareMap.dcMotor.get("lMotorB");

        rightDriveMotorA = hardwareMap.dcMotor.get("rMotorA");
        rightDriveMotorB = hardwareMap.dcMotor.get("rMotorB");
        //liftMotors
        liftMotorA = hardwareMap.dcMotor.get("liftMotorA");
        liftMotorA.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotorB = hardwareMap.dcMotor.get("liftMotorB");
        liftMotorB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


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
        while(opModeIsActive()){
            // driving
            if(true){
                double forwardPower = gamepad1.left_stick_y * -1; //Set forward power to the Y of the right stick. -1 makes up forward.
                double turningPower = gamepad1.left_stick_x; //Set the turning power to the X of the right stick.

                double rightPower = forwardPower - turningPower; //Determine the power for the right.
                double leftPower = forwardPower + turningPower; //Determine the power for the left.

                double maxVal = Math.max(rightPower, leftPower);
                if (Math.abs(maxVal) > 1) {
                    rightPower /= maxVal;
                    leftPower /= maxVal;
                }

                //set the left and right powers.
                setRightPow(-rightPower);
                setLeftPow(-leftPower);
            } else {
                setLeftPow(gamepad1.left_stick_y);
                setRightPow(gamepad1.right_stick_y);
            }
            //
            setLiftPower(-gamepad2.left_stick_y);
            armMotor.setPower(-gamepad2.right_stick_y);

            if(gamepad2.x) intakeStore();
            else if(gamepad2.b) intakeDown();
            else if(gamepad2.a) intakeTransfer();

            if(gamepad2.left_bumper){
                rDump.setPosition(.375);
                lDump.setPosition(0.63);
            } else {
                rDump.setPosition(0.55);
                lDump.setPosition(0.43);
            }
            if(gamepad2.right_bumper){
                intakeMotor.setPower(0.75);
            } else if(gamepad2.right_trigger > 0.2){
                intakeMotor.setPower(-gamepad2.right_trigger);
            } else {
                intakeMotor.setPower(0.0);
            }


            if (gamepad1.right_bumper && !val){
                notTank = !notTank;
            }
            val = gamepad1.right_bumper;

            if(gamepad2.dpad_left ||gamepad2.dpad_down || gamepad2.dpad_right || gamepad2.dpad_up || gamepad2.left_trigger > 0.5 ){
                sorterServo.setPosition(0.8);
            } else {
                sorterServo.setPosition(0.5);
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
        ritkServo.setPosition(0.95);
        litkServo.setPosition(0.05);

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
