package com.example.alexa.notes;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by alexander on 13.02.18.
 */

public class Gps implements LocationListener {

    public static String address;

    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder = new Geocoder(CreateEdit_activity.context, Locale.getDefault());
        List<Address> addresses;
            CreateEdit_activity.progressBar.setVisibility(View.INVISIBLE);
        try{
            addresses = geocoder.
                    getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);

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
}
