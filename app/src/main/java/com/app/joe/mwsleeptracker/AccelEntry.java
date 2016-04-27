package com.app.joe.mwsleeptracker;

/**
 * Created by jfran on 4/26/2016.
 */
public class AccelEntry {
    private long id;
    private String logdatetime;
    private String xvalue;
    private String yvalue;
    private String zvalue;

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

    public String getXValue() {
        return xvalue;
    }

    public void setXValue(String xvalue) {
        this.xvalue = xvalue;
    }

    public String getYValue() {
        return yvalue;
    }

    public void setYValue(String yvalue) {
        this.yvalue = yvalue;
    }

    public String getZValue() {
        return zvalue;
    }

    public void setZValue(String zvalue) {
        this.zvalue = zvalue;
    }
}
