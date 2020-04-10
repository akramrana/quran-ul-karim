package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.akramhossain.quranulkarim.adapter.RubVViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Rub;

import java.util.ArrayList;

public class RubActivity extends Activity{

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = RubActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private ArrayList<Rub> rubs;
    public RubVViewAdapter rvAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_rub);

        recyclerview = (RecyclerView) findViewById(R.id.all_rub_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        dbhelper = new DatabaseHelper(getApplicationContext());
        getDataFromLocalDb();
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "select ayah.ayah_index,ayah.ayah_num,ayah.surah_id,ayah.page_num,ayah.rub_num,ayah.text_tashkeel,ayah.ayah_key,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic\n" +
                "from ayah \n" +
                "inner join sura ON ayah.surah_id = sura.surah_id\n" +
                "group by rub_num " +
                "HAVING MIN(ayah_index) " +
                "order by rub_num asc";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Rub rub = new Rub();
                    rub.setAyah_index(cursor.getString(cursor.getColumnIndex("ayah_index")));
                    rub.setSurah_id(cursor.getString(cursor.getColumnIndex("surah_id")));
                    rub.setPage_num(cursor.getString(cursor.getColumnIndex("page_num")));
                    rub.setRub_num(cursor.getString(cursor.getColumnIndex("rub_num")));
                    rub.setText_tashkeel(cursor.getString(cursor.getColumnIndex("text_tashkeel")));
                    rub.setAyah_key(cursor.getString(cursor.getColumnIndex("ayah_key")));
                    rub.setName_simple(cursor.getString(cursor.getColumnIndex("name_simple")));
                    rub.setName_complex(cursor.getString(cursor.getColumnIndex("name_complex")));
                    rub.setName_english(cursor.getString(cursor.getColumnIndex("name_english")));
                    rub.setName_arabic(cursor.getString(cursor.getColumnIndex("name_arabic")));
                    rub.setAyah_num(cursor.getString(cursor.getColumnIndex("ayah_num")));
                    rubs.add(rub);
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
        rubs = new ArrayList<Rub>();
        rvAdapter = new RubVViewAdapter(RubActivity.this, rubs);
        recyclerview.setAdapter(rvAdapter);
    }
}
