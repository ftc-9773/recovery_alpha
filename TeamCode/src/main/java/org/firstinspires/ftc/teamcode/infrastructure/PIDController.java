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

    public PIDController(double KP, double KI, double KD){
        this.KP = KP;
        this.KI = KI;
        this.KD = KD;
    }
    public double getPIDCorrection(double target, double actual){
        //If this is the first time getPIDCorrection has been run, set lastTime to the current time to avoid excessive error.
        if(firstRun) {
            prevError = target-actual;
            lastTime = System.currentTimeMillis();
            firstRun = false;
        }
        deltaTime = System.currentTimeMillis() - lastTime;
        error = target-actual;
        integral = integral + (error*deltaTime);
        derivative = (error-prevError)/deltaTime;
        output = KP*error+KI*integral+KD*derivative;
        return output;
    }
}
