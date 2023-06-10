package com.akramhossain.quranulkarim.task;

import com.akramhossain.quranulkarim.MoreMenuActivity;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.app.AppController;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class CmsJsonFromUrlTask {
    private Activity activity;
    private String url;
    private final static String TAG = MoreMenuActivity.class.getSimpleName();
    ProgressBar progressBar;

    public CmsJsonFromUrlTask(Activity activity, String url) {
        super();
        this.activity = activity;
        this.url = url;
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        getData();
    }

    private void getData(){
        // Tag used to cancel the request
        String tag_string_req = "cms_api";
        progressBar.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Api Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    ((MoreMenuActivity) activity).parseJsonResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Api error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Api Error: " + error.getMessage());
                Toast.makeText(activity.getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {};

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
