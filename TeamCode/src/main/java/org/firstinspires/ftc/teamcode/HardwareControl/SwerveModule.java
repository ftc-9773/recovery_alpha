package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.opmodes.Swerve;
import org.firstinspires.ftc.teamcode.resources.cartesianVector;
import org.firstinspires.ftc.teamcode.resources.polarVector;

/**
 * Created by Vikesh on 10/28/2017.
 */
public class SwerveModule{

    private HardwareMap hwMap;
    private DcMotor swerveMotor;
    private Servo swerveServo;
    private cartesianVector moduleVector = new cartesianVector(0,0);
    private polarVector modPolarVector;
    private double driveMag;
    private double driveDir;
    private double range = 1;

    public SwerveModule(HardwareMap mapHW, String servoName, String motorName, String jsonId){
        hwMap = mapHW;
        hwMap.servo.get(servoName);
        hwMap.dcMotor.get(motorName);
    }

    public void setVector(double xComponent, double yComponent){
        moduleVector.set(xComponent, yComponent);
        modPolarVector = moduleVector.cartToPolar();

        driveDir = (modPolarVector.getDir()+90)/(180/range);
        driveMag = modPolarVector.getMag();

        swerveServo.setPosition(driveDir);
        swerveMotor.setPower(driveMag);
    }
}
