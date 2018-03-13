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

    // Init
    public RasiActions(String rasiFilename, String[] rasiTag, LinearOpModeCamera myLinearOpModeCamera, Gamepad gamepad1, Gamepad gamepad2, Telemetry telemetry, HardwareMap hwMap){
        Log.i("RasiAction", "Passing linearopmode");
        this.linearOpModeCamera = myLinearOpModeCamera;
        Log.i("RasiActions", "Building RasiParser");
        rasiParser = new RasiParser(rasiFilename, rasiTag);
        Log.i("RasiActions", "Building FTCRobot");
        ftcRobot = new FTCrobot(hwMap, telemetry, gamepad1, gamepad2, myLinearOpModeCamera);
        Log.i("RasiActions", "Setting CubeTray to auto mode");
        ftcRobot.myCubeTray.setAutonomousMode(true);
        ftcRobot.myCubeTray.setZeroFromCompStart();
        Log.i("RasiActions", "Done with Rasi init");
    }

    // Run the rasi commands
    public void runRasi() throws InterruptedException {
        ftcRobot.myCubeTray.setZeroFromCompStart();
        rasiParser.loadNextCommand();
        while (!linearOpModeCamera.isStopRequested()) {
            Log.i("RasiActions", "Started new loop!");

            Log.i("RasiActions", "Parameter - " + rasiParser.getParameter(0));
            switch (rasiParser.getParameter(0)) {
                case "addstuck":
                    if(ftcRobot.myDriveWithPID.isStuck()){
                        String[] temptags = rasiParser.rasiTag;
                        rasiParser.rasiTag = new String[temptags.length + 1];
                        for(int index = 0; index<rasiParser.rasiTag.length; index ++){
                            if (index < temptags.length){
                                rasiParser.rasiTag[index] = temptags[index];
                            }
                            else{
                                rasiParser.rasiTag[index] = "STUCK";
                            }
                        }
                        rasiParser.rasiTag[2] = "STUCK";
                    }
                    break;
                case "turn":
                    ftcRobot.myDriveWithPID.turnRobot(rasiParser.getAsDouble(1));
                    break;
                case "drvleftultra":
                    ftcRobot.myDriveWithPID.driveByLeftUltraonicDis(rasiParser.getAsDouble(1), rasiParser.getAsDouble(2));
                    break;
                case "drvstopintake":
                    ftcRobot.myDriveWithPID.driveDistStopIntake(rasiParser.getAsDouble(1), rasiParser.getAsDouble(2), rasiParser.getAsDouble(3), -1);
                    break;
                case "drvintake":
                    ftcRobot.myDriveWithPID.driveIntake(rasiParser.getAsDouble(1), rasiParser.getAsDouble(2), rasiParser.getAsDouble(3),-1, rasiParser.getAsDouble(4));
                    break;
                case "drvd":
                    ftcRobot.myDriveWithPID.driveDist(rasiParser.getAsDouble(1), rasiParser.getAsDouble(2), rasiParser.getAsDouble(3));
                    break;
                case "drvt":
                    ftcRobot.myDriveWithPID.driveTime(rasiParser.getAsDouble(1), rasiParser.getAsDouble(2), rasiParser.getAsDouble(3));
                    break;
                case "drvintkl":
                    ftcRobot.myDriveWithPID.driveLowerIntake(rasiParser.getAsDouble(1), rasiParser.getAsDouble(2), rasiParser.getAsDouble(3), -1);
                case "intkl":
                    timer2 = new Timer(0.75);
                    ftcRobot.myRelicSystem.runToPosition(400);
                    while (!timer2.isDone() && linearOpModeCamera.opModeIsActive()){}
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
                    break;
                case "ctload":
                    ftcRobot.myCubeTray.setToPos(LiftFinalStates.LOADING);
                    ftcRobot.myCubeTray.setToPos(LiftFinalStates.LOADING);
                    ftcRobot.myCubeTray.updatePosition();
                    break;
                case "ctlow":
                    ftcRobot.myCubeTray.setToPos(LiftFinalStates.LOW);
                    ftcRobot.myCubeTray.updatePosition();
                    break;
                case "cthigh":
                    ftcRobot.myCubeTray.setToPos(LiftFinalStates.HIGH);
                    ftcRobot.myCubeTray.updatePosition();
                    break;
                case "ctmid":
                    ftcRobot.myCubeTray.setToPos(LiftFinalStates.MID);
                    ftcRobot.myCubeTray.updatePosition();
                    break;
                case "ctrun":
                    timer2 = new Timer(rasiParser.getAsDouble(1));
                    while (!linearOpModeCamera.isStopRequested() && !timer2.isDone()) {
                        ftcRobot.myCubeTray.updatePosition();
                    }
                case "ctjwlc":
                    timer2 = new Timer(0.5);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        ftcRobot.myCubeTray.setToPos(LiftFinalStates.JEWELC);
                        ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "ctjwlr":
                    timer2 = new Timer(0.5);
                    ftcRobot.myCubeTray.setToPos(LiftFinalStates.JEWELR);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "ctjwll":
                    timer2 = new Timer(0.5);
                    ftcRobot.myCubeTray.setToPos(LiftFinalStates.JEWELL);
                    while(!linearOpModeCamera.isStopRequested()&&!timer2.isDone()) {
                        ftcRobot.myCubeTray.updatePosition();
                    }
                    break;
                case "ctout":
                        ftcRobot.myCubeTray.startDump();
                    break;
                case "ctstop":
                    ftcRobot.myCubeTray.dump();
                case "wait":
                    timer2 = new Timer(rasiParser.getAsDouble(1));
                    while (!timer2.isDone()&&!linearOpModeCamera.isStopRequested()) {ftcRobot.myCubeTray.updatePosition();}
                    break;
                case "gyrolg":
                    ftcRobot.myGyro.recordHeading();
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
