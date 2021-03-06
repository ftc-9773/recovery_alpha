package org.firstinspires.ftc.teamcode.HardwareControl;

import android.util.Log;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.infrastructure.PIDController;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

/**
 * Created by zacharye on 2/4/18.
 */


/**
 * ---------------------------------------------------------------------------------------------------
 * ::: How to use this class properly :::
 * ---------------------------------------------------------------------------------------------------
 * this class uses state logic, and is made to work iteratively, to accomplish tasks without needing
 * to 'steal' time from the rest of the robot.
 *
 * HOWEVER, it must be called regularly in a loop to ensure it
 * functions correctly
 *
 * for deafault Tele-op operation, use the updateFromGamepad(); command in the loop. it preforms all necessary actions
 *
 * if, for some reason, you need to avoid the deafault operation mode, (say in autonomous) use:
 * the SetToPos() function in order to write a position
 * This NEEDS to be followed by the updatePosition(); method
 *
 * Miscilanious methods:
 *
 * homeLift();   // homes the lift: NOTE: as of now, the lift must not be in the loading position for this to work
 *
 * defineStartPosition(); // sets the start position of the lift as of yet, the program cannot tell what the starting
 * // position of the lift is, so it is necessary to set this when transitioning between opmodes
 * // put at the
 *
 *
 * ----------------------------------------------------------------------------------------------------
 *
 * To go to the dirrectory:
 * cd TeamCode/src/main/java/org//ftc/teamcode/JSON/
 *
 * To push the file:
 * ~/Library/Android/sdk/platform-tools/adb push SlotTrayPositions.json /sdcard/FIRST/team9773/json18
 * ~/Library/Android/sdk/platform-tools/adb push JSON/jewelknockerpositions.json /sdcard/FIRST/team9773/json18
 * ~/Library/Android/sdk/platform-tools/adb push RASI/5GlyphRed.rasi /sdcard/FIRST/team9773/rasi18
 *
 * To pull the file
 * ~/Library/Android/sdk/platform-tools/adb pull /sdcard/FIRST/team9773/json18/CubeTrayServoPositions.json
 */

public class SlotTray implements CubeTrays {
    public enum TrayPositions {GRABBED, OPEN, LOADING, START_POS, JEWEL_LEFT, JEWEL_CENTER, JEWEL_RIGHT}

    int TargetLoadPos;

    int zeroPos = 0;

    private int loadingPosTicks;
    private int lowPosTicks;
    private int midPosTicks;
    private int highPosTicks;
    private int compStartPos;

    double openGrabberPos;
    double closedGrabberPos;
    double loadGrabberPos;
    double startGrabberPos;

    double blockBlockerPos;
    double leftStowBlockerPos;
    double rightBlockerPos;

    private double grabberPos = 0;
    private double blockerPos = 0;

    private double leftRollerOutVal = 0.89;
    private double rightRollerOutVal = 0.11;

    private boolean ejecting = false;

    private static int liftPosTol = 50;

    // stuff for state machine
    private LiftFinalStates targetPos = LiftFinalStates.LOADING;
    private boolean waitingForLiftUp = false;
    private long timer = -1;
    private long servoUpTime = 250;

    // for homing and restart methods
    private static final double homingPower = 0.5; //TODO: this might need to be changed or inversed
    private boolean homing = false;
    private long lastTime ;
    private int lastPosition;
    private int minSpeed = 2;

    //set up the writing for restart method
    private int iterNum = 0;
    private static int itersPerWrite = 3;

    private boolean usingExMotor = false;


    public boolean AutonomousMode = false;

    // create motor and servo objects
    private Gamepad gamepad1;
    private Gamepad gamepad2;
    private DcMotor liftMotor;
    private Servo grabServo;
    private Servo blockServo;
    AnalogInput limitSwitch;
    // for motor ratio calculations
    private static final double gearmotorRatio = -40 ;  // ex. 40, 20, 60, etc
    private static final double afterMotorRatio = .7272727273 ;  // ratio from the motor to the chain
    private static final double driveSprocketToothNumb = 16;//number of teeth
    private static final double ticksPerBareMotorRot = 28;
    // calculated par*
    private static final double TicksPerinch = (ticksPerBareMotorRot * gearmotorRatio * afterMotorRatio / (driveSprocketToothNumb*0.25));

    // for roller ejection

    private boolean usingRollerEjection = true;
    private Servo leftEjectRoller;
    private Servo rightEjectRoller;
    private boolean durationRollerEjection = false;

    PIDController liftHeightPidController;
    DcMotorImplEx liftMotorEx;


    private double liftMotorPower = 0;
    public int liftTargetPosition = 0;  // change to private

    //DEBUGING
    private static final boolean DEBUG = false;
    private static final String TAG = "ftc9773_CubeTray";


    private SafeJsonReader myCubeTrayPositions;
    private static final boolean useBlockerServo = true;


    public SlotTray(HardwareMap hwMap, Gamepad gamepad1, Gamepad gamepad2) {

        // read values from json
        myCubeTrayPositions = new SafeJsonReader("SlotTrayPositions");

        loadingPosTicks = (int)(myCubeTrayPositions.getDouble("loadPosIn")*TicksPerinch);
        if (DEBUG) Log.i(TAG, "set Loading Pos to" + loadingPosTicks);
        lowPosTicks = (int)(myCubeTrayPositions.getDouble("bottomPosIn")*TicksPerinch);
        if (DEBUG) Log.i(TAG, "set low Pos to" + lowPosTicks);

        midPosTicks = (int)(myCubeTrayPositions.getDouble("middlePosIn")*TicksPerinch);
        if (DEBUG) Log.i(TAG, "set mid Pos to" + midPosTicks);

        highPosTicks = (int)(myCubeTrayPositions.getDouble("topPosIn")*TicksPerinch);
        if (DEBUG) Log.i(TAG, "set high Pos to" + highPosTicks);
        compStartPos = (int)(myCubeTrayPositions.getDouble("compStartPosIn")*TicksPerinch);
        if (DEBUG) Log.i(TAG, "set start Pos to" + compStartPos);


        // set grabber position values
        openGrabberPos = myCubeTrayPositions.getDouble("openGrabberPos");
        if (DEBUG) Log.i(TAG, "set open grabber Pos to" + openGrabberPos);
        closedGrabberPos = myCubeTrayPositions.getDouble("grabbedGrabberPos");
        if (DEBUG) Log.i(TAG, "set closed grabber Pos to" + closedGrabberPos);
        loadGrabberPos = myCubeTrayPositions.getDouble("loadGrabberPos");
        if (DEBUG) Log.i(TAG, "set laod grabber Pos to" + loadGrabberPos);
        startGrabberPos = myCubeTrayPositions.getDouble("startGrabberPos");
        if (DEBUG) Log.i(TAG, "set start grabber Pos to" + startGrabberPos);

        // setBlockerPositiobns
        blockBlockerPos = myCubeTrayPositions.getDouble("blockBlockerPos");
        if (DEBUG) Log.i(TAG, "set blocked blocker Pos to" + blockBlockerPos);
        leftStowBlockerPos = myCubeTrayPositions.getDouble("stowLeftBlockerPos");
        if (DEBUG) Log.i(TAG, "set stow blocker Pos to" + leftStowBlockerPos);
        rightBlockerPos = myCubeTrayPositions.getDouble("rightBlockerPos");
        if (DEBUG) Log.i(TAG, "set right blocker Pos to" + rightBlockerPos);

        // init roller ejection stuff
        usingRollerEjection = myCubeTrayPositions.getBoolean("usingRollerEjection");
        if (DEBUG) Log.i(TAG, "set usingRollerEjection to" + usingRollerEjection);


        rightRollerOutVal = myCubeTrayPositions.getDouble("rightRollerOutVal");
        if (DEBUG) Log.i(TAG, "set rightRollerOutVal to" + rightRollerOutVal);
        leftRollerOutVal = myCubeTrayPositions.getDouble("leftRollerOutVal");
        if (DEBUG) Log.i(TAG, "set leftRollerOutVal to" + leftRollerOutVal);

        //state machine stuff
        //servoUpTime = (long)myCubeTrayPositions.getInt("servoUpTime");
        if (DEBUG) Log.i(TAG, "set servoUpTIme to" + servoUpTime);
        //liftPosTol = myCubeTrayPositions.getInt("liftPosTol");
        if (DEBUG) Log.i(TAG, "set liftPosTol to" + liftPosTol);


        // initialize the motor and servos
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        liftMotor = hwMap.dcMotor.get("ctlMotor");
        liftMotorEx = (DcMotorImplEx) liftMotor;
        limitSwitch = hwMap.analogInput.get("ctlLimitSwitch");
        grabServo = hwMap.servo.get("ctgServo");
        blockServo = hwMap.servo.get("csServo");
        // initialize the Extended motor
        if (usingExMotor) {
            Double kp = myCubeTrayPositions.getDouble("liftHeightP_EX");
            Double ki = myCubeTrayPositions.getDouble("liftHeightI_EX");
            Double kd = myCubeTrayPositions.getDouble("liftHeightD_EX");

            if (DEBUG) if (DEBUG) Log.i(TAG, "liftHeightEx P = " + kp);
            if (DEBUG) Log.i(TAG, "liftHeightEx I = " + ki);
            if (DEBUG) Log.i(TAG, "liftHeightEx D = " + kd);
            PIDCoefficients liftPID = new PIDCoefficients(kp, ki, kd);

            liftMotorEx = (DcMotorImplEx) liftMotor;
            liftMotorEx.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            if (DEBUG) Log.d(TAG, "currentPID coeffs: p" + liftMotorEx.getPIDCoefficients(DcMotor.RunMode.RUN_TO_POSITION).p);
            if (DEBUG) Log.d(TAG, "currentPID coeffs: i" + liftMotorEx.getPIDCoefficients(DcMotor.RunMode.RUN_TO_POSITION).i);
            if (DEBUG) Log.d(TAG, "currentPID coeffs: d" + liftMotorEx.getPIDCoefficients(DcMotor.RunMode.RUN_TO_POSITION).d);

            liftMotorEx.setPIDCoefficients(DcMotor.RunMode.RUN_TO_POSITION, liftPID);
        } else {

            Double kp = myCubeTrayPositions.getDouble("liftHeightP");
            Double ki = myCubeTrayPositions.getDouble("liftHeightI");
            Double kd = myCubeTrayPositions.getDouble("liftHeightD");
            liftHeightPidController = new PIDController(kp, ki, kd);

            if (DEBUG) Log.i(TAG, "liftHeightP = " + kp);
            if (DEBUG) Log.i(TAG, "liftHeight I = " + ki);
            if (DEBUG) Log.i(TAG, "liftHeight D = " + kd);

        }


        // initialize roller ejection
        if (usingRollerEjection) {
            leftEjectRoller = hwMap.servo.get("ctleServo");
            rightEjectRoller = hwMap.servo.get("ctreServo");
        }


        setServoPos(TrayPositions.LOADING);








    }

    /// DEAFAULT INTERFACE
    public void updateFromGamepad() {
        if (gamepad2.x) {
            setToPos(LiftFinalStates.LOADING);
        } else if (gamepad2.a) {
            setToPos(LiftFinalStates.LOW);
        } else if (gamepad2.b) {
            setToPos(LiftFinalStates.MID);
        } else if (gamepad2.y) {
            setToPos(LiftFinalStates.HIGH);
        }

        // Let gamepad 1 dump as well
        if (gamepad1.right_bumper && targetPos != LiftFinalStates.LOADING) {
            dump();
        }

        if (gamepad2.right_bumper && targetPos!= LiftFinalStates.LOADING) {
            if(gamepad2.right_trigger >0.5){
                setServoPos(TrayPositions.OPEN);
                ejecting = true;
            } else
            dump();
        } else if(gamepad2.right_trigger >0.5){
            // if the bottom trigger is pressed, pull in the cubes
            // this is achieved by writing the opposite roller's out val
                // note this is NOT a mistake
            leftEjectRoller.setPosition(rightRollerOutVal);
            rightEjectRoller.setPosition(leftRollerOutVal);
        }

        updatePosition();

    }

    // command to eject the cubes
    public void dump() {

        if (!usingRollerEjection) {
            //setServoPos(TrayPositions.OPEN);
        } else {
            leftEjectRoller.setPosition(leftRollerOutVal);
            rightEjectRoller.setPosition(rightRollerOutVal);
            ejecting = true;

            if (DEBUG) Log.d(TAG, "set leftRoller to " + leftEjectRoller.getPosition());
            if (DEBUG) Log.d(TAG, "set leftRoller to " + rightEjectRoller.getPosition());


        }
    }

    public void startDump() {
        durationRollerEjection = true;
        ejecting = true;
    }
    public void openDump(){
        setServoPos(TrayPositions.OPEN);
    }

    public void stopDump() {
        durationRollerEjection = false;
        ejecting = false;
        leftEjectRoller.setPosition(0.5);
        rightEjectRoller.setPosition(0.5);
    }

    @Override
    public void home(){ // TODO: test this method and add a way to run it from teleop
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if(!homing){
            homing = true;
            liftMotor.setPower(0.5);
            setServoPos(TrayPositions.GRABBED);
            lastPosition = liftMotor.getCurrentPosition();
            lastTime = System.currentTimeMillis();
            return;
        } else {
            setServoPos(TrayPositions.GRABBED);
        }
        // check if the lift has homed correctly
        if(Math.abs(lastPosition - liftMotor.getCurrentPosition())/ (System.currentTimeMillis()-lastTime)>minSpeed)
            setZeroFromKnownPosition(0);



    }

    public void setToPos(LiftFinalStates state) {
        switch (state) {
            case STOWED:
                setServoPos(TrayPositions.LOADING);
                break;
            case LOADING:
                setServoPos(TrayPositions.LOADING);
                break;
            case LOW:
            case MID:
            case HIGH:
                setServoPos(TrayPositions.GRABBED);
                break;
            case JEWELC:
            case JEWELR:
            case JEWELL:
                setServoPos(TrayPositions.OPEN);
                break;
            default:
                break;
        }
        targetPos = state;
        updatePosition();
    }//

    public void updatePosition() {
        if(!homing){
            home();
        } else {
            // mini state machine to allow the blocker servo time to turn
            setLiftPos(targetPos);

            setToPoitionPID(liftTargetPosition);

            // update the ejection rollers based off of wether or not they are ejecting
            // if they are, set to ejection position
            // otherwise stop the rollers
            if (ejecting) {
                leftEjectRoller.setPosition(leftRollerOutVal);
                rightEjectRoller.setPosition(rightRollerOutVal);
            } else {
                leftEjectRoller.setPosition(0.5);
                rightEjectRoller.setPosition(0.5);
            }
            if (!durationRollerEjection)
            ejecting = false;
        }

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
        if (iterNum%itersPerWrite == 0) {
            myCubeTrayPositions.modifyInt("LastLiftHeight", getliftPos());

            myCubeTrayPositions.updateFile();

             if (DEBUG) Log.i(TAG,"Wrote the following vals to file: (cubeTrayLogging)");
             if (DEBUG) Log.i(TAG + "Height", String.valueOf(getliftPos()));

        }
        iterNum++;

    }

    public void setToPoitionPID(int targetPos) {
        if (usingExMotor) {
            liftMotorEx.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            liftMotorEx.setMotorEnable();
            liftMotorEx.setTargetPosition(targetPos);

        } else {
            double correction = liftHeightPidController.getPIDCorrection(targetPos, getliftPos());

            if (DEBUG) Log.d(TAG, "lift Target position" + targetPos);
            if (DEBUG) Log.d(TAG, "lift position correction =" + correction);

            liftMotor.setPower(correction);
        }
    }

    private void setServoPos(TrayPositions position) {
        switch (position) {
            case GRABBED:
                grabberPos = closedGrabberPos;
                break;
            case OPEN:
                grabberPos = openGrabberPos;
                break;
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

        if (DEBUG) Log.i(TAG, "set servo positions to " + position.toString());

        if (DEBUG) Log.i(TAG, "set grabber position to: " + grabberPos);
        if (DEBUG) Log.i(TAG, "set blocker Position to : " + blockerPos);

    }

    public int getliftPos() {
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
    public void setZeroFromCompStart() {
        zeroPos = liftMotor.getCurrentPosition() - compStartPos;
    }

    public void setZeroFromLastOpmode() {
        int lastPos = myCubeTrayPositions.getInt("LastLiftHeight");
        if (lastPos == 0 || lastPos == -1) {
            if (DEBUG) Log.e(TAG, "might have been unnable to read Lift Height");
        } else {
            setZeroFromKnownPosition(lastPos);
        }
    }
    private void setZeroFromKnownPosition(int position){
        zeroPos = liftMotor.getCurrentPosition() - position;

    }

    public void setAutonomousMode(boolean val) {
        AutonomousMode = val;
    }

    public boolean isInLoadingPocket() {
        return (Math.abs(loadingPosTicks - liftMotor.getCurrentPosition()) < 400);
    }



    public void endDump() {






    }

}
