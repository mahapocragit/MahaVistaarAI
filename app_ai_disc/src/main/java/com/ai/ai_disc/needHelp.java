package com.ai.ai_disc;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class needHelp extends AppCompatActivity {

    InternetReceiver internet;
    TextView app_version;
    ArrayList<String> email=new ArrayList<>();
    ArrayList<String> address=new ArrayList<>();
    LinearLayout lin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_help);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        setTitle("Help Desk");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        internet = new InternetReceiver();
        //TextView inst=findViewById(R.id.inst);




        //inst.setSelected(true);

        //app_version.setText("App Version :"+BuildConfig.VERSION_CODE);



    }




    @Override
    public void onStart() {

        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater inflator = getMenuInflater();
//        inflator.inflate(R.menu.menu1, menu);
//        return super.onCreateOptionsMenu(menu);
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
    public void onStop() {
        super.onStop();
        unregisterReceiver(internet);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}