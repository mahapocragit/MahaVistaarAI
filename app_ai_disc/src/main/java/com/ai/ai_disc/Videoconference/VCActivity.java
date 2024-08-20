package com.ai.ai_disc.Videoconference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ai.ai_disc.R;
import com.ai.ai_disc.create_report;
import com.ai.ai_disc.history_page;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hsalf.smileyrating.SmileyRating;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VCActivity extends AppCompatActivity {
    EditText secretCodeBox;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";
    String  AUTH_KEY_FCM="";
    String app_id="",scode="",query="",sldate="",slot="";
    TextView qw,slotd;
    int joined;
    Button joinBtn, shareBtn,cancel;
    private String[] slots=new String[]{"10 - 10:30 ",
            "10:30 - 11 ",
            "11 - 11:30 ",
            "11:30  - 12 ","12 - 12:30 ",
            "12:30 - 13 ",
            "14:30 - 15",
            "15- 15:30 PM",
            "15:30 - 16 PM",
            "16 - 16:30 PM",
            "16:30 - 17 PM"};

    private String[] slotss=new String[]{"10:00",
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        joined=0;

        setContentView(R.layout.activity_vc);

        secretCodeBox = findViewById(R.id.codeBox);
        joinBtn = findViewById(R.id.joinBtn);
        slotd=findViewById(R.id.slot);

        qw=findViewById(R.id.qid);
         scode=getIntent().getExtras().getString("scode");
        sldate=getIntent().getExtras().getString("date");
        slot=getIntent().getExtras().getString("slot");
         app_id=getIntent().getExtras().getString("app_id");
         query=getIntent().getExtras().getString("query");
        //"RETJAVAX"
        slotd.setText(slots[Integer.parseInt(slot)]);
        secretCodeBox.setText("******");
        qw.setText("Date : "+sldate);
        secretCodeBox.setEnabled(false);

        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat tms = new SimpleDateFormat("HH:mm");
                SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yyyy");
                String dated=sldate;
                try {
                    Date strDate = sdfs.parse(dated);
                    Date nb = new Date();
                    String formattedDate = sdfs.format(nb);
                    String tmds = tms.format(nb);
                    Date std = tms.parse(slotss[Integer.parseInt(slot)]);
                    Date std1 = tms.parse(slotss[Integer.parseInt(slot) + 1]);
                    Date bn = tms.parse(tmds);
                    //Log.d("lll",bn.toString()+"   "+std.toString());
                    Date xvnb = sdfs.parse(formattedDate);

                        if (bn.after(std1)) {
                            Toast.makeText(VCActivity.this,"Slot is expired",Toast.LENGTH_LONG).show();
                            joinBtn.setEnabled(false);
                        } else {

                                asyncall vbm=new asyncall();
                                joined=1;
                                vbm.execute(app_id,user_singleton.getInstance().getUser_type().toLowerCase().trim());

                                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                        .setRoom(scode)
                                        .setWelcomePageEnabled(false)
                                        .build();
                                JitsiMeetActivity.launch(VCActivity.this, options);

                        }

                }
                catch(Exception c){
                }




            }
        });

    }
    public class asyncall extends AsyncTask<String,String,String>{
        String appoint="";
        String type="";
        String ret="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            appoint=strings[0];
            type=strings[1];

            JSONObject object1 = new JSONObject();
            try {

                object1.put("appoint", appoint);
                object1.put("type", type);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("hhhh", "onResponse: " + object1.toString());

            AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/expert_vc_join")
                    .addJSONObjectBody(object1)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            String user="";
                            try {
                                if (type.matches("expert")) {
                                    user = response.getString("farmer");
                                } else {
                                    user = response.getString("expert");
                                }
                                String e = response.getString("join_e");
                                String f = response.getString("join_f");
                                if (e.matches("1") && f.matches("1")) {
                                    Log.d("do","nothing");
                                } else {
                                    db.collection("loginaidisc")
                                            .whereEqualTo("username", user)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isComplete()) {
                                                        String token = "";
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            //Log.d("homedatallll", document.getId() + " => " + document.getData().get("token").toString());
                                                            token = String.valueOf(document.getData().get("token"));
                                                        }
                                                        sendmsg(token);
                                                        //;
                                                    } else {
                                                        Log.d("homedata", "Error getting documents: ", task.getException());
                                                    }


                                                }
                                            });
                                }
                            }
                            catch (Exception e){

                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
            return ret;
        }
    }
    void sendmsg(String token){
        //Log.d("lllllllllg", exp_token.toString());
        if (!token.matches("") ){

            JSONArray tokens = new JSONArray();

                tokens.put(token.trim());

            JSONObject body = new JSONObject();
            JSONObject data1 = new JSONObject();

            try {
                data1.put("request", "coming_call");
                data1.put("scode", scode);
                data1.put("app", app_id);
                data1.put("query", query);
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

    @Override
    public void onBackPressed() {
        backed();

    }
    void dialog(){
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(VCActivity.this);

        SmileyRating sml=new SmileyRating(VCActivity.this);
        LinearLayout linearLayout = new LinearLayout(VCActivity.this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        sml.setLayoutParams(lp);
        EditText cv=new EditText(VCActivity.this);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp1.setMargins(10,20,10,10);
        cv.setLayoutParams(lp1);

        linearLayout.addView(sml);
        linearLayout.addView(cv);
        linearLayout.setGravity(Gravity.CENTER);

//
//                popDialog.setIcon(android.R.drawable.gallery_thumb);
        popDialog.setTitle("How useful is it to you?");

        //add linearLayout to dailog
        popDialog.setView(linearLayout);
        popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SmileyRating.Type smiley = sml.getSelectedSmiley();
// You can compare it with rating Type

                // You can get the user rating too
                // rating will between 1 to 5, but -1 is none selected
                int rating = smiley.getRating();
                String text="";
                text=cv.getText().toString();

                ratings(app_id,rating,text);
            }
        });
        popDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                VCActivity.super.onBackPressed();
                finish();
            }
        });
        popDialog.show();

    }
    void ratings(String id,int rate,String text){
        JSONObject object1 = new JSONObject();
        try {
            object1.put("app_id", id);
            object1.put("rt", rate);
            object1.put("comm", text);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/expert_vc_rate")
                .addJSONObjectBody(object1)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VCActivity.super.onBackPressed();
                        finish();
                    }

                    @Override
                    public void onError(ANError anError) {
                        VCActivity.super.onBackPressed();
                        finish();
                    }
                });
    }
    void backed(){
        SimpleDateFormat tms = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yyyy");
        String dated=sldate;
        try {
            Date strDate = sdfs.parse(dated);
            Date nb = new Date();
            String formattedDate = sdfs.format(nb);
            String tmds = tms.format(nb);
            Date std = tms.parse(slotss[Integer.parseInt(slot)]);
            Date std1 = tms.parse(slotss[Integer.parseInt(slot) + 1]);
            Date bn = tms.parse(tmds);
            //Log.d("lll",bn.toString()+"   "+std.toString());
            Date xvnb = sdfs.parse(formattedDate);

            if (bn.after(std1)) {
                Toast.makeText(VCActivity.this,"Slot is expired",Toast.LENGTH_LONG).show();
                joinBtn.setEnabled(false);
                if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("farmer")){
                if (joined==1){
                dialog();
                }
                else{
                    VCActivity.super.onBackPressed();
                    finish();
                }}
                else{
                    VCActivity.super.onBackPressed();
                    finish();
                }



            } else {
                super.onBackPressed();
            }
        }
        catch(Exception c){
        }
    }

}