package com.ai.ai_disc.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai.ai_disc.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Custom_grid_enable_disable_crop extends BaseAdapter {
    private Context mContext;
    ArrayList<String> web;
    ArrayList<Integer> Imageid;

    public Custom_grid_enable_disable_crop(Context c, ArrayList<String> web, ArrayList<Integer> Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
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
        grid = inflater.inflate(R.layout.grid_profile, null);
        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
        textView.setText(web.get(position));


        Glide.with(mContext)
                .load(Imageid.get(position))
                .error(R.drawable.gray)
                .into(imageView);


        return grid;
    }
}
