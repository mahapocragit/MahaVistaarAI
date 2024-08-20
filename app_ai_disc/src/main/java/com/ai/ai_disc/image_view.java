package com.ai.ai_disc;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.widget.ImageView;

import com.bumptech.glide.Glide;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class image_view extends AppCompatActivity {

    String url = "";
    ImageView image;

    ImageView back_button;
    InternetReceiver internet = new InternetReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC");

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        url = data.getString("url");


        image = (ImageView) findViewById(R.id.image);
        /*
        back_button=(ImageView)findViewById(R.id.back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

         */


        Glide.with(image_view.this)
                .load(url)
                .error(R.drawable.gray)
                .into(image);


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
