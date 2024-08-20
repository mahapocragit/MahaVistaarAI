package com.ai.ai_disc;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class Image_activity extends AppCompatActivity {

    InternetReceiver internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        internet = new InternetReceiver();


        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");

        // System.out.println(" bitmap is:"+bitmap);

    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);

        // InternetReceiver internet = new InternetReceiver();


    }


    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(internet);

    }

}
