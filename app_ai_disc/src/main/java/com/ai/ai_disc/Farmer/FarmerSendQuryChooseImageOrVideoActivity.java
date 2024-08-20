package com.ai.ai_disc.Farmer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ai.ai_disc.Login;

import com.ai.ai_disc.R;
import com.ai.ai_disc.shared_pref;

public class FarmerSendQuryChooseImageOrVideoActivity extends AppCompatActivity {

    Button sendImageButton,expertListButton;
    String cropId="",cropName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_send_qury_choose_image_or_video);
        sendImageButton=(Button)findViewById(R.id.sendImage);
        expertListButton=(Button) findViewById(R.id.expertList);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        cropId=data.getString("cropID");
        cropName=data.getString("cropName");
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(FarmerSendQuryChooseImageOrVideoActivity.this, list_of_query.class);
                Bundle data1 = new Bundle();
                data1.putString("cropID",cropId);
                data1.putString("cropName",cropName);
                intent.putExtras(data1);
                startActivity(intent);

            }
        });
        expertListButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(FarmerSendQuryChooseImageOrVideoActivity.this,"Solve problem via Video Calling",Toast.LENGTH_LONG).show();

            }
        });

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
        switch (item.getItemId()) {
            case R.id.logout:

                Intent intent = new Intent(FarmerSendQuryChooseImageOrVideoActivity.this, Login.class);
                startActivity(intent);
                finish();

                shared_pref.remove_shared_preference(FarmerSendQuryChooseImageOrVideoActivity.this);

                break;
            default:
                break;

        }

        return true;
    }

}