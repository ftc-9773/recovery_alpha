package org.firstinspires.ftc.teamcode.resources;

/**
 * Created by Vikesh on 10/26/2017.
 */

public class cartesianVector {
    double xComponent = 0;
    double yComponent = 0;
    polarVector conversionVector = new polarVector();
    double conversionDir = 0;
    double conversionMag = 0;

    public void cartesianVector() {
    }

    public void set(double xcomp, double ycomp) {
        xComponent = xcomp;
        yComponent = ycomp;
    }

    public polarVector cartToPolar() {
        if (xComponent != 0) {
            conversionDir = Math.toDegrees(Math.atan(yComponent / xComponent));
        }
        conversionMag = Math.sqrt(xComponent * xComponent + yComponent * yComponent);
        conversionVector.set(conversionDir, conversionMag);
        return conversionVector;
    }
}
