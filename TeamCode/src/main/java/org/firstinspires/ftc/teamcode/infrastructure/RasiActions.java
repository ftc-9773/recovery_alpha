package org.firstinspires.ftc.teamcode.infrastructure;

import android.util.Log;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;
import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.opmodes.Swerve;
import org.firstinspires.ftc.teamcode.resources.Timer;

/**
 * Created by vikesh on 1/5/18.
 */

public class RasiActions {
    private RasiParser rasiParser;
    private FTCrobot ftcRobot;
    private Timer timer2;
    private LinearOpModeCamera linearOpModeCamera;
    public RasiActions(String rasiFilename, String rasiTag, LinearOpModeCamera myLinearOpModeCamera, Gamepad gamepad1, Gamepad gamepad2, Telemetry telemetry, HardwareMap hwMap){
        this.linearOpModeCamera = myLinearOpModeCamera;
        rasiParser = new RasiParser(rasiFilename, rasiTag);
        //ftcRobot = new FTCrobot(hwMap, telemetry, gamepad1, gamepad2, myLinearOpModeCamera);
    }
    public void runRasi() throws InterruptedException {
        //ftcRobot.myCubeTray.setZeroFromCompStart();
        rasiParser.loadNextCommand();
        while (!linearOpModeCamera.isStopRequested()) {
            switch (rasiParser.getParameter(0)) {
                case "drv":
                    //ftcRobot.myDriveWithPID.driveDist(rasiParser.getAsDouble(1), rasiParser.getAsDouble(2), rasiParser.getAsDouble(3));
                    Log.i("RasiActions", "drv");
                    break;
                case "intkl":
                    //ftcRobot.myManualIntakeController.lowerIntake(true);
                    Timer timer = new Timer(2.0);
                    while (!timer.isDone() && !linearOpModeCamera.isStopRequested()) {}
                    //ftcRobot.myManualIntakeController.lowerIntake(false);
                    Log.i("RasiActions", "intkl");
                    break;
                case "intki":
                    //ftcRobot.myManualIntakeController.RunIntake(0, -1);
                    Log.i("RasiActions", "intki");
                    break;
                case "intko":
                    //ftcRobot.myManualIntakeController.RunIntake(0, 1);
                    Log.i("RasiActions", "intko");
                    break;
                case "intks":
                    //ftcRobot.myManualIntakeController.RunIntake(0, 1);
                    //ftcRobot.myManualIntakeController.RunIntake(0, 0);
                    Log.i("RasiActions", "intks");
                case "ctload":
                    timer2 = new Timer(2.0);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        //ftcRobot.myCubeTray.setToPos(CubeTray.LiftFinalStates.LOADING);
                        //ftcRobot.myCubeTray.updatePosition();
                    }
                    Log.i("RasiActions", "ctload");
                    break;
                case "ctlow":
                    timer2 = new Timer(2.0);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        //ftcRobot.myCubeTray.setToPos(CubeTray.LiftFinalStates.LOW);
                        //ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "cthigh":
                    Timer timer2 = new Timer(2.0);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        //ftcRobot.myCubeTray.setToPos(CubeTray.LiftFinalStates.HIGH);
                        //ftcRobot.myCubeTray.updatePosition();
                    }
                    Log.i("RasiActions", "cthigh");
                    break;
                case "wait":
                    timer2 = new Timer(rasiParser.getAsDouble(1));
                    while (!timer2.isDone()&&!linearOpModeCamera.isStopRequested()) {}
                    Log.i("RasiActions", "wait");
                    break;
                case "end":
                    linearOpModeCamera.requestOpModeStop();
                    while(linearOpModeCamera.opModeIsActive()){}
                    Log.i("RasiActions", "end");
                    break;
                default:
                    break;
            }
            rasiParser.loadNextCommand();
        }
    }
}
