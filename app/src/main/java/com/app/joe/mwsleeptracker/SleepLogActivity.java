package com.app.joe.mwsleeptracker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SleepLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        Date date = new Date();

        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = df.format(date);
        tvDate.setText(formattedDate);

        generateGraph();
    }
    private void generateGraph(){
        String DATE_FORMAT2 = "yyyyMMddHHmmss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT2);
        Calendar calenderStart = Calendar.getInstance();
        Calendar calenderEnd = Calendar.getInstance();

        try{
            String start = "20160429180000";
            String end = "20160430180000";

            Date startDate = dateFormat.parse(start);
            Date endDate = dateFormat.parse(end);


            calenderStart.setTime(startDate);


            calenderEnd.setTime(endDate);
        }
        catch (ParseException e){

        }

        Number[] sleep;
        Number[] times;

        DBHandler dbHandler = new DBHandler(SleepLogActivity.this);
        SleepLog sleepLog = dbHandler.getSleepLog(Long.parseLong(dateFormat.format(calenderStart.getTime())),
                Long.parseLong(dateFormat.format(calenderEnd.getTime())));

        sleep = sleepLog.getSleepStatus();
        times = sleepLog.getSleepTime();

        int[] margins = {100, 150, 100, 25};

        GraphicalView mChart;




        TimeSeries sleepQual = new TimeSeries("sleepQual");
        Date[] dt = new Date[7];

        Calendar calendar = new GregorianCalendar(2016, 3, 30, 22, 10, 0);
        Calendar calendar2 = new GregorianCalendar(2016, 3, 30, 22, 30, 0);
        Calendar calendar3 = new GregorianCalendar(2016, 3, 30, 22, 30, 0);
        Calendar calendar4 = new GregorianCalendar(2016, 3, 30, 22, 50, 0);
        Calendar calendar5 = new GregorianCalendar(2016, 3, 30, 22, 50, 0);
        Calendar calendar6 = new GregorianCalendar(2016, 4, 1, 2, 30, 0);
        Calendar calendar7 = new GregorianCalendar(2016, 4, 1, 2, 30, 0);

        dt[0] = calendar.getTime();
        dt[1] = calendar2.getTime();
        dt[2] = calendar3.getTime();
        dt[3] = calendar4.getTime();
        dt[4] = calendar5.getTime();
        dt[5] = calendar6.getTime();
        dt[6] = calendar7.getTime();

        int[] quality = new int[7];
        quality[0] = 0;
        quality[1] = 0;
        quality[2] = 5;
        quality[3] = 5;
        quality[4] = 10;
        quality[5] = 10;
        quality[6] = 0;


        for(int i=0; i<dt.length;i++){
            sleepQual.add(dt[i], quality[i]);
        }

        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        mDataset.addSeries(sleepQual);

        XYSeriesRenderer mSleepRenderer = new XYSeriesRenderer();
        mSleepRenderer.setColor(Color.GREEN);

        XYSeriesRenderer.FillOutsideLine fill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ABOVE);
        fill.setColor(Color.GREEN);
        mSleepRenderer.addFillOutsideLine(fill);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

        mRenderer.addSeriesRenderer(mSleepRenderer);
        mRenderer.setLegendTextSize(48f);
        mRenderer.setFitLegend(true);

        mRenderer.setYLabels(3);
        mRenderer.setXLabels(4);
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(10);
//        mRenderer.setXAxisMin(22.1);
//        mRenderer.setPanLimits(new double[]{22,40,0,0});

        mRenderer.setMargins(margins);
        mRenderer.setYLabelsPadding(80f);
        mRenderer.setLabelsTextSize(48f);

        mRenderer.addYTextLabel(0, "AWK");
        mRenderer.addYTextLabel(5, "RST");
        mRenderer.addYTextLabel(10, "ASLP");

        mRenderer.setPanEnabled(true,false);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setChartTitle("");
        mRenderer.setYTitle("");
        mRenderer.setXTitle("");
        mRenderer.setGridColor(Color.WHITE);
        mRenderer.setBackgroundColor(Color.BLACK);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setShowGrid(true);

//        mRenderer.setBarWidth(10f);






        mChart = ChartFactory.getTimeChartView(this, mDataset, mRenderer, "HH:mm");

        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        layout.addView(mChart);
    }















}
