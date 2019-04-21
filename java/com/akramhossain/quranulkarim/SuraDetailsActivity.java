package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.SuraDetailsViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;

import java.util.ArrayList;

public class SuraDetailsActivity extends Activity {

    public static String suraId;
    public static String suraName;
    public static String suraNameArabic;
    public static String suraLastPosition="";

    private RecyclerView recyclerview;
    private SuraDetailsViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Ayah> ayahs;
    private static final String TAG = SuraDetailsActivity.class.getSimpleName();
    DatabaseHelper dbhelper;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 100;
    Integer counter = 0;
    TextView titleEn,titleAr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            suraId = extras.getString("sura_id");
            suraName = extras.getString("sura_name");
            suraNameArabic = extras.getString("sura_name_arabic");
            suraLastPosition = extras.getString("position");
        }

        setTitle(suraNameArabic+"-"+suraName);

        setContentView(R.layout.activity_sura_details);

        titleEn = (TextView) findViewById(R.id.name_title_en);
        titleEn.setText(suraName);

        titleAr = (TextView) findViewById(R.id.name_title_ar);
        titleAr.setText(suraNameArabic);

        recyclerview = (RecyclerView) findViewById(R.id.ayah_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    SQLiteDatabase db1 = dbhelper.getWritableDatabase();
                    String sql1 = "DELETE FROM last_position";
                    try {
                        db1.execSQL(sql1);
                    }catch (Exception e){
                        Log.i("Last Position Deleted", e.getMessage());
                    }finally {
                        db1.close();
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int firstVisiblePosition = mLayoutManager.findLastVisibleItemPosition();
                    //Log.i("Last Position Visible", Integer.toString(firstVisiblePosition));
                    //int firstVisiblePosition = mLayoutManager.findLastVisibleItemPosition();
                    SQLiteDatabase db1 = dbhelper.getWritableDatabase();
                    String sql1 = "DELETE FROM last_position";
                    try {
                        db1.execSQL(sql1);
                        ContentValues values = new ContentValues();
                        values.put("sura_id", suraId);
                        values.put("position", firstVisiblePosition);
                        dbhelper.getWritableDatabase().insertOrThrow("last_position", "", values);
                    }
                    catch (Exception e){
                        Log.i("Last Position", e.getMessage());
                    }
                    finally {
                        db1.close();
                    }

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

        if (suraLastPosition != null && !suraLastPosition.isEmpty()) {
            recyclerview.scrollToPosition(Integer.parseInt(suraLastPosition));
        }

        dbhelper = new DatabaseHelper(getApplicationContext());

        getDataFromLocalDb();

        Button previousBtn = (Button) findViewById(R.id.previousBtn);
        Button nextBtn = (Button) findViewById(R.id.nextBtn);
        final Button quickLinkBtn = (Button) findViewById(R.id.quickLinkBtn);

        String checksql = "SELECT * FROM quick_link WHERE sura_id = "+suraId;
        SQLiteDatabase chkdb = dbhelper.getWritableDatabase();
        Cursor cursor1 = chkdb.rawQuery(checksql,null);

        try {
            if (cursor1.moveToFirst()) {
                quickLinkBtn.setText("Remove from quick links");
                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
            } else {
                quickLinkBtn.setText("Add to quick links");
                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_upload, 0, 0, 0);
            }
        }catch (Exception e){
            Log.i("Quick Link Check", e.getMessage());
        }finally {
            if (cursor1 != null && !cursor1.isClosed()){
                cursor1.close();
            }
            chkdb.close();
        }

        previousBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                String sql = "SELECT * FROM sura WHERE surah_id < "+suraId+" order by surah_id DESC limit 1";
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        String prevSuraId = cursor.getString(cursor.getColumnIndex("surah_id")).toString();
                        String prevSuraNameEn = cursor.getString(cursor.getColumnIndex("name_english")).toString();
                        String prevSuraNameAr = cursor.getString(cursor.getColumnIndex("name_arabic")).toString();

                        suraId = prevSuraId;
                        suraName = prevSuraNameEn;
                        suraNameArabic = prevSuraNameAr;

                        titleEn.setText(suraName);
                        titleAr.setText(suraNameArabic);

                        //SQLiteDatabase db1 = dbhelper.getWritableDatabase();
                        String checksql = "SELECT * FROM quick_link WHERE sura_id = "+suraId;
                        Cursor cursor1 = db.rawQuery(checksql,null);

                        Log.i("Quick Link Check Inner", checksql);

                        try {
                            if (cursor1.moveToFirst()) {
                                quickLinkBtn.setText("Remove from quick links");
                                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
                            } else {
                                quickLinkBtn.setText("Add to quick links");
                                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_upload, 0, 0, 0);
                            }
                        }catch (Exception e){
                            Log.i(TAG, e.getMessage());
                        }finally {
                            if (cursor1 != null && !cursor1.isClosed()){
                                cursor1.close();
                            }
                        }
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor != null && !cursor.isClosed()){
                        cursor.close();
                    }
                    db.close();

                    setRecyclerViewAdapter();
                    getDataFromLocalDb();
                }

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                String sql = "SELECT * FROM sura WHERE surah_id > "+suraId+" order by surah_id ASC limit 1";
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        String prevSuraId = cursor.getString(cursor.getColumnIndex("surah_id")).toString();
                        String prevSuraNameEn = cursor.getString(cursor.getColumnIndex("name_english")).toString();
                        String prevSuraNameAr = cursor.getString(cursor.getColumnIndex("name_arabic")).toString();

                        suraId = prevSuraId;
                        suraName = prevSuraNameEn;
                        suraNameArabic = prevSuraNameAr;

                        titleEn.setText(suraName);
                        titleAr.setText(suraNameArabic);

                        //SQLiteDatabase db1 = dbhelper.getWritableDatabase();
                        String checksql = "SELECT * FROM quick_link WHERE sura_id = "+suraId;
                        Cursor cursor1 = db.rawQuery(checksql,null);

                        Log.i("Quick Link Check Inner", checksql);

                        try {
                            if (cursor1.moveToFirst()) {
                                quickLinkBtn.setText("Remove from quick links");
                                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
                            } else {
                                quickLinkBtn.setText("Add to quick links");
                                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_upload, 0, 0, 0);
                            }
                        }catch (Exception e){
                            Log.i(TAG, e.getMessage());
                        }
                        finally {
                            if (cursor1 != null && !cursor1.isClosed()){
                                cursor1.close();
                            }
                        }
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor != null && !cursor.isClosed()){
                        cursor.close();
                    }
                    db.close();
                    setRecyclerViewAdapter();
                    getDataFromLocalDb();
                }

            }
        });

        quickLinkBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                String sql = "SELECT * FROM quick_link WHERE sura_id = "+suraId;
                Log.i(TAG, sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        db.execSQL("DELETE FROM quick_link WHERE sura_id = " + suraId);
                        Toast.makeText(getApplicationContext(), "Deleted from quick links.", Toast.LENGTH_LONG).show();
                        quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_upload, 0, 0, 0);
                        quickLinkBtn.setText("Add to quick links");
                    }
                    else {
                        ContentValues values = new ContentValues();
                        values.put("sura_id", suraId);
                        dbhelper.getWritableDatabase().insertOrThrow("quick_link", "", values);
                        Toast.makeText(getApplicationContext(), "Added to quick links.", Toast.LENGTH_LONG).show();
                        quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
                        quickLinkBtn.setText("Remove from quick links");
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

            }
        });
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM ayah WHERE surah_id = "+suraId+" order by ayah_index ASC limit " + offset + "," + limit;
        //Log.i(TAG, sql);
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

    private void setRecyclerViewAdapter() {
        ayahs = new ArrayList<Ayah>();
        rvAdapter = new SuraDetailsViewAdapter(SuraDetailsActivity.this, ayahs);
        recyclerview.setAdapter(rvAdapter);
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }
}
