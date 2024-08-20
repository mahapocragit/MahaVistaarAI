package com.ai.ai_disc.Farmer;

import android.content.Context;
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

public class DashboardAdapter_old extends RecyclerView.Adapter<DashboardAdapter_old.ViewHolder> {
    private ArrayList<Model_DashboardContent1> model_dashboardContents;
    private Context context;

    public DashboardAdapter_old(Context context, ArrayList<Model_DashboardContent1> dashboardContents)
    {
        this.model_dashboardContents = dashboardContents;
        this.context = context;
    }

    @Override
    public DashboardAdapter_old.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_dashboard, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DashboardAdapter_old.ViewHolder viewHolder, int i) {

        viewHolder.gridText.setText(model_dashboardContents.get(i).getGetGridText());

        //viewHolder.gridImage.setColorFilter(Color.WHITE, PorterDuff.Mode.);
        try {
            if(!model_dashboardContents.get(i).getGridImage().matches("")){
                String imagePath = "https://nibpp.krishimegh.in/Content/reportingbase/crop_file/" + model_dashboardContents.get(i).getGridImage();
                //card_view1.setVisibility(View.VISIBLE);
                //Log.d("56565", imagePath);
                Glide.with(context)
                        .load(imagePath.trim())
                        .placeholder(R.drawable.facilities)
                        .error(R.drawable.gray)
                        .into(viewHolder.gridImage);
            }

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
