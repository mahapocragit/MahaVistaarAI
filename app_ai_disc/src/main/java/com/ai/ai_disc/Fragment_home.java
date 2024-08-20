package com.ai.ai_disc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ai.ai_disc.Farmer.GPSTracker;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_home extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Location my_location;
    int geocoderMaxResults = 1;
    LocationManager locationManager;
    boolean gpssatus;
    TextView eml;

    ImageView imageView2;
    Button chck;

    public Fragment_home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment_mod_dis.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_home newInstance(String param1, String param2) {
        Fragment_home fragment = new Fragment_home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String username=getArguments().getString("argu1");
        String loc=getArguments().getString("argu2");

        View view= inflater.inflate(R.layout.fragment_home, container, false);
        String msgtime="null";
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
            msgtime="Good Morning";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            msgtime="Good Afternoon";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            msgtime="Good Evening";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            msgtime="Good Night";
        }
        TextView gd=view.findViewById(R.id.gd);
        imageView2=view.findViewById(R.id.imageView2);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ibt=new Intent(getContext(), editprofile.class);
                startActivity(ibt);
            }
        });
        gd.setText(msgtime);
        TextView usd=view.findViewById(R.id.usrname);
        usd.setText("Welcome ' "+username+" '");
        eml=view.findViewById(R.id.loc);
        eml.setText(loc);
        int LAUNCH_MAP_ACTIVITY = 1;
        MaterialButton edt=view.findViewById(R.id.editprofile);
        edt.setVisibility(View.GONE);
        GPSTracker gpsTracker=new GPSTracker(getContext());
         chck=view.findViewById(R.id.checkgps);

        chck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gpsTracker.getIsGPSTrackingEnabled())
                {double laty=gpsTracker.getLatitude();
                    if (laty!=0.00){
                        eml.setText(gpsTracker.getAddressLine(getContext()));
                        Toast.makeText(getContext(),"GPS is ON !",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intent = new Intent(getContext(), setMaps.class);
                        startActivityForResult(intent,LAUNCH_MAP_ACTIVITY);
                        chck.setVisibility(View.GONE);


                    }
                }else{
                    Intent intent = new Intent(getContext(), setMaps.class);
                    startActivityForResult(intent,LAUNCH_MAP_ACTIVITY);
                    chck.setVisibility(View.GONE);

                }


            }
        });
        chck.setVisibility(View.GONE);
//        if (loc.matches("Unknown!")){
//            //CheckGpsStatus();
//            chck.setVisibility(View.VISIBLE);
//        }else{
//            chck.setVisibility(View.GONE);
//        }
        edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ibt=new Intent(getContext(), editprofile.class);
                startActivity(ibt);
            }
        });





        return view;
    }
    public void CheckGpsStatus(){
        locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        gpssatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gpssatus) {

            Toast.makeText(getContext(),"GPS Is Disabled",Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);
            chck.setVisibility(View.VISIBLE);
            CheckGpsStatus();

        }
        else{
            chck.setVisibility(View.GONE);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){

            if(resultCode == Activity.RESULT_OK){
                String latitude=data.getStringExtra("latitude");
                String longitude=data.getStringExtra("longitude");
//                Log.d(TAG, "onActivityResult: "+latitude);
//                Log.d(TAG, "onActivityResult: "+longitude);

                if(my_location==null){
                    my_location = new Location("");
                }

                my_location.setLatitude(Double.parseDouble(latitude));
                my_location.setLongitude(Double.parseDouble(longitude));
                List<Address> addresses = getGeocoderAddress(getContext());
                String locality="";
                String countryName="";
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                     locality= address.getLocality();

                         countryName= address.getCountryName();


            }eml.setText(locality+", "+countryName);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    /*
                    if (mGoogleApiClient == null) {
                      initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();

                     */


                } else {
                    // updateGPSStatus("Location Permission denied.");
                    Toast.makeText(getContext(), "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }



    private List<Address> getGeocoderAddress(Context context) {
        if (my_location != null) {

            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                List<Address> addresses = geocoder.getFromLocation(my_location.getLatitude(), my_location.getLongitude(), this.geocoderMaxResults);

                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
                //Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }
    }