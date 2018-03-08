package org.firstinspires.ftc.teamcode.AutonomousDriving;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.EncoderTracking;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.resources.Vector;

/**
 * Created by nicky on 3/7/18.
 */

public class PointToPointDriving {

    // For stopIsRequested
    LinearOpModeCamera opmode;

    // My Classes
    EncoderTracking myEncoderTracker;
    SwerveController mySwerveController;

    // Actual variables
    double acceleration = 4 / 2 / 1000000;// m/s ** converted to power per ms


    // INIT
    public PointToPointDriving(LinearOpModeCamera linearOpModeCamera, EncoderTracking myEncoderTracker, SwerveController mySwerveController, DriveWithPID myDriveWithPID) {
        this.opmode = linearOpModeCamera;
        this.myEncoderTracker = myEncoderTracker;
        this.mySwerveController = mySwerveController;
    }

    // Driving function

    public void driveToPoint(double minSpeed, double maxSpeed, double xCord, double yCord, double headingDegrees) {

        // Variables
        double currentSpeed = minSpeed;
        Vector distanceVector = new Vector(true, 0, 0);

        long lastTime;
        long currentTime = System.currentTimeMillis();



        while (!opmode.isStopRequested()) {

            // Calculate error in position
            double xError = xCord - myEncoderTracker.getXinInches();
            double yError = yCord - myEncoderTracker.getYinInches();

            boolean exit = false;

            distanceVector.set(true, xCord, yCord);


            // Set the current time
            lastTime = currentTime;
            currentTime = System.currentTimeMillis();



            // Acceleration and deceleration

            // Calculate distance to start decelerating
            final double minDist = (currentSpeed*currentSpeed + minSpeed*minSpeed) / 2;

            // If you are far enough away, accelerate
            if (distanceVector.getMagnitude() > minDist && currentSpeed < maxSpeed) {
                currentSpeed += acceleration * (currentTime - lastTime);

                // Verify that speed is less than max
                if (currentSpeed > maxSpeed) currentSpeed = maxSpeed;

            // If you are close enough, decelerate
            } else if (distanceVector.getMagnitude() <= minDist && currentSpeed > minSpeed) {
                currentSpeed -= acceleration * (currentTime - lastTime);

                // Verify that speed is less than min
                if (currentSpeed < minSpeed) currentSpeed = minSpeed;
            }


            // Check to see if it has driven the distance
            if (distanceVector.getMagnitude() < 0.05) break;

            // Drive
            mySwerveController.steerSwerve(false, currentSpeed, distanceVector.getAngle(), 0, headingDegrees);
            mySwerveController.moveRobot(true);

        }

        mySwerveController.stopRobot();

    }


}
