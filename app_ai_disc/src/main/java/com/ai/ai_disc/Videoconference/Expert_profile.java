package com.ai.ai_disc.Videoconference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ai.ai_disc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Expert_profile extends AppCompatActivity {
    private static final String TAG = "Expert_profile";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";

    ArrayList<String> web;
    ArrayList<Integer> imageId;


    GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Expert Profile");

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String mail = data.getString("mail");
        id_firebase = data.getString("id");

        grid = findViewById(R.id.grid);

        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        String token_got = shared_pref.sp.getString("token", "");

        add_token(token_got, id_firebase);


        web = new ArrayList<String>();
        imageId = new ArrayList<Integer>();


        web.add("List Request");


        imageId.add(R.drawable.view_record);


        custom_grid_adapter1 adapter1 = new custom_grid_adapter1(Expert_profile.this, web, imageId);

        grid.setAdapter(adapter1);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) {

                    Intent intent6 = new Intent(Expert_profile.this, List_request.class);
                    startActivity(intent6);
                }


            }
        });


    }

    public void add_token(String token, String id) {
        Map<String, Object> user = new HashMap<>();
        user.put("token", token);

        db.collection("users").document(id)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                    //    Toast.makeText(Expert_profile.this, "Token updated", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w(TAG, "Error writing document", e);

                       // Toast.makeText(Expert_profile.this, "Token not  updated", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.signout) {
            sign_out(id_firebase);
            Intent intent = new Intent(Expert_profile.this, Login_VideoCalling.class);
            startActivity(intent);
            shared_pref.remove_shared_preference(Expert_profile.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sign_out(String id) {


        DocumentReference docRef = db.collection("users").document(id);

// Remove the 'capital' field from the document
        Map<String, Object> updates = new HashMap<>();
        updates.put("token", FieldValue.delete());

        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                       // Toast.makeText(Expert_profile.this, "Token Deleted.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      //  Log.w(TAG, "Error writing document", e);

                      //  Toast.makeText(Expert_profile.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}