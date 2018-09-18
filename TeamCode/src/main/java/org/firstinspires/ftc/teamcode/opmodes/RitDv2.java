package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Nonsense.AbstractDrivebase;
import org.firstinspires.ftc.teamcode.Nonsense.AbstractIntake;
import org.firstinspires.ftc.teamcode.Nonsense.Dumper;
import org.firstinspires.ftc.teamcode.Nonsense.Intake;
import org.firstinspires.ftc.teamcode.Nonsense.Lift;
import org.firstinspires.ftc.teamcode.Nonsense.AbstractLift;
import org.firstinspires.ftc.teamcode.Nonsense.TankDrivebase;

public class RitDv2 extends LinearOpMode {


    public void runOpMode(){
        // init
        Lift lift = new Lift("liftMotorA", "liftMotorB", hardwareMap);
        Intake intake = new Intake("ritkServo", "litkServor", "armMotor", hardwareMap);
        TankDrivebase drivebase = new TankDrivebase("lMotorA", "lMotorB", "rMotorA", "rMotorB", hardwareMap);
        Dumper scorer = new Dumper("sortServo", "ldump", "rdump", hardwareMap);


        if(!validateClasses()){
            throw new ArithmeticException("Invalid class");
        }
        waitForStart();

        while (opModeIsActive()){
            //Opmode
            drivebase.setRightPow(gamepad1.left_stick_y);
            drivebase.setLeftPow(gamepad1.right_stick_y);

            lift.setPower(gamepad2.left_stick_y);
            intake.setArmMotor(-gamepad2.right_stick_y);

            if(gamepad2.left_bumper){
                scorer.dump();
            }
            else{
                scorer.reset();
            }

            if(gamepad2.left_trigger > 0.2){
                scorer.setBeltSpeed(.85);
            }
            else{scorer.setBeltSpeed(0.5);}

            if(gamepad2.y){intake.storeState();}
            else if(gamepad2.a) {intake.intakeState();}
            else if(gamepad2.x) {intake.transferState();}

            //Set the correct value for the intake
            if(gamepad2.right_bumper){
                intake.intakeOn(0.75);
            } else if(gamepad2.right_trigger > 0.2){
                intake.intakeOn(-gamepad2.right_trigger);
            } else {
                intake.intakeOff();
            }
        }
    }

    public boolean validateClasses(){
        return true;
    }

}