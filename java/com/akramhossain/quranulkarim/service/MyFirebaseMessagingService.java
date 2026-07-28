package com.akramhossain.quranulkarim.service;

import android.util.Log;

import androidx.annotation.NonNull;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.helper.PushNotificationHelper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebasePush";
    private static final String CHANNEL_ID = "general_notifications";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "FCM token: " + token);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

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