package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyGoalsActivity extends AppCompatActivity {
    private static final String TAG = DailyGoalsActivity.class.getSimpleName();
    Typeface font;
    CheckBox chkTahajjud, chkFajr, chkAdhkar, chkReci, chkHadith;
    CheckBox chkDoha, chkDhuhr, chkAsr, chkMaghrib, chkIsha, chkCharity;
    CheckBox chkLiterature, chkMulk, chkBaqara, chkQursi, chkKahf, chkTasbih;
    CheckBox chkHaram1, chkHaram2, chkHaram3, chkHaram4, chkHaram5;

    Integer points_perform_tahajjud=5;
    Integer points_perform_fajr = 10;
    Integer points_morning_adhkar=2;
    Integer points_quran_recitation=2;
    Integer points_study_hadith=2;
    Integer points_salat_ud_doha=2;
    Integer points_dhuhr_prayer= 10;
    Integer points_asr_prayer= 10;
    Integer points_maghrib_prayer= 10;
    Integer points_isha_prayer= 10;
    Integer points_charity=3;
    Integer points_literature=3;
    Integer points_surah_mulk_recitation=2;
    Integer points_recitation_last_2_surah_baqarah=2;
    Integer points_ayatul_kursi=2;
    Integer points_recitation_first_last_10_surah_kahf=2;
    Integer points_tasbih=2;
    Integer points_smoking=3;
    Integer points_alcohol=3;
    Integer points_haram_things=5;
    Integer points_backbiting=5;
    Integer points_slandering=5;

    ProgressBar progressTahajjud, progressFajr, progressAdhkar, progressRecite, progressHadith;
    ProgressBar progressDoha, progressDhuhr, progressAsr, progressMaghrib, progressIsha, progressCharity;
    ProgressBar progressLiterature, progressMulk, progressBaqara, progressQursi, progressKahf, progressTasbih;
    ProgressBar progressHaram1, progressHaram2, progressHaram3, progressHaram4, progressHaram5;
    TextView title;
    String globalSelectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        TextView txtTahajjud = (TextView) findViewById(R.id.txtTahajjud);
        txtTahajjud.setTypeface(font);

        TextView txtFajr = (TextView) findViewById(R.id.txtFajr);
        txtFajr.setTypeface(font);

        TextView txtAdhkar = (TextView) findViewById(R.id.txtAdhkar);
        txtAdhkar.setTypeface(font);

        TextView txtQuran = (TextView) findViewById(R.id.txtQuran);
        txtQuran.setTypeface(font);

        TextView txtHadith = (TextView) findViewById(R.id.txtHadith);
        txtHadith.setTypeface(font);

        TextView txtDoha = (TextView) findViewById(R.id.txtDoha);
        txtDoha.setTypeface(font);

        TextView txtDhuhr = (TextView) findViewById(R.id.txtDhuhr);
        txtDhuhr.setTypeface(font);

        TextView txtAsr = (TextView) findViewById(R.id.txtAsr);
        txtAsr.setTypeface(font);

        TextView txtMaghrib = (TextView) findViewById(R.id.txtMaghrib);
        txtMaghrib.setTypeface(font);

        TextView txtIsha = (TextView) findViewById(R.id.txtIsha);
        txtIsha.setTypeface(font);

        TextView txtCharity = (TextView) findViewById(R.id.txtCharity);
        txtCharity.setTypeface(font);

        TextView txtLiterature = (TextView) findViewById(R.id.txtLiterature);
        txtLiterature.setTypeface(font);

        TextView txtMulk = (TextView) findViewById(R.id.txtMulk);
        txtMulk.setTypeface(font);

        TextView txtBaqarah = (TextView) findViewById(R.id.txtBaqarah);
        txtBaqarah.setTypeface(font);

        TextView txtQursi = (TextView) findViewById(R.id.txtQursi);
        txtQursi.setTypeface(font);

        TextView txtKahf = (TextView) findViewById(R.id.txtKahf);
        txtKahf.setTypeface(font);

        TextView txtTasbih = (TextView) findViewById(R.id.txtTasbih);
        txtTasbih.setTypeface(font);

        TextView txtHaram1 = (TextView) findViewById(R.id.txtHaram1);
        txtHaram1.setTypeface(font);

        TextView txtHaram2 = (TextView) findViewById(R.id.txtHaram2);
        txtHaram2.setTypeface(font);

        TextView txtHaram3 = (TextView) findViewById(R.id.txtHaram3);
        txtHaram3.setTypeface(font);

        TextView txtHaram4 = (TextView) findViewById(R.id.txtHaram4);
        txtHaram4.setTypeface(font);

        TextView txtHaram5 = (TextView) findViewById(R.id.txtHaram5);
        txtHaram5.setTypeface(font);

        progressTahajjud = findViewById(R.id.progressTahajjud);
        progressFajr = findViewById(R.id.progressFajr);
        progressAdhkar = findViewById(R.id.progressAdhkar);
        progressRecite = findViewById(R.id.progressRecite);
        progressHadith = findViewById(R.id.progressHadith);
        progressDoha = findViewById(R.id.progressDoha);

        progressDhuhr = findViewById(R.id.progressDhuhr);
        progressAsr = findViewById(R.id.progressAsr);
        progressMaghrib = findViewById(R.id.progressMaghrib);
        progressIsha = findViewById(R.id.progressIsha);
        progressCharity = findViewById(R.id.progressCharity);
        progressLiterature = findViewById(R.id.progressLiterature);

        progressMulk = findViewById(R.id.progressMulk);
        progressBaqara = findViewById(R.id.progressBaqara);
        progressQursi = findViewById(R.id.progressQursi);
        progressKahf = findViewById(R.id.progressKahf);
        progressTasbih = findViewById(R.id.progressTasbih);

        progressHaram1 = findViewById(R.id.progressHaram1);
        progressHaram2 = findViewById(R.id.progressHaram2);
        progressHaram3 = findViewById(R.id.progressHaram3);
        progressHaram4 = findViewById(R.id.progressHaram4);
        progressHaram5 = findViewById(R.id.progressHaram5);

        chkTahajjud = findViewById(R.id.chkTahajjud);
        chkTahajjud.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("perform_tahajjud", points_perform_tahajjud);
                    progressTahajjud.setProgress(100,true);
                } else {
                    updateDailyReport("perform_tahajjud", 0);
                    progressTahajjud.setProgress(0,true);
                }
            }
        });
        chkFajr = findViewById(R.id.chkFajr);
        chkFajr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("perform_fajr", points_perform_fajr);
                    progressFajr.setProgress(100,true);
                } else {
                    updateDailyReport("perform_fajr", 0);
                    progressFajr.setProgress(0,true);
                }
            }
        });
        chkAdhkar = findViewById(R.id.chkAdhkar);
        chkAdhkar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("morning_adhkar", points_morning_adhkar);
                    progressAdhkar.setProgress(100, true);
                } else {
                    updateDailyReport("morning_adhkar", 0);
                    progressAdhkar.setProgress(0, true);
                }
            }
        });
        chkReci = findViewById(R.id.chkReci);
        chkReci.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("quran_recitation", points_quran_recitation);
                    progressRecite.setProgress(100,true);
                } else {
                    updateDailyReport("quran_recitation", 0);
                    progressRecite.setProgress(0,true);
                }
            }
        });
        chkHadith = findViewById(R.id.chkHadith);
        chkHadith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("study_hadith", points_study_hadith);
                    progressHadith.setProgress(100, true);
                } else {
                    updateDailyReport("study_hadith", 0);
                    progressHadith.setProgress(0, true);
                }
            }
        });
        chkDoha = findViewById(R.id.chkDoha);
        chkDoha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("salat_ud_doha", points_salat_ud_doha);
                    progressDoha.setProgress(100, true);
                } else {
                    updateDailyReport("salat_ud_doha", 0);
                    progressDoha.setProgress(0, true);
                }
            }
        });
        chkDhuhr = findViewById(R.id.chkDhuhr);
        chkDhuhr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("dhuhr_prayer", points_dhuhr_prayer);
                    progressDhuhr.setProgress(100, true);
                } else {
                    updateDailyReport("dhuhr_prayer", 0);
                    progressDhuhr.setProgress(0, true);
                }
            }
        });
        chkAsr = findViewById(R.id.chkAsr);
        chkAsr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("asr_prayer", points_asr_prayer);
                    progressAsr.setProgress(100, true);
                } else {
                    updateDailyReport("asr_prayer", 0);
                    progressAsr.setProgress(0, true);
                }
            }
        });
        chkMaghrib = findViewById(R.id.chkMaghrib);
        chkMaghrib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("maghrib_prayer", points_maghrib_prayer);
                    progressMaghrib.setProgress(100, true);
                } else {
                    updateDailyReport("maghrib_prayer", 0);
                    progressMaghrib.setProgress(0, true);
                }
            }
        });
        chkIsha = findViewById(R.id.chkIsha);
        chkIsha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("isha_prayer", points_isha_prayer);
                    progressIsha.setProgress(100, true);
                } else {
                    updateDailyReport("isha_prayer", 0);
                    progressIsha.setProgress(0, true);
                }
            }
        });
        chkCharity = findViewById(R.id.chkCharity);
        chkCharity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("charity", points_charity);
                    progressCharity.setProgress(100, true);
                } else {
                    updateDailyReport("charity", 0);
                    progressCharity.setProgress(0, true);
                }
            }
        });
        chkLiterature = findViewById(R.id.chkLiterature);
        chkLiterature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("literature", points_literature);
                    progressLiterature.setProgress(100, true);
                } else {
                    updateDailyReport("literature", 0);
                    progressLiterature.setProgress(0, true);
                }
            }
        });
        chkMulk = findViewById(R.id.chkMulk);
        chkMulk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("surah_mulk_recitation", points_surah_mulk_recitation);
                    progressMulk.setProgress(100, true);
                } else {
                    updateDailyReport("surah_mulk_recitation", 0);
                    progressMulk.setProgress(0, true);
                }
            }
        });
        chkBaqara = findViewById(R.id.chkBaqara);
        chkBaqara.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("recitation_last_2_surah_baqarah", points_recitation_last_2_surah_baqarah);
                    progressBaqara.setProgress(100, true);
                } else {
                    updateDailyReport("recitation_last_2_surah_baqarah", 0);
                    progressBaqara.setProgress(0, true);
                }
            }
        });
        chkQursi = findViewById(R.id.chkQursi);
        chkQursi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("ayatul_kursi", points_ayatul_kursi);
                    progressQursi.setProgress(100, true);
                } else {
                    updateDailyReport("ayatul_kursi", 0);
                    progressQursi.setProgress(0, true);
                }
            }
        });
        chkKahf = findViewById(R.id.chkKahf);
        chkKahf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("recitation_first_last_10_surah_kahf", points_recitation_first_last_10_surah_kahf);
                    progressKahf.setProgress(100, true);
                } else {
                    updateDailyReport("recitation_first_last_10_surah_kahf", 0);
                    progressKahf.setProgress(0, true);
                }
            }
        });
        chkTasbih = findViewById(R.id.chkTasbih);
        chkTasbih.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("tasbih", points_tasbih);
                    progressTasbih.setProgress(100, true);
                } else {
                    updateDailyReport("tasbih", 0);
                    progressTasbih.setProgress(0, true);
                }
            }
        });
        chkHaram1 = findViewById(R.id.chkHaram1);
        chkHaram1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("smoking", points_smoking);
                    progressHaram1.setProgress(100, true);
                } else {
                    updateDailyReport("smoking", 0);
                    progressHaram1.setProgress(0, true);
                }
            }
        });
        chkHaram2 = findViewById(R.id.chkHaram2);
        chkHaram2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("alcohol", points_alcohol);
                    progressHaram2.setProgress(100, true);
                } else {
                    updateDailyReport("alcohol", 0);
                    progressHaram2.setProgress(0, true);
                }
            }
        });
        chkHaram3 = findViewById(R.id.chkHaram3);
        chkHaram3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("haram_things", points_haram_things);
                    progressHaram3.setProgress(100, true);
                } else {
                    updateDailyReport("haram_things", 0);
                    progressHaram3.setProgress(0, true);
                }
            }
        });
        chkHaram4 = findViewById(R.id.chkHaram4);
        chkHaram4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("backbiting", points_backbiting);
                    progressHaram4.setProgress(100, true);
                } else {
                    updateDailyReport("backbiting", 0);
                    progressHaram4.setProgress(0, true);
                }
            }
        });
        chkHaram5 = findViewById(R.id.chkHaram5);
        chkHaram5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("slandering", points_slandering);
                    progressHaram5.setProgress(100, true);
                } else {
                    updateDailyReport("slandering", 0);
                    progressHaram5.setProgress(0, true);
                }
            }
        });

        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        String dtStr = dateFormat1.format(date1);
        globalSelectedDate = dtStr;
        getReportData(dtStr);
        //
        title = (TextView) findViewById(R.id.title);
        title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMMM dd yyyy");
        Date date = new Date();
        String dtStr1 = dateFormat.format(date);
        title.setText(dtStr1);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        DailyGoalsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                /*Log.d(TAG,dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);*/
                                String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                try {
                                    SimpleDateFormat sdfSource = new SimpleDateFormat("dd-M-yyyy");
                                    Date date2 = sdfSource.parse(selectedDate);
                                    SimpleDateFormat sdfDestination = new SimpleDateFormat("EEE, MMMM dd yyyy");
                                    String strDate = sdfDestination.format(date2);
                                    /*Log.d(TAG, strDate);*/
                                    title.setText(strDate);

                                    SimpleDateFormat sdfDestination1 = new SimpleDateFormat("yyyy-MM-dd");
                                    String strDate1 = sdfDestination1.format(date2);

                                    globalSelectedDate = strDate1;
                                    getReportData(strDate1);


                                }catch (Exception e){
                                    Log.i(TAG, e.getMessage());
                                }
                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        LinearLayout summary_section = (LinearLayout) findViewById(R.id.summarySection);
        summary_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MonthlySummaryActivity.class);
                startActivity(i);
            }
        });
    }

    public void getReportData(String dtStr){

        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select * from reports where date = '"+dtStr+"'";
        Log.d(TAG,sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                Integer perform_tahajjud = cursor.getInt(cursor.getColumnIndexOrThrow("perform_tahajjud"));
                if(perform_tahajjud > 0){
                    chkTahajjud.setChecked(true);
                    progressTahajjud.setProgress(100, true);
                }else{
                    chkTahajjud.setChecked(false);
                    progressTahajjud.setProgress(0, true);
                }

                Integer perform_fajr = cursor.getInt(cursor.getColumnIndexOrThrow("perform_fajr"));
                if(perform_fajr > 0){
                    chkFajr.setChecked(true);
                    progressFajr.setProgress(100, true);
                }else{
                    chkFajr.setChecked(false);
                    progressFajr.setProgress(0, true);
                }

                Integer morning_adhkar = cursor.getInt(cursor.getColumnIndexOrThrow("morning_adhkar"));
                if(morning_adhkar > 0){
                    chkAdhkar.setChecked(true);
                    progressAdhkar.setProgress(100, true);
                }else{
                    chkAdhkar.setChecked(false);
                    progressAdhkar.setProgress(0, true);
                }

                Integer quran_recitation = cursor.getInt(cursor.getColumnIndexOrThrow("quran_recitation"));
                if(quran_recitation > 0){
                    chkReci.setChecked(true);
                    progressRecite.setProgress(100, true);
                }else{
                    chkReci.setChecked(false);
                    progressRecite.setProgress(0, true);
                }

                Integer study_hadith = cursor.getInt(cursor.getColumnIndexOrThrow("study_hadith"));
                if(study_hadith > 0){
                    chkHadith.setChecked(true);
                    progressHadith.setProgress(100, true);
                }else{
                    chkHadith.setChecked(false);
                    progressHadith.setProgress(0, true);
                }

                Integer salat_ud_doha = cursor.getInt(cursor.getColumnIndexOrThrow("salat_ud_doha"));
                if(salat_ud_doha > 0){
                    chkDoha.setChecked(true);
                    progressDoha.setProgress(100, true);
                }else{
                    chkDoha.setChecked(false);
                    progressDoha.setProgress(0, true);
                }

                Integer dhuhr_prayer = cursor.getInt(cursor.getColumnIndexOrThrow("dhuhr_prayer"));
                if(dhuhr_prayer > 0){
                    chkDhuhr.setChecked(true);
                    progressDhuhr.setProgress(100, true);
                }else{
                    chkDhuhr.setChecked(false);
                    progressDhuhr.setProgress(0, true);
                }

                Integer asr_prayer = cursor.getInt(cursor.getColumnIndexOrThrow("asr_prayer"));
                if(asr_prayer > 0){
                    chkAsr.setChecked(true);
                    progressAsr.setProgress(100, true);
                }else{
                    chkAsr.setChecked(false);
                    progressAsr.setProgress(0, true);
                }

                Integer maghrib_prayer = cursor.getInt(cursor.getColumnIndexOrThrow("maghrib_prayer"));
                if(maghrib_prayer > 0){
                    chkMaghrib.setChecked(true);
                    progressMaghrib.setProgress(100, true);
                }else{
                    chkMaghrib.setChecked(false);
                    progressMaghrib.setProgress(0, true);
                }

                Integer isha_prayer = cursor.getInt(cursor.getColumnIndexOrThrow("isha_prayer"));
                if(isha_prayer > 0){
                    chkIsha.setChecked(true);
                    progressIsha.setProgress(100, true);
                }else{
                    chkIsha.setChecked(false);
                    progressIsha.setProgress(0, true);
                }

                Integer charity = cursor.getInt(cursor.getColumnIndexOrThrow("charity"));
                if(charity > 0){
                    chkCharity.setChecked(true);
                    progressCharity.setProgress(100, true);
                }else{
                    chkCharity.setChecked(false);
                    progressCharity.setProgress(0, true);
                }

                Integer literature = cursor.getInt(cursor.getColumnIndexOrThrow("literature"));
                if(literature > 0){
                    chkLiterature.setChecked(true);
                    progressLiterature.setProgress(100, true);
                }else{
                    chkLiterature.setChecked(false);
                    progressLiterature.setProgress(0, true);
                }

                Integer surah_mulk_recitation = cursor.getInt(cursor.getColumnIndexOrThrow("surah_mulk_recitation"));
                if(surah_mulk_recitation > 0){
                    chkMulk.setChecked(true);
                    progressMulk.setProgress(100, true);
                }else{
                    chkMulk.setChecked(false);
                    progressMulk.setProgress(0, true);
                }

                Integer recitation_last_2_surah_baqarah = cursor.getInt(cursor.getColumnIndexOrThrow("recitation_last_2_surah_baqarah"));
                if(recitation_last_2_surah_baqarah > 0){
                    chkBaqara.setChecked(true);
                    progressBaqara.setProgress(100, true);
                }else{
                    chkBaqara.setChecked(false);
                    progressBaqara.setProgress(0, true);
                }

                Integer ayatul_kursi = cursor.getInt(cursor.getColumnIndexOrThrow("ayatul_kursi"));
                if(ayatul_kursi > 0){
                    chkQursi.setChecked(true);
                    progressQursi.setProgress(100, true);
                }else{
                    chkQursi.setChecked(false);
                    progressQursi.setProgress(0, true);
                }

                Integer recitation_first_last_10_surah_kahf = cursor.getInt(cursor.getColumnIndexOrThrow("recitation_first_last_10_surah_kahf"));
                if(recitation_first_last_10_surah_kahf > 0){
                    chkKahf.setChecked(true);
                    progressKahf.setProgress(100, true);
                }else{
                    chkKahf.setChecked(false);
                    progressKahf.setProgress(0, true);
                }

                Integer tasbih = cursor.getInt(cursor.getColumnIndexOrThrow("tasbih"));
                if(tasbih > 0){
                    chkTasbih.setChecked(true);
                    progressTasbih.setProgress(100, true);
                }else{
                    chkTasbih.setChecked(false);
                    progressTasbih.setProgress(0, true);
                }

                Integer smoking = cursor.getInt(cursor.getColumnIndexOrThrow("smoking"));
                if(smoking > 0){
                    chkHaram1.setChecked(true);
                    progressHaram1.setProgress(100, true);
                }else{
                    chkHaram1.setChecked(false);
                    progressHaram1.setProgress(0, true);
                }

                Integer alcohol = cursor.getInt(cursor.getColumnIndexOrThrow("alcohol"));
                if(alcohol > 0){
                    chkHaram2.setChecked(true);
                    progressHaram2.setProgress(100, true);
                }else{
                    chkHaram2.setChecked(false);
                    progressHaram2.setProgress(0, true);
                }

                Integer haram_things = cursor.getInt(cursor.getColumnIndexOrThrow("haram_things"));
                if(haram_things > 0){
                    chkHaram3.setChecked(true);
                    progressHaram3.setProgress(100, true);
                }else{
                    chkHaram3.setChecked(false);
                    progressHaram3.setProgress(0, true);
                }

                Integer backbiting = cursor.getInt(cursor.getColumnIndexOrThrow("backbiting"));
                if(backbiting > 0){
                    chkHaram4.setChecked(true);
                    progressHaram4.setProgress(100, true);
                }else{
                    chkHaram4.setChecked(false);
                    progressHaram4.setProgress(0, true);
                }

                Integer slandering = cursor.getInt(cursor.getColumnIndexOrThrow("slandering"));
                if(slandering > 0){
                    chkHaram5.setChecked(true);
                    progressHaram5.setProgress(100, true);
                }else{
                    chkHaram5.setChecked(false);
                    progressHaram5.setProgress(0, true);
                }
            }else{
                chkTahajjud.setChecked(false);
                progressTahajjud.setProgress(0, true);

                chkFajr.setChecked(false);
                progressFajr.setProgress(0, true);

                chkAdhkar.setChecked(false);
                progressAdhkar.setProgress(0, true);

                chkReci.setChecked(false);
                progressRecite.setProgress(0, true);

                chkHadith.setChecked(false);
                progressHadith.setProgress(0, true);

                chkDoha.setChecked(false);
                progressDoha.setProgress(0, true);

                chkDhuhr.setChecked(false);
                progressDhuhr.setProgress(0, true);

                chkAsr.setChecked(false);
                progressAsr.setProgress(0, true);

                chkMaghrib.setChecked(false);
                progressMaghrib.setProgress(0, true);

                chkIsha.setChecked(false);
                progressIsha.setProgress(0, true);

                chkCharity.setChecked(false);
                progressCharity.setProgress(0, true);

                chkLiterature.setChecked(false);
                progressLiterature.setProgress(0, true);

                chkMulk.setChecked(false);
                progressMulk.setProgress(0, true);

                chkBaqara.setChecked(false);
                progressBaqara.setProgress(0, true);

                chkQursi.setChecked(false);
                progressQursi.setProgress(0, true);

                chkKahf.setChecked(false);
                progressKahf.setProgress(0, true);

                chkTasbih.setChecked(false);
                progressTasbih.setProgress(0, true);

                chkHaram1.setChecked(false);
                progressHaram1.setProgress(0, true);

                chkHaram2.setChecked(false);
                progressHaram2.setProgress(0, true);

                chkHaram3.setChecked(false);
                progressHaram3.setProgress(0, true);

                chkHaram4.setChecked(false);
                progressHaram4.setProgress(0, true);

                chkHaram5.setChecked(false);
                progressHaram5.setProgress(0, true);
            }
        }catch (Exception e) {
            Log.i("Report SQL", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }

    }

    public void updateDailyReport(String columnName,Integer value){
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date date = new Date();
        //System.out.println(dateFormat.format(date));
        //String dtStr = dateFormat.format(date);
        String dtStr = globalSelectedDate;
        //
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select * from reports where date = '"+dtStr+"'";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put(columnName, value);
                values.put("date",dtStr);
                DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase().update("reports", values, "date = ?", new String[]{dtStr});
                //Toast.makeText(getApplicationContext(), "Reports updated.", Toast.LENGTH_LONG).show();
            }else{
                ContentValues values = new ContentValues();
                values.put(columnName, value);
                values.put("date",dtStr);
                DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase().insertOrThrow("reports", "", values);
                //Toast.makeText(getApplicationContext(), "Reports updated.", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {
            Log.i("Report SQL", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }

    }

}