package com.akramhossain.quranulkarim.task;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.akramhossain.quranulkarim.notification.AlarmReceiver;
import com.akramhossain.quranulkarim.util.PrayTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PrayerScheduler {

    public static final int RC_FAJR = 101;
    public static final int RC_DHUHR = 102;
    public static final int RC_ASR = 103;
    public static final int RC_MAGHRIB = 104;
    public static final int RC_ISHA = 105;

    public static volatile boolean anyScheduled = false;

    public static void scheduleToday(Context ctx, double lat, double lon, double tz,
                                     int calcMethod, int asrJuristicMethod) {
        anyScheduled = false;
        Calendar day = Calendar.getInstance();
        scheduleForDay(ctx, day, lat, lon, tz, calcMethod, asrJuristicMethod);
    }

    public static void scheduleTomorrow(Context ctx, double lat, double lon, double tz,
                                        int calcMethod, int asrJuristicMethod) {
        Calendar day = Calendar.getInstance();
        day.add(Calendar.DAY_OF_YEAR, 1);
        scheduleForDay(ctx, day, lat, lon, tz, calcMethod, asrJuristicMethod);
    }

    private static void scheduleForDay(Context ctx, Calendar day, double lat, double lon, double tz,
                                       int calcMethod, int asrJuristicMethod) {
        try {
            PrayTime prayers = new PrayTime();

            prayers.setTimeFormat(prayers.Time12);
            prayers.setCalcMethod(calcMethod);
            prayers.setAsrJuristic(asrJuristicMethod);
            prayers.setAdjustHighLats(prayers.AngleBased);
            prayers.tune(new int[]{0,0,0,0,0,0,0});

            ArrayList<String> times = prayers.getPrayerTimes(day, lat, lon, tz);

            Log.d("PrayerScheduler", "Actual: " + times.toString());

            // Your note: Maghrib == Sunset, so use index 4
            long fajr = parseDayMillis(day, times.get(0));
            long dhuhr = parseDayMillis(day, times.get(2));
            long asr = parseDayMillis(day, times.get(3));
            long maghrib = parseDayMillis(day, times.get(4));
            long isha = parseDayMillis(day, times.get(6));

            // If scheduling for "today", skip past times
            long now = System.currentTimeMillis();

            if (fajr > now) {
                scheduleAlarm(ctx, RC_FAJR, fajr, "FAJR");
                anyScheduled = true;
            }
            if (dhuhr > now) {
                scheduleAlarm(ctx, RC_DHUHR, dhuhr, "DHUHR");
                anyScheduled = true;
            }
            if (asr > now) {
                scheduleAlarm(ctx, RC_ASR, asr, "ASR");
                anyScheduled = true;
            }
            if (maghrib > now) {
                scheduleAlarm(ctx, RC_MAGHRIB, maghrib, "MAGHRIB");
                anyScheduled = true;
            }
            if (isha > now) {
                scheduleAlarm(ctx, RC_ISHA, isha, "ISHA");
                anyScheduled = true;
            }

            Log.d("PrayerScheduler", "Scheduled: " + times.toString());

        } catch (Exception e) {
            Log.e("PrayerScheduler", "scheduleForDay failed", e);
        }
    }

    private static long parseDayMillis(Calendar day, String time12h) throws Exception {
        // Example: "05:12 am"
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        Date t = sdf.parse(time12h.trim().toUpperCase(Locale.ENGLISH));

        Calendar c = Calendar.getInstance();
        c.setTime(t);

        c.set(Calendar.YEAR, day.get(Calendar.YEAR));
        c.set(Calendar.MONTH, day.get(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, day.get(Calendar.DAY_OF_MONTH));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    private static void scheduleAlarm(Context ctx, int requestCode, long whenMillis, String prayerKey) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent i = new Intent(ctx, AlarmReceiver.class);
        i.putExtra("prayer_key", prayerKey);

        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, requestCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        am.cancel(pi);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenMillis, pi);
                return;
            }
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenMillis, pi);

        } catch (SecurityException e) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenMillis, pi);
        }
    }

    public static void cancelAll(Context ctx) {
        cancel(ctx, RC_FAJR);
        cancel(ctx, RC_DHUHR);
        cancel(ctx, RC_ASR);
        cancel(ctx, RC_MAGHRIB);
        cancel(ctx, RC_ISHA);
    }

    private static void cancel(Context ctx, int requestCode) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, requestCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (am != null) am.cancel(pi);
    }

}
