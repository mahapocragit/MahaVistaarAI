package com.ai.ai_disc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Farmer.Account_expert;
import com.ai.ai_disc.model.Expert;
import com.ai.ai_disc.model.expert_list_adapter18;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class expert_setting extends AppCompatActivity {

    ArrayList<String> web;
    ArrayList<Integer> imageId ;
    ArrayList<Expert> list_expert;
    ArrayList<ArrayList<ArrayList<String>>> av_slot_temp;
    RecyclerView list;
    InternetReceiver internet ;
    expert_list_adapter18 ca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_list_expert);

        internet = new InternetReceiver();
        list=findViewById(R.id.gridc);
        list_expert=new ArrayList<>();
        av_slot_temp=new ArrayList<>();


        ca = new expert_list_adapter18(expert_setting.this, list_expert,av_slot_temp);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(expert_setting.this);
        list.setLayoutManager(mLayoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setAdapter(ca);

getting_list();
    }
    private void getsettings(ArrayList<String> ex) {

        //av_slot.clear();
        JSONArray exc=new JSONArray(ex);
        JSONObject object = new JSONObject();
        try {
            object.put("exper_id", exc);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
       // Log.d("bbb",exc.toString());
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getslotsettingsaa")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arraytop=response.getJSONArray("sloting");
                            for (int i = 0; i < arraytop.length(); i++) {
                                JSONObject nk = (JSONObject) arraytop.get(i);
                                ArrayList<ArrayList<String>> av_slot=new ArrayList<>();
                                JSONArray array1 = nk.getJSONArray("slots");
                                for (int ii = 0; ii < array1.length(); ii++) {
                                    JSONObject nki = (JSONObject) array1.get(ii);
                                    JSONArray mn = nki.getJSONArray("slot_time");

                                    ArrayList<String> vb = new ArrayList<>();
                                    for (int iii = 0; iii < mn.length(); iii++) {
                                        vb.add(String.valueOf(mn.getInt(iii)));
                                    }
                                    av_slot.add(vb);
                                }
                                av_slot_temp.add(av_slot);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //av_slot=new ArrayList<>();
                        //Collections.copy(av_slot_temp, av_slot);

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }
public void getting_list() {
        ArrayList<String> ex=new ArrayList<>();
            AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/list_expert1")
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {


                    System.out.println("adminRecord" + response.toString());

                    try {

                        list_expert.clear();
                        JSONArray array = response.getJSONArray("expert_list");
                        for (int i = 0; i < array.length(); i++) {


                            JSONObject object = (JSONObject) array.get(i);
                            String user_id = object.getString("user_id");
                            String user_name = object.getString("user_name");
                            String first_name = object.getString("first_name");
                            String email_id = object.getString("email_id");
                            String last_name = object.getString("last_name");
                            String imagepath = object.getString("imagepath");


                            Expert expert = new Expert();
                            expert.setUser_id(user_id);
                            expert.setUser_name(user_name);
                            expert.setFirst_name(first_name);
                            expert.setEmail_id(email_id);
                            expert.setimagepath(imagepath);
                            expert.setlast_name(last_name);

                            ex.add(user_id);
                            list_expert.add(expert);

                        }
                        Expert expert1 = new Expert();
                        expert1.setUser_id("admin");
                        expert1.setUser_name("user_name");
                        expert1.setFirst_name("first_name");
                        expert1.setEmail_id("email_id");
                        expert1.setimagepath("");
                        expert1.setlast_name("last_name");
                        list_expert.add(expert1);
                        ca.notifyDataSetChanged();
                        getsettings(ex);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(ANError error) {


                    Toast.makeText(expert_setting.this, "Error in getting list.", Toast.LENGTH_LONG).show();
                }
            });
}



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflator= getMenuInflater();
        inflator.inflate(R.menu.menu1,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.logout:

                AlertDialog.Builder opt = new AlertDialog.Builder(expert_setting.this);
                opt.setTitle("Are you sure ?");
                opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shared_pref.remove_shared_preference(expert_setting.this);
                        Intent intent = new Intent(expert_setting.this, Login.class);
                        startActivity(intent);
                        finish();



                    }
                });
                opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                opt.show();

                break;
            default:
                break;

        }

        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet,intentFilter);

    }


//    @Override
//    public void onStop(){
//        super.onStop();
//        unregisterReceiver(internet);
//
//    }

}
