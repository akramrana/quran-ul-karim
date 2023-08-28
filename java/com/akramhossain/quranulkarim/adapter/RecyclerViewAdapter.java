package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akramhossain.quranulkarim.PopUpClass;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.SuraDetailsActivity;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.model.Sura;

import java.util.ArrayList;

/**
 * Created by akram on 3/30/2019.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context c;
    ArrayList<Sura> suras;
    private Activity activity;
    Typeface fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, font, fontTahaNaskh, fontKitab;
    SharedPreferences mPrefs;

    public RecyclerViewAdapter(Context c, ArrayList<Sura> suras, Activity activity) {
        this.c = c;
        this.suras = suras;
        this.activity = activity;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        fontUthmani = Typeface.createFromAsset(c.getAssets(), "fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(), "fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(), "fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(), "fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(), "fonts/PDMS_Saleem_QuranFont.ttf");
        font = Typeface.createFromAsset(c.getAssets(),"fonts/Siyamrupali.ttf");
        fontTahaNaskh = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(c.getAssets(),"fonts/kitab.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.all_sura, parent, false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder = (RecyclerViewHolder) holder;
        Sura sura = suras.get(position);
        rvHolder.suraId.setText(sura.getSurah_id());
        rvHolder.nameArabicTxt.setText(sura.getName_arabic());
        rvHolder.nameSimpleTxt.setText(sura.getName_simple());
        rvHolder.nameEnglishTxt.setText(sura.getName_english());
        rvHolder.revelationPlaceTxt.setText("Revelation place: " + sura.getRevelation_place());
        rvHolder.ayatTxt.setText("Ayah: " + sura.getAyat());
        rvHolder.revelationOrderTxt.setText("Revelation order: " + sura.getRevelation_order());
        rvHolder.sid.setText(sura.getId());
        rvHolder.nameBangla.setText(sura.getName_bangla());

        rvHolder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpClass popUpClass = new PopUpClass();
                popUpClass.showPopupWindow(view, sura, c);
            }
        });

        rvHolder.middle_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(c, SuraDetailsActivity.class);
                in.putExtra("sura_id", sura.getSurah_id());
                in.putExtra("sura_name", sura.getName_english());
                in.putExtra("sura_name_arabic", sura.getName_arabic());
                c.startActivity(in);
            }
        });

        rvHolder.right_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(c, SuraDetailsActivity.class);
                in.putExtra("sura_id", sura.getSurah_id());
                in.putExtra("sura_name", sura.getName_english());
                in.putExtra("sura_name_arabic", sura.getName_arabic());
                c.startActivity(in);
            }
        });

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
        ImageButton playBtn;
        LinearLayout middle_section;
        LinearLayout right_section;
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
            playBtn = (ImageButton) itemView.findViewById(R.id.btnPlay);
            nameBangla = (TextView) itemView.findViewById(R.id.name_bangla);

            middle_section = (LinearLayout) itemView.findViewById(R.id.mid_section);
            right_section = (LinearLayout) itemView.findViewById(R.id.right_section);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
            if (mp_arabicFontFamily.equals("Al Majeed Quranic Font")) {
                nameArabicTxt.setTypeface(fontAlmajeed);
            }
            if (mp_arabicFontFamily.equals("Al Qalam Quran")) {
                nameArabicTxt.setTypeface(fontAlQalam);
            }
            if (mp_arabicFontFamily.equals("Noore Huda")) {
                nameArabicTxt.setTypeface(fontUthmani);
            }
            if (mp_arabicFontFamily.equals("Noore Hidayat")) {
                nameArabicTxt.setTypeface(fontNooreHidayat);
            }
            if (mp_arabicFontFamily.equals("Saleem Quran")) {
                nameArabicTxt.setTypeface(fontSaleem);
            }
            if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
                nameArabicTxt.setTypeface(fontTahaNaskh);
            }
            if(mp_arabicFontFamily.equals("Arabic Regular")){
                nameArabicTxt.setTypeface(fontKitab);
            }

            nameBangla.setTypeface(font);
        }
    }
}
