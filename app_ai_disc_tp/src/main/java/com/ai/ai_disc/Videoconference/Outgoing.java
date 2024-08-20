package com.ai.ai_disc.Videoconference;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ai.ai_disc.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Outgoing extends AppCompatActivity {


    private static final String TAG = "Outgoing";
    TextView email;
    Button cancel;
    String  AUTH_KEY_FCM="";
    TextView text,request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Outgoing Call");

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String email_get = data.getString("email");
        String token_get = data.getString("token");
        String my_email_get = data.getString("my_email");
        String my_token_get = data.getString("my_token");

        email = findViewById(R.id.email);
        cancel = findViewById(R.id.cancel);
        text=findViewById(R.id.text);
        request=findViewById(R.id.request);
        request.setText("");

        email.setText(email_get);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*
        JSONObject json = null;
        try {
            json = new JSONObject();


            json.put("to", token_get.trim());

            JSONObject info = new JSONObject();
            info.put("title", "notification title"); // Notification title
            info.put("body", "message body"); // Notification
            // body
            json.put("notification", info);



        } catch (JSONException e) {
            e.printStackTrace();
        }

         */

/*
        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        String token_got = shared_pref.sp.getString("token", "");

 */

        JSONArray tokens = new JSONArray();
        tokens.put(token_get.trim());

        JSONObject body = new JSONObject();
        JSONObject data1 = new JSONObject();

        try {
            data1.put("request", "queue");
            data1.put("email", my_email_get);
            data1.put("my_token", my_token_get);
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
                                                             Log.d(TAG, "onResponse: " + response);


                                                             try {
                                                                 int code = (int) response.get("success");

                                                                 if (code == 1) {
                                                                     /// Toast.makeText(Outgoing.this,"Request sent",Toast.LENGTH_LONG).show();
                                                                     request.setText("Request sent");
                                                                     //  finish();
                                                                 } else if (code == 0) {
                                                                     //  Toast.makeText(Outgoing.this,"Request  not sent",Toast.LENGTH_LONG).show();
                                                                     request.setText("Request not  sent");
                                                                 }
                                                             } catch (JSONException e) {
                                                                 e.printStackTrace();
                                                             }

                                                         }

                                                         @Override
                                                         public void onError(ANError error) {
                                                             // handle error
                                                             Log.d(TAG, "onError: " + error.getMessage());
                                                             // Toast.makeText(Outgoing.this,"Error,Request not sent",Toast.LENGTH_LONG).show();
                                                             request.setText("Error");
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
}