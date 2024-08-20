package com.ai.ai_disc;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;


public class shared_pref {

    public static SharedPreferences sp;
    public static String pre = "mypreference";

    //Encrypted file:
    public static EncryptedSharedPreferences esp;
    public static String preEncrypted = "preferences";

    public static void remove_shared_preference(Context context) {

        //Before Encrption;
            /*shared_pref.sp =context.getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared_pref.sp.edit();
            editor.remove("username");
            editor.remove("password");
            editor.commit();*/

        //for signout it is mandatory;
        //After Encrption
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Initialize/open an instance of EncryptedSharedPreferences on below line.
        try {
            // initializing our encrypted shared preferences and passing our key to it.
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    // passing a file name to share a preferences
                    "preferences",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            //get data from encrpypted shared pref, before removing field;
            String user_name_got = sharedPreferences.getString("username", "");
            String password_got = sharedPreferences.getString("password", "");

            // remove shared pref field;
            sharedPreferences.edit().remove("username").commit();
            sharedPreferences.edit().remove("password").commit();

            //get data from encrpypted shared pref, after removing field;
            String user_name_got1 = sharedPreferences.getString("username", "");
            String password_got1 = sharedPreferences.getString("password", "");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

