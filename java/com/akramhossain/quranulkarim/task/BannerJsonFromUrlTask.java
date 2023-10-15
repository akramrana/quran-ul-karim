package com.akramhossain.quranulkarim.task;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akramhossain.quranulkarim.MainActivity;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.app.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class BannerJsonFromUrlTask {
    private Activity activity;
    private String url;
    private final static String TAG = MainActivity.class.getSimpleName();

    public BannerJsonFromUrlTask(Activity activity, String url) {
        super();
        this.activity = activity;
        this.url = url;
        getData();
    }

    private void getData(){
        // Tag used to cancel the request
        String tag_string_req = "banner_api";
        StringRequest strReq = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Api Response: " + response.toString());
                try {
                    ((MainActivity) activity).parseJsonResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Api error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Api Error: " + error);
            }
        }) {};

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
