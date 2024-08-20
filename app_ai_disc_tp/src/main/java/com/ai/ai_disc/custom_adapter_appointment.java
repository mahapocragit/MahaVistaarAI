package com.ai.ai_disc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Dataentry.ListAccountInfo;
import com.ai.ai_disc.Videoconference.VCActivity;
import com.ai.ai_disc.model.getall_appoinment_model;
import com.ai.ai_disc.model.history_model;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class custom_adapter_appointment extends RecyclerView.Adapter<custom_adapter_appointment.MyViewHolder>  {

    Context mContext;
    ArrayList<String> user_id_array;
    ArrayList<String> user_name_array;
    ArrayList<String> name_array;
    ArrayList<String> contact_number_array;
    ArrayList<String> email_id_array;
    ArrayList<String> timestamp_array;
    ArrayList<String> created_by_array;
    ArrayList<String> created_by_name_array;
    ArrayList<ListAccountInfo> listAccountInfos;
    ArrayList<getall_appoinment_model> listDataFiltered;

    private String scode,status,app_id;
    private String[] slots=new String[]{"10 - 10:30 ",
            "10:30 - 11 ",
            "11 - 11:30 ",
            "11:30  - 12 ","12 - 12:30 ",
            "12:30 - 13 ",
            "14:30 - 15",
            "15- 15:30 PM",
            "15:30 - 16 PM",
            "16 - 16:30 PM",
            "16:30 - 17 PM"};

    private String[] slotss=new String[]{"10:00",
            "10:30",
            "11:00",
            "11:30",
            "12:00",
            "12:30",
            "14:30",
            "15:00",
            "15:30",
            "16:00",
            "16:30","17:00","17:30"};
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView datel ;
        TextView time ;
        ImageView imgd;
        TextView query ;
        TextView pend;
        TextView app;
        CardView l,r;
        TextView dater ;
        TextView timer ;
        ImageView imgdr;
        TextView queryr ;

        TextView appr;
        Button call;
        public MyViewHolder(View grid) {
            super(grid);
             datel = (TextView) grid.findViewById(R.id.dats);
             time = (TextView) grid.findViewById(R.id.tm);
             imgd=grid.findViewById(R.id.img);
             query = (TextView) grid.findViewById(R.id.query);
            pend=(TextView) grid.findViewById(R.id.status);
            call=grid.findViewById(R.id.call);

             app = (TextView) grid.findViewById(R.id.appointment);

            dater = (TextView) grid.findViewById(R.id.datsr);
            timer = (TextView) grid.findViewById(R.id.tmr);
            imgdr=grid.findViewById(R.id.imgr);
            queryr = (TextView) grid.findViewById(R.id.queryr);


            appr = (TextView) grid.findViewById(R.id.appointmentr);
            l = (CardView) grid.findViewById(R.id.cardl);
            r = (CardView) grid.findViewById(R.id.cardr);
        }
    }



    public custom_adapter_appointment(expert_appointment list_account, ArrayList<getall_appoinment_model> listAccountInfos)
    {
        this.mContext=list_account;

        this.listDataFiltered=listAccountInfos;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_layout_app, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        getall_appoinment_model data=listDataFiltered.get(i);
        if (data.gethide()==1)
        {
            myViewHolder.r.setVisibility(View.GONE);
            myViewHolder.l.setVisibility(View.VISIBLE);
            if (!data.getimg().isEmpty())
        {
            Glide.with(mContext)
                    .load("https://nibpp.krishimegh.in/Content/NIDPPD/FILE_UPLOAD/" + data.getimg())
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(myViewHolder.imgd);
            myViewHolder.imgd.setEnabled(true);

        }else{
            myViewHolder.imgd.setEnabled(false);
        }
        myViewHolder.imgd.setOnClickListener(new View.OnClickListener() {
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
        myViewHolder.datel.setText(data.getslotdate());
        int vb=Integer.parseInt(data.getslot());
        myViewHolder.time.setText(slots[vb]);
        myViewHolder.query.setText(data.getquery());
        app_id=data.getappointment();
        myViewHolder.app.setText(app_id);
        status=data.getstatus();

        if (data.gethide()==0){
            myViewHolder.call.setText("Accept");
            myViewHolder.pend.setText("Pending");
            myViewHolder.pend.setTextColor(Color.RED);

        }else {
            myViewHolder.call.setText("Call");
            myViewHolder.pend.setText("Accepted");
            myViewHolder.pend.setTextColor(Color.GREEN);

        }
        if (status.matches("0")){
            myViewHolder.call.setText("Accept");
            myViewHolder.pend.setText("Pending");
            myViewHolder.pend.setTextColor(Color.RED);

        }else if (status.matches("1")){
            myViewHolder.call.setText("Call");
            myViewHolder.pend.setText("Accepted");
            myViewHolder.pend.setTextColor(Color.GREEN);

        }else{
            myViewHolder.call.setEnabled(false);
            myViewHolder.pend.setText("Completed");
            myViewHolder.pend.setTextColor(Color.BLACK);
        }
        myViewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.trim().matches("1")){
                    SimpleDateFormat tms = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yyyy");
                    String dated=data.getslotdate();
                    try {
                        Date strDate = sdfs.parse(dated);
                        Date nb = new Date();
                        String formattedDate = sdfs.format(nb);
                        String tmds = tms.format(nb);
                        Date std = tms.parse(slotss[Integer.parseInt(data.getslot())]);
                        Date std1 = tms.parse(slotss[Integer.parseInt(data.getslot()) + 1]);
                        Date bn = tms.parse(tmds);
                        //Log.d("lll",bn.toString()+"   "+std.toString());
                        Date xvnb = sdfs.parse(formattedDate);
                        if (xvnb.equals(strDate)){
                            if (bn.after(std1)) {
                                Toast.makeText(mContext,"Slot is expired",Toast.LENGTH_LONG).show();
                            } else {
                                if (bn.after(std)) {
                                    Intent nes=new Intent(mContext, VCActivity.class);


                                    nes.putExtra("query",data.getquery());
                                    nes.putExtra("date",data.getslotdate());
                                    nes.putExtra("slot",data.getslot());
                                    nes.putExtra("app_id",data.getappointment());
                                    nes.putExtra("scode",scode);

                                    mContext.startActivity(nes);
                                }else
                                {Toast.makeText(mContext,"Please wait for slot",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        else
                        {Toast.makeText(mContext,"Please wait for slot",Toast.LENGTH_LONG).show();
                        }
                }
                    catch(Exception c){
                }
                }else{
                    AlertDialog.Builder op=new AlertDialog.Builder(mContext);
                    op.setTitle("Accept ?").setMessage("To approve the appointment from the farmer tap YES and to reject tap CANCEL !").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            approveprocess(data.getappointment(),myViewHolder);
                        }
                    }).show();
                }

            }
        });
    }
        else{
            {
                myViewHolder.r.setVisibility(View.VISIBLE);
                myViewHolder.l.setVisibility(View.GONE);
                myViewHolder.r.setEnabled(false);
                if (!data.getimg().isEmpty())
                {
                    Glide.with(mContext)
                            .load("https://nibpp.krishimegh.in/Content/NIDPPD/FILE_UPLOAD/" + data.getimg())
                            .placeholder(R.drawable.facilities)
                            .error(R.drawable.gray)
                            .into(myViewHolder.imgdr);
                    myViewHolder.imgdr.setEnabled(true);

                }else{
                    myViewHolder.imgdr.setEnabled(false);
                }




                scode=data.getscode();
                myViewHolder.dater.setText(data.getslotdate());
                int vb=Integer.parseInt(data.getslot());
                myViewHolder.timer.setText(slots[vb]);
                myViewHolder.queryr.setText(data.getquery());
                app_id=data.getappointment();
                myViewHolder.appr.setText(app_id);




            }
        }

    }
    private void approveprocess(String appd,MyViewHolder vb) {
        JSONObject object = new JSONObject();
        //int fg=list.size();
        try {

            object.put("app", appd);
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
                            vb.pend.setText("Accepted");
                            vb.call.setVisibility(View.VISIBLE);
                            vb.pend.setTextColor(Color.GREEN);
                            vb.call.setText("Call");
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


    @Override
    public int getItemCount() {
        return listDataFiltered.size();
    }

    /*
    public void click(String user_id){
        Intent intent = new Intent(context,Validator_image_record1.class);
        intent.putExtra("user_id",user_id);
        // intent.putExtra("user_id",user_id_array.get(position));
        context.startActivity(intent);
    }

     */

//    public void click(String user_id){
//        Intent intent = new Intent(context,validator_image_record2.class);
//        intent.putExtra("user_id",user_id);
//        // intent.putExtra("user_id",user_id_array.get(position));
//        context.startActivity(intent);
//    }







}
