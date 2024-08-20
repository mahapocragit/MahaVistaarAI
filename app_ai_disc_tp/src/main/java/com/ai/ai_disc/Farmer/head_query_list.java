package com.ai.ai_disc.Farmer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ai.ai_disc.Expert_profile;
import com.ai.ai_disc.Login;
import com.ai.ai_disc.MultiChat_FarmerExpertQuery_Activity;
import com.ai.ai_disc.R;
import com.ai.ai_disc.shared_pref;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class head_query_list extends AppCompatActivity {

    ListView list_query1;
    Context  context;
    RelativeLayout layout;
    String userId="";
    List<Model_expert_query_content> expertQueryList;
    ArrayList<ArrayList<String>> full_details;
    custom_adapter_query_answer custom_adapter_query_answer1;
    String expertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_query_list);
     /*   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        full_details = new ArrayList<ArrayList<String>>();
        userId= user_singleton.getInstance().getUser_id();
        expertQueryList=new ArrayList<>();
         expertId= user_singleton.getInstance().getUser_id();

        setTitle("AI-DISC");
        list_query1=(ListView)findViewById(R.id.list_query);
        layout=(RelativeLayout)findViewById(R.id.content_head_query_list);

        getting_query_list();

        list_query1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

             //Previous Data Value

              // ArrayList<String> details_query =full_details.get(position);
              /*  Intent intent = new Intent(head_query_list.this,head_query_details_and_reply.class);
                Bundle bundle=new Bundle();

                bundle.putString("farmer_name",expertQueryList.get(position).getFarmer_name());
                bundle.putString("farmer_query",expertQueryList.get(position).getFarmer_query());
                bundle.putString("query_id",expertQueryList.get(position).getQuery_id());
                bundle.putString("img_path",expertQueryList.get(position).getImg_path());
                bundle.putString("farmer_address",expertQueryList.get(position).getFarmer_address());
                bundle.putString("query_resolution",expertQueryList.get(position).getQuery_resolution());
                bundle.putString("query_status",expertQueryList.get(position).getQuery_status());

                intent.putExtras(bundle);
               // startActivityForResult(intent, 1);
               // intent.putExtra("details",details_query);

                //Farmer_description,farmer_imagePath,farmerId,expertId,queryId,cropId,userType,farmerName
                startActivity(intent);*/

              // ArrayList<String> details_query =full_details.get(position);



                // Current Value....


              /*  Log.d("imagePath",expertQueryList.get(position).getImg_path());
                Log.d("desc",expertQueryList.get(position).getFarmer_query());
                Log.d("farmerName",expertQueryList.get(position).getFarmer_name());
                Log.d("farmerId",expertQueryList.get(position).getFarmerId());
                Log.d("expertId",expertId);
                Log.d("queryId",expertQueryList.get(position).getQuery_id());
                Log.d("cropId_Get",expertQueryList.get(position).getCropID());
                Log.d("userType",expertQueryList.get(position).getUserType());
*/


                Intent intent = new Intent(head_query_list.this, MultiChat_FarmerExpertQuery_Activity.class);
                Bundle bundle=new Bundle();

                bundle.putString("farmerName",expertQueryList.get(position).getFarmer_name());
                bundle.putString("desc",expertQueryList.get(position).getFarmer_query());
                bundle.putString("queryId",expertQueryList.get(position).getQuery_id());
                bundle.putString("imagePath",expertQueryList.get(position).getImg_path());
                bundle.putString("expertId",expertId);
                bundle.putString("cropId", expertQueryList.get(position).getCropID());
                bundle.putString("farmerId",expertQueryList.get(position).getFarmerId());
                bundle.putString("userType","7");
                intent.putExtra("bundle",bundle);
                //intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();


    }
/*

    @Override
    public   boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflator= getMenuInflater();
        inflator.inflate(R.menu.tool_bar7,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_logout:

                Intent intent = new  Intent(head_query_list.this,Login_Home_Mobile_Activity.class);
                startActivity(intent);

                shared_pref.sp =getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared_pref.sp.edit();
                editor.remove("username");
                editor.remove("password");
                editor.apply();


                break;
            case R.id.action_home:

                Intent intent2 = new Intent(head_query_list.this,kvk_head_new.class);
                startActivity(intent2);

                break;
            default:
                break;

        }

        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet,intentFilter);

    }


    @Override
    public void onStop(){
        super.onStop();
        unregisterReceiver(internet);

    }
*/

    public void  getting_query_list()
    {


        //newly added token
        String token_key = "PMAK-646d993c4ae18d7b534b6aad-afe1811f3b9c9feff73dc2633c1410b885";
        //AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Query_resolution?expertId=" + userId)
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Query_resolution?expertId=" + userId)
                .addHeaders("ranjan_api", token_key)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener()
                {
                    @Override
                    public void onResponse(JSONArray response) {

                         System.out.println(" response :"+response);
                        expertQueryList.clear();

                      ArrayList<String>  query_list= new ArrayList<String>();
                        ArrayList<String> list_scientist_id = new ArrayList<String>();

                        try {

                       // System.out.println("p.5:");
                        for (int i = 0; i < response.length(); i++)
                        {


                            JSONObject jso = (JSONObject) response.get(i);
                            Model_expert_query_content model_expert_query_content=new Model_expert_query_content();

                             String Query_id = (String) jso.optString("Query_id");

                            String farmer_name= jso.optString("farmer_name");

                            String farmer_address=jso.optString("farmer_address");
                            String expert_name=jso.optString("expert_nmae");
                            String expert_address=jso.optString("expert_address");
                            String farmer_query=jso.optString("farmer_query");
                            String img_path=jso.optString("img_path");
                            String query_resolution=jso.optString("query_resolution");
                            String query_status=jso.optString("query_status");
                            String crop_id=jso.optString("cropId");
                            String farmerID=jso.optString("farmerId");
                            String userType= jso.optString("userType");

                            model_expert_query_content.setQuery_id(Query_id);
                            model_expert_query_content.setFarmer_name(farmer_name);
                            model_expert_query_content.setFarmer_address(farmer_address);
                            model_expert_query_content.setExpert_nmae(expert_name);
                            model_expert_query_content.setExpert_address(expert_address);
                            model_expert_query_content.setFarmer_query(farmer_query);
                            model_expert_query_content.setImg_path(img_path);
                            model_expert_query_content.setQuery_resolution(query_resolution);
                            model_expert_query_content.setQuery_status(query_status);
                            model_expert_query_content.setCropID(crop_id);
                            model_expert_query_content.setFarmerId(farmerID);
                            model_expert_query_content.setUserType(userType);


                            expertQueryList.add(model_expert_query_content);


                            /*ArrayList<String> query_details = new ArrayList<String>();
                            query_details.add(0,first_name);
                            query_details.add(1,address);
                            query_details.add(2,scientist_id);
                            query_details.add(3,query_resolution);
                            query_details.add(4,desc);
                            query_details.add(5,query_id);
                            query_details.add(6,image1);
                            query_details.add(7,image2);
                            query_details.add(8,image3);
                            query_details.add(9,video1);
                            query_details.add(10,video2);
                            query_details.add(11,video3);
                            query_details.add(12,audio1);
                            query_details.add(13,audio2);
                            query_details.add(14,audio3);
                            query_details.add(15,forward_scientist_id);
                            query_details.add(16,scientist_image);
                            query_details.add(17,scientist_video);
                            query_details.add(18,scientist_audio);

                            query_list.add(desc);
                            list_scientist_id.add(scientist_id);
                            full_details.add(query_details);*/


                        }

                        if(expertQueryList.size()==0)
                        {

                         /*   LinearLayout.LayoutParams text_params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                            TextView text = new TextView(head_query_list.this);
                            text.setText("NO QUERY FOUND");
                            text.setTextSize(20);
                            text.setLayoutParams(text_params);
                            text.setGravity(Gravity.CENTER);*/
                            setContentView(R.layout.textview_center);
                          /*  Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
                            setSupportActionBar(toolbar);*/
                            TextView textView=(TextView)findViewById(R.id.text_view);
                            textView.setGravity(Gravity.CENTER);
                            textView.setText("NO QUERY FOUND");
                            layout.removeAllViews();
                            //layout.addView(text);

                        }
                        else
                            {
                                custom_adapter_query_answer1=new custom_adapter_query_answer(head_query_list.this,expertQueryList);
                                list_query1.setAdapter(custom_adapter_query_answer1);

                           // list_query1.setAdapter(new custom_adapter_query_answer(head_query_list.this,expertQueryList));

                        }

                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }




                    }@Override
                    public void onError(ANError anError) {


                        Toast.makeText(head_query_list.this,"Error",Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);

        //OVCCA 01 DOM DSD
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                Intent intent = new Intent(head_query_list.this, Login.class);
                startActivity(intent);

                shared_pref.remove_shared_preference(head_query_list.this);

                break;
            default:
                break;

        }

        return true;
    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if(resultCode == RESULT_OK)
            {
                *//*custom_adapter_query_answer1=new custom_adapter_query_answer(head_query_list.this,expertQueryList);
                list_query1.setAdapter(custom_adapter_query_answer1);*//*
                custom_adapter_query_answer1.notifyDataSetChanged();

                //Update List
            }

        }
    }
*/

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Expert_profile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
      //  Toast.makeText(FarmerProfileActivity.this, "No Back.Please logout", Toast.LENGTH_LONG).show();

    }




}
