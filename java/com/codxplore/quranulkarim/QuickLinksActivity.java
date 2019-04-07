package com.codxplore.quranulkarim;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codxplore.quranulkarim.adapter.RecyclerViewAdapter;
import com.codxplore.quranulkarim.helper.DatabaseHelper;
import com.codxplore.quranulkarim.listener.RecyclerTouchListener;
import com.codxplore.quranulkarim.model.Sura;

import java.util.ArrayList;

public class QuickLinksActivity extends Activity {

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

        dbhelper = new DatabaseHelper(getApplicationContext());

        getDataFromLocalDb();

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
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
        }));
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "SELECT sura.*,quick_link.quick_link_id " +
                "FROM quick_link " +
                "LEFT JOIN sura ON  quick_link.sura_id = sura.surah_id " +
                "Order by quick_link_id ASC";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Sura sura = new Sura();
                    sura.setSurah_id(cursor.getString(cursor.getColumnIndex("surah_id")));
                    sura.setName_arabic(cursor.getString(cursor.getColumnIndex("name_arabic")));
                    sura.setName_english(cursor.getString(cursor.getColumnIndex("name_english")));
                    sura.setName_simple(cursor.getString(cursor.getColumnIndex("name_simple")));
                    sura.setRevelation_place(cursor.getString(cursor.getColumnIndex("revelation_place")));
                    sura.setAyat(cursor.getString(cursor.getColumnIndex("ayat")));
                    sura.setRevelation_order(cursor.getString(cursor.getColumnIndex("revelation_order")));
                    sura.setId(cursor.getString(cursor.getColumnIndex("sid")));
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
        rvAdapter = new RecyclerViewAdapter(QuickLinksActivity.this, suras);
        recyclerview.setAdapter(rvAdapter);
    }
}
