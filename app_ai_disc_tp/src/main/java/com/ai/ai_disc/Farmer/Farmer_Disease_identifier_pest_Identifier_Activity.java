package com.ai.ai_disc.Farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.About_us1;

import com.ai.ai_disc.Login;

import com.ai.ai_disc.R;
import com.ai.ai_disc.Result_identify;
import com.ai.ai_disc.contributor;
import com.ai.ai_disc.create_report;
import com.ai.ai_disc.detection_instance;
import com.ai.ai_disc.disclaimer;
import com.ai.ai_disc.editprofile;
import com.ai.ai_disc.expertprofile_fragment;
import com.ai.ai_disc.farmerdiseasepestIdentify;
import com.ai.ai_disc.farmerdiseasepestIdentify1;
import com.ai.ai_disc.farmersprofile_fragment1;
import com.ai.ai_disc.history;
import com.ai.ai_disc.history_page;
import com.ai.ai_disc.needHelp;
import com.ai.ai_disc.shared_pref;
import com.ai.ai_disc.user_singleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Farmer_Disease_identifier_pest_Identifier_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    CardView diseaseLayout, pestLayout;
    String crop = "", type = "";
    int priority;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String farmerIdentificationString = "";
    TextView loct;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_diseaseidentifier_pest_identifer);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("AI-DISC");
        setNavigationViewListener();
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
        try{userd.setText(user_singleton.getInstance().getfname()+" "+user_singleton.getInstance().getMname()+" "+user_singleton.getInstance().getlname());
            acc.setText(user_singleton.getInstance().getUser_type()+" Account");
            loct.setText(user_singleton.getInstance().getloct());
            //Log.d("ggg",head.findViewById(R.id.vsr).toString());


        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("error",e.toString());
        }



        diseaseLayout = (CardView) findViewById(R.id.disease_layout);
        pestLayout = (CardView) findViewById(R.id.pest_layout);

        try{
            Intent intent = getIntent();
            Bundle data = intent.getExtras();
            crop = data.getString("crop");
            priority = data.getInt("number");

            farmerIdentificationString = data.getString("farmerIdentification");
        } catch (Exception e) {
            e.printStackTrace();
            try{
                crop = detection_instance.getInstance().getcrop();
                priority = detection_instance.getInstance().getnumb();

                farmerIdentificationString = detection_instance.getInstance().getdetection();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }


        TextView declareheading=findViewById(R.id.heading_dp);
        declareheading.setText("Crop: "+crop);

        diseaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priority==2) {
                    Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "No Disease Model !", Toast.LENGTH_SHORT).show();

                }else{
                type = "disease";
                gotoidentification(crop, type);}
            }
        });

        pestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priority==1) {
                    Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "No Pest Model !", Toast.LENGTH_SHORT).show();

                }else{type = "pest";
                gotoidentification(crop, type);}

            }
        });
        if (priority==1){
            //pestLayout.setEnabled(false);
//            Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "No Pest Model !", Toast.LENGTH_SHORT).show();
            //int colorId = inc_cards.get(i).inc_status;
            diseaseLayout.setCardBackgroundColor(Color.parseColor("#38704a"));
            pestLayout.setCardBackgroundColor(Color.GRAY);
        }
        else if (priority==2){
            //diseaseLayout.setEnabled(false);
            pestLayout.setCardBackgroundColor(Color.parseColor("#38704a"));
            diseaseLayout.setCardBackgroundColor(Color.GRAY);
        }
        else {
            pestLayout.setCardBackgroundColor(Color.parseColor("#38704a"));
            diseaseLayout.setCardBackgroundColor(Color.parseColor("#38704a"));
        }

    }
    @Override
    public void onBackPressed()
    {
        if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("farmer")){

            Intent gotopage =new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, farmersprofile_fragment1.class);
            startActivity(gotopage);finish();}
        else{
            Intent gotopage =new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, expertprofile_fragment.class);
            startActivity(gotopage);finish();
        }


       /* Intent intent=new Intent(Result_identify.this, Identify_disease.class);
        startActivity(intent);*/
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

                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.help1:

                Intent intent1 = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, needHelp.class);
                startActivity(intent1);

                break;
            case R.id.home:

                Intent ibtwd;
                if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("expert")){
                    ibtwd=new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, expertprofile_fragment.class);
                }else{ ibtwd=new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, farmersprofile_fragment1.class);}
                startActivity(ibtwd);

                break;
            case R.id.disclaimer:

                Intent intent1q = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, disclaimer.class);
                startActivity(intent1q);



                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.editme:

                Intent ibt=new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, contributor.class);
                startActivity(ibt1);

                break;
            case R.id.history:

                Intent ibt1q=new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, history.class);
                startActivity(ibt1q);

                break;
            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void gotoidentification(String crp, String typ){
        String cropprediction=typ+"_detection_"+crp.replace(" ", "");;
        Intent gotopage =new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, farmerdiseasepestIdentify.class);
        gotopage.putExtra("crop",crp);
        gotopage.putExtra("type",typ);
        gotopage.putExtra("url",cropprediction.toLowerCase());
        gotopage.putExtra("farmerIdentification","Farmer_prediction");
        //Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, cropprediction, Toast.LENGTH_SHORT).show();
        detection_instance.getInstance().settype(typ);
        detection_instance.getInstance().seturl(cropprediction.toLowerCase());
        startActivity(gotopage);
    }

//    public void checkDisease_pest(String crop, String type) {
//
//        if (type.equals("pest"))
//        {
//
//            if (crop.equalsIgnoreCase(Identify_disease.maize_model1))
//            {
//           /*     Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
//                 Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Model 2 ,Maize", Toast.LENGTH_LONG).show();
//
//            } /*else if (crop.equalsIgnoreCase(Identify_disease.maize_model2)) {
//
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            }*/
//            else if (crop.equalsIgnoreCase(Identify_disease.wheat))
//            {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.tomato)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.cotton)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.mustard)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.rice))
//            {
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//
///*
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//*/
//
//               // Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.potato)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.citus_mandarin)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.grapes)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.soybean)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.chickpea)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.groundnut))
//            {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            }
//            else if (crop.equalsIgnoreCase(Identify_disease.datepalm))
//            {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.coriander)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.chilli)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Fenugreek)) {
//                // below 97% case
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//             /*   Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
//            } else if (crop.equalsIgnoreCase(Identify_disease.Kingchilli))
//            {
//               /* Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Peach))
//            {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            }
//            else if (crop.equalsIgnoreCase(Identify_disease.Greengram))
//            {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            }
//            else if (crop.equalsIgnoreCase(Identify_disease.Cucurbits)) {
//        /*        Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Assamlemon)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Lentil)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Mothbean)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Okra)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Clusterbean)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Walnut)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Apple))
//            {
//                // below 97% case
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.CitrusKinnow)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Opiumpoppy)) {
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Sugercane)) {
//             /*   Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Cowpea)) {
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//
//          /*      Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
//            }
//        } else if (type.equals("disease"))
//        {
//
//            if (crop.equalsIgnoreCase(Identify_disease.maize_model1))
//            {
//                //avl
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//                // Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Model 2 ,Maize", Toast.LENGTH_LONG).show();
//
//            } /*else if (crop.equalsIgnoreCase(Identify_disease.maize_model2)) {
//
//
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Not available", Toast.LENGTH_LONG).show();
//            }*/ else if (crop.equalsIgnoreCase(Identify_disease.wheat))
//            {
////avl
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//            } else if (crop.equalsIgnoreCase(Identify_disease.tomato)) {
//                //avl
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
////marked
//             //   Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.cotton))
//            {
////avl
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
////marked
//               // Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.mustard)) {
////avl
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
////marked
//             //   Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.rice))
//            {
////avl
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
//            } else if (crop.equalsIgnoreCase(Identify_disease.potato)) {
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//              /*
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
//
//            } else if (crop.equalsIgnoreCase(Identify_disease.citus_mandarin)) {
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
//            } else if (crop.equalsIgnoreCase(Identify_disease.grapes)) {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
//            }
//            else if (crop.equalsIgnoreCase(Identify_disease.soybean)) {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//            }
//            else if (crop.equalsIgnoreCase(Identify_disease.chickpea)) {
////marked
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            }
//            else if (crop.equalsIgnoreCase(Identify_disease.groundnut)) {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
//            } else if (crop.equalsIgnoreCase(Identify_disease.datepalm))
//            {
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//            } else if (crop.equalsIgnoreCase(Identify_disease.coriander))
//            {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
//            } else if (crop.equalsIgnoreCase(Identify_disease.chilli)) {
//
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//
////marked
//             //  Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Fenugreek)) {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//            } else if (crop.equalsIgnoreCase(Identify_disease.Kingchilli)) {
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
////marked
//             //   Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Peach)) {
//               /* Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
////marked
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Greengram)) {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
//            } else if (crop.equalsIgnoreCase(Identify_disease.Cucurbits)) {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
//            } else if (crop.equalsIgnoreCase(Identify_disease.Assamlemon)) {
////marked
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Lentil)) {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
//            } else if (crop.equalsIgnoreCase(Identify_disease.Mothbean))
//            {
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
///*
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
//            } else if (crop.equalsIgnoreCase(Identify_disease.Okra))
//            {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//            }
//            else if (crop.equalsIgnoreCase(Identify_disease.Clusterbean))
//            {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
//            } else if (crop.equalsIgnoreCase(Identify_disease.Walnut))
//            {
//              /*  Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
////marked
//               Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Apple)) {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
////marked
//           //     Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.CitrusKinnow)) {
//
//               /* Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);*/
////marked
//                Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Opiumpoppy)) {
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
////marked
//            //    Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Module under construction", Toast.LENGTH_LONG).show();
//            } else if (crop.equalsIgnoreCase(Identify_disease.Sugercane)) {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//
//            } else if (crop.equalsIgnoreCase(Identify_disease.Cowpea)) {
//
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Identify_disease.class);
//                intent.putExtra("crop", crop);
//                intent.putExtra("farmerIdentification", farmerIdentificationString);
//                intent.putExtra("type", type);
//                startActivity(intent);
//            }
//
//
//
//        }
//
//
//    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case android.R.id.home:
//
//                finish();
//
//                break;
//            case R.id.logout:
//
//                signout();
//                break;
//
//            default:
//                break;
//
//        }
//
//        return true;
//    }

//    private void signout() {
//        AlertDialog.Builder opt = new AlertDialog.Builder(Farmer_Disease_identifier_pest_Identifier_Activity.this);
//        opt.setTitle("Are you sure ?");
//        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Login.class);
//                startActivity(intent);
//                finish();
//
//                shared_pref.remove_shared_preference(Farmer_Disease_identifier_pest_Identifier_Activity.this);
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
                    Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(Farmer_Disease_identifier_pest_Identifier_Activity.this);
                    Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.w("fff", "Error writing document", e);
                    Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                }
            });
}
    private void signout() {
        android.app.AlertDialog.Builder opt = new AlertDialog.Builder(Farmer_Disease_identifier_pest_Identifier_Activity.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(Farmer_Disease_identifier_pest_Identifier_Activity.this);
                    Intent intent = new Intent(Farmer_Disease_identifier_pest_Identifier_Activity.this, Login.class);
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

}