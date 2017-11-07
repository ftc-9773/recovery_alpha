package org.firstinspires.ftc.teamcode.resources;

/**
 * Created by Vikesh on 10/26/2017.
 */

public class polarVector {

    //declarations
    private double direction;
    private double magnitude;
    private cartesianVector conversionVector;

    //constructor
    public polarVector(double dir, double mag) {
        this.direction = dir;
        this.magnitude = mag;
        conversionVector = new cartesianVector(0,0);
    }

    //sets direction and magnitude
    public void set(double dir, double mag) {
        direction = dir;
        magnitude = mag;
    }

    //returns the direction
    public double getDir(){
        return direction;
    }

    //returns the magnitude
    public double getMag(){
        return magnitude;
    }
}
