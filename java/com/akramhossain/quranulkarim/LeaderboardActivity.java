package com.akramhossain.quranulkarim;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.LeaderboardViewAdapter;
import com.akramhossain.quranulkarim.app.AppController;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.helper.SessionManager;
import com.akramhossain.quranulkarim.model.Leaderboard;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardActivity extends AppCompatActivity {

    private static final String TAG = LeaderboardActivity.class.getSimpleName();
    public static String URL;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Leaderboard> leaderboards;
    public LeaderboardViewAdapter rvAdapter;
    public static String host = "http://quran.codxplore.com/";
    ProgressBar progressBar;
    private SessionManager session;
    public static String SYNC_URL;
    public String user_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerview = (RecyclerView) findViewById(R.id.leaderboard_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        setRecyclerViewAdapter();

        URL = host+"api/v1/leaderboard.php";
        SYNC_URL = host+"api/v1/sync.php";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        Button sign_in_button = (Button) findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout logged_user_sec = (LinearLayout) findViewById(R.id.logged_user_sec);

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            sign_in_button.setVisibility(View.GONE);
            logged_user_sec.setVisibility(View.VISIBLE);
            String userJson = session.getLoginData();
            try {
                JSONObject response = new JSONObject(userJson);
                user_id = response.getString("user_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            sign_in_button.setVisibility(View.VISIBLE);
            logged_user_sec.setVisibility(View.GONE);
        }

        getDataFromInternet();

        Button btn_sign_out = (Button) findViewById(R.id.btn_sign_out);
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.setLogin(false);
                session.setLoginData("");

                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        Button btn_publish_data = (Button) findViewById(R.id.btn_publish_data);
        btn_publish_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.isLoggedIn()) {
                    SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    String sql = "SELECT * FROM word_answers WHERE is_sync = 0";
                    Cursor cursor = db.rawQuery(sql, null);
                    try {
                        if (cursor.moveToFirst()) {
                            do {
                                String word_id = cursor.getString(cursor.getColumnIndexOrThrow("word_id"));
                                String datetime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
                                String is_right_answer = cursor.getString(cursor.getColumnIndexOrThrow("is_right_answer"));
                                pushAnswerData(word_id, datetime, is_right_answer);
                            } while (cursor.moveToNext());
                        }
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    } finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                        db.close();
                    }
                }
            }
        });

    }

    private void getDataFromInternet() {
        if (isInternetPresent) {
            String remoteUrl = URL+"?user_id="+user_id;
            Log.i(TAG, remoteUrl.toString());
            new JsonFromUrlTask(this, remoteUrl, TAG, "");
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(LeaderboardActivity.this);
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
            JSONArray jArray = new JSONArray(json.getString("list"));
            Log.i(TAG, jArray.toString());
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                Leaderboard ma = new Leaderboard();
                ma.setUser_id(jObject.getString("user_id"));
                ma.setName(jObject.getString("name"));
                ma.setCountry_id(jObject.getString("country_id"));
                ma.setIso(jObject.getString("iso"));
                ma.setIso3(jObject.getString("iso3"));
                ma.setRight_ans(jObject.getString("right_ans"));
                ma.setWrong_ans(jObject.getString("wrong_ans"));
                ma.setTotal_ans(jObject.getString("total_ans"));
                ma.setRank(jObject.getString("rank"));
                ma.setFlag(jObject.getString("flag"));
                leaderboards.add(ma);
            }
            rvAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRecyclerViewAdapter() {
        leaderboards = new ArrayList<Leaderboard>();
        rvAdapter = new LeaderboardViewAdapter(LeaderboardActivity.this, leaderboards, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private void pushAnswerData(final String word_id, final String datetime,final String is_right_answer){
        String tag_string_req = "req_sync_data";
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Sync URL: " + SYNC_URL.toString());
        StringRequest strReq = new StringRequest(Request.Method.POST, SYNC_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Sync Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in database
                        Toast.makeText(getApplicationContext(), "User data successfully sync", Toast.LENGTH_LONG).show();
                        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                        String sql = "SELECT * FROM word_answers WHERE word_id = "+word_id;
                        Cursor cursor = db.rawQuery(sql, null);
                        try {
                            if (cursor.moveToFirst()) {
                                ContentValues values = new ContentValues();
                                values.put("is_sync", 1);
                                DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase().update("word_answers", values, "word_id = ?", new String[]{word_id});
                            }
                        }catch (Exception e) {
                            Log.i("SYNC ERROR SQL", e.getMessage());
                        } finally {
                            if (cursor != null && !cursor.isClosed()) {
                                cursor.close();
                            }
                            db.close();
                        }
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Sync Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("word_id", word_id);
                params.put("datetime", datetime);
                params.put("is_right_answer", is_right_answer);
                params.put("user_id",user_id);
                return params;
            }
        };
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}