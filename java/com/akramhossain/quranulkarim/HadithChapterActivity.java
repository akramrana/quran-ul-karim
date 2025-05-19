package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.HadithChapterViewAdapter;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.HadithBook;
import com.akramhossain.quranulkarim.model.HadithChapter;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;
import com.akramhossain.quranulkarim.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class HadithChapterActivity extends AppCompatActivity {

    public static String bookId;
    public static String bookNameEn;

    public static String bookNameAr;

    public static String bookNameBn;

    Typeface font;

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = HadithChapterActivity.class.getSimpleName();
    private ArrayList<HadithChapter> hadithChapters;
    private static final int TIME_INTERVAL = 2000;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    public static String URL;
    public HadithChapterViewAdapter rvAdapter;

    public static String host = "http://quran.codxplore.com/";
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_hadith_chapter);

        View rootView = findViewById(R.id.topAboutBar);
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            bookId = extras.getString("book_id");
            bookNameEn = extras.getString("name_en");
            bookNameAr = extras.getString("name_ar");
            bookNameBn = extras.getString("name_bn");
        }

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        TextView info_title = (TextView) findViewById(R.id.info_title);
        info_title.setText(bookNameEn);

        TextView info_sub_title = (TextView) findViewById(R.id.info_sub_title);
        info_sub_title.setText(bookNameBn);
        info_sub_title.setTypeface(font);

        recyclerview = (RecyclerView) findViewById(R.id.hadith_chapter_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                HadithChapter hb = hadithChapters.get(position);
                Intent in = new Intent(getApplicationContext(),HadithListActivity.class);
                in.putExtra("bid", hb.getBid());
                in.putExtra("name_en", hb.getName_english());
                in.putExtra("name_ar", hb.getName_arabic());
                in.putExtra("name_bn", hb.getName_bangla());
                in.putExtra("kitab_id", hb.getKitab_id());
                in.putExtra("reference_book", hb.getReference_book());
                startActivity(in);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        URL = host+"api/v1/app-book.php?id="+bookId;

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);
        String IS_HADITH_BOOK_JSON_DATA_STORED = mPrefs.getString("IS_HADITH_CHAPTER_JSON_DATA_STORED_"+bookId, "0");
        if(IS_HADITH_BOOK_JSON_DATA_STORED.equals("1")){
            String HADITH_CHAPTER_JSON_DATA_ = mPrefs.getString("HADITH_CHAPTER_JSON_DATA_"+bookId, "{}");
            Log.i(TAG, HADITH_CHAPTER_JSON_DATA_);
            parseJsonResponse(HADITH_CHAPTER_JSON_DATA_);
        }else {
            getDataFromInternet();
        }
        TextView search_hadith = (TextView) findViewById(R.id.search_hadith);
        search_hadith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HadithSearchActivity.class);
                startActivity(i);
            }
        });
    }

    private void setRecyclerViewAdapter() {
        hadithChapters = new ArrayList<HadithChapter>();
        rvAdapter = new HadithChapterViewAdapter(HadithChapterActivity.this, hadithChapters, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private void getDataFromInternet() {
        Log.i(TAG, URL);
        if (isInternetPresent) {
            new JsonFromUrlTask(this, URL, TAG, bookId);
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(HadithChapterActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    public void parseJsonResponse(String result) {
        //Log.i(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONObject json = response.getJSONObject("data");
            JSONArray jArray = new JSONArray(json.getString("bookList"));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //Log.i(TAG, jObject.toString());
                HadithChapter hc = new HadithChapter();
                hc.setBid(jObject.getString("bid"));
                hc.setName_english(jObject.getString("name_en"));
                hc.setName_arabic(jObject.getString("name_ar"));
                hc.setName_bangla(jObject.getString("name_bn"));
                hc.setReference_book(jObject.getString("reference_book"));
                hc.setKitab_id(jObject.getString("kitab_id"));
                hc.setName_slug(jObject.getString("name_slug"));
                hadithChapters.add(hc);
            }

            rvAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}