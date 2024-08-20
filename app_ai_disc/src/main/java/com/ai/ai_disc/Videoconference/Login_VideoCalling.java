package com.ai.ai_disc.Videoconference;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ai.ai_disc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Login_VideoCalling extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText username, password;
    Button login, demo, signup_button, signup_expert_button;
    private static final String TAG = "Login";
    ProgressDialog progress;
    String type;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Login");
         extras = getIntent().getExtras();
        if (extras != null)
        {
             type = extras.getString("type");

        }



        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        demo = findViewById(R.id.demo);
        signup_button = findViewById(R.id.signup);
        signup_expert_button = findViewById(R.id.signup_expert);

       if(type!=null)
       {
           if(type.equals("1"))
           {
                signup_button.setVisibility(View.GONE);

           }
           if(type.equals("2"))
           {
                signup_expert_button.setVisibility(View.GONE);
           }
       }


        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        String email_id = shared_pref.sp.getString("email", "");
        String password_got = shared_pref.sp.getString("password", "");

        if(!email_id.isEmpty() && !password_got.isEmpty()){
            do_login(email_id,password_got);
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                do_login(username.getText().toString(),password.getText().toString());
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_VideoCalling.this, signup.class);
                startActivity(intent);

            }
        });
        signup_expert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_VideoCalling.this, expert_signup.class);
                startActivity(intent);

            }
        });

        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_VideoCalling.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
    public void do_login(String email, String password){

        progress = new ProgressDialog(Login_VideoCalling.this);
        progress.setCancelable(false);
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        db.collection("users")
                .whereEqualTo("email",email)
                .whereEqualTo("password",password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        progress.cancel();
                        if (task.isSuccessful()) {
                            boolean found=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String user_type= (String) document.getData().get("usertype");
                                if(user_type.equals("farmer")){
                                    save(email,password);
                                    Intent intent = new Intent(Login_VideoCalling.this, Farmer_profile.class);
                                    intent.putExtra("mail",email);
                                    intent.putExtra("id",document.getId());
                                    startActivity(intent);
                                }else if(user_type.equals("expert")){
                                    save(email,password);
                                    Intent intent = new Intent(Login_VideoCalling.this,Expert_profile.class);
                                    intent.putExtra("mail",email);
                                    intent.putExtra("id",document.getId());
                                    startActivity(intent);
                                }
                             found=true;
                            }
                            if(!found){
                               // Toast.makeText(Login.this, " Not Correct.", Toast.LENGTH_SHORT).show();
                                show_dialog("Username and password is not correct.");
                            }

                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                          //  Toast.makeText(Login.this, "Error.", Toast.LENGTH_SHORT).show();
                            show_dialog("Error");
                        }
                    }
                });
    }


    public void show_dialog(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login_VideoCalling.this);
        alertDialogBuilder.setTitle("Response");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {//yes
                    public void onClick(final DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void save(String email,String password){
        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_pref.sp.edit();
        editor.putString("email", email);
        editor.putString("password", password);

        editor.commit();
    }
}