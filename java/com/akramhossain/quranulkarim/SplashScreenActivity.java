package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.util.Utils;

import org.w3c.dom.Text;

import java.io.IOException;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreenActivity extends Activity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    Typeface fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, font, fontTahaNaskh, fontKitab;
    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_splash_screen);

        View rootView = findViewById(R.id.root_view);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to avoid overlap with status/navigation bars
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);

        fontUthmani = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        fontTahaNaskh = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/kitab.ttf");

        TextView splash_title_ar = (TextView) findViewById(R.id.splash_title_ar);
        TextView splash_title_bn = (TextView) findViewById(R.id.splash_title_bn);

        String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
        if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
            splash_title_ar.setTypeface(fontAlmajeed);
        }
        if(mp_arabicFontFamily.equals("Al Qalam Quran")){
            splash_title_ar.setTypeface(fontAlQalam);
        }
        if(mp_arabicFontFamily.equals("Noore Huda")){
            splash_title_ar.setTypeface(fontUthmani);
        }
        if(mp_arabicFontFamily.equals("Noore Hidayat")){
            splash_title_ar.setTypeface(fontNooreHidayat);
        }
        if(mp_arabicFontFamily.equals("Saleem Quran")){
            splash_title_ar.setTypeface(fontSaleem);
        }
        if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
            splash_title_ar.setTypeface(fontTahaNaskh);
        }
        if(mp_arabicFontFamily.equals("Arabic Regular")){
            splash_title_ar.setTypeface(fontKitab);
        }

        TextView splash_app_bn = (TextView) findViewById(R.id.splash_app_bn);

        splash_title_bn.setTypeface(font);
        splash_app_bn.setTypeface(font);

        //mDBHelper = new DatabaseHelper(this);
        mDBHelper = DatabaseHelper.getInstance(getApplicationContext());

//        try {
//            mDBHelper.updateDataBase();
//        } catch (IOException mIOException) {
//            throw new Error("UnableToUpdateDatabase");
//        }
//        try {
//            mDb = mDBHelper.getWritableDatabase();
//        } catch (SQLException mSQLException) {
//            throw mSQLException;
//        }
//
//        new Handler().postDelayed(new Runnable(){
//            @Override
//            public void run() {
//                /* Create an Intent that will start the Menu-Activity. */
//                Intent mainIntent = new Intent(SplashScreenActivity.this,MainActivity.class);
//                SplashScreenActivity.this.startActivity(mainIntent);
//                SplashScreenActivity.this.finish();
//            }
//        }, 2000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Copy database if needed
                    mDBHelper.updateDataBase();
                    // Open database connection
                    mDb = mDBHelper.getWritableDatabase();
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                    // Optional: show error or fallback UI here
                    return;
                }
                // Now launch MainActivity on UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                });
            }
        }).start();
    }
}
