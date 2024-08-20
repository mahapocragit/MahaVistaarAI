package com.ai.ai_disc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.model.report_adapter18;
import com.ai.ai_disc.model.reportdata;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class my_reports extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String mParam1;
    private String mParam2;
    InternetReceiver internet;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private int numb;
    LinearLayout cardcall,searchhere;
    TextInputEditText searchtext;
    RadioGroup group;
    TextView loct1,noreport;
    ImageView cross,searched,filtered,createnew, addnew ,refreshed,optbutton;
    ImageView search;
    private RecyclerView grid;
    Custom_grid_reporting adapter_grid;
    report_adapter18 datare;
    ArrayList<reportdata> list;
    LatLng loct;
    int tool,sort,fil;
    report_adapter18 ca;
    CardView sortlay,opt,cardfilter;
    EditText sr;
    ArrayList<String> followed;
    ArrayList<reportdata> s_index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);
        internet = new InternetReceiver();
        numb=0;
        setTitle("My Reports");
        setNavigationViewListener();
        drawerLayout = findViewById(R.id.my_drawer_layout2);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer2);
        View head=navigationView.getHeaderView(0);

        TextView userd=head.findViewById(R.id.name_dataentry);
        TextView acc=head.findViewById(R.id.type_nav);
        loct1=head.findViewById(R.id.loctfff);
        //loct.setText(addressed);
        try{userd.setText(user_singleton.getInstance().getfname()+" "+user_singleton.getInstance().getMname()+" "+user_singleton.getInstance().getlname());
            acc.setText(user_singleton.getInstance().getUser_type()+" Account");
            loct1.setText(user_singleton.getInstance().getloct());
            //Log.d("ggg",head.findViewById(R.id.vsr).toString());


        } catch (Exception e) {
            e.printStackTrace();
           // Log.d("error",e.toString());
        }
        tool=0;
        sort=0;
        fil=0;

        noreport=findViewById(R.id.noreport);
        searched=findViewById(R.id.search);
        createnew=findViewById(R.id.create_report);
        addnew=findViewById(R.id.more);
        refreshed=findViewById(R.id.refresh);
        filtered=findViewById(R.id.filtered);
        searchhere=findViewById(R.id.searchhere);
        searchtext=findViewById(R.id.searchtext);
        search=findViewById(R.id.searchbutton);
        cross=findViewById(R.id.imageView13);
        group=findViewById(R.id.radio);
        opt=findViewById(R.id.opt);
        optbutton=findViewById(R.id.optbutton);
        sortlay=findViewById(R.id.sortlay);
        sortlay.setVisibility(View.GONE);
        cross.setVisibility(View.GONE);
        searchhere.setVisibility(View.GONE);
        cardfilter=findViewById(R.id.cardfilter);
        cardfilter.setVisibility(View.GONE);
        followed=new ArrayList<>();
        list=new ArrayList<>();
        addreports();

        opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fil==0){
                    fil=1;
                    cardfilter.setVisibility(View.VISIBLE);
                    optbutton.setImageDrawable(my_reports.this.getResources().getDrawable(R.drawable.ic_baseline_expand_less_24, my_reports.this.getTheme()));
                }else{
                    fil=0;
                    cardfilter.setVisibility(View.GONE);
                    sortlay.setVisibility(View.GONE);
                    optbutton.setImageDrawable(my_reports.this.getResources().getDrawable(R.drawable.ic_baseline_dehaze1_24, my_reports.this.getTheme()));
                }
            }
        });

        refreshed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onoff=false;
//
//                views.setVisibility(View.GONE);
//                slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getContext().getTheme()));
                refreshreports();
            }
        });
        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onoff=false;
//
//                views.setVisibility(View.GONE);
//                slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getContext().getTheme()));
                numb+=10;
                addreports();
            }
        });
        createnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onoff=false;
//
//                views.setVisibility(View.GONE);
//                slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getContext().getTheme()));
                addnew();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hg=searchtext.getText().toString();
                if (hg.matches("")){
                    Toast.makeText(my_reports.this, "Enter Something", Toast.LENGTH_SHORT).show();
                }
                else{
                    goforfliter(hg);
                }
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cross.setVisibility(View.GONE);
                searchhere.setVisibility(View.GONE);
                searchtext.getText().clear();
                grid.setAdapter(ca);
                tool=0;
            }
        });
        searched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tool==1){
//                    imageView6.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.ic_menu_search, getContext().getTheme()));
                    //grid.setAdapter(ca);
                    tool=0;
                    searchhere.setVisibility(View.GONE);

                    //sr.setVisibility(View.GONE);
                }else{
                    searchhere.setVisibility(View.VISIBLE);
                    tool=1;
                    //sr.setVisibility(View.VISIBLE);
                }

            }
        });
        filtered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sort==0){
                    sortlay.setVisibility(View.VISIBLE);
                    sort=1;
                }else{
                    sortlay.setVisibility(View.GONE);
                    sort=0;
                }
            }
        });
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int nm=group.getCheckedRadioButtonId();
                RadioButton btt=findViewById(nm);
                if (btt.getText().toString().matches("Most Liked")){
                    //likedreports();
                    sortlay.setVisibility(View.GONE);
                    sort=0;
                }else{if (btt.getText().toString().matches("Followed First")){
                    //followedreports();
                    sortlay.setVisibility(View.GONE);
                    sort=0;
                }
                else{
                    //datedreports();
                    sortlay.setVisibility(View.GONE);
                    sort=0;
                }

                }
            }
        });

        s_index=new ArrayList<>();
        grid=findViewById(R.id.grid);
        grid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (fil==1){
                    fil=0;
                    cardfilter.setVisibility(View.GONE);
                    sortlay.setVisibility(View.GONE);
                    optbutton.setImageDrawable(my_reports.this.getResources().getDrawable(R.drawable.ic_baseline_dehaze1_24, my_reports.this.getTheme()));
                }}
        });


        ca = new report_adapter18(my_reports.this, list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(my_reports.this);
        grid.setLayoutManager(mLayoutManager);
        grid.setItemAnimator(new DefaultItemAnimator());

        grid.setAdapter(ca);
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer2);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void addnew() {
        Intent nb=new Intent(my_reports.this, create_report.class);
        startActivity(nb);
    }
    private void goforfliter(String value) {
        //search.setText("searching...");
        s_index.clear();
        for(int s=0;s< list.size();s+=1){
            reportdata ad=list.get(s);

            if (ad.getdpname().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getdated().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.gethead().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") ||ad.getcrop_name().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getlocate().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getuser_name().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)")){
                s_index.add(ad);
            }
        }
        if (s_index.size()!=0) {
            datare = new report_adapter18(my_reports.this, s_index);
            if(s_index.size()==0){
                noreport.setVisibility(View.VISIBLE);
            }else{
                noreport.setVisibility(View.GONE);
            }
            grid.setAdapter(datare);
            //search.setText("GO");
            cross.setVisibility(View.VISIBLE);

            Toast.makeText(my_reports.this, "Showing "+String.valueOf(s_index.size())+" Results", Toast.LENGTH_SHORT).show();
            //imageView6.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_backspace_24, getContext().getTheme()));
        }
        else{
            //search.setText("GO");
            Toast.makeText(my_reports.this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }


    }
    private void refreshreports() {
        //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_access_time_24, getContext().getTheme()));
        JSONObject object = new JSONObject();
        list.clear();
        int fg= 0;
        numb=0;
        try {

            object.put("number", numb);object.put("fl", user_singleton.getInstance().getUser_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_all_reports11")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                           // Log.d("datatattt",response.toString());
                            JSONArray array = response.getJSONArray("list");
                            //Log.d(TAG, "onResponse: " + array1);
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                if (object.getString("user_id").matches(user_singleton.getInstance().getUser_id())) {
                                    reportdata data = new reportdata();


                                    data.setcrop_name(object.getString("crop_name"));
                                    data.setlocate(object.getString("locate"));
                                    data.setdpname(object.getString("dp_name"));
                                    data.setdated(object.getString("dated"));

                                    data.setreport_type(object.getString("report_type"));
                                    data.setrecord_id(object.getString("record_id"));
                                    data.setuser_name(object.getString("nm"));
                                    data.sethead(object.getString("head"));
                                    data.setinfo(object.getString("info"));
                                    data.setuser_id(object.getString("user_id"));
                                    data.setlikednumber(object.getString("likednumber"));
                                    data.setprio(object.getString("priority"));
                                    data.setfl(followed.contains(object.getString("user_id")));
                                    data.setlk(object.getString("like").matches("1"));

                                    list.add(data);
                                }
                            }
                            if(list.size()==0){
                                noreport.setVisibility(View.VISIBLE);
                            }else{
                                noreport.setVisibility(View.GONE);
                            }
                            ca.notifyDataSetChanged();
                            grid.setAdapter(ca);
                            fil=0;
                            cardfilter.setVisibility(View.GONE);
                            sortlay.setVisibility(View.GONE);
                            optbutton.setImageDrawable(my_reports.this.getResources().getDrawable(R.drawable.ic_baseline_dehaze1_24, my_reports.this.getTheme()));

                            int hg=list.size();
                            if (hg==fg){
                                //cardcall.setVisibility(View.GONE);
                                Toast.makeText(my_reports.this, "Showing All Reports", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(my_reports.this, "Refreshed", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onError(ANError error) {

                    }
                });
    }
    private void addreports() {
        //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_access_time_24, getContext().getTheme()));
        JSONObject object = new JSONObject();
        int fg=list.size();
        try {

            object.put("number", numb);
            object.put("fl", user_singleton.getInstance().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_all_reports11")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                           // Log.d("datatattt",response.toString());
                            JSONArray array = response.getJSONArray("list");
                            //Log.d(TAG, "onResponse: " + array1);
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                if (object.getString("user_id").matches(user_singleton.getInstance().getUser_id())){
                                    reportdata data=new reportdata();
                                    data.setcrop_name(object.getString("crop_name"));
                                    data.setlocate(object.getString("locate"));
                                    data.setdpname(object.getString("dp_name"));
                                    data.setdated(object.getString("dated"));
                                    data.setreport_type(object.getString("report_type"));
                                    data.setrecord_id(object.getString("record_id"));
                                    data.setuser_name(object.getString("nm"));
                                    data.sethead(object.getString("head"));
                                    data.setuser_id(object.getString("user_id"));
                                    data.setinfo(object.getString("info"));
                                    data.setlikednumber(object.getString("likednumber"));
                                    data.setprio(object.getString("priority"));
                                    data.setfl(followed.contains(object.getString("user_id")));
                                    data.setlk(object.getString("like").matches("1"));

                                    list.add(data);
                                }


                            }
                            if(list.size()==0){
                                noreport.setVisibility(View.VISIBLE);
                            }else{
                                noreport.setVisibility(View.GONE);
                            }
                            ca.notifyDataSetChanged();
                            fil=0;
                            cardfilter.setVisibility(View.GONE);
                            sortlay.setVisibility(View.GONE);
                            optbutton.setImageDrawable(my_reports.this.getResources().getDrawable(R.drawable.ic_baseline_dehaze1_24, my_reports.this.getTheme()));

                            int hg=list.size();
                            if (hg==fg){
                                //cardcall.setVisibility(View.GONE);
                                Toast.makeText(my_reports.this, "Showing All Reports", Toast.LENGTH_SHORT).show();

                            }else{
                                //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_post_add_24, getContext().getTheme()));
                                //Toast.makeText(getContext(), "Showing "+String.valueOf(list.size())+" Reports", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                    }


                    @Override
                    public void onError(ANError error) {

                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.about_us1:

                Intent intent = new Intent(my_reports.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.help1:

                Intent intent1 = new Intent(my_reports.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.disclaimer:

                Intent intent1q = new Intent(my_reports.this, disclaimer.class);
                startActivity(intent1q);



                break;
            case R.id.home:

                Intent ibtwd;
                if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("expert")){
                    ibtwd=new Intent(my_reports.this, expertprofile_fragment.class);
                }else{ ibtwd=new Intent(my_reports.this, farmersprofile_fragment1.class);}
                startActivity(ibtwd);

                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.editme:

                Intent ibt=new Intent(my_reports.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(my_reports.this, contributor.class);
                startActivity(ibt1);

                break;

            case R.id.history:

                Intent ibtw=new Intent(my_reports.this, history.class);
                startActivity(ibtw);

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
                        Toast.makeText(my_reports.this, "Signed out", Toast.LENGTH_LONG).show();
                        shared_pref.remove_shared_preference(my_reports.this);
                        Intent intent = new Intent(my_reports.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("fff", "Error writing document", e);
                        Toast.makeText(my_reports.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(my_reports.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(my_reports.this, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(my_reports.this);
                    Intent intent = new Intent(my_reports.this, Login.class);
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {

        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }
}