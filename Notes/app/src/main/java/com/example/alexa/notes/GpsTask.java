package com.example.alexa.notes;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexander on 14.02.18.
 */

public class GpsTask extends AsyncTask<Void, Void, Void> {


    private LocationListener locationListener;
    private Location gpsLocation = null;

    @Override
    protected void onPreExecute() {
        C.bar.setVisibility(View.VISIBLE);
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
                Toast.makeText(C.context, C.MESSAGE_GPS_ON, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                C.context.startActivity(intent);
                Toast.makeText(C.context, C.MESSAGE_GPS_OFF,
                        Toast.LENGTH_SHORT).show();
            }
        };

        try {
             if (C.locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                 C.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
             }
            if (C.locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                C.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }catch (SecurityException e){
            Toast.makeText(C.context,
                    C.GPS_ERROR,
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
            Toast.makeText(C.context,
                    C.GPS_PLACE_NOT_FOUND,
                    Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        C.bar.setVisibility(View.GONE);
        C.locationManager.removeUpdates(locationListener);
        C.lintitude = gpsLocation.getLatitude();
        C.longtitude = gpsLocation.getLongitude();
        C.ToastMakeText(C.context,
                C.GPS_PLACE_FOUND);
        CreateEdit_activity.saveButton.setEnabled(true);
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}