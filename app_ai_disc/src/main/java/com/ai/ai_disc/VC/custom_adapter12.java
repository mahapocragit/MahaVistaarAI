package com.ai.ai_disc.VC;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.ai.ai_disc.R;

import java.util.ArrayList;


public class custom_adapter12 extends RecyclerView.Adapter<custom_adapter12.MyViewHolder> {

    Context context;
    ArrayList<expert> list_expert;
    String mail;
    String my_token;


    public custom_adapter12(Context context, ArrayList<expert> list_expert, String mail, String my_token) {
        this.context = context;
        this.list_expert = list_expert;
        this.mail = mail;
        this.my_token = my_token;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_adapter12_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        expert e = list_expert.get(position);

        holder.email.setText("Email : "+e.getEmail_id());

        holder.audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (e.getToken() != null) {
                    Toast.makeText(context, " Online", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, Outgoing.class);
                    intent.putExtra("email", e.getEmail_id());
                    intent.putExtra("token", e.getToken().trim());

                    intent.putExtra("my_email", mail);
                    intent.putExtra("my_token",my_token);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Not online", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return list_expert.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView email;

        Button audio;
        Button video;


        public MyViewHolder(View view) {
            super(view);


            email = (TextView) view.findViewById(R.id.email);
            audio = view.findViewById(R.id.audio);
            video = view.findViewById(R.id.video);

        }
    }

}


