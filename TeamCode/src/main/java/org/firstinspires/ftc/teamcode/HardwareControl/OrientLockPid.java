package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.PIDController;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.resources.Timer;


/**
 * Created by Vikesh on 2/18/2018.
 */

public class OrientLockPid {
    private LinearOpModeCamera linearOpModeCamera;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    private SwerveController swerveController;
    private PIDController pidController;
    private Gyro gyro;
    private Timer timer;

    private double wheelDiameter = 3.0;
    private double ticksPerInch = (19.8 * 28) / (wheelDiameter * Math.PI);;
    private double distanceDriven = 0.0;
    private double error = 0.0;
    private double rotation = 0.0;
    private double initGyroHeading;
    private double kP;
    private double kI;
    private double kD;
    private long initEncoderPos;
    private long flwEncoderZero;
    private long blwEncoderZero;
    private long frwEncoderZero;
    private long brwEncoderZero;
    private long flwEncoder;
    private long frwEncoder;
    private long blwEncoder;
    private long brwEncoder;

    private SafeJsonReader safeJsonReader;

    public OrientLockPid(LinearOpModeCamera linearOpModeCamera){
        this.linearOpModeCamera = linearOpModeCamera;
        hardwareMap = linearOpModeCamera.hardwareMap;
        telemetry = linearOpModeCamera.telemetry;
        gyro = new Gyro(hardwareMap);
        swerveController = new SwerveController(hardwareMap, gyro, telemetry);
        safeJsonReader = new SafeJsonReader("OrientPidParameters");
        kP = safeJsonReader.getDouble("kP");
        kI = safeJsonReader.getDouble("kI");
        kD = safeJsonReader.getDouble("kD");
        pidController = new PIDController(kP, kI, kD);
    }

    public void driveStraightDist(double angleDegrees, double power, double distanceInches, double maxTimeSeconds){
        resetEnocders();
        initGyroHeading = gyro.getHeading();
        while(swerveController.getIsTurning()) {
            swerveController.steerSwerve(false, power, angleDegrees, 0, -1);
        }
        timer = new Timer(maxTimeSeconds);
        while(distanceDriven < Math.abs(distanceInches) && !timer.isDone()){
            swerveController.moveRobot(false);
            distanceDriven = averageEncoderPos()/ticksPerInch;
            error = initGyroHeading-gyro.getHeading();
            rotation = pidController.getPIDCorrection(error);
            swerveController.steerSwerve(false, power, angleDegrees, rotation, -1);
        }
    }
    public double averageEncoderPos(){
        flwEncoder = swerveController.getFlwEncoderCount()-flwEncoderZero;
        frwEncoder = swerveController.getFrwEncoderCount()-frwEncoderZero;
        blwEncoder = swerveController.getBlwEncoderCount()-blwEncoderZero;
        brwEncoder = swerveController.getBrwEncoderCount()-brwEncoderZero;
        return((flwEncoder+blwEncoder+brwEncoder+frwEncoder)/4);
    }
    public void resetEnocders(){
        flwEncoderZero = swerveController.getFlwEncoderCount();
        blwEncoderZero = swerveController.getBlwEncoderCount();
        brwEncoderZero = swerveController.getBrwEncoderCount();
        frwEncoderZero = swerveController.getFrwEncoderCount();
    }
}
