package org.firstinspires.ftc.teamcode.infrastructure;

/**
 * Created by Nicky Eichenberger on 3/2/2018.
 */

public class PIDWithBaseValue {

    private double KP;
    private double KI;
    private double KD;
    private double KE;

    private double BASE_VALUE;

    private double integral;
    private double derivative;
    private double prevError;
    private double output;
    private long lastTime;
    private long deltaTime;
    private boolean firstRun = true;

    private boolean useExponential;

    private static int maxDeltaTime = 800;
    private static boolean DEBUG = true;

    public PIDWithBaseValue (double KP, double KE, double KI, double KD, double baseValue) {
        this.KP = KP;
        this.KI = KI;
        this.KD = KD;
        this.KE = KE;
        this.BASE_VALUE = baseValue;
        useExponential = true;
    }

    public PIDWithBaseValue (double KP, double KI, double KD, double baseValue) {
        this.KP = KP;
        this.KI = KI;
        this.KD = KD;
        this.BASE_VALUE = baseValue;
        useExponential = false;
    }

    public double getPIDCorrection(double error) {
        // calculate helper variables
        deltaTime = System.currentTimeMillis() - lastTime;

        //Calculate exponential error
        double expError = 1;
        if (useExponential) {expError = Math.pow(Math.abs(error), KE - 1); }

        // If it is the first run, just return proportional error as i and d cannot be cauculated yet
        if (firstRun || deltaTime > maxDeltaTime) {
            firstRun = false;
            return error * expError * KP;
        } else {
            // Calculate I and D errors
            integral = integral + (error * deltaTime);
            derivative = (error - prevError) / deltaTime;
            output = KP * error * expError + KI * integral + KD * derivative;
        }
        // Set previous values for next time

        prevError = error;
        lastTime = System.currentTimeMillis();

        // add on the base value (min) and cap to max value
        if (output > 0) {
            output += BASE_VALUE;
        } else {
            output -= BASE_VALUE;
        }

        return output;
    }

    public double getPIDCorrection(double target, double actual) {
        return getPIDCorrection(target - actual);
    }

}
