package com.example.alexa.notes;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexander on 14.02.18.
 */

public class GpsTask extends AsyncTask<Void, Void, Void> {
    public static ProgressBar bar;
    public static LocationManager locationManager;
    private LocationListener locationListener;
    private Location gpsLocation = null;

    @Override
    protected void onPreExecute() {
        bar.setVisibility(View.VISIBLE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gpsLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(CreateEdit_activity.context, "Gsp is turned on", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                CreateEdit_activity.context.startActivity(intent);
                Toast.makeText(CreateEdit_activity.context, "Gps is turned off!! ",
                        Toast.LENGTH_SHORT).show();
            }
        };

        try {
             if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                 locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
             }
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }catch (SecurityException e){
            Toast.makeText(CreateEdit_activity.context,
                    "Ошибка GSP модуля",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        for (int i = 0; i < 100000; i++) {

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (gpsLocation != null){
                break;
            }
        }

        if (gpsLocation == null){
            gpsLocation.setLatitude(0.0);
            gpsLocation.setLongitude(0.0);
            Toast.makeText(CreateEdit_activity.context,
                    "Не могу определить местонахождение",
                    Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        bar.setVisibility(View.GONE);
        locationManager.removeUpdates(locationListener);
        CreateEdit_activity.lintitude = gpsLocation.getLatitude();
        CreateEdit_activity.longtitude = gpsLocation.getLongitude();
        Toast.makeText(CreateEdit_activity.context,
                "Координаты определены",
                Toast.LENGTH_SHORT).show();
        CreateEdit_activity.saveButton.setEnabled(true);
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}