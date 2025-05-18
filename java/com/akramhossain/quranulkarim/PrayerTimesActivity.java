package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.model.CalculationMethod;
import com.akramhossain.quranulkarim.model.JuristicMethod;
import com.akramhossain.quranulkarim.util.PrayTime;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class PrayerTimesActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;

    TextView ftime,stime,ztime,atime,mtime,itime;

    boolean gps_enabled=false;
    boolean network_enabled=false;

    Spinner cm_spinner, jm_spinner;

    double selectedLatitude = -1;
    double selectedlongitude = -1;
    int calcMethod = 0;
    int asrJuristicMethod = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_prayer_times);

        View rootView = findViewById(R.id.ptTopBar);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to avoid overlap with status/navigation bars
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    0
            );
            return insets;
        });

        ftime = (TextView) findViewById(R.id.ftime);
        stime = (TextView) findViewById(R.id.stime);
        ztime = (TextView) findViewById(R.id.ztime);
        atime = (TextView) findViewById(R.id.atime);
        mtime = (TextView) findViewById(R.id.mtime);
        itime = (TextView) findViewById(R.id.itime);

        TimeZone tmzone = TimeZone.getDefault();

        double rawOffet = tmzone.getRawOffset();
        //double rawOffet = 19800000;
        double rawOffetDiv = 1000;
        double divBySec = 3600;

        double hourDiff = (rawOffet/ rawOffetDiv)/divBySec;

        System.out.println("Timezone: "+hourDiff);

        ArrayList cmList = this.calculationMethods();
        cm_spinner = (Spinner) findViewById( R.id.calcMtd_spinner);
        ArrayAdapter<CalculationMethod> spinnerAdapter = new ArrayAdapter<CalculationMethod>(this,android.R.layout.simple_spinner_item, cmList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cm_spinner.setAdapter(spinnerAdapter);
        cm_spinner.setSelection(4);
        cm_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (checkPermission()) {
                    Log.d("Calculation Method:", String.valueOf(id));
                    calcMethod = (int) id;
                    if(selectedLatitude !=-1 && selectedlongitude!=-1) {
                        getPrayerTimes(selectedLatitude, selectedlongitude, hourDiff);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        //
        ArrayList jmList = this.juristicMethods();
        jm_spinner = (Spinner) findViewById( R.id.jurisMtd_spinner);
        ArrayAdapter<JuristicMethod> spinnerJmAdapter = new ArrayAdapter<JuristicMethod>(this,android.R.layout.simple_spinner_item, jmList);
        spinnerJmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jm_spinner.setAdapter(spinnerJmAdapter);
        jm_spinner.setSelection(1);
        jm_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (checkPermission()) {
                    Log.d("Juristic Method:", String.valueOf(id));
                    asrJuristicMethod = (int) id;
                    if(selectedLatitude !=-1 && selectedlongitude!=-1) {
                        getPrayerTimes(selectedLatitude, selectedlongitude, hourDiff);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        //
        PrayTime prayers = new PrayTime();
        calcMethod = prayers.Makkah;
        asrJuristicMethod = prayers.Hanafi;
        //
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
                if(location!= null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    double timezone = hourDiff;
                    //double latitude = 23.810331;
                    //double longitude = 90.412521;
                    selectedLatitude = latitude;
                    selectedlongitude = longitude;
                    getPrayerTimes(latitude, longitude, timezone);
                }else{
                    Toast.makeText(PrayerTimesActivity.this, "Sorry! We could not retrive your current location.", Toast.LENGTH_LONG).show();
//                    double latitude = 22.978624;
//                    double longitude = 87.747803;
//                    double timezone = hourDiff;
//                    selectedLatitude = latitude;
//                    selectedlongitude = longitude;
//                    getPrayerTimes(latitude, longitude, timezone);
                }

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
            if(location!= null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double timezone = hourDiff;
                selectedLatitude = latitude;
                selectedlongitude = longitude;
                getPrayerTimes(latitude, longitude, timezone);
            }else{
                Toast.makeText(PrayerTimesActivity.this, "Sorry! We could not retrive your current location.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getPrayerTimes(double latitude,double longitude,double timezone){
        PrayTime prayers = new PrayTime();

        prayers.setTimeFormat(prayers.Time12);
        //prayers.setCalcMethod(prayers.Makkah);
        //prayers.setAsrJuristic(prayers.Hanafi);
        prayers.setCalcMethod(calcMethod);
        prayers.setAsrJuristic(asrJuristicMethod);
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

        /*String[] ids = TimeZone.getAvailableIDs();
        for (String id : ids) {
            System.out.println(displayTimeZone(TimeZone.getTimeZone(id)));
        }*/

    }

    private static String displayTimeZone(TimeZone tz) {

        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())- TimeUnit.HOURS.toMinutes(hours);
        minutes = Math.abs(minutes);
        String result = "";
        if (hours > 0) {
            result = String.format("(GMT+%d:%02d) %s %s", hours, minutes, tz.getID(), tz.getRawOffset());
        } else {
            result = String.format("(GMT%d:%02d) %s %s", hours, minutes, tz.getID(), tz.getRawOffset());
        }
        return result;
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Log.e("value", "Permission Granted.");
                    TimeZone tmzone = TimeZone.getDefault();

                    double hourDiff = (tmzone.getRawOffset() / 1000) / 3600;

                    if (checkPermission()) {
                        LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
                        //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        try {
                            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        } catch (Exception ex) {
                        }
                        try {
                            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        } catch (Exception ex) {
                        }
                        Location networkLoacation = null, gpsLocation = null, location = null;
                        if (gps_enabled) {
                            gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                        if (network_enabled) {
                            networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        if (gpsLocation != null && networkLoacation != null) {
                            if (gpsLocation.getAccuracy() > networkLoacation.getAccuracy()) {
                                location = networkLoacation;
                            } else {
                                location = gpsLocation;
                            }
                        } else {
                            if (gpsLocation != null) {
                                location = gpsLocation;
                            } else if (networkLoacation != null) {
                                location = networkLoacation;
                            }
                        }
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            double timezone = hourDiff;
                            selectedLatitude = latitude;
                            selectedlongitude = longitude;
                            getPrayerTimes(latitude, longitude, timezone);
                        } else {
                            Toast.makeText(PrayerTimesActivity.this, "Sorry! We could not retrive your current location.", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Log.e("value", "Permission Denied.");
                }
                break;
        }
    }

    public ArrayList<CalculationMethod> calculationMethods(){
        ArrayList < CalculationMethod > cm = new ArrayList<>();
        cm.add(new CalculationMethod(0, "Shia Ithna Ashari"));
        cm.add(new CalculationMethod(1, "University of Islamic Sciences, Karachi"));
        cm.add(new CalculationMethod(2, "Islamic Society of North America (ISNA)"));
        cm.add(new CalculationMethod(3, "Muslim World League (MWL)"));
        cm.add(new CalculationMethod(4, "Umm al-Qura, Makkah"));
        cm.add(new CalculationMethod(5, "Egyptian General Authority of Survey"));
        cm.add(new CalculationMethod(6, "Institute of Geophysics, University of Tehran"));
        return cm;
    }

    public ArrayList<JuristicMethod> juristicMethods(){
        ArrayList < JuristicMethod > jm = new ArrayList<>();
        jm.add(new JuristicMethod(0, "Shafii (Standard)"));
        jm.add(new JuristicMethod(1, "Hanafi Juristic"));
        return jm;
    }
}
