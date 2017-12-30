package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTray;
import org.firstinspires.ftc.teamcode.HardwareControl.IntakeControllerManual;
import org.firstinspires.ftc.teamcode.HardwareControl.RelicSystem;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.Vision.JewelDetector;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;
import org.firstinspires.ftc.teamcode.sample_camera_opmodes.LinearDetectColor;

/**
 * Created by vikesh on 12/28/17.
 */
@Autonomous(name = "Auto Red")
public class AutonomousRed extends LinearOpModeCamera{

    JewelDetector myJewelDetector = new JewelDetector(this);
    IntakeControllerManual myIntakeControllerManual;
    SwerveController mySwerveController;
    CubeTray myCubeTray;
    Gyro myGyro;
    SafeJsonReader mySafeJsonReader = new SafeJsonReader("auto_red_parameters");
    RelicSystem myRelicSystem;

    char liftState;
    char autonomouspath;
    long[] times = {0,0,0,0};
    boolean intakeIsLowered = false;

    int intakeLowerTime;
    int intakeStartDelay;
    int intakeRunDuration;

    String colorString;
    String markString;
    RelicRecoveryVuMark mark;
    VumarkGlyphPattern pattern;
    @Override
    public void runOpMode() throws InterruptedException {
        //init:
        pattern = new VumarkGlyphPattern(hardwareMap);
        myGyro = new Gyro(hardwareMap);
        myIntakeControllerManual = new IntakeControllerManual(hardwareMap);
        mySwerveController = new SwerveController(hardwareMap, myGyro, telemetry);
        myCubeTray = new CubeTray(hardwareMap, gamepad1, gamepad2);
        autonomouspath = chooseAutonomousPath(true, (char)1);
        myCubeTray.setZeroFromCompStart();

        //play:
        waitForStart();

        myJewelDetector.startCamera();
        mark = pattern.getColumn();
        markString = mark.toString();

        telemetry.addData("Mark", markString);

        intakeLowerTime = mySafeJsonReader.getInt("intakeLowerTime");
        intakeRunDuration = mySafeJsonReader.getInt("intakeRunDuration");
        intakeStartDelay = mySafeJsonReader.getInt("intakeStartDelay");

        times[0] = System.currentTimeMillis();
        while(opModeIsActive() && System.currentTimeMillis()-intakeLowerTime<times[0]){
            myIntakeControllerManual.lowerIntake(true);
            myCubeTray.setToPos(CubeTray.LiftFinalStates.LOADING);
            myCubeTray.updatePosition();
        }
        times[1] = System.currentTimeMillis();
        myIntakeControllerManual.lowerIntake(false);
        while(System.currentTimeMillis()-intakeStartDelay<times[1]){}
        times[1] = System.currentTimeMillis();
        while(opModeIsActive()  && System.currentTimeMillis()-(intakeRunDuration)<times[1]){
            myIntakeControllerManual.RunIntake(0,-1);
        }
        myIntakeControllerManual.RunIntake(0,1);
        myIntakeControllerManual.RunIntake(0,0);
    }

    public char chooseAutonomousPath(boolean redJewelIsLeft, char glyphPosition){
        char autonomousPath;
        switch (glyphPosition){
            case 0:
                if(redJewelIsLeft){autonomousPath = 0;}
                else{autonomousPath = 1;}
                break;
            case 1:
                if(redJewelIsLeft){autonomousPath = 2;}
                else{autonomousPath = 3;}
                break;
            case 2:
                if(redJewelIsLeft){autonomousPath = 4;}
                else{autonomousPath = 5;}
                break;
            default:
                autonomousPath = 6;
                break;
        }
        return autonomousPath;
    }

}
