package com.ai.ai_disc;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ai.ai_disc.Videoconference.VCActivity;
import com.ai.ai_disc.model.contributor_model;
import com.ai.ai_disc.model.getall_appoinment_model;
import com.ai.ai_disc.model.getall_report_model;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.xeoh.android.checkboxgroup.CheckBoxGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class expert_appointment extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<getall_appoinment_model> list;
    custom_adapter_appointment adap;
    RecyclerView grid;
    TextView hello,nothing;
    TextView loct;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ArrayList<ArrayList<String>> av_slot_temp;
    ArrayList<ArrayList<String>> av_slot;
    private final String[] days=new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday"};
    private final String[] days_slot=new String[]{"10:00-10:30 AM",
            "10:30 - 11:00 AM",
            "11:00 - 11:30 AM",
            "11:30 AM - 12:00 PM",
            "12:00 - 12:30 PM",
            "12:30 - 1:00 PM",
            "2:30 - 3:00 PM",
            "3:00 - 3:30 PM",
            "3:30 - 4:00 PM",
            "4:00 - 4:30 PM",
            "4:30 - 5:00 PM"};
    private final String[] slots=new String[]{"10:00",
            "10:30",
            "11:00",
            "11:30",
            "12:00",
            "12:30",
            "14:30",
            "15:00",
            "15:30",
            "16:00",
            "16:30"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_appointment);
        setnavigation();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        context =expert_appointment.this;
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        View head=navigationView.getHeaderView(0);

        TextView userd=head.findViewById(R.id.name_dataentry);
        TextView acc=head.findViewById(R.id.type_nav);
        loct=head.findViewById(R.id.loctfff);
        //loct.setText(addressed);
        try{userd.setText(user_singleton.getInstance().getfname()+" "+user_singleton.getInstance().getMname()+" "+user_singleton.getInstance().getlname());
            acc.setText(expert_singleton.getInstance().getcrop()+" Expert");
            loct.setText(user_singleton.getInstance().getloct());
            //Log.d("ggg",head.findViewById(R.id.vsr).toString());


        } catch (Exception e) {
            e.printStackTrace();
           // Log.d("error",e.toString());
        }

        av_slot_temp=new ArrayList<>();
        av_slot=new ArrayList<>();
        list=new ArrayList<>();
         grid=findViewById(R.id.grid);
         getlist();
         setTitle("AI-DISC");
         hello=findViewById(R.id.hello);
         hello.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //Log.d("results",av_slot.toString());
             }
         });
        SwipeRefreshLayout lay= findViewById(R.id.ref);
        lay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshdata();
                lay.setRefreshing(false);
            }
        });
         nothing=findViewById(R.id.nothing);
        MaterialButton sett=findViewById(R.id.mat_but);
         hello.setText("Loading");
        adap=new custom_adapter_appointment(expert_appointment.this,list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        grid.setLayoutManager(mLayoutManager);
        grid.setItemAnimator(new DefaultItemAnimator());
        grid.setAdapter(adap);
        getsettings();
        sett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //av_slot_temp.addAll(av_slot);
                                AlertDialog.Builder ob = new AlertDialog.Builder(expert_appointment.this);
                HorizontalScrollView mot=new HorizontalScrollView(expert_appointment.this);
                LinearLayout.LayoutParams paramm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramm.setMargins(2,2,2,2);
                mot.setLayoutParams(paramm);
                LinearLayout nb = new LinearLayout(expert_appointment.this);
                nb.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param.setMargins(20,20,20,20);
                nb.setLayoutParams(param);
                mot.addView(nb);

                LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param1.setMargins(5,5,5,5);
                LinearLayout nb_left = new LinearLayout(expert_appointment.this);
                nb_left.setLayoutParams(param1);
                nb_left.setOrientation(LinearLayout.VERTICAL);
                nb_left.setGravity(Gravity.CENTER_HORIZONTAL);
                RadioGroup rd=new RadioGroup(expert_appointment.this);
                nb_left.addView(rd);
                nb.addView(nb_left);

                LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param.setMargins(10,10,10,10);
                for (String day : days) {
                    RadioButton bt = new RadioButton(expert_appointment.this);
                    bt.setLayoutParams(param3);
                    rd.addView(bt);
                    bt.setText(day);
                }
                LinearLayout nb_right = new LinearLayout(expert_appointment.this);
                nb_right.setOrientation(LinearLayout.VERTICAL);
                nb_right.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout.LayoutParams param31 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param.setMargins(5,5,5,5);
                nb_right.setLayoutParams(param31);
                nb.addView(nb_right);

//                 ArrayList<CheckBox> box=new ArrayList<>();
//                 for (int bb=0;bb<11;bb+=1){
//                    CheckBox sl0 = new CheckBox(expert_appointment.this);
//                    sl0.setText(days_slot[bb]);
//                    nb1.addView(sl0);
//                    box.add(sl0);
//                }
                rd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton
                                r
                                = (RadioButton)group
                                .findViewById(checkedId);

                        String selectedtext = r.getText().toString();
                        int position= Arrays.asList(days).indexOf(selectedtext);
                        nb_right.removeAllViews();

                        for (int bbc=0;bbc<11;bbc+=1){
                            CheckBox sl0 = new CheckBox(expert_appointment.this);
                            sl0.setText(days_slot[bbc]);
                            nb_right.addView(sl0);
                            sl0.setChecked(av_slot_temp.get(position).contains(String.valueOf(bbc)));

                            int finalBbc = bbc;
                            sl0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked){
                                        av_slot_temp.get(position).add(String.valueOf(finalBbc));
                                    }else{
                                        av_slot_temp.get(position).remove(String.valueOf(finalBbc));
                                    }
                                }
                            });
                        }
                    }
                });

                 ob.setView(mot);
                 ob.setTitle("Slot setting").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder mn=new AlertDialog.Builder(expert_appointment.this);
                            mn.setTitle("Are you sure ").setMessage("It will change your appointment slot").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    av_slot=av_slot_temp;
                                    changeslot(av_slot_temp);
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                     }
                 }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                     }
                 });
                 ob.show();

            }
        });


    }
    void refreshdata(){
        getlist();
        getsettings();
    }

    private void changeslot(ArrayList<ArrayList<String>> av_slot_temp) {
        JSONObject object = new JSONObject();
        ArrayList<JSONArray> al=new ArrayList<>();
        for (int cd=0;cd<av_slot_temp.size();cd+=1){
            al.add(new JSONArray(av_slot_temp.get(cd)));
        }
        JSONArray nb=new JSONArray(al);

        try {
            object.put("data",nb);
            object.put("expert_id",user_singleton.getInstance().getUser_id());
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/updateslots")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean bv=false;
                        try {
                            bv=response.getBoolean("book");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (bv){
                            Toast.makeText(expert_appointment.this,"Updated",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(expert_appointment.this,"Error",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void getsettings() {
        av_slot_temp.clear();
        av_slot.clear();
        JSONObject object = new JSONObject();
        try {
            object.put("exper_id", user_singleton.getInstance().getUser_id());
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getslotsettings")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array1=response.getJSONArray("slots");
                            for (int i = 0; i < array1.length(); i++) {
                                JSONObject nk= (JSONObject) array1.get(i);
                                JSONArray mn=nk.getJSONArray("slot_time");
                                ArrayList<String> vb=new ArrayList<>();
                                for (int ii = 0; ii < mn.length(); ii++) {
                                    vb.add(String.valueOf(mn.getInt(ii)));
                                }
                                av_slot_temp.add(vb);
                                                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //av_slot=new ArrayList<>();
                        //Collections.copy(av_slot_temp, av_slot);
                        av_slot.addAll(av_slot_temp);
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    private void setnavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getlist() {
        list.clear();

    JSONObject object = new JSONObject();
        try {
        object.put("expert", user_singleton.getInstance().getUser_id());
    } catch (
    JSONException e) {
        e.printStackTrace();
    }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_all_appointments")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
        @Override
        public void onResponse(JSONObject response) {
            hello.setText("Appointments");
            try {

                JSONArray array = response.getJSONArray("list");
                SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat tms = new SimpleDateFormat("HH:mm");

                //Log.d(TAG, "onResponse: " + array1);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = (JSONObject) array.get(i);
                    getall_appoinment_model data=new getall_appoinment_model();
                    data.setappointment(object.getString("appointment_id"));
                    data.setquery(object.getString("query_id"));
                    data.setscode(object.getString("scode"));
                    data.setslot(object.getString("slot"));
                    data.setslotdate(object.getString("slot_date"));
                    data.setstatus(object.getString("status"));
                    Date strDate = null;
                    try {
                        strDate = sdfs.parse(object.getString("slot_date"));
                        Date nb=new Date();
                        String formattedDate = sdfs.format(nb);
                        String tmds=tms.format(nb);
                        nb=sdfs.parse(formattedDate);
                        Date std=tms.parse(slots[Integer.parseInt(object.getString("slot"))]);

                        Date bn=tms.parse(tmds);
                        //Log.d("lll",bn.toString()+"   "+std.toString());
                        if (nb.before(strDate) ) {
                            data.sethide(1);}
                        else if (nb.equals(strDate)){
                            if (bn.before(std)) {
                                data.sethide(1);
                            }else{
                                data.sethide(0);
                            }
                        }
                        else{
                            data.sethide(0);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        data.sethide(1);
                    }

                    data.setimg(object.getString("img"));
                    list.add(data);
                }

                if (list.size()==0){
                    nothing.setVisibility(View.VISIBLE);
                    hello.setVisibility(View.GONE);

                }else{
                    try{
                        list.sort(new Comparator<getall_appoinment_model>() {
                            @Override
                            public int compare(getall_appoinment_model o1, getall_appoinment_model o2) {
                                return Integer.compare(Integer.parseInt(o2.getappointment()), Integer.parseInt(o1.getappointment()));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    nothing.setVisibility(View.GONE);
                }
                adap.notifyDataSetChanged();
                grid.setAdapter(adap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onError(ANError error) {
        }
    });}
//    private void signout() {
//        AlertDialog.Builder opt = new AlertDialog.Builder(expert_appointment.this);
//        opt.setTitle("Are you sure ?");
//        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                shared_pref.remove_shared_preference(expert_appointment.this);
//                Intent intent = new Intent(expert_appointment.this, Login.class);
//                startActivity(intent);
//                finish();
//
//
//
//            }
//        });
//        opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//        opt.show();
//    }
public void sign_out() {


    DocumentReference docRef = db.collection("loginaidisc").document(user_singleton.getInstance().getfb_id());

// Remove the 'capital' field from the document
    Map<String, Object> updates = new HashMap<>();
    updates.put("token", FieldValue.delete());

    docRef.update(updates)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                   // Log.d("fff", "DocumentSnapshot successfully written!");
                    Toast.makeText(context, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(context);
                    Intent intent = new Intent(context, Login.class);
                    startActivity(intent);
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.w("fff", "Error writing document", e);
                    Toast.makeText(context, "Token not Deleted.", Toast.LENGTH_LONG).show();
                }
            });
}
    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(context);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(context, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(context);
                    Intent intent = new Intent(context, Login.class);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.about_us1:

                Intent intent = new Intent(expert_appointment.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.disclaimer:

                Intent intent1q = new Intent(expert_appointment.this, disclaimer.class);
                startActivity(intent1q);



                break;
            case R.id.home:
                Intent ibtwd;
                if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("expert")){
                    ibtwd=new Intent(expert_appointment.this, expertprofile_fragment.class);
                }else{ ibtwd=new Intent(expert_appointment.this, farmersprofile_fragment1.class);}
                startActivity(ibtwd);
                break;
            case R.id.help1:

                Intent intent1 = new Intent(expert_appointment.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.editme:

                Intent ibt=new Intent(expert_appointment.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(expert_appointment.this, contributor.class);
                startActivity(ibt1);

                break;
            case R.id.history:

                Intent ibt1q=new Intent(expert_appointment.this, history.class);
                startActivity(ibt1q);

                break;

            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}