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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Farmer.Model_expert_query_content;
import com.ai.ai_disc.Farmer.list_of_query;
import com.ai.ai_disc.Videoconference.VCActivity;
import com.ai.ai_disc.model.MultiChatFarmerExpertQuery_Model;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiChat_ExpertQuery_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    ArrayList<String> exp_token;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    static String[] image_data = new String[1];
    List<MultiChatFarmerExpertQuery_Model> multiChatFarmerExpertQuery_modelsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_askcommunity);
        setTitle("AI-DISC");

        getWindow().setSoftInputMode(1);
        setnavigation();
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
            acc.setText(expert_singleton.getInstance().getcrop()+" Expert");
            loct.setText(user_singleton.getInstance().getloct());
            //Log.d("ggg",head.findViewById(R.id.vsr).toString());


        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("error",e.toString());
        }

        query_ImageView = (ImageView) findViewById(R.id.image);
        farmerNameTextView = (TextView) findViewById(R.id.farmer_name);
        time_text=(TextView)findViewById(R.id.time_text);
        linearLayout=(LinearLayout)findViewById(R.id.reply_ByScientist_Layout);
        query_DescriptionTextView = (TextView) findViewById(R.id.query_description);
        chatRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setHasFixedSize(true);
        strun=findViewById(R.id.status_run);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MultiChat_ExpertQuery_Activity.this, 1);
        chatRecyclerView.setLayoutManager(layoutManager);
        layout_add_image_etc = (LinearLayout) findViewById(R.id.linear_layout_image_video_audio);
        upload_Image = (ImageButton) findViewById(R.id.upload_img);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        description=(EditText)findViewById(R.id.editText_enterMessage);
        multiChatFarmerExpertQuery_modelsList=new ArrayList<>();
//        vc=findViewById(R.id.vcop);
//        clo=findViewById(R.id.close);
        ref=findViewById(R.id.ref);

        // queryDetailsList = (ArrayList<QueryDetails>) args.getParcelable("queryList");
        desc="";
        imagePath="";
        farmerId1="";
        expertId1="";
        cropId_Get1="";
        userType1="";
        farmerName="";
        queryId1="";



        upload_Image.setEnabled(false);
        sendButton.setEnabled(true);
        ref.setEnabled(true);
        description.setEnabled(true);
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

            farmerId1 = args.getString("farmerId");
            expertId1 = args.getString("expertId");
            queryId1 = args.getString("queryId");
            cropId_Get1 = args.getString("cropId");

            crop= args.getString("crop");
            userType1="7";
            farmerName=args.getString("farmerName");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                desc = chat_expert_singleton.getInstance().getchat().getFarmer_query();
                imagePath = chat_expert_singleton.getInstance().getchat().getImg_path();

                farmerId1 = chat_expert_singleton.getInstance().getchat().getFarmerId();
                expertId1 = user_singleton.getInstance().getUser_id();
                queryId1 = chat_expert_singleton.getInstance().getchat().getQuery_id();
                cropId_Get1 = chat_expert_singleton.getInstance().getchat().getCropID();

                crop= chat_expert_singleton.getInstance().getchat().getcrop_name();
                userType1="7";
                farmerName=chat_expert_singleton.getInstance().getchat().getFarmer_name();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        setpro();}
        else{
            getrecord(queryId1);
        }



        getchatter();

        ca=new customAdapterMultiChatFarmerExpertQuery(MultiChat_ExpertQuery_Activity.this,multiChatFarmerExpertQuery_modelsList);
        chatRecyclerView.setAdapter(ca);
        multiChatGetValue();

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressDialog_at_send_query = new ProgressDialog(MultiChat_ExpertQuery_Activity.this);
                progressDialog_at_send_query.setCancelable(false);
                progressDialog_at_send_query.setMessage("Send Query");
                progressDialog_at_send_query.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog_at_send_query.show();

                if (!description.getText().toString().isEmpty() )
                {
                    problem = description.getText().toString();
                    shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
                    Json_new_submit_query json_new_submit = new Json_new_submit_query(MultiChat_ExpertQuery_Activity.this, progressDialog_at_send_query);
                    json_new_submit.execute(farmerId1,queryId1,expertId1,cropId_Get1,userType1,problem);
                } else
                {
                     if(description.getText().toString().isEmpty())
                    {
                        Toast.makeText(MultiChat_ExpertQuery_Activity.this, "Please write the query", Toast.LENGTH_LONG).show();
                    }
                    progressDialog_at_send_query.dismiss();
                    sendButton.setEnabled(true);
                }
            }
        });

        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiChatGetValue1();
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("chat_refresh_message"));
    }
    public  BroadcastReceiver mMessageReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int signal = intent.getIntExtra("position", 0);
            String qu = intent.getStringExtra("query");
            if (signal==1 && qu.matches(queryId1)){
                multiChatGetValue1();
                //Log.d("hdj","sjksnks");
            }
        }
    };
    void sendmsg(String msg){
        //Log.d("lllllllllg", exp_token.toString());
        if (exp_token.size()!=0 ){

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
    void setpro(){
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



                    AlertDialog.Builder ob = new AlertDialog.Builder(MultiChat_ExpertQuery_Activity.this);
                    LinearLayout nb = new LinearLayout(MultiChat_ExpertQuery_Activity.this);
                    ImageView img = new ImageView(MultiChat_ExpertQuery_Activity.this);

                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    nb.setLayoutParams(param);
                    img.setLayoutParams(param);
                    img.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(MultiChat_ExpertQuery_Activity.this)
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
                            Model_expert_query_content model_expert_query_content=new Model_expert_query_content();
                            model_expert_query_content.setQuery_id(rc);
                            model_expert_query_content.setFarmer_name(farmerName);
                            model_expert_query_content.setFarmer_address("farmer_address");
                            model_expert_query_content.setExpert_nmae(expertId1);
                            model_expert_query_content.setExpert_address("");
                            model_expert_query_content.setFarmer_query("");
                            model_expert_query_content.setImg_path(imagePath);
                            model_expert_query_content.setQuery_resolution(sol);
                            model_expert_query_content.setQuery_status("query_status");
                            model_expert_query_content.setCropID(cropId_Get1);
                            model_expert_query_content.setFarmerId(farmerId1);
                            model_expert_query_content.setUserType(userType1);
                            model_expert_query_content.setstatus_run("0");
                            model_expert_query_content.setcrop_name(crop);
                            model_expert_query_content.settimes("times");
                            chat_expert_singleton.getInstance().setchat(model_expert_query_content);
setpro();
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
                                if (!sf.matches("") && !sf.matches(user_singleton.getInstance().getUser_name())){
                                    followed.add((String) array.getString(i));
                                }
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        followed.add(farmerName);

                        if (followed.size()!=0){
                            for (int i =0;i<followed.size();i+=1){
                               // Log.d("lllllllll1", followed.get(i));
                                db.collection("loginaidisc")
                                        .whereEqualTo("username", followed.get(i))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        //Log.d("homedatallll", document.getId() + " => " + document.getData().get("token").toString());
                                                        String tk = String.valueOf(document.getData().get("token"));
                                                        exp_token.add(tk);

                                                    }
                                                    //;
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

    private void setnavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
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
                        Toast.makeText(MultiChat_ExpertQuery_Activity.this, "Signed out", Toast.LENGTH_LONG).show();
                        shared_pref.remove_shared_preference(MultiChat_ExpertQuery_Activity.this);
                        Intent intent = new Intent(MultiChat_ExpertQuery_Activity.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("fff", "Error writing document", e);
                        Toast.makeText(MultiChat_ExpertQuery_Activity.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(MultiChat_ExpertQuery_Activity.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(MultiChat_ExpertQuery_Activity.this, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(MultiChat_ExpertQuery_Activity.this);
                    Intent intent = new Intent(MultiChat_ExpertQuery_Activity.this, Login.class);
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

                Intent intent = new Intent(MultiChat_ExpertQuery_Activity.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.help1:

                Intent intent1 = new Intent(MultiChat_ExpertQuery_Activity.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.home:

                Intent ibtwd=new Intent(MultiChat_ExpertQuery_Activity.this, expertprofile_fragment.class);
                startActivity(ibtwd);

                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.editme:

                Intent ibt=new Intent(MultiChat_ExpertQuery_Activity.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(MultiChat_ExpertQuery_Activity.this, contributor.class);
                startActivity(ibt1);

                break;
            case R.id.history:

                Intent ibt1q=new Intent(MultiChat_ExpertQuery_Activity.this, history.class);
                startActivity(ibt1q);

                break;
            case R.id.disclaimer:

                Intent intent1q = new Intent(MultiChat_ExpertQuery_Activity.this, disclaimer.class);
                startActivity(intent1q);



                break;

            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
                                    if(model.getReplyeQueryId().isEmpty() ||model.getReplyeQueryId().equals("null"))
                                    {

                                    }

                                    if(multiChatFarmerExpertQuery_modelsList.size()>0)
                                    {
                                        multiChatGetValue1();


                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {

                                Toast.makeText(MultiChat_ExpertQuery_Activity.this, "Error", Toast.LENGTH_LONG).show();
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
                            if(multiChatFarmerExpertQuery_modelsList.size()>0)
                            {
                                if(model.getReplyeQueryId().isEmpty()||model.getReplyeQueryId().equals("null"))
                                {
                                    linearLayout.setVisibility(View.GONE);
                                }
                                else
                                {
                                   ca.notifyDataSetChanged();
                                    description.getText().clear();
                                }
                              //  Toast.makeText(MultiChat_FarmerExpertQuery_Activity.this, "Submitted" + multiChatFarmerExpertQuery_modelsList.size(), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(MultiChat_ExpertQuery_Activity.this, "Error" + multiChatFarmerExpertQuery_modelsList.size(), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(MultiChat_ExpertQuery_Activity.this, "Error", Toast.LENGTH_LONG).show();
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
                                System.out.println("12345678" + response.toString());


                            }
                            if(multiChatFarmerExpertQuery_modelsList.size()>0)
                            {
                                if(model.getReplyeQueryId().isEmpty()||model.getReplyeQueryId().equals("null"))
                                {
                                    linearLayout.setVisibility(View.GONE);
                                }
                                else
                                {linearLayout.setVisibility(View.VISIBLE);
                       /*             ca=new customAdapterMultiChatFarmerExpertQuery(MultiChat_FarmerExpertQuery_Activity.this,multiChatFarmerExpertQuery_modelsList);
                                    chatRecyclerView.setAdapter(ca);*/
                                    ca.notifyDataSetChanged();
                                    chatRecyclerView.setAdapter(ca);
                                    description.getText().clear();
                                    //Toast.makeText(MultiChat_ExpertQuery_Activity.this, "Successful...", Toast.LENGTH_LONG).show();

                              /*  RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MultiChat_FarmerExpertQuery_Activity.this);
                                chatRecyclerView.setLayoutManager(mLayoutManager);
                                chatRecyclerView.setItemAnimator(new DefaultItemAnimator());*/

                                }


                            }
                            else
                            {
                                Toast.makeText(MultiChat_ExpertQuery_Activity.this, "Error" + multiChatFarmerExpertQuery_modelsList.size(), Toast.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(MultiChat_ExpertQuery_Activity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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


                        LayoutInflater layoutinflater2 = (LayoutInflater) MultiChat_ExpertQuery_Activity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View parent2 = layoutinflater2.inflate(R.layout.query_image_layout, null);
                        ImageView image = (ImageView) parent2.findViewById(R.id.image);
                        ImageButton remove_button = (ImageButton) parent2.findViewById(R.id.cancel);


                        Glide.with(MultiChat_ExpertQuery_Activity.this)
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

                        Toast.makeText(MultiChat_ExpertQuery_Activity.this, "please choose less then 8mb size", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(MultiChat_ExpertQuery_Activity.this,"error", Toast.LENGTH_LONG).show();
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

        if (ContextCompat.checkSelfPermission(MultiChat_ExpertQuery_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MultiChat_ExpertQuery_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MultiChat_ExpertQuery_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(MultiChat_ExpertQuery_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

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
