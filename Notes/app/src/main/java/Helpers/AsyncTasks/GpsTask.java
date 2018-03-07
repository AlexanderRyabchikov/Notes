package Helpers.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.alexa.notes.CreateEdit_activity;

import java.util.concurrent.TimeUnit;

import Helpers.Constants.Constants;

/**
 * Created by alexander on 14.02.18.
 */

public class GpsTask extends AsyncTask<Void, Void, Void> {


    private LocationListener locationListener;
    private LocationManager locationManager;
    private Location gpsLocation = null;
    private Context context = null;
    private ProgressBar progressBar = null;

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        getProgressBar().setVisibility(View.VISIBLE);
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
                Toast.makeText(context, Constants.MESSAGE_GPS_ON, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Toast.makeText(context, Constants.MESSAGE_GPS_OFF,
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
        } catch (SecurityException e) {
            Toast.makeText(context,
                    Constants.GPS_ERROR,
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

            if (gpsLocation != null) {
                break;
            }
        }

        if (null == gpsLocation) {
            Location targetLocation = new Location("");
            targetLocation.setLatitude(0.0d);
            targetLocation.setLongitude(0.0d);
            gpsLocation = targetLocation;
            Toast.makeText(context,
                    Constants.GPS_PLACE_NOT_FOUND,
                    Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        getProgressBar().setVisibility(View.INVISIBLE);
        locationManager.removeUpdates(locationListener);
        Constants.lintitude = gpsLocation.getLatitude();
        Constants.longtitude = gpsLocation.getLongitude();
        Constants.ToastMakeText(context,
                Constants.GPS_PLACE_FOUND);
        CreateEdit_activity.saveButton.setEnabled(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}