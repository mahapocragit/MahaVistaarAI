package com.ai.ai_disc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;

import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ai.ai_disc.Farmer.DashboardAdapter;

import com.ai.ai_disc.Farmer.GPSTracker;

import com.ai.ai_disc.model.getIdentifier_crops_response;
import com.ai.ai_disc.model.identifier_model_croplist;
import com.ai.ai_disc.view_model.identifier_croplist;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class farmersprofile_fragment extends AppCompatActivity {
    private static final int NUM_PAGES = 4;
    private static ArrayList<String> discrops,pestcrops,croplist;
    private static ArrayList<Integer> discrops_img,number_exist;
    DashboardAdapter dashboardAdapter;
    String farmerIdentification="";
    private ViewPager mPager;
    private static ArrayList<String> cropnamesAtpresent,valpest,valdis,userdis,userpest,crop_id;
    private PagerAdapter pagerAdapter;
    InternetReceiver internet;

    static String username;
    static String emailid;
    static String Latitude="",Longitude="";
    static String country,city,addressed="";
    TabLayout tabLayout ;
    GPSTracker gpsTracker;
    Location location;
    int geocoderMaxResults = 1;
    // Initializing other items
    // from layout file
    TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    //model_onoff_view ViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_farmersprofile);

        setTitle("AI-DISC");


        //Toolbar toolbar = findViewById(R.id.toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        internet = new InternetReceiver();

        //CheckGpsStatus();
         //gpsTracker = new GPSTracker(farmersprofile_fragment.this);
        //gpsTracker.getLocation();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try{
        getLastLocation();} catch (Exception e) {
            e.printStackTrace();
        }
        // method to get the location





        //getcrops_viewmodel(tabLayout);

        updatemodelenable();
    }



    private void updatemodelenable(){

        identifier_croplist viewmodel= ViewModelProviders.of(farmersprofile_fragment.this).get(identifier_croplist.class);
        viewmodel.getting_crops().observe(farmersprofile_fragment.this, new Observer<getIdentifier_crops_response>() {
            @Override
            public void onChanged(getIdentifier_crops_response getIdentifier_crops_response) {
                if (getIdentifier_crops_response.getModel()!=null){
                    identifier_model_croplist model = getIdentifier_crops_response.getModel();
                    discrops=model.getcropsd();
                    pestcrops=model.getcropsp();
                    getcroplist();
                    getImages();



                    //statusCheck();



//                    if (gpsTracker.getIsGPSTrackingEnabled())
//                    {double laty=gpsTracker.getLatitude();
//
//                        Latitude = String.valueOf(laty);
//                        //laty=locations.getLatitude();
//                        //Toast.makeText(farmersprofile_fragment.this, "Can not access here."+Latitude, Toast.LENGTH_SHORT).show();
//
//                        Longitude = String.valueOf(gpsTracker.getLongitude());
//                        country = gpsTracker.getCountryName(farmersprofile_fragment.this);
//                        city = gpsTracker.getLocality(farmersprofile_fragment.this);
//                        postal = gpsTracker.getPostalCode(farmersprofile_fragment.this);
//                        address = gpsTracker.getAddressLine(farmersprofile_fragment.this);
//                        if (laty==0.00){
//                            Toast.makeText(farmersprofile_fragment.this, "GPS not working !", Toast.LENGTH_SHORT).show();
//                            //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        }
//                        else{
//                            //Toast.makeText(farmersprofile_fragment.this, "GPS is on !", Toast.LENGTH_SHORT).show();
//                            //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        }
//
//                    }
//                    else{
//                        Toast.makeText(farmersprofile_fragment.this, "GPS is not enabled !", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    }


                    username = user_singleton.getInstance().getUser_name();
                    emailid = user_singleton.getInstance().getEmail_id();
                    tabLayout =
                            (TabLayout) findViewById(R.id.tab_layout_model);

//                    tabLayout.addTab(tabLayout.newTab().setText("Home"));
//                    tabLayout.addTab(tabLayout.newTab().setText("Identify"));
//                    tabLayout.addTab(tabLayout.newTab().setText("Report"));
//                    tabLayout.addTab(tabLayout.newTab().setText("Expert"));
//                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                    mPager = (ViewPager) findViewById(R.id.view_pagerq);
                    //PagerAdapter adapter = new MyPagerAdapterdd(getSupportFragmentManager());
                    MyPagerAdapterdd adapters = new MyPagerAdapterdd(farmersprofile_fragment.this,getSupportFragmentManager(), tabLayout.getTabCount());
                    mPager.setAdapter(adapters);

                    mPager.addOnPageChangeListener(new
                            TabLayout.TabLayoutOnPageChangeListener(tabLayout));


                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            mPager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });
                    //getImages(pestcrops,2);
                }
                else{
                    Toast.makeText(farmersprofile_fragment.this, "Can not access here.", Toast.LENGTH_SHORT).show();
                }
            }
        });}

    private void  getImages() {
        //Log.d("page", "onResponse: " + croplist);
        ArrayList<Integer> num=new ArrayList<>();
        for(int ij=0;ij<croplist.size();ij+=1){
            String imagename= croplist.get(ij);
            String imagecrop=imagename.replace(" ","").toLowerCase()+"_iasri";
            int ids=R.drawable.rice;
            try{ ids = getResources().getIdentifier(imagecrop ,
                    "drawable", this.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            num.add(ids);
        }discrops_img=num;

    }
    void getcroplist(){
//        croplist.clear();
//        number_exist.clear();
        ArrayList<String> cpl=new ArrayList<>();
        ArrayList<Integer> num=new ArrayList<>();
        if (discrops.isEmpty()){

        }
        if (pestcrops.isEmpty()){

        }
        for (int h=0;h<discrops.size();h+=1){
            String cropf=discrops.get(h);
            cpl.add(cropf);
            if (pestcrops.contains(cropf)){
                num.add(3);
            }else{
                num.add(1);
            }

        }
        for(int k =0;k<pestcrops.size();k+=1){
            String crop=pestcrops.get(k);
            if (cpl.contains(crop)){
                int g=0;
            }else{cpl.add(crop);
                num.add(2);

            }
        }
        croplist=cpl;
        number_exist=num;
    }


    private void buildAlertMessageNoGps(LocationManager manager) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        startActivity(new Intent(farmersprofile_fragment.this,farmersprofile_fragment.class));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(farmersprofile_fragment.this);
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


            android.app.AlertDialog alertDialog = alertDialogBuilder.create();


            alertDialog.show();

        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    public static class MyPagerAdapterdd extends FragmentPagerAdapter {
        private final String[] titlenames={"Home","Identify","Report","Expert"};
        int totalTabs;
        private Context myContext;
        public MyPagerAdapterdd(Context context,FragmentManager fragmentManager, int totalTabs) {
            super(fragmentManager);
            myContext = context;
            this.totalTabs = totalTabs;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return 4;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();

            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    Fragment_home frag1=new Fragment_home();
                    bundle.putString("argu1", username);
                    if (addressed.matches("")){
                        bundle.putString("argu2", "Unknown!");
                    }else{bundle.putString("argu2", addressed);}

                    frag1.setArguments(bundle);
                    return frag1;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    Fragment_identification frag2=new Fragment_identification();
                    bundle.putStringArrayList("argu1",croplist);
                    bundle.putIntegerArrayList("argu2", discrops_img);
                    bundle.putIntegerArrayList("argu3", number_exist);
                    frag2.setArguments(bundle);
                    return frag2;
                case 2: // Fragment # 1 - This will show SecondFragment
                    Fragment_reporting frag3=new Fragment_reporting();
                    bundle.putString("argu1",Latitude );
                    bundle.putString("argu2", Longitude);
                    bundle.putString("argu3", addressed);
                    frag3.setArguments(bundle);
                    return frag3;
                case 3: // Fragment # 1 - This will show SecondFragment
                    return new Fragment_expert();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return titlenames[position];
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }
    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(internet);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())

        {

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

    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(farmersprofile_fragment.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                shared_pref.remove_shared_preference(farmersprofile_fragment.this);
                Intent intent = new Intent(farmersprofile_fragment.this, Login.class);
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
                        try{
                         location = task.getResult();} catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            //latitudeTextView.setText(String.format("%.2f",location.getLatitude())+"/"+String.format("%.2f",location.getLongitude()));
                            Latitude=String.valueOf(location.getLatitude());
                            Longitude=String.valueOf(location.getLongitude());
                            List<Address> addresses = getGeocoderAddress(farmersprofile_fragment.this);
                            String locality="";
                            String countryName="";
                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                locality= address.getLocality();

                                countryName= address.getCountryName();


                            }
                            addressed=locality+", "+countryName;
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
            //latitudeTextView.setText(String.format("%.2f",mLastLocation.getLatitude())+"/"+String.format("%.2f",mLastLocation.getLongitude()));
            Latitude=String.valueOf(mLastLocation.getLatitude());
            Longitude=String.valueOf(mLastLocation.getLongitude());
            List<Address> addresses = getGeocoderAddress(farmersprofile_fragment.this);
            String locality="";
            String countryName="";
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                locality= address.getLocality();

                countryName= address.getCountryName();


            }
            addressed=locality+", "+countryName;

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
    private List<Address> getGeocoderAddress(Context context) {


        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

        try {
            /**
             * Geocoder.getFromLocation - Returns an array of Addresses
             * that are known to describe the area immediately surrounding the given latitude and longitude.
             */
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(Latitude), Double.parseDouble(Longitude), this.geocoderMaxResults);

            return addresses;
        } catch (IOException e) {
            //e.printStackTrace();
            //Log.e(TAG, "Impossible to connect to Geocoder", e);
        }


        return null;
    }

}