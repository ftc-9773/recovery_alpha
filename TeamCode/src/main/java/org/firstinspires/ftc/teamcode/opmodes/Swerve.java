package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;
import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;
import org.firstinspires.ftc.teamcode.HardwareControl.IntakeController;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;

/**
 * Created by Vikesh on 10/29/2017.
 */

@TeleOp(name = "swerveOpMode")
@Disabled
public class Swerve extends LinearOpMode {

    private static final String TAG = "ftc9773 Swerve";
    private static final boolean DEBUG = false;

    private static FTCrobot myRobot;

    @Override
    public void runOpMode() throws InterruptedException {
        Log.e(TAG, "Started initializing");

        // Create objects
        myRobot = new FTCrobot(hardwareMap, telemetry, gamepad1, gamepad2);

        // init the lift
        myRobot.homeLift(CubeTray.LiftFinalStates.HIGH);
        myRobot.myCubeTray.setToPos(CubeTray.LiftFinalStates.HIGH);


        waitForStart();
        myRobot.myCubeTray.setServoPos(CubeTray.TrayPositions.CARRYING);



        while (opModeIsActive()) {


            double timeStart = System.currentTimeMillis();

            myRobot.runGamepadCommands();
            myRobot.doTelemetry();

            // Display gamepad values
            telemetry.addData("Gamepad x:", gamepad1.left_stick_x);
            telemetry.addData("Gamepad y:", gamepad1.left_stick_y);


            telemetry.update();

            if (DEBUG) { Log.e(TAG, "Time Loop End : " + (System.currentTimeMillis() - timeStart)); }
        }
    }
}
