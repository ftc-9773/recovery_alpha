package org.firstinspires.ftc.teamcode.RASI;

import android.util.Log;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.teamcode.infrastructure.FileRW;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
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
    private HashMap<String, String> hashMap;
    private TeamRasiCommands teamRasiCommands;
    private boolean isSeperateInit;
    private String methodString;
    private String lcString;
    private String type;
    private String command;
    private String ucString;
    private String[] parameters;
    private String[] arguments;
    private String[] methodInfo;
    private Object[] finalParameters;
    private StringBuilder stringBuilder;
    private HashMap<String, String[]> infoHashmap;
    private boolean hasArguments;

    public RasiExecutor(LinearOpModeCamera linearOpModeCamera, String filepath, String filename){
        this.linearOpModeCamera = linearOpModeCamera;
        teamRasiCommands = new TeamRasiCommands(linearOpModeCamera);
        rasiParser = new RasiParserV2(filepath, filename, linearOpModeCamera);
        hashMap = new HashMap<String, String>();
        infoHashmap = new HashMap<String, String[]>();
        for(int x = 0; x < teamRasiCommands.getClass().getMethods().length; x++){
            Log.d("RasiExecutor", Integer.toString(x));
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
                Log.d("RasiExecutor",methodString);
                ucString = methodString.split("\\.")[1].split("\\(")[0];
                if(methodString.charAt(methodString.length()-1)!= '(') {
                    parameters = methodString.split("\\(");
                    parameters = parameters[parameters.length-1].split(",");
                    hasArguments = true;
                }
                else{
                    hasArguments= false;
                    parameters = new String[0];
                }
                lcString = ucString.toLowerCase();
                hashMap.put(lcString, ucString);
                Log.i("RasiExecutor", parameters.toString());
                infoHashmap.put(ucString, parameters);
            }
        }
    }

    public void runRasi(){
        command = rasiParser.getCommand();
        if(hasArguments) {
            finalParameters = new Object[infoHashmap.get(hashMap.get(command)).length];
            for (int index = 0; index < parameters.length; index++) {
                type = infoHashmap.get(hashMap.get(command))[index];
                switch (type) {
                    case "int":
                        finalParameters[index] = Integer.valueOf(rasiParser.parameters[index]);
                        break;
                    case "char":
                        finalParameters[index] = rasiParser.parameters[index].charAt(0);
                        break;
                    case "long":
                        finalParameters[index] = Long.valueOf(rasiParser.parameters[index]);
                        break;
                    case "float":
                        finalParameters[index] = Float.valueOf(rasiParser.parameters[index]);
                        break;
                    case "double":
                        finalParameters[index] = Double.valueOf(rasiParser.parameters[index]);
                        break;
                    case "java.lang.String":
                        finalParameters[index] = rasiParser.parameters[index];
                        break;
                    case "boolean":
                        finalParameters[index] = Boolean.valueOf(rasiParser.parameters[index]);
                        break;
                }
            }
        }
        else{
            finalParameters = new Object[0];
        }
        try {
            method = teamRasiCommands.getClass().getMethod(command);
        }
        catch(NoSuchMethodException e){
            Log.e("RasiExecutor", "NoSuchMethodException");
        }
        Log.d("RasiExecutor", method.toString());
        try{
            method.invoke(teamRasiCommands, finalParameters);
        }
        catch (IllegalAccessException e){
            Log.e("rasiExecutor", "illegalAccessException");
            }
        catch(InvocationTargetException e){
            Log.e("rasiExecutor", "InvocationTargetException");
        }
    }
}
