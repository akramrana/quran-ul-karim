package com.akramhossain.quranulkarim;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.util.PrayTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PrayerTimesActivity extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 101;

    TextView ftime,stime,ztime,atime,mtime,itime;

    boolean gps_enabled=false;
    boolean network_enabled=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_times);

        ftime = (TextView) findViewById(R.id.ftime);
        stime = (TextView) findViewById(R.id.stime);
        ztime = (TextView) findViewById(R.id.ztime);
        atime = (TextView) findViewById(R.id.atime);
        mtime = (TextView) findViewById(R.id.mtime);
        itime = (TextView) findViewById(R.id.itime);

        TimeZone tmzone = TimeZone.getDefault();

        double hourDiff = (tmzone.getRawOffset()/ 1000)/3600;

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
                try{
                    gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                }catch(Exception ex){
                }
                try{
                    network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                }catch(Exception ex){
                }
                Location networkLoacation = null, gpsLocation = null, location = null;
                if(gps_enabled){
                    gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if(network_enabled){
                    networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (gpsLocation != null && networkLoacation != null) {
                    if (gpsLocation.getAccuracy() > networkLoacation.getAccuracy()) {
                        location = networkLoacation;
                    }else {
                        location = gpsLocation;
                    }
                } else {
                    if (gpsLocation != null) {
                        location = gpsLocation;
                    } else if (networkLoacation != null) {
                        location = networkLoacation;
                    }
                }
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double timezone = hourDiff;
                //double latitude = 23.810331;
                //double longitude = 90.412521;
                getPrayerTimes(latitude,longitude,timezone);

            }else{
                requestPermission();
            }
        }else{
            LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
            //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            try{
                gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }catch(Exception ex){
            }
            try{
                network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }catch(Exception ex){
            }
            Location networkLoacation = null, gpsLocation = null, location = null;
            if(gps_enabled){
                gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(network_enabled){
                networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (gpsLocation != null && networkLoacation != null) {
                if (gpsLocation.getAccuracy() > networkLoacation.getAccuracy()) {
                    location = networkLoacation;
                }else {
                    location = gpsLocation;
                }
            } else {
                if (gpsLocation != null) {
                    location = gpsLocation;
                } else if (networkLoacation != null) {
                    location = networkLoacation;
                }
            }

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double timezone = hourDiff;
            getPrayerTimes(latitude,longitude,timezone);
        }
    }

    public void getPrayerTimes(double latitude,double longitude,double timezone){
        PrayTime prayers = new PrayTime();

        prayers.setTimeFormat(prayers.Time12);
        prayers.setCalcMethod(prayers.Makkah);
        prayers.setAsrJuristic(prayers.Hanafi);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal,
                latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();

        for (int i = 0; i < prayerTimes.size(); i++) {
            Log.d("Prayer Time", prayerNames.get(i) + " - " + prayerTimes.get(i));
            //Log.d("i", String.valueOf(i));
            if(i==0){
                ftime.setText(prayerTimes.get(i));
            }
            if(i==1){
                stime.setText(prayerTimes.get(i));
            }
            if(i==2){
                ztime.setText(prayerTimes.get(i));
            }
            if(i==3){
                atime.setText(prayerTimes.get(i));
            }
            if(i==4){
                mtime.setText(prayerTimes.get(i));
            }
            if(i==6){
                itime.setText(prayerTimes.get(i));
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(PrayerTimesActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PrayerTimesActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(PrayerTimesActivity.this, "Access Location  permission allows us to determine your current location. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(PrayerTimesActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Log.e("value", "Permission Granted.");
                    TimeZone tmzone = TimeZone.getDefault();

                    double hourDiff = (tmzone.getRawOffset()/ 1000)/3600;

                    if (checkPermission()) {
                        LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
                        //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        try{
                            gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        }catch(Exception ex){
                        }
                        try{
                            network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        }catch(Exception ex){
                        }
                        Location networkLoacation = null, gpsLocation = null, location = null;
                        if(gps_enabled){
                            gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                        if(network_enabled){
                            networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        if (gpsLocation != null && networkLoacation != null) {
                            if (gpsLocation.getAccuracy() > networkLoacation.getAccuracy()) {
                                location = networkLoacation;
                            }else {
                                location = gpsLocation;
                            }
                        } else {
                            if (gpsLocation != null) {
                                location = gpsLocation;
                            } else if (networkLoacation != null) {
                                location = networkLoacation;
                            }
                        }

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        double timezone = hourDiff;
                        getPrayerTimes(latitude, longitude, timezone);
                    }
                } else {
                    Log.e("value", "Permission Denied.");
                }
                break;
        }
    }

}
