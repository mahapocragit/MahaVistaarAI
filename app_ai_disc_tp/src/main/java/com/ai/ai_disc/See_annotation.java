package com.ai.ai_disc;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;


public class See_annotation extends AppCompatActivity {


    private static final String TAG = "See_annotation";
    ArrayList<String> point_x1_list;
    ArrayList<String> point_y1_list;
    ArrayList<String> point_x2_list;
    ArrayList<String> point_y2_list;
    ArrayList<String> point_x3_list;
    ArrayList<String> point_y3_list;
    ArrayList<String> point_x4_list;
    ArrayList<String> point_y4_list;
    ArrayList<String> message_list;
    String url="";
    String image_id="";

    @BindView(R.id.list)
    RecyclerView listView;
    Bitmap bitmap;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    int number=0;
    InternetReceiver internet = new InternetReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_annotation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC");

        Intent intent=getIntent();
        Bundle data=  intent.getExtras();
        url= data.getString("url");
        image_id= data.getString("image_id");



        ButterKnife.bind(this);

         point_x1_list = new ArrayList<String>();
         point_y1_list= new ArrayList<String>();
         point_x2_list= new ArrayList<String>();
         point_y2_list= new ArrayList<String>();
         point_x3_list= new ArrayList<String>();
         point_y3_list= new ArrayList<String>();
         point_x4_list= new ArrayList<String>();
         point_y4_list= new ArrayList<String>();
         message_list= new ArrayList<String>();


        get_bitmap( url);
     //   get_list_annotation(image_id);

    }

    public void get_bitmap(String url){


        GlideApp.with(See_annotation.this)
                .load(url)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        //int width = resource.getWidth();
                       // int height = resource.getHeight();

                       // bitmap=resource;
                        //Log.d(TAG, "onResourceReady: ");
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                        bitmap = bitmapDrawable.getBitmap();

                        get_list_annotation(image_id);


                        // System.out.println(" width:"+width);
                        //  System.out.println(" ");
                       // Log.i(TAG, "onResourceReady:  width "+width);
                       // Log.i(TAG, "onResourceReady, height:  "+height);
                    }
                });
        /*
        Glide.with(See_annotation.this)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        System.out.println(" bitmap is ready");

                        int width = resource.getWidth();
                        int height = resource.getHeight();

                        bitmap=resource;

                        get_list_annotation(image_id);


                       // System.out.println(" width:"+width);
                      //  System.out.println(" ");
                        Log.i(TAG, "onResourceReady:  width "+width);
                        Log.i(TAG, "onResourceReady, height:  "+height);
                    }
                });

         */
    }


    public void get_list_annotation(String image_id){

        /*

        {
            "image_id": "sample string 1"
        }

         */


        JSONObject object = new JSONObject();
        try {
            object.put("image_id",image_id);
        }catch (JSONException e){

        }
       // Log.d(TAG, "get_list_annotation: "+object);


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_annotation")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                     public void onResponse(JSONObject response) {




                        progressbar.setVisibility(View.GONE);
                        System.out.println(" response:"+response);
                      //  Log.d(TAG, "onResponse: "+response);

                        point_x1_list.clear();
                        point_y1_list.clear();
                        point_x2_list.clear();
                        point_y2_list.clear();
                        point_x3_list.clear();
                        point_y3_list.clear();
                        point_x4_list.clear();
                        point_y4_list.clear();


                        /*
                        {
                            "annotation_list": [
                            {
                                "image_id": "sample string 1",
                                    "point_x1": "sample string 2",
                                    "point_y1": "sample string 3",
                                    "point_x2": "sample string 4",
                                    "point_y2": "sample string 5",
                                    "point_x3": "sample string 6",
                                    "point_y3": "sample string 7",
                                    "point_x4": "sample string 8",
                                    "point_y4": "sample string 9"
                            },
                            {
                                "image_id": "sample string 1",
                                    "point_x1": "sample string 2",
                                    "point_y1": "sample string 3",
                                    "point_x2": "sample string 4",
                                    "point_y2": "sample string 5",
                                    "point_x3": "sample string 6",
                                    "point_y3": "sample string 7",
                                    "point_x4": "sample string 8",
                                    "point_y4": "sample string 9"
                            }
  ]
                        }

                         */


                        try {
                            JSONArray array =response.getJSONArray("annotation_list");

                            number=array.length();

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                String image_id = object.optString("image_id");
                                String point_x1 = object.optString("point_x1");
                                String point_y1 = object.optString("point_y1");
                                String point_x2 = object.optString("point_x2");
                                String point_y2 = object.optString("point_y2");
                                String point_x3 = object.optString("point_x3");
                                String point_y3 = object.optString("point_y3");
                                String point_x4 = object.optString("point_x4");
                                String point_y4 = object.optString("point_y4");
                                String message = object.optString("message");


                                point_x1_list.add(point_x1);
                                point_y1_list.add(point_y1);

                                point_x2_list.add(point_x2);
                                point_y2_list.add(point_y2);

                                point_x3_list.add(point_x3);
                                point_y3_list.add(point_y3);

                                point_x4_list.add(point_x4);
                                point_y4_list.add(point_y4);
                                message_list.add(message);


                            }

                            if(array.length()==0){
                                head.setText("NO ANNOTATION FOUND");
                              // Toast.makeText(See_annotation.this, "No annotation found.", Toast.LENGTH_SHORT).show();
                            }else{
                                head.setText("LIST ANNOTATION  ");
                            }

                            annotation_adapter aa = new annotation_adapter(See_annotation.this,
                                    point_x1_list,
                            point_y1_list,
                            point_x2_list,
                            point_y2_list,
                            point_x3_list,
                            point_y3_list,
                            point_x4_list,
                            point_y4_list,bitmap,message_list );


                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(See_annotation.this);
                            listView.setLayoutManager(mLayoutManager);
                            listView.setItemAnimator(new DefaultItemAnimator());
                            listView.setAdapter(aa);





                        }catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                    @Override
                    public void onError(ANError error) {
                        progressbar.setVisibility(View.GONE);
                        Toast.makeText(See_annotation.this,"Error in getting annotation list .",Toast.LENGTH_LONG).show();
                    }
                });


    }

    @Override
    public   boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflator= getMenuInflater();
        inflator.inflate(R.menu.all_in_one,menu);

       // menu_list=menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.all_in_one:

if(number>0){
    Intent intent = new Intent(See_annotation.this,All_in_one_annotation.class);
    intent.putExtra("url",url);
    intent.putExtra("image_id",image_id);
    startActivity(intent);
}else{
    Toast.makeText(See_annotation.this,"No annotation found.",Toast.LENGTH_LONG).show();
}



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


}
