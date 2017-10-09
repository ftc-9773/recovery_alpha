package org.firstinspires.ftc.teamcode.initialSwerveDrive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Vikesh on 10/8/2017.
 */


@TeleOp(name = "SwerveDrive")
public class TeleOpSwerveOne extends LinearOpMode{

    private DcMotor swerveMotor0;
    private DcMotor swerveMotor1;
    private DcMotor swerveMotor2;
    private DcMotor swerveMotor3;

    private Servo swerveServo0;
    private Servo swerveServo1;
    private Servo swerveServo2;
    private Servo swerveServo3;

    @Override
    public void runOpMode() throws InterruptedException {
        swerveMotor0 = hardwareMap.get(DcMotor.class, "swerveMotor0");
        swerveMotor1 = hardwareMap.get(DcMotor.class, "swerveMotor1");
        swerveMotor2 = hardwareMap.get(DcMotor.class, "swerveMotor2");
        swerveMotor3 = hardwareMap.get(DcMotor.class, "swerveMotor3");

        swerveServo0 = hardwareMap.get(Servo.class,"swerveServe0");
        swerveServo1 = hardwareMap.get(Servo.class,"swerveServe1");
        swerveServo2 = hardwareMap.get(Servo.class,"swerveServe2");
        swerveServo3 = hardwareMap.get(Servo.class,"swerveServe3");

        swerveServo0.setPosition(0.5);
        swerveServo1.setPosition(0.5);
        swerveServo2.setPosition(0.5);
        swerveServo3.setPosition(0.5);

        swerveDriveActions swerveDrive = new swerveDriveActions(1, 2880);
        waitForStart();

        while(opModeIsActive()){

            swerveServo0.setPosition(swerveDrive.gpToDirection());
            swerveMotor0.setPower(swerveDrive.gpToMagnitude());

            swerveServo1.setPosition(swerveDrive.gpToDirection());
            swerveMotor1.setPower(swerveDrive.gpToMagnitude());

            swerveServo2.setPosition(swerveDrive.gpToDirection());
            swerveMotor2.setPower(swerveDrive.gpToMagnitude());

            swerveServo3.setPosition(swerveDrive.gpToDirection());
            swerveMotor3.setPower(swerveDrive.gpToMagnitude());

        }
    }
}
