package com.akramhossain.quranulkarim;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;

public class AllahNameWidget extends AppWidgetProvider {

    private static final String TAG = "AllahNameWidget";
    private static final String JSON_FILE = "99_Names_Of_Allah.json";

    @Override
    public void onUpdate(
            Context context,
            AppWidgetManager appWidgetManager,
            int[] appWidgetIds
    ) {
        BroadcastReceiver.PendingResult pendingResult = goAsync();
        Context appContext = context.getApplicationContext();

        new Thread(() -> {
            try {
                JSONObject name = getTodaysName(appContext);

                for (int widgetId : appWidgetIds) {
                    RemoteViews views = new RemoteViews(
                            appContext.getPackageName(),
                            R.layout.allah_name_widget
                    );

                    if (name != null) {

                        views.setTextViewText(
                                R.id.allah_name_arabic,
                                name.getString("name")
                        );

                        views.setTextViewText(
                                R.id.allah_name_english,
                                name.getString("transliteration")
                                        .toUpperCase(Locale.ENGLISH)
                        );

                        views.setTextViewText(
                                R.id.allah_name_bangla,
                                name.getJSONObject("bn")
                                        .getString("transliteration")
                        );

                        views.setTextViewText(
                                R.id.allah_name_meaning,
                                name.getJSONObject("en").getString("meaning")
                        );

                    } else {
                        views.setTextViewText(
                                R.id.allah_name_arabic,
                                "الله"
                        );
                        views.setTextViewText(
                                R.id.allah_name_english,
                                "NAMES OF ALLAH"
                        );
                        views.setTextViewText(R.id.allah_name_bangla, "");
                        views.setTextViewText(R.id.allah_name_meaning, "");
                    }

                    Intent openIntent = appContext.getPackageManager()
                            .getLaunchIntentForPackage(
                                    appContext.getPackageName()
                            );

                    if (openIntent != null) {
                        PendingIntent openPendingIntent =
                                PendingIntent.getActivity(
                                        appContext,
                                        0,
                                        openIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                                | PendingIntent.FLAG_IMMUTABLE
                                );

                        views.setOnClickPendingIntent(
                                R.id.allah_name_widget_root,
                                openPendingIntent
                        );
                    }

                    appWidgetManager.updateAppWidget(widgetId, views);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to update Names of Allah widget", e);
            } finally {
                pendingResult.finish();
            }
        }).start();
    }

    private static JSONObject getTodaysName(Context context)
            throws Exception {

        String json;

        try (InputStream input =
                     context.getAssets().open(JSON_FILE);
             ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int length;

            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }

            json = output.toString("UTF-8");
        }

        JSONArray names = new JSONObject(json).getJSONArray("data");

        if (names.length() == 0) {
            return null;
        }

        long hourNumber = System.currentTimeMillis()
                / (60L * 60L * 1000L);

        int index = (int) Math.floorMod(
                hourNumber,
                (long) names.length()
        );

        return names.getJSONObject(index);
    }
}