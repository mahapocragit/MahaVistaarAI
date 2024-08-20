package com.ai.ai_disc.Farmer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ai.ai_disc.R;
import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;


public class Selected_query_details1 extends AppCompatActivity {


    TextView commodity_type,commodity_name,problem_area,description,solution;
    //Context context;
    String query_id;

    List<QueryDetails> queryDetailsList;

    Button button_image,button_video,button_audio;

    TextView query_details_text_view;
    TextView commodity_type_text_view;
    TextView commodity_name_text_view;
    TextView problem_area_text_view;
    TextView description_text_view;
    TextView solution_text_view;
    String position,desc,solution1,imagePath;
    int pos=0;
    ImageView imageView;
    Button image_solution,video_solution,audio_solution;

    CardView cardView_solution,card_view_question,card_view1;
    TextView attachment_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_query_details1);
        queryDetailsList=new ArrayList<>();
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("");
        getWindow().setSoftInputMode(1);

*/


     /*   final Intent intent = getIntent();
        Bundle data = intent.getExtras();
        query_details_in_list=(ArrayList<String>) data.get("details");
        query_id=query_details_in_list.get(15);*/

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("bundle");
       // queryDetailsList = (ArrayList<QueryDetails>) args.getParcelable("queryList");
        desc=args.getString("desc");
        solution1=args.getString("solution");
        imagePath=args.getString("imagePath");
       // position=args.getString("position");
       // pos=Integer.parseInt(position);

        button_image=(Button)findViewById(R.id.go_to_image);
        button_video=(Button)findViewById(R.id.go_to_video);
        button_audio=(Button)findViewById(R.id.go_to_audio);

        image_solution=(Button)findViewById(R.id.image_solution);
        video_solution=(Button)findViewById(R.id.video_solution);
        audio_solution=(Button)findViewById(R.id.audio_solution);
        card_view1=(CardView)findViewById(R.id.card_view1) ;
        image_solution.setVisibility(View.GONE);
        video_solution.setVisibility(View.GONE);
        audio_solution.setVisibility(View.GONE);

        cardView_solution=(CardView)findViewById(R.id.card_view_solution);
        card_view_question=(CardView)findViewById(R.id.card_view_question);
        attachment_text_view=(TextView)findViewById(R.id.text_view_solution);

        imageView=(ImageView)findViewById(R.id.image1);
        cardView_solution.setVisibility(View.GONE);
        card_view_question.setVisibility(View.GONE);


       // query_details.add(16,scientist_image);
       // query_details.add(17,scientist_video);
       // query_details.add(18,scientist_audio);

      /*  if(!query_details_in_list.get(16).isEmpty()){

            image_solution.setVisibility(View.VISIBLE);
            cardView_solution.setVisibility(View.VISIBLE);
        }
*//*
        if(!query_details_in_list.get(17).isEmpty()){

            video_solution.setVisibility(View.VISIBLE);
            cardView_solution.setVisibility(View.VISIBLE);
        }

        if(!query_details_in_list.get(18).isEmpty()){

            audio_solution.setVisibility(View.VISIBLE);
            cardView_solution.setVisibility(View.VISIBLE);
        }

*/

        query_details_text_view=(TextView)findViewById(R.id.textView7);
        commodity_type_text_view=(TextView)findViewById(R.id.textView11);
        commodity_name_text_view=(TextView)findViewById(R.id.textView13);
        problem_area_text_view=(TextView)findViewById(R.id.textView14);
        description_text_view=(TextView)findViewById(R.id.textView16);
        solution_text_view=(TextView)findViewById(R.id.textView17);

        if(!imagePath.isEmpty())
        {
            imageView.setVisibility(View.VISIBLE);
            card_view1.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imagePath)
                    .placeholder(R.drawable.facilities)
                    .error(R.drawable.gray)
                    .into(imageView);
        }

      /*  button_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent( Selected_query_details1.this,image_selected_query_details.class);
                //  intent5.putExtra("query_id",query_id);
                intent5.putExtra("details",imagePath);
                startActivity(intent5);
            }
        });*/

    /*    button_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 = new Intent( Selected_query_details1.this,video_selected_query_details.class);
                //  intent6.putExtra("query_id",query_id);
                intent6.putExtra("details",query_details_in_list);
                startActivity(intent6);
            }
        });

        button_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent7 = new Intent( Selected_query_details1.this,audio_selected_new_query_details.class);
                intent7.putExtra("details",query_details_in_list);
                startActivity(intent7);
            }
        });
*/
     /*   image_solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayList<String> list_image = new ArrayList<String>();
                list_image.add(0,"");
                list_image.add(1,"");
                list_image.add(2,"");
                list_image.add(3,"");
                list_image.add(4,query_details_in_list.get(16));
                list_image.add(5,"");
                list_image.add(6,"");


                Intent intent5 = new Intent( Selected_query_details1.this,image_selected_query_details.class);
                intent5.putExtra("details",list_image);
                startActivity(intent5);

             //   Toast.makeText(Selected_query_details1.this," image solution",Toast.LENGTH_LONG).show();
            }
        });*/
/*

        video_solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayList<String> list_video = new ArrayList<String>();
                list_video.add(0,"");
                list_video.add(1,"");
                list_video.add(2,"");
                list_video.add(3,"");
                list_video.add(4,"");
                list_video.add(5,"");
                list_video.add(6,"");
                list_video.add(7,query_details_in_list.get(17));
                list_video.add(8,"");
                list_video.add(9,"");


                Intent intent6 = new Intent( Selected_query_details1.this,video_selected_query_details.class);
                //  intent6.putExtra("query_id",query_id);
                intent6.putExtra("details",list_video);
                startActivity(intent6);
               // Toast.makeText(Selected_query_details1.this," video solution",Toast.LENGTH_LONG).show();
            }
        });

        audio_solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayList<String> list_audio = new ArrayList<String>();
                list_audio.add(0,"");
                list_audio.add(1,"");
                list_audio.add(2,"");
                list_audio.add(3,"");
                list_audio.add(4,"");
                list_audio.add(5,"");
                list_audio.add(6,"");
                list_audio.add(7,"");
                list_audio.add(8,"");
                list_audio.add(9,"");
                list_audio.add(10,query_details_in_list.get(18));
                list_audio.add(11,"");
                list_audio.add(12,"");

                Intent intent7 = new Intent( Selected_query_details1.this,audio_selected_new_query_details.class);
                intent7.putExtra("details",list_audio);
                startActivity(intent7);

             //   Toast.makeText(Selected_query_details1.this,"audio solution",Toast.LENGTH_LONG).show();
            }
        });
*/




        commodity_type=(TextView)findViewById(R.id.editText50);
        commodity_name=(TextView)findViewById(R.id.editText51);
        problem_area=(TextView)findViewById(R.id.editText52);
        description=(TextView)findViewById(R.id.editText53);
        solution=(TextView)findViewById(R.id.editText54);





       // commodity_type.setText( query_details_in_list.get(2));
       // commodity_name.setText( query_details_in_list.get(3));
      //  problem_area.setText( query_details_in_list.get(1));
        description.setText( desc);

        if(solution1!=null)
        {
            if(!solution1.isEmpty())
            {
                solution.setText( solution1);

            }
            else
            {
                solution.setText( "Pending");
            }

        }
        else
        {
            solution.setText( "Pending");
        }



        String image1_address=imagePath;
      /*  String image2_address=query_details_in_list.get(5);
        String image3_address=query_details_in_list.get(6);
        String video1_address=query_details_in_list.get(7);
        String video2_address=query_details_in_list.get(8);
        String video3_address=query_details_in_list.get(9);
        String audio1_address=query_details_in_list.get(10);
        String audio2_address=query_details_in_list.get(11);
        String audio3_address=query_details_in_list.get(12);
        String pdf_address=query_details_in_list.get(13);*/

      /*  System.out.println(" image 1 is:"+image1_address);
        System.out.println(" image 2 is:"+image2_address);
        System.out.println(" image3 is:"+image3_address);
        System.out.println(" video1 is:"+video1_address);
        System.out.println(" video2 is:"+video2_address);
        System.out.println(" video3 is:"+video3_address);
        System.out.println(" audio1 is:"+audio1_address);
        System.out.println(" audio2 is:"+audio2_address);
        System.out.println(" audio3 is:"+audio3_address);
        System.out.println(" pdf is:"+pdf_address);
        */



       // commodity_type.setEnabled(false);
       // commodity_name.setEnabled(false);
      //  problem_area.setEnabled(false);
        description.setEnabled(false);
        solution.setEnabled(false);

      //  commodity_name.setTextColor(Color.BLACK);
      //  commodity_type.setTextColor(Color.BLACK);
      //  problem_area.setTextColor(Color.BLACK);
        description.setTextColor(Color.BLACK);
        solution.setTextColor(Color.BLACK);



        button_image.setVisibility(View.GONE);
        button_video.setVisibility(View.GONE);
        button_audio.setVisibility(View.GONE);
        //  button_pdf.setVisibility(View.GONE);

        if(!image1_address.isEmpty()){
            // System.out.println(" not null image 1");
            card_view_question.setVisibility(View.VISIBLE);
            button_image.setVisibility(View.VISIBLE);

        }
     /*   if(!image2_address.isEmpty()){
            //  System.out.println(" not null image 2");
            card_view_question.setVisibility(View.VISIBLE);
            button_image.setVisibility(View.VISIBLE);
        }
        if(!image3_address.isEmpty()){
            //  System.out.println(" not null image 3");
            card_view_question.setVisibility(View.VISIBLE);
            button_image.setVisibility(View.VISIBLE);
        }

        if(!video1_address.isEmpty()){
            //  System.out.println(" not null video 1");
            card_view_question.setVisibility(View.VISIBLE);
            button_video.setVisibility(View.VISIBLE);
        }
        if(!video2_address.isEmpty()){
            //  System.out.println(" not null video 2 ");
            card_view_question.setVisibility(View.VISIBLE);
            button_video.setVisibility(View.VISIBLE);
        }
        if(!video3_address.isEmpty()){
            // System.out.println(" not null video 3");
            card_view_question.setVisibility(View.VISIBLE);
            button_video.setVisibility(View.VISIBLE);
        }
        if(!audio1_address.isEmpty()){
            // System.out.println(" not null audio 1");
            card_view_question.setVisibility(View.VISIBLE);
            button_audio.setVisibility(View.VISIBLE);
        }
        if(!audio2_address.isEmpty()){
            // System.out.println(" not null audio 2");
            card_view_question.setVisibility(View.VISIBLE);
            button_audio.setVisibility(View.VISIBLE);
        }
        if(!audio3_address.isEmpty()){
            // System.out.println(" not null audio  3");
            card_view_question.setVisibility(View.VISIBLE);
            button_audio.setVisibility(View.VISIBLE);
        }*/

    }
  /*  @Override
    public   boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflator= getMenuInflater();
        inflator.inflate(R.menu.selectedquerydetail,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_logout:


                shared_pref.sp =getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared_pref.sp.edit();
                editor.remove("username");
                editor.remove("password");
                editor.commit();


                Intent intent = new  Intent(Selected_query_details1.this,Login_Home_Mobile_Activity.class);
                startActivity(intent);


                break;
            case R.id.action_home:


                Intent intent2 = new Intent(Selected_query_details1.this,profile.class);
                intent2.putExtra("id",user_details.user_id_static);
                intent2.putExtra("district_code",user_details.district_code_static);
                intent2.putExtra("organization_id",user_details.organisation_id_static);
                startActivity(intent2);

                break;
            default:
                break;

        }

        return true;
    }*/

   /* @Override
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
