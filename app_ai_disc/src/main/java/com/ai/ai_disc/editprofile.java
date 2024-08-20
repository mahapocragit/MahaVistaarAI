package com.ai.ai_disc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.Farmer.Farmer_Disease_identifier_pest_Identifier_Activity;
import com.ai.ai_disc.Farmer.Farmer_RegisterationPageActivity;
import com.ai.ai_disc.model.editprofile_model;
import com.ai.ai_disc.model.editprofile_response;
import com.ai.ai_disc.view_model.editprofile_view;
import com.ai.ai_disc.view_model.identifier_croplist;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class editprofile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView status,update;
    TextView fisrname,midname,lastname,email,phone,local,state,userid,district,pass1;
    TextView fisrname1,midname1,lastname1,email1,phone1,local1,state1,image1,district1;
    ImageView img;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";
    String statecode="";
    String statecodeid="";
    CardView card1,card2;
    ArrayList<String> state_name_list;
    ArrayList<String> state_code_list;
    ArrayList<String> district_code_list;
    ArrayList<String> district_name_list;
    ProgressBar spin;
    editprofile_view viewmodel;
    Button go;private static final int CAMERA_REQUEST = 1888;
    TextView loct;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    String val;

    String selectedtext="",selectedtext_dis="";
    WorkManager mWorkManager;
    my_application application = (my_application) getApplication();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setTitle("AI-DISC");
        setNavigationViewListener();
        context=editprofile.this;
        drawerLayout = findViewById(R.id.my_drawer_layout1);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

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
        try{userd.setText(user_singleton.getInstance().getUser_name());
            acc.setText(user_singleton.getInstance().getUser_type()+" Account");
            loct.setText(user_singleton.getInstance().getloct());
            //Log.d("ggg",head.findViewById(R.id.vsr).toString());


        } catch (Exception e) {
            e.printStackTrace();
           // Log.d("error",e.toString());
        }
        state_code_list=new ArrayList<>();
        state_name_list=new ArrayList<>();
        district_code_list=new ArrayList<>();
        district_name_list=new ArrayList<>();
        state_name_list.add(0, "select state");
        state_code_list.add(0, "");

        district_name_list.add(0, "select district");
        district_code_list.add(0, "");
        mWorkManager = WorkManager.getInstance();
        fisrname=findViewById(R.id.firsname_text);
        fisrname1=findViewById(R.id.firstnamebutton);
        lastname=findViewById(R.id.lastname_text);
        lastname1=findViewById(R.id.lastnamebutton);
        midname=findViewById(R.id.midname_text);
        midname1=findViewById(R.id.midnamebutton);
        email=findViewById(R.id.email_text);
        email1=findViewById(R.id.emailbutton);
        phone=findViewById(R.id.phone_text);
        phone1=findViewById(R.id.phonebutton);
        local=findViewById(R.id.loc_text);
        local1=findViewById(R.id.locbutton);
        district=findViewById(R.id.dist_text);
        district1=findViewById(R.id.districtbutton);
        state=findViewById(R.id.state_text);
        state1=findViewById(R.id.statebutton);
        img=findViewById(R.id.grid_image);
        image1=findViewById(R.id.changeimg);
        pass1=findViewById(R.id.passbutton);
        userid=findViewById(R.id.userid);
        userid.setText(user_singleton.getInstance().getUser_name());
        fisrname.setText(user_singleton.getInstance().getfname());
        lastname.setText(user_singleton.getInstance().getlname());
        email.setText(user_singleton.getInstance().getEmail_id());
        phone.setText(user_singleton.getInstance().getMobile_number());
        //getting_state();
        district1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statecodeid.matches("")){
                    Toast.makeText(editprofile.this, "Select State First", Toast.LENGTH_SHORT).show();
                }else{

                    AlertDialog.Builder oj=new AlertDialog.Builder(editprofile.this);

                    oj.setTitle("Change District !");
                    CardView card=new CardView(editprofile.this);
                    ScrollView bv=new ScrollView(editprofile.this);
                    LinearLayout linearLayout = new LinearLayout(editprofile.this);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    bv.setLayoutParams(lp2);
                    lp.setMargins(20,15,20,10);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    final RadioButton[] rb = new RadioButton[district_name_list.size()];
                    RadioGroup rg = new RadioGroup(editprofile.this); //create the RadioGroup
                    rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                    for(int i=0; i<district_name_list.size(); i++){
                        rb[i]  = new RadioButton(editprofile.this);
                        rb[i].setText(" " + district_name_list.get(i));

                        rb[i].setId(i + 100);
                        rg.addView(rb[i]);
                    }
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    lp1.setMargins(60,15,20,10);
                    rg.setLayoutParams(lp1);
                    linearLayout.addView(rg);
                    //lp.setMargins(20,5,20,5);
                    linearLayout.setLayoutParams(lp);
                    card.setRadius(10);

                    card.addView(linearLayout);
                    bv.addView(card);
                    oj.setView(bv);

                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            RadioButton
                                    r
                                    = (RadioButton) group
                                    .findViewById(checkedId);
                            selectedtext_dis= r.getText().toString();
                        }
                    });
                    oj.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (selectedtext_dis.matches("") || selectedtext_dis.matches("select district")){
                                Toast.makeText(editprofile.this, "Please select district ", Toast.LENGTH_SHORT).show();
                            }else{
                                setupdate(selectedtext_dis,8);
                            }
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                            .show();
                }

            }
        });
        state1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oj=new AlertDialog.Builder(editprofile.this);

                oj.setTitle("Change State !");
                CardView card=new CardView(editprofile.this);
                ScrollView bv=new ScrollView(editprofile.this);
                LinearLayout linearLayout = new LinearLayout(editprofile.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                bv.setLayoutParams(lp2);
                lp.setMargins(20,15,20,10);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                final RadioButton[] rb = new RadioButton[state_name_list.size()];
                RadioGroup rg = new RadioGroup(editprofile.this); //create the RadioGroup
                rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                for(int i=0; i<state_name_list.size(); i++){
                    rb[i]  = new RadioButton(editprofile.this);
                    rb[i].setText(" " + state_name_list.get(i));

                    rb[i].setId(i + 100);
                    rg.addView(rb[i]);
                }
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                lp1.setMargins(60,15,20,10);
                rg.setLayoutParams(lp1);
                linearLayout.addView(rg);
                //lp.setMargins(20,5,20,5);
                linearLayout.setLayoutParams(lp);
                card.setRadius(10);
                card.addView(linearLayout);
                bv.addView(card);
                oj.setView(bv);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton
                                r
                                = (RadioButton) group
                                .findViewById(checkedId);
                        selectedtext= r.getText().toString();
                    }
                });

                oj.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if (selectedtext.matches("") || selectedtext.matches("select state")){
                                Toast.makeText(editprofile.this, "Please select state ", Toast.LENGTH_SHORT).show();
                            }else{
                                setupdate(selectedtext,7);
                            }
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


            }
        });
        local1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oj=new AlertDialog.Builder(editprofile.this);

                oj.setTitle("Change Address !");
                CardView card=new CardView(editprofile.this);
                LinearLayout linearLayout = new LinearLayout(editprofile.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                lp1.setMargins(60,15,20,10);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                //lp.setMargins(20,5,20,5);
                linearLayout.setLayoutParams(lp);


                EditText pass=new EditText(editprofile.this);
                pass.setLayoutParams(lp1);

                pass.setHint("Enter Address");

                linearLayout.addView(pass);
                card.addView(linearLayout);
                oj.setView(card);
                oj.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!pass.getText().toString().matches("")){
                            setupdate(pass.getText().toString(),6);
                        }
                        else{
                            Toast.makeText(editprofile.this, "Enter Address", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        lastname1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oj=new AlertDialog.Builder(editprofile.this);

                oj.setTitle("Change Last Name !");
                CardView card=new CardView(editprofile.this);
                LinearLayout linearLayout = new LinearLayout(editprofile.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                lp1.setMargins(60,15,20,10);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                //lp.setMargins(20,5,20,5);
                linearLayout.setLayoutParams(lp);


                EditText pass=new EditText(editprofile.this);
                pass.setLayoutParams(lp1);

                pass.setHint("Enter Last Name");

                linearLayout.addView(pass);
                card.addView(linearLayout);
                oj.setView(card);
                oj.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!pass.getText().toString().matches("")){
                            setupdate(pass.getText().toString(),5);
                        }
                        else{
                            Toast.makeText(editprofile.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        midname1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oj=new AlertDialog.Builder(editprofile.this);

                oj.setTitle("Change Middle Name !");
                CardView card=new CardView(editprofile.this);
                LinearLayout linearLayout = new LinearLayout(editprofile.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                lp1.setMargins(60,15,20,10);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                //lp.setMargins(20,5,20,5);
                linearLayout.setLayoutParams(lp);


                EditText pass=new EditText(editprofile.this);
                pass.setLayoutParams(lp1);

                pass.setHint("Enter Middle Name");

                linearLayout.addView(pass);
                card.addView(linearLayout);
                oj.setView(card);
                oj.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!pass.getText().toString().matches("")){
                            setupdate(pass.getText().toString(),4);
                        }
                        else{
                            Toast.makeText(editprofile.this, "Enter Middle Name", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        fisrname1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oj=new AlertDialog.Builder(editprofile.this);

                oj.setTitle("Change First Name !");
                CardView card=new CardView(editprofile.this);
                LinearLayout linearLayout = new LinearLayout(editprofile.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                lp1.setMargins(60,15,20,10);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                //lp.setMargins(20,5,20,5);
                linearLayout.setLayoutParams(lp);


                EditText pass=new EditText(editprofile.this);
                pass.setLayoutParams(lp1);

                pass.setHint("Enter First Name");

                linearLayout.addView(pass);
                card.addView(linearLayout);
                oj.setView(card);
                oj.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!pass.getText().toString().matches("")){
                            setupdate(pass.getText().toString(),3);
                        }
                        else{
                            Toast.makeText(editprofile.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        email1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oj=new AlertDialog.Builder(editprofile.this);

                oj.setTitle("Change Email !");
                CardView card=new CardView(editprofile.this);
                LinearLayout linearLayout = new LinearLayout(editprofile.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                lp1.setMargins(60,15,20,10);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                //lp.setMargins(20,5,20,5);
                linearLayout.setLayoutParams(lp);


                EditText pass=new EditText(editprofile.this);
                pass.setLayoutParams(lp1);
                pass.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS );
                pass.setHint("Enter New Email Id");

                linearLayout.addView(pass);
                card.addView(linearLayout);
                oj.setView(card);
                oj.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!pass.getText().toString().matches("")){
                            setupdate(pass.getText().toString(),2);
                        }
                        else{
                            Toast.makeText(editprofile.this, "Enter Email Id", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        phone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oj=new AlertDialog.Builder(editprofile.this);

                oj.setTitle("Change Phone Number !");
                CardView card=new CardView(editprofile.this);
                LinearLayout linearLayout = new LinearLayout(editprofile.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                lp1.setMargins(60,15,20,10);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                //lp.setMargins(20,5,20,5);
                linearLayout.setLayoutParams(lp);


                EditText pass=new EditText(editprofile.this);
                pass.setLayoutParams(lp1);
                pass.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                pass.setHint("Enter New Phone Number");

                linearLayout.addView(pass);
                card.addView(linearLayout);
                oj.setView(card);
                oj.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!pass.getText().toString().matches("")){
                            setupdate(pass.getText().toString(),1);
                        }
                        else{
                            Toast.makeText(editprofile.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });



        pass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oj=new AlertDialog.Builder(editprofile.this);

                oj.setTitle("Change Password !");
                CardView card=new CardView(editprofile.this);
                LinearLayout linearLayout = new LinearLayout(editprofile.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                lp1.setMargins(60,15,20,10);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                //lp.setMargins(20,5,20,5);
                linearLayout.setLayoutParams(lp);
                EditText current=new EditText(editprofile.this);
                current.setLayoutParams(lp1);
                current.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                current.setHint("Enter Current Password");

                EditText pass=new EditText(editprofile.this);
                pass.setLayoutParams(lp1);
                pass.setHint("Enter New Password");
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                EditText conf=new EditText(editprofile.this);
                conf.setLayoutParams(lp1);
                conf.setHint("Confirm New Password");

                TextView infopass=new TextView(editprofile.this);
                infopass.setLayoutParams(lp1);
                infopass.setTextColor(Color.GRAY);

                infopass.setText("");
                infopass.setVisibility(View.GONE);
                TextView info=new TextView(editprofile.this);
                info.setLayoutParams(lp1);
                info.setTextColor(Color.GRAY);
                info.setVisibility(View.GONE);
                info.setText("");


                linearLayout.addView(current);
                linearLayout.addView(pass);
                linearLayout.addView(infopass);
                linearLayout.addView(conf);
                linearLayout.addView(info);

                conf.setVisibility(View.GONE);
                pass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() <8)
                        {
                            infopass.setVisibility(View.VISIBLE);
                            infopass.setText("Atleast 8 Characters with Atleast One Digit, One of #,$,&,^,%,*,?,!,@");
                            conf.setEnabled(false);
                        }
                        else{
                            my_application application = (my_application) getApplication();

                            String status=application.check_signup_password(s.toString().trim(), editprofile.this) ;

                            infopass.setText(status);
                            conf.setEnabled(true);


                        }


                        if (s.length() >0){
                            conf.setVisibility(View.VISIBLE);
                        }else{conf.setVisibility(View.GONE);
                        infopass.setVisibility(View.GONE);}
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                conf.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String psd=pass.getText().toString();
                        if (psd.length()>0 && psd.matches(s.toString())){
                            info.setVisibility(View.VISIBLE);
                            info.setText("Matched !");
                        }else {
                            info.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                card.addView(linearLayout);
                oj.setView(card);
                oj.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (current.getText().toString().matches(user_singleton.getInstance().getpassword())){
                            if(pass.getText().toString().matches(conf.getText().toString())){
                                my_application application = (my_application) getApplication();
                                String statuss=application.check_signup_password(pass.getText().toString().trim(), editprofile.this) ;
                                if (statuss.matches("perfect !")){
                                    setupdate(pass.getText().toString(),9);
                                }else{
                                    Toast.makeText(editprofile.this, statuss, Toast.LENGTH_SHORT).show();
                                }
                            }else{
                            Toast.makeText(editprofile.this, "Password does not match", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(editprofile.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });
        getinfo();
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
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {getimage();}else{
                if (checking_permision()   ){
                    getimage();

                }}
            }
        });
    }
    void checkdistrict(String states){

    }

    private void setupdate(String toString, int i) {
        try{
        JSONObject jo = new JSONObject();
        try {

            jo.put("str", toString);
            jo.put("type", i);
            jo.put("user_id", user_singleton.getInstance().getUser_id());

        } catch (JSONException E) {

        }
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/setupdate_profile")
                .addJSONObjectBody(jo)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        boolean td=false;
                        try {
                            td=jsonObject.getBoolean("result");
                            if (td) {
                                Toast.makeText(editprofile.this, "Updated", Toast.LENGTH_SHORT).show();
                                if (i == 1) {
                                    phone.setText(toString);
                                }
                                if (i == 2) {
                                    email.setText(toString);
                                }
                                if (i == 3) {
                                    fisrname.setText(toString);
                                }
                                if (i == 4) {
                                    midname.setText(toString);
                                }
                                if (i == 5) {
                                    lastname.setText(toString);
                                }
                                if (i == 6) {
                                    local.setText(toString);
                                }
                                if (i == 7) {
                                    state.setText(toString);
                                    statecode=toString;
                                }
                                if (i == 8) {
                                    district.setText(toString);
                                }

                            }
                            else{Toast.makeText(editprofile.this, "Error !", Toast.LENGTH_SHORT).show();}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

        if (i==9){
            Map<String, Object> user = new HashMap<>();
            user.put("password", toString);
            db.collection("loginaidisc").document(user_singleton.getInstance().getfb_id())
                    .update(user);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getimage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(editprofile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {
                        Intent intent = new Intent(editprofile.this, CAM2.class);
                        startActivityForResult(intent, 4);
                    }else{
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
//                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        Intent intent = new Intent(editprofile.this, CAM2.class);
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

    public boolean checking_permision() {


        if ((ContextCompat.checkSelfPermission(editprofile.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(editprofile.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(editprofile.this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 4 ) {


                String url = data.getStringExtra("url");
                System.out.println(" url is:" + url);
                //imn.setImageURI(Uri.parse(url));
                Bitmap bitmap=null;
                try {
                    bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(url));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                image.setVisibility(View.VISIBLE);
//                upload.setVisibility(View.VISIBLE);
//                file_path = url;
                uploadprofileimg(Uri.parse(url),bitmap);


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
                //imn.setImageBitmap(thumbnail);
                //bitmaps.add(thumbnail);
                uploadprofileimg(selectedImage,thumbnail);
            }
            else {
                Toast.makeText(editprofile.this, "No image selected", Toast.LENGTH_LONG).show();


            }
        }
    }

    private void uploadprofileimg(Uri urid,Bitmap bitmap) {
        try {
            File compressedImageFile = new Compressor(editprofile.this).compressToFile(FileUtils.from(editprofile.this, urid));
            String imb = compressedImageFile.getAbsolutePath();
            Data data = new Data.Builder()
                    .putString("url", Uri.fromFile(new File(imb)).toString())
                    .putString("id", user_singleton.getInstance().getUser_id()).putString("user_name", user_singleton.getInstance().getUser_name())
                    .build();

            OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(Uploadingworker_profileimg.class)
                    .setInputData(data).build();
            image1.setText("uploading...");

            mWorkManager.enqueue(mRequest);

            LiveData<WorkInfo> liveData = mWorkManager.getWorkInfoByIdLiveData(mRequest.getId());

            liveData.observe((LifecycleOwner) editprofile.this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(@Nullable WorkInfo workInfo) {
                    if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        img.setImageURI(Uri.fromFile(new File(imb)));
                        image1.setText("Edit");
                        Toast.makeText(editprofile.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                    if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED) {
                        Toast.makeText(editprofile.this, "Error", Toast.LENGTH_SHORT).show();
                        image1.setText("Edit");
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }




    private void getinfo() {
        JSONObject jo = new JSONObject();
        try {

            jo.put("user_id", user_singleton.getInstance().getUser_id());

        } catch (JSONException E) {

        }
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/geteditfile")
                .addJSONObjectBody(jo)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            midname.setText(jsonObject.getString("middlename"));
                            state.setText(jsonObject.getString("state"));
                            statecode=jsonObject.getString("state");
                            district.setText(jsonObject.getString("district"));
                            local.setText(jsonObject.getString("address"));
                            String imh=jsonObject.getString("imagepath");
                            if (!imh.matches("")){
                                try {
                                    Glide.with(editprofile.this)
                                            .load("https://nibpp.krishimegh.in/Content/reportingbase/profile_image/" + imh+".jpg")
                                            .error(R.drawable.gray)
                                            .into(img);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            getting_state();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
    private void gosubmit(int i, String val) {
        Toast.makeText(editprofile.this, "Good", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(editprofile.this, "Signed out", Toast.LENGTH_LONG).show();
                        shared_pref.remove_shared_preference(editprofile.this);
                        Intent intent = new Intent(editprofile.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w("fff", "Error writing document", e);
                        Toast.makeText(editprofile.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(editprofile.this);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.about_us1:

                Intent intent = new Intent(editprofile.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.help1:

                Intent intent1 = new Intent(editprofile.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.disclaimer:

                Intent intent1q = new Intent(editprofile.this, disclaimer.class);
                startActivity(intent1q);



                break;
            case R.id.home:Intent ibtwd;
                if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("expert")){
                    ibtwd=new Intent(editprofile.this, expertprofile_fragment.class);
                }else{ ibtwd=new Intent(editprofile.this, farmersprofile_fragment1.class);}

                startActivity(ibtwd);

                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.editme:

                Intent ibt=new Intent(editprofile.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(editprofile.this, contributor.class);
                startActivity(ibt1);

                break;
            case R.id.history:

                Intent ibt1q=new Intent(editprofile.this, history.class);
                startActivity(ibt1q);

                break;
            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void getting_state()
    {

        AndroidNetworking.post("https://kvk.icar.gov.in/api/api/KMS/StateList")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {


                        //adapter_state.notifyDataSetChanged();


                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = (JSONObject) response.get(i);

                                String state_code = object.optString("Code");
                                String state_name_english = object.optString("EnglishName");

                                state_name_list.add(i + 1, state_name_english);
                                state_code_list.add(i + 1, state_code);

                            }
                            //adapter_state.notifyDataSetChanged();
                            try{
                                if(!statecode.matches("")){
                            int indexof=state_name_list.indexOf(statecode);
                            statecodeid=state_code_list.get(indexof);
                            getting_district(statecodeid);}}
                            catch (Exception e){}

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(editprofile.this, "Error", Toast.LENGTH_LONG).show();
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


                                //adapter_district.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(editprofile.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });
    }
}