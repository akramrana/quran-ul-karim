package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.akramhossain.quranulkarim.adapter.JuzViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.Juz;

import java.util.ArrayList;

public class JuzActivity extends AppCompatActivity {

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

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Juz vd = juzs.get(position);
                //Toast.makeText(getApplicationContext(), vd.getVideo_id() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(),JuzHizbRubDetailsActivity.class);
                in.putExtra("juz_num", vd.getJuz_num());
                in.putExtra("hizb_num", "");
                in.putExtra("rub_num", "");
                in.putExtra("sura_id", vd.getSurah_id());
                in.putExtra("sura_name", vd.getName_english());
                in.putExtra("sura_name_arabic", vd.getName_arabic());
                in.putExtra("activity_title", "Juz\' "+vd.getJuz_num());
                startActivityForResult(in, 100);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());
        getDataFromLocalDb();

    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select ayah.ayah_index,ayah.ayah_num,ayah.surah_id,ayah.page_num,ayah.juz_num,ayah.text_tashkeel,ayah.ayah_key,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic,ayah_indo.text as indo_pak,ut.text_uthmani_tajweed,u.text_uthmani\n" +
                "from ayah \n" +
                "inner join sura ON ayah.surah_id = sura.surah_id\n" +
                "inner join ayah_indo ON ayah.ayah_num = ayah_indo.ayah and ayah_indo.sura = ayah.surah_id "+
                "inner JOIN uthmani_tajweed ut ON ayah.ayah_key = ut.verse_key " +
                "inner JOIN uthmani u ON ayah.ayah_key = u.verse_key "+
                "group by juz_num " +
                "HAVING MIN(ayah_index) " +
                "order by juz_num asc";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Juz juz = new Juz();
                    juz.setAyah_index(cursor.getString(cursor.getColumnIndexOrThrow("ayah_index")));
                    juz.setSurah_id(cursor.getString(cursor.getColumnIndexOrThrow("surah_id")));
                    juz.setPage_num(cursor.getString(cursor.getColumnIndexOrThrow("page_num")));
                    juz.setJuz_num(cursor.getString(cursor.getColumnIndexOrThrow("juz_num")));
                    juz.setText_tashkeel(cursor.getString(cursor.getColumnIndexOrThrow("text_tashkeel")));
                    juz.setAyah_key(cursor.getString(cursor.getColumnIndexOrThrow("ayah_key")));
                    juz.setName_simple(cursor.getString(cursor.getColumnIndexOrThrow("name_simple")));
                    juz.setName_complex(cursor.getString(cursor.getColumnIndexOrThrow("name_complex")));
                    juz.setName_english(cursor.getString(cursor.getColumnIndexOrThrow("name_english")));
                    juz.setName_arabic(cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")));
                    juz.setAyah_num(cursor.getString(cursor.getColumnIndexOrThrow("ayah_num")));
                    juz.setIndo_pak(cursor.getString(cursor.getColumnIndexOrThrow("indo_pak")));
                    juz.setText_uthmani(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani")));
                    juz.setText_uthmani_tajweed(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani_tajweed")));
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
        rvAdapter = new JuzViewAdapter(JuzActivity.this, juzs, this);
        recyclerview.setAdapter(rvAdapter);
    }
}
