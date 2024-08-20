package com.ai.ai_disc.Farmer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ai.ai_disc.InternetReceiver;
import com.ai.ai_disc.R;


public class Farmer_LoginActivity extends AppCompatActivity {
    InternetReceiver internet;
    TextView registerHereText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        internet = new InternetReceiver();
        getWindow().setSoftInputMode(1);
        //ButterKnife.bind(this);
        registerHereText=(TextView)findViewById(R.id.registerHere);
        registerHereText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Farmer_LoginActivity.this, Farmer_RegisterationPageActivity.class);
                startActivity(intent);
            }
        });
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