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

import com.akramhossain.quranulkarim.adapter.RubVViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.Rub;

import java.util.ArrayList;

public class RubActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = RubActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private ArrayList<Rub> rubs;
    public RubVViewAdapter rvAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_rub);

        View rootView = findViewById(R.id.topBarSlist);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to avoid overlap with status/navigation bars
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    0
            );
            return insets;
        });

        recyclerview = (RecyclerView) findViewById(R.id.all_rub_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Rub vd = rubs.get(position);
                //Toast.makeText(getApplicationContext(), vd.getVideo_id() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(),JuzHizbRubDetailsActivity.class);
                in.putExtra("juz_num", "");
                in.putExtra("hizb_num", "");
                in.putExtra("rub_num", vd.getRub_num());
                in.putExtra("sura_id", vd.getSurah_id());
                in.putExtra("sura_name", vd.getName_english());
                in.putExtra("sura_name_arabic", vd.getName_arabic());
                in.putExtra("activity_title", "Rub\'  "+vd.getRub_num());
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
        String sql = "select ayah.ayah_index,ayah.ayah_num,ayah.surah_id,ayah.page_num,ayah.rub_num,ayah.text_tashkeel,ayah.ayah_key,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic,ayah_indo.text as indo_pak,ut.text_uthmani_tajweed,u.text_uthmani\n" +
                "from ayah \n" +
                "inner join sura ON ayah.surah_id = sura.surah_id\n" +
                "inner join ayah_indo ON ayah.ayah_num = ayah_indo.ayah and ayah_indo.sura = ayah.surah_id "+
                "inner JOIN uthmani_tajweed ut ON ayah.ayah_key = ut.verse_key " +
                "inner JOIN uthmani u ON ayah.ayah_key = u.verse_key "+
                "group by rub_num " +
                "HAVING MIN(ayah_index) " +
                "order by rub_num asc";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Rub rub = new Rub();
                    rub.setAyah_index(cursor.getString(cursor.getColumnIndexOrThrow("ayah_index")));
                    rub.setSurah_id(cursor.getString(cursor.getColumnIndexOrThrow("surah_id")));
                    rub.setPage_num(cursor.getString(cursor.getColumnIndexOrThrow("page_num")));
                    rub.setRub_num(cursor.getString(cursor.getColumnIndexOrThrow("rub_num")));
                    rub.setText_tashkeel(cursor.getString(cursor.getColumnIndexOrThrow("text_tashkeel")));
                    rub.setAyah_key(cursor.getString(cursor.getColumnIndexOrThrow("ayah_key")));
                    rub.setName_simple(cursor.getString(cursor.getColumnIndexOrThrow("name_simple")));
                    rub.setName_complex(cursor.getString(cursor.getColumnIndexOrThrow("name_complex")));
                    rub.setName_english(cursor.getString(cursor.getColumnIndexOrThrow("name_english")));
                    rub.setName_arabic(cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")));
                    rub.setAyah_num(cursor.getString(cursor.getColumnIndexOrThrow("ayah_num")));
                    rub.setIndo_pak(cursor.getString(cursor.getColumnIndexOrThrow("indo_pak")));
                    rub.setText_uthmani(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani")));
                    rub.setText_uthmani_tajweed(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani_tajweed")));
                    rubs.add(rub);
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
        rubs = new ArrayList<Rub>();
        rvAdapter = new RubVViewAdapter(RubActivity.this, rubs, this);
        recyclerview.setAdapter(rvAdapter);
    }
}
