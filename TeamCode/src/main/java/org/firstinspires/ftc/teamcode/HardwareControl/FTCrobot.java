package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.infrastructure.controlParser;
import org.firstinspires.ftc.teamcode.opmodes.Swerve;

/**
 * Created by Vikesh on 11/22/2017.
 */

public class FTCrobot {
    private SwerveController driveSystem;
    private Gyro gyro;
    private controlParser opModeControl;
    private String[] currentCommand;
    private IntakeController intakeController;
    HardwareMap hwMap;

    public FTCrobot(HardwareMap hwmap){
        this.hwMap = hwmap;
        intakeController = new IntakeController(hwMap);
        this.driveSystem = new SwerveController(hwMap);
        this.gyro = new Gyro(hwMap);
    }
    public void runRASI(String filename){
        opModeControl = new controlParser(filename);
        int index = 0;
        while(index<opModeControl.commands.length) {
            currentCommand = opModeControl.getNextCommand();
            switch (currentCommand[0]) {
                case "drv":
                    driveSystem.pointDirection(false, Double.parseDouble(currentCommand[1]), Double.parseDouble(currentCommand[2]), Double.parseDouble(currentCommand[3]));
                    break;
                case "intk":
                    intakeController.runIntakeIn();
                    break;
                case "outk":
                    intakeController.runIntakeOut();
                    break;
                case "end":
                    index = opModeControl.commands.length;
            }
        }
    }
}
