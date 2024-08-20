package com.ai.ai_disc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.model.report_adapter18;
import com.ai.ai_disc.model.report_adapter18_admin;
import com.ai.ai_disc.model.reportdata;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class admin_reports extends AppCompatActivity {
    private int numb;
    LinearLayout searchhere;
    TextInputEditText searchtext;

    Context context;
    ImageView cross,searched,optbutton;
    ImageView search;
    private RecyclerView grid;

    report_adapter18 datare;
    ArrayList<reportdata> list;

    TextView noreports;
    int tool,fil;
    report_adapter18_admin ca;
    CardView sortlay,opt,cardfilter;

    ArrayList<String> followed;
    ArrayList<reportdata> s_index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);
        SwipeRefreshLayout lay= findViewById(R.id.refr);
        context=admin_reports.this;
        tool=0;

        fil=0;

        noreports=findViewById(R.id.noreports);
        searched=findViewById(R.id.search);


        searchhere=findViewById(R.id.searchhere);
        searchtext=findViewById(R.id.searchtext);
        search=findViewById(R.id.searchbutton);
        cross=findViewById(R.id.imageView13);

        cross.setVisibility(View.GONE);
        searchhere.setVisibility(View.GONE);






        lay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshreports();
                lay.setRefreshing(false);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hg=searchtext.getText().toString();
                if (hg.matches("")){
                    Toast.makeText(context, "Enter Something", Toast.LENGTH_SHORT).show();
                }
                else{
                    goforfliter(hg);
                }
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cross.setVisibility(View.GONE);
                searchhere.setVisibility(View.GONE);
                searchtext.getText().clear();
                grid.setAdapter(ca);
                tool=0;
            }
        });
        searched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tool==1){
//                    imageView6.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.ic_menu_search, getContext().getTheme()));
                    //grid.setAdapter(ca);
                    tool=0;
                    searchhere.setVisibility(View.GONE);

                    //sr.setVisibility(View.GONE);
                }else{
                    searchhere.setVisibility(View.VISIBLE);
                    tool=1;
                    //sr.setVisibility(View.VISIBLE);
                }

            }
        });
        s_index=new ArrayList<>();
        grid=findViewById(R.id.grid);

        list=new ArrayList<>();
        numb=0;
        addreports();
        ca = new report_adapter18_admin(context, list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        grid.setLayoutManager(mLayoutManager);
        grid.setItemAnimator(new DefaultItemAnimator());
        grid.setAdapter(ca);
    }
    private void addreports() {
        //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_access_time_24, getContext().getTheme()));
        JSONObject object = new JSONObject();
        int fg=list.size();
        try {

            object.put("number", numb);
            object.put("fl", user_singleton.getInstance().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_all_reportsadmin")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Log.d("datatattt",response.toString());
                            JSONArray array = response.getJSONArray("list");
                            //Log.d(TAG, "onResponse: " + array1);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                reportdata data=new reportdata();
                                data.setcrop_name(object.getString("crop_name"));
                                data.setlocate(object.getString("locate"));
                                data.setdpname(object.getString("dp_name"));
                                data.setdated(object.getString("dated"));

                                data.setreport_type(object.getString("report_type"));
                                data.setrecord_id(object.getString("record_id"));
                                data.setuser_name(object.getString("nm"));
                                data.sethead(object.getString("head"));
                                data.setuser_id(object.getString("user_id"));
                                data.setinfo(object.getString("info"));
                                data.setlikednumber(object.getString("likednumber"));
                                data.setprio(object.getString("priority"));
                                data.setfl(true);
                                data.setlk(false);

                                list.add(data);

                            }
                            ca.notifyDataSetChanged();
                            fil=0;
                            cardfilter.setVisibility(View.GONE);
                            sortlay.setVisibility(View.GONE);
                            optbutton.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_dehaze1_24, getTheme()));

                            int hg=list.size();
                            if (hg==0){
                                noreports.setVisibility(View.VISIBLE);opt.setVisibility(View.GONE);
                            }else{
                                noreports.setVisibility(View.GONE);
                                opt.setVisibility(View.VISIBLE);
                                if (hg==fg){
                                    //cardcall.setVisibility(View.GONE);
                                    //Toast.makeText(getContext(), "Showing All Reports", Toast.LENGTH_SHORT).show();

                                }else{
                                    //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_post_add_24, getContext().getTheme()));
                                    //Toast.makeText(getContext(), "Showing "+String.valueOf(list.size())+" Reports", Toast.LENGTH_SHORT).show();
                                }
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onError(ANError error) {

                    }
                });
    }
    private void goforfliter(String value) {
        //search.setText("searching...");
        s_index.clear();
        for(int s=0;s< list.size();s+=1){
            reportdata ad=list.get(s);

            if (ad.getdpname().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getdated().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.gethead().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") ||ad.getcrop_name().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getlocate().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getuser_name().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)")){
                s_index.add(ad);
            }
        }
        if (s_index.size()!=0) {
            datare = new report_adapter18(context, s_index);
            grid.setAdapter(datare);
            //search.setText("GO");
            cross.setVisibility(View.VISIBLE);

            Toast.makeText(context, "Showing "+String.valueOf(s_index.size())+" Results", Toast.LENGTH_SHORT).show();
            //imageView6.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_backspace_24, getContext().getTheme()));
        }
        else{
            //search.setText("GO");
            Toast.makeText(context, "Nothing Found", Toast.LENGTH_SHORT).show();
        }


    }
    private void refreshreports() {
        //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_access_time_24, getContext().getTheme()));
        JSONObject object = new JSONObject();
        list.clear();
        int fg= 0;
        numb=0;
        try {

            object.put("number", numb);object.put("fl", user_singleton.getInstance().getUser_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_all_reports11")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Log.d("datatattt",response.toString());
                            JSONArray array = response.getJSONArray("list");
                            //Log.d(TAG, "onResponse: " + array1);
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                reportdata data=new reportdata();


                                data.setcrop_name(object.getString("crop_name"));
                                data.setlocate(object.getString("locate"));
                                data.setdpname(object.getString("dp_name"));
                                data.setdated(object.getString("dated"));

                                data.setreport_type(object.getString("report_type"));
                                data.setrecord_id(object.getString("record_id"));
                                data.setuser_name(object.getString("nm"));
                                data.sethead(object.getString("head"));
                                data.setinfo(object.getString("info"));
                                data.setuser_id(object.getString("user_id"));
                                data.setlikednumber(object.getString("likednumber"));
                                data.setprio(object.getString("priority"));
                                data.setfl(followed.contains(object.getString("user_id")));
                                data.setlk(object.getString("like").matches("1"));

                                list.add(data);

                            }
                            ca.notifyDataSetChanged();
                            grid.setAdapter(ca);
                            fil=0;
                            cardfilter.setVisibility(View.GONE);
                            sortlay.setVisibility(View.GONE);
                            optbutton.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_dehaze1_24, getTheme()));

                            int hg=list.size();
                            if (hg==fg){
                                //cardcall.setVisibility(View.GONE);
                                Toast.makeText(context, "Showing All Reports", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onError(ANError error) {

                    }
                });
    }
}