package com.ai.ai_disc.Farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ai.ai_disc.R;
import com.bumptech.glide.Glide;


import java.util.List;

public class head_image_selected_query extends AppCompatActivity {

    ImageView image1,image2,image3;
    List<String> query_detail;
  //  Context context;
    String query_id;
    String image_path;

     //internet_receiver internet = new internet_receiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_image_selected_query);
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        setTitle("AI-DISC");
      //  context=this;

         Intent intent = getIntent();
         Bundle data = intent.getExtras();
        image_path=data.getString("image");
       // query_detail= (ArrayList<String>) data.get("image");
       // System.out.println(" query id is:"+query_id);

        image1=(ImageView)findViewById(R.id.image1);
       /* image2=(ImageView)findViewById(R.id.image2);
        image3=(ImageView)findViewById(R.id.image3);*/

        image1.setVisibility(View.GONE);
     /*   image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);*/

       // query_detail= head_query_resolution_asyntask.hashmap_head_query_details.get(query_id);

       // String image_1=query_detail.get(0);



        if(!image_path.isEmpty()){
            image1.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(image_path)
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(image1);
        }

      /*  if(!image_2.isEmpty()){
            image2.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(image_2)
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(image2);
        }
        if(!image_3.isEmpty()){
            image3.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(image_3)
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(image3);
        }
*/
    }

  /*  @Override
    public   boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflator= getMenuInflater();
        inflator.inflate(R.menu.tool_bar4,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_logout:

                Intent intent = new  Intent(head_image_selected_query.this,Login_Home_Mobile_Activity.class);
                startActivity(intent);
                shared_pref.sp =getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared_pref.sp.edit();
                editor.remove("username");
                editor.remove("password");
                editor.commit();


                break;
            case R.id.action_home:

                Intent intent2 = new Intent(head_image_selected_query.this,kvk_head_new.class);
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

    }
*/

}
