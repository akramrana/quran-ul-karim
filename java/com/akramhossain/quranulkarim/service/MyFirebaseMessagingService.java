package com.akramhossain.quranulkarim.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.akramhossain.quranulkarim.DuaZikrActivity;
import com.akramhossain.quranulkarim.MainActivity;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.SuraDetailsActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebasePush";
    private static final String CHANNEL_ID = "general_notifications";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "FCM token: " + token);

        // Send this token to your Node.js backend.
        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message sender: " + remoteMessage.getFrom());

        String title = getApplicationContext().getString(R.string.app_name);
        String body = "";

        Map<String, String> data = remoteMessage.getData();

        if (!data.isEmpty()) {
            Log.d(TAG, "Message data: " + data);

            if (data.containsKey("title")) {
                title = data.get("title");
            }

            if (data.containsKey("body")) {
                body = data.get("body");
            }
        }

        showNotification(title, body, data);
    }

    private void showNotification(
            String title,
            String body,
            Map<String, String> data
    ) {
        createNotificationChannel();

        Intent intent;

        String target = data.get("target");

        if ("surah".equals(target)) {
            intent = new Intent(this, SuraDetailsActivity.class);

            String suraIdValue = data.get("sura_id");
            String suraName = data.get("sura_name");
            String suraNameArabic = data.get("sura_name_arabic");

            Log.d(TAG,"ID:" + suraIdValue);
            Log.d(TAG,"Name:" + suraName);
            Log.d(TAG,"Arabic:" + suraNameArabic);

            intent.putExtra("sura_id", suraIdValue);
            intent.putExtra("sura_name", suraName);
            intent.putExtra("sura_name_arabic", suraNameArabic);


        } else if ("dua".equals(target)) {
            intent = new Intent(this, DuaZikrActivity.class);

            String tagEn = data.get("tag_en");
            String tagBn = data.get("tag_bn");

            Log.d(TAG,"tag En:" + tagEn);
            Log.d(TAG,"tag Bn:" + tagBn);

            intent.putExtra("tag_en", tagEn);
            intent.putExtra("tag_bn", tagBn);

        } else {
            intent = new Intent(this, MainActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
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
                (NotificationManager) getSystemService(
                        NOTIFICATION_SERVICE
                );

        if (manager != null) {
            int notificationId =
                    (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

            manager.notify(notificationId, builder.build());
        }
    }

    private void createNotificationChannel() {
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
                    getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void sendTokenToServer(String token) {
        // Call your backend API here.
        // Store the token against the logged-in user/device.
        PushTokenManager.sendTokenToServer(
                getApplicationContext(),
                token,
                "fcm"
        );
    }

}