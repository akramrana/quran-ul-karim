package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.DuaZikrViewAdapter;
import com.akramhossain.quranulkarim.model.DuaZikr;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DuaZikrActivity extends AppCompatActivity {

    public static String tagEn;
    public static String tagBn;
    TextView info_title,info_sub_title;
    Typeface font;
    private static final String TAG = DuaZikrActivity.class.getSimpleName();
    private static final int TIME_INTERVAL = 2000;
    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    public static String host = "http://quran.codxplore.com";
    Boolean isInternetPresent = false;
    public static String URL;
    ConnectionDetector cd;
    private ArrayList<DuaZikr> duaZikr;
    public DuaZikrViewAdapter rvAdapter;
    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dua_zikr);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tagEn = extras.getString("tag_en");
            tagBn = extras.getString("tag_bn");
        }
        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");
        //
        info_title = (TextView) findViewById(R.id.info_title);
        info_title.setText(tagEn);
        info_sub_title = (TextView) findViewById(R.id.info_sub_title);
        info_sub_title.setTypeface(font);
        info_sub_title.setText(tagBn);
        //
        recyclerview = (RecyclerView) findViewById(R.id.dua_zikr);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        setRecyclerViewAdapter();
        //
        URL = host+"/api/v1/app-dua-zikr-list.php?q="+tagEn;
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String IS_DUA_ZIKR_JSON_DATA_STORED = mPrefs.getString("IS_DUA_ZIKR_JSON_DATA_STORED_"+tagEn, "0");
        if(IS_DUA_ZIKR_JSON_DATA_STORED.equals("1")){
            String DUA_ZIKR_JSON_DATA = mPrefs.getString("DUA_ZIKR_JSON_DATA_"+tagEn, "{}");
            Log.i(TAG, DUA_ZIKR_JSON_DATA);
            parseJsonResponse(DUA_ZIKR_JSON_DATA);
        }else {
            getDataFromInternet();
        }
    }

    private void setRecyclerViewAdapter() {
        duaZikr = new ArrayList<DuaZikr>();
        rvAdapter = new DuaZikrViewAdapter(DuaZikrActivity.this, duaZikr, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private void getDataFromInternet() {
        Log.i(TAG, URL);
        if (isInternetPresent) {
            new JsonFromUrlTask(this, URL, TAG, tagEn);
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(DuaZikrActivity.this);
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
            JSONArray jArray = new JSONArray(response.getString("list"));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //Log.i(TAG, jObject.toString());
                DuaZikr dz = new DuaZikr();
                dz.setDua_zikr_id(jObject.getString("dua_zikr_id"));
                dz.setArabic(jObject.getString("arabic"));

                dz.setTransliteration_en(jObject.getString("transliteration_en"));
                dz.setTransliteration_bn(jObject.getString("transliteration_bn"));

                dz.setTranslations_en(jObject.getString("translations_en"));
                dz.setTranslations_bn(jObject.getString("translations_bn"));

                dz.setReference_en(jObject.getString("reference_en"));
                dz.setReference_bn(jObject.getString("reference_bn"));

                dz.setRepeat_times(jObject.getString("repeat_times"));

                dz.setWhen_to_en(jObject.getString("when_to_en"));
                dz.setWhen_to_bn(jObject.getString("when_to_bn"));

                dz.setName_en(jObject.getString("name_en"));
                dz.setName_bn(jObject.getString("name_bn"));

                dz.setTag_en(jObject.getString("tag_en"));
                dz.setTag_bn(jObject.getString("tag_bn"));
                duaZikr.add(dz);
            }

            rvAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}