package org.firstinspires.ftc.teamcode.InitialTests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Vision.VumarkGlyphPattern;

/**
 * Created by michaelzhou on 11/22/17.
 */

@Autonomous(name = "Cryptobox Navigation", group="Navigation")
public class CryptoboxNavigationTest extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        while (opModeIsActive()){
            VumarkGlyphPattern pattern = new VumarkGlyphPattern();
            pattern.init(hardwareMap);
            RelicRecoveryVuMark mark = pattern.getColumn();
            //robot.moveDistance(mark==RelicRecoveryVuMark.LEFT ? farDist : mark==RelicRecoveryVuMark.CENTER ? midDist : mark==RelicRecoveryVuMark.RIGHT ? nearDist);
        }
    }

    //1. Initialize robot object and set robot direction
    //2. Move certain distance according to Vuforia reading
    //(3. Release the cub into the designated area)
}
