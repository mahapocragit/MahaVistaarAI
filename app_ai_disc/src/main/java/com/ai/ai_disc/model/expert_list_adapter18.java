package com.ai.ai_disc.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Farmer.Account_expert;
import com.ai.ai_disc.R;
import com.ai.ai_disc.editprofile;
import com.ai.ai_disc.expert_appointment;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class expert_list_adapter18 extends RecyclerView.Adapter<expert_list_adapter18.MyViewHolder> {

    Context context;
    ArrayList<Expert> list_expert;
    ArrayList<ArrayList<ArrayList<String>>> av_slot_temps;
    private final String[] days=new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday"};
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

    public expert_list_adapter18(Context context, ArrayList<Expert> list_expert,ArrayList<ArrayList<ArrayList<String>>> av_slot_temp) {
        this.context = context;
        this.list_expert=list_expert;
        this.av_slot_temps=av_slot_temp;


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView user_name_value;
        ImageView im;
        TextView email_text;

        Button add;


        public MyViewHolder(View view) {
            super(view);


            im = (ImageView) view.findViewById(R.id.im);
            user_name_value = (TextView) view.findViewById(R.id.name);

            email_text = (TextView) view.findViewById(R.id.email);

            add= (Button) view.findViewById(R.id.add);


        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_adapter18_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int ps=position;
        Expert data = list_expert.get(ps);

        holder.user_name_value.setText(data.getFirst_name()+" "+data.getlast_name());

        holder.email_text.setText(data.getEmail_id());
        if (data.getUser_id().trim().matches("admin")){
            holder.add.setText("Add Expert");
            holder.user_name_value.setVisibility(View.GONE);

            holder.email_text.setVisibility(View.GONE);
            Glide.with(context)
                    .load(R.drawable.add)
                    .error(R.drawable.gray)
                    .into(holder.im);
        }else{
            if (!data.getimagepath().matches("")){
                try {
                    Glide.with(context)
                            .load("https://nibpp.krishimegh.in/Content/reportingbase/experts/" + data.getimagepath()+".jpg")
                            .error(R.drawable.gray)
                            .into(holder.im);
                } catch (Exception e) {
                    e.printStackTrace();Glide.with(context)
                            .load(R.drawable.expert)
                            .error(R.drawable.gray)
                            .into(holder.im);
                }
            }else{
                Glide.with(context)
                        .load(R.drawable.expert)
                        .error(R.drawable.gray)
                        .into(holder.im);
            }
        }
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getUser_id().trim().matches("admin")){
                    Intent gn=new Intent(context, Account_expert.class);
                    context.startActivity(gn);
                }
                else{
                    slotsetting(ps,data.getUser_id());
                }
            }
        });
    }

    void slotsetting(int position,String ex){
    ArrayList<ArrayList<String>> av_slot_temp=new ArrayList<>();
    av_slot_temp=av_slot_temps.get(position);
    AlertDialog.Builder ob = new AlertDialog.Builder(context);
    HorizontalScrollView mot=new HorizontalScrollView(context);
    LinearLayout.LayoutParams paramm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    paramm.setMargins(2,2,2,2);
    mot.setLayoutParams(paramm);
    LinearLayout nb = new LinearLayout(context);
    nb.setOrientation(LinearLayout.HORIZONTAL);
    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    param.setMargins(20,20,20,20);
    nb.setLayoutParams(param);
    mot.addView(nb);

    LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    param1.setMargins(5,5,5,5);
    LinearLayout nb_left = new LinearLayout(context);
    nb_left.setLayoutParams(param1);
    nb_left.setOrientation(LinearLayout.VERTICAL);
    nb_left.setGravity(Gravity.CENTER_HORIZONTAL);
    RadioGroup rd=new RadioGroup(context);
    nb_left.addView(rd);
    nb.addView(nb_left);

    LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    param.setMargins(10,10,10,10);
    for (String day : days) {
        RadioButton bt = new RadioButton(context);
        bt.setLayoutParams(param3);
        rd.addView(bt);
        bt.setText(day);
    }
    LinearLayout nb_right = new LinearLayout(context);
    nb_right.setOrientation(LinearLayout.VERTICAL);
    nb_right.setGravity(Gravity.CENTER_HORIZONTAL);
    LinearLayout.LayoutParams param31 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    param.setMargins(5,5,5,5);
    nb_right.setLayoutParams(param31);
    nb.addView(nb_right);

//                 ArrayList<CheckBox> box=new ArrayList<>();
//                 for (int bb=0;bb<11;bb+=1){
//                    CheckBox sl0 = new CheckBox(expert_appointment.this);
//                    sl0.setText(days_slot[bb]);
//                    nb1.addView(sl0);
//                    box.add(sl0);
//                }
    ArrayList<ArrayList<String>> finalAv_slot_temp = av_slot_temp;
    rd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton
                    r
                    = (RadioButton)group
                    .findViewById(checkedId);

            String selectedtext = r.getText().toString();
            int position= Arrays.asList(days).indexOf(selectedtext);
            nb_right.removeAllViews();

            for (int bbc=0;bbc<11;bbc+=1){
                CheckBox sl0 = new CheckBox(context);
                sl0.setText(days_slot[bbc]);
                nb_right.addView(sl0);
                sl0.setChecked(finalAv_slot_temp.get(position).contains(String.valueOf(bbc)));

                int finalBbc = bbc;
                sl0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            finalAv_slot_temp.get(position).add(String.valueOf(finalBbc));
                        }else{
                            finalAv_slot_temp.get(position).remove(String.valueOf(finalBbc));
                        }
                    }
                });
            }
        }
    });

    ob.setView(mot);
    ob.setTitle("Slot setting").setPositiveButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog.Builder mn=new AlertDialog.Builder(context);
            mn.setTitle("Are you sure ").setMessage("It will change your appointment slot").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //av_slot=av_slot_temp;
                    changeslot(finalAv_slot_temp,ex);
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    });
    ob.show();

}

void changeslot(ArrayList<ArrayList<String>> av_slot_temp,String ex){
    JSONObject object = new JSONObject();
    ArrayList<JSONArray> al=new ArrayList<>();
    for (int cd=0;cd<av_slot_temp.size();cd+=1){
        al.add(new JSONArray(av_slot_temp.get(cd)));
    }
    JSONArray nb=new JSONArray(al);

    try {
        object.put("data",nb);
        object.put("expert_id", ex);
    } catch (
            JSONException e) {
        e.printStackTrace();
    }
    AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/updateslots")
            .addJSONObjectBody(object)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    boolean bv=false;
                    try {
                        bv=response.getBoolean("book");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (bv){
                        Toast.makeText(context,"Updated",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onError(ANError anError) {

                }
            });
}


    @Override
    public int getItemCount() {
        return list_expert.size();
    }

}



