package com.ai.ai_disc;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ai.ai_disc.model.commentdata;
import com.ai.ai_disc.model.reportdata;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class report_page extends AppCompatActivity {
    public String user_id ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";
    String  AUTH_KEY_FCM="";
    Context context;
    ArrayList<commentdata> list;
    public String record_id ;
    public String crop_id ;
    public String crop_name ;
    public String report_type ;
    LinearLayout lincomm;
    public String dpname ;
    public String disease_id ;
    public String nut_id ;
    public String insect_id ;
    int commn;
    public String dated ;
    int sho;
    public String info ;
    public String head ;
    CardView carddetails;
    ImageView showdetails;
    TextView img,nextcomments,precomments;
    public String lat ;
    LinearLayout linimage ;
    public String locate ;
    EditText editText_enterMessage;
    InternetReceiver internet;
    ImageView send;
    LinearLayout croplin,catelin,dplin,linnot,croplinfull;
    private ArrayList<String> exp_token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);
        reportdata data=report_singleton.getInstance().getdata();
        SwipeRefreshLayout lay= findViewById(R.id.refr);
        context=report_page.this;
        lay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshdata();
                lay.setRefreshing(false);
            }
        });
        record_id = data.getrecord_id();
        dpname = data.getdpname();
        report_type = data.getreport_type();
        crop_name = data.getcrop_name();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sho=0;
        commn=0;
        head = data.gethead();
        dated = data.getdated();
        info = data.getinfo();
        locate = data.getlocate();
        list=new ArrayList<>();

        linimage=findViewById(R.id.imagelayout);
        carddetails=findViewById(R.id.carddetails);
        showdetails=findViewById(R.id.showdetails);
        send=findViewById(R.id.sendButton);
        editText_enterMessage=findViewById(R.id.editText_enterMessage);
        lincomm=findViewById(R.id.lincomm);
        carddetails.setVisibility(View.GONE);
        showdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sho==0){
                    carddetails.setVisibility(View.VISIBLE);
                    sho=1;
                    showdetails.setImageDrawable(getResources().getDrawable(R.drawable.dropdown2, getTheme()));
                }else{
                    carddetails.setVisibility(View.GONE);
                    sho=0;
                    showdetails.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_receipt_long_24, getTheme()));
                }
            }
        });
        precomments=findViewById(R.id.previouscomments);
        nextcomments=findViewById(R.id.nextcomments);
        nextcomments.setVisibility(View.GONE);
        croplin=findViewById(R.id.lincrop);
        catelin=findViewById(R.id.lincat);
        dplin=findViewById(R.id.lindp);
        linnot=findViewById(R.id.linnot);
        croplinfull=findViewById(R.id.croplinfull);
        //img=findViewById(R.id.img);
        TextView reportno=findViewById(R.id.reportno);
        reportno.setText("Report No. "+record_id);
        TextView datwed=findViewById(R.id.username);
        datwed.setText(data.getuser_name());
        datwed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeprofile(data.getuser_id());
            }
        });
        getchatter(data.getuser_name());
        TextView palced=findViewById(R.id.placed);
        palced.setText(locate+", "+data.getdated());

        TextView crop=findViewById(R.id.cropname);
        crop.setText(crop_name);
        TextView infest=findViewById(R.id.infest);
        TextView dpnamev=findViewById(R.id.dpname);
        dpnamev.setText(dpname);
        internet=new InternetReceiver();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics=this.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        TextView hd=findViewById(R.id.head);
        TextView inf=findViewById(R.id.info);

        hd.setText(head);
        inf.setText(info);
        switch (report_type){
            case "1":{
                croplin.setVisibility(View.GONE);
                dplin.setVisibility(View.GONE);
                catelin.setVisibility(View.GONE);
                break;
            }case "2":{
                linnot.setVisibility(View.GONE);
                infest.setText("Disease Infestation");
                break;
            }case "3":{
                linnot.setVisibility(View.GONE);infest.setText("Insect Occurrence");
                break;
            }
            case "4":{
                linnot.setVisibility(View.GONE);infest.setText("Nutrient Deficiency");
                break;
            }
            case "5":{
                infest.setText("Other Field Incidence");
                linnot.setVisibility(View.GONE);
                dplin.setVisibility(View.GONE);

                break;
            }
        }

        JSONObject object = new JSONObject();
        try {
            object.put("report_id", record_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //img.setText("Images Loading...");
        //Log.d("hhhh",object.toString());
        try{
            AndroidNetworking.post("https://aidisc.krishimegh.in:32517/get_news_images")
                    .addJSONObjectBody(object).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    //img.setText("Report Images ");
                    //Log.d("jjjj",jsonObject.toString());
                    try {
                        JSONArray arr=jsonObject.getJSONArray("image_files");
                        if (arr.length()==0){
                            linimage.setVisibility(View.GONE);
                            //img.setText("No images");
                        }else{
                            if (arr.length()==1){
                                ImageView imn=new ImageView(report_page.this);
                                LinearLayout.LayoutParams vb=new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);

                                imn.setLayoutParams(vb);

                                imn.setScaleType(ImageView.ScaleType.FIT_XY);

                                String object = (String) arr.get(0);
                                byte[] decodedString = Base64.decode(object, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                imn.setImageBitmap(decodedByte);
                                imn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder ob=new AlertDialog.Builder(report_page.this);
                                        LinearLayout nb=new LinearLayout(report_page.this);
                                        ImageView img=new ImageView(report_page.this);

                                        LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                        nb.setLayoutParams(param);
                                        img.setLayoutParams(param);
                                        img.setScaleType(ImageView.ScaleType.FIT_XY);
                                        img.setImageBitmap(decodedByte);
                                        nb.addView(img);
                                        ob.setView(nb);
                                        ob.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        ob.show();

                                    }
                                });
                                linimage.addView(imn);


                            }
                            else{
                                for (int i=0;i<arr.length();i+=1){
                                    ImageView imn=new ImageView(report_page.this);
                                    LinearLayout.LayoutParams vb=new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.MATCH_PARENT);
                                    imn.setLayoutParams(vb);
                                    imn.setScaleType(ImageView.ScaleType.FIT_XY);
                                    String object = (String) arr.get(i);
                                    byte[] decodedString = Base64.decode(object, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    imn.setImageBitmap(decodedByte);
                                    imn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AlertDialog.Builder ob=new AlertDialog.Builder(report_page.this);
                                            LinearLayout nb=new LinearLayout(report_page.this);
                                            ImageView img=new ImageView(report_page.this);

                                            LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                            nb.setLayoutParams(param);
                                            img.setLayoutParams(param);
                                            img.setScaleType(ImageView.ScaleType.FIT_XY);
                                            img.setImageBitmap(decodedByte);
                                            nb.addView(img);
                                            ob.setView(nb);
                                            ob.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            ob.show();

                                        }
                                    });
                                    linimage.addView(imn);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //img.setText("Report Images ");
                        Toast.makeText(report_page.this, "Error 1", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    //img.setText("Report Images ");
                    Toast.makeText(report_page.this, "Error 2", Toast.LENGTH_SHORT).show();
                }
            });
                    //Log.d("hhhh",jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        getcomments();
        precomments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commn+=4;
                addviewdata();

            }
        });
        nextcomments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commn-=4;
                addviewdata();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gdh=editText_enterMessage.getText().toString();
                if (!gdh.matches("")){
                    sendcomments(gdh);
                }else{
                    Toast.makeText(report_page.this, "Write comments", Toast.LENGTH_SHORT).show();
                }
                        
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("comment"));
    }
    public BroadcastReceiver mMessageReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int signal = intent.getIntExtra("position", 0);
            String qu = intent.getStringExtra("report");
            if (signal==1 && qu.matches(record_id)){
                refreshdata();
                //Log.d("someone","texted");
            }
        }
    };
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
                        android.app.AlertDialog.Builder bv=new android.app.AlertDialog.Builder(context,R.style.CustomAlertDialog);
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
    private void refreshdata() {
        nextcomments.setVisibility(View.GONE);
        commn=0;
        getcomments();
    }
    void getchatter(String usr){
        JSONObject objectd = new JSONObject();
        try {
            objectd.put("report_id", record_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        exp_token=new ArrayList<>();
        ArrayList<String>commenter=new ArrayList<>();
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getcommentresponder")
                .addJSONObjectBody(objectd)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = null;
                        //Log.d("hhh",response.toString());
                        try {
                            array = response.getJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {
                                String sf=(String) array.getString(i);
                                commenter.add(sf);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        commenter.add(usr);
                        if (commenter.contains(user_singleton.getInstance().getUser_name())){
                            commenter.remove(user_singleton.getInstance().getUser_name());
                        }

                        if (commenter.size()!=0){

                            for (int i =0;i<commenter.size();i+=1){
                                //Log.d("lllllllll1", commenter.get(i));
                                db.collection("loginaidisc")
                                        .whereEqualTo("username", commenter.get(i))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        //Log.d("homedata", document.getId() + " => " + document.getData().get("token").toString());
                                                        String tk = String.valueOf(document.getData().get("token"));
                                                        exp_token.add(tk);
                                                    }
                                                } else {
                                                   // Log.d("homedata", "Error getting documents: ", task.getException());
                                                }

                                            }
                                        });
                            }
                        }



                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void sendcomments(String gdh) {
        JSONObject object = new JSONObject();
        try {
            object.put("news_id", record_id);
            object.put("user_id", user_singleton.getInstance().getUser_id());
            object.put("comment", gdh);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d("fff",object.toString());

        try{
            AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Add_comment")
                    .addJSONObjectBody(object).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    //Log.d("sd1",response.toString());
                    try {//Log.d("sd",response.getString("message"));
                        boolean res=response.getBoolean("result");

                        if (res){
                            commn=0;
                            getcomments();
                            editText_enterMessage.getText().clear();
                            sendmsg(gdh);

                        }
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
    void sendmsg(String msg){
        //Log.d("lllllllll", exp_token.toString());
        if (exp_token.size()!=0  ){

            JSONArray tokens = new JSONArray();
            for (int i =0;i<exp_token.size();i+=1){
                tokens.put(exp_token.get(i).trim());
            }
            JSONObject body = new JSONObject();
            JSONObject data1 = new JSONObject();

            try {
                data1.put("request", "comment");
                data1.put("msg", msg);
                data1.put("report", record_id);
                data1.put("username", user_singleton.getInstance().getUser_name());
                body.put("data", data1);
                body.put("registration_ids", tokens);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        /*
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");
         .addHeaders("Authorization","key="+AUTH_KEY_FCM)
                .addHeaders("Content-Type","application/json")
         */
            AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getfcn")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                AUTH_KEY_FCM=jsonObject.getString("fcn");
                                AndroidNetworking.post("https://fcm.googleapis.com/fcm/send")
                                        .addHeaders("Authorization", "key=" + AUTH_KEY_FCM)
                                        .addHeaders("Content-Type", "application/json")


                                        .addJSONObjectBody(body)


                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                // do anything with response
                                                //Log.d("send _notify", "onResponse: " + response);


                                                try {
                                                    int code = (int) response.get("success");

                                                    if (code == 1) {
                                                        /// Toast.makeText(Outgoing.this,"Request sent",Toast.LENGTH_LONG).show();
                                                        //request.setText("Request sent");
                                                        //  finish();
                                                    } else if (code == 0) {
                                                        //  Toast.makeText(Outgoing.this,"Request  not sent",Toast.LENGTH_LONG).show();
                                                        //request.setText("Request not  sent");
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                            @Override
                                            public void onError(ANError error) {
                                                // handle error
                                                //Log.d(TAG, "onError: " + error.getMessage());
                                                // Toast.makeText(Outgoing.this,"Error,Request not sent",Toast.LENGTH_LONG).show();
                                                //request.setText("Error");
                                            }
                                        });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }else{
           // Log.d("token not found", "onError: " );
        }



    }
    private void getcomments() {
        list.clear();
        JSONObject object = new JSONObject();
        try {
            object.put("report_id", record_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getcomments")
                    .addJSONObjectBody(object).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        //Log.d("sd1",response.toString());
                        JSONArray arr=response.getJSONArray("com");
                        for (int f=0;f<arr.length();f+=1){
                            commentdata data=new commentdata();
                            JSONObject obj= (JSONObject) arr.get(f);
                            data.setdated(obj.getString("dated"));
                            data.settext(obj.getString("text"));
                            data.setrecord_id(obj.getString("report_id"));
                            data.setuser_name(obj.getString("user_name"));
                            data.setuser_id(obj.getString("user_id"));
                            list.add(data);
                        }
                        if (list.size()==0){
                            Toast.makeText(report_page.this, "No comments", Toast.LENGTH_SHORT).show();
                            precomments.setVisibility(View.GONE);
                            nextcomments.setVisibility(View.GONE);
                        }else{
                            addviewdata();
                        }



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

    private void addviewdata() {
        lincomm.removeAllViews();
        int cv=commn+4;
        if (commn>=4){
            nextcomments.setVisibility(View.VISIBLE);
        }else
        {nextcomments.setVisibility(View.GONE);}


        if (cv<list.size()){
            for (int g=commn;g<cv;g+=1){
                commentdata data=list.get(g);
                CardView vc=new CardView(this);
                vc.setCardBackgroundColor(getResources().getColor(R.color.white));
                vc.setRadius(10);

                LinearLayout master=new LinearLayout(report_page.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5,2,5,2);
                master.setLayoutParams(params);master.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams paramc = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramc.setMargins(20,2,10,2);
                LinearLayout.LayoutParams paramc1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramc1.setMargins(30,2,10,2);
                LinearLayout child=new LinearLayout(report_page.this);
                child.setOrientation(LinearLayout.HORIZONTAL);
                child.setLayoutParams(params);
                vc.setLayoutParams(params);
                TextView user=new TextView(report_page.this);
                TextView text=new TextView(report_page.this);
                TextView date=new TextView(report_page.this);
                user.setTextColor(getResources().getColor(R.color.darkblue));
                date.setTextColor(getResources().getColor(R.color.brown));
                text.setTextColor(Color.BLACK);

                user.setLayoutParams(paramc);
                date.setLayoutParams(paramc);

                child.addView(user);
                child.addView(date);
                master.addView(child);
                user.setTextSize(18);
                user.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

                text.setLayoutParams(params);
                master.addView(text);
                user.setText(data.getuser_name());
                date.setText(data.getdated());
                text.setLayoutParams(paramc1);
                text.setText(data.gettext());
                vc.addView(master);
                lincomm.addView(vc);
            }
            precomments.setVisibility(View.VISIBLE);

        }else{
            for (int g=commn;g<list.size();g+=1){
                CardView vc=new CardView(this);

                vc.setCardBackgroundColor(getResources().getColor(R.color.white));
                LinearLayout.LayoutParams paramc1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramc1.setMargins(30,2,10,2);
                vc.setRadius(10);
                LinearLayout master=new LinearLayout(report_page.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5,2,5,2);
                vc.setLayoutParams(params);
                master.setLayoutParams(params);master.setOrientation(LinearLayout.VERTICAL);
                commentdata data=list.get(g);

                LinearLayout.LayoutParams paramc = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramc.setMargins(20,2,10,2);
                LinearLayout child=new LinearLayout(report_page.this);
                child.setOrientation(LinearLayout.HORIZONTAL);
                child.setLayoutParams(params);

                TextView user=new TextView(report_page.this);

                TextView date=new TextView(report_page.this);
                user.setLayoutParams(paramc);
                date.setLayoutParams(paramc);

                child.addView(user);
                child.addView(date);
                master.addView(child);

                TextView text=new TextView(report_page.this);
                user.setTextSize(18);
                user.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

                text.setLayoutParams(params);
                master.addView(text);
                user.setText(data.getuser_name());
                date.setText(data.getdated());
                text.setText(data.gettext());
                text.setLayoutParams(paramc1);
                user.setTextColor(getResources().getColor(R.color.darkblue));
                date.setTextColor(getResources().getColor(R.color.brown));
                text.setTextColor(Color.BLACK);
                vc.addView(master);
                lincomm.addView(vc);
            }
            precomments.setVisibility(View.GONE);
        }


    }

    private void addview(Bitmap decodedByte) {
        ImageView nimage=new ImageView(report_page.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(600, LinearLayout.LayoutParams.MATCH_PARENT);
        //params.setMargins(5, 5, 5, 5);
        nimage.setLayoutParams(params);
        nimage.setScaleType(ImageView.ScaleType.FIT_XY);
        nimage.setImageBitmap(decodedByte);
        linimage.addView(nimage);
        nimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ob=new AlertDialog.Builder(report_page.this);
                LinearLayout nb=new LinearLayout(report_page.this);
                ImageView img=new ImageView(report_page.this);

                LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                nb.setLayoutParams(param);
                img.setLayoutParams(param);
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                img.setImageBitmap(decodedByte);
                nb.addView(img);
                ob.setView(nb);
                ob.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ob.show();

            }
        });
    }
    @Override
    public void onStart() {

        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }



//    @Override
//    public void onStop() {
//        super.onStop();
//        unregisterReceiver(internet);
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                break;
            default:
                break;

        }

        return true;
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.setPriority(2);
        //notify_singleton.getInstance().setmsg("1");
        registerReceiver(mMessageReceiver, filter);
        super.onResume();
    }
}