package com.ai.ai_disc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;


public class Forgot_password extends AppCompatActivity {

    private static final String TAG = "Forgot_password";
    TextInputEditText username;
    TextInputLayout textlayout;
    Button submit;
    ImageView us,em,cl;
    TextView help,fm,ex;
    int type=0;
    int ft=10;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        submit = findViewById(R.id.submit);
        textlayout=findViewById(R.id.textforget);
        us=findViewById(R.id.us);
        em=findViewById(R.id.em);
        help=findViewById(R.id.help);
        ex=findViewById(R.id.ex);
        fm=findViewById(R.id.fm);
        cl=findViewById(R.id.cl);
        ex.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ft=7;
                ex.setTextColor(Color.parseColor("#1dbfb7"));
                fm.setTextColor(Color.parseColor("#355250"));
            }
        });
        fm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ft=10;
                fm.setTextColor(Color.parseColor("#1dbfb7"));
                ex.setTextColor(Color.parseColor("#355250"));
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inb=new Intent(Forgot_password.this,needHelp.class);
                startActivity(inb);
            }
        });
        us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textlayout.setVisibility(View.VISIBLE);
                textlayout.setHint("Enter Registered User Name");
                submit.setVisibility(View.VISIBLE);
                type=1;
            }
        });
        em.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textlayout.setVisibility(View.VISIBLE);
                textlayout.setHint("Enter Registered Email Id");

                submit.setVisibility(View.VISIBLE);
                type=2;
            }
        });
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textlayout.setVisibility(View.VISIBLE);
                textlayout.setHint("Enter Registered Phone Number");

                submit.setVisibility(View.VISIBLE);
                type=3;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().trim().matches("")){
                    Toast.makeText(Forgot_password.this, "Enter Username", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (type!=0){
                        send(username.getText().toString(),type,ft);
                    }

                }
            }
        });

    }

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

    public void send(final String username,int ty,int ft) {



        /*
        {
  "username": "sample string 1",

}
         */


        progress = new ProgressDialog(Forgot_password.this);
        progress.setCancelable(false);
        progress.setMessage("Sending");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        JSONObject object = new JSONObject();
        try {
            object.put("username", username);
            object.put("ty", ty);
            object.put("account", ft);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.i(TAG, "object is: " + object);


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/ForgotPassword1")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progress.cancel();

                        try {


                            boolean result = response.getBoolean("result");
                            boolean error = response.getBoolean("error");
                            boolean username_email_found = response.getBoolean("username_email_found");


                            if (result == true && error == false && username_email_found == true) {
                                show_dialog("Username and Password are sent to Registered Email-ID.",0);
                            }
                            if (result == false && error == false && username_email_found == false) {
                                show_dialog("username or email or phone number not found",1);
                            }
                            if (result == false && error == true) {
                                show_dialog("Error",1);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        progress.cancel();

                        show_dialog("Error",1);
                        System.out.println(" error :" + error.getMessage());
                    }
                });
    }

    public void show_dialog(String message,int b) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Forgot_password.this);

        alertDialogBuilder.setTitle("Response");


        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {//yes
                    public void onClick(final DialogInterface dialog, int id) {
                        if (b==0){
                            dialog.cancel();
                            Intent intt=new Intent(Forgot_password.this,Login.class);
                            startActivity(intt);
                        }
                        else{

                        dialog.cancel();}
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();
    }
}