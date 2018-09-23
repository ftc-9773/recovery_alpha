package org.firstinspires.ftc.teamcode.Nonsense;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class Dumper extends AbstractScorer {
    Servo Belt, Left, Right;
    HardwareMap hwmp;


    public Dumper(String BeltName, String LeftDump, String RightDump, HardwareMap hwmp){
        this.hwmp = hwmp;
        this.Belt = hwmp.servo.get(BeltName);
        this.Left = hwmp.servo.get(LeftDump);
        this.Right = hwmp.servo.get(RightDump);
    }
    public void dump(){
        this.Left.setPosition(0.75);
        this.Right.setPosition(0.5);
    }
    public void score(){
        this.dump();
        //Thread.sleep(100);
        this.reset();
    }
    public void reset(){
        //servos are opposite
        this.Left.setPosition(0.0);
        this.Right.setPosition(0.75);
    }
    public void setBeltSpeed(double pow){
        Belt.setPosition(pow);
    }
}
