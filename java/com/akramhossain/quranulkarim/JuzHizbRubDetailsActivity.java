package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class JuzHizbRubDetailsActivity extends Activity {

    public static String suraId;
    public static String suraName;
    public static String suraNameArabic;
    public static String suraJuzNum;
    public static String suraHizbNum;
    public static String suraRubNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juz_hizb_rub_details);
    }
}
