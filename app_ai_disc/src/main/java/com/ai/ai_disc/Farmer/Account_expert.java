package com.ai.ai_disc.Farmer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.ai.ai_disc.FileUtils;
import com.ai.ai_disc.InternetReceiver;
import com.ai.ai_disc.Login;
import com.ai.ai_disc.R;
import com.ai.ai_disc.Uploadingworker_profile;
import com.ai.ai_disc.editprofile;
import com.ai.ai_disc.my_application;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;


public class Account_expert extends AppCompatActivity {

    TextInputEditText user_name,first_name,password,contact_number,email_id,confirm_password,speciality,designation;
    TextInputEditText middle_name,last_name,address,pincode;
    Button submit,uploadim;
    Uri marray=null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int CAMERA_REQUEST = 1888;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ArrayList<String> institute_id_array,institute_name_array,institute_address_array;
    ArrayList<String> crpname=new ArrayList<>();
    ArrayList <String>crpid=new ArrayList<>();
    ArrayAdapter institute_adapter,title_spinner_adapter,cropadapter;
    Spinner institute_spinner;
            Spinner cropspineer;
    String institute_id_selected="";
    String title_selected="";
    String cropname="";
    String cropid;
    Spinner title_spinner;
ImageView expertimage;
    ArrayList<String> title_list;
    TextInputLayout txtusr,txtpass,confpass;
    ProgressDialog progress;
    InternetReceiver internet = new InternetReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_account_validator);
        setContentView(R.layout.activity_account_expert);


        setTitle("AI-DISC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_name=(TextInputEditText)findViewById(R.id.username);

        first_name=(TextInputEditText)findViewById(R.id.first_name);
        middle_name=(TextInputEditText)findViewById(R.id.middle_name);
        expertimage=(ImageView) findViewById(R.id.addexpertimage);
        designation=(TextInputEditText)findViewById(R.id.designation);
        speciality=(TextInputEditText) findViewById(R.id.speciality);
        last_name=(TextInputEditText)findViewById(R.id.last_name);
        address=(TextInputEditText)findViewById(R.id.address);
        pincode=(TextInputEditText)findViewById(R.id.pincode);
        password=(TextInputEditText)findViewById(R.id.password);
        confirm_password=(TextInputEditText)findViewById(R.id.confirm_password);
        contact_number=(TextInputEditText)findViewById(R.id.contact_number);
        email_id=(TextInputEditText)findViewById(R.id.email_id);
        submit=(Button) findViewById(R.id.submit);

        txtusr = (TextInputLayout) findViewById(R.id.userup);
        txtpass = (TextInputLayout) findViewById(R.id.userpass);

                confpass = (TextInputLayout) findViewById(R.id.confpass);
        institute_spinner=(Spinner)findViewById(R.id.institute_spinner);
        title_spinner=(Spinner)findViewById(R.id.title_spinner);
        cropspineer=(Spinner)findViewById(R.id.crop_spinnerd);

        institute_id_array = new ArrayList<String>();
        institute_name_array = new ArrayList<String>();
        institute_address_array = new ArrayList<String>();
        user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <8)
                {
                    txtusr.setHelperText("At least 8 Characters and @,-,.,_");
                    //confirm_password.setEnabled(false);
                }
                else{
                    my_application application = (my_application) getApplication();

                    String status=application.check_signup_username(s.toString().trim(), Account_expert.this) ;

                    txtusr.setHelperText(status);
                    //confirm_password.setEnabled(true);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <8)
                {
                    txtpass.setHelperText("Atleast 8 Characters with Atleast One Digit, One of #,$,&,^,%,*,?,!,@");
                    confirm_password.setEnabled(false);
                }
                else{
                    my_application application = (my_application) getApplication();

                    String status=application.check_signup_password(s.toString().trim(), Account_expert.this) ;

                    txtpass.setHelperText(status);
                    confirm_password.setEnabled(true);


                }


                if (s.length() >0){
                    confirm_password.setVisibility(View.VISIBLE);
                }else{confirm_password.setVisibility(View.GONE);}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String psd=password.getText().toString();
                if (psd.length()>0 && psd.matches(s.toString())){
                    confpass.setHelperText("Matched !");
                }else{
                    confpass.setHelperText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        title_list= new ArrayList<String>();
        title_list.add("Select Title");
        title_list.add("Mr.");
        title_list.add("Mrs.");
        title_list.add("Dr.");
        title_list.add("Ms.");



        institute_name_array.add("Select Institute");
        institute_id_array.add("");
        institute_address_array.add("");

        institute_adapter = new ArrayAdapter(Account_expert.this,android.R.layout.simple_list_item_1,institute_name_array);
        title_spinner_adapter = new ArrayAdapter(Account_expert.this,android.R.layout.simple_list_item_1,title_list);
        cropadapter=new ArrayAdapter(Account_expert.this,android.R.layout.simple_list_item_1,crpname);


        institute_spinner.setAdapter(institute_adapter);

        title_spinner.setAdapter(title_spinner_adapter);
        expertimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checking_permision()   ){
                AlertDialog.Builder opt=new AlertDialog.Builder(Account_expert.this);
                CharSequence[] option=new CharSequence[]{"Take a picture","Choose from Gallery","Cancel"};
                opt.setTitle("Set profile picture");
                opt.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Dexter.withActivity(Account_expert.this)
                                    .withPermissions(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .withListener(new MultiplePermissionsListener() {
                                        @Override
                                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                                            // check if all permissions are granted
                                            if (report.areAllPermissionsGranted()) {
                                                // do you work now

                                                Intent intentd = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                startActivityForResult(intentd, 111);

                                            } else {
                                                Toast.makeText(Account_expert.this, "Permission is required.", Toast.LENGTH_LONG).show();
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
                        if (which == 1) {
                            Dexter.withActivity(Account_expert.this)
                                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse response) {
                                            // permission is granted, open the camera

                                            Log.i("TAG", "onPermissionGranted: ");
                                            Intent intent = new Intent();
                                            intent.setType("image/*");
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                            startActivityForResult(Intent.createChooser(intent, "Select Image"), 112);
                                            //    Toast.makeText(Upload_activity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse response) {
                                            // check for permanent denial of permission
                                            Log.i("TAG", "onPermissionDenied 1: ");
                                            // if (response.isPermanentlyDenied()) {
                                            // navigate user to app settings
                                            Log.i("TAG", "onPermissionDenied: ");
                                            Toast.makeText(Account_expert.this, "Permission is required.", Toast.LENGTH_LONG).show();
                                            // }
                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                            token.continuePermissionRequest();
                                        }
                                    }).check();
                        }
                        if (which == 2) {
                            dialog.dismiss();
                        }
                    }
                });
                opt.show();}

            }
        });


        getWindow().setSoftInputMode(1);

        institute_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                institute_id_selected=institute_id_array.get(position);
                System.out.println(" institute id  selected :"+institute_id_selected);

                //  Toast.makeText(Account_validator.this,"selected value:"+institute_id_selected,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cropspineer.setAdapter(cropadapter);
        title_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position!=0){
                    title_selected= (String) title_spinner.getSelectedItem();
                    System.out.println(" selected title"+title_selected);

                    // Toast.makeText(Account_validator.this, "inside if ,"+title_selected, Toast.LENGTH_SHORT).show();
                }else{
                    // Toast.makeText(Account_validator.this, "inside else ,"+title_selected, Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cropspineer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cropid=crpid.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validation()){

                    //  reset_form();
                    if (marray!=null ){
                            profile();
                    }
                    else{
                        show_dialog("Upload Profile Image");
                    }

                }


            }
        });

        getting_list_institute_crop();

    }
    public boolean checking_permision() {


        if ((ContextCompat.checkSelfPermission(Account_expert.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(Account_expert.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Account_expert.this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==111 && data!=null){
            Uri imageUri = data.getData();

            Bitmap photo = (Bitmap)data.getExtras()
                    .get("data");

            // Set the image in imageview for display
            expertimage.setImageBitmap(photo);
            marray=imageUri;
        }
        if (requestCode==112 && data!=null){
            Uri selectedImageUri = data.getData();
            Log.d("jjjj",String.valueOf(selectedImageUri));
            if ( selectedImageUri!=null) {
                // update the preview image in the layout
                expertimage.setImageURI(selectedImageUri);
                marray=selectedImageUri;
            }
        }

    }

    public  boolean validation(){


        if(title_selected.isEmpty()){

            Toast.makeText(Account_expert.this, "Title is not selected.", Toast.LENGTH_SHORT).show();

            return  false;
        }

        if(designation.getText().toString().trim().isEmpty()){

            Toast.makeText(Account_expert.this, "Designation can not be empty", Toast.LENGTH_SHORT).show();

            return  false;
        }
        if(speciality.getText().toString().trim().isEmpty()){

            Toast.makeText(Account_expert.this, "Speciality can not be empty", Toast.LENGTH_SHORT).show();

            return  false;
        }
        if(first_name.getText().toString().trim().isEmpty()){

            Toast.makeText(Account_expert.this, "First name can not be empty", Toast.LENGTH_SHORT).show();

            return  false;
        }
        if(address.getText().toString().trim().isEmpty()){

            Toast.makeText(Account_expert.this, "Address can not be empty", Toast.LENGTH_SHORT).show();

            return  false;
        }
        /*
        if(pincode.getText().toString().trim().isEmpty()){

            Toast.makeText(Account_validator.this, "Pincode is empty", Toast.LENGTH_SHORT).show();

            return  false;
        }

         */

        if(contact_number.getText().toString().trim().isEmpty()){

            Toast.makeText(Account_expert.this, "Contact number can not be empty", Toast.LENGTH_SHORT).show();

            return  false;
        }


        if(email_id.getText().toString().trim().isEmpty()){

            Toast.makeText(Account_expert.this, "Email id  can not be empty", Toast.LENGTH_SHORT).show();

            return  false;
        }

        if(!isEmailValid(email_id.getText().toString())){

            Toast.makeText(Account_expert.this, "Please enter valid Email ID.", Toast.LENGTH_SHORT).show();

            return  false;
        };

        if(institute_id_selected.isEmpty()){

            Toast.makeText(Account_expert.this, "Institute is not selected.", Toast.LENGTH_SHORT).show();

            return  false;
        }

        if(user_name.getText().toString().trim().isEmpty()){


            Toast.makeText(Account_expert.this, "User name can not be empty", Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(password.getText().toString().trim().isEmpty()){

            Toast.makeText(Account_expert.this, "Password can not be empty", Toast.LENGTH_SHORT).show();

            return  false;
        }


        if(confirm_password.getText().toString().trim().isEmpty()){

            Toast.makeText(Account_expert.this, "Confirm password can not be empty", Toast.LENGTH_SHORT).show();

            return  false;
        }
        if(!(password.getText().toString().trim().equals(confirm_password.getText().toString().trim()))){

            Toast.makeText(Account_expert.this, "Password and Confirm password is not same.", Toast.LENGTH_SHORT).show();

            return  false;
        }




        my_application application = (my_application) getApplication();

        if(application.check_user_name(user_name.getText().toString().trim(),Account_expert.this)){

        }else{

            return  false;
        }

        if(application.check_password(password.getText().toString().trim(),Account_expert.this)){

        }else{

            return  false;
        }
        if(application.check_password(confirm_password.getText().toString().trim(),Account_expert.this)){

        }else{

            return  false;
        }





        return  true;
    }

    boolean isEmailValid(CharSequence email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    public  void data_sending1(String user_name,String name,String contact,String email,String password,String middle_name_got,
                               String last_name_got,String address_got,String pincode_got,String cropid,String stname){



        JSONObject object = new JSONObject();
        try {
            object.put("title",title_selected);
            object.put("user_name",user_name);
            object.put("first_name",name);
            object.put("middle_name",middle_name_got);
            object.put("last_name",last_name_got);
            object.put("address",address_got);
            object.put("pincode",pincode_got);
            object.put("institute_id",institute_id_selected);
            object.put("contact_number",contact);
            object.put("email_id",email);
            object.put("password",password);
            object.put("crop_id",cropid);
            object.put("speciality",speciality.getText().toString());
            object.put("designation",designation.getText().toString());
            object.put("imagepath",stname);
            object.put("created_by","");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Add_expert")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        progress.cancel();

                        System.out.println(" responsegggg:"+response);
                        try {


                            String message= response.getString("message");
                            boolean result=  response.getBoolean("result");


                            if(result){
                                settoken(message,user_name,password);
                                // Toast.makeText(Account_validator.this, "Account is created", Toast.LENGTH_SHORT).show();
                                                            //

                                reset_form();
                            }else{
                                // Toast.makeText(Account_validator.this, "Account is not created", Toast.LENGTH_SHORT).show();
                                show_dialog("Account is not created");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                    }
                    @Override
                    public void onError(ANError error) {

                        progress.cancel();
                        //   Toast.makeText(Account_validator.this,"There is error in creating account of validator.",Toast.LENGTH_LONG).show();
                        show_dialog("Error in creating expert account.");
                    }
                });
    }
    private void settoken(String message,String userName_entered,String password_entered) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", userName_entered);
        user.put("password", password_entered);
        user.put("usertype", "2");
        db.collection("loginaidisc")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Account_expert.this, message, Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(Account_expert.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Register_expert","Error-F18");
            }
        });
    }

    public  void getting_list_institute_crop(){




        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_institute1")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        institute_id_array.clear();
                        institute_name_array.clear();
                        institute_address_array.clear();
                        crpid.clear();
                        crpname.clear();

                        institute_id_array.add("");
                        institute_name_array.add("Select Institute");
                        institute_address_array.add("");
                        crpname.add("Select Crop");
                        crpid.add("0");

                        System.out.println(" response list institute:"+response);

                            /*
{
  "institute_list": [
    {
      "institute_id": "sample string 1",
      "institute_name": "sample string 2",
      "institute_address": "sample string 3"
    },
    {
      "institute_id": "sample string 1",
      "institute_name": "sample string 2",
      "institute_address": "sample string 3"
    }
  ]
}
                             */

                        try {

                            JSONArray array = response.getJSONArray("institute_list");
                            for(int i=0; i<array.length();i++){

                                JSONObject object=(JSONObject)array.get(i);
                                String institute_id=  object.getString("institute_id");
                                String institute_name=  object.getString("institute_name");
                                String institute_address= object.getString("institute_address");



                                institute_id_array.add(i+1,institute_id);
                                institute_name_array.add(i+1,institute_name);
                                institute_address_array.add(i+1,institute_address);


                            }
                            JSONArray array1 = response.getJSONArray("croplist");
                            for(int i=0; i<array1.length();i++){

                                JSONObject object=(JSONObject)array1.get(i);
                                String institute_id=  object.getString("crop_id");
                                String institute_name=  object.getString("crop_name");



                                crpid.add(i+1,institute_id);
                                crpname.add(i+1,institute_name);
                                //institute_address_array.add(i+1,institute_address);


                            }


                            institute_adapter.notifyDataSetChanged();
                            cropadapter.notifyDataSetChanged();




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                    }
                    @Override
                    public void onError(ANError error) {


                        Toast.makeText(Account_expert.this,"Error in getting list of institute.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void show_dialog(String message){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Account_expert.this);

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
    public   boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflator= getMenuInflater();
        inflator.inflate(R.menu.empty,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){


            case android.R.id.home:
                finish();

                break;
            default:
                break;

        }

        return true;
    }

    public void reset_form(){

        first_name.setText("");
        middle_name.setText("");
        last_name.setText("");
        address.setText("");
        pincode.setText("");
        contact_number.setText("");
        email_id.setText("");
        user_name.setText("");
        password.setText("");
        confirm_password.setText("");

        title_selected="";
        title_spinner.setSelection(0);

        institute_id_selected="";
        institute_spinner.setSelection(0);
    }

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet,intentFilter);

        // InternetReceiver internet = new InternetReceiver();


    }


    @Override
    public void onStop(){
        super.onStop();
        unregisterReceiver(internet);

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
    private void profile() {
        progress = new ProgressDialog(Account_expert.this);
        progress.setCancelable(false);
        progress.setMessage("Sending Data");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        WorkManager mWorkManager = WorkManager.getInstance();
        String mArray_file="";
        try{
            File compressedImageFile= new Compressor(Account_expert.this).compressToFile(FileUtils.from(Account_expert.this, marray));
             mArray_file= compressedImageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String stname=user_name.getText().toString()+getAlphaNumericString();

        Data data = new Data.Builder()
                .putString("url", Uri.fromFile(new File(mArray_file)).toString())
                .putString("id", stname).putString("code", "0")
                .build();

        OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(Uploadingworker_profile.class)
                .setInputData(data).build();


        mWorkManager.enqueue(mRequest);


        mWorkManager.getWorkInfoByIdLiveData(mRequest.getId())
                .observe(Account_expert.this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            Log.d("hhhhhhhhhhhhhhhhhhh","mmm");
                            String middle_name_got=middle_name.getText().toString();
                            String last_name_got=last_name.getText().toString();
                            String address_got=address.getText().toString();
                            String pincode_got=pincode.getText().toString();
                            data_sending1(user_name.getText().toString(),first_name.getText().toString(),contact_number.getText().toString(),email_id.getText().toString(),password.getText().toString(),
                                    middle_name_got,last_name_got,address_got,pincode_got,cropid,stname);

                        }
                        if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED) {

                            show_dialog("Error");
                            progress.cancel();
                        }
                    }
                });
    }

}
