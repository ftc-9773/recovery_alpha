package org.firstinspires.ftc.teamcode.HardwareControl;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.resources.ButtonStatus;

/**
 * Created by arjun on 11/4/2017.
 */
@Disabled
@TeleOp(name="CubeIntakeMovement")
public class CubeIntakeMovement extends LinearOpMode
{
    private DcMotor RightIntakeMotor;
    private DcMotor LeftIntakeMotor;
    HardwareMap hwMap           =  null;

    public CubeIntakeMovement()
    {

    }

    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        LeftIntakeMotor   = hwMap.dcMotor.get("leftIntake");
        RightIntakeMotor  = hwMap.dcMotor.get("rightIntake");
        LeftIntakeMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        RightIntakeMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors

        // Set all motors to zero power
        LeftIntakeMotor.setPower(0);
        RightIntakeMotor.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        LeftIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RightIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    boolean leftDpadPrevState = false;
    boolean leftDpadCurrState = false;
    boolean rightDpadPrevState = false;
    boolean rightDpadCurrState = false;
    ButtonStatus leftDPadButtonStatus = new ButtonStatus();
    ButtonStatus rightDPadButtonStatus = new ButtonStatus();

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();
        while (opModeIsActive()){
            leftDPadButtonStatus.recordNewValue(gamepad1.dpad_left);
            rightDPadButtonStatus.recordNewValue(gamepad1.dpad_right);

            leftDpadCurrState = gamepad1.dpad_left;

            // check for button state transitions.
            if (leftDpadCurrState && (leftDpadCurrState != leftDpadPrevState))  {
                //LeftIntakeMotor.setPower(100);
                leftDpadPrevState = leftDpadCurrState;
                telemetry.addData("Left Cube Intake system now working.", "thanks!");
                telemetry.update();
            } else {
                leftDpadPrevState = false;
                //LeftIntakeMotor.setPower(0);
            }
            // check the status of the x button on either gamepad.
            rightDpadCurrState = gamepad1.dpad_right;


            // check for button state transitions.
            if (rightDpadCurrState && (rightDpadCurrState != rightDpadPrevState))  {
                //RightIntakeMotor.setPower(100);
                rightDpadPrevState = rightDpadCurrState;
                telemetry.addData("Right Cube Intake now working.", "thanks!");
                telemetry.update();
            } else {
                rightDpadPrevState = false;
                //RightIntakeMotor.setPower(0);
            }
        }

    }


}



