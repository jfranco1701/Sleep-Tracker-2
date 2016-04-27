package com.app.joe.mwsleeptracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jfran on 4/26/2016.
 */


public class SleepHistoryDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SleepHistory.db";

    public static final String TABLE_NAME = "sleephistory";
    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_LOGDATETIME = "logdatetime";
    public static final String COLUMN_NAME_XVALUE = "xvalue";
    public static final String COLUMN_NAME_YVALUE = "yvalue";
    public static final String COLUMN_NAME_ZVALUE = "zvalue";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_NAME_ID + " integer primary key autoincrement, "
            + COLUMN_NAME_LOGDATETIME + "text not null, "
            + COLUMN_NAME_XVALUE + "text not null, "
            + COLUMN_NAME_YVALUE + "text not null, "
            + COLUMN_NAME_ZVALUE + "text not null);";

    public SleepHistoryDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
