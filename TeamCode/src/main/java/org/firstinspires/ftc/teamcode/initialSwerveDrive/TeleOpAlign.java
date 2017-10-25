package org.firstinspires.ftc.teamcode.initialSwerveDrive;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.JSON.jsonIO;
import org.firstinspires.ftc.teamcode.resources.ButtonStatus;
import org.json.JSONException;

import java.io.IOException;
import java.lang.*;



/**
 * Created by Vikesh on 10/8/2017.
 */
@TeleOp(name="TeleOpAlign")
@Disabled
public class TeleOpAlign extends LinearOpMode{

    //hardware declarations - servos only, because this opmode does not use the motors:
    private Servo swerveServo0;
    private Servo swerveServo1;
    private Servo swerveServo2;
    private Servo swerveServo3;

    //create new JsonReader object.
    jsonIO opmodeCfg = new jsonIO(jsonIO.opModesDir + "test.json");

    //setServo sets all servos to the position on the imput.
    private void setServo(double pos) {
        swerveServo0.setPosition(pos);
        swerveServo1.setPosition(pos);
        swerveServo2.setPosition(pos);
        swerveServo3.setPosition(pos);
    }

    @Override
    public void runOpMode() throws InterruptedException {


//defining the servos from the hardwareMap
        swerveServo0 = hardwareMap.get(Servo.class, "swerveServo0");
        swerveServo1 = hardwareMap.get(Servo.class, "swerveServo1");
        swerveServo2 = hardwareMap.get(Servo.class, "swerveServo2");
        swerveServo3 = hardwareMap.get(Servo.class, "swerveServo3");


//boolean data for the states of the x and b buttons
        boolean bPrevState = false;
        boolean bCurrState = false;
        boolean xPrevState = false;
        boolean xCurrState = false;

        //position is used as an argument to setServo
        double position = 0.5;
        //increment is the amount that the servo will be changed for every button press.
        double increment = 0.001;
        double buttonIncrement = 0.032;
        double neutral = 0.1;
        //servoAngle contains the angles of each servo
        double servoAngle[] = new double[4];
        servoAngle[0] = 0.5;
        servoAngle[1] = 0.5;
        servoAngle[2] = 0.5;
        servoAngle[3] = 0.5;
        setServo(position);
        ButtonStatus xButtonStatus = new ButtonStatus();
        ButtonStatus yButtonStatus = new ButtonStatus();
        ButtonStatus aButtonStatus = new ButtonStatus();
        ButtonStatus bButtonStatus = new ButtonStatus();
        ButtonStatus backLeftBumperButtonStatus = new ButtonStatus();
        ButtonStatus backRightBumperButtonStatus = new ButtonStatus();

        int currentServoProgram = -1;

        waitForStart();
        while (opModeIsActive()){
            try {
            opmodeCfg.jsonRoot.put("modOneDefPos", servoAngle[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

            xButtonStatus.recordNewValue(gamepad1.x);
            yButtonStatus.recordNewValue(gamepad1.y);
            aButtonStatus.recordNewValue(gamepad1.a);
            bButtonStatus.recordNewValue(gamepad1.b);
            backLeftBumperButtonStatus.recordNewValue(gamepad1.left_bumper);
            backRightBumperButtonStatus.recordNewValue(gamepad1.right_bumper);
            if (currentServoProgram == -1){
                if (xButtonStatus.isJustOn()){
                    currentServoProgram = 0;
                } else if (yButtonStatus.isJustOn()){
                    currentServoProgram = 1;
                } else if (aButtonStatus.isJustOn()){
                    currentServoProgram = 2;
                } else if (bButtonStatus.isJustOn()){
                    currentServoProgram = 3;
                }
            } else {
                if (backLeftBumperButtonStatus.isJustOn()){
                    currentServoProgram = -1;
                } else {
                    double dir = gamepad1.left_stick_x;
                    if (dir>0) {
                        servoAngle[currentServoProgram] -= increment;
                        if (servoAngle[currentServoProgram]<0.0) { position = 0.0; }
                    } else if (dir < 0) {
                        servoAngle[currentServoProgram] += increment;
                        if (servoAngle[currentServoProgram]>1.0) { position = 1.0; }
                    }
                }
            }

            double dir = gamepad1.left_stick_x;
            if (dir>0) {
                position -= increment;
                if (position>1.0) { position = 1.0; }
            } else if (dir < 0) {
                position += increment;
                if (position<0) { position = 0; }
            }

            // check the status of the x button on either gamepad.
            /*bCurrState = gamepad1.x;

            // check for button state transitions.
            if (bCurrState && (bCurrState != bPrevState))  {
                pos += buttonIncrement;
                bPrevState = bCurrState;
                telemetry.addData("Button x is clicked, pos updated. New pos is: ", "%.2f", pos);
                telemetry.update();
            } else {
                bPrevState = false;
            }
            // check the status of the x button on either gamepad.
            xCurrState = gamepad1.b;
            */

            // check for button state transitions.
            /*if (xCurrState && (xCurrState != xPrevState))  {
                pos -= buttonIncrement;
                xPrevState = xCurrState;
                telemetry.addData("Button b is clicked, pos updated. New pos is: ", "%.2f", pos);
                telemetry.update();
            } else {
                xPrevState = false;
            }
            */

            setServo(position);
            // Send telemetry message to signify robot running;
            telemetry.addData("hi Nicky, my pos is " ,  "%.2f", position);
            telemetry.addData("hi Alex, my dir is " ,  "%.2f", dir);
            telemetry.addData("", opmodeCfg.jsonRoot.toString());
            telemetry.update();

        }
        try {
            opmodeCfg.writeJson();
        }
        catch(IOException e){
            e.printStackTrace();
            telemetry.addData("error:", "IOException");
        }
        telemetry.update();
    }
}
