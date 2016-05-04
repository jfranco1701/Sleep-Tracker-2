package com.app.joe.mwsleeptracker;

import java.util.Date;

/**
 * Class SleepLog
 *
 * Class used to store the query results when viewing the sleep log
 *
 */
public class SleepLog {
    private int totalAwake;
    private int totalRestless;
    private int totalAsleep;
    private int[] sleepStatus;
    private Date[] sleepDate;

    public int getTotalAwake() { return this.totalAwake; }
    public void setTotalAwake(int totalAwake) {this.totalAwake = totalAwake; }

    public int getTotalRestless() { return this.totalRestless; }
    public void setTotalRestless(int totalRestless) { this.totalRestless = totalRestless; }

    public int getTotalAsleep() { return this.totalAsleep; }
    public void setTotalAsleep(int totalAsleep) { this.totalAsleep = totalAsleep; }

    public int[] getSleepStatus() { return this.sleepStatus; }
    public void setSleepStatus(int[] sleepStatus) { this.sleepStatus = sleepStatus; }

    public Date[] getSleepDate() { return this.sleepDate; }
    public void setSleepDate(Date[] sleepDate) { this.sleepDate = sleepDate; }
}
