package com.ai.ai_disc;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Dataentry.ListAccountInfo;
import com.ai.ai_disc.model.contributor_model;
import com.ai.ai_disc.model.history_model;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class custom_adapter_history extends RecyclerView.Adapter<custom_adapter_history.MyViewHolder>  {

    Context context;
    ArrayList<String> user_id_array;
    ArrayList<String> user_name_array;
    ArrayList<String> name_array;
    ArrayList<String> contact_number_array;
    ArrayList<String> email_id_array;
    ArrayList<String> timestamp_array;
    ArrayList<String> created_by_array;
    ArrayList<String> created_by_name_array;
    ArrayList<ListAccountInfo> listAccountInfos;
    ArrayList<history_model> listDataFiltered;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView crop ;
        private TextView diseased ;
        TextView dp ,date;
        ImageView rate ,gotod;


        public MyViewHolder(View grid) {
            super(grid);
             crop = (TextView) grid.findViewById(R.id.crop);
             diseased = (TextView) grid.findViewById(R.id.dp);
             dp = (TextView) grid.findViewById(R.id.dptype);
            date = (TextView) grid.findViewById(R.id.dat);
             rate = (ImageView) grid.findViewById(R.id.imageView7);
            gotod = (ImageView) grid.findViewById(R.id.gotopage);

        }
    }



    public custom_adapter_history(history list_account, ArrayList<history_model> listAccountInfos)
    {
        this.context=list_account;

        this.listDataFiltered=listAccountInfos;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        history_model dataposition=listDataFiltered.get(i);
        myViewHolder.date.setText(dataposition.getdate());
        myViewHolder.crop.setText(dataposition.getcrop());
        if (dataposition.getdp().matches("0")){
            myViewHolder.dp.setText(String.valueOf(i+1)+". Healthy Crop");
           // Log.d("guuuu",String.valueOf(i));
            myViewHolder.diseased.setText("");
        }if (dataposition.getdp().matches("1")){
            myViewHolder.dp.setText(String.valueOf(i+1)+". Disease Identification");
            myViewHolder.diseased.setText(dataposition.getdisease());
        }
        if (dataposition.getdp().matches("2")){
            myViewHolder.dp.setText(String.valueOf(i+1)+". Insect Identification");
            myViewHolder.diseased.setText(dataposition.getdisease());
        }



        switch (dataposition.getlike()){
            case "1":{
                myViewHolder.rate.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_sentiment_very_dissatisfied_24, context.getTheme()));break;
            }
            case "2":{
                myViewHolder.rate.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_sentiment_dissatisfied_24, context.getTheme()));break;
            }
            case "3":{
                myViewHolder.rate.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_sentiment_satisfied_24, context.getTheme()));break;
            }
            case "4":{
                myViewHolder.rate.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_sentiment_satisfied_alt_24, context.getTheme()));break;
            }
            case "5":{
                myViewHolder.rate.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_sentiment_very_satisfied_24, context.getTheme()));break;
            }

        }
        myViewHolder.gotod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nb=new Intent(context,history_page.class);
                nb.putExtra("id",dataposition.getid());
                nb.putExtra("crop",dataposition.getcrop());
                nb.putExtra("dp_type",dataposition.getdp());
                nb.putExtra("dp_id",dataposition.getdp_id());
                nb.putExtra("crop_id",dataposition.getcrop_id());
                nb.putExtra("rate",dataposition.getlike());
                nb.putExtra("dpname",dataposition.getdisease());
                nb.putExtra("dat",dataposition.getdate());
                context.startActivity(nb);

            }
        });


    }



    @Override
    public int getItemCount() {
        return listDataFiltered.size();
    }

    /*
    public void click(String user_id){
        Intent intent = new Intent(context,Validator_image_record1.class);
        intent.putExtra("user_id",user_id);
        // intent.putExtra("user_id",user_id_array.get(position));
        context.startActivity(intent);
    }

     */

//    public void click(String user_id){
//        Intent intent = new Intent(context,validator_image_record2.class);
//        intent.putExtra("user_id",user_id);
//        // intent.putExtra("user_id",user_id_array.get(position));
//        context.startActivity(intent);
//    }







}
