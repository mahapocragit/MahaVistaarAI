package com.ai.ai_disc.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Farmer.Account_expert;
import com.ai.ai_disc.R;
import com.ai.ai_disc.editprofile;
import com.ai.ai_disc.report_page;
import com.ai.ai_disc.report_singleton;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
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

import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class report_adapter18 extends RecyclerView.Adapter<report_adapter18.MyViewHolder> {
    String  AUTH_KEY_FCM="";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    String bv="";
    ArrayList<reportdata> list_report;
    List<ImageView> flll = new ArrayList<>();
    List<TextView> fllltex = new ArrayList<>();

    public report_adapter18(Context context, ArrayList<reportdata> list_report) {
        this.context = context;
        this.list_report=list_report;


    }
    public List<TextView> getTextViews(){
        return fllltex;
    }
    public List<ImageView> getImageView(){
        return flll;
    }
    public ArrayList<reportdata> getreport(){
        return list_report;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView dates;
        TextView username;
        TextView place,followtext;
        ImageView follow;
        ImageView like;
        ImageView comment;
        TextView texts,infor;
        TextView desc,likednumber;
        LinearLayout addimages;
        ProgressBar bar;

        public MyViewHolder(View view) {
            super(view);


            like = (ImageView) view.findViewById(R.id.like);
            comment = (ImageView) view.findViewById(R.id.comment);
            follow = (ImageView) view.findViewById(R.id.follow);
            place = (TextView) view.findViewById(R.id.place);
            desc=view.findViewById(R.id.desc);
            username = (TextView) view.findViewById(R.id.username);
            texts= (TextView) view.findViewById(R.id.text);
            dates= (TextView) view.findViewById(R.id.date);
            infor= (TextView) view.findViewById(R.id.infor);
            likednumber= (TextView) view.findViewById(R.id.likenumber);
            addimages=view.findViewById(R.id.addimage);
            bar=view.findViewById(R.id.progressBar4);
            followtext=view.findViewById(R.id.followtext);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_layout_reports1, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        reportdata data = list_report.get(position);
        flll.add(holder.follow);
        fllltex.add(holder.followtext);

        holder.username.setText(data.getuser_name());
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeprofile(data.getuser_id());
            }
        });
        holder.dates.setText(data.getdated());
        holder.place.setText(data.getlocate());
        if (user_singleton.getInstance().getUser_id().matches(data.getuser_id())){
            holder.followtext.setText("Myself");
            holder.followtext.setEnabled(false);
        }

        holder.addimages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nb=new Intent(context,report_page.class);
//
                report_singleton.getInstance().setdata(data);
                context.startActivity(nb);
            }
        });
        try{
        holder.texts.setText(data.gethead());
            holder.infor.setText(data.getinfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data.getfl()){

            holder.followtext.setText("following");
            holder.follow.setVisibility(View.VISIBLE);
        }if (data.getlk()){
            Glide.with(context)
                    .load(R.drawable.ic_baseline_favorite_24)
                    .error(R.drawable.gray)
                    .into(holder.like);
        }
        if (data.getlikednumber().matches("0")){
            holder.likednumber.setVisibility(View.GONE);
        }else{
            holder.likednumber.setText(String.valueOf(data.getlikednumber()));
        }
        holder.followtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getfl()){

                    data.setfl(false);
                    holder.follow.setVisibility(View.GONE);
                    followup(0,data.getuser_id(),"Unfollowed "+data.getuser_name());
                    holder.followtext.setText("follow");
                }
                else{
                    setfollownotice(data.getuser_name());
                    data.setfl(true);
                    holder.follow.setVisibility(View.VISIBLE);
                    holder.followtext.setText("following");
                    followup(1,data.getuser_id(),"following "+data.getuser_name());}
            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getlk()){
                    Glide.with(context)
                            .load(R.drawable.ic_baseline_favorite_border_24)
                            .error(R.drawable.gray)
                            .into(holder.like);
                    data.setlk(false);
                    followup(2,data.getrecord_id(),"Unliked");
                    String newl=String.valueOf(Integer.parseInt(data.getlikednumber())-1);
                    data.setlikednumber(newl);
                    if (newl.matches("0")){
                        holder.likednumber.setVisibility(View.GONE);
                    }else{
                        holder.likednumber.setText(String.valueOf(data.getlikednumber()));
                    }
                }
                else{
                    Glide.with(context)
                            .load(R.drawable.ic_baseline_favorite_24)
                            .error(R.drawable.gray)
                            .into(holder.like);
                    data.setlk(true);
                    followup(3,data.getrecord_id(),"Liked");
                    String newl=String.valueOf(Integer.parseInt(data.getlikednumber())+1);
                    data.setlikednumber(newl);
                    if (newl.matches("0")){
                        holder.likednumber.setVisibility(View.GONE);
                    }else{
                        holder.likednumber.setText(String.valueOf(data.getlikednumber()));
                    }
                }
            }
        });

        holder.bar.setVisibility(View.VISIBLE);
        JSONObject object = new JSONObject();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics=context.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        try {
            object.put("report_id", data.getrecord_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try{
            AndroidNetworking.post("https://aidisc.krishimegh.in:32517/get_news_images")
                    .addJSONObjectBody(object).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    holder.bar.setVisibility(View.GONE);

                    try {
                        JSONArray arr=jsonObject.getJSONArray("image_files");

                        if (arr.length()==1){
                            ImageView imn=new ImageView(context);
                            LinearLayout.LayoutParams vb=new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);

                            imn.setLayoutParams(vb);

                            imn.setScaleType(ImageView.ScaleType.FIT_XY);

                            String object = (String) arr.get(0);
                            byte[] decodedString = Base64.decode(object, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imn.setImageBitmap(decodedByte);
                            holder.addimages.addView(imn);
                            holder.desc.setText("1 image");

                        }
                        else{
                            for (int i=0;i<arr.length();i+=1){
                                ImageView imn=new ImageView(context);
                                LinearLayout.LayoutParams vb=new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.MATCH_PARENT);
                                imn.setLayoutParams(vb);
                                imn.setScaleType(ImageView.ScaleType.FIT_XY);
                                    String object = (String) arr.get(i);
                                    byte[] decodedString = Base64.decode(object, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    imn.setImageBitmap(decodedByte);

                                holder.addimages.addView(imn);
                            }
                            holder.desc.setText(String.valueOf(arr.length())+" images");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Toast.makeText(context, "Error 1", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    holder.bar.setVisibility(View.GONE);

                    Toast.makeText(context, "Error 2", Toast.LENGTH_SHORT).show();
                }
            });
            //Log.d("hhhh",jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nb=new Intent(context,report_page.class);
//
                report_singleton.getInstance().setdata(data);
                context.startActivity(nb);
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
    private void setfollownotice(String getuser_name) {

        db.collection("loginaidisc")
                .whereEqualTo("username", getuser_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("homedata", document.getId() + " => " + document.getData().get("token").toString());
                                sendmsg(String.valueOf(document.getData().get("token")));
                            }
                        } else {
                            Log.d("homedata", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    void sendmsg(String token_to){
        if (!token_to.isEmpty()  ){

            JSONArray tokens = new JSONArray();

                tokens.put(token_to.trim());

            JSONObject body = new JSONObject();
            JSONObject data1 = new JSONObject();

            try {
                data1.put("request", "follow");

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
            Log.d("token not found", "onError: " );
        }



    }
    private void followup(int i,String info,String link) {
        JSONObject object = new JSONObject();

        try {

            object.put("number", i);
            object.put("before", info);
            object.put("next", user_singleton.getInstance().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/set_like")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean df=false;
                        try {
                            df=response.getBoolean("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (df) {
                            Toast.makeText(context, link, Toast.LENGTH_SHORT).show();

                            if(i==0){
                                Intent intent = new Intent("follow");
                                //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                                intent.putExtra("position",2);
                                intent.putExtra("user",info);

                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }
                            if(i==1){
                                Intent intent = new Intent("follow");
                                //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                                intent.putExtra("position",1);
                                intent.putExtra("user",info);


                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }
                        }
                        else{
                            Toast.makeText(context, "Error 5", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, "Error 6", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list_report.size();
    }

}



