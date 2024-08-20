package com.ai.ai_disc.Videoconference;

import android.content.Context;
import android.content.SharedPreferences;


public class shared_pref {

    public static SharedPreferences sp;
    public static String pre="mypreference";


    public static void remove_shared_preference(Context context){


        shared_pref.sp =context.getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_pref.sp.edit();

        editor.remove("email");
        editor.remove("password");

        editor.commit();
    }



}

