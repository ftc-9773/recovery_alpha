package org.firstinspires.ftc.teamcode.Nonsense;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * Implements abstractScorer for the Robot in 3 days scorer
 * @author Cadence
 * @version 1.0
 * */
public class Dumper extends AbstractScorer {
    Servo Belt, Left, Right;
    HardwareMap hwmp;

    /**
     * @param BeltName the name of the servo that controls the belt which moves the cubes along.
     * @param LeftDump the name of the servo that controls the left dumper
     * @param RightDump the name of the servo that controls the right dumper
     * @param hwmp HardwareMap object created by the opmode for interfacing with the RevHub
     */
    public Dumper(String BeltName, String LeftDump, String RightDump, HardwareMap hwmp){
        this.hwmp = hwmp;
        this.Belt = hwmp.servo.get(BeltName);
        this.Left = hwmp.servo.get(LeftDump);
        this.Right = hwmp.servo.get(RightDump);
    }
    /**
     * Set the servos to the postion needed to dump cubes and balls
     * */
    public void dump(){
        this.Left.setPosition(0.75);
        this.Right.setPosition(0.5);
    }
    /**
     * Incomplete function (Nonfunctional) that dumps cubes and balls, then resets the dumper
     * */
    public void score(){
        this.dump();
        //Thread.sleep(100);
        this.reset();
    }
    /**
     * Resets the position of the dumping motors
     * */
    public void reset(){
        //servos are opposite of each other
        this.Left.setPosition(0.0);
        this.Right.setPosition(0.75);
    }
    /**
     * Adjusts the speed of the belt that moves the cubes along
     */
    public void setBeltSpeed(double pow){
        Belt.setPosition(pow);
    }
}