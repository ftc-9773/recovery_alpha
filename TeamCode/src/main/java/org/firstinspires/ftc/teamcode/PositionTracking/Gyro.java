package org.firstinspires.ftc.teamcode.PositionTracking;

import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

/**
 * Created by nicky on 11/22/17.
 */

public class Gyro {

    BNO055IMU imuLeft;
    //BNO055IMU imuRight;

    private double currentPosition;

    private double lastReadTime = -1;
    private static final int minReadDeltaTime = 40;

    private double zeroPositionLeft = 0;

    //private double zeroPositionRight = 0;

    private static String TAG = "9773_Gyro";
    private static boolean DEBUG = false;

    // Init
    public Gyro (HardwareMap hardwareMap) {
        ///// Initialize the IMU /////
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit            = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit            = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled       = false;
        parameters.mode                 = BNO055IMU.SensorMode.IMU;
        parameters.loggingTag           = "IMU";
        imuLeft                         = hardwareMap.get(BNO055IMU.class, "imuLeft");

        imuLeft.initialize(parameters);
    }

    public double getImuReading() {
        return -imuLeft.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle;
    }

    public double getHeading () {

        // Only read again if it has been at least 80 ms
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastReadTime > minReadDeltaTime) {
            currentPosition = setOnZeroTwoPi(getImuReading() - zeroPositionLeft);
            lastReadTime = currentTime;
            if (DEBUG) { Log.e(TAG, "Got new gyro position - Read: " + currentPosition + "Actual position - " + getImuReading()); }
        }

        return currentPosition;
    }


    public void setZeroPosition() {
        this.zeroPositionLeft = getImuReading();
        currentPosition = 0;
    }


    public double setOnZeroTwoPi(double angle) {
        while (angle > 2*Math.PI) {
            angle -= 2*Math.PI;
        }
        while (angle < 0) {
            angle += 2*Math.PI;
        }
        return angle;
    }

}