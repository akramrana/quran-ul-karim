package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MosqueNearActivity extends AppCompatActivity {

    boolean gps_enabled=false;
    boolean network_enabled=false;
    private static final int PERMISSION_REQUEST_CODE = 101;
    WebView myWebView;
    String mapPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_mosque_near);

        View rootView = findViewById(R.id.topSearchBar);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to avoid overlap with status/navigation bars
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    view.getPaddingBottom()
            );
            return insets;
        });

        myWebView = (WebView)findViewById(R.id.mapview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath(this.getFilesDir().getPath());
        myWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return (false);
            }
        });

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
                    mapPath = "http://websites.codxplore.com/heremap/index.php?lat="+latitude+"&lng="+longitude;

                    Log.d("MAP ULR",mapPath);
                    myWebView.loadUrl(mapPath);

                }else{
                    Toast.makeText(MosqueNearActivity.this, "Sorry! We could not retrive your current location.", Toast.LENGTH_LONG).show();
                }

            }else{
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MosqueNearActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MosqueNearActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(MosqueNearActivity.this, "Access Location  permission allows us to determine your current location. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MosqueNearActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
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

                            mapPath = "http://websites.codxplore.com/heremap/index.php?lat=" + latitude + "&lng=" + longitude;

                            Log.d("MAP ULR", mapPath);

                            myWebView.loadUrl(mapPath);

                        } else {
                            Toast.makeText(MosqueNearActivity.this, "Sorry! We could not retrive your current location.", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Log.e("value", "Permission Denied.");
                }
                break;
        }
    }
}