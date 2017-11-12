package org.firstinspires.ftc.teamcode.infrastructure;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by robocracy on 11/11/17.
 */

public class Instrumentation{
    private static final String baseDir = "/sdcard/FIRST/team9773/log18"; // must end with a name
    private static final String timeStamp;

    protected static final String TAG = "ftc9773 Instrumentation";
    protected static final boolean DEBUG = false; // enable debugging info
    protected static final boolean ENABLED = true; // set to false to run quicker

    // global state
    protected static long loopStep = 0;
    protected static long startTimeInMs;
    protected boolean enabled = true;

    // data for shared implementation in the base
    private File file = null;
    private FileWriter fileWriter = null;
    private BufferedWriter bufferedWriter = null;
    private String fileName = null;
    private boolean hasPushed = false;
    private boolean firstElement = true;

    /////////////////////////////////////////////////////////////////
    // main user interface

    // put in main loop, to keep track of loop iteration
    public static void NextLoopIteration() {
        loopStep++;
    }

    // enable gathering of stats (default enabled)
    public void enable() {
        enabled = true;
    }

    // idable gatehring of stats
    public void disable() {
        enabled = false;
    }

    // put at end of main loop, to cleanly close the log
    public void close(){
        if (! ENABLED)  return;
        try {
            bufferedWriter.flush();
            bufferedWriter.close();
            if (! this.hasPushed) {
                file.delete();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while trying to close to the csv file " + this.fileName, e);
        }
    }

    /////////////////////////////////////////////////////////////////
    // calls needed for implementer of subclass

    // initializer
    Instrumentation(String name) {
        if (! ENABLED)  return;
        this.fileName = name.concat(timeStamp);
        String fullFileName = fullName();
        Log.e(TAG, "try to open csv file " + fullFileName);
        // open file
        try {
            this.file = new File(fullFileName);
            file.createNewFile();
            this.fileWriter = new FileWriter(fullFileName);
            this.bufferedWriter = new BufferedWriter(this.fileWriter);
        } catch (IOException e) {
            Log.e(TAG, "Error while trying to open the csv file " + this.fileName + ", full name "+ fullFileName, e);
        }
    }

    // write header of csv table
    protected void writeHeader(String header) {
        if (! ENABLED || ! this.enabled)  return;
        String preHeader = "loopStep,timeStamp,";
        try{
            bufferedWriter.write(preHeader);
            bufferedWriter.write(header);
            bufferedWriter.newLine();
        }
        catch(IOException e){
            Log.e(TAG, "Error while trying to write to the csv file " + this.fileName + " and header " + header, e);
        }
    }

    // write a full stat
    protected void writeFullLine(String data) {
        if (! ENABLED || ! this.enabled)  return;
        long currTime = System.currentTimeMillis() - startTimeInMs;
        try{
            bufferedWriter.write(loopStep + "," + currTime + ",");
            bufferedWriter.write(data);
            bufferedWriter.newLine();
        }
        catch(IOException e){
            Log.e(TAG, "Error while trying to write to the csv file " + this.fileName + " and data " + data, e);
        }
        hasPushed = true;
    }

    // write element of stat
    protected void writeElement(String data) {
        if (! ENABLED|| ! this.enabled)  return;
        long currTime = System.currentTimeMillis() - startTimeInMs;
        try{
            if (this.firstElement) {
                bufferedWriter.write(loopStep + "," + currTime);
                this.firstElement = false;
            }
            bufferedWriter.write("," + data);
        }
        catch(IOException e){
            Log.e(TAG, "Error while trying to write to the csv file " + this.fileName + " and element data " + data, e);
        }
    }

    // indicate that stats are done (for one entry)
    protected void writeElementDone() {
        if (! ENABLED|| ! this.enabled)  return;
        try{
            bufferedWriter.newLine();
        }
        catch(IOException e){
            Log.e(TAG, "Error while trying to write to the csv file " + this.fileName + " end of line", e);
        }
        this.hasPushed = true;
        this.firstElement = true;
    }

    /////////////////////////////////////////////////////////////////
    // implementation details

    private String fullName() {
        return baseDir + "/" + this.fileName + ".csv";
    }

    static {
        if (ENABLED) {
            // time stamp common to all log files
            timeStamp = new SimpleDateFormat("-yyyy.MM.dd.HH.mm.ss").format(new Date());
            startTimeInMs = System.currentTimeMillis();
        }
    }

}


