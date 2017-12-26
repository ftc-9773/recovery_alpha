package org.firstinspires.ftc.teamcode.RASI;

import org.firstinspires.ftc.teamcode.infrastructure.FileRW;
import org.firstinspires.ftc.teamcode.infrastructure.RasiParser;

/**
 * Created by vikesh on 12/26/17.
 */

public class RasiParserV2 {

    private FileRW rasiRW;
    private StringBuilder rasiBuilder;
    private String rasiFileString;

    private int index2 = 0;
    private boolean rasiIsComplete = false;

    private String[] commands;
    private String[] singleCommand;
    private String[] action;
    private String[] condition;

    public RasiParserV2(String filepath, String filename){
        rasiRW = new FileRW(filepath + filename, false);
        rasiFileString = rasiRW.getNextLine();
        rasiBuilder = new StringBuilder(rasiFileString);
    }

    public void initRasi(){
        int index = 0;

        while(index<rasiBuilder.length()){
            if (rasiBuilder.charAt(index) == ' '){
                rasiBuilder.deleteCharAt(index);
            }
            else{
                index++;
            }
        }
        index = 0;
        rasiFileString = rasiBuilder.toString();

        commands = rasiFileString.split(";");
    }

    public void loadNextCommand(){
        if(index2 >= commands.length) {
            rasiIsComplete = true;
        }
        if (!rasiIsComplete) {
            singleCommand = commands[index2].split("/");

            if (singleCommand.length == 2) {
                action = singleCommand[0].split(",");
                condition = singleCommand[1].split(",");
            } else if (singleCommand.length == 1) {
                action = singleCommand[1].split(",");
                condition = new String[1];
                condition[0] = "nocondition";
            } else if (singleCommand.length == 0) {
                action = new String[1];
                action[0] = "noaction";
                condition = new String[1];
                condition[0] = "nocondition";
            } else {
                action = new String[1];
                action[0] = "error";
                condition = new String[1];
                condition[0] = "error";
            }
            index2++;
        }
        else{
            action = new String[1];
            action[0] = "end";
            condition = new String[1];
            condition[0] = "end";
        }
    }
    public String getActionParameter(int parameter){

        if (parameter < action.length) {
            return action[parameter];
        }
        else{
            return "error";
        }
    }
    public String getConditionParameter(int parameter){
        if (parameter < condition.length) {
            return condition[parameter];
        }
        else{
            return "error";
        }
    }
}
