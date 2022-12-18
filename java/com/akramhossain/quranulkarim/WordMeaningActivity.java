package com.akramhossain.quranulkarim;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.WordListViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Word;

import java.util.ArrayList;

public class WordMeaningActivity extends AppCompatActivity {

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
    private static final int PERMISSION_REQUEST_CODE = 100;

    Typeface fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem;
    SharedPreferences mPrefs;

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

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        fontUthmani = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");

        setTitle("Word Meaning");

        setContentView(R.layout.activity_word_meaning);

        TextView titleAr = (TextView) findViewById(R.id.name_title_ar);
        titleAr.setText(text_tashkeel);

        recyclerview = (RecyclerView) findViewById(R.id.word_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        getDataFromLocalDb();

        if (checkPermission()) {

        }else{
            requestPermission();
        }

        String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Arabic Regular");
        if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
            titleAr.setTypeface(fontAlmajeed);
        }
        if(mp_arabicFontFamily.equals("Al Qalam Quran")){
            titleAr.setTypeface(fontAlQalam);
        }
        if(mp_arabicFontFamily.equals("Uthmanic Script")){
            titleAr.setTypeface(fontUthmani);
        }
        if(mp_arabicFontFamily.equals("Noore Hidayat")){
            titleAr.setTypeface(fontNooreHidayat);
        }
        if(mp_arabicFontFamily.equals("Saleem Quran")){
            titleAr.setTypeface(fontSaleem);
        }

        titleAr.setMovementMethod(new ScrollingMovementMethod());
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT words.*,(select translate_bn from bywords b where b._id =  words.word_id) as bangla " +
                "FROM words " +
                "WHERE ayah_index = "+ayah_index+" order by word_id ASC";
        //Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Word word = new Word();
                    word.setAyah_index(cursor.getString(cursor.getColumnIndexOrThrow("ayah_index")));
                    word.setWord_id(cursor.getString(cursor.getColumnIndexOrThrow("word_id")));
                    word.setArabic(cursor.getString(cursor.getColumnIndexOrThrow("arabic")));
                    word.setTransliteration(cursor.getString(cursor.getColumnIndexOrThrow("transliteration")));
                    word.setTranslation(cursor.getString(cursor.getColumnIndexOrThrow("translation")));
                    word.setCode(cursor.getString(cursor.getColumnIndexOrThrow("code")));
                    word.setCode_hex(cursor.getString(cursor.getColumnIndexOrThrow("code_hex")));
                    word.setCode_dec(cursor.getString(cursor.getColumnIndexOrThrow("code_dec")));
                    word.setAyah_key(cursor.getString(cursor.getColumnIndexOrThrow("ayah_key")));
                    word.setPosition(cursor.getString(cursor.getColumnIndexOrThrow("position")));
                    word.setBangla(cursor.getString(cursor.getColumnIndexOrThrow("bangla")));
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
        rvAdapter = new WordListViewAdapter(WordMeaningActivity.this, words, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(WordMeaningActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(WordMeaningActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(WordMeaningActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(WordMeaningActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
}
