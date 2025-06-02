package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import io.sentry.Sentry;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.util.Utils;

public class InfoActivity extends AppCompatActivity {

    public static String suraId;
    public static String suraName;
    public static String suraNameArabic;
    String infoText, infoTafhimText;
    Typeface font;
    SharedPreferences mPrefs;
    TextView info_content_tafhim, info_content;
    Button tafhim,fezilalil_quran;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            suraId = extras.getString("sura_id");
            suraName = extras.getString("sura_name");
            suraNameArabic = extras.getString("sura_name_arabic");
        }

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_info);

        View rootView = findViewById(R.id.topAboutBar);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to avoid overlap with status/navigation bars
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    view.getPaddingBottom()
            );
            return insets;
        });

        View bottomBar = findViewById(R.id.scrollView);
        ViewCompat.setOnApplyWindowInsetsListener(bottomBar, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    bottomInset
            );
            return insets;
        });

        TextView info_title = (TextView) findViewById(R.id.info_title);
        info_title.setText(suraNameArabic);

        TextView info_sub_title = (TextView) findViewById(R.id.info_sub_title);
        info_sub_title.setText(suraName);

        info_content = (TextView) findViewById(R.id.info_content);
        info_content_tafhim = (TextView) findViewById(R.id.info_content_tafhim);

        //TextView info_content_ttl = (TextView) findViewById(R.id.info_content_ttl);
        //info_content_ttl.setTypeface(font);
        tafhim = (Button) findViewById(R.id.tafhim);
        tafhim.setTypeface(font);
        //
        fezilalil_quran = (Button) findViewById(R.id.fezilalil_quran);
        fezilalil_quran.setTypeface(font);
        fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
        tafhim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_content_tafhim.setVisibility(View.VISIBLE);
                info_content.setVisibility(View.GONE);
                tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
            }
        });
        fezilalil_quran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_content_tafhim.setVisibility(View.GONE);
                info_content.setVisibility(View.VISIBLE);
                tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
            }
        });

        getFezilalilTafsirFromLocalDB();
        getIntroduction();
    }

    private void getIntroduction(){
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select * from introduction where sura_id = "+suraId;
        Log.i("SQL", sql);
        Cursor cursor = db.rawQuery(sql,null);
        try {
            if (cursor.moveToFirst()) {
                infoTafhimText = cursor.getString(1);
                info_content_tafhim.setText(Html.fromHtml(infoTafhimText,Html.FROM_HTML_MODE_LEGACY));
                info_content_tafhim.setTypeface(font);

                String mp_bnFz = mPrefs.getString("bnFontSize", "15");
                if(!mp_bnFz.equals("") && mp_bnFz != null){
                    try {
                        info_content_tafhim.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                    }catch (NumberFormatException e) {
                        Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                    }
                }
            }
        }
        catch (Exception e){
            Log.e("Info", e.getMessage());
            //throw new RuntimeException("SQL Query: " + sql, e);
            Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
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
                info_content.setText(Html.fromHtml(infoText,Html.FROM_HTML_MODE_LEGACY));
                info_content.setTypeface(font);
                String mp_bnFz = mPrefs.getString("bnFontSize", "15");
                if(!mp_bnFz.equals("") && mp_bnFz !=null ){
                    try {
                        info_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                    }catch (NumberFormatException e) {
                        Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                    }
                }
            }
        }
        catch (Exception e){
            Log.e("Info", e.getMessage());
            //throw new RuntimeException("SQL Query: " + sql, e);
            Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
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