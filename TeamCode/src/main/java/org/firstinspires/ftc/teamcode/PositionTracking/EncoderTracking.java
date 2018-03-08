package org.firstinspires.ftc.teamcode.PositionTracking;

import org.firstinspires.ftc.teamcode.infrastructure.Utilities;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveModule;
import org.firstinspires.ftc.teamcode.resources.Vector;

/**
 * Created by Nicky Eichenberger on 2/15/2018.
 */

public class EncoderTracking {

    // Constants
    private static final double wheelDiameter = 3;
    private static final double encoderTicksPerInch = (19.8 * 28) / (wheelDiameter * Math.PI);

    private static final double WHEEL_CENTER_RADIUS = 10.28125; // inches

    // Hardware Objects
    Gyro myGyro;
    SwerveModule[] wheels = new SwerveModule[4];

    double[] rotationDirection = new double[4];

    // Tracking variables
    long[] lastPos = new long[4];
    long[] currentPos = new long[4];

    double[] currentHeading = new double[4];

    double lastGyroHeading;
    double currentGyroHeading;


    // Units: inches
    double[] position = new double[2];

    public EncoderTracking (SwerveModule flw, SwerveModule frw, SwerveModule blw, SwerveModule brw, Gyro myGyro) {
        this.myGyro = myGyro;
        wheels[0] = flw;
        wheels[1] = frw;
        wheels[2] = blw;
        wheels[3] = brw;

        rotationDirection[0] = 1*Math.PI/4;
        rotationDirection[1] = 3*Math.PI/4;
        rotationDirection[2] = 7*Math.PI/4;
        rotationDirection[3] = 5*Math.PI/4;

        // Needs to be done twice to initialize all variables
        readCurrentPosition();
        readCurrentPosition();


        currentGyroHeading = myGyro.getHeading();

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

        // Update gyro headings:
        lastGyroHeading = currentGyroHeading;
        currentGyroHeading = myGyro.getHeading();

        // Add each wheel's distance to the total
        for (int i = 0; i< 4; i++) {
            // Make sure the wheel isn't just pivoting
            if (!wheels[i].isPivoting()) {

                // Calculate difference in position due to robot rotation, and subtract this from estimated displacement

                // Find local displacement and divide by encoder ticks per inch
                double dist = (currentPos[i] - lastPos[i]) / encoderTicksPerInch;
                Vector tempVector = new Vector(false, dist, currentHeading[i]);

                // Subtract rotation vector
                final double distFromRotation = Utilities.negToPosPi(currentGyroHeading - lastGyroHeading) * WHEEL_CENTER_RADIUS;
                tempVector.addVector(false, -distFromRotation, rotationDirection[i]);

                // Shift to robot perspective
                tempVector.shiftAngle(-currentGyroHeading);

                xDisp += tempVector.getX();
                yDisp += tempVector.getY();
            }
        }
        xDisp/=4;
        yDisp/=4;


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
