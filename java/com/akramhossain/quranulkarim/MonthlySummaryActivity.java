package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.sentry.Sentry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

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
        DateFormat dateFormat1 = new SimpleDateFormat("EEE, MMMM dd");
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

            ArrayList<Double> valueList = new ArrayList<Double>();
            String title = "Monthly Progress";
            ArrayList<String> xAxisValues = new ArrayList<String>();

            SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
            String sql = "select * from \n" +
                    "reports\n" +
                    "where date BETWEEN '"+firstDate+"' and '"+lastDate+"' order by date asc";

            /*String sql = "select * from reports";*/

            Log.i(TAG, sql);
            Cursor cursor = db.rawQuery(sql, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String label = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                        Integer totalpoint = cursor.getInt(cursor.getColumnIndexOrThrow("perform_tahajjud"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("perform_fajr"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("morning_adhkar"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("quran_recitation"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("study_hadith"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("salat_ud_doha"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("dhuhr_prayer"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("asr_prayer"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("maghrib_prayer"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("isha_prayer"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("charity"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("literature"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("surah_mulk_recitation"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("recitation_last_2_surah_baqarah"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("ayatul_kursi"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("recitation_first_last_10_surah_kahf"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("tasbih"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("smoking"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("alcohol"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("haram_things"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("backbiting"))
                                +cursor.getInt(cursor.getColumnIndexOrThrow("slandering"));

                        SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd");
                        // parse the string into Date object
                        Date date2 = sdfSource.parse(label);
                        // create SimpleDateFormat object with desired date format
                        SimpleDateFormat sdfDestination = new SimpleDateFormat("MMM dd");
                        // parse the date into another format
                        String strDate = sdfDestination.format(date2);

                        //Log.d(TAG,strDate);

                        xAxisValues.add(strDate);
                        valueList.add(Double.valueOf(totalpoint));

                    }while (cursor.moveToNext());
                }
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
                //throw new RuntimeException("SQL Query: " + sql, e);
                Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
            }
            finally {
                if (cursor != null && !cursor.isClosed()){
                    cursor.close();
                }
                db.close();
            }

            chart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

            //fit the data into a bar
            for (int i = 0; i < valueList.size(); i++) {
                BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
                entries.add(barEntry);
            }

            BarDataSet barDataSet = new BarDataSet(entries, title);
            //barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            barDataSet.setDrawValues(true);
            barDataSet.setHighlightEnabled(true);
            barDataSet.setHighLightColor(ContextCompat.getColor(this,R.color.transColor));
            barDataSet.setValueTextColor(ContextCompat.getColor(this,R.color.transColor));

            BarData data = new BarData(barDataSet);
            data.setBarWidth(0.3f);

            chart.setData(data);
            chart.setFitBars(true);
            chart.getDescription().setEnabled(false);
            chart.invalidate();
            // barChart.animateXY(2000, 2000);
            // llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setGranularityEnabled(true);
            chart.getXAxis().setTextColor(ContextCompat.getColor(this,R.color.transColor));
            chart.getAxisLeft().setTextColor(ContextCompat.getColor(this, R.color.transColor));
            chart.getAxisRight().setTextColor(ContextCompat.getColor(this, R.color.transColor));
            chart.animateY(1400, Easing.EaseInOutSine);

            Legend l = chart.getLegend();
            l.setTextColor(ContextCompat.getColor(this,R.color.transColor));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}