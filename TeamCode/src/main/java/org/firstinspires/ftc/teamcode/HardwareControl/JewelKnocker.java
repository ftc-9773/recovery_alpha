package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by nicky on 2/19/18.
 */

public class JewelKnocker {

    private Servo armServo;
    private Servo knockerServo;

    private static final double armStoredPos = 0.0;
    private static final double armOutPos = .6;
    private static final double knockerOutPos= .6;
    private static final double knockerRightSidePos= 1;
    private static final double knockerLeftSideStowedPos= 0;




    public JewelKnocker(HardwareMap hwMap) {

        armServo = hwMap.servo.get("jaServo");
        knockerServo = hwMap.servo.get("jkServo");



    }
    public void ArmInitialLower() { armServo.setPosition(armOutPos);}
    public void ArmReturn(){ armServo.setPosition(armStoredPos);}
    public void KnockerStartMove(){knockerServo.setPosition(knockerOutPos);}
    public void KnockerLeftStowed(){knockerServo.setPosition(knockerLeftSideStowedPos);}
    public void KnockerRight(){knockerServo.setPosition(knockerRightSidePos);}





}
