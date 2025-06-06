package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.sentry.Sentry;

import android.util.Log;
import android.view.View;

import com.akramhossain.quranulkarim.adapter.HizbViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.Hizb;

import java.util.ArrayList;

public class HizbActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = HizbActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private ArrayList<Hizb> hizbs;
    public HizbViewAdapter rvAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_hizb);

        View rootView = findViewById(R.id.topBarSlist);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to avoid overlap with status/navigation bars
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    view.getPaddingBottom()
            );
            return insets;
        });

        View bottomBar = findViewById(R.id.all_hizb_list);
        ViewCompat.setOnApplyWindowInsetsListener(bottomBar, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    bottomInset
            );
            return insets;
        });

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

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());
        getDataFromLocalDb();
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select ayah.ayah_index,ayah.ayah_num,ayah.surah_id,ayah.page_num,ayah.hizb_num,ayah.text_tashkeel,ayah.ayah_key,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic, ayah_indo.text as indo_pak,ut.text_uthmani_tajweed,u.text_uthmani \n" +
                "from ayah \n" +
                "inner join sura ON ayah.surah_id = sura.surah_id\n" +
                "inner join ayah_indo ON ayah.ayah_num = ayah_indo.ayah and ayah_indo.sura = ayah.surah_id "+
                "inner JOIN uthmani_tajweed ut ON ayah.ayah_key = ut.verse_key " +
                "inner JOIN uthmani u ON ayah.ayah_key = u.verse_key "+
                "group by hizb_num " +
                "HAVING MIN(ayah_index) " +
                "order by hizb_num asc";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Hizb hizb = new Hizb();
                    hizb.setAyah_index(cursor.getString(cursor.getColumnIndexOrThrow("ayah_index")).toString());
                    hizb.setSurah_id(cursor.getString(cursor.getColumnIndexOrThrow("surah_id")).toString());
                    hizb.setPage_num(cursor.getString(cursor.getColumnIndexOrThrow("page_num")).toString());
                    hizb.setHizb_num(cursor.getString(cursor.getColumnIndexOrThrow("hizb_num")).toString());
                    hizb.setText_tashkeel(cursor.getString(cursor.getColumnIndexOrThrow("text_tashkeel")).toString());
                    hizb.setAyah_key(cursor.getString(cursor.getColumnIndexOrThrow("ayah_key")).toString());
                    hizb.setName_simple(cursor.getString(cursor.getColumnIndexOrThrow("name_simple")).toString());
                    hizb.setName_complex(cursor.getString(cursor.getColumnIndexOrThrow("name_complex")).toString());
                    hizb.setName_english(cursor.getString(cursor.getColumnIndexOrThrow("name_english")).toString());
                    hizb.setName_arabic(cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")).toString());
                    hizb.setAyah_num(cursor.getString(cursor.getColumnIndexOrThrow("ayah_num")).toString());
                    hizb.setIndo_pak(cursor.getString(cursor.getColumnIndexOrThrow("indo_pak")));
                    hizb.setText_uthmani(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani")));
                    hizb.setText_uthmani_tajweed(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani_tajweed")));
                    hizbs.add(hizb);
                }while (cursor.moveToNext());
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
        hizbs = new ArrayList<Hizb>();
        rvAdapter = new HizbViewAdapter(HizbActivity.this, hizbs, this);
        recyclerview.setAdapter(rvAdapter);
    }
}
