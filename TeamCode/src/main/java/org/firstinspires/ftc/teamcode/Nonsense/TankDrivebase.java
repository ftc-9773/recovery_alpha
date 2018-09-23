package org.firstinspires.ftc.teamcode.Nonsense;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * @version 1.0
 * @author Cadence
 * driver for the tank drive on the robot in three days robot.
 * */
public class TankDrivebase extends AbstractDrivebase {
    DcMotor leftDriveMotor0, leftDriveMotor1, rightDriveMotor0, rightDriveMotor1;
    HardwareMap hwmp;

    /**
     * @param ldm0 The name of the first left drive motor.
     * @param ldm1 The name of the second left drive motor
     * @param rdm0 The name of the first right dive motor
     * @param rdm1 The name of the second right drive motor
     * @param hwmp The hardware map defined in the opmode
     * */
    public TankDrivebase(String ldm0, String ldm1, String rdm0, String rdm1,HardwareMap hwmp ){
        this.hwmp = hwmp;
        this.leftDriveMotor0 = hwmp.dcMotor.get(ldm0);
        this.leftDriveMotor1 = hwmp.dcMotor.get(ldm1);
        this.rightDriveMotor0 = hwmp.dcMotor.get(rdm0);
        this.rightDriveMotor1 = hwmp.dcMotor.get(rdm1);
    }
    /**
     * @param pow Double between -1 and 1 specifying the power. Negative numbers are backwards
     * */
    public void setRightPow(double pow){
        this.rightDriveMotor0.setPower(pow);
        this.rightDriveMotor1.setPower(pow);
    }
    /**
     * @param pow Double between -1 and 1 specifying the power. Negative numbers are backwards
     * */
    public void setLeftPow (double pow){
        this.leftDriveMotor0.setPower(-pow);
        this.leftDriveMotor1.setPower(-pow);
    }
}
