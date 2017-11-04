package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.resources.polarVector;

/**
 * Created by Vikesh on 10/28/2017.
 */
public class SwerveModule extends LinearOpMode{

    private AnalogInput analogIn;
    private DcMotor swerveMotor;
    private Servo swerveServo;

    public SwerveModule(/*String analogIn1, String swerveMotor1, String swerveServo1*/){

        this.analogIn = hardwareMap.get(AnalogInput.class, "input0");
        this.swerveMotor = hardwareMap.get(DcMotor.class, "swerveMotor0");
        this.swerveServo = hardwareMap.get(Servo.class, "swerveServo0");
    }
    public void setVector(polarVector angleVector){
        angleVector.direction = (angleVector.direction+90)/360;
        if (angleVector.direction<((analogIn.getVoltage())/3.24-0.005)){
            swerveServo.setPosition(1);
        }
        else if (angleVector.direction>((analogIn.getVoltage())/2.4+0.005)){
            swerveServo.setPosition(0);
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {

    }
}
