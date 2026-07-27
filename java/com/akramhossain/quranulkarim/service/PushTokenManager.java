package com.akramhossain.quranulkarim.service;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.akramhossain.quranulkarim.helper.SessionManager;
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

        SessionManager session = new SessionManager(context.getApplicationContext());
        String userId = null;
        if (session.isLoggedIn()) {
            try {
                JSONObject response = new JSONObject(session.getLoginData());
                userId = response.optString("user_id", null);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse login data", e);
            }
        }

        try {
            JSONObject body = new JSONObject();

            body.put("deviceId", androidId);
            body.put("pushType", pushType);
            body.put("pushToken", token);

            if (userId != null) {
                body.put("userId", userId);
            }

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