package com.ai.ai_disc;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
//import androidx.work.Data;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkInfo;

import com.ai.ai_disc.Farmer.Farmer_Disease_identifier_pest_Identifier_Activity;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

public class create_report extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.abouradio)
    RadioGroup abouradio;
    String  AUTH_KEY_FCM="";
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @BindView(R.id.categoryradio)
    RadioGroup categoryradio;
    @BindView(R.id.forgot_head)
    TextView forgot_head;
    ProgressDialog progress;
    @BindView(R.id.forgot_info)
    TextView forgot_info;
    int bits=1;
    @BindView(R.id.itisabout)
    TextView itisabout;
    @BindView(R.id.crop)
    TextView crop;
    @BindView(R.id.categorytext)
    TextView categorytext;
    InternetReceiver internet ;
    @BindView(R.id.dpnametext)
    TextView dpnametext;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    @BindView(R.id.cropspinner)
    Spinner cropspinner;
    @BindView(R.id.dpspinner)
    Spinner dpspinner;
    @BindView(R.id.otherdplin)
    LinearLayout otherdplin;
    int geocoderMaxResults = 1;
    @BindView(R.id.othercroplin)
    LinearLayout othercroplin;
    @BindView(R.id.croplin)
    LinearLayout croplin;
    @BindView(R.id.otherincidenceslin)
    CardView otherincidenceslin;
    @BindView(R.id.categorylin)
    LinearLayout categorylin;
    @BindView(R.id.dplin)
    LinearLayout dplin;
    @BindView(R.id.imageuploadcard)
    CardView imageuploadcard;
    @BindView(R.id.bottomline)
    LinearLayout bottomline;
    @BindView(R.id.otherincidencehead)
    EditText otherincidencehead;
    @BindView(R.id.otherincidenceinfo)
    EditText otherincidenceinfo;
    @BindView(R.id.otherdp)
    EditText otherdp;
    @BindView(R.id.othercrop)
    EditText othercrop;
    @BindView(R.id.imageView8)
    ImageView othercroppush;
    @BindView(R.id.imageView9)
    ImageView otherdppush;
    @BindView(R.id.addimage)
    ImageView addimage;
    @BindView(R.id.imagefirst)
    ImageView imagefirst;
    TextView loct;
    private static ImageView imn;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    Location location;
    @BindView(R.id.go)
    Button go;
    @BindView(R.id.reset)
    Button reset;
    static String Latitude="",Longitude="";
    int target=0;
    ArrayList<Uri> bitmaps;
    String crop_id="",crop_name="",disease_id="",dpname="",insect_id="",nut_id="",headings="",info_full="";
    int numb_img=0;
    int report_type=0;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ArrayAdapter cropadapter,disadapter;
    ArrayList<String> crop_array,crop_id_array,dp_array,dp_id_array;
ArrayList<String> imagefilesstring;
    ArrayList<String>exp_token;
String lnb="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        setTitle("Report");
        numb_img=0;
        context=create_report.this;
        imagefilesstring=new ArrayList<>();
        bits=0;
        bitmaps=new ArrayList<>();
        report_type=0;
        ButterKnife.bind(this);
        internet = new InternetReceiver();
        setNavigationViewListener();
        crop_array=new ArrayList<>();
        crop_id_array=new ArrayList<>();
        dp_array=new ArrayList<>();
        dp_id_array=new ArrayList<>();
        getcrops();
        othercroppush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (othercrop.getText().toString().matches("")){
                    Toast.makeText(create_report.this,"Enter Crop name",Toast.LENGTH_SHORT).show();
                }
                else{
                    crop_name=othercrop.getText().toString();
                    crop.setVisibility(View.VISIBLE);
                    crop.setText(othercrop.getText().toString());
                    cropspinner.setVisibility(View.GONE);
                    categorylin.setVisibility(View.VISIBLE);
                    othercroplin.setVisibility(View.GONE);
                }
            }
        });
        dpspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sd=dp_id_array.get(position);
                if (!sd.isEmpty()){
                    dpspinner.setVisibility(View.GONE);
                    dpnametext.setVisibility(View.VISIBLE);
                    dpnametext.setText(dp_array.get(position));
                    switch (report_type)
                    {
                        case 2:{disease_id=sd;  break;  }
                        case 3:{insect_id=sd;  break; }
                        case 4:{nut_id=sd;  break;  }
                    }
                    target=10;
                    dpname=dp_array.get(position);
                    otherdplin.setVisibility(View.GONE);
                }
                else{
                    if(position==dp_id_array.size()-1){
                        otherdplin.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        otherdppush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otherdp.getText().toString().matches("")){
                    Toast.makeText(create_report.this,"Enter Something",Toast.LENGTH_SHORT).show();
                }
                else{
                    if (!crop_id.isEmpty()){
                        dpname=otherdp.getText().toString();
                        dpnametext.setVisibility(View.VISIBLE);
                        dpnametext.setText(otherdp.getText().toString());
                        otherdplin.setVisibility(View.GONE);
                        dpspinner.setVisibility(View.GONE);

                    }else{
                        dpname=otherdp.getText().toString();
                        dpnametext.setVisibility(View.VISIBLE);
                        dpnametext.setText(otherdp.getText().toString());
                        otherdplin.setVisibility(View.GONE);
                        dpspinner.setVisibility(View.GONE);
                    }
                    target=10;
                }
            }
        });
        categoryradio.clearCheck();
        categoryradio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton
                        r
                        = (RadioButton)group
                        .findViewById(checkedId);

                String selectedtext = r.getText().toString();
                //Toast.makeText(create_report.this,"dd"+selectedtext,Toast.LENGTH_SHORT).show();

                if (selectedtext.matches("Disease Infestation")){
                    if (!crop_id.isEmpty()) {
                        categorytext.setVisibility(View.VISIBLE);
                        categorytext.setText(selectedtext);
                        categoryradio.setVisibility(View.GONE);
                        dplin.setVisibility(View.VISIBLE);
                        report_type=2;
                        getting_disease_name(crop_id);
                    }else{
                        categorytext.setVisibility(View.VISIBLE);
                        categorytext.setText(selectedtext);
                        categoryradio.setVisibility(View.GONE);
                        dplin.setVisibility(View.VISIBLE);
                        dpspinner.setVisibility(View.GONE);
                        otherdplin.setVisibility(View.VISIBLE);
                    }
                }else
                if (selectedtext.matches("Insect Occurrence")){
                    if (!crop_id.isEmpty()) {
                        categorytext.setVisibility(View.VISIBLE);
                        categorytext.setText(selectedtext);
                        categoryradio.setVisibility(View.GONE);
                        dplin.setVisibility(View.VISIBLE);
                        getting_list_insect(crop_id);
                        report_type=3;
                    }else{
                        categorytext.setVisibility(View.VISIBLE);
                        categorytext.setText(selectedtext);
                        categoryradio.setVisibility(View.GONE);
                        dpspinner.setVisibility(View.GONE);
                        dplin.setVisibility(View.VISIBLE);
                        otherdplin.setVisibility(View.VISIBLE);
                    }
                }else
                if (selectedtext.matches("Nutrient Defficiency Symtoms")){
                    if (!crop_id.isEmpty()) {
                        categorytext.setVisibility(View.VISIBLE);
                        categorytext.setText(selectedtext);
                        categoryradio.setVisibility(View.GONE);
                        dplin.setVisibility(View.VISIBLE);
                        getting_Nutrition_list();
                        report_type=4;
                    }else{
                        categorytext.setVisibility(View.VISIBLE);
                        categorytext.setText(selectedtext);
                        categoryradio.setVisibility(View.GONE);
                        dpspinner.setVisibility(View.GONE);
                        dplin.setVisibility(View.VISIBLE);
                        otherdplin.setVisibility(View.VISIBLE);
                    }
                }else
                if (selectedtext.matches("Other Crop Incidences")){
                    categorytext.setVisibility(View.VISIBLE);
                    categorytext.setText(selectedtext);

                    categoryradio.setVisibility(View.GONE);
                    report_type=5;
                    target=10;
                }else{

                }
            }
        });

        cropadapter=new ArrayAdapter(create_report.this,android.R.layout.simple_list_item_1,crop_array);
        cropspinner.setAdapter(cropadapter);
        cropspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String idf=crop_id_array.get(position);
                if (!idf.isEmpty()){
                        crop_name=crop_array.get(position);
                        crop_id=idf;
                        crop.setVisibility(View.VISIBLE);
                    crop.setText(crop_array.get(position));
                    cropspinner.setVisibility(View.GONE);
                    categorylin.setVisibility(View.VISIBLE);
                    othercroplin.setVisibility(View.GONE);

                }else{
                if(position==crop_array.size()-1){
                    othercroplin.setVisibility(View.VISIBLE);
                }}

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        disadapter=new ArrayAdapter(create_report.this,android.R.layout.simple_list_item_1,dp_array);
        dpspinner.setAdapter(disadapter);

        drawerLayout = findViewById(R.id.my_drawer_layout2);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            getLastLocation();} catch (Exception e) {
            e.printStackTrace();
        }
        exp_token=new ArrayList<>();
        getfollowers();
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer1);
        View head=navigationView.getHeaderView(0);

        TextView userd=head.findViewById(R.id.name_dataentry);
        TextView acc=head.findViewById(R.id.type_nav);
        loct=head.findViewById(R.id.loctfff);
        //loct.setText(addressed);
        try{userd.setText(user_singleton.getInstance().getfname()+" "+user_singleton.getInstance().getMname()+" "+user_singleton.getInstance().getlname());
            acc.setText(user_singleton.getInstance().getUser_type()+" Account");
            loct.setText(user_singleton.getInstance().getloct());
            //Log.d("ggg",head.findViewById(R.id.vsr).toString());


        } catch (Exception e) {
            e.printStackTrace();
           // Log.d("error",e.toString());
        }
        abouradio.clearCheck();
        abouradio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton
                        r
                        = (RadioButton)group
                        .findViewById(checkedId);

                String selectedtext = r.getText().toString();
                if (selectedtext.matches("Crop Field")){
                    itisabout.setVisibility(View.VISIBLE);
                    itisabout.setText("CROP");
                    abouradio.setVisibility(View.GONE);
                    croplin.setVisibility(View.VISIBLE);


                }
                else {
                    report_type=1;
                    target=10;
                }

            }
        });

        imagefirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imn=imagefirst;
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {getimage();}else{
                if (checking_permision()   ){
                    getimage();

                }}
            }
        });

        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bits<=5) {

                    ImageView nimage = new ImageView(create_report.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.setMargins(5, 5, 5, 5);
                    nimage.setLayoutParams(params);
                    nimage.setImageDrawable(getResources().getDrawable(R.drawable.addimage, getTheme()));
                    bottomline.addView(nimage);
                    nimage.setId(bits);
                    bits += 1;

                    nimage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imn = nimage;
                            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {getimage();}else{
                            if (checking_permision()) {
                                getimage();

                            }}

                        }
                    });
                }
                else{
                    Toast.makeText(create_report.this, "Allowed 5 images only", Toast.LENGTH_SHORT).show();
                }
            }
        });
        otherincidencehead.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==0){forgot_head.setVisibility(View.VISIBLE);}else{forgot_head.setVisibility(View.GONE);}

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otherincidenceinfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==0){forgot_info.setVisibility(View.VISIBLE);}else{forgot_info.setVisibility(View.GONE);}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(create_report.this, String.valueOf(target)+" "+String.valueOf(bitmaps.size()), Toast.LENGTH_SHORT).show();
                if(target==10){
                     headings=otherincidencehead.getText().toString();
                     info_full=otherincidenceinfo.getText().toString();
                    if (bitmaps.size()!=0 && !headings.matches("") && !info_full.matches("")){
                        go.setText("submitting...");
                        reset.setVisibility(View.GONE);
                        if (!Latitude.matches("")){
                        gosubmit();}else{
                            Toast.makeText(create_report.this,"Getting Location please wait...",Toast.LENGTH_LONG).show();
                            try{
                                getLastLocation();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else{
                        if(headings.matches("")){
                            forgot_head.setVisibility(View.VISIBLE);
                        }
                        if (info_full.matches(""))
                        {
                            forgot_info.setVisibility(View.VISIBLE);
                        }
                        if(bitmaps.size()==0){
                            Toast.makeText(create_report.this, "Upload at least one image", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                else{
                    Toast.makeText(create_report.this, "Fill up required information", Toast.LENGTH_SHORT).show();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder obt=new AlertDialog.Builder(create_report.this);
                obt.setTitle("Are You sure ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent nv=new Intent(create_report.this,create_report.class);
                                startActivity(nv);
                                finish();

                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if(Environment.isExternalStorageManager())
//            {
//                File internal = new File("/sdcard");
//                //internalContents = internal.listFiles();
//                //Log.d("path",internal.toString());
//                //getimage();
//            }
//            else
//            {
//                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivity(permissionIntent);
//            }
//        }
        
        


    }

    private void gosubmit() {
        progress = new ProgressDialog(create_report.this);
        progress.setCancelable(false);
        progress.setMessage("Please wait..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        JSONObject object = new JSONObject();
        try {

            object.put("image_file", new JSONArray(imagefilesstring));
            object.put("user_id", user_singleton.getInstance().getUser_id());
            object.put("crop_id", crop_id);
            object.put("crop_name", crop_name);
            object.put("disease_id", disease_id);
            object.put("insect_id", insect_id);
            object.put("nut_id", nut_id);
            object.put("report_type", String.valueOf(report_type));
            object.put("head", headings);
            object.put("info", info_full);
            object.put("lat", Latitude);
            object.put("lon", Longitude);
            object.put("loc", lnb);
            object.put("dpname", dpname);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("console",object.toString());
        AndroidNetworking.post("https://aidisc.krishimegh.in:32517/submit_news_images")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        progress.cancel();
                        go.setText("Done");
                        reset.setVisibility(View.VISIBLE);
                        //Log.d("jjjj",jsonObject.toString());
                        boolean res=false;
                        try {
                             res=jsonObject.getBoolean("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (res){
                            sendmsg(exp_token);
                            Toast.makeText(create_report.this,"Success",Toast.LENGTH_SHORT).show();
                            create_report.super.onBackPressed();
                            finish();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        go.setText("Submit");
                        progress.cancel();
                        reset.setVisibility(View.VISIBLE);
                        Toast.makeText(create_report.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                });

    }
    void sendmsg(ArrayList<String> token_to){
        if (!token_to.isEmpty()  ){

            JSONArray tokens = new JSONArray();
            for (int i =0;i<token_to.size();i+=1){
                tokens.put(token_to.get(i).trim());
            }
            JSONObject body = new JSONObject();
            JSONObject data1 = new JSONObject();

            try {
                data1.put("request", "news");
                data1.put("news", headings);

                data1.put("username", user_singleton.getInstance().getUser_name());
                body.put("data", data1);
                body.put("registration_ids", tokens);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        /*
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");
         .addHeaders("Authorization","key="+AUTH_KEY_FCM)
                .addHeaders("Content-Type","application/json")
         */
            AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getfcn")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                AUTH_KEY_FCM=jsonObject.getString("fcn");
                                AndroidNetworking.post("https://fcm.googleapis.com/fcm/send")
                                        .addHeaders("Authorization", "key=" + AUTH_KEY_FCM)
                                        .addHeaders("Content-Type", "application/json")


                                        .addJSONObjectBody(body)


                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                // do anything with response
                                                //Log.d("send _notify", "onResponse: " + response);


                                                try {
                                                    int code = (int) response.get("success");

                                                    if (code == 1) {
                                                        /// Toast.makeText(Outgoing.this,"Request sent",Toast.LENGTH_LONG).show();
                                                        //request.setText("Request sent");
                                                        //  finish();
                                                    } else if (code == 0) {
                                                        //  Toast.makeText(Outgoing.this,"Request  not sent",Toast.LENGTH_LONG).show();
                                                        //request.setText("Request not  sent");
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                            @Override
                                            public void onError(ANError error) {
                                                // handle error
                                                //Log.d(TAG, "onError: " + error.getMessage());
                                                // Toast.makeText(Outgoing.this,"Error,Request not sent",Toast.LENGTH_LONG).show();
                                                //request.setText("Error");
                                            }
                                        });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }else{
            Log.d("token not found", "onError: " );
        }



    }


    void getfollowers(){
        JSONObject objectd = new JSONObject();
        try {
            objectd.put("user_id", user_singleton.getInstance().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String>followed=new ArrayList<>();
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getfolloweds")
                .addJSONObjectBody(objectd)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = null;
                        Log.d("hhh",response.toString());
                        try {
                            array = response.getJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {

                                followed.add((String) array.getString(i));}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (!followed.isEmpty()){
                            for (int i =0;i<followed.size();i+=1){
                                db.collection("loginaidisc")
                                        .whereEqualTo("username", followed.get(i))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        //Log.d("homedata", document.getId() + " => " + document.getData().get("token").toString());
                                                        String tk = String.valueOf(document.getData().get("token"));
                                                        exp_token.add(tk);
                                                    }
                                                } else {
                                                    Log.d("homedata", "Error getting documents: ", task.getException());
                                                }

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }


    private void getcrops() {
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/list_crop")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        crop_array.clear();
                        crop_id_array.clear();
                        crop_array.add(0,"select crop");
                        crop_id_array.add(0,"");
                        try {


                            JSONArray array = response.getJSONArray("cropreport_list");

                            for(int i=0;i<array.length();i++){

                                JSONObject object = (JSONObject) array.get(i);

                                String id= object.getString("id");
                                String name= object.getString("name");
                                crop_array.add(i+1,name);
                                crop_id_array.add(i+1,id);
                            }
                            crop_array.add("Other Crop");
                            crop_id_array.add("");
                            cropadapter.notifyDataSetChanged();
                            cropspinner.setAdapter(cropadapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                    }
                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(create_report.this,"There is error in list of crop.",Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void getting_list_insect(String crop_id) {

        JSONObject object = new JSONObject();
        try {
            object.put("crop_id", crop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(" object sending:" + object);


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/list_insect")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        dp_array.clear();
                        dp_id_array.clear();

                        dp_id_array.add(0, "");
                        dp_array.add(0, "Select Insect");

                        try {


                            JSONArray array = response.getJSONArray("insect_list");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);

                                String id = object.getString("insect_id");
                                String name = object.getString("insect_name");
                                dp_array.add(i + 1, name);
                                dp_id_array.add(i + 1, id);
                            }
                            dp_array.add("Others");
                            dp_id_array.add("");

                            disadapter.notifyDataSetChanged();
                            dpspinner.setAdapter(disadapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(create_report.this, "Error in list of insect.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getimage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(create_report.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {Intent intent = new Intent(create_report.this, CAM2.class);
                        startActivityForResult(intent, 4);}else{
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
//                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        Intent intent = new Intent(create_report.this, CAM2.class);
                        startActivityForResult(intent, 4);
                    }
                }}
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 4 ) {


                String url = data.getStringExtra("url");
                System.out.println(" url is:" + url);
                imn.setImageURI(Uri.parse(url));
                Bitmap bitmap=null;
                try {
                    bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(url));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                image.setVisibility(View.VISIBLE);
//                upload.setVisibility(View.VISIBLE);
//                file_path = url;

                bitmaps.add(Uri.parse(url));
                File compressedImageFile = null;
                try {
                    compressedImageFile = new Compressor(create_report.this).compressToFile(FileUtils.from(create_report.this, Uri.parse(url)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String imb = compressedImageFile.getAbsolutePath();
                String image_proper = converting_string(Uri.fromFile(new File(imb)).toString()).replaceAll("\n", "");
                imagefilesstring.add(image_proper);
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
//                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//
//                    imagefilesstring.add(BitMapToString(decoded));} catch (Exception e) {
//                    e.printStackTrace();
//                }


            }

//                if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
//                {
//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    imn.setImageBitmap(photo);
//                    bitmaps.add(photo);
//                    imagefilesstring.add(BitMapToString(photo));
//
//                }
             else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();

                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                //Log.w("path of image from gallery......******************.........", picturePath+"");
                imn.setImageBitmap(thumbnail);
                bitmaps.add(selectedImage);
                File compressedImageFile = null;
                try {
                    compressedImageFile = new Compressor(create_report.this).compressToFile(FileUtils.from(create_report.this, selectedImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String imb = compressedImageFile.getAbsolutePath();

                String image_proper = converting_string(Uri.fromFile(new File(imb)).toString()).replaceAll("\n", "");
                imagefilesstring.add(image_proper);

            }
            else {
                Toast.makeText(create_report.this, "No image selected", Toast.LENGTH_LONG).show();


            }
        }
    }



    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    
        public boolean checking_permision() {


        if ((ContextCompat.checkSelfPermission(create_report.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(create_report.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(create_report.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return false;

        }


        return true;
    }
    public String converting_string(String file_path) {
        String file_path_string = "";
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

                file_path_string = Base64.encodeToString(data_in_byte_image, Base64.DEFAULT);
                // System.out.println(" image in string :" +file_path_string);


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(create_report.this, "Error in converting ", Toast.LENGTH_LONG).show();
                file_path_string = "";
            }
        } else {

            System.out.println(" inside else ");
        }
        return file_path_string;
    }


    public void getting_Nutrition_list()
    {


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_Neutrient_detail")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        dp_id_array.clear();
                        dp_array.clear();

                        dp_array.add(0, "Select Nutrient");
                        dp_id_array.add(0, "");
                        try {
                            JSONArray array = response.getJSONArray("Neutrient_list");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                String Nutrient_id = object.optString("Neutrient_id");
                                String Nutrient_symbol = object.optString("Neutrient_Symbol");
                                String Nutrient_Name = object.optString("Neutrient_name");

                                //   System.out.println("58.1" + affected_name);

                                dp_array.add((i + 1), Nutrient_symbol);
                                dp_id_array.add((i + 1), Nutrient_id);


                            }dp_array.add("Others");
                            dp_id_array.add("");

                            disadapter.notifyDataSetChanged();
                            dpspinner.setAdapter(disadapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(create_report.this, "Error in getting Nutrient list.", Toast.LENGTH_LONG).show();
                    }
                });


    }
    public void getting_disease_name(String code) {


        JSONObject object = new JSONObject();
        try {
            object.put("crop_id", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_disease")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        dp_array.clear();
                        dp_id_array.clear();

                        dp_array.add(0, "Select Disease");
                        dp_id_array.add(0, "");

                        try {
                            JSONArray array = response.getJSONArray("disease_list");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                String disease_id = object.optString("disease_id");
                                String disease_name = object.optString("disease_name");

                                System.out.println("58.1" + disease_name);

                                dp_array.add((i + 1), disease_name);
                                dp_id_array.add((i + 1), disease_id);


                            }
                            dp_array.add("Others");
                            dp_id_array.add("");

                            disadapter.notifyDataSetChanged();
                            dpspinner.setAdapter(disadapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(create_report.this, "Error in getting disease list.", Toast.LENGTH_LONG).show();
                    }
                });

    }


    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer1);
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

                Intent intent = new Intent(create_report.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.help1:

                Intent intent1 = new Intent(create_report.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.editme:

                Intent ibt=new Intent(create_report.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(create_report.this, contributor.class);
                startActivity(ibt1);

                break;
            case R.id.home:
                Intent ibtwd;
                if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("expert")){
                    ibtwd=new Intent(create_report.this, expertprofile_fragment.class);
                }else{ ibtwd=new Intent(create_report.this, farmersprofile_fragment1.class);}
                startActivity(ibtwd);
                break;
            case R.id.history:

                Intent ibt1q=new Intent(create_report.this, history.class);
                startActivity(ibt1q);

                break;
            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
                        //Log.d("fff", "DocumentSnapshot successfully written!");
                        Toast.makeText(create_report.this, "Signed out", Toast.LENGTH_LONG).show();
                        shared_pref.remove_shared_preference(create_report.this);
                        Intent intent = new Intent(create_report.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("fff", "Error writing document", e);
                        Toast.makeText(create_report.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(create_report.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(context, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(context);
                    Intent intent = new Intent(context, Login.class);
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
//    private void signout() {
//        AlertDialog.Builder opt = new AlertDialog.Builder(create_report.this);
//        opt.setTitle("Are you sure ?");
//        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                shared_pref.remove_shared_preference(create_report.this);
//                Intent intent = new Intent(create_report.this, Login.class);
//                startActivity(intent);
//                finish();
//
//
//
//            }
//        });
//        opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//        opt.show();
//    }
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
                            List<Address> addresses = getGeocoderAddress(create_report.this);
                            String locality="";
                            String countryName="";
                            String sub="";
                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                locality= address.getLocality();
                                sub=address.getSubAdminArea();
                                countryName= address.getCountryName();


                            }
                            lnb=locality+", "+countryName;
                            
                            loct.setText(locality+", "+countryName);
                            if (!Latitude.matches("")){
                                Toast.makeText(create_report.this, "Location is set", Toast.LENGTH_LONG).show();
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
            List<Address> addresses = getGeocoderAddress(create_report.this);
            String locality="";
            String countryName="";
            String sub="";
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                locality= address.getLocality();
                sub=address.getSubAdminArea();
                countryName= address.getCountryName();
            }
            loct.setText(locality+", "+countryName);
            lnb=sub+", "+locality;
            if (!(locality).matches("")){
                user_singleton.getInstance().setloct(locality+", "+countryName);
            }
            if (!Latitude.matches("")){
                Toast.makeText(create_report.this, "Location is set", Toast.LENGTH_LONG).show();
            }

        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
//    @Override
//    public void
//    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            }
//        }
//    }
    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet,intentFilter);

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

       /* Intent intent=new Intent(Result_identify.this, Identify_disease.class);
        startActivity(intent);*/
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