package com.akramhossain.quranulkarim.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import android.os.Handler;
import android.os.Looper;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

public class AudioPlay {

    public static MediaPlayer mp;
    public static boolean isAudioPlaying = false;
    private static boolean isAudioLoaded = false;
    private static boolean isAudioStopped = false;
    private static String mp3Uri;

    private static ExoPlayer player;

    private AudioPlay() {
        // Private constructor to prevent instantiation
    }

    public static synchronized void playAudio(Context context, String audioUri) {
        try {
            initializeMediaPlayer(audioUri);
            mp.start();
            isAudioPlaying = true;
            isAudioLoaded = true;
            isAudioStopped = false;
            mp3Uri = audioUri;
        } catch (Exception e) {
            Log.e("MP3", e.toString());
        }
    }

    public static synchronized void prepareAudio(Context context, String audioUri) {
        try {
            initializeMediaPlayer(audioUri);
        } catch (Exception e) {
            Log.e("MP3", e.toString());
        }
    }

    public static synchronized void stopAudio() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.reset();
            mp.release();
            mp = null;
        }

        // ExoPlayer
        stopExoAudio();

        resetAudioFlags();
    }

    public static synchronized void startAudio() {
        if (mp != null) {
            mp.start();
            isAudioPlaying = true;
            isAudioStopped = false;
        }
    }

    public static synchronized void pauseAudio() {
        if (mp != null && isAudioPlaying) {
            mp.pause();
            isAudioPlaying = false;
            isAudioStopped = false;
        }
    }

    public static synchronized void resumeAudio() {
        if (mp != null) {
            mp.start();
            isAudioPlaying = true;
            isAudioStopped = false;
        }
    }

    public static synchronized int getDuration() {
        return mp != null ? mp.getDuration() : 0;
    }

    public static synchronized int getCurrentPosition() {
        return mp != null ? mp.getCurrentPosition() : 0;
    }

    public static synchronized void seekTo(int time) {
        if (mp != null) {
            mp.seekTo(time);
        }
    }

    public static synchronized boolean isLoadedAudio() {
        return isAudioLoaded;
    }

    public static synchronized boolean isStopped() {
        isAudioStopped = mp == null || !mp.isPlaying();
        return isAudioStopped;
    }

    public static synchronized String getAudioUri() {
        return mp3Uri;
    }

    private static void initializeMediaPlayer(String audioUri) throws Exception {
        if (mp != null) {
            mp.reset();
            mp.release();
        }
        mp = new MediaPlayer();
        mp.setDataSource(audioUri);
        mp.prepare();
        isAudioPlaying = false;
        isAudioLoaded = true;
        isAudioStopped = false;
    }

    private static void resetAudioFlags() {
        isAudioPlaying = false;
        isAudioLoaded = false;
        isAudioStopped = true;
        mp3Uri = null;
    }

    public static boolean isPlayableAudio(String path) {
        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(path);
            mp.prepare();

            int duration = mp.getDuration();
            Log.d("AUDIO", "MediaPlayer duration = " + duration);

            return true; // playable even if duration is 0
        } catch (Exception e) {
            Log.e("AUDIO", "Not playable", e);
            return false;
        } finally {
            mp.release();
        }
    }

    private static void runOnMain(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }

    public static void playExoAudio(Context context, String audioUri) {
        runOnMain(() -> {
            try {
                stopAudio();

                player = new ExoPlayer.Builder(context.getApplicationContext()).build();
                player.setMediaItem(MediaItem.fromUri(Uri.parse(audioUri)));
                player.prepare();
                player.play();

                isAudioPlaying = true;
                isAudioLoaded = true;
                isAudioStopped = false;
                mp3Uri = audioUri;

            } catch (Exception e) {
                Log.e("PLAYER", "ExoPlayer error", e);
            }
        });
    }

    private static void stopExoAudio() {
        if (player == null) return;

        ExoPlayer temp = player;
        player = null;

        runOnMain(() -> {
            try {
                temp.stop();
                temp.release();
            } catch (Exception e) {
                Log.e("EXO", "Release error", e);
            }
        });
    }
}
