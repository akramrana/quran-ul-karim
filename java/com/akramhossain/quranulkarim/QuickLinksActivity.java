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

import com.akramhossain.quranulkarim.adapter.RecyclerViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.Sura;

import java.util.ArrayList;

public class QuickLinksActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private RecyclerViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Sura> suras;
    private static final String TAG = QuickLinksActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_links);

        recyclerview = (RecyclerView) findViewById(R.id.quick_link_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        setRecyclerViewAdapter();

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        getDataFromLocalDb();

        /*recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Sura vd = suras.get(position);
                //Toast.makeText(getApplicationContext(), vd.getVideo_id() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(),SuraDetailsActivity.class);
                in.putExtra("sura_id", vd.getSurah_id());
                in.putExtra("sura_name", vd.getName_english());
                in.putExtra("sura_name_arabic", vd.getName_arabic());
                startActivityForResult(in, 100);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT sura.*,quick_link.quick_link_id, bangla_name.name_bangla " +
                "FROM quick_link " +
                "LEFT JOIN sura ON  quick_link.sura_id = sura.surah_id " +
                "left join bangla_name on sura.surah_id = bangla_name.surah_id " +
                "Order by quick_link_id ASC";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Sura sura = new Sura();
                    sura.setSurah_id(cursor.getString(cursor.getColumnIndexOrThrow("surah_id")));
                    sura.setName_arabic(cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")));
                    sura.setName_english(cursor.getString(cursor.getColumnIndexOrThrow("name_english")));
                    sura.setName_simple(cursor.getString(cursor.getColumnIndexOrThrow("name_simple")));
                    sura.setRevelation_place(cursor.getString(cursor.getColumnIndexOrThrow("revelation_place")));
                    sura.setAyat(cursor.getString(cursor.getColumnIndexOrThrow("ayat")));
                    sura.setRevelation_order(cursor.getString(cursor.getColumnIndexOrThrow("revelation_order")));
                    sura.setId(cursor.getString(cursor.getColumnIndexOrThrow("sid")));
                    sura.setName_bangla(cursor.getString(cursor.getColumnIndexOrThrow("name_bangla")));
                    suras.add(sura);
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
        suras = new ArrayList<Sura>();
        rvAdapter = new RecyclerViewAdapter(QuickLinksActivity.this, suras, this);
        recyclerview.setAdapter(rvAdapter);
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }
}
