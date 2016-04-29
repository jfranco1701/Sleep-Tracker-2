package com.app.joe.mwsleeptracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jfran on 4/27/2016.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "SleepHistory";
    private static final String TABLE_NAME = "sleephistory";
    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_LOGDATETIME = "logdatetime";
    private static final String COLUMN_NAME_SLEEPSTATE = "sleepstate";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME_LOGDATETIME + " TEXT,"
                + COLUMN_NAME_SLEEPSTATE + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Creating tables again
        onCreate(db);
    }

    public void addSleepHistory(SleepEntry sleepEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_LOGDATETIME, sleepEntry.getLogDateTime());
        values.put(COLUMN_NAME_SLEEPSTATE, sleepEntry.getSleepState());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public int getRecordCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }
}
