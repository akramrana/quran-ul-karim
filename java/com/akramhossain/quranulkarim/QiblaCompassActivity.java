package com.akramhossain.quranulkarim;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.TimeZone;

public class QiblaCompassActivity extends AppCompatActivity implements SensorEventListener {

    public static ImageView image,arrow;

    // record the compass picture angle turned
    private float currentDegree = 0f;
    private float currentDegreeNeedle = 0f;
    Location userLoc=new Location("service Provider");
    // device sensor manager
    private static SensorManager mSensorManager ;
    private Sensor sensor;
    public static TextView tvHeading;

    boolean gps_enabled=false;
    boolean network_enabled=false;
    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qibla_compass);

        image = (ImageView) findViewById(R.id.compass);
        arrow = (ImageView) findViewById(R.id.needle);

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
                double altitude = location.getAltitude();

                double longi = longitude;
                double lati = latitude;
                double alti = altitude;

                // TextView that will tell the user what degree is he heading
                tvHeading = (TextView) findViewById(R.id.heading);

                userLoc.setLongitude(longi);
                userLoc.setLatitude(lati);
                userLoc.setAltitude(alti);

                mSensorManager =  (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
                sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
                if(sensor!=null) {
                    // for the system's orientation sensor registered listeners
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);//SensorManager.SENSOR_DELAY_Fastest
                }else{
                    Toast.makeText(getApplicationContext(),"Not Supported", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(QiblaCompassActivity.this, "Sorry! We could not retrive your current location.", Toast.LENGTH_LONG).show();
            }

        }else{
            requestPermission();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float degree = Math.round(sensorEvent.values[0]);
        float head = Math.round(sensorEvent.values[0]);

        Location destinationLoc = new Location("service Provider");
        destinationLoc.setLatitude(21.422487); //kaaba latitude setting
        destinationLoc.setLongitude(39.826206); //kaaba longitude setting
        float bearTo = userLoc.bearingTo(destinationLoc);
        //bearTo = The angle from true north to the destination location from the point we're your currently standing.(asal image k N se destination taak angle )
        //head = The angle that you've rotated your phone from true north. (jaise image lagi hai wo true north per hai ab phone jitne rotate yani jitna image ka n change hai us ka angle hai ye)
        GeomagneticField geoField = new GeomagneticField( Double.valueOf( userLoc.getLatitude() ).floatValue(), Double
                .valueOf( userLoc.getLongitude() ).floatValue(),
                Double.valueOf( userLoc.getAltitude() ).floatValue(),
                System.currentTimeMillis() );
        head -= geoField.getDeclination(); // converts magnetic north into true north
        if (bearTo < 0) {
            bearTo = bearTo + 360;
        }
        //This is where we choose to point it
        float direction = bearTo - head;
        // If the direction is smaller than 0, add 360 to get the rotation clockwise.
        if (direction < 0) {
            direction = direction + 360;
        }
        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees" );
        RotateAnimation raQibla = new RotateAnimation(currentDegreeNeedle, direction, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        raQibla.setDuration(210);
        raQibla.setFillAfter(true);
        arrow.startAnimation(raQibla);
        currentDegreeNeedle = direction;
        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // how long the animation will take place
        ra.setDuration(210);
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        Toast.makeText(getApplicationContext(), "Destroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(QiblaCompassActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(QiblaCompassActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(QiblaCompassActivity.this, "Access Location  permission allows us to determine your current location. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(QiblaCompassActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
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
                        } else {
                            Toast.makeText(QiblaCompassActivity.this, "Sorry! We could not retrive your current location.", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Log.e("value", "Permission Denied.");
                }
                break;
        }
    }
}