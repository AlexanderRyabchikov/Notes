package helpers.async_tasks;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import helpers.constants.Constants;

/**
 * Created by alexa on 08.03.2018.
 */

public class Gps {
    private LocationListener locationListener;
    private LocationManager locationManager;
    private Context context = null;
    private ProgressBar progressBar = null;
    private ImageButton button = null;
    private boolean checkStateGps = false;
    private double latitude = 0xFFFF;
    private double longtitude = 0xFFFF;

    public Gps(Context context, ProgressBar progressBar, ImageButton saveButton){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        this.progressBar = progressBar;
        this.button = saveButton;
        this.context = context;
    }

    public void findLocation(){
        progressBar.setVisibility(View.VISIBLE);
        button.setVisibility(View.INVISIBLE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longtitude = location.getLongitude();
                progressBar.setVisibility(View.INVISIBLE);
                button.setVisibility(View.VISIBLE);
                locationManager.removeUpdates(locationListener);
                locationManager = null;
                checkStateGps = true;
                Constants.ToastMakeText(context,
                        Constants.GPS_PLACE_FOUND, Constants.TYPE_MESSAGE_INFO);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Constants.ToastMakeText(context, Constants.MESSAGE_GPS_ON, Constants.TYPE_MESSAGE_INFO);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Constants.ToastMakeText(context, Constants.MESSAGE_GPS_OFF, Constants.TYPE_MESSAGE_ERROR);
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
            Constants.ToastMakeText(context, Constants.GPS_ERROR, Constants.TYPE_MESSAGE_ERROR);
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void cancelFindLocation(){
        progressBar.setVisibility(View.INVISIBLE);
        button.setVisibility(View.VISIBLE);
        if(!checkStateGps) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
    }
}
