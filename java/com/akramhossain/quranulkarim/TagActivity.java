package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.akramhossain.quranulkarim.adapter.TagViewAdapter;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.Tag;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TagActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = TagActivity.class.getSimpleName();
    private ArrayList<Tag> tags;
    public TagViewAdapter rvAdapter;
    private static final int TIME_INTERVAL = 2000;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    public static String URL;

    public static String host = "http://quran.codxplore.com/";

    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        recyclerview = (RecyclerView) findViewById(R.id.dua_tags);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2));

        setRecyclerViewAdapter();

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Tag tg = tags.get(position);
                Intent in = new Intent(getApplicationContext(),DuaZikrActivity.class);
                in.putExtra("tag_en", tg.getTag_en());
                in.putExtra("tag_bn", tg.getTag_bn());
                startActivity(in);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        URL = host+"api/v1/app-dua-zikr-tags.php";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        String IS_DUA_TAG_JSON_DATA_STORED = mPrefs.getString("IS_DUA_TAG_JSON_DATA_STORED", "0");
        if(IS_DUA_TAG_JSON_DATA_STORED.equals("1")){
            String DUA_TAG_JSON_DATA = mPrefs.getString("DUA_TAG_JSON_DATA", "{}");
            Log.i(TAG, DUA_TAG_JSON_DATA);
            parseJsonResponse(DUA_TAG_JSON_DATA);
        }else {
            //FETCH DATA FROM REMOTE SERVER
            getDataFromInternet();
        }
    }

    private void setRecyclerViewAdapter() {
        tags = new ArrayList<Tag>();
        rvAdapter = new TagViewAdapter(TagActivity.this, tags, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private void getDataFromInternet() {
        Log.i(TAG, URL);
        if (isInternetPresent) {
            new JsonFromUrlTask(this, URL, TAG, "");
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(TagActivity.this);
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
                Tag tg = new Tag();
                tg.setTag_bn(jObject.getString("tag_bn"));
                tg.setTag_en(jObject.getString("tag_en"));
                tags.add(tg);
            }

            rvAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}