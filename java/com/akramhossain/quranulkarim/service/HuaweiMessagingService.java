package com.akramhossain.quranulkarim.service;

import android.os.Bundle;
import android.util.Log;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.helper.PushNotificationHelper;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class HuaweiMessagingService extends HmsMessageService {

    private static final String TAG = "HuaweiPush";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d(TAG, "Huawei token: " + token);

        saveHuaweiToken(token);
    }

    @Override
    public void onNewToken(String token, Bundle bundle) {
        super.onNewToken(token, bundle);

        Log.d(TAG, "Huawei token with bundle: " + token);

        saveHuaweiToken(token);
    }

    @Override
    public void onTokenError(Exception exception) {
        super.onTokenError(exception);

        Log.e(TAG, "Huawei token error", exception);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Huawei message received");

        String title = getApplicationContext().getString(R.string.app_name);

        String body = "";

        Map<String, String> data = new HashMap<>();

        Map<String, String> huaweiData =
                remoteMessage.getDataOfMap();

        if (huaweiData != null && !huaweiData.isEmpty()) {
            data.putAll(huaweiData);

            Log.d(TAG, "Huawei message data: " + data);

            String dataTitle = data.get("title");
            String dataBody = data.get("body");

            if (dataTitle != null && !dataTitle.isEmpty()) {
                title = dataTitle;
            }

            if (dataBody != null && !dataBody.isEmpty()) {
                body = dataBody;
            }
        }

        RemoteMessage.Notification notification =
                remoteMessage.getNotification();

        if (notification != null) {

            if (!data.containsKey("title")) {
                String notificationTitle = notification.getTitle();

                if (notificationTitle != null
                        && !notificationTitle.isEmpty()) {

                    title = notificationTitle;
                    data.put("title", notificationTitle);
                }
            }

            if (!data.containsKey("body")) {
                String notificationBody = notification.getBody();

                if (notificationBody != null
                        && !notificationBody.isEmpty()) {

                    body = notificationBody;
                    data.put("body", notificationBody);
                }
            }
        }

        showNotification(title, body, data);
    }

    private void saveHuaweiToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }
        PushTokenManager.sendTokenToServer(
                getApplicationContext(),
                token,
                "hms"
        );
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
}