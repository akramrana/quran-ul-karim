package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.SuraDetailsViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;
import com.akramhossain.quranulkarim.util.ConnectionDetector;
import com.akramhossain.quranulkarim.task.GetJsonFromUrlTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    public String url;

    private static final int PERMISSION_REQUEST_CODE = 100;

    TextView play_audio;
    TextView pause_audio;
    TextView stop_audio;
    TextView resume_audio;

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

        url = "http://websites.codxplore.com/islamicvideo/api/patch?sura_id="+suraId;
        //url = "http://10.0.2.2/islamicvideo/api/patch?sura_id="+suraId;

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

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        //getPatchFromInternet();
        if (checkPermission()) {

        }else{
            requestPermission();
        }

        play_audio = (TextView) findViewById(R.id.play_audio);
        pause_audio = (TextView) findViewById(R.id.pause_audio);
        resume_audio = (TextView) findViewById(R.id.resume_audio);
        stop_audio = (TextView) findViewById(R.id.stop_audio);

        if (isInternetPresent) {
            play_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioPlay.stopAudio();
                    AudioPlay.playAudio(getApplicationContext(), "https://download.quranicaudio.com/qdc/mishari_al_afasy/murattal/" + suraId + ".mp3");

                    play_audio.setVisibility(View.GONE);
                    pause_audio.setVisibility(View.VISIBLE);
                    resume_audio.setVisibility(View.GONE);
                    stop_audio.setVisibility(View.VISIBLE);
                }
            });

            pause_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioPlay.pauseAudio();
                    play_audio.setVisibility(View.GONE);
                    pause_audio.setVisibility(View.GONE);
                    resume_audio.setVisibility(View.VISIBLE);
                    stop_audio.setVisibility(View.VISIBLE);
                }
            });

            resume_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioPlay.resumeAudio();
                    play_audio.setVisibility(View.GONE);
                    pause_audio.setVisibility(View.VISIBLE);
                    resume_audio.setVisibility(View.GONE);
                    stop_audio.setVisibility(View.VISIBLE);
                }
            });

            stop_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioPlay.stopAudio();
                    play_audio.setVisibility(View.VISIBLE);
                    pause_audio.setVisibility(View.GONE);
                    resume_audio.setVisibility(View.GONE);
                    stop_audio.setVisibility(View.GONE);
                }
            });
        }

    }

    private void getPatchFromInternet() {
        if (isInternetPresent) {
            new GetJsonFromUrlTask(this, url).execute();
        }
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "SELECT ayah.*,sura.name_arabic,sura.name_complex,sura.name_english,sura.name_simple " +
                "FROM ayah " +
                "LEFT join sura ON ayah.surah_id = sura.surah_id " +
                "WHERE ayah.surah_id = "+suraId+" " +
                "order by ayah.ayah_index ASC " +
                "limit " + offset + "," + limit;
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
                    //
                    ayah.setName_simple(cursor.getString(cursor.getColumnIndex("name_simple")));
                    ayah.setName_complex(cursor.getString(cursor.getColumnIndex("name_complex")));
                    ayah.setName_english(cursor.getString(cursor.getColumnIndex("name_english")));
                    ayah.setName_arabic(cursor.getString(cursor.getColumnIndex("name_arabic")));
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
        rvAdapter = new SuraDetailsViewAdapter(SuraDetailsActivity.this, ayahs, this);
        recyclerview.setAdapter(rvAdapter);
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }

    public void parseJsonResponse(String result) {
        //Log.i(TAG, result);
        try {
            JSONObject res = new JSONObject(result);
            JSONArray jarray = new JSONArray(res.getString("data"));

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);
                //Log.i(TAG, jObject.toString());
                String where_column = jObject.getString("where_column");
                String where_value = jObject.getString("where_value");
                String table_name = jObject.getString("table_name");
                //
                JSONArray jarrayColumn = new JSONArray(jObject.getString("columns"));
                for (int j = 0; j < jarrayColumn.length(); i++) {
                    JSONObject jObjectColumn = jarrayColumn.getJSONObject(i);
                    String update_column = jObjectColumn.getString("update_column");
                    String update_value = jObjectColumn.getString("update_value");

                    //Log.i(TAG, update_column+"="+update_value);

                    SQLiteDatabase db = dbhelper.getWritableDatabase();
                    try {
                        ContentValues cv = new ContentValues();
                        cv.put(update_column, update_value);
                        String selection = (where_column + " = ?");
                        String[] selectionArgs = {String.valueOf(where_value)};
                        db.update(table_name, cv, selection, selectionArgs);

                    }catch (Exception e){
                        Log.i(TAG, e.getMessage());
                    }
                    finally {
                        db.close();
                    }
                }
//                String sql = jObject.toString();
//                SQLiteDatabase db = dbhelper.getReadableDatabase();
//                try {
//                    db.execSQL(sql);
//                    //Log.i(TAG, sql);
//                }catch (Exception e){
//                    Log.i(TAG, e.getMessage());
//                }finally {
//                    db.close();
//                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SuraDetailsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SuraDetailsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(SuraDetailsActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SuraDetailsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
}
