package org.firstinspires.ftc.teamcode.HardwareControl;

/**
 * Created by zacharye on 2/24/18.
 */

public interface CubeTrays {


     void updateFromGamepad();
     void setToPos(LiftFinalStates state);
     void dump();
     void updatePosition();
     int getliftPos();
    int getRawLiftPos();

    // notice this has limited functionality for the slot tray, as it is unnable to work with the motor.
    void setServoPos(CubeTray.TrayPositions trayPos);

    void home ();
    void setZeroFromCompStart();
    void setZeroFromLastOpmode();
    void setAutonomousMode(boolean val);

    void startDump();
    void stopDump();
    void endDump();
    void openDump();
    boolean isInLoadingPocket();


}
