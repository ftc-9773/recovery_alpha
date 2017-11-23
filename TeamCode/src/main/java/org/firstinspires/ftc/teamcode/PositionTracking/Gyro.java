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

    private double lastReadTime = 0;
    private static final int minReadDeltaTime = 50;

    private double zeroPoitionLeft = 0;
    //private double zeroPositionRight = 0;

    private static String TAG = "9773_Gyro";
    private static boolean DEBUG = true;

    // Init
    public Gyro (HardwareMap hardwareMap) {
        ///// Initialize the IMU /////
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.angleUnit           = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";

        imuLeft = hardwareMap.get(BNO055IMU.class, "imuLeft");
        imuLeft.initialize(parameters);

        //imuRight = hardwareMap.get(BNO055IMU.class, "imuRight");
        //imuRight.initialize(parameters);
    }

    public double getHeading () {

        // Only read again if it has been at least 50 ms
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastReadTime > minReadDeltaTime) {
            lastReadTime = currentTime;
            currentPosition = imuLeft.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.YXY, AngleUnit.RADIANS).firstAngle - zeroPoitionLeft;
        }

        // Return the current position
        return currentPosition;
    }

    public void setZeroPosition() {
        this.zeroPoitionLeft = imuLeft.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.YXY, AngleUnit.RADIANS).firstAngle;
        currentPosition = 0;
    }


    public void logHeading () {
        //double yawXLeft = imuLeft.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS).firstAngle;
        double yawYLeft = imuLeft.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS).secondAngle;
        //double yawZLeft = imuLeft.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS).thirdAngle;

        //double yawXRight = imuRight.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS).firstAngle;
        //double yawYRight = imuRight.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS).secondAngle;
        //double yawZRight = imuRight.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS).thirdAngle;


        //if (DEBUG) { Log.e(TAG, "Left - Time: " + System.currentTimeMillis() + "  Heading XYZ: " + yawXLeft + " " + yawYLeft + " " + yawZLeft); }
        //if (DEBUG) { Log.e(TAG, "Right - Time: " + System.currentTimeMillis() + "  Heading XYZ: " + yawXRight + " " + yawYRight + " " + yawZRight); }


        if (DEBUG) { Log.e(TAG, "Time: " + System.currentTimeMillis() + "Left - Right: " + yawYLeft); }
    }



}