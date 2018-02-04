package org.firstinspires.ftc.teamcode.opmodes;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.HardwareControl.DistanceColorSensor;
import org.firstinspires.ftc.teamcode.HardwareControl.DriveWithPID;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.resources.ButtonStatus;

/**
 * Created by Vikesh on 11/19/2017.
 */
@Autonomous(name = "testbed")

public class testbed extends LinearOpMode{

    private DistanceColorSensor leftDistanceSensor;
    private DistanceColorSensor rightDistanceSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        leftDistanceSensor = new DistanceColorSensor(hardwareMap, "leftColorSensor");
        rightDistanceSensor = new DistanceColorSensor(hardwareMap, "rightColorSensor");
        waitForStart();
        while(opModeIsActive()){
            telemetry.addData("leftDistance", leftDistanceSensor.getDistance(DistanceUnit.CM));
            telemetry.addData("rightDistance", rightDistanceSensor.getDistance(DistanceUnit.CM));
            telemetry.addData("leftDerivative", leftDistanceSensor.getDerivative(DistanceUnit.CM));
            telemetry.addData("rightDerivative", rightDistanceSensor.getDerivative(DistanceUnit.CM));
        }
    }
}
