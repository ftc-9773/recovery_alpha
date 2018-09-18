package org.firstinspires.ftc.teamcode.Nonsense;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Nonsense.AbstractLift.liftStates;
import org.firstinspires.ftc.teamcode.Nonsense.AbstractLift;
import java.sql.Time;


/**
 * @author Cadence
 * @version 0.5
 *
 * A driver for the lift mechanism on the robot built in approximately 36 hours.
 * It should be used in conjunction with the Robot class in /Nonsense
 * */
public class Lift extends AbstractLift{
    DcMotor LeftLiftMotor;
    DcMotor RightLiftMotor;
    liftStates currState = liftStates.DOWN;

    public Lift(String LeftmotorName, String RightmotorName, HardwareMap hwmp){
        LeftLiftMotor = hwmp.dcMotor.get(LeftmotorName);
        RightLiftMotor = hwmp.dcMotor.get(RightmotorName);
    }
    /**
     * Used to leave the motors
     * */
    public void hang(){
        LeftLiftMotor.setPower(-1);
        RightLiftMotor.setPower(1);
    }
    public void drop() {
        float duration = 100; //100 milliseconds of lowering the lift.
        float startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < duration) {
            this.setPower(0.5);
            if (false) {/*If the robot has touched the ground and the hook has detaached, stop.*/}
        }
        this.GoDown();
    }
    /**
     * Sets the power of the motors.
     * @param pow double between -1 and 1, specifying the [voltage?] applied to each motor
     * */
    public void setPower(double pow){
        LeftLiftMotor.setPower(pow);
        RightLiftMotor.setPower(-pow);
    }

    public void setDeposit(){
        //To be implemented . . .
    }

    public void GoDown(){
        float time = System.currentTimeMillis();
        this.setPower(1);
        while(System.currentTimeMillis() - time < 300){}
        this.setPower(0);
        this.currState = liftStates.DOWN;
    }
}
