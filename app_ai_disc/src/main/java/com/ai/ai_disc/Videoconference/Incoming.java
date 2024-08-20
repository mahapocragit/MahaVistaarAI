package com.ai.ai_disc.Videoconference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ai.ai_disc.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class Incoming extends AppCompatActivity {

    Button accept, cancel;
    String  AUTH_KEY_FCM="";
    private static final String TAG = "Incoming";
    TextView incoming_email;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Incoming call");
        accept=findViewById(R.id.accept);
        cancel=findViewById(R.id.cancel);
        incoming_email=findViewById(R.id.email);


        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String email_get = data.getString("email");
        String token_get = data.getString("my_token");

        incoming_email.setText(email_get);
        
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
                String token_got1 = shared_pref.sp.getString("token", "");
                String email1 = shared_pref.sp.getString("email", "");

                String code= UUID.randomUUID().toString();

                send_cancel(token_get,token_got1,email1,code);
              //  finish();

            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
                String token_got1 = shared_pref.sp.getString("token", "");
                String email1 = shared_pref.sp.getString("email", "");

               String code= UUID.randomUUID().toString();
                
                send(token_get,token_got1,email1,code);
            }
        });
        
        


    }

    public void send_cancel(String token , String token2,String  email,String code){

        JSONArray tokens = new  JSONArray();
        tokens.put(token);

        JSONObject body = new JSONObject();
        JSONObject data1 = new JSONObject();

        try {
            data1.put("request", "reject");
            data1.put("email",email);
            data1.put("my_token",token2);
            data1.put("room_id",code);
            body.put("data",data1);
            body.put("registration_ids",tokens);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //AAAAyc-i6Uk:APA91bHoL0daHB_dttCUR-sb6uPg-JGB1evj419etbRFWEGs91nUD8W8bMgTRAvhh3mWO5Q-co4uwUQZ6sPyTdwRfs3Tsiln8GW1KX7ko_yxi-EMWG7VGng7GJrmO8QZPbK8uszwgGQRUp9TjWMv7mqRxaaU5SB5Xg
        /*
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");
         .addHeaders("Authorization","key="+AUTH_KEY_FCM)
                .addHeaders("Content-Type","application/json")
         */

        AndroidNetworking.post("https://fcm.googleapis.com/fcm/send")
                .addHeaders("Authorization","key="+Constant.AUTH_KEY_FCM)
                .addHeaders("Content-Type","application/json")


                .addJSONObjectBody(body)


                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d(TAG, "onResponse: "+response);


                        try {
                            int    response_code = (int) response.get("success");
                            if( response_code ==1){
                                Toast.makeText(Incoming.this,"Response sent",Toast.LENGTH_LONG).show();
                                // request.setText("Request sent");

                               // add_to_room(code);
                                  finish();
                            }else if ( response_code ==0){
                                Toast.makeText(Incoming.this,"Response not sent",Toast.LENGTH_LONG).show();
                                //  request.setText("Request not  sent");
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d(TAG, "onError: "+error.getMessage());
                        Toast.makeText(Incoming.this,"Error",Toast.LENGTH_LONG).show();

                        finish();
                    }
                });
    }
    
    
    public void send(String token , String token2,String  email,String code){

        JSONArray tokens = new  JSONArray();
        tokens.put(token);

        JSONObject body = new JSONObject();
        JSONObject data1 = new JSONObject();

        try {
            data1.put("request", "answer");
            data1.put("email",email);
            data1.put("my_token",token2);
            data1.put("room_id",code);
            body.put("data",data1);
            body.put("registration_ids",tokens);
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
                                                             Log.d(TAG, "onResponse: " + response);


                                                             try {
                                                                 int response_code = (int) response.get("success");
                                                                 if (response_code == 1) {
                                                                     /// Toast.makeText(Outgoing.this,"Request sent",Toast.LENGTH_LONG).show();
                                                                     // request.setText("Request sent");

                                                                     add_to_room(code);
                                                                     //  finish();
                                                                 } else if (response_code == 0) {
                                                                     Toast.makeText(Incoming.this, "Response not sent", Toast.LENGTH_LONG).show();
                                                                     //  request.setText("Request not  sent");
                                                                     finish();
                                                                 }
                                                             } catch (JSONException e) {
                                                                 e.printStackTrace();
                                                             }


                                                         }

                                                         @Override
                                                         public void onError(ANError error) {
                                                             // handle error
                                                             Log.d(TAG, "onError: " + error.getMessage());
                                                             Toast.makeText(Incoming.this, "Error", Toast.LENGTH_LONG).show();

                                                             finish();
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

    }
    
    public void add_to_room(String room_name){

        finish();
        Log.d(TAG, "add_to_room: incoming ");
        try {
            // object creation of JitsiMeetConferenceOptions
            // class by the name of options
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(""))
                    .setWelcomePageEnabled(false)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()

                .setRoom(room_name)

                .build();



        JitsiMeetActivity.launch(Incoming.this, options);
    }
}