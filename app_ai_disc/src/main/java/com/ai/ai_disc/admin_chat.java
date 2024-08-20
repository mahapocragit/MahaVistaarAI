package com.ai.ai_disc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Farmer.QueryDetails1;
import com.ai.ai_disc.Farmer.custom_adapter_admin_chat;
import com.ai.ai_disc.model.history_model_admin;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class admin_chat extends AppCompatActivity {
GridView grid;
    ArrayList<QueryDetails1> dev=new ArrayList<>();
    ArrayList<QueryDetails1> s_index=new ArrayList<>();
    custom_adapter_admin_chat dataa;
    custom_adapter_admin_chat datare;
    Button prev,next;
    int s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_identify);
        grid=findViewById(R.id.grid);
        prev=findViewById(R.id.prev);next=findViewById(R.id.next);
        s=0;

        //dataa=new custom_adapter_admin_chat(admin_chat.this,s_index);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        grid.setLayoutManager(mLayoutManager);
//        grid.setItemAnimator(new DefaultItemAnimator());
        gethistory();
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (s==0){
                    Toast.makeText(admin_chat.this,"Not possible",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(admin_chat.this,"Not possible",Toast.LENGTH_SHORT).show();
                }else{
                    s=s+10;
                    setdata();
                }

            }
        });
    }
    void gethistory(){

        //newly added token (if not needed then remove header from AndroidNetworking)
        String token_key = "PMAK-646d993c4ae18d7b534b6aad-afe1811f3b9c9feff73dc2633c1410b885";
        //AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/query_user_for_admin111")
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/query_user_for_admin111")
                .addHeaders("ranjan_api", token_key)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                                           //Log.d("hhh",response.toString());
                        JSONArray array1 = null;
                        String totalident="";
                        dev.clear();
                        try {
                            for (int i = 0; i < response.length(); i++)
                            {
                                QueryDetails1 queryDetails=new QueryDetails1();
                                JSONObject object = (JSONObject) response.get(i);
                                String query_id = object.optString("Query_id");
                                String query_resolution = object.optString("Query_resolution");
                                String queryStatus = object.optString("Query_status");
                                String expertId = object.optString("expert_id");

                                String imagePath= object.optString("image_1");
                                String language = object.optString("language");
                                String description = object.optString("desc");
                                String user_id = object.optString("user_id");

                                String firstName=object.optString("firstName");
                                String userType=object.optString("userType");
                                String cropId=object.optString("crop_id");
                                String status_run=object.optString("status_run");
                                //  list_query1.add(description);
                                queryDetails.setQuery_id(query_id);
                                queryDetails.settim(object.optString("times"));
                                queryDetails.setQuery_resolution(query_resolution);
                                queryDetails.setQueryStatus(queryStatus);
                                queryDetails.setExpertId(expertId);
                                queryDetails.setImagePath(imagePath);
                                queryDetails.setLanguage(language);
                                queryDetails.setDescription(description);
                                queryDetails.setUser_id(user_id);
                                queryDetails.setFirstName(firstName);
                                queryDetails.setUserType(userType);
                                queryDetails.setCropId(cropId);
                                queryDetails.setstatus_run(status_run);
                                queryDetails.setcrop(object.optString("crop_name"));
                                dev.add(queryDetails);

                                // query_id_list.add(i, query_id);


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


        //dataa.notifyDataSetChanged();

        grid.setAdapter(new custom_adapter_admin_chat(admin_chat.this,s_index));
    }
}