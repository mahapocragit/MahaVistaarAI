package com.ai.ai_disc.Farmer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.Dialog;
import com.ai.ai_disc.Login;
import com.ai.ai_disc.R;
import com.ai.ai_disc.booikingdone;
import com.ai.ai_disc.reportglobeview;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.wx.wheelview.widget.WheelView;
import com.wx.wheelview.widget.WheelViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by akmu on 3/20/2018.
 */

public class custom_adapter_expertlist extends BaseAdapter {


        Context context;
        public  static LayoutInflater inflater=null;
        String number="1";
        int slot_number;
         String username;
    MaterialButton tn;
        private String query;
        String dates;
    ProgressDialog progress;
        List<expertDetails1> expertDetailsList;
        ArrayList<Integer> avail;
    private final String[] slots=new String[]{"10:00",
            "10:30",
            "11:00",
            "11:30",
            "12:00",
            "12:30",
            "14:30",
            "15:00",
            "15:30",
            "16:00",
            "16:30"};
    private final String[] days_slot=new String[]{"10:00-10:30 AM",
            "10:30 - 11:00 AM",
            "11:00 - 11:30 AM",
            "11:30 AM - 12:00 PM",
            "12:00 - 12:30 PM",
            "12:30 - 1:00 PM",
            "2:30 - 3:00 PM",
            "3:00 - 3:30 PM",
            "3:30 - 4:00 PM",
            "4:00 - 4:30 PM",
            "4:30 - 5:00 PM"};
        //String[] slots=new String[]{"10 - 10:30","10:30 - 11","11 - 11:30","11:30 - 12","12:30 - 1","2:30 - 3","3- 3:30","3:30 - 4","4 - 4:30","4:30 - 5"};
        private ArrayList<String> slotav;
    private ArrayList<Integer> slotint;
        public custom_adapter_expertlist(Context context, List<expertDetails1> expertDetailsList){

            this.context=context;
            this.expertDetailsList=expertDetailsList;

            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

    @Override
        public int getCount() {
            return expertDetailsList.size();
        }

        @Override
        public Object getItem(int position) {
            return expertDetailsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view=inflater.inflate(R.layout.content_expert_list_show,null);
            ImageView imageView=(ImageView)view.findViewById(R.id.image_path);
            TextView expert=(TextView) view.findViewById(R.id.farmer_name);
            TextView mail=(TextView) view.findViewById(R.id.email);
            TextView institute=view.findViewById(R.id.institute);
            TextView attended=view.findViewById(R.id.numb_attend);
            TextView speciality=(TextView) view.findViewById(R.id.speciality);
            TextView desig=(TextView) view.findViewById(R.id.desig);

            tn=view.findViewById(R.id.btn);
            username=expertDetailsList.get(position).getuser();
            query=expertDetailsList.get(position).getquery();
            slotint=new ArrayList<>();
            slotav=new ArrayList<>();
            tn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datepicker(expertDetailsList.get(position).getid(),expertDetailsList.get(position).getuser());
                }
            });
            avail=new ArrayList<>();
            if(!expertDetailsList.get(position).name.isEmpty())
            {
                try{
                imageView.setVisibility(View.VISIBLE);
                //card_view1.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load("https://nibpp.krishimegh.in/Content/reportingbase/experts/"+expertDetailsList.get(position).getuser()+".jpg")
                        .placeholder(R.drawable.facilities)
                        .error(R.drawable.gray)
                        .into(imageView);}
                catch (Exception e) {
                    e.printStackTrace();
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.expert, context.getTheme()));
                }

            }
            else
            {
                imageView.setVisibility(View.GONE);
            }

            expert.setText(expertDetailsList.get(position).gettitle()+" "+expertDetailsList.get(position).getname());
            mail.setText(expertDetailsList.get(position).getmail());
            if(expertDetailsList.get(position).getdesig().isEmpty()){
                desig.setVisibility(View.GONE);
            }else{
                desig.setText(expertDetailsList.get(position).getdesig());
            }
            if(expertDetailsList.get(position).getspeciality().isEmpty()){
                speciality.setVisibility(View.GONE);
            }else{
                speciality.setText(expertDetailsList.get(position).getspeciality());
            }

            institute.setText(expertDetailsList.get(position).getinstitute());
            institute.setSelected(true);
            if (!expertDetailsList.get(position).getrated().matches("0")) {

                attended.setText("Calls : " + expertDetailsList.get(position).getnumber() + "; Rating : " + expertDetailsList.get(position).getrated());
            }else{
                if (expertDetailsList.get(position).getnumber().matches("0")) {
                attended.setText("New Expert" );}
                else{
                    attended.setText("Calls : " + expertDetailsList.get(position).getnumber() );
                }
            }
            return view;
        }
    private void datepicker(String id,String usr){
            int vk=0;
        SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat tms = new SimpleDateFormat("HH:mm");

        try{
            Date nb=new Date();
            String tmds=tms.format(nb);
            Date bn=tms.parse(tmds);
            Date std=tms.parse(slots[10]);
            if (bn.after(std)){
                vk=1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String dates=simpleDateFormat.format(calendar.getTime());
                wheeldialog(id,dates,usr);


            }
        },Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("Select a date");
        final Calendar c1 = Calendar.getInstance();

        c1.set(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        if(vk==1){datePickerDialog.getDatePicker().setMinDate(c1.getTimeInMillis()+24*60*60*1000);}else{datePickerDialog.getDatePicker().setMinDate(c1.getTimeInMillis());}

        datePickerDialog.getDatePicker().setMinDate(c1.getTimeInMillis());
        datePickerDialog.show();

        //Toast.makeText(reportglobeview.this, "hi",Toast.LENGTH_SHORT).show();

    }
    private void getslots(ArrayList<Integer> sl,String dt,String id,String usr){
            slotav.clear();
            slotint.clear();
            try {

            for (int j=0;j<sl.size();j+=1){
                    slotav.add(days_slot[sl.get(j)]);
                slotint.add(sl.get(j));
            }} catch (Exception e) {
                e.printStackTrace();
            }

        WheelViewDialog dialog = new WheelViewDialog(context);
        dialog.setTitle("Available Slots")
                .setItems(slotav)
                .setDialogStyle(Color.parseColor("#6699ff"))
                .setCount(3)
                .setLoop(true)
                .setButtonText("Ok")
                .setOnDialogItemClickListener(new WheelViewDialog.OnDialogItemClickListener() {
                    @Override
                    public void onItemClick(int i, String s) {
                        slot_number=slotint.get(i);
                        bookapp1(slot_number,dt,id,usr);

                    }
                })
                .show();
    }

    private void bookapp1(int slot_number, String dt, String id,String usr) {
        String cv=getAlphaNumericString();
        progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setMessage("Please wait..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        JSONObject object1 = new JSONObject();
        try {

            object1.put("expert_id", id);
            object1.put("date", dt);
            object1.put("slot", slot_number);
            object1.put("query", query);
            object1.put("scode", cv);
            object1.put("user_id", user_singleton.getInstance().getUser_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("donnnn",object1.toString()+usr);
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/bookslot")
                .addJSONObjectBody(object1)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        progress.cancel();
                        boolean book = false;
                        try {
                            book = jsonObject.getBoolean("book");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (book) {
                            Intent ned=new Intent(context,booikingdone.class);
                            ned.putExtra("username",usr);
                            ned.putExtra("query",query);
                            ned.putExtra("date",dt);
                            ned.putExtra("slot",slots[slot_number]);
                            context.startActivity(ned);
                        }else{
                            Toast.makeText(context, "You have another appointment at this time, Please try another slot", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                    progress.cancel();
                    }
                });
    }

    private void wheeldialog(String id, String dt,String usr){
        SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat tms = new SimpleDateFormat("HH:mm");

        JSONObject object1 = new JSONObject();
        try {

            object1.put("expert_id", id);
            object1.put("date", dt);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("dattttt",object1.toString());
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getslot")
                .addJSONObjectBody(object1)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject jsonObject) {
                                         ArrayList<Integer> sl=new ArrayList<>();
                                         JSONArray array1 = new JSONArray();
                                         try {
                                             array1 = jsonObject.getJSONArray("slots");
                                             for (int i = 0; i < array1.length(); i++) {

                                                 sl.add(array1.getInt(i));

                                             }
                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }
                                         ArrayList<Integer> sl_av=new ArrayList<>();
                                         int h=10;
                                         try{
                                             Date strDate = sdfs.parse(dt);
                                             Date nb=new Date();
                                             String formattedDate = sdfs.format(nb);
                                             String tmds=tms.format(nb);

                                             Date bn=tms.parse(tmds);
                                             nb=sdfs.parse(formattedDate);

                                             if ( nb.equals(strDate)) {

                                                 while (bn.before(tms.parse(slots[h])) && h>=0){
                                                     avail.add(h);
                                                     h=h-1;
                                                 }
                                             }

                                         } catch (Exception e) {
                                             e.printStackTrace();
                                         }


                                         //Log.d(TAG, "onResponse: " + array1);
                                        if (sl.size()==0){
                                            Toast.makeText(context, "No slot is available on "+dt, Toast.LENGTH_SHORT).show();
                                        }else{
                                            if(h==10){
                                                getslots(sl, dt, id,usr);
                                            }else{
                                                for (int j=0;j<sl.size();j+=1){
                                                    if (avail.contains(sl.get(j))){
                                                        sl_av.add(sl.get(j));
                                                    }
                                                }
                                                if (sl_av.size()==0) {
                                                    Toast.makeText(context, "No slot is available today", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    getslots(sl_av, dt, id,usr);}
                                            }


                                        }
                                     }
                                     @Override
                                     public void onError(ANError anError) {

                                     }
                                 });


    }

     String getAlphaNumericString()
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
    }


