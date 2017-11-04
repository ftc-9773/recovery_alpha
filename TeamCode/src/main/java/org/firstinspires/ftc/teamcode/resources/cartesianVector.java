package org.firstinspires.ftc.teamcode.resources;

/**
 * Created by Vikesh on 10/26/2017.
 */

public class cartesianVector {
    double xComponent;
    double yComponent;
    polarVector conversionVector;
    double conversionDir;
    double conversionMag;

    public void cartesianVector(){
        this.xComponent = 0;
        this.yComponent = 0;
        this.conversionVector = new polarVector();
    }
    public void set(double xcomp, double ycomp){
        xComponent = xcomp;
        yComponent = ycomp;
    }
    public polarVector cartToPolar(){
        if(xComponent!=0) {
            conversionDir = Math.abs(Math.atan(yComponent / xComponent))*(180/3.141593);
        }else{
            conversionDir = 90;
        }
        if(xComponent<0 || yComponent<0) {
            conversionMag = -1*(xComponent * xComponent + yComponent * yComponent);
        }
        else{
            conversionMag = (xComponent * xComponent + yComponent * yComponent);
        }
        conversionVector.set(conversionDir, conversionMag);
        return conversionVector;
    }
}
