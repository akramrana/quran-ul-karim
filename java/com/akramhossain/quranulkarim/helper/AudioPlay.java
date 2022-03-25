package com.akramhossain.quranulkarim.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlay {
    public static boolean isAudioPlaying = false;
    public static MediaPlayer mp;

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
        if (isAudioPlaying)
        {
            isAudioPlaying = false;
            mp.stop();
        }
    }

    public static void startAudio()
    {
        if (mp != null){
            isAudioPlaying = true;
            mp.start();
        }
    }

    public static void pauseAudio()
    {
        if (isAudioPlaying)
        {
            isAudioPlaying = false;
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
        }
    }
}
