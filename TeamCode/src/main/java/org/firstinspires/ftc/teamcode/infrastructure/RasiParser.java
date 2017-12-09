package org.firstinspires.ftc.teamcode.infrastructure;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Vikesh on 11/19/2017.
 */

public class RasiParser {

    public String[] commands;
    private String[] commandOut = new String[4];
    private String input;
    private BufferedReader buffReader = null;
    private int index = 0;
    FileReader fileReader = null;

    private static String TAG= "9773_ControlParser";
    private static boolean DEBUG = true;

    public RasiParser(String fileName){
        try {
            fileReader = new FileReader("/storage/emulated/0/FIRST/team9773/rasi18/" + fileName + ".rasi");
            buffReader = new BufferedReader(fileReader);
        }
        catch(IOException e){
            if(DEBUG) { Log.e(TAG, "Failed to build buffReader"); }
            e.printStackTrace();
        }
        try {
            input = buffReader.readLine();
        }
        catch(IOException e) {
            if (DEBUG) {
                Log.e(TAG, "Failed to get input");
            }
            e.printStackTrace();
        }
        boolean condition = true;
        index = 0;
        StringBuilder inputBuilder = new StringBuilder(input);
            while(inputBuilder.length() > index) {
                if (inputBuilder.charAt(index) == ' ') {
                    inputBuilder.deleteCharAt(index);
                }
            }
        input = inputBuilder.toString();
        this.commands = input.split(";");
        }
    public void loadNextCommand(){
        if(index<commands.length) {
            commandOut = commands[index].split(",");
            index++;
        }
    }
    public String getParameter(int parameterNumber){
        return commandOut[parameterNumber];
    }
}
