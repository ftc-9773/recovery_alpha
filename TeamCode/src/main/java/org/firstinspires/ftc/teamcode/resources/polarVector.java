package org.firstinspires.ftc.teamcode.resources;

/**
 * Created by Vikesh on 10/26/2017.
 */

public class polarVector {
    public double direction;
    public double magnitude;
    public cartesianVector conversionVector;

    public void polarVector() {
        this.direction = 0;
        this.magnitude = 0;
        conversionVector = new cartesianVector();
    }

    public void set(double dir, double mag) {
        direction = dir;
        magnitude = mag;
    }
}
