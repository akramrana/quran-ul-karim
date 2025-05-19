package com.akramhossain.quranulkarim;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.akramhossain.quranulkarim.util.Utils;

import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.Editable;
import android.text.TextWatcher;

public class RamadanPlannerForm extends AppCompatActivity {

    public static String ramadan_planner_id;
    public static String name_en;
    public static String name_bn;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;
    private String currentYear;

    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_ramadan_planner_form);

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

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

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

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);
        editor = mPrefs.edit();
        //
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        currentYear = sdf.format(Calendar.getInstance().getTime());
        //
        CheckBox[] checkBoxes = {
                findViewById(R.id.d_ma),
                findViewById(R.id.d_ea),
                findViewById(R.id.d_istg),
                findViewById(R.id.d_hamd),
                findViewById(R.id.d_charity),
                findViewById(R.id.d_act_knd),
                findViewById(R.id.d_quran_tab),
                findViewById(R.id.d_dod),
                findViewById(R.id.d_abs)
        };
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setTypeface(font);
            boolean isChecked = mPrefs.getBoolean(checkBox.getId() + "_state_"+ramadan_planner_id+"_"+currentYear, false);
            checkBox.setChecked(isChecked);
            // Save state on click
            checkBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                editor.putBoolean(buttonView.getId() + "_state_"+ramadan_planner_id+"_"+currentYear, isChecked1);
                editor.apply();
            });
        }
        //
        CheckBox[] fardCheckBoxes = {
                findViewById(R.id.fajr_fard),
                findViewById(R.id.dhuhr_fard),
                findViewById(R.id.asr_fard),
                findViewById(R.id.maghrib_fard),
                findViewById(R.id.isha_fard),
        };
        for (CheckBox checkBox : fardCheckBoxes) {
            checkBox.setTypeface(font);
            boolean isChecked = mPrefs.getBoolean(checkBox.getId() + "_state_"+ramadan_planner_id+"_"+currentYear, false);
            checkBox.setChecked(isChecked);
            // Save state on click
            checkBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                editor.putBoolean(buttonView.getId() + "_state_"+ramadan_planner_id+"_"+currentYear, isChecked1);
                editor.apply();
            });
        }
        //
        CheckBox[] sunnahCheckBoxes = {
                findViewById(R.id.fajr_sunnah),
                findViewById(R.id.dhuhr_sunnah),
                findViewById(R.id.asr_sunnah),
                findViewById(R.id.maghrib_sunnah),
                findViewById(R.id.isha_sunnah),
                findViewById(R.id.taraweeh_sunnah),
                findViewById(R.id.witr_sunnah),
                findViewById(R.id.tahajjud_sunnah),
                findViewById(R.id.duha_sunnah),
        };
        for (CheckBox checkBox : sunnahCheckBoxes) {
            checkBox.setTypeface(font);
            boolean isChecked = mPrefs.getBoolean(checkBox.getId() + "_state_"+ramadan_planner_id+"_"+currentYear, false);
            checkBox.setChecked(isChecked);
            // Save state on click
            checkBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                editor.putBoolean(buttonView.getId() + "_state_"+ramadan_planner_id+"_"+currentYear, isChecked1);
                editor.apply();
            });
        }
        //
        EditText verseCount = findViewById(R.id.verse_count);
        EditText surahCount = findViewById(R.id.surah_count);
        EditText juzCount = findViewById(R.id.juz_count);

        verseCount.setText(mPrefs.getString("Verse_" + ramadan_planner_id+"_"+currentYear, ""));
        surahCount.setText(mPrefs.getString("Surah_" + ramadan_planner_id+"_"+currentYear, ""));
        juzCount.setText(mPrefs.getString("Juz_" + ramadan_planner_id+"_"+currentYear, ""));

        saveOnChange(verseCount, "Verse_" + ramadan_planner_id+"_"+currentYear);
        saveOnChange(surahCount, "Surah_" + ramadan_planner_id+"_"+currentYear);
        saveOnChange(juzCount, "Juz_" + ramadan_planner_id+"_"+currentYear);
        //
        TextView fajr = findViewById(R.id.fajr);
        fajr.setTypeface(font);

        TextView fajr_bn = findViewById(R.id.fajr_bn);
        fajr_bn.setTypeface(font);

        TextView dhuhr = findViewById(R.id.dhuhr);
        dhuhr.setTypeface(font);

        TextView dhuhr_bn = findViewById(R.id.dhuhr_bn);
        dhuhr_bn.setTypeface(font);

        TextView asr = findViewById(R.id.asr);
        dhuhr.setTypeface(font);

        TextView asr_bn = findViewById(R.id.asr_bn);
        asr_bn.setTypeface(font);

        TextView maghrib = findViewById(R.id.maghrib);
        maghrib.setTypeface(font);

        TextView maghrib_bn = findViewById(R.id.maghrib_bn);
        maghrib_bn.setTypeface(font);

        TextView isha = findViewById(R.id.isha);
        isha.setTypeface(font);

        TextView isha_bn = findViewById(R.id.isha_bn);
        isha_bn.setTypeface(font);

        TextView taraweeh = findViewById(R.id.taraweeh);
        taraweeh.setTypeface(font);

        TextView taraweeh_bn = findViewById(R.id.taraweeh_bn);
        taraweeh_bn.setTypeface(font);

        TextView witr = findViewById(R.id.witr);
        witr.setTypeface(font);

        TextView witr_bn = findViewById(R.id.witr_bn);
        witr_bn.setTypeface(font);

        TextView tahajjud = findViewById(R.id.tahajjud);
        tahajjud.setTypeface(font);

        TextView tahajjud_bn = findViewById(R.id.tahajjud_bn);
        tahajjud_bn.setTypeface(font);

        TextView duha = findViewById(R.id.duha);
        duha.setTypeface(font);

        TextView duha_bn = findViewById(R.id.duha_bn);
        duha_bn.setTypeface(font);

        TextView verse = findViewById(R.id.verse);
        verse.setTypeface(font);

        TextView verse_bn = findViewById(R.id.verse_bn);
        verse_bn.setTypeface(font);

        TextView surah = findViewById(R.id.surah);
        surah.setTypeface(font);

        TextView surah_bn = findViewById(R.id.surah_bn);
        surah_bn.setTypeface(font);

        TextView juz = findViewById(R.id.juz);
        juz.setTypeface(font);

        TextView juz_bn = findViewById(R.id.juz_bn);
        juz_bn.setTypeface(font);

    }

    private void saveOnChange(EditText editText, String key) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editor.putString(key, s.toString());
                editor.apply();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}