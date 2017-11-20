package org.firstinspires.ftc.teamcode.infrastructure;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Vikesh on 11/19/2017.
 */

public class controlParser {

    private String[] commands;
    private String[] commandOut = new String[4];
    private String input;
    private BufferedReader buffReader = null;
    private int index = 0;
    FileReader fileReader = null;

    private static String TAG= "9773_ControlParser";
    private static boolean DEBUG = true;

    public controlParser(String fileName){
        try {
            fileReader = new FileReader("/sdcard/FIRST/team9773/ibwt18/" + fileName);
            buffReader = new BufferedReader(fileReader);
        }
        catch(IOException e){
            if(DEBUG) { Log.e(TAG, "Failed to build buffReader"); }
            e.printStackTrace();
        }
        try {
            input = buffReader.readLine();
        }
        catch(IOException e){
            if(DEBUG) { Log.e(TAG, "Failed to get input"); }
            e.printStackTrace();
        }
        this.commands = input.split(";");
        }

    public String[] getNextCommand(){
        if(index<commands.length) {
            commandOut = commands[index].split(", ");
            index++;
        }
        return commandOut;
    }
}
