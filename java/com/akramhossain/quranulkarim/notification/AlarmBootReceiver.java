package com.akramhossain.quranulkarim.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.akramhossain.quranulkarim.task.PrayerScheduler;
import com.akramhossain.quranulkarim.util.Utils;

public class AlarmBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        double lat = Utils.getLat(context);
        double lon = Utils.getLon(context);
        double tz  = Utils.getTz(context);

        int calcMethod = Utils.getCalcMethod(context);
        int asrMethod  = Utils.getAsrMethod(context);

        // schedule remaining today, and after Isha it will schedule tomorrow automatically
        PrayerScheduler.scheduleToday(context, lat, lon, tz, calcMethod, asrMethod);
        if (!PrayerScheduler.anyScheduled) {
            PrayerScheduler.scheduleTomorrow(context, lat, lon, tz, calcMethod, asrMethod);
        }
    }
}
