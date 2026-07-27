package com.akramhossain.quranulkarim.service;

import android.content.Context;
import android.util.Log;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public final class HuaweiTokenManager {

    private static final String TAG = "HuaweiPush";

    private static final ExecutorService EXECUTOR =
            Executors.newSingleThreadExecutor();

    private HuaweiTokenManager() {
    }

    public static void getToken(Context context) {
        Context appContext = context.getApplicationContext();

        EXECUTOR.execute(() -> {
            try {
                String appId = AGConnectServicesConfig
                        .fromContext(appContext)
                        .getString("client/app_id");

                String token = HmsInstanceId
                        .getInstance(appContext)
                        .getToken(appId, "HCM");

                Log.d(TAG, "Manual Huawei token: " + token);

                if (token != null && !token.isEmpty()) {
                    // Upload token to your backend with provider = HMS
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(
                                    appContext,
                                    "Huawei token received",
                                    Toast.LENGTH_LONG
                            ).show()
                    );

                    PushTokenManager.sendTokenToServer(
                            appContext,
                            token,
                            "hms"
                    );
                }

            } catch (Exception exception) {
                Log.e(TAG, "Getting Huawei token failed", exception);
            }
        });
    }
}