package org.firstinspires.ftc.teamcode.resources;

/**
 * Created by Nicky Eichenberger on 1/4/2018.
 */

public class Timer {
    long delayTimeMillis = 0;
    long startTimeMillis = 0;
    boolean isInit = false;

    public Timer(double delayInSeconds) {
        isInit = true;
        delayTimeMillis = (long) delayInSeconds * 1000;
        startTimeMillis = System.currentTimeMillis();
    }

    public boolean isDone () {
        if(isInit) {
            return ((System.currentTimeMillis() - startTimeMillis) > delayTimeMillis);
        }
        else{
            return false;
        }
    }

    public boolean isDone(double delayInSeconds) {
        return ((System.currentTimeMillis() - startTimeMillis) > delayInSeconds*1000);
    }

    public double timePassedInSeconds() {return (System.currentTimeMillis() - startTimeMillis) / 1000; }
}
