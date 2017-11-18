package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by zacharye on 11/12/17.
 */


public class CubeTray {

    // define servos & motor forthe tray
    private Servo leftFlap  ;
    private Servo rightFlap ;
    private Servo leftAngle ;
    private Servo rightAngle ;
    private DcMotor liftMotor ;
    // define gamepad value
    private Gamepad gamepad1;

    // class variables
    //  variables used for dump wait
    private boolean waitingForDump = false;
    private long  dumpStartTime ;
    final private int dumpWaitTime = 500; // time in miliseconds to wait untill 2nd part of dump occurs
    // positions for servos - used for adjustments
    public double leftFlapPos ;
    public double rightFlapPos;
    public double leftAnglePos;
    public double rightAnglePos ;


    //TESTING variables
    private boolean TESTING = false;
    private Gamepad gamepad2 ;
    private static final double increment =.001;

    //
    private int zeroPos = 0;



    //TODO: migrate to a JSON config file for easy config

    //define servo positions - each array of postitons go in the following order:
    // stowed, loading, carrying, dumpingFlap, dumpingAngleAndFlap

    private static final double[] leftFlapPositions = {0.532, 0.5328, 0.35, 0.73, 0.73 } ;
    private static final double[] rightFlapPostions = {.581, .581, 0.78, 0.4,1,1 } ;
    private static final double[] leftAnglePostions = {0.885, 0.554, 0.169, 0.169, 0.039} ;  // dump used to be .039
    private static final double[] rightAnglePostions = {0.07, 0.350, 0.754, 0.754,0.901 } ;   // dump used to be .901

        public CubeTray (HardwareMap hwMap, Gamepad gamepad1){  // constructor takes hardware map

            // attach all the servos to their hardware map components
            leftFlap = hwMap.servo.get("ctlfServo");
            rightFlap = hwMap.servo.get("ctrfServo");
            leftAngle = hwMap.servo.get("ctlaServo");
            rightAngle = hwMap.servo.get("ctraServo");
            // passes gamepad, instead of gamepad values for ease of use
            this.gamepad1 = gamepad1 ;
            this.gamepad2 = gamepad2;
            // attach DC lift motor
            liftMotor = hwMap.dcMotor.get("ctlMotor");

            // TODO: initialise  JSON config file etc

        }
    // testing harware map consturctor - not for regular use
    public CubeTray (HardwareMap hwMap, Gamepad gamepad1,Gamepad gamepad2){  // constructor takes hardware map
        // attach all the servos to their hardware map components
        leftFlap = hwMap.servo.get("ctlfServo");
        rightFlap = hwMap.servo.get("ctrfServo");
        leftAngle = hwMap.servo.get("ctlaServo");
        rightAngle = hwMap.servo.get("ctraServo");
        // passes gamepad, instead of gamepad values for ease of use
        this.gamepad1 = gamepad1 ;
        this.gamepad2 = gamepad2;
        // attach DC lift motor
        liftMotor = hwMap.dcMotor.get("ctlMotor");

        // TODO: initialise  JSON config file etc

        //TESTING
        this.gamepad2 = gamepad2;
        TESTING = true;
    }

    public void updateFromGamepad() {
        // set motor movement
        liftMotor.setPower(-1*gamepad1.left_stick_y);
        // set tray pos based on gamepad values
        if (gamepad1.a){
            goToLoadPos();
        } else if (gamepad1.b){
            goToCarryPos();
        } else if (gamepad1.y){
            goToDumpPosFlaps();
          //  waitingForDump = true;
          //  dumpStartTime = System.currentTimeMillis();
        }else if (gamepad1.x){
            goToStowPos();

        }
        //if (System.currentTimeMillis()-dumpStartTime >= dumpWaitTime && waitingForDump) {
         //   goToDumpPosAngle();
         //   waitingForDump = false;
        //}

        // testing
        if (TESTING) {
            if (gamepad2.a) {
                leftFlapPos -= increment;
                rightFlapPos += increment;
            } else if (gamepad2.y) {
                leftFlapPos += increment;
                rightFlapPos -= increment;
            }
            if (gamepad2.b) {
                leftAnglePos -= increment;
               rightAnglePos += increment;
            } else if (gamepad2.x) {
                leftAnglePos += increment;
                rightAnglePos -= increment;
            }
        }
        updateServos();
    }

    // create commands to move the servos between the 4 preset positions
    public void goToStowPos(){
        setToPos(0);
    }
    public void goToLoadPos(){
        setToPos(1);
    }
    public void goToCarryPos(){
        setToPos(2);
    }
    public void goToDumpPosFlaps(){
        setToPos(3);
    }
    public void goToDumpPosAngle() {
        setToPos(4);
    }

    public void resetLiftPos(){
        zeroPos = liftMotor.getCurrentPosition();
    }
    public int getliftPos(){
        return getRawLiftPos() - zeroPos;
    }
    public int getRawLiftPos() {
        int rawPos = liftMotor.getCurrentPosition();
        return rawPos ;
    }

    // a utility function to allow me to set positions more easily
    private void setToPos(int posNum){

        // set the servos to their positions using the positions array

        leftFlapPos = leftFlapPositions[posNum];
        rightFlapPos =rightFlapPostions[posNum] ;
        leftAnglePos = leftAnglePostions[posNum];
        rightAnglePos = rightAnglePostions[posNum];

    }
    public void updateServos() {
        leftFlap.setPosition(leftFlapPos);
        rightFlap.setPosition(rightFlapPos);
        leftAngle.setPosition(leftAnglePos);
        rightAngle.setPosition(rightAnglePos);
    }


}
