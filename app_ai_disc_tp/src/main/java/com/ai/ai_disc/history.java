package com.ai.ai_disc;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.model.contributor_model;
import com.ai.ai_disc.model.history_model;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class history extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
InternetReceiver internet;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";
    JSONObject array_foll;
    Context context;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ArrayList<history_model> dev=new ArrayList<>();
    ArrayList<history_model> s_index=new ArrayList<>();
    custom_adapter_history dataa;
    custom_adapter_history datare;
    TextView loct;
    ArrayList<String>followed;
    TextView tal,report,query,followers;
    LinearLayout fl,rp,idn,qu;
    int tool=0;
    Button newd;
    RecyclerView grid;
    Button search;
    EditText sr;
    CardView card;
    int rppp=0,qqqq=0;
    ImageView imageView6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("My Activity");
        tool=0;
        context=history.this;
        internet = new InternetReceiver();
        tal = (TextView) findViewById(R.id.total);
        report = (TextView) findViewById(R.id.reports);
        query = (TextView) findViewById(R.id.query);
        followers = (TextView) findViewById(R.id.follow);
        newd = (Button) findViewById(R.id.newident);
        grid = (RecyclerView) findViewById(R.id.grid);
        search = (Button) findViewById(R.id.searchbutton);
         imageView6 =findViewById(R.id.imageView6);
        rp =findViewById(R.id.rpt);
        idn =findViewById(R.id.idn);
        fl =findViewById(R.id.fl);
        qu =findViewById(R.id.qu);
         card=findViewById(R.id.card);

        sr=findViewById(R.id.searchtext);
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (tool==1){
                    imageView6.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_search, getTheme()));
                    grid.setAdapter(dataa);
                    tool=0;
                    search.setVisibility(View.GONE);
                    sr.setVisibility(View.GONE);
                }else{
                    search.setVisibility(View.VISIBLE);
                    sr.setVisibility(View.VISIBLE);
                }

            }
        });
        setNavigationViewListener();
        drawerLayout = findViewById(R.id.my_drawer_layout2);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gethistory();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer2);
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
        newd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nb=new Intent(history.this, farmersprofile_fragment1.class);
                startActivity(nb);
            }
        });
        newd.setEnabled(false);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hg=sr.getText().toString();
                if (hg.matches("")){
                    Toast.makeText(history.this, "Enter Something", Toast.LENGTH_SHORT).show();

                }
                else{
                    goforfliter(hg);
                }
            }
        });

         dataa=new custom_adapter_history(history.this,dev);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        grid.setLayoutManager(mLayoutManager);
        grid.setItemAnimator(new DefaultItemAnimator());
        qu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_singleton.getInstance().getUser_type().toLowerCase().trim().matches("farmer")){
                    Intent nb=new Intent(history.this, farmersprofile_fragment1.class);
                    startActivity(nb);                }
                else{
                    Intent nb=new Intent(history.this, expertprofile_fragment.class);
                    startActivity(nb);
                }
            }
        });
        idn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_singleton.getInstance().getUser_type().toLowerCase().trim().matches("farmer")){
                    Intent nb=new Intent(history.this, farmersprofile_fragment1.class);
                    startActivity(nb);                }
                else{
                    Intent nb=new Intent(history.this, expertprofile_fragment.class);
                    startActivity(nb);
                }
            }
        });
        getfollowers();

        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder ob=new androidx.appcompat.app.AlertDialog.Builder(history.this,R.style.CustomAlertDialog);
                LinearLayout nb=new LinearLayout(history.this);
                LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                nb.setLayoutParams(param);
                LinearLayout.LayoutParams paramt=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramt.setMargins(5,5,5,5);
                if (followed.size()==0){
                    TextView img=new TextView(history.this);
                    img.setLayoutParams(paramt);
                    img.setText("No Followers");
                    img.setGravity(Gravity.CENTER);
                    nb.addView(img);
                }else{
                    try {
                        JSONArray arrayi = array_foll.getJSONArray("listi");
                        JSONArray arrayn = array_foll.getJSONArray("listn");
                        JSONArray arraya = array_foll.getJSONArray("lista");
                        JSONArray arrayu = array_foll.getJSONArray("listu");
                        for (int i=0;i<followed.size();i+=1){

                            LinearLayout ll =new LinearLayout(history.this);
                            LinearLayout.LayoutParams paramt11=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            paramt11.setMargins(50,50,50,50);
                            ll.setOrientation(LinearLayout.HORIZONTAL);


                            LinearLayout llleft =new LinearLayout(history.this);
                            LinearLayout.LayoutParams paramtll=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            paramtll.setMargins(20,20,20,20);
                            llleft.setLayoutParams(paramtll);
                            llleft.setOrientation(LinearLayout.VERTICAL);
                            ll.addView(llleft);
                            llleft.setHorizontalGravity(Gravity.CENTER);

                            LinearLayout llright =new LinearLayout(history.this);
                            llright.setOrientation(LinearLayout.VERTICAL);
                            llright.setLayoutParams(paramtll);
                            ll.addView(llright);
                            llright.setVerticalGravity(Gravity.CENTER);

                            CircleImageView img=new CircleImageView(history.this);
                            LinearLayout.LayoutParams paramimg=new LinearLayout.LayoutParams(100,100);
                            paramimg.setMargins(10,10,10,10);
                            //img.setScaleType(ImageView.ScaleType.FIT_XY);
                            img.setBorderWidth(5);
                            img.setBorderColor(getResources().getColor(R.color.greendark));
                            img.setLayoutParams(paramimg);
                            llleft.addView(img);
                            String imagepath= (String) arrayi.get(i);
                            //Log.d("dddddddd",imagepath);
                            if (!imagepath.matches("")){
                                try {
                                    Glide.with(history.this)
                                            .load("https://nibpp.krishimegh.in/Content/reportingbase/profile_image/" + imagepath+".jpg")
                                            .error(R.drawable.gray)
                                            .into(img);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Glide.with(history.this)
                                            .load(R.drawable.profile)
                                            .error(R.drawable.gray)
                                            .into(img);
                                }
                            }


                            String named= (String) arrayn.get(i);
                            if (!named.matches("")){
                                TextView txname1=new TextView(history.this);
                                LinearLayout.LayoutParams paramtxn1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                txname1.setLayoutParams(paramtxn1);
                                llright.addView(txname1);
                                txname1.setText(named);
                                txname1.setTextSize(20);
                                txname1.setTextColor(getResources().getColor(R.color.darkblue));}
                            String usr= (String) arrayu.get(i);
//                            if (!address.matches("")){
//                                TextView txname1=new TextView(history.this);
//                                LinearLayout.LayoutParams paramtxn1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                txname1.setLayoutParams(paramtxn1);
//                                llright.addView(txname1);
//                                txname1.setText(address);
//
//                                txname1.setTextColor(getResources().getColor(R.color.black));}
                            if (!followed.get(i).matches("")){
                                TextView txname1=new TextView(history.this);
                                LinearLayout.LayoutParams paramtxn1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                txname1.setLayoutParams(paramtxn1);
                                llright.addView(txname1);
                                txname1.setText("@"+followed.get(i));
                                txname1.setTextColor(getResources().getColor(R.color.brown));
                            }

                            nb.addView(ll);
                            ll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    seeprofile(usr);
                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                ob.setView(nb);
                ob.setTitle("Followers");

                ob.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ob.show();

            }
        });
       rp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(history.this,my_reports.class));
           }
       });

    }
    private void seeprofile(String getuser_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", getuser_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_profile")
                    .addJSONObjectBody(object).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String username=response.getString("user_name");
                        String contact_number=response.getString("contact_number");
                        String email=response.getString("email");
                        String institute=response.getString("institute");
                        String user_type=response.getString("user_type");
                        String address=response.getString("address");
                        String named=response.getString("named");
                        String report=response.getString("report");
                        String followers=response.getString("followers");
                        String imagepath=response.getString("img");
                        AlertDialog.Builder bv=new AlertDialog.Builder(context,R.style.CustomAlertDialog);
                        //CardView card=new CardView(context);
                        LinearLayout ll=new LinearLayout(context);
//                        ViewGroup.LayoutParams paramcard = card.getLayoutParams();
//                        card.setLayoutParams(paramcard);
//                        if (paramcard!=null){
//                            paramcard.height=LinearLayout.LayoutParams.WRAP_CONTENT;
//                            paramcard.width=LinearLayout.LayoutParams.WRAP_CONTENT;
//                            card.setLayoutParams(paramcard);
//                        }
//
//                        card.setRadius(10);
//                        card.addView(ll);
                        LinearLayout.LayoutParams param1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        ll.setLayoutParams(param1);
                        param1.setMargins(100,100,100,100);
                        //param1.setLayoutDirection(Gravity.CENTER);
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ll.setHorizontalGravity(Gravity.CENTER);


                        LinearLayout subll=new LinearLayout(context);
                        LinearLayout.LayoutParams paramsubll=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsubll.setMargins(50,50,50,50);
                        subll.setLayoutParams(paramsubll);
                        subll.setOrientation(LinearLayout.HORIZONTAL);

                        CircleImageView img=new CircleImageView(context);
                        LinearLayout.LayoutParams paramimg=new LinearLayout.LayoutParams(200,200);
                        paramimg.setMargins(10,10,10,10);
                        //img.setScaleType(ImageView.ScaleType.FIT_XY);
                        img.setBorderWidth(5);
                        img.setBorderColor(context.getResources().getColor(R.color.greendark));
                        img.setLayoutParams(paramimg);
                        subll.addView(img);
                        if (!imagepath.matches("")){
                            try {
                                Glide.with(context)
                                        .load("https://nibpp.krishimegh.in/Content/reportingbase/profile_image/" + imagepath+".jpg")
                                        .error(R.drawable.gray)
                                        .into(img);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Glide.with(context)
                                        .load(R.drawable.profile)
                                        .error(R.drawable.gray)
                                        .into(img);
                            }
                        }


                        LinearLayout subll1=new LinearLayout(context);
                        LinearLayout.LayoutParams paramsubll1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsubll1.setMargins(50,5,5,20);
                        subll1.setLayoutParams(paramsubll1);
                        subll1.setOrientation(LinearLayout.VERTICAL);
                        subll1.setVerticalGravity(Gravity.CENTER);

                        TextView txname=new TextView(context);
                        LinearLayout.LayoutParams paramtxn=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        txname.setLayoutParams(paramtxn);
                        subll1.addView(txname);
                        txname.setText(named);
                        txname.setTypeface(Typeface.SERIF,Typeface.BOLD);
                        txname.setTextSize(20);
                        txname.setTextColor(context.getResources().getColor(R.color.darkblue));

                        if (!address.matches("")){
                            TextView txname1=new TextView(context);
                            LinearLayout.LayoutParams paramtxn1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            txname1.setLayoutParams(paramtxn1);
                            subll1.addView(txname1);

                            txname1.setText(address);
                            txname1.setTextColor(Color.BLACK);}

                        if (!user_type.matches("")){
                            TextView txname1=new TextView(context);
                            LinearLayout.LayoutParams paramtxn1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            txname1.setLayoutParams(paramtxn1);
                            subll1.addView(txname1);

                            txname1.setText(user_type.toUpperCase(Locale.ROOT)+" Account");txname1.setTextColor(Color.BLACK);}


                        subll.addView(subll1);
                        ll.addView(subll);

                        if (!institute.matches("")){
                            TextView txname1=new TextView(context);
                            LinearLayout.LayoutParams paramtxn1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            paramtxn1.setMargins(50,50,50,50);
                            txname1.setLayoutParams(paramtxn1);
                            ll.addView(txname1);

                            txname1.setText(institute);txname1.setTextColor(Color.BLACK);}



                        LinearLayout subllre=new LinearLayout(context);
                        LinearLayout.LayoutParams paramsubllre=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsubllre.setMargins(50,5,50,5);
                        subllre.setLayoutParams(paramsubllre);
                        subllre.setOrientation(LinearLayout.HORIZONTAL);

                        LinearLayout subllrepo=new LinearLayout(context);
                        LinearLayout.LayoutParams paramsubllrepo=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsubllrepo.setMargins(15,15,15,15);
                        subllrepo.setLayoutParams(paramsubllrepo);
                        subllrepo.setOrientation(LinearLayout.VERTICAL);
                        subllrepo.setHorizontalGravity(Gravity.CENTER);
                        subllre.addView(subllrepo);

                        TextView txnamere=new TextView(context);
                        LinearLayout.LayoutParams paramtxnre=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        txnamere.setLayoutParams(paramtxnre);
                        subllrepo.addView(txnamere);
                        txnamere.setText("Reports")
                        ;txnamere.setGravity(Gravity.CENTER);
                        txnamere.setTypeface(Typeface.SERIF,Typeface.BOLD);txnamere.setTextColor(Color.BLACK);


                        TextView txname1re=new TextView(context);
                        LinearLayout.LayoutParams paramtxnre1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        txname1re.setLayoutParams(paramtxnre1);
                        subllrepo.addView(txname1re);
                        if (!report.matches("")){
                            txname1re.setText(report);
                            txname1re.setGravity(Gravity.CENTER);txname1re.setTextColor(Color.BLACK);}
                        ll.addView(subllre);






                        LinearLayout subllfoll=new LinearLayout(context);
                        LinearLayout.LayoutParams paramsubllfoll=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsubllfoll.setMargins(15,15,15,15);
                        subllfoll.setLayoutParams(paramsubllfoll);
                        subllfoll.setOrientation(LinearLayout.VERTICAL);
                        subllre.addView(subllfoll);
                        subllfoll.setHorizontalGravity(Gravity.CENTER);

                        TextView txnamefoll=new TextView(context);
                        LinearLayout.LayoutParams paramtxnfoll=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        txnamefoll.setLayoutParams(paramtxnfoll);
                        subllfoll.addView(txnamefoll);
                        txnamefoll.setText("Followers");
                        txnamefoll.setGravity(Gravity.CENTER);
                        txnamefoll.setTypeface(Typeface.SERIF,Typeface.BOLD);txnamefoll.setTextColor(Color.BLACK);

                        TextView txname1foll=new TextView(context);
                        LinearLayout.LayoutParams paramtxnfoll1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        txname1foll.setLayoutParams(paramtxnfoll1);
                        subllfoll.addView(txname1foll);
                        if (!followers.matches("")){
                            txname1foll.setText(followers);
                            txname1foll.setGravity(Gravity.CENTER);txname1foll.setTextColor(Color.BLACK);}

                        if (!contact_number.matches("")){
                            TextView txname1=new TextView(context);
                            LinearLayout.LayoutParams paramtxn1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            paramtxn1.setMargins(50,5,50,5);
                            txname1.setLayoutParams(paramtxn1);
                            ll.addView(txname1);

                            txname1.setText("Mob : "+contact_number);txname1.setTextColor(Color.BLACK);
                            txname1.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);}
                        if (!email.matches("")){
                            TextView txname1=new TextView(context);
                            LinearLayout.LayoutParams paramtxn1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            paramtxn1.setMargins(50,5,50,5);
                            txname1.setLayoutParams(paramtxn1);
                            ll.addView(txname1);
                            txname1.setTextColor(Color.BLACK);
                            txname1.setText("Email : "+email);
                            txname1.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);}

                        bv.setView(ll);
                        bv.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        bv.show();




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(ANError anError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void getfollowers(){
        JSONObject objectd = new JSONObject();
        try {
            objectd.put("user_id", user_singleton.getInstance().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        followed=new ArrayList<>();
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getfolloweds")
                .addJSONObjectBody(objectd)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = null;
                        //Log.d("hhhfff",response.toString());
                        array_foll=null;
                        array_foll=response;
                        try {
                            array = response.getJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {

                                followed.add((String) array.getString(i));}
                            qqqq=response.getInt("query");
                            rppp=response.getInt("reports");
                            report.setText(String.valueOf(rppp));
                            query.setText(String.valueOf(qqqq));
                            followers.setText(String.valueOf(followed.size()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void goforfliter(String value) {
        search.setText("searching...");
        s_index.clear();
        for(int s=0;s< dev.size();s+=1){
            history_model ad=dev.get(s);

            if (ad.getcrop().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getdate().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getdp().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") ||ad.getdisease().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") ){
                s_index.add(ad);
            }
        }
        if (s_index.size()!=0) {
            datare = new custom_adapter_history(history.this, s_index);
            grid.setAdapter(datare);
            tool=1;
            search.setText("GO");
            Toast.makeText(history.this, "Showing "+String.valueOf(s_index.size())+" Results", Toast.LENGTH_SHORT).show();
            imageView6.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_backspace_24, getTheme()));
        }
        else{
            search.setText("Go");
            Toast.makeText(history.this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }


    }

    private void setNavigationViewListener() {

            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer2);
            navigationView.setNavigationItemSelectedListener(this);

    }

    void gethistory(){
        JSONObject object = new JSONObject();
        try {
            object.put("user_id",user_singleton.getInstance().getUser_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_history")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("hhh",response.toString());
                        JSONArray array1 = null;
                        String totalident="";
                        try {
                            array1 = response.getJSONArray("data");
                            for (int i = 0; i < array1.length(); i++) {

                                JSONObject object = (JSONObject) array1.get(i);
                                history_model admin5 = new history_model();
                                admin5.setid(object.optString("id"));
                                admin5.setcrop(object.optString("crop"));
                                admin5.setdisease(object.optString("disease"));
                                admin5.setdate(object.optString("dt"));
                                admin5.setdp(object.optString("dp"));
                                admin5.setlike(object.optString("like"));
                                admin5.setdp_id(object.optString("dp_id"));
                                admin5.setcrop_id(object.optString("crop_id"));
                                dev.add(admin5);
                            }
                            totalident = response.optString("total");
                            if (totalident.matches("0")){
                                card.setVisibility(View.GONE);
                            }else{
                                card.setVisibility(View.VISIBLE);
                            }

                            dataa.notifyDataSetChanged();

                            grid.setAdapter(dataa);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tal.setText(totalident);
                        //Log.d(TAG, "onResponse: " + array1);

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
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
                      //  Log.d("fff", "DocumentSnapshot successfully written!");
                        Toast.makeText(history.this, "Signed out", Toast.LENGTH_LONG).show();
                        shared_pref.remove_shared_preference(history.this);
                        Intent intent = new Intent(history.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w("fff", "Error writing document", e);
                        Toast.makeText(history.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(history.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(history.this, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(history.this);
                    Intent intent = new Intent(history.this, Login.class);
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

                Intent intent = new Intent(history.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.help1:

                Intent intent1 = new Intent(history.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.disclaimer:

                Intent intent1q = new Intent(history.this, disclaimer.class);
                startActivity(intent1q);



                break;
            case R.id.home:

                Intent ibtwd;
                if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("expert")){
                    ibtwd=new Intent(history.this, expertprofile_fragment.class);
                }else{ ibtwd=new Intent(history.this, farmersprofile_fragment1.class);}
                startActivity(ibtwd);

                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.editme:

                Intent ibt=new Intent(history.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(history.this, contributor.class);
                startActivity(ibt1);

                break;

            case R.id.history:

                Intent ibtw=new Intent(history.this, history.class);
                startActivity(ibtw);

                break;
            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onStart() {

        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }



}