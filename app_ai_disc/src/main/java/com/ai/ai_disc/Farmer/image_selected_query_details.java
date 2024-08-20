package com.ai.ai_disc.Farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ai.ai_disc.R;
import com.bumptech.glide.Glide;


import java.util.ArrayList;


public class image_selected_query_details extends AppCompatActivity {

     ImageView image_1,image_2,image_3;
     CardView cardView1,cardView2,cardView3;
   // Context context;
     String query_id="";
     ArrayList<String> query_details_in_list;
     String imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selected_query_details1);
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/
        setTitle("AI-DISC");
        image_1=(ImageView)findViewById(R.id.imageView60);
        image_2=(ImageView)findViewById(R.id.imageView61);
        image_3=(ImageView)findViewById(R.id.imageView62);
        cardView1=(CardView)findViewById(R.id.card_view1);
        cardView2=(CardView)findViewById(R.id.card_view2);
        cardView3=(CardView)findViewById(R.id.card_view3);


        final Intent intent = getIntent();
        Bundle data = intent.getExtras();
         imagePath=data.getString("details");
        //query_details_in_list=(ArrayList<String>)data.get("details");

        image_1.setVisibility(View.GONE);
        image_2.setVisibility(View.GONE);
        image_3.setVisibility(View.GONE);

        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        cardView3.setVisibility(View.GONE);

/*        String image1_address=query_details_in_list.get(4);
        String image2_address=query_details_in_list.get(5);
        String image3_address=query_details_in_list.get(6);*/


        if(!imagePath.isEmpty())
        {
            image_1.setVisibility(View.VISIBLE);
            cardView1.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imagePath)
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(image_1);
        }

       /* if(!image2_address.isEmpty()){
            image_2.setVisibility(View.VISIBLE);
            cardView2.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(image2_address)
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(image_2);
        }
        if(!image3_address.isEmpty()){
            image_3.setVisibility(View.VISIBLE);
            cardView3.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(image3_address)
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(image_3);
        }*/
    }
   /* @Override
    public   boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflator= getMenuInflater();
        inflator.inflate(R.menu.tool_bar7,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_logout:

                Intent intent = new  Intent(image_selected_query_details.this,Login_Home_Mobile_Activity.class);
                startActivity(intent);
                shared_pref.sp =getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared_pref.sp.edit();
                editor.remove("username");
                editor.remove("password");
                editor.apply();


                break;
            case R.id.action_home:


                Intent intent2 = new Intent(image_selected_query_details.this,profile.class);
                intent2.putExtra("id",user_details.user_id_static);
                intent2.putExtra("district_code",user_details.district_code_static);
                intent2.putExtra("organization_id",user_details.organisation_id_static);
                startActivity(intent2);

                break;
            default:
                break;

        }

        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet,intentFilter);

    }


    @Override
    public void onStop(){
        super.onStop();
        unregisterReceiver(internet);

    }*/
}
