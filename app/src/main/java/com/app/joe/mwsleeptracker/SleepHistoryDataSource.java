package com.app.joe.mwsleeptracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jfran on 4/26/2016.
 */
public class SleepHistoryDataSource {
    // Database fields
    private SQLiteDatabase database;
    private SleepHistoryDbHelper dbHelper;
    private String[] allColumns = { SleepHistoryDbHelper.COLUMN_NAME_ID,
            SleepHistoryDbHelper.COLUMN_NAME_LOGDATETIME,
            SleepHistoryDbHelper.COLUMN_NAME_XVALUE,
            SleepHistoryDbHelper.COLUMN_NAME_YVALUE,
            SleepHistoryDbHelper.COLUMN_NAME_ZVALUE };

    public SleepHistoryDataSource(Context context) {
        dbHelper = new SleepHistoryDbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
}

    public void createSleepHistoryEntry(String logdatetime, String xvalue, String yvalue, String zvalue) {
        ContentValues values = new ContentValues();
        values.put(SleepHistoryDbHelper.COLUMN_NAME_LOGDATETIME, logdatetime);
        values.put(SleepHistoryDbHelper.COLUMN_NAME_XVALUE, xvalue);
        values.put(SleepHistoryDbHelper.COLUMN_NAME_YVALUE, yvalue);
        values.put(SleepHistoryDbHelper.COLUMN_NAME_ZVALUE, zvalue);

        long insertId = database.insert(SleepHistoryDbHelper.TABLE_NAME, null,
                values);
    }
}
