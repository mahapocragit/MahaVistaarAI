package com.ai.ai_disc;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class arrayadapter_model_reports extends ArrayAdapter<gridview_model_datafetcher> {
    Context context;
    public arrayadapter_model_reports(@NonNull Context context, ArrayList<gridview_model_datafetcher> cropModelArrayList) {
        super(context, 0, cropModelArrayList);
        this.context=context;

    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        int geocoderMaxResults = 1;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_child_model_reports, parent, false);
        }

        gridview_model_datafetcher cropname_model = getItem(position);
        String cropname=cropname_model.getcropnames();
        String dpd=cropname_model.getdpname();
        TextView reportid = listitemView.findViewById(R.id.reportid);
        TextView cropnamereport = listitemView.findViewById(R.id.cropnamereport);
        TextView datereport = listitemView.findViewById(R.id.datereport);
        TextView area = listitemView.findViewById(R.id.area);
        TextView dptype = listitemView.findViewById(R.id.dptype);
        TextView dpname = listitemView.findViewById(R.id.dpname);
        TextView numbofimages = listitemView.findViewById(R.id.numbofimages);
        ImageView img = listitemView.findViewById(R.id.croppic);
        MaterialButton newreport=listitemView.findViewById(R.id.uploadreportimage);

        img.setImageResource(cropname_model.getimgid());
        cropnamereport.setText(cropname);
        //Date dt= Date.valueOf(cropname_model.getdates());
        datereport.setText(cropname_model.getdates());
        dpname.setText(dpd);
        numbofimages.setText(String.valueOf(cropname_model.getnumbs()));
        reportid.setText(String.valueOf(cropname_model.getreportid()));
        area.setText(cropname_model.getlats()+", "+cropname_model.getlongs());
        if (cropname_model.getwhichtype()==1){
            dptype.setText("Disease");
        }else{
            dptype.setText("Pest");
        }
        newreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dp = new AlertDialog.Builder(context);
                dp.setTitle("Make new report on '"+dpd+"' of '"+cropname+"' ?");
                dp.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent ijh=new Intent(context, reportfarmerdisease_new.class);
                        ijh.putExtra("crop",cropname_model.getcropid());
                        ijh.putExtra("dp",cropname_model.getdpid());
                        ijh.putExtra("cropname",cropname_model.getcropnames());
                        ijh.putExtra("dpname",cropname_model.getdpname());
                        ijh.putExtra("type",cropname_model.getwhichtype());
                        context.startActivity(ijh);


                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                dp.show();
            }
        });



        return listitemView;
    }






}

