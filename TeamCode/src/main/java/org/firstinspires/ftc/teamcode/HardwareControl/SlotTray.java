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
 * Created by zacharye on 2/4/18.
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
 *
 * To go to the dirrectory:
 * cd TeamCode/src/main/java/org/firstinspires/ftc/teamcode/JSON/
 *
 * To push the file:
 ~/Library/Android/sdk/platform-tools/adb push SlotTrayPositions.json /sdcard/FIRST/team9773/json18
 *
 * To pull the file
 * ~/Library/Android/sdk/platform-tools/adb pull /sdcard/FIRST/team9773/json18/CubeTrayServoPositions.json
 *
 */

public class SlotTray implements CubeTrays {
    public enum TrayPositions{GRABBED, OPEN,LOADING, START_POS,JEWEL_LEFT,JEWEL_CENTER,JEWEL_RIGHT}

    int TargetLoadPos;

    int zeroPos = 0;

    int loadingPosTicks;
    int lowPosTicks;
    int midPosTicks;
    int highPosTicks;
    int compStartPos;

    double openGrabberPos;
    double closedGrabberPos;
    double loadGrabberPos;
    double startGrabberPos;

    double blockBlockerPos;
    double leftStowBlockerPos;
    double rightBlockerPos;

    private double grabberPos = 0;
    private double blockerPos = 0;

    private double leftRollerOutVal =  0.89;
    private double rightRollerOutVal = 0.11;

    private boolean ejecting = false;

    private static int liftPosTol = 50;

    // stuff for state machine
    private LiftFinalStates targetPos = LiftFinalStates.LOADING;
    private boolean waitingForLiftUp = false;
    private long timer = -1;
    private long servoUpTime = 250;




    public boolean AutonomousMode = false;

    // create motor and servo objects
    private Gamepad gamepad1;
    private DcMotor liftMotor;
    private Servo grabServo;
    private Servo blockServo;
    AnalogInput limitSwitch;

    // for roller ejection
    private boolean usingRollerEjection= true;
    private Servo leftEjectRoller;
    private Servo rightEjectRoller;

    PIDController liftHeightPidController;


    private double liftMotorPower = 0;
    public int liftTargetPosition = 0;  // change to private

    //DEBUGING
    private static final boolean DEBUG = true;
    private static final String TAG = "ftc9773_CubeTray" ;



    private SafeJsonReader myCubeTrayPositions;
    private static final boolean useBlockerServo = true;


    public SlotTray(HardwareMap hwMap , Gamepad gamepad1){

        // read values from json
        myCubeTrayPositions = new SafeJsonReader("SlotTrayPositions");

        loadingPosTicks = myCubeTrayPositions.getInt("loadPosTicks");
        Log.i(TAG, "set Loading Pos to" + loadingPosTicks);
        lowPosTicks = myCubeTrayPositions.getInt("bottomPosTicks");
        Log.i(TAG, "set low Pos to" + lowPosTicks);

        midPosTicks = myCubeTrayPositions.getInt("middlePosTicks");
        Log.i(TAG, "set mid Pos to" + midPosTicks);

        highPosTicks = myCubeTrayPositions.getInt("topPosTicks");
        Log.i(TAG, "set high Pos to" + highPosTicks);
        compStartPos = myCubeTrayPositions.getInt("compStartPos");
        Log.i(TAG, "set start Pos to" + compStartPos);


        // set grabber position values
        openGrabberPos = myCubeTrayPositions.getDouble("openGrabberPos");
        Log.i(TAG, "set open grabber Pos to" + openGrabberPos);
        closedGrabberPos = myCubeTrayPositions.getDouble("grabbedGrabberPos");
        Log.i(TAG, "set closed grabber Pos to" + closedGrabberPos);
        loadGrabberPos = myCubeTrayPositions.getDouble("loadGrabberPos");
        Log.i(TAG, "set laod grabber Pos to" + loadGrabberPos);
        startGrabberPos = myCubeTrayPositions.getDouble("startGrabberPos");
        Log.i(TAG, "set start grabber Pos to" + startGrabberPos);

        // setBlockerPositiobns
        blockBlockerPos = myCubeTrayPositions.getDouble("blockBlockerPos");
        Log.i(TAG, "set blocked blocker Pos to" + blockBlockerPos);
        leftStowBlockerPos = myCubeTrayPositions.getDouble("stowLeftBlockerPos");
        Log.i(TAG, "set stow blocker Pos to" + leftStowBlockerPos);
        rightBlockerPos = myCubeTrayPositions.getDouble("rightBlockerPos");
        Log.i(TAG, "set right blocker Pos to" + rightBlockerPos);

        // init roller ejection stuff
        usingRollerEjection = myCubeTrayPositions.getBoolean("usingRollerEjection");
        Log.i(TAG, "set usingRollerEjection to" + usingRollerEjection);


        rightRollerOutVal = myCubeTrayPositions.getDouble("rightRollerOutVal");
        Log.i(TAG, "set rightRollerOutVal to" + rightRollerOutVal);
        leftRollerOutVal = myCubeTrayPositions.getDouble("leftRollerOutVal");
        Log.i(TAG, "set leftRollerOutVal to" + leftRollerOutVal);

        //state machine stuff
        //servoUpTime = (long)myCubeTrayPositions.getInt("servoUpTime");
        Log.i(TAG, "set servoUpTIme to" + servoUpTime);
        //liftPosTol = myCubeTrayPositions.getInt("liftPosTol");
        Log.i(TAG, "set liftPosTol to" + liftPosTol);




        // initialize the motor and servos
        this.gamepad1 = gamepad1;
        liftMotor = hwMap.dcMotor.get("ctlMotor");
        limitSwitch = hwMap.analogInput.get("ctlLimitSwitch");
        grabServo = hwMap.servo.get("ctgServo");
        blockServo = hwMap.servo.get("csServo");
        // initialize roller ejection
        if(usingRollerEjection) {
            leftEjectRoller = hwMap.servo.get("ctleServo");
            rightEjectRoller = hwMap.servo.get("ctreServo");
        }



        Double kp = myCubeTrayPositions.getDouble("liftHeightP");
        Double ki = myCubeTrayPositions.getDouble("liftHeightI");
        Double kd = myCubeTrayPositions.getDouble("liftHeightD");
        liftHeightPidController = new PIDController(kp, ki, kd);

        Log.i(TAG,"liftHeightP = " + kp);
        Log.i(TAG,"liftHeight I = " + ki);
        Log.i(TAG,"liftHeight D = " + kd);

        setServoPos(TrayPositions.LOADING);
    }
    /// DEAFAULT INTERFACE
    public void updateFromGamepad(){
        if(gamepad1.x){
            setToPos(LiftFinalStates.LOADING);
        } else if (gamepad1.a){
            setToPos(LiftFinalStates.LOW);
        } else if (gamepad1.b){
            setToPos(LiftFinalStates.MID);
        } else if(gamepad1.y){
            setToPos(LiftFinalStates.HIGH);
        }
        if(gamepad1.right_bumper){
            dump();
        }
        updatePosition();

    }
    public void dump(){
        int i;
        if(!usingRollerEjection)
             i = 1;
        //setServoPos(TrayPositions.OPEN);
        else{
            leftEjectRoller.setPosition(leftRollerOutVal);
            rightEjectRoller.setPosition(rightRollerOutVal);
            ejecting = true;

            Log.d(TAG, "set leftRoller to " + leftEjectRoller.getPosition());
            Log.d(TAG, "set leftRoller to " + rightEjectRoller.getPosition());


        }
    }

    public void setToPos(LiftFinalStates state){
        targetPos = state;
        updatePosition();
    }//

    public void updatePosition(){
        // mini state machine to allow the blocker servo time to turn
        switch (targetPos) {
            case LOADING:
                if(isInLoadingPocket()) {
                    liftTargetPosition = loadingPosTicks;
                    setServoPos(TrayPositions.LOADING);
                    blockServo.setPosition(blockBlockerPos);
                } else {
                    liftTargetPosition = loadingPosTicks;
                    setServoPos(TrayPositions.LOADING);
                }
                break;
            case LOW:
            case MID:
            case HIGH:
                setServoPos(TrayPositions.GRABBED);
                if (isInLoadingPocket() && !waitingForLiftUp){
                    timer = System.currentTimeMillis();
                    waitingForLiftUp = true;
                    blockServo.setPosition(leftStowBlockerPos);
                } else if ( isInLoadingPocket() && waitingForLiftUp){
                    if((System.currentTimeMillis()- timer)>= servoUpTime){
                        setLiftPos(targetPos);
                    }
                } else {
                    waitingForLiftUp = false;
                    timer = -1;
                    setLiftPos(targetPos);
                }
            default:
                break;
        }



        setToPoitionPID(liftTargetPosition);

        // update the ejection rollers based off of wether or not they are ejecting
        // if they are, set to ejection position
        // otherwise stop the rollers
        if(ejecting){
            leftEjectRoller.setPosition(leftRollerOutVal);
            rightEjectRoller.setPosition(rightRollerOutVal);
        }  else {
            leftEjectRoller.setPosition(0.5);
            rightEjectRoller.setPosition(0.5);
        }
        ejecting = false;

    }
   private void setLiftPos(LiftFinalStates state) {
        switch (state) {
            case LOADING:
                liftTargetPosition = loadingPosTicks;
                break;
            case LOW:
                liftTargetPosition = lowPosTicks;
                break;
            case MID:
                liftTargetPosition = midPosTicks;
                break;
            case HIGH:
                liftTargetPosition = highPosTicks;
                break;
            case JEWELC:
                liftTargetPosition = lowPosTicks;
                break;
            case JEWELL:
                liftTargetPosition = lowPosTicks;
                break;
            case JEWELR:
                liftTargetPosition = lowPosTicks;
            default:
                break;
        }
    }

    public void setToPoitionPID(int targetPos){
        if (!liftMotor.getMode().equals(DcMotor.RunMode.RUN_WITHOUT_ENCODER)){
            liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        double correction = liftHeightPidController.getPIDCorrection(targetPos, getliftPos());

        Log.d(TAG,"lift Target position"+ targetPos );
        Log.d(TAG,"lift position correction =" + correction);

        liftMotor.setPower(correction);
    }
    private void setServoPos(TrayPositions position){
        switch (position){
            case GRABBED:
                grabberPos = closedGrabberPos;
                break;
            case OPEN:
                grabberPos = openGrabberPos;
                break ;
            case LOADING:
                grabberPos = loadGrabberPos;
                break;
            case START_POS:
                blockerPos = startGrabberPos;
            case JEWEL_CENTER:
                grabberPos = openGrabberPos;
                break;
            case JEWEL_LEFT:
                grabberPos = openGrabberPos;
                break;
            case JEWEL_RIGHT:
                grabberPos = openGrabberPos;
                break;
            default:
                break;
        }

        grabServo.setPosition(grabberPos);

        Log.i(TAG, "set grabber position to: "+  grabberPos);
        Log.i(TAG, "set blocker Position to : " + blockerPos);

    }
    public int getliftPos(){
        return liftMotor.getCurrentPosition() - zeroPos;
    }

    @Override
    public int getRawLiftPos() {
        return liftMotor.getCurrentPosition();
    }

    @Override
    public void setServoPos(CubeTray.TrayPositions trayPos) {
        // for now do nothing
    }

    @Override
    public void homeLiftVersA() {
        // for now do nothing
    }

    @Override
    public void setZeroFromCompStart() {
        int compStartPos =   myCubeTrayPositions.getInt("CompStartPos");
        zeroPos = liftMotor.getCurrentPosition() - compStartPos;
    }

    public void setZeroFromLastOpmode(){
        int lastPos = myCubeTrayPositions.getInt("LastLiftHeight");
        if (lastPos == 0|| lastPos== -1){
            Log.e (TAG, "unnable to read Lift Height");
            return;
        }
    }

    public void setAutonomousMode(boolean val) {
        AutonomousMode = val;
    }

    private boolean isInLoadingPocket(){
        return (Math.abs(loadingPosTicks - liftMotor.getCurrentPosition())< liftPosTol);
    }




}
