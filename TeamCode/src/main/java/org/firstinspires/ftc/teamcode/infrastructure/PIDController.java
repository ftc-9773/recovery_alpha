package org.firstinspires.ftc.teamcode.infrastructure;

/**
 * Created by Vikesh on 11/20/2017.
 */

public class PIDController {
    private double KP;
    private double KI;
    private double KD;
    private double integral;
    private double derivative;
    private double error;
    private double prevError;
    private double output;
    private long lastTime;
    private long deltaTime;
    private boolean firstRun = true;

    private static int maxDeltaTime = 500;

    public PIDController (double KP, double KI, double KD) {
        this.KP = KP;
        this.KI = KI;
        this.KD = KD;
    }

    public double getPIDCorrection(double error) {
        // calculate helper variables
        deltaTime = System.currentTimeMillis() - lastTime;

        // If it is the first run, just return proportional error as i and d cannot be cauculated yet
        if (firstRun || deltaTime > maxDeltaTime) {
            firstRun = false;
            return error * KP;
        } else {
            // Calculate I and D errors
            integral = integral + (error * deltaTime);
            derivative = (error - prevError) / deltaTime;
            output = KP * error + KI * integral + KD * derivative;
        }
        // Set previous values for next time

        prevError = error;
        lastTime = System.currentTimeMillis();

        return output;
    }

    public double getPIDCorrection(double target, double actual) {
        return getPIDCorrection(target - actual);
    }
}
