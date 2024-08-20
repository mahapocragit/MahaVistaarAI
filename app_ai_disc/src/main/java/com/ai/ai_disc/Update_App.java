package com.ai.ai_disc;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;


import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Update_App extends AppCompatActivity {

    private static final String TAG = "Update_App";

    @BindView(R.id.update)
    Button check_update;
    int MY_REQUEST_CODE = 10;
    InternetReceiver internet = new InternetReceiver();
    AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__app);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC");
        ButterKnife.bind(this);

        appUpdateManager = AppUpdateManagerFactory.create(this);

        //  check_update.setVisibility(View.GONE);

        check_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log.i(TAG, "onClick:  inside on click ");

                appUpdateManager
                        .getAppUpdateInfo()
                        .addOnSuccessListener(
                                appUpdateInfo -> {

                                    // Checks that the platform will allow the specified type of update.
                                    if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                        // Request the update.
                                        try {
                                            appUpdateManager.startUpdateFlowForResult(
                                                    appUpdateInfo,
                                                    AppUpdateType.IMMEDIATE, Update_App.this, MY_REQUEST_CODE);

                                           // Log.i(TAG, "sucess:  inside on click ");
                                        } catch (IntentSender.SendIntentException e) {
                                           // Log.i(TAG, "Fail:  inside on click ");
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(Update_App.this, "Immediate Update not available ", Toast.LENGTH_LONG).show();

                                    }


                                });



              /*  appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {

                      *//*  if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                        {
                            // Request the update.


                            Log.i(TAG, "onSuccess:  inside if ");
                            Toast.makeText(Update_App.this, " Update available ", Toast.LENGTH_LONG).show();


                            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                                Toast.makeText(Update_App.this, "Immediate Update available ", Toast.LENGTH_LONG).show();
                                try {
                                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,
                                            Update_App.this, MY_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(Update_App.this, "Immediate Update not available ", Toast.LENGTH_LONG).show();

                            }


                        } else {
                            Toast.makeText(Update_App.this, " Update not available ", Toast.LENGTH_LONG).show();
                        }*//*


                    }
                });*/
            }
        });


    }

    /*   @Override
       protected void onResume()
       {
           super.onResume();

           appUpdateManager
                   .getAppUpdateInfo()
                   .addOnSuccessListener(
                           appUpdateInfo -> {

                               if (appUpdateInfo.updateAvailability()
                                       == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                   // If an in-app update is already running, resume the update.
                                   try {
                                       appUpdateManager.startUpdateFlowForResult(
                                               appUpdateInfo,
                                               AppUpdateType.IMMEDIATE,
                                               this,
                                               MY_REQUEST_CODE);
                                   } catch (IntentSender.SendIntentException e) {
                                       e.printStackTrace();
                                   }
                               }
                           });
       }
   */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {

            if (resultCode != RESULT_OK) {
                // Log.i(TAG, "Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
               // Log.i("NOt_avi", "onActivityResult: result is ok");
                Toast.makeText(Update_App.this, " Update  failed. ", Toast.LENGTH_LONG).show();

            } else {
               // Log.i(TAG, "onActivityResult: result is ok");
                Toast.makeText(Update_App.this, " Update flow ok. ", Toast.LENGTH_LONG).show();

            }
        }
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
