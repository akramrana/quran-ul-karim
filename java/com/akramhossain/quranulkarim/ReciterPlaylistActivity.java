package com.akramhossain.quranulkarim;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.AudioAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.model.AudioItem;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;
import com.akramhossain.quranulkarim.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReciterPlaylistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AudioAdapter adapter;
    private final List<AudioItem> items = new ArrayList<>();
    private static final String TAG = ReciterPlaylistActivity.class.getSimpleName();
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    public static String URL;
    String reciter_name;
    String relative_path;
    String qari_id, qariId;
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_reciter_playlist);

        View rootView = findViewById(R.id.topAboutBar);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to avoid overlap with status/navigation bars
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    view.getPaddingBottom()
            );
            return insets;
        });

        View bottomBar = findViewById(R.id.recyclerView);
        ViewCompat.setOnApplyWindowInsetsListener(bottomBar, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    bottomInset
            );
            return insets;
        });

        reciter_name = getIntent().getStringExtra("reciter_name");
        relative_path = getIntent().getStringExtra("relative_path");
        qari_id = getIntent().getStringExtra("qari_id");

        TextView recit_title = findViewById(R.id.recit_title);
        recit_title.setText(reciter_name);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AudioAdapter(this, items);
        recyclerView.setAdapter(adapter);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        qariId = getIntent().getStringExtra("qari_id");

        URL = "https://quranicaudio.com/api/qaris/"+qariId+"/audio_files/mp3";

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);
        String is_playlist_exist = mPrefs.getString("is_playlist_exist_"+qariId, "0");
        if(is_playlist_exist.equals("1")){
            String playlist_json_data = mPrefs.getString("playlist_json_data_"+qariId, "{}");
            Log.i(TAG, playlist_json_data);
            parseJsonResponse(playlist_json_data);
        }else {
            getDataFromInternet();
        }
    }

    private void getDataFromInternet() {
        Log.i(TAG, URL);
        if (isInternetPresent) {
            new JsonFromUrlTask(this, URL, TAG, qariId);
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(ReciterPlaylistActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    public void parseJsonResponse(String result) {
        try {
            List<AudioItem> list = new ArrayList<>();
            JSONArray arr = new JSONArray(result);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONObject meta = obj.optJSONObject("metadata");
                JSONObject fmt = obj.optJSONObject("format");

                AudioItem ai = new AudioItem();
                ai.fileName = obj.optString("file_name", "");
                ai.title = meta != null ? meta.optString("title", ai.fileName) : ai.fileName;
                ai.artist = meta != null ? meta.optString("artist", "") : "";
                ai.duration = fmt != null ? fmt.optString("duration", "") : "";
                ai.url = "https://download.quranicaudio.com/quran/"+relative_path + ai.fileName;
                ai.qariId = reciter_name.toLowerCase().replaceAll("[^a-z0-9 ]", "").replaceAll("\\s+", "_") ;
                list.add(ai);
            }

            items.clear();
            items.addAll(list);
            adapter.notifyDataSetChanged();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause()
    {
        super.onPause();
        if (adapter != null) adapter.release();
    }

    @Override protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.release();
    }
}