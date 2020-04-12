package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.akramhossain.quranulkarim.adapter.HizbViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.Hizb;

import java.util.ArrayList;

public class HizbActivity extends Activity {

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = HizbActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private ArrayList<Hizb> hizbs;
    public HizbViewAdapter rvAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_hizb);

        recyclerview = (RecyclerView) findViewById(R.id.all_hizb_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Hizb vd = hizbs.get(position);
                //Toast.makeText(getApplicationContext(), vd.getVideo_id() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(),JuzHizbRubDetailsActivity.class);
                in.putExtra("juz_num", "");
                in.putExtra("hizb_num", vd.getHizb_num());
                in.putExtra("rub_num", "");
                in.putExtra("sura_id", vd.getSurah_id());
                in.putExtra("sura_name", vd.getName_english());
                in.putExtra("sura_name_arabic", vd.getName_arabic());
                in.putExtra("activity_title", "Hizb "+vd.getHizb_num());
                startActivityForResult(in, 100);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        dbhelper = new DatabaseHelper(getApplicationContext());
        getDataFromLocalDb();
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "select ayah.ayah_index,ayah.ayah_num,ayah.surah_id,ayah.page_num,ayah.hizb_num,ayah.text_tashkeel,ayah.ayah_key,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic\n" +
                "from ayah \n" +
                "inner join sura ON ayah.surah_id = sura.surah_id\n" +
                "group by hizb_num " +
                "HAVING MIN(ayah_index) " +
                "order by hizb_num asc";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Hizb hizb = new Hizb();
                    hizb.setAyah_index(cursor.getString(cursor.getColumnIndex("ayah_index")));
                    hizb.setSurah_id(cursor.getString(cursor.getColumnIndex("surah_id")));
                    hizb.setPage_num(cursor.getString(cursor.getColumnIndex("page_num")));
                    hizb.setHizb_num(cursor.getString(cursor.getColumnIndex("hizb_num")));
                    hizb.setText_tashkeel(cursor.getString(cursor.getColumnIndex("text_tashkeel")));
                    hizb.setAyah_key(cursor.getString(cursor.getColumnIndex("ayah_key")));
                    hizb.setName_simple(cursor.getString(cursor.getColumnIndex("name_simple")));
                    hizb.setName_complex(cursor.getString(cursor.getColumnIndex("name_complex")));
                    hizb.setName_english(cursor.getString(cursor.getColumnIndex("name_english")));
                    hizb.setName_arabic(cursor.getString(cursor.getColumnIndex("name_arabic")));
                    hizb.setAyah_num(cursor.getString(cursor.getColumnIndex("ayah_num")));
                    hizbs.add(hizb);
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
        hizbs = new ArrayList<Hizb>();
        rvAdapter = new HizbViewAdapter(HizbActivity.this, hizbs);
        recyclerview.setAdapter(rvAdapter);
    }
}
