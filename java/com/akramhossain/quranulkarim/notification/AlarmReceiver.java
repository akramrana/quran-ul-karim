package com.akramhossain.quranulkarim.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.akramhossain.quranulkarim.task.PrayerScheduler;
import com.akramhossain.quranulkarim.util.Utils;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String key = intent.getStringExtra("prayer_key");
        if (key == null) key = "PRAYER";

        // Show notification
        String title = key.equals("FAJR") ? "Fajr" :
                key.equals("DHUHR") ? "Dhuhr" :
                        key.equals("ASR") ? "Asr" :
                                key.equals("MAGHRIB") ? "Maghrib" :
                                        key.equals("ISHA") ? "Isha" : "Prayer";

        int notifId = key.hashCode();
        NotificationHelper.show(context, notifId, key, title, "It's time for prayer");

        // After Isha → schedule tomorrow
        if ("ISHA".equals(key)) {
            double lat = Utils.getLat(context);
            double lon = Utils.getLon(context);
            double tz  = Utils.getTz(context);

            int calcMethod = Utils.getCalcMethod(context);
            int asrMethod  = Utils.getAsrMethod(context);

            PrayerScheduler.scheduleTomorrow(context, lat, lon, tz, calcMethod, asrMethod);
        }
    }
}