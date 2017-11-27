package org.firstinspires.ftc.teamcode.InitialTests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.HardwareControl.CubeTrayController;
import org.firstinspires.ftc.teamcode.HardwareControl.DriveWithPID;
import org.firstinspires.ftc.teamcode.HardwareControl.SwerveController;
import org.firstinspires.ftc.teamcode.PositionTracking.Gyro;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;

@Autonomous(name = "Cryptobox Navigation", group="Navigation")
public class CryptoboxNavigationTest extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException {

        Gyro gyro = new Gyro(hardwareMap);
        SwerveController swerveController = new SwerveController(hardwareMap, gyro, false, telemetry);
        DriveWithPID driver = new DriveWithPID(swerveController, gyro);
        CubeTrayController cubeTrayController = new CubeTrayController(hardwareMap, null);

        VumarkGlyphPattern pattern = new VumarkGlyphPattern(hardwareMap);
        double dist = 0;

        waitForStart();

        while(!opModeIsActive()) {
            RelicRecoveryVuMark mark = pattern.getColumn();
            dist = mark == RelicRecoveryVuMark.LEFT ? 43.0625 : mark == RelicRecoveryVuMark.CENTER ? 35.5625 : 28.0625;
            telemetry.addData("column position: ", mark);
            telemetry.update();
        }

        while (opModeIsActive()){
            driver.driveStraight(true, 1, 0, dist);

            cubeTrayController.goToStowPos();
            cubeTrayController.updateServos();
        }
    }
}
