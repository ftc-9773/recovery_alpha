package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareControl.DriveWithPID;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.controlParser;

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
        mySwerveController = new SwerveController(hardwareMap, myGyro, false);
        myDriveWithPID = new DriveWithPID(mySwerveController, myGyro);

        waitForStart();

        while(opModeIsActive()) {
            /*
            switch (command[0]) {
                case "m1":
                    telemetry.addData("Command Type: ", "m1");
                    break;
                case "m2":
                    telemetry.addData("Command Type: ", "m2");
                    break;
                default:
                    telemetry.addData("Error: ", "No data found");
                    break;
            }
            telemetry.update(); */
            myDriveWithPID.driveStraight(false, 0.5, 90, 5);

        }
    }
}
