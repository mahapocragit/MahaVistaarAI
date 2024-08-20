package com.ai.ai_disc;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ai.ai_disc.Farmer.Model_expert_query_content;
import com.ai.ai_disc.Farmer.custom_adapter_query_answer;
import com.ai.ai_disc.Farmer.head_query_list;
import com.ai.ai_disc.Videoconference.Login_VideoCalling;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Expert_profile extends AppCompatActivity {

    private static final String TAG = "Expert_profile";
    ArrayList<String> web;
    ArrayList<Integer> imageId;
    @BindView(R.id.hello)
    TextView hello;
    @BindView(R.id.grid)
    GridView grid;
    InternetReceiver internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC Expert");

        ButterKnife.bind(this);

        internet = new InternetReceiver();

        web = new ArrayList<String>();
        imageId = new ArrayList<Integer>();

        hello.setText(" Welcome , " + get_name());
        hello.startAnimation(AnimationUtils.loadAnimation(Expert_profile.this, R.anim.slide_down));


        web.add("Ask Query");
      /*  web.add("Query solve via Video calling");*/

        imageId.add(R.drawable.view_record);
        web.add("Appointments");
        /*  web.add("Query solve via Video calling");*/

        imageId.add(R.drawable.ic_baseline_video_camera_front_24);
       // imageId.add(R.drawable.query);
      /*  imageId.add(R.drawable.videocalling);*/

        custom_grid_adapter1 adapter1 = new custom_grid_adapter1(Expert_profile.this, web, imageId);
        grid.setAdapter(adapter1);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

/*
                if (position == 0)
                {

                    Intent intent6 = new Intent(Expert_profile.this, Image_list.class);
                    startActivity(intent6);
                }*/
                 if (position == 0)
                {

                    Intent intent = new Intent(Expert_profile.this, head_query_list.class);

                     startActivity(intent);

                }

                 else if (position == 1)
                 {

                     //Intent intent = new Intent(Profile.this, Insect_form.class);
                     // startActivity(intent);

                     Intent intent=new Intent(Expert_profile.this, expert_appointment.class);
                     //intent.putExtra("type","1");
                     startActivity(intent);
                 }


            }
        });

    }


    public String get_name() {
        return user_singleton.getInstance().getUser_name();
    }


    @Override
    public void onBackPressed()
    {
        Toast.makeText(Expert_profile.this, "Please logout.", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                Intent intent = new Intent(Expert_profile.this, Login.class);
                startActivity(intent);

                shared_pref.remove_shared_preference(Expert_profile.this);

                break;
            default:
                break;

        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }


    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(internet);

    }
}