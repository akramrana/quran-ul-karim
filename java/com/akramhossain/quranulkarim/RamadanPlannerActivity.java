package com.akramhossain.quranulkarim;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.akramhossain.quranulkarim.adapter.RamadanPlannerViewAdapter;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.RamadanPlanner;

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

public class RamadanPlannerActivity extends AppCompatActivity {

    private static final String TAG = RamadanPlannerActivity.class.getSimpleName();
    private RecyclerView recyclerview;
    GridLayoutManager mLayoutManager;

    private ArrayList<RamadanPlanner> ramadanPlanner;
    public RamadanPlannerViewAdapter rvAdapter;
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_ramadan_planner);

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

        recyclerview = (RecyclerView) findViewById(R.id.planner_list);
        mLayoutManager = new GridLayoutManager(this, 2);
        recyclerview.setLayoutManager(mLayoutManager);
        setRecyclerViewAdapter();
        getData();
        //
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                RamadanPlanner rp = ramadanPlanner.get(position);
                Intent in = new Intent(getApplicationContext(),RamadanPlannerForm.class);
                in.putExtra("ramadan_planner_id", rp.getRamadan_planner_id());
                in.putExtra("name_en", rp.getName_en());
                in.putExtra("name_bn", rp.getName_bn());
                startActivityForResult(in, 100);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void setRecyclerViewAdapter() {
        ramadanPlanner = new ArrayList<RamadanPlanner>();
        rvAdapter = new RamadanPlannerViewAdapter(RamadanPlannerActivity.this, ramadanPlanner, this);
        recyclerview.setAdapter(rvAdapter);
    }

    private void getData() {
        String[] nameBn = {
                "১ রামাদান", "২ রামাদান", "৩ রামাদান", "৪ রামাদান", "৫ রামাদান",
                "৬ রামাদান", "৭ রামাদান", "৮ রামাদান", "৯ রামাদান", "১০ রামাদান",
                "১১ রামাদান", "১২ রামাদান", "১৩ রামাদান", "১৪ রামাদান", "১৫ রামাদান",
                "১৬ রামাদান", "১৭ রামাদান", "১৮ রামাদান", "১৯ রামাদান", "২০ রামাদান",
                "২১ রামাদান", "২২ রামাদান", "২৩ রামাদান", "২৪ রামাদান", "২৫ রামাদান",
                "২৬ রামাদান", "২৭ রামাদান", "২৮ রামাদান", "২৯ রামাদান", "৩০ রামাদান"
        };

        for (int i = 1; i <= 30; i++) {
            RamadanPlanner planner = new RamadanPlanner();
            planner.setRamadan_planner_id(String.valueOf(i));
            planner.setName_en(i + " Ramadan");
            planner.setName_bn(nameBn[i - 1]);
            ramadanPlanner.add(planner);
        }
        rvAdapter.notifyDataSetChanged();
    }
}