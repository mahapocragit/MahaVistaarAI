package com.ai.ai_disc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.security.acl.Group;
import java.util.ArrayList;

public class sendnotification extends AppCompatActivity {
    String  AUTH_KEY_FCM="";
    ArrayList<String> exp_token;

    String cv1="";
    Button bn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendnotification);


         bn=findViewById(R.id.button3);
        RadioGroup vb=findViewById(R.id.group);
        EditText head=findViewById(R.id.msg);
        EditText text=findViewById(R.id.msgtext);
        EditText url=findViewById(R.id.url);
        url.setVisibility(View.GONE);
        vb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                 RadioButton cv=findViewById(checkedId);
                if (cv.getText().toString().matches("For update")){
                    cv1="1";
                    url.setVisibility(View.GONE);
                }if (cv.getText().toString().matches("For new programme")){
                    cv1="2";
                    url.setVisibility(View.VISIBLE);
                }if (cv.getText().toString().matches("For other notification")){
                    cv1="3";
                    url.setVisibility(View.GONE);
                }
            }
        });

        exp_token=new ArrayList<>();
        gett();
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cv1.matches("")){
                if (cv1.matches("2")){
                    cv1=url.getText().toString();
                }

                sendmsg(exp_token,cv1,head.getText().toString(),text.getText().toString());
            }else{
                    Toast.makeText(sendnotification.this,"Choose option first",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    void sendmsg(ArrayList<String> token_to,String cv1,String hd, String txt){
        if (!token_to.isEmpty()  ){
            bn.setText("wait");

            JSONArray tokens = new JSONArray();
            for (int i =0;i<token_to.size();i+=1){
                tokens.put(token_to.get(i).trim());
            }
            JSONObject body = new JSONObject();
            JSONObject data1 = new JSONObject();

            try {
                data1.put("request", "admin");
                data1.put("head", hd);
                data1.put("text", txt);
                data1.put("type", cv1);
                body.put("data", data1);
                body.put("registration_ids", tokens);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                                                        Toast.makeText(sendnotification.this,"Message sent",Toast.LENGTH_LONG).show();
                                                        //request.setText("Request sent");
                                                        //  finish();
                                                    } else if (code == 0) {
                                                          Toast.makeText(sendnotification.this,"Error0: Message  not sent",Toast.LENGTH_LONG).show();
                                                        //request.setText("Request not  sent");
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                bn.setText("SEND");

                                            }

                                            @Override
                                            public void onError(ANError error) {
                                                // handle error
                                                //Log.d(TAG, "onError: " + error.getMessage());
                                                Toast.makeText(sendnotification.this,"Error3,Request not sent",Toast.LENGTH_LONG).show();
                                                //request.setText("Error");
                                                bn.setText("SEND");
                                            }
                                        });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                bn.setText("SEND");
                                Toast.makeText(sendnotification.this,"Error3,Request not sent",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            bn.setText("SEND");
                            Toast.makeText(sendnotification.this,"Error3,Request not sent",Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
           // Log.d("token not found", "onError: " );
            bn.setText("SEND");
        }



}

//    ArrayList<String> gettokens(){
//        JSONObject objectd = new JSONObject();
//        try {
//            objectd.put("user_id", "1232");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        ArrayList<String>exp_token=new ArrayList<>();
//        ArrayList<String>followed=new ArrayList<>();
//        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getfolloweds")
//                .addJSONObjectBody(objectd)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        JSONArray array = null;
//                        Log.d("hhh",response.toString());
//                        try {
//                            array = response.getJSONArray("list");
//                            for (int i = 0; i < array.length(); i++) {
//
//                                followed.add((String) array.getString(i));}
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (!followed.isEmpty()){
//                            for (int i =0;i<followed.size();i+=1){
//
//                            }
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//
//                    }
//                });return exp_token;
//    }
    void gett() {

        db.collection("loginaidisc")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String tk = String.valueOf(document.getData().get("token"));
                        exp_token.add(tk);
                    }
                    Toast.makeText(sendnotification.this, "all collected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(sendnotification.this, "Error 01", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}