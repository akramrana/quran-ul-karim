package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.JuzHizbRubViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;

import java.util.ArrayList;

public class JuzHizbRubDetailsActivity extends Activity {

    public static String suraId;
    public static String suraName;
    public static String suraNameArabic;
    public static String suraJuzNum;
    public static String suraHizbNum;
    public static String suraRubNum;
    public static String activityTitle;
    TextView page_title;

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Ayah> ayahs;
    private static final String TAG = JuzHizbRubDetailsActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private JuzHizbRubViewAdapter rvAdapter;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 50;
    Integer counter = 0;
    String where_clause = "";
    String btn_next_where_clause = "";
    String btn_prev_where_clause = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            suraId = extras.getString("sura_id");
            suraName = extras.getString("sura_name");
            suraNameArabic = extras.getString("sura_name_arabic");
            suraJuzNum = extras.getString("juz_num");
            suraHizbNum = extras.getString("hizb_num");
            suraRubNum = extras.getString("rub_num");
            activityTitle = extras.getString("activity_title");
        }

        setContentView(R.layout.activity_juz_hizb_rub_details);

        setTitle(activityTitle);

        page_title = (TextView) findViewById(R.id.page_title);
        page_title.setText(activityTitle);

        recyclerview = (RecyclerView) findViewById(R.id.ayah_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();



        if(suraJuzNum!=null && !suraJuzNum.isEmpty()){
            where_clause = "ayah.juz_num = "+suraJuzNum;
            btn_next_where_clause = "ayah.juz_num > "+suraJuzNum+" ORDER BY ayah.juz_num ASC limit 1";
            btn_prev_where_clause = "ayah.juz_num < "+suraJuzNum+" ORDER BY ayah.juz_num DESC limit 1";
        }
        if(suraHizbNum!=null && !suraHizbNum.isEmpty()){
            where_clause = "ayah.hizb_num = "+suraHizbNum;
            btn_next_where_clause = "ayah.hizb_num > "+suraHizbNum+" ORDER BY ayah.hizb_num ASC limit 1";
            btn_prev_where_clause = "ayah.hizb_num < "+suraHizbNum+" ORDER BY ayah.hizb_num DESC limit 1";
        }
        if(suraRubNum!=null && !suraRubNum.isEmpty()){
            where_clause = "ayah.rub_num = "+suraRubNum;
            btn_next_where_clause = "ayah.rub_num > "+suraRubNum+" ORDER BY ayah.rub_num ASC limit 1";
            btn_prev_where_clause = "ayah.rub_num < "+suraRubNum+" ORDER BY ayah.rub_num DESC limit 1";
        }

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
                            String sql = String.format("SELECT COUNT(*) FROM ayah WHERE %s", where_clause);
                            Log.i(TAG, sql);
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
                                Log.i("On Scroll Count Check", e.getMessage());
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

        dbhelper = new DatabaseHelper(getApplicationContext());

        getDataFromLocalDb();

        Button previousBtn = (Button) findViewById(R.id.previousBtn);
        previousBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                String sql = String.format("SELECT ayah.*,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic " +
                        "FROM ayah " +
                        "inner join sura ON ayah.surah_id = sura.surah_id " +
                        "WHERE %s", btn_prev_where_clause);
                Log.i(TAG, sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        String juz_num = "";
                        String hizb_num = "";
                        String rub_num = "";
                        String next_sura_id = cursor.getString(cursor.getColumnIndex("surah_id")).toString();
                        String next_sura_name = cursor.getString(cursor.getColumnIndex("name_english")).toString();
                        String next_sura_name_arabic = cursor.getString(cursor.getColumnIndex("name_arabic")).toString();
                        String next_activity_title = "";
                        //
                        if(suraJuzNum!=null && !suraJuzNum.isEmpty()){
                            String str_juz_name = cursor.getString(cursor.getColumnIndex("juz_num")).toString();
                            juz_num = str_juz_name;
                            next_activity_title = "Juz\' "+str_juz_name;
                            where_clause = "ayah.juz_num = "+str_juz_name;
                            btn_next_where_clause = "ayah.juz_num > "+str_juz_name+" ORDER BY ayah.juz_num ASC limit 1";
                            btn_prev_where_clause = "ayah.juz_num < "+str_juz_name+" ORDER BY ayah.juz_num DESC limit 1";
                        }
                        if(suraHizbNum!=null && !suraHizbNum.isEmpty()){
                            String str_hizb_num = cursor.getString(cursor.getColumnIndex("hizb_num")).toString();
                            hizb_num = str_hizb_num;
                            next_activity_title = "Hizb "+str_hizb_num;
                            where_clause = "ayah.hizb_num = "+str_hizb_num;
                            btn_next_where_clause = "ayah.hizb_num > "+str_hizb_num+" ORDER BY ayah.hizb_num ASC limit 1";
                            btn_prev_where_clause = "ayah.hizb_num < "+str_hizb_num+" ORDER BY ayah.hizb_num DESC limit 1";
                        }
                        if(suraRubNum!=null && !suraRubNum.isEmpty()){
                            String str_rub_num = cursor.getString(cursor.getColumnIndex("rub_num")).toString();
                            rub_num = str_rub_num;
                            next_activity_title = "Rub\' "+str_rub_num;
                            where_clause = "ayah.rub_num = "+str_rub_num;
                            btn_next_where_clause = "ayah.rub_num > "+str_rub_num+" ORDER BY ayah.rub_num ASC limit 1";
                            btn_prev_where_clause = "ayah.rub_num < "+str_rub_num+" ORDER BY ayah.rub_num DESC limit 1";
                        }

                        suraId = next_sura_id;
                        suraName = next_sura_name;
                        suraNameArabic = next_sura_name_arabic;
                        suraJuzNum = juz_num;
                        suraHizbNum = hizb_num;
                        suraRubNum = rub_num;
                        activityTitle = next_activity_title;

                        page_title.setText(next_activity_title);
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor != null && !cursor.isClosed()){
                        cursor.close();
                    }
                    db.close();

                    setRecyclerViewAdapter();
                    getDataFromLocalDb();
                }

            }
        });
        //
        Button nextBtn = (Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                String sql = String.format("SELECT ayah.*,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic " +
                        "FROM ayah " +
                        "inner join sura ON ayah.surah_id = sura.surah_id  " +
                        "WHERE %s", btn_next_where_clause);
                Log.i(TAG, sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        String juz_num = "";
                        String hizb_num = "";
                        String rub_num = "";
                        String next_sura_id = cursor.getString(cursor.getColumnIndex("surah_id")).toString();
                        String next_sura_name = cursor.getString(cursor.getColumnIndex("name_english")).toString();
                        String next_sura_name_arabic = cursor.getString(cursor.getColumnIndex("name_arabic")).toString();
                        String next_activity_title = "";
                        //
                        if(suraJuzNum!=null && !suraJuzNum.isEmpty()){
                            String str_juz_name = cursor.getString(cursor.getColumnIndex("juz_num")).toString();
                            juz_num = str_juz_name;
                            next_activity_title = "Juz\' "+str_juz_name;
                            where_clause = "ayah.juz_num = "+str_juz_name;
                            btn_next_where_clause = "ayah.juz_num > "+str_juz_name+" ORDER BY ayah.juz_num ASC limit 1";
                            btn_prev_where_clause = "ayah.juz_num < "+str_juz_name+" ORDER BY ayah.juz_num DESC limit 1";
                        }
                        if(suraHizbNum!=null && !suraHizbNum.isEmpty()){
                            String str_hizb_num = cursor.getString(cursor.getColumnIndex("hizb_num")).toString();
                            hizb_num = str_hizb_num;
                            next_activity_title = "Hizb "+str_hizb_num;
                            where_clause = "ayah.hizb_num = "+str_hizb_num;
                            btn_next_where_clause = "ayah.hizb_num > "+str_hizb_num+" ORDER BY ayah.hizb_num ASC limit 1";
                            btn_prev_where_clause = "ayah.hizb_num < "+str_hizb_num+" ORDER BY ayah.hizb_num DESC limit 1";
                        }
                        if(suraRubNum!=null && !suraRubNum.isEmpty()){
                            String str_rub_num = cursor.getString(cursor.getColumnIndex("rub_num")).toString();
                            rub_num = str_rub_num;
                            next_activity_title = "Rub\' "+str_rub_num;
                            where_clause = "ayah.rub_num = "+str_rub_num;
                            btn_next_where_clause = "ayah.rub_num > "+str_rub_num+" ORDER BY ayah.rub_num ASC limit 1";
                            btn_prev_where_clause = "ayah.rub_num < "+str_rub_num+" ORDER BY ayah.rub_num DESC limit 1";
                        }

                        suraId = next_sura_id;
                        suraName = next_sura_name;
                        suraNameArabic = next_sura_name_arabic;
                        suraJuzNum = juz_num;
                        suraHizbNum = hizb_num;
                        suraRubNum = rub_num;
                        activityTitle = next_activity_title;

                        page_title.setText(next_activity_title);
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor != null && !cursor.isClosed()){
                        cursor.close();
                    }
                    db.close();
                    setRecyclerViewAdapter();
                    getDataFromLocalDb();
                }

            }
        });
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = String.format("SELECT ayah.*,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic \nFROM ayah \nLEFT JOIN sura ON ayah.surah_id = sura.surah_id \nWHERE %s order by ayah_index ASC limit %d,%d", where_clause, offset, limit);
        Log.i(TAG, sql);
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
                    ayah.setName_simple(cursor.getString(cursor.getColumnIndex("name_simple")));
                    ayah.setName_complex(cursor.getString(cursor.getColumnIndex("name_complex")));
                    ayah.setName_english(cursor.getString(cursor.getColumnIndex("name_english")));
                    ayah.setName_arabic(cursor.getString(cursor.getColumnIndex("name_arabic")));
                    ayahs.add(ayah);
                } while (cursor.moveToNext());
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
        rvAdapter.notifyDataSetChanged();
    }

    private void setRecyclerViewAdapter() {
        ayahs = new ArrayList<Ayah>();
        rvAdapter = new JuzHizbRubViewAdapter(JuzHizbRubDetailsActivity.this, ayahs, this);
        recyclerview.setAdapter(rvAdapter);
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }
}
