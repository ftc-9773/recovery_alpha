package org.firstinspires.ftc.teamcode.initialSwerveDrive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.infrastructure.ButtonStatus;
import org.firstinspires.ftc.teamcode.infrastructure.SafeJsonReader;

import java.lang.*;

/*
where are the file on the phone?
  storage / emulated / FIRST / team9773

  1) open terminal tab on android studio

  2) get to the right dir on the computer, for example
  cd TeamCode/src/main/java/org/firstinspires/ftc/teamcode/json/

  3) push a file there: (location of adb on mac: $HOME/Library/Android/sdk/platform-tools
  adb push myfile.json /sdcard/FIRST/team9773/json18/

  get a file from there
  adb pull  /sdcard/FIRST/team9773/json18/myfile.json


*/

/**
 * Created by robocracy on 10/30/17.
 */

@TeleOp(name="JasonTester")
public class JasonTester extends LinearOpMode {

    private SafeJsonReader swerveInfo;
    private double incrementServo0;
    private double incrementServo1;
    private double incrementServo2;
    private double incrementServo3;
    private boolean isGreat;
    private String whoIsBest;
    private int teamYears;

    private void readServoInfo() {
        swerveInfo = new SafeJsonReader("test");
        incrementServo0 = swerveInfo.getDouble("increment0");
        incrementServo1 = swerveInfo.getDouble("increment1");
        incrementServo2 = swerveInfo.getDouble("increment2");
        incrementServo3 = swerveInfo.getDouble("increment3");
        isGreat = swerveInfo.getBoolean("is9773great");
        whoIsBest = swerveInfo.getString("whoIsTheBest");
        teamYears = swerveInfo.getInt("teamYears");
    }

    @Override
    public void runOpMode() throws InterruptedException {

        readServoInfo();

        telemetry.addData("Say", "Hello Driver, incr0 is %f", incrementServo1);
        if (isGreat) {
            telemetry.addData("Say", "team is great");
        } else {
            telemetry.addData("Say", "team is good");
        }
        telemetry.addData("Say", "who is best %s", whoIsBest);
        telemetry.addData("Say", "years in action %d", teamYears);
        telemetry.update();
        swerveInfo.modifyInt("teamYears", teamYears+1);
        swerveInfo.modifyDouble("increment3", 10.5);
        swerveInfo.modifyBoolean("is9773great", false);
        swerveInfo.modifyString("whoIsTheBest", "we are all the best");
        swerveInfo.updateFile();
        waitForStart();
        while (opModeIsActive()) {

        }
    }

}
