package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareControl.DriveWithPID;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.resources.ButtonStatus;

/**
 * Created by Vikesh on 11/19/2017.
 */
@Autonomous(name = "testbed")
@Disabled

public class testbed extends LinearOpMode{

    Gyro myGyro;
    CRServo flwServo;
    double power = 0;
    double change = 0.1;

    ButtonStatus a = new ButtonStatus();
    ButtonStatus y = new ButtonStatus();
    ButtonStatus x = new ButtonStatus();
    ButtonStatus b = new ButtonStatus();

    @Override
    public void runOpMode() throws InterruptedException {
        /*String[] command = new String[4];
        controlParser control = new controlParser("test", telemetry);
        command = control.getNextCommand();
        */

        flwServo = hardwareMap.crservo.get("flwServo");
        myGyro = new Gyro(hardwareMap);

        waitForStart();

        while(opModeIsActive()) {

            a.recordNewValue(gamepad1.a);
            y.recordNewValue(gamepad1.y);
            x.recordNewValue(gamepad1.x);
            b.recordNewValue(gamepad1.b);

            if (a.isJustOn()) {
                power -= change;
            } else if (y.isJustOn()) {
                power += change;
            } else if (x.isJustOn()) {
                change /= 10;
            } else if (b.isJustOn()) {
                change *= 10;
            }

            flwServo.setPower(power);

            telemetry.addData("Power", power);
            telemetry.addData("Change", change);
            telemetry.addData("Gyro Reading", myGyro.getImuReading());
            telemetry.addData("Gyro heading", myGyro.getHeading());
            telemetry.update();
        }
    }
}
