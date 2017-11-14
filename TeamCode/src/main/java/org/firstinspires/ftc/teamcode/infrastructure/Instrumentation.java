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
 *
 *
 *
 * User interface:
 *
 * Instrumentation is by default on (unless the ENABLED flag is set to false here,
 * in which case nothing happens.
 *
 * A given instrumentation object can be enabled and disabled programatically. Use the
 * enable/disable methods to do so.
 *
 * In the main opmode loop, call nextLoopIteration exactly once at the beginning to allow the
 * instrumentation to relate statistics to a given iteration loop count.
 *
 *  To see how an actual instrumentation is used, check out documentation for InstrumentDoubleArray
 *
 *
 *
 * How to create a new instrumentation subclass:
 *
 * Create a subclass, and in the constructor pass the file name to the super-class.
 * The subclass constructor should also push a string with comma separated header.
 *
 * Then create a method that pushes a new statistic, giving the new stats to write,
 * or having saved in the object enough data to retrieve the data by itself.
 * The pushing method should detect if the stats is the same as before, in which case
 * we suggest that the identical value not be pushed... as otherwise the log becomes
 * with identical values.
 * If a new stat is seen, then you write it by calling the Instrumentation.Write***
 *    The writeFullLine function writes the whole stat in one go, expecting a single string of
 *    comma separated values.
 *    Otherwise, one can push one value at a time (as a string) using the writeElement
 *    each individual value. The line of stat is terminated by a writeElementDone
 *  Instrumentation class will automatically add a time stamp and a count corresponding to
 *  the main loop.
 *  The closing of the stats is used to close the file. Empty files are automatically destroyed.
 *
 *  To see how an actual instrumentation is defined, check out documentation for InstrumentDoubleArray
 */

/*
  where are the file on the phone?
          storage / emulated / FIRST / team9773 / log18

          1) open terminal tab on android studio

          2) get to the right dir on the computer, for example
          cd Download

          3) get a file from the phone
          adb pull /sdcard/FIRST/team9773/log18/filename.csv

          location of adb on mac: $HOME/Library/Android/sdk/platform-tools
          where you can get the $HOME value by typing "echo $HOME" in a terminal
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
    public static void nextLoopIteration() {
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


