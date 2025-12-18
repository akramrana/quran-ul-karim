package com.akramhossain.quranulkarim.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.akramhossain.quranulkarim.MainActivity;
import com.akramhossain.quranulkarim.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


public class NotificationHelper {

    public static final String CHANNEL_ID = "quran_ul_karim_prayer_channel";

    public static void ensureChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    "Prayer Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            ch.setDescription("Prayer time notifications");

            NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    public static void show(Context ctx, int notifId, String prayerKey, String title, String body) {
        ensureChannel(ctx);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        if (!NotificationManagerCompat.from(ctx).areNotificationsEnabled()) {
            return;
        }
        Intent open = new Intent(ctx, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        open.putExtra("from_prayer_notification", true);
        open.putExtra("prayer_key", prayerKey);
        PendingIntent contentIntent = PendingIntent.getActivity(
                ctx,
                notifId,
                open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent);
        try {
            NotificationManagerCompat.from(ctx).notify(notifId, b.build());
        } catch (SecurityException ignored) {

        }
    }

}