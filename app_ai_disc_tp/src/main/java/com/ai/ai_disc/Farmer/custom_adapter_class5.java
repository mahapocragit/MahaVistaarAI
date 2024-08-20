package com.ai.ai_disc.Farmer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.cardview.widget.CardView;

import com.ai.ai_disc.MultiChat_ExpertQuery_Activity;
import com.ai.ai_disc.MultiChat_FarmerExpertQuery_Activity;
import com.ai.ai_disc.R;
import com.ai.ai_disc.chat_expert_singleton;
import com.ai.ai_disc.chat_singleton;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by akmu on 3/20/2018.
 */

public class custom_adapter_class5 extends BaseAdapter {


        Context context;
        public  static LayoutInflater inflater=null;
        String number="1";


        String numb;
        List<QueryDetails1> queryDetailsList;

        public custom_adapter_class5(Context context, List<QueryDetails1> queryDetailsList){

            this.context=context;
            this.queryDetailsList=queryDetailsList;

            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

    @Override
        public int getCount() {
            return queryDetailsList.size();
        }

        @Override
        public Object getItem(int position) {
            return queryDetailsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view=inflater.inflate(R.layout.content_farmer_list_askcommunity,null);
            //ImageView imageView=(ImageView)view.findViewById(R.id.image_path);
            TextView chat=(TextView) view.findViewById(R.id.qu);
            CardView card=view.findViewById(R.id.card);

            chat.setText(queryDetailsList.get(position).getQuery_id());
            TextView descriptionText=(TextView) view.findViewById(R.id.head);
            descriptionText.setText(queryDetailsList.get(position).getDescription());

            Button status_vc=view.findViewById(R.id.chat);

            TextView crop=view.findViewById(R.id.crop);
            crop.setText(queryDetailsList.get(position).getcrop());


            TextView date=(TextView)view.findViewById(R.id.date);
            date.setText(queryDetailsList.get(position).gettim());
            ImageView img=(ImageView) view.findViewById(R.id.img);
            Glide.with(context)
                    .load(queryDetailsList.get(position).getImagePath())
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.errro_2)
                    .into(img);
            img.setEnabled(true);
//            if(!queryDetailsList.get(position).imagePath.isEmpty())
//            {
//                imageView.setVisibility(View.VISIBLE);
//                //card_view1.setVisibility(View.VISIBLE);
//                Glide.with(context)
//                        .load(queryDetailsList.get(position).imagePath)
//                        .placeholder(R.drawable.facilities)
//                        .error(R.drawable.gray)
//                        .into(imageView);
//            }
//            else
//            {
//                imageView.setVisibility(View.GONE);
//            }

            //farmerNameText.setText(queryDetailsList.get(position).firstName);
            //descriptionText.setText(queryDetailsList.get(position).description);
            if (queryDetailsList.get(position).getstatus_run().matches("0")) {
                status_vc.setText("CHAT");

            }else{
                status_vc.setText("Closed");
                status_vc.setTextColor(Color.parseColor("#c42721"));
                status_vc.setEnabled(false);
                card.setCardBackgroundColor(Color.GRAY);
            }
            status_vc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MultiChat_FarmerExpertQuery_Activity.class);
//
                    chat_singleton.getInstance().setchat(queryDetailsList.get(position));
                    context.startActivity(intent);
                }
            });

            return view;
        }
    }

