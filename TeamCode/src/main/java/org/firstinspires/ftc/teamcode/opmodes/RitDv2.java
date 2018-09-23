package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Nonsense.AbstractDrivebase;
import org.firstinspires.ftc.teamcode.Nonsense.AbstractIntake;
import org.firstinspires.ftc.teamcode.Nonsense.AbstractScorer;
import org.firstinspires.ftc.teamcode.Nonsense.Dumper;
import org.firstinspires.ftc.teamcode.Nonsense.Intake;
import org.firstinspires.ftc.teamcode.Nonsense.Lift;
import org.firstinspires.ftc.teamcode.Nonsense.AbstractLift;
import org.firstinspires.ftc.teamcode.Nonsense.TankDrivebase;
@TeleOp(name = "RitDV2")
/**
 *
 * Implements Zach's Robot in three days code with seperate classes.
 * @author Cadence
 * @version 2.0
 * */
public class RitDv2 extends LinearOpMode {

    public void runOpMode(){
        // init
        Lift lift = new Lift("liftMotorA", "liftMotorB", hardwareMap);
        Intake intake = new Intake("ritkServo", "litkServo", "armMotor", "intakeMotor",  hardwareMap);
        TankDrivebase drivebase = new TankDrivebase("lMotorA", "lMotorB", "rMotorA", "rMotorB", hardwareMap);
        Dumper scorer = new Dumper("sortServo", "lDump", "rDump", hardwareMap);

        //Check that each class extends or implements an Abstract
        if(!validateClasses(drivebase, intake, lift, scorer)){
            throw new ArithmeticException("Invalid class");
        }
        waitForStart();

        while (opModeIsActive()){
            //Opmode

            //Driving
            drivebase.setLeftPow(gamepad1.left_stick_y);
            drivebase.setRightPow(gamepad1.right_stick_y);

            //Set lift music
            lift.setPower(-gamepad2.left_stick_y);
            intake.setArmMotor(-gamepad2.right_stick_y);

            if(gamepad2.left_bumper){
                scorer.dump(); //Release the cubes/balls
            }
            else{
                scorer.reset();
            }

            if(gamepad2.left_trigger > 0.2){
                scorer.setBeltSpeed(.85); //Turn sorter belt on.
            }
            else{scorer.setBeltSpeed(0.5);}

            if(gamepad2.y){intake.storeState();}
            else if(gamepad2.a) {intake.intakeState();}
            else if(gamepad2.x) {intake.transferState();}

            //Set the correct value for the intake
            if(gamepad2.right_bumper){
                intake.intakeOn();
            } else if(gamepad2.right_trigger > 0.2){
                intake.intakeOn(-gamepad2.right_trigger);
                } else {
                intake.intakeOff();
            }
        }
    }
    /**
     * Checks to make sure all classes are extending the abstract classes
     * */
    public boolean validateClasses(AbstractDrivebase drivebase, AbstractIntake intake, AbstractLift lift, AbstractScorer scorer){
        return true;
    }

}
