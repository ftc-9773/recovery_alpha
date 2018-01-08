package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;
import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;
import org.firstinspires.ftc.teamcode.HardwareControl.IntakeController;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;


/**
 * Created by zacharye on 12/16/17.
 */

    @TeleOp(name = "CompTeleop" )
    public class CompetitionTeleOp extends LinearOpModeCamera {

        private static final String TAG = "ftc9773 CompTeleop";
        private static final boolean DEBUG = false;

        private static FTCrobot myRobot;

        @Override
        public void runOpMode() throws InterruptedException {
            if (DEBUG) Log.e(TAG, "Started initializing");

            telemetry.addData("Init", "Waiting...");
            telemetry.update();


            // Create objects
            myRobot = new FTCrobot(hardwareMap, telemetry, gamepad1, gamepad2, this);

            // init the lift
            myRobot.myCubeTray.setZeroFromCompStart();
            myRobot.myCubeTray.setStartPosition(CubeTray.LiftFinalStates.STOWED);


            while (!opModeIsActive() && !isStopRequested()) {
                telemetry.addData("Init", "Success!!");
                telemetry.update();
            }

            waitForStart();
            myRobot.myCubeTray.setServoPos(CubeTray.TrayPositions.LOADING);
            myRobot.myCubeTray.setToPos(CubeTray.LiftFinalStates.LOADING);


            while (opModeIsActive()) {

                double timeStart = System.currentTimeMillis();

                myRobot.runGamepadCommands();
                myRobot.doTelemetry();

                if (DEBUG) { Log.e(TAG, "Time Loop End : " + (System.currentTimeMillis() - timeStart)); }
            }
            myRobot.recordGyroPosition();
        }
}


