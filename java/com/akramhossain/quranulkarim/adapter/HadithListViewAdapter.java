package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.model.HadithList;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class HadithListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<HadithList> hadithList;
    private Activity activity;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem;
    SharedPreferences mPrefs;

    public HadithListViewAdapter(Context c, ArrayList<HadithList> hadithList, Activity activity) {
        this.c = c;
        this.hadithList = hadithList;
        this.activity = activity;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        font = Typeface.createFromAsset(c.getAssets(), "fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.hadith_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.text_bn.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        HadithList hc = hadithList.get(position);
        rvHolder.hid.setText(hc.getHid());
        rvHolder.hadithnumber.setText(hc.getHadithnumber());
        rvHolder.arabicnumber.setText(hc.getArabicnumber());
        rvHolder.grades.setText(hc.getGrades());
        rvHolder.reference_book.setText(hc.getReference_book());
        rvHolder.reference_hadith.setText(hc.getReference_hadith());
        rvHolder.text_ar.setText(hc.getText_ar());
        rvHolder.text_bn.setText(hc.getText_bn());
        rvHolder.text_en.setText(hc.getText_en());
    }

    @Override
    public int getItemCount() {
        return hadithList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView hid, hadithnumber, arabicnumber, grades;
        TextView reference_book, reference_hadith, text_ar, text_en, text_bn;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            hid = (TextView) itemView.findViewById(R.id.hid);
            hadithnumber = (TextView) itemView.findViewById(R.id.hadithnumber);
            arabicnumber = (TextView) itemView.findViewById(R.id.arabicnumber);
            grades = (TextView) itemView.findViewById(R.id.grades);
            reference_book = (TextView) itemView.findViewById(R.id.reference_book);
            reference_hadith = (TextView) itemView.findViewById(R.id.reference_hadith);
            text_ar = (TextView) itemView.findViewById(R.id.text_ar);
            text_en = (TextView) itemView.findViewById(R.id.text_en);
            text_bn = (TextView) itemView.findViewById(R.id.text_bn);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
            String mp_arFz = mPrefs.getString("arFontSize", "30");
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");

            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                text_ar.setTypeface(fontAlmajeed);
                text_ar.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                text_ar.setTypeface(fontAlQalam);
                text_ar.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Noore Huda")){
                text_ar.setTypeface(fontUthmani);
                text_ar.setTypeface(fontUthmani);
            }
            if(mp_arabicFontFamily.equals("Noore Hidayat")){
                text_ar.setTypeface(fontNooreHidayat);
                text_ar.setTypeface(fontNooreHidayat);
            }
            if(mp_arabicFontFamily.equals("Saleem Quran")){
                text_ar.setTypeface(fontSaleem);
                text_ar.setTypeface(fontSaleem);
            }

            if (!mp_arFz.equals("")) {
                text_ar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
            }
            if (!mp_enFz.equals("")) {
                text_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
            }
            if (!mp_bnFz.equals("")) {
                text_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
            }

        }
    }

}
