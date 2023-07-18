package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.akramhossain.quranulkarim.adapter.QuranIndexViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.QuranIndex;

import java.util.ArrayList;

public class SubjectWiseActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private QuranIndexViewAdapter rvAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<QuranIndex> quranIndexes;

    private static final String TAG = SubjectWiseActivity.class.getSimpleName();
    DatabaseHelper dbhelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_wise);
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

        dbhelper = DatabaseHelper.getInstance(getApplicationContext());
        getDataFromLocalDb();
    }

    private void getDataFromLocalDb() {
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = sql = "SELECT quran_index.* " +
                "FROM quran_index " +
                "order by en ASC";
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

    private void setRecyclerViewAdapter() {
        quranIndexes = new ArrayList<QuranIndex>();
        rvAdapter = new QuranIndexViewAdapter(SubjectWiseActivity.this, quranIndexes, this);
        recyclerview.setAdapter(rvAdapter);
    }
}