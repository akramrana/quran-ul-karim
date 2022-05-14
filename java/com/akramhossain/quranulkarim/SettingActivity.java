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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends Activity {

    EditText arFontSize, enFontSize, bnFontSize, enFontSizeTafsir, bnFontSizeTafsir;
    SharedPreferences mPrefs;

    public static final String sp_arabicFontFamily = "arabicFontFamily";
    public static final String sp_arFontSize = "arFontSize";
    public static final String sp_enFontSize = "enFontSize";
    public static final String sp_bnFontSize = "bnFontSize";
    public static final String sp_enFontSizeTafsir = "enFontSizeTafsir";
    public static final String sp_bnFontSizeTafsir = "bnFontSizeTafsir";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //
        final Spinner spinner = (Spinner) findViewById(R.id.arabic_font_spinner);
        List<String> fontFamily = new ArrayList<String>();
        fontFamily.add("Arabic Regular");
        fontFamily.add("Al Majeed Quranic Font");
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
                editor.apply();
                Toast.makeText(getApplicationContext(),"Settings saved",Toast.LENGTH_SHORT).show();
            }
        });
    }
}