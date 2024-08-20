package com.ai.ai_disc.Farmer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.ai.ai_disc.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private ArrayList<Model_DashboardContent> model_dashboardContents;
    private Context context;

    public DashboardAdapter(Context context,ArrayList<Model_DashboardContent> dashboardContents)
    {
        this.model_dashboardContents = dashboardContents;
        this.context = context;
    }

    @Override
    public DashboardAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_dashboard, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DashboardAdapter.ViewHolder viewHolder, int i) {

        viewHolder.gridText.setText(model_dashboardContents.get(i).getGetGridText());

        //viewHolder.gridImage.setColorFilter(Color.WHITE, PorterDuff.Mode.);
        try {
            String imagePath = "https://nibpp.krishimegh.in/Content/reportingbase/crop_file/" + (model_dashboardContents.get(i).getGetGridText()).replace(" ","").toLowerCase() + "_iasri.png";
            //card_view1.setVisibility(View.VISIBLE);
            Log.d("56565", imagePath);
            Glide.with(context)
                    .load(imagePath.trim())
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(viewHolder.gridImage);
        } catch (Exception e) {
            e.printStackTrace();

        }

        //viewHolder.gridImage.setImageResource(model_dashboardContents.get(i).getGridImage());

       /* Glide.with(context)
                .load(model_dashboardContents.get(i).getGridImage())
                .error(R.drawable.gray)
                .into(viewHolder.gridImage);*/


    }

    @Override
    public int getItemCount() {
        return model_dashboardContents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView gridText;
        private final ImageView gridImage;

        public ViewHolder(View view)
        {
            super(view);

            gridText = (TextView)view.findViewById(R.id.grid_text);
            gridImage = (ImageView) view.findViewById(R.id.grid_image);
        }
    }

}
