package com.akramhossain.quranulkarim;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.akramhossain.quranulkarim.adapter.SectionedAdapter;
import com.akramhossain.quranulkarim.model.Reciter;
import com.akramhossain.quranulkarim.model.SectionItem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReciterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_reciter);

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

        String json = loadRecitersFromAssets();
        Log.d("JSON", json.toString());
        List<Reciter> reciters = parseReciters(json);

        Map<Character, List<Reciter>> grouped = groupByAlphabet(reciters);

        List<SectionItem> sectioned = new ArrayList<>();
        for (Map.Entry<Character, List<Reciter>> entry : grouped.entrySet()) {
            sectioned.add(SectionItem.header(entry.getKey()));
            for (Reciter r : entry.getValue()) {
                sectioned.add(SectionItem.reciter(r));
            }
        }

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new SectionedAdapter(sectioned, r -> {
            // TODO: handle click (open details, start playback, etc.)
            //Toast.makeText(this, r.name, Toast.LENGTH_SHORT).show();
            Intent in = new Intent(getApplicationContext(), ReciterPlaylistActivity.class);
            in.putExtra("reciter_name", r.name);
            in.putExtra("relative_path", r.relative_path);
            in.putExtra("qari_id",r.id);
            startActivity(in);
        }));
    }

    private String loadRecitersFromAssets() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("qaris.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private List<Reciter> parseReciters(String json) {
        List<Reciter> reciters = new ArrayList<>();
        if (json == null) return reciters;
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                String name = obj.optString("name", "");
                String arabic = obj.optString("arabic_name", "");
                String path = obj.optString("relative_path", "");
                String id = obj.optString("id", "");

                reciters.add(new Reciter(name, arabic, path, id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reciters;
    }

    private Map<Character, List<Reciter>> groupByAlphabet(List<Reciter> reciters) {
        Map<Character, List<Reciter>> grouped = new TreeMap<>();
        for (Reciter r : reciters) {
            if (r.name == null || r.name.isEmpty()) continue;
            char first = Character.toUpperCase(r.name.charAt(0));
            if (!Character.isLetter(first)) first = '#'; // fallback for non-letters
            if (!grouped.containsKey(first)) grouped.put(first, new ArrayList<>());
            grouped.get(first).add(r);
        }
        // sort names inside each group
        for (List<Reciter> list : grouped.values()) {
            Collections.sort(list, (a, b) -> a.name.compareToIgnoreCase(b.name));
        }
        return grouped;
    }


}