package com.app.joe.mwsleeptracker;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class PrefManager
 *
 * Class is used to read and write shared preferences
 */

public class PrefManager {
    public static final String PREF_FILE = "SleepTrackerPref";
    private static Context mContext;

    public static void Init(Context context){
        mContext = context;
    }

    //Read a shared preference with the given name.  If it does not exist,
    //return the default value passed in the second parameter
    private static String readPreference(String prefName, String dfltValue){
        SharedPreferences settings = mContext.getSharedPreferences(PREF_FILE, 0);
        String prefValue = settings.getString(prefName, dfltValue);

        return prefValue;
    }

    //Write a shared preference with the given name and the given value
    private static void writePreference(String prefName, String prefValue){
        SharedPreferences settings = mContext.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(prefName, prefValue);
        editor.commit();
    }

    //Read the shared preference named 'macAddress'
    public static String readMACAddress(){
        String macAddress = readPreference("macAddress", "");
        return macAddress;
    }

    //Write the shared preference named 'macAddress'
    public static void writeMACAddress(String newMACAddress){
        writePreference("macAddress", newMACAddress);
    }
}
