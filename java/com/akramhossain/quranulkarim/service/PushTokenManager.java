package com.akramhossain.quranulkarim.service;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PushTokenManager {

    private static final String TAG = "PushTokenManager";

    public static void sendTokenToServer(
            Context context,
            String token,
            String pushType
    ) {
        String url = "https://quran.codxplore.com/api/v1/save-device-token.php";

        String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        try {
            JSONObject body = new JSONObject();

            body.put("deviceId", androidId);
            body.put("pushType", pushType);
            body.put("pushToken", token);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> Log.d(
                            TAG,
                            "Token saved: " + response
                    ),
                    error -> Log.e(
                            TAG,
                            "Token save failed",
                            error
                    )
            );

            Volley.newRequestQueue(
                    context.getApplicationContext()
            ).add(request);

        } catch (JSONException exception) {
            Log.e(TAG, "Failed to create request body", exception);
        }
    }
}