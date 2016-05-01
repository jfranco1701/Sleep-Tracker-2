package com.app.joe.mwsleeptracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by jfran on 4/27/2016.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "SleepHistory";
    private static final String TABLE_NAME = "sleephistory";
    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_LOGDATETIME = "logdatetime";
    private static final String COLUMN_NAME_SLEEPSTATE = "sleepstate";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    private static final int ASLEEP = 10;
    private static final int RESTLESS = 5;
    private static final int AWAKE = 0;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME_LOGDATETIME + " INTEGER,"
                + COLUMN_NAME_SLEEPSTATE + " INTEGER)";
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
        values.put(COLUMN_NAME_LOGDATETIME, Long.parseLong(dateFormat.format(sleepEntry.getLogDateTime().getTime())));
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

    public SleepLog getSleepLog(long startDateTime, long endDateTime){
        SleepLog sleepLog = new SleepLog();
        Number[] sleepState = null;
        Number[] sleepTime = null;

        String logQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME_LOGDATETIME + " >= " + startDateTime + " AND " +
                COLUMN_NAME_LOGDATETIME + " <= " + endDateTime;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(logQuery, null);
        int count = cursor.getCount();

        if (count > 0){
            sleepState = new Number[count+2];
            sleepTime = new Number[count+2];

            int x = 1;

            cursor.moveToFirst();

            do{
                sleepState[x] = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_SLEEPSTATE));

                int datetime = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_LOGDATETIME));

                Date d = new Date(datetime);
                Calendar cal = new GregorianCalendar();
                cal.setTime(d);

                double time = cal.get(Calendar.HOUR);
                time = time + ((double)cal.get(Calendar.MINUTE) / 60);
                sleepTime[x] = time;
                x++;
            } while (cursor.moveToNext());

            sleepState[0] = AWAKE;
            sleepState[count] = AWAKE;
            sleepTime[0] = sleepTime[1].intValue() -0.01;
            sleepTime[count] = sleepTime[count-1].intValue() + 0.01;

            sleepLog.setSleepStatus(sleepState);
            sleepLog.setSleepTime(sleepTime);
        }

        cursor.close();
        // return count

        return sleepLog;
    }
}
