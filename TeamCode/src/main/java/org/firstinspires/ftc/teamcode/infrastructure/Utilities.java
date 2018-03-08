package org.firstinspires.ftc.teamcode.infrastructure;

/**
 * Created by nicky on 3/7/18.
 */

public class Utilities {

    public static double modPi(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    public static  double negToPosPi (double num) {
        if (num < -Math.PI) {
            return num + 2*Math.PI;
        } else if (num > Math.PI) {
            return num - 2*Math.PI;
        } else {
            return num;
        }
    }

}
