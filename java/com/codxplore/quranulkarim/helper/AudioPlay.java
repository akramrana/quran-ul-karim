package com.codxplore.quranulkarim.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class AudioPlay {
    public static boolean isAudioPlaying = false;
    public static MediaPlayer mp;

    public static void playAudio(Context context, String audioUri)
    {
        mp = new MediaPlayer();
        try
        {
            if (mp.isPlaying())
            {
                mp.stop();
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

    public static void stopAudio()
    {
        if (isAudioPlaying)
        {
            isAudioPlaying = false;
            mp.stop();
        }
    }
}
