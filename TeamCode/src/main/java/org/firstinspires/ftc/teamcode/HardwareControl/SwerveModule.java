package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.opmodes.Swerve;
import org.firstinspires.ftc.teamcode.resources.Vector;

/**
 * Created by Vikesh on 10/28/2017.
 * Modified by Nicky on 11/7/2017
 */
public class SwerveModule {

    private HardwareMap hwMap;
    private DcMotor swerveMotor;
    private Servo swerveServo;
    private AnalogInput swerveAbsEncoder;
    private Vector velocityVector = new Vector(true, 0, 0);
    private double dist;
    private double servoMove;

    private static final double PIDCoef = 3;
    private static final double PIDExp = 2;
    private static final String TAG = "ftc9773 SwerveModule";

    public SwerveModule(HardwareMap mapHW, String hardwareMapTag, String jsonId) {
        // Pass the hardware map
        hwMap = mapHW;

        // Set the electronics
        swerveServo = hwMap.servo.get(hardwareMapTag + "Servo");
        swerveMotor = hwMap.dcMotor.get(hardwareMapTag + "Motor");
        swerveAbsEncoder = hwMap.analogInput.get(hardwareMapTag + "AbsEncoder");
    }

    // Updates the module direction
    public void setVector(double xComponent, double yComponent){
        velocityVector.set(true, xComponent, yComponent);
        double currentPosition = swerveAbsEncoder.getVoltage() / 3.245;

        //Calculates distance to move on -pi to pi
        dist = velocityVector.getAngle() - currentPosition * 2*Math.PI;
        if (dist > 2 * Math.PI) {
            dist -= 2 * Math.PI;
        } else if (dist < -1 * Math.PI) {
            dist += 2 * Math.PI;
        }

        // Figure out how to move the servo

        // Give 0.05 radians of not moving
        if (Math.abs(dist) < 0.1) {
            swerveServo.setPosition(0.5);

        } else {
            //Use a multiplyer
            servoMove = 0.5 + Math.pow(dist, PIDExp) * PIDCoef;

            if (servoMove > 1) {
                swerveServo.setPosition(1.0);
            } else if (servoMove < 0) {
                swerveServo.setPosition(0);
            } else {
                swerveServo.setPosition(servoMove);
            }
        }

        //swerveMotor.setPower(driveMag);
        Log.e(TAG, "Encoder Position: " + currentPosition + "  Desired Position: " + velocityVector.getAngle() + "  Told Servo: " + dist);
    }
}
