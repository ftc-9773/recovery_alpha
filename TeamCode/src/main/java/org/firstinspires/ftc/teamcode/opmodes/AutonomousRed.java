package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareControl.DriveWithPID;
import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;

/**
 * Created by Vikesh on 11/22/2017.
 */
@Autonomous(name = "AutonomousRed")
public class AutonomousRed extends LinearOpMode {
    FTCrobot ftcRobot;
    DriveWithPID mydriveWithPID;
    SwerveController mySwerveController;
    Gyro myGyro;
    @Override
    public void runOpMode() throws InterruptedException {
        mySwerveController = new SwerveController(hardwareMap, myGyro, false);
        ftcRobot = new FTCrobot(hardwareMap, telemetry, gamepad1, gamepad2);
        myGyro = new Gyro(hardwareMap);
        mydriveWithPID = new DriveWithPID(mySwerveController, myGyro);
        waitForStart();
        //ftcRobot.runRASI("autored");
        mydriveWithPID.driveStraight(false, 0.5, 45, 3);
    }
}
