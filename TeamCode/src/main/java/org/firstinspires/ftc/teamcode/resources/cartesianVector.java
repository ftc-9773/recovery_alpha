package org.firstinspires.ftc.teamcode.resources;

/**
 * Created by Vikesh on 10/26/2017.
 */

public class cartesianVector {
    private double xComponent = 0;
    private double yComponent = 0;
    private polarVector conversionVector = new polarVector(0,0);
    private double conversionDir = 0;
    private double conversionMag = 0;

    //constructor
    public cartesianVector(double x, double y) {
        this.xComponent = x;
        this.yComponent = y;
    }

    //sets x and y components
    public void set(double xcomp, double ycomp) {
        xComponent = xcomp;
        yComponent = ycomp;
    }

    public double getx(){
        return xComponent;
    }

    public double gety(){
        return yComponent;
    }

    //returns a polarVector object with the same direction and magnitude as the cartesianVector.
    public polarVector cartToPolar() {
        if (xComponent != 0) {
            conversionDir = Math.toDegrees(Math.atan(yComponent / xComponent));
        }
        conversionMag = Math.sqrt(xComponent * xComponent + yComponent * yComponent)*(xComponent/Math.abs(xComponent));
        conversionVector.set(conversionDir, conversionMag);
        return conversionVector;
    }
}
