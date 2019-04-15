package com.akramhossain.quranulkarim;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.akramhossain.quranulkarim.adapter.RecyclerViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.Sura;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuraListV2Activity extends Activity {

    private RecyclerView recyclerview;
    private RecyclerViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Sura> suras;
    private static final String TAG = SuraListV2Activity.class.getSimpleName();
    DatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sura_list_v2);

        recyclerview = (RecyclerView) findViewById(R.id.all_sura_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        setRecyclerViewAdapter();

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Sura vd = suras.get(position);
                //Toast.makeText(getApplicationContext(), vd.getVideo_id() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(),SuraDetailsActivity.class);
                in.putExtra("sura_id", vd.getSurah_id());
                in.putExtra("sura_name", vd.getName_english());
                in.putExtra("sura_name_arabic", vd.getName_arabic());
                startActivityForResult(in, 100);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        dbhelper = new DatabaseHelper(getApplicationContext());

        //getSuraListFromFile();
        getDataFromLocalDb();
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM sura order by surah_id ASC";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Sura sura = new Sura();
                    sura.setSurah_id(cursor.getString(cursor.getColumnIndex("surah_id")));
                    sura.setName_arabic(cursor.getString(cursor.getColumnIndex("name_arabic")));
                    sura.setName_english(cursor.getString(cursor.getColumnIndex("name_english")));
                    sura.setName_simple(cursor.getString(cursor.getColumnIndex("name_simple")));
                    sura.setRevelation_place(cursor.getString(cursor.getColumnIndex("revelation_place")));
                    sura.setAyat(cursor.getString(cursor.getColumnIndex("ayat")));
                    sura.setRevelation_order(cursor.getString(cursor.getColumnIndex("revelation_order")));
                    sura.setId(cursor.getString(cursor.getColumnIndex("sid")));
                    suras.add(sura);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.i(TAG, e.getMessage());
        }
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
        rvAdapter.notifyDataSetChanged();
    }

    /*private void getSuraListFromFile() {
        new BrowseJsonFromFileTask(this).execute();
    }*/

    private void setRecyclerViewAdapter() {
        suras = new ArrayList<Sura>();
        rvAdapter = new RecyclerViewAdapter(SuraListV2Activity.this, suras);
        recyclerview.setAdapter(rvAdapter);
    }

    public void parseJsonResponse(String result) {
        Log.e(TAG, result);
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //featured videos
                Sura sura = new Sura();
                sura.setSurah_id(jObject.getString("surah_id"));
                sura.setName_arabic(jObject.getString("name_arabic"));
                sura.setName_english(jObject.getString("name_english"));
                sura.setName_simple(jObject.getString("name_simple"));
                sura.setRevelation_place(jObject.getString("revelation_place"));
                sura.setAyat(jObject.getString("ayat"));
                sura.setRevelation_order(jObject.getString("revelation_order"));
                sura.setId(jObject.getString("sid"));
                suras.add(sura);
            }
            rvAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
