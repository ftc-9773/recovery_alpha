package org.firstinspires.ftc.teamcode.HardwareControl;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Created by vikesh on 2/3/18.
 */

public class DistanceColorSensor {

    private DistanceSensor distanceSensor;
    private double derivative = 0;
    private double oldVal = 0;
    private boolean firstRun = true;
    private long lastTime = 0;

    public DistanceColorSensor(HardwareMap hwMap, String DeviceName){
        distanceSensor = hwMap.get(DistanceSensor.class, DeviceName);
    }

    public double getDistance(DistanceUnit unit){
        return distanceSensor.getDistance(unit);
    }
    //run this in a loop
    public double getDerivative(DistanceUnit distUnit){
        if(firstRun){
            firstRun = false;
            lastTime = System.currentTimeMillis() - 1;
            oldVal = distanceSensor.getDistance(distUnit);
        }
        derivative = (System.currentTimeMillis() - lastTime)/(distanceSensor.getDistance(distUnit) - oldVal);
        return derivative;
    }
}
