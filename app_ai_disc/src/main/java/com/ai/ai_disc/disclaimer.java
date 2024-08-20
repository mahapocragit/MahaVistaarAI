package com.ai.ai_disc;

import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.model.contributor_model;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

public class disclaimer extends AppCompatActivity {
    InternetReceiver internet;
    ArrayList<contributor_model> dev=new ArrayList<>();
    TextView nod,nop;
    LinearLayout pest,pestlin,disease,dislin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Disclaimer");
        internet = new InternetReceiver();
        getdis();
        //pest=findViewById(R.id.pest);
        //disease=findViewById(R.id.disease);
        pestlin=findViewById(R.id.pestlin);
        dislin=findViewById(R.id.dislin);
        nod=findViewById(R.id.nodisease);
        nop=findViewById(R.id.nopest);







    }
    @Override
    public void onStart() {

        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }
    void addview(String crop,JSONArray dslist,int t){
        ArrayList<String> bvm=new ArrayList<>();
        for (int y=0;y<dslist.length();y+=1){
            try {
                bvm.add(dslist.getString(y));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        LinearLayout linh=new LinearLayout(disclaimer.this);
        LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linh.setOrientation(LinearLayout.HORIZONTAL);
        param.setMargins(5,5,5,5);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1);
        params.setMargins(100,2,2,2);
        linh.setLayoutParams(param);
        TextView crp=new TextView(disclaimer.this);
        crp.setText(crop);
        crp.setLayoutParams(params);
        crp.setTextColor(Color.BLACK);
        crp.setGravity(Gravity.CENTER_VERTICAL);
        linh.addView(crp);
        LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        params1.setMargins(2,2,50,2);
        LinearLayout ds=new LinearLayout(disclaimer.this);
        ds.setOrientation(LinearLayout.VERTICAL);
        ds.setLayoutParams(params1);
        ds.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams parads=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        parads.setMargins(20,2,2,2);
    for (int d=0;d<bvm.size();d+=1) {
            TextView dm = new TextView(disclaimer.this);
            dm.setText(bvm.get(d));
            dm.setLayoutParams(parads);
            dm.setTextColor(Color.BLACK);
            ds.addView(dm);
        }
        linh.addView(ds);

        if (t==0){
            dislin.addView(linh);    }
        else{
            pestlin.addView(linh);
        }




    }



    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(internet);

    }
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
    void getdis(){
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_disclaimer")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("hhh",response.toString());
                        JSONArray array1 = null;
                        JSONArray array2 = null;
                        try {
                            array1 = response.getJSONArray("disease");
                            if (array1.length()==0){
                                //disease.setVisibility(View.GONE);
                                dislin.setVisibility(View.GONE);
                                nod.setVisibility(View.VISIBLE);
                            }
                            else{
                            for (int i = 0; i < array1.length(); i++) {

                                JSONObject object = (JSONObject) array1.get(i);
                                String bv=object.getString("crop")+" Diseases";
                                JSONArray arr = object.getJSONArray("dslist");
                                addview(bv,arr,0);

                            }}
                            array2 = response.getJSONArray("pest");
                            if (array2.length()==0){
                                //pest.setVisibility(View.GONE);
                                pestlin.setVisibility(View.GONE);
                                nop.setVisibility(View.VISIBLE);
                            }
                            else{

                            for (int i = 0; i < array2.length(); i++) {

                                JSONObject object = (JSONObject) array2.get(i);
                                String bv=object.getString("crop")+" Pests";
                                JSONArray arr = object.getJSONArray("dslist");
                                addview(bv,arr,1);

                            }}


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