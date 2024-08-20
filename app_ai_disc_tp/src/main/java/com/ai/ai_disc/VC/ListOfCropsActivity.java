package com.ai.ai_disc.VC;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Farmer.RecyclerTouchListener;
import com.ai.ai_disc.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListOfCropsActivity extends AppCompatActivity
{

    String crop_Id,crop_Name;
    ArrayList<String> cropListCodeList,cropListNameList;
    RecyclerView recyclerView;
    CropListAdapter cropListAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_crop);
        setTitle("AI-DISC");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ListOfCropsActivity.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        cropListCodeList=new ArrayList<>();
        cropListNameList=new ArrayList<>();
        getting_list_crop();


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(ListOfCropsActivity.this, recyclerView, new RecyclerTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {

                String getCropId=cropListCodeList.get(position);

                Intent intent=new Intent(ListOfCropsActivity.this,expert_list.class);
                intent.putExtra("cropId",getCropId);
                startActivity(intent);
                //Toast.makeText(ListOfCropsActivity.this, getCropId, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position)
            {

            }

           /* @Override
            public void onLongClick(View view, int position) {
                // Toast.makeText(getActivity(),"Long click",Toast.LENGTH_LONG).show();
                Model_Level_Creation model_level_creation = model_level_creations.get(position);
                showUpdateDeleteDialog(model_level_creation.getLevelId(), model_level_creation.getName());

            }*/
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


                            // model_level_creations.clear();
                            cropListCodeList.clear();
                            cropListNameList.clear();
                          /*  cropListNameList.add(0,"Select your crop");
                            cropListCodeList.add(0,"");*/

                            JSONArray array = response.getJSONArray("cropreport_list");

                            for(int i=0;i<array.length();i++)
                            {
                                // Model_DashboardContent model_dashboardContent=new Model_DashboardContent();
                                JSONObject object = (JSONObject) array.get(i);

                                crop_Id= object.getString("id");
                                crop_Name= object.getString("name");
                                cropListCodeList.add(crop_Id);
                                cropListNameList.add(crop_Name);



                                //  model_dashboardContent.setGetGridText(crop_Name);
                                // model_dashboardContent.setCropId(crop_Id);

                                // model_level_creations.add(model_dashboardContent);

                            }


                            cropListAdapter=new CropListAdapter(ListOfCropsActivity.this,cropListNameList,cropListCodeList);
                            recyclerView.setAdapter(cropListAdapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                    }
                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(ListOfCropsActivity.this,"Error in list of crop.",Toast.LENGTH_LONG).show();
                    }
                });
    }



}
