package com.akramhossain.quranulkarim;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.sentry.Sentry;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.JuzHizbRubViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;

import java.util.ArrayList;

public class JuzHizbRubDetailsActivity extends AppCompatActivity {

    public static String suraId;
    public static String suraName;
    public static String suraNameArabic;
    public static String suraJuzNum;
    public static String suraHizbNum;
    public static String suraRubNum;
    public static String activityTitle;
    TextView page_title;

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Ayah> ayahs;
    private static final String TAG = JuzHizbRubDetailsActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private JuzHizbRubViewAdapter rvAdapter;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 50;
    Integer counter = 0;
    String where_clause = "";
    String btn_next_where_clause = "";
    String btn_prev_where_clause = "";

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            suraId = extras.getString("sura_id");
            suraName = extras.getString("sura_name");
            suraNameArabic = extras.getString("sura_name_arabic");
            suraJuzNum = extras.getString("juz_num");
            suraHizbNum = extras.getString("hizb_num");
            suraRubNum = extras.getString("rub_num");
            activityTitle = extras.getString("activity_title");
        }

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_juz_hizb_rub_details);

        View rootView = findViewById(R.id.topBar);
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

        setTitle(activityTitle);

        page_title = (TextView) findViewById(R.id.page_title);
        page_title.setText(activityTitle);

        recyclerview = (RecyclerView) findViewById(R.id.ayah_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();



        if(suraJuzNum!=null && !suraJuzNum.isEmpty()){
            where_clause = "ayah.juz_num = "+suraJuzNum;
            btn_next_where_clause = "ayah.juz_num > "+suraJuzNum+" ORDER BY ayah.juz_num ASC limit 1";
            btn_prev_where_clause = "ayah.juz_num < "+suraJuzNum+" ORDER BY ayah.juz_num DESC limit 1";
        }
        if(suraHizbNum!=null && !suraHizbNum.isEmpty()){
            where_clause = "ayah.hizb_num = "+suraHizbNum;
            btn_next_where_clause = "ayah.hizb_num > "+suraHizbNum+" ORDER BY ayah.hizb_num ASC limit 1";
            btn_prev_where_clause = "ayah.hizb_num < "+suraHizbNum+" ORDER BY ayah.hizb_num DESC limit 1";
        }
        if(suraRubNum!=null && !suraRubNum.isEmpty()){
            where_clause = "ayah.rub_num = "+suraRubNum;
            btn_next_where_clause = "ayah.rub_num > "+suraRubNum+" ORDER BY ayah.rub_num ASC limit 1";
            btn_prev_where_clause = "ayah.rub_num < "+suraRubNum+" ORDER BY ayah.rub_num DESC limit 1";
        }

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                            String sql = String.format("SELECT COUNT(*) FROM ayah WHERE %s", where_clause);
                            Log.i(TAG, sql);
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
                                Log.e("On Scroll Count Check", e.getMessage());
                                //throw new RuntimeException("SQL Query: " + sql, e);
                                Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
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

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        getDataFromLocalDb();

        Button previousBtn = (Button) findViewById(R.id.previousBtn);
        previousBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql = String.format("SELECT ayah.*,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic " +
                        "FROM ayah " +
                        "inner join sura ON ayah.surah_id = sura.surah_id " +
                        "WHERE %s", btn_prev_where_clause);
                Log.i(TAG, sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        String juz_num = "";
                        String hizb_num = "";
                        String rub_num = "";
                        String next_sura_id = cursor.getString(cursor.getColumnIndexOrThrow("surah_id")).toString();
                        String next_sura_name = cursor.getString(cursor.getColumnIndexOrThrow("name_english")).toString();
                        String next_sura_name_arabic = cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")).toString();
                        String next_activity_title = "";
                        //
                        if(suraJuzNum!=null && !suraJuzNum.isEmpty()){
                            String str_juz_name = cursor.getString(cursor.getColumnIndexOrThrow("juz_num")).toString();
                            juz_num = str_juz_name;
                            next_activity_title = "Juz\' "+str_juz_name;
                            where_clause = "ayah.juz_num = "+str_juz_name;
                            btn_next_where_clause = "ayah.juz_num > "+str_juz_name+" ORDER BY ayah.juz_num ASC limit 1";
                            btn_prev_where_clause = "ayah.juz_num < "+str_juz_name+" ORDER BY ayah.juz_num DESC limit 1";
                        }
                        if(suraHizbNum!=null && !suraHizbNum.isEmpty()){
                            String str_hizb_num = cursor.getString(cursor.getColumnIndexOrThrow("hizb_num")).toString();
                            hizb_num = str_hizb_num;
                            next_activity_title = "Hizb "+str_hizb_num;
                            where_clause = "ayah.hizb_num = "+str_hizb_num;
                            btn_next_where_clause = "ayah.hizb_num > "+str_hizb_num+" ORDER BY ayah.hizb_num ASC limit 1";
                            btn_prev_where_clause = "ayah.hizb_num < "+str_hizb_num+" ORDER BY ayah.hizb_num DESC limit 1";
                        }
                        if(suraRubNum!=null && !suraRubNum.isEmpty()){
                            String str_rub_num = cursor.getString(cursor.getColumnIndexOrThrow("rub_num")).toString();
                            rub_num = str_rub_num;
                            next_activity_title = "Rub\' "+str_rub_num;
                            where_clause = "ayah.rub_num = "+str_rub_num;
                            btn_next_where_clause = "ayah.rub_num > "+str_rub_num+" ORDER BY ayah.rub_num ASC limit 1";
                            btn_prev_where_clause = "ayah.rub_num < "+str_rub_num+" ORDER BY ayah.rub_num DESC limit 1";
                        }

                        suraId = next_sura_id;
                        suraName = next_sura_name;
                        suraNameArabic = next_sura_name_arabic;
                        suraJuzNum = juz_num;
                        suraHizbNum = hizb_num;
                        suraRubNum = rub_num;
                        activityTitle = next_activity_title;

                        page_title.setText(next_activity_title);
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

                    setRecyclerViewAdapter();
                    getDataFromLocalDb();
                }

            }
        });
        //
        Button nextBtn = (Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql = String.format("SELECT ayah.*,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic " +
                        "FROM ayah " +
                        "inner join sura ON ayah.surah_id = sura.surah_id  " +
                        "WHERE %s", btn_next_where_clause);
                Log.i(TAG, sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        String juz_num = "";
                        String hizb_num = "";
                        String rub_num = "";
                        String next_sura_id = cursor.getString(cursor.getColumnIndexOrThrow("surah_id")).toString();
                        String next_sura_name = cursor.getString(cursor.getColumnIndexOrThrow("name_english")).toString();
                        String next_sura_name_arabic = cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")).toString();
                        String next_activity_title = "";
                        //
                        if(suraJuzNum!=null && !suraJuzNum.isEmpty()){
                            String str_juz_name = cursor.getString(cursor.getColumnIndexOrThrow("juz_num")).toString();
                            juz_num = str_juz_name;
                            next_activity_title = "Juz\' "+str_juz_name;
                            where_clause = "ayah.juz_num = "+str_juz_name;
                            btn_next_where_clause = "ayah.juz_num > "+str_juz_name+" ORDER BY ayah.juz_num ASC limit 1";
                            btn_prev_where_clause = "ayah.juz_num < "+str_juz_name+" ORDER BY ayah.juz_num DESC limit 1";
                        }
                        if(suraHizbNum!=null && !suraHizbNum.isEmpty()){
                            String str_hizb_num = cursor.getString(cursor.getColumnIndexOrThrow("hizb_num")).toString();
                            hizb_num = str_hizb_num;
                            next_activity_title = "Hizb "+str_hizb_num;
                            where_clause = "ayah.hizb_num = "+str_hizb_num;
                            btn_next_where_clause = "ayah.hizb_num > "+str_hizb_num+" ORDER BY ayah.hizb_num ASC limit 1";
                            btn_prev_where_clause = "ayah.hizb_num < "+str_hizb_num+" ORDER BY ayah.hizb_num DESC limit 1";
                        }
                        if(suraRubNum!=null && !suraRubNum.isEmpty()){
                            String str_rub_num = cursor.getString(cursor.getColumnIndexOrThrow("rub_num")).toString();
                            rub_num = str_rub_num;
                            next_activity_title = "Rub\' "+str_rub_num;
                            where_clause = "ayah.rub_num = "+str_rub_num;
                            btn_next_where_clause = "ayah.rub_num > "+str_rub_num+" ORDER BY ayah.rub_num ASC limit 1";
                            btn_prev_where_clause = "ayah.rub_num < "+str_rub_num+" ORDER BY ayah.rub_num DESC limit 1";
                        }

                        suraId = next_sura_id;
                        suraName = next_sura_name;
                        suraNameArabic = next_sura_name_arabic;
                        suraJuzNum = juz_num;
                        suraHizbNum = hizb_num;
                        suraRubNum = rub_num;
                        activityTitle = next_activity_title;

                        page_title.setText(next_activity_title);
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
                    setRecyclerViewAdapter();
                    getDataFromLocalDb();
                }

            }
        });

        if (checkPermission()) {

        }else{
            requestPermission();
        }
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        /*String sql = String.format("SELECT ayah.*,sura.name_simple,sura.name_complex,sura.name_english,sura.name_arabic,transliteration.trans,ayah_indo.text as indo_pak,ut.text_uthmani_tajweed,u.text_uthmani \n" +
                "FROM ayah \n" +
                "LEFT JOIN sura ON ayah.surah_id = sura.surah_id \n" +
                "LEFT join transliteration ON ayah.ayah_num = transliteration.ayat_id and transliteration.sura_id = ayah.surah_id \n" +
                "LEFT join ayah_indo ON ayah.ayah_num = ayah_indo.ayah and ayah_indo.sura = ayah.surah_id " +
                "LEFT JOIN uthmani_tajweed ut ON ayah.ayah_key = ut.verse_key " +
                "LEFT JOIN uthmani u ON ayah.ayah_key = u.verse_key\n" +
                "WHERE %s order by ayah_index ASC limit %d,%d", where_clause, offset, limit);*/
        String sql = "SELECT ayah.*, sura.name_simple, sura.name_complex, sura.name_english, sura.name_arabic, transliteration.trans, " +
                "ayah_indo.text as indo_pak, ut.text_uthmani_tajweed, u.text_uthmani " +
                "FROM ayah " +
                "LEFT JOIN sura ON ayah.surah_id = sura.surah_id " +
                "LEFT JOIN transliteration ON ayah.ayah_num = transliteration.ayat_id AND transliteration.sura_id = ayah.surah_id " +
                "LEFT JOIN ayah_indo ON ayah.ayah_num = ayah_indo.ayah AND ayah_indo.sura = ayah.surah_id " +
                "LEFT JOIN uthmani_tajweed ut ON ayah.ayah_key = ut.verse_key " +
                "LEFT JOIN uthmani u ON ayah.ayah_key = u.verse_key " +
                (where_clause.isEmpty() ? "" : "WHERE " + where_clause)+ " ORDER BY ayah_index ASC LIMIT " + offset + ", " + limit;
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Ayah ayah = new Ayah();
                    ayah.setAyah_index(cursor.getString(cursor.getColumnIndexOrThrow("ayah_index")));
                    ayah.setSurah_id(cursor.getString(cursor.getColumnIndexOrThrow("surah_id")));
                    ayah.setAyah_num(cursor.getString(cursor.getColumnIndexOrThrow("ayah_num")));
                    ayah.setPage_num(cursor.getString(cursor.getColumnIndexOrThrow("page_num")));
                    ayah.setJuz_num(cursor.getString(cursor.getColumnIndexOrThrow("juz_num")));
                    ayah.setHizb_num(cursor.getString(cursor.getColumnIndexOrThrow("hizb_num")));
                    ayah.setRub_num(cursor.getString(cursor.getColumnIndexOrThrow("rub_num")));
                    ayah.setText(cursor.getString(cursor.getColumnIndexOrThrow("text")));
                    ayah.setAyah_key(cursor.getString(cursor.getColumnIndexOrThrow("ayah_key")));
                    ayah.setSajdah(cursor.getString(cursor.getColumnIndexOrThrow("sajdah")));
                    ayah.setText_tashkeel(cursor.getString(cursor.getColumnIndexOrThrow("text_tashkeel")));
                    ayah.setContent_en(cursor.getString(cursor.getColumnIndexOrThrow("content_en")));
                    ayah.setContent_bn(cursor.getString(cursor.getColumnIndexOrThrow("content_bn")));
                    ayah.setAudio_duration(cursor.getString(cursor.getColumnIndexOrThrow("audio_duration")));
                    ayah.setAudio_url(cursor.getString(cursor.getColumnIndexOrThrow("audio_url")));
                    ayah.setName_simple(cursor.getString(cursor.getColumnIndexOrThrow("name_simple")));
                    ayah.setName_complex(cursor.getString(cursor.getColumnIndexOrThrow("name_complex")));
                    ayah.setName_english(cursor.getString(cursor.getColumnIndexOrThrow("name_english")));
                    ayah.setName_arabic(cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")));
                    ayah.setTrans(cursor.getString(cursor.getColumnIndexOrThrow("trans")));
                    ayah.setIndo_pak(cursor.getString(cursor.getColumnIndexOrThrow("indo_pak")));
                    ayah.setText_uthmani(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani")));
                    ayah.setText_uthmani_tajweed(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani_tajweed")));
                    ayahs.add(ayah);
                } while (cursor.moveToNext());
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
        ayahs = new ArrayList<Ayah>();
        rvAdapter = new JuzHizbRubViewAdapter(JuzHizbRubDetailsActivity.this, ayahs, this);
        recyclerview.setAdapter(rvAdapter);
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(JuzHizbRubDetailsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(JuzHizbRubDetailsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(JuzHizbRubDetailsActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(JuzHizbRubDetailsActivity.this, permissions(), PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };
    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }
}
