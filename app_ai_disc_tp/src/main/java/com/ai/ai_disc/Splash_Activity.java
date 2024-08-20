package com.ai.ai_disc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ai.ai_disc.identify.BlankActivity;


public class Splash_Activity extends AppCompatActivity {

    private static final String TAG = "Splash_Activity";
    splash_viewmodel splash;
    View decorView;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate: ");
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        thrd();

//        decorView = getWindow().getDecorView();
//         //int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//         int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION ;
//
//        decorView.setSystemUiVisibility(uiOptions);
//
//        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int i) {
//                if (i == 0) {
//                    decorView.setSystemUiVisibility(View.GONE);
//                }
//            }
//        });
//
//
//
//
//        splash = ViewModelProviders.of(Splash_Activity.this).get(splash_viewmodel.class);
//        splash.get().observe(Splash_Activity.this, new Observer<splash_model>() {
//            @Override
//            public void onChanged(@Nullable splash_model model) {
//                // editText.setText(charSequence);
//                Log.d(TAG, "onChanged: ");
//                prefManager = new PrefManager(Splash_Activity.this);
//                Log.d(TAG, "onChanged 1: ");
//                if (!prefManager.isFirstTimeLaunch()) {
//                    Log.d(TAG, "onChanged 2: ");
//                    launchHomeScreen();
//                    Log.d(TAG, "onChanged 3 : ");
//
//                } else {
//                    Log.d(TAG, "onChanged 4 : ");
//                    Intent intent = new Intent(Splash_Activity.this, Welcome_screen.class);
//                    startActivity(intent);
//                    finish();
//                    Log.d(TAG, "onChanged 5 : ");
//                }
//
//                Log.d(TAG, "onChanged 6 : ");
//            }
//        });


    }
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    private void thrd(){
        Thread thf = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                   // Log.d(TAG, "onChanged: ");
                    prefManager = new PrefManager(Splash_Activity.this);
                    //Log.d(TAG, "onChanged 1: ");
                    if (!prefManager.isFirstTimeLaunch()) {
                       // Log.d(TAG, "onChanged 2: ");
                        launchHomeScreen();
                       // Log.d(TAG, "onChanged 3 : ");

                    } else {
                       // Log.d(TAG, "onChanged 4 : ");
                        Intent intent = new Intent(Splash_Activity.this, Welcome_screen.class);
                        startActivity(intent);
                        finish();
                       // Log.d(TAG, "onChanged 5 : ");
                    }

                   // Log.d(TAG, "onChanged 6 : ");

                }

            }
        };thf.start();
    }

    private void launchHomeScreen() {
       // Log.d(TAG, "launchHomeScreen 1: ");
        prefManager.setFirstTimeLaunch(false);
        Intent inh=new Intent(Splash_Activity.this, BlankActivity.class);
        inh.putExtra("loops",1);
        startActivity(inh);
        finish();
       // Log.d(TAG, "launchHomeScreen 2 : ");
    }

    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

            decorView.setSystemUiVisibility(hide());
        }

    }

    public int hide() {
        return View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    }

     */




}

