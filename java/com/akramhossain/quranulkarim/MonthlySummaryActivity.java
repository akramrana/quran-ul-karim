package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonthlySummaryActivity extends AppCompatActivity {
    private BarChart chart;
    private static final String TAG = MonthlySummaryActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_summary);

        chart = findViewById(R.id.chart1);
        List<BarEntry> entries = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("EEE, MMMM dd yyyy");
        Date date1 = new Date();
        String dtStr = dateFormat.format(date1);
        String firstDate, lastDate;
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        try {
            Date date = dateFormat.parse(dtStr);
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.setTime(date);
            // Just set the day to 1
            myCalendar.set(Calendar.DAY_OF_MONTH, 1);
            firstDate = newFormat.format(myCalendar.getTime());
            // Add a month
            // add day -1
            myCalendar.add(Calendar.MONTH, 1);
            myCalendar.add(Calendar.DAY_OF_MONTH, -1);
            lastDate = newFormat.format(myCalendar.getTime());

            Log.e("firstDate: ", firstDate);
            Log.e("lastDate: ", lastDate);

            SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
            String sql = "select * from \n" +
                    "reports\n" +
                    "where date BETWEEN '"+firstDate+"' and '"+lastDate+"'";
            Log.i(TAG, sql);
            Cursor cursor = db.rawQuery(sql, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String label = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                        String value = String.valueOf(Math.random());

                    }while (cursor.moveToNext());
                }
            }catch (Exception e){
                Log.i(TAG, e.getMessage());
            }
            finally {
                if (cursor != null && !cursor.isClosed()){
                    cursor.close();
                }
                db.close();
            }


            /*entries.add(new BarEntry(0f, 30f));
            entries.add(new BarEntry(1f, 80f));
            entries.add(new BarEntry(2f, 60f));
            entries.add(new BarEntry(3f, 50f));
            entries.add(new BarEntry(5f, 70f));
            entries.add(new BarEntry(6f, 60f));*/

            ArrayList<Double> valueList = new ArrayList<Double>();
            //ArrayList<BarEntry> entries = new ArrayList<>();
            String title = " Avg Obtain Marks";
            ArrayList<String> xAxisValues = new ArrayList<String>();
            xAxisValues.add("2001");
            xAxisValues.add("2002");
            xAxisValues.add("2003");
            xAxisValues.add("2004");
            xAxisValues.add("2005");
            xAxisValues.add("2006");
            xAxisValues.add("2007");
            xAxisValues.add("2008");
            xAxisValues.add("2009");
            xAxisValues.add("2010");
            xAxisValues.add("2011");
            xAxisValues.add("2012");
            xAxisValues.add("2013");
            xAxisValues.add("2014");
            xAxisValues.add("2015");

            chart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

            valueList.add(20.1);
            valueList.add(30.1);
            valueList.add(50.1);
            valueList.add(30.1);
            valueList.add(60.1);
            valueList.add(60.1);
            valueList.add(60.1);
            valueList.add(60.1);
            valueList.add(65.1);
            valueList.add(60.1);
            valueList.add(55.1);
            valueList.add(60.1);
            valueList.add(50.1);

            //fit the data into a bar
            for (int i = 0; i < valueList.size(); i++) {
                BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
                entries.add(barEntry);
            }
            BarDataSet barDataSet = new BarDataSet(entries, title);
            BarData data = new BarData(barDataSet);
            chart.setData(data);
            chart.invalidate();
            // barChart.animateXY(2000, 2000);
            // llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.animateY(1400, Easing.EaseInOutSine);

            /*BarDataSet set = new BarDataSet(entries, "Progress");
            BarData data = new BarData(set);
            data.setBarWidth(0.9f); // set custom bar width
            chart.setData(data);
            chart.setFitBars(true); // make the x-axis fit exactly all bars
            chart.invalidate();*/

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}