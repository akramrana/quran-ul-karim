package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.model.RamadanPlanner;
import com.akramhossain.quranulkarim.util.Utils;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class RamadanPlannerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<RamadanPlanner> ramadanPlanner;
    private Activity activity;
    Typeface font;
    SharedPreferences mPrefs;

    public RamadanPlannerViewAdapter(Context c, ArrayList<RamadanPlanner> ramadanPlanner, Activity activity) {
        this.c = c;
        this.ramadanPlanner = ramadanPlanner;
        this.activity = activity;

        mPrefs = c.getSharedPreferences(Utils.PREF_NAME, 0);
        font = Typeface.createFromAsset(c.getAssets(), "fonts/Siyamrupali.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.ramadan_plan_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.name_bn.setTypeface(font);
        return rvHolder;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        RamadanPlanner rm = ramadanPlanner.get(position);
        rvHolder.ramadan_planner_id.setText(rm.getRamadan_planner_id());
        rvHolder.name_en.setText(rm.getName_en());
        rvHolder.name_bn.setText(rm.getName_bn());
    }

    @Override
    public int getItemCount() {
        return ramadanPlanner.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView ramadan_planner_id;
        TextView name_en;
        TextView name_bn;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            ramadan_planner_id = (TextView) itemView.findViewById(R.id.ramadan_planner_id);
            name_en = (TextView) itemView.findViewById(R.id.name_en);
            name_bn = (TextView) itemView.findViewById(R.id.name_bn);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");
            String mp_arFz = mPrefs.getString("arFontSize", "30");

            /*if (!mp_enFz.equals("") && mp_enFz !=null) {
                try {
                    name_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                }catch (NumberFormatException e) {
                    Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                }
            }

            if (!mp_bnFz.equals("") && mp_bnFz !=null) {
                try {
                    name_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                }catch (NumberFormatException e) {
                    Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                }
            }*/

        }
    }
}
