package com.app.joe.mwsleeptracker;

/**
 * Created by jfran on 4/28/2016.
 */
public class SleepEntry {
    private long id;
    private String logdatetime;
    private String sleepState;

    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getLogDateTime() {
        return logdatetime;
    }

    public void setLogDateTime(String logdatetime){
        this.logdatetime = logdatetime;
    }

    public String getSleepState() {
        return sleepState;
    }

    public void setSleepState(String sleepState) {
        this.sleepState = sleepState;
    }

}
