package com.akramhossain.quranulkarim;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.akramhossain.quranulkarim.adapter.QuranPagerAdapter;
import com.akramhossain.quranulkarim.util.Utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class QuranReaderActivity extends AppCompatActivity {

    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran_reader);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);

        ViewPager2 pager = findViewById(R.id.pager);
        pager.setAdapter(new QuranPagerAdapter(this));

        // Right-to-left swipe like a Mushaf (optional)
        pager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        int lastReadPage = mPrefs.getInt("last_read_quran_page", 1);
        //Jump to last-read page:
        pager.setCurrentItem(lastReadPage - 1, false);

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mPrefs.edit()
                        .putInt("last_read_quran_page", position + 1)
                        .apply();
            }
        });
    }
}