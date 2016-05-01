package com.app.joe.mwsleeptracker;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by jfran on 4/28/2016.
 */
public class SleepEntry {
    private long id;
    private Calendar logdatetime;
    private int sleepState;

    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public Calendar getLogDateTime() {
        return logdatetime;
    }

    public void setLogDateTime(Calendar logdatetime){
        this.logdatetime = logdatetime;
    }

    public int getSleepState() {
        return sleepState;
    }

    public void setSleepState(int sleepState) {
        this.sleepState = sleepState;
    }

}
