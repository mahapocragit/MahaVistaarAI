package com.ai.ai_disc.Farmer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ai.ai_disc.MultiChat_ExpertQuery_Activity;
import com.ai.ai_disc.R;
import com.ai.ai_disc.chat_expert_singleton;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Administrator on 27-02-2017.
 */

public class custom_adapter_query_answer extends BaseAdapter

{


    Context context;
    public  static LayoutInflater inflater=null;

    /*ArrayList<String> content_value;
    ArrayList<String> list_scientist_id;*/
    String numb;
    List<Model_expert_query_content> expertQueryList;

  /*  public custom_adapter_query_answer(Context context, ArrayList<String> content_value, ArrayList<String> list_scientist_id){

        this.context=context;
        this.content_value=content_value;
        this.list_scientist_id=list_scientist_id;

        inflater = ( LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
*/
    public custom_adapter_query_answer(Context context, List<Model_expert_query_content> expertQueryList)
    {
        this.context=context;
        this.expertQueryList=expertQueryList;

        inflater = ( LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return expertQueryList.size();
    }

    @Override
    public Object getItem(int position) {
        return expertQueryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(R.layout.layout_query_new,null);
        Model_expert_query_content cnt=expertQueryList.get(position);
        //LinearLayout layout=(LinearLayout)view.findViewById(R.id.layout1);
        //TextView crop=(TextView)view.findViewById(R.id.crop);
        TextView farmer=(TextView)view.findViewById(R.id.user);
        TextView query=(TextView)view.findViewById(R.id.qu);
        TextView date=(TextView)view.findViewById(R.id.date);
        ImageView img=(ImageView) view.findViewById(R.id.img);
        TextView head=(TextView)view.findViewById(R.id.head);
        head.setText(cnt.getFarmer_query());

        query.setText(cnt.getQuery_id());
        //numb= String.valueOf(position+1);
        //  System.out.println(" numb:"+numb);
        farmer.setText(cnt.getFarmer_name());
        //crop.setText(cnt.getcrop_name());

        date.setText(cnt.gettimes());
        Glide.with(context)
                .load(cnt.getImg_path())
                .placeholder(R.drawable.facilities)
                .error(R.drawable.errro_2)
                .into(img);
        img.setEnabled(true);
        Button bn=view.findViewById(R.id.chat);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MultiChat_ExpertQuery_Activity.class);
//                Bundle bundle=new Bundle();
//                bundle.putString("queryId",expertQueryList.get(position).getQuery_id());
//                bundle.putString("expertId",user_singleton.getInstance().getUser_id());
//                bundle.putString("cropId", expertQueryList.get(position).getCropID());
//                bundle.putString("farmerId",expertQueryList.get(position).getFarmerId());
//
//                bundle.putString("farmerName",expertQueryList.get(position).getFarmer_name());
//                bundle.putString("crop",expertQueryList.get(position).getcrop_name());
//                bundle.putString("desc",expertQueryList.get(position).getFarmer_query());
//                bundle.putString("imagePath",expertQueryList.get(position).getImg_path());
//                bundle.putString("userType","7");
//
//                bundle.putString("status_run",expertQueryList.get(position).getstatus_run());
//                intent.putExtra("bundle",bundle);
                //intent.putExtras(bundle);
                chat_expert_singleton.getInstance().setchat(cnt);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
