package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

import java.io.IOException;

/**
 * Created by nicky on 1/2/18.
 */

public class JewelServoController {

    Servo jewelServo;

    double downPosition = 0.72;
    double upPosition = 0.1;

    public JewelServoController(HardwareMap hardwareMap) {
        jewelServo = hardwareMap.servo.get("jServo");

        try {
            SafeJsonReader servoPositions = new SafeJsonReader("JewelArmPositions");
            downPosition = servoPositions.getDouble("downPosition");
            upPosition = servoPositions.getDouble("upPosition");
        } catch (NullPointerException e) {
            Log.e("FTC9773_JewelServoCont", "Did not initialize from JSON");
        }
    }

    public void lowerArm() {
        jewelServo.setPosition(downPosition);
    }

    public void raiseArm() {
        jewelServo.setPosition(upPosition);
    }
}
