package com.example.alexa.notes;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import Helpers.Constants.Constants;
import Helpers.DataBase.DataBase;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity  extends FragmentActivity
                    implements  OnMapReadyCallback,
                                GoogleMap.OnMapClickListener,
                                GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Cursor cursor;
    private DataBase dataBase;
    private int id_note;
    private String title;
    private Intent intentFlag;
    private boolean ctrlFlag = false;
    private double latitude;
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intentFlag = getIntent();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(mMap != null){
            mMap.clear();
            addMarkerFromDataBase();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ctrlFlag) {
            stopManagingCursor(cursor);
            cursor.close();
            dataBase.close_connection();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(ctrlFlag) {
            stopManagingCursor(cursor);
            cursor.close();
            dataBase.close_connection();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        addMarkerFromDataBase();
    }

    private void addMarkerFromDataBase() {
        if (intentFlag.getBooleanExtra(Constants.map, false)) {
            dataBase = new DataBase(getBaseContext());
            ctrlFlag = true;
            dataBase.open_connection();

            cursor = dataBase.getEntries();
            if (cursor.getCount() >= 0) {
                if (cursor.moveToFirst()) {
                    do {
                        id_note = cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_ID));
                        title = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_TITLE));
                        latitude = cursor.getDouble(cursor.getColumnIndex(DataBase.COLUMN_LINTITIDE));
                        longitude = cursor.getDouble(cursor.getColumnIndex(DataBase.COLUMN_LONGTITUDE));
                        mMap.addMarker(new MarkerOptions()
                                .title(title)
                                .position(new LatLng(latitude, longitude))
                                .alpha(id_note));


                    } while (cursor.moveToNext());
                }
            }
        }
    }

    /**
    * Получение координат с карты через маркер
     */
    @Override
    public void onMapClick(LatLng latLng) {

        if (!ctrlFlag) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng));
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            Constants.ToastMakeText(getBaseContext(), Constants.COORDINATE_SELECT);
        }
    }

    /**
    * Переход к по маркеру к просмотру записки
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        long positionId = (long)marker.getAlpha();
        Intent intentPreviewNote = new Intent(this, PreviewNote.class);
        intentPreviewNote.putExtra(Constants.INTENT_PREVIEW_NOTE, positionId);
        startActivityForResult(intentPreviewNote, 2);
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            sendResultWithClose();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void sendResultWithClose(){
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_UPDATE_MAIN, true);
        intent.putExtra(Constants.INTENT_MAPS_WITH_COORDINATES_LAT, latitude);
        intent.putExtra(Constants.INTENT_MAPS_WITH_COORDINATES_LONG, longitude);
        setResult(RESULT_OK, intent);
        finish();
    }
}

