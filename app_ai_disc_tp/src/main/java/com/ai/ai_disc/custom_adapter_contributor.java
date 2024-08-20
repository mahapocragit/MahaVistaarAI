package com.ai.ai_disc;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Dataentry.ListAccountInfo;
import com.ai.ai_disc.model.contributor_model;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class custom_adapter_contributor extends RecyclerView.Adapter<custom_adapter_contributor.MyViewHolder>  {

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
    ArrayList<contributor_model> listDataFiltered;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nam ;
        TextView des ;
        TextView phn ;
        TextView email1 ;
        ImageView imageView;

        public MyViewHolder(View grid) {
            super(grid);
             nam = (TextView) grid.findViewById(R.id.grid_name);
             des = (TextView) grid.findViewById(R.id.grid_designation);
             phn = (TextView) grid.findViewById(R.id.grid_phn);
             email1 = (TextView) grid.findViewById(R.id.grid_email);
             imageView = (ImageView)grid.findViewById(R.id.grid_image);
        }
    }



    public custom_adapter_contributor(contributor list_account, ArrayList<contributor_model> listAccountInfos)
    {
        this.context=list_account;

        this.listDataFiltered=listAccountInfos;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_con, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {

        contributor_model dataposition=listDataFiltered.get(position);


        if (!dataposition.getimagepath().matches("")){
            try {
                Glide.with(context)
                        .load("https://nibpp.krishimegh.in/Content/reportingbase/contributors/" + dataposition.getimagepath())
                        .error(R.drawable.gray)
                        .into(holder.imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.expert, context.getTheme()));
        }
        if (dataposition.getphone().matches("")){
            holder.phn.setVisibility(View.GONE);
        }
        if (dataposition.getemail().matches("")){
            holder.email1.setVisibility(View.GONE);
        }
        holder.nam.setText(dataposition.gettitle()+" "+ dataposition.getfull_name());
        if (dataposition.getinstitute().matches("")){
            holder.des.setText(dataposition.getdesignation());
        }else{
            holder.des.setText(dataposition.getdesignation()+", "+dataposition.getinstitute());
        }

        holder.phn.setText("Phone: "+dataposition.getphone());
        holder.email1.setText("Email: "+dataposition.getemail());

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
