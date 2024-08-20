package com.ai.ai_disc.Farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import com.ai.ai_disc.Login;
import com.ai.ai_disc.R;
import com.ai.ai_disc.model.getIdentifier_crops_response;
import com.ai.ai_disc.model.identifier_model_croplist;
import com.ai.ai_disc.shared_pref;
import com.ai.ai_disc.view_model.identifier_croplist;


import java.util.ArrayList;

public class Farmer_DiseasIdentifier_profile1 extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    RecyclerView recyclerView;
    private ArrayList<String> discrops,pestcrops,croplist;

    private ArrayList<Integer> discrops_img,number_exist;
    DashboardAdapter dashboardAdapter;
    String farmerIdentification="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_diseas_identifier_profile1);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        farmerIdentification=data.getString("farmerIdentification");
        updatemodelenable();

    }
    private void updatemodelenable(){
        identifier_croplist viewmodel= ViewModelProviders.of(Farmer_DiseasIdentifier_profile1.this).get(identifier_croplist.class);
        viewmodel.getting_crops().observe(Farmer_DiseasIdentifier_profile1.this, new Observer<getIdentifier_crops_response>() {
            @Override
            public void onChanged(getIdentifier_crops_response getIdentifier_crops_response) {
                if (getIdentifier_crops_response.getModel()!=null){
                    identifier_model_croplist model = getIdentifier_crops_response.getModel();
                    discrops=model.getcropsd();
                    pestcrops=model.getcropsp();
                    getcroplist();
                    getImages();
                    //getImages(pestcrops,2);
                    designgrid();
                }
                else{
                    Toast.makeText(Farmer_DiseasIdentifier_profile1.this, "Can not access here.", Toast.LENGTH_SHORT).show();
                }
            }
        });}

    private void  getImages() {
       // Log.d("page", "onResponse: " + croplist);
        ArrayList<Integer> num=new ArrayList<>();
        for(int ij=0;ij<croplist.size();ij+=1){
            String imagename= croplist.get(ij);
            String imagecrop=imagename.replace(" ","").toLowerCase()+"_iasri";
            int ids=R.drawable.rice;
            try{ ids = getResources().getIdentifier(imagecrop ,
                    "drawable", this.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            num.add(ids);
        }discrops_img=num;

    }
    void getcroplist(){
//        croplist.clear();
//        number_exist.clear();
        ArrayList<String> cpl=new ArrayList<>();
        ArrayList<Integer> num=new ArrayList<>();
        if (discrops.isEmpty()){
            Toast.makeText(Farmer_DiseasIdentifier_profile1.this, "No disease model available", Toast.LENGTH_LONG).show();

        }
        if (pestcrops.isEmpty()){
            Toast.makeText(Farmer_DiseasIdentifier_profile1.this, "No pest model available", Toast.LENGTH_LONG).show();

        }
        for (int h=0;h<discrops.size();h+=1){
            String cropf=discrops.get(h);
                cpl.add(cropf);
                if (pestcrops.contains(cropf)){
                    num.add(3);
                }else{
                    num.add(1);
                }

            }
        for(int k =0;k<pestcrops.size();k+=1){
            String crop=pestcrops.get(k);
            if (cpl.contains(crop)){
                int g=0;
            }else{cpl.add(crop);
                num.add(2);

            }
        }
        croplist=cpl;
        number_exist=num;
    }

    private void designgrid(){
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Farmer_DiseasIdentifier_profile1.this, 2);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Model_DashboardContent> model_level_creations = dashboardContent();
        //creating adapter
        dashboardAdapter = new DashboardAdapter(Farmer_DiseasIdentifier_profile1.this, model_level_creations);
        //attaching adapter to the RecyclerView
        recyclerView.setAdapter(dashboardAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(Farmer_DiseasIdentifier_profile1.this, recyclerView, new RecyclerTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                String cropname= croplist.get(position);
                go_to_identify_disease(cropname,number_exist.get(position));

            }

            @Override
            public void onLongClick(View view, int position)
            {

            }

        }));
///////////

    }
    public void go_to_identify_disease(String name,int numb)
    {
         Intent intent = new Intent(Farmer_DiseasIdentifier_profile1.this, Farmer_Disease_identifier_pest_Identifier_Activity.class);
        intent.putExtra("crop", name);
        intent.putExtra("number", numb);
        intent.putExtra("farmerIdentification",farmerIdentification);
        startActivity(intent);

       /* Intent intent = new Intent(Farmer_DiseasIdentifier_profile1.this, Identify_disease.class);
        intent.putExtra("crop", name);
        intent.putExtra("farmerIdentification",farmerIdentification);
        startActivity(intent);*/
    }


    private ArrayList<Model_DashboardContent> dashboardContent()
    {
        ArrayList<Model_DashboardContent> model_dashboardContents = new ArrayList<>();
        for (int i = 0; i < croplist.size(); i++) {
            Model_DashboardContent model_dashboardContent = new Model_DashboardContent();
            model_dashboardContent.setGetGridText(croplist.get(i));
            model_dashboardContent.setGridImage(discrops_img.get(i));
            model_dashboardContents.add(model_dashboardContent);
        }

        return model_dashboardContents;
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
        switch (item.getItemId())

        {

            case android.R.id.home:

                finish();

                break;

            case R.id.logout:
                signout();

                break;
            default:
                break;

        }

        return true;
    }

    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(getApplicationContext());
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();

                shared_pref.remove_shared_preference(getApplicationContext());

            }
        });
        opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        opt.show();
    }

   /* @Override
    public void onBackPressed() {


        Toast.makeText(Farmer_DiseasIdentifier_profile1.this, "No Back.Please logout", Toast.LENGTH_LONG).show();

    }
*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}