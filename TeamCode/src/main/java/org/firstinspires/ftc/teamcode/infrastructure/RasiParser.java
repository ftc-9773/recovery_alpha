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
    private int commandReadingIndex = 0;

    private static String TAG= "9773_RasiParser";
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
        if(DEBUG){
            Log.i(TAG,"String is:" + input);
        }
        boolean condition = true;

        index = 0;
        StringBuilder inputBuilder = new StringBuilder(input);
        if (DEBUG) { Log.i(TAG, "StringBuilder is: ." + inputBuilder); }

        while(inputBuilder.length() > index) {
            if (inputBuilder.charAt(index) == ' ') {
                    inputBuilder.deleteCharAt(index);
            }
            else {
                index ++;
            }
        }

        if (DEBUG) { Log.i(TAG, "New StringBuilder: ." + inputBuilder); }

        input = inputBuilder.toString();
        this.commands = input.split(";");

        if (DEBUG) {
            Log.i(TAG, "Printing commands:");
            for (String i: this.commands) {
                Log.i(TAG, "next-" + i);
            }
        }

    }

    public void loadNextCommand(){
        if(commandReadingIndex<commands.length) {
            String currentCommand = commands[commandReadingIndex];
            commandOut = currentCommand.split(",");
            commandReadingIndex++;
        } else {
            Log.e(TAG, "THERE I A REALLY BIG PROBLEM - TRIED TO ACCESS TOO MANY COMMANDS");
        }
        if (DEBUG) {
            String thinggy = "Printing this command:";
            for (String i: commandOut) {
                thinggy += ", " + i;
            }
            Log.i(TAG, thinggy);
        }
    }
    public String getParameter(int parameterNumber){
        Log.i(TAG, "Command is: ." + commandOut[parameterNumber]);
        return commandOut[parameterNumber];
    }
}