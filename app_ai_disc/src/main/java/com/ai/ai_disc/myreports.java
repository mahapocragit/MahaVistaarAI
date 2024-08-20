package com.ai.ai_disc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.model.myreport_model;
import com.ai.ai_disc.model.myreports_response;
import com.ai.ai_disc.view_model.getcropidnames;
import com.ai.ai_disc.view_model.myreport_view;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class myreports extends AppCompatActivity {
    int uid;
    myreport_view viem;
    GridView grid;
    int geocoderMaxResults=1;
    InternetReceiver internet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_myreports);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("My Reports");
        uid=Integer.parseInt(user_singleton.getInstance().getUser_id());

         grid= findViewById(R.id.grid_viewwaa);

        internet = new InternetReceiver();

        viem= ViewModelProviders.of(myreports.this).get(myreport_view.class);
        viem.getting_crops(uid).observe(myreports.this, new Observer<myreports_response>() {
            @Override
            public void onChanged(myreports_response myreports_response) {

                if(myreports_response.getModel()!=null){

                    myreport_model model=myreports_response.getModel();
                    if (model.getcropnames().size()!=0) {

                        ArrayList<gridview_model_datafetcher> courseModelArrayList = new ArrayList<gridview_model_datafetcher>();

                        for (int ij = 0; ij < model.getcropnames().size(); ij += 1) {
                            String cropname = model.getcropnames().get(ij);
                            String dates = model.getdates().get(ij);

                            String lats = model.getlats().get(ij);
                            String longs = model.getlongs().get(ij);
                            String dpname = model.getdpname().get(ij);
                            int whichtype = model.getwhichtype().get(ij);
                            int nums = model.getnumbs().get(ij);
                            int reportid = model.getreportid().get(ij);
                            int dpid = model.getdpids().get(ij);
                            int cropid = model.getcropid().get(ij);

                            String[] st_ll = getlocation(lats, longs);
                            lats = st_ll[0];
                            longs = st_ll[1];

                            String imagecrop = cropname.replace(" ", "").toLowerCase() + "_iasri";
                            int imgid = R.drawable.splashscreenaidisc;
                            try {
                                imgid = getResources().getIdentifier(imagecrop,
                                        "drawable", myreports.this.getPackageName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            courseModelArrayList.add(new gridview_model_datafetcher(imgid, cropname, reportid, nums, dates,
                                    lats, longs, whichtype, dpname, dpid, cropid));

                        }
                        arrayadapter_model_reports adapter = new arrayadapter_model_reports(myreports.this, courseModelArrayList);
                        grid.setAdapter(adapter);

                    }
                else{
                        Toast.makeText(myreports.this, "No Records Found !", Toast.LENGTH_LONG).show();

                    }}else{
                    Toast.makeText(myreports.this, "Error !", Toast.LENGTH_LONG).show();


                }



            }
        });


    }
    private List<Address> getGeocoderAddress(Context context, String lat, String longg) {



        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

        try {
            /**
             * Geocoder.getFromLocation - Returns an array of Addresses
             * that are known to describe the area immediately surrounding the given latitude and longitude.
             */
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(longg), this.geocoderMaxResults);

            return addresses;
        } catch (IOException e) {
            //e.printStackTrace();
            //Log.e(TAG, "Impossible to connect to Geocoder", e);
        }


        return null;
    }
    private String[] getlocation(String lat, String longg) {
        List<Address> addresses = getGeocoderAddress(myreports.this, lat,  longg);
        String locality="";
        String countryName="";
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            locality= address.getLocality();

            countryName= address.getCountryName();


        }
        return new String[]{locality,countryName};
    }
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();

        //progressDialog.dismiss();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                break;
            case R.id.logout:

                signout();
                break;

            default:
                break;

        }

        return true;
    }
    public void show_dialog(String message) {

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(myreports.this);


        alertDialogBuilder.setTitle("Result");


        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {//yes
                    public void onClick(final DialogInterface dialog, int id) {

                        dialog.cancel();


                    }
                });


        android.app.AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();
    }
    private void signout() {
        android.app.AlertDialog.Builder opt = new android.app.AlertDialog.Builder(myreports.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                shared_pref.remove_shared_preference(getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();



            }
        });
        opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        opt.show();
    }

}