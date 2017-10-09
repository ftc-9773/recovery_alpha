package org.firstinspires.ftc.teamcode.initialSwerveDrive;

import com.qualcomm.robotcore.eventloop.opmode.*;
import java.lang.Math;
/**
 * Created by Vikesh on 10/7/2017.
 */
@Disabled
public class swerveDriveActions extends LinearOpMode{
    double magnitude;
    double direction;
    double degrees;
    double gpx;
    double gpy;
    private int servoRange; // range of the servo in degrees.
    private int gpNumber; //gpNumber is the number of the gamepad.

    public swerveDriveActions(int gpNumber, int servoRange) {
        this.gpNumber = gpNumber;
        this.servoRange = servoRange;
    }

    double gpToMagnitude() {
        if (gpNumber == 1) { //if gpNumber == 1, gamepad1 values will be used, else gamepad2 will be used.
            gpx = gamepad1.left_stick_x;
            gpy = gamepad1.left_stick_y;
            magnitude = gpx*gpx+gpy*gpy;
        }else{
            gpx = gamepad2.left_stick_x;
            gpy = gamepad2.left_stick_y;
            magnitude = gpx*gpx+gpy*gpy;
        }
        return magnitude;
    }

    double gpToDirection(){
        if (gpNumber != 1) { //if gpNumber == 1, gamepad1 values will be used, else gamepad2 will be used.
            gpx = gamepad1.left_stick_x;
            gpy = gamepad1.left_stick_y;
            if (gpx<0){
                degrees = 180 + Math.atan(gpy/gpx)*(180/3.14159);
            }else if (gpx > 0) {
                degrees = Math.atan(gpy/gpx)*(180/3.14159);
            }else if(gpx == 0 & gpy != 0){
                degrees = 90;
            }

        }else{
            gpx = gamepad2.left_stick_x;
            gpy = gamepad2.left_stick_y;
            if (gpx<0){
                degrees = 180 + Math.atan(gpy/gpx)*(180/3.14159);
            }else if (gpx > 0) {
                degrees = Math.atan(gpy/gpx)*(180/3.14159);
            }else if(gpx == 0 & gpy != 0){
                degrees = 90;
            }

        }
        direction = degrees/servoRange+.5;
        return direction;
    }

    @Override
    public void runOpMode() throws InterruptedException {
    }
}
