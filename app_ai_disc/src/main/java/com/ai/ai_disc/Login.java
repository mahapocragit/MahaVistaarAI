package com.ai.ai_disc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.ai.ai_disc.Farmer.GPSTracker;
import com.ai.ai_disc.Farmer.Proceed_Activity_Registeration;
import com.ai.ai_disc.Videoconference.Expert_profile;
import com.ai.ai_disc.Videoconference.Farmer_profile;
import com.ai.ai_disc.Videoconference.Login_VideoCalling;
import com.ai.ai_disc.model.appuse_response;
import com.ai.ai_disc.model.appuusemodel;
import com.ai.ai_disc.model.editprofile_model;
import com.ai.ai_disc.model.editprofile_response;
import com.ai.ai_disc.view_model.appused;
import com.ai.ai_disc.view_model.getcropidnames;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.ai.ai_disc.Farmer.Farmer_RegisterationPageActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Login extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "Login";
    public final String user_type_dataentry = "dataentry";
    public final String user_type_validator = "validator";
    public final String user_type_administrator = "administrator";
    public final String user_type_model_developer = "model_developer";
    public final String user_type_diseaseidentifier = "diseaseidentifier";
    public final String user_type_reports = "reports";
    public final String user_type_expert = "expert";
    public final String user_type_blocked = "blocked";
    public final String user_type_maize_reports = "maizereports";
    public final String user_type_farmer="farmer";
    Location location;
     String latt="";
     String longt="";
     int loops=0;
    appused appuse;
    int geocoderMaxResults = 1;
    @BindView(R.id.edittext_username)
    TextInputEditText username;
    @BindView(R.id.edittext_password)
    TextInputEditText password;
    @BindView(R.id.login)
    Button login;
    ProgressDialog progress;
    int MY_REQUEST_CODE = 0;
    InternetReceiver internet;
    @BindView(R.id.image)
    ImageView imageView;


    @BindView(R.id.forgot_password)
    TextView forgot_password;
LocationManager locationManager;
    @BindView(R.id.create_account)
    MaterialButton create_account;
    boolean GpsStatus ;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;


    @BindView(R.id.tv_lockout)
    TextView tv_lockout;
    //Account Lockout Policy, if wrong credentials entered in login;
    int attempt = 1;
    String isLoggedIn = "false";
    //String isValid_loginAttempt = "ranjan_true1";
    String isValid_loginAttempt = "ranjan_false";




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(1);
        getSupportActionBar().hide();

        ImageView imgInfo = findViewById(R.id.img_infoMenu);
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent11 = new Intent(Login.this, contributor.class);
                startActivity(intent11);
            }
        });

        //menu on image click;
        initToolBar();



        ButterKnife.bind(this);
        TextView forgetuser=findViewById(R.id.forgot_user);
        TextView policy=findViewById(R.id.privacy);
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nb=new Intent(Login.this, policy.class);
                startActivity(nb);
            }
        });
        forgetuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Login.this,Forgot_password.class);
                startActivity(intent);
            }
        });
        forgetuser.setVisibility(View.GONE);
        internet = new InternetReceiver();
            username.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length()>=1){
                        forgetuser.setVisibility(View.VISIBLE);

                    }
                    else{
                        forgetuser.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        forgot_password.setVisibility(View.GONE);
        //CheckGpsStatus();
        //Log.d(TAG,latt);
        try{loops=getIntent().getExtras().getInt("loops");} catch (Exception e) {
            e.printStackTrace();
        }

//        longt=getIntent().getExtras().getString("lon");
        //Log.d(TAG,latt);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        try{
        getLastLocation();} catch (Exception e) {
            e.printStackTrace();
        }


        //forgot_password.setVisibility(View.VISIBLE);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>=1){
                    forgot_password.setVisibility(View.VISIBLE);

                }
                else{
                    forgot_password.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(Login.this,Forgot_password.class);
                startActivity(intent);
            }
        });
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, Farmer_RegisterationPageActivity.class);
                startActivity(intent);
            }
        });


        tv_lockout.setVisibility(View.GONE);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*String username_get = username.getText().toString().trim();
                String password_get = password.getText().toString().trim();
                if (validation(username_get, password_get)) {
                    login(username_get, password_get);
                }*/


                tv_lockout.setVisibility(View.GONE);
                String username_get = username.getText().toString().trim();
                String password_get = password.getText().toString().trim();
                if(attempt <= 3){
                    //isValid_loginAttempt = "ranjan_true1";

                    if (validation(username_get, password_get)) {
                        //Matching username & password using Api >> go to respective page!!
                        //login(username_get, password_get);  //old methof without token and security;
                        secure_newLogin(username_get, password_get);
                    }

                    //username.setText("");
                    password.setText("");
                    if(isLoggedIn.equals("false") &&   attempt < 3){
                        tv_lockout.setText("Invalid Credentials, Only "+(3- attempt)+" attempts left.");
                    }else if( isLoggedIn.equals("false") &&   attempt == 3){
                        tv_lockout.setText("Your login Attempts over; Please login after 2min.");
                        isLoggedIn = "true";
                        blockUserAccount(username_get);
                    }



                }else if(attempt >= 4){
                    //isValid_loginAttempt = "ranjan_false";
                    secure_newLogin(username_get, password_get);
                    tv_lockout.setVisibility(View.VISIBLE);
                    Toast.makeText(Login.this,"Your login Attempts over; Please login after 2min.",Toast.LENGTH_SHORT).show();

                }/*else if(attempt > 4){
                    //isValid_loginAttempt = "ranjan_false";
                    //System.exit(0); //restarts the app with one fewer activity on the stack.
                    finish();
                }*/

                // attempt++ ;  //only if wrong credential entered
            }

        });

        bypassLoginForLoggedInUser();

    }

    private void blockUserAccount(String username_get) {
        /*progress = new ProgressDialog(Login.this);
        progress.setCancelable(false);
        progress.setMessage("Multiple Login Attempts...blocking user Account.");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();*/

       /* // you can verify the output of Api in postman with below POST(body> raw> json(format)) method;
       {
            "username": "dataentry1"
        }
        */
        JSONObject object = new JSONObject();
        try {
            object.put("username", username_get);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String token_key = "PMAK-646d993c4ae18d7b534b6aad-afe1811f3b9c9feff73dc2633c1410b885";
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/blockUserAccount")
                .addHeaders("ranjan_api", token_key)
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // progress.cancel();

                        //Log.e(TAG, "onResponse: ==>"+response);
                        /*{
                            "message": "User Account Blocked with last Attempt time.",
                                "result": true
                        }*/
                        try{
                            String message = response.getString("message");
                            //boolean status = response.getBoolean("result");
                            if(message.equals("User Account Blocked with last Attempt time.")){
                                show_dialog("User account Blocked..try after 2 minute!!");
                            }else{
                                show_dialog("User Account Blocking Failed");
                            }
                        }catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        progress.cancel();
                        show_dialog("Error in account blocking");
                        System.out.println(" error :" + error.getMessage());
                    }
                });

    }

    // before login page:Proceed_Activity_Registeration => loop=2 set;
    private void bypassLoginForLoggedInUser() {

        // Before Encryption shared_pref
        /*shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        String user_name_got = shared_pref.sp.getString("username", "");
        String password_got = shared_pref.sp.getString("password", "");
        String my_token = shared_pref.sp.getString("token", "");

        if (!user_name_got.isEmpty() && !password_got.isEmpty()  ){ //&& !latt.isEmpty()
            if (validation(user_name_got, password_got)) {
                Toast.makeText(Login.this,"Welcome back...",Toast.LENGTH_SHORT).show();
                login(user_name_got, password_got);
            }
        }
        else{
            if (loops!=2){
                startActivity(new Intent(Login.this, Proceed_Activity_Registeration.class));
                finish();}
        }*/


        //After Decryption shared_pref
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize/open an instance of EncryptedSharedPreferences on below line.
        try {
            // initializing our encrypted shared preferences and passing our key to it.
            EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    // passing a file name to share a preferences
                    "preferences",
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String user_name_got = sharedPreferences.getString("username", "");
            String password_got = sharedPreferences.getString("password", "");
            if (!user_name_got.isEmpty() && !password_got.isEmpty()) {
                if (validation(user_name_got, password_got)) {
                    Toast.makeText(Login.this,"Welcome back...",Toast.LENGTH_SHORT).show();
                    //login(user_name_got, password_got);
                    secure_newLogin(user_name_got,password_got);
                }
            }
            else{
                if (loops!=2){
                    startActivity(new Intent(Login.this, Proceed_Activity_Registeration.class));
                    finish();}
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Test Menu");

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.info_i);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //implement your click stuff here
                        switch (v.getId())
                        {
                            case R.id.about_us:

                                Intent intent = new Intent(Login.this, About_us1.class);
                                startActivity(intent);
                                break;
                            case R.id.help:

                                Intent intent1 = new Intent(Login.this, needHelp.class);
                                startActivity(intent1);
                                break;
                            case R.id.contr:

                                Intent intent11 = new Intent(Login.this, contributor.class);
                                startActivity(intent11);
                                break;

                            default:
                                break;

                        }
                    }
                }

        );
    }


    public void CheckGpsStatus(){
        locationManager = (LocationManager)Login.this.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus) {

            Toast.makeText(Login.this,"GPS Is Disabled",Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);
            CheckGpsStatus();

        }

    }

    public boolean validation(String username, String password)
    {

        if (username.isEmpty()) {

            Toast.makeText(Login.this, "Username is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()) {

            Toast.makeText(Login.this, "Password is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }


        my_application application = (my_application) getApplication();

        if (application.login_user_name(username.trim(), Login.this)) {

        } else {

            return false;
        }

        if (application.login_password(password.trim(), Login.this)) {

        } else {

            return false;
        }


        return true;
    }

    public void secure_newLogin(final String username, final String password){

        progress = new ProgressDialog(Login.this);
        progress.setCancelable(false);
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

       /* // you can verify the output of Api in postman with below POST(body> raw> json(format)) method;
       {
            "username": "dataentry1",
            "password": "dataentry1@123"
        }
        */

        JSONObject object = new JSONObject();
        try {
            object.put("username", username);
            object.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Log.i(TAG, "object is: " + object);
        // .addHeaders("Content-Type", "application/json")
        String token_key = "PMAK-646d993c4ae18d7b534b6aad-afe1811f3b9c9feff73dc2633c1410b885";
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Upload_Login_new")
                .addHeaders("ranjan_api", token_key)
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progress.cancel();
                        //Log.e(TAG, "onResponse: ==>"+response);
                        //    System.out.println(" response login:" + response);
                        // Log.i(TAG, "onResponse: " + response);

                            /* // response of POST method in postman using above Api url;
                            {
                                "found": true,
                                "name": "",
                                "user_id": "91",
                                "email_id": "test@gmail.com",
                                "mobile_number": "1234567890",
                                "user_type": "Farmer",
                                "fname": "Vaibhav",
                                "lname": "Singh",
                                "user_name": "shashi",
                                "password": "password_123"
                            }
                             */


                        //handle the response in different condition;
                        //For Blocked Account(check and Update)...unblock it after 2 min;
                        //Log.e(TAG, "onResponse: ==>"+response);
                        try {
                            String errorCode = response.getString("user_id");
                            switch (errorCode){
                                case "1":
                                    show_dialog("User Not Found..wrong credentials.");
                                    attempt++ ;  //only if wrong credential entered
                                    tv_lockout.setVisibility(View.VISIBLE);
                                    isLoggedIn = "false";
                                    return;    // no need to look down further;
                                case "2":
                                    show_dialog("User Account unBlocked failed..may be time not completed.");
                                    return;    // no need to look down further;
                                case "3":
                                    if(attempt > 4){
                                        show_dialog("User Not Found..Possible reasons: Account Blocked, wrong credentials, or may be login " +
                                                "Attempt time not completed..try after 2 min.");
                                    }else{
                                        show_dialog("Invalid Credentials !! Please check your Username and Password.");
                                    }
                                    attempt++ ;  //only if wrong credential entered
                                    tv_lockout.setVisibility(View.VISIBLE);
                                    isLoggedIn = "false";
                                    return;    // no need to look down further;
                                case "4":
                                    show_dialog("Something went wrong...");
                                    return;    // no need to look down further;
                                case "5":
                                    show_dialog("Invalid Token..or ranjan_api not found in header");
                                    return;    // no need to look down further;
                                //default: return;    // no need to look down further;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                            boolean status = response.getBoolean("found");
                            String name = response.getString("name");
                            String user_id = response.getString("user_id");
                            String email_id = response.getString("email_id");
                            String mobile_number = response.getString("mobile_number");
                            String user_type = response.getString("user_type");
                            String fname = response.getString("fname");
                            String lname = response.getString("lname");
                            String user_name = response.getString("user_name");
                            String passworded = response.getString("password");

                            if (status) // if user with username & password found successfully!!
                            {
                                if (user_type.toLowerCase().equals(user_type_dataentry)) {

                                    //save_user_name_password(username, password);
                                    // System.out.println(" user id before :"+user_singleton.getInstance().getUser_id());

                                    //isLoggedIn = "true";
                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();

                                } else if (user_type.toLowerCase().equals(user_type_validator)) {

                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();

                                } else if (user_type.toLowerCase().equals(user_type_administrator)) {

                                    save_user_name_password(user_name, passworded);
                                    user_singleton.getInstance().setUser_name(user_name);
                                    user_singleton.getInstance().setUser_id(user_id);
                                    user_singleton.getInstance().setMname(name);
                                    user_singleton.getInstance().setName(fname);
                                    user_singleton.getInstance().setEmail_id(email_id);
                                    user_singleton.getInstance().setMobile_number(mobile_number);
                                    user_singleton.getInstance().setUser_type(user_type);
                                    user_singleton.getInstance().setpassword(passworded);
                                    user_singleton.getInstance().setfname(fname);
                                    user_singleton.getInstance().setlname(lname);

                                    Intent intent3 = new Intent(Login.this, Admin_profile.class);
                                    startActivity(intent3);

                                    isLoggedIn = "true";

                                } else if (user_type.toLowerCase().equals(user_type_model_developer)) {

                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();

                                } else if (user_type.toLowerCase().equals(user_type_diseaseidentifier)) {

                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();

                                } else if (user_type.toLowerCase().equals(user_type_reports)) {

                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();

                                } else if (user_type.toLowerCase().equals(user_type_expert)) {
                                    user_singleton.getInstance().setUser_name(user_name);
                                    user_singleton.getInstance().setUser_id(user_id);
                                    user_singleton.getInstance().setMname(name);
                                    user_singleton.getInstance().setName(fname);
                                    user_singleton.getInstance().setEmail_id(email_id);
                                    user_singleton.getInstance().setMobile_number(mobile_number);
                                    user_singleton.getInstance().setUser_type(user_type);
                                    user_singleton.getInstance().setpassword(passworded);
                                    user_singleton.getInstance().setfname(fname);
                                    user_singleton.getInstance().setlname(lname);

                                    progress = new ProgressDialog(Login.this);
                                    progress.setCancelable(false);
                                    progress.setMessage("Loading... Wait");
                                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progress.show();

                                    db.collection("loginaidisc")
                                            .whereEqualTo("username",user_name)
                                            .whereEqualTo("password",passworded)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        boolean found=false;
                                                        String idd="";
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            // Log.d(TAG, document.getId() + " => " + document.getData());
                                                            String user_type= (String) document.getData().get("usertype");
                                                            found=true;
                                                            idd=String.valueOf(document.getId());
                                                        }
                                                        if(idd.isEmpty()){
                                                            //progress.cancel();
                                                            save_user_name_password(user_name, passworded);
                                                            user_singleton.getInstance().setfb_id("");
                                                            loginwithappuse(Integer.parseInt(user_id),user_type,2);
                                                        }
                                                        else{
                                                            save_user_name_password(user_name, passworded);
                                                            user_singleton.getInstance().setfb_id(idd);
                                                            loginwithappuse(Integer.parseInt(user_id),user_type,2);
                                                        }

                                                        isLoggedIn = "true";

                                                    } else {
                                                        //Log.w(TAG, "Error getting documents.", task.getException());
                                                        progress.cancel();
                                                        //  Toast.makeText(Login.this, "Error.", Toast.LENGTH_SHORT).show();
                                                        show_dialog("Error2222");
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progress.cancel();
                                            isLoggedIn = "false";
                                        }
                                    });

                                } else if (user_type.toLowerCase().equals(user_type_maize_reports)) {

                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();
                                }
                                else if(user_type.toLowerCase().equals(user_type_farmer))
                                {
                                    user_singleton.getInstance().setUser_name(user_name);
                                    user_singleton.getInstance().setUser_id(user_id);
                                    user_singleton.getInstance().setMname(name);
                                    user_singleton.getInstance().setName(fname);
                                    user_singleton.getInstance().setEmail_id(email_id);
                                    user_singleton.getInstance().setMobile_number(mobile_number);
                                    user_singleton.getInstance().setUser_type(user_type);
                                    user_singleton.getInstance().setpassword(passworded);
                                    user_singleton.getInstance().setfname(fname);
                                    user_singleton.getInstance().setlname(lname);

                                    progress = new ProgressDialog(Login.this);
                                    progress.setCancelable(false);
                                    progress.setMessage("Loading... Wait");
                                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progress.show();

                                    db.collection("loginaidisc")
                                            .whereEqualTo("username",user_name)
                                            .whereEqualTo("password",passworded)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        boolean found=false;
                                                        String idd="";
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            //  Log.d(TAG, document.getId() + " => " + document.getData());
                                                            String user_type= (String) document.getData().get("usertype");
                                                            found=true;
                                                            idd=String.valueOf(document.getId());
                                                        }
                                                        if(idd.isEmpty()){
                                                            //progress.cancel();
                                                            save_user_name_password(user_name, passworded);
                                                            user_singleton.getInstance().setfb_id("");
                                                            loginwithappuse(Integer.parseInt(user_id),user_type,1);
                                                        }
                                                        else{
                                                            save_user_name_password(user_name, passworded);
                                                            user_singleton.getInstance().setfb_id(idd);
                                                            loginwithappuse(Integer.parseInt(user_id),user_type,1);
                                                        }
                                                        isLoggedIn = "true";

                                                    } else {
                                                        // Log.w(TAG, "Error getting documents.", task.getException());
                                                        progress.cancel();
                                                        //  Toast.makeText(Login.this, "Error.", Toast.LENGTH_SHORT).show();
                                                        show_dialog("Error2222");
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progress.cancel();
                                            isLoggedIn = "false";
                                        }
                                    });

                                }else if(user_type.toLowerCase().equals(user_type_blocked)){
                                    Toast.makeText(Login.this, "Account is blocked", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Login.this, "Can not access here.", Toast.LENGTH_SHORT).show();
                                    isLoggedIn = "false";
                                }

                            }else {
                                //show_dialog("Invalid Credentials !! Please check your Username and Password.");
                                Toast.makeText(Login.this, "Invalid Credentials !! Please check your Username and Password.", Toast.LENGTH_LONG).show();
                                attempt++ ;  //only if wrong credential entered
                                tv_lockout.setVisibility(View.VISIBLE);
                                isLoggedIn = "false";
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            isLoggedIn = "false";
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        progress.cancel();
                        show_dialog("Error in login");
                        //System.out.println(" error :" + error.getMessage());
                        isLoggedIn = "false";
                    }
                });
    }


    public void login(final String username, final String password) {

        /*
           {
              "username": "sample string 1",
              "password": "sample string 2"
            }
         */

        progress = new ProgressDialog(Login.this);
        progress.setCancelable(false);
        progress.setMessage("Loading...Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        JSONObject object = new JSONObject();
        try {
            object.put("username", username);
            object.put("password", password);


        } catch (JSONException e) {
            e.printStackTrace();
        }

       // Log.i(TAG, "object is: " + object);
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Upload_Login")
                .addJSONObjectBody(object)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progress.cancel();

                        //    System.out.println(" response login:" + response);
                         // Log.i(TAG, "onResponse: " + response);

                            /*
                                {
                                  "found": true,
                                  "name": "sample string 2",
                                  "user_id": "sample string 3",
                                  "email_id": "sample string 4",
                                  "mobile_number": "sample string 5"
                                }
                             */

                        try {
                            boolean status = response.getBoolean("found");
                            String name = response.getString("name");
                            String user_id = response.getString("user_id");
                            String email_id = response.getString("email_id");
                            String mobile_number = response.getString("mobile_number");
                            String user_type = response.getString("user_type");
                            String fname = response.getString("fname");
                            String lname = response.getString("lname");
                            String user_name = response.getString("user_name");
                            String passworded = response.getString("password");

                            if (status) {
                                if (user_type.toLowerCase().equals(user_type_dataentry)) {
                                    //save_user_name_password(username, password);
                                    // System.out.println(" user id after :"+user_singleton.getInstance().getUser_id());
                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();

                                } else if (user_type.toLowerCase().equals(user_type_validator)) {
                                    //save_user_name_password(username, password);
      //                              Intent intent2 = new Intent(Login.this, Validator_profile.class);
//                                    startActivity(intent2);
                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();

                                } else if (user_type.toLowerCase().equals(user_type_administrator)) {

                                    save_user_name_password(user_name, passworded);
                                    user_singleton.getInstance().setUser_name(user_name);
                                    user_singleton.getInstance().setUser_id(user_id);
                                    user_singleton.getInstance().setMname(name);
                                    user_singleton.getInstance().setName(fname);
                                    user_singleton.getInstance().setEmail_id(email_id);
                                    user_singleton.getInstance().setMobile_number(mobile_number);
                                    user_singleton.getInstance().setUser_type(user_type);
                                    user_singleton.getInstance().setpassword(passworded);
                                    user_singleton.getInstance().setfname(fname);
                                    user_singleton.getInstance().setlname(lname);

                                    Intent intent3 = new Intent(Login.this, Admin_profile.class);
                                    startActivity(intent3);
                                    //Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();

                                } else if (user_type.toLowerCase().equals(user_type_model_developer)) {

                                    //save_user_name_password(username, password);

                                    //user.user_id = user_id;
//                                    Intent intent3 = new Intent(Login.this, Model_developer_profile.class);
//                                    startActivity(intent3);
                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();


                                } else if (user_type.toLowerCase().equals(user_type_diseaseidentifier))
                                {

                                    //save_user_name_password(username, password);
//                                    Intent intent4 = new Intent(Login.this, Identification_profile.class);
//                                    startActivity(intent4);
                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();


                                } else if (user_type.toLowerCase().equals(user_type_reports))
                                {
//                                    Intent intent4 = new Intent(Login.this, Reporting_profile.class);
//                                    startActivity(intent4);
                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();

                                }
                                else if (user_type.toLowerCase().equals(user_type_expert)) {

                                    //save_user_name_password(user_name, passworded);
                                    user_singleton.getInstance().setUser_name(user_name);
                                    user_singleton.getInstance().setUser_id(user_id);
                                    user_singleton.getInstance().setMname(name);
                                    user_singleton.getInstance().setName(fname);
                                    user_singleton.getInstance().setEmail_id(email_id);
                                    user_singleton.getInstance().setMobile_number(mobile_number);
                                    user_singleton.getInstance().setUser_type(user_type);
                                    user_singleton.getInstance().setpassword(passworded);
                                    user_singleton.getInstance().setfname(fname);
                                    user_singleton.getInstance().setlname(lname);
                                    //user_singleton.getInstance().setfb_id(fb_id);

                                    //progress.show();
                                   // Log.d(TAG,"expert");
                                    //do_login_firebase(user_name,passworded);

                                    //loginwithappuse(Integer.parseInt(user_id),user_type,2);
                                    progress = new ProgressDialog(Login.this);
                                    progress.setCancelable(false);
                                    progress.setMessage("Loading... Wait");
                                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progress.show();

                                    db.collection("loginaidisc")
                                            .whereEqualTo("username",user_name)
                                            .whereEqualTo("password",passworded)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        boolean found=false;
                                                        String idd="";
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                           // Log.d(TAG, document.getId() + " => " + document.getData());
                                                            String user_type= (String) document.getData().get("usertype");
                                                            found=true;
                                                            //progress.cancel();
                                                            idd=String.valueOf(document.getId());
                                                            //login(username,password,String.valueOf(document.getId()));
                                                            //progress.show();

                                                        }
                                                        if(idd.isEmpty()){
                                                            //progress.cancel();
                                                            save_user_name_password(user_name, passworded);
                                                            user_singleton.getInstance().setfb_id("");
                                                            loginwithappuse(Integer.parseInt(user_id),user_type,2);

                                                            //login(username,password,"");

                                                        }
                                                        else{
                                                            save_user_name_password(user_name, passworded);
                                                            user_singleton.getInstance().setfb_id(idd);
                                                            loginwithappuse(Integer.parseInt(user_id),user_type,2);

                                                        }

                                                    } else {
                                                        //Log.w(TAG, "Error getting documents.", task.getException());
                                                        progress.cancel();
                                                        //  Toast.makeText(Login.this, "Error.", Toast.LENGTH_SHORT).show();
                                                        show_dialog("Error2222");

                                                    }
                                                }



                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progress.cancel();
                                        }
                                    });



                                    //Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();

                                } else if (user_type.toLowerCase().equals(user_type_maize_reports)) {



//                                    Intent intent4 = new Intent(Login.this, Maize_reports.class);
//                                    startActivity(intent4);
                                    Toast.makeText(Login.this, "Error !!! Farmer account only...Try NIBPP app", Toast.LENGTH_SHORT).show();


                                }
                                else if(user_type.toLowerCase().equals(user_type_farmer))
                                {


                                    user_singleton.getInstance().setUser_name(user_name);
                                    user_singleton.getInstance().setUser_id(user_id);
                                    user_singleton.getInstance().setMname(name);
                                    user_singleton.getInstance().setName(fname);
                                    user_singleton.getInstance().setEmail_id(email_id);
                                    user_singleton.getInstance().setMobile_number(mobile_number);
                                    user_singleton.getInstance().setUser_type(user_type);
                                    user_singleton.getInstance().setpassword(passworded);
                                    user_singleton.getInstance().setfname(fname);
                                    user_singleton.getInstance().setlname(lname);
                                    //user_singleton.getInstance().setfb_id(fb_id);

                                  //  Log.d(TAG,"farmers");

                                    progress = new ProgressDialog(Login.this);
                                    progress.setCancelable(false);
                                    progress.setMessage("Loading... Wait");
                                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progress.show();

                                    db.collection("loginaidisc")
                                            .whereEqualTo("username",user_name)
                                            .whereEqualTo("password",passworded)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        boolean found=false;
                                                        String idd="";
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                          //  Log.d(TAG, document.getId() + " => " + document.getData());
                                                            String user_type= (String) document.getData().get("usertype");
                                                            found=true;
                                                            //progress.cancel();
                                                            idd=String.valueOf(document.getId());
                                                            //login(username,password,String.valueOf(document.getId()));
                                                            //progress.show();

                                                        }
                                                        if(idd.isEmpty()){
                                                            //progress.cancel();
                                                            save_user_name_password(user_name, passworded);
                                                            user_singleton.getInstance().setfb_id("");
                                                            loginwithappuse(Integer.parseInt(user_id),user_type,1);

                                                            //login(username,password,"");

                                                        }
                                                        else{
                                                            save_user_name_password(user_name, passworded);
                                                            user_singleton.getInstance().setfb_id(idd);
                                                            loginwithappuse(Integer.parseInt(user_id),user_type,1);

                                                        }

                                                    } else {
                                                       // Log.w(TAG, "Error getting documents.", task.getException());
                                                        progress.cancel();
                                                        //  Toast.makeText(Login.this, "Error.", Toast.LENGTH_SHORT).show();
                                                        show_dialog("Error2222");

                                                    }
                                                }



                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progress.cancel();
                                        }
                                    });

                                }
                                else if(user_type.toLowerCase().equals(user_type_blocked)){
                                    Toast.makeText(Login.this, "Account is blocked", Toast.LENGTH_SHORT).show();
                                }

                                else {

                                    Toast.makeText(Login.this, "Can not access here.", Toast.LENGTH_SHORT).show();

                                }


                            } else {
                                //Toast.makeText(Login.this, "username  and password is not correct.", Toast.LENGTH_LONG).show();
                                //progress.cancel();
                                show_dialog("Username and Password is incorrect");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progress.cancel();
                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        progress.cancel();

                        show_dialog("Error in login");
                        System.out.println(" error :" + error.getMessage());
                    }
                });
    }

    public void save_user_name_password(String username, String password) {

        //before encrypted shared pref.
        /*shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_pref.sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        //editor.putString("token", token);
        editor.commit();*/


        //store data in shared preferences in encrpted form;
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    // passing a file name to share a preferences
                    "preferences",
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            // on below line we are storing data in shared preferences file.
            sharedPreferences.edit().putString("username", username).apply();
            sharedPreferences.edit().putString("password", password).apply();

            //get data from encrpypted shared pref;
            String user_name_got = sharedPreferences.getString("username", "");
            String password_got = sharedPreferences.getString("password", "");

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void show_dialog(String message) {
        //progress
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
        alertDialogBuilder.setTitle("Response");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //progress.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                // log("Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
                Toast.makeText(Login.this, "Update flow failed.", Toast.LENGTH_SHORT).show();
            }

            if (resultCode == RESULT_OK) {
                Toast.makeText(Login.this, "Update flow success.", Toast.LENGTH_SHORT).show();

            }
        }
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
        alertDialogBuilder.setTitle("Do you want to Exit?")

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

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);
    }


//    @Override
//    public void onStop() {
//        super.onStop();
//        unregisterReceiver(internet);
//
//    }

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
                Intent intent = new Intent(Login.this, About_us1.class);
                startActivity(intent);
                break;
            case R.id.help:
                Intent intent1 = new Intent(Login.this, needHelp.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
        return true;
    }

    private void loginwithappuse(int userid,String usertype,int typed){
        appuse= ViewModelProviders.of(Login.this).get(appused.class);
        appuse.getting_crops(userid,latt,longt,usertype).observe(Login.this, new Observer<appuse_response>() {
            @Override
            public void onChanged(appuse_response appuse_response) {
                progress.cancel();
                //Log.d(TAG,appuse_response.toString());
                if (appuse_response.getModel()!=null){
                    appuusemodel model=appuse_response.getModel();
                    boolean result=model.getcropid();
                    if (result){
                        if (typed==1) {
                            String locality="-";
                            String countryName="";
                            try{
                            List<Address> addresses = getGeocoderAddress(Login.this);

                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                locality= address.getLocality();

                                countryName= address.getCountryName();


                            }} catch (Exception e) {
                                e.printStackTrace();
                            }
                            user_singleton.getInstance().setloct(locality+", "+countryName);
                            //do_login_firebase( username,  password,typed);
                            Intent intent4 = new Intent(Login.this, farmersprofile_fragment1.class);
                            startActivity(intent4);
                        }else {
                            String locality="-";
                            String countryName="";
                            try{
                                List<Address> addresses = getGeocoderAddress(Login.this);

                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    locality= address.getLocality();

                                    countryName= address.getCountryName();


                                }} catch (Exception e) {
                                e.printStackTrace();
                            }
                            user_singleton.getInstance().setloct(locality+", "+countryName);
                            progress.show();
                            setexpert(String.valueOf(userid));
                            //Intent intent4 = new Intent(Login.this, Expert_profile.class);

                        }
                    }
                    else{
                        Toast.makeText(Login.this, "Can not access here.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }
    public void do_login_firebase(String username, String password){

        progress = new ProgressDialog(Login.this);
        progress.setCancelable(false);
        progress.setMessage("Loading... Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        db.collection("loginaidisc")
                .whereEqualTo("username",username)
                .whereEqualTo("password",password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean found=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                String user_type= (String) document.getData().get("usertype");
                                found=true;
                                progress.cancel();
                                //login(username,password,String.valueOf(document.getId()));
//                                if(user_type.equals("1")){
//                                    save_user_name_password(username,password);
//                                    user_singleton.getInstance().setfb_id(document.getId().toString());
//                                    Intent intent = new Intent(Login.this, farmersprofile_fragment1.class);

//                                    intent.putExtra("mail",email);
//                                    intent.putExtra("id",document.getId());
                                    //startActivity(intent);
//                                }else if(user_type.equals("2")){
//                                    save_user_name_password(username,password);
//                                    user_singleton.getInstance().setfb_id(document.getId().toString());
//                                    Intent intent = new Intent(Login.this, expertprofile_fragment.class);
//                                    intent.putExtra("mail",email);
//                                    intent.putExtra("id",document.getId());
                                    //startActivity(intent);
                                //}

                            }
                            if(!found){
                                progress.cancel();
                                //login(username,password,"");

//                                if (typed==1) {
//                                    Intent intent = new Intent(Login.this, farmersprofile_fragment1.class);
//
////                                    intent.putExtra("mail",email);
////                                    intent.putExtra("id",document.getId());
//                                    startActivity(intent);
//                                }
//                                else{
//                                    Intent intent = new Intent(Login.this, expertprofile_fragment.class);
////                                    intent.putExtra("mail",email);
////                                    intent.putExtra("id",document.getId());
//                                    startActivity(intent);
//                                }
                                // Toast.makeText(Login.this, " Not Correct.", Toast.LENGTH_SHORT).show();
                                //show_dialog("Username and password is not correct.");
                            }

                        } else {
                           // Log.w(TAG, "Error getting documents.", task.getException());
                            progress.cancel();
                            //  Toast.makeText(Login.this, "Error.", Toast.LENGTH_SHORT).show();
                            show_dialog("Error2222");

                        }
                    }



                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.cancel();
            }
        });
    }

        private void setexpert(String userid) {
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", userid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

       // Log.i(TAG, "object is: " + object);


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getexpert")
                .addJSONObjectBody(object)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        progress.cancel();
                       // Log.i(TAG, "ffffffff: " + jsonObject.toString());
                        expert_singleton.getInstance().setdesignation(jsonObject.optString("designation"));
                        expert_singleton.getInstance().setuser_id(userid);
                        expert_singleton.getInstance().setcrop(jsonObject.optString("crop"));
                        expert_singleton.getInstance().setdesignation(jsonObject.optString("fullname"));
                        //do_login_firebase(username,password,2);
                        Intent intent4 = new Intent(Login.this, expertprofile_fragment.class);
                        startActivity(intent4);
                    }

                    @Override
                    public void onError(ANError anError) {
                    progress.cancel();
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
                            //latitudeTextView.setText(String.format("%.2f",location.getLatitude())+"/"+String.format("%.2f",location.getLongitude()));
                            latt=String.valueOf(location.getLatitude());
                            longt=String.valueOf(location.getLongitude());
//                            Intent inh=new Intent(Login.this, Login.class);
//                            inh.putExtra("loops",1);
//                            startActivity(inh);
//                            finish();
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
    private List<Address> getGeocoderAddress(Context context) {

        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

        try {
            /**
             * Geocoder.getFromLocation - Returns an array of Addresses
             * that are known to describe the area immediately surrounding the given latitude and longitude.
             */
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latt), Double.parseDouble(longt), this.geocoderMaxResults);

            return addresses;
        } catch (IOException e) {
            //e.printStackTrace();
            //Log.e(TAG, "Impossible to connect to Geocoder", e);
        }


        return null;
    }
}

