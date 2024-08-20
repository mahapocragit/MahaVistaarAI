package com.ai.ai_disc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.Farmer.Farmer_Disease_identifier_pest_Identifier_Activity;
import com.ai.ai_disc.Farmer.custom_adapter_class5;
import com.ai.ai_disc.Farmer.custom_adapter_expertlist;
import com.ai.ai_disc.Farmer.expertDetails1;
import com.ai.ai_disc.Farmer.list_of_query;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class appointment extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
 String crop,query,crop_id;
    ListView list_view;
    TextView centeredText;
    TextView loct;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    RelativeLayout centeredRelativeLayout;
    ArrayList<expertDetails1> list_e=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        Intent intent = getIntent();
        setTitle("Appointment");

        setNavigationViewListener();
        drawerLayout = findViewById(R.id.my_drawer_layout1);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer1);
        View head=navigationView.getHeaderView(0);

        TextView userd=head.findViewById(R.id.name_dataentry);
        TextView acc=head.findViewById(R.id.type_nav);
        loct=head.findViewById(R.id.loctfff);
        //loct.setText(addressed);
        try{userd.setText(user_singleton.getInstance().getfname()+" "+user_singleton.getInstance().getMname()+" "+user_singleton.getInstance().getlname());
            acc.setText(user_singleton.getInstance().getUser_type()+" Account");
            loct.setText(user_singleton.getInstance().getloct());
            //Log.d("ggg",head.findViewById(R.id.vsr).toString());


        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("error",e.toString());
        }




        Bundle args = intent.getBundleExtra("bundle");
        crop=args.getString("crop");
        crop_id=args.getString("crop_id");
        query=args.getString("query");
        list_view=findViewById(R.id.list_view1);
        TextView cp=findViewById(R.id.expert_view);
        cp.setText("Experts of "+crop);
        centeredRelativeLayout = (RelativeLayout) findViewById(R.id.centeredRelative1);
        centeredText = (TextView) findViewById(R.id.centeredText1);
        getview();
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer1);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getview(){
        JSONObject object1 = new JSONObject();
        try {

            object1.put("crop", crop_id);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d("hhhh", "onResponse: " + object1.toString());

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getexperts1")
                .addJSONObjectBody(object1)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            JSONArray array1 = jsonObject.getJSONArray("result");
                            //Log.d("hhhh", "onResponse: " + array1);
                            for (int i = 0; i < array1.length(); i++) {
                                expertDetails1 experts= new expertDetails1();
                                JSONObject object = (JSONObject) array1.get(i);
                                experts.setname(object.optString("name"));
                                experts.setuser(object.optString("username"));
                                experts.setmail(object.optString("mail"));
                                experts.setnumber(object.optString("numb"));
                                experts.setrated(object.optString("lrt"));
                                experts.setinstitute(object.optString("institute"));

                                experts.settitle(object.optString("title"));
                                experts.setdesig(object.optString("designation"));
                                experts.setspeciality(object.optString("speciality"));

                                experts.setquery(query);

                                experts.setid(object.optString("ids"));
                                list_e.add(experts);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (list_e.size() == 0)
                        {

                            list_view.setVisibility(View.GONE);
                            centeredRelativeLayout.setVisibility(View.VISIBLE);
                            centeredText.setVisibility(View.VISIBLE);
                            centeredText.setText("No Expert found");



                        } else {

                            list_view.setAdapter(new custom_adapter_expertlist(appointment.this, list_e));
                        }



                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.about_us1:

                Intent intent = new Intent(appointment.this, About_us1.class);
                startActivity(intent);

                break;
            case R.id.home:
                Intent ibtwd;
                if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("expert")){
                    ibtwd=new Intent(appointment.this, expertprofile_fragment.class);
                }else{ ibtwd=new Intent(appointment.this, farmersprofile_fragment1.class);}
                startActivity(ibtwd);
                break;
            case R.id.help1:

                Intent intent1 = new Intent(appointment.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.disclaimer:

                Intent intent1q = new Intent(appointment.this, disclaimer.class);
                startActivity(intent1q);



                break;
            case R.id.editme:

                Intent ibt=new Intent(appointment.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(appointment.this, contributor.class);
                startActivity(ibt1);

                break;
            case R.id.history:

                Intent ibt1q=new Intent(appointment.this, history.class);
                startActivity(ibt1q);

                break;
            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void sign_out() {


        DocumentReference docRef = db.collection("loginaidisc").document(user_singleton.getInstance().getfb_id());

// Remove the 'capital' field from the document
        Map<String, Object> updates = new HashMap<>();
        updates.put("token", FieldValue.delete());

        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d("fff", "DocumentSnapshot successfully written!");
                        Toast.makeText(appointment.this, "Signed out", Toast.LENGTH_LONG).show();
                        shared_pref.remove_shared_preference(appointment.this);
                        Intent intent = new Intent(appointment.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("fff", "Error writing document", e);
                        Toast.makeText(appointment.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(appointment.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(appointment.this, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(appointment.this);
                    Intent intent = new Intent(appointment.this, Login.class);
                    startActivity(intent);
                    finish();
                }else{
                    sign_out();}



            }
        });
        opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        opt.show();
    }


}