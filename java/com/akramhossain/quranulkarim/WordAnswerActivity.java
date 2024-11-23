package com.akramhossain.quranulkarim;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class WordAnswerActivity extends AppCompatActivity {

    private static final String TAG = WordAnswerActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;
    SharedPreferences mPrefs;
    TextView quiz_arabic,transliteration_en,transliteration_bn;
    TextView answer_one_en,answer_one_bn,answer_two_en,answer_two_bn,answer_three_en,answer_three_bn,answer_four_en,answer_four_bn,tv_word_id,tv_right_answer;
    Boolean isWrongAnswer = false;
    CardView option1,option2,option3,option4;
    Button skip_button,point_button;
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

        tv_word_id = (TextView) findViewById(R.id.word_id);
        tv_right_answer = (TextView) findViewById(R.id.right_answer);

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

        skip_button = (Button) findViewById(R.id.skip_button);
        skip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromLocalDb();
            }
        });

        option1 = (CardView) findViewById(R.id.option1);
        option2 = (CardView) findViewById(R.id.option2);
        option3 = (CardView) findViewById(R.id.option3);
        option4 = (CardView) findViewById(R.id.option4);

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOption(1);
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOption(2);
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOption(3);
            }
        });

        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOption(4);
            }
        });

        point_button = (Button) findViewById(R.id.point_button);

        countTotalPoint();

        Button reportAnIssue = (Button) findViewById(R.id.reportAnIssue);
        reportAnIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BugReportActivity.class);
                i.putExtra("position", 3);
                startActivity(i);
            }
        });
    }

    private void countTotalPoint(){
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT count(*) as total FROM word_answers WHERE is_right_answer = 1";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                String total = cursor.getString(cursor.getColumnIndexOrThrow("total"));
                Integer pointsTotal = Integer.parseInt(total)*1;
                point_button.setText(String.valueOf(pointsTotal)+" Point(s)");
            }
        }catch (Exception e) {
            Log.i(TAG, e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }
    }

    private void checkOption(int option){
        String right_answer = tv_right_answer.getText().toString().trim();
        if(isWrongAnswer){
            AlertDialog.Builder alert = new AlertDialog.Builder(WordAnswerActivity.this);
            alert.setTitle(R.string.text_ans_taken);
            alert.setMessage(R.string.text_ans_taken_desc);
            alert.setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.d("right_answer",right_answer);
                    String answer_1 = answer_one_en.getText().toString().trim();
                    String answer_2 = answer_two_en.getText().toString().trim();
                    String answer_3 = answer_three_en.getText().toString().trim();
                    String answer_4 = answer_four_en.getText().toString().trim();
                    if(right_answer.equals(answer_1)){
                        answer_one_en.setTextColor(getColor(R.color.transColor));
                        answer_one_bn.setTextColor(getColor(R.color.transColor));
                    }
                    else if(right_answer.equals(answer_2)){
                        answer_two_en.setTextColor(getColor(R.color.transColor));
                        answer_two_bn.setTextColor(getColor(R.color.transColor));
                    }
                    else if(right_answer.equals(answer_3)){
                        answer_three_en.setTextColor(getColor(R.color.transColor));
                        answer_three_bn.setTextColor(getColor(R.color.transColor));
                    }
                    else if(right_answer.equals(answer_4)){
                        answer_four_en.setTextColor(getColor(R.color.transColor));
                        answer_four_bn.setTextColor(getColor(R.color.transColor));
                    }
                }
            });
            alert.setNegativeButton(R.string.text_no, null);
            alert.show();
        }else {
            String word_id = tv_word_id.getText().toString();
            String answer = "";
            String answerBn = "";
            if (option == 1) {
                answer = answer_one_en.getText().toString().trim();
                answerBn = answer_one_bn.getText().toString();
            } else if (option == 2) {
                answer = answer_two_en.getText().toString().trim();
                answerBn = answer_two_bn.getText().toString();
            } else if (option == 3) {
                answer = answer_three_en.getText().toString().trim();
                answerBn = answer_three_bn.getText().toString();
            } else if (option == 4) {
                answer = answer_four_en.getText().toString().trim();
                answerBn = answer_four_bn.getText().toString();
            }
            Log.d("option", String.valueOf(option));
            Log.d("answer", answer);
            if (answer != null && !answer.equals("")) {
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();

                String sql = "SELECT * FROM words WHERE trim(translation) = ? and word_id = ?";
                Cursor cursor = db.rawQuery(sql, new String[]{answer, word_id});
                try {
                    if (cursor.moveToFirst()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(WordAnswerActivity.this);
                        alert.setTitle(R.string.text_right_ans);
                        alert.setMessage(R.string.text_right_ans_desc);
                        alert.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                String dtStr1 = dateFormat.format(date);
                                ContentValues values = new ContentValues();
                                values.put("word_id", word_id);
                                values.put("datetime", dtStr1);
                                values.put("is_right_answer",1);
                                try {
                                    DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase().insertOrThrow("word_answers", "", values);
                                    getDataFromLocalDb();
                                    countTotalPoint();
                                } catch (Exception e) {
                                    Log.i("right_answers", e.getMessage());
                                }
                            }
                        });
                        alert.show();
                    } else {
                        isWrongAnswer = true;
                        AlertDialog.Builder alert = new AlertDialog.Builder(WordAnswerActivity.this);
                        alert.setTitle(R.string.text_wrong_ans);
                        alert.setMessage(R.string.text_wrong_ans_desc);
                        alert.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                String dtStr1 = dateFormat.format(date);
                                ContentValues values = new ContentValues();
                                values.put("word_id", word_id);
                                values.put("datetime", dtStr1);
                                values.put("is_right_answer",0);
                                try {
                                    DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase().insertOrThrow("word_answers", "", values);

                                } catch (Exception e) {
                                    Log.i("wrong_answers", e.getMessage());
                                }
                            }
                        });
                        alert.show();
                    }
                } catch (Exception e) {
                    Log.i(TAG, e.getMessage());
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                    db.close();
                }
            }
        }
    }

    private void getDataFromLocalDb() {
        isWrongAnswer = false;
        answer_one_en.setTextColor(getColor(R.color.colorWhite));
        answer_one_bn.setTextColor(getColor(R.color.colorWhite));

        answer_two_en.setTextColor(getColor(R.color.colorWhite));
        answer_two_bn.setTextColor(getColor(R.color.colorWhite));

        answer_three_en.setTextColor(getColor(R.color.colorWhite));
        answer_three_bn.setTextColor(getColor(R.color.colorWhite));

        answer_four_en.setTextColor(getColor(R.color.colorWhite));
        answer_four_bn.setTextColor(getColor(R.color.colorWhite));
        //
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select w.word_id,w.arabic,w.translation, w.transliteration,b.translate_bn,b.words_ar " +
                "from words w " +
                "inner join bywords b on w.word_id = b._id " +
                "where w.word_id NOT IN(select wa.word_id from word_answers wa where wa.is_right_answer = 1)" +
                "group by w.translation order by RANDOM() " +
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
                Log.d("word_id",word_id);

                quiz_arabic.setText(arabic);
                transliteration_en.setText(transliteration);
                tv_word_id.setText(word_id);
                tv_right_answer.setText(translation);

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
                        "group by w.translation LIMIT 4 " +
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