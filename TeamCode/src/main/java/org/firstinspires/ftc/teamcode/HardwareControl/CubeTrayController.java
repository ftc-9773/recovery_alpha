package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by zacharye on 11/12/17.
 */


public class CubeTrayController {
    // Define states for state machine logic
    public enum TrayPositions { STOWED, LOADING, CARRYING, DUMP_A, DUMP_B, NA}
    public enum LiftPositions {LOADING_HEIGHT, LOW, MID, HIGH, HOMING }


    public TrayPositions trayState;
    public LiftPositions liftState;
    public LiftPositions liftExpectedState;
    // define limit switch
    private AnalogInput limitSwitch;
    // define servos & motor for the tray
    private Servo leftFlap  ;
    private Servo rightFlap ;
    private Servo leftAngle ;
    private Servo rightAngle ;
    private DcMotor liftMotor ;
    // define gamepad value
    private Gamepad gamepad1;

    private double liftMotorPower = 0;
    private int liftTargetPosition ;


    // positions for servos - used for adjustments
    public double leftFlapPos ;
    public double rightFlapPos;
    public double leftAnglePos;
    public double rightAnglePos;

    //TESTING variables
    private boolean TESTING = false;
    private Gamepad gamepad2 ;
    private static final double increment =.001;

    //
    private int zeroPos = 0;

    // setup variables for positioning
    private static final int sensorPosTicks = 1570;
    private static final int topPosTicks = 1500;
    private static final int bottomPosTicks = 40;
    private static final int loadPosTicks = 10;

    private static final int positionTolerance = 15;




    //TODO: migrate to a JSON config file for easy config

    //define servo positions - each array of postitons go in the following order:
    // stowed, loading, carrying, dumpingFlap, dumpingAngleAndFlap

    private static final double[] leftFlapPositions = {0.532, 0.5328, 0.35, 0.73, 0.73 } ;
    private static final double[] rightFlapPostions = {.581, .581, 0.78, 0.4,1,1 } ;
    private static final double[] leftAnglePostions = {0.885, 0.554, 0.169, 0.169, 0.039} ;  // dump used to be .039
    private static final double[] rightAnglePostions = {0.07, 0.350, 0.754, 0.754,0.901 } ;   // dump used to be .901

    public CubeTrayController(HardwareMap hwMap, Gamepad gamepad1){  // constructor takes hardware map

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

        limitSwitch = hwMap.analogInput.get("limitSwitch");


        // TODO: initialise  JSON config file etc

    }
    // testing harware map consturctor - not for regular use
    public CubeTrayController(HardwareMap hwMap, Gamepad gamepad1, Gamepad gamepad2){  // constructor takes hardware map
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

        //TESTIN
        this.gamepad2 = gamepad2;
        TESTING = true;
    }

    public void updateFromGamepad() {
        // set motor movement

        // liftMotorPower = -1*gamepad1.left_stick_y; // old
        liftTargetPosition += 10*gamepad1.left_stick_y ;
        // set tray pos based on gamepad values
        if (gamepad1.a){
            goToCarryPosLow();
        } else if (gamepad1.b){
            goToDumpPosFlaps();
        } else if (gamepad1.y){
            goToCarryPosHigh();
            //  waitingForDump = true;
            //  dumpStartTime = System.currentTimeMillis();
        }else if (gamepad1.x){
            goToLoadPos();
        }

        updateServos();
         testIfTop();

        // testing - not activated if testing is not enabled
        // allows user to tweak the servo values
        if (TESTING) {
            RunServoAdjustmentPotocol();
        }
        if(liftExpectedState.equals(LiftPositions.HOMING)) {
            liftMotor.setPower(liftMotorPower);
        }

    }

    // create commands to move the servos between the 4 preset positions
    public void goToStowPos(){

        setToPos(0);
        trayState = TrayPositions.STOWED;
    }
    public void goToLoadPos(){
        setToPos(1);
        trayState = TrayPositions.LOADING;

    }
    public void goToCarryPosLow(){
        setToPos(2);
        trayState = TrayPositions.CARRYING;
        goToLiftPos(LiftPositions.LOW);
    }
    public void goToCarryPosHigh(){
        setToPos(2);
        trayState = TrayPositions.CARRYING;
        goToLiftPos(LiftPositions.LOW);
    }
    public void goToDumpPosFlaps(){
        setToPos(3);
        trayState = TrayPositions.DUMP_A;

    }
    public void goToDumpPosAngle() {
        setToPos(4);
        trayState = TrayPositions.DUMP_B;

    }

    public void homeLift () {
        if (trayState == TrayPositions.LOADING){ // lift cannot be in loading pos to start
            goToDumpPosFlaps();                      // moves tray out of load to carry position
        }
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // move the lift slowly upwards
        liftMotor.setPower (.5);
        liftExpectedState = LiftPositions.HOMING;
    }
    private boolean testIfTop(){
        if (limitSwitchIsPressed()) {
            liftMotor.setPower(0);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);   // stop motor and set reference position
            setLiftZeroPos();
            return true;
        } else return false;
    }
    public LiftPositions getLiftState() {
        int liftTics = getliftPos();
        if (Math.abs(liftTics-topPosTicks) < positionTolerance ){
            liftState = LiftPositions.HIGH ;
        } else  if ( liftTics< topPosTicks - positionTolerance && liftTics> bottomPosTicks+positionTolerance) {
            liftState = LiftPositions.MID;
        } else if (Math.abs(liftTics-bottomPosTicks)< positionTolerance){
            liftState = LiftPositions.LOW;
        } else if (Math.abs(liftTics-loadPosTicks)< positionTolerance) {
            liftState = LiftPositions.LOADING_HEIGHT;
        } else {
            Log.d("Lift State Reader", "Not able to read Lift position");
        }
        return liftState ;
    }
    public void goToLiftPos(LiftPositions targetPos){
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if (liftExpectedState.equals(liftState.HOMING)) {
            return;
        }
        switch(targetPos){
            case LOW :
                liftTargetPosition = bottomPosTicks;

            case HIGH :
                liftTargetPosition = topPosTicks;

            case LOADING_HEIGHT :
                liftTargetPosition = loadPosTicks;
            default :
        }
         liftMotor.setTargetPosition(liftTargetPosition);
    }


    public void setLiftZeroPos(){
        zeroPos = liftMotor.getCurrentPosition() - sensorPosTicks;
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
    private void RunServoAdjustmentPotocol(){
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
    private boolean limitSwitchIsPressed(){
        return limitSwitch.getVoltage() > 1.5 ;
    }
}
