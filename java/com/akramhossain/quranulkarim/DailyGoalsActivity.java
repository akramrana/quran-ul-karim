package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

public class DailyGoalsActivity extends AppCompatActivity {

    Typeface font;
    CheckBox chkTahajjud, chkFajr, chkAdhkar, chkReci, chkHadith;
    CheckBox chkDoha, chkDhuhr, chkAsr, chkMaghrib, chkIsha, chkCharity;
    CheckBox chkLiterature, chkMulk, chkBaqara, chkQursi, chkKahf;
    CheckBox chkHaram1, chkHaram2, chkHaram3, chkHaram4, chkHaram5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        TextView txtTahajjud = (TextView) findViewById(R.id.txtTahajjud);
        txtTahajjud.setTypeface(font);

        TextView txtFajr = (TextView) findViewById(R.id.txtFajr);
        txtFajr.setTypeface(font);

        TextView txtAdhkar = (TextView) findViewById(R.id.txtAdhkar);
        txtAdhkar.setTypeface(font);

        TextView txtQuran = (TextView) findViewById(R.id.txtQuran);
        txtQuran.setTypeface(font);

        TextView txtHadith = (TextView) findViewById(R.id.txtHadith);
        txtHadith.setTypeface(font);

        TextView txtDoha = (TextView) findViewById(R.id.txtDoha);
        txtDoha.setTypeface(font);

        TextView txtDhuhr = (TextView) findViewById(R.id.txtDhuhr);
        txtDhuhr.setTypeface(font);

        TextView txtAsr = (TextView) findViewById(R.id.txtAsr);
        txtAsr.setTypeface(font);

        TextView txtMaghrib = (TextView) findViewById(R.id.txtMaghrib);
        txtMaghrib.setTypeface(font);

        TextView txtIsha = (TextView) findViewById(R.id.txtIsha);
        txtIsha.setTypeface(font);

        TextView txtCharity = (TextView) findViewById(R.id.txtCharity);
        txtCharity.setTypeface(font);

        TextView txtLiterature = (TextView) findViewById(R.id.txtLiterature);
        txtLiterature.setTypeface(font);

        TextView txtMulk = (TextView) findViewById(R.id.txtMulk);
        txtMulk.setTypeface(font);

        TextView txtBaqarah = (TextView) findViewById(R.id.txtBaqarah);
        txtBaqarah.setTypeface(font);

        TextView txtQursi = (TextView) findViewById(R.id.txtQursi);
        txtQursi.setTypeface(font);

        TextView txtKahf = (TextView) findViewById(R.id.txtKahf);
        txtKahf.setTypeface(font);

        TextView txtHaram1 = (TextView) findViewById(R.id.txtHaram1);
        txtHaram1.setTypeface(font);

        TextView txtHaram2 = (TextView) findViewById(R.id.txtHaram2);
        txtHaram2.setTypeface(font);

        TextView txtHaram3 = (TextView) findViewById(R.id.txtHaram3);
        txtHaram3.setTypeface(font);

        TextView txtHaram4 = (TextView) findViewById(R.id.txtHaram4);
        txtHaram4.setTypeface(font);

        TextView txtHaram5 = (TextView) findViewById(R.id.txtHaram5);
        txtHaram5.setTypeface(font);

    }
}