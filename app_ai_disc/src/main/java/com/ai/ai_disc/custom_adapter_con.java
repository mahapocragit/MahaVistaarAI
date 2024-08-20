package com.ai.ai_disc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai.ai_disc.model.contributor_model;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class custom_adapter_con extends BaseAdapter {
    private Context mContext;
    ArrayList<contributor_model> data;

    public custom_adapter_con(Context c, ArrayList<contributor_model> data) {
        mContext = c;
        this.data = data;


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contributor_model dataposition=data.get(position);

        grid = new View(mContext);
        grid = inflater.inflate(R.layout.grid_con, null);

        TextView nam = (TextView) grid.findViewById(R.id.grid_name);
        TextView des = (TextView) grid.findViewById(R.id.grid_designation);
        TextView phn = (TextView) grid.findViewById(R.id.grid_phn);
        TextView email1 = (TextView) grid.findViewById(R.id.grid_email);
        ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);



        if (!dataposition.getimagepath().matches("")){
            Glide.with(mContext)
                    .load("https://nibpp.krishimegh.in/Content/reportingbase/contributor/"+dataposition.getimagepath()+".jpg")
                    .error(R.drawable.gray)
                    .into(imageView);
        }
        if (dataposition.getphone().matches("")){
            phn.setVisibility(View.GONE);
        }
        if (dataposition.getemail().matches("")){
            email1.setVisibility(View.GONE);
        }
        nam.setText(dataposition.gettitle()+" "+ dataposition.getfull_name());
        des.setText(dataposition.getdesignation()+", "+dataposition.getinstitute());
        phn.setText("Phone: "+dataposition.getphone());
        email1.setText("Email: "+dataposition.getemail());




        return grid;
    }
}
