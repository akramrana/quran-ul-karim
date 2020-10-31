package com.akramhossain.quranulkarim;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.WordListViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Word;

import java.util.ArrayList;

public class WordMeaningActivity extends Activity {

    public static String ayah_index;
    public static String text_tashkeel;
    public static String content_en;
    public static String content_bn;

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = WordMeaningActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private ArrayList<Word> words;
    private WordListViewAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            ayah_index = extras.getString("ayah_index");
            text_tashkeel = extras.getString("text_tashkeel");
            content_en = extras.getString("content_en");
            content_bn = extras.getString("content_bn");
        }

        setTitle("Word Meaning");

        setContentView(R.layout.activity_word_meaning);

        TextView titleAr = (TextView) findViewById(R.id.name_title_ar);
        titleAr.setText(text_tashkeel);

        recyclerview = (RecyclerView) findViewById(R.id.word_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        dbhelper = new DatabaseHelper(getApplicationContext());

        getDataFromLocalDb();
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM words WHERE ayah_index = "+ayah_index+" order by word_id ASC";
        //Log.i(TAG, sql);
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
        rvAdapter = new WordListViewAdapter(WordMeaningActivity.this, words);
        recyclerview.setAdapter(rvAdapter);
    }
}
