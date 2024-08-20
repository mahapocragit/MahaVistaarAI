package com.ai.ai_disc.VC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ai.ai_disc.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Call_by_expert extends AppCompatActivity {

    private static final String TAG = "Call_by_expert";

    Button cancel;
    TextView email, status;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            String status_call = intent.getStringExtra("status");
            Log.d(TAG, "onReceive: "+status_call);
            if (status_call.equals("accepted"))
            {
                String room_id = intent.getStringExtra("room_id");
                Log.d(TAG, "onReceive 1: "+room_id);
                status.setText("ACCEPTED");
               // finish();
                add_to_room(room_id);
            } else if (status_call.equals("not_accepted")) {
                status.setText(" NOT ACCEPTED");
                Log.d(TAG, "onReceive: not accepted");
                //  finish();
                // add_to_room(room_id);
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_by_expert_2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Call by Expert");

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String my_email = data.getString("my_email");
        String my_token = data.getString("my_token");
        String receiver_token = data.getString("receiver_token");
        String receiver_email = data.getString("receiver_email");

        cancel = findViewById(R.id.cancel);
        email = findViewById(R.id.email);
        status = findViewById(R.id.status);
        status.setText("SENDING");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        email.setText(receiver_email);

        send_message(receiver_token, my_token, my_email);


    }

    public void add_to_room(String room_name)
    {

finish();
        Log.d(TAG, "add_to_room:  myfirebase ");
       // try {
            // object creation of JitsiMeetConferenceOptions
            // class by the name of options
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()

                    .setWelcomePageEnabled(false)
                   // .build();
      //  } catch (MalformedURLException e) {
          //  e.printStackTrace();
          //  Log.d(TAG, "add_to_room: "+e.getMessage());

      //  }
      //  JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()

                .setRoom(room_name)

                .build();



        JitsiMeetActivity.launch(Call_by_expert.this, options);

    }

    public void send_message(String token, String token1, String email) {

        JSONArray tokens = new JSONArray();
        tokens.put(token);

        JSONObject body = new JSONObject();
        JSONObject data1 = new JSONObject();

        try {
            data1.put("request", "call");
            data1.put("email", email);
            data1.put("my_token", token1);
            body.put("data", data1);
            body.put("registration_ids", tokens);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //AAAAyc-i6Uk:APA91bHoL0daHB_dttCUR-sb6uPg-JGB1evj419etbRFWEGs91nUD8W8bMgTRAvhh3mWO5Q-co4uwUQZ6sPyTdwRfs3Tsiln8GW1KX7ko_yxi-EMWG7VGng7GJrmO8QZPbK8uszwgGQRUp9TjWMv7mqRxaaU5SB5Xg
       // String AUTH_KEY_FCM = "AAAAyc-i6Uk:APA91bHoL0daHB_dttCUR-sb6uPg-JGB1evj419etbRFWEGs91nUD8W8bMgTRAvhh3mWO5Q-co4uwUQZ6sPyTdwRfs3Tsiln8GW1KX7ko_yxi-EMWG7VGng7GJrmO8QZPbK8uszwgGQRUp9TjWMv7mqRxaaU5SB5Xg";
       // String AUTH_KEY_FCM = "AAAAnivBMq4:APA91bEHHItaWWlxewsHRxlv7ksmz-bPVEfMpdGeqY6vl0PfjEKfNRM67dctbz09XljoFm1FWqm5IskiqJ4OcIVAL5_eouNjjsNNeK-x9sGgAYaAI2VX5xoQE7brEil3P2HP_SDNNgze";
        String AUTH_KEY_FCM = "";
        /*
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");
         .addHeaders("Authorization","key="+AUTH_KEY_FCM)
                .addHeaders("Content-Type","application/json")
         */

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
                                // Toast.makeText(Call_by_expert.this,"Sent",Toast.LENGTH_LONG).show();
                                // request.setText("Request sent");

                                status.setText("SENT");

                                //  finish();
                            } else if (response_code == 0) {
                                // Toast.makeText(Call_by_expert.this,"Not Sent",Toast.LENGTH_LONG).show();
                                //  request.setText("Request not  sent");
                                status.setText(" NOT SENT");
                                // finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d(TAG, "onError: " + error.getMessage());
                        // Toast.makeText(Call_by_expert.this,"Error",Toast.LENGTH_LONG).show();
                        status.setText("ERROR");
                        //  finish();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-action-local-broadcast"));


    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);


    }
}