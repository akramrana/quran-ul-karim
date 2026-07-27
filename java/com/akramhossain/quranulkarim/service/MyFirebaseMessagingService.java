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
import com.akramhossain.quranulkarim.helper.PushNotificationHelper;
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
        PushNotificationHelper.showNotification(
                this,
                title,
                body,
                data
        );
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