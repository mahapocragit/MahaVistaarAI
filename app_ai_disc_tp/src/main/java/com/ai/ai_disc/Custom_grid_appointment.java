package com.ai.ai_disc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ai.ai_disc.Videoconference.VCActivity;
import com.ai.ai_disc.model.getall_appoinment_model;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Custom_grid_appointment extends BaseAdapter {
    private Context mContext;
    private ArrayList<getall_appoinment_model> web;
    private int geocoderMaxResults=0;
    private getall_appoinment_model data;
    private Button call;
    private TextView pend;
    private View grid;
    private String scode,status,app_id;
    private String[] slots=new String[]{"10 - 10:30 AM","10:30 - 11 AM","11 - 11:30 AM","11:30 AM - 12 PM","12:30 - 1 PM","2:30 - 3 PM","3- 3:30 PM","3:30 - 4 PM","4 - 4:30 PM","4:30 - 5 PM"};

    public Custom_grid_appointment(Context c, ArrayList<getall_appoinment_model> web ) {
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        grid = new View(mContext);
        grid = inflater.inflate(R.layout.custom_layout_app, null);

        TextView datel = (TextView) grid.findViewById(R.id.dats);
        TextView time = (TextView) grid.findViewById(R.id.tm);
        ImageView imgd=grid.findViewById(R.id.img);
        TextView query = (TextView) grid.findViewById(R.id.query);
        pend=(TextView) grid.findViewById(R.id.status);
        data=web.get(position);
        TextView app = (TextView) grid.findViewById(R.id.appointment);
        if (!data.getimg().isEmpty())
        {
            Glide.with(mContext)
                    .load("https://nibpp.krishimegh.in/Content/NIDPPD/FILE_UPLOAD/" + data.getimg())
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(imgd);
            imgd.setEnabled(true);

        }else{
            imgd.setEnabled(false);
        }
        imgd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ob=new AlertDialog.Builder(mContext);
                LinearLayout nb=new LinearLayout(mContext);
                ImageView img=new ImageView(mContext);

                LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                nb.setLayoutParams(param);
                img.setLayoutParams(param);
                Glide.with(mContext)
                        .load("https://nibpp.krishimegh.in/Content/NIDPPD/FILE_UPLOAD/" + data.getimg())
                        .placeholder(R.drawable.facilities)
                        .error(R.drawable.gray)
                        .into(img);
                nb.addView(img);
                ob.setView(nb);
                ob.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ob.show();
            }
        });



        scode=data.getscode();
        datel.setText(data.getslotdate());
        int vb=Integer.parseInt(data.getslot());
        time.setText(slots[vb]);
        query.setText(data.getquery());
        app_id=data.getappointment();
        app.setText(app_id);
        status=data.getstatus();
        call=grid.findViewById(R.id.call);
        if (status.matches("0")){
            call.setText("Accept");
            pend.setText("Pending");
            pend.setTextColor(Color.RED);

        }else if (status.matches("1")){
            call.setText("Call");
            pend.setText("Accepted");
            pend.setTextColor(Color.GREEN);

        }else{
            call.setEnabled(false);
            pend.setText("Completed");
            pend.setTextColor(Color.BLACK);
        }
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.trim().matches("1")){
                    Intent nes=new Intent(mContext, VCActivity.class);


                    nes.putExtra("query",data.getquery());

                    nes.putExtra("scode",scode);

                    mContext.startActivity(nes);

                }else{
                    AlertDialog.Builder op=new AlertDialog.Builder(mContext);
                    op.setTitle("Accept ?").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            approveprocess();
                        }
                    }).show();
                }

            }
        });

        return grid;
        //return null;
    }

    private void approveprocess() {
        JSONObject object = new JSONObject();
        //int fg=list.size();
        try {

            object.put("app", data.getappointment());
            object.put("val", 1);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/app_appointment")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean res=false;
                        try {

                             res = response.getBoolean("status");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (res){
                            status="1";
                            pend.setText("Accepted");
                            call.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext, "Accepted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                    }
                });}



}
