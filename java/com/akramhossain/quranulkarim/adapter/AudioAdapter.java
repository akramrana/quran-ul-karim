package com.akramhossain.quranulkarim.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.model.AudioItem;
import com.akramhossain.quranulkarim.util.AudioStorage;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private final Context context;
    private final List<AudioItem> items;
    private MediaPlayer player;
    private int currentPos = -1;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable monitorRunnable;

    private int playingPos = RecyclerView.NO_POSITION;
    private AudioViewHolder activeHolder = null;

    private Handler seekHandler;

    public AudioAdapter(Context context, List<AudioItem> items) {
        this.context = context;
        this.items = items;
        seekHandler = new Handler();
        seekHandler.removeCallbacksAndMessages(null);
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

        holder.seekBar.setVisibility(isThisPlaying ? View.VISIBLE : View.GONE);

        if (isThisPlaying) {
            int current = AudioPlay.getCurrentPosition();
            int duration = AudioPlay.getDuration();

            if (duration > 0) {
                holder.seekBar.setMax(duration);
                holder.seekBar.setProgress(current);
            }
        }

        holder.btnPlay.setOnClickListener(v -> handlePlayClick(holder, position));

        String fileName = item.qariId+"_"+item.title.replace(" ", "_") + ".mp3";
        Log.d("fileName",fileName);
        boolean isDownloaded = AudioStorage.isAudioDownloaded(context, fileName);

        holder.btnDownload.setVisibility(isDownloaded ? GONE : VISIBLE);

        holder.btnDownload.setOnClickListener(v -> {
            downloadAudio(item.url, fileName, position);
        });

    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            // Full bind (first time or real data change)
            onBindViewHolder(holder, position);
        } else {
            // Partial bind – only update what changed
            for (Object payload : payloads) {
                if ("PROGRESS_UPDATE".equals(payload)) {
                    if (position == playingPos && AudioPlay.isAudioPlaying && AudioPlay.mp != null) {
                        int current = AudioPlay.getCurrentPosition();
                        int duration = AudioPlay.getDuration();
                        if (duration > 0) {
                            holder.seekBar.setMax(duration);
                            holder.seekBar.setProgress(current);
                        }
                    }
                }
            }
        }
    }

    private void downloadAudio(String url, String fileName, int position) {

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationUri(Uri.fromFile(AudioStorage.getAudioFile(context, fileName)));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedOverMetered(true);
        request.setTitle(fileName);

        manager.enqueue(request);

        notifyItemChanged(position);
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

        String fileName = item.qariId+"_"+item.title.replace(" ", "_") + ".mp3";
        File localFile = AudioStorage.getAudioFile(context, fileName);

        if (localFile.exists()) {
            AudioPlay.playAudio(context, localFile.getAbsolutePath());
        }else {
            AudioPlay.playAudio(context, item.url);
        }

        if (previous != RecyclerView.NO_POSITION) notifyItemChanged(previous);

        notifyItemChanged(position);

        startMonitor();

        seekHandler.postDelayed(seekRunnable, 300);
    }

    private Runnable seekRunnable = new Runnable() {
        @Override
        public void run() {
            boolean isAudioStopped = AudioPlay.isStopped();
            if(isAudioStopped || AudioPlay.mp == null){
                seekHandler.removeCallbacks(this);
            }else {
                if (playingPos != -1 && AudioPlay.mp != null) {
                    int pos = AudioPlay.getCurrentPosition();
                    int dur = AudioPlay.getDuration();
                    if (playingPos != RecyclerView.NO_POSITION) {
                        notifyItemChanged(playingPos, "PROGRESS_UPDATE");
                    }
                    //Log.d("seek","seek pos"+pos);
                    seekHandler.postDelayed(this, 300);
                }
            }
        }
    };

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

    public void release() {
        stopMonitor();
        AudioPlay.stopAudio();
        if (seekRunnable != null) {
            seekHandler.removeCallbacks(seekRunnable);
        }
        int prev = playingPos;
        playingPos = RecyclerView.NO_POSITION;
        if (prev != RecyclerView.NO_POSITION) notifyItemChanged(prev);
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
        ImageButton btnPlay, btnDownload;
        SeekBar seekBar;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSubtitle = itemView.findViewById(R.id.txtSubtitle);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            seekBar = itemView.findViewById(R.id.seekBar);
        }
    }
}

