package org.firstinspires.ftc.teamcode.Nonsense;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TankDrivebase extends AbstractDrivebase {
    DcMotor leftDriveMotor0, leftDriveMotor1, rightDriveMotor0, rightDriveMotor1;
    HardwareMap hwmp;

    public TankDrivebase(String ldm0, String ldm1, String rdm0, String rdm1,HardwareMap hwmp ){
        this.hwmp = hwmp;
        this.leftDriveMotor0 = hwmp.dcMotor.get(ldm0);
        this.leftDriveMotor1 = hwmp.dcMotor.get(ldm1);
        this.rightDriveMotor0 = hwmp.dcMotor.get(rdm0);
        this.rightDriveMotor1 = hwmp.dcMotor.get(rdm1);
    }
    public void setRightPow(double pow){
        rightDriveMotor0.setPower(pow);
        rightDriveMotor1.setPower(pow);

    }
    public void setLeftPow (double pow){
        leftDriveMotor0.setPower(-pow);
        leftDriveMotor1.setPower(-pow);
    }
}
