package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;

public class InfoActivity extends AppCompatActivity {

    public static String suraId;
    public static String suraName;
    public static String suraNameArabic;
    String infoText;
    Typeface font;
    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            suraId = extras.getString("sura_id");
            suraName = extras.getString("sura_name");
            suraNameArabic = extras.getString("sura_name_arabic");
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        setContentView(R.layout.activity_info);

        TextView info_title = (TextView) findViewById(R.id.info_title);
        info_title.setText(suraNameArabic);

        TextView info_sub_title = (TextView) findViewById(R.id.info_sub_title);
        info_sub_title.setText(suraName);

        getFezilalilTafsirFromLocalDB();
    }

    private void getFezilalilTafsirFromLocalDB(){
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String banglaSuraId = getDigitBanglaFromEnglish(suraId);
        String banglaVerseId = getDigitBanglaFromEnglish("0");
        String sql = "select tafsir_text\n" +
                "from tafsir_fezilalil_quran\n" +
                "where sura_id = '"+banglaSuraId+"' and verse_id = '"+banglaVerseId+"'";

        Log.i("SQL", sql);
        Cursor cursor = db.rawQuery(sql,null);
        try {
            if (cursor.moveToFirst()) {
                infoText = cursor.getString(0);
                TextView info_content = (TextView) findViewById(R.id.info_content);
                info_content.setText(Html.fromHtml(infoText,Html.FROM_HTML_MODE_LEGACY));
                info_content.setTypeface(font);

                String mp_bnFz = mPrefs.getString("bnFontSize", "15");
                if(!mp_bnFz.equals("")){
                    info_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                }
            }
        }
        catch (Exception e){
            Log.i("Info", e.getMessage());
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    public static final String getDigitBanglaFromEnglish(String number) {
        char[] banglaDigits = { '০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯' };
        if (number == null)
            return new String("");
        StringBuilder builder = new StringBuilder();
        try {
            for (int i = 0; i < number.length(); i++) {
                if (Character.isDigit(number.charAt(i))) {
                    if (((int) (number.charAt(i)) - 48) <= 9) {
                        builder.append(banglaDigits[(int) (number.charAt(i)) - 48]);
                    } else {
                        builder.append(number.charAt(i));
                    }
                } else {
                    builder.append(number.charAt(i));
                }
            }
        } catch (Exception e) {
            return new String("");
        }
        return builder.toString();
    }

}