package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.model.Leaderboard;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Leaderboard> leaderboard;
    private Activity activity;

    public LeaderboardViewAdapter(Context c, ArrayList<Leaderboard> leaderboard, Activity activity){
        this.c = c;
        this.leaderboard = leaderboard;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.leaderboard_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        Leaderboard lb = leaderboard.get(position);
        rvHolder.user_id.setText(lb.getUser_id());
        rvHolder.name.setText(lb.getName());
        rvHolder.right_ans.setText(lb.getRight_ans());
        rvHolder.rank.setText(lb.getRank());
        Picasso.get().load(lb.getFlag()).into(rvHolder.flag);
    }

    @Override
    public int getItemCount() {
        return leaderboard.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView user_id;
        TextView name;
        TextView right_ans;
        TextView rank;
        ImageView flag;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            user_id = (TextView) itemView.findViewById(R.id.user_id);
            name = (TextView) itemView.findViewById(R.id.name);
            right_ans = (TextView) itemView.findViewById(R.id.right_ans);
            rank = (TextView) itemView.findViewById(R.id.rank);
            flag = (ImageView) itemView.findViewById(R.id.flag);
        }
    }
}
