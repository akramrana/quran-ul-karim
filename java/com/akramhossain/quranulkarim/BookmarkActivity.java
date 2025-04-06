package com.akramhossain.quranulkarim;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.sentry.Sentry;

import android.util.Log;

import com.akramhossain.quranulkarim.adapter.BookmarkViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private BookmarkViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Ayah> ayahs;
    private static final String TAG = BookmarkActivity.class.getSimpleName();
    DatabaseHelper dbhelper;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 10;
    Integer counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        setTitle("Bookmark");

        recyclerview = (RecyclerView) findViewById(R.id.bookmark_list);
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
                            SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                            String sql = "SELECT COUNT(*) FROM bookmark";
                            Cursor countHistory = db.rawQuery(sql,null);
                            try {
                                countHistory.moveToFirst();
                                int maxHistoryCount = countHistory.getInt(0);
                                countHistory.close();
                                int maxPageCount = (int) Math.ceil(maxHistoryCount / limit);
                                if (counter < maxPageCount) {
                                    counter = (counter + 1);
                                    offset = offset + limit;
                                    getDataFromLocalDb();
                                }
                            }catch (Exception e){
                                Log.e("On Scroll Count Check", e.getMessage());
                                //throw new RuntimeException("SQL Query: " + sql, e);
                                Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
                            }finally {
                                if (countHistory != null && !countHistory.isClosed()){
                                    countHistory.close();
                                }
                                db.close();
                            }
                        }
                    }
                }
            }

        });

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        getDataFromLocalDb();
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT ayah.*,bookmark.bookmark_id,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic,transliteration.trans,ayah_indo.text as indo_pak,ut.text_uthmani_tajweed,u.text_uthmani " +
                "FROM bookmark " +
                "LEFT JOIN ayah ON bookmark.ayah_id = ayah.ayah_index " +
                "LEFT JOIN sura ON ayah.surah_id = sura.surah_id LEFT join transliteration ON ayah.ayah_num = transliteration.ayat_id and transliteration.sura_id = ayah.surah_id " +
                "LEFT join ayah_indo ON ayah.ayah_num = ayah_indo.ayah and ayah_indo.sura = ayah.surah_id " +
                "LEFT JOIN uthmani_tajweed ut ON ayah.ayah_key = ut.verse_key " +
                "LEFT JOIN uthmani u ON ayah.ayah_key = u.verse_key "+
                "order by bookmark_id DESC limit " + offset + "," + limit;
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Ayah ayah = new Ayah();
                    ayah.setAyah_index(cursor.getString(cursor.getColumnIndexOrThrow("ayah_index")));
                    ayah.setSurah_id(cursor.getString(cursor.getColumnIndexOrThrow("surah_id")));
                    ayah.setAyah_num(cursor.getString(cursor.getColumnIndexOrThrow("ayah_num")));
                    ayah.setPage_num(cursor.getString(cursor.getColumnIndexOrThrow("page_num")));
                    ayah.setJuz_num(cursor.getString(cursor.getColumnIndexOrThrow("juz_num")));
                    ayah.setHizb_num(cursor.getString(cursor.getColumnIndexOrThrow("hizb_num")));
                    ayah.setRub_num(cursor.getString(cursor.getColumnIndexOrThrow("rub_num")));
                    ayah.setText(cursor.getString(cursor.getColumnIndexOrThrow("text")));
                    ayah.setAyah_key(cursor.getString(cursor.getColumnIndexOrThrow("ayah_key")));
                    ayah.setSajdah(cursor.getString(cursor.getColumnIndexOrThrow("sajdah")));
                    ayah.setText_tashkeel(cursor.getString(cursor.getColumnIndexOrThrow("text_tashkeel")));
                    ayah.setContent_en(cursor.getString(cursor.getColumnIndexOrThrow("content_en")));
                    ayah.setContent_bn(cursor.getString(cursor.getColumnIndexOrThrow("content_bn")));
                    ayah.setAudio_duration(cursor.getString(cursor.getColumnIndexOrThrow("audio_duration")));
                    ayah.setAudio_url(cursor.getString(cursor.getColumnIndexOrThrow("audio_url")));
                    ayah.setName_simple(cursor.getString(cursor.getColumnIndexOrThrow("name_simple")));
                    ayah.setName_complex(cursor.getString(cursor.getColumnIndexOrThrow("name_complex")));
                    ayah.setName_english(cursor.getString(cursor.getColumnIndexOrThrow("name_english")));
                    ayah.setName_arabic(cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")));
                    ayah.setTrans(cursor.getString(cursor.getColumnIndexOrThrow("trans")));
                    ayah.setIndo_pak(cursor.getString(cursor.getColumnIndexOrThrow("indo_pak")));
                    ayah.setText_uthmani(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani")));
                    ayah.setText_uthmani_tajweed(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani_tajweed")));
                    ayahs.add(ayah);
                } while (cursor.moveToNext());
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
        rvAdapter.notifyDataSetChanged();
    }

    private void setRecyclerViewAdapter() {
        ayahs = new ArrayList<Ayah>();
        rvAdapter = new BookmarkViewAdapter(BookmarkActivity.this, ayahs, this);
        recyclerview.setAdapter(rvAdapter);
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }
}
