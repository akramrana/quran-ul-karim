package com.codxplore.quranulkarim;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.SearchView;

import com.codxplore.quranulkarim.adapter.WordListViewAdapter;
import com.codxplore.quranulkarim.helper.DatabaseHelper;
import com.codxplore.quranulkarim.model.Word;

import java.util.ArrayList;

public class DictionaryActivity extends Activity implements SearchView.OnQueryTextListener{

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = DictionaryActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private ArrayList<Word> words;
    private WordListViewAdapter rvAdapter;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 50;
    Integer counter = 0;
    SearchView editsearch;
    String searchTxt = "";
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        setTitle("Dictionary");

        recyclerview = (RecyclerView) findViewById(R.id.dictionary_word_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        dbhelper = new DatabaseHelper(getApplicationContext());

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
                            SQLiteDatabase db = dbhelper.getWritableDatabase();
                            String sql = "SELECT COUNT(*) FROM words";
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

        getDataFromLocalDb();

        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "";
        if(searchTxt.equals("")) {
             sql = "SELECT * FROM words order by word_id ASC limit " + offset + "," + limit;
        }else{
             sql = "SELECT * " +
                     "FROM words " +
                     "WHERE translation LIKE '%"+searchTxt+"%' OR transliteration LIKE '%"+searchTxt+"%' OR arabic LIKE '%"+searchTxt+"%' " +
                     "Order by word_id ASC " +
                     "limit " + offset + "," + limit;
        }
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Word word = new Word();
                    word.setAyah_index(cursor.getString(cursor.getColumnIndex("ayah_index")));
                    word.setWord_id(cursor.getString(cursor.getColumnIndex("word_id")));
                    word.setArabic(cursor.getString(cursor.getColumnIndex("arabic")));
                    word.setTransliteration(cursor.getString(cursor.getColumnIndex("transliteration")));
                    word.setTranslation(cursor.getString(cursor.getColumnIndex("translation")));
                    word.setCode(cursor.getString(cursor.getColumnIndex("code")));
                    word.setCode_hex(cursor.getString(cursor.getColumnIndex("code_hex")));
                    word.setCode_dec(cursor.getString(cursor.getColumnIndex("code_dec")));
                    word.setAyah_key(cursor.getString(cursor.getColumnIndex("ayah_key")));
                    word.setPosition(cursor.getString(cursor.getColumnIndex("position")));
                    words.add(word);
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
        words = new ArrayList<Word>();
        rvAdapter = new WordListViewAdapter(DictionaryActivity.this, words);
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
                    rvAdapter = new WordListViewAdapter(DictionaryActivity.this, words);
                    recyclerview.setAdapter(rvAdapter);
                    offset = 0;
                    getDataFromLocalDb();
                }
                else{
                    words = new ArrayList<Word>();
                    rvAdapter = new WordListViewAdapter(DictionaryActivity.this, words);
                    recyclerview.setAdapter(rvAdapter);
                    offset = 0;
                    getDataFromLocalDb();
                }
            }
        }, 100);


        return true;
    }

}
