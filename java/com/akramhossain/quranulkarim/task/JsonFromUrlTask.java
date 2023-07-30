package com.akramhossain.quranulkarim.task;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akramhossain.quranulkarim.HadithBookActivity;
import com.akramhossain.quranulkarim.R;
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

    public JsonFromUrlTask(Activity activity, String url, String TAG) {
        super();
        this.activity = activity;
        this.url = url;
        this.TAG = TAG;
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        getData();
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
                    ((HadithBookActivity) activity).parseJsonResponse(response);
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
        strReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
