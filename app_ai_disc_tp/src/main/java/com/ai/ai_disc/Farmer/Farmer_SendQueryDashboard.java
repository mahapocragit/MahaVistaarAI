package com.ai.ai_disc.Farmer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.ai.ai_disc.Login;

import com.ai.ai_disc.R;
import com.ai.ai_disc.VC.ListOfCropsActivity;
import com.ai.ai_disc.Videoconference.Login_VideoCalling;
import com.ai.ai_disc.shared_pref;

public class Farmer_SendQueryDashboard extends AppCompatActivity {

    LinearLayout farmerQueryList,farmerVideoCalling;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_send_query_dashboard);
        farmerQueryList=(LinearLayout) findViewById(R.id.farmerquerylist);
        farmerVideoCalling=(LinearLayout)findViewById(R.id.farmerVideoCalling);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        farmerQueryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(Farmer_SendQueryDashboard.this, list_of_query.class);
                startActivity(intent);
            }
        });
        farmerVideoCalling.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Farmer_SendQueryDashboard.this, Login_VideoCalling.class);
                intent.putExtra("type","2");
                startActivity(intent);

              /*  Intent intent=new Intent(Farmer_SendQueryDashboard.this, ListOfCropsActivity.class);
                startActivity(intent);*/
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



            case android.R.id.home:

                finish();

                break;
            case R.id.logout:

                Intent intent = new Intent(Farmer_SendQueryDashboard.this, Login.class);
                startActivity(intent);
                finish();

                shared_pref.remove_shared_preference(Farmer_SendQueryDashboard.this);

                break;
            default:
                break;

        }

        return true;
    }

}