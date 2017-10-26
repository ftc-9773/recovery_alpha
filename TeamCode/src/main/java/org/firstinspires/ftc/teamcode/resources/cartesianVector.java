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

    public cartesianVector(){
        this.xComponent = 0;
        this.yComponent = 0;
        this.conversionVector = new polarVector();
    }
    public void set(double xcomp, double ycomp){
        xComponent = xcomp;
        yComponent = ycomp;
    }
    public polarVector cartToPolar(double xcomp, double ycomp){
        if(xComponent!=0) {
            conversionDir = Math.abs(Math.atan(ycomp / xcomp))*(180/3.141593);
        }else{
            conversionDir = 90;
        }
        if(xComponent<0 || ycomp<0) {
            conversionMag = -1*(xcomp * xcomp + ycomp * ycomp);
        }
        else{
            conversionMag = (xcomp * xcomp + ycomp * ycomp);
        }
        conversionVector.set(conversionDir, conversionMag);
        return conversionVector;
    }
}
