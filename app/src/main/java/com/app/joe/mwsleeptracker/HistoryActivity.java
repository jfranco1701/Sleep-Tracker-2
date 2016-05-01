package com.app.joe.mwsleeptracker;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidplot.Plot;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/*import android.graphics.DashPathEffect;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;*/

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.RangeBarChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;


public class HistoryActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

//    private XYPlot plot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        Date date = new Date();

        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = df.format(date);
        tvDate.setText(formattedDate);



        generateGraph();



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        DBHandler dbHandler = new DBHandler(HistoryActivity.this);
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




    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "History Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.app.joe.mwsleeptracker/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "History Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.app.joe.mwsleeptracker/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
