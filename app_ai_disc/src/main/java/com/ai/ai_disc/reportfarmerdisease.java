package com.ai.ai_disc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.Farmer.GPSTracker;
import com.ai.ai_disc.model.create_report_response;
import com.ai.ai_disc.model.createreport_model;
import com.ai.ai_disc.model.map_info_response;
import com.ai.ai_disc.model.report_info_model;
import com.ai.ai_disc.view_model.create_report;
import com.ai.ai_disc.view_model.getcropidnames;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class reportfarmerdisease extends AppCompatActivity {
    TextView usern,latlongt,disorpest,dptexts;
    RecyclerView gvGallery;
    EditText write;
    String file_path = "";
    String file_path_string = "";
    Spinner cropn,dispest;
    ArrayList<String> croplist=new ArrayList<>();
    ArrayList<String> cropidlist=new ArrayList<>();
    ArrayList<JSONArray> dispestlist=new ArrayList<>();
    ArrayList<JSONArray> dispestidlist=new ArrayList<>();
    String cropchoosen="",diseasepestchoosen="";
            int cropidchoosen,dispestidchoosen;
    int type;
    Location location;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    int LAUNCH_MAP_ACTIVITY=2;
    int geocoderMaxResults = 1;
    int reportid;
    InternetReceiver internet;
    getcropidnames viewmodel;
    ProgressBar spin;
    static Bitmap bitmap=null;
    //ImageView img;
    MaterialButton upload;
    LinearLayout adding;
    Button addimage,gpscheck;
    int bits;
    static String Latitude="22.5",Longitude="77.5";
    static String country,city,address="unknown",postal;
    private static ImageView img;
    ArrayList<Bitmap> bitmaps=new ArrayList<>();
    ArrayList<Uri> mArrayUri=new ArrayList<>();
    ArrayList<String> filepaths=new ArrayList<>();
    ArrayList<String> filepathstrings=new ArrayList<>();
    GalleryAdapter_report1 galleryAdapter1;
    GPSTracker gpsTracker;
    SupportMapFragment mapFragment;
    create_report reportview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportfarmerdisease);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create Report");
//        String latn=getIntent().getExtras().get("lat").toString();
//        String longi=getIntent().getExtras().get("lang").toString();
//        String address=getIntent().getExtras().get("address").toString();

        internet = new InternetReceiver();
        gpsTracker = new GPSTracker(reportfarmerdisease.this);
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapupload);

        String usn=user_singleton.getInstance().getUser_name().toString();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try{getLastLocation();} catch (Exception e) {
            e.printStackTrace();
        }
        // method to get the location

        //String latlon=latn+", "+longi;
        usern=findViewById(R.id.textView6);
        latlongt=findViewById(R.id.textView8);
        cropn=findViewById(R.id.cropspin);
        dispest=findViewById(R.id.dispestspin);
        disorpest=findViewById(R.id.textView9);
        gpscheck=findViewById(R.id.checkgps1);
        spin=findViewById(R.id.progressBar3);
        spin.setVisibility(View.GONE);
        cropn.setVisibility(View.GONE);
        dispest.setVisibility(View.GONE);

        //gvGallery=findViewById(R.id.grid_viewq);
        gpscheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gpsTracker.getIsGPSTrackingEnabled())
                {double laty=gpsTracker.getLatitude();
                    if (laty!=0.00){
                        address=gpsTracker.getAddressLine(reportfarmerdisease.this);
                        latlongt.setText(address);
                        Latitude=String.valueOf(gpsTracker.getLatitude());
                        Longitude=String.valueOf(gpsTracker.getLongitude());


                        Toast.makeText(reportfarmerdisease.this,"GPS is ON !",Toast.LENGTH_SHORT).show();
                        gpscheck.setVisibility(View.GONE);
                    }
                    else{
                        Intent intent = new Intent(reportfarmerdisease.this, setMaps.class);
                        startActivityForResult(intent,LAUNCH_MAP_ACTIVITY);
                        gpscheck.setVisibility(View.GONE);


                    }
                }else{
                    Intent intent = new Intent(reportfarmerdisease.this, setMaps.class);
                    startActivityForResult(intent,LAUNCH_MAP_ACTIVITY);
                    gpscheck.setVisibility(View.GONE);

                }


            }
        });

        gpscheck.setVisibility(View.GONE);
        usern.setText(usn);
        bits=0;
        dptexts=findViewById(R.id.dpest);

        write=findViewById(R.id.writesomething);
        write.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 100)
                {
                    Toast.makeText(reportfarmerdisease.this, "Maximum limit is 100 characters !", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

         upload=findViewById(R.id.reporting);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmaps.size()!=0){
                    String hs=write.getText().toString();
                    if (hs.matches("")){
                        hs="nothing";
                    }

                    uploadpics(hs);


                }else{
                    Toast.makeText(reportfarmerdisease.this, "Upload atleast one Image !", Toast.LENGTH_LONG).show();

                }
            }
        });
        adding=findViewById(R.id.addingimageview);
        addimage=findViewById(R.id.addmore);
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (address.matches("unknown")){
                    Toast.makeText(reportfarmerdisease.this, "Set your Location !", Toast.LENGTH_LONG).show();
                }
                else{
                    bits+=1;
                    // initialising new layout
                    ImageView imageView = new ImageView(reportfarmerdisease.this);

                    // setting the image in the layout
                    imageView.setImageResource(R.drawable.imageicon);
                    imageView.setId(bits);

                    // calling addview with width and height
                    addvieW(imageView, 200, 200);
                    if (bits!=0){
                        addimage.setText("Add more image");
                    }

                    // adding the background color
                    colorrandom(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            img=imageView;
                            CharSequence[] dptext={"Take Photo","Select from Gallery", "Exit"};
                            AlertDialog.Builder dp=new AlertDialog.Builder(reportfarmerdisease.this);
                            dp.setTitle("Upload Image");
                            dp.setItems(dptext, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (dptext[i].equals("Take Photo")){
                                        if (checking_permision()) {


                                            Intent intent = new Intent(reportfarmerdisease.this, CAM2.class);
                                            startActivityForResult(intent, 4);

                                        }
                                    }
                                    else if(dptext[i].equals("Select from Gallery")){
                                        if (ContextCompat.checkSelfPermission(reportfarmerdisease.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(reportfarmerdisease.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                                        } else {

                                            Intent intent = new Intent();
                                            intent.setType("image/*");
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                            startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), 1);
                                        }
                                    }else{
                                        dialogInterface.dismiss();
                                    }
                                }
                            });dp.show();

                        }
                    });
                }

            }
        });
        upload.setVisibility(View.GONE);
        adding.setVisibility(View.GONE);
        addimage.setVisibility(View.GONE);
        write.setVisibility(View.GONE);
        gettype();

    }
    private void gettype() {
        CharSequence[] dptext = {"Disease", "Pest", "Exit"};
        AlertDialog.Builder dp = new AlertDialog.Builder(reportfarmerdisease.this);
        dp.setTitle("Type of occurrence?");
        dp.setItems(dptext, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dptext[i].equals("Disease")) {
                    type = 1;
                    getcrops(1);
                } else if (dptext[i].equals("Pest")) {
                    type = 2;
                    dptexts.setText("Pest");
                    getcrops(2);
                } else {
                    dialogInterface.dismiss();
                    Intent iny = new Intent(reportfarmerdisease.this, farmersprofile_fragment.class);
                    startActivity(iny);
                }
            }
        });
        dp.show();
    }

    private void getcrops(int typ){

        spin.setVisibility(View.VISIBLE);

        viewmodel= ViewModelProviders.of(reportfarmerdisease.this).get(getcropidnames.class);
        viewmodel.getting_crops(typ).observe(reportfarmerdisease.this, new Observer<map_info_response>() {
            @Override
            public void onChanged(map_info_response map_info_response) {
                spin.setVisibility(View.GONE);
                if (map_info_response!=null){
                    report_info_model model = map_info_response.getModel();
                    croplist=model.getcropname();
                    cropidlist=model.getcropid();
                    dispestlist=model.getdpnames();
                    dispestidlist=model.getdpid();
                    cropn.setVisibility(View.VISIBLE);
                    dispest.setVisibility(View.VISIBLE);


//                    if (gpsTracker.getIsGPSTrackingEnabled())
//                    {double laty=gpsTracker.getLatitude();
//
//                        Latitude = String.valueOf(laty);
//                        //Toast.makeText(farmersprofile_fragment.this, "Can not access here."+Latitude, Toast.LENGTH_SHORT).show();
//
//                        Longitude = String.valueOf(gpsTracker.getLongitude());
//                        country = gpsTracker.getCountryName(reportfarmerdisease.this);
//                        city = gpsTracker.getLocality(reportfarmerdisease.this);
//                        postal = gpsTracker.getPostalCode(reportfarmerdisease.this);
//                        address = gpsTracker.getAddressLine(reportfarmerdisease.this);
//                        if (laty==0.00){
//                            Toast.makeText(reportfarmerdisease.this, "GPS not working !", Toast.LENGTH_SHORT).show();
//
//                        }
//                        else{
//                            //Toast.makeText(reportfarmerdisease.this, "GPS is on !", Toast.LENGTH_SHORT).show();
//
//                        }
//
//                    }
//                    else{
//                        Toast.makeText(reportfarmerdisease.this, "GPS not enabled !", Toast.LENGTH_SHORT).show();
//
//                    }
                    if (address.matches("unknown")){
                        gpscheck.setVisibility(View.VISIBLE);
                    }else{
                        gpscheck.setVisibility(View.GONE);
                    }
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            final LatLngBounds dfg = new LatLngBounds(
                                    new LatLng(5.15, 67.4), new LatLng(37.35, 101.23));
                            LatLng loct = new LatLng(Float.parseFloat(Latitude), Float.parseFloat(Longitude));
                            googleMap.setLatLngBoundsForCameraTarget(dfg);
                            //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                            googleMap.addMarker(new MarkerOptions().position(loct).title("Here you are !!!").visible(true));


                            // below lin is use to zoom our camera on map.
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));

                            // below line is use to move our camera to the specific location.
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(loct));

                        }
                    });
                    latlongt.setText(address);
                    cropn.setVisibility(View.VISIBLE);
                    dispest.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.VISIBLE);
                    adding.setVisibility(View.VISIBLE);
                    addimage.setVisibility(View.VISIBLE);
                    write.setVisibility(View.VISIBLE);
                    //upload.setEnabled(false);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(reportfarmerdisease.this,
                            android.R.layout.simple_spinner_item,croplist);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cropn.setAdapter(adapter);
                    cropn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String crp=croplist.get(i);
                            String crpid=cropidlist.get(i);
                            cropchoosen=crp;
                            cropidchoosen=Integer.parseInt(crpid);
                            ArrayList<String> listdatas = new ArrayList<String>();
                            ArrayList<String> listdatasid = new ArrayList<String>();
                            JSONArray jArrayi = dispestlist.get(i);
                            JSONArray jArrayid = dispestidlist.get(i);
                            if (jArrayi != null) {
                                for (int ii=0;ii<jArrayi.length();ii++){
                                    listdatas.add(jArrayi.optString(ii));
                                    listdatasid.add(jArrayid.optString(ii));
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(reportfarmerdisease.this,
                                    android.R.layout.simple_spinner_item,listdatas);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            dispest.setAdapter(adapter);
                            dispest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int ij, long l) {
                                    diseasepestchoosen= listdatas.get(ij);
                                    dispestidchoosen=Integer.parseInt(listdatasid.get(ij));

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }

            }
        });


    }
    public boolean checking_permision() {


        if ((ContextCompat.checkSelfPermission(reportfarmerdisease.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(reportfarmerdisease.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(reportfarmerdisease.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return false;

        }


        return true;
    }
    public void converting_string(String file_path) {

        if (!file_path.isEmpty()) {


            Uri address_to_convert = Uri.parse(file_path);
            System.out.println("Uri is:" + address_to_convert);

            try {

                InputStream inputstream = null;
                inputstream = getContentResolver().openInputStream(address_to_convert);
                //  inputstream = new


                System.out.println(" input stream is :");
                int length_image = 0;

                length_image = inputstream.available();
                // System.out.println("  length:" + length_image);
                byte[] data_in_byte_image = new byte[length_image];
                // System.out.println("  length of data in byte " + data_in_byte_image.length);
                // System.out.println(" data in byte " + data_in_byte_image.length);
                inputstream.read(data_in_byte_image);
                inputstream.close();
                System.out.println(" after inputstream close");
                file_path_string = "";
                file_path_string = Base64.encodeToString(data_in_byte_image, Base64.DEFAULT);
                // System.out.println(" image in string :" +file_path_string);


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(reportfarmerdisease.this, "Error in converting ", Toast.LENGTH_LONG).show();
                file_path_string = "";
            }
        } else {

            System.out.println(" inside else ");
        }
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
    public void onActivityResult(int requestcode, int responsecode, Intent data) {
        super.onActivityResult(requestcode, responsecode, data);
        switch (requestcode) {

            case 1:

                if (requestcode == 1 && responsecode == RESULT_OK && data != null) {

                    final Uri address = data.getData();

                    System.out.println(" address 1:" + address);

                    img.setImageURI(address);
                    try {
                        bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    img.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.VISIBLE);
                    upload.setEnabled(true);
                    file_path = String.valueOf(address);
                    bitmaps.add(bitmap);
                    filepaths.add(file_path);
                    mArrayUri.add(address);


                } else {
                    Toast.makeText(reportfarmerdisease.this, "File is not selected.", Toast.LENGTH_LONG).show();
                }

                break;


            case 4:


                if (requestcode == 4 && responsecode == RESULT_OK) {


                    String url = data.getStringExtra("url");
                    System.out.println(" url is:" + url);
                    img.setImageURI(Uri.parse(url));
                    try {
                        bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(url));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    img.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.VISIBLE);
                    upload.setEnabled(true);
                    file_path = url;
                    bitmaps.add(bitmap);
                    filepaths.add(file_path);
                    mArrayUri.add(Uri.parse(url));


                } else {
                    Toast.makeText(reportfarmerdisease.this, "No image captured", Toast.LENGTH_LONG).show();


                }

                break;
            case 2:

                if(responsecode == Activity.RESULT_OK){
                     Latitude=data.getStringExtra("latitude");
                    Longitude=data.getStringExtra("longitude");
//                Log.d(TAG, "onActivityResult: "+latitude);
//                Log.d(TAG, "onActivityResult: "+longitude);

                    List<Address> addresses = getGeocoderAddress(reportfarmerdisease.this);
                    String locality="";
                    String countryName="";
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        locality= address.getLocality();

                        countryName= address.getCountryName();


                    }latlongt.setText(locality+", "+countryName);
                }
                if (responsecode == Activity.RESULT_CANCELED) {
                    // Write your code if there's no result
                }

            default:
                break;
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(internet);

    }

    public void colorrandom(ImageView imageView) {

        // Initialising the Random();
        Random random = new Random();

        // adding the random background color
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        // setting the background color
        imageView.setBackgroundColor(color);
    }

    private void addvieW(ImageView imageView, int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200,200);

        // setting the margin in linearlayout
        params.setMargins(0, 10, 0, 10);
        imageView.setLayoutParams(params);


        // adding the image in layout
        adding.addView(imageView);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
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

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(reportfarmerdisease.this);


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
        android.app.AlertDialog.Builder opt = new android.app.AlertDialog.Builder(reportfarmerdisease.this);
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
    private void uploadpics(String info){
        spin.setVisibility(View.VISIBLE);
        int uidd=Integer.parseInt(user_singleton.getInstance().getUser_id().toString());
        reportview= ViewModelProviders.of(reportfarmerdisease.this).get(create_report.class);
        reportview.getting_crops(type, dispestidchoosen,  uidd,  Double.parseDouble(Latitude), Double.parseDouble(Longitude), info,cropidchoosen).observe(reportfarmerdisease.this, new Observer<create_report_response>() {
            @Override
            public void onChanged(create_report_response create_report_response) {
                spin.setVisibility(View.GONE);
                if (create_report_response.getModel()!=null){
                    createreport_model model=create_report_response.getModel();
                    String reportids=model.getreport();
                    if (reportids.matches("")){
                        Toast.makeText(reportfarmerdisease.this,"Error !!!",Toast.LENGTH_SHORT).show();
                    }else{
                        reportid=Integer.parseInt(reportids);
                        Intent iju=new Intent(reportfarmerdisease.this,uploadimagereport.class);
                        iju.putParcelableArrayListExtra("array",mArrayUri);
                        iju.putExtra("report",reportid);
                        startActivity(iju);
                    finish();}
                }
                else{
                    Toast.makeText(reportfarmerdisease.this, "Error !!!", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    private void trial(String info){
        Intent iju=new Intent(reportfarmerdisease.this,uploadimagereport.class);
        iju.putParcelableArrayListExtra("array",mArrayUri);
        iju.putExtra("report",reportid);
        startActivity(iju);


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
                        try{ location = task.getResult();} catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            //latitudeTextView.setText(String.format("%.2f",location.getLatitude())+"/"+String.format("%.2f",location.getLongitude()));
                            Latitude=String.valueOf(location.getLatitude());
                            Longitude=String.valueOf(location.getLongitude());

                            List<Address> addresses = getGeocoderAddress(reportfarmerdisease.this);
                            String locality="";
                            String countryName="";
                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                locality= address.getLocality();

                                countryName= address.getCountryName();


                            }
                            address=locality+", "+countryName;
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
            List<Address> addresses = getGeocoderAddress(reportfarmerdisease.this);
            String locality="";
            String countryName="";
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                locality= address.getLocality();

                countryName= address.getCountryName();


            }
            address=locality+", "+countryName;

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

}