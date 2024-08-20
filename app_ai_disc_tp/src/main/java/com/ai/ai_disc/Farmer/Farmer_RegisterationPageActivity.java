package com.ai.ai_disc.Farmer;

import static com.ai.ai_disc.R.drawable.*;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.BuildConfig;
import com.ai.ai_disc.CAM2;
import com.ai.ai_disc.FileUtils;
import com.ai.ai_disc.InternetReceiver;
import com.ai.ai_disc.Login;
import com.ai.ai_disc.R;
import com.ai.ai_disc.Uploadingworker_profile;
import com.ai.ai_disc.create_report;
import com.ai.ai_disc.editprofile;
import com.ai.ai_disc.expert_appointment;
import com.ai.ai_disc.my_application;
import com.ai.ai_disc.reportfarmerdisease;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;

public class
Farmer_RegisterationPageActivity extends AppCompatActivity {
    Spinner state, district;
    private FirebaseAuth mAuth;;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayAdapter<String> adapter_state,code_names_adapter;
    ArrayAdapter<String> adapter_district;
    ArrayList<String> state_name_list;
    ArrayList<String> state_code_list;
    ArrayList<String> district_code_list;
    ArrayList<String> district_name_list;
    ArrayList<String> usernamelist;
    EditText first_name, middleName,lastName, address, pinCode, emailId,userName, password, confirm_password;
    String selected_state;
    String code_selected_state;
    Location location;
    String latt="";
    File photoFile ;
    String longt="";
    ProgressDialog progress;
    String cod="";
    String selected_district;
    String selected_district_code = "";
    int count_MobileNo;
    Button submit;
    Uri marray=null;
    private static final int CAMERA_REQUEST = 1888;
    int verification;
    private String verificationId;
    String otps="";
    ArrayList<String> c_names,c_code,c_iso;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    String firstname_entered, midNameString,lastNameString,emailIdString;
    String address_entered;
    String pinCode_enterd;
    String contactNumber_entered="";
    String userName_entered;
    String password_entered;
    String confirmPassword_entered;
    String state_entered;
    String district_entered;
    TextView txtpas,txtusr;
    TextView contxtpas;
    ImageView expertimage;
    LinearLayout upper,next,next1upper;
    CardView basiccad,addcard,photcard,logincard;
    TextInputLayout cont,otp;
    Button getotp,resend,verify;
    TextView codeedit;
    Spinner codespinner;
    String CountryID="";
    String mcurrent="";
    Button namenext,addnext,addback,photonext,photoback,loginback;
    InternetReceiver internetReceiver;
    TextInputEditText otptext,contactNumber;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registeration_page);
        Toolbar toolbar = findViewById(R.id.toolbarw);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create Account");
        mAuth = FirebaseAuth.getInstance();
        //conty();
        verificationId="";
        internetReceiver=new InternetReceiver();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        verification=0;
        c_code=new ArrayList<>();c_names=new ArrayList<>();c_iso=new ArrayList<>();
        usernamelist=new ArrayList<>();
        upper=findViewById(R.id.upperlay);
        next1upper=findViewById(R.id.next1);
        next=findViewById(R.id.nextlayout);
        cont=findViewById(R.id.cont);
        otp=findViewById(R.id.otp);
        otptext=findViewById(R.id.otptext);
        getotp=findViewById(R.id.getotp);
        codespinner=findViewById(R.id.codespineer);
        codeedit=findViewById(R.id.codevalue);
        resend=findViewById(R.id.resend);
        verify=findViewById(R.id.Verify);
        next.setVisibility(View.GONE);
        next1upper.setVisibility(View.GONE);
        otp.setVisibility(View.GONE);
        basiccad=findViewById(R.id.basiccard);
        addcard=findViewById(R.id.addresscard);
        photcard=findViewById(R.id.photocard);
        logincard=findViewById(R.id.logincard);

        loginback=findViewById(R.id.loginback);
        addback=findViewById(R.id.addressback);
        addnext=findViewById(R.id.addressnext);
        photoback=findViewById(R.id.photoback);
        photonext=findViewById(R.id.photonext);
        namenext=findViewById(R.id.namenext);

        contactNumber= (TextInputEditText) findViewById(R.id.contact_number);

        TelephonyManager manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getNetworkCountryIso().toUpperCase();


        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                if (contactNumber.getText().toString().isEmpty() || contactNumber.getText().toString().length()!=10) {
                    // when mobile number text field is empty
                    // displaying a toast message.
                    Toast.makeText(Farmer_RegisterationPageActivity.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    // if the text field is not empty we are calling our
                    // send OTP method for getting OTP from Firebase.
                    String number = codeedit.getText().toString() + contactNumber.getText().toString();
                    progress = new ProgressDialog(Farmer_RegisterationPageActivity.this);

                    progress.setCancelable(false);
                    progress.setMessage("Sending...Wait");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.show();
                    //sendVerificationCode(phone);
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(number)            // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(Farmer_RegisterationPageActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            otps=phoneAuthCredential.getSmsCode();
                                            //Log.d("hhhhreg1", sv);
                                            if(!otps.matches("")){
                                            otptext.setText(otps);

                                            otptext.setEnabled(false);}
                                            progress.cancel();
                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                            super.onCodeSent(s, forceResendingToken);
                                            verificationId=s;
                                            contactNumber_entered=contactNumber.getText().toString();

                                            //Log.d("hhhhreg", sv);
                                            getotp.setVisibility(View.GONE);
                                            next1upper.setVisibility(View.VISIBLE);
                                            otp.setVisibility(View.VISIBLE);
                                            progress.cancel();
                                            Toast.makeText(Farmer_RegisterationPageActivity.this, "OTP is sent to your mobile number", Toast.LENGTH_SHORT).show();
                                            //cont.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            progress.cancel();
                                        }
                                    })         // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                otps=otptext.getText().toString();
                if (!otps.matches("")){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otps);
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // if the code is correct and the task is successful
                                        // we are sending our user to new activity.
                                        upper.setVisibility(View.GONE);
                                        next.setVisibility(View.VISIBLE);
                                        basiccad.setVisibility(View.VISIBLE);
                                        Toast.makeText(Farmer_RegisterationPageActivity.this,"Phone number is verified successfully",Toast.LENGTH_LONG).show();
                                    } else {
                                        // if the code is not correct then we are
                                        // displaying an error message to the user.
                                        Toast.makeText(Farmer_RegisterationPageActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(Farmer_RegisterationPageActivity.this,"Please enter otp",Toast.LENGTH_SHORT).show();
                }} catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (contactNumber.getText().toString().isEmpty()) {
                        // when mobile number text field is empty
                        // displaying a toast message.
                        Toast.makeText(Farmer_RegisterationPageActivity.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                    } else {
                        // if the text field is not empty we are calling our
                        // send OTP method for getting OTP from Firebase.
                        String number = codeedit.getText().toString() + contactNumber.getText().toString();
                        progress = new ProgressDialog(Farmer_RegisterationPageActivity.this);

                        progress.setCancelable(false);
                        progress.setMessage("Sending...Wait");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();
                        //sendVerificationCode(phone);
                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber(number)            // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(Farmer_RegisterationPageActivity.this)                 // Activity (for callback binding)
                                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                            @Override
                                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                                otps = phoneAuthCredential.getSmsCode();
                                                //Log.d("hhhhreg1", sv);
                                                getotp.setText(otps);

                                                getotp.setEnabled(false);
                                                progress.cancel();
                                            }

                                            @Override
                                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                super.onCodeSent(s, forceResendingToken);
                                                verificationId = s;
                                                contactNumber_entered = contactNumber.getText().toString();

                                                String sv = forceResendingToken.toString();
                                                Log.d("hhhhreg", sv);
                                                getotp.setVisibility(View.GONE);
                                                next1upper.setVisibility(View.VISIBLE);
                                                otp.setVisibility(View.VISIBLE);
                                                progress.cancel();
                                                Toast.makeText(Farmer_RegisterationPageActivity.this, "OTP is sent to your mobile number", Toast.LENGTH_SHORT).show();
                                                //cont.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                                progress.cancel();

                                            }
                                        })         // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        state = (Spinner) findViewById(R.id.spinner3);
        district = (Spinner) findViewById(R.id.spinner4);

        first_name = (EditText) findViewById(R.id.first_name);
        middleName= (EditText) findViewById(R.id.middle_name);
        lastName = (EditText) findViewById(R.id.last_name);
        address= (EditText) findViewById(R.id.address);
        pinCode = (EditText) findViewById(R.id.pincode);

        emailId = (EditText) findViewById(R.id.email_id);
         expertimage=(ImageView) findViewById(R.id.addprofileimage);
         activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
             @Override
             public void onActivityResult(ActivityResult result) {
                 try {
                     if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                         marray = result.getData().getData();
//                     try {
//                         bitmapd= MediaStore.Images.Media.getBitmap(getContentResolver(),finuri);
//                         img.setImageBitmap(bitmapd);
//                     } catch (IOException e) {
//                         e.printStackTrace();
//                     }
                     } else {
                         marray = Uri.fromFile(new File(mcurrent));
                         Bitmap bitmap = null;
                         try {
                             bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), marray);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         expertimage.setImageBitmap(bitmap);
                         //marray=Uri.parse(url);
//                     WorkManager mWorkManager = WorkManager.getInstance();
//                     String mArray_file="";
//                     try{
//                         File compressedImageFile= new Compressor(Farmer_RegisterationPageActivity.this).compressToFile(FileUtils.from(Farmer_RegisterationPageActivity.this, marray));
//                         mArray_file= compressedImageFile.getAbsolutePath();
//                     } catch (IOException e) {
//                         e.printStackTrace();
//                     }
                         //Log.d("cammera",Uri.fromFile(new File(mArray_file)).toString());

                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         });
        expertimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checking_permision()) {
                        AlertDialog.Builder opt = new AlertDialog.Builder(Farmer_RegisterationPageActivity.this);
                        CharSequence[] option = new CharSequence[]{"Take a picture", "Choose from Gallery", "Cancel"};
                        opt.setTitle("Set profile picture");
                        opt.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {
//                                Intent intent = new Intent(Farmer_RegisterationPageActivity.this, CAM2.class);
//                                startActivityForResult(intent, 111);
                                        Intent intentd = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        try {
                                            photoFile = createImageFile();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                            // Error occurred while creating the File
                                        }
                                        Uri fileProvider = null;
                                        if (photoFile != null) {
                                            fileProvider = FileProvider.getUriForFile(Farmer_RegisterationPageActivity.this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                                            intentd.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                                        }
                                        if (intentd.resolveActivity(getPackageManager()) != null) {
                                            activityResultLauncher.launch(intentd);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "not possible", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Dexter.withActivity(Farmer_RegisterationPageActivity.this)
                                                .withPermissions(
                                                        Manifest.permission.CAMERA,
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                                .withListener(new MultiplePermissionsListener() {
                                                    @Override
                                                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                                                        // check if all permissions are granted
                                                        if (report.areAllPermissionsGranted()) {
                                                            Intent intentd = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                            try {
                                                                photoFile = createImageFile();
                                                            } catch (IOException ex) {
                                                                ex.printStackTrace();
                                                                // Error occurred while creating the File
                                                            }
                                                            Uri fileProvider = null;
                                                            if (photoFile != null) {
                                                                fileProvider = FileProvider.getUriForFile(Farmer_RegisterationPageActivity.this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                                                                intentd.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                                                            }
                                                            if (intentd.resolveActivity(getPackageManager()) != null) {
                                                                activityResultLauncher.launch(intentd);
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "not possible", Toast.LENGTH_SHORT).show();
                                                            }
//                                                    Intent intent = new Intent(Farmer_RegisterationPageActivity.this, CAM2.class);
//                                                    startActivityForResult(intent, 111);

                                                        } else {
                                                            Toast.makeText(Farmer_RegisterationPageActivity.this, "Permission is required.", Toast.LENGTH_LONG).show();
                                                        }


                                                        // check for permanent denial of any permission
                                                        //   if (report.isAnyPermissionPermanentlyDenied()) {
                                                        // permission is denied permenantly, navigate user to app settings
                                                        //  }
                                                    }

                                                    @Override
                                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                                        token.continuePermissionRequest();
                                                    }
                                                })
                                                .check();
                                    }
                                }
                                if (which == 1) {
                                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {
                                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(intent, 112);
                                    } else {
                                        Dexter.withActivity(Farmer_RegisterationPageActivity.this)
                                                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                                .withListener(new MultiplePermissionsListener() {
                                                    @Override
                                                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                                                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                        startActivityForResult(intent, 112);
                                                    }

                                                    @Override
                                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                                    }
                                                }).check();
                                        // permission is granted, open the camera

//                                            Log.i("TAG", "onPermissionGranted: ");
//                                            Intent intent = new Intent();
//                                            intent.setType("image/*");
//                                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                                            startActivityForResult(Intent.createChooser(intent, "Select Image"), 112);
//                                            //    Toast.makeText(Upload_activity.this, "Permission Granted", Toast.LENGTH_LONG).show();
//                                        }
//
//                                        @Override
//                                        public void onPermissionDenied(PermissionDeniedResponse response) {
//                                            // check for permanent denial of permission
//                                            Log.i("TAG", "onPermissionDenied 1: ");
//                                            // if (response.isPermanentlyDenied()) {
//                                            // navigate user to app settings
//                                            Log.i("TAG", "onPermissionDenied: ");
//                                            Toast.makeText(Farmer_RegisterationPageActivity.this, "Permission is required.", Toast.LENGTH_LONG).show();
//                                            // }
//                                        }
//
//                                        @Override
//                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                                            token.continuePermissionRequest();
//                                        }
//                                    }).check();


                                    }
                                }
                                if (which == 2) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        opt.show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        emailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if( s.toString().contains("@")){
                   emailId.setTextColor(Color.BLACK);
               }
               else{
                   emailId.setTextColor(Color.RED);
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userName= (EditText) findViewById(R.id.username);
        txtusr =  findViewById(R.id.usernamehint);

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() <8)
                {
                    txtusr.setVisibility(View.VISIBLE);
                    txtusr.setTextColor(getColor(R.color.darkblue));
                    txtusr.setText("(Create a unique USERNAME of at least 8 characters)");
                    userName.setTextColor(Color.RED);
                    //confirm_password.setEnabled(false);
                }
                else{if(usernamelist.contains(charSequence.toString().trim())){
                    txtusr.setText("(USERNAME is already taken, create another)");
                    txtusr.setVisibility(View.VISIBLE);
                    txtusr.setTextColor(Color.RED);
                    userName.setTextColor(Color.RED);}
                else {
                    my_application application = (my_application) getApplication();
                    txtusr.setTextColor(getColor(R.color.darkblue));
                    String status=application.check_signup_username(charSequence.toString().trim(), Farmer_RegisterationPageActivity.this) ;
                    txtusr.setVisibility(View.VISIBLE);
                        txtusr.setText(status);
                        userName.setTextColor(Color.BLACK);
                    }
                    //confirm_password.setEnabled(true);

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        txtpas = findViewById(R.id.passhint);

        contxtpas =  findViewById(R.id.confhint);
        confirm_password.setVisibility(View.GONE);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() <8)
                {
                    txtpas.setText("Create an alphanumeric password with at least 8 Characters");
                    confirm_password.setEnabled(false);
                    contxtpas.setVisibility(View.GONE);
                    txtpas.setVisibility(View.VISIBLE);
                    txtpas.setTextColor(getColor(R.color.darkblue));
                }
                else{
                    my_application application = (my_application) getApplication();
                    txtpas.setVisibility(View.VISIBLE);
                    String status=application.check_signup_password(charSequence.toString().trim(), Farmer_RegisterationPageActivity.this) ;
                    txtpas.setText(status);
                    if
                    (status.matches("Perfect!")){
                        txtpas.setTextColor(getColor(R.color.darkblue));
                    confirm_password.setEnabled(true);}
                    else{
                        confirm_password.setEnabled(false);
                        contxtpas.setVisibility(View.GONE);
                        txtpas.setTextColor(getColor(R.color.red));
                    }


                }


                if (charSequence.length() >0){
                    confirm_password.setVisibility(View.VISIBLE);

                }else{confirm_password.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        confirm_password.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String psd=password.getText().toString();
                if (psd.length()>0 && psd.matches(charSequence.toString())){
                    contxtpas.setText("Matched !");
                    contxtpas.setVisibility(View.VISIBLE);
                    confirm_password.setTextColor(Color.BLACK);
                }else{
                    contxtpas.setText("");
                    contxtpas.setVisibility(View.GONE);
                    confirm_password.setTextColor(Color.RED);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password.setLongClickable(false);
        confirm_password.setLongClickable(false);

        state_name_list = new ArrayList<String>();
        state_code_list = new ArrayList<String>();
        district_name_list = new ArrayList<String>();
        district_code_list = new ArrayList<String>();
        state_name_list.clear();
        state_code_list.clear();
        district_name_list.clear();
        district_code_list.clear();
        submit=(Button) findViewById(R.id.submit);

        state_name_list.add(0, "select state");
        state_code_list.add(0, "");

        district_name_list.add(0, "select district");
        district_code_list.add(0, "");
        getting_state();


        contactNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                count_MobileNo = cs.length();
                if( cs.toString().length()==10){
                    contactNumber.setTextColor(Color.BLACK);
                }
                else{
                    contactNumber.setTextColor(Color.RED);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Toast.makeText(getApplicationContext(),"before text change",Toast.LENGTH_LONG).show();

            }

            @Override
            public void afterTextChanged(Editable arg0) {


                // Toast.makeText(getApplicationContext(),"after text change",Toast.LENGTH_LONG).show();
                // Toast.makeText(getApplicationContext(),""+ count_mobileNo,Toast.LENGTH_LONG).show();

            }
        });

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (userName.getText().toString().trim().isEmpty() ){
                    Toast.makeText(Farmer_RegisterationPageActivity.this, "Username is missing", Toast.LENGTH_SHORT).show();
                    userName.requestFocus();
                }else if (password.getText().toString().trim().isEmpty() ){
                    Toast.makeText(Farmer_RegisterationPageActivity.this, "Password is missing", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                }else if (confirm_password.getText().toString().trim().isEmpty()){
                    Toast.makeText(Farmer_RegisterationPageActivity.this, "Password confirmation is missing", Toast.LENGTH_SHORT).show();
                    confirm_password.requestFocus();
                }else {

                    AlertDialog.Builder bo = new AlertDialog.Builder(Farmer_RegisterationPageActivity.this);
                    bo.setTitle("Register")
                    .setMessage("Please note down the login credentials.\nDo you wanna proceed to sign up?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("jjjjjjjjjjjj","hhhhhhhhhhhhhh");
                            submitdata();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("jjjjjjjjjjjj1","hhhhhhhhhhhhhh1");
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
                }



            }
        });
        adapter_state = new ArrayAdapter<String>(Farmer_RegisterationPageActivity.this, android.R.layout.simple_spinner_item, state_name_list);
        adapter_district = new ArrayAdapter<String>(Farmer_RegisterationPageActivity.this, android.R.layout.simple_spinner_item, district_name_list);
        code_names_adapter = new ArrayAdapter<String>(Farmer_RegisterationPageActivity.this,android.R.layout.simple_spinner_item,c_names);
        code_names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_district.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(adapter_state);
        district.setAdapter(adapter_district);
        codespinner.setAdapter(code_names_adapter);

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected_state="";
                code_selected_state="";
                selected_state = (String) state.getItemAtPosition(position);
                //System.out.println(" selected state "+selected_state);

                code_selected_state = state_code_list.get(position);


                if (!code_selected_state.isEmpty())
                {
                    getting_district(code_selected_state);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("20");
            }
        });


        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected_district="";
                selected_district_code="";

                selected_district = district.getSelectedItem().toString();
                //  System.out.println(" selected district name:"+selected_state);


                selected_district_code = district_code_list.get(position);
                // System.out.println("district code :"+selected_district_code);


            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        codespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codeedit.setText(c_code.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        conty();
        try{
            getLastLocation();} catch (Exception e) {
            e.printStackTrace();
        }
        int spinnerPosition = c_iso.indexOf(CountryID);
        //Log.d("mydesh",CountryID+String.valueOf(spinnerPosition));
//set the default according to value
        codespinner.setSelection(spinnerPosition);

        namenext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (first_name.getText().toString().isEmpty() ){
                    Toast.makeText(Farmer_RegisterationPageActivity.this, "First Name is missing", Toast.LENGTH_SHORT).show();

                    first_name.requestFocus();
                }else if (emailId.getText().toString().trim().isEmpty()){
                    Toast.makeText(Farmer_RegisterationPageActivity.this, "Email is missing", Toast.LENGTH_SHORT).show();

                    emailId.requestFocus();
                }
                else{
                    basiccad.setVisibility(View.GONE);
                    addcard.setVisibility(View.VISIBLE);
                }
            }
        });
        addnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (address.getText().toString().trim().isEmpty() ){
                    Toast.makeText(Farmer_RegisterationPageActivity.this, "Address is missing", Toast.LENGTH_SHORT).show();
                    address.requestFocus();
                }else if (selected_state.trim().matches("select state")){
                    Toast.makeText(Farmer_RegisterationPageActivity.this, "State is missing", Toast.LENGTH_SHORT).show();
                    state.requestFocus();
                }else if (selected_district.trim().matches("select district")){
                    Toast.makeText(Farmer_RegisterationPageActivity.this, "District is missing", Toast.LENGTH_SHORT).show();
                    district.requestFocus();
                }else if (pinCode.getText().toString().trim().isEmpty()){
                    Toast.makeText(Farmer_RegisterationPageActivity.this, "Pincode is missing", Toast.LENGTH_SHORT).show();
                    pinCode.requestFocus();
                }
                else{
                    photcard.setVisibility(View.VISIBLE);
                    addcard.setVisibility(View.GONE);
                }
            }
        });
        addback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basiccad.setVisibility(View.VISIBLE);
                addcard.setVisibility(View.GONE);
            }
        });
        photonext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logincard.setVisibility(View.VISIBLE);
                photcard.setVisibility(View.GONE);
            }
        });
        photoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photcard.setVisibility(View.GONE);
                addcard.setVisibility(View.VISIBLE);
            }
        });
        loginback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photcard.setVisibility(View.VISIBLE);
                logincard.setVisibility(View.GONE);
            }
        });
        getusernames();

    }
    void   getcontrylocatiion(){

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(Double.parseDouble(latt), Double.parseDouble(longt), 1); //1 - is number of result you want you write it any integer value. But as you require country name 1 will suffice.
            if (addresses.size() > 0)
                cod=addresses.get(0).getCountryName();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (CountryID.matches("") && !cod.matches("")){

            int spinnerPosition = c_names.indexOf(cod);
            //Log.d("mydesh",CountryID+String.valueOf(spinnerPosition));
//set the default according to value
            Log.d("nnnnn",String.valueOf(spinnerPosition));
            codespinner.setSelection(spinnerPosition);
        }

    }
    String getAlphaNumericString()
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
    void getusernames(){
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getusernames")

                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray obj=response.getJSONArray("list");
                            for (int i=0;i<obj.length();i+=1){
                                usernamelist.add((String) obj.get(i));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
    void submitdata(){

        progress = new ProgressDialog(Farmer_RegisterationPageActivity.this);

        progress.setCancelable(false);
        progress.setMessage("Registering...Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        //submit.setEnabled(false);

            firstname_entered = first_name.getText().toString().trim();
            midNameString=middleName.getText().toString().trim();
            lastNameString=lastName.getText().toString().trim();
            address_entered = address.getText().toString();
            pinCode_enterd = pinCode.getText().toString();
            //contactNumber_entered = contactNumber.getText().toString();
            emailIdString=emailId.getText().toString().trim();
            userName_entered = userName.getText().toString().trim();
            password_entered = password.getText().toString().trim();
            confirmPassword_entered = confirm_password.getText().toString();
            state_entered = selected_state;
            district_entered = selected_district;
            if(validation())
            {
                if ( marray==null){
                    registerfarmer("");
                    Log.d("ggg1","ffff");
                }
                else if (marray.toString().isEmpty() ){
                    registerfarmer("");
                    Log.d("ggg2","ffff");
                }


                else {
                    WorkManager mWorkManager = WorkManager.getInstance();
                    String mArray_file = "";
                    try {
                        File compressedImageFile = new Compressor(Farmer_RegisterationPageActivity.this).compressToFile(FileUtils.from(Farmer_RegisterationPageActivity.this, marray));
                        mArray_file = compressedImageFile.getAbsolutePath();


                    String stname = userName.getText().toString() + getAlphaNumericString();
                    Data data = new Data.Builder()
                            .putString("url", Uri.fromFile(new File(mArray_file)).toString())
                            .putString("id", stname).putString("code", "1")
                            .build();

                    OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(Uploadingworker_profile.class)
                            .setInputData(data).build();


                    mWorkManager.enqueue(mRequest);


                    mWorkManager.getWorkInfoByIdLiveData(mRequest.getId()).observe(Farmer_RegisterationPageActivity.this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {

                                Log.d("Register_farmer", "hi" + workInfo.toString());
                                registerfarmer(stname);
//                            AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/RegisterFarmer1")
//                                    .addJSONObjectBody(jo)
//                                    .build()
//                                    .getAsJSONObject(new JSONObjectRequestListener() {
//                                        @Override
//                                        public void onResponse(JSONObject response) {
//                                            Log.d("bbbbb",response.toString());
//
//
//                                            try {
//
//
//                                                boolean result = response.getBoolean("result");
//                                                String message = response.getString("message");
//
//
//                                                if (result)
//                                                {
//                                                    Map<String, Object> user = new HashMap<>();
//                                                    user.put("username", userName_entered);
//                                                    user.put("password", password_entered);
//                                                    user.put("usertype", "1");
//                                                    db.collection("loginaidisc")
//                                                            .add(user)
//                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                                @Override
//                                                                public void onSuccess(DocumentReference documentReference) {
//                                                                    //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                                                                    Toast.makeText(Farmer_RegisterationPageActivity.this, message, Toast.LENGTH_LONG).show();
//                                                                    Intent intent=new Intent(Farmer_RegisterationPageActivity.this, Login.class);
//                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                                    startActivity(intent);
//                                                                }
//                                                            })
//                                                            .addOnFailureListener(new OnFailureListener() {
//                                                                                      @Override
//                                                                                      public void onFailure(@NonNull Exception e) {
//                                                                                          Toast.makeText(Farmer_RegisterationPageActivity.this, "Error-F18", Toast.LENGTH_LONG).show();
//                                                                                      }
//                                                                                  });
//
//
//
//
//                                                } else {
//                                                    Toast.makeText(Farmer_RegisterationPageActivity.this,message,Toast.LENGTH_LONG).show();
//
//                                                }
//
//
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                                Toast.makeText(Farmer_RegisterationPageActivity.this,"Error1",Toast.LENGTH_LONG).show();
//                                            }
//
//
//                                        }
//
//                                        @Override
//                                        public void onError(ANError error) {
//
//
//                                            Toast.makeText(Farmer_RegisterationPageActivity.this, "Error", Toast.LENGTH_LONG).show();
//                                        }
//                                    });

                            }
                            if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED) {
                                progress.cancel();

                                show_dialog("Error2");
                            }
                        }
                    });
                    } catch (Exception e) {
                        e.printStackTrace();
                        registerfarmer("");
                    }
                    //registerfarmer(stname);
                }


                }else{
                submit.setEnabled(true);
                Log.d("ggg","ffff");
                //.makeText(Farmer_RegisterationPageActivity.this, "Something is missing", Toast.LENGTH_SHORT).show();
                progress.cancel();
            }
        }
//    private void sendVerificationCode(String number) {
//        // this method is used for getting
//        // OTP on user phone number.
//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(mAuth)
//                        .setPhoneNumber(number)            // Phone number to verify
//                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                        .setActivity(this)                 // Activity (for callback binding)
//                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
//                        .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);
//    }
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            final String code = phoneAuthCredential.getSmsCode();
//
//            // checking if the code
//            // is null or not.
//            if (code != null) {
//                // if the code is not null then
//                // we are setting that code to
//                // our OTP edittext field.
//                //Log.d("codecomplete",code.toString());
////                androidx.appcompat.app.AlertDialog.Builder ob = new androidx.appcompat.app.AlertDialog.Builder(Farmer_RegisterationPageActivity.this);
////                LinearLayout mot = new LinearLayout(Farmer_RegisterationPageActivity.this);
////                LinearLayout.LayoutParams paramm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////                paramm.setMargins(10, 10, 10, 10);
////                mot.setLayoutParams(paramm);
////                mot.setOrientation(LinearLayout.VERTICAL);
////                mot.setHorizontalGravity(Gravity.CENTER);
////
//////            TextView tx1=new TextView(Farmer_RegisterationPageActivity.this);
////                LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(100, 100);
////                paramm.setMargins(5, 5, 5, 5);
//////            tx1.setText("Phone Verification");
//////            tx1.setLayoutParams(param1);
//////            mot.addView(tx1);
////
////                ImageView tx21 = new ImageView(Farmer_RegisterationPageActivity.this);
////                tx21.setImageDrawable(Farmer_RegisterationPageActivity.this.getResources().getDrawable(correct, Farmer_RegisterationPageActivity.this.getTheme()));
////                tx21.setLayoutParams(param1);
////                mot.addView(tx21);
////
////                ob.setView(mot);
////                ob.setTitle("Phone Number is successfully verified").setPositiveButton("OK", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        dialog.dismiss();
////                    }
////                }).show();
////                edtOTP.setText(code);
////
////                // after setting this code
////                // to OTP edittext field we
////                // are calling our verifycode method.
////                verifyCode(code);
//            }
//        }
//
//        @Override
//        public void onVerificationFailed(@NonNull FirebaseException e) {
//
//        }
//
//        @Override
//        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//            verificationId=s;
//            Log.d("codesent",verificationId.toString());
//
//        }
//    };


    private void registerfarmer(String stname) {
        JSONObject jo = new JSONObject();
        try {

            jo.put("FirstName", firstname_entered);
            jo.put("MidName", midNameString);
            jo.put("LastName", lastNameString);
            jo.put("Address", address_entered);
            jo.put("contactNumber", contactNumber_entered);
            jo.put("state", state_entered);
            jo.put("district", district_entered);
            jo.put("pinCode", pinCode_enterd);
            jo.put("Email", emailIdString);
            jo.put("Username", userName_entered);
            jo.put("Password", password_entered);
            jo.put("imagepath", stname);

        } catch (JSONException E) {
            progress.cancel();
        }
        Log.d("bbbbb1",jo.toString());
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/RegisterFarmer1")
                .addJSONObjectBody(jo)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("bbbbb", response.toString());
                        //progress.cancel();

                        try {


                            boolean result = response.getBoolean("result");
                            String message = response.getString("message");


                            if (result) {
                                settoken(message);
                            }
                            else{
                                progress.cancel();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.cancel();
                        }
                    }
                        @Override
                    public void onError(ANError anError) {
                        Log.d("Register_farmer","Error-F17");
                            progress.cancel();
                            //Toast.makeText(Farmer_RegisterationPageActivity.this, , Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void settoken(String message) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", userName_entered);
        user.put("password", password_entered);
        user.put("usertype", "1");
        db.collection("loginaidisc")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progress.cancel();
                        Toast.makeText(Farmer_RegisterationPageActivity.this, message, Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(Farmer_RegisterationPageActivity.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Register_farmer","Error-F17");
                progress.cancel();
            }
        });
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internetReceiver, intentFilter);
        //updateUI(currentUser);
        super.onStart();
    }

    public boolean checking_permision() {


        if ((ContextCompat.checkSelfPermission(Farmer_RegisterationPageActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(Farmer_RegisterationPageActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Farmer_RegisterationPageActivity.this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return false;

        }


        return true;
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
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
    public void show_dialog(String message){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Farmer_RegisterationPageActivity.this);

        alertDialogBuilder.setTitle("Response");


        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {//yes
                    public void onClick(final DialogInterface dialog, int id) {


                        dialog.cancel();
                    }
                });



        AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
        if (requestCode==111 && data!=null){
//            Uri imageUri = data.getData();
//            Log.d("jjjj1",String.valueOf(imageUri));
//            Bitmap photo = (Bitmap)data.getExtras()
//                    .get("data");
//
//            // Set the image in imageview for display
//            expertimage.setImageBitmap(photo);
//            marray=imageUri;

            String url = data.getStringExtra("url");
            System.out.println(" url is:" + url);
            //imn.setImageURI(Uri.parse(url));
            Bitmap bitmap=null;
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
            expertimage.setImageBitmap(bitmap);
            marray=Uri.parse(url);
//                image.setVisibility(View.VISIBLE);
//                upload.setVisibility(View.VISIBLE);
//                file_path = url;
            //uploadprofileimg(Uri.parse(url),bitmap);
        }
        if (requestCode==112 && data!=null){
//            Uri selectedImageUri = data.getData();
//            Log.d("jjjj2",String.valueOf(selectedImageUri));
//            if ( selectedImageUri!=null) {
//                // update the preview image in the layout
//                expertimage.setImageURI(selectedImageUri);
//                marray=selectedImageUri;
//            }
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();

            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            //Log.w("path of image from gallery......******************.........", picturePath+"");
            //imn.setImageBitmap(thumbnail);
            //bitmaps.add(thumbnail);
            //uploadprofileimg(selectedImage,thumbnail);
            expertimage.setImageBitmap(thumbnail);
                marray=selectedImage;
//            WorkManager mWorkManager = WorkManager.getInstance();
//            String mArray_file="";
//            try{
//                File compressedImageFile= new Compressor(Farmer_RegisterationPageActivity.this).compressToFile(FileUtils.from(Farmer_RegisterationPageActivity.this, marray));
//                mArray_file= compressedImageFile.getAbsolutePath();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Log.d("galll",Uri.fromFile(new File(mArray_file)).toString());

        }} catch (Exception e) {
            e.printStackTrace();
        }

    }
    public boolean validation()
    {


        if (first_name.getText().toString().isEmpty())
        {


            Toast.makeText(Farmer_RegisterationPageActivity.this, "Please enter first name", Toast.LENGTH_LONG).show();


            return false;
        }

        if (address.getText().toString().isEmpty()) {
            Toast.makeText(Farmer_RegisterationPageActivity.this, "Please enter address", Toast.LENGTH_LONG).show();

            return false;
        }

        if (code_selected_state.isEmpty()) {
            Toast.makeText(Farmer_RegisterationPageActivity.this,"Please select state", Toast.LENGTH_LONG).show();

            return false;
        }

        if (selected_district_code.isEmpty()) {


            Toast.makeText(Farmer_RegisterationPageActivity.this, "Please select district", Toast.LENGTH_LONG).show();

            return false;
        }


        if (contactNumber.getText().toString().isEmpty()) {


            Toast.makeText(Farmer_RegisterationPageActivity.this, "Please enter contact number", Toast.LENGTH_LONG).show();
            return false;
        }


        if (count_MobileNo < 10) {
            Toast.makeText(Farmer_RegisterationPageActivity.this,"Please enter 10 digit mobile number", Toast.LENGTH_LONG).show();


            return false;
        }
        if (password.getText().toString().isEmpty()) {
            Toast.makeText(Farmer_RegisterationPageActivity.this, "PLease enter password", Toast.LENGTH_LONG).show();


            return false;
        }
        if (confirm_password.getText().toString().isEmpty()) {

            Toast.makeText(Farmer_RegisterationPageActivity.this, "PLease enter confirm password", Toast.LENGTH_LONG).show();
        }

        if (!(password.getText().toString().equals(confirm_password.getText().toString()))) {
            Toast.makeText(Farmer_RegisterationPageActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }
        my_application application = (my_application) getApplication();

        if (application.check_signup_username(userName_entered.trim(), Farmer_RegisterationPageActivity.this).matches("Perfect!")) {

        } else {

            return false;
        }

        if (application.check_signup_password(password_entered.trim(), Farmer_RegisterationPageActivity.this).matches("Perfect!")) {

        } else {

            return false;
        }
        if(!isEmailValid(emailId.getText().toString())){

            Toast.makeText(Farmer_RegisterationPageActivity.this, "Please enter valid Email ID.", Toast.LENGTH_SHORT).show();

            return  false;
        };


        return true;
    }
    boolean isEmailValid(CharSequence email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

       /* Intent intent=new Intent(Result_identify.this, Identify_disease.class);
        startActivity(intent);*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            default:
                break;

        }

        return true;
    }

    public void setSpinnerValue()
    {



    }

    public void getting_state()
    {

        AndroidNetworking.post("https://kvk.icar.gov.in/api/api/KMS/StateList")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {


                        adapter_state.notifyDataSetChanged();


                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = (JSONObject) response.get(i);

                                String state_code = object.optString("Code");
                                String state_name_english = object.optString("EnglishName");

                                state_name_list.add(i + 1, state_name_english);
                                state_code_list.add(i + 1, state_code);

                            }
                            adapter_state.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(Farmer_RegisterationPageActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void getting_district(String state_code)
    {

      /*  district_name_list.clear();
        district_code_list.clear();*/

        AndroidNetworking.get("https://kvk.icar.gov.in/api/api/KMS/DistrictList?StateCode={state_code}")
                .addPathParameter("state_code", state_code)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {


                        district_name_list.clear();
                        district_code_list.clear();

                       // adapter_district.notifyDataSetChanged();

                        district_name_list.add(0, "select district");
                        district_code_list.add(0, "");
                        int n = 0;

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = (JSONObject) response.get(i);

                                String district_name_english = object.optString("EnglishName");
                                String district_code = object.optString("Code");
                                String kvk_english_name = object.optString("Kvk_EnglishName");


                                if (!kvk_english_name.isEmpty()) {


                                    n = n + 1;
                                    district_name_list.add(n, kvk_english_name);
                                    district_code_list.add(n, district_code);
                                }


                                adapter_district.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(Farmer_RegisterationPageActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });
    }
    public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }
    void conty(){
        Map<String, String> countries = new HashMap<>();
        for (String iso : Locale.getISOCountries())

        {
            Locale l = new Locale("", iso);
            countries.put( iso,l.getDisplayCountry());
        }

        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for (String s : rl) {
            String[] g = s.split(",");
            c_code.add("+"+g[0].trim());
            
            String val = null;
            try{
            val = countries.get(g[1].trim());} catch (Exception e) {
                e.printStackTrace();
            }
            if (val==null ){
                val=s;
            }


            c_names.add(val);
            c_iso.add(g[1].trim());
        }
        code_names_adapter.notifyDataSetChanged();

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
                            getcontrylocatiion();
//                            Intent inh=new Intent(Login.this, Login.class);
//                            inh.putExtra("loops",1);
//                            startActivity(inh);
//                            finish();
                            //longitTextView.setText(location.getLongitude() + "");
                            //Toast.makeText(Farmer_RegisterationPageActivity.this, "GPS Is On !"+latt+longt, Toast.LENGTH_LONG).show();
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
            getcontrylocatiion();


        }
    };
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mcurrent = image.getAbsolutePath();
        return image;
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
//    @Override
//    public void
//    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//
//    }

}