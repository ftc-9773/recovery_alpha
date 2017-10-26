package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.resources.ButtonStatus;
import org.json.JSONObject;
import org.json.JSONException;
import org.firstinspires.ftc.teamcode.JSON.jsonIO;

import java.io.IOException;

/**
 * Created by Vikesh on 10/25/2017.
 */
@TeleOp(name = "Align By Servo")
public class AlignByServo extends LinearOpMode{

    //initialize the servos
    private Servo mod0Servo;
    private Servo mod1Servo;
    private Servo mod2Servo;
    private Servo mod3Servo;

    //this array will hold the servo positions
    public double[] positions = new double[4];

    //create a jsonIO object to read and write from json file

    jsonIO alignJson = new jsonIO(jsonIO.opModesDir + "positions.json");

    //create a ButtonStatus object for "B"
    ButtonStatus buttonB = new ButtonStatus();

    //define utility variables:

    char servoNum = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        //define servos
        mod0Servo = hardwareMap.get(Servo.class, "swerveServo0");
        mod1Servo = hardwareMap.get(Servo.class, "swerveServo1");
        mod2Servo = hardwareMap.get(Servo.class, "swerveServo2");
        mod3Servo = hardwareMap.get(Servo.class, "swerveServo3");

        //load values from json file into positions[]
        try
        {
            positions[0] = alignJson.jsonRoot.getDouble("modOneDefPos");
            positions[1] = alignJson.jsonRoot.getDouble("modTwoDefPos");
            positions[2] = alignJson.jsonRoot.getDouble("modThreeDefPos");
            positions[3] = alignJson.jsonRoot.getDouble("modFourDefPos");
        }
        catch(JSONException e){
            e.printStackTrace();
            telemetry.addData("exception: ", "JSONException");
        }

        waitForStart();

        //begin main program loop:

        while(opModeIsActive()){

            //set servos to positions in positions[]
            mod0Servo.setPosition(positions[0]);
            mod1Servo.setPosition(positions[1]);
            mod2Servo.setPosition(positions[2]);
            mod3Servo.setPosition(positions[3]);

            //If the b button has just been pressed, increment servoNum. if servoNum is >= 3, set it to 0.
            buttonB.recordNewValue(gamepad1.b);
            if(buttonB.isJustOn() && servoNum < 3){
                servoNum++;
            }
            else if(buttonB.isJustOn()){
                servoNum = 0;
            }

            //increment servo position when button A is pressed. If servo position = 1, then it is set to 0.
            if(gamepad1.y && positions[servoNum]<=1) {
                positions[servoNum] += 0.000075;
            }else if (gamepad1.y){
                positions[servoNum] = 0;
            }

            if(gamepad1.a && positions[servoNum]>=0) {
                positions[servoNum] -= 0.000075;
            }else if (gamepad1.a){
                positions[servoNum] = 1;
            }


            telemetry.addData("servo0: ", "%.3f", positions[0]);
            telemetry.addData("servo1: ", "%.3f", positions[1]);
            telemetry.addData("servo2: ", "%.3f", positions[2]);
            telemetry.addData("servo3: ", "%.3f", positions[3]);
            telemetry.update();
        }

        //add values to the JSON string
        try {
            alignJson.jsonRoot.put("modOneDefPos", positions[0]);
            alignJson.jsonRoot.put("modTwoDefPos", positions[1]);
            alignJson.jsonRoot.put("modThreeDefPos", positions[2]);
            alignJson.jsonRoot.put("modFourDefPos", positions[3]);
        }
        catch(JSONException e1){
            e1.printStackTrace();
        }

        //write json string to json file.
        try{
            alignJson.writeJson();
        }
        catch(IOException e2){
            e2.printStackTrace();
        }
    }
}
