package com.ai.ai_disc;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;




public class InternetNotWorking extends AppCompatActivity {

    Button retry;
    String status_of_internet="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_not_working);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC");

        retry=(Button)findViewById(R.id.retry);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                status_of_internet=NetworkStatusChecking.get_connectivity_status(InternetNotWorking.this);

                if(status_of_internet.equals("not_connected")){
                    Toast.makeText(InternetNotWorking.this,"Internet not connected.",Toast.LENGTH_LONG).show();
                }else{

                    Intent intent = new Intent(InternetNotWorking.this,Login.class);
                    startActivity(intent);

                    finish();
                    Toast.makeText(InternetNotWorking.this,"Internet Connected.",Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    @Override
    public  void onBackPressed(){
        Toast.makeText(InternetNotWorking.this,"No Back.",Toast.LENGTH_LONG).show();

    }

}
