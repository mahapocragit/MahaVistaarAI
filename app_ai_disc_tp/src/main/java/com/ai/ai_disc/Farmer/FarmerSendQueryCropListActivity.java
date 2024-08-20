package com.ai.ai_disc.Farmer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ai.ai_disc.Login;
import com.ai.ai_disc.R;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.ai.ai_disc.shared_pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FarmerSendQueryCropListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    RecyclerView recyclerView;
    String farmerId;
    FarmerSendQueryCropListAdapter farmerSendQueryCropListAdapter;
    ArrayList<Model_DashboardContent> model_level_creations;
    String crop_Name,crop_Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_diseas_identifier_profile1);
         model_level_creations =new ArrayList<>();
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        farmerId= user_singleton.getInstance().getUser_id();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(FarmerSendQueryCropListActivity.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        getting_list_crop();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(FarmerSendQueryCropListActivity.this, recyclerView, new RecyclerTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {


                Intent intent=new Intent(FarmerSendQueryCropListActivity.this, FarmerSendQuryChooseImageOrVideoActivity.class);
                Bundle data1 = new Bundle();
                data1.putString("cropID",model_level_creations.get(position).getCropId());
                data1.putString("cropName",model_level_creations.get(position).getGetGridText());
                intent.putExtras(data1);

                startActivity(intent);

              // Toast.makeText(FarmerSendQueryCropListActivity.this,model_level_creations.get(position).getCropId(),Toast.LENGTH_LONG).show();


            }

            @Override
            public void onLongClick(View view, int position)
            {

            }

        }));

    }

    public  void getting_list_crop()
    {



        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/list_crop")

                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {



                        try {


                            model_level_creations.clear();
                            JSONArray array = response.getJSONArray("cropreport_list");

                            for(int i=0;i<array.length();i++){
                                Model_DashboardContent model_dashboardContent=new Model_DashboardContent();
                                JSONObject object = (JSONObject) array.get(i);

                                 crop_Id= object.getString("id");
                                crop_Name= object.getString("name");
                                model_dashboardContent.setGetGridText(crop_Name);
                                model_dashboardContent.setCropId(crop_Id);

                                model_level_creations.add(model_dashboardContent);

                            }

                            if(crop_Name.equals(""))
                            {
                                Toast.makeText(FarmerSendQueryCropListActivity.this,"No crop found.",Toast.LENGTH_LONG).show();

                            }else{
                                farmerSendQueryCropListAdapter = new FarmerSendQueryCropListAdapter(FarmerSendQueryCropListActivity.this, model_level_creations);
                                //attaching adapter to the RecyclerView
                                recyclerView.setAdapter(farmerSendQueryCropListAdapter);

                            }





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                    }
                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(FarmerSendQueryCropListActivity.this,"Error in list of crop.",Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.logout:

                Intent intent = new Intent(FarmerSendQueryCropListActivity.this, Login.class);
                startActivity(intent);
                finish();

                shared_pref.remove_shared_preference(FarmerSendQueryCropListActivity.this);

                break;
            default:
                break;

        }

        return true;
    }
}
