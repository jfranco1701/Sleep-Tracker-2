package com.app.joe.mwsleeptracker;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    public static final String PREF_FILE = "SleepTrackerPref";
    private static Context mContext;

    public static void Init(Context context){
        mContext = context;
    }

    private static String readPreference(String prefName, String dfltValue){
        SharedPreferences settings = mContext.getSharedPreferences(PREF_FILE, 0);
        String prefValue = settings.getString(prefName, dfltValue);

        return prefValue;
    }

    private static void writePreference(String prefName, String prefValue){
        SharedPreferences settings = mContext.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(prefName, prefValue);
        editor.commit();
    }

    public static String readMACAddress(){
        String macAddress = readPreference("macAddress", "");
        return macAddress;
    }

    public static void writeMACAddress(String newMACAddress){
        writePreference("macAddress", newMACAddress);
    }
}
