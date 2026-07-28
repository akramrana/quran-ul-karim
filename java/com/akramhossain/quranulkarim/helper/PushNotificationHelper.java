package com.akramhossain.quranulkarim.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.akramhossain.quranulkarim.DailyGoalsActivity;
import com.akramhossain.quranulkarim.DictionaryActivity;
import com.akramhossain.quranulkarim.DuaZikrActivity;
import com.akramhossain.quranulkarim.HadithBookActivity;
import com.akramhossain.quranulkarim.MainActivity;
import com.akramhossain.quranulkarim.MosqueNearActivity;
import com.akramhossain.quranulkarim.NameOfAllahActivity;
import com.akramhossain.quranulkarim.PdfListActivity;
import com.akramhossain.quranulkarim.PrayerTimesActivity;
import com.akramhossain.quranulkarim.QiblaCompassActivity;
import com.akramhossain.quranulkarim.QuranReaderActivity;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.RamadanPlannerActivity;
import com.akramhossain.quranulkarim.ReciterActivity;
import com.akramhossain.quranulkarim.SubjectWiseActivity;
import com.akramhossain.quranulkarim.SuraDetailsActivity;
import com.akramhossain.quranulkarim.WordAnswerActivity;

import java.util.Map;

import androidx.core.app.NotificationCompat;

public class PushNotificationHelper {

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "general_notifications";

    public static void showNotification(
            Context context,
            String title,
            String body,
            Map<String, String> data
    ) {
        createNotificationChannel(context);

        Intent intent;

        String target = data.get("target");

        if ("surah".equals(target)) {
            intent = new Intent(context, SuraDetailsActivity.class);

            String suraIdValue = data.get("sura_id");
            String suraName = data.get("sura_name");
            String suraNameArabic = data.get("sura_name_arabic");

            Log.d(TAG, "ID:" + suraIdValue);
            Log.d(TAG, "Name:" + suraName);
            Log.d(TAG, "Arabic:" + suraNameArabic);

            intent.putExtra("sura_id", suraIdValue);
            intent.putExtra("sura_name", suraName);
            intent.putExtra("sura_name_arabic", suraNameArabic);

        } else if ("dua".equals(target)) {

            intent = new Intent(context, DuaZikrActivity.class);

            String tagEn = data.get("tag_en");
            String tagBn = data.get("tag_bn");

            Log.d(TAG, "tag En:" + tagEn);
            Log.d(TAG, "tag Bn:" + tagBn);

            intent.putExtra("tag_en", tagEn);
            intent.putExtra("tag_bn", tagBn);

        }else if ("name_of_allah".equals(target)) {
            intent = new Intent(context, NameOfAllahActivity.class);
        }
        else if ("daily_activity".equals(target)) {
            intent = new Intent(context, DailyGoalsActivity.class);
        }
        else if ("topic".equals(target)) {
            intent = new Intent(context, SubjectWiseActivity.class);
        }
        else if ("word".equals(target)) {
            intent = new Intent(context, DictionaryActivity.class);
        }
        else if ("madani_mushaf".equals(target)) {
            intent = new Intent(context, QuranReaderActivity.class);
        }
        else if ("prayer_time".equals(target)) {
            intent = new Intent(context, PrayerTimesActivity.class);
        }
        else if ("find_qibla".equals(target)) {
            intent = new Intent(context, QiblaCompassActivity.class);
        }
        else if ("recitation".equals(target)) {
            intent = new Intent(context, ReciterActivity.class);
        }
        else if ("masjid".equals(target)) {
            intent = new Intent(context, MosqueNearActivity.class);
        }
        else if ("hadith".equals(target)) {
            intent = new Intent(context, HadithBookActivity.class);
        }
        else if ("tafsir".equals(target)) {
            intent = new Intent(context, PdfListActivity.class);

            String tafsir_book_id = data.get("tafsir_book_id");
            String name_en = data.get("name_en");
            String name_ar = data.get("name_ar");
            String name_bn = data.get("name_bn");
            String thumb = data.get("thumb");
            String pdf_list_url = data.get("pdf_list_url");

            Log.d(TAG, "tafsir_book_id:" + tafsir_book_id);
            Log.d(TAG, "name_en:" + name_en);
            Log.d(TAG, "name_ar:" + name_ar);
            Log.d(TAG, "name_bn:" + name_bn);
            Log.d(TAG, "thumb:" + thumb);
            Log.d(TAG, "pdf_list_url:" + pdf_list_url);

            intent.putExtra("tafsir_book_id", tafsir_book_id);
            intent.putExtra("name_en", name_en);
            intent.putExtra("name_ar", name_ar);
            intent.putExtra("name_bn", name_bn);
            intent.putExtra("thumb", thumb);
            intent.putExtra("pdf_list_url", pdf_list_url);
        }
        else if ("word_challenge".equals(target)) {
            intent = new Intent(context, WordAnswerActivity.class);
        }
        else if ("ramadan_planner".equals(target)) {
            intent = new Intent(context, RamadanPlannerActivity.class);
        }
        else {
            intent = new Intent(context, MainActivity.class);
        }

        int requestCode =
                (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
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
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );

        if (manager != null) {
            int notificationId =
                    (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

            manager.notify(notificationId, builder.build());
        }
    }

    private static void createNotificationChannel(Context context) {

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
                    context.getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
