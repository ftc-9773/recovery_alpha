package org.firstinspires.ftc.teamcode.PositionTracking;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.resources.Vector;

/**
 * Created by Vikesh on 11/26/2017.
 */

public class CoordinateSystem {
    private double xPosition;
    private double yPosition;
    private long prevEncoderVal = 0;
    private double distance = 0;
    private double xDistance;
    private double yDistance;
    private double robotHeading;
    private float wheelDiameter = 3;
    private double encoderTicksPerInch = (19.8 * 28) / (wheelDiameter * Math.PI);
    private boolean firstRun;
    private Gyro myGyro;
    private double wheelHeading;
    private Telemetry telemetry;

    public CoordinateSystem(double xPosition, double yPosition, Gyro myGyro, Telemetry telemetry){
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.myGyro = myGyro;
        this.telemetry = telemetry;
    }

    public void beginPositionUpdate(long currentEncoderVal, double wheelDirection){
        prevEncoderVal = currentEncoderVal;
        robotHeading = myGyro.getHeading()+wheelDirection;
    }
    public void endPositionUpdate(long currentEncoderVal){
        distance = (prevEncoderVal - currentEncoderVal)/encoderTicksPerInch;
        xDistance = Math.sin(robotHeading)*distance;
        yDistance = Math.cos(robotHeading)*distance;
        xPosition += xDistance;
        yPosition += yDistance;
        telemetry.addData("X Position", "%.1f", xPosition);
        telemetry.addData("Y Position", "%.1f", yPosition);
    }
}
