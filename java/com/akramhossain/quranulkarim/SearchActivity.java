package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.SuraDetailsViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;
import com.akramhossain.quranulkarim.model.SpinnerObject;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    DatabaseHelper dbhelper;
    AutoCompleteTextView text;
    EditText ayat_number;
    private RecyclerView recyclerview;
    private SuraDetailsViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Ayah> ayahs;
    private static final String TAG = SearchActivity.class.getSimpleName();
    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 10;
    Integer counter = 0;
    public static String suraId;
    public static String ayahNumber="";
    Spinner spinner;
    Button quickLinkBtn;
    TextView search_by_term, collapse, expand;
    private static final int PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Search");

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());
        ayat_number=(EditText) findViewById(R.id.ayat_number);
        ArrayList suras = this.getAllSura();



        text=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        ArrayAdapter<SpinnerObject> dataAdapter = new ArrayAdapter<SpinnerObject>(this,android.R.layout.simple_spinner_item, suras);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        text.setAdapter(dataAdapter);
        text.setThreshold(1);

//        spinner = (Spinner) findViewById( R.id.surah_spinner);
//        ArrayAdapter<SpinnerObject> spinnerAdapter = new ArrayAdapter<SpinnerObject>(this,android.R.layout.simple_spinner_item, suras);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(spinnerAdapter);

        recyclerview = (RecyclerView) findViewById(R.id.ayah_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);


        Button btnSubmit = (Button) findViewById(R.id.search_sura_btn);

        quickLinkBtn = (Button) findViewById(R.id.quickLinkBtn);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String suraName = text.getText().toString();
                //SpinnerObject sb = (SpinnerObject) spinner.getSelectedItem();
                //String suraName = spinner.getSelectedItem().toString();
                //Integer selectedSuraId = sb.getId();
                //
                Integer selectedSuraId = 0;
                if (suraName != null && !suraName.isEmpty()) {
                    String[] parts = suraName.split("\\(");
                    SQLiteDatabase db1 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    String term = parts[0];
                    term = term.replaceAll("'","''");
                    String sql1 = "select * from sura where name_simple = '"+term+"'";
                    //Log.i(TAG, sql1);
                    Cursor cursor1 = db1.rawQuery(sql1, null);
                    try {
                        if (cursor1.moveToFirst()) {
                            selectedSuraId = cursor1.getInt(cursor1.getColumnIndexOrThrow("surah_id"));
                            String checksql = "SELECT * FROM quick_link WHERE sura_id = "+selectedSuraId;
                            Cursor cursor2 = db1.rawQuery(checksql,null);
                            if (cursor2.moveToFirst()) {
                                quickLinkBtn.setText("Remove from favourites");
                                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
                            } else {
                                quickLinkBtn.setText("Add to favourites");
                                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_upload, 0, 0, 0);
                            }
                            quickLinkBtn.setVisibility(View.VISIBLE);
                        }else{
                            quickLinkBtn.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    } finally {
                        if (cursor1 != null && !cursor1.isClosed()) {
                            cursor1.close();
                        }
                        db1.close();
                    }
                }
                Log.i("Q", suraName);
                Log.i("ID", Integer.toString(selectedSuraId));
                //
                String verseNum = ayat_number.getText().toString();
                String regex = "[0-9, /,]+";
                if(verseNum.matches(regex)) {
                    ayahNumber = verseNum;
                    Log.i("AYAT NUMBER", ayahNumber);
                }else{
                    ayahNumber = "";
                }
                if (selectedSuraId != null && !selectedSuraId.equals("")) {
                    SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    String sql = "select * from sura where surah_id = "+selectedSuraId;
                    Cursor cursor = db.rawQuery(sql, null);
                    Log.i(TAG, sql);
                    try {
                        if (cursor.moveToFirst()) {
                            suraId = cursor.getString(cursor.getColumnIndexOrThrow("surah_id")).toString();
                            setRecyclerViewAdapter();
                            getDataFromLocalDb();
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
        });

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if(ayahNumber.equals("")) {
                        if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                            if (itShouldLoadMore) {
                                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                                String sql = "SELECT COUNT(*) FROM ayah WHERE surah_id = " + suraId;
                                Cursor countHistory = db.rawQuery(sql, null);
                                try {
                                    countHistory.moveToFirst();
                                    int maxHistoryCount = countHistory.getInt(0);
                                    countHistory.close();
                                    int maxPageCount = (int) Math.ceil(maxHistoryCount / limit);
                                    if (counter < maxPageCount) {
                                        counter = (counter + 1);
                                        offset = offset + limit;
                                        getDataFromLocalDb();
                                    }
                                } catch (Exception e) {
                                    Log.i("On Scroll Count Check", e.getMessage());
                                } finally {
                                    if (countHistory != null && !countHistory.isClosed()) {
                                        countHistory.close();
                                    }
                                    db.close();
                                }
                            }
                        }
                    }
                }
            }

        });

        quickLinkBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql = "SELECT * FROM quick_link WHERE sura_id = "+suraId;
                Log.i(TAG, sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        db.execSQL("DELETE FROM quick_link WHERE sura_id = " + suraId);
                        Toast.makeText(getApplicationContext(), "Deleted from favourites.", Toast.LENGTH_LONG).show();
                        quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_upload, 0, 0, 0);
                        quickLinkBtn.setText("Add to favourites");
                    }
                    else {
                        ContentValues values = new ContentValues();
                        values.put("sura_id", suraId);
                        DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase().insertOrThrow("quick_link", "", values);
                        Toast.makeText(getApplicationContext(), "Added to favourites.", Toast.LENGTH_LONG).show();
                        quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
                        quickLinkBtn.setText("Remove from favourites");
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

        search_by_term = (TextView) findViewById(R.id.search_by_term);
        search_by_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SearchAyatActivity.class);
                startActivity(i);
            }
        });

        if (checkPermission()) {

        }else{
            requestPermission();
        }

        collapse = (TextView) findViewById(R.id.collapse);
        expand = (TextView) findViewById(R.id.expand);

        RelativeLayout searchInput = (RelativeLayout) findViewById(R.id.searchInput);
        collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setVisibility(View.GONE);
                expand.setVisibility(View.VISIBLE);
                collapse.setVisibility(View.GONE);
            }
        });

        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setVisibility(View.VISIBLE);
                collapse.setVisibility(View.VISIBLE);
                expand.setVisibility(View.GONE);
            }
        });

    }

    private void setRecyclerViewAdapter() {
        ayahs = new ArrayList<Ayah>();
        rvAdapter = new SuraDetailsViewAdapter(SearchActivity.this, ayahs, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String conSql = " ";
        if (ayahNumber != null && !ayahNumber.equals("")) {
            conSql = " AND ayah_num IN ("+ayahNumber+") ";
        }
        String sql = "SELECT ayah.*,sura.name_arabic,sura.name_complex,sura.name_english,sura.name_simple,transliteration.trans,ayah_indo.text as indo_pak " +
                "FROM ayah " +
                "LEFT join sura ON ayah.surah_id = sura.surah_id LEFT join transliteration ON ayah.ayah_num = transliteration.ayat_id and transliteration.sura_id = ayah.surah_id " +
                "LEFT join ayah_indo ON ayah.ayah_num = ayah_indo.ayah and ayah_indo.sura = ayah.surah_id "+
                "WHERE ayah.surah_id = "+suraId+conSql+
                "order by ayah_index ASC limit " + offset + "," + limit;
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Ayah ayah = new Ayah();
                    ayah.setAyah_index(cursor.getString(cursor.getColumnIndexOrThrow("ayah_index")));
                    ayah.setSurah_id(cursor.getString(cursor.getColumnIndexOrThrow("surah_id")));
                    ayah.setAyah_num(cursor.getString(cursor.getColumnIndexOrThrow("ayah_num")));
                    ayah.setPage_num(cursor.getString(cursor.getColumnIndexOrThrow("page_num")));
                    ayah.setJuz_num(cursor.getString(cursor.getColumnIndexOrThrow("juz_num")));
                    ayah.setHizb_num(cursor.getString(cursor.getColumnIndexOrThrow("hizb_num")));
                    ayah.setRub_num(cursor.getString(cursor.getColumnIndexOrThrow("rub_num")));
                    ayah.setText(cursor.getString(cursor.getColumnIndexOrThrow("text")));
                    ayah.setAyah_key(cursor.getString(cursor.getColumnIndexOrThrow("ayah_key")));
                    ayah.setSajdah(cursor.getString(cursor.getColumnIndexOrThrow("sajdah")));
                    ayah.setText_tashkeel(cursor.getString(cursor.getColumnIndexOrThrow("text_tashkeel")));
                    ayah.setContent_en(cursor.getString(cursor.getColumnIndexOrThrow("content_en")));
                    ayah.setContent_bn(cursor.getString(cursor.getColumnIndexOrThrow("content_bn")));
                    ayah.setAudio_duration(cursor.getString(cursor.getColumnIndexOrThrow("audio_duration")));
                    ayah.setAudio_url(cursor.getString(cursor.getColumnIndexOrThrow("audio_url")));
                    //
                    ayah.setName_simple(cursor.getString(cursor.getColumnIndexOrThrow("name_simple")));
                    ayah.setName_complex(cursor.getString(cursor.getColumnIndexOrThrow("name_complex")));
                    ayah.setName_english(cursor.getString(cursor.getColumnIndexOrThrow("name_english")));
                    ayah.setName_arabic(cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")));
                    ayah.setTrans(cursor.getString(cursor.getColumnIndexOrThrow("trans")));
                    ayah.setIndo_pak(cursor.getString(cursor.getColumnIndexOrThrow("indo_pak")));
                    ayahs.add(ayah);
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

    public ArrayList<SpinnerObject> getAllSura(){
        ArrayList < SpinnerObject > suras = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getReadableDatabase();
        String sql = "SELECT * FROM sura order by surah_id ASC";
        Log.i("Search SQL", sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String suraName = cursor.getString(cursor.getColumnIndexOrThrow("name_simple"))+"("+cursor.getString(cursor.getColumnIndexOrThrow("name_arabic"))+")";
                    suras.add(new SpinnerObject(cursor.getInt(cursor.getColumnIndexOrThrow("surah_id")), suraName));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.i("Search", e.getMessage());
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            cursor.close();
        }
        return suras;
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SearchActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SearchActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(SearchActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SearchActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
