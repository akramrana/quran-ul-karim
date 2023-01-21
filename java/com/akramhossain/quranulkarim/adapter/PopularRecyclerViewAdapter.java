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
import com.akramhossain.quranulkarim.model.Sura;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class PopularRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Sura> suras;
    private Activity activity;
    Typeface fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, font;
    SharedPreferences mPrefs;

    public PopularRecyclerViewAdapter(Context c, ArrayList<Sura> suras, Activity activity) {
        this.c = c;
        this.suras = suras;
        this.activity = activity;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        fontUthmani = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        font = Typeface.createFromAsset(c.getAssets(),"fonts/Siyamrupali.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.popular_sura,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        Sura sura = suras.get(position);
        rvHolder.suraId.setText(sura.getSurah_id());
        rvHolder.nameArabicTxt.setText(sura.getName_arabic());
        rvHolder.nameSimpleTxt.setText(sura.getName_simple());
        rvHolder.nameEnglishTxt.setText(sura.getName_english());
        rvHolder.revelationPlaceTxt.setText("Revelation place: "+sura.getRevelation_place());
        rvHolder.ayatTxt.setText("Ayah: "+sura.getAyat());
        rvHolder.revelationOrderTxt.setText("Revelation order: "+sura.getRevelation_order());
        rvHolder.sid.setText(sura.getId());
        rvHolder.nameBangla.setText(sura.getName_bangla());
    }

    @Override
    public int getItemCount() {
        return suras.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView suraId;
        TextView nameArabicTxt;
        TextView nameSimpleTxt;
        TextView nameEnglishTxt;
        TextView revelationPlaceTxt;
        TextView ayatTxt;
        TextView revelationOrderTxt;
        TextView sid;
        TextView nameBangla;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            suraId = (TextView) itemView.findViewById(R.id.sura_id);
            nameArabicTxt = (TextView) itemView.findViewById(R.id.name_arabic);
            nameSimpleTxt = (TextView) itemView.findViewById(R.id.name_simple);
            nameEnglishTxt = (TextView) itemView.findViewById(R.id.name_english);
            revelationPlaceTxt = (TextView) itemView.findViewById(R.id.revelation_place);
            ayatTxt = (TextView) itemView.findViewById(R.id.ayat);
            revelationOrderTxt = (TextView) itemView.findViewById(R.id.revelation_order);
            sid = (TextView) itemView.findViewById(R.id.sid);
            nameBangla = (TextView) itemView.findViewById(R.id.name_bangla);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Arabic Regular");
            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                nameArabicTxt.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                nameArabicTxt.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Uthmanic Script")){
                nameArabicTxt.setTypeface(fontUthmani);
            }
            if(mp_arabicFontFamily.equals("Noore Hidayat")){
                nameArabicTxt.setTypeface(fontNooreHidayat);
            }
            if(mp_arabicFontFamily.equals("Saleem Quran")){
                nameArabicTxt.setTypeface(fontSaleem);
            }
            nameBangla.setTypeface(font);
        }
    }
}
