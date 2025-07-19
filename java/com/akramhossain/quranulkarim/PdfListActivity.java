package com.akramhossain.quranulkarim;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.PdfBookViewAdapter;
import com.akramhossain.quranulkarim.model.TafsirPdfList;
import com.akramhossain.quranulkarim.task.JsonFromUrlTask;
import com.akramhossain.quranulkarim.util.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PdfListActivity extends AppCompatActivity {

    public String tafsir_book_id;
    public String name_en;
    public String name_ar;
    public String name_bn;
    public String thumb;
    public String pdf_list_url;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    private static final String TAG = PdfListActivity.class.getSimpleName();

    private RecyclerView pdfRecyclerview;
    LinearLayoutManager pdfLayoutManager;
    public PdfBookViewAdapter pdfRvAdapter;
    private ArrayList<TafsirPdfList> pdfBookList;

    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_pdf_list);

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

        View bottomBar = findViewById(R.id.pdf_list);
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tafsir_book_id = extras.getString("tafsir_book_id");
            name_en = extras.getString("name_en");
            name_ar = extras.getString("name_ar");
            name_bn = extras.getString("name_bn");
            thumb = extras.getString("thumb");
            pdf_list_url = extras.getString("pdf_list_url");
        }

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        TextView info_title = (TextView) findViewById(R.id.info_title);
        info_title.setText(name_en);

        TextView info_sub_title = (TextView) findViewById(R.id.info_sub_title);
        info_sub_title.setText(name_bn);
        info_sub_title.setTypeface(font);

        pdfRecyclerview = (RecyclerView) findViewById(R.id.pdf_list);
        pdfLayoutManager = new GridLayoutManager(this, 2);
        pdfRecyclerview.setLayoutManager(pdfLayoutManager);
        setPdfRecyclerViewAdapter();
        getPdfDataFromInternet();
    }

    private void setPdfRecyclerViewAdapter() {
        pdfBookList = new ArrayList<TafsirPdfList>();
        pdfRvAdapter = new PdfBookViewAdapter(PdfListActivity.this, pdfBookList, this, "MA");
        pdfRecyclerview.setAdapter(pdfRvAdapter);
    }

    private void getPdfDataFromInternet(){
        String Url = pdf_list_url;
        if (isInternetPresent) {
            new JsonFromUrlTask(this, Url, TAG, "");
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(PdfListActivity.this);
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

                TafsirPdfList tb = new TafsirPdfList();
                tb.setName_english(jObject.getString("title_english"));
                tb.setName_bangla(jObject.getString("title_bangla"));
                tb.setFile_name(jObject.getString("fileName"));
                tb.setThumb(thumb);
                tb.setUrl(jObject.getString("url"));

                pdfBookList.add(tb);
            }
            pdfRvAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}