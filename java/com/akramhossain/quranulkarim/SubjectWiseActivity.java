package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.akramhossain.quranulkarim.adapter.QuranIndexViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
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