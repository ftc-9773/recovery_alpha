package org.firstinspires.ftc.teamcode.PositionTracking;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.HardwareControl.SwerveModule;
import org.firstinspires.ftc.teamcode.resources.Vector;

/**
 * Created by Nicky Eichenberger on 2/15/2018.
 */

public class EncoderTracking {

    // Constants
    private static final double wheelDiameter = 3;
    private static final double encoderTicksPerInch = (19.8 * 28) / (wheelDiameter * Math.PI);

    // Hardware Objects
    Gyro myGyro;
    SwerveModule[] wheels = new SwerveModule[4];

    // Tracking variables
    long[] lastPos = new long[4];
    long[] currentPos = new long[4];

    double[] currentHeading = new double[4];


    // Units: inches
    double[] position = new double[2];

    public EncoderTracking (SwerveModule flw, SwerveModule frw, SwerveModule blw, SwerveModule brw, Gyro myGyro) {
        this.myGyro = myGyro;
        wheels[0] = flw;
        wheels[1] = frw;
        wheels[2] = blw;
        wheels[3] = brw;
       
        // Needs to be done twice to initialize all variables
        readCurrentPosition();
        readCurrentPosition();

        // Sets the position to zero
        position[0] = 0;
        position[1] = 0;

    }

    private void readCurrentPosition() {
        for(int i = 0; i < 4; i++) {
            lastPos[i] = currentPos[i];

            currentPos[i] = wheels[i].getEncoderCount();
            currentHeading[i] = wheels[i].getModulePosition();
        }
    }

    public void updatePosition() {
        readCurrentPosition();
        double xDisp = 0;
        double yDisp = 0;

        // Add each wheel's distance to the total
        for (int i = 0; i< 4; i++) {
            // Make sure the wheel isn't just pivoting
            if (!wheels[i].isPivoting()) {
                double dist = currentPos[i] - lastPos[i];
                Vector tempVector = new Vector(false, dist, currentHeading[i] + myGyro.getHeading());
                xDisp += tempVector.getX();
                yDisp += tempVector.getY();
            }
        }
        xDisp/=4;
        yDisp/=4;

        xDisp/=encoderTicksPerInch;
        yDisp/=encoderTicksPerInch;


        position[0] += xDisp;
        position[1] += yDisp;
    }

    public void setPosition (double xPosition, double yPosition) {
        position[0] = xPosition;
        position[1] = yPosition;
    }

    public double getXinInches() { return position[0]; }
    public double getYinInches() { return position[1]; }

}
