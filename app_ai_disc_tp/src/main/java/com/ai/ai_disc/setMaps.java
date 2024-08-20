package com.ai.ai_disc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.common.collect.Maps;


public class setMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = "Maps";
    Button ok,cancel;
    String user_latitude="";
    String user_longitude="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapss);

        ok=findViewById(R.id.ok);
        cancel=findViewById(R.id.cancel);

        ok.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);

        Toast.makeText(setMaps.this, "Set your location mannualy !", Toast.LENGTH_SHORT).show();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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

        // Add a marker in Sydney and move the camera
        LatLng delhi = new LatLng(28.637440, 77.145990);
        mMap.addMarker(new MarkerOptions().position(delhi).title("ICAR-IASRI,DELHI"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(delhi));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                //Log.d(TAG, "onMapClick: "+latLng.latitude);
              //  Log.d(TAG, "onMapClick: "+latLng.longitude);

                ok.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);


                user_latitude= String.valueOf(latLng.latitude);
                user_longitude= String.valueOf(latLng.longitude);

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("User location"));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ok.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                user_latitude= "";
                user_longitude= "";
                mMap.clear();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("latitude",user_latitude);
                returnIntent.putExtra("longitude",user_longitude);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}