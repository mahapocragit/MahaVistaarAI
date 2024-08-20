package com.ai.ai_disc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;

public class custom_adapter_image_data extends BaseAdapter {


    Context context;
    public LayoutInflater inflater = null;
    ArrayList<String> list_image;

    ArrayList<String> list_crop;
    ArrayList<String> list_disease;
    ArrayList<String> list_part;
    ArrayList<String> list_date;
    ArrayList<String> list_latitude;
    ArrayList<String> list_longitude;
    ArrayList<String> list_id;
    ArrayList<String> status_array;
    String user_id;

    public custom_adapter_image_data(Context context, ArrayList<String> list_image, ArrayList<String> list_crop, ArrayList<String> list_disease,
                                     ArrayList<String> list_part,
                                     ArrayList<String> list_date,
                                     ArrayList<String> list_latitude,
                                     ArrayList<String> list_longitude,
                                     ArrayList<String> list_id,
                                     ArrayList<String> status_array,
                                     String user_id) {

        this.context = context;
        this.list_image = list_image;
        this.list_crop = list_crop;
        this.list_disease = list_disease;
        this.list_part = list_part;
        this.list_date = list_date;
        this.list_latitude = list_latitude;
        this.list_longitude = list_longitude;
        this.list_id = list_id;
        this.status_array = status_array;
        this.user_id = user_id;


        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list_image.size();
    }

    @Override
    public Object getItem(int position) {
        return list_image.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.custom_layout_validator, null);

        ImageView content = (ImageView) view.findViewById(R.id.image);

        TextView crop_name = (TextView) view.findViewById(R.id.crop_name);
        TextView crop_name_value = (TextView) view.findViewById(R.id.crop_name_value);
        TextView disease_name = (TextView) view.findViewById(R.id.disease_name);
        TextView disease_name_value = (TextView) view.findViewById(R.id.disease_name_value);
        TextView part_name = (TextView) view.findViewById(R.id.part_name);
        TextView part_name_value = (TextView) view.findViewById(R.id.part_name_value);
        TextView longitude = (TextView) view.findViewById(R.id.longitude);
        TextView longitude_value = (TextView) view.findViewById(R.id.longitude_value);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView date_value = (TextView) view.findViewById(R.id.date_value);
        TextView latitude = (TextView) view.findViewById(R.id.latitude);
        TextView latitude_value = (TextView) view.findViewById(R.id.latitude_value);
        TextView status = (TextView) view.findViewById(R.id.status);
        TextView status_value = (TextView) view.findViewById(R.id.status_value);
        Button change_status = (Button) view.findViewById(R.id.change_status);

        Glide.with(context)
                .load(list_image.get(position))
                .into(content);

        crop_name_value.setText(list_crop.get(position));
        disease_name_value.setText(list_disease.get(position));
        part_name_value.setText(list_part.get(position));
        date_value.setText(list_date.get(position));
        latitude_value.setText(list_latitude.get(position));
        longitude_value.setText(list_longitude.get(position));
        status_value.setText(status_array.get(position));

        crop_name.setText("Crop Name");
        disease_name.setText("Disease Name");
        part_name.setText("Affected Part Name");
        date.setText("Date");
        latitude.setText("Latitude");
        longitude.setText("Longitude");

        status.setText("Status");

        change_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  update_status(list_id.get(position), user_id);
            }
        });

        return view;
    }
}

