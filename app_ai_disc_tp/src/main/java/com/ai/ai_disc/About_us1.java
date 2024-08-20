package com.ai.ai_disc;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.multidex.BuildConfig;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class About_us1 extends AppCompatActivity {

    private static final String TAG = "About_us1";
//    @BindView(R.id.app_version)
//    TextView app_version;
    ArrayList<String> email=new ArrayList<>();
    ArrayList<String> address=new ArrayList<>();
    ArrayList<String> full_name=new ArrayList<>();
    LinearLayout lin;
//    @BindView(R.id.updateimg)
//    ImageView check_update;
    int MY_REQUEST_CODE=0;
    InternetReceiver internet = new InternetReceiver();
    AppUpdateManager appUpdateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("About Us");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        internet = new InternetReceiver();


        appUpdateManager = AppUpdateManagerFactory.create(this);

        //check_update.setVisibility(View.GONE);

//        check_update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Log.i(TAG, "onClick:  inside on click ");
//                appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
//                    @Override
//                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
//
//                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
//                            // Request the update.
//
//
//                            Log.i(TAG, "onSuccess:  inside if " );
//                            Toast.makeText(About_us1.this," Update available ",Toast.LENGTH_LONG).show();
//                            /*
//                            try {
//                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,
//                                        About_us1.this, MY_REQUEST_CODE);
//                            } catch (IntentSender.SendIntentException e) {
//                                e.printStackTrace();
//                            }
//
//                             */
//                            /*
//                            (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                                        // For a flexible update, use AppUpdateType.FLEXIBLE
//                                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
//                            Log.i(TAG, "onSuccess:  inside else ");
//
//                            Toast.makeText(About_us1.this," flexible ",Toast.LENGTH_LONG).show();
//
//                        }
//                             */
//
//
//                        }else{
//                            Toast.makeText(About_us1.this," Update not available ",Toast.LENGTH_LONG).show();
//                        }
//
//
//                    }
//                });
//            }
//        });


    }
    void getvalidators(){
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/niblp/get_val")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // Log.d("hhh",response.toString());
                        JSONArray array1 = null;
                        try {
                            array1 = response.getJSONArray("list");
                            for (int i = 0; i < array1.length(); i++) {

                                JSONObject object = (JSONObject) array1.get(i);
                                email.add(object.optString("email"));
                                address.add(object.optString("address"));
                                full_name.add(object.optString("full_name"));
                            }
                            addview();




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
    void addview(){
        for (int j=0;j<email.size();j+=1){
            TextView newemail=new TextView(About_us1.this);
            TextView newaddress=new TextView(About_us1.this);
            LinearLayout.LayoutParams txt=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            newaddress.setLayoutParams(txt);
            newemail.setLayoutParams(txt);
            newaddress.setTypeface(null, Typeface.BOLD);

            newaddress.setTextColor(Color.parseColor("#964a3b"));
            newemail.setTypeface(null,Typeface.ITALIC);
            newaddress.setText(String.valueOf(j+1)+". "+address.get(j));
            newemail.setText(full_name.get(j)+" ; "+email.get(j));
            newemail.setGravity(Gravity.CENTER);
            newaddress.setGravity(Gravity.START);
            lin.addView(newaddress);
            lin.addView(newemail);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == MY_REQUEST_CODE) {

            if (resultCode != RESULT_OK) {
                //Log.i(TAG,"Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }else{
               // Log.i(TAG, "onActivityResult: result is ok");
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet,intentFilter);




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
    public void onStop(){
        super.onStop();
        unregisterReceiver(internet);

    }
    }
