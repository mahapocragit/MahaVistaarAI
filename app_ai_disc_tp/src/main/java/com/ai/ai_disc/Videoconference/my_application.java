package com.ai.ai_disc.Videoconference;

import android.app.Application;
import android.content.Context;

import com.androidnetworking.AndroidNetworking;

public class my_application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        AndroidNetworking.initialize(getApplicationContext());


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //  MultiDex.install(this);

    }
}
