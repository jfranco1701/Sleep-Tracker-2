package com.app.joe.mwsleeptracker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

        //Set the date to the current date
        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = df.format(date);
        tvDate.setText(formattedDate);

        tvDate.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                generateGraph();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        generateGraph();
    }

    private void generateGraph(){
        //Generate the graph of the sleep data for the selected date
        Calendar calendarStart = null;
        Calendar calendarEnd = null;

        //Get date from the text view
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm");

        TextView tvDate = (TextView)findViewById(R.id.tvDate);
        String searchDateString = tvDate.getText().toString()+ " 23:59";

        try{
            //Create the start and end dates for the search
            Date searchDate = (Date)df.parse(searchDateString);

            calendarStart = Calendar.getInstance();
            calendarStart.setTime(searchDate);
            calendarStart.add(Calendar.HOUR_OF_DAY, -12);

            calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(searchDate);
            calendarEnd.add(Calendar.HOUR_OF_DAY, 12);
        }
        catch (ParseException e){

        }

        if (calendarStart != null && calendarEnd != null){
            int[] sleepStatus = null;
            Date[] sleepDate = null;

            DBHandler dbHandler = new DBHandler(SleepLogActivity.this);
            SleepLog sleepLog = dbHandler.getSleepLog(calendarStart.getTimeInMillis(),
                    calendarEnd.getTimeInMillis());

            sleepStatus = sleepLog.getSleepStatus();
            sleepDate = sleepLog.getSleepDate();

            if (sleepStatus == null || sleepStatus.length < 1){
                TextView tvTotalAsleep = (TextView)findViewById(R.id.tvTotalSleep);
                tvTotalAsleep.setText("There is no log data for the date selected");

                TextView tvTotalAwake = (TextView)findViewById(R.id.tvTotalAwake);
                tvTotalAwake.setVisibility(View.GONE);

                TextView tvTotalRestless = (TextView)findViewById(R.id.tvTotalRestless);
                tvTotalRestless.setVisibility(View.GONE);

                LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
                layout.setVisibility(View.GONE);
            }
            else
            {

                int[] margins = {100, 150, 100, 25};

                GraphicalView mChart;
                TimeSeries tsSleepQual = new TimeSeries("Sleep Quality");

                for(int i=0; i<sleepDate.length;i++){
                    tsSleepQual.add(sleepDate[i], sleepStatus[i]);
                }

                XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
                mDataset.addSeries(tsSleepQual);

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

                mChart = ChartFactory.getTimeChartView(this, mDataset, mRenderer, "MM-dd HH:mm");

                LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
                layout.addView(mChart);
                layout.setVisibility(View.VISIBLE);

                TextView tvTotalAwake = (TextView)findViewById(R.id.tvTotalAwake);
                tvTotalAwake.setText("Time Awake (mins): " + String.format("%.0f", (double)((double)sleepLog.getTotalAwake() / 1000) / 60));
                tvTotalAwake.setVisibility(View.VISIBLE);

                TextView tvTotalSleep = (TextView)findViewById(R.id.tvTotalSleep);
                tvTotalSleep.setText("Time Asleep (mins): " + String.format("%.0f", ((double)sleepLog.getTotalAsleep() / 1000) / 60));
                tvTotalSleep.setVisibility(View.VISIBLE);

                TextView tvTotalRestless = (TextView)findViewById(R.id.tvTotalRestless);
                tvTotalRestless.setText("Time Restless (mins): " + String.format("%.0f", ((double)sleepLog.getTotalRestless() / 1000) / 60));
                tvTotalRestless.setVisibility(View.VISIBLE);
            }
        }
    }
}
