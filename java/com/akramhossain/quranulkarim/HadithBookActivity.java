package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.HadithBookViewAdapter;
import com.akramhossain.quranulkarim.model.HadithBook;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HadithBookActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = HadithBookActivity.class.getSimpleName();
    private ArrayList<HadithBook> hadithBook;
    public HadithBookViewAdapter rvAdapter;
    private static final int TIME_INTERVAL = 2000;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    public static String URL;

    public static String host = "http://quran.codxplore.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hadith_book);

        recyclerview = (RecyclerView) findViewById(R.id.hadith_book_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        setRecyclerViewAdapter();

        URL = host+"api/v1/book-list.php";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        getDataFromInternet();

    }

    private void setRecyclerViewAdapter() {
        hadithBook = new ArrayList<HadithBook>();
        rvAdapter = new HadithBookViewAdapter(HadithBookActivity.this, hadithBook, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private void getDataFromInternet() {
        Log.i(TAG, URL);
        if (isInternetPresent) {
            new JsonFromUrlTask(this, URL, TAG);
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(HadithBookActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    public void parseJsonResponse(String result) {
        Log.i(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONArray jArray = new JSONArray(response.getString("data"));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                Log.i(TAG, jObject.toString());
                HadithBook hb = new HadithBook();
                hb.setBook_id(jObject.getString("id"));
                hb.setName_english(jObject.getString("name_en"));
                hb.setName_arabic(jObject.getString("name_ar"));
                hb.setName_bangla(jObject.getString("name_bn"));
                hadithBook.add(hb);
            }

            rvAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}