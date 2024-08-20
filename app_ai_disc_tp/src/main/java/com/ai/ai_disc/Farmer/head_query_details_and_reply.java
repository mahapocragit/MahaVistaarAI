package com.ai.ai_disc.Farmer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ai.ai_disc.Login;
import com.ai.ai_disc.R;
import com.ai.ai_disc.shared_pref;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;

/*import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;*/

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class head_query_details_and_reply extends AppCompatActivity {

    TextView name,address,query_description;
    EditText solution;
    Button reply,image,video,audio,forward,cancelTranslation_Button;
    String query_id;
    List<String> query_detail ;

     //internet_receiver internet = new internet_receiver();
    String solution_entered="";
    String first_name;
    String address1;
    String scientist_id;
    String answer;
    String description;
    String userId="";
    String image1;
    String image2;
    String image3;
    String video1;
    String video2;
    String video3;
    String audio1;
    String audio2;
    String audio3;
    String query_id1;
    String forward_scientist_id;
    String image_solution_url,video_solution_url,audio_solution_url,queryId;

    RelativeLayout layout_multi_media;
    TextView textView_attachment;
    LinearLayout layout_image,layout_video,layout_audio;
     ArrayAdapter translation_adapter;
    CardView  cardView_image;
    ImageView image_card_view;
    ImageView video_card_view;

    ImageButton cancel_image_button,cancel_video_button,cancel_audio_button;
    String image_address="";;
    String video_address="";
    String audio_address="";
    int transClicked=0;
    LinearLayout translateLayout;
    ArrayList<String> translation_language_List;
    Button image_solution;
      Spinner translationSpinner;
    CardView cardView_question;
    CardView cardView_solution;


    String farmer_name,farmer_address,farmer_query,img_path,query_resolution,query_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_query_details_and_reply);
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
  /*      translateLayout=(LinearLayout)findViewById(R.id.translateLayout);
        translation_language_List=new ArrayList<String>();*/
        //translated_Language_ediText=(EditText)findViewById(R.id.translated_Language);
        query_detail = new ArrayList<String>();
        setTitle("AI-DISC");
        getWindow().setSoftInputMode(1);

       // cancelTranslation_Button=(Button)findViewById(R.id.cancelButton);


        Bundle data = getIntent().getExtras();
        farmer_name=data.getString("farmer_name");
        farmer_address=data.getString("farmer_address");
        farmer_query=data.getString("farmer_query");
        img_path=data.getString("img_path");
        query_resolution=data.getString("query_resolution");
        query_status=data.getString("query_status");
        queryId=data.getString("query_id");
        userId= user_singleton.getInstance().getUser_id();


       // query_detail =(ArrayList<String>)data.get("details");


      /*  translationSpinner=(Spinner)findViewById(R.id.translation_spinner);
        translation_language_List.add("Translate to");
        translation_language_List.add("Bengali");
        translation_adapter=new ArrayAdapter(head_query_details_and_reply.this,android.R.layout.simple_list_item_1,translation_language_List);
        translationSpinner.setAdapter(translation_adapter);*/
         /*    first_name= query_detail.get(0);
             address1=query_detail.get(1);
             scientist_id=query_detail.get(2);
             answer= query_detail.get(3);
             description =query_detail.get(4);
             query_id1=query_detail.get(5);
             image1= query_detail.get(6);
             image2=query_detail.get(7);
             image3=query_detail.get(8);
             video1=query_detail.get(9);
             video2=query_detail.get(10);
             video3=query_detail.get(11);
             audio1= query_detail.get(12);
             audio2= query_detail.get(13);
             audio3= query_detail.get(14);
             forward_scientist_id=query_detail.get(15);
             image_solution_url=query_detail.get(16);
             video_solution_url=query_detail.get(17);
             audio_solution_url=query_detail.get(18);*/



        name=(TextView)findViewById(R.id.name);
        address=(TextView)findViewById(R.id.address);
        query_description=(TextView)findViewById(R.id.query_description);
        solution=(EditText)findViewById(R.id.query_solution);
        reply=(Button)findViewById(R.id.reply);
        image=(Button)findViewById(R.id.image);
       /* video=(Button)findViewById(R.id.video);
        audio=(Button)findViewById(R.id.audio);
        forward=(Button)findViewById(R.id.forward);*/
        layout_multi_media=(RelativeLayout)findViewById(R.id.layout_media);
        textView_attachment=(TextView)findViewById(R.id.text_attachment);
    /*    cardView_audio=(CardView)findViewById(R.id.card_view_audio);
        cardView_video=(CardView)findViewById(R.id.card_view_video);*/
        cardView_image=(CardView)findViewById(R.id.card_view_image);

        layout_image=(LinearLayout)findViewById(R.id.new_layout1);
     /*   layout_video=(LinearLayout)findViewById(R.id.new_layout2);
        layout_audio=(LinearLayout)findViewById(R.id.new_layout3);
*/
        image_card_view=(ImageView)findViewById(R.id.image_added);
      //  video_card_view=(ImageView)findViewById(R.id.video_image_added);

        cancel_image_button=(ImageButton)findViewById(R.id.cancel_image);
       // cancel_video_button=(ImageButton)findViewById(R.id.cancel_video);
       // cancel_audio_button=(ImageButton)findViewById(R.id.cancel_audio);


        image_solution=(Button)findViewById(R.id.image_solution);
      /*  video_solution=(Button)findViewById(R.id.video_solution);
        audio_solution=(Button)findViewById(R.id.audio_solution);*/

        cardView_question=(CardView)findViewById(R.id.cardviewquestion);
        cardView_solution=(CardView)findViewById(R.id.cardviewanswer);

        cardView_question.setVisibility(View.GONE);
        cardView_solution.setVisibility(View.GONE);

      //  cardView_audio.setVisibility(View.GONE);
       // cardView_video.setVisibility(View.GONE);
        cardView_image.setVisibility(View.GONE);


        image.setVisibility(View.GONE);
    //    video.setVisibility(View.GONE);
      //  audio.setVisibility(View.GONE);
      //  forward.setVisibility(View.GONE);


        image_solution.setVisibility(View.GONE);
     //   video_solution.setVisibility(View.GONE);
     //   audio_solution.setVisibility(View.GONE);

      /*  if(!img_path.isEmpty()){
            image_solution.setVisibility(View.VISIBLE);
            cardView_solution.setVisibility(View.VISIBLE);
        }*/

      /*  if(!video_solution_url.isEmpty()){
            video_solution.setVisibility(View.VISIBLE);
            cardView_solution.setVisibility(View.VISIBLE);
        }

        if(!audio_solution_url.isEmpty()){
            audio_solution.setVisibility(View.VISIBLE);
            cardView_solution.setVisibility(View.VISIBLE);
        }
*/
       /* translationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String translation_language=translation_language_List.get(position);
                if(translation_language.contains("Bengali"))
                {
                    transClicked=1;
                    translateLayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cancelTranslation_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                translateLayout.setVisibility(View.GONE);
                translationSpinner.setAdapter(translation_adapter);
                translated_Language_ediText.setText("");
                solution.setText("");

            }
        });*/


        solution.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                String getText=s.toString();
                /*if(s.toString().equals(""))
                {
                    //translated_Language_ediText.setText("");
                }
                else
                {
                    identifyLanguage(getText);
                }
*/

                // Toast.makeText(getApplicationContext(),  getText,Toast.LENGTH_LONG).show();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(""))
                {
                   // translated_Language_ediText.setText("");
                }
            }
        });

      /*  image_solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//Toast.makeText(head_query_details_and_reply.this,"image",Toast.LENGTH_LONG).show();
                ArrayList<String> list_image = new ArrayList<String>();
                list_image.add(0,image_solution_url);
                list_image.add(1,"");
                list_image.add(2,"");


                Intent intent = new Intent(head_query_details_and_reply.this,head_image_selected_query.class);
                intent.putExtra("image",list_image);
                startActivity(intent);

            }
        });

        video_solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Toast.makeText(head_query_details_and_reply.this,"video",Toast.LENGTH_LONG).show();

                ArrayList<String> list_video = new ArrayList<String>();
                list_video.add(0,video_solution_url);
                list_video.add(1,"");
                list_video.add(2,"");

                Intent intent = new Intent(head_query_details_and_reply.this,head_video_selected_query.class);
                intent.putExtra("video",list_video);
                startActivity(intent);


            }
        });

       audio_solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(head_query_details_and_reply.this,"audio",Toast.LENGTH_LONG).show();

                ArrayList<String> list_audio = new ArrayList<String>();
                list_audio.add(0,audio_solution_url);
                list_audio.add(1,"");
                list_audio.add(2,"");

                Intent intent = new Intent(head_query_details_and_reply.this,head_audio_selected_query_list.class);
                intent.putExtra("audio",list_audio);
                startActivity(intent);


            }
        });*/

        cancel_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardView_image.setVisibility(View.GONE);
                image_address="";
            }
        });

        /*cancel_video_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardView_video.setVisibility(View.GONE);
                video_address="";
            }
        });

        cancel_audio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardView_audio.setVisibility(View.GONE);
                audio_address="";
            }
        });*/

        layout_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Toast.makeText(head_query_details_and_reply.this,"select picture",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"select picture"),1);
            }
        });

/*
        layout_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Toast.makeText(head_query_details_and_reply.this,"select video", Toast.LENGTH_LONG).show();
                    Intent intent2 = new Intent();
                    intent2.setType("video/mp4");
                    intent2.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent2,"select video"), 2);

            }
        });

        layout_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Toast.makeText(head_query_details_and_reply.this,"select audio",Toast.LENGTH_LONG).show();
                    Intent intent5 = new Intent();
                    intent5.setType("audio/*");
                    intent5.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent5,"select audio"),3);

            }

        });
*/


        if(!img_path.isEmpty())
        {
            image.setVisibility(View.VISIBLE);
            cardView_question.setVisibility(View.VISIBLE);
        }
/*

        if(!video1.isEmpty() || !video2.isEmpty() || !video3.isEmpty()){
            video.setVisibility(View.VISIBLE);
            cardView_question.setVisibility(View.VISIBLE);
        }
        if(!audio1.isEmpty() || !audio2.isEmpty() || !audio3.isEmpty()){
            audio.setVisibility(View.VISIBLE);
            cardView_question.setVisibility(View.VISIBLE);
        }
*/

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* ArrayList<String> list_image = new ArrayList<String>();
                list_image.add(0,image1);*/
              /*  list_image.add(1,image2);
                list_image.add(2,image3);
*/

                Intent intent = new Intent(head_query_details_and_reply.this,head_image_selected_query.class);
                intent.putExtra("image",img_path);
                startActivity(intent);
            }
        });

      /*  video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_video = new ArrayList<String>();
                list_video.add(0,video1);
                list_video.add(1,video2);
                list_video.add(2,video3);

                Intent intent = new Intent(head_query_details_and_reply.this,head_video_selected_query.class);
                intent.putExtra("video",list_video);
                startActivity(intent);

            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> list_audio = new ArrayList<String>();
                list_audio.add(0,audio1);
                list_audio.add(1,audio2);
                list_audio.add(2,audio3);

                Intent intent = new Intent(head_query_details_and_reply.this,head_audio_selected_query_list.class);
                intent.putExtra("audio",list_audio);
                startActivity(intent);

            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(head_query_details_and_reply.this,Forward_to.class);
                intent.putExtra("query_id",query_id1);
                startActivity(intent);
            }
        });*/

        name.setText(farmer_name);
        address.setText(farmer_address);
        query_description.setText(farmer_query);
       // solution.setText(answer);

        name.setEnabled(false);
        address.setEnabled(false);
        query_description.setEnabled(false);


        if( query_status.equals("0"))
        {

            Toast.makeText(head_query_details_and_reply.this,"  not answered",Toast.LENGTH_LONG).show();
         // forward.setVisibility(View.VISIBLE);
        }

        if(query_status.equals("1"))
        {

            Toast.makeText(head_query_details_and_reply.this,"answered",Toast.LENGTH_LONG).show();
            reply.setText("REPLIED");
            reply.setEnabled(false);
            solution.setEnabled(false);
            solution.setText(query_resolution);
           /* translationSpinner.setEnabled(false);
            translationSpinner.setClickable(false);*/
            layout_multi_media.setVisibility(View.GONE);
            textView_attachment.setVisibility(View.GONE);
        }

       /* if(scientist_id.isEmpty() && (!forward_scientist_id.isEmpty())){


            reply.setText("forwarded ");
            reply.setEnabled(false);
            solution.setEnabled(false);
            layout_multi_media.setVisibility(View.GONE);
            textView_attachment.setVisibility(View.GONE);
        }*/
      /*  if((!scientist_id.isEmpty()) && (!forward_scientist_id.isEmpty()))
        {

             Toast.makeText(head_query_details_and_reply.this,"answered",Toast.LENGTH_LONG).show();
            reply.setText("REPLIED");
            reply.setEnabled(false);
            solution.setEnabled(false);
            solution.setText(answer);

            layout_multi_media.setVisibility(View.GONE);
            textView_attachment.setVisibility(View.GONE);
        }
*/
       reply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                 solution_entered=solution.getText().toString();

                if(solution_entered.isEmpty())
                {

                    Toast.makeText(head_query_details_and_reply.this," please enter some solution.",Toast.LENGTH_LONG).show();
                }
                else{

                    try
                    {

                       JSONObject obj = new JSONObject();


                        obj.put("query_id",queryId);
                        obj.put("query_resolution",solution_entered);
                        obj.put("expert_id",userId);

                        System.out.println("idProblem " + obj.toString());

                        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Query_ResoultionBy_Expert")
                                .addJSONObjectBody(obj)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {


                                        try {


                                            boolean result = response.getBoolean("result");
                                            String message = response.getString("message");


                                            if (result)
                                            {
                                                Toast.makeText(head_query_details_and_reply.this,message,Toast.LENGTH_LONG).show();
                                               /* Intent returnIntent = new Intent();
                                                setResult(RESULT_OK, returnIntent);
                                                finish();*/
                                                Intent intent = new Intent(head_query_details_and_reply.this,head_query_list.class);
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                               // startActivityForResult(intent, 1);
                                               startActivity(intent);
                                                //setResult(RESULT_OK,intent);
                                                finish();
                                            } else {
                                                Toast.makeText(head_query_details_and_reply.this,message,Toast.LENGTH_LONG).show();
                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                    @Override
                                    public void onError(ANError error) {


                                        Toast.makeText(head_query_details_and_reply.this, "Error", Toast.LENGTH_LONG).show();
                                    }
                                });




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });


    }
   /* @Override
    public   boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflator= getMenuInflater();
        inflator.inflate(R.menu.head_querydetail_reply,menu);
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

                Intent intent = new  Intent(head_query_details_and_reply.this,Login_Home_Mobile_Activity.class);
                startActivity(intent);

                break;
            case R.id.action_home:

                Intent intent2 = new Intent(head_query_details_and_reply.this,kvk_head_new.class);
                startActivity(intent2);

                break;
            default:
                break;

        }

        return true;
    }*/

  /*  @Override
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
    @Override
    public void onActivityResult(int requestcode,int responsecode,Intent data)
    {

        super.onActivityResult(requestcode, responsecode, data);
        switch (requestcode) {
            case 1:

                if (requestcode == 1 && responsecode == RESULT_OK && data != null) {

                    final Uri address = data.getData();
                    // System.out.println(" address of image:"+address);
                    long file_length = check_length(address);
                    // System.out.println(" file length image  is:"+file_length);
                    if (file_length < 2050) {
                        //   System.out.println(" less than 2 MB:");

                        image_address = address.toString();

                        cardView_image.setVisibility(View.VISIBLE);
                        Glide.with(head_query_details_and_reply.this).load(address).into(image_card_view);

                    } else {
                        // System.out.println(" more than 2 MB :");

                        Toast.makeText(head_query_details_and_reply.this, "more than size", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(head_query_details_and_reply.this, "some error in selecting picture ", Toast.LENGTH_LONG).show();
                }

                break;
         /*   case 2:

                if (requestcode == 2 && responsecode == RESULT_OK && data != null) {

                    final Uri address = data.getData();
                    long file_length = check_length(address);
                    if (file_length < 2050) {
                        //   System.out.println(" less than 2 MB:");

                        video_address = address.toString();
                        cardView_video.setVisibility(View.VISIBLE);
                        Glide.with(head_query_details_and_reply.this).load(address).into(video_card_view);

                    } else {
                        // System.out.println(" more than 2 MB :");

                        Toast.makeText(head_query_details_and_reply.this, "more than size", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(head_query_details_and_reply.this, " some error in selecting video", Toast.LENGTH_LONG).show();
                }

                break;
            case 3:

                if (requestcode == 3 && responsecode == RESULT_OK && data != null) {

                    final Uri address = data.getData();
                    // System.out.println(" address of image:"+address);
                    long file_length = check_length(address);
                    // System.out.println(" file length image  is:"+file_length);
                    if (file_length < 2050) {
                        //   System.out.println(" less than 2 MB:");

                        audio_address = address.toString();
                        cardView_audio.setVisibility(View.VISIBLE);

                    } else {
                        // System.out.println(" more than 2 MB :");

                        Toast.makeText(head_query_details_and_reply.this, "more than size", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(head_query_details_and_reply.this, "some error in selecting  audio", Toast.LENGTH_LONG).show();
                }

                break;
*/
            default:
                break;


        }
    }

    public long check_length(Uri address_of_file){


        long length_file = 0;


        try {
          InputStream inputstream2 = getContentResolver().openInputStream(address_of_file);
            //  Toast.makeText(send_query2_layout.this,"checking size  ",Toast.LENGTH_LONG).show();


            length_file = inputstream2.available();
            //   System.out.println(" size is 1:"+length_file);
            length_file=length_file/1024;
            // System.out.println(" size is  2:"+length_file);
            inputstream2.close();
            // return

        } catch (FileNotFoundException e) {
            //System.out.println(" file not found exception:");
            e.printStackTrace();
        }catch (IOException e){
            // System.out.println(" io exception :");
            e.printStackTrace();
        }
        return length_file;

    }


    public String converting_to_string(String address){

       // for(int i=0; i<audio_list.length;i++){

        String image_in_string="";
            System.out.println(" address is:"+address);
            if(!address.isEmpty()){

                System.out.println("inside for loop: not  empty"+address);
                Uri address_to_convert=Uri.parse(address);
                System.out.println("Uri is:"+address_to_convert);

                try {
                 InputStream   inputstream = getContentResolver().openInputStream(address_to_convert);


                    System.out.println(" inputstream is :");
                    int length_audio = 0;

                    length_audio = inputstream.available();
                    System.out.println("  length:" + length_audio);
                    byte[] data_in_byte_audio = new byte[length_audio];
                    System.out.println("  length of data in byte " + data_in_byte_audio.length);
                    System.out.println(" data in byte " + data_in_byte_audio.length);
                    inputstream.read(data_in_byte_audio);
                    inputstream.close();
                    System.out.println(" after inputstream close");
                     image_in_string = Base64.encodeToString(data_in_byte_audio, Base64.DEFAULT);
                    System.out.println("image in string :" +image_in_string);
                   // System.out.println(" image string in before :" +image_in_string);

                  //  System.out.println("audio string in after :" +image_in_string);
                }catch(IOException e){
                    e.printStackTrace();
                    Toast.makeText(head_query_details_and_reply.this,"error in converting string",Toast.LENGTH_LONG).show();
                }
            }else{
                System.out.println(" empty at:"+address);
            }


         //   return  image_in_string.replaceAll("\n","");
      //  }
        String proper_image= image_in_string.replaceAll("\n","");
        return proper_image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                Intent intent = new Intent(head_query_details_and_reply.this, Login.class);
                startActivity(intent);

                shared_pref.remove_shared_preference(head_query_details_and_reply.this);

                break;
            default:
                break;

        }

        return true;
    }


 /*   public void identifyLanguage(final String text)
    {

        FirebaseLanguageIdentification languageIdentifier =
                FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode != "und")
                                {
                                    if (languageCode != null) {
                                        if(languageCode.equalsIgnoreCase("en"))
                                        {
                                            translateText(text);
                                        }

                                    }
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be loaded or other internal error.
                                // ...
                            }
                        });


    }

    public void translateText(final String text)
    {

        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                        .setTargetLanguage(FirebaseTranslateLanguage.BN)
                        .build();
        final FirebaseTranslator englishGermanTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        englishGermanTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void v)
                            {
                                englishGermanTranslator.translate(text)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {
                                                    @Override

                                                    public void onSuccess(@NonNull String translatedText)
                                                    {

                                                        translated_Language_ediText.setText(translatedText);
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e)
                                                    {
                                                        Toast.makeText(getApplicationContext(),"Wifi Needed", Toast.LENGTH_LONG).show();
                                                        // Error.
                                                        // ...
                                                    }
                                                });
                                // Model downloaded successfully. Okay to start translating.
                                // (Set a flag, unhide the translation UI, etc.)
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Log.d("NoTABLEtODOWNLOAD",e.getMessage());
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                            }
                        });



    }
*/

    @Override
    public void onBackPressed()
    {

        head_query_details_and_reply.this.finish();
        //  Toast.makeText(FarmerProfileActivity.this, "No Back.Please logout", Toast.LENGTH_LONG).show();

    }



}
