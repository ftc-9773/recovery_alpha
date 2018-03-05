package org.firstinspires.ftc.teamcode.RASI;

import org.firstinspires.ftc.robotcontroller.for_camera_opmodes.LinearOpModeCamera;
import org.firstinspires.ftc.teamcode.infrastructure.FileRW;
import org.firstinspires.ftc.teamcode.infrastructure.RasiParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by vikesh on 12/26/17.
 */

public class RasiParserV2 {
    private LinearOpModeCamera linearOpModeCamera;

    private File rasiFile;                  //File object for the rasi file
    private Scanner fileScanner;            //Scanner object to read the file line by line
    private StringBuilder commandBuilder;   //StringBuilder object for miscellaneous manipulation

    private String currentCommand;          //The String that will contain the current command
    private String[] parameters;            //The String array that contains the parameters
    private String returnString;            //The String which contains the index to be
    private String Tag;
    private String[] TAGS;
    private String[] reservedCommands;
    private boolean shouldExecute = false;
    private boolean isReservedCommand;

    public RasiParserV2(String filepath, String filename, LinearOpModeCamera linearOpModeCamera){

        this.linearOpModeCamera = linearOpModeCamera;

        //Make sure file extension is rasi
        if(filename.split(".")[1].toLowerCase() == "rasi"){
            rasiFile = new File(filepath + filename);
            try {
                fileScanner = new Scanner(rasiFile);
            }
            catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    private void loadNextCommand(){
        currentCommand = fileScanner.nextLine();
        commandBuilder = new StringBuilder(currentCommand);

        int index = 0;
        while(index < commandBuilder.length()){
            if(commandBuilder.charAt(index) == ' '){
                commandBuilder.deleteCharAt(index);
            }
            else{
                index++;
            }
        }

        Tag = currentCommand.split(":")[0];
        parameters = currentCommand.split(":")[1].split(",");
        if ((Arrays.asList(TAGS).contains(Tag) || Tag.length() == 0) && !Arrays.asList(reservedCommands).contains(parameters[0])){
            shouldExecute = true;
            isReservedCommand = false;
        }
        else if(Arrays.asList(reservedCommands).contains(parameters[0])){
            shouldExecute = false;
            isReservedCommand = true;
        }

        if(isReservedCommand){
            runReservedCommand(parameters[0]);
        }
    }
    public String getCommand(){
        loadNextCommand();
        while(!shouldExecute && linearOpModeCamera.opModeIsActive()) {
            loadNextCommand();
        }
        return parameters[0];
    }
    public String getParam(int paramNumber){
        return parameters[paramNumber];
    }

    public void runReservedCommand(String command){
        switch (command){
            case "changeTags":
                TAGS = new String[parameters.length-1];
                for(int n = 0; n < TAGS.length; n++){
                    TAGS[n] = parameters[n+1];
                }
                break;

            case "end":
                linearOpModeCamera.requestOpModeStop();
                while(linearOpModeCamera.opModeIsActive()){}
                break;
        }
    }
}
