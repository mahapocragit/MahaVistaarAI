package com.ai.ai_disc.Farmer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.ai.ai_disc.R;

import java.util.ArrayList;

public class FarmerSendQueryCropListAdapter extends RecyclerView.Adapter<FarmerSendQueryCropListAdapter.ViewHolder> {
    private ArrayList<Model_DashboardContent> model_dashboardContents;
    private Context context;

    public FarmerSendQueryCropListAdapter(Context context,ArrayList<Model_DashboardContent> dashboardContents)
    {
        this.model_dashboardContents = dashboardContents;
        this.context = context;
    }

    @Override
    public FarmerSendQueryCropListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_farmer_sendquerycrop_list, viewGroup, false);
        return new FarmerSendQueryCropListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FarmerSendQueryCropListAdapter.ViewHolder viewHolder, int i) {

        viewHolder.gridText.setText(model_dashboardContents.get(i).getGetGridText());


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


        public ViewHolder(View view)
        {
            super(view);
            gridText = (TextView)view.findViewById(R.id.grid_text);

        }
    }

}
