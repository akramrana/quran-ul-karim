package com.akramhossain.quranulkarim.notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.SuraDetailsActivity;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;

import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "quranulkarim";
    //DatabaseHelper dbhelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get notification manager to manage/send notifications

        if(isAppIsInBackground(context)) {


            DatabaseHelper dbhelper = new DatabaseHelper(context);

            SQLiteDatabase db = dbhelper.getWritableDatabase();
            String sql = "SELECT sura.surah_id,sura.name_english,sura.name_arabic,sura.name_simple,ayah.ayah_num,ayah.text_tashkeel,ayah.content_en \n" +
                    "FROM ayah \n" +
                    "LEFT JOIN sura ON ayah.surah_id = sura.surah_id \n" +
                    "ORDER BY RANDOM() LIMIT 1";

            Cursor ayah = db.rawQuery(sql,null);
            String ayahDescription = "";
            String text_tashkeel = "";
            String suraName = "";
            String ayah_num = "";
            String surah_id = "";
            String name_english = "";
            String name_arabic = "";
            try {
                if(ayah.moveToFirst()) {
                    text_tashkeel = ayah.getString(ayah.getColumnIndex("text_tashkeel")).toString();
                    ayahDescription = ayah.getString(ayah.getColumnIndex("content_en")).toString();
                    suraName = ayah.getString(ayah.getColumnIndex("name_simple")).toString();
                    ayah_num = ayah.getString(ayah.getColumnIndex("ayah_num")).toString();
                    surah_id = ayah.getString(ayah.getColumnIndex("surah_id")).toString();
                    name_english = ayah.getString(ayah.getColumnIndex("name_english")).toString();
                    name_arabic = ayah.getString(ayah.getColumnIndex("name_arabic")).toString();

                    //Intent to invoke app when click on notification.
                    //In this sample, we want to start/launch this sample app when user clicks on notification
                    Intent intentToRepeat = new Intent(context, SuraDetailsActivity.class);
                    intentToRepeat.putExtra("sura_id",surah_id);
                    intentToRepeat.putExtra("sura_name",name_english);
                    intentToRepeat.putExtra("sura_name_arabic",name_arabic);
                    //set flag to restart/relaunch the app
                    intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    //Pending intent to handle launch of Activity in intent above
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(context, NotificationHelper.ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);
                    //Build notification
                    Notification repeatedNotification = buildLocalNotification(context, pendingIntent,text_tashkeel,ayahDescription,suraName,ayah_num,surah_id).build();

                    //Send local notification
                    NotificationHelper.getNotificationManager(context).notify(NotificationHelper.ALARM_TYPE_RTC, repeatedNotification);
                }
            }catch (Exception e){
                Log.i("On Scroll Count Check", e.getMessage());
            }finally {
                if (ayah != null && !ayah.isClosed()){
                    ayah.close();
                }
                db.close();
            }

        }
    }

    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent, String text_tashkeel,String ayahDescription,String suraName,String ayah_num,String surah_id) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CharSequence name = "quranulkarim";
            String Description = "Quran ul karim";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }


        Bitmap licon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context,CHANNEL_ID)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_quran_radio)
                        .setContentTitle("Quran ul karim reminder for you!")
                        .setContentText(text_tashkeel)
                        .setStyle(new NotificationCompat.InboxStyle().addLine(text_tashkeel).addLine(ayahDescription).addLine(suraName+":"+ayah_num))
                        .setLargeIcon(licon)
                        .setAutoCancel(true);

        return builder;
    }

    /**
     * Method checks if the app is in background or not
     */
    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }
        else
        {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

    private  boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAppOnForeground(Context context,String appPackageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = appPackageName;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                Log.e("app",appPackageName);
                return true;
            }
        }
        return false;
    }
}