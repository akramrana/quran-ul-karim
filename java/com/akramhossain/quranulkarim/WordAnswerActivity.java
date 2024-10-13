package com.akramhossain.quranulkarim;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.util.ConnectionDetector;

import androidx.appcompat.app.AppCompatActivity;

public class WordAnswerActivity extends AppCompatActivity {

    private static final String TAG = WordAnswerActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;
    SharedPreferences mPrefs;
    TextView quiz_arabic,transliteration_en,transliteration_bn;
    TextView answer_one_en,answer_one_bn,answer_two_en,answer_two_bn,answer_three_en,answer_three_bn,answer_four_en,answer_four_bn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_answer);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        fontTahaNaskh = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/kitab.ttf");

        quiz_arabic = (TextView) findViewById(R.id.quiz_arabic);
        transliteration_en = (TextView) findViewById(R.id.transliteration_en);
        //transliteration_bn = (TextView) findViewById(R.id.transliteration_bn);
        //transliteration_bn.setTypeface(font);
        answer_one_en = (TextView) findViewById(R.id.answer_one_en);
        answer_one_bn = (TextView) findViewById(R.id.answer_one_bn);
        answer_one_bn.setTypeface(font);

        answer_two_en = (TextView) findViewById(R.id.answer_two_en);
        answer_two_bn = (TextView) findViewById(R.id.answer_two_bn);
        answer_two_bn.setTypeface(font);

        answer_three_en = (TextView) findViewById(R.id.answer_three_en);
        answer_three_bn = (TextView) findViewById(R.id.answer_three_bn);
        answer_three_bn.setTypeface(font);

        answer_four_en = (TextView) findViewById(R.id.answer_four_en);
        answer_four_bn = (TextView) findViewById(R.id.answer_four_bn);
        answer_four_bn.setTypeface(font);

        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
        String mp_arFz = mPrefs.getString("arFontSize", "30");
        if (mp_arabicFontFamily.equals("Al Majeed Quranic Font")) {
            quiz_arabic.setTypeface(fontAlmajeed);
        }
        if (mp_arabicFontFamily.equals("Al Qalam Quran")) {
            quiz_arabic.setTypeface(fontAlQalam);
        }
        if (mp_arabicFontFamily.equals("Noore Huda")) {
            quiz_arabic.setTypeface(fontUthmani);
        }
        if (mp_arabicFontFamily.equals("Noore Hidayat")) {
            quiz_arabic.setTypeface(fontNooreHidayat);
        }
        if (mp_arabicFontFamily.equals("Saleem Quran")) {
            quiz_arabic.setTypeface(fontSaleem);
        }
        if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
            quiz_arabic.setTypeface(fontTahaNaskh);
        }
        if(mp_arabicFontFamily.equals("Arabic Regular")){
            quiz_arabic.setTypeface(fontKitab);
        }
        if (!mp_arFz.equals("")) {
            quiz_arabic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
        }

        getDataFromLocalDb();

        Button skip_button = (Button) findViewById(R.id.skip_button);
        skip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromLocalDb();
            }
        });

    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select w.word_id,w.arabic,w.translation, w.transliteration,b.translate_bn,b.words_ar " +
                "from words w " +
                "inner join bywords b on w.word_id = b._id " +
                "where w.word_id NOT IN(select wa.word_id from word_answers wa)" +
                "order by RANDOM() " +
                "limit 1";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                String arabic = cursor.getString(cursor.getColumnIndexOrThrow("arabic"));
                String translation = cursor.getString(cursor.getColumnIndexOrThrow("translation"));
                String transliteration = cursor.getString(cursor.getColumnIndexOrThrow("transliteration"));
                String translate_bn = cursor.getString(cursor.getColumnIndexOrThrow("translate_bn"));
                String word_id = cursor.getString(cursor.getColumnIndexOrThrow("word_id"));
                String words_ar = cursor.getString(cursor.getColumnIndexOrThrow("words_ar"));
                Log.d("arabic",arabic);

                quiz_arabic.setText(arabic);
                transliteration_en.setText(transliteration);

                SQLiteDatabase db2 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String choiceSql = "SELECT * FROM( " +
                        "SELECT w.word_id, w.arabic, w.translation, w.transliteration, b.translate_bn, b.words_ar " +
                        "FROM words w " +
                        "INNER JOIN bywords b ON w.word_id = b._id " +
                        "WHERE w.word_id = " +word_id +
                        " UNION ALL " +
                        "SELECT w.word_id, w.arabic, w.translation, w.transliteration, b.translate_bn, b.words_ar " +
                        "FROM words w " +
                        "INNER JOIN bywords b ON w.word_id = b._id " +
                        "WHERE w.word_id in (SELECT words.word_id from words where words.word_id != "+word_id+" order by RANDOM() limit 3) "+
                        " LIMIT 4 " +
                        ") as tmp " +
                        "order by RANDOM() ";

                Cursor cursor2 = db2.rawQuery(choiceSql, null);
                try {
                    if (cursor2.moveToFirst()) {
                        int i = 0;
                        do {
                            System.out.println("i is: " + i);
                            String translation1 = cursor2.getString(cursor2.getColumnIndexOrThrow("translation"));
                            String translate_bn1 = cursor2.getString(cursor2.getColumnIndexOrThrow("translate_bn"));
                            if(i==0){
                                answer_one_en.setText(translation1.trim());
                                answer_one_bn.setText(translate_bn1.trim());
                            }
                            else if(i==1){
                                answer_two_en.setText(translation1.trim());
                                answer_two_bn.setText(translate_bn1.trim());
                            }
                            else if(i==2){
                                answer_three_en.setText(translation1.trim());
                                answer_three_bn.setText(translate_bn1.trim());
                            }
                            else if(i==3){
                                answer_four_en.setText(translation1.trim());
                                answer_four_bn.setText(translate_bn1.trim());
                            }
                            i++;
                        }while (cursor2.moveToNext());
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor2 != null && !cursor2.isClosed()){
                        cursor2.close();
                    }
                    db2.close();
                }

            }
        }catch (Exception e){
            Log.i(TAG, e.getMessage());
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }
}