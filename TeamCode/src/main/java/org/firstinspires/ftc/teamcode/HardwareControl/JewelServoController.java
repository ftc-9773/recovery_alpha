package org.firstinspires.ftc.teamcode.HardwareControl;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import android.util.Log;

import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

import java.io.IOException;

/**
 * Created by nicky on 1/2/18.
 */

public class JewelServoController {

    Servo jewelServo;

    double leftPos = 0.81;
    double rightPos = 0.35;
    double centerPos = 0.16;
    String TAG = "ftc9773_JewelArm";


    public JewelServoController(HardwareMap hardwareMap) {
        jewelServo = hardwareMap.servo.get("jServo");
            SafeJsonReader servoPositions = new SafeJsonReader("JewelArmPositions");
        leftPos = checkIfIssueWithRead(leftPos,"LeftPos", servoPositions);
        centerPos = checkIfIssueWithRead(centerPos,"CenterPos", servoPositions);
        rightPos = checkIfIssueWithRead(rightPos,"RightPos", servoPositions);


    }
    public void setToLeftPos() {
        jewelServo.setPosition(leftPos);
    }
    public void setToRightPos() {
        jewelServo.setPosition(rightPos);
    }
    public void setToCenterPos() {
        jewelServo.setPosition(centerPos);
    }




    private double checkIfIssueWithRead(double backupVal, String name, SafeJsonReader reader){
        double readVal = reader.getDouble(name);
        if (readVal != 0.0) return readVal;
        else return backupVal;
    }

    // aliases
    public void setToBlockPos(){  setToCenterPos();}
    public void setToRetractPos() {setToLeftPos();}

}
