package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;
import com.akramhossain.quranulkarim.util.Utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import io.sentry.Sentry;

public class TafsirActivity extends AppCompatActivity {

    public static String ayah_index;
    public static String text_tashkeel;
    public static String content_en;
    public static String content_bn;
    public static String ayah_num;
    public static String surah_id;
    public static String ayah_key;
    public static String surah_name;
    public static String ayah_trans;
    public static String text_tajweed;

    DatabaseHelper dbhelper;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;
    SharedPreferences mPrefs;

    TextView bayaan_content,zakaria_content,jalalayn_content, ibn_kathir_content, tafhim_content, fathul_mazid_content, fezilalil_quran_content, trans;

    String bayaan_text, zakaria_text, jalalayn_text, ibn_kasir_text, tafhim_text, fathul_mazid_text, fezilalil_quran_text;

    TextView tv_surah_name,tv_ayah_arabic,tv_ayah_english,tv_ayah_bangla,tv_ayah_num;

    Button btn_bayaan, btn_zakaria, btn_jalalayn, btn_ibnkathir, btn_tafhim, btn_fathul_mazid, btn_fezilalil_quran, copyButton;

    WebView wv_text_tajweed;

    private static final String TAG = TafsirActivity.class.getSimpleName();

    public static final String is_tafsir_ibn_kasir_selected = "isTafsirIbnKasirSelected";
    public static final String is_tafsir_bayaan_selected = "isTafsirBayaanSelected";
    public static final String is_tafsir_zakaria_selected = "isTafsirZakariaSelected";
    public static final String is_tafsir_tafhim_selected = "isTafsirTafhimSelected";
    public static final String is_tafsir_fathul_mazid_selected = "isTafsirFathulMazidSelected";
    public static final String is_tafsir_fezilalil_selected = "isTafsirFezilalilSelected";
    public static final String is_tafsir_jalalayn_selected = "isTafsirJalalaynSelected";

    public String active_tafsir = "";
    String fontFamily = "fontUthmani";
    String fontSize = "30px";
    String bodyBgColor = "#303030";
    String bodyTxtColor = "#ffffff";
    String appTheme = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (extras != null) {
            ayah_index = extras.getString("ayah_index");
            text_tashkeel = extras.getString("text_tashkeel");
            content_en = extras.getString("content_en");
            content_bn = extras.getString("content_bn");
            ayah_num = extras.getString("ayah_num");
            surah_id = extras.getString("surah_id");
            ayah_key = extras.getString("ayah_key");
            ayah_trans = extras.getString("trans");
            text_tajweed = extras.getString("text_tajweed");
        }

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        fontTahaNaskh = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/kitab.ttf");

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_tafsir);

        View rootView = findViewById(R.id.topTafsirBar);
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
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottomInset);
            return insets;
        });


        setTitle("Tafsir");

        bayaan_content = (TextView) findViewById(R.id.bayaan_content);
        bayaan_content.setTypeface(font);

        zakaria_content = (TextView) findViewById(R.id.zakaria_content);
        zakaria_content.setTypeface(font);

        jalalayn_content = (TextView) findViewById(R.id.jalalayn_content);

        ibn_kathir_content = (TextView) findViewById(R.id.ibn_kathir_content);
        ibn_kathir_content.setTypeface(font);

        tafhim_content = (TextView) findViewById(R.id.tafhim_content);
        tafhim_content.setTypeface(font);

        fathul_mazid_content = (TextView) findViewById(R.id.fathul_mazid_content);
        fathul_mazid_content.setTypeface(font);

        fezilalil_quran_content = (TextView) findViewById(R.id.fezilalil_quran_content);
        fezilalil_quran_content.setTypeface(font);

        tv_surah_name = (TextView) findViewById(R.id.surah_name);
        tv_ayah_arabic = (TextView) findViewById(R.id.ayah_arabic);
        tv_ayah_english = (TextView) findViewById(R.id.ayah_english);
        tv_ayah_bangla = (TextView) findViewById(R.id.ayah_bangla);
        tv_ayah_bangla.setTypeface(font);
        tv_ayah_num = (TextView) findViewById(R.id.ayah_num);
        trans = (TextView) findViewById(R.id.trans);
        trans.setTypeface(font);

        wv_text_tajweed = (WebView) findViewById(R.id.text_tajweed);

        getDataFromLocalDb();

        active_tafsir = "ibn_kasir";

        btn_ibnkathir = (Button) findViewById(R.id.ibnkathir);
        btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));

        btn_bayaan = (Button) findViewById(R.id.bayaan);
        btn_zakaria = (Button) findViewById(R.id.zakaria);
        btn_jalalayn = (Button) findViewById(R.id.jalalayn);
        //
        btn_tafhim = (Button) findViewById(R.id.tafhim);
        btn_fathul_mazid = (Button) findViewById(R.id.fathul_mazid);
        btn_fezilalil_quran = (Button) findViewById(R.id.fezilalil_quran);

        copyButton = (Button) findViewById(R.id.copyButton);

        btn_ibnkathir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ibn_kathir_content.setVisibility(View.VISIBLE);
                bayaan_content.setVisibility(View.GONE);
                zakaria_content.setVisibility(View.GONE);
                jalalayn_content.setVisibility(View.GONE);
                //
                tafhim_content.setVisibility(View.GONE);
                fathul_mazid_content.setVisibility(View.GONE);
                fezilalil_quran_content.setVisibility(View.GONE);
                //
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                //
                btn_tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fathul_mazid.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

                active_tafsir = "ibn_kasir";
            }
        });

        btn_bayaan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bayaan_content.setVisibility(View.VISIBLE);
                zakaria_content.setVisibility(View.GONE);
                jalalayn_content.setVisibility(View.GONE);
                ibn_kathir_content.setVisibility(View.GONE);
                //
                tafhim_content.setVisibility(View.GONE);
                fathul_mazid_content.setVisibility(View.GONE);
                fezilalil_quran_content.setVisibility(View.GONE);
                //
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                //
                btn_tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fathul_mazid.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

                active_tafsir = "bayaan";
            }
        });

        btn_zakaria.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bayaan_content.setVisibility(View.GONE);
                zakaria_content.setVisibility(View.VISIBLE);
                jalalayn_content.setVisibility(View.GONE);
                ibn_kathir_content.setVisibility(View.GONE);
                //
                tafhim_content.setVisibility(View.GONE);
                fathul_mazid_content.setVisibility(View.GONE);
                fezilalil_quran_content.setVisibility(View.GONE);
                //
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                //
                btn_tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fathul_mazid.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

                active_tafsir = "zakaria";
            }
        });

        btn_jalalayn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bayaan_content.setVisibility(View.GONE);
                zakaria_content.setVisibility(View.GONE);
                jalalayn_content.setVisibility(View.VISIBLE);
                ibn_kathir_content.setVisibility(View.GONE);
                //
                tafhim_content.setVisibility(View.GONE);
                fathul_mazid_content.setVisibility(View.GONE);
                fezilalil_quran_content.setVisibility(View.GONE);
                //
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                //
                btn_tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fathul_mazid.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

                active_tafsir = "jalalayn";
            }
        });

        btn_tafhim.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bayaan_content.setVisibility(View.GONE);
                zakaria_content.setVisibility(View.GONE);
                jalalayn_content.setVisibility(View.GONE);
                ibn_kathir_content.setVisibility(View.GONE);
                //
                tafhim_content.setVisibility(View.VISIBLE);
                fathul_mazid_content.setVisibility(View.GONE);
                fezilalil_quran_content.setVisibility(View.GONE);
                //
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                //
                btn_tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                btn_fathul_mazid.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

                getTafhimTafsirFromLocalDB();

                active_tafsir = "tafhim";
            }
        });

        btn_fathul_mazid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bayaan_content.setVisibility(View.GONE);
                zakaria_content.setVisibility(View.GONE);
                jalalayn_content.setVisibility(View.GONE);
                ibn_kathir_content.setVisibility(View.GONE);
                //
                tafhim_content.setVisibility(View.GONE);
                fathul_mazid_content.setVisibility(View.VISIBLE);
                fezilalil_quran_content.setVisibility(View.GONE);
                //
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                //
                btn_tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fathul_mazid.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                btn_fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

                getFathulMazidTafsirFromLocalDB();

                active_tafsir = "fathul";
            }
        });

        btn_fezilalil_quran.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bayaan_content.setVisibility(View.GONE);
                zakaria_content.setVisibility(View.GONE);
                jalalayn_content.setVisibility(View.GONE);
                ibn_kathir_content.setVisibility(View.GONE);
                //
                tafhim_content.setVisibility(View.GONE);
                fathul_mazid_content.setVisibility(View.GONE);
                fezilalil_quran_content.setVisibility(View.VISIBLE);
                //
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                //
                btn_tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fathul_mazid.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));

                getFezilalilTafsirFromLocalDB();

                active_tafsir = "fezilalil";
            }
        });



        Button previousBtn = (Button) findViewById(R.id.previousBtn);
        Button nextBtn = (Button) findViewById(R.id.nextBtn);

        String mushaf = mPrefs.getString("mushaf", "IndoPak");
        previousBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql = "SELECT ayah.*,sura.name_arabic,sura.name_complex,sura.name_english,sura.name_simple,transliteration.trans,ayah_indo.text as indo_pak,ut.text_uthmani_tajweed,u.text_uthmani " +
                        "FROM ayah " +
                        "LEFT join sura ON ayah.surah_id = sura.surah_id " +
                        "LEFT join transliteration ON ayah.ayah_num = transliteration.ayat_id and transliteration.sura_id = ayah.surah_id " +
                        "LEFT join ayah_indo ON ayah.ayah_num = ayah_indo.ayah and ayah_indo.sura = ayah.surah_id "+
                        "LEFT JOIN uthmani_tajweed ut ON ayah.ayah_key = ut.verse_key " +
                        "LEFT JOIN uthmani u ON ayah.ayah_key = u.verse_key "+
                        "WHERE ayah.surah_id = "+surah_id+" and ayah.ayah_num < "+ayah_num+" " +
                        "order by ayah.ayah_index DESC " +
                        "limit 1";
                //Log.i(TAG, sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        ayah_index = cursor.getString(0);
                        if(mushaf.equals("ImlaeiSimple")) {
                            text_tashkeel = cursor.getString(10);
                        }
                        else if(mushaf.equals("Uthmanic")) {
                            text_tashkeel = cursor.getString(22);
                        }
                        else{
                            text_tashkeel = cursor.getString(20);
                        }
                        content_en = cursor.getString(11);
                        content_bn = cursor.getString(12);
                        ayah_num = cursor.getString(2);
                        surah_id = cursor.getString(1);
                        ayah_key = cursor.getString(8);
                        ayah_trans = cursor.getString(19);
                        text_tajweed = cursor.getString(21);

                        getDataFromLocalDb();
                        getTafhimTafsirFromLocalDB();
                        getFathulMazidTafsirFromLocalDB();
                        getFezilalilTafsirFromLocalDB();
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
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql = "SELECT ayah.*,sura.name_arabic,sura.name_complex,sura.name_english,sura.name_simple,transliteration.trans,ayah_indo.text as indo_pak,ut.text_uthmani_tajweed,u.text_uthmani " +
                        "FROM ayah " +
                        "LEFT join sura ON ayah.surah_id = sura.surah_id " +
                        "LEFT join transliteration ON ayah.ayah_num = transliteration.ayat_id and transliteration.sura_id = ayah.surah_id " +
                        "LEFT join ayah_indo ON ayah.ayah_num = ayah_indo.ayah and ayah_indo.sura = ayah.surah_id "+
                        "LEFT JOIN uthmani_tajweed ut ON ayah.ayah_key = ut.verse_key " +
                        "LEFT JOIN uthmani u ON ayah.ayah_key = u.verse_key "+
                        "WHERE ayah.surah_id = "+surah_id+" and ayah.ayah_num > "+ayah_num+" " +
                        "order by ayah.ayah_index ASC " +
                        "limit 1";
                //Log.i(TAG, sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        ayah_index = cursor.getString(0);
                        if(mushaf.equals("ImlaeiSimple")) {
                            text_tashkeel = cursor.getString(10);
                        }
                        else if(mushaf.equals("Uthmanic")) {
                            text_tashkeel = cursor.getString(22);
                        }
                        else{
                            text_tashkeel = cursor.getString(20);
                        }
                        content_en = cursor.getString(11);
                        content_bn = cursor.getString(12);
                        ayah_num = cursor.getString(2);
                        surah_id = cursor.getString(1);
                        ayah_key = cursor.getString(8);
                        ayah_trans = cursor.getString(19);
                        text_tajweed = cursor.getString(21);

                        getDataFromLocalDb();
                        getTafhimTafsirFromLocalDB();
                        getFathulMazidTafsirFromLocalDB();
                        getFezilalilTafsirFromLocalDB();
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
            }
        });

        String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
        String mp_arFz = mPrefs.getString("arFontSize", "30");
        String mp_enFz = mPrefs.getString("enFontSize", "15");
        String mp_bnFz = mPrefs.getString("bnFontSize", "15");

        if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
            tv_ayah_arabic.setTypeface(fontAlmajeed);
            fontFamily = "fontAlmajeed";
        }
        if(mp_arabicFontFamily.equals("Al Qalam Quran")){
            tv_ayah_arabic.setTypeface(fontAlQalam);
            fontFamily = "fontAlQalam";
        }
        if(mp_arabicFontFamily.equals("Noore Huda")){
            tv_ayah_arabic.setTypeface(fontUthmani);
            fontFamily = "fontUthmani";
        }
        if(mp_arabicFontFamily.equals("Noore Hidayat")){
            tv_ayah_arabic.setTypeface(fontNooreHidayat);
            fontFamily = "fontNooreHidayat";
        }
        if(mp_arabicFontFamily.equals("Saleem Quran")){
            tv_ayah_arabic.setTypeface(fontSaleem);
            fontFamily = "fontSaleem";
        }
        if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
            tv_ayah_arabic.setTypeface(fontTahaNaskh);
            fontFamily = "fontTahaNaskh";
        }
        if(mp_arabicFontFamily.equals("Arabic Regular")){
            tv_ayah_arabic.setTypeface(fontKitab);
            fontFamily = "fontKitab";
        }
        if(!mp_arFz.equals("") && mp_arFz !=null){
            try {
                tv_ayah_arabic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
                fontSize = mp_arFz + "px";
            }catch (NumberFormatException e) {
                Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
            }
        }
        if(!mp_enFz.equals("") && mp_enFz !=null){
            try {
                tv_ayah_english.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
            }catch (NumberFormatException e) {
                Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
            }
        }
        if(!mp_bnFz.equals("") && mp_bnFz !=null){
            try {
                tv_ayah_bangla.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                trans.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
            }catch (NumberFormatException e) {
                Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
            }
        }

        String mp_enFzTs = mPrefs.getString("enFontSizeTafsir", "15");
        if(!mp_enFzTs.equals("") && mp_enFzTs !=null){
            try {
                jalalayn_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFzTs));
            }catch (NumberFormatException e) {
                Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
            }
        }

        String mp_bnFzTs = mPrefs.getString("bnFontSizeTafsir", "15");
        if(!mp_bnFzTs.equals("") && mp_bnFzTs !=null){
            try {
                bayaan_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));
                zakaria_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));
                ibn_kathir_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));

                tafhim_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));
                fathul_mazid_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));
                fezilalil_quran_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));
            }catch (NumberFormatException e) {
                Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
            }
        }

        String is_ibn_kasir = mPrefs.getString(is_tafsir_ibn_kasir_selected, "2");
        if(is_ibn_kasir.equals("-1")){
            btn_ibnkathir.setVisibility(View.GONE);
            ibn_kathir_content.setVisibility(View.GONE);
        }
        String is_bayaan = mPrefs.getString(is_tafsir_bayaan_selected, "2");
        if(is_bayaan.equals("-1")){
            btn_bayaan.setVisibility(View.GONE);
        }
        String is_zakaria = mPrefs.getString(is_tafsir_zakaria_selected, "2");
        if(is_zakaria.equals("-1")){
            btn_zakaria.setVisibility(View.GONE);
        }
        String is_tafhim = mPrefs.getString(is_tafsir_tafhim_selected, "2");
        if(is_tafhim.equals("-1")){
            btn_tafhim.setVisibility(View.GONE);
        }
        String is_fathul = mPrefs.getString(is_tafsir_fathul_mazid_selected, "2");
        if(is_fathul.equals("-1")){
            btn_fathul_mazid.setVisibility(View.GONE);
        }
        String is_fezilalil = mPrefs.getString(is_tafsir_fezilalil_selected, "2");
        if(is_fezilalil.equals("-1")){
            btn_fezilalil_quran.setVisibility(View.GONE);
        }
        String is_jalalayn = mPrefs.getString(is_tafsir_jalalayn_selected, "2");
        if(is_jalalayn.equals("-1")){
            btn_jalalayn.setVisibility(View.GONE);
        }

        copyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, active_tafsir);

                String label = surah_name+" "+ayah_key;
                String copyTxt =  "Surah "+surah_name+" "+ayah_key +"\n"+ text_tashkeel +"\n"+ ayah_trans +"\n"+ content_en +"\n"+ content_bn+"\n\n";
                String tafsirTxt = "";

                if(active_tafsir.equals("ibn_kasir")){
                    tafsirTxt = ibn_kathir_content.getText().toString();
                }
                else if(active_tafsir.equals("bayaan")){
                    tafsirTxt = bayaan_content.getText().toString();
                }
                else if(active_tafsir.equals("zakaria")){
                    tafsirTxt = zakaria_content.getText().toString();
                }
                else if(active_tafsir.equals("jalalayn")){
                    tafsirTxt = jalalayn_content.getText().toString();
                }
                else if(active_tafsir.equals("tafhim")){
                    tafsirTxt = tafhim_content.getText().toString();
                }
                else if(active_tafsir.equals("fathul")){
                    tafsirTxt = fathul_mazid_content.getText().toString();
                }
                else if(active_tafsir.equals("fezilalil")){
                    tafsirTxt = fezilalil_quran_content.getText().toString();
                }

                Log.d(label,copyTxt+tafsirTxt);
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText(label,copyTxt+tafsirTxt);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Tafsir Copied.", Toast.LENGTH_LONG).show();
            }
        });

        String show_bn_pron = mPrefs.getString("show_bn_pron", "2");
        if (show_bn_pron.equals("-1")) {
            trans.setVisibility(View.GONE);
        }
        String show_en_trans = mPrefs.getString("show_en_trans", "2");
        if (show_en_trans.equals("-1")) {
            tv_ayah_english.setVisibility(View.GONE);
        }
        String show_bn_trans = mPrefs.getString("show_bn_trans", "2");
        if (show_bn_trans.equals("-1")) {
            tv_ayah_bangla.setVisibility(View.GONE);
        }

        appTheme = mPrefs.getString("APP_NIGHT_MODE", "-1");
        if (appTheme.equals("1")) {
            bodyBgColor = "#303030";
            bodyTxtColor = "#ffffff";
        }else if (appTheme.equals("0")) {
            bodyBgColor = "#FAFAFA";
            bodyTxtColor = "#000000";
        }else {
            bodyBgColor = "#303030";
            bodyTxtColor = "#ffffff";
        }
        String style = Utils.tajweedCss(fontFamily,fontSize,bodyBgColor,bodyTxtColor,appTheme);
        String html = "<html><head>"+style+"</head><body>"+text_tajweed+"</body></html>";
        wv_text_tajweed.loadDataWithBaseURL(null,html, "text/html; charset=utf-8", "UTF-8",null);
        if(mushaf.equals("Tajweed")) {
            wv_text_tajweed.setVisibility(View.VISIBLE);
            tv_ayah_arabic.setVisibility(View.GONE);
        }else{
            wv_text_tajweed.setVisibility(View.GONE);
            tv_ayah_arabic.setVisibility(View.VISIBLE);
        }

    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();

        String bayaanSql = "select ayah.surah_id,ayah.ayah_num,\n" +
                "(select c2text from verses_content_tafsir_bayaan where verses_content_tafsir_bayaan.c0sura = ayah.surah_id and verses_content_tafsir_bayaan.c1ayah = ayah.ayah_num) tafsir_bayaan,\n" +
                "(select c2text from verses_content_tafsir_zakaria where verses_content_tafsir_zakaria.c0sura = ayah.surah_id and verses_content_tafsir_zakaria.c1ayah = ayah.ayah_num) tafsir_zakaria,\n" +
                "(select c2text from verses_content_tafsir_jalalayn where verses_content_tafsir_jalalayn.c0sura = ayah.surah_id and verses_content_tafsir_jalalayn.c1ayah = ayah.ayah_num) tafsir_jalalayn,\n" +
                "(select c2text from verses_content_tafsir_ibn_kasir where verses_content_tafsir_ibn_kasir.c0sura = ayah.surah_id and verses_content_tafsir_ibn_kasir.c1ayah = ayah.ayah_num) tafsir_ibn_kasir,\n" +
                "(select name_simple from sura where surah_id = ayah.surah_id) surah_name\n" +
                "from ayah\n" +
                "where ayah.surah_id = "+surah_id+" and ayah.ayah_num = "+ayah_num;
        Cursor cursor = db.rawQuery(bayaanSql,null);

        try {
            if (cursor.moveToFirst()) {
                bayaan_text = cursor.getString(2);
                zakaria_text = cursor.getString(3);
                jalalayn_text = cursor.getString(4);
                ibn_kasir_text = cursor.getString(5);
                surah_name = cursor.getString(6);
                /*Log.d("Tafsir Bayaan", bayaan_text);
                Log.d("Tafsir Zakariya", zakaria_text);
                Log.d("Tafsir Jalalayn", jalalayn_text);
                Log.d("Tafsir IBN Kathir", ibn_kasir_text);*/

                bayaan_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/>"+bayaan_text));
                zakaria_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/>"+zakaria_text));
                jalalayn_content.setText(Html.fromHtml("<b>Tafsir:</b><br/><br/>"+jalalayn_text));
                ibn_kathir_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/>"+ibn_kasir_text));

                tv_surah_name.setText(surah_name+" "+ayah_key);
                tv_ayah_arabic.setText(text_tashkeel);
                tv_ayah_english.setText(content_en);
                tv_ayah_bangla.setText(content_bn);
                tv_ayah_num.setText(surah_name+" "+ayah_key);
                trans.setText(ayah_trans);
                //
                String style = Utils.tajweedCss(fontFamily,fontSize,bodyBgColor,bodyTxtColor,appTheme);
                String html = "<html><head>"+style+"</head><body>"+text_tajweed+"</body></html>";
                wv_text_tajweed.loadDataWithBaseURL(null,html, "text/html; charset=utf-8", "UTF-8",null);
                String mushaf = mPrefs.getString("mushaf", "IndoPak");
                if(mushaf.equals("Tajweed")) {
                    wv_text_tajweed.setVisibility(View.VISIBLE);
                    tv_ayah_arabic.setVisibility(View.GONE);
                }else{
                    wv_text_tajweed.setVisibility(View.GONE);
                    tv_ayah_arabic.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception e){
            Log.e("Tafsir", e.getMessage());
            //throw new RuntimeException("SQL Query: " + bayaanSql, e);
            Sentry.captureException(new RuntimeException("SQL Query: " + bayaanSql, e));
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    private void getTafhimTafsirFromLocalDB(){
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select case when gloss_expl = 'NULL' THEN trans_text when gloss_expl IS NULL THEN trans_text else group_concat(gloss_expl,'') end tafsir_text\n" +
                "from tafsir_tafhimul_quran \n" +
                "where verse_id = "+ayah_index+"\n" +
                "group by verse_id";

        //Log.i("SQL", sql);
        Cursor cursor = db.rawQuery(sql,null);
        try {
            if (cursor.moveToFirst()) {
                tafhim_text = cursor.getString(0);
                tafhim_text = tafhim_text.replaceAll("NULL", "");
                tafhim_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/>"+tafhim_text));
            }
        }
        catch (Exception e){
            Log.e("Tafsir", e.getMessage());
            //throw new RuntimeException("SQL Query: " + sql, e);
            Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    private void getFathulMazidTafsirFromLocalDB(){
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select expl_text tafsir_text\n" +
                "from tafsir_fathul_mazid \n" +
                "where surah_id = "+surah_id+" and ayah_id = "+ayah_num;

        //Log.i("SQL", sql);
        Cursor cursor = db.rawQuery(sql,null);
        try {
            if (cursor.moveToFirst()) {
                fathul_mazid_text = cursor.getString(0);
                fathul_mazid_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/>"+fathul_mazid_text));
            }else{
                fathul_mazid_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/> দুঃখিত! এই আয়াতের তাফসীর পাওয়া যায় নি।"));
            }
        }
        catch (Exception e){
            Log.e("Tafsir", e.getMessage());
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
        String banglaSuraId = getDigitBanglaFromEnglish(surah_id);
        String banglaVerseId = getDigitBanglaFromEnglish(ayah_num);
        String sql = "select tafsir_text\n" +
                "from tafsir_fezilalil_quran\n" +
                "where sura_id = '"+banglaSuraId+"' and verse_id = '"+banglaVerseId+"'";

        //Log.i("SQL", sql);
        Cursor cursor = db.rawQuery(sql,null);
        try {
            if (cursor.moveToFirst()) {
                fezilalil_quran_text = cursor.getString(0);
                fezilalil_quran_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/>"+fezilalil_quran_text));
            }else{
                fathul_mazid_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/> দুঃখিত! এই আয়াতের তাফসীর পাওয়া যায় নি।"));
            }
        }
        catch (Exception e){
            Log.e("Tafsir", e.getMessage());
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