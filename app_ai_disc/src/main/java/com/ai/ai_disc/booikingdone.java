package com.ai.ai_disc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class booikingdone extends AppCompatActivity {
    String username,tokens,date="",slot="",query="";

    String  AUTH_KEY_FCM="";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booikingdone);
        tokens="";
        username=getIntent().getExtras().getString("username");
        date=getIntent().getExtras().getString("date");
        slot=getIntent().getExtras().getString("slot");
        query=getIntent().getExtras().getString("query");

        Thread thf = new Thread() {
            @Override
            public void run() {
                try{
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
//                    Json_new_submit_query jsd=new Json_new_submit_query(booikingdone.this);
//                    jsd.execute();
                    //Log.d("vbbb",username);
                    try {
                        setalarm();
                        db.collection("loginaidisc")
                                .whereEqualTo("username", username)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                tokens=String.valueOf(document.getData().get("token"));}
                                           // Log.d("homedata ",tokens);
                                                if (!tokens.matches("")){
//                                        JSONArray tokensd = new JSONArray();
//
//                                            tokensd.put(tokens.trim());

                                                    JSONObject body = new JSONObject();
                                                    JSONObject data1 = new JSONObject();
                                                    JSONArray tokens1 = new JSONArray();

                                                        tokens1.put(tokens);

                                                    try {
                                                        data1.put("request", "appointment_to_expert");
                                                        data1.put("date", date);
                                                        data1.put("username", user_singleton.getInstance().getUser_name());
                                                        body.put("data", data1);
                                                        body.put("registration_ids", tokens1);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getfcn")
                                                            .build()
                                                            .getAsJSONObject(new JSONObjectRequestListener() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    try {
                                                                        AUTH_KEY_FCM=response.getString("fcn");

                                                                        AndroidNetworking.post("https://fcm.googleapis.com/fcm/send")
                                                                                .addHeaders("Authorization", "key=" + AUTH_KEY_FCM)
                                                                                .addHeaders("Content-Type", "application/json")


                                                                                .addJSONObjectBody(body)


                                                                                .build()
                                                                                .getAsJSONObject(new JSONObjectRequestListener() {
                                                                                    @Override
                                                                                    public void onResponse(JSONObject response) {
                                                                                        try {
                                                                                            int code = (int) response.get("success");

                                                                                            if (code == 1) {
                                                                                                Toast.makeText(booikingdone.this,"Request sent",Toast.LENGTH_LONG).show();
                                                                                                //request.setText("Request sent");
                                                                                                //  finish();
                                                                                            } else if (code == 0) {
                                                                                                //  Toast.makeText(Outgoing.this,"Request  not sent",Toast.LENGTH_LONG).show();
                                                                                                //request.setText("Request not  sent");
                                                                                            }
                                                                                        } catch (JSONException e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                        Intent intent1 = new Intent(booikingdone.this, MultiChat_FarmerExpertQuery_Activity.class);
                                                                                        startActivity(intent1);
                                                                                        finish();
                                                                                    }

                                                                                    @Override
                                                                                    public void onError(ANError anError) {
                                                                                        Intent intent1 = new Intent(booikingdone.this, MultiChat_FarmerExpertQuery_Activity.class);
                                                                                        startActivity(intent1);
                                                                                        finish();
                                                                                    }
                                                                                });
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                        Intent intent1 = new Intent(booikingdone.this, MultiChat_FarmerExpertQuery_Activity.class);
                                                                        startActivity(intent1);
                                                                        finish();
                                                                    }

                                                                }

                                                                @Override
                                                                public void onError(ANError anError) {
                                                                    Intent intent1 = new Intent(booikingdone.this, MultiChat_FarmerExpertQuery_Activity.class);
                                                                    startActivity(intent1);
                                                                    finish();
                                                                }
                                                            });

                                                }

                                        } else {
                                           // Log.d("homedata", "Error getting documents: ", task.getException());
                                            Intent intent1 = new Intent(booikingdone.this, MultiChat_FarmerExpertQuery_Activity.class);
                                            startActivity(intent1);
                                            finish();
                                        }
                                    }
                                });

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Intent intent1 = new Intent(booikingdone.this, MultiChat_FarmerExpertQuery_Activity.class);
                        startActivity(intent1);
                        finish();
                    }

                }
            }
            };thf.start();

    }
    void setalarm(){
        String myFormat = "dd-MM-yyyy HH:mm" ;
        String dt=date+" " +slot;//In which you need put here   "29-11-2022 18:55:03"
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat) ;
        Date date = null ;
        try{
            //String gb=sdf.format(dt);
            date = sdf.parse(dt);
            long diffInMs =   date.getTime()-new Date().getTime();

            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);


            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, MyReceiver.class);
            intent.putExtra("myAction", "set_alarm_book");
            intent.putExtra("query", query);
            intent.putExtra("slot", slot);
            PendingIntent pendingIntent ;//= PendingIntent.getBroadcast(this, 0, intent, 0);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(
                        this, 0, intent, PendingIntent.FLAG_MUTABLE);
                //Toast.makeText(booikingdone.this, "hhhhhhhhh1 ", Toast.LENGTH_SHORT).show();
            }
            else
            {
                pendingIntent = PendingIntent.getBroadcast
                        (this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //Toast.makeText(booikingdone.this, "hhhhhhhhh2 "+String.valueOf(diffInSec*1000), Toast.LENGTH_SHORT).show();
            }
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime ()+diffInSec*1000, pendingIntent);
                    } catch (ParseException e) {
            e.printStackTrace();
        }
//
//
//        long diffInSec = TimeUnit.MILLISECONDS.toMillis(diffInMs);
        //Toast.makeText(alarmservice.this, "hhhhhhhhh ", Toast.LENGTH_SHORT).show();
//        scheduleNotification(getNotification( btnDate.getText().toString()) , diffInSec*1000 ) ;
    }

    public class Json_new_submit_query extends AsyncTask<String,Void,String>
    {
        Context context1;
        public Json_new_submit_query(Context context){
            context1=context;
        }


        @Override
        protected String doInBackground(String... param)
        {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result)
        {

        }

    }
}