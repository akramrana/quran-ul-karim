package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.model.NameOfAllah;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class NameOfAllahViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context c;
    ArrayList<NameOfAllah> nameOfAllah;
    private Activity activity;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;
    SharedPreferences mPrefs;

    public NameOfAllahViewAdapter(Context c, ArrayList<NameOfAllah> nameOfAllah, Activity activity) {
        this.c = c;
        this.nameOfAllah = nameOfAllah;
        this.activity = activity;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        font = Typeface.createFromAsset(c.getAssets(), "fonts/Siyamrupali.ttf");
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
        View v= LayoutInflater.from(c).inflate(R.layout.name_allah_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        NameOfAllah noa = nameOfAllah.get(position);
        rvHolder.name.setText(noa.getName());
        rvHolder.transliteration.setText(noa.getTransliteration());
        rvHolder.number.setText(noa.getNumber());
        rvHolder.found.setText(noa.getFound());
        rvHolder.en_meaning.setText(noa.getEn_meaning());
        rvHolder.en_desc.setText(noa.getEn_desc());
        rvHolder.bn_meaning.setText(noa.getBn_meaning());
        rvHolder.bn_desc.setText(noa.getBn_desc());
    }

    @Override
    public int getItemCount() {
        return nameOfAllah.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView transliteration;
        TextView number;
        TextView found;
        TextView en_meaning;
        TextView en_desc;
        TextView bn_meaning;
        TextView bn_desc;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            transliteration = (TextView) itemView.findViewById(R.id.transliteration);
            number = (TextView) itemView.findViewById(R.id.number);
            found = (TextView) itemView.findViewById(R.id.found);
            en_meaning = (TextView) itemView.findViewById(R.id.en_meaning);
            en_desc = (TextView) itemView.findViewById(R.id.en_desc);
            bn_meaning = (TextView) itemView.findViewById(R.id.bn_meaning);
            bn_desc = (TextView) itemView.findViewById(R.id.bn_desc);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
            String mp_arFz = mPrefs.getString("arFontSize", "30");
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");

            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                name.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                name.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Noore Huda")){
                name.setTypeface(fontUthmani);
            }
            if(mp_arabicFontFamily.equals("Noore Hidayat")){
                name.setTypeface(fontNooreHidayat);
            }
            if(mp_arabicFontFamily.equals("Saleem Quran")){
                name.setTypeface(fontSaleem);
            }
            if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
                name.setTypeface(fontTahaNaskh);
            }
            if(mp_arabicFontFamily.equals("Arabic Regular")){
                name.setTypeface(fontKitab);
            }
            bn_meaning.setTypeface(font);
            bn_desc.setTypeface((font));
        }
    }
}
