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

    double grabberPos = 0;
    double blockerPos = 0;




    public boolean AutonomousMode = false;

    // create motor and servo objects
    private Gamepad gamepad1;
    private DcMotor liftMotor;
    private Servo grabServo;
    private Servo blockServo;
    AnalogInput limitSwitch;

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
        lowPosTicks = myCubeTrayPositions.getInt("bottomPosTicks");
        midPosTicks = myCubeTrayPositions.getInt("middlePosTicks");
        highPosTicks = myCubeTrayPositions.getInt("topPosTicks");
        compStartPos = myCubeTrayPositions.getInt("compStartPos");

        openGrabberPos = myCubeTrayPositions.getDouble("openGrabberPos");
        closedGrabberPos = myCubeTrayPositions.getDouble("grabbedGrabberPos");
        loadGrabberPos = myCubeTrayPositions.getDouble("loadGrabberPos");
        startGrabberPos = myCubeTrayPositions.getDouble("startGrabberPos");

        blockBlockerPos = myCubeTrayPositions.getDouble("blockBlockerPos");
        leftStowBlockerPos = myCubeTrayPositions.getDouble("stowLeftBlockerPos");
        rightBlockerPos = myCubeTrayPositions.getDouble("rightBlockerPos");


        // initialize the motor and servos
        this.gamepad1 = gamepad1;
        liftMotor = hwMap.dcMotor.get("ctlMotor");
        limitSwitch = hwMap.analogInput.get("ctlLimitSwitch");
        grabServo = hwMap.servo.get("ctgServo");
        blockServo = hwMap.servo.get("ctbServo");


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
        setServoPos(TrayPositions.OPEN);
    }

    public void setToPos(LiftFinalStates state){
        switch (state){
            case LOADING:
                liftTargetPosition = loadingPosTicks;
                setServoPos(TrayPositions.LOADING);
                break;
            case LOW:
                liftTargetPosition = lowPosTicks;
                setServoPos(TrayPositions.GRABBED);
                break;
            case MID:
                liftTargetPosition = midPosTicks;
                setServoPos(TrayPositions.GRABBED);
                break;
            case HIGH:
                liftTargetPosition = highPosTicks;
                setServoPos(TrayPositions.GRABBED);
                break;
            case JEWELC:
                liftTargetPosition = lowPosTicks;
                setServoPos(TrayPositions.JEWEL_CENTER);
                break;
            case JEWELL:
                liftTargetPosition = lowPosTicks;
                setServoPos(TrayPositions.JEWEL_LEFT);
                break;
            case JEWELR:
                liftTargetPosition = lowPosTicks;
                setServoPos(TrayPositions.JEWEL_RIGHT);
            default:
                break;
        }
        updatePosition();
    }

    public void updatePosition(){
        setToPoitionPID(liftTargetPosition);

    }

    public void setToPoitionPID(int targetPos){
        if (!liftMotor.getMode().equals(DcMotor.RunMode.RUN_WITHOUT_ENCODER)){
            liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        double correction = liftHeightPidController.getPIDCorrection(targetPos, getliftPos());

        Log.d(TAG,"lift position correction =" + correction);

        liftMotor.setPower(correction);
    }
    private void setServoPos(TrayPositions position){
        switch (position){
            case GRABBED:
                grabberPos = closedGrabberPos;
                blockerPos = leftStowBlockerPos;
                break;
            case OPEN:
                grabberPos = openGrabberPos;
                blockerPos = leftStowBlockerPos;
                break ;
            case LOADING:
                grabberPos = loadGrabberPos;
                blockerPos = blockBlockerPos;
                break;
            case START_POS:
                blockerPos = startGrabberPos;
                blockerPos = leftStowBlockerPos;
            case JEWEL_CENTER:
                grabberPos = openGrabberPos;
                blockerPos = blockBlockerPos;
                break;
            case JEWEL_LEFT:
                grabberPos = openGrabberPos;
                blockerPos = leftStowBlockerPos;
                break;
            case JEWEL_RIGHT:
                grabberPos = openGrabberPos;
                blockerPos = rightBlockerPos;
                break;
            default:
                break;
        }

        grabServo.setPosition(grabberPos);
        blockServo.setPosition(blockerPos);

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



}
