package org.firstinspires.ftc.teamcode.resources;

/**
 * Created by Nicky on 11/7/2017.
 */


public class Vector {

    private double xComponent = 0;
    private double yComponent = 0;

    // Corrects onto the correct range of 0 to 2Pi
    private double modPi(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    //constructor
    public Vector(boolean isCartesian, double xMag, double yAng) {
        if (isCartesian) {
            xComponent = xMag;
            yComponent = yAng;
        } else {
            if (xMag < 0) {
                xMag *= -1;
                yAng += Math.PI;
            }

            xComponent = Math.cos(modPi(yAng)) * xMag;
            yComponent = Math.sin(modPi(yAng)) * xMag;
        }
    }

    //sets x and y components
    public void set(boolean isCartesian, double xMag, double yAng) {
        if (isCartesian) {
            xComponent = xMag;
            yComponent = yAng;
        } else {
            if (xMag < 0) {
                xMag *= -1;
                yAng += Math.PI;
            }

            xComponent = Math.cos(modPi(yAng)) * xMag;
            yComponent = Math.sin(modPi(yAng)) * xMag;
        }
    }

    // Add a vector
    public void addVector (boolean isCartesian, double xMag, double yAng) {
        if (isCartesian) {
            xComponent += xMag;
            yComponent += yAng;
        } else {
            xComponent += xMag * Math.cos(yAng);
            yComponent += xMag * Math.sin(yAng);
        }
    }

    //Fetching values
    public double getX () { return xComponent; }
    public double getY () { return yComponent; }

    public double getMagnitude () { return Math.sqrt( Math.pow(xComponent, 2) + Math.pow(yComponent, 2)); }

    public double getAngle () {
        final double angle = Math.atan2(yComponent, xComponent);
        if (angle < 0) {
            return angle + 2 * Math.PI;
        }  else {
            return Math.atan2(yComponent, xComponent);
        }
    }


}