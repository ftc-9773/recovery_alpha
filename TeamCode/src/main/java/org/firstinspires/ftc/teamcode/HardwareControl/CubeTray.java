package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

/**
 * Created by zacharye on 11/12/17.
 */


public class CubeTray {
    public enum TrayPositions { STOWED, LOADING, CARRYING, DUMP_A, DUMP_B, NA}
    public enum LiftPositions {LOADING_HEIGHT, LOW, MID, HIGH, INBETWEEN }
    public enum LiftFinalStates {STOWED, LOADING, LOW, MID, HIGH, }
    public boolean homing = false;

    // Json setup
    private SafeJsonReader myServoPositions;

    // create state machines
    public TrayPositions trayState = TrayPositions.STOWED;
    public LiftPositions liftState = LiftPositions.MID;
    public LiftFinalStates liftFinalState  = LiftFinalStates.STOWED;
    public boolean StillNeedsUpdating ;

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

    // servo position variables for testing
    public double leftFlapPos ;
    public double rightFlapPos;
    public double leftAnglePos;
    public double rightAnglePos;

    //TESTING variables
    private boolean TESTING = false;
    private Gamepad gamepad2 ;
    private static final double increment =.001;

    private int zeroPos = 0;

    // setup variables for positioning
    private static final int sensorPosTicks = 3415;
    private static final int topPosTicks = 3260;
    private static final int middlePosTicks = 1850;
    private static final int bottomPosTicks = 450;
    private static final int loadPosTicks = 190;

    private static final int positionTolerance = 40;

    //TODO: migrate to a JSON config file for easy config

    //define servo positions - each array of postitons go in the following order:
    // stowed, loading, carrying, dumpingFlap, dumpingAngleAndFlap

    private static final double[] leftFlapPositions = {0.532, 0.5328, 0.35, 0.73, 0.73 } ;
    private static final double[] rightFlapPostions = {.581, .581, 0.78, 0.4,1,1 } ;
    private static final double[] leftAnglePostions = {0.885, 0.554, 0.169, 0.169, 0.039} ;  // dump used to be .039
    private static final double[] rightAnglePostions = {0.07, 0.350, 0.754, 0.754,0.901 } ;   // dump used to be .901

    public CubeTray(HardwareMap hwMap, Gamepad gamepad1){  // constructor takes hardware map

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
        myServoPositions = new SafeJsonReader("CubeTrayServoPositions");
    }
    public CubeTray(HardwareMap hwMap, Gamepad gamepad1, Gamepad gamepad2){  // constructor takes hardware map
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
    /// DEAFAULT INTERFACE
    public void updateFromGamepad(){
        if(gamepad1.x){
            liftFinalState = LiftFinalStates.LOADING;
        } else if (gamepad1.a){
            liftFinalState = liftFinalState.LOW;
        } else if (gamepad1.b){
            liftFinalState = liftFinalState.MID;
        } else if(gamepad1.y){
            liftFinalState = liftFinalState.HIGH;
        }
        if(gamepad1.right_bumper){
            setServoPos(TrayPositions.DUMP_A);
        }
        updatePosition();
    }
    public void updatePosition(){
        if ( trayState.equals(TrayPositions.STOWED)&& !liftFinalState.equals(LiftFinalStates.STOWED)){
            liftMotor.setTargetPosition(bottomPosTicks);
            setToPosNum(1);
            if(readLiftState().equals(LiftPositions.LOADING_HEIGHT)){
                trayState = TrayPositions.LOADING;
            }
        }
        if (homing){
            return;
        }
        switch (readLiftState()){
            case INBETWEEN:
                // leave as is
            case LOW:
                switch (liftFinalState){
                    case STOWED:
                        liftMotor.setTargetPosition(middlePosTicks);
                        setServoPos(TrayPositions.LOADING);
                        break;
                    case MID:
                        liftMotor.setTargetPosition(middlePosTicks);
                        setServoPos(TrayPositions.CARRYING);
                        break;
                    case HIGH:
                        liftMotor.setTargetPosition(topPosTicks);
                        setServoPos(TrayPositions.CARRYING);
                        break;
                    case LOADING:
                        liftMotor.setTargetPosition(loadPosTicks);
                        setServoPos(TrayPositions.LOADING);
                        break;
                    default:
                        break;

                }
                break;
            case MID:
                switch (liftFinalState) {
                    case LOADING:
                        setServoPos(TrayPositions.CARRYING);
                        liftMotor.setTargetPosition(bottomPosTicks);
                        break;
                    case LOW:
                        setServoPos(TrayPositions.CARRYING);
                        liftMotor.setTargetPosition(bottomPosTicks);
                        break;
                    case HIGH:
                        setServoPos(TrayPositions.CARRYING);
                        liftMotor.setTargetPosition(topPosTicks);
                        break;
                    case MID:
                        break;
                    case STOWED:
                        setServoPos(TrayPositions.STOWED);
                        liftMotor.setTargetPosition(topPosTicks);
                    default:
                        break;
                }
                break;

            case HIGH:
                switch (liftFinalState) {
                    case LOADING:
                        setServoPos(TrayPositions.CARRYING);
                        liftMotor.setTargetPosition(bottomPosTicks);
                        break;
                    case LOW:
                        setServoPos(TrayPositions.CARRYING);
                        liftMotor.setTargetPosition(bottomPosTicks);
                        break;

                    case MID:
                        setServoPos(TrayPositions.CARRYING);
                        liftMotor.setTargetPosition(bottomPosTicks);
                        break;
                    case HIGH:
                        break;
                    default:
                        break;

                }
                break;

            case LOADING_HEIGHT:
                switch (liftFinalState) {
                    case LOW:
                        setServoPos(TrayPositions.CARRYING);
                        liftMotor.setTargetPosition(bottomPosTicks);
                        break;
                    case MID:
                        setServoPos(TrayPositions.CARRYING);
                        liftMotor.setTargetPosition(bottomPosTicks);
                        break;

                    case HIGH:
                        setServoPos(TrayPositions.CARRYING);
                        liftMotor.setTargetPosition(bottomPosTicks);
                        break;

                    case LOADING:
                        break;
                    default:
                        break;
                }
                break;
            default:
                Log.d("CubeTray", "updatePosition: no case used");
                break;
        }
    }



    /////////////////////////////////////// ______ UTIL __

    /// Lift motor util functions
    public LiftPositions readLiftState(){
        int liftTics = getliftPos();
        if (Math.abs(liftTics-topPosTicks) < positionTolerance ){
            liftState = LiftPositions.HIGH ;
        } else  if ( liftTics< topPosTicks - positionTolerance && liftTics> bottomPosTicks+positionTolerance) {
            liftState = LiftPositions.MID;
        } else if (Math.abs(liftTics-bottomPosTicks)< positionTolerance){
            liftState = LiftPositions.LOW;
        } else if (Math.abs(liftTics-loadPosTicks)< positionTolerance) {
            liftState = LiftPositions.LOADING_HEIGHT;
        } else if ( liftTics < sensorPosTicks + positionTolerance && liftTics > bottomPosTicks-positionTolerance) {
            liftState = LiftPositions.INBETWEEN;
        }   else {
            Log.d("Lift State Reader", "Not able to read Lift position");
        }
        return liftState ;
    }


    // resets the zero position of the lift
    public void setLiftZeroPos(){
        zeroPos = liftMotor.getCurrentPosition() - sensorPosTicks;
    }
    // returns the adjusted lift position
    public int getliftPos(){
        return getRawLiftPos() - zeroPos;
    }
    // retruns the unscaled lift position
    public int getRawLiftPos() {
        int rawPos = liftMotor.getCurrentPosition();
        return rawPos ;
    }
    // returns a boolean value if the limit switch is pressed
    private boolean limitSwitchIsPressed() {
        return limitSwitch.getVoltage() > 1.5;
    }

    /// servo util functions

    public void updateServos() {
        leftFlap.setPosition(leftFlapPos);
        rightFlap.setPosition(rightFlapPos);
        leftAngle.setPosition(leftAnglePos);
        rightAngle.setPosition(rightAnglePos);
    }
    public void setServoPos(TrayPositions trayPos) {
        int posNum = -1;
        trayState = trayPos ; // update TrayState
        switch (trayPos) {
            case STOWED:
                posNum = 0;
                break;
            case LOADING:
                posNum = 1;
                break;
            case CARRYING:
                posNum = 2;
                break;
            case DUMP_A:
                posNum = 3;
                break;
            default:
                break;
        }
        if (posNum == -1) return;
            setToPosNum(posNum);
    }
    private void setToPosNum(int posNum){
        // set the servos to their positions using the positions array
        leftFlapPos = leftFlapPositions[posNum];
        rightFlapPos = rightFlapPostions[posNum];
        leftAnglePos = leftAnglePostions[posNum];
        rightAnglePos = rightAnglePostions[posNum];
    }
    public void homeLift () {
        if (trayState == TrayPositions.LOADING){ // lift cannot be in loading pos to start
            setServoPos(TrayPositions.DUMP_A);                      // moves tray out of load to carry position
        }
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // move the lift slowly upwards
        liftMotor.setPower (.5);
        homing = true;
    }
    private boolean testIfTop(){
        if (limitSwitchIsPressed()) {
            liftMotor.setPower(0);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);   // stop motor and set reference position
            setLiftZeroPos();
            return true;
        } else return false;
    }    //////////////////////////////////////////// _____ Testing ____
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

}
