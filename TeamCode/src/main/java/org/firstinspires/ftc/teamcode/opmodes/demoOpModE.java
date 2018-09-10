package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

public class demoOpModE  extends LinearOpMode {
    //assumes

    BNO055IMU imu;

    public void runOpMode(){

        int currentState = 0; // stores the state of the robot

        while(opModeIsActive()){
            switch (currentState){
                case 1:
                    // perform one action
                    break;
                case 2:
                    // perform a second action
                    break;
            }
        }

    }

    void initGyro(){
        // Initialization
        BNO055IMU.Parameters  parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           	 = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit            	= BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled       	= false;
        parameters.mode                 	= BNO055IMU.SensorMode.IMU;
        parameters.loggingTag           	= "IMU";
        imu	                         		= hardwareMap.get(BNO055IMU.class, "imu name");
        imu.initialize(parameters);
    }





}
