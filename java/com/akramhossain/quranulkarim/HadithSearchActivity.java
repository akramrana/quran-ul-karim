package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.akramhossain.quranulkarim.adapter.HadithListViewAdapter;
import com.akramhossain.quranulkarim.model.HadithBook;
import com.akramhossain.quranulkarim.model.HadithList;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HadithSearchActivity extends AppCompatActivity {

    private static final String TAG = HadithSearchActivity.class.getSimpleName();
    public static String host = "http://quran.codxplore.com/";
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    public static String URL;
    public static String SEARCH_URL;
    Spinner spinner;
    ArrayAdapter<String> dataAdapter;
    List<String> items = new ArrayList<String>();
    List<String> itemIds = new ArrayList<String>();
    private ArrayList<HadithList> hadithLists;
    EditText hint;
    String search_term;
    String kitabId;
    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    public HadithListViewAdapter rvAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hadith_search);

        spinner = (Spinner) findViewById(R.id.kitab_spinner);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        URL = host+"api/v1/book-list.php";

        SEARCH_URL = host+"/api/v1/app-search-hadith.php?q=&kitab_id=&page=1&perPage=20";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        hint=(EditText) findViewById(R.id.hint);

        getDataFromInternet();

        Button btnSubmit = (Button) findViewById(R.id.search_hadith_btn);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_term = hint.getText().toString();
                int kitabNamePos = spinner.getSelectedItemPosition();
                kitabId = itemIds.get(kitabNamePos);
                if (search_term != null && !search_term.isEmpty()) {
                    hadithLists.clear();
                    getDataSearchDataFromInternet();
                }
            }
        });

        recyclerview = (RecyclerView) findViewById(R.id.hadith_listing);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();
    }

    private void setRecyclerViewAdapter() {
        hadithLists = new ArrayList<HadithList>();
        rvAdapter = new HadithListViewAdapter(HadithSearchActivity.this, hadithLists, this);
        recyclerview.setAdapter(rvAdapter);
    }

    public void getDataSearchDataFromInternet(){
        Log.i("kitab_id", kitabId);
        Log.i("search_term", search_term);
        SEARCH_URL = host+"/api/v1/app-search-hadith.php?q="+search_term+"&kitab_id="+kitabId+"&page=1&perPage=20";
        Log.i(TAG, SEARCH_URL);
        if (isInternetPresent) {
            new JsonFromUrlTask(this, SEARCH_URL, TAG);
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(HadithSearchActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    private void getDataFromInternet() {
        Log.i("HadithSearchActivityBookList", URL);
        if (isInternetPresent) {
            new JsonFromUrlTask(this, URL, "HadithSearchActivityBookList");
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(HadithSearchActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    public void parseJsonResponse(String result) {
        //Log.i(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONArray jArray = new JSONArray(response.getString("data"));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //Log.i(TAG, jObject.toString());
                items.add(jObject.getString("name_en"));
                itemIds.add(jObject.getString("id"));
            }

            spinner.setAdapter(dataAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseJsonResponseSearch(String result) {
        //Log.i(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONObject json = response.getJSONObject("data");
            JSONArray jArray = new JSONArray(json.getString("hadithList"));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //Log.i(TAG, jObject.toString());
                HadithList hc = new HadithList();
                hc.setHid(jObject.getString("hid"));
                hc.setHadithnumber(jObject.getString("hadithnumber"));
                hc.setArabicnumber(jObject.getString("arabicnumber"));
                hc.setGrades(jObject.getString("grades"));
                hc.setReference_book(jObject.getString("reference_book"));
                hc.setReference_hadith(jObject.getString("reference_hadith"));
                hc.setText_bn(jObject.getString("text_bn"));
                hc.setText_en(jObject.getString("text_en"));
                hc.setText_ar(jObject.getString("text_ar"));
                hc.setKitab_bn(jObject.getString("kitab_bn"));
                hc.setKitab_en(jObject.getString("kitab_en"));
                hc.setKitab_ar(jObject.getString("kitab_ar"));
                hc.setBook_name_bn(jObject.getString("book_name_bn"));
                hc.setBook_name_en(jObject.getString("book_name_en"));
                hc.setBook_name_ar(jObject.getString("book_name_ar"));
                hadithLists.add(hc);
            }

            rvAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}