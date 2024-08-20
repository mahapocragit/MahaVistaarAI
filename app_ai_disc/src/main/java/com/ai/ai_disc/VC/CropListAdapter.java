package com.ai.ai_disc.VC;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.ai.ai_disc.R;

import java.util.ArrayList;

public class CropListAdapter extends RecyclerView.Adapter<CropListAdapter.ViewHolder> {
   private ArrayList<String> cropIdList, cropNameList;
    private Context context;

    public CropListAdapter(ListOfCropsActivity listOfCropsActivity, ArrayList<String> cropListNameList, ArrayList<String> cropListCodeList)
    {
        this.cropIdList = cropListCodeList;
        this.cropNameList=cropListNameList;
        this.context = listOfCropsActivity;
    }

 /*   public CropListAdapter(Context context,ArrayList<String> cropNameList1,ArrayList<String> cropIdList)
    {
        this.cropIdList = cropIdList;
        this.cropNameList=cropNameList1;
        this.context = context;
    }*/

    @Override
    public CropListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_croplist_adapter, viewGroup, false);
        return new CropListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CropListAdapter.ViewHolder viewHolder, int i) {

        viewHolder.gridText.setText(cropNameList.get(i));

    }

    @Override
    public int getItemCount() {
        return cropNameList.size();
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
