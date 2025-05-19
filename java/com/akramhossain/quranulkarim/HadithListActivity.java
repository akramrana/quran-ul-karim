package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.HadithListViewAdapter;
import com.akramhossain.quranulkarim.model.HadithList;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HadithListActivity extends AppCompatActivity {

    public static String bid;
    public static String bookNameEn;
    public static String bookNameAr;
    public static String bookNameBn;
    public static String kitabId;
    public static String referenceBook;
    TextView info_title,info_sub_title;
    Typeface font;
    public static String host = "http://quran.codxplore.com/";
    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = HadithListActivity.class.getSimpleName();
    private static final int TIME_INTERVAL = 2000;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    public static String URL;
    public String page = "1";
    public HadithListViewAdapter rvAdapter;
    public int Counter = 1;
    public String maxPage;
    private ArrayList<HadithList> hadithLists;
    private boolean itShouldLoadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_hadith_list);

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
            bid = extras.getString("bid");
            bookNameEn = extras.getString("name_en");
            bookNameAr = extras.getString("name_ar");
            bookNameBn = extras.getString("name_bn");
            kitabId = extras.getString("kitab_id");
            referenceBook = extras.getString("reference_book");
        }
        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        info_title = (TextView) findViewById(R.id.info_title);
        info_sub_title = (TextView) findViewById(R.id.info_sub_title);
        info_sub_title.setTypeface(font);

        recyclerview = (RecyclerView) findViewById(R.id.hadith_listing);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        URL = host+"/api/v1/app-hadith-list.php?kitab_id="+kitabId+"&book_id="+referenceBook+"&bookInfo=1&page="+page+"&perPage=10";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        getDataFromInternet();

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
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            //loadMore();
                            int counter = Integer.parseInt(page);
                            int maxPageCount = Integer.parseInt(maxPage);
                            if(counter < maxPageCount) {
                                counter = (counter + 1);
                                page = Integer.toString(counter);
                                URL = host+"/api/v1/app-hadith-list.php?kitab_id="+kitabId+"&book_id="+referenceBook+"&bookInfo=1&page="+page+"&perPage=10";
                                getDataFromInternet();
                            }
                        }
                    }
                }
            }

        });
    }

    private void setRecyclerViewAdapter() {
        hadithLists = new ArrayList<HadithList>();
        rvAdapter = new HadithListViewAdapter(HadithListActivity.this, hadithLists, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private void getDataFromInternet() {
        Log.i(TAG, URL);
        if (isInternetPresent) {
            new JsonFromUrlTask(this, URL, TAG, "");
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(HadithListActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    public void parseJsonResponse(String result) {
        Log.i(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONObject json = response.getJSONObject("data");
            JSONArray jArray = new JSONArray(json.getString("hadithList"));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //Log.i(TAG, jObject.toString());
                HadithList hc = new HadithList();
                hc.setHid(jObject.getString("hid"));
                hc.setHadithnumber(jObject.getString("hadithnumber"));
                hc.setArabicnumber(jObject.getString("arabicnumber"));
                hc.setGrades(jObject.getString("grades"));
                hc.setReference_book(jObject.getString("reference_book"));
                hc.setReference_hadith(jObject.getString("reference_hadith"));
                hc.setText_bn(jObject.getString("text_bn"));
                hc.setText_en(jObject.getString("text_en"));
                hc.setText_ar(jObject.getString("text_ar"));
                hc.setKitab_bn(jObject.getString("kitab_bn"));
                hc.setKitab_en(jObject.getString("kitab_en"));
                hc.setKitab_ar(jObject.getString("kitab_ar"));
                hc.setBook_name_bn(jObject.getString("book_name_bn"));
                hc.setBook_name_en(jObject.getString("book_name_en"));
                hc.setBook_name_ar(jObject.getString("book_name_ar"));
                hadithLists.add(hc);
            }

            rvAdapter.notifyDataSetChanged();

            JSONObject bookInfo = json.getJSONObject("bookInfo");
            info_title.setText(bookInfo.getString("kitab_name_en")+": "+bookInfo.getString("name_en"));
            info_sub_title.setText(bookInfo.getString("kitab_name_bn")+": "+bookInfo.getString("name_bn"));

            itShouldLoadMore = true;
            maxPage = json.getString("total_pages");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}