package com.ai.ai_disc.VC;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ai.ai_disc.Login;
import com.ai.ai_disc.R;
import com.ai.ai_disc.shared_pref;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Expert_profile extends AppCompatActivity {
    private static final String TAG = "Expert_profile";
    String userId="";
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
        userId= user_singleton.getInstance().getUser_id();
        grid = findViewById(R.id.grid);

        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        String token_got = shared_pref.sp.getString("token", "");

        add_token(token_got);


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

    public void add_token(String token)
    {
        JSONObject jo = new JSONObject();
        try
        {

            jo.put("token_id", token);
            jo.put("user_Id", userId);




        } catch (JSONException E)
        {

        }

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Update_Firebasetoken")
                .addJSONObjectBody(jo)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener()
                {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {


                            boolean result = response.getBoolean("result");
                            String message = response.getString("message");


                            if (result)
                            {
                                Toast.makeText(Expert_profile.this,message,Toast.LENGTH_LONG).show();
                               /* Intent intent=new Intent(Farmer_profile.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);*/

                            } else {
                                Toast.makeText(Expert_profile.this,message,Toast.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {


                        Toast.makeText(Expert_profile.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });

    }





    /*public void add_token(String token, String id)
    {
        Map<String, Object> user = new HashMap<>();
        user.put("token", token);

        db.collection("users").document(id)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(Expert_profile.this, "Token updated", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);

                        Toast.makeText(Expert_profile.this, "Token not  updated", Toast.LENGTH_LONG).show();
                    }
                });
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout)
        {
            sign_out(id_firebase);
            Intent intent = new Intent(Expert_profile.this, Login.class);
            startActivity(intent);

            shared_pref.remove_shared_preference(Expert_profile.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void sign_out(String id)
    {

        JSONObject jo = new JSONObject();
        try
        {


            jo.put("user_Id", userId);




        } catch (JSONException E)
        {

        }

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Delete_Firebasetoken")
                .addJSONObjectBody(jo)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener()
                {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {


                            boolean result = response.getBoolean("result");
                            String message = response.getString("message");


                            if (result)
                            {
                                Toast.makeText(Expert_profile.this,message,Toast.LENGTH_LONG).show();
                               /* Intent intent=new Intent(Farmer_profile.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);*/

                            } else {
                                Toast.makeText(Expert_profile.this,message,Toast.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {


                        Toast.makeText(Expert_profile.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });
    }
}