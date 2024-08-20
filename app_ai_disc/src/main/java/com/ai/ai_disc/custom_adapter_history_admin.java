package com.ai.ai_disc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Dataentry.ListAccountInfo;
import com.ai.ai_disc.model.history_model;
import com.ai.ai_disc.model.history_model_admin;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class custom_adapter_history_admin extends RecyclerView.Adapter<custom_adapter_history_admin.MyViewHolder>  {

    Context context;
    ArrayList<String> user_id_array;
    ArrayList<String> user_name_array;
    ArrayList<String> name_array;
    ArrayList<String> contact_number_array;
    ArrayList<String> email_id_array;
    ArrayList<String> timestamp_array;
    ArrayList<String> created_by_array;
    ArrayList<String> created_by_name_array;
    ArrayList<ListAccountInfo> listAccountInfos;
    ArrayList<history_model_admin> listDataFiltered;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView crop ;
        private TextView diseased ;
        TextView dp ,date;
        ImageView rate,pic ;
                TextView gotod,predict;


        public MyViewHolder(View grid) {
            super(grid);
             crop = (TextView) grid.findViewById(R.id.crop);
             diseased = (TextView) grid.findViewById(R.id.dp);
             dp = (TextView) grid.findViewById(R.id.dptype);
            date = (TextView) grid.findViewById(R.id.dat);
             rate = (ImageView) grid.findViewById(R.id.imageView7);
            gotod =  grid.findViewById(R.id.gotopage);
            pic=grid.findViewById(R.id.pic);
            predict=grid.findViewById(R.id.predict);
        }
    }



    public custom_adapter_history_admin(admin_identify list_account, ArrayList<history_model_admin> listAccountInfos)
    {
        this.context=list_account;

        this.listDataFiltered=listAccountInfos;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_history_admin, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        history_model_admin dataposition=listDataFiltered.get(i);
        myViewHolder.date.setText(dataposition.getdate());
        myViewHolder.crop.setText(dataposition.getcrop());
        if (dataposition.getdp().matches("0")){
            myViewHolder.dp.setText(". Healthy Crop");
            //Log.d("guuuu",String.valueOf(i));
            myViewHolder.diseased.setText(" User_name: "+dataposition.getuser_name());
        }if (dataposition.getdp().matches("1")){
            myViewHolder.dp.setText(" User_name: "+dataposition.getuser_name());
            myViewHolder.diseased.setText(dataposition.getdisease());
        }
        if (dataposition.getdp().matches("2")){
            myViewHolder.dp.setText(". Insect Identification");
            myViewHolder.diseased.setText(dataposition.getdisease());
        }
        JSONObject object = new JSONObject();
        try {
            object.put("image_id", dataposition.getidd());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d("gyyy",object.toString());
        myViewHolder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //imn.setScaleType(ImageView.ScaleType.FIT_XY);
                //imn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_access_time_24));
                Toast.makeText(context,"Loading image",Toast.LENGTH_SHORT).show();
                AndroidNetworking.post("https://aidisc.krishimegh.in:32517/get_images")
                        .addJSONObjectBody(object)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    String nb = jsonObject.getString("encoded_string");
                                    //Log.d("guuuu",nb);
                                    byte[] decodedString = Base64.decode(nb, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    AlertDialog.Builder ob=new AlertDialog.Builder(context);
                                    LinearLayout lb=new LinearLayout(context);
                                    ob.setView(lb);
                                    ViewGroup.LayoutParams param=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    lb.setLayoutParams(param);
                                    ImageView imn=new ImageView(context);
                                    lb.addView(imn);
                                    imn.setLayoutParams(param);
                                    imn.setImageBitmap(decodedByte);
                                    ob.setTitle("Input Image").setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                //Log.d("gggg","errorr "+anError.toString());
                                Toast.makeText(context,"Error ID-ADMIN-0001",Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });



        switch (dataposition.getlike()){
            case "1":{
                myViewHolder.rate.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_sentiment_very_dissatisfied_24, context.getTheme()));break;
            }
            case "2":{
                myViewHolder.rate.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_sentiment_dissatisfied_24, context.getTheme()));break;
            }
            case "3":{
                myViewHolder.rate.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_sentiment_satisfied_24, context.getTheme()));break;
            }
            case "4":{
                myViewHolder.rate.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_sentiment_satisfied_alt_24, context.getTheme()));break;
            }
            case "5":{
                myViewHolder.rate.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_sentiment_very_satisfied_24, context.getTheme()));break;
            }

        }
        myViewHolder.gotod.setText("ID: "+dataposition.getidd().toString());
        myViewHolder.predict.setText("Pred_confidence: "+dataposition.getpred());

    }



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
