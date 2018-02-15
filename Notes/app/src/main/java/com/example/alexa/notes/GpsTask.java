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
    public static TextView textView;
    String address = null;
    public static LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onPreExecute() {
        Log.d("String", "start");
        bar.setVisibility(View.VISIBLE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Geocoder geocoder = new Geocoder(CreateEdit_activity.context, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.
                            getFromLocation(location.getLatitude(),
                                    location.getLongitude(), 1);
                    address = addresses.get(0).getAddressLine(0) +
                            "\n" + addresses.get(0).getAddressLine(1) +
                            "\n" + addresses.get(0).getAddressLine(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        Log.d("String", "task start");
        for (int i = 0; i < 100000; i++){

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (address != null){
                break;
            }
        }
        if (address == null){
            address = "местонахождение не определено";
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        bar.setVisibility(View.GONE);
        locationManager.removeUpdates(locationListener);
        textView.setVisibility(View.VISIBLE);
        textView.setText(address);
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}