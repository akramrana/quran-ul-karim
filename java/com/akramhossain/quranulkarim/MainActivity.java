package com.akramhossain.quranulkarim;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.HadithBookViewAdapter;
import com.akramhossain.quranulkarim.adapter.PopularRecyclerViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.HadithBook;
import com.akramhossain.quranulkarim.model.Sura;
import com.akramhossain.quranulkarim.task.BannerJsonFromUrlTask;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;
import com.akramhossain.quranulkarim.util.ConnectionDetector;
import com.akramhossain.quranulkarim.util.PrayTime;
import com.akramhossain.quranulkarim.util.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.akramhossain.quranulkarim.BugReportActivity.host;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private GridviewAdapter mAdapter;
    private ArrayList<String> listText;
    private ArrayList<Integer> listImage;

    private GridView gridView;

    //private DatabaseHelper mDBHelper;
    //private SQLiteDatabase mDb;
    TextView start_from_last;
    TextView txtNightMode;
    TextView name_title_ar;

    View horizontal_line;
    DatabaseHelper dbhelper;
    LinearLayout sura_link, bookmark_link, search_link, quick_links_link, word_collection_link, about_link, juz_link, hizb_link, rub_link, time_link, setting_link, masjid_link, feedback_link, privacy_link, terms_link, qibla_compass, daily_goal;
    Button btnPrayerTime;

    public static final String NIGHT_MODE = "APP_NIGHT_MODE";
    SharedPreferences mPrefs;
    String appTheme;

    private RecyclerView popularSearchView;
    private ArrayList<Sura> popularSearches;
    private PopularRecyclerViewAdapter rvAdapter;

    TextView mosque_near_me, settings, bugReport;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    Typeface fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, font, fontTahaNaskh, fontKitab;

    TextView txtPer;

    ProgressBar prog;

    public static String URL;

    ImageView img;

    boolean gps_enabled=false;
    boolean network_enabled=false;
    int calcMethod = 4;
    int asrJuristicMethod = 1;
    TextView ftime,stime,ztime,atime,mtime,itime;
    double selectedLatitude = -1;
    double selectedlongitude = -1;
    LinearLayout night_recite_section;
    LinearLayout friday_section;

    private RecyclerView hbRecyclerview;
    LinearLayoutManager hbLayoutManager;
    private ArrayList<HadithBook> hadithBook;
    public HadithBookViewAdapter hbRvAdapter;
    public static String HB_URL;
    public static String hb_host = "http://quran.codxplore.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);
        appTheme = mPrefs.getString(NIGHT_MODE, "-1");

//        SwitchCompat switchCompat = findViewById(R.id.switchCompat);
        CheckBox switchCompat = findViewById(R.id.switchCompat);
        txtNightMode = findViewById(R.id.txtNightMode);

        Log.d("APP THEME", String.valueOf(appTheme));

        if (appTheme.equals("1")) {
            switchCompat.setChecked(true);
            txtNightMode.setText("Dark mode enabled");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (appTheme.equals("0")) {
            switchCompat.setChecked(false);
            txtNightMode.setText("Dark mode disabled");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchCompat.setChecked(true);
            txtNightMode.setText("Dark mode enabled");
        }
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(NIGHT_MODE, "1");
                    editor.apply();
                    //
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);
                } else {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(NIGHT_MODE, "0");
                    editor.apply();
                    //
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);
                }
            }
        });

        //prepareList();

        start_from_last = (TextView) findViewById(R.id.start_from_last);
        horizontal_line = (View) findViewById(R.id.horizontal_line);
        name_title_ar = (TextView) findViewById(R.id.name_title_ar);

        sura_link = (LinearLayout) findViewById(R.id.sura_link);
        sura_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SuraListV2Activity.class);
                startActivity(i);
            }
        });

        bookmark_link = (LinearLayout) findViewById(R.id.bookmark_link);
        bookmark_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BookmarkActivity.class);
                startActivity(i);
            }
        });

        search_link = (LinearLayout) findViewById(R.id.search_link);
        search_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(i);
            }
        });

        quick_links_link = (LinearLayout) findViewById(R.id.quick_links_link);
        quick_links_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), QuickLinksActivity.class);
                startActivity(i);
            }
        });

        word_collection_link = (LinearLayout) findViewById(R.id.word_collection_link);
        word_collection_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
                startActivity(i);
            }
        });

        about_link = (LinearLayout) findViewById(R.id.about_link);
        about_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(i);
            }
        });

        time_link = (LinearLayout) findViewById(R.id.time_link);
        time_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PrayerTimesActivity.class);
                startActivity(i);
            }
        });

        juz_link = (LinearLayout) findViewById(R.id.juz_link);
        juz_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), JuzActivity.class);
                startActivity(i);
            }
        });

        hizb_link = (LinearLayout) findViewById(R.id.hizb_link);
        hizb_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HizbActivity.class);
                startActivity(i);
            }
        });

        rub_link = (LinearLayout) findViewById(R.id.rub_link);
        rub_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RubActivity.class);
                startActivity(i);
            }
        });

        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        start_from_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql = "SELECT last_position.sura_id,position,name_english,name_arabic " +
                        "FROM last_position " +
                        "LEFT JOIN sura ON last_position.sura_id = sura.surah_id " +
                        "LIMIT 1";
                Log.d("Last Position SQL", sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        String sura_id = cursor.getString(cursor.getColumnIndexOrThrow("sura_id")).toString();
                        String position = cursor.getString(cursor.getColumnIndexOrThrow("position")).toString();
                        String sura_name = cursor.getString(cursor.getColumnIndexOrThrow("name_english")).toString();
                        String sura_name_arabic = cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")).toString();

                        Log.d("Last Position Sura", sura_id);
                        Log.d("Last Position Sura POS", position);
                        //
                        Intent in = new Intent(getApplicationContext(), SuraDetailsActivity.class);
                        in.putExtra("sura_id", sura_id);
                        in.putExtra("sura_name", sura_name);
                        in.putExtra("sura_name_arabic", sura_name_arabic);
                        in.putExtra("position", position);
                        startActivityForResult(in, 100);
                    }
                } catch (Exception e) {
                    Log.i("Last Position Select", e.getMessage());
                    throw new RuntimeException("SQL Query: " + sql, e);
                } finally {
                    db.close();
                }
            }
        });

        //popular searches
        popularSearchView = (RecyclerView) findViewById(R.id.popularSearchView);
        LinearLayoutManager rLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        popularSearchView.setLayoutManager(rLinearLayoutManager);
        setPopularSearchViewAdapter();

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();


        //mosque_near_me = (TextView) findViewById(R.id.mosque_near_me);
        masjid_link = (LinearLayout) findViewById(R.id.masjid_link);
        masjid_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetPresent) {
                    Intent in = new Intent(getApplicationContext(), MosqueNearActivity.class);
                    startActivityForResult(in, 100);
                } else {
                    Toast.makeText(MainActivity.this, R.string.text_enable_internet, Toast.LENGTH_LONG).show();
                }
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //settings = (TextView) findViewById(R.id.settings);
        setting_link = (LinearLayout) findViewById(R.id.setting_link);
        setting_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), SettingActivity.class);
                startActivityForResult(in, 100);
            }
        });

        fontUthmani = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");
        fontTahaNaskh = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/kitab.ttf");

        String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
        if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
            name_title_ar.setTypeface(fontAlmajeed);
        }
        if(mp_arabicFontFamily.equals("Al Qalam Quran")){
            name_title_ar.setTypeface(fontAlQalam);
        }
        if(mp_arabicFontFamily.equals("Noore Huda")){
            name_title_ar.setTypeface(fontUthmani);
        }
        if(mp_arabicFontFamily.equals("Noore Hidayat")){
            name_title_ar.setTypeface(fontNooreHidayat);
        }
        if(mp_arabicFontFamily.equals("Saleem Quran")){
            name_title_ar.setTypeface(fontSaleem);
        }
        if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
            name_title_ar.setTypeface(fontTahaNaskh);
        }
        if(mp_arabicFontFamily.equals("Arabic Regular")){
            name_title_ar.setTypeface(fontKitab);
        }

        TextView textView11 = (TextView) findViewById(R.id.textView11);
        textView11.setTypeface(font);

        TextView juzTextView11 = (TextView) findViewById(R.id.juzTextView11);
        juzTextView11.setTypeface(font);

        TextView hizbTextView11 = (TextView) findViewById(R.id.hizbTextView11);
        hizbTextView11.setTypeface(font);

        TextView rubTextView11 = (TextView) findViewById(R.id.rubTextView11);
        rubTextView11.setTypeface(font);

        TextView textView33 = (TextView) findViewById(R.id.textView33);
        textView33.setTypeface(font);

        TextView textView22 = (TextView) findViewById(R.id.textView22);
        textView22.setTypeface(font);

        TextView textView44 = (TextView) findViewById(R.id.textView44);
        textView44.setTypeface(font);

        TextView textView55 = (TextView) findViewById(R.id.textView55);
        textView55.setTypeface(font);

        TextView timeTextView11 = (TextView) findViewById(R.id.timeTextView11);
        timeTextView11.setTypeface(font);

        TextView textView66 = (TextView) findViewById(R.id.textView66);
        textView66.setTypeface(font);

        TextView textView77 = (TextView) findViewById(R.id.textView77);
        textView77.setTypeface(font);

        TextView textView88 = (TextView) findViewById(R.id.textView88);
        textView88.setTypeface(font);

        TextView textView99 = (TextView) findViewById(R.id.textView99);
        textView99.setTypeface(font);

        TextView textView1010 = (TextView) findViewById(R.id.textView1010);
        textView1010.setTypeface(font);

        TextView textView1212 = (TextView) findViewById(R.id.textView1212);
        textView1212.setTypeface(font);

        TextView qc_text_view_bn = (TextView) findViewById(R.id.qc_text_view_bn);
        qc_text_view_bn.setTypeface(font);

        TextView dg_text_view_bn = (TextView) findViewById(R.id.dg_text_view_bn);
        dg_text_view_bn.setTypeface(font);

        TextView subj_text_view_bn = (TextView) findViewById(R.id.subj_text_view_bn);
        subj_text_view_bn.setTypeface(font);

        TextView hadith_text_view_bn = (TextView) findViewById(R.id.hadith_text_view_bn);
        hadith_text_view_bn.setTypeface(font);

        TextView noa_text_view_bn = (TextView) findViewById(R.id.noa_text_view_bn);
        noa_text_view_bn.setTypeface(font);

        //bugReport = (TextView) findViewById(R.id.bugReport);
        feedback_link = (LinearLayout) findViewById(R.id.feedback_link);
        feedback_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), BugReportActivity.class);
                startActivityForResult(in, 100);
            }
        });

        privacy_link = (LinearLayout) findViewById(R.id.privacy_link);
        privacy_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MoreMenuActivity.class);
                i.putExtra("cms_title", "Privacy Policy");
                i.putExtra("cms_page", "privacy-policy");
                startActivity(i);
            }
        });

        terms_link = (LinearLayout) findViewById(R.id.terms_link);
        terms_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MoreMenuActivity.class);
                i.putExtra("cms_title", "Terms and Conditions");
                i.putExtra("cms_page", "terms-quran");
                startActivity(i);
            }
        });

        qibla_compass = (LinearLayout) findViewById(R.id.qibla_compass);
        qibla_compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), QiblaCompassActivity.class);
                startActivity(i);
            }
        });

        daily_goal = (LinearLayout) findViewById(R.id.daily_goal);
        daily_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DailyGoalsActivity.class);
                startActivity(i);
            }
        });

        txtPer = (TextView) findViewById(R.id.txtPer);
        prog = (ProgressBar) findViewById(R.id.prog);

        LinearLayout subj_sec = (LinearLayout) findViewById(R.id.subj_sec);
        subj_sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SubjectWiseActivity.class);
                startActivity(i);
            }
        });

        LinearLayout hadith_sec = (LinearLayout) findViewById(R.id.hadith_sec);
        hadith_sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HadithBookActivity.class);
                startActivity(i);
            }
        });

        LinearLayout name_of_allah = (LinearLayout) findViewById(R.id.name_of_allah);
        name_of_allah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NameOfAllahActivity.class);
                startActivity(i);
            }
        });
        start_from_last.setTypeface(font);
        img = (ImageView) findViewById(R.id.img);
        //
        TextView dz_text_view_bn = (TextView) findViewById(R.id.dz_text_view_bn);
        dz_text_view_bn.setTypeface(font);
        LinearLayout dua_zikr = (LinearLayout) findViewById(R.id.dua_zikr);
        dua_zikr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TagActivity.class);
                startActivity(i);
            }
        });
        TextView bcre_text_view_bn = (TextView) findViewById(R.id.bcre_text_view_bn);
        bcre_text_view_bn.setTypeface(font);
        LinearLayout back_restore = (LinearLayout) findViewById(R.id.back_restore);
        back_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BackupRestoreActivity.class);
                startActivity(i);
            }
        });
        //
        TextView friday_ttl_bn = (TextView) findViewById(R.id.friday_ttl_bn);
        friday_ttl_bn.setTypeface(font);

        TextView friday_subttl_bn = (TextView) findViewById(R.id.friday_subttl_bn);
        friday_subttl_bn.setTypeface(font);

        Button kahf_button = (Button) findViewById(R.id.kahf_button);
        kahf_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SuraDetailsActivity.class);
                i.putExtra("sura_id", "18");
                i.putExtra("sura_name", "Al-Kahf");
                i.putExtra("sura_name_arabic", "الكهف");
                startActivity(i);
            }
        });
        //
        friday_section = (LinearLayout) findViewById(R.id.friday_section);
        //
        TextView night_ttl_bn = (TextView) findViewById(R.id.night_ttl_bn);
        night_ttl_bn.setTypeface(font);

        TextView night_subttl_bn = (TextView) findViewById(R.id.night_subttl_bn);
        night_subttl_bn.setTypeface(font);

        TextView night_subttl_10_verse_bn = (TextView) findViewById(R.id.night_subttl_10_verse_bn);
        night_subttl_10_verse_bn.setTypeface(font);
        //
        night_recite_section = (LinearLayout) findViewById(R.id.night_recite_section);
        //
        Button mulk_button = (Button) findViewById(R.id.mulk_button);
        mulk_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SuraDetailsActivity.class);
                i.putExtra("sura_id", "67");
                i.putExtra("sura_name", "Al-Mulk");
                i.putExtra("sura_name_arabic", "الملك");
                startActivity(i);
            }
        });

        Button baqara_button = (Button) findViewById(R.id.baqara_button);
        baqara_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SuraDetailsActivity.class);
                i.putExtra("sura_id", "2");
                i.putExtra("sura_name", "Al-Baqarah");
                i.putExtra("sura_name_arabic", "البقرة");
                i.putExtra("position", "276");
                startActivity(i);
            }
        });
        //
        Button enter_challenge_button = (Button) findViewById(R.id.enter_challenge_button);
        enter_challenge_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChallengeDashboardActivity.class);
                startActivity(i);
            }
        });
        //
        ftime = (TextView) findViewById(R.id.ftime);
        ztime = (TextView) findViewById(R.id.ztime);
        atime = (TextView) findViewById(R.id.atime);
        mtime = (TextView) findViewById(R.id.mtime);
        itime = (TextView) findViewById(R.id.itime);
        //
        TimeZone tmzone = TimeZone.getDefault();
        double rawOffet = tmzone.getRawOffset();
        //double rawOffet = 19800000;
        double rawOffetDiv = 1000;
        double divBySec = 3600;
        double hourDiff = (rawOffet/ rawOffetDiv)/divBySec;
        System.out.println("Timezone: "+hourDiff);

        LinearLayout prayer_times_section = (LinearLayout) findViewById(R.id.prayer_times_section);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (checkPermission()) {
                LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
                try{
                    gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                }catch(Exception ex){
                    Log.d(TAG,ex.getMessage());
                }
                //
                try{
                    network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                }catch(Exception ex){
                    Log.d(TAG,ex.getMessage());
                }
                //
                Location networkLoacation = null, gpsLocation = null, location = null;
                //
                if(gps_enabled){
                    gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if(network_enabled){
                    networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                //
                if (gpsLocation != null && networkLoacation != null) {
                    if (gpsLocation.getAccuracy() > networkLoacation.getAccuracy()) {
                        location = networkLoacation;
                    }else {
                        location = gpsLocation;
                    }
                } else {
                    if (gpsLocation != null) {
                        location = gpsLocation;
                    } else if (networkLoacation != null) {
                        location = networkLoacation;
                    }
                }
                if(location!= null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    double timezone = hourDiff;
                    selectedLatitude = latitude;
                    selectedlongitude = longitude;
                    getPrayerTimes(latitude, longitude, timezone);
                    //
                    prayer_times_section.setVisibility(View.VISIBLE);
                }else{
                    prayer_times_section.setVisibility(View.GONE);
                    nightlyRecitationWithoutPrayerTimes();
                    fridayRecitationWithoutPrayerTimes();
                }
            }else{
                prayer_times_section.setVisibility(View.GONE);
                nightlyRecitationWithoutPrayerTimes();
                fridayRecitationWithoutPrayerTimes();
            }
        }else{
            prayer_times_section.setVisibility(View.GONE);
            nightlyRecitationWithoutPrayerTimes();
            fridayRecitationWithoutPrayerTimes();
        }
        //
        URL = host+"api/banner";
        getBannerFromInternet();
        //
        hbRecyclerview = (RecyclerView) findViewById(R.id.hadith_book_list);
        hbLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        hbRecyclerview.setLayoutManager(hbLayoutManager);
        //
        HB_URL = hb_host+"api/v1/book-list.php";
        setHbRecyclerViewAdapter();
        String IS_HADITH_BOOK_JSON_DATA_STORED = mPrefs.getString("IS_HADITH_BOOK_JSON_DATA_STORED", "0");
        if(IS_HADITH_BOOK_JSON_DATA_STORED.equals("1")){
            String HADITH_BOOK_JSON_DATA = mPrefs.getString("HADITH_BOOK_JSON_DATA", "{}");
            Log.i(TAG, HADITH_BOOK_JSON_DATA);
            parseHadithJsonResponse(HADITH_BOOK_JSON_DATA);
        }else {
            getHbDataFromInternet();
        }
        hbRecyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), hbRecyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                HadithBook hb = hadithBook.get(position);
                Intent in = new Intent(getApplicationContext(),HadithChapterActivity.class);
                in.putExtra("book_id", hb.getBook_id());
                in.putExtra("name_en", hb.getName_english());
                in.putExtra("name_ar", hb.getName_arabic());
                in.putExtra("name_bn", hb.getName_bangla());
                startActivity(in);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        TextView ramadan_planner_text_view_bn = (TextView) findViewById(R.id.ramadan_planner_text_view_bn);
        ramadan_planner_text_view_bn.setTypeface(font);

        LinearLayout ramadan_planner_sec = (LinearLayout) findViewById(R.id.ramadan_planner_sec);
        ramadan_planner_sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RamadanPlannerActivity.class);
                startActivity(i);
            }
        });

        AnimationDrawable animationDrawable = (AnimationDrawable) ramadan_planner_sec.getBackground();
        if (animationDrawable != null) {
            animationDrawable.start();

            new Handler(Looper.getMainLooper()).postDelayed(() -> animationDrawable.stop(), 30000);
        }
    }

    private void setHbRecyclerViewAdapter() {
        hadithBook = new ArrayList<HadithBook>();
        hbRvAdapter = new HadithBookViewAdapter(MainActivity.this, hadithBook, this, "MA");
        hbRecyclerview.setAdapter(hbRvAdapter);
    }

    private void getHbDataFromInternet() {
        Log.i(TAG, URL);
        if (isInternetPresent) {
            new JsonFromUrlTask(this, HB_URL, TAG, "");
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    public void fridayRecitationWithoutPrayerTimes(){
        Date c = Calendar.getInstance().getTime();
        String dayWeekText = new SimpleDateFormat("EEEE", Locale.getDefault()).format(c);
        Log.d("day name",dayWeekText);
        if(dayWeekText.equals("Friday")){
            friday_section.setVisibility(View.VISIBLE);
        }else{
            friday_section.setVisibility(View.GONE);
        }
    }

    public void nightlyRecitationWithoutPrayerTimes(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalTime currentTime = LocalTime.now();
            LocalTime nightTimeStart = LocalTime.of(20, 0); // 8 PM
            LocalTime nightTimeEnd = LocalTime.of(4, 0); // 4 AM
            if (currentTime.isAfter(nightTimeStart) || currentTime.isBefore(nightTimeEnd)) {
                // It's night time
                night_recite_section.setVisibility(View.VISIBLE);
            }else{
                night_recite_section.setVisibility(View.GONE);
            }
        }
    }

    public void getPrayerTimes(double latitude,double longitude,double timezone){
        PrayTime prayers = new PrayTime();
        prayers.setTimeFormat(prayers.Time12);
        prayers.setCalcMethod(calcMethod);
        prayers.setAsrJuristic(asrJuristicMethod);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal,latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();

        String magribTime = "";
        String fajrTime = "";

        for (int i = 0; i < prayerTimes.size(); i++) {
            Log.d(TAG, prayerNames.get(i) + " - " + prayerTimes.get(i));
            if(i==0){
                ftime.setText(prayerTimes.get(i));
                fajrTime = prayerTimes.get(i);
            }
            if(i==2){
                ztime.setText(prayerTimes.get(i));
            }
            if(i==3){
                atime.setText(prayerTimes.get(i));
            }
            if(i==4){
                mtime.setText(prayerTimes.get(i));
                magribTime = prayerTimes.get(i);
            }
            if(i==6){
                itime.setText(prayerTimes.get(i));
            }
        }
        Log.d("Magrib Time",magribTime);
        Log.d("Fajr Time",fajrTime);

        if(magribTime != null && !magribTime.isEmpty() && fajrTime !=null && !fajrTime.isEmpty()){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
                LocalTime magPrayerTime = null, fjrPrayerTime = null;
                try {
                    magPrayerTime = LocalTime.parse(magribTime.toUpperCase(), formatter);
                }catch (DateTimeParseException e) {
                    System.out.println("Error parsing time: " + e.getMessage());
                }
                //
                try {
                    fjrPrayerTime = LocalTime.parse(fajrTime.toUpperCase(), formatter);
                }catch (DateTimeParseException e) {
                    System.out.println("Error parsing time: " + e.getMessage());
                }
                //
                LocalTime currentTime = LocalTime.now();

                Log.d("Parsed Magrib Time",magPrayerTime.toString());
                Log.d("Parsed Fajr Time",fjrPrayerTime.toString());
                Log.d("Parsed Current Time",currentTime.toString());

                if (currentTime.isAfter(magPrayerTime) || currentTime.isBefore(fjrPrayerTime)) {
                    night_recite_section.setVisibility(View.VISIBLE);
                }else{
                    night_recite_section.setVisibility(View.GONE);
                }
                //
                Date c = Calendar.getInstance().getTime();
                String dayWeekText = new SimpleDateFormat("EEEE", Locale.getDefault()).format(c);
                Log.d("day name",dayWeekText);
                if(dayWeekText.equals("Friday")){
                    if (currentTime.isAfter(magPrayerTime)) {
                        friday_section.setVisibility(View.GONE);
                    }else {
                        friday_section.setVisibility(View.VISIBLE);
                    }
                }else{
                    friday_section.setVisibility(View.GONE);
                }
            }else{
                nightlyRecitationWithoutPrayerTimes();
                fridayRecitationWithoutPrayerTimes();
            }
        }else{
            nightlyRecitationWithoutPrayerTimes();
            fridayRecitationWithoutPrayerTimes();
        }
    }

    private void getPopularSearchFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT sura.*,bangla_name.name_bangla " +
                "FROM sura " +
                "left join bangla_name on sura.surah_id = bangla_name.surah_id " +
                "WHERE sura.surah_id IN(17,18,19,36,49,51,55,56,67,71,72,73,77,78,85) " +
                "order by RANDOM() " +
                "LIMIT 10";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Sura sura = new Sura();
                    sura.setSurah_id(cursor.getString(cursor.getColumnIndexOrThrow("surah_id")));
                    sura.setName_arabic(cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")));
                    sura.setName_english(cursor.getString(cursor.getColumnIndexOrThrow("name_english")));
                    sura.setName_simple(cursor.getString(cursor.getColumnIndexOrThrow("name_simple")));
                    sura.setRevelation_place(cursor.getString(cursor.getColumnIndexOrThrow("revelation_place")));
                    sura.setAyat(cursor.getString(cursor.getColumnIndexOrThrow("ayat")));
                    sura.setRevelation_order(cursor.getString(cursor.getColumnIndexOrThrow("revelation_order")));
                    sura.setId(cursor.getString(cursor.getColumnIndexOrThrow("sid")));
                    sura.setName_bangla(cursor.getString(cursor.getColumnIndexOrThrow("name_bangla")));
                    popularSearches.add(sura);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new RuntimeException("SQL Query: " + sql, e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }
        rvAdapter.notifyDataSetChanged();
    }

    private void setPopularSearchViewAdapter() {
        popularSearches = new ArrayList<Sura>();
        rvAdapter = new PopularRecyclerViewAdapter(MainActivity.this, popularSearches, this);
        popularSearchView.setAdapter(rvAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void prepareList() {
        listText = new ArrayList<String>();

        listText.add("Sura list");
        listText.add("Bookmarks");
        listText.add("Search");
        listText.add("Quick Links");
        listText.add("Dictionary");
        listText.add("About");

        listImage = new ArrayList<Integer>();
        listImage.add(R.drawable.menu_icon);
        listImage.add(R.drawable.bookmark);
        listImage.add(R.drawable.search);
        listImage.add(R.drawable.links);
        listImage.add(R.drawable.collection);
        listImage.add(R.drawable.about);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d("ACTIVTY","RESUME");
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT last_position.sura_id,position,name_english,name_arabic " +
                "FROM last_position " +
                "LEFT JOIN sura ON last_position.sura_id = sura.surah_id " +
                "LIMIT 1";
        Log.d("Last Position SQL", sql);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                start_from_last.setVisibility(View.VISIBLE);
                horizontal_line.setVisibility(View.VISIBLE);
            } else {
                start_from_last.setVisibility(View.GONE);
                horizontal_line.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.i("Last Position Select", e.getMessage());
            throw new RuntimeException("SQL Query: " + sql, e);
        } finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }

        getPopularSearchFromLocalDb();
        calculateReportsValue();
    }

    public void calculateReportsValue(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        //System.out.println(dateFormat.format(date));
        String dtStr = dateFormat.format(date);
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select sum(perform_tahajjud+perform_fajr+morning_adhkar+quran_recitation+study_hadith+salat_ud_doha+dhuhr_prayer+asr_prayer+maghrib_prayer+isha_prayer+charity+literature+surah_mulk_recitation+recitation_last_2_surah_baqarah+ayatul_kursi+recitation_first_last_10_surah_kahf+tasbih+smoking+alcohol+haram_things+backbiting+slandering) as total_points\n" +
                    "from reports \n" +
                    "where date = '"+dtStr+"'";
        Cursor cursor = db.rawQuery(sql, null);
        Log.i("Report SQL", sql);
        try {
            if (cursor.moveToFirst()) {
                Integer total_points = cursor.getInt(cursor.getColumnIndexOrThrow("total_points"));
                System.out.println("Report value: "+total_points);
                txtPer.setText("Daily Goals\n"+total_points+"%\nCompleted");
                prog.setProgress(total_points,true);
            }
        }catch (Exception e) {
            Log.i("Report SQL", e.getMessage());
            throw new RuntimeException("SQL Query: " + sql, e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }
    }

    private void getBannerFromInternet() {
        if (isInternetPresent) {
            new BannerJsonFromUrlTask(this, URL);
        }
    }

    public void parseJsonResponse(String result) {
        //Log.i(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONObject json = response.getJSONObject("data");
            if(json.length()==0){
                Log.d("banner","Banner is empty");
                img.setVisibility(View.GONE);
            }else{
                Picasso.get().load(json.getString("image"))
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(img);
                img.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseHadithJsonResponse(String result){
        try {
            JSONObject response = new JSONObject(result);
            JSONArray jArray = new JSONArray(response.getString("data"));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //Log.i(TAG, jObject.toString());
                HadithBook hb = new HadithBook();
                hb.setBook_id(jObject.getString("id"));
                hb.setName_english(jObject.getString("name_en"));
                hb.setName_arabic(jObject.getString("name_ar"));
                hb.setName_bangla(jObject.getString("name_bn"));
                hadithBook.add(hb);
            }

            hbRvAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
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

}
