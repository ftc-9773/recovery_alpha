/*
 * Copyright (c) 2017 Robocracy 9773
 */

package org.firstinspires.ftc.teamcode.infrastructure;

import android.support.annotation.Nullable;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.io.FileWriter;

/*
  where are the file on the phone?
        storage / emulated / FIRST / team9773 / json18

        1) open terminal tab on android studio

        2) get to the right dir on the computer, for example
        cd TeamCode/src/main/java/org/firstinspires/ftc/teamcode/json/

        3) push a file to the phone:
        adb push myfile.json /sdcard/FIRST/team9773/json18/

        location of adb on mac: $HOME/Library/Android/sdk/platform-tools
          where you can get the $HOME value by typing "echo $HOME" in a terminal
          export PATH=$PATH:$HOME/Library/Android/sdk/platform-tools

        4) get a file from the phone
        adb pull  /sdcard/FIRST/team9773/json18/myfile.json
*/

public class SafeJsonReader {
    private static final String baseDir = "/sdcard/FIRST/team9773/json18"; // must end with a name
    private static final String TAG = "ftc9773 SafeJasonReader";
    private static final boolean DEBUG = false;

    private String fileName;
    private boolean modified;
    public String jsonStr;
    public JSONObject jsonRoot;

    private String FullName() {
        return baseDir + "/" + this.fileName + ".json";
    }  //Returns path and name to this.filenName on the phone

    // fileName is local name, baseDir will be appended to create full path name
    public SafeJsonReader(String fileName) {
        this.fileName = fileName;
        this.modified = false;
        // read file
        String filePath = FullName();
        if (DEBUG) Log.d(TAG, "try to read json file " + filePath);
        FileReader fileReader = null;
        BufferedReader bufReader = null;
        StringBuilder strBuilder = new StringBuilder();
        String line = null;
        // If the given file path does not exist, give an error showing the JSON file not able to open
        try {
            fileReader = new FileReader(filePath);
            bufReader = new BufferedReader(fileReader);
        }
        catch (IOException e) {
            Log.e(TAG, "Error while trying to open the json file" + filePath, e);
        }

        // Read the file and append to the string builder
        try {
            while ((line = bufReader.readLine()) != null) {
                strBuilder.append(line);
            }
            // Now initialize the main variable that holds the entire json config
            this.jsonStr = new String(strBuilder);
        }
        catch (IOException e) {
            Log.e(TAG, "Error while trying to reading the json file" + filePath, e);
        }

        // construct the json root object
        try {
            this.jsonRoot = new JSONObject(jsonStr);
        }
        catch (JSONException e) {
            Log.e(TAG, "Error while trying to parsing the json file" + fileName, e);
        }

        // cleanup file
        try {
            fileReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error while trying to closing the file" + filePath, e);
        }
    }

    public void updateFile()
    {
        if (! this.modified) return;

        // file path (same as reading)
        String filePath = FullName();
        if (DEBUG) Log.e(TAG, "try to write json file " + filePath);
        // open file
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath);
        }
        catch (IOException e) {
            Log.e(TAG, "Error while trying to open the json file" + this.fileName + " in write mode", e);
        }
        // write file
        try {
            fileWriter.write(this.jsonRoot.toString());
        }
        catch (IOException e) {
            Log.e(TAG, "Error while trying to write the json file" + this.fileName, e);
        }
        // cleanup file
        try {
            fileWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Error while trying to closing the file" + filePath, e);
        }
        this.modified = false;

    }

    // This is a private class method which read key while ignoring the case
    @Nullable
    private static String getRealKeyIgnoreCase(JSONObject jobj, String key) throws JSONException {
        Iterator<String> iter = jobj.keys();
        while (iter.hasNext()) {
            String key1 = iter.next();
            if (key1.equalsIgnoreCase(key)) {
                return (key1);
            }
        }
        return null;
    }

    // read string while ignoring caps in name
    public String getString(JSONObject obj, String name)
    {
        String value=null;
        try {
            String key = getRealKeyIgnoreCase(obj, name);
            value = obj.getString(key);
        } catch (JSONException e) {
            Log.e(TAG, "Error while getting string value for key " + name + " in jason file " + this.fileName, e);
        }
        if (DEBUG) {
            if (value!=null) Log.d(TAG, "read string for key " + name + " and got " + value);
            else Log.e(TAG, "read string for key " + name + " and got null");
        }
        return (value);
    }

    public void modifyString(String name, String newValue)
    {
        try {
            String key = getRealKeyIgnoreCase(jsonRoot, name);
            String oldValue = this.jsonRoot.getString(key);
            if (! oldValue.equals(newValue)) {
                this.jsonRoot.put(key, newValue);
                this.modified = true;
                if (DEBUG) Log.d(TAG, "write string for key " + name + " with new value " + newValue);
            }
        } catch (JSONException e) {
            Log.d(TAG, "Error while setting string value for key " + name + " to " + newValue + " in jason file " + this.fileName, e);
        }

    }
    // read int while ignoring caps in name
    public int getInt(JSONObject obj, String name) {
        int value=0;
        try {
            String key = getRealKeyIgnoreCase(obj, name);
            value = obj.getInt(key);
        } catch (JSONException e) {
            Log.e(TAG, "Error while getting int value for key " + name + " in jason file " + this.fileName, e);
        }
        if (DEBUG) Log.d(TAG, "read int for key " + name + " and got " + value);
        return (value);
    }

    public void modifyInt(String name, int newValue) {
        try {
            String key = getRealKeyIgnoreCase(this.jsonRoot, name);
            int oldValue = this.jsonRoot.getInt(key);
            if (oldValue != newValue) {
                this.jsonRoot.put(key, newValue);
                this.modified = true;
                if (DEBUG) Log.d(TAG, "write int for key " + name + " with new value " + newValue);

            }
        } catch (JSONException e) {
            Log.e(TAG, "Error while modifying int value for key " + name + " to " + newValue + " in jason file " + this.fileName, e);
        }
    }


    // read int while ignoring caps in name
    public double getDouble(JSONObject obj, String name) {
        String key;
        double value=0.0;
        try {
            key = getRealKeyIgnoreCase(obj, name);
            value = obj.getDouble(key);
        } catch (JSONException e) {
            Log.e(TAG, "Error while getting double value for key " + name + " in jason file " + this.fileName, e);
        }
        if (DEBUG) Log.d(TAG, "read double for key " + name + " and got " + value);
        return (value);
    }

    public void modifyDouble(String name, double newValue) {
        try {
            String key = getRealKeyIgnoreCase(this.jsonRoot, name);
            double oldValue = this.jsonRoot.getDouble(key);
            if (oldValue != newValue) {
                this.jsonRoot.put(key, newValue);
                this.modified = true;
                if (DEBUG) Log.d(TAG, "write double for key " + name + " with new value " + newValue);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error while modifying double value for key " + name + " to " + newValue + " in jason file " + this.fileName, e);
        }
    }

    // read boolean while ignoring caps in name
    public boolean getBoolean(JSONObject obj, String name) {
        String key;
        boolean value=false;
        try {
            key = getRealKeyIgnoreCase(obj, name);
            value = obj.getBoolean(key);
        } catch (JSONException e) {
            Log.e(TAG, "Error while getting boolean value for key " + name + " in jason file " + this.fileName, e);
        }
        if (DEBUG) Log.d(TAG, "read boolean for key " + name + " and got " + value);
        return (value);
    }

    public void modifyBoolean(String name, boolean newValue) {
        try {
            String key = getRealKeyIgnoreCase(this.jsonRoot, name);
            boolean oldValue = this.jsonRoot.getBoolean(key);
            if (oldValue != newValue) {
                this.jsonRoot.put(key, newValue);
                this.modified = true;
                if (DEBUG) Log.d(TAG, "write boolean for key " + name + " with new value " + newValue);

            }
        } catch (JSONException e) {
            Log.e(TAG, "Error while modifying boolean value for key " + name + " to " + newValue + " in json file " + this.fileName, e);
        }
    }

    // read json object while ignoring caps in name
    public JSONObject getJSONObject(JSONObject obj, String name) {
        String key;
        JSONObject value=null;
        try {
            key = getRealKeyIgnoreCase(obj, name);
            value = obj.getJSONObject(key);
        } catch (JSONException e) {
            Log.e(TAG, "Error while getting json object value for key " + name + " in json file " + this.fileName, e);
        }
        return (value);
    }

    // read json array while ignoring caps in name
    public JSONArray getJSONArray(JSONObject obj, String name) {
        String key;
        JSONArray value=null;
        try {
            key = getRealKeyIgnoreCase(obj, name);
            value = obj.getJSONArray(key);
        } catch (JSONException e) {
            Log.e(TAG, "Error while getting json array value for key " + name + " in json file " + this.fileName, e);
        }
        return (value);
    }

    // aliases
    public String getString(String name) { return getString(jsonRoot, name); }
    public int getInt(String name) { return getInt(jsonRoot, name); }
    public double getDouble(String name) { return getDouble(jsonRoot, name); }
    public boolean getBoolean(String name) { return getBoolean(jsonRoot, name); }
    public JSONObject getJSONObject(String name) { return getJSONObject(jsonRoot, name); }
    public JSONArray getJSONArray(String name) { return getJSONArray(jsonRoot, name); }

}
