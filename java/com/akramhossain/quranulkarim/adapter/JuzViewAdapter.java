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
import com.akramhossain.quranulkarim.model.Juz;
import java.util.ArrayList;

public class JuzViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context c;
    ArrayList<Juz> juzs;
    String cutTT;
    private Activity activity;
    Typeface fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;
    SharedPreferences mPrefs;

    public JuzViewAdapter(Context c, ArrayList<Juz> juzs, Activity activity) {
        this.c = c;
        this.juzs = juzs;
        this.activity = activity;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        fontUthmani = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        fontTahaNaskh = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(c.getAssets(),"fonts/kitab.ttf");

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.juz_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        Juz juz = juzs.get(position);
        rvHolder.ayah_index.setText(juz.getAyah_index());
        //rvHolder.surah_id.setText(juz.getSurah_id());
        //rvHolder.page_num.setText(juz.getPage_num());
        rvHolder.juz_num.setText("JUZ\' "+juz.getJuz_num());
        String mushaf = mPrefs.getString("mushaf", "IndoPak");
        String tt = "";
        if(mushaf.equals("Uthmanic")) {
            tt = juz.getText_tashkeel();
        }else {
            tt = juz.getIndo_pak();
        }
        if (tt.length() > 110) {
            cutTT = tt.substring(0, 110);
        }else{
            cutTT = tt;
        }

        rvHolder.text_tashkeel.setText(cutTT);
        rvHolder.ayah_key.setText(juz.getAyah_key());
        rvHolder.name_simple.setText("Sura "+juz.getName_simple()+", Ayah "+juz.getAyah_num());
        rvHolder.name_complex.setText(juz.getName_complex());
        rvHolder.name_english.setText(juz.getName_english());
        rvHolder.name_arabic.setText(juz.getName_arabic());
    }

    @Override
    public int getItemCount() {
        return juzs.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView ayah_index;
        TextView surah_id;
        TextView page_num;
        TextView ayah_num;
        TextView juz_num;
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
            juz_num = (TextView) itemView.findViewById(R.id.juz_num);
            text_tashkeel = (TextView) itemView.findViewById(R.id.text_tashkeel);
            ayah_key = (TextView) itemView.findViewById(R.id.ayah_key);
            name_simple = (TextView) itemView.findViewById(R.id.name_simple);
            name_complex = (TextView) itemView.findViewById(R.id.name_complex);
            name_english = (TextView) itemView.findViewById(R.id.name_english);
            name_arabic = (TextView) itemView.findViewById(R.id.name_arabic);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                text_tashkeel.setTypeface(fontAlmajeed);
                name_arabic.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                text_tashkeel.setTypeface(fontAlQalam);
                name_arabic.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Noore Huda")){
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
            if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
                text_tashkeel.setTypeface(fontTahaNaskh);
                name_arabic.setTypeface(fontTahaNaskh);
            }
            if(mp_arabicFontFamily.equals("Arabic Regular")){
                text_tashkeel.setTypeface(fontKitab);
                name_arabic.setTypeface(fontKitab);
            }
        }

    }

}
