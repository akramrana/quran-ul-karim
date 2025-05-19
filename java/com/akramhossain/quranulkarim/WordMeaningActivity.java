package com.akramhossain.quranulkarim;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;

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

import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.WordListViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Word;
import com.akramhossain.quranulkarim.util.Utils;

import java.util.ArrayList;

public class WordMeaningActivity extends AppCompatActivity {

    public static String ayah_index;
    public static String text_tashkeel;
    public static String content_en;
    public static String content_bn;
    public static String text_tajweed;

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = WordMeaningActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    private ArrayList<Word> words;
    private WordListViewAdapter rvAdapter;
    private static final int PERMISSION_REQUEST_CODE = 100;

    Typeface fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;
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
            text_tajweed = extras.getString("text_tajweed");
        }

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);
        fontUthmani = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        fontTahaNaskh = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/kitab.ttf");

        setTitle("Word Meaning");

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_word_meaning);

        View rootView = findViewById(R.id.wordTopBar);
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

        TextView titleAr = (TextView) findViewById(R.id.name_title_ar);
        titleAr.setText(text_tashkeel);

        WebView wb_text_tajweed = (WebView) findViewById(R.id.text_tajweed);

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
        String mp_arFz = mPrefs.getString("arFontSize", "30");
        String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
        String fontFamily = "fontUthmani";
        String fontSize = "30px";
        if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
            titleAr.setTypeface(fontAlmajeed);
            fontFamily = "fontAlmajeed";
        }
        if(mp_arabicFontFamily.equals("Al Qalam Quran")){
            titleAr.setTypeface(fontAlQalam);
            fontFamily = "fontAlQalam";
        }
        if(mp_arabicFontFamily.equals("Noore Huda")){
            titleAr.setTypeface(fontUthmani);
            fontFamily = "fontUthmani";
        }
        if(mp_arabicFontFamily.equals("Noore Hidayat")){
            titleAr.setTypeface(fontNooreHidayat);
            fontFamily = "fontNooreHidayat";
        }
        if(mp_arabicFontFamily.equals("Saleem Quran")){
            titleAr.setTypeface(fontSaleem);
            fontFamily = "fontSaleem";
        }
        if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
            titleAr.setTypeface(fontTahaNaskh);
            fontFamily = "fontTahaNaskh";
        }
        if(mp_arabicFontFamily.equals("Arabic Regular")){
            titleAr.setTypeface(fontKitab);
            fontFamily = "fontKitab";
        }
        titleAr.setMovementMethod(new ScrollingMovementMethod());
        if(!mp_arFz.equals("")){
            fontSize = mp_arFz+"px";
        }
        String appTheme = mPrefs.getString("APP_NIGHT_MODE", "-1");
        String bodyBgColor = "#424242";
        String bodyTxtColor = "#ffffff";
        if (appTheme.equals("1")) {
            bodyBgColor = "#424242";
            bodyTxtColor = "#ffffff";
        }else if (appTheme.equals("0")) {
            bodyBgColor = "#FFFFFF";
            bodyTxtColor = "#000000";
        }else {
            bodyBgColor = "#424242";
            bodyTxtColor = "#ffffff";
        }
        String style = Utils.tajweedCss(fontFamily,fontSize,bodyBgColor,bodyTxtColor,appTheme);
        String html = "<html><head>"+style+"</head><body>"+text_tajweed+"</body></html>";
        wb_text_tajweed.loadDataWithBaseURL(null,html, "text/html; charset=utf-8", "UTF-8",null);

        String mushaf = mPrefs.getString("mushaf", "IndoPak");
        if(mushaf.equals("Tajweed")) {
            wb_text_tajweed.setVisibility(View.VISIBLE);
            titleAr.setVisibility(View.GONE);
        }else{
            wb_text_tajweed.setVisibility(View.GONE);
            titleAr.setVisibility(View.VISIBLE);
        }
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT words.*,(select translate_bn from bywords b where b._id =  words.word_id) as bangla,(select words_id from bywords b where b._id =  words.word_id) as words_id " +
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
        rvAdapter = new WordListViewAdapter(WordMeaningActivity.this, words, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
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
            ActivityCompat.requestPermissions(WordMeaningActivity.this, permissions(), PERMISSION_REQUEST_CODE);
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
