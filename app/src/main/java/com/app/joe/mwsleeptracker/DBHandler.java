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
    private static final int DATABASE_VERSION = 12;
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
                + COLUMN_NAME_LOGDATETIME + " LONG,"
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
        values.put(COLUMN_NAME_LOGDATETIME, sleepEntry.getLogDateTime());
        values.put(COLUMN_NAME_SLEEPSTATE, sleepEntry.getSleepState());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void createSampleData(){
        //Used to create sample data for testing
        SleepEntry sampleSleepEntry = new SleepEntry();
        Calendar cal = new GregorianCalendar();

        cal.set(2016, 3, 28, 22, 15, 12);
        sampleSleepEntry.setLogDateTime(cal.getTimeInMillis());
        sampleSleepEntry.setSleepState(0);
        addSleepHistory(sampleSleepEntry);

        cal.set(2016, 3, 28, 22, 30, 10);
        sampleSleepEntry.setLogDateTime(cal.getTimeInMillis());
        sampleSleepEntry.setSleepState(5);
        addSleepHistory(sampleSleepEntry);

        cal.set(2016, 3, 28, 23, 15, 10);
        sampleSleepEntry.setLogDateTime(cal.getTimeInMillis());
        sampleSleepEntry.setSleepState(10);
        addSleepHistory(sampleSleepEntry);

        cal.set(2016, 3, 29, 1, 50, 10);
        sampleSleepEntry.setLogDateTime(cal.getTimeInMillis());
        sampleSleepEntry.setSleepState(0);
        addSleepHistory(sampleSleepEntry);

        cal.set(2016, 3, 29, 2, 20, 5);
        sampleSleepEntry.setLogDateTime(cal.getTimeInMillis());
        sampleSleepEntry.setSleepState(5);
        addSleepHistory(sampleSleepEntry);

        cal.set(2016, 3, 29, 3, 00, 5);
        sampleSleepEntry.setLogDateTime(cal.getTimeInMillis());
        sampleSleepEntry.setSleepState(10);
        addSleepHistory(sampleSleepEntry);

        cal.set(2016, 3, 29, 6, 00, 5);
        sampleSleepEntry.setLogDateTime(cal.getTimeInMillis());
        sampleSleepEntry.setSleepState(0);
        addSleepHistory(sampleSleepEntry);
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
        int[] sleepState = null;
        Date[] sleepDate = null;
        int totalAwake = 0;
        int totalAsleep = 0;
        int totalRestless = 0;

        String logQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME_LOGDATETIME + " >= " + startDateTime + " AND " +
                COLUMN_NAME_LOGDATETIME + " <= " + endDateTime;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(logQuery, null);
        int count = cursor.getCount();

        if (count > 0){
            sleepState = new int[count*2 - 1];
            sleepDate = new Date[count*2 - 1];

            int x = 0;

            cursor.moveToFirst();

            do{
                //Get sleep state
                sleepState[x] = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_SLEEPSTATE));

                //Get date time integer and convert it back to a date
                long longDateTime = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_LOGDATETIME));
                Calendar currCalendar = new GregorianCalendar();
                currCalendar.setTimeInMillis(longDateTime);

                sleepDate[x] = currCalendar.getTime();

                if (x > 0){
                    //put the sleep data into the array
                    sleepState[x-1] = sleepState[x-2];
                    sleepDate[x-1] = sleepDate[x];

                    //update sleep status times
                    Calendar prevCalendar = new GregorianCalendar();
                    prevCalendar.setTime(sleepDate[x-2]);
                    long longPrevDateTime = prevCalendar.getTimeInMillis();

                    int timeSinceLastEntry = (int)(longDateTime - longPrevDateTime);

                    switch (sleepState[x-1]){
                        case 0: totalAwake += timeSinceLastEntry;
                            break;
                        case 5: totalRestless += timeSinceLastEntry;
                            break;
                        case 10: totalAsleep += timeSinceLastEntry;
                            break;
                    }
                }

                x+=2;
            } while (cursor.moveToNext());

            sleepLog.setSleepStatus(sleepState);
            sleepLog.setSleepDate(sleepDate);

            sleepLog.setTotalAsleep(totalAsleep);
            sleepLog.setTotalAwake(totalAwake);
            sleepLog.setTotalRestless(totalRestless);
        }

        cursor.close();
        // return count

        return sleepLog;
    }
}
