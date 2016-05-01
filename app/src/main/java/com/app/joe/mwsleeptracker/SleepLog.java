package com.app.joe.mwsleeptracker;

/**
 * Created by jfran on 4/30/2016.
 */
public class SleepLog {
    private int totalAwake;
    private int totalRestless;
    private int totalAsleep;
    private Number[] sleepStatus;
    private Number[] sleepTime;

    public int getTotalAwake() { return this.totalAwake; }
    public void setTotalAwake(int totalAwake) {this.totalAwake = totalAwake; }

    public int getTotalRestless() { return this.totalRestless; }
    public void setTotalRestless(int totalRestless) { this.totalRestless = totalRestless; }

    public int getTotalAsleep() { return this.totalAsleep; }
    public void setTotalAsleep(int totalAsleep) { this.totalAsleep = totalAsleep; }

    public Number[] getSleepStatus() { return this.sleepStatus; }
    public void setSleepStatus(Number[] sleepStatus) { this.sleepStatus = sleepStatus; }

    public Number[] getSleepTime() { return this.sleepTime; }
    public void setSleepTime(Number[] sleepTime) { this.sleepTime = sleepTime; }
}
