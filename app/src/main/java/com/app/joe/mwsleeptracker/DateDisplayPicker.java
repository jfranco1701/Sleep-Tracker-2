package com.app.joe.mwsleeptracker;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

/**
 * Class DateDisplayPicker
 *
 * Taken from blog by Nick Rubino
 * https://blog.tallan.com/2013/02/18/android-date-time-picker-dialog/
 */
public class DateDisplayPicker extends TextView implements DatePickerDialog.OnDateSetListener{
    private Context _context;

    public DateDisplayPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _context = context;
    }

    public DateDisplayPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        setAttributes();
    }

    public DateDisplayPicker(Context context) {
        super(context);
        _context = context;
        setAttributes();
    }

    private void setAttributes() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });
    }

    private void showDateDialog() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(_context, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {

        setText(String.format("%02d-%02d-%s", monthOfYear + 1, dayOfMonth, year));
    }
}
