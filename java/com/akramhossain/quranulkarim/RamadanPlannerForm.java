package com.akramhossain.quranulkarim;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RamadanPlannerForm extends AppCompatActivity {

    public static String ramadan_planner_id;
    public static String name_en;
    public static String name_bn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ramadan_planner_form);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ramadan_planner_id = extras.getString("ramadan_planner_id");
            name_en = extras.getString("name_en");
            name_bn = extras.getString("name_bn");
        }

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(name_en);

        TextView subtitle = (TextView) findViewById(R.id.subtitle);
        subtitle.setText("Planner for "+name_en);

    }
}