package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyGoalsActivity extends AppCompatActivity {

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

        CheckBox chkTahajjud = findViewById(R.id.chkTahajjud);
        chkTahajjud.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("perform_tahajjud", points_perform_tahajjud);
                } else {
                    updateDailyReport("perform_tahajjud", 0);
                }
            }
        });
        CheckBox chkFajr = findViewById(R.id.chkFajr);
        chkFajr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("perform_fajr", points_perform_fajr);
                } else {
                    updateDailyReport("perform_fajr", 0);
                }
            }
        });
        CheckBox chkAdhkar = findViewById(R.id.chkAdhkar);
        chkAdhkar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("morning_adhkar", points_morning_adhkar);
                } else {
                    updateDailyReport("morning_adhkar", 0);
                }
            }
        });
        CheckBox chkReci = findViewById(R.id.chkReci);
        chkReci.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("quran_recitation", points_quran_recitation);
                } else {
                    updateDailyReport("quran_recitation", 0);
                }
            }
        });
        CheckBox chkHadith = findViewById(R.id.chkHadith);
        chkHadith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("study_hadith", points_study_hadith);
                } else {
                    updateDailyReport("study_hadith", 0);
                }
            }
        });
        CheckBox chkDoha = findViewById(R.id.chkDoha);
        chkHadith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("salat_ud_doha", points_salat_ud_doha);
                } else {
                    updateDailyReport("salat_ud_doha", 0);
                }
            }
        });
        CheckBox chkDhuhr = findViewById(R.id.chkDhuhr);
        chkDhuhr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("dhuhr_prayer", points_dhuhr_prayer);
                } else {
                    updateDailyReport("dhuhr_prayer", 0);
                }
            }
        });
        CheckBox chkAsr = findViewById(R.id.chkAsr);
        chkAsr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("asr_prayer", points_asr_prayer);
                } else {
                    updateDailyReport("asr_prayer", 0);
                }
            }
        });
        CheckBox chkMaghrib = findViewById(R.id.chkMaghrib);
        chkMaghrib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("maghrib_prayer", points_maghrib_prayer);
                } else {
                    updateDailyReport("maghrib_prayer", 0);
                }
            }
        });
        CheckBox chkIsha = findViewById(R.id.chkIsha);
        chkIsha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("isha_prayer", points_isha_prayer);
                } else {
                    updateDailyReport("isha_prayer", 0);
                }
            }
        });
        CheckBox chkCharity = findViewById(R.id.chkCharity);
        chkCharity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("charity", points_charity);
                } else {
                    updateDailyReport("charity", 0);
                }
            }
        });
        CheckBox chkLiterature = findViewById(R.id.chkLiterature);
        chkLiterature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("literature", points_literature);
                } else {
                    updateDailyReport("literature", 0);
                }
            }
        });
        CheckBox chkMulk = findViewById(R.id.chkMulk);
        chkMulk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("surah_mulk_recitation", points_surah_mulk_recitation);
                } else {
                    updateDailyReport("surah_mulk_recitation", 0);
                }
            }
        });
        CheckBox chkBaqara = findViewById(R.id.chkBaqara);
        chkBaqara.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("recitation_last_2_surah_baqarah", points_recitation_last_2_surah_baqarah);
                } else {
                    updateDailyReport("recitation_last_2_surah_baqarah", 0);
                }
            }
        });
        CheckBox chkQursi = findViewById(R.id.chkQursi);
        chkQursi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("ayatul_kursi", points_ayatul_kursi);
                } else {
                    updateDailyReport("ayatul_kursi", 0);
                }
            }
        });
        CheckBox chkKahf = findViewById(R.id.chkKahf);
        chkKahf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("recitation_first_last_10_surah_kahf", points_recitation_first_last_10_surah_kahf);
                } else {
                    updateDailyReport("recitation_first_last_10_surah_kahf", 0);
                }
            }
        });
        CheckBox chkTasbih = findViewById(R.id.chkTasbih);
        chkTasbih.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("tasbih", points_tasbih);
                } else {
                    updateDailyReport("tasbih", 0);
                }
            }
        });
        CheckBox chkHaram1 = findViewById(R.id.chkHaram1);
        chkHaram1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("smoking", points_smoking);
                } else {
                    updateDailyReport("smoking", 0);
                }
            }
        });
        CheckBox chkHaram2 = findViewById(R.id.chkHaram2);
        chkHaram2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("alcohol", points_alcohol);
                } else {
                    updateDailyReport("alcohol", 0);
                }
            }
        });
        CheckBox chkHaram3 = findViewById(R.id.chkHaram3);
        chkHaram3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("haram_things", points_haram_things);
                } else {
                    updateDailyReport("haram_things", 0);
                }
            }
        });
        CheckBox chkHaram4 = findViewById(R.id.chkHaram4);
        chkHaram4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("backbiting", points_backbiting);
                } else {
                    updateDailyReport("backbiting", 0);
                }
            }
        });
        CheckBox chkHaram5 = findViewById(R.id.chkHaram5);
        chkHaram5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateDailyReport("slandering", points_slandering);
                } else {
                    updateDailyReport("slandering", 0);
                }
            }
        });


    }

    public void updateDailyReport(String columnName,Integer value){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        //System.out.println(dateFormat.format(date));
        String dtStr = dateFormat.format(date);
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
                Toast.makeText(getApplicationContext(), "Reports updated.", Toast.LENGTH_LONG).show();
            }else{
                ContentValues values = new ContentValues();
                values.put(columnName, value);
                values.put("date",dtStr);
                DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase().insertOrThrow("reports", "", values);
                Toast.makeText(getApplicationContext(), "Reports updated.", Toast.LENGTH_LONG).show();
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