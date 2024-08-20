package com.ai.ai_disc;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;


import androidx.multidex.MultiDex;

import com.androidnetworking.AndroidNetworking;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;

public class my_application extends Application {

    int least_character_user_name = 8;
    int max_character_user_name = 20;
    int least_character_password = 8;
    int max_character_password = 20;
    ArrayList<String> allowed_character = new ArrayList<String>(
            Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s"
                    , "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"
                    , "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6"
                    , "7", "8", "9", "-","@",".","_"));

    ArrayList<String> allowed_character_password = new ArrayList<String>(
            Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s"
                    , "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"
                    , "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6"
                    , "7", "8", "9", "#","$", "&","^","%","*","?","!","@"));

    ArrayList<String> integers=new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","8","9","0"));
    ArrayList<String> special=new ArrayList<String>(Arrays.asList("-","@", "$","!","#", "_","&","^","%","*","?","."));
    ArrayList<String> chart=new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s"
            , "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"
            , "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));
    @Override
    public void onCreate() {
        super.onCreate();

        // Adding an Network Interceptor for Debugging purpose :
        OkHttpClient okHttpClient = UnsafeHttpClient.getUnsafeOkHttpClient();


        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);




    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }


    public boolean check_user_name(String user_name, Context context) {

        if (user_name.isEmpty()) {

            Toast.makeText(context, "Username is empty.", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (user_name.contains(" ")) {

            Toast.makeText(context, "Space is not allowed in username.", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (int i = 0; i < user_name.length(); i++) {
            char a = user_name.charAt(i);
            if (allowed_character.contains(String.valueOf(a))) {


            } else {

                Toast.makeText(context, a + " is not allowed", Toast.LENGTH_LONG).show();
                return false;
            }

        }

        if (user_name.trim().length() < least_character_user_name) {

            Toast.makeText(context, " Username should not be less than " + least_character_user_name + " character.", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (user_name.trim().length() > max_character_user_name) {

            Toast.makeText(context, "Username should not be more than " + max_character_user_name + " character.", Toast.LENGTH_SHORT).show();

            return false;
        }


        return true;
    }


    public boolean check_password(String password, Context context) {

        if (password.isEmpty()) {

            Toast.makeText(context, "Password is empty.", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (password.contains(" ")) {

            Toast.makeText(context, "Space is not allowed in password.", Toast.LENGTH_SHORT).show();

            return false;
        }

        for (int i = 0; i < password.length(); i++) {
            char a = password.charAt(i);
            if (allowed_character_password.contains(String.valueOf(a))) {


            } else {

                Toast.makeText(context, a + " is not allowed", Toast.LENGTH_LONG).show();
                return false;
            }

        }

        if (password.trim().length() < least_character_password) {

            Toast.makeText(context, "Password should not be less than " + least_character_password + " character.", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (password.trim().length() > max_character_password) {

            Toast.makeText(context, "Password should not be more than " + max_character_password + " character.", Toast.LENGTH_SHORT).show();

            return false;
        }


        return true;
    }


    public boolean login_user_name(String user_name, Context context) {

        if (user_name.isEmpty()) {

            Toast.makeText(context, "Username is empty.", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (user_name.contains(" ")) {

            Toast.makeText(context, "Space is not allowed in username.", Toast.LENGTH_SHORT).show();

            return false;
        }

        for (int i = 0; i < user_name.length(); i++) {
            char a = user_name.charAt(i);
            if (allowed_character.contains(String.valueOf(a))) {


            } else {

//                Toast.makeText(context, a + " is not allowed", Toast.LENGTH_LONG).show();
//                return false;
            }

        }
        if (user_name.trim().length() > max_character_user_name) {

            Toast.makeText(context, "Username should not be more than " + max_character_user_name + " character.", Toast.LENGTH_SHORT).show();

            return false;
        }


        return true;
    }


    public boolean login_password(String password, Context context) {

        if (password.isEmpty()) {

            Toast.makeText(context, "Password is empty.", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (password.contains(" ")) {

            Toast.makeText(context, "Space is not allowed in password.", Toast.LENGTH_SHORT).show();

            return false;
        }


        for (int i = 0; i < password.length(); i++) {
            char a = password.charAt(i);
            if (allowed_character_password.contains(String.valueOf(a))) {


            } else {

//                Toast.makeText(context, a + " is not allowed", Toast.LENGTH_LONG).show();
//                return false;
            }

        }
        if (password.trim().length() > max_character_user_name) {

            Toast.makeText(context, "Password should not be more than " + max_character_user_name + " character.", Toast.LENGTH_SHORT).show();

            return false;
        }


        return true;
    }

    public boolean signup_password(String password, Context context) {

        if (password.isEmpty()) {

            Toast.makeText(context, "Password should not be empty !", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (password.contains(" ")) {

            Toast.makeText(context, "Space is not allowed in password !", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (password.length()<8) {

            Toast.makeText(context, "Password should be of at least 8 characters !", Toast.LENGTH_SHORT).show();

            return false;
        }
        int sp=0;
        int dg=0;
        int cdt=0;


        for (int i = 0; i < password.length(); i++) {
            char a = password.charAt(i);
            if (allowed_character_password.contains(String.valueOf(a))) {
                if (integers.contains(String.valueOf(a))){dg+=1;}
                if (special.contains(String.valueOf(a))){sp+=1;}
                if (chart.contains(String.valueOf(a))){cdt+=1;}


            } else {

                Toast.makeText(context, a + " is not allowed !", Toast.LENGTH_LONG).show();
                return false;
            }

        }
        if (sp==0 || dg==0 || cdt ==0){
            Toast.makeText(context, "Password should contain atleast \n one digit, special character and alphabet !", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.trim().length() > max_character_user_name) {

            Toast.makeText(context, "Password should not be more than " + max_character_user_name + " character.", Toast.LENGTH_SHORT).show();

            return false;
        }


        return true;
    }

    public String check_signup_password(String password, Context context) {

        if (password.isEmpty()) {

            //Toast.makeText(context, "Password should not be empty !", Toast.LENGTH_SHORT).show();

            return "Password should not be empty !";
        }
        if (password.contains(" ")) {

            //Toast.makeText(context, "Space is not allowed in password !", Toast.LENGTH_SHORT).show();

            return "Space is not allowed in password !";
        }
        if (password.length()<8) {

            //Toast.makeText(context, "Password should be of atleast 8 characters !", Toast.LENGTH_SHORT).show();

            return "Password should be of atleast 8 characters !";
        }
        int sp=0;
        int dg=0;
        int cdt=0;


        for (int i = 0; i < password.length(); i++) {
            char a = password.charAt(i);
            if (allowed_character_password.contains(String.valueOf(a))) {
                if (integers.contains(String.valueOf(a))){dg+=1;}
                if (special.contains(String.valueOf(a))){sp+=1;}
                if (chart.contains(String.valueOf(a))){cdt+=1;}


            } else {

                //Toast.makeText(context, a + " is not allowed !", Toast.LENGTH_LONG).show();
                //return String.valueOf(a + " is not allowed !");
            }

        }
//        if (sp==0 ){
//            //Toast.makeText(context, "Password should contain atleast \n one digit, special character and alphabet !", Toast.LENGTH_LONG).show();
//            return "must contain a special character (e.g. @, $, !, #)";
//        }
//        if (dg==0 ){
//            //Toast.makeText(context, "Password should contain atleast \n one digit, special character and alphabet !", Toast.LENGTH_LONG).show();
//            return "must contain a digit (0-9)";
//        }
//        if (cdt==0 ){
//            //Toast.makeText(context, "Password should contain atleast \n one digit, special character and alphabet !", Toast.LENGTH_LONG).show();
//            return "must contain an alphabet (a-z/A-Z)";
//        }
        if (password.trim().length() > max_character_user_name) {

            //Toast.makeText(context, , Toast.LENGTH_SHORT).show();

            return "Password should not be more than " + max_character_user_name + " character.";
        }


        return "Perfect!";
    }
///////////
    public String check_signup_username(String Username, Context context) {

        if (Username.isEmpty()) {

            //Toast.makeText(context, "Password should not be empty !", Toast.LENGTH_SHORT).show();

            return "Username should not be empty !";
        }
        if (Username.contains(" ")) {

            //Toast.makeText(context, "Space is not allowed in password !", Toast.LENGTH_SHORT).show();

            return "Space is not allowed in Username !";
        }
        if (Username.length()<8) {

            //Toast.makeText(context, "Password should be of atleast 8 characters !", Toast.LENGTH_SHORT).show();

            return "Username should be of atleast 8 characters !";
        }



        int sp=0;
        int dg=0;
        int cdt=0;


        for (int i = 0; i < Username.length(); i++) {
            char a = Username.charAt(i);
            if (allowed_character.contains(String.valueOf(a))) {
                if (integers.contains(String.valueOf(a))){dg+=1;}
                if (special.contains(String.valueOf(a))){sp+=1;}
                if (chart.contains(String.valueOf(a))){cdt+=1;}


            } else {

                //Toast.makeText(context, a + " is not allowed !", Toast.LENGTH_LONG).show();
                //return String.valueOf(a + " is not allowed !");
            }

        }
        if (sp==0 || dg==0 || cdt ==0){
            //Toast.makeText(context, "Password should contain atleast \n one digit, special character and alphabet !", Toast.LENGTH_LONG).show();
            //return "Username should contain atleast \n one digit, special character and alphabet !";
        }

        if (Username.trim().length() > max_character_user_name) {

            //Toast.makeText(context, , Toast.LENGTH_SHORT).show();

            return "Username should not be more than " + max_character_user_name + " character.";
        }


        return "Perfect!";
    }
}

