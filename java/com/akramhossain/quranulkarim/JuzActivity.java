package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.akramhossain.quranulkarim.adapter.JuzViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Juz;

import java.util.ArrayList;

public class JuzActivity extends Activity {

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = JuzActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private ArrayList<Juz> juzs;
    public JuzViewAdapter rvAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_juz);

        recyclerview = (RecyclerView) findViewById(R.id.all_juz_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        dbhelper = new DatabaseHelper(getApplicationContext());
        getDataFromLocalDb();

    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "select ayah.ayah_index,ayah.ayah_num,ayah.surah_id,ayah.page_num,ayah.juz_num,ayah.text_tashkeel,ayah.ayah_key,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic\n" +
                "from ayah \n" +
                "inner join sura ON ayah.surah_id = sura.surah_id\n" +
                "group by juz_num " +
                "HAVING MIN(ayah_index) " +
                "order by juz_num asc";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Juz juz = new Juz();
                    juz.setAyah_index(cursor.getString(cursor.getColumnIndex("ayah_index")));
                    juz.setSurah_id(cursor.getString(cursor.getColumnIndex("surah_id")));
                    juz.setPage_num(cursor.getString(cursor.getColumnIndex("page_num")));
                    juz.setJuz_num(cursor.getString(cursor.getColumnIndex("juz_num")));
                    juz.setText_tashkeel(cursor.getString(cursor.getColumnIndex("text_tashkeel")));
                    juz.setAyah_key(cursor.getString(cursor.getColumnIndex("ayah_key")));
                    juz.setName_simple(cursor.getString(cursor.getColumnIndex("name_simple")));
                    juz.setName_complex(cursor.getString(cursor.getColumnIndex("name_complex")));
                    juz.setName_english(cursor.getString(cursor.getColumnIndex("name_english")));
                    juz.setName_arabic(cursor.getString(cursor.getColumnIndex("name_arabic")));
                    juz.setAyah_num(cursor.getString(cursor.getColumnIndex("ayah_num")));
                    juzs.add(juz);
                }while (cursor.moveToNext());
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
        juzs = new ArrayList<Juz>();
        rvAdapter = new JuzViewAdapter(JuzActivity.this, juzs);
        recyclerview.setAdapter(rvAdapter);
    }
}
