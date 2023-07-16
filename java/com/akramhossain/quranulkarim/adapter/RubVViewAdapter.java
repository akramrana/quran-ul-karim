package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import java.util.ArrayList;
import com.akramhossain.quranulkarim.model.Rub;

public class RubVViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Rub> rubs;
    String cutTT;
    private Activity activity;
    Typeface fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem;
    SharedPreferences mPrefs;

    public RubVViewAdapter(Context c, ArrayList<Rub> rubs, Activity activity) {
        this.c = c;
        this.rubs = rubs;
        this.activity = activity;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        fontUthmani = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.rub_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        Rub rub = rubs.get(position);
        rvHolder.ayah_index.setText(rub.getAyah_index());
        //rvHolder.surah_id.setText(juz.getSurah_id());
        //rvHolder.page_num.setText(juz.getPage_num());
        rvHolder.rub_num.setText("Rub\' "+rub.getRub_num());
        //String tt = rub.getText_tashkeel();
        String tt = rub.getIndo_pak();
        if (tt.length() > 110) {
            cutTT = tt.substring(0, 110);
        }else{
            cutTT = tt;
        }

        rvHolder.text_tashkeel.setText(cutTT);
        rvHolder.ayah_key.setText(rub.getAyah_key());
        rvHolder.name_simple.setText("Sura "+rub.getName_simple()+", Ayah "+rub.getAyah_num());
        rvHolder.name_complex.setText(rub.getName_complex());
        rvHolder.name_english.setText(rub.getName_english());
        rvHolder.name_arabic.setText(rub.getName_arabic());
    }

    @Override
    public int getItemCount() {
        return rubs.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView ayah_index;
        TextView surah_id;
        TextView page_num;
        TextView ayah_num;
        TextView rub_num;
        TextView text_tashkeel;
        TextView ayah_key;
        TextView name_simple;
        TextView name_complex;
        TextView name_english;
        TextView name_arabic;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ayah_index = (TextView) itemView.findViewById(R.id.ayah_index);
            //surah_id = (TextView) itemView.findViewById(R.id.surah_id);
            //page_num = (TextView) itemView.findViewById(R.id.page_num);
            rub_num = (TextView) itemView.findViewById(R.id.rub_num);
            text_tashkeel = (TextView) itemView.findViewById(R.id.text_tashkeel);
            ayah_key = (TextView) itemView.findViewById(R.id.ayah_key);
            name_simple = (TextView) itemView.findViewById(R.id.name_simple);
            name_complex = (TextView) itemView.findViewById(R.id.name_complex);
            name_english = (TextView) itemView.findViewById(R.id.name_english);
            name_arabic = (TextView) itemView.findViewById(R.id.name_arabic);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Arabic Regular");
            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                text_tashkeel.setTypeface(fontAlmajeed);
                name_arabic.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                text_tashkeel.setTypeface(fontAlQalam);
                name_arabic.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Uthmanic Script")){
                text_tashkeel.setTypeface(fontUthmani);
                name_arabic.setTypeface(fontUthmani);
            }
            if(mp_arabicFontFamily.equals("Noore Hidayat")){
                text_tashkeel.setTypeface(fontNooreHidayat);
                name_arabic.setTypeface(fontNooreHidayat);
            }
            if(mp_arabicFontFamily.equals("Saleem Quran")){
                text_tashkeel.setTypeface(fontSaleem);
                name_arabic.setTypeface(fontSaleem);
            }

        }

    }

}
