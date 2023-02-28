package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
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

    CheckBox chk_ibnkasir, chk_bayaan, chk_zakaria, chk_tafhim, chk_fathul_mazid, chk_fezilalil, chk_jalalayn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //
        final Spinner spinner = (Spinner) findViewById(R.id.arabic_font_spinner);
        List<String> fontFamily = new ArrayList<String>();
        fontFamily.add("Arabic Regular");
        fontFamily.add("Taha Naskh");
        fontFamily.add("Al Qalam Quran");
        fontFamily.add("Uthmanic Script");
        fontFamily.add("Noore Hidayat");
        fontFamily.add("Saleem Quran");
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fontFamily);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        //
        arFontSize = (EditText)findViewById(R.id.arFontSize);
        enFontSize = (EditText)findViewById(R.id.enFontSize);
        bnFontSize = (EditText)findViewById(R.id.bnFontSize);
        enFontSizeTafsir = (EditText)findViewById(R.id.enFontSizeTafsir);
        bnFontSizeTafsir = (EditText)findViewById(R.id.bnFontSizeTafsir);
        //
        String mp_arabicFontFamily = mPrefs.getString(sp_arabicFontFamily, "Arabic Regular");
        String mp_arFz = mPrefs.getString(sp_arFontSize, "30");
        String mp_enFz = mPrefs.getString(sp_enFontSize, "15");
        String mp_bnFz = mPrefs.getString(sp_bnFontSize, "15");
        String mp_enFzTs = mPrefs.getString(sp_enFontSizeTafsir, "15");
        String mp_bnFzTs = mPrefs.getString(sp_bnFontSizeTafsir, "15");
        //
        arFontSize.setText(mp_arFz);
        enFontSize.setText(mp_enFz);
        bnFontSize.setText(mp_bnFz);
        enFontSizeTafsir.setText(mp_enFzTs);
        bnFontSizeTafsir.setText(mp_bnFzTs);
        spinner.setSelection(dataAdapter.getPosition(mp_arabicFontFamily));
        //
        String is_ibn_kasir = mPrefs.getString(is_tafsir_ibn_kasir_selected, "2");
        String is_bayaan = mPrefs.getString(is_tafsir_bayaan_selected, "2");
        String is_zakaria = mPrefs.getString(is_tafsir_zakaria_selected, "2");
        String is_tafhim = mPrefs.getString(is_tafsir_tafhim_selected, "2");
        String is_fathul = mPrefs.getString(is_tafsir_fathul_mazid_selected, "2");
        String is_fezilalil = mPrefs.getString(is_tafsir_fezilalil_selected, "2");
        String is_jalalayn = mPrefs.getString(is_tafsir_jalalayn_selected, "2");

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
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String arabicFontFamily = spinner.getSelectedItem().toString();
                String arFz = arFontSize.getText().toString();
                String enFz = enFontSize.getText().toString();
                String bnFz = bnFontSize.getText().toString();
                String enFzTs = enFontSizeTafsir.getText().toString();
                String bnFzTs = bnFontSizeTafsir.getText().toString();
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

                String arabicFontFamily = "Arabic Regular";
                String arFz = "30";
                String enFz = "15";
                String bnFz = "15";
                String enFzTs ="15";
                String bnFzTs = "15";
                //
                spinner.setSelection(dataAdapter.getPosition(arabicFontFamily));
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString(sp_arabicFontFamily, arabicFontFamily);
                editor.putString(sp_arFontSize, arFz);
                editor.putString(sp_enFontSize, enFz);
                editor.putString(sp_bnFontSize, bnFz);
                editor.putString(sp_enFontSizeTafsir, enFzTs);
                editor.putString(sp_bnFontSizeTafsir, bnFzTs);
                //
                editor.putString(is_tafsir_ibn_kasir_selected, "2");
                editor.putString(is_tafsir_bayaan_selected, "2");
                editor.putString(is_tafsir_zakaria_selected, "2");
                editor.putString(is_tafsir_tafhim_selected, "2");
                editor.putString(is_tafsir_fathul_mazid_selected, "2");
                editor.putString(is_tafsir_fezilalil_selected, "2");
                editor.putString(is_tafsir_jalalayn_selected, "2");
                chk_ibnkasir.setChecked(true);
                chk_bayaan.setChecked(true);
                chk_zakaria.setChecked(true);
                chk_tafhim.setChecked(true);
                chk_fathul_mazid.setChecked(true);
                chk_fezilalil.setChecked(true);
                chk_jalalayn.setChecked(true);
                //
                editor.apply();
                Toast.makeText(getApplicationContext(),"Settings saved",Toast.LENGTH_SHORT).show();
            }
        });

    }
}