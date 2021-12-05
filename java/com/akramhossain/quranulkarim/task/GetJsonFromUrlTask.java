package com.akramhossain.quranulkarim.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.SuraDetailsActivity;

public class GetJsonFromUrlTask extends AsyncTask<Void, Void, String>{

    private Activity activity;
    private String url;
    private ProgressDialog dialog;
    private final static String TAG = GetJsonFromUrlTask.class.getSimpleName();
    ProgressBar progressBar;

    public GetJsonFromUrlTask(Activity activity, String url) {
        super();
        this.activity = activity;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        // call load JSON from url method
        JSONObject json = loadJSON(this.url);
        if(json!= null && json.length()==0){
            return null;
        }else {
            return json.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        ((SuraDetailsActivity) activity).parseJsonResponse(result);
        //dialog.dismiss();
        //Log.i(TAG, result);
    }

    public JSONObject loadJSON(String url) {
        //Log.e("Remote Url", url.toString());
        // Creating JSON Parser instance
        JSONGetter jParser = new JSONGetter();
        // getting JSON string from URL
        JSONObject json = jParser.getJSONFromUrl(url);
        return json;
    }

    private class JSONGetter {
        private InputStream is = null;
        private JSONObject jObj = null;
        private String json = "";
        HttpURLConnection urlConnection = null;
        // constructor
        public JSONGetter() {

        }
        public JSONObject getJSONFromUrl(String url) {
            // Making HTTP request
            try {
                URL remoteUrl = new URL(url);
                urlConnection = (HttpURLConnection) remoteUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int lengthOfFile = urlConnection.getContentLength();
                // Read the input stream into a String
                is = urlConnection.getInputStream();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            // return JSON String
            return jObj;
        }
    }
}
