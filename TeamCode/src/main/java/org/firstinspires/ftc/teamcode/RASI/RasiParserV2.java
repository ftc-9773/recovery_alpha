package org.firstinspires.ftc.teamcode.RASI;

import android.util.Log;

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
    private String TAG = "TeamRasiCommands";


    private LinearOpModeCamera linearOpModeCamera;

    private File rasiFile;                  //File object for the rasi file
    private Scanner fileScanner;            //Scanner object to read the file line by line
    private StringBuilder commandBuilder;   //StringBuilder object for miscellaneous manipulation

    private String currentCommand;          //The String that will contain the current command
    public String[] parameters;            //The String array that contains the parameters
    private String returnString;            //The String which contains the index to be
    private String Tag;
    private String[] TAGS = new String[0];
    private String[] reservedCommands = {"end", "changetags"};
    private boolean shouldExecute = false;
    private boolean isReservedCommand;

    public RasiParserV2(String filepath, String filename, LinearOpModeCamera linearOpModeCamera){

        this.linearOpModeCamera = linearOpModeCamera;
        //Make sure file extension is rasi
        Log.i(TAG, filepath+filename);
        Log.i(TAG, filename.split("\\.")[1].toLowerCase());
        if(filename.split("\\.")[1].toLowerCase().equals("rasi")){
            rasiFile = new File(filepath + filename);
            Log.i(TAG,filepath+filename);
            try {
                fileScanner = new Scanner(rasiFile);
            }
            catch(FileNotFoundException e){
                Log.e(TAG, "FileNotFoundException");
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
        if(currentCommand.split(":").length>1) {
            Tag = currentCommand.split(":")[0];
        }
        else{
            Tag = "";
        }
        if(Tag != "") {
            parameters = currentCommand.split(":")[1].split(",");
        }
        else{
            parameters = currentCommand.split(",");
        }
        if ((Arrays.asList(TAGS).contains(Tag) || Tag.length() == 0) && !Arrays.asList(reservedCommands).contains(parameters[0])) {
            shouldExecute = true;
            isReservedCommand = false;
        } else if (Arrays.asList(reservedCommands).contains(parameters[0])) {
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
            case "changetags":
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
