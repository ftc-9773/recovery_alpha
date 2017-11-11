package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

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
    private double errorAmt;
    private double servoMove;
    private double zeroPosition;

    private static final String TAG = "ftc9773 SwerveModule";



    //    PID STUFF   //

    // Variables
    private double proportionalCorrection;
    private double differentialCorrection;

    private double lastTime = -1;
    private double lastError;

    // Temporary PID Constants
    private final double Kp = 1.7;
    private final double Kd = 0.6;



    // For outside access
    public boolean isTurning = false;
    public double currentPosition = 0;



    //INIT
    public SwerveModule(HardwareMap hwMap, String hardwareMapTag, String jsonId) {
        // Pass the hardware map
        this.hwMap = hwMap;

        // Set the electronics
        swerveServo = hwMap.servo.get(hardwareMapTag + "Servo");
        swerveMotor = hwMap.dcMotor.get(hardwareMapTag + "Motor");
        swerveAbsEncoder = hwMap.analogInput.get(hardwareMapTag + "AbsEncoder");
    }


    // Private Functions:
    private double calculatePDCorrection() {

        // Proportional error
        proportionalCorrection = errorAmt * Kp;

        // Differential error
        if (lastTime != 0)  {
            differentialCorrection = Kd * (errorAmt - lastError) / (System.currentTimeMillis() - lastTime) / 1000;
            // Differential correction = Constant * (change in error / change in time)
        }

        return proportionalCorrection + differentialCorrection;
    }



    // Writes the module direction
    public void setVector(Vector velocityVector){

        // Finds the current position
        currentPosition = swerveAbsEncoder.getVoltage() / 3.245 * 2 * Math.PI - zeroPosition;

        //Calculates distance to move on -pi to pi
        errorAmt = velocityVector.getAngle() - currentPosition;
        if (errorAmt > 2 * Math.PI) {
            errorAmt -= 2 * Math.PI;
        } else if (errorAmt < -1 * Math.PI) {
            errorAmt += 2 * Math.PI;
        }
    }

    // Rotates the Module
    public void pointModule() {
        // ** Figure out how to move the servo **

        //Calculate PID
        double tellServo = calculatePDCorrection();

        //Correct onto servo's range
        if (tellServo > 1) {
            tellServo = 1;
        } else if (tellServo < 0) {
            tellServo = 0;
        }

        swerveServo.setPosition(tellServo);
        Log.e(TAG, "Desired Position: " + velocityVector.getAngle() + "  Told Servo: " + errorAmt + "   Current position: " + currentPosition);
    }

    // Drives the wheel
    public void driveModule() {
        swerveMotor.setPower(velocityVector.getMagnitude());
    }

}
