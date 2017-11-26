package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.infrastructure.PIDController;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

/**
 * Created by zacharye on 11/12/17.
 */


public class CubeTray {
    public enum TrayPositions {STOWED, LOADING, CARRYING, DUMP_A}
    public enum OverallStates {LOADING, CARRY, STOWED, TO_LOADING, FROM_STOWED, TO_CARRY}
    public enum LiftFinalStates {STOWED, LOADING, LOW, MID, HIGH}
    public boolean homing = false;

    // Json setup
    private SafeJsonReader myCubeTrayPositions;

    // create state machines
    // TODO: make this initialised or automated for start
    public TrayPositions trayState = TrayPositions.CARRYING;
    public OverallStates overallState = OverallStates.CARRY;
    public LiftFinalStates liftFinalState  = LiftFinalStates.HIGH;
    public long transitionTimer ;
    private static int trayUpTime = 250; // time in miliseconds given to angle up

    // define limit switch
    private AnalogInput limitSwitch;
    // define servos & motor for the tray
    private Servo leftFlap  ;
    private Servo rightFlap ;
    private Servo leftAngle ;
    private Servo rightAngle ;
    public DcMotor liftMotor ;
    // define gamepad value
    private Gamepad gamepad1;

    private double liftMotorPower = 0;
    public int liftTargetPosition = 0;  // change to private
    //DEBUGING
    private static final boolean DEBUG = true;
    private static final String TAG = "ftc9773 CubeTray" ;

    // servo position variables for testing
    public double leftFlapPos ;
    public double rightFlapPos;
    public double leftAnglePos;
    public double rightAnglePos;

    //TESTING variables
    private boolean TESTING = false;
    private Gamepad gamepad2 ;
    private static final double increment =.001;
    //HOMING variables
    private int zeroPos = 0;

    // setup variables for positioning
    private static int sensorPosTicks = 3615;
    private static int topPosTicks = 3260;
    private static int middlePosTicks = 1850;
    private static int bottomPosTicks = 450;
    private static int loadPosTicks = 190;

    private static final int positionTolerance = 40;

    private PIDController liftHeightPidController;

    //TODO: migrate to a JSON config file for easy config

    //define servo positions - each array of postitons go in the following order:
    // stowed, loading, carrying, dumpingFlap, dumpingAngleAndFlap

    private static final double[] leftFlapPositions = {0.532, 0.5328, 0.35, 0.73, 0.73 } ;
    private static final double[] rightFlapPostions = {.581, .581, 0.78, 0.4,1,1 } ;
    private static final double[] leftAnglePostions = {0.885, 0.554, 0.169, 0.169, 0.039} ;  // dump used to be .039
    private static final double[] rightAnglePostions = {0.07, 0.350, 0.754, 0.754,0.901 } ;   // dump used to be .901


    public CubeTray(HardwareMap hwMap, Gamepad gamepad1, Gamepad gamepad2){  // constructor takes hardware map
        // attach all the servos to their hardware map components
        leftFlap = hwMap.servo.get("ctlfServo");
        rightFlap = hwMap.servo.get("ctrfServo");
        leftAngle = hwMap.servo.get("ctlaServo");
        rightAngle = hwMap.servo.get("ctraServo");
        // passes gamepad, instead of gamepad values for ease of use
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;

        // attach DC lift motor
        liftMotor = hwMap.dcMotor.get("ctlMotor");
        limitSwitch = hwMap.analogInput.get("ctlLimitSwitch");

        // TODO: initialise  JSON config file etc
        sensorPosTicks = myCubeTrayPositions.getInt("sensorPos");
        topPosTicks = myCubeTrayPositions.getInt("topPos");
        middlePosTicks = myCubeTrayPositions.getInt("middlePos");
        bottomPosTicks = myCubeTrayPositions.getInt("bottomPos");
        loadPosTicks = myCubeTrayPositions.getInt("loadPos");
        //todo: finish tuning PID

        // setup PID for lift
        Double kp = myCubeTrayPositions.getDouble("liftHeightP");
        Double ki = myCubeTrayPositions.getDouble("liftHeightI");
        Double kd = myCubeTrayPositions.getDouble("liftHeightD");
        liftHeightPidController = new PIDController(kp,ki,kd);


        //TESTING
        if (gamepad2!= null ){
            TESTING = true;
        }
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

    private void updateOverallState(){
        if(!liftMotor.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION)){
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        switch (liftFinalState) {
            case LOW:
                if(overallState == OverallStates.STOWED) {
                    overallState = OverallStates.FROM_STOWED;
                } else if (!overallState.equals(OverallStates.CARRY)&&!overallState.equals(OverallStates.TO_CARRY)) {
                    overallState = OverallStates.TO_CARRY;
                    transitionTimer = System.currentTimeMillis();
                }
                break;

            case MID:
                if(overallState == OverallStates.STOWED) {
                    overallState = OverallStates.FROM_STOWED;
                } else if (!overallState.equals(OverallStates.CARRY)&&!overallState.equals(OverallStates.TO_CARRY)) {
                    overallState = OverallStates.TO_CARRY;
                    transitionTimer = System.currentTimeMillis();
                }
                break;

            case HIGH:
                if(overallState == OverallStates.STOWED) {
                    overallState = OverallStates.FROM_STOWED;
                } else if (!overallState.equals(OverallStates.CARRY)&&!overallState.equals(OverallStates.TO_CARRY)) {
                    overallState = OverallStates.TO_CARRY;
                    transitionTimer = System.currentTimeMillis();
                }
                break;
            case LOADING:
                if(overallState == OverallStates.STOWED) {
                    overallState = OverallStates.FROM_STOWED;
                } else if (!overallState.equals(OverallStates.LOADING)&&!overallState.equals(OverallStates.TO_LOADING)) {
                    overallState = OverallStates.TO_LOADING;
                }
                break;

            default:
                break;
        }

    }
    public void updatePosition(){
        updateOverallState();
        switch (overallState) {
            case LOADING:
                liftTargetPosition = loadPosTicks;
                setServoPos(TrayPositions.LOADING);
                break;
            case CARRY:
                switch (liftFinalState){
                    case LOW:
                        liftTargetPosition = bottomPosTicks;
                        break;
                    case MID:
                        liftTargetPosition = middlePosTicks;
                        break;
                    case HIGH:
                        liftTargetPosition = topPosTicks;
                        break;
                    default:
                        break;
                }
                break;
            case STOWED:
                break;
            case TO_LOADING:
                // if the lift is above the threshold, set target to be threshold
                if (liftMotor.getCurrentPosition() >= bottomPosTicks + positionTolerance) {
                    liftTargetPosition = loadPosTicks;
                    setServoPos(TrayPositions.CARRYING);
                } else {
                    // otherwise, set to loadingState
                    overallState = OverallStates.LOADING;
                    liftTargetPosition = loadPosTicks;
                    setServoPos(TrayPositions.LOADING);
                }
                break;
            case TO_CARRY:
                liftTargetPosition = bottomPosTicks;
                setServoPos(TrayPositions.CARRYING);
                if (System.currentTimeMillis()-transitionTimer >= trayUpTime){
                    overallState = OverallStates.CARRY ;
                }
                break;
            case FROM_STOWED:
                setServoPos(TrayPositions.LOADING);
                liftTargetPosition = loadPosTicks ;
                if (liftMotor.getCurrentPosition() <= loadPosTicks + positionTolerance){
                    overallState = OverallStates.LOADING ;
                }
                break;
        }
        //liftMotor.setTargetPosition(scalePosition(liftTargetPosition));
       // liftMotor.setPower(.6);
        setToPoitionPID(liftTargetPosition);
        updateServos();
        printInfo();
        if (limitSwitchIsPressed()){
            liftMotor.setPower(0);
        }
    }



    /////////////////////////////////////// ______ UTIL __

    /// Lift motor util functions


    private int scalePosition(int input){
        return zeroPos - input;
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
        return (limitSwitch.getVoltage() > 1.5);
    }
    public void setToPoitionPID(int targetPos){
        if (!liftMotor.getMode().equals(DcMotor.RunMode.RUN_WITHOUT_ENCODER)){
            liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        double correction = liftHeightPidController.getPIDCorrection(targetPos, getliftPos());
        correction *= .0001;
        liftMotor.setPower(correction);
    }

    /// servo util functions

    public void updateServos() {
        leftFlap.setPosition(leftFlapPos);
        rightFlap.setPosition(rightFlapPos);
        leftAngle.setPosition(leftAnglePos);
        rightAngle.setPosition(rightAnglePos);
    }
    public void setServoPos(TrayPositions trayPos){
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


    public void homeLiftVersA () {
        if (trayState == TrayPositions.LOADING){ // lift cannot be in loading pos to start
            setServoPos(TrayPositions.DUMP_A);                      // moves tray out of load to carry position
        }
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // move the lift slowly upwards
        liftMotor.setPower (.35);
        while (!limitSwitchIsPressed()){ }
        liftMotor.setPower(0);
        setLiftZeroPos();
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        overallState = OverallStates.CARRY;
        trayState = TrayPositions.CARRYING;
        liftFinalState = LiftFinalStates.HIGH;
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

    private void printInfo(){
        if(DEBUG){
            Log.e(TAG, "overall state: " + overallState);
            Log.e(TAG,"Final State: " + liftFinalState);
            Log.e(TAG, "cubeTray state: "+ trayState);
            Log.e(TAG, "lift cur target pos: " + liftTargetPosition);
            Log.e(TAG, "liftTargetPosition :  " + liftMotor.getTargetPosition());
            Log.e(TAG, "lift current dPosition :  " + liftMotor.getCurrentPosition());
            Log.e(TAG, "lift motor current mode " + liftMotor.getMode());

        }
    }
}
