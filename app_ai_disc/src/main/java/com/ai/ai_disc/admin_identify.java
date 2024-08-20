package com.ai.ai_disc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ai.ai_disc.model.history_model;
import com.ai.ai_disc.model.history_model_admin;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class admin_identify extends AppCompatActivity {
RecyclerView grid;
    ArrayList<history_model_admin> dev=new ArrayList<>();
    ArrayList<history_model_admin> s_index=new ArrayList<>();
    custom_adapter_history_admin dataa;
    custom_adapter_history_admin datare;
    Button prev,next;
    int s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_identify);
        grid=findViewById(R.id.grid);
        prev=findViewById(R.id.prev);next=findViewById(R.id.next);
        s=0;

        dataa=new custom_adapter_history_admin(admin_identify.this,s_index);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        grid.setLayoutManager(mLayoutManager);
        grid.setItemAnimator(new DefaultItemAnimator());
        gethistory();
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (s==0){
                    Toast.makeText(admin_identify.this,"Not possible",Toast.LENGTH_SHORT).show();
                }else{
                    s=s-10;
                    setdata();
                }

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (s+10>dev.size()){
                    Toast.makeText(admin_identify.this,"Not possible",Toast.LENGTH_SHORT).show();
                }else{
                    s=s+10;
                    setdata();
                }

            }
        });
    }
    void gethistory(){

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_history_admin")

                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("hhh",response.toString());
                        JSONArray array1 = null;
                        String totalident="";
                        try {
                            array1 = response.getJSONArray("data");
                            for (int i = 0; i < array1.length(); i++) {

                                JSONObject object = (JSONObject) array1.get(i);
                                history_model_admin admin5 = new history_model_admin();
                                admin5.setid(object.optString("id"));
                                admin5.setidd(object.optString("idd"));
                                admin5.setcrop(object.optString("crop"));
                                admin5.setdisease(object.optString("disease"));
                                admin5.setdate(object.optString("dt"));
                                admin5.setdp(object.optString("dp"));
                                admin5.setlike(object.optString("like"));
                                admin5.setdp_id(object.optString("dp_id"));
                                admin5.setcrop_id(object.optString("crop_id"));
                                admin5.setuser_name(object.optString("user_name"));
                                admin5.setpred(object.optString("pred"));
                                dev.add(admin5);
                            }
//                            totalident = response.optString("total");
//                            if (totalident.matches("0")){
//                                card.setVisibility(View.GONE);
//                            }else{
//                                card.setVisibility(View.VISIBLE);
//                            }
setdata();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //tal.setText(totalident);
                        //Log.d(TAG, "onResponse: " + array1);

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void setdata() {
        s_index.clear();
        if(s+10<dev.size()){
        for (int i=s; i<s+10;i+=1){
            s_index.add(dev.get(i));
        }}else{
            for (int i=s; i<dev.size();i+=1){
                s_index.add(dev.get(i));
            }
        }


        dataa.notifyDataSetChanged();

        grid.setAdapter(dataa);
    }
}