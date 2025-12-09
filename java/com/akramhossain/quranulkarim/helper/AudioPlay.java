package com.akramhossain.quranulkarim.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlay {

    public static MediaPlayer mp;
    public static boolean isAudioPlaying = false;
    private static boolean isAudioLoaded = false;
    private static boolean isAudioStopped = false;
    private static String mp3Uri;

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
}
