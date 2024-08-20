package com.ai.ai_disc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Farmer.FarmerSendQueryCropListActivity;
import com.ai.ai_disc.Farmer.QueryDetails1;
import com.ai.ai_disc.Farmer.json_new_submit_query_asyntask;
import com.ai.ai_disc.Farmer.list_of_query;
import com.ai.ai_disc.Farmer.send_query2_layout;
import com.ai.ai_disc.Videoconference.VCActivity;
import com.ai.ai_disc.model.Management_Practices_Disease;
import com.ai.ai_disc.model.MultiChatFarmerExpertQuery_Model;
import com.ai.ai_disc.model.appuse_response;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener;
import com.bumptech.glide.Glide;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class MultiChat_FarmerExpertQuery_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";
    String  AUTH_KEY_FCM="";
    ImageView query_ImageView;
    TextView farmerNameTextView, query_DescriptionTextView,time_text;
    RecyclerView chatRecyclerView;
    ImageButton upload_Image;
    ImageView sendButton;
    int number_image = 0;
    EditText description;
    LinearLayout layout_add_image_etc;
    String farmerName,desc="", imagePath="",farmerId1="",expertId1="",queryId1,cropId_Get1="",userType1="",problem="",sol="",crop="";
    MultiChatFarmerExpertQuery_Model model;
    InputStream inputstream2 = null;
    String[] image_list = new String[1];
    ProgressDialog progressDialog_at_send_query;
    customAdapterMultiChatFarmerExpertQuery ca;
    LinearLayout linearLayout;
    ImageView vc;
    ImageView ref,clo;
    String status_run;
    TextView strun;
    InputStream inputstream = null;
    String app_id="";
    String slot="";
    String dated="";
    String scoded="";
    int geocoderMaxResults = 1;
    TextView loct;
    boolean onoff;
    int not;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    static String[] image_data = new String[1];
    List<MultiChatFarmerExpertQuery_Model> multiChatFarmerExpertQuery_modelsList;
    View view;
    private String[] slots=new String[]{"10:00",
            "10:30",
            "11:00",
            "11:30",
            "12:00",
            "12:30",
            "14:30",
            "15:00",
            "15:30",
            "16:00",
            "16:30","17:00","17:30"};
    private ArrayList<String> exp_token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_askcommunity);
        setTitle("AI-DISC");
        not=0;
        getWindow().setSoftInputMode(1);
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
        try{userd.setText(user_singleton.getInstance().getfname()+" "+user_singleton.getInstance().getMname()+" "+user_singleton.getInstance().getlname());
            acc.setText(user_singleton.getInstance().getUser_type()+" Account");
            loct.setText(user_singleton.getInstance().getloct());
            //Log.d("ggg",head.findViewById(R.id.vsr).toString());


        } catch (Exception e) {
            e.printStackTrace();
           // Log.d("error",e.toString());
        }

        onoff=false;
        ImageView slide=findViewById(R.id.vcop);
        view=findViewById(R.id.viewslide);
        //view.setVisibility(View.GONE);

        slide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if(!onoff){

//                    TranslateAnimation animate = new TranslateAnimation(
//                            +view.getWidth()+100,
//                            0 ,
//                            0,
//                            0);
//                    animate.setDuration(500);
//                    animate.setFillAfter(true);
//                    view.startAnimation(animate);
                    view.setVisibility(View.VISIBLE);
                    slide.setImageDrawable(getResources().getDrawable(R.drawable.dropdown2, getTheme()));
                } else {
                    //view.setVisibility(View.INVISIBLE);
//                    TranslateAnimation animate = new TranslateAnimation(
//                            0,
//                            +view.getWidth()+100,
//                            0,
//                            0);
//                    animate.setDuration(500);
//                    animate.setFillAfter(true);
//                    view.startAnimation(animate);
                    slide.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getTheme()));
                    view.setVisibility(View.GONE);
                }
                onoff = !onoff;
            }
        });
        query_ImageView = (ImageView) findViewById(R.id.image);
        farmerNameTextView = (TextView) findViewById(R.id.farmer_name);
        time_text=(TextView)findViewById(R.id.time_text);
        linearLayout=(LinearLayout)findViewById(R.id.reply_ByScientist_Layout);
        query_DescriptionTextView = (TextView) findViewById(R.id.query_description);
        chatRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setHasFixedSize(true);
        strun=findViewById(R.id.status_run);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MultiChat_FarmerExpertQuery_Activity.this, 1);
        chatRecyclerView.setLayoutManager(layoutManager);
        layout_add_image_etc = (LinearLayout) findViewById(R.id.linear_layout_image_video_audio);
        upload_Image = (ImageButton) findViewById(R.id.upload_img);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        description=(EditText)findViewById(R.id.editText_enterMessage);
        multiChatFarmerExpertQuery_modelsList=new ArrayList<>();
        vc=findViewById(R.id.call);
        clo=findViewById(R.id.close);
        ref=findViewById(R.id.refresh);
        desc="";
        imagePath="";
        farmerId1="";
        expertId1="";
        cropId_Get1="";
        userType1="";
        farmerName="";
        queryId1="";

        try{
            queryId1=notify_singleton.getInstance().getnot();
            notify_singleton.getInstance().setnot("");
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (queryId1==null ){
            queryId1="";
        }

        if (queryId1.matches("") ){

        try {
            Intent intent = getIntent();
            Bundle args = intent.getBundleExtra("bundle");
            desc = args.getString("desc");
            imagePath = args.getString("imagePath");
            sol = args.getString("solution");
            farmerId1 = args.getString("farmerId");
            expertId1 = args.getString("expertId");
            queryId1 = args.getString("queryId");
            cropId_Get1 = args.getString("cropId");
            userType1 = args.getString("userType");
            farmerName = args.getString("farmerName");
            crop = args.getString("crop");
            status_run=args.getString("status_run");
        } catch (Exception e) {
            e.printStackTrace();
            try{
                desc = chat_singleton.getInstance().getchat().getDescription();
                imagePath = chat_singleton.getInstance().getchat().getImagePath();
                //Log.d("bbbb",imagePath)
                sol = chat_singleton.getInstance().getchat().getQuery_resolution();
                farmerId1 = chat_singleton.getInstance().getchat().getUser_id();
                expertId1 = chat_singleton.getInstance().getchat().getExpertId();
                queryId1 = chat_singleton.getInstance().getchat().getQuery_id();
                cropId_Get1 = chat_singleton.getInstance().getchat().getCropId();
                userType1 = chat_singleton.getInstance().getchat().getUserType();
                farmerName = chat_singleton.getInstance().getchat().getFirstName();
                crop = chat_singleton.getInstance().getchat().getcrop();
                status_run=chat_singleton.getInstance().getchat().getstatus_run();
            } catch (Exception exception) {
                exception.printStackTrace();

            }
        }
        setpro();
        }
        else{
            getrecord(queryId1);
        }

        // queryDetailsList = (ArrayList<QueryDetails>) args.getParcelable("queryList");





//
//
//        Log.d("imagePath",imagePath);
//        Log.d("desc",desc);
//        Log.d("farmerName",farmerName);
//        Log.d("farmerId",farmerId1);
//        Log.d("expertId",expertId1);
//        Log.d("queryId",queryId1);
//        Log.d("cropId_Get",cropId_Get1);
//        Log.d("userType",userType1);



        ca=new customAdapterMultiChatFarmerExpertQuery(MultiChat_FarmerExpertQuery_Activity.this,multiChatFarmerExpertQuery_modelsList);
        chatRecyclerView.setAdapter(ca);
        multiChatGetValue();

        upload_Image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                check_permissions();
                if (number_image < 1)
                {
                    // Toast.makeText(send_query2_layout.this, "select picture", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    // System.out.println("q.3.2");
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    // System.out.println("q.3.3");
                    startActivityForResult(Intent.createChooser(intent, "select picture"), 1);

                } else
                {
                    Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "", Toast.LENGTH_LONG).show();
                }
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressDialog_at_send_query = new ProgressDialog(MultiChat_FarmerExpertQuery_Activity.this);
                progressDialog_at_send_query.setCancelable(false);
                progressDialog_at_send_query.setMessage("Send Query");
                progressDialog_at_send_query.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog_at_send_query.show();

                if (!description.getText().toString().isEmpty() )
                {

                    problem = description.getText().toString();

                /*    for (int i = 0; i < image_list.length; i++)
                    {
                        System.out.println("image is:" + image_list[i]);
                        if (!image_list[i].isEmpty())
                        {


                            Uri address_to_convert = Uri.parse(image_list[i]);


                            try {
                                inputstream = getContentResolver().openInputStream(address_to_convert);

                                int length_image = 0;

                                length_image = inputstream.available();
                                System.out.println("  length:" + length_image);
                                byte[] data_in_byte_image = new byte[length_image];
                                inputstream.read(data_in_byte_image);
                                inputstream.close();
                                String image_in_string = Base64.encodeToString(data_in_byte_image, Base64.DEFAULT);
                                image_data[i] = image_in_string;
                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                        }


                    }*/



                    shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);

                    Json_new_submit_query json_new_submit = new Json_new_submit_query(MultiChat_FarmerExpertQuery_Activity.this, progressDialog_at_send_query);
                    json_new_submit.execute(farmerId1,queryId1,expertId1,cropId_Get1,userType1,problem);
                } else
                {

                     if(description.getText().toString().isEmpty())
                    {
                        Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Please write the query", Toast.LENGTH_LONG).show();

                    }

                    progressDialog_at_send_query.dismiss();
                    sendButton.setEnabled(true);
                }
            }
        });
        vc.setEnabled(true);
        //Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this,"MUlti",Toast.LENGTH_LONG).show();

        vc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onoff=false;
////                TranslateAnimation animate = new TranslateAnimation(
////                        0,
////                        +view.getWidth()+100,
////                        0,
////                        0);
////                animate.setDuration(500);
////                animate.setFillAfter(true);
////                view.startAnimation(animate);
//                view.setVisibility(View.GONE);
//                slide.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getTheme()));


                if (imagePath.isEmpty()||desc.isEmpty()){
                    Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this,"Upload Image and description first",Toast.LENGTH_SHORT).show();
                }
                else{
                    videoapp();
                }

            }
        });
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onoff=false;
////                TranslateAnimation animate = new TranslateAnimation(
////                        0,
////                        +view.getWidth()+100,
////                        0,
////                        0);
////                animate.setDuration(500);
////                animate.setFillAfter(true);
////                view.startAnimation(animate);
//                view.setVisibility(View.GONE);
//                slide.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getTheme()));
                multiChatGetValue1();
            }
        });
        clo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onoff=false;
////                TranslateAnimation animate = new TranslateAnimation(
////                        0,
////                        +view.getWidth()+100,
////                        0,
////                        0);
////                animate.setDuration(500);
////                animate.setFillAfter(true);
////                view.startAnimation(animate);
//                view.setVisibility(View.GONE);
//                slide.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getTheme()));

                    closechat();


            }
        });
        getchatter();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("chat_refresh_message"));
    }
    void setpro(){
        if (status_run.matches("1")){
            strun.setText("Closed");
            strun.setTextColor(Color.parseColor("#c42721"));
            upload_Image.setEnabled(false);
            vc.setEnabled(false);
            clo.setEnabled(false);
            ref.setEnabled(false);
            description.setEnabled(false);
            sendButton.setEnabled(false);
        }else{
            strun.setText("Active");
            upload_Image.setEnabled(true);
            vc.setEnabled(true);
            clo.setEnabled(true);
            sendButton.setEnabled(true);
            ref.setEnabled(true);
            description.setEnabled(true);
            strun.setTextColor(Color.parseColor("#169B89"));
        }
        if (!imagePath.isEmpty())
        {

            Glide.with(this)
                    .load(imagePath)
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(query_ImageView);
        }
        else
        {
            query_ImageView.setVisibility(View.GONE);
        }
        if (!desc.isEmpty())
        {
            query_DescriptionTextView.setText(desc);
        }
        query_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imagePath.isEmpty()) {
                    AlertDialog.Builder ob = new AlertDialog.Builder(MultiChat_FarmerExpertQuery_Activity.this);
                    LinearLayout nb = new LinearLayout(MultiChat_FarmerExpertQuery_Activity.this);
                    ImageView img = new ImageView(MultiChat_FarmerExpertQuery_Activity.this);

                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    nb.setLayoutParams(param);
                    img.setLayoutParams(param);
                    img.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(MultiChat_FarmerExpertQuery_Activity.this)
                            .load(imagePath)
                            .placeholder(R.drawable.facilities)
                            .error(R.drawable.gray)
                            .into(img);
                    nb.addView(img);
                    ob.setView(nb);
                    ob.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ob.show();
                }
            }
        });

        farmerNameTextView.setText(farmerName);
    }
    public BroadcastReceiver mMessageReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int signal = intent.getIntExtra("position", 0);
            String qu = intent.getStringExtra("query");
            if (signal==1 && qu.matches(queryId1)){
                multiChatGetValue1();
               // Log.d("someone","texted");
            }

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void getrecord(String rc){
    AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/query_user_not?user_id=" + rc)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                     sol = response.getString("Query_resolution");
                     //queryStatus = response.getString("Query_status");
                        expertId1 = response.getString("expert_id");

                     imagePath= response.getString("image_1");
                     //language = response.getString("language");
                     desc = response.getString("desc");
                        farmerId1 = response.getString("user_id");

                        farmerName=response.getString("firstName");
                        userType1=response.getString("userType");
                        cropId_Get1=response.getString("crop_id");
                     status_run=response.getString("status_run");
                     crop=response.optString("crop_name");
                        setpro();
                        QueryDetails1 queryDetails=new QueryDetails1();
                        queryDetails.setQuery_id(rc);
                        queryDetails.setQuery_resolution(sol);
                        queryDetails.setQueryStatus("queryStatus");
                        queryDetails.setExpertId(expertId1);
                        queryDetails.setImagePath(imagePath);
                        queryDetails.setLanguage("language");
                        queryDetails.setDescription(desc);
                        queryDetails.setUser_id(farmerId1);
                        queryDetails.setFirstName(farmerName);
                        queryDetails.setUserType(userType1);
                        queryDetails.setCropId(cropId_Get1);
                        queryDetails.setstatus_run(status_run);
                        queryDetails.setcrop(crop);
                        chat_singleton.getInstance().setchat(queryDetails);
                    } catch (JSONException e) {
                    e.printStackTrace();
                    setpro();
                }
                }

                @Override
                public void onError(ANError anError) {
                setpro();
                }
            });
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

                Intent intent = new Intent(MultiChat_FarmerExpertQuery_Activity.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.help1:

                Intent intent1 = new Intent(MultiChat_FarmerExpertQuery_Activity.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.home:

                Intent ibtwd=new Intent(MultiChat_FarmerExpertQuery_Activity.this, farmersprofile_fragment1.class);
                startActivity(ibtwd);

                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.disclaimer:

                Intent intent1q = new Intent(MultiChat_FarmerExpertQuery_Activity.this, disclaimer.class);
                startActivity(intent1q);



                break;
            case R.id.editme:

                Intent ibt=new Intent(MultiChat_FarmerExpertQuery_Activity.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(MultiChat_FarmerExpertQuery_Activity.this, contributor.class);
                startActivity(ibt1);

                break;
            case R.id.history:

                Intent ibt1q=new Intent(MultiChat_FarmerExpertQuery_Activity.this, history.class);
                startActivity(ibt1q);

                break;

            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(MultiChat_FarmerExpertQuery_Activity.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(MultiChat_FarmerExpertQuery_Activity.this);
                    Intent intent = new Intent(MultiChat_FarmerExpertQuery_Activity.this, Login.class);
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
                        Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Signed out", Toast.LENGTH_LONG).show();
                        shared_pref.remove_shared_preference(MultiChat_FarmerExpertQuery_Activity.this);
                        Intent intent = new Intent(MultiChat_FarmerExpertQuery_Activity.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w("fff", "Error writing document", e);
                        Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    void sendmsg(String msg){
       // Log.d("lllllllll", exp_token.toString());
        if (!exp_token.isEmpty()  ){
            JSONArray tokens = new JSONArray();
            for (int i =0;i<exp_token.size();i+=1){
                tokens.put(exp_token.get(i).trim());
            }
            JSONObject body = new JSONObject();
            JSONObject data1 = new JSONObject();

            try {
                data1.put("request", "chat_farmer");
                data1.put("msg", msg);
                data1.put("report", queryId1);
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
           // Log.d("token not found", "onError: " );
        }
    }

    void getchatter(){
        JSONObject objectd = new JSONObject();
        try {
            objectd.put("Query_id", queryId1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        exp_token=new ArrayList<>();
        ArrayList<String>followed=new ArrayList<>();
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getchatresponder")
                .addJSONObjectBody(objectd)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = null;
                       // Log.d("hhh",response.toString());
                        try {
                            array = response.getJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {
                                String sf=(String) array.getString(i);
                                if (!sf.matches("")){
                                    followed.add((String) array.getString(i));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //followed.add(farmerName);

                        if (followed.size()!=0){

                            for (int i =0;i<followed.size();i+=1){
                                //Log.d("lllllllll1", followed.get(i));
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
                                                   // Log.d("homedata", "Error getting documents: ", task.getException());
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

    public class Json_new_submit_query extends AsyncTask<String,Void,String>
    {

        String value_returned;
        String id_returned="";
        Context context1;
        String user_id,farmerId,queryId,cropId,expertId,imagePath,queryResolution,queryStatus,userType;
        String organization_id;
        ProgressDialog progressDialog1;
        Button submit1;
        String image1_proper;
        JSONObject obj;
        public Json_new_submit_query(Context context, ProgressDialog progressDialog){
            context1=context;
            progressDialog1=progressDialog;

        }


        @Override
        protected String doInBackground(String... param)
        {


            try {
                farmerId="";
                queryId="";
                expertId="";
                cropId="";
                userType="";
                queryResolution="";


 //   Log.d("param",param.toString());
                farmerId=param[0];
                queryId=param[1];
                expertId =param[2];
                cropId=param[3];
                userType=param[4];
                queryResolution=param[5];
                multiChatFarmerExpertQuery_modelsList.clear();
                String image_1= image_list[0];

                if(image_1!=null)
                {
                    if(!image_1.isEmpty())
                    {
                        image1_proper=image_1.replaceAll("\n","");

                    }
                    else
                    {
                        image1_proper="";
                    }

                }
                else
                {
                    image1_proper="";
                }




               // multiChatFarmerExpertQuery_modelsList.clear();
                JSONObject jo = new JSONObject();
                try {


                    jo.put("user_type", userType);
                    jo.put("query_id", queryId1);
                    jo.put("crop_id", cropId);
                    if(userType.equals("10"))
                    {
                        jo.put("expert_id", "");
                        jo.put("farmer_id", farmerId);
                    }
                    if(userType.equals("7"))
                    {
                        jo.put("expert_id", expertId);
                        jo.put("farmer_id", "");
                    }


                    jo.put("image_path", "");
                    jo.put("query_resolution", queryResolution);
                    jo.put("query_status", "1");
                    jo.put("language_type", "English");

                   // Log.d("jsonData", jo.toString());



                } catch (JSONException E)
                {

                }

                AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Farmer_Expert_MultiQuery_Chat")
                        .addJSONObjectBody(jo)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {

                                try {

                                    for (int i = 0; i < response.length(); i++)
                                    {


                                        JSONObject object = (JSONObject) response.get(i);

                                        model = new MultiChatFarmerExpertQuery_Model();
                                        model.setQueryId(object.optString("Query_id"));
                                        model.setCropId(object.optString("crop_id"));
                                        model.setFarmerName(object.optString("farmer_name"));
                                        model.setFarmerAddress(object.optString("farmer_address"));
                                        model.setReply_ImagePath(object.optString("img_path "));
                                        model.setMessage(object.optString("message1"));
                                        model.setResult(object.optString("result1"));
                                        model.setUserType(object.optString("user_Type"));
                                        model.setReplyerName(object.optString("reply_Name"));
                                       /* model.setReplyerAddress(object.optString("replyt_Address"));*/
                                        model.setReplyQueryResolution(object.optString("reply_query_Resolution"));
                                        model.setReplyUserId(object.optString("reply_UserId"));
                                        model.setReply_expertId(object.optString("reply_expertId"));
                                        model.setReplyeQueryId(object.optString("reply_equeryId"));
                                        model.setReply_ImagePath(object.optString("reply_imagePath"));
                                        model.setReplerTime(object.optString("reply_time"));
                                        multiChatFarmerExpertQuery_modelsList.add(model);
                                        System.out.println("12345" + response.toString());


                                    }
                                    //Log.d("error11",model.getReplyeQueryId()+String.valueOf(multiChatFarmerExpertQuery_modelsList.size())+"ll");
                                    if(model.getReplyeQueryId().isEmpty() ||model.getReplyeQueryId().equals("null"))
                                    {
                                        //Log.d("error",model.getReplyeQueryId()+String.valueOf(response.length()));
                                    }

                                    if(multiChatFarmerExpertQuery_modelsList.size()>0)
                                    {
                                        //Log.d("errorvvv",model.getReplyeQueryId()+String.valueOf(multiChatFarmerExpertQuery_modelsList.size())+"vvv");
                                        multiChatGetValue1();

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {

                                Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Error", Toast.LENGTH_LONG).show();
                            }
                        });

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            sendmsg(queryResolution);

            return value_returned;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1.show();
        }

        @Override
        protected void onPostExecute(String result)
        {

            //ca.notifyDataSetChanged();
            progressDialog1.dismiss();
         //   submit1.setEnabled(true);



        }

    }

    public void multiChatGetValue()
    {

        multiChatFarmerExpertQuery_modelsList.clear();
        JSONObject jo = new JSONObject();
       // multiChatFarmerExpertQuery_modelsList.clear();
        try {



            jo.put("query_id", queryId1);



        } catch (JSONException E) {

        }
       // Log.d("bbn",jo.toString());

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Farmer_Expert_MultiQuery_Chat_GETVALUE")
                .addJSONObjectBody(jo)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String dats="";

                        try {
                            for (int i = 0; i < response.length(); i++) {


                                JSONObject object = (JSONObject) response.get(i);

                                model = new MultiChatFarmerExpertQuery_Model();
                                model.setQueryId(object.optString("Query_id"));
                                model.setCropId(object.optString("crop_id"));
                                model.setFarmerName(object.optString("farmer_name"));
                                model.setFarmerAddress(object.optString("farmer_address"));
                                model.setReply_ImagePath(object.optString("img_path "));
                                model.setMessage(object.optString("message1"));
                                model.setResult(object.optString("result1"));
                                model.setUserType(object.optString("user_Type"));
                                model.setReplyerName(object.optString("reply_Name"));
                               /* model.setReplyerAddress(object.optString("replyt_Address"));*/
                                model.setReplyQueryResolution(object.optString("reply_query_Resolution"));
                                model.setReplyUserId(object.optString("reply_UserId"));
                                model.setReply_expertId(object.optString("reply_expertId"));
                                model.setReplyeQueryId(object.optString("reply_equeryId"));
                                model.setReply_ImagePath(object.optString("reply_imagePath"));
                                String datedtimes=object.optString("reply_time");
                                if (!datedtimes.trim().isEmpty()){
                                    String dt=datedtimes.substring(0,11);
                                    String tm=datedtimes.substring(11,16);
                                    model.setReplerTime(tm);
                                    if (dt.matches(dats)){
                                        model.setreplerdated("");
                                    }else{
                                        dats=dt;
                                        model.setreplerdated(dt);
                                    }
                                }
                                else{
                                    model.setReplerTime(datedtimes);
                                }

                                multiChatFarmerExpertQuery_modelsList.add(model);
                                //Log.d("bb",model.toString());
                                //System.out.println("responseDataaaa" + response.toString());


                            }

                            if(multiChatFarmerExpertQuery_modelsList.size()>0)
                            {
                                if(model.getReplyeQueryId().isEmpty()||model.getReplyeQueryId().equals(""))
                                {
                                   // Log.d("error",model.getReplyeQueryId()+"kkkk");
                                    linearLayout.setVisibility(View.GONE);
                                }
                                else
                                {

                                   /* ca=new customAdapterMultiChatFarmerExpertQuery(MultiChat_FarmerExpertQuery_Activity.this,multiChatFarmerExpertQuery_modelsList);
                                    chatRecyclerView.setAdapter(ca);*/
                                   ca.notifyDataSetChanged();
                                   chatRecyclerView.setAdapter(ca);
                                    description.getText().clear();

                              /*  RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MultiChat_FarmerExpertQuery_Activity.this);
                                chatRecyclerView.setLayoutManager(mLayoutManager);
                                chatRecyclerView.setItemAnimator(new DefaultItemAnimator());*/

                                }

                              //  Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Submitted" + multiChatFarmerExpertQuery_modelsList.size(), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Error" + multiChatFarmerExpertQuery_modelsList.size(), Toast.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });




    }


    public void multiChatGetValue1()
    {

        multiChatFarmerExpertQuery_modelsList.clear();
        model=null;
        JSONObject jo = new JSONObject();
        // multiChatFarmerExpertQuery_modelsList.clear();
        try {



            jo.put("query_id", queryId1);



        } catch (JSONException E) {

        }

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Farmer_Expert_MultiQuery_Chat_GETVALUE")
                .addJSONObjectBody(jo)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String dats="";
                        try {
                            for (int i = 0; i < response.length(); i++) {


                                JSONObject object = (JSONObject) response.get(i);

                                model = new MultiChatFarmerExpertQuery_Model();
                                model.setQueryId(object.optString("Query_id"));
                                model.setCropId(object.optString("crop_id"));
                                model.setFarmerName(object.optString("farmer_name"));
                                model.setFarmerAddress(object.optString("farmer_address"));
                                model.setReply_ImagePath(object.optString("img_path "));
                                model.setMessage(object.optString("message1"));
                                model.setResult(object.optString("result1"));
                                model.setUserType(object.optString("user_Type"));
                                model.setReplyerName(object.optString("reply_Name"));
                                /* model.setReplyerAddress(object.optString("replyt_Address"));*/
                                model.setReplyQueryResolution(object.optString("reply_query_Resolution"));
                                model.setReplyUserId(object.optString("reply_UserId"));
                                model.setReply_expertId(object.optString("reply_expertId"));
                                model.setReplyeQueryId(object.optString("reply_equeryId"));
                                model.setReply_ImagePath(object.optString("reply_imagePath"));
                                String datedtimes=object.optString("reply_time");
                                String dt=datedtimes.substring(0,11);
                                String tm=datedtimes.substring(11,16);
                                model.setReplerTime(tm);
                                if (dt.matches(dats)){
                                    model.setreplerdated("");
                                }else{
                                    dats=dt;
                                    model.setreplerdated(dt);
                                }
                                multiChatFarmerExpertQuery_modelsList.add(model);

                            }
                            //Log.d("errorwww",model.getReplyeQueryId()+String.valueOf(multiChatFarmerExpertQuery_modelsList.size())+"ww");
                            if(multiChatFarmerExpertQuery_modelsList.size()>0)
                            {
                                if(model.getReplyeQueryId().isEmpty()||model.getReplyeQueryId().equals("null"))
                                {
                                    //Log.d("error",model.getReplyeQueryId());
                                    linearLayout.setVisibility(View.GONE);
                                }
                                else
                                {
                       /*             ca=new customAdapterMultiChatFarmerExpertQuery(MultiChat_FarmerExpertQuery_Activity.this,multiChatFarmerExpertQuery_modelsList);
                                    chatRecyclerView.setAdapter(ca);*/
                                    linearLayout.setVisibility(View.VISIBLE);
                                    ca.notifyDataSetChanged();
                                    chatRecyclerView.setAdapter(ca);
                                    description.getText().clear();


                                }


                            }
                            else
                            {
                                Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Error" + multiChatFarmerExpertQuery_modelsList.size(), Toast.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });




    }



    @Override
    public void onActivityResult(int requestcode, int responsecode, Intent data)
    {

        super.onActivityResult(requestcode, responsecode, data);
        switch (requestcode) {

            case 1:

                if (requestcode == 1 && responsecode == RESULT_OK && data != null) {

                    final Uri address = data.getData();
                    long file_length = check_length(address);

                    if (file_length < 8050)
                    {


                        LayoutInflater layoutinflater2 = (LayoutInflater) MultiChat_FarmerExpertQuery_Activity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View parent2 = layoutinflater2.inflate(R.layout.query_image_layout, null);
                        ImageView image = (ImageView) parent2.findViewById(R.id.image);
                        ImageButton remove_button = (ImageButton) parent2.findViewById(R.id.cancel);


                        Glide.with(MultiChat_FarmerExpertQuery_Activity.this)
                                .load(address)
                                .into(image);


                        layout_add_image_etc.addView(parent2);


                        number_image = number_image + 1;
                        add_image_address(address.toString());

                        remove_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layout_add_image_etc.removeView(parent2);
                                number_image = number_image - 1;
                                remove_image_address(address.toString());

                            }
                        });


                    } else {
                        // System.out.println(" more than 2 MB :");

                        Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "please choose less then 8mb size", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this,"error", Toast.LENGTH_LONG).show();
                }

                break;

            default:
                break;
        }
    }
    public long check_length(Uri address_of_file)
    {
        long length_file = 0;


        try {
            inputstream2 = getContentResolver().openInputStream(address_of_file);
            //  Toast.makeText(send_query2_layout.this,"checking size  ",Toast.LENGTH_LONG).show();


            length_file = inputstream2.available();
            //   System.out.println(" size is 1:"+length_file);
            length_file = length_file / 1024;
            // System.out.println(" size is  2:"+length_file);
            inputstream2.close();
            // return

        } catch (FileNotFoundException e) {
            //System.out.println(" file not found exception:");
            e.printStackTrace();
        } catch (IOException e) {
            // System.out.println(" io exception :");
            e.printStackTrace();
        }
        return length_file;

    }

    public void check_permissions() {

        if (ContextCompat.checkSelfPermission(MultiChat_FarmerExpertQuery_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MultiChat_FarmerExpertQuery_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MultiChat_FarmerExpertQuery_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(MultiChat_FarmerExpertQuery_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {


        }

    }


    public void add_image_address(String address_to_add) {

        //   System.out.println("inside  adding image ");

        // System.out.println(" value at 0:"+image_list[0]);
        // System.out.println(" value at 1:"+image_list[1]);
        // System.out.println(" value at 2:"+image_list[2]);

        for (int i = 0; i < image_list.length; i++) {
            if (image_list[i].isEmpty()) {
                //            System.out.println(" empty:"+i);
                image_list[i] = address_to_add;
                //          System.out.println(" not empty,now is:"+image_list[i]);
                return;
            }
        }


    }


    public void remove_image_address(String address_to_remove) {

        // System.out.println("inside  removing image ");
        // System.out.println(" value at 0:"+image_list[0]);
        // System.out.println(" value at 1:"+image_list[1]);
        //System.out.println(" value at 2:"+image_list[2]);

        for (int i = 0; i < image_list.length; i++) {
            if (image_list[i].equals(address_to_remove)) {
                //      System.out.println(" matching:"+i);
                image_list[i] = "";
                //    System.out.println(" set to empty :"+image_list[i]);
                return;
            }
        }

    }




    private void closechat(){
        AlertDialog.Builder opt = new AlertDialog.Builder(MultiChat_FarmerExpertQuery_Activity.this);
        opt.setTitle("Are you sure to close the chat ?");
        opt.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject object = new JSONObject();
                try {

                    object.put("queryId", queryId1);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/closechat")
                        .addJSONObjectBody(object)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                boolean res = false;
                                try {

                                    res = jsonObject.getBoolean("result");
                                    //Log.d(TAG, "onResponse: " + String.valueOf(res));
                                    if (res) {
                                        Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Chat is closed", Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(MultiChat_FarmerExpertQuery_Activity.this, list_of_query.class);
                                        startActivity(intent1);
                                        finish();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this,"Error !",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this,"Error !",Toast.LENGTH_SHORT).show();


                            }
                        });
            }
        });
        opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        opt.show();
    }

    private void videoapp(){


        JSONObject object = new JSONObject();
        try {

            object.put("query", queryId1);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getappointment")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Log.d("hhhhhhhh",jsonObject.toString());
                        int res=0;

                        try {
                             res = jsonObject.getInt("result");
                            app_id = jsonObject.getString("app_id");
                            slot = jsonObject.getString("slot");
                            dated = jsonObject.getString("dated");
                            scoded = jsonObject.getString("scode");

                            if (res==1000){
                                Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Service is deactivated", Toast.LENGTH_LONG).show();
                            }else{
                                SimpleDateFormat tms = new SimpleDateFormat("HH:mm");
                                SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yyyy");
                                Date strDate=null;
                                try {
                                    if (res!=1) {
                                        strDate = sdfs.parse(dated);
                                        Date nb = new Date();
                                        String formattedDate = sdfs.format(nb);
                                        String tmds = tms.format(nb);
                                        Date std = tms.parse(slots[Integer.parseInt(slot)]);
                                        Date std1 = tms.parse(slots[Integer.parseInt(slot) + 1]);
                                        Date bn = tms.parse(tmds);
                                        //Log.d("lll",bn.toString()+"   "+std.toString());
                                        Date xvnb=sdfs.parse(formattedDate);
                                        if (xvnb.equals(strDate)){

                                            if (bn.after(std1)) {
                                                res = 1;
                                            } else {
                                                if (bn.after(std)) {
                                                    res = 4;
                                                }
                                            }
                                        }else if(xvnb.after(strDate)){
                                            res=1;
                                        }

                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();

                                }

                            }
                                                    } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (res==1){
                            //take appoint
                            Intent nes=new Intent(MultiChat_FarmerExpertQuery_Activity.this,appointment.class);
                            Bundle args = new Bundle();
                            args.putString("crop",crop);
                            args.putString("crop_id",cropId_Get1);
                            args.putString("query",queryId1);
                            nes.putExtra("bundle",args);
                            startActivity(nes);


                        }else if (res==3){
                            AlertDialog.Builder opt = new AlertDialog.Builder(MultiChat_FarmerExpertQuery_Activity.this);
                            opt.setTitle("Pending Video Appointment  !");
                            opt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            opt.show();
                        }else if (res==2){
                            AlertDialog.Builder opt = new AlertDialog.Builder(MultiChat_FarmerExpertQuery_Activity.this);
                            opt.setTitle("Appointment is approved, pls wait for slot ").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();


                        }else { if (res==4){
                            AlertDialog.Builder opt = new AlertDialog.Builder(MultiChat_FarmerExpertQuery_Activity.this);
                            opt.setTitle("Approved Video Appointment ");
                            opt.setPositiveButton("Proceed to Video Call ?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent nes=new Intent(MultiChat_FarmerExpertQuery_Activity.this, VCActivity.class);


                                    nes.putExtra("query",queryId1);
                                    nes.putExtra("date",dated);
                                    nes.putExtra("slot",slot);
                                    nes.putExtra("app_id",app_id);


                                    nes.putExtra("scode",scoded);

                                    startActivity(nes);
                                }
                            });opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            opt.show();
                        }

                            else{
                            //Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mMessageReceiver);
        notify_singleton.getInstance().setmsg("0");
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.setPriority(2);
        notify_singleton.getInstance().setmsg("1");
        registerReceiver(mMessageReceiver, filter);
        super.onResume();
    }
}
