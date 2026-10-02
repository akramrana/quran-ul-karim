package com.akramhossain.quranulkarim;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;

import com.akramhossain.quranulkarim.model.Ayah;
import com.akramhossain.quranulkarim.repository.WidgetAyahRepository;

import android.os.Bundle;
import android.util.TypedValue;

public class DailyAyahBnWidget extends AppWidgetProvider {

    private static final String ACTION_NEXT_AYAH = "com.akramhossain.quranulkarim.NEXT_WIDGET_AYAH";

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
                // Load once, even if the user has added multiple copies.
                Ayah ayah = WidgetAyahRepository.getDailyAyah(appContext);

                for (int widgetId : appWidgetIds) {
                    RemoteViews views = new RemoteViews(
                            appContext.getPackageName(),
                            R.layout.daily_ayah_bn_widget
                    );

                    if (ayah != null) {
                        views.setTextViewText(
                                R.id.ayah_arabic,
                                ayah.getText_tashkeel()
                        );
                        views.setTextViewText(
                                R.id.ayah_translation,
                                ayah.getContent_bn()
                        );
                        views.setTextViewText(
                                R.id.ayah_reference,
                                ayah.getName_simple() + " " +
                                        ayah.getSurah_id() + ":" + ayah.getAyah_num()
                        );

                        views.setTextViewText(R.id.ayah_english, ayah.getContent_en());

                    } else {
                        views.setTextViewText(R.id.ayah_arabic, "");
                        views.setTextViewText(
                                R.id.ayah_translation,
                                "Today's ayah is unavailable"
                        );
                        views.setTextViewText(R.id.ayah_english, "");
                        views.setTextViewText(R.id.ayah_reference, "");
                    }

                    Intent nextIntent = new Intent(appContext, DailyAyahBnWidget.class);
                    nextIntent.setAction(ACTION_NEXT_AYAH);

                    PendingIntent nextPendingIntent = PendingIntent.getBroadcast(
                            appContext,
                            0,
                            nextIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    views.setOnClickPendingIntent(
                            R.id.ayah_refresh,
                            nextPendingIntent
                    );

                    Intent openIntent = appContext.getPackageManager()
                            .getLaunchIntentForPackage(appContext.getPackageName());

                    if (openIntent != null) {
                        openIntent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                        );

                        PendingIntent openPendingIntent = PendingIntent.getActivity(
                                appContext,
                                0,
                                openIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        views.setOnClickPendingIntent(
                                R.id.ayah_widget_root,
                                openPendingIntent
                        );
                    }

                    applyFontSizes(
                            views,
                            appWidgetManager.getAppWidgetOptions(widgetId)
                    );

                    appWidgetManager.updateAppWidget(widgetId, views);
                }
            } catch (Exception e) {
                Log.e("DailyAyahBnWidget", "Failed to update widget", e);
            } finally {
                pendingResult.finish();
            }
        }).start();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_NEXT_AYAH.equals(intent.getAction())) {
            WidgetAyahRepository.showNextAyah(context);

            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] widgetIds = manager.getAppWidgetIds(
                    new ComponentName(context, DailyAyahBnWidget.class)
            );

            onUpdate(context, manager, widgetIds);
            return;
        }

        super.onReceive(context, intent);
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
                R.id.ayah_arabic,
                TypedValue.COMPLEX_UNIT_SP,
                compact ? 20 : 25
        );
        views.setTextViewTextSize(
                R.id.ayah_english,
                TypedValue.COMPLEX_UNIT_SP,
                compact ? 11 : 13
        );
        views.setTextViewTextSize(
                R.id.ayah_translation,
                TypedValue.COMPLEX_UNIT_SP,
                compact ? 12 : 14
        );
        views.setTextViewTextSize(
                R.id.ayah_reference,
                TypedValue.COMPLEX_UNIT_SP,
                compact ? 10 : 13
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
                R.layout.daily_ayah_bn_widget
        );

        applyFontSizes(views, newOptions);
        manager.partiallyUpdateAppWidget(widgetId, views);
    }
}