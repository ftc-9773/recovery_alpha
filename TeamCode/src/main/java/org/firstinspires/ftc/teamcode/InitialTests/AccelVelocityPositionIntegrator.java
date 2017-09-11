package org.firstinspires.ftc.teamcode.InitialTests;

/**
 * Created by eichen on 9/10/17.
 */

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;


/**
 * {@link com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator} is an integrator that doesn't actually
 * integrate accelerations, but merely reports them in the logcat log. This is a debugging
 * and demonstration tool, little more.
 */
public class AccelVelocityPositionIntegrator implements BNO055IMU.AccelerationIntegrator
{
    BNO055IMU.Parameters parameters;
    Position lastPos, currPos;
    Velocity lastVelocity, currVelocity;
    Acceleration lastAccel, currAccel;
    long updateCount;

    @Override public void initialize(BNO055IMU.Parameters parameters, Position initialPosition, Velocity initialVelocity)
    {
        this.parameters = parameters;
        // grab position and speed, assuming identical acquisitionTime for both
        currPos = initialPosition;
        lastPos = null;
        currVelocity = initialVelocity;
        lastVelocity = null;
        currAccel = null;
        lastAccel = null;
        updateCount = 0;
    }

    @Override public Position getPosition()
    {
        return currPos == null ? new Position() : currPos;
    }

    @Override public Velocity getVelocity()
    {
        return this.currVelocity == null ? new Velocity() : this.currVelocity;
    }

    @Override public Acceleration getAcceleration()
    {
        return this.currAccel == null ? new Acceleration() : this.currAccel;
    }

    @Override public void update(Acceleration linearAcceleration)
    {

        // We should always be given a timestamp here
        if (linearAcceleration.acquisitionTime != 0)
        {
            if (updateCount++ == 0) {
                // first update; don't use it to get the accurate time
                this.currAccel = linearAcceleration;
                //if (linearAcceleration.unit != this.currPos.unit || linearAcceleration.unit != this.currVelocity.unit) {
                //throw new java.lang.RuntimeException("mistmatch of units");
                //}
            } else {
                // acceleration
                this.lastAccel = this.currAccel;
                this.currAccel = linearAcceleration;

                // delta time
                double deltaT = ((double) this.currAccel.acquisitionTime - (double) this.lastAccel.acquisitionTime) * 1e-9;

                // speed
                this.lastVelocity = this.currVelocity;
                this.currVelocity.xVeloc = this.currVelocity.xVeloc + 0.5 * (this.lastAccel.xAccel + this.currAccel.xAccel) * deltaT;
                this.currVelocity.yVeloc = this.currVelocity.yVeloc + 0.5 * (this.lastAccel.yAccel + this.currAccel.yAccel) * deltaT;
                this.currVelocity.zVeloc = this.currVelocity.zVeloc + 0.5 * (this.lastAccel.zAccel + this.currAccel.zAccel) * deltaT;
                this.currVelocity.acquisitionTime = linearAcceleration.acquisitionTime;
                // position
                this.lastPos = this.currPos;
                this.currPos.x= this.currPos.x + 0.5 * (this.lastVelocity.xVeloc + this.currVelocity.xVeloc) * deltaT;
                this.currPos.y= this.currPos.y + 0.5 * (this.lastVelocity.yVeloc + this.currVelocity.yVeloc) * deltaT;
                this.currPos.z= this.currPos.z + 0.5 * (this.lastVelocity.zVeloc + this.currVelocity.zVeloc) * deltaT;
                if (parameters.loggingEnabled)
                {
                    RobotLog.vv(parameters.loggingTag, "dt=%.3fs accel=%s, velocity=%s, position=%s",
                            (currAccel.acquisitionTime - lastAccel.acquisitionTime)*1e-9, currAccel, currVelocity, currPos);
                }
            }
        }

    }
}
