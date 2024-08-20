package com.ai.ai_disc.Videoconference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class List_request extends AppCompatActivity {
    private static final String TAG = "List_request";
    RecyclerView list;
    ArrayList<request> list_request;
    custom_adapter13 ca;

    TextView no_request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("List request");


        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        String token_got = shared_pref.sp.getString("token", "");
        String my_email = shared_pref.sp.getString("email", "");

        list_request = new ArrayList<request>();

        list = (RecyclerView) findViewById(R.id.list);
        no_request=findViewById(R.id.no_request);


        no_request.setVisibility(View.GONE);
        ca = new custom_adapter13(List_request.this, list_request, token_got, my_email);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(List_request.this);
        list.setLayoutManager(mLayoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setAdapter(ca);

        get_list(list_request);

    }


    @Override
    public void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        get_list(list_request);

    }

    public void get_list(ArrayList<request> list_request) {

        list_request.clear();
        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        String list = shared_pref.sp.getString("list", "");
        no_request.setVisibility(View.GONE);
        if (!list.isEmpty()) {

            no_request.setVisibility(View.GONE);
            try {
                JSONObject list_json = new JSONObject(list);
                JSONArray array = list_json.getJSONArray("array");

                for (int i = array.length()-1; i >=0; i--) {
                    JSONObject object = array.getJSONObject(i);

                    String email = object.getString("email");
                    String token = object.getString("token");
                    String date = object.optString("date");

                    request r = new request();
                    r.setEmail_id(email);
                    r.setMy_token(token);
                    r.setDate(date);
                    list_request.add(r);




                }

                ca.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "get_list: empty :" + list);
            no_request.setVisibility(View.VISIBLE);

        }
    }
}