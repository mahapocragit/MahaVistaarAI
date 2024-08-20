package com.ai.ai_disc.VC;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ai.ai_disc.R;
import com.ai.ai_disc.shared_pref;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class expert_list extends AppCompatActivity {

    private static final String TAG = "expert_list";
    SwipeRefreshLayout swipe;
    RecyclerView list;
    ArrayList<expert> list_expert;
    custom_adapter12 ca;
    String userId="";
    String cropId;
    String token_got;
    //FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Expert List");

        Bundle extras = getIntent().getExtras();
        userId= user_singleton.getInstance().getUser_id();
        if (extras != null)
        {
             cropId = extras.getString("cropId");
            //The key argument here must match that used in the other activity
        }

        list = (RecyclerView) findViewById(R.id.list);
        swipe = findViewById(R.id.swipe);
        list_expert = new ArrayList<expert>();

        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
         token_got = shared_pref.sp.getString("token", "");
        String mail = shared_pref.sp.getString("email", "");


        ca = new custom_adapter12(expert_list.this, list_expert, mail, token_got);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(expert_list.this);
        list.setLayoutManager(mLayoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setAdapter(ca);


        get_data();


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                get_data();
                swipe.setRefreshing(false);
            }
        });
    }

    public void get_data()
    {

        list_expert.clear();

        JSONObject object = new JSONObject();
        try {
            object.put("crop_Id", cropId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/ExpertList_VideoCalling")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("stringData" + response.toString());

                        try {


                            for (int i = 0; i < response.length(); i++)
                            {

                                JSONObject object = response.getJSONObject(i);

                                String email =object.optString("emailId");
                                String token =object.optString("tokenID");
                                // Log.d(TAG, token);
                                expert e = new expert();
                                e.setEmail_id(email);
                                e.setToken(token);
                                list_expert.add(e);


                            }

                            if(list_expert.size()==0)
                            {
                                Toast.makeText(expert_list.this,"No expert found",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                ca.notifyDataSetChanged();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {


                        Toast.makeText(expert_list.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });






      /*  db.collection("users")
                .whereEqualTo("usertype", "expert")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String email = (String) document.getData().get("email");
                                String token = (String) document.getData().get("token");
                                // Log.d(TAG, token);
                                expert e = new expert();
                                e.setEmail_id(email);
                                e.setToken(token);
                                list_expert.add(e);

                            }

                            ca.notifyDataSetChanged();


                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(expert_list.this, "Error.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
    }


    public void addToken()
    {
        JSONObject object = new JSONObject();
        try {
            object.put("token_id", token_got);
            object.put("user_Id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

       AndroidNetworking.post("")
               .addJSONObjectBody(object)
               .build()
               .getAsJSONObject(new JSONObjectRequestListener() {
                   @Override
                   public void onResponse(JSONObject jsonObject)
                   {
                       boolean result=jsonObject.optBoolean("result");
                       String message=jsonObject.optString("message");
                       if(result)
                       {
                           Toast.makeText(expert_list.this, "Added", Toast.LENGTH_SHORT).show();
                       }

                   }

                   @Override
                   public void onError(ANError anError) {

                   }
               });
    }

    public void removeID()
    {
        JSONObject object = new JSONObject();
        try {

            object.put("user_Id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

         AndroidNetworking.post("")
                 .build()
                 .getAsJSONObject(new JSONObjectRequestListener() {
                     @Override
                     public void onResponse(JSONObject jsonObject) {
                         boolean result=jsonObject.optBoolean("result");
                         String message=jsonObject.optString("message");
                         if(result)
                         {
                             Toast.makeText(expert_list.this,"deleted",Toast.LENGTH_LONG).show();
                         }
                     }

                     @Override
                     public void onError(ANError anError) {

                     }
                 });
    }


}