package org.firstinspires.ftc.teamcode.infrastructure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Vikesh on 11/19/2017.
 */

public class  RasiParser {

    //variables and such
    private String nextLineString = "";
    private String input = "";
    public String[] commands;
    private String[] commandOut = new String[4];
    public String[] rasiTag;
    private int index = 0;
    private int commandReadingIndex = 0;

    //objects and such
    private FileReader fileReader = null;
    private BufferedReader buffReader = null;

    //debug stuff
    private static String TAG= "9773_RasiParser";
    private static boolean DEBUG = false;

    //constructor
    public RasiParser(String fileName, String[] rasiTag){
        this.rasiTag = rasiTag;

        //Build buffered reader
        try {
            fileReader = new FileReader("/storage/emulated/0/FIRST/team9773/rasi18/" + fileName + ".rasi");
            buffReader = new BufferedReader(fileReader);
        }
        catch(IOException e){
            e.printStackTrace();
        }

        //read from file
        try {
            while((nextLineString = buffReader.readLine()) != null) {
                input += nextLineString;
            }
        }
        catch(IOException e) {

            e.printStackTrace();
        }

        index = 0;
        StringBuilder inputBuilder = new StringBuilder(input);

        //remove spaces
        while(inputBuilder.length() > index) {
            if (inputBuilder.charAt(index) == ' ') {
                inputBuilder.deleteCharAt(index);
            }
            else {
                index ++;
            }
        }

        input = inputBuilder.toString();

        //split input by semicolon (individual commands)
        commands = input.split(";");

    }

    public void loadNextCommand(){

        //make sure we are reading a valid address in the array
        if(commandReadingIndex<commands.length) {

            //get the current line that we are looking at
            String currentCommand = commands[commandReadingIndex];

            //split by colon to seperate commands from tags
            commandOut = currentCommand.split(":");

            //If the array has length one, there is no tag, so we should load the command.
            if(commandOut.length == 1 && currentCommand.charAt(0) != '/' && currentCommand.charAt(1) != '/'){
                commandOut = commandOut[0].split(",");
            }

            //if the array has length 2, there is a tag. We check if the tag array contains this tag, and if it does, we load the command.
            else if(commandOut.length == 2 && Arrays.asList(rasiTag).contains(commandOut[0]) && currentCommand.charAt(0) != '/' && currentCommand.charAt(1) != '/'){
                commandOut = commandOut[1].split(",");
            }

            //if the array is longer than 2, there are 2 or more tags. This shouldn't happen, so the command will be skipped.
            else if(commandOut.length > 2){
            }

            //if the array is length 0, or if the tag is not in the tag array, increment the command number and don't execute the rest of the function
            else{
                commandReadingIndex++;
                return;
            }

            commandReadingIndex++;
        }
    }

    //Functions to get the specified parameter in various data types
    public String getParameter(int parameterNumber){
        return commandOut[parameterNumber];
    }

    public int getAsInt(int parameterNumber){
        return Integer.valueOf(commandOut[parameterNumber]);
    }

    public long getAsLong(int parameterNumber){
        return Long.valueOf(commandOut[parameterNumber]);
    }

    public double getAsDouble(int parameterNumber){
        return Double.valueOf(commandOut[parameterNumber]);
    }

    public boolean getAsBoolean(int parameterNumber){
        return Boolean.valueOf(commandOut[parameterNumber]);
    }

    //Function used to change the RASI tags midway through a program.
    public void changeTags(String newTags[]){
        rasiTag = newTags;
    }
}
