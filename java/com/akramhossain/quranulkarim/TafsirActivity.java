package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;

import androidx.core.content.ContextCompat;

public class TafsirActivity extends Activity {

    public static String ayah_index;
    public static String text_tashkeel;
    public static String content_en;
    public static String content_bn;
    public static String ayah_num;
    public static String surah_id;
    public static String ayah_key;
    public static String surah_name;
    DatabaseHelper dbhelper;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem;
    SharedPreferences mPrefs;

    TextView bayaan_content,zakaria_content,jalalayn_content, ibn_kathir_content, tafhim_content, fathul_mazid_content, fezilalil_quran_content;

    String bayaan_text, zakaria_text, jalalayn_text, ibn_kasir_text, tafhim_text, fathul_mazid_text, fezilalil_quran_text;

    TextView tv_surah_name,tv_ayah_arabic,tv_ayah_english,tv_ayah_bangla,tv_ayah_num;

    Button btn_bayaan, btn_zakaria, btn_jalalayn, btn_ibnkathir, btn_tafhim, btn_fathul_mazid, btn_fezilalil_quran;

    private static final String TAG = TafsirActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            ayah_index = extras.getString("ayah_index");
            text_tashkeel = extras.getString("text_tashkeel");
            content_en = extras.getString("content_en");
            content_bn = extras.getString("content_bn");
            ayah_num = extras.getString("ayah_num");
            surah_id = extras.getString("surah_id");
            ayah_key = extras.getString("ayah_key");
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");

        dbhelper = new DatabaseHelper(getApplicationContext());

        setContentView(R.layout.activity_tafsir);
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

        getDataFromLocalDb();

        btn_ibnkathir = (Button) findViewById(R.id.ibnkathir);
        btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));

        btn_bayaan = (Button) findViewById(R.id.bayaan);
        btn_zakaria = (Button) findViewById(R.id.zakaria);
        btn_jalalayn = (Button) findViewById(R.id.jalalayn);
        //
        btn_tafhim = (Button) findViewById(R.id.tafhim);
        btn_fathul_mazid = (Button) findViewById(R.id.fathul_mazid);
        btn_fezilalil_quran = (Button) findViewById(R.id.fezilalil_quran);

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
            }
        });



        Button previousBtn = (Button) findViewById(R.id.previousBtn);
        Button nextBtn = (Button) findViewById(R.id.nextBtn);

        previousBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                String sql = "SELECT ayah.*,sura.name_arabic,sura.name_complex,sura.name_english,sura.name_simple " +
                        "FROM ayah " +
                        "LEFT join sura ON ayah.surah_id = sura.surah_id " +
                        "WHERE ayah.surah_id = "+surah_id+" and ayah.ayah_num < "+ayah_num+" " +
                        "order by ayah.ayah_index DESC " +
                        "limit 1";
                //Log.i(TAG, sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        ayah_index = cursor.getString(0);
                        text_tashkeel = cursor.getString(10);
                        content_en = cursor.getString(11);
                        content_bn = cursor.getString(12);
                        ayah_num = cursor.getString(2);
                        surah_id = cursor.getString(1);
                        ayah_key = cursor.getString(8);

                        getDataFromLocalDb();
                        getTafhimTafsirFromLocalDB();
                        getFathulMazidTafsirFromLocalDB();
                        getFezilalilTafsirFromLocalDB();
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
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                String sql = "SELECT ayah.*,sura.name_arabic,sura.name_complex,sura.name_english,sura.name_simple " +
                        "FROM ayah " +
                        "LEFT join sura ON ayah.surah_id = sura.surah_id " +
                        "WHERE ayah.surah_id = "+surah_id+" and ayah.ayah_num > "+ayah_num+" " +
                        "order by ayah.ayah_index ASC " +
                        "limit 1";
                //Log.i(TAG, sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        ayah_index = cursor.getString(0);
                        text_tashkeel = cursor.getString(10);
                        content_en = cursor.getString(11);
                        content_bn = cursor.getString(12);
                        ayah_num = cursor.getString(2);
                        surah_id = cursor.getString(1);
                        ayah_key = cursor.getString(8);

                        getDataFromLocalDb();
                        getTafhimTafsirFromLocalDB();
                        getFathulMazidTafsirFromLocalDB();
                        getFezilalilTafsirFromLocalDB();
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
            }
        });

        String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Arabic Regular");
        String mp_arFz = mPrefs.getString("arFontSize", "30");
        String mp_enFz = mPrefs.getString("enFontSize", "15");
        String mp_bnFz = mPrefs.getString("bnFontSize", "15");

        if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
            tv_ayah_arabic.setTypeface(fontAlmajeed);
        }
        if(mp_arabicFontFamily.equals("Al Qalam Quran")){
            tv_ayah_arabic.setTypeface(fontAlQalam);
        }
        if(mp_arabicFontFamily.equals("Uthmanic Script")){
            tv_ayah_arabic.setTypeface(fontUthmani);
        }
        if(mp_arabicFontFamily.equals("Noore Hidayat")){
            tv_ayah_arabic.setTypeface(fontNooreHidayat);
        }
        if(mp_arabicFontFamily.equals("Saleem Quran")){
            tv_ayah_arabic.setTypeface(fontSaleem);
        }
        if(!mp_arFz.equals("")){
            tv_ayah_arabic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
        }
        if(!mp_enFz.equals("")){
            tv_ayah_english.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
        }
        if(!mp_bnFz.equals("")){
            tv_ayah_bangla.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
        }

        String mp_enFzTs = mPrefs.getString("enFontSizeTafsir", "15");
        if(!mp_enFzTs.equals("")){
            jalalayn_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFzTs));
        }

        String mp_bnFzTs = mPrefs.getString("bnFontSizeTafsir", "15");
        if(!mp_bnFzTs.equals("")){
            bayaan_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));
            zakaria_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));
            ibn_kathir_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));

            tafhim_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));
            fathul_mazid_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));
            fezilalil_quran_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFzTs));
        }

    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();

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
            }
        }catch (Exception e){
            Log.i("Tafsir", e.getMessage());
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    private void getTafhimTafsirFromLocalDB(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
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
            Log.i("Tafsir", e.getMessage());
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    private void getFathulMazidTafsirFromLocalDB(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
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
            Log.i("Tafsir", e.getMessage());
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    private void getFezilalilTafsirFromLocalDB(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
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
            Log.i("Tafsir", e.getMessage());
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