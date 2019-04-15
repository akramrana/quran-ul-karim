package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.akramhossain.quranulkarim.adapter.SuraDetailsViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;
import com.akramhossain.quranulkarim.model.SpinnerObject;

import java.util.ArrayList;

public class SearchActivity extends Activity {

    DatabaseHelper dbhelper;
    AutoCompleteTextView text;
    EditText ayat_number;
    private RecyclerView recyclerview;
    private SuraDetailsViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Ayah> ayahs;
    private static final String TAG = SearchActivity.class.getSimpleName();
    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 10;
    Integer counter = 0;
    public static String suraId;
    public static String ayahNumber="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Search");

        dbhelper = new DatabaseHelper(getApplicationContext());

        text=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        ayat_number=(EditText) findViewById(R.id.ayat_number);

        ArrayList suras = this.getAllSura();

        ArrayAdapter<SpinnerObject> dataAdapter = new ArrayAdapter<SpinnerObject>(this,android.R.layout.simple_spinner_item, suras);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        text.setAdapter(dataAdapter);
        text.setThreshold(1);

        recyclerview = (RecyclerView) findViewById(R.id.ayah_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);



        Button btnSubmit = (Button) findViewById(R.id.search_sura_btn);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String suraName = text.getText().toString();
                String verseNum = ayat_number.getText().toString();

                String regex = "[0-9, /,]+";

                if(verseNum.matches(regex)) {
                    ayahNumber = verseNum;
                    Log.i("AYAT NUMBER", ayahNumber);
                }else{
                    ayahNumber = "";
                }

                if (suraName != null && !suraName.equals("")) {

                    Log.i("SURA", suraName);

                    offset = 0;

                    String[] data = suraName.split("-");

                    String nameEn = data[0];
                    String nameAr = data[1];

                    SQLiteDatabase db = dbhelper.getWritableDatabase();
                    String sql = "select * from sura where name_english LIKE \"%" + nameEn + "%\" OR name_simple LIKE \"%"+nameEn+"%\" OR name_arabic LIKE \"%" + nameAr + "%\" limit 1";
                    Cursor cursor = db.rawQuery(sql, null);
                    Log.i(TAG, sql);
                    try {
                        if (cursor.moveToFirst()) {
                            suraId = cursor.getString(cursor.getColumnIndex("surah_id")).toString();
                            setRecyclerViewAdapter();
                            getDataFromLocalDb();
                        }
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    } finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                        db.close();
                    }
                }
            }
        });

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

    }

    private void setRecyclerViewAdapter() {
        ayahs = new ArrayList<Ayah>();
        rvAdapter = new SuraDetailsViewAdapter(SearchActivity.this, ayahs);
        recyclerview.setAdapter(rvAdapter);
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String conSql = " ";
        if (ayahNumber != null && !ayahNumber.equals("")) {
            conSql = " AND ayah_num IN ("+ayahNumber+") ";
        }
        String sql = "SELECT * FROM ayah " +
                "WHERE surah_id = "+suraId+conSql+
                "order by ayah_index ASC limit " + offset + "," + limit;
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

    public ArrayList<SpinnerObject> getAllSura(){
        ArrayList < SpinnerObject > suras = new ArrayList<>();
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT * FROM sura order by surah_id ASC";
        Log.i("Search SQL", sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String suraName = cursor.getString(cursor.getColumnIndex("name_english"))+"-"+cursor.getString(cursor.getColumnIndex("name_arabic"));
                    suras.add(new SpinnerObject(cursor.getInt(cursor.getColumnIndex("surah_id")), suraName));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.i("Search", e.getMessage());
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            cursor.close();
        }
        return suras;
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }
}
