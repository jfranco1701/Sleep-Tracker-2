package com.app.joe.mwsleeptracker;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Class SleepEntry
 *
 * Sleep data is entered into a instance of this class and is sent to the
 * dbhandler to be inserted into the database
 */
public class SleepEntry {
    private long id;
    private long logdatetime;
    private int sleepState;

    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getLogDateTime() {
        return logdatetime;
    }

    public void setLogDateTime(long logdatetime){
        this.logdatetime = logdatetime;
    }

    public int getSleepState() {
        return sleepState;
    }

    public void setSleepState(int sleepState) {
        this.sleepState = sleepState;
    }

}
