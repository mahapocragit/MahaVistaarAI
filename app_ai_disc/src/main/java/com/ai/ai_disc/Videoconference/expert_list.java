package com.ai.ai_disc.Videoconference;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ai.ai_disc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class expert_list extends AppCompatActivity {

    private static final String TAG = "expert_list";
    SwipeRefreshLayout swipe;
    RecyclerView list;
    ArrayList<expert> list_expert;
    custom_adapter12 ca;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Expert List");

        list = (RecyclerView) findViewById(R.id.list);
        swipe = findViewById(R.id.swipe);
        list_expert = new ArrayList<expert>();

        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        String token_got = shared_pref.sp.getString("token", "");
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

    public void get_data() {

        list_expert.clear();

        db.collection("users")
                .whereEqualTo("usertype", "expert")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {


                            for (QueryDocumentSnapshot document : task.getResult()) {
                               // Log.d(TAG, document.getId() + " => " + document.getData());
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
                           // Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(expert_list.this, "Error.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}