package com.akramhossain.quranulkarim.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.SuraListV2Activity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by akram on 3/30/2019.
 */

public class BrowseJsonFromFileTask extends AsyncTask<Void, Void, String> {

    private Activity activity;
    private final static String TAG = BrowseJsonFromFileTask.class.getSimpleName();
    ProgressBar progressBar;

    public BrowseJsonFromFileTask(Activity activity) {
        super();
        this.activity = activity;
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(Void... params) {
        // call load JSON from url method
        return parseJSON();
    }

    @Override
    protected void onPostExecute(String result) {
        ((SuraListV2Activity) activity).parseJsonResponse(result);
        //dialog.dismiss();
        progressBar.setVisibility(View.GONE);
    }

    public String parseJSON() {
        JSONGetter jParser = new JSONGetter();
        String json = jParser.getJSONFromFile();
        return json;
    }

    private class JSONGetter {

        private InputStream is = null;
        private JSONObject jObj = null;
        private String json = "";
        // constructor
        public JSONGetter() {

        }

        public String getJSONFromFile() {
            StringBuffer sb = new StringBuffer();

            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(activity.getAssets().open(
                        "surahs.json")));
                String temp;
                while ((temp = br.readLine()) != null) {
                    sb.append(temp);
                }
                json = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close(); // stop reading
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return json;
        }
    }
}
