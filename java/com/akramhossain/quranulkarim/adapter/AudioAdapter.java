package com.akramhossain.quranulkarim.adapter;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.model.AudioItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private final Context context;
    private final List<AudioItem> items;
    private MediaPlayer player;
    private int currentPos = -1;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable monitorRunnable;

    public AudioAdapter(Context context, List<AudioItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false);
        return new AudioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioItem item = items.get(position);
        holder.txtTitle.setText(item.title);
        holder.txtSubtitle.setText(item.artist + " • " + formatDuration(item.duration));

        holder.btnPlay.setOnClickListener(v -> handlePlayClick(holder, position));
    }

    private void handlePlayClick(AudioViewHolder holder, int position) {
        AudioItem item = items.get(position);
        try {
            AudioPlay.stopAudio();
            holder.btnPlay.setImageResource(android.R.drawable.ic_media_play);
            AudioPlay.playAudio(context,item.url);

            holder.btnPlay.setImageResource(AudioPlay.isAudioPlaying
                    ? android.R.drawable.ic_media_pause
                    : android.R.drawable.ic_media_play);

            startMonitor(holder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMonitor(AudioViewHolder holder) {
        stopMonitor(); // stop old monitor
        monitorRunnable = new Runnable() {
            @Override
            public void run() {
                boolean stopped = AudioPlay.isStopped();
                if (stopped) {
                    holder.btnPlay.setImageResource(android.R.drawable.ic_media_play);
                    stopMonitor(); // stop checking once audio is done
                } else {
                    handler.postDelayed(this, 500); // recheck every 0.5s
                }
            }
        };
        handler.postDelayed(monitorRunnable, 500);
    }

    private void stopMonitor() {
        if (monitorRunnable != null) {
            handler.removeCallbacks(monitorRunnable);
            monitorRunnable = null;
        }
    }

    @Override
    public void onViewRecycled(@NonNull AudioViewHolder holder) {
        super.onViewRecycled(holder);
        stopMonitor();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        stopMonitor();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatDuration(String seconds) {
        try {
            double s = Double.parseDouble(seconds);
            int total = (int) Math.round(s);
            int min = total / 60;
            int sec = total % 60;
            return String.format("%d:%02d", min, sec);
        } catch (Exception e) {
            return seconds;
        }
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtSubtitle;
        ImageButton btnPlay;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSubtitle = itemView.findViewById(R.id.txtSubtitle);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }
}

