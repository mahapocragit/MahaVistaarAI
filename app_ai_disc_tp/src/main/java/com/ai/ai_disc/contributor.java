package com.ai.ai_disc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ai.ai_disc.model.contributor_model;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.*;
public class contributor extends AppCompatActivity {
    InternetReceiver internet;
    ArrayList<contributor_model> dev=new ArrayList<>();
    ArrayList<contributor_model> ai=new ArrayList<>();
    ArrayList<contributor_model> domain=new ArrayList<>();
    ArrayList<contributor_model> minister=new ArrayList<>();
    ArrayList<contributor_model> dg=new ArrayList<>();
    ArrayList<contributor_model> admin=new ArrayList<>();
    ArrayList<contributor_model> aed=new ArrayList<>();
    static int smin=0,sadmin=0,sai=0,sdomain=0,sdg=0,saed=0,sdev=0;


RecyclerView ministergrid,admingrid,aigrid, domaingrid,aedgrid,dggrid,devgrid;

custom_adapter_contributor adapter_min,adapter_aed,adapter_ai,adapter_admin,adapter_dg,adapter_domain,devadapater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_contributors);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Credits");
        internet = new InternetReceiver();
        getcredits();
        admingrid=findViewById(R.id.grid_admin);
        domaingrid=findViewById(R.id.grid_domain);
        dggrid=findViewById(R.id.grid_icar);
        aedgrid=findViewById(R.id.grid_aed);
        ministergrid=findViewById(R.id.grid_minister);
        aigrid=findViewById(R.id.grid_ai);
        devgrid=findViewById(R.id.grid_dev);

        adapter_min=new custom_adapter_contributor(contributor.this,minister);

         adapter_admin=new custom_adapter_contributor(contributor.this,admin);

         adapter_aed=new custom_adapter_contributor(contributor.this,aed);

         adapter_ai=new custom_adapter_contributor(contributor.this,ai);

         adapter_domain=new custom_adapter_contributor(contributor.this,domain);

         adapter_dg=new custom_adapter_contributor(contributor.this,dg);
         devadapater=new custom_adapter_contributor(contributor.this,dev);

        RecyclerView.LayoutManager mLayoutManagerai = new LinearLayoutManager(getApplicationContext());
        aigrid.setLayoutManager(mLayoutManagerai);
        aigrid.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManagerdo = new LinearLayoutManager(getApplicationContext());
        domaingrid.setLayoutManager(mLayoutManagerdo);
        domaingrid.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManagerad = new LinearLayoutManager(getApplicationContext());
        admingrid.setLayoutManager(mLayoutManagerad);
        admingrid.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManageraed = new LinearLayoutManager(getApplicationContext());
        aedgrid.setLayoutManager(mLayoutManageraed);
        aedgrid.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManagerdg = new LinearLayoutManager(getApplicationContext());
        dggrid.setLayoutManager(mLayoutManagerdg);
        dggrid.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mLayoutManagerdev = new LinearLayoutManager(getApplicationContext());
        devgrid.setLayoutManager(mLayoutManagerdev);
        devgrid.setItemAnimator(new DefaultItemAnimator());

        dggrid.setAdapter(adapter_dg);
        domaingrid.setAdapter(adapter_domain);
        aigrid.setAdapter(adapter_ai);
        aedgrid.setAdapter(adapter_aed);
        admingrid.setAdapter(adapter_admin);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ministergrid.setLayoutManager(mLayoutManager);
        ministergrid.setItemAnimator(new DefaultItemAnimator());


        ministergrid.setAdapter(adapter_min);
        devgrid.setAdapter(devadapater);



//        LinearLayout minview=findViewById(R.id.ll_minister_switch);
//        LinearLayout aiview=findViewById(R.id.ll_ai_switch);
//        LinearLayout domainview=findViewById(R.id.ll_domain_switch);
//        LinearLayout aedview=findViewById(R.id.ll_aed_switch);
//        LinearLayout dgview=findViewById(R.id.ll_icar_switch);
//        LinearLayout adminview=findViewById(R.id.ll_admin_switch);
//        LinearLayout devview=findViewById(R.id.ll_dev_switch);
//        minview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (smin==0){
//                    ministergrid.setVisibility(View.VISIBLE);
//                    //minview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
//                    minview.setL(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
//                    smin=1;
//                }
//                else {
//                    ministergrid.setVisibility(View.GONE);
//                    minview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
//                    smin=0;
//                }
//            }
//        });
//        aiview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (sai==0){
//                    aigrid.setVisibility(View.VISIBLE);
//                    aiview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
//                    sai=1;
//                }
//                else {
//                    aigrid.setVisibility(View.GONE);
//                    aiview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
//                    sai=0;
//                }
//            }
//        });
//        domainview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (sdomain==0){
//                    domaingrid.setVisibility(View.VISIBLE);
//                    domainview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
//                    sdomain=1;
//                }
//                else {
//                    domaingrid.setVisibility(View.GONE);
//                    domainview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
//                    sdomain=0;
//                }
//            }
//        });
//        adminview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (sadmin==0){
//                    admingrid.setVisibility(View.VISIBLE);
//                    adminview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
//                    sadmin=1;
//                }
//                else {
//                    admingrid.setVisibility(View.GONE);
//                    adminview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
//                    sadmin=0;
//                }
//            }
//        });
//        aedview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (saed==0){
//                    aedgrid.setVisibility(View.VISIBLE);
//                    aedview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
//                    saed=1;
//                }
//                else {
//                    aedgrid.setVisibility(View.GONE);
//                    aedview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
//                    saed=0;
//                }
//            }
//        });
//        dgview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (sdg==0){
//                    dggrid.setVisibility(View.VISIBLE);
//                    dgview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
//                    sdg=1;
//                }
//                else {
//                    dggrid.setVisibility(View.GONE);
//                    dgview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
//                    sdg=0;
//                }
//            }
//        });
//        devview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (sdev==0){
//                    devgrid.setVisibility(View.VISIBLE);
//                    devview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
//                    sdev=1;
//                }
//                else {
//                    devgrid.setVisibility(View.GONE);
//                    devview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
//                    sdev=0;
//                }
//            }
//        });









        ImageView minview=findViewById(R.id.minister_switch);
        ImageView aiview=findViewById(R.id.ai_switch);
        ImageView domainview=findViewById(R.id.domain_switch);
        ImageView aedview=findViewById(R.id.aed_switch);
        ImageView dgview=findViewById(R.id.icar_switch);
        ImageView adminview=findViewById(R.id.admin_switch);
        ImageView devview=findViewById(R.id.dev_switch);
        minview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smin==0){
                    ministergrid.setVisibility(View.VISIBLE);
                    minview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
                    smin=1;
                }
                else {
                    ministergrid.setVisibility(View.GONE);
                    minview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
                    smin=0;
                }
            }
        });
        aiview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sai==0){
                    aigrid.setVisibility(View.VISIBLE);
                    aiview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
                    sai=1;
                }
                else {
                    aigrid.setVisibility(View.GONE);
                    aiview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
                    sai=0;
                }
            }
        });
        domainview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sdomain==0){
                    domaingrid.setVisibility(View.VISIBLE);
                    domainview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
                    sdomain=1;
                }
                else {
                    domaingrid.setVisibility(View.GONE);
                    domainview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
                    sdomain=0;
                }
            }
        });
        adminview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sadmin==0){
                    admingrid.setVisibility(View.VISIBLE);
                    adminview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
                    sadmin=1;
                }
                else {
                    admingrid.setVisibility(View.GONE);
                    adminview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
                    sadmin=0;
                }
            }
        });
        aedview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saed==0){
                    aedgrid.setVisibility(View.VISIBLE);
                    aedview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
                    saed=1;
                }
                else {
                    aedgrid.setVisibility(View.GONE);
                    aedview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
                    saed=0;
                }
            }
        });
        dgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sdg==0){
                    dggrid.setVisibility(View.VISIBLE);
                    dgview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
                    sdg=1;
                }
                else {
                    dggrid.setVisibility(View.GONE);
                    dgview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
                    sdg=0;
                }
            }
        });
        devview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sdev==0){
                    devgrid.setVisibility(View.VISIBLE);
                    devview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_minimize_24, getApplicationContext().getTheme()));
                    sdev=1;
                }
                else {
                    devgrid.setVisibility(View.GONE);
                    devview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24, getApplicationContext().getTheme()));
                    sdev=0;
                }
            }
        });







    }
    @Override
    public void onStart() {

        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }



//    @Override
//    public void onStop() {
//        super.onStop();
//        unregisterReceiver(internet);
//
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                break;
            default:
                break;

        }

        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void getcredits(){
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_credits")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("hhh",response.toString());
                        JSONArray array1 = null;
                        try {
                            array1 = response.getJSONArray("data");
                            for (int i = 0; i < array1.length(); i++) {

                                JSONObject object = (JSONObject) array1.get(i);
                                if ((object.optString("type")).matches("3")){
                                    contributor_model admin2=new contributor_model();
                                    admin2.setfull_name(object.optString("full_name"));
                                    admin2.setinstitute(object.optString("institute"));
                                    admin2.settitle(object.optString("title"));
                                    admin2.setemail(object.optString("email"));
                                    admin2.setphone(object.optString("phone"));
                                    admin2.setdesignation(object.optString("designation"));
                                    admin2.setimagepath(object.optString("image"));
                                    try{
                                        admin2.setprio(Integer.parseInt(object.optString("prio")));
                                    }
                                    catch (Exception e){
                                        admin2.setprio(1000);
                                    }
                                    admin.add(admin2);
                                }

                                if ((object.optString("type")).matches("5")){
                                    contributor_model dg1=new contributor_model();
                                    dg1.setfull_name(object.optString("full_name"));
                                    dg1.setinstitute(object.optString("institute"));
                                    dg1.settitle(object.optString("title"));
                                    dg1.setemail(object.optString("email"));
                                    dg1.setphone(object.optString("phone"));
                                    dg1.setdesignation(object.optString("designation"));
                                    dg1.setimagepath(object.optString("image"));
                                    try{
                                        dg1.setprio(Integer.parseInt(object.optString("prio")));
                                    }
                                    catch (Exception e){
                                        dg1.setprio(1000);
                                    }
                                    dg.add(dg1);
                                }
                                if ((object.optString("type")).matches("4")){
                                    contributor_model admin4=new contributor_model();
                                    admin4.setfull_name(object.optString("full_name"));
                                    admin4.setinstitute(object.optString("institute"));
                                    admin4.settitle(object.optString("title"));
                                    admin4.setemail(object.optString("email"));
                                    admin4.setphone(object.optString("phone"));
                                    admin4.setdesignation(object.optString("designation"));
                                    admin4.setimagepath(object.optString("image"));
                                    try{
                                        admin4.setprio(Integer.parseInt(object.optString("prio")));
                                    }
                                    catch (Exception e){
                                        admin4.setprio(1000);
                                    }
                                    aed.add(admin4);
                                }
                                if ((object.optString("type")).matches("6")){
                                    contributor_model admin5=new contributor_model();
                                    admin5.setfull_name(object.optString("full_name"));
                                    admin5.setinstitute(object.optString("institute"));
                                    admin5.settitle(object.optString("title"));
                                    admin5.setemail(object.optString("email"));
                                    admin5.setphone(object.optString("phone"));
                                    admin5.setdesignation(object.optString("designation"));
                                    admin5.setimagepath(object.optString("image"));
                                    try{
                                        admin5.setprio(Integer.parseInt(object.optString("prio")));
                                    }
                                    catch (Exception e){
                                        admin5.setprio(1000);
                                    }
                                    minister.add(admin5);
                                }
                                if ((object.optString("type")).matches("2")){
                                    contributor_model admin5=new contributor_model();
                                    admin5.setfull_name(object.optString("full_name"));
                                    admin5.setinstitute(object.optString("institute"));
                                    admin5.settitle(object.optString("title"));
                                    admin5.setemail(object.optString("email"));
                                    admin5.setphone(object.optString("phone"));
                                    admin5.setdesignation(object.optString("designation"));
                                    admin5.setimagepath(object.optString("image"));
                                    try{
                                        admin5.setprio(Integer.parseInt(object.optString("prio")));
                                    }
                                    catch (Exception e){
                                        admin5.setprio(1000);
                                    }
                                    domain.add(admin5);
                                }
                                if ((object.optString("type")).matches("1")){
                                    contributor_model admin5=new contributor_model();
                                    admin5.setfull_name(object.optString("full_name"));
                                    admin5.setinstitute(object.optString("institute"));
                                    admin5.settitle(object.optString("title"));
                                    admin5.setemail(object.optString("email"));
                                    admin5.setphone(object.optString("phone"));
                                    admin5.setdesignation(object.optString("designation"));
                                    admin5.setimagepath(object.optString("image"));
                                    try{
                                        admin5.setprio(Integer.parseInt(object.optString("prio")));
                                    }
                                    catch (Exception e){
                                        admin5.setprio(1000);
                                    }

                                    ai.add(admin5);
                                }
                                if ((object.optString("type")).matches("7")){
                                    contributor_model admin5=new contributor_model();
                                    admin5.setfull_name(object.optString("full_name"));
                                    admin5.setinstitute(object.optString("institute"));
                                    admin5.settitle(object.optString("title"));
                                    admin5.setemail(object.optString("email"));
                                    admin5.setphone(object.optString("phone"));
                                    admin5.setdesignation(object.optString("designation"));
                                    admin5.setimagepath(object.optString("image"));
                                    try{
                                        admin5.setprio(Integer.parseInt(object.optString("prio")));
                                    }
                                    catch (Exception e){
                                        admin5.setprio(1000);
                                    }

                                    dev.add(admin5);
                                }


                            }

                            minister.sort(Comparator.comparing(contributor_model::getprio));
                            dg.sort(Comparator.comparing(contributor_model::getprio));
                            admin.sort(Comparator.comparing(contributor_model::getprio));
                            aed.sort(Comparator.comparing(contributor_model::getprio));
                            try{
                                domain.sort(Comparator.comparing(contributor_model::getprio));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try{
                                ai.sort(Comparator.comparing(contributor_model::getprio));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try{
                                dev.sort(Comparator.comparing(contributor_model::getprio));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                            Log.d("nnnn",String.valueOf(minister.size()+String.valueOf(admin.size())));

                            adapter_admin.notifyDataSetChanged();
                            adapter_aed.notifyDataSetChanged();
                            adapter_ai.notifyDataSetChanged();
                            adapter_dg.notifyDataSetChanged();
                            adapter_min.notifyDataSetChanged();
                            adapter_domain.notifyDataSetChanged();
                            devadapater.notifyDataSetChanged();

                            dggrid.setAdapter(adapter_dg);
                            domaingrid.setAdapter(adapter_domain);
                            aigrid.setAdapter(adapter_ai);
                            aedgrid.setAdapter(adapter_aed);
                            admingrid.setAdapter(adapter_admin);
                            devgrid.setAdapter(devadapater);
                            ministergrid.setAdapter(adapter_min);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.d(TAG, "onResponse: " + array1);

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
}