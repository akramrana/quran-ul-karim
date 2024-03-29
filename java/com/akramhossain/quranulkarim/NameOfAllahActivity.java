package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.akramhossain.quranulkarim.adapter.NameOfAllahViewAdapter;
import com.akramhossain.quranulkarim.model.HadithBook;
import com.akramhossain.quranulkarim.model.NameOfAllah;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class NameOfAllahActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = NameOfAllahActivity.class.getSimpleName();
    private ArrayList<NameOfAllah> nameOfAllah;
    public NameOfAllahViewAdapter rvAdapter;
    private static final int TIME_INTERVAL = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_of_allah);

        recyclerview = (RecyclerView) findViewById(R.id.name_of_allah_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        getDataFromJson();
    }

    private void setRecyclerViewAdapter() {
        nameOfAllah = new ArrayList<NameOfAllah>();
        rvAdapter = new NameOfAllahViewAdapter(NameOfAllahActivity.this, nameOfAllah, this);
        recyclerview.setAdapter(rvAdapter);
    }

    public void getDataFromJson() {
        try {
            JSONObject response = new JSONObject(loadJSONFromAsset());
            //Log.i(TAG, response.toString());
            JSONArray jArray = new JSONArray(response.getString("data"));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //Log.i(TAG, jObject.toString());
                //
                JSONObject bn = jObject.getJSONObject("bn");
                //
                NameOfAllah noa = new NameOfAllah();
                noa.setName(jObject.getString("name"));
                noa.setTransliteration(jObject.getString("number")+"."+jObject.getString("transliteration"));
                noa.setFound("Found In Quraan Ayah(s): "+jObject.getString("found"));
                //
                JSONObject en = jObject.getJSONObject("en");
                noa.setEn_meaning(en.getString("meaning"));
                noa.setEn_desc(en.getString("desc"));
                noa.setBn_meaning(bn.getString("meaning"));
                noa.setBn_desc(bn.getString("transliteration"));
                //
                nameOfAllah.add(noa);
            }

            rvAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("99_Names_Of_Allah.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}