package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.MobileAppViewAdapter;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.MobileApp;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;
import com.akramhossain.quranulkarim.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static com.akramhossain.quranulkarim.BugReportActivity.host;

public class AboutActivity extends AppCompatActivity {

    public static String URL;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    private static final String TAG = AboutActivity.class.getSimpleName();
    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private ArrayList<MobileApp> mobileApps;
    public MobileAppViewAdapter rvAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_about);

        View topBar = findViewById(R.id.topAboutBar);
        View scrollView = findViewById(R.id.about_scroll);

        int headerTopPadding = topBar.getPaddingTop();
        int scrollBottomPadding = scrollView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(topBar, (view, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    view.getPaddingLeft(),
                    headerTopPadding + bars.top,
                    view.getPaddingRight(),
                    view.getPaddingBottom()
            );

            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (view, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    scrollBottomPadding + bars.bottom
            );

            return insets;
        });

        setTitle("About");
        //
        recyclerview = (RecyclerView) findViewById(R.id.mobile_app_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setHasFixedSize(false);
        setRecyclerViewAdapter();

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MobileApp ma = mobileApps.get(position);
                Toast.makeText(getApplicationContext(), ma.getName(), Toast.LENGTH_SHORT).show();
                if(Utils.isHuaweiDevice()){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ma.getHuawei_url())));
                }else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ma.getUrl())));
                }
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        //
        URL = host+"api/mobile-app-list";
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        getDataFromInternet();

        TextView quran_online = (TextView) findViewById(R.id.quran_online);
        quran_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://quran.codxplore.com")));
            }
        });

        TextView tajweed_text = (TextView) findViewById(R.id.tajweed_text);
        tajweed_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://alquran.cloud/tajweed-guide")));
            }
        });


        findViewById(R.id.share_app).setOnClickListener(v -> {
            String appUrl = "https://play.google.com/store/apps/details?id="
                    + getPackageName();

            String message = getString(
                    R.string.about_share_message,
                    appUrl
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);

            startActivity(Intent.createChooser(
                    shareIntent,
                    getString(R.string.about_share_app)
            ));
        });

        findViewById(R.id.rate_app).setOnClickListener(
            v -> openRateApp()
        );

    }

    private void getDataFromInternet() {
        if (isInternetPresent) {
            new JsonFromUrlTask(this, URL, TAG, "");
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(AboutActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    public void parseJsonResponseSearch(String result) {
        Log.i(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONObject json = response.getJSONObject("data");
            JSONArray jArray = new JSONArray(json.getString("appList"));
            Log.i(TAG, jArray.toString());
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                MobileApp ma = new MobileApp();
                ma.setId(jObject.getString("id"));
                ma.setName(jObject.getString("name"));
                ma.setUrl(jObject.getString("url"));
                ma.setImg(jObject.getString("img"));
                ma.setHuawei_url(jObject.getString("huawei_url"));
                mobileApps.add(ma);
            }
            rvAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRecyclerViewAdapter() {
        mobileApps = new ArrayList<MobileApp>();
        rvAdapter = new MobileAppViewAdapter(AboutActivity.this, mobileApps, this);
        recyclerview.setAdapter(rvAdapter);
    }


    private void openRateApp() {
        String packageName = getPackageName();

        if (Utils.isHuaweiDevice()) {
            // Huawei AppGallery
            try {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("appmarket://details?id=" + packageName)
                );

                intent.setPackage("com.huawei.appmarket");
                startActivity(intent);
                return;

            } catch (ActivityNotFoundException e) {
                // AppGallery unavailable, try its website
                openStoreWebsite(
                        "https://appgallery.huawei.com/#/app/"
                );
                return;
            }
        }

        // Google Play Store
        try {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)
            );

            intent.setPackage("com.android.vending");
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            // Google Play unavailable, open website
            openStoreWebsite(
                    "https://play.google.com/store/apps/details?id="
                            + packageName
            );
        }
    }

    private void openStoreWebsite(String url) {
        try {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
            );

            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(
                    this,
                    "No browser available to open the store.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


}
