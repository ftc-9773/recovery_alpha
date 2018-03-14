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
    private double armUpPos;
    private double armOutPos;
    private double knockerOutPos;
    private double knockerOutLeft;
    private double knockerOutRight;
    private double armStoredPos;
    private double knockerLeftSideStowedPos;
    private double knockerRightSidePos;



    public JewelKnocker(HardwareMap hwMap) {
        SafeJsonReader safeJsonReader = new SafeJsonReader("jewelknockerpositions");

        armServo = hwMap.servo.get("jaServo");
        knockerServo = hwMap.servo.get("jkServo");

        armOutPos = safeJsonReader.getDouble("armOutPos");
        armStoredPos = safeJsonReader.getDouble("armStoredPos");
        knockerOutPos = safeJsonReader.getDouble("knockerOutPos");
        knockerOutLeft = safeJsonReader.getDouble("knockerOutLeft");
        knockerOutRight = safeJsonReader.getDouble("knockerOutRight");
        knockerLeftSideStowedPos = safeJsonReader.getDouble("knockerLeftSideStowedPos");
        knockerRightSidePos = safeJsonReader.getDouble("knockerRightSidePos");
        armUpPos = safeJsonReader.getDouble("armUpPos");
    }
    public void ArmInitialLower() { armServo.setPosition(armOutPos);}
    public void ArmReturn(){ armServo.setPosition(armStoredPos);
        Log.e("Servo Position", "" + armStoredPos);
    }
    public void ArmUp() {armServo.setPosition(armUpPos);}
    public void KnockerStartMove(){knockerServo.setPosition(knockerOutPos);}
    public void KnockerLeftStowed(){knockerServo.setPosition(knockerLeftSideStowedPos);}
    public void KnockerRight(){knockerServo.setPosition(knockerRightSidePos);}





}

