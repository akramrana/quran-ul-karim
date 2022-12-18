package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.SuraDetailsViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;
import com.akramhossain.quranulkarim.util.ConnectionDetector;
import com.akramhossain.quranulkarim.task.GetJsonFromUrlTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SuraDetailsActivity extends AppCompatActivity {

    public static String suraId;
    public static String suraName;
    public static String suraNameArabic;
    public static String suraLastPosition="";

    private RecyclerView recyclerview;
    private SuraDetailsViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Ayah> ayahs;
    private static final String TAG = SuraDetailsActivity.class.getSimpleName();
    DatabaseHelper dbhelper;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 100;
    Integer counter = 0;
    TextView titleEn,titleAr, text_bismillah;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    public String url;

    private static final int PERMISSION_REQUEST_CODE = 100;

    TextView play_audio;
    TextView pause_audio;
    TextView stop_audio;
    TextView resume_audio;

    RelativeLayout rl;

    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem;
    SharedPreferences mPrefs;

    private TextView startTime, songTime;
    private ImageButton forwardbtn, backwardbtn, pausebtn, playbtn;

    private static int oTime =0, sTime =0, eTime =0, fTime = 5000, bTime = 5000;
    private SeekBar songPrgs;
    private Handler hdlr = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (extras != null) {
            suraId = extras.getString("sura_id");
            suraName = extras.getString("sura_name");
            suraNameArabic = extras.getString("sura_name_arabic");
            suraLastPosition = extras.getString("position");
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");

        url = "http://websites.codxplore.com/islamicvideo/api/patch?sura_id="+suraId;
        //url = "http://10.0.2.2/islamicvideo/api/patch?sura_id="+suraId;

        setTitle(suraNameArabic+"-"+suraName);

        setContentView(R.layout.activity_sura_details);

        titleEn = (TextView) findViewById(R.id.name_title_en);
        titleEn.setText(suraName);

        titleAr = (TextView) findViewById(R.id.name_title_ar);
        titleAr.setText(suraNameArabic);

        text_bismillah = (TextView) findViewById(R.id.text_bismillah);

        recyclerview = (RecyclerView) findViewById(R.id.ayah_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        suraId = suraId.trim();

        Log.d("SURAH ID",suraId.trim());

        rl = (RelativeLayout)findViewById(R.id.bismillah_section);

        if(suraId.equals("1") || suraId.equals("9")){
            rl.setVisibility(View.GONE);
        }

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    SQLiteDatabase db1 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    String sql1 = "DELETE FROM last_position";
                    try {
                        db1.execSQL(sql1);
                    }catch (Exception e){
                        Log.i("Last Position Deleted", e.getMessage());
                    }finally {
                        db1.close();
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int firstVisiblePosition = mLayoutManager.findLastVisibleItemPosition();
                    //Log.i("Top Position", Integer.toString(topPosition));
                    //Log.i("Last Position Visible", Integer.toString(firstVisiblePosition));
                    //int firstVisiblePosition = mLayoutManager.findLastVisibleItemPosition();
                    if(firstVisiblePosition > 1) {
                        rl.setVisibility(View.GONE);
                    }
                    SQLiteDatabase db1 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    String sql1 = "DELETE FROM last_position";
                    try {
                        db1.execSQL(sql1);
                        ContentValues values = new ContentValues();
                        values.put("sura_id", suraId);
                        values.put("position", firstVisiblePosition);
                        DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase().insertOrThrow("last_position", "", values);
                    }
                    catch (Exception e){
                        Log.i("Last Position", e.getMessage());
                    }
                    finally {
                        db1.close();
                    }

                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                            String sql = "SELECT COUNT(*) FROM ayah WHERE surah_id = "+suraId;
                            Cursor countHistory = db.rawQuery(sql,null);
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
                            }catch (Exception e){
                                Log.i("On Scroll Count Check", e.getMessage());
                            }finally {
                                if (countHistory != null && !countHistory.isClosed()){
                                    countHistory.close();
                                }
                                db.close();
                            }
                        }
                    }
                }else{
                    if(!recyclerView.canScrollVertically(-1)){
                        //Toast.makeText(getApplicationContext(),"Top most item",Toast.LENGTH_SHORT).show();
                        if(!suraId.equals("1") && !suraId.equals("9")) {
                            rl.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

        });

        if (suraLastPosition != null && !suraLastPosition.isEmpty()) {
            recyclerview.scrollToPosition(Integer.parseInt(suraLastPosition));
        }

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        getDataFromLocalDb();

        Button previousBtn = (Button) findViewById(R.id.previousBtn);
        Button nextBtn = (Button) findViewById(R.id.nextBtn);
        final Button quickLinkBtn = (Button) findViewById(R.id.quickLinkBtn);

        String checksql = "SELECT * FROM quick_link WHERE sura_id = "+suraId;
        SQLiteDatabase chkdb = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        Cursor cursor1 = chkdb.rawQuery(checksql,null);

        try {
            if (cursor1.moveToFirst()) {
                quickLinkBtn.setText("Remove from favourites");
                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
            } else {
                quickLinkBtn.setText("Add to favourites");
                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_upload, 0, 0, 0);
            }
        }catch (Exception e){
            Log.i("Quick Link Check", e.getMessage());
        }finally {
            if (cursor1 != null && !cursor1.isClosed()){
                cursor1.close();
            }
            chkdb.close();
        }

        /*play_audio = (TextView) findViewById(R.id.play_audio);
        pause_audio = (TextView) findViewById(R.id.pause_audio);
        resume_audio = (TextView) findViewById(R.id.resume_audio);
        stop_audio = (TextView) findViewById(R.id.stop_audio);*/

        startTime = (TextView)findViewById(R.id.txtStartTime);
        songTime = (TextView)findViewById(R.id.txtSongTime);

        previousBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AudioPlay.stopAudio();
                songTime.setText(String.format("%d min, %d sec", 0, 0));
                startTime.setText(String.format("%d min, %d sec", 0, 0));
                hdlr.removeCallbacksAndMessages(null);
                pausebtn.setEnabled(false);
                playbtn.setEnabled(true);
                oTime = 0;
                sTime =0;
                eTime =0;
                fTime = 5000;
                bTime = 5000;
                songPrgs.setProgress(0);
                /*play_audio.setVisibility(View.VISIBLE);
                pause_audio.setVisibility(View.GONE);
                resume_audio.setVisibility(View.GONE);
                stop_audio.setVisibility(View.GONE);*/
                //
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql = "SELECT * FROM sura WHERE surah_id < "+suraId+" order by surah_id DESC limit 1";
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        String prevSuraId = cursor.getString(cursor.getColumnIndexOrThrow("surah_id")).toString();
                        String prevSuraNameEn = cursor.getString(cursor.getColumnIndexOrThrow("name_english")).toString();
                        String prevSuraNameAr = cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")).toString();

                        suraId = prevSuraId;
                        suraName = prevSuraNameEn;
                        suraNameArabic = prevSuraNameAr;

                        titleEn.setText(suraName);
                        titleAr.setText(suraNameArabic);

                        //SQLiteDatabase db1 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                        String checksql = "SELECT * FROM quick_link WHERE sura_id = "+suraId;
                        Cursor cursor1 = db.rawQuery(checksql,null);

                        Log.i("Quick Link Check Inner", checksql);

                        Log.d("surah Id",suraId);

                        try {
                            if (cursor1.moveToFirst()) {
                                quickLinkBtn.setText("Remove from favourites");
                                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
                            } else {
                                quickLinkBtn.setText("Add to favourites");
                                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_upload, 0, 0, 0);
                            }
                        }catch (Exception e){
                            Log.i(TAG, e.getMessage());
                        }finally {
                            if (cursor1 != null && !cursor1.isClosed()){
                                cursor1.close();
                            }
                        }
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor != null && !cursor.isClosed()){
                        cursor.close();
                    }
                    db.close();

                    setRecyclerViewAdapter();
                    getDataFromLocalDb();
                }

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AudioPlay.stopAudio();
                songTime.setText(String.format("%d min, %d sec", 0, 0));
                startTime.setText(String.format("%d min, %d sec", 0, 0));
                hdlr.removeCallbacksAndMessages(null);
                pausebtn.setEnabled(false);
                playbtn.setEnabled(true);
                oTime = 0;
                sTime =0;
                eTime =0;
                fTime = 5000;
                bTime = 5000;
                songPrgs.setProgress(0);
                /*play_audio.setVisibility(View.VISIBLE);
                pause_audio.setVisibility(View.GONE);
                resume_audio.setVisibility(View.GONE);
                stop_audio.setVisibility(View.GONE);*/
                //
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql = "SELECT * FROM sura WHERE surah_id > "+suraId+" order by surah_id ASC limit 1";
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        String prevSuraId = cursor.getString(cursor.getColumnIndexOrThrow("surah_id")).toString();
                        String prevSuraNameEn = cursor.getString(cursor.getColumnIndexOrThrow("name_english")).toString();
                        String prevSuraNameAr = cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")).toString();

                        suraId = prevSuraId;
                        suraName = prevSuraNameEn;
                        suraNameArabic = prevSuraNameAr;

                        Log.d("surah Id",suraId);

                        titleEn.setText(suraName);
                        titleAr.setText(suraNameArabic);

                        //SQLiteDatabase db1 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                        String checksql = "SELECT * FROM quick_link WHERE sura_id = "+suraId;
                        Cursor cursor1 = db.rawQuery(checksql,null);

                        Log.i("Quick Link Check Inner", checksql);

                        try {
                            if (cursor1.moveToFirst()) {
                                quickLinkBtn.setText("Remove from favourites");
                                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
                            } else {
                                quickLinkBtn.setText("Add to favourites");
                                quickLinkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_upload, 0, 0, 0);
                            }
                        }catch (Exception e){
                            Log.i(TAG, e.getMessage());
                        }
                        finally {
                            if (cursor1 != null && !cursor1.isClosed()){
                                cursor1.close();
                            }
                        }
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor != null && !cursor.isClosed()){
                        cursor.close();
                    }
                    db.close();
                    setRecyclerViewAdapter();
                    getDataFromLocalDb();
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

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        //getPatchFromInternet();
        if (checkPermission()) {

        }else{
            requestPermission();
        }



        playbtn = (ImageButton)findViewById(R.id.btnPlay);
        pausebtn = (ImageButton)findViewById(R.id.btnPause);
        backwardbtn = (ImageButton)findViewById(R.id.btnBackward);
        forwardbtn = (ImageButton)findViewById(R.id.btnForward);

        songPrgs = (SeekBar)findViewById(R.id.sBar);
        songPrgs.setClickable(false);
        pausebtn.setEnabled(false);

        AudioPlay.stopAudio();
        songTime.setText(String.format("%d min, %d sec", 0, 0));
        startTime.setText(String.format("%d min, %d sec", 0, 0));
        songPrgs.setProgress(0);
        hdlr.removeCallbacksAndMessages(null);

        if (isInternetPresent) {
            playbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isLoaded = AudioPlay.isLoadedAudio();
                    String mp3Uri = AudioPlay.getAudioUri();
                    String audioUri = "https://download.quranicaudio.com/qdc/mishari_al_afasy/murattal/" + suraId + ".mp3";

                    if(isLoaded) {
                        if(mp3Uri.equals(audioUri)) {
                            AudioPlay.resumeAudio();
                            eTime = AudioPlay.getDuration();
                            sTime = AudioPlay.getCurrentPosition();
                        }else{
                            AudioPlay.stopAudio();
                            AudioPlay.playAudio(getApplicationContext(), audioUri);
                            eTime = AudioPlay.getDuration();
                            sTime = AudioPlay.getCurrentPosition();
                            oTime = 0;
                        }
                    }else{
                        AudioPlay.stopAudio();
                        AudioPlay.playAudio(getApplicationContext(), audioUri);
                        eTime = AudioPlay.getDuration();
                        sTime = AudioPlay.getCurrentPosition();
                        oTime = 0;
                    }
                    Log.d("audioUri",audioUri);
                    Log.d("eTime",String.valueOf(eTime));

                    if (oTime == 0) {
                        songPrgs.setMax(eTime);
                        oTime = 1;
                    }
                    songTime.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(eTime), TimeUnit.MILLISECONDS.toSeconds(eTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eTime))));
                    startTime.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime), TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
                    //songPrgs.setProgress(sTime);
                    hdlr.postDelayed(UpdateSongTime, 1000);
                    pausebtn.setEnabled(true);
                    playbtn.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_SHORT).show();
                }
            });

            pausebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioPlay.pauseAudio();
                    pausebtn.setEnabled(false);
                    playbtn.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Pausing Audio", Toast.LENGTH_SHORT).show();
                }
            });

            forwardbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((sTime + fTime) <= eTime)
                    {
                        sTime = sTime + fTime;
                        AudioPlay.seekTo(sTime);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
                    }
                    if(!playbtn.isEnabled()){
                        playbtn.setEnabled(true);
                    }
                }
            });

            backwardbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((sTime - bTime) > 0)
                    {
                        sTime = sTime - bTime;
                        AudioPlay.seekTo(sTime);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
                    }
                    if(!playbtn.isEnabled()){
                        playbtn.setEnabled(true);
                    }
                }
            });

//            play_audio.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AudioPlay.stopAudio();
//                    AudioPlay.playAudio(getApplicationContext(), "https://download.quranicaudio.com/qdc/mishari_al_afasy/murattal/" + suraId + ".mp3");
//
//                    play_audio.setVisibility(View.GONE);
//                    pause_audio.setVisibility(View.VISIBLE);
//                    resume_audio.setVisibility(View.GONE);
//                    stop_audio.setVisibility(View.VISIBLE);
//                }
//            });
//
//            pause_audio.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AudioPlay.pauseAudio();
//                    play_audio.setVisibility(View.GONE);
//                    pause_audio.setVisibility(View.GONE);
//                    resume_audio.setVisibility(View.VISIBLE);
//                    stop_audio.setVisibility(View.VISIBLE);
//                }
//            });
//
//            resume_audio.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AudioPlay.resumeAudio();
//                    play_audio.setVisibility(View.GONE);
//                    pause_audio.setVisibility(View.VISIBLE);
//                    resume_audio.setVisibility(View.GONE);
//                    stop_audio.setVisibility(View.VISIBLE);
//                }
//            });
//
//            stop_audio.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AudioPlay.stopAudio();
//                    play_audio.setVisibility(View.VISIBLE);
//                    pause_audio.setVisibility(View.GONE);
//                    resume_audio.setVisibility(View.GONE);
//                    stop_audio.setVisibility(View.GONE);
//                }
//            });
        }

        String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Arabic Regular");
        if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
            titleAr.setTypeface(fontAlmajeed);
            text_bismillah.setTypeface(fontAlmajeed);
        }
        if(mp_arabicFontFamily.equals("Al Qalam Quran")){
            titleAr.setTypeface(fontAlQalam);
            text_bismillah.setTypeface(fontAlQalam);
        }
        if(mp_arabicFontFamily.equals("Uthmanic Script")){
            titleAr.setTypeface(fontUthmani);
            text_bismillah.setTypeface(fontUthmani);
        }
        if(mp_arabicFontFamily.equals("Noore Hidayat")){
            titleAr.setTypeface(fontNooreHidayat);
            text_bismillah.setTypeface(fontNooreHidayat);
        }
        if(mp_arabicFontFamily.equals("Saleem Quran")){
            titleAr.setTypeface(fontSaleem);
            text_bismillah.setTypeface(fontSaleem);
        }

    }

    private void getPatchFromInternet() {
        if (isInternetPresent) {
            new GetJsonFromUrlTask(this, url).execute();
        }
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT ayah.*,sura.name_arabic,sura.name_complex,sura.name_english,sura.name_simple " +
                "FROM ayah " +
                "LEFT join sura ON ayah.surah_id = sura.surah_id " +
                "WHERE ayah.surah_id = "+suraId+" " +
                "order by ayah.ayah_index ASC " +
                "limit " + offset + "," + limit;
        //Log.i(TAG, sql);
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

    private void setRecyclerViewAdapter() {
        ayahs = new ArrayList<Ayah>();
        rvAdapter = new SuraDetailsViewAdapter(SuraDetailsActivity.this, ayahs, this);
        recyclerview.setAdapter(rvAdapter);
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }

    public void parseJsonResponse(String result) {
        //Log.i(TAG, result);
        try {
            JSONObject res = new JSONObject(result);
            JSONArray jarray = new JSONArray(res.getString("data"));

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);
                //Log.i(TAG, jObject.toString());
                String where_column = jObject.getString("where_column");
                String where_value = jObject.getString("where_value");
                String table_name = jObject.getString("table_name");
                //
                JSONArray jarrayColumn = new JSONArray(jObject.getString("columns"));
                for (int j = 0; j < jarrayColumn.length(); i++) {
                    JSONObject jObjectColumn = jarrayColumn.getJSONObject(i);
                    String update_column = jObjectColumn.getString("update_column");
                    String update_value = jObjectColumn.getString("update_value");

                    //Log.i(TAG, update_column+"="+update_value);

                    SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    try {
                        ContentValues cv = new ContentValues();
                        cv.put(update_column, update_value);
                        String selection = (where_column + " = ?");
                        String[] selectionArgs = {String.valueOf(where_value)};
                        db.update(table_name, cv, selection, selectionArgs);

                    }catch (Exception e){
                        Log.i(TAG, e.getMessage());
                    }
                    finally {
                        db.close();
                    }
                }
//                String sql = jObject.toString();
//                SQLiteDatabase db = dbhelper.getReadableDatabase();
//                try {
//                    db.execSQL(sql);
//                    //Log.i(TAG, sql);
//                }catch (Exception e){
//                    Log.i(TAG, e.getMessage());
//                }finally {
//                    db.close();
//                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SuraDetailsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SuraDetailsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(SuraDetailsActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SuraDetailsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            boolean isAudioStopped = AudioPlay.isStopped();
            if(isAudioStopped){
                hdlr.removeCallbacks(this);
                pausebtn.setEnabled(false);
                playbtn.setEnabled(true);

            }else {
                sTime = AudioPlay.getCurrentPosition();
                Log.d("stopped", String.valueOf(isAudioStopped));
                startTime.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime), TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
                songPrgs.setProgress(sTime);
                hdlr.postDelayed(this, 1000);
            }
        }
    };
}
