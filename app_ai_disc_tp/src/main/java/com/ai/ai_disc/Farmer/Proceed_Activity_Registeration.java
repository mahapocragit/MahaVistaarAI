package com.ai.ai_disc.Farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;

import com.ai.ai_disc.About_us1;
import com.ai.ai_disc.contributor;
import com.ai.ai_disc.farmersprofile_fragment1;
import com.ai.ai_disc.needHelp;
import com.google.android.gms.location.FusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.Login;
import com.ai.ai_disc.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class Proceed_Activity_Registeration extends AppCompatActivity {
     Button proceedButton;
    FusedLocationProviderClient mFusedLocationClient;
    static String latt="";
    static String longt="";
    Location location;

    // Initializing other items
    // from layout file
   TextView latitudeTextView;
    int PERMISSION_ID = 44;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed_registeration);
        proceedButton=(Button) findViewById(R.id.proceedButton);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        latitudeTextView = findViewById(R.id.latlon);
        // method to get the location
        try{getLastLocation();} catch (Exception e) {
            e.printStackTrace();
        }




        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inj=new Intent(Proceed_Activity_Registeration.this, Login.class);
              inj.putExtra("loops",2);
//                inj.putExtra("lon",longt);
                startActivity(inj);
                finish();
            }
        });
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        try {
                            location = task.getResult();
                        }
                        catch (Exception e)
                        {

                        }

                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitudeTextView.setText(String.format("%.2f",location.getLatitude())+"/"+String.format("%.2f",location.getLongitude()));
                            latt=String.valueOf(location.getLatitude());
                            longt=String.valueOf(location.getLongitude());
                            //longitTextView.setText(location.getLongitude() + "");
                            //Toast.makeText(Proceed_Activity_Registeration.this, "GPS Is On !", Toast.LENGTH_LONG).show();
                        }
                    }
                    });

            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            //Toast.makeText(Proceed_Activity_Registeration.this, "GPS Is On !", Toast.LENGTH_LONG).show();
            latitudeTextView.setText(String.format("%.2f",mLastLocation.getLatitude())+"/"+String.format("%.2f",mLastLocation.getLongitude()));
            latt=String.valueOf(mLastLocation.getLatitude());
            longt=String.valueOf(mLastLocation.getLongitude());

        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
       inflator.inflate(R.menu.menuproceed, menu);
       if(menu instanceof MenuBuilder){
           MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
       }
       return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())

        {

            case R.id.about_us:

                Intent intent = new Intent(Proceed_Activity_Registeration.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.help:

                Intent intent1 = new Intent(Proceed_Activity_Registeration.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.contr:

                Intent intent11 = new Intent(Proceed_Activity_Registeration.this, contributor.class);
                startActivity(intent11);



                break;

            default:
                break;

        }

        return true;
    }
    @Override
    public void onBackPressed() {

        /*
        new MaterialAlertDialogBuilder(Login.this,R.style.AlertDialogTheme)
                .setTitle("Title")
                .setMessage("Your message goes here. Keep it short but clear.")
                .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

         */


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Proceed_Activity_Registeration.this);
        alertDialogBuilder.setTitle("Exit");
        alertDialogBuilder.setMessage("Wanna Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {//yes
                    public void onClick(final DialogInterface dialog, int id) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent a = new Intent(Intent.ACTION_MAIN);
                                a.addCategory(Intent.CATEGORY_HOME);
                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(a);

                                dialog.cancel();
                            }
                        }, 10);

                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {//no
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();


    }

}