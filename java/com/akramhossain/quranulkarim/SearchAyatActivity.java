package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.SearchTermViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;
import com.akramhossain.quranulkarim.util.ConnectionDetector;

import java.util.ArrayList;

public class SearchAyatActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private SearchTermViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Ayah> ayahs;
    private static final String TAG = SearchAyatActivity.class.getSimpleName();
    DatabaseHelper dbhelper;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 100;
    Integer counter = 0;
    TextView titleEn,titleAr;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    EditText ayat_term;
    String search_term;

    public String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ayat);

        ayat_term=(EditText) findViewById(R.id.ayat_term);

        recyclerview = (RecyclerView) findViewById(R.id.bookmark_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        dbhelper = new DatabaseHelper(getApplicationContext());

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        Button btnSubmit = (Button) findViewById(R.id.search_sura_btn);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_term = ayat_term.getText().toString();
                if (search_term != null && !search_term.isEmpty()) {
                    ayahs.clear();
                    getDataFromLocalDb();
                }
            }
        });

    }

    private void setRecyclerViewAdapter() {
        ayahs = new ArrayList<Ayah>();
        rvAdapter = new SearchTermViewAdapter(SearchAyatActivity.this, ayahs, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "SELECT ayah.*,sura.name_arabic,sura.name_complex,sura.name_english,sura.name_simple " +
                "FROM ayah " +
                "LEFT join sura ON ayah.surah_id = sura.surah_id " +
                "WHERE (text LIKE \"%"+search_term+"%\" or text_tashkeel LIKE \"%"+search_term+"%\" or content_en LIKE \"%"+search_term+"%\" or content_bn LIKE \"%"+search_term+"%\") " +
                "order by ayah_index ASC " +
                "limit " + offset + "," + limit;
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
                    //
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

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }
}