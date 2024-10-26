package com.akramhossain.quranulkarim;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import com.akramhossain.quranulkarim.adapter.LeaderboardViewAdapter;
import com.akramhossain.quranulkarim.model.Leaderboard;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerview = (RecyclerView) findViewById(R.id.leaderboard_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        setRecyclerViewAdapter();

        URL = host+"api/v1/leaderboard.php";
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        getDataFromInternet();
    }

    private void getDataFromInternet() {
        if (isInternetPresent) {
            new JsonFromUrlTask(this, URL, TAG, "");
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
            JSONArray jArray = new JSONArray(response.getString("list"));
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
}