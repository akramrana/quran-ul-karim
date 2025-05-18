package com.akramhossain.quranulkarim;

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

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.SearchTermViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;
import com.akramhossain.quranulkarim.util.ConnectionDetector;

import java.util.ArrayList;

public class SearchAyatActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private SearchTermViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Ayah> ayahs;
    private static final String TAG = SearchAyatActivity.class.getSimpleName();
    DatabaseHelper dbhelper;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 100;
    Integer counter = 0;
    TextView titleEn,titleAr;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    EditText ayat_term;
    String search_term;

    public String url;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_search_ayat);

        View rootView = findViewById(R.id.topSearchBar);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to avoid overlap with status/navigation bars
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    0
            );
            return insets;
        });

        ayat_term=(EditText) findViewById(R.id.ayat_term);

        recyclerview = (RecyclerView) findViewById(R.id.bookmark_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        Button btnSubmit = (Button) findViewById(R.id.search_sura_btn);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_term = ayat_term.getText().toString();
                if (search_term != null && !search_term.isEmpty()) {
                    ayahs.clear();
                    getDataFromLocalDb();
                }
            }
        });

        if (checkPermission()) {

        }else{
            requestPermission();
        }

    }

    private void setRecyclerViewAdapter() {
        ayahs = new ArrayList<Ayah>();
        rvAdapter = new SearchTermViewAdapter(SearchAyatActivity.this, ayahs, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT ayah.*,sura.name_arabic,sura.name_complex,sura.name_english,sura.name_simple,transliteration.trans,ayah_indo.text as indo_pak,ut.text_uthmani_tajweed,u.text_uthmani " +
                "FROM ayah " +
                "LEFT join sura ON ayah.surah_id = sura.surah_id " +
                "LEFT join transliteration ON ayah.ayah_num = transliteration.ayat_id and transliteration.sura_id = ayah.surah_id " +
                "LEFT join ayah_indo ON ayah.ayah_num = ayah_indo.ayah and ayah_indo.sura = ayah.surah_id "+
                "LEFT JOIN uthmani_tajweed ut ON ayah.ayah_key = ut.verse_key " +
                "LEFT JOIN uthmani u ON ayah.ayah_key = u.verse_key "+
                "WHERE (ayah.text LIKE \"%"+search_term+"%\" or text_tashkeel LIKE \"%"+search_term+"%\" or content_en LIKE \"%"+search_term+"%\" or content_bn LIKE \"%"+search_term+"%\" or ayah_key LIKE \"%"+search_term+"%\") " +
                "order by ayah_index ASC " +
                "limit " + offset + "," + limit;
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
                    ayah.setText_uthmani(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani")));
                    ayah.setText_uthmani_tajweed(cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani_tajweed")));
                    ayahs.add(ayah);
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

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(SearchAyatActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SearchAyatActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(SearchAyatActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SearchAyatActivity.this, permissions(), PERMISSION_REQUEST_CODE);
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