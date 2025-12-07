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

    private int playingPos = RecyclerView.NO_POSITION;

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

        boolean isThisPlaying = (position == playingPos) && AudioPlay.isAudioPlaying;

        holder.btnPlay.setImageResource(isThisPlaying
                ? android.R.drawable.ic_media_pause
                : android.R.drawable.ic_media_play);

        holder.btnPlay.setOnClickListener(v -> handlePlayClick(holder, position));
    }

    private void handlePlayClick(AudioViewHolder holder, int position) {
        if (position == RecyclerView.NO_POSITION) return;
        int previous = playingPos;
        if (position == playingPos) {
            if (AudioPlay.isAudioPlaying) {
                AudioPlay.pauseAudio();
            } else {
                AudioPlay.resumeAudio();
            }
            notifyItemChanged(position);
            startMonitor();
            return;
        }

        AudioPlay.stopAudio();
        playingPos = position;

        AudioItem item = items.get(position);
        AudioPlay.playAudio(context, item.url);

        if (previous != RecyclerView.NO_POSITION) notifyItemChanged(previous);
        notifyItemChanged(position);

        startMonitor();
    }

    private void startMonitor() {
        stopMonitor();
        monitorRunnable = new Runnable() {
            @Override public void run() {
                if (AudioPlay.isStopped()) {
                    int prev = playingPos;
                    playingPos = RecyclerView.NO_POSITION;
                    if (prev != RecyclerView.NO_POSITION) notifyItemChanged(prev);
                    stopMonitor();
                } else {
                    handler.postDelayed(this, 400);
                }
            }
        };
        handler.postDelayed(monitorRunnable, 400);
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

