package com.akramhossain.quranulkarim;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.SearchView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.WordListViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Word;

import java.util.ArrayList;

public class DictionaryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = DictionaryActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private ArrayList<Word> words;
    private WordListViewAdapter rvAdapter;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 100;
    Integer counter = 0;
    SearchView editsearch;
    String searchTxt = "";
    Handler mHandler = new Handler();
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_dictionary);

        View rootView = findViewById(R.id.topBarDictionary);
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

        setTitle("By Word");

        recyclerview = (RecyclerView) findViewById(R.id.dictionary_word_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

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
                            String sql = "SELECT count(*) from words";
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

        getDataFromLocalDb();

        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);

        if (checkPermission()) {

        }else{
            requestPermission();
        }
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "";
        searchTxt = searchTxt.replaceAll("\'","");
        if(searchTxt.equals("")) {
             sql = "SELECT words.*, b.translate_bn as bangla,b.words_id " +
                     "FROM words " +
                     "inner join bywords b on words.word_id = b._id " +
                     //"group by arabic  " +
                     "order by word_id ASC limit " + offset + "," + limit;
        }else{
             sql = "SELECT words.*, b.translate_bn as bangla,b.words_id " +
                     "FROM words " +
                     "inner join bywords b on words.word_id = b._id " +
                     "WHERE translation LIKE '%"+searchTxt+"%' OR transliteration LIKE '%"+searchTxt+"%' OR arabic LIKE '%"+searchTxt+"%' " +
                     //"group by arabic  " +
                     "Order by word_id ASC " +
                     "limit " + offset + "," + limit;
        }
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Word word = new Word();
                    word.setAyah_index(cursor.getString(cursor.getColumnIndexOrThrow("ayah_index")).toString());
                    word.setWord_id(cursor.getString(cursor.getColumnIndexOrThrow("word_id")).toString());
                    word.setArabic(cursor.getString(cursor.getColumnIndexOrThrow("arabic")).toString());
                    word.setTransliteration(cursor.getString(cursor.getColumnIndexOrThrow("transliteration")).toString());
                    word.setTranslation(cursor.getString(cursor.getColumnIndexOrThrow("translation")).toString());
                    word.setCode(cursor.getString(cursor.getColumnIndexOrThrow("code")).toString());
                    word.setCode_hex(cursor.getString(cursor.getColumnIndexOrThrow("code_hex")).toString());
                    word.setCode_dec(cursor.getString(cursor.getColumnIndexOrThrow("code_dec")).toString());
                    word.setAyah_key(cursor.getString(cursor.getColumnIndexOrThrow("ayah_key")).toString());
                    word.setPosition(cursor.getString(cursor.getColumnIndexOrThrow("position")).toString());
                    word.setBangla(cursor.getString(cursor.getColumnIndexOrThrow("bangla")));
                    word.setWords_id(cursor.getString(cursor.getColumnIndexOrThrow("words_id")));
                    words.add(word);
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
        words = new ArrayList<Word>();
        rvAdapter = new WordListViewAdapter(DictionaryActivity.this, words, this);
        recyclerview.setAdapter(rvAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchTxt = query;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int length = searchTxt.length();
                if(length > 0) {
                    words = new ArrayList<Word>();
                    rvAdapter = new WordListViewAdapter(DictionaryActivity.this, words, DictionaryActivity.this);
                    recyclerview.setAdapter(rvAdapter);
                    offset = 0;
                    getDataFromLocalDb();
                }
                else{
                    words = new ArrayList<Word>();
                    rvAdapter = new WordListViewAdapter(DictionaryActivity.this, words, DictionaryActivity.this);
                    recyclerview.setAdapter(rvAdapter);
                    offset = 0;
                    getDataFromLocalDb();
                }
            }
        }, 100);


        return true;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(DictionaryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(DictionaryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(DictionaryActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(DictionaryActivity.this, permissions(), PERMISSION_REQUEST_CODE);
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
