package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.model.HadithBook;
import com.akramhossain.quranulkarim.model.MobileApp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class MobileAppViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context c;
    ArrayList<MobileApp> mobileApp;
    private Activity activity;

    public MobileAppViewAdapter(Context c, ArrayList<MobileApp> mobileApp, Activity activity) {
        this.c = c;
        this.mobileApp = mobileApp;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.mobile_app_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        return rvHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        MobileApp ma = mobileApp.get(position);
        rvHolder.id.setText(ma.getId());
        rvHolder.name.setText(ma.getName());
        rvHolder.url.setText(ma.getUrl());
        //rvHolder.img.setText(ma.getImg());
        Picasso.get().load(ma.getImg()).into(rvHolder.img);
    }

    @Override
    public int getItemCount() {
        return mobileApp.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView name;
        TextView url;
        ImageView img;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.name);
            url = (TextView) itemView.findViewById(R.id.url);
            img = (ImageView) itemView.findViewById(R.id.img);

        }
    }
}
