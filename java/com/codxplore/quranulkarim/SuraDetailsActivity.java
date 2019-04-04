package com.codxplore.quranulkarim;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.codxplore.quranulkarim.adapter.RecyclerViewAdapter;
import com.codxplore.quranulkarim.adapter.SuraDetailsViewAdapter;
import com.codxplore.quranulkarim.helper.AudioPlay;
import com.codxplore.quranulkarim.helper.DatabaseHelper;
import com.codxplore.quranulkarim.model.Ayah;

import java.util.ArrayList;

public class SuraDetailsActivity extends Activity {

    public static String suraId;
    public static String suraName;
    public static String suraNameArabic;

    private RecyclerView recyclerview;
    private SuraDetailsViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Ayah> ayahs;
    private static final String TAG = SuraDetailsActivity.class.getSimpleName();
    DatabaseHelper dbhelper;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 10;
    Integer counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            suraId = extras.getString("sura_id");
            suraName = extras.getString("sura_name");
            suraNameArabic = extras.getString("sura_name_arabic");
        }

        setTitle(suraNameArabic+"-"+suraName);

        setContentView(R.layout.activity_sura_details);

        TextView titleEn = (TextView) findViewById(R.id.name_title_en);
        titleEn.setText(suraName);

        TextView titleAr = (TextView) findViewById(R.id.name_title_ar);
        titleAr.setText(suraNameArabic);

        recyclerview = (RecyclerView) findViewById(R.id.ayah_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            SQLiteDatabase db = dbhelper.getWritableDatabase();
                            String sql = "SELECT COUNT(*) FROM ayah WHERE surah_id = "+suraId;
                            Cursor countHistory = db.rawQuery(sql,null);
                            countHistory.moveToFirst();
                            int maxHistoryCount = countHistory.getInt(0);
                            countHistory.close();
                            int maxPageCount = (int) Math.ceil(maxHistoryCount/limit);
                            if(counter < maxPageCount) {
                                counter = (counter + 1);
                                offset = offset+limit;
                                getDataFromLocalDb();
                            }
                        }
                    }
                }
            }

        });

        dbhelper = new DatabaseHelper(getApplicationContext());

        getDataFromLocalDb();
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM ayah WHERE surah_id = "+suraId+" order by ayah_index ASC limit " + offset + "," + limit;
        //Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Ayah ayah = new Ayah();
                    ayah.setAyah_index(cursor.getString(cursor.getColumnIndex("ayah_index")));
                    ayah.setSurah_id(cursor.getString(cursor.getColumnIndex("surah_id")));
                    ayah.setAyah_num(cursor.getString(cursor.getColumnIndex("ayah_num")));
                    ayah.setPage_num(cursor.getString(cursor.getColumnIndex("page_num")));
                    ayah.setJuz_num(cursor.getString(cursor.getColumnIndex("juz_num")));
                    ayah.setHizb_num(cursor.getString(cursor.getColumnIndex("hizb_num")));
                    ayah.setRub_num(cursor.getString(cursor.getColumnIndex("rub_num")));
                    ayah.setText(cursor.getString(cursor.getColumnIndex("text")));
                    ayah.setAyah_key(cursor.getString(cursor.getColumnIndex("ayah_key")));
                    ayah.setSajdah(cursor.getString(cursor.getColumnIndex("sajdah")));
                    ayah.setText_tashkeel(cursor.getString(cursor.getColumnIndex("text_tashkeel")));
                    ayah.setContent_en(cursor.getString(cursor.getColumnIndex("content_en")));
                    ayah.setContent_bn(cursor.getString(cursor.getColumnIndex("content_bn")));
                    ayah.setAudio_duration(cursor.getString(cursor.getColumnIndex("audio_duration")));
                    ayah.setAudio_url(cursor.getString(cursor.getColumnIndex("audio_url")));
                    ayahs.add(ayah);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.i(TAG, e.getMessage());
        }
        finally {
            db.close();
        }
        rvAdapter.notifyDataSetChanged();
    }

    private void setRecyclerViewAdapter() {
        ayahs = new ArrayList<Ayah>();
        rvAdapter = new SuraDetailsViewAdapter(SuraDetailsActivity.this, ayahs);
        recyclerview.setAdapter(rvAdapter);
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }
}
