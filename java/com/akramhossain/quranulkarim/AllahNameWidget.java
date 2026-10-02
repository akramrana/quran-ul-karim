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

import android.os.Bundle;
import android.util.TypedValue;

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

                        views.setTextViewText(
                                R.id.allah_name_bangla_meaning,
                                name.getJSONObject("bn")
                                        .getString("meaning")
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
                        views.setTextViewText(R.id.allah_name_bangla_meaning, "");
                    }

                    Intent openIntent = new Intent(appContext, MainActivity.class);

                    openIntent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );

                    PendingIntent openPendingIntent = PendingIntent.getActivity(
                            appContext,
                            widgetId,
                            openIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                                    | PendingIntent.FLAG_IMMUTABLE
                    );

                    views.setOnClickPendingIntent(
                            R.id.allah_name_widget_root,
                            openPendingIntent
                    );

                    applyFontSizes(
                            views,
                            appWidgetManager.getAppWidgetOptions(widgetId)
                    );

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

    private static void applyFontSizes(RemoteViews views, Bundle options) {
        int width = options.getInt(
                AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250
        );
        int height = options.getInt(
                AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 180
        );

        boolean compact = width < 200 || height < 150;

        views.setTextViewTextSize(
                R.id.allah_name_arabic,
                TypedValue.COMPLEX_UNIT_SP,
                compact ? 32 : 45
        );
        views.setTextViewTextSize(
                R.id.allah_name_english,
                TypedValue.COMPLEX_UNIT_SP,
                compact ? 16 : 21
        );
        views.setTextViewTextSize(
                R.id.allah_name_meaning,
                TypedValue.COMPLEX_UNIT_SP,
                compact ? 12 : 15
        );
        views.setTextViewTextSize(
                R.id.allah_name_bangla,
                TypedValue.COMPLEX_UNIT_SP,
                compact ? 13 : 17
        );
    }

    @Override
    public void onAppWidgetOptionsChanged(
            Context context,
            AppWidgetManager manager,
            int widgetId,
            Bundle newOptions
    ) {
        RemoteViews views = new RemoteViews(
                context.getPackageName(),
                R.layout.allah_name_widget
        );

        applyFontSizes(views, newOptions);
        manager.partiallyUpdateAppWidget(widgetId, views);
    }
}