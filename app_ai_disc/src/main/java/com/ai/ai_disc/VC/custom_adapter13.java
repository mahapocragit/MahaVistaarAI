package com.ai.ai_disc.VC;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class custom_adapter13 extends RecyclerView.Adapter<custom_adapter13.MyViewHolder> {

    private static final String TAG = "custom_adapter13";
    Context context;
    ArrayList<request> list_request;
    String token;
    String my_email;

    public custom_adapter13(Context context, ArrayList<request> list_request, String token, String my_email) {
        this.context = context;
        this.list_request = list_request;
        this.token = token;
        this.my_email = my_email;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_adapter_13_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        request e = list_request.get(position);

        holder.email.setText("Email : "+e.getEmail_id());

        holder.date.setText("Time : "+e.getDate());

        holder.audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                if (e.getToken() != null) {
                    Toast.makeText(context, " Online", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, Outgoing.class);
                    intent.putExtra("email", e.getEmail_id());
                    intent.putExtra("token", e.getToken().trim());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Not online", Toast.LENGTH_LONG).show();
                }

 */
                Intent intent = new Intent(context, Call_by_expert.class);
                intent.putExtra("my_email",my_email);
                intent.putExtra("my_token",token);
                intent.putExtra("receiver_token",e.getMy_token());
                intent.putExtra("receiver_email",e.getEmail_id());
                context.startActivity(intent);

              //  send_message(e.getMy_token(), token, my_email);
            }
        });


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
        //String AUTH_KEY_FCM = "AAAAyc-i6Uk:APA91bHoL0daHB_dttCUR-sb6uPg-JGB1evj419etbRFWEGs91nUD8W8bMgTRAvhh3mWO5Q-co4uwUQZ6sPyTdwRfs3Tsiln8GW1KX7ko_yxi-EMWG7VGng7GJrmO8QZPbK8uszwgGQRUp9TjWMv7mqRxaaU5SB5Xg";
        String AUTH_KEY_FCM = "ewPMEE62RKefqZnINKdEav:APA91bF09H1Z48qrwdS4-JLAKycAqnosY-ziHRk2_ByUGDWimweqnOkH-pq4zTo6GsvTVCwpPYh9TSIfoRiCN7RyC_IqNeW-XY2UP-OgSCpHRU4MqYk33ei37N-DiSZRiFKI7wWHleUB";
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
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d(TAG, "onError: " + error.getMessage());
                    }
                });
    }


    @Override
    public int getItemCount() {
        return list_request.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView email, date;

        Button audio;
        Button video;


        public MyViewHolder(View view) {
            super(view);


            email = (TextView) view.findViewById(R.id.email);
            audio = view.findViewById(R.id.audio);
            video = view.findViewById(R.id.video);
            date = view.findViewById(R.id.date);

        }
    }

}


