package org.firstinspires.ftc.teamcode.infrastructure;

import android.util.Log;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;
import org.firstinspires.ftc.teamcode.HardwareControl.FTCrobot;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;

import org.firstinspires.ftc.teamcode.resources.Timer;
import org.firstinspires.ftc.teamcode.HardwareControl.LiftFinalStates;

/**
 * Created by vikesh on 1/5/18.
 */

public class RasiActions {
    public RasiParser rasiParser;
    private FTCrobot ftcRobot;
    private Timer timer2;
    private LinearOpModeCamera linearOpModeCamera;
    private Gyro myGyro;

    // Init
    public RasiActions(String rasiFilename, String[] rasiTag, LinearOpModeCamera myLinearOpModeCamera, Gamepad gamepad1, Gamepad gamepad2, Telemetry telemetry, HardwareMap hwMap){
        myGyro = new Gyro(hwMap);
        this.linearOpModeCamera = myLinearOpModeCamera;
        rasiParser = new RasiParser(rasiFilename, rasiTag);
        ftcRobot = new FTCrobot(hwMap, telemetry, gamepad1, gamepad2, myLinearOpModeCamera);
        ftcRobot.myCubeTray.setAutonomousMode(true) ;
    }

    // Run the rasi commands
    public void runRasi() throws InterruptedException {
        ftcRobot.myCubeTray.setZeroFromCompStart();
        rasiParser.loadNextCommand();
        while (!linearOpModeCamera.isStopRequested()) {
            Log.i("RasiActions", rasiParser.getParameter(0));
            switch (rasiParser.getParameter(0)) {
                case "turn":
                    ftcRobot.myDriveWithPID.turnRobot(rasiParser.getAsDouble(1));
                    break;
                case "drvd":
                    ftcRobot.myDriveWithPID.driveDist(rasiParser.getAsDouble(1), rasiParser.getAsDouble(2), rasiParser.getAsDouble(3));
                    break;
                case "drvultra":
                    break;
                case "drvt":
                    ftcRobot.myDriveWithPID.driveTime(rasiParser.getAsDouble(1), rasiParser.getAsDouble(2), rasiParser.getAsDouble(3));
                    break;
                case "intkl":
                    timer2 = new Timer(1.3);
                    ftcRobot.myRelicSystem.runToPosition(400);
                    while(!timer2.isDone()&&linearOpModeCamera.opModeIsActive()){}
                    ftcRobot.myRelicSystem.runToPosition(0);
                    break;
                case "intki":
                    ftcRobot.myManualIntakeController.RunIntake(0, -1);
                    break;
                case "intko":
                    ftcRobot.myManualIntakeController.RunIntake(0, 1);
                    break;
                case "intks":
                    ftcRobot.myManualIntakeController.RunIntake(0, 1);
                    ftcRobot.myManualIntakeController.RunIntake(0, 0);
                case "ctload":
                    timer2 = new Timer(1);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        ftcRobot.myCubeTray.setToPos(LiftFinalStates.LOADING);
                        ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "ctlow":
                    timer2 = new Timer(2.0);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        ftcRobot.myCubeTray.setToPos(LiftFinalStates.LOW);
                        ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "cthigh":
                    timer2 = new Timer(1.0);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        ftcRobot.myCubeTray.setToPos(LiftFinalStates.HIGH);
                        ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "ctmid":
                    timer2 = new Timer(1.0);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        ftcRobot.myCubeTray.setToPos(LiftFinalStates.MID);
                        ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "ctjwlc":
                    timer2 = new Timer(1.0);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        ftcRobot.myCubeTray.setToPos(LiftFinalStates.JEWELC);
                        ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "ctjwlr":
                    timer2 = new Timer(1);
                    ftcRobot.myCubeTray.setToPos(LiftFinalStates.JEWELR);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "ctjwll":
                    timer2 = new Timer(1);
                    ftcRobot.myCubeTray.setToPos(LiftFinalStates.JEWELL);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "wait":
                    timer2 = new Timer(rasiParser.getAsDouble(1));
                    while (!timer2.isDone()&&!linearOpModeCamera.isStopRequested()) {}
                    break;
                case "gyrolg":
                    myGyro.recordHeading();
                    break;
                case "jwlarmd":
                    ftcRobot.jewelKnocker.ArmInitialLower();
                    Log.i("RasiActions", "jwlarmd");
                    break;
                case "jwlarmu":
                    ftcRobot.jewelKnocker.ArmReturn();
                    Log.i("RasiActions", "jwlarmu");
                    break;
                case "jwlarmr":
                    ftcRobot.jewelKnocker.KnockerRight();
                    Log.i("RasiActions", "jwlarmr");
                    break;
                case "jwlarml":
                    ftcRobot.jewelKnocker.KnockerLeftStowed();
                    Log.i("RasiActions", "jwlarml");
                    break;
                case "jwlarmc":
                    ftcRobot.jewelKnocker.KnockerStartMove();
                    Log.i("RasiActions", "jwlarmc");
                    break;
                case "end":
                    linearOpModeCamera.requestOpModeStop();
                    while(linearOpModeCamera.opModeIsActive()){}
                    break;
                default:
                    break;
            }
            rasiParser.loadNextCommand();
        }
    }
}
