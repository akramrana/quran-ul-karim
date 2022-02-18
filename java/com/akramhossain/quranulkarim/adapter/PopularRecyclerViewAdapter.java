package com.akramhossain.quranulkarim.adapter;

import android.content.Context;
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

    public PopularRecyclerViewAdapter(Context c, ArrayList<Sura> suras) {
        this.c = c;
        this.suras = suras;
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
        }
    }
}
