package com.akramhossain.quranulkarim.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.akramhossain.quranulkarim.DuaZikrActivity;
import com.akramhossain.quranulkarim.MainActivity;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.SuraDetailsActivity;

import java.util.Map;

import androidx.core.app.NotificationCompat;

public class PushNotificationHelper {

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "general_notifications";

    public static void showNotification(
            Context context,
            String title,
            String body,
            Map<String, String> data
    ) {
        createNotificationChannel(context);

        Intent intent;

        String target = data.get("target");

        if ("surah".equals(target)) {
            intent = new Intent(context, SuraDetailsActivity.class);

            String suraIdValue = data.get("sura_id");
            String suraName = data.get("sura_name");
            String suraNameArabic = data.get("sura_name_arabic");

            Log.d(TAG, "ID:" + suraIdValue);
            Log.d(TAG, "Name:" + suraName);
            Log.d(TAG, "Arabic:" + suraNameArabic);

            intent.putExtra("sura_id", suraIdValue);
            intent.putExtra("sura_name", suraName);
            intent.putExtra("sura_name_arabic", suraNameArabic);

        } else if ("dua".equals(target)) {

            intent = new Intent(context, DuaZikrActivity.class);

            String tagEn = data.get("tag_en");
            String tagBn = data.get("tag_bn");

            Log.d(TAG, "tag En:" + tagEn);
            Log.d(TAG, "tag Bn:" + tagBn);

            intent.putExtra("tag_en", tagEn);
            intent.putExtra("tag_bn", tagBn);

        } else {

            intent = new Intent(context, MainActivity.class);
        }

        int requestCode =
                (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(body)
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );

        if (manager != null) {
            int notificationId =
                    (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

            manager.notify(notificationId, builder.build());
        }
    }

    private static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "General notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription(
                    "General application notifications"
            );

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
