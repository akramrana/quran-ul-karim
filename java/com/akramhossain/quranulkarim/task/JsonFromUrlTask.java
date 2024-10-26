package com.akramhossain.quranulkarim.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akramhossain.quranulkarim.AboutActivity;
import com.akramhossain.quranulkarim.DuaZikrActivity;
import com.akramhossain.quranulkarim.HadithBookActivity;
import com.akramhossain.quranulkarim.HadithChapterActivity;
import com.akramhossain.quranulkarim.HadithListActivity;
import com.akramhossain.quranulkarim.HadithSearchActivity;
import com.akramhossain.quranulkarim.LeaderboardActivity;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.TagActivity;
import com.akramhossain.quranulkarim.app.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class JsonFromUrlTask {

    private Activity activity;
    private String url;
    private String TAG;
    ProgressBar progressBar;
    SharedPreferences mPrefs;
    private String sharedPref;

    public JsonFromUrlTask(Activity activity, String url, String TAG, String sharedPref) {
        super();
        this.activity = activity;
        this.url = url;
        this.TAG = TAG;
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        getData();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        this.sharedPref = sharedPref;
    }

    private void getData(){
        // Tag used to cancel the request
        String tag_string_req = "book_list_api";
        progressBar.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Api Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    if(TAG.equals("HadithBookActivity")) {
                        ((HadithBookActivity) activity).parseJsonResponse(response);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString("HADITH_BOOK_JSON_DATA", response.toString());
                        editor.putString("IS_HADITH_BOOK_JSON_DATA_STORED", "1");
                        editor.apply();
                    }
                    else if(TAG.equals("HadithChapterActivity")){
                        ((HadithChapterActivity) activity).parseJsonResponse(response);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString("HADITH_CHAPTER_JSON_DATA_"+sharedPref, response.toString());
                        editor.putString("IS_HADITH_CHAPTER_JSON_DATA_STORED_"+sharedPref, "1");
                        editor.apply();
                    }
                    else if(TAG.equals("HadithListActivity")){
                        ((HadithListActivity) activity).parseJsonResponse(response);
                    }
                    else if(TAG.equals("HadithSearchActivityBookList")){
                        ((HadithSearchActivity) activity).parseJsonResponse(response);
                    }
                    else if(TAG.equals("HadithSearchActivity")){
                        ((HadithSearchActivity) activity).parseJsonResponseSearch(response);
                    }
                    else if(TAG.equals("AboutActivity")){
                        ((AboutActivity) activity).parseJsonResponseSearch(response);
                    }
                    else if(TAG.equals("TagActivity")){
                        ((TagActivity) activity).parseJsonResponse(response);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString("DUA_TAG_JSON_DATA", response.toString());
                        editor.putString("IS_DUA_TAG_JSON_DATA_STORED", "1");
                        editor.apply();
                    }
                    else if(TAG.equals("DuaZikrActivity")){
                        ((DuaZikrActivity) activity).parseJsonResponse(response);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString("DUA_ZIKR_JSON_DATA_"+sharedPref, response.toString());
                        editor.putString("IS_DUA_ZIKR_JSON_DATA_STORED_"+sharedPref, "1");
                        editor.apply();
                    }
                    else if(TAG.equals("LeaderboardActivity")){
                        ((LeaderboardActivity) activity).parseJsonResponseSearch(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Api error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Api Error: " + error);
                Toast.makeText(activity.getApplicationContext(), "Something went wrong, please try again after sometime", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {};

        // Adding request to request queue
        //strReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
