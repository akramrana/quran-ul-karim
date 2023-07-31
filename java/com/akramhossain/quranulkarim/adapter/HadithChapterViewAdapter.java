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
import com.akramhossain.quranulkarim.model.HadithChapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class HadithChapterViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<HadithChapter> hadithChapter;
    private Activity activity;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem;
    SharedPreferences mPrefs;

    public HadithChapterViewAdapter(Context c, ArrayList<HadithChapter> hadithChapter, Activity activity) {
        this.c = c;
        this.hadithChapter = hadithChapter;
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
        View v= LayoutInflater.from(c).inflate(R.layout.hadith_chapter_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.name_bangla.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        HadithChapter hc = hadithChapter.get(position);
        rvHolder.bid.setText(hc.getBid());
        rvHolder.name_bangla.setText(hc.getName_bangla());
        rvHolder.name_english.setText(hc.getName_english());
        rvHolder.name_arabic.setText(hc.getName_arabic());
        rvHolder.reference_book.setText(hc.getReference_book());
    }

    @Override
    public int getItemCount() {
        return hadithChapter.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView bid;
        TextView name_bangla;
        TextView name_english;
        TextView name_arabic;

        TextView reference_book;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            bid = (TextView) itemView.findViewById(R.id.bid);
            name_bangla = (TextView) itemView.findViewById(R.id.name_bangla);
            name_english = (TextView) itemView.findViewById(R.id.name_english);
            name_arabic = (TextView) itemView.findViewById(R.id.name_arabic);
            reference_book = (TextView) itemView.findViewById(R.id.reference_book);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
            String mp_arFz = mPrefs.getString("arFontSize", "30");
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");

            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                name_arabic.setTypeface(fontAlmajeed);
                name_arabic.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                name_arabic.setTypeface(fontAlQalam);
                name_arabic.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Noore Huda")){
                name_arabic.setTypeface(fontUthmani);
                name_arabic.setTypeface(fontUthmani);
            }
            if(mp_arabicFontFamily.equals("Noore Hidayat")){
                name_arabic.setTypeface(fontNooreHidayat);
                name_arabic.setTypeface(fontNooreHidayat);
            }
            if(mp_arabicFontFamily.equals("Saleem Quran")){
                name_arabic.setTypeface(fontSaleem);
                name_arabic.setTypeface(fontSaleem);
            }

            if (!mp_arFz.equals("")) {
                name_arabic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
            }
            if (!mp_enFz.equals("")) {
                name_english.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                reference_book.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
            }
            if (!mp_bnFz.equals("")) {
                name_bangla.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
            }

        }
    }
}
