package com.akramhossain.quranulkarim;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;

import com.akramhossain.quranulkarim.adapter.RecyclerViewAdapter;
import com.akramhossain.quranulkarim.adapter.WordListViewAdapter;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.Sura;
import com.akramhossain.quranulkarim.model.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuraListV2Activity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private RecyclerView recyclerview;
    private RecyclerViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<Sura> suras;
    private static final String TAG = SuraListV2Activity.class.getSimpleName();
    DatabaseHelper dbhelper;
    SearchView editsearch;
    String searchTxt = "";
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_sura_list_v2);

        recyclerview = (RecyclerView) findViewById(R.id.all_sura_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        setRecyclerViewAdapter();

        /*recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Sura vd = suras.get(position);
                Intent in = new Intent(getApplicationContext(),SuraDetailsActivity.class);
                in.putExtra("sura_id", vd.getSurah_id());
                in.putExtra("sura_name", vd.getName_english());
                in.putExtra("sura_name_arabic", vd.getName_arabic());
                startActivityForResult(in, 100);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        //getSuraListFromFile();
        getDataFromLocalDb();

        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "";
        searchTxt = searchTxt.replaceAll("\'","");
        if(searchTxt.equals("")) {
            sql = "SELECT sura.*,bangla_name.name_bangla " +
                    "FROM sura " +
                    "left join bangla_name on sura.surah_id = bangla_name.surah_id " +
                    "order by surah_id ASC";
        }else{
            sql = "SELECT sura.*,bangla_name.name_bangla " +
                    "FROM sura " +
                    "left join bangla_name on sura.surah_id = bangla_name.surah_id " +
                    "WHERE name_complex LIKE '%"+searchTxt+"%' OR name_simple LIKE '%"+searchTxt+"%' OR name_english LIKE '%"+searchTxt+"%' OR name_arabic LIKE '%"+searchTxt+"%' OR bangla_name.name_bangla LIKE '%"+searchTxt+"%' " +
                    "order by surah_id ASC";
        }
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Sura sura = new Sura();
                    sura.setSurah_id(cursor.getString(cursor.getColumnIndexOrThrow("surah_id")));
                    sura.setName_arabic(cursor.getString(cursor.getColumnIndexOrThrow("name_arabic")));
                    sura.setName_english(cursor.getString(cursor.getColumnIndexOrThrow("name_english")));
                    sura.setName_simple(cursor.getString(cursor.getColumnIndexOrThrow("name_simple")));
                    sura.setRevelation_place(cursor.getString(cursor.getColumnIndexOrThrow("revelation_place")));
                    sura.setAyat(cursor.getString(cursor.getColumnIndexOrThrow("ayat")));
                    sura.setRevelation_order(cursor.getString(cursor.getColumnIndexOrThrow("revelation_order")));
                    sura.setId(cursor.getString(cursor.getColumnIndexOrThrow("sid")));
                    sura.setName_bangla(cursor.getString(cursor.getColumnIndexOrThrow("name_bangla")));
                    suras.add(sura);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.i(TAG, e.getMessage());
            throw new RuntimeException("SQL Query: " + sql, e);
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
        rvAdapter = new RecyclerViewAdapter(SuraListV2Activity.this, suras, this);
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
                sura.setName_bangla(jObject.getString("name_bangla"));
                suras.add(sura);
            }
            rvAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchTxt = query;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int length = searchTxt.length();
                if(length > 0) {
                    suras = new ArrayList<Sura>();
                    rvAdapter = new RecyclerViewAdapter(SuraListV2Activity.this, suras, SuraListV2Activity.this);
                    recyclerview.setAdapter(rvAdapter);
                    getDataFromLocalDb();
                }
                else{
                    suras = new ArrayList<Sura>();
                    rvAdapter = new RecyclerViewAdapter(SuraListV2Activity.this, suras, SuraListV2Activity.this);
                    recyclerview.setAdapter(rvAdapter);
                    getDataFromLocalDb();
                }
            }
        }, 100);


        return true;
    }

    public void onPause()
    {
        super.onPause();
        AudioPlay.stopAudio();
    }

}
