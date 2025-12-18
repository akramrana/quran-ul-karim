package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.akramhossain.quranulkarim.task.PrayerScheduler;
import com.akramhossain.quranulkarim.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity{

    EditText arFontSize, enFontSize, bnFontSize, enFontSizeTafsir, bnFontSizeTafsir;
    SharedPreferences mPrefs;

    public static final String sp_arabicFontFamily = "arabicFontFamily";
    public static final String sp_arFontSize = "arFontSize";
    public static final String sp_enFontSize = "enFontSize";
    public static final String sp_bnFontSize = "bnFontSize";
    public static final String sp_enFontSizeTafsir = "enFontSizeTafsir";
    public static final String sp_bnFontSizeTafsir = "bnFontSizeTafsir";

    public static final String is_tafsir_ibn_kasir_selected = "isTafsirIbnKasirSelected";
    public static final String is_tafsir_bayaan_selected = "isTafsirBayaanSelected";
    public static final String is_tafsir_zakaria_selected = "isTafsirZakariaSelected";
    public static final String is_tafsir_tafhim_selected = "isTafsirTafhimSelected";
    public static final String is_tafsir_fathul_mazid_selected = "isTafsirFathulMazidSelected";
    public static final String is_tafsir_fezilalil_selected = "isTafsirFezilalilSelected";
    public static final String is_tafsir_jalalayn_selected = "isTafsirJalalaynSelected";

    public static final String show_bn_pron = "show_bn_pron";
    public static final String show_en_trans = "show_en_trans";
    public static final String show_bn_trans = "show_bn_trans";

    public static final String sp_mushaf = "mushaf";

    CheckBox chk_ibnkasir, chk_bayaan, chk_zakaria, chk_tafhim, chk_fathul_mazid, chk_fezilalil, chk_jalalayn;
    CheckBox chk_showBnPronunciation, chk_showEnTranslation, chk_showBnTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_setting);

        View rootView = findViewById(R.id.topTafsirBar);
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

        View bottomBar = findViewById(R.id.buttonSection);

        ViewCompat.setOnApplyWindowInsetsListener(bottomBar, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, 13, 0, bottomInset);
            return insets;
        });

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);
        //
        final Spinner spinner = (Spinner) findViewById(R.id.arabic_font_spinner);
        List<String> fontFamily = new ArrayList<String>();
        fontFamily.add("Arabic Regular");
        fontFamily.add("KFGQPC Uthman Taha Naskh");
        fontFamily.add("Al Qalam Quran");
        fontFamily.add("Noore Huda");
        fontFamily.add("Noore Hidayat");
        fontFamily.add("Saleem Quran");
        fontFamily.add("Al Majeed Quranic Font");
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fontFamily);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        //
        final Spinner spinner1 = (Spinner) findViewById(R.id.mushaf_spinner);
        List<String> mushafs = new ArrayList<String>();
        mushafs.add("ImlaeiSimple");
        mushafs.add("IndoPak");
        mushafs.add("Uthmanic");
        mushafs.add("Tajweed");
        final ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mushafs);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter1);
        //
        arFontSize = (EditText)findViewById(R.id.arFontSize);
        enFontSize = (EditText)findViewById(R.id.enFontSize);
        bnFontSize = (EditText)findViewById(R.id.bnFontSize);
        enFontSizeTafsir = (EditText)findViewById(R.id.enFontSizeTafsir);
        bnFontSizeTafsir = (EditText)findViewById(R.id.bnFontSizeTafsir);
        //
        String mp_arabicFontFamily = mPrefs.getString(sp_arabicFontFamily, "Noore Huda");
        String mp_arFz = mPrefs.getString(sp_arFontSize, "30");
        String mp_enFz = mPrefs.getString(sp_enFontSize, "15");
        String mp_bnFz = mPrefs.getString(sp_bnFontSize, "15");
        String mp_enFzTs = mPrefs.getString(sp_enFontSizeTafsir, "15");
        String mp_bnFzTs = mPrefs.getString(sp_bnFontSizeTafsir, "15");
        String mp_mushaf = mPrefs.getString(sp_mushaf, "IndoPak");
        //
        arFontSize.setText(mp_arFz);
        enFontSize.setText(mp_enFz);
        bnFontSize.setText(mp_bnFz);
        enFontSizeTafsir.setText(mp_enFzTs);
        bnFontSizeTafsir.setText(mp_bnFzTs);
        spinner.setSelection(dataAdapter.getPosition(mp_arabicFontFamily));
        spinner1.setSelection(dataAdapter1.getPosition(mp_mushaf));
        //
        String is_ibn_kasir = mPrefs.getString(is_tafsir_ibn_kasir_selected, "2");
        String is_bayaan = mPrefs.getString(is_tafsir_bayaan_selected, "2");
        String is_zakaria = mPrefs.getString(is_tafsir_zakaria_selected, "2");
        String is_tafhim = mPrefs.getString(is_tafsir_tafhim_selected, "2");
        String is_fathul = mPrefs.getString(is_tafsir_fathul_mazid_selected, "2");
        String is_fezilalil = mPrefs.getString(is_tafsir_fezilalil_selected, "2");
        String is_jalalayn = mPrefs.getString(is_tafsir_jalalayn_selected, "2");

        String is_show_bn_pronunciation = mPrefs.getString(show_bn_pron, "2");
        String is_show_en_translation = mPrefs.getString(show_en_trans, "2");
        String is_show_bn_translation = mPrefs.getString(show_bn_trans, "2");

        chk_ibnkasir = (CheckBox) findViewById(R.id.tafsirIbnKasirCheckBox);
        if(is_ibn_kasir.equals("1") || is_ibn_kasir.equals("2")){
            chk_ibnkasir.setChecked(true);
        }
        chk_bayaan = (CheckBox) findViewById(R.id.tafsirBayaanCheckBox);
        if(is_bayaan.equals("1") || is_bayaan.equals("2")){
            chk_bayaan.setChecked(true);
        }
        chk_zakaria = (CheckBox) findViewById(R.id.tafsirZakariaCheckBox);
        if(is_zakaria.equals("1") || is_zakaria.equals("2")){
            chk_zakaria.setChecked(true);
        }
        chk_tafhim = (CheckBox) findViewById(R.id.tafsirTafhimCheckBox);
        if(is_tafhim.equals("1") || is_tafhim.equals("2")){
            chk_tafhim.setChecked(true);
        }
        chk_fathul_mazid = (CheckBox) findViewById(R.id.tafsirFathulCheckBox);
        if(is_fathul.equals("1") || is_fathul.equals("2")){
            chk_fathul_mazid.setChecked(true);
        }
        chk_fezilalil = (CheckBox) findViewById(R.id.tafsirFezilalilCheckBox);
        if(is_fezilalil.equals("1") || is_fezilalil.equals("2")){
            chk_fezilalil.setChecked(true);
        }
        chk_jalalayn = (CheckBox) findViewById(R.id.tafsirJalalaynCheckBox);
        if(is_jalalayn.equals("1") || is_jalalayn.equals("2")){
            chk_jalalayn.setChecked(true);
        }
        //
        chk_showBnPronunciation = (CheckBox) findViewById(R.id.showBnPronunciation);
        if(is_show_bn_pronunciation.equals("1") || is_show_bn_pronunciation.equals("2")){
            chk_showBnPronunciation.setChecked(true);
        }
        chk_showEnTranslation = (CheckBox) findViewById(R.id.showEnTranslation);
        if(is_show_en_translation.equals("1") || is_show_en_translation.equals("2")){
            chk_showEnTranslation.setChecked(true);
        }
        chk_showBnTranslation = (CheckBox) findViewById(R.id.showBnTranslation);
        if(is_show_bn_translation.equals("1") || is_show_bn_translation.equals("2")){
            chk_showBnTranslation.setChecked(true);
        }
        //
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String arabicFontFamily = spinner.getSelectedItem().toString();
                String arFz = arFontSize.getText().toString();
                String enFz = enFontSize.getText().toString();
                String bnFz = bnFontSize.getText().toString();
                String enFzTs = enFontSizeTafsir.getText().toString();
                String bnFzTs = bnFontSizeTafsir.getText().toString();
                String mushaf = spinner1.getSelectedItem().toString();
                //
                /*Log.i("arabicFontFamily", arabicFontFamily);
                Log.i("arFontSize", arFz);
                Log.i("enFontSize", enFz);
                Log.i("bnFontSize", bnFz);
                Log.i("enFontSizeTafsir", enFzTs);
                Log.i("bnFontSizeTafsir", bnFzTs);*/
                //
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString(sp_arabicFontFamily, arabicFontFamily);
                editor.putString(sp_arFontSize, arFz);
                editor.putString(sp_enFontSize, enFz);
                editor.putString(sp_bnFontSize, bnFz);
                editor.putString(sp_enFontSizeTafsir, enFzTs);
                editor.putString(sp_bnFontSizeTafsir, bnFzTs);
                editor.putString(sp_mushaf, mushaf);
                //
                if(chk_ibnkasir.isChecked()){
                    editor.putString(is_tafsir_ibn_kasir_selected, "1");
                }else{
                    editor.putString(is_tafsir_ibn_kasir_selected, "-1");
                }
                if(chk_bayaan.isChecked()){
                    editor.putString(is_tafsir_bayaan_selected, "1");
                }else{
                    editor.putString(is_tafsir_bayaan_selected, "-1");
                }
                if(chk_zakaria.isChecked()){
                    editor.putString(is_tafsir_zakaria_selected, "1");
                }else{
                    editor.putString(is_tafsir_zakaria_selected, "-1");
                }
                if(chk_tafhim.isChecked()){
                    editor.putString(is_tafsir_tafhim_selected, "1");
                }else{
                    editor.putString(is_tafsir_tafhim_selected, "-1");
                }
                if(chk_fathul_mazid.isChecked()){
                    editor.putString(is_tafsir_fathul_mazid_selected, "1");
                }else{
                    editor.putString(is_tafsir_fathul_mazid_selected, "-1");
                }
                if(chk_fezilalil.isChecked()){
                    editor.putString(is_tafsir_fezilalil_selected, "1");
                }else{
                    editor.putString(is_tafsir_fezilalil_selected, "-1");
                }
                if(chk_jalalayn.isChecked()){
                    editor.putString(is_tafsir_jalalayn_selected, "1");
                }else{
                    editor.putString(is_tafsir_jalalayn_selected, "-1");
                }
                //
                if(chk_showBnPronunciation.isChecked()){
                    editor.putString(show_bn_pron, "1");
                }else{
                    editor.putString(show_bn_pron, "-1");
                }
                if(chk_showEnTranslation.isChecked()){
                    editor.putString(show_en_trans, "1");
                }else{
                    editor.putString(show_en_trans, "-1");
                }
                if(chk_showBnTranslation.isChecked()){
                    editor.putString(show_bn_trans, "1");
                }else{
                    editor.putString(show_bn_trans, "-1");
                }
                //
                editor.apply();
                //
                Toast.makeText(getApplicationContext(),"Settings saved",Toast.LENGTH_SHORT).show();
            }
        });

        final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                arFontSize.setText("30");
                enFontSize.setText("15");
                bnFontSize.setText("15");
                enFontSizeTafsir.setText("15");
                bnFontSizeTafsir.setText("15");

                String arabicFontFamily = "Noore Huda";
                String arFz = "30";
                String enFz = "15";
                String bnFz = "15";
                String enFzTs ="15";
                String bnFzTs = "15";
                String mushaf = "IndoPak";
                //
                spinner.setSelection(dataAdapter.getPosition(arabicFontFamily));
                spinner1.setSelection(dataAdapter1.getPosition(mushaf));
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString(sp_arabicFontFamily, arabicFontFamily);
                editor.putString(sp_arFontSize, arFz);
                editor.putString(sp_enFontSize, enFz);
                editor.putString(sp_bnFontSize, bnFz);
                editor.putString(sp_enFontSizeTafsir, enFzTs);
                editor.putString(sp_bnFontSizeTafsir, bnFzTs);
                editor.putString(sp_mushaf, mushaf);
                //
                editor.putString(is_tafsir_ibn_kasir_selected, "2");
                editor.putString(is_tafsir_bayaan_selected, "2");
                editor.putString(is_tafsir_zakaria_selected, "2");
                editor.putString(is_tafsir_tafhim_selected, "2");
                editor.putString(is_tafsir_fathul_mazid_selected, "2");
                editor.putString(is_tafsir_fezilalil_selected, "2");
                editor.putString(is_tafsir_jalalayn_selected, "2");
                //
                editor.putString(show_bn_pron, "2");
                editor.putString(show_en_trans, "2");
                editor.putString(show_bn_trans, "2");
                //
                chk_ibnkasir.setChecked(true);
                chk_bayaan.setChecked(true);
                chk_zakaria.setChecked(true);
                chk_tafhim.setChecked(true);
                chk_fathul_mazid.setChecked(true);
                chk_fezilalil.setChecked(true);
                chk_jalalayn.setChecked(true);
                //
                chk_showBnPronunciation.setChecked(true);
                chk_showEnTranslation.setChecked(true);
                chk_showBnTranslation.setChecked(true);
                //
                editor.apply();
                Toast.makeText(getApplicationContext(),"Settings saved",Toast.LENGTH_SHORT).show();
            }
        });

        CheckBox cbPrayerAlert = findViewById(R.id.showPrayerAlert);
        boolean enabled = mPrefs.getBoolean("pr_alert_enabled", false);
        cbPrayerAlert.setChecked(enabled);
        cbPrayerAlert.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // User ENABLED prayer alerts
                mPrefs.edit()
                        .putBoolean("pr_alert_enabled", true)
                        .putBoolean("pr_first_schedule_done", false)
                        .apply();

            } else {
                mPrefs.edit()
                        .putBoolean("pr_alert_enabled", false)
                        .putBoolean("pr_first_schedule_done", false)
                        .apply();
                PrayerScheduler.cancelAll(this);
            }
        });

    }
}