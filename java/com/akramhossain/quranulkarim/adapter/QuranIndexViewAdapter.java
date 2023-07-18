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
import com.akramhossain.quranulkarim.model.QuranIndex;

import org.w3c.dom.Text;

import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class QuranIndexViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<QuranIndex> quranIndex;
    private Activity activity;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem;
    SharedPreferences mPrefs;

    public QuranIndexViewAdapter(Context c, ArrayList<QuranIndex> quranIndex, Activity activity) {
        this.c = c;
        this.quranIndex = quranIndex;
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
        View v= LayoutInflater.from(c).inflate(R.layout.quran_index_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.bn.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        QuranIndex qi = quranIndex.get(position);
        rvHolder.en.setText(qi.getEn());
        rvHolder.bn.setText(qi.getBn());
        //rvHolder.verses.setText(qi.getVerses());
        //rvHolder.tags.setText(qi.getTags());
        String verses = qi.getVerses();
        String[] ayahList = verses.split(",");
        rvHolder.ayah_num.setText("No.of Ayah: "+ayahList.length);
    }

    @Override
    public int getItemCount() {
        return quranIndex.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView bn;
        //TextView tags;
        TextView en;
        //TextView verses;
        TextView ayah_num;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");

            bn = (TextView) itemView.findViewById(R.id.bn);
            //tags = (TextView) itemView.findViewById(R.id.tags);
            en = (TextView) itemView.findViewById(R.id.en);
            //verses = (TextView) itemView.findViewById(R.id.verses);
            ayah_num = (TextView) itemView.findViewById(R.id.ayah_count);

            if (!mp_bnFz.equals("")) {
                bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
            }
            if (!mp_enFz.equals("")) {
                en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
            }
        }

    }

}
