package com.akramhossain.quranulkarim.util;

import android.content.Context;

import java.io.File;

public class AudioStorage {

    public static File getAudioFile(Context context, String fileName) {
        File dir = new File(context.getExternalFilesDir(null), "audio");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, fileName);
    }

    public static boolean isAudioDownloaded(Context context, String fileName) {
        return AudioStorage.getAudioFile(context, fileName).exists();
    }

}
