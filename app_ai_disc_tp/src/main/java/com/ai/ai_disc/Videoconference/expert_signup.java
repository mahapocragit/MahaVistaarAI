package com.ai.ai_disc.Videoconference;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ai.ai_disc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class expert_signup extends AppCompatActivity {

    private static final String TAG = "expert_signup";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText email;
    EditText password;
    Button submit;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setTitle("Expert Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                add_data(email.getText().toString(), password.getText().toString());
            }
        });
    }

    public void add_data(String email, String password) {

        progress = new ProgressDialog(expert_signup.this);
        progress.setCancelable(false);
        progress.setMessage("Sending Data");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);
        user.put("usertype", "expert");

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        cancel();
                        show_dialog("Account created.");
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        // Toast.makeText(signup.this, "Account created.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w(TAG, "Error adding document", e);
                        cancel();
                        show_dialog("Error.");
                        // Toast.makeText(expert_signup.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void show_dialog(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(expert_signup.this);

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

    public void cancel() {
        progress.cancel();

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
}