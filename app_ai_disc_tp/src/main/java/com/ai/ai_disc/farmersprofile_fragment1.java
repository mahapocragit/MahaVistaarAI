package com.ai.ai_disc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ai.ai_disc.Farmer.DashboardAdapter;
import com.ai.ai_disc.Farmer.GPSTracker;
import com.ai.ai_disc.Videoconference.Farmer_profile;
import com.ai.ai_disc.model.getIdentifier_crops_response;
import com.ai.ai_disc.model.identifier_model_croplist;
import com.ai.ai_disc.view_model.identifier_croplist;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class farmersprofile_fragment1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int NUM_PAGES = 4;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";
    private static ArrayList<String> discrops,pestcrops,croplist;
    private static ArrayList<Integer> discrops_img,number_exist;
    DashboardAdapter dashboardAdapter;
    String farmerIdentification="";
    private ViewPager mPager;
    private static ArrayList<String> cropnamesAtpresent,valpest,valdis,userdis,userpest,crop_id;
    private PagerAdapter pagerAdapter;
    InternetReceiver internet;
    Location location;
    static String username;
    static String emailid;
    static String Latitude="",Longitude="";
    static String country,city,addressed="",postal;
    TabLayout tabLayout ;
    GPSTracker gpsTracker;
    TextView tabOne;

    TextView tabtwo;
    int geocoderMaxResults = 1;
    TextView loct;

    ActivityResultLauncher<String> activityResultLauncher;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    // Initializing other items
    // from layout file
    TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    private int requestCode;
    private String[] permissions;
    private int[] grantResults;

    //model_onoff_view ViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_farmersprofile);

        setTitle("AI-DISC");


        //Toolbar toolbar = findViewById(R.id.toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        internet = new InternetReceiver();
        setNavigationViewListener();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        View head=navigationView.getHeaderView(0);

        TextView userd=head.findViewById(R.id.name_dataentry);
        TextView acc=head.findViewById(R.id.type_nav);
        loct=head.findViewById(R.id.loctfff);
        //loct.setText(addressed);
       /* try{userd.setText(user_singleton.getInstance().getfname()+" "+user_singleton.getInstance().getMname()+" "+user_singleton.getInstance().getlname());
            acc.setText(user_singleton.getInstance().getUser_type()+" Account");
            //loct.setText(user_singleton.getInstance().getloct());
            //Log.d("ggg",head.findViewById(R.id.vsr).toString());
        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("error",e.toString());
        }
        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        String token_got = shared_pref.sp.getString("token", "");
        String fb_id=user_singleton.getInstance().getfb_id();
        //Log.d("jkkls",token_got.toString()+"hhhhhhh");
        //Log.d("jkkls1",fb_id.toString()+"hhhh");
        if (!fb_id.isEmpty()){
            add_token(token_got,fb_id);
        }
*/
        //CheckGpsStatus();
         //gpsTracker = new GPSTracker(farmersprofile_fragment.this);
        //gpsTracker.getLocation();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            getLastLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // method to get the location
        tabLayout =
                (TabLayout) findViewById(R.id.tab_layout_model);
        tabOne = (TextView) LayoutInflater.from(farmersprofile_fragment1.this).inflate(R.layout.custom_tab, null);
        tabOne.setTextColor(Color.RED);
        tabtwo = (TextView) LayoutInflater.from(farmersprofile_fragment1.this).inflate(R.layout.custom_tab, null);
        tabtwo.setTextColor(Color.BLACK);
        tabLayout.addTab(tabLayout.newTab().setText("Loading..."));
        boolean b = NotificationManagerCompat.from(farmersprofile_fragment1.this).areNotificationsEnabled();
        activityResultLauncher =registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                ActivityCompat.requestPermissions(farmersprofile_fragment1.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 12);
            }
        });

            //if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {
                if (!b){
                    if (ContextCompat.checkSelfPermission(
                            farmersprofile_fragment1.this, Manifest.permission.POST_NOTIFICATIONS) ==
                            PackageManager.PERMISSION_GRANTED) {
                        // You can use the API that requires the permission.
                        //performAction(...);
                    } else {
                        // You can directly ask for the permission.
                        // The registered ActivityResultCallback gets the result of this request.
//                        activityResultLauncher.launch(
//                                Manifest.permission.POST_NOTIFICATIONS);
                        activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    }
            //}
        }

        username = user_singleton.getInstance().getUser_name();
        emailid = user_singleton.getInstance().getEmail_id();


//                    tabLayout.addTab(tabLayout.newTab().setText("Home"));
//                    tabLayout.addTab(tabLayout.newTab().setText("Identify"));
//                    tabLayout.addTab(tabLayout.newTab().setText("Report"));
//                    tabLayout.addTab(tabLayout.newTab().setText("Expert"));
//                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mPager = (ViewPager) findViewById(R.id.view_pagerq);
        //PagerAdapter adapter = new MyPagerAdapterdd(getSupportFragmentManager());
        MyPagerAdapterdd adapters = new MyPagerAdapterdd(farmersprofile_fragment1.this,getSupportFragmentManager(), tabLayout.getTabCount());
        mPager.setAdapter(adapters);



        mPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#803300"));


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
        //getd();


        //getcrops_viewmodel(tabLayout);

        //updatemodelenable();
    }
    void getd(){
        db.collection("loginaidisc")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("homedata", document.getId() + " => " + document.getData().get("token").toString());
                            }
                        } else {
                           // Log.d("homedata", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void add_token(String token, String id) {
        Map<String, Object> user = new HashMap<>();
        user.put("token", token);


        db.collection("loginaidisc").document(id)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d("Token updated", "DocumentSnapshot successfully written!"+token.toString());
                        //Toast.makeText(Farmer_profile.this, , Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("Token not updated"+token.toString(), "Error writing document", e);

                        //Toast.makeText(Farmer_profile.this, "Token not  updated", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(farmersprofile_fragment1.this);
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
//            // Otherwise, select the previous step.
           mPager.setCurrentItem(0);
       }
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.about_us1:

                Intent intent = new Intent(farmersprofile_fragment1.this, About_us1.class);
                startActivity(intent);

                break;
            case R.id.disclaimer:

                Intent intent1q = new Intent(farmersprofile_fragment1.this, disclaimer.class);
                startActivity(intent1q);



                break;
            case R.id.help1:

                Intent intent1 = new Intent(farmersprofile_fragment1.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.editme:

                Intent ibt=new Intent(farmersprofile_fragment1.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(farmersprofile_fragment1.this, contributor.class);
                startActivity(ibt1);

                break;
            case R.id.history:

                Intent ibt1q=new Intent(farmersprofile_fragment1.this, history.class);
                startActivity(ibt1q);

                break;

            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    public class MyPagerAdapterdd extends FragmentPagerAdapter {
        private final String[] titlenames={"Identification","Expert","News"};
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
            return 3;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();

            switch (position) {
//                case 0: // Fragment # 0 - This will show FirstFragment
//                    Fragment_home frag1=new Fragment_home();
//                    bundle.putString("argu1", username);
//                    if (addressed.matches("")){
//                        bundle.putString("argu2", "Unknown!");
//                    }else{bundle.putString("argu2", addressed);}
//
//                    frag1.setArguments(bundle);
//                    return frag1;
                case 0: // Fragment # 0 - This will show FirstFragment different title
                    //                    bundle.putStringArrayList("argu1",croplist);
//                    bundle.putIntegerArrayList("argu2", discrops_img);
//                    bundle.putIntegerArrayList("argu3", number_exist);
                    //frag2.setArguments(bundle);
                    return new Fragment_identification();
                case 2: // Fragment # 1 - This will show SecondFragment
                    Fragment_reporting frag3=new Fragment_reporting();

                    frag3.setArguments(bundle);
                    return frag3;
                case 1: // Fragment # 1 - This will show SecondFragment
                    return new Fragment_expert();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            //return titlenames[position];
            SpannableStringBuilder sb = null;
            sb=new SpannableStringBuilder("  \n" + titlenames[position]);;
            if (position == 0) {

                Drawable drawable = getDrawable(R.drawable.idnnn);
                drawable.setBounds(0, 0, 48, 48);
                ImageSpan imageSpan = new ImageSpan(drawable);

                //to make our tabs icon only, set the Text as blank string with white space
                sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (position == 1) {
                Drawable drawable = getDrawable(R.drawable.expert);
                drawable.setBounds(0, 0, 48, 48);
                ImageSpan imageSpan = new ImageSpan(drawable);
                //to make our tabs icon only, set the Text as blank string with white space
                sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (position == 2) {
                Drawable drawable = getDrawable(R.drawable.ic_baseline_language_24);
                drawable.setBounds(0, 0, 48, 48);
                ImageSpan imageSpan = new ImageSpan(drawable);
                //to make our tabs icon only, set the Text as blank string with white space
                sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            return sb;
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
        //unregisterReceiver(internet);

    }

    public void sign_out() {

        DocumentReference docRef = db.collection("loginaidisc").document(user_singleton.getInstance().getfb_id());

        // Remove the 'capital' field from the document
        Map<String, Object> updates = new HashMap<>();
        updates.put("token", FieldValue.delete());

        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       // Log.d("fff", "DocumentSnapshot successfully written!");
                        Toast.makeText(farmersprofile_fragment1.this, "Signed out", Toast.LENGTH_LONG).show();
                        shared_pref.remove_shared_preference(farmersprofile_fragment1.this);
                        Intent intent = new Intent(farmersprofile_fragment1.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("fff", "Error writing document", e);
                        Toast.makeText(farmersprofile_fragment1.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(farmersprofile_fragment1.this,R.style.CustomAlertDialog);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(farmersprofile_fragment1.this, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(farmersprofile_fragment1.this);
                    Intent intent = new Intent(farmersprofile_fragment1.this, Login.class);
                    startActivity(intent);
                    finish();
                }else{
                sign_out();}
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

                            String locality="";
                            String countryName="";
                            try{
                            List<Address> addresses = getGeocoderAddress(farmersprofile_fragment1.this);
                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                locality= address.getLocality();

                                countryName= address.getCountryName();


                            }}
                            catch (Exception e){}
                            addressed=locality+", "+countryName;
                            loct.setText(addressed);
                            if (!locality.matches("")){
                                user_singleton.getInstance().setloct(addressed);
                            }
                            if (!Latitude.matches("")){
                                user_singleton.getInstance().setlon(Longitude);
                                user_singleton.getInstance().setlat(Latitude);
                                updateloctoken(Latitude,Longitude);
                            }
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
            //List<Address> addresses = getGeocoderAddress(farmersprofile_fragment1.this);
            String locality="";
            String countryName="";
            try{
                List<Address> addresses = getGeocoderAddress(farmersprofile_fragment1.this);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    locality= address.getLocality();

                    countryName= address.getCountryName();


                }}
            catch (Exception e){}
            addressed=locality+", "+countryName;
            loct.setText(addressed);
            if (!locality.matches("")){
                user_singleton.getInstance().setloct(addressed);
            }
            if (!Latitude.matches("")){
                user_singleton.getInstance().setlon(Longitude);
                user_singleton.getInstance().setlat(Latitude);
                updateloctoken(Latitude,Longitude);
            }

        }
    };

    private void updateloctoken(String latitude, String longitude) {
        JSONObject objectd = new JSONObject();
        try {
            objectd.put("user_id", user_singleton.getInstance().getUser_id());
            objectd.put("lat", latitude);
            objectd.put("lon", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/updatelocation")
                .addJSONObjectBody(objectd)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean res=response.getBoolean("book");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

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

            return geocoder.getFromLocation(Double.parseDouble(Latitude), Double.parseDouble(Longitude), this.geocoderMaxResults);
        } catch (IOException e) {
            //e.printStackTrace();
            //Log.e(TAG, "Impossible to connect to Geocoder", e);
        }


        return null;
    }

}