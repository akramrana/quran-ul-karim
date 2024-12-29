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
import com.akramhossain.quranulkarim.model.HadithBook;
import com.akramhossain.quranulkarim.util.Utils;

import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class HadithBookViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<HadithBook> hadithBook;
    private Activity activity;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;
    SharedPreferences mPrefs;
    String screen;

    public HadithBookViewAdapter(Context c, ArrayList<HadithBook> hadithBook, Activity activity, String screen) {
        this.c = c;
        this.hadithBook = hadithBook;
        this.activity = activity;
        this.screen = screen;
        mPrefs = c.getSharedPreferences(Utils.PREF_NAME, 0);
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
        View v;
        if(screen.equals("MA")){
            v = LayoutInflater.from(c).inflate(R.layout.hadith_book_list_home, parent, false);
        }else {
            v = LayoutInflater.from(c).inflate(R.layout.hadith_book_list, parent, false);
        }
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.name_bangla.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        HadithBook hb = hadithBook.get(position);
        rvHolder.book_id.setText(hb.getBook_id());
        rvHolder.name_bangla.setText(hb.getName_bangla());
        rvHolder.name_english.setText(hb.getName_english());
        rvHolder.name_arabic.setText(hb.getName_arabic());
    }

    @Override
    public int getItemCount() {
        return hadithBook.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView book_id;
        TextView name_bangla;
        TextView name_english;
        TextView name_arabic;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            book_id = (TextView) itemView.findViewById(R.id.book_id);
            name_bangla = (TextView) itemView.findViewById(R.id.name_bangla);
            name_english = (TextView) itemView.findViewById(R.id.name_english);
            name_arabic = (TextView) itemView.findViewById(R.id.name_arabic);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
            String mp_arFz = mPrefs.getString("arFontSize", "30");
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");

            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                name_arabic.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                name_arabic.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Noore Huda")){
                name_arabic.setTypeface(fontUthmani);
            }
            if(mp_arabicFontFamily.equals("Noore Hidayat")){
                name_arabic.setTypeface(fontNooreHidayat);
            }
            if(mp_arabicFontFamily.equals("Saleem Quran")){
                name_arabic.setTypeface(fontSaleem);
            }
            if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
                name_arabic.setTypeface(fontTahaNaskh);
            }
            if(mp_arabicFontFamily.equals("Arabic Regular")){
                name_arabic.setTypeface(fontKitab);
            }

//            if (!mp_arFz.equals("")) {
//                name_arabic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
//            }
//            if (!mp_enFz.equals("")) {
//                name_english.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
//            }
//            if (!mp_bnFz.equals("")) {
//                name_bangla.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
//            }

        }
    }
}
