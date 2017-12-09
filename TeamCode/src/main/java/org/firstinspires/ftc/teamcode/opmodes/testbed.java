package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareControl.DriveWithPID;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;

/**
 * Created by Vikesh on 11/19/2017.
 */
@Autonomous(name = "testbed")
public class testbed extends LinearOpMode{

    Gyro myGyro;
    SwerveController mySwerveController;
    DriveWithPID myDriveWithPID;

    @Override
    public void runOpMode() throws InterruptedException {
        /*String[] command = new String[4];
        controlParser control = new controlParser("test", telemetry);
        command = control.getNextCommand();
        */

        myGyro = new Gyro(hardwareMap);
        mySwerveController = new SwerveController(hardwareMap, myGyro, telemetry);
        myDriveWithPID = new DriveWithPID(mySwerveController, myGyro);

        waitForStart();

        while(opModeIsActive()) {
            mySwerveController.pointModules(true, gamepad1.left_stick_x, gamepad1.left_stick_y * -1, gamepad1.right_stick_x);
            mySwerveController.moveRobot();
            telemetry.addData("Gyro Reading", myGyro.getImuReading());
            telemetry.addData("Gyro heading", myGyro.getHeading());
            telemetry.update();
        }
    }
}
