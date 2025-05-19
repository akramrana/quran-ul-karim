package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.sentry.Sentry;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.QuranIndexViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.QuranIndex;

import java.util.ArrayList;

public class SubjectWiseActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private RecyclerView recyclerview;
    private QuranIndexViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<QuranIndex> quranIndexes;

    private static final String TAG = SubjectWiseActivity.class.getSimpleName();
    DatabaseHelper dbhelper;

    SearchView editsearch;

    String searchTxt = "";

    Handler mHandler = new Handler();

    Typeface font;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_subject_wise);

        View rootView = findViewById(R.id.topBarSlist);
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

        recyclerview = (RecyclerView) findViewById(R.id.all_index_list);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        setRecyclerViewAdapter();

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                QuranIndex qi = quranIndexes.get(position);
                //Toast.makeText(getApplicationContext(), vd.getVideo_id() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(),SubjectDetailsActivity.class);
                in.putExtra("en", qi.getEn());
                in.putExtra("bn", qi.getBn());
                in.putExtra("tags", qi.getTags());
                in.putExtra("verses", qi.getVerses());
                startActivityForResult(in, 100);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        dbhelper = DatabaseHelper.getInstance(getApplicationContext());
        getDataFromLocalDb();

        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);

        TextView hint = (TextView) findViewById(R.id.hint);
        hint.setTypeface(font);
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "";
        searchTxt = searchTxt.replaceAll("\'","");
        Log.d("Search Term",searchTxt);
        if(searchTxt.equals("")) {
            sql = "SELECT quran_index.* " +
                    "FROM quran_index " +
                    "order by en ASC";
        }else{
            sql = "SELECT quran_index.* " +
                    "FROM quran_index " +
                    "WHERE en LIKE '%"+searchTxt+"%' OR bn LIKE '%"+searchTxt+"%' "+
                    "order by en ASC";
        }
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    QuranIndex qi = new QuranIndex();
                    qi.setBn(cursor.getString(cursor.getColumnIndexOrThrow("bn")));
                    qi.setEn(cursor.getString(cursor.getColumnIndexOrThrow("en")));
                    qi.setTags(cursor.getString(cursor.getColumnIndexOrThrow("tags")));
                    qi.setVerses(cursor.getString(cursor.getColumnIndexOrThrow("verses")));
                    quranIndexes.add(qi);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            //throw new RuntimeException("SQL Query: " + sql, e);
            Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
        }
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
        rvAdapter.notifyDataSetChanged();
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
                    quranIndexes = new ArrayList<QuranIndex>();
                    rvAdapter = new QuranIndexViewAdapter(SubjectWiseActivity.this, quranIndexes, SubjectWiseActivity.this);
                    recyclerview.setAdapter(rvAdapter);
                    getDataFromLocalDb();
                }
                else{
                    quranIndexes = new ArrayList<QuranIndex>();
                    rvAdapter = new QuranIndexViewAdapter(SubjectWiseActivity.this, quranIndexes, SubjectWiseActivity.this);
                    recyclerview.setAdapter(rvAdapter);
                    getDataFromLocalDb();
                }
            }
        }, 100);


        return true;
    }

    private void setRecyclerViewAdapter() {
        quranIndexes = new ArrayList<QuranIndex>();
        rvAdapter = new QuranIndexViewAdapter(SubjectWiseActivity.this, quranIndexes, this);
        recyclerview.setAdapter(rvAdapter);
    }
}