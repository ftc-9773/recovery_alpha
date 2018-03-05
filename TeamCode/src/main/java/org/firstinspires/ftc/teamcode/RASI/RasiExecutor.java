package org.firstinspires.ftc.teamcode.RASI;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.teamcode.infrastructure.FileRW;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Vikesh on 2/20/2018.
 */

public class RasiExecutor {

    private RasiParserV2 rasiParser;
    private LinearOpModeCamera linearOpModeCamera;
    private Method method;
    private HashMap<String, String[]> hashMap;
    private TeamRasiCommands teamRasiCommands = new TeamRasiCommands();
    private boolean isSeperateInit;
    private String methodString;
    private String lcString;
    private String[] ucString;
    private String[] parameters;
    private String[] methodInfo;
    private StringBuilder stringBuilder;

    public RasiExecutor(LinearOpModeCamera linearOpModeCamera, String filepath, String filename){
        this.linearOpModeCamera = linearOpModeCamera;
        rasiParser = new RasiParserV2(filepath, filename, linearOpModeCamera);
        hashMap = new HashMap<String, String[]>();
        for(int x = 0; x < teamRasiCommands.getClass().getMethods().length; x++){
            if(teamRasiCommands.getClass().getMethods()[x].toString().contains("TeamRasiCommands.")){
                methodString = teamRasiCommands.getClass().getMethods()[x].toString();
                stringBuilder = new StringBuilder(methodString);
                int index = 0;
                while(index < stringBuilder.length()){
                    if(stringBuilder.charAt(index) == ' ' || stringBuilder.charAt(index) == ')'){
                        stringBuilder.deleteCharAt(index);
                    }
                    else{
                        index++;
                    }
                }
                methodString = stringBuilder.toString();
                ucString[0] = methodString.split("\\.")[1].split("\\(")[0];
                parameters = methodString.split("\\.")[1].split("\\(")[1].split(",");
                lcString = ucString[0].toLowerCase();
            }
        }
    }

    public void runRasi(){

    }
}
