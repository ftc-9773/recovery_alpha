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

/**
 ---------------------------------------------------------------------------------------------------
 *                             ::: How to use this class properly :::
 ---------------------------------------------------------------------------------------------------
 *
 * this class uses state logic, and is made to work iteratively, to accomplish tasks without needing
 * to 'steal' time from the rest of the robot.
 *
 * HOWEVER, it must be called regularly in a loop to ensure it
 * functions correctly
 *
 * for deafault Tele-op operation, use the updateFromGamepad(); command in the loop. it preforms all necessary actions
 *
 * if, for some reason, you need to avoid the deafault operation mode, (say in autonomous) use:
 *          the SetToPos() function in order to write a position
 *          This NEEDS to be followed by the updatePosition(); method
 *
 * Miscilanious methods:
 *
 *  homeLift();   // homes the lift: NOTE: as of now, the lift must not be in the loading position for this to work
 *
 *  defineStartPosition(); // sets the start position of the lift as of yet, the program cannot tell what the starting
 *                         // position of the lift is, so it is necessary to set this when transitioning between opmodes
 *                         // put at the
 *
 *
 * ----------------------------------------------------------------------------------------------------
 * To change PID parameters, go to swervePIDCoefficients.json under the JSON package and change the value
 *
 * To go to the dirrectory:
 * cd TeamCode/src/main/java/org/firstinspires/ftc/teamcode/JSON/
 *
 * To push the file:
 * ~/Library/Android/sdk/platform-tools/adb push CubeTrayServoPositions.json /sdcard/FIRST/team9773/json18
 *
 * To pull the file
 * ~/Library/Android/sdk/platform-tools/adb pull /sdcard/FIRST/team9773/json18/CubeTrayServoPositions.json
 *
 */


public class CubeTray implements CubeTrays {
    // setup enum valeus for state machine
    public enum TrayPositions {STOWED, LOADING, CARRYING, DUMP_A, JEWEL}
    public enum OverallStates {LOADING, CARRY, STOWED, TO_LOADING, FROM_STOWED, TO_CARRY, TO_JEWEL}
    public boolean homing = false;
    public boolean grabbing = true;
    public boolean AutonomousMode = false;

    // Json setup
    private SafeJsonReader myCubeTrayPositions;
    private static final boolean useBlockerServo = false;

    // create state machines
    // TODO: make this initialised or automated for start
    public TrayPositions trayState = TrayPositions.CARRYING;
    public OverallStates overallState = OverallStates.CARRY;
    public LiftFinalStates liftFinalState  = LiftFinalStates.HIGH;
    public long transitionTimer ;
    private static int trayUpTime = 500; // time in miliseconds given to angle up

    // define limit switch
    private AnalogInput limitSwitch;
    // define servos & motor for the tray
    private Servo grabber  ;
    private Servo jewelServo ;
    //private Servo leftAngle ;
    //private Servo rightAngle ;
    public DcMotor liftMotor ;
    // define gamepad value
    private Gamepad gamepad1;

    private double liftMotorPower = 0;
    public int liftTargetPosition = 0;  // change to private

    //DEBUGING
    private static final boolean DEBUG = false;
    private static final String TAG = "ftc9773_CubeTray" ;

    // servo position variables for testing
    public double grabberPos;
    public double leftAnglePos;
    public double rightAnglePos;

    //TESTING variables
    private boolean TESTING = false;
    private static final double increment =.001;
    //HOMING variables
    private int zeroPos = 0;

    //restart value - only writes the position every tenth time
    int iterNum = 0;
    static final int itersPerWrite = 10;
    static final boolean RestartOn = true;

    // TODO: get actual values for these parameters

    private static final double motorRatio = 19.2;
    private static final double motorToLiftRatio = 1.0;//(22.0/16.0);
    private static final double  ticksPerRotation = 28 * motorRatio ;// fix
    private static int outputSprocketSize = 16;

    // Safety ABORT variables
    //  private static int safteyAbortTime = 50;
    //   private static double safteyAbortMinSpeed = 0.5;
    //    private int lastTickPos = -1;
//     private long timeAtLastPoll = -1;


    // setup variables for positioning    // default values are hardcoded in case of issue
    private static int sensorPosTicks = 3355;
    private static int topPosTicks = 3360;
    private static int middlePosTicks = 1950;
    private static int bottomPosTicks = 450;
    private static int loadPosTicks = 250;
    private int toLoadingThreshold  = 250;
    private static int jewelPosTicks = 1000;

    private static final int positionTolerance = 40;

    private PIDController liftHeightPidController;

    //TODO: migrate to a JSON config file for easy config

    //define servo positions - each array of postitons go in the following order:
    // stowed, loading, carrying, dumpingFlap, dumpingAngleAndFlap
    private static double[] grabberPositions = {0.55, 0.62, 0.77, 0.4,.4,.4 } ;
    private static double[] leftAnglePostions = {0.885, 0.554, 0.169, 0.169, 0.039} ;  // dump used to be .039
    private static double[] rightAnglePostions = {0.07, 0.350, 0.754, 0.754,0.901 } ;   // dump used to be .901


    public CubeTray(HardwareMap hwMap, Gamepad gamepad1, Gamepad gamepad2){  // constructor takes hardware map
        // attach all the servos to their hardware map components
         grabber = hwMap.servo.get("ctgServo");
        //leftAngle = hwMap.servo.get("ctlaServo");
        //rightAngle = hwMap.servo.get("ctraServo");
        // passes gamepad, instead of gamepad values for ease of use
        this.gamepad1 = gamepad1;

        // setup JewelBlockerServo



        // attach DC lift motor
        liftMotor = hwMap.dcMotor.get("ctlMotor");
        limitSwitch = hwMap.analogInput.get("ctlLimitSwitch");

        myCubeTrayPositions = new SafeJsonReader("CubeTrayServoPositions");

        // TODO: initialise  JSON config file etc

        sensorPosTicks = myCubeTrayPositions.getInt("sensorPosTicks");
        topPosTicks = myCubeTrayPositions.getInt("topPosTicks");
        middlePosTicks = myCubeTrayPositions.getInt("middlePosTicks");
        bottomPosTicks = myCubeTrayPositions.getInt("bottomPosTicks");
        loadPosTicks = myCubeTrayPositions.getInt("loadPosTicks");
        toLoadingThreshold = myCubeTrayPositions.getInt("toLoadingThreshold");

        /*
        if (DEBUG) Log.i(TAG, "sensor position set to: " + sensorPosTicks);
        if (DEBUG) Log.i(TAG, "TopPosTicks set to: " + topPosTicks);
        if (DEBUG) Log.i(TAG, "middle position ticks set to: " + middlePosTicks);
        if (DEBUG) Log.i(TAG, "bottom position set to: " + bottomPosTicks);
        if (DEBUG) Log.i(TAG, "load position set to: " + loadPosTicks);
*/

        // set up servos
        boolean usingJsonServoPositons = myCubeTrayPositions.getBoolean("usingJsonServoPositions");

        String logging = "Positions are: ";
        for (double i: grabberPositions) {
            logging += ", " + i;
        }
        //if (DEBUG) Log.v(TAG, logging);


        if (usingJsonServoPositons) {
            leftAnglePostions = buildServoPosArrayFromJson("leftAngle", leftAnglePostions);
            rightAnglePostions = buildServoPosArrayFromJson("rightAngle", rightAnglePostions);
            grabberPositions = buildServoPosArrayFromJson("grabber", grabberPositions);

        }
        logging = "Positions are: ";
        for (double i: grabberPositions) {
            logging += ", " + i;
        }
        if (DEBUG) Log.v(TAG, logging);


        //todo: finish tuning PID

        // setup PID for lift
        Double kp = myCubeTrayPositions.getDouble("liftHeightP");
        Double ki = myCubeTrayPositions.getDouble("liftHeightI");
        Double kd = myCubeTrayPositions.getDouble("liftHeightD");
        liftHeightPidController = new PIDController(kp, ki, kd);

        //if (DEBUG) Log.i(TAG,"liftHeightP = " + kp);
        //if (DEBUG) Log.i(TAG,"liftHeight I = " + ki);
        //if (DEBUG) Log.i(TAG,"liftHeight D = " + kd);

        // abort stuff
        // afteyAbortTime = myCubeTrayPositions.getInt("safteyAbortTime");
//        safteyAbortMinSpeed = myCubeTrayPositions.getDouble("safetyAbortMinSpeed");



        //TESTING
    }

    /// DEAFAULT INTERFACE
    public void updateFromGamepad(){
        if(gamepad1.x){
            liftFinalState = LiftFinalStates.LOADING;
        } else if (gamepad1.a){
            liftFinalState = liftFinalState.LOW;
            setServoPos(TrayPositions.CARRYING);
        } else if (gamepad1.b){
            liftFinalState = liftFinalState.MID;
            setServoPos(TrayPositions.CARRYING);
        } else if(gamepad1.y){
            liftFinalState = liftFinalState.HIGH;
            setServoPos(TrayPositions.CARRYING);
        }
        if(gamepad1.right_bumper){
            dump();
        }
        grabbing  = gamepad1.right_trigger > .5;

        updatePosition();

    }
    public void dump(){
        setServoPos(TrayPositions.DUMP_A);
    }

    // to update without taking joystick input
    public void updatePosition(){
        // update state prior to carrying out actions
        updateOverallState();
        // state machine is now ready to set to positions
        switch (overallState) {
            case LOADING:
                // when the state is loading, sets both lift and tray to loading position
                liftTargetPosition = loadPosTicks;
                setServoPos(TrayPositions.LOADING);
                break;

            case CARRY:

                switch (liftFinalState){
                    // when in carry position, can set directly to target position
                    case STOWED:
                        break;
                    case LOADING:
                        break;
                    case LOW:
                        liftTargetPosition = bottomPosTicks;
                        break;
                    case MID:
                        liftTargetPosition = middlePosTicks;
                        break;
                    case HIGH:
                        liftTargetPosition = topPosTicks;
                        break;
                    case JEWELC:
                        liftTargetPosition = jewelPosTicks;
                        dump();
                        break;
                    case JEWELR:
                        liftTargetPosition = jewelPosTicks;
                        dump();
                        break;
                    case JEWELL:
                        liftTargetPosition = jewelPosTicks;
                        dump();
                        break;
                    default:
                        // otherwise do nothing
                        break;
                }
                break;
            case STOWED:  // as of now no instructions to go to stowed position
                liftTargetPosition = loadPosTicks ;
                setServoPos(TrayPositions.LOADING);
                break;
            //
            case TO_LOADING:
                // if the lift is above the threshold, set target to be threshold
                if (getliftPos() >= toLoadingThreshold) {
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
                long time = System.currentTimeMillis();
                if(time - transitionTimer>trayUpTime){
                    overallState = OverallStates.CARRY;
                    updatePosition();    // idk if I should do this
                }
                break;
            case TO_JEWEL:
                liftTargetPosition = bottomPosTicks;
                setServoPos(TrayPositions.JEWEL);
                long curTime = System.currentTimeMillis();
                if(curTime - transitionTimer>trayUpTime){
                    overallState = OverallStates.CARRY;
                    updatePosition();    // idk if I should do this
                }

                break;
            case FROM_STOWED:
                setServoPos(TrayPositions.LOADING);
                liftTargetPosition = loadPosTicks ;
                if (getRawLiftPos() <= loadPosTicks + positionTolerance){
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

        // update json file for positions
        if (iterNum%itersPerWrite == 0) {
            myCubeTrayPositions.modifyInt("LastLiftHeight", getliftPos());
            myCubeTrayPositions.modifyString("LastLiftTrayPos", overallState.toString());
            myCubeTrayPositions.modifyInt("LastWriteTime", (int) System.currentTimeMillis());

            myCubeTrayPositions.updateFile();

            //if (DEBUG) Log.i(TAG,"Wrote the following vals to file: (cubeTrayLogging)");
            //if (DEBUG) Log.i(TAG + "Height", String.valueOf(getliftPos()));
            //if (DEBUG) Log.i(TAG + "Pos", overallState.toString());

        }
        iterNum++;
        //if (DEBUG) Log.v (TAG, "Ser o position leftAngle = " + leftAngle.getPosition() );
        //if (DEBUG) Log.v (TAG, "Ser o position rightAngle = " + rightAngle.getPosition() );
        if (DEBUG) Log.v (TAG, "Ser o position grabber = " + grabber.getPosition() );



    }

    // util function to translate final positions into overall positions based on position - the brains of the state machine
    private void updateOverallState(){
/*        if(!liftMotor.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION)){
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
*///        }
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
            case JEWELC:
                if(overallState == OverallStates.STOWED) {
                    overallState = OverallStates.FROM_STOWED;
                } else if (!overallState.equals(OverallStates.CARRY)&&!overallState.equals(OverallStates.TO_JEWEL)) {
                    overallState = OverallStates.TO_JEWEL;
                    transitionTimer = System.currentTimeMillis();
                }

            default:
                break;
        }
        printInfo();

    }



    /////////////////////////////////////// ______ UTIL __

    /// Lift motor util functions


    private int scalePosition(int input){
        return zeroPos - input;
    }
    // resets the zero position of the lift
    private void setLiftZeroPos(){
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
    // sets to position

    public void setToPoitionPID(int targetPos){
        if (!liftMotor.getMode().equals(DcMotor.RunMode.RUN_WITHOUT_ENCODER)){
            liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        double correction = liftHeightPidController.getPIDCorrection(targetPos, getliftPos());

        //if (DEBUG) if (DEBUG) Log.d(TAG,"lift position correction =" + correction);

        liftMotor.setPower(correction);
    }

    /// servo util functions

    private void updateServos() {
        if (grabbing){
            grabber.setPosition(grabberPositions[2]);
            if (DEBUG) Log.e("Nicky", "" + grabberPositions[2]);
        } else {
            grabber.setPosition(grabberPos);
            if (DEBUG) Log.e("Nicky", "" + grabberPos);
        }
        //leftAngle.setPosition(leftAnglePos);
        //rightAngle.setPosition(rightAnglePos);
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
                if(useBlockerServo){
                }else
                break;
            case CARRYING:
                posNum = 2;
                if(AutonomousMode && liftFinalState.equals(LiftFinalStates.LOADING)){
                } else
                break;
            case DUMP_A:
                posNum = 3;
                break;

            case JEWEL:
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
        grabberPos = grabberPositions[posNum];
        leftAnglePos = leftAnglePostions[posNum];
        rightAnglePos = rightAnglePostions[posNum];

        if (DEBUG) Log.d(TAG, "Thought I wrote grabber to" + grabberPos);

        if (DEBUG) Log.d(TAG, "wrote grabber servo to " + grabber.getPosition());

        String logging = " setting servo positions:Positions are: ";
        for (double i: grabberPositions) {
            logging += ", " + i;
        }
        if (DEBUG) Log.i(TAG,"wrote the following Grabber positions" + logging);
    }


    public void home ()  { // might help use
        if (trayState == TrayPositions.LOADING){ // lift cannot be in loading pos to start
            setServoPos(TrayPositions.CARRYING);                      // moves tray out of load to carry position
        }
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // move the lift slowly upwards
        liftMotor.setPower (.55);
        while (!limitSwitchIsPressed() ){}
        liftMotor.setPower(0);
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER) ;
        setLiftZeroPos();
    }


    private boolean testIfTop(){
        if (limitSwitchIsPressed()) {
            liftMotor.setPower(0);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);   // stop motor and set reference position
            setLiftZeroPos();
            return true;
        } else return false;
    }


/*
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
*/

    private void printInfo(){
        if(DEBUG){
            if (DEBUG) Log.d(TAG, "overall state: " + overallState);
            if (DEBUG) Log.d(TAG,"Final State: " + liftFinalState);
            if (DEBUG) Log.d(TAG, "cubeTray state: "+ trayState);
            if (DEBUG) Log.d(TAG, "lift cur target pos: " + liftTargetPosition);
            if (DEBUG) Log.d(TAG, "lift current dPosition :  " + liftMotor.getCurrentPosition());
            if (DEBUG) Log.d(TAG, "lift motor current mode " + liftMotor.getMode());
            if (DEBUG) Log.d(TAG,"power written to lift" + liftMotor.getPower());


        }
    }

    // non-joystick update functions
    public void setToPos(LiftFinalStates state){
        if (!state.equals(LiftFinalStates.STOWED)) {
            liftFinalState = state;
        } else{
            if (DEBUG) Log.w(TAG, "SetToPos is unnable to set to STOWED position as of now. Sorry.");
        }
    }


    private double[] buildServoPosArrayFromJson(String servoName, double[] positions){
        boolean success = true;
        if (DEBUG) Log.i(TAG, "building servo: " + servoName);

        // build stowed pos setting
        double stowedTempVal = myCubeTrayPositions.getDouble(servoName + "Stowed");
        if(stowedTempVal != 0.0) {
            positions[0] = stowedTempVal;
            //if (DEBUG) Log.i(TAG, "built servo " +servoName + " stowed pos to" +stowedTempVal);
        }   else {
            success = false;
            //if (DEBUG) Log.w(TAG, "unnable to build servo " +servoName + " stowed pos; set to hardcoded default");
        }

        // build loading pos setting
        double loadingTempVal = myCubeTrayPositions.getDouble(servoName + "Loading");
        if(loadingTempVal != 0.0) {
            positions[1] = loadingTempVal;
            //if (DEBUG) Log.i(TAG, "built servo " +servoName + " loading pos to" +loadingTempVal);

        }   else {
            success = false;
            //if (DEBUG) Log.w(TAG, "unnable to build servo " +servoName + " laoding pos; set to hardcoded default");
        }

        // build carrying pos setting
        double carryingTempVal = myCubeTrayPositions.getDouble(servoName + "Carrying");
        if(carryingTempVal != 0.0) {
            positions[2] = carryingTempVal;
            //if (DEBUG) Log.i(TAG, "built servo " +servoName + " carrying pos to" +carryingTempVal);
        }   else {
            //if (DEBUG) Log.w(TAG, "unnable to build servo " +servoName + " carrying pos; set to hardcoded default");
            success = false;
        }

        // build dumpingA pos setting
        double dumpingFlapTempVal = myCubeTrayPositions.getDouble(servoName + "DumpingFlap");
        if(dumpingFlapTempVal != 0.0) {
            positions[3] = dumpingFlapTempVal;
            //if (DEBUG) Log.i(TAG, "built servo " +servoName + " dumpA pos to" +dumpingFlapTempVal);
        }   else {
            //if (DEBUG) Log.w(TAG, "unnable to build servo " +servoName + " DumpA pos; set to hardcoded default");
            success = false;
        }

        // build dumpingB pos setting
        double dumpingAngleTempVal = myCubeTrayPositions.getDouble(servoName + "DumpingAngle");
        if(dumpingAngleTempVal != 0.0) {
            positions[4] = dumpingAngleTempVal;
            //if (DEBUG) Log.i(TAG, "built servo " +servoName + " dumpB pos to" +dumpingAngleTempVal);
        }   else{
            success = false;
            //if (DEBUG) Log.w(TAG, "unnable to build servo " +servoName + " dumpingB pos; set to hardcoded default");

        }
        return positions;
    }

    public void setZeroFromCompStart() {
        int compStartPos =   myCubeTrayPositions.getInt("CompStartPos");
        zeroPos = liftMotor.getCurrentPosition() - compStartPos;
        overallState = OverallStates.STOWED;
    }
    private OverallStates readTrayPositions(){
        String value = myCubeTrayPositions.getString("LastLiftTrayPos");
        OverallStates result = null;
        switch (value) {
            case "LOADING":
                result = OverallStates.LOADING;
                break;
            case "CARRY":
                result = OverallStates.CARRY;
                break;
            case "STOWED":
                result = OverallStates.STOWED;
                break;
            case "TO_LOADING":
                result = OverallStates.TO_LOADING;
                break;
            case "FROM_STOWED, TO_CARRY":
                result = OverallStates.FROM_STOWED;
                break;
            case "TO_CARRY":
                result = OverallStates.TO_CARRY;
                break;
            default:
                break;
        }
        return result;
    }

    public void setZeroFromLastOpmode(){
        int lastPos = myCubeTrayPositions.getInt("LastLiftHeight");
        OverallStates lastState = readTrayPositions();
        if (lastPos == 0|| lastPos== -1){
            if (DEBUG) Log.e (TAG, "unnable to read Lift Height");
            return;
        }
        if (lastState.equals (null)){
            if (DEBUG) Log.e (TAG, "unnabe to read Lift state");
            return;
        }
        zeroPos = liftMotor.getCurrentPosition() - lastPos;
        overallState = lastState;
    }

    public void setAutonomousMode(boolean val) {
        AutonomousMode = val;
    }
    public void startDump(){
        dump();
    }
    public void endDump(){}
    public void stopDump() {}
}