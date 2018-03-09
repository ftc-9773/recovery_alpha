package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

/**
 * Created by nicky on 2/19/18.
 */

public class JewelKnocker {

    private Servo armServo;
    private Servo knockerServo;
    private SafeJsonReader safeJsonReader = new SafeJsonReader("jewel knocker positions");
    private double armOutPos;
    private double knockerOutPos;
    private double armStoredPos;
    private double knockerLeftSideStowedPos;
    private double knockerRightSidePos;



    public JewelKnocker(HardwareMap hwMap) {
        armServo = hwMap.servo.get("jaServo");
        knockerServo = hwMap.servo.get("jkServo");

        armOutPos = safeJsonReader.getDouble("armOutPos");
        armStoredPos = safeJsonReader.getDouble("armStoredPos");
        knockerOutPos = safeJsonReader.getDouble("knockerOutPos");
        knockerLeftSideStowedPos = safeJsonReader.getDouble("knockerLeftSideStowedPos");
        knockerRightSidePos = safeJsonReader.getDouble("knockerRightSidePos");
    }
    public void ArmInitialLower() { armServo.setPosition(armOutPos);}
    public void ArmReturn(){ armServo.setPosition(armStoredPos);
        Log.e("Servo Position", "" + armStoredPos);
    }
    public void KnockerStartMove(){knockerServo.setPosition(knockerOutPos);}
    public void KnockerLeftStowed(){knockerServo.setPosition(knockerLeftSideStowedPos);}
    public void KnockerRight(){knockerServo.setPosition(knockerRightSidePos);}





}

