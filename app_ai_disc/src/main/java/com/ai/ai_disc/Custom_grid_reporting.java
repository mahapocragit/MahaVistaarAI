package com.ai.ai_disc;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.ai.ai_disc.model.getall_report_model;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Custom_grid_reporting extends BaseAdapter {
    private Context mContext;
    ArrayList<getall_report_model> web;
    int geocoderMaxResults=0;


    public Custom_grid_reporting(Context c, ArrayList<getall_report_model> web ) {
        mContext = c;
        this.web = web;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return web.get(position);
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

        grid = new View(mContext);
        grid = inflater.inflate(R.layout.custom_layout_reports, null);
        //TextView dater = (TextView) grid.findViewById(R.id.dater);
        TextView datel = (TextView) grid.findViewById(R.id.datel);
        TextView placel = (TextView) grid.findViewById(R.id.placel);
        //TextView placer = (TextView) grid.findViewById(R.id.placer);
        //TextView cropr = (TextView) grid.findViewById(R.id.cropr);
        TextView cropl = (TextView) grid.findViewById(R.id.crop);
        //TextView infor = (TextView) grid.findViewById(R.id.infor);
        //TextView infol = (TextView) grid.findViewById(R.id.info);

        TextView dpl = (TextView) grid.findViewById(R.id.dpname);

        getall_report_model data=web.get(position);
        String headdd=data.gethead();
        String report_type=data.getreport_type();
        datel.setText(data.getdated());
        placel.setText(data.getlocate());
            switch (report_type){
                case "1":{
                    cropl.setVisibility(View.GONE);
                    dpl.setText(headdd);
                    dpl.setTypeface(dpl.getTypeface(), Typeface.BOLD);break;

                }
                case "2":{
                    cropl.setText(data.getcrop_name()+" Disease");
                    dpl.setText(data.dpname+" : "+headdd);break;

                }
                case "4":{
                    cropl.setText(data.getcrop_name()+" Nutrient Deficiency");
                    dpl.setText(data.dpname+" : "+headdd);break;

                }
                case "3":{
                    cropl.setText(data.getcrop_name()+" Insect");
                    dpl.setText(data.dpname+" : "+headdd);break;

                }
                case "5":{
                    cropl.setText(data.getcrop_name());
                    dpl.setText(headdd);break;

                }
            }
//            infor.setText(data.getinfo());
//            dater.setText(data.getdated());
//            placer.setText(place);



        return grid;
    }

}
