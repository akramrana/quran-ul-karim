package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;

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
    Typeface font;

    TextView bayaan_content,zakaria_content,jalalayn_content, ibn_kathir_content;

    String bayaan_text, zakaria_text, jalalayn_text, ibn_kasir_text;

    TextView tv_surah_name,tv_ayah_arabic,tv_ayah_english,tv_ayah_bangla,tv_ayah_num;

    Button btn_bayaan, btn_zakaria, btn_jalalayn, btn_ibnkathir;

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
        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");
        dbhelper = new DatabaseHelper(getApplicationContext());

        setContentView(R.layout.activity_tafsir);
        setTitle("Tafsir");

        getDataFromLocalDb();

        bayaan_content = (TextView) findViewById(R.id.bayaan_content);
        bayaan_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/>"+bayaan_text));

        zakaria_content = (TextView) findViewById(R.id.zakaria_content);
        zakaria_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/>"+zakaria_text));

        jalalayn_content = (TextView) findViewById(R.id.jalalayn_content);
        jalalayn_content.setText(Html.fromHtml("<b>Tafsir:</b><br/><br/>"+jalalayn_text));

        ibn_kathir_content = (TextView) findViewById(R.id.ibn_kathir_content);
        ibn_kathir_content.setText(Html.fromHtml("<b>তাফসির:</b><br/><br/>"+ibn_kasir_text));

        tv_surah_name = (TextView) findViewById(R.id.surah_name);
        tv_surah_name.setText(surah_name+" "+ayah_key);

        tv_ayah_arabic = (TextView) findViewById(R.id.ayah_arabic);
        tv_ayah_arabic.setText(text_tashkeel);

        tv_ayah_english = (TextView) findViewById(R.id.ayah_english);
        tv_ayah_english.setText(content_en);

        tv_ayah_bangla = (TextView) findViewById(R.id.ayah_bangla);
        tv_ayah_bangla.setTypeface(font);
        tv_ayah_bangla.setText(content_bn);

        tv_ayah_num = (TextView) findViewById(R.id.ayah_num);
        tv_ayah_num.setText(surah_name+" "+ayah_key);

        btn_ibnkathir = (Button) findViewById(R.id.ibnkathir);
        btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));

        btn_bayaan = (Button) findViewById(R.id.bayaan);
        btn_zakaria = (Button) findViewById(R.id.zakaria);
        btn_jalalayn = (Button) findViewById(R.id.jalalayn);

        btn_ibnkathir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ibn_kathir_content.setVisibility(View.VISIBLE);
                bayaan_content.setVisibility(View.GONE);
                zakaria_content.setVisibility(View.GONE);
                jalalayn_content.setVisibility(View.GONE);
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
            }
        });

        btn_bayaan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bayaan_content.setVisibility(View.VISIBLE);
                zakaria_content.setVisibility(View.GONE);
                jalalayn_content.setVisibility(View.GONE);
                ibn_kathir_content.setVisibility(View.GONE);
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
            }
        });

        btn_zakaria.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bayaan_content.setVisibility(View.GONE);
                zakaria_content.setVisibility(View.VISIBLE);
                jalalayn_content.setVisibility(View.GONE);
                ibn_kathir_content.setVisibility(View.GONE);
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
            }
        });

        btn_jalalayn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bayaan_content.setVisibility(View.GONE);
                zakaria_content.setVisibility(View.GONE);
                jalalayn_content.setVisibility(View.VISIBLE);
                ibn_kathir_content.setVisibility(View.GONE);
                btn_jalalayn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                btn_zakaria.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_bayaan.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                btn_ibnkathir.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
            }
        });

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
                Log.d("Tafsir Bayaan", bayaan_text);
                Log.d("Tafsir Zakariya", zakaria_text);
                Log.d("Tafsir Jalalayn", jalalayn_text);
                Log.d("Tafsir IBN Kathir", ibn_kasir_text);
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
}