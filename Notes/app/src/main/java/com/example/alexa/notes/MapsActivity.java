package com.example.alexa.notes;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import helpers.constants.Constants;
import helpers.data_base.Notes;
import helpers.data_base.RoomDB;
import helpers.interfaces.IDataBaseApi;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class MapsActivity  extends FragmentActivity
                    implements  OnMapReadyCallback,
                                GoogleMap.OnMapClickListener,
                                GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private IDataBaseApi dataBase;
    private Intent intentFlag;
    private boolean ctrlFlag = false;
    private double latitude;
    private double longitude;
    private boolean flagCheckBox = true;
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
            dataBase.close_connection();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(ctrlFlag) {
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
            dataBase = new RoomDB();
            ctrlFlag = true;
            dataBase.open_connection();

            List<Notes> notes = dataBase.getEntries();
            if(!notes.isEmpty()){
                for(Notes item : notes){
                    mMap.addMarker(new MarkerOptions()
                            .title(item.titleDB)
                            .position(new LatLng(item.latitude, item.longtitude))
                            .alpha(item._id));
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
            flagCheckBox = true;
            Constants.ToastMakeText(getBaseContext(), Constants.COORDINATE_SELECT);
        }
    }

    /**
    * Переход к по маркеру к просмотру записки
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(ctrlFlag) {
            long positionId = (long) marker.getAlpha();
            Intent intentPreviewNote = new Intent(this, PreviewNote.class);
            intentPreviewNote.putExtra(Constants.INTENT_PREVIEW_NOTE, positionId);
            startActivityForResult(intentPreviewNote, 2);
        }
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
        intent.putExtra(Constants.INTENT_MAPS_CHEKCBOX_FLAG, flagCheckBox);
        intent.putExtra(Constants.INTENT_MAPS_WITH_COORDINATES_LAT, latitude);
        intent.putExtra(Constants.INTENT_MAPS_WITH_COORDINATES_LONG, longitude);
        setResult(RESULT_OK, intent);
        finish();
    }
}

