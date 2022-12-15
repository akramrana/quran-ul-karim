package com.akramhossain.quranulkarim.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlay {
    public static boolean isAudioPlaying = false;
    public static boolean isAudioLoaded = false;
    public static boolean isAudioStopped = false;
    public static MediaPlayer mp;
    public static String mp3Uri;

    public static void playAudio(Context context, String audioUri)
    {
        mp = new MediaPlayer();
        try
        {
            if (mp != null && mp.isPlaying())
            {
                mp.stop();
                mp.release();
                mp = null;
                return;
            }
            mp.setDataSource(audioUri);
            mp.prepare();
            int i = mp.getCurrentPosition();
            mp.seekTo(i);
            mp.start();
            isAudioPlaying = true;
            isAudioLoaded = true;
            isAudioStopped = false;
            mp3Uri = audioUri;
            return;
        }
        catch (Exception e)
        {
            Log.e("MP3", e.getMessage());
        }
    }

    public static void prepareAudio(Context context, String audioUri){
        mp = new MediaPlayer();
        try{
            if (mp != null && mp.isPlaying())
            {
                mp.stop();
                mp.release();
                mp = null;
                return;
            }
            mp.setDataSource(audioUri);
            mp.prepare();
            return;
        }
        catch (Exception e)
        {
            Log.e("MP3", e.getMessage());
        }
    }

    public static void stopAudio()
    {
        isAudioPlaying = false;
        isAudioLoaded = false;
        isAudioStopped = true;
        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
        return;
    }

    public static void startAudio()
    {
        if (mp != null){
            isAudioPlaying = true;
            isAudioStopped = false;
            mp.start();
        }
    }

    public static void pauseAudio()
    {
        if (isAudioPlaying)
        {
            isAudioPlaying = false;
            isAudioStopped = false;
            mp.pause();
        }
    }

    public static void resumeAudio()
    {
        if (mp != null) {
            int i = mp.getCurrentPosition();
            mp.seekTo(i);
            mp.start();
            isAudioPlaying = true;
            isAudioStopped = false;
        }
    }

    public static int getDuration(){
        int duration = 0;
        if (mp != null) {
            duration = mp.getDuration();
        }
        return duration;
    }

    public static int getCurrentPosition(){
        int position = 0;
        if (mp != null) {
            position = mp.getCurrentPosition();
        }
        return position;
    }

    public static void seekTo(int time){
        if (mp != null) {
            mp.seekTo(time);
        }
    }

    public static boolean isLoadedAudio(){
        return isAudioLoaded;
    }

    public static boolean isStopped(){
        if (mp != null && !mp.isPlaying()) {
            isAudioStopped = true;
        }
        return isAudioStopped;
    }

    public static String getAudioUri(){
        return mp3Uri;
    }

}
