package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.model.Tag;
import com.akramhossain.quranulkarim.util.Utils;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class TagViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Tag> tag;
    private Activity activity;
    Typeface font;
    SharedPreferences mPrefs;

    public TagViewAdapter(Context c, ArrayList<Tag> tag, Activity activity) {
        this.c = c;
        this.tag = tag;
        this.activity = activity;
        mPrefs = c.getSharedPreferences(Utils.PREF_NAME, 0);
        font = Typeface.createFromAsset(c.getAssets(), "fonts/Siyamrupali.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.tag_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.tag_bn.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        Tag tg = tag.get(position);
        rvHolder.tag_en.setText(tg.getTag_en());
        rvHolder.tag_bn.setText(tg.getTag_bn());
    }

    @Override
    public int getItemCount() {
        return tag.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView tag_en;
        TextView tag_bn;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            tag_en = (TextView) itemView.findViewById(R.id.tag_en);
            tag_bn = (TextView) itemView.findViewById(R.id.tag_bn);

            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");

            if (!mp_enFz.equals("") && mp_enFz != null) {
                try {
                    tag_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                }catch (NumberFormatException e) {
                    Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                }
            }
            if (!mp_bnFz.equals("") && mp_bnFz != null) {
                try {
                    tag_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                }catch (NumberFormatException e) {
                    Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                }
            }

        }

    }
}
