package org.firstinspires.ftc.teamcode.resources;

/**
 * Created by Nicky Eichenberger on 1/4/2018.
 */

public class Timer {
    long delayTimeMillis;
    long startTimeMillis;

    public Timer(double delayInSeconds) {
        delayTimeMillis = (long) delayInSeconds * 1000;
        startTimeMillis = System.currentTimeMillis();
    }

    public boolean isDone () {
        return ((System.currentTimeMillis() - startTimeMillis) > delayTimeMillis);
    }

    public boolean isDone(double delayInSeconds) {
        return ((System.currentTimeMillis() - startTimeMillis) > delayInSeconds*1000);
    }

    public double timePassedInSeconds() {return (System.currentTimeMillis() - startTimeMillis) / 1000; }
}
