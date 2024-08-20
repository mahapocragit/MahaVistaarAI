package com.ai.ai_disc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Farmer.TimeAgo2;
import com.ai.ai_disc.model.MultiChatFarmerExpertQuery_Model;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class customAdapterMultiChatFarmerExpertQuery extends RecyclerView.Adapter<customAdapterMultiChatFarmerExpertQuery.MyViewHolder> {

    Context context;

    List<MultiChatFarmerExpertQuery_Model> data_list;


    public customAdapterMultiChatFarmerExpertQuery(Context context, List<MultiChatFarmerExpertQuery_Model> data_list) {
        this.context = context;
        this.data_list=data_list;


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name_text,time_text;
        ImageView imageView,expertTypeImage,farmerImage;
        LinearLayout linearLayout,time_layout;
        TextView description_text;

        TextView dats;
        CardView card;

        public MyViewHolder(View view)
        {
            super(view);


            name_text = (TextView) view.findViewById(R.id.name);
            card=view.findViewById(R.id.card);
            imageView = (ImageView) view.findViewById(R.id.image);
            time_text=(TextView)view.findViewById(R.id.time_text) ;
            expertTypeImage=(ImageView)view.findViewById(R.id.farmer_name_Text3);
            farmerImage=(ImageView)view.findViewById(R.id.farmer_name_Text2);
            linearLayout=(LinearLayout)view.findViewById(R.id.customLayout);
            description_text = (TextView) view.findViewById(R.id.query_description);
            dats=view.findViewById(R.id.datse);

        }
    }


    @Override
    public customAdapterMultiChatFarmerExpertQuery.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_multichatadapter, parent, false);

        return new customAdapterMultiChatFarmerExpertQuery.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(customAdapterMultiChatFarmerExpertQuery.MyViewHolder holder, int position) {

        MultiChatFarmerExpertQuery_Model data = data_list.get(position);
        LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        if (data.getreplerdated().matches("")){
            holder.dats.setVisibility(View.GONE);
        }else{
            holder.dats.setVisibility(View.VISIBLE);
            holder.dats.setText(data.getreplerdated());
        }
        if (!user_singleton.getInstance().getUser_id().matches(data.getReplyUserId())){
            holder.name_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (data.getUserType().equals("10")){
                    seeprofile(data.getReplyUserId());}
                    else{
                        seeprofile(data.getReply_expertId());
                    }
                }
            });
        }

        //farmer
        if(data.getUserType().equals("10"))
        {
            holder.expertTypeImage.setVisibility(View.GONE);
            holder.farmerImage.setVisibility(View.VISIBLE);
            param.setMargins(5,2,80,2);
            holder.card.setLayoutParams(param);
            holder.card.setCardBackgroundColor(Color.parseColor("#288ebf"));

        }
        //expert
        if(data.getUserType().equals("7"))
        {holder.card.setCardBackgroundColor(Color.parseColor("#169B89"));
            param.setMargins(80,2,5,2);
            holder.card.setLayoutParams(param);
            holder.expertTypeImage.setVisibility(View.VISIBLE);
            holder.farmerImage.setVisibility(View.GONE);

        }

        if(data.getReplyUserId().isEmpty() && data.getReply_expertId().isEmpty())
        {
            holder.name_text.setVisibility(View.GONE);
            holder.description_text.setVisibility(View.GONE);
        }


        if(data.getReply_ImagePath().isEmpty())
        {
            holder.imageView.setVisibility(View.GONE);
        }
        if(!data.getReplyerName().isEmpty())
        {
            holder.name_text.setText(data.getReplyerName());
            holder.description_text.setText(data.getReplyQueryResolution());
            if(!data.getReplerTime().isEmpty())
            {
                //TimeAgo2 timeAgo2 = new TimeAgo2();
               // long lng=Long.parseLong(data.getReplerTime());
              //  String MyFinalValue = TimeAgo2.getTimeAgo(Long.parseLong(data.getReplerTime()));
               // Log.d("data",MyFinalValue);

                holder.time_text.setText(data.getReplerTime());
            }
            else
            {
                holder.time_layout.setVisibility(View.GONE);
            }



        }



        if(!data.getReply_ImagePath().isEmpty())
        {
            holder.imageView.setVisibility(View.GONE);
            String imagePath="https://nibpp.krishimegh.in/Content/NIDPPD/FILE_UPLOAD/"+data.getReply_ImagePath();
            //card_view1.setVisibility(View.VISIBLE);
           // Log.d("56565",imagePath);
            Glide.with(context)
                    .load(imagePath.trim())
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(holder.imageView);
        }
        else
        {
            holder.imageView.setVisibility(View.GONE);
        }

        if(data.getReplyeQueryId().equals("null")||data.getReplyeQueryId().isEmpty())
        {
            holder.linearLayout.setVisibility(View.GONE);

        }


    }
    private void seeprofile(String getuser_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", getuser_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d("custom",object.toString());
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
                                        .load("https://nibpp.krishimegh.in/Content/reportingbase/experts/" + imagepath+".jpg")
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
    @Override
    public int getItemCount() {
        return data_list.size();
    }

}


