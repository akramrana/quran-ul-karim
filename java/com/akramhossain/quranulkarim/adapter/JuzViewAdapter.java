package com.akramhossain.quranulkarim.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
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

    public JuzViewAdapter(Context c, ArrayList<Juz> juzs) {
        this.c = c;
        this.juzs = juzs;
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
        String tt = juz.getText_tashkeel();
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
        }

    }

}
