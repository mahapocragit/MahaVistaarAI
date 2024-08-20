package com.ai.ai_disc;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class All_in_one_annotation extends AppCompatActivity {


    ArrayList<String> point_x1_list;
    ArrayList<String> point_y1_list;
    ArrayList<String> point_x2_list;
    ArrayList<String> point_y2_list;
    ArrayList<String> point_x3_list;
    ArrayList<String> point_y3_list;
    ArrayList<String> point_x4_list;
    ArrayList<String> point_y4_list;
    String url="";
    String image_id="";

    ImageView imageView;
    Bitmap bitmap;
    TextView head;
    InternetReceiver internet ;
    Context context;
    private static final String TAG = "All_in_one_annotation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_in_one_annotation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC");

        Intent intent=getIntent();
        Bundle data=  intent.getExtras();
        url= data.getString("url");
        image_id= data.getString("image_id");

        internet = new InternetReceiver();

        //System.out.println("url "+url);
       // System.out.println("image id is : "+image_id);

       // listView=(ListView)findViewById(R.id.list);
        imageView=(ImageView)findViewById(R.id.image);
        head=(TextView)findViewById(R.id.head);

        point_x1_list = new ArrayList<String>();
        point_y1_list= new ArrayList<String>();
        point_x2_list= new ArrayList<String>();
        point_y2_list= new ArrayList<String>();
        point_x3_list= new ArrayList<String>();
        point_y3_list= new ArrayList<String>();
        point_x4_list= new ArrayList<String>();
        point_y4_list= new ArrayList<String>();


        context=this;
        get_bitmap( url);
        //   get_list_annotation(image_id);



    }

    public void get_bitmap(String url){

        /*
        GlideApp.with(All_in_one_annotation.this)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        System.out.println(" bitmap is ready");

                        int width = resource.getWidth();
                        int height = resource.getHeight();

                        bitmap=resource;
                        imageView.setImageBitmap(bitmap);

                        get_list_annotation(image_id);


                        System.out.println(" width:"+width);
                        System.out.println(" height:"+height);
                    }
                });

         */


        GlideApp.with(All_in_one_annotation.this)
                .load(url)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                       // int width = resource;
                      //  int height = resource.getHeight();

                        //bitmap=resource;
                       // imageView.setImageBitmap(bitmap);


                        BitmapDrawable bitmapDrawable =(BitmapDrawable) resource;
                        bitmap =bitmapDrawable.getBitmap();
                        imageView.setImageBitmap(bitmap);

                        get_list_annotation(image_id);

                      //  get_list_annotation(image_id);


                       // System.out.println(" width:"+width);
                       // System.out.println(" height:"+height);
                    }
                });



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


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_annotation")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        System.out.println(" response:"+response);

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


                                point_x1_list.add(point_x1);
                                point_y1_list.add(point_y1);

                                point_x2_list.add(point_x2);
                                point_y2_list.add(point_y2);

                                point_x3_list.add(point_x3);
                                point_y3_list.add(point_y3);

                                point_x4_list.add(point_x4);
                                point_y4_list.add(point_y4);


                            }

                            add_all();



                        }catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(All_in_one_annotation.this,"Error in getting annotation list .",Toast.LENGTH_LONG).show();
                    }
                });


    }

    public void add_all(){

      //  Bitmap bitmap1=bitmap;

for(int position=0;position<point_x1_list.size();position++) {


    ArrayList<point> list_point = new ArrayList<point>();

    point point1 = new point();
    point1.setX(Integer.valueOf(point_x1_list.get(position)));
    point1.setY(Integer.valueOf(point_y1_list.get(position)));

    list_point.add(point1);


    point point2 = new point();
    point2.setX(Integer.valueOf(point_x2_list.get(position)));
    point2.setY(Integer.valueOf(point_y2_list.get(position)));

    list_point.add(point2);

    point point3 = new point();
    point3.setX(Integer.valueOf(point_x3_list.get(position)));
    point3.setY(Integer.valueOf(point_y3_list.get(position)));

    list_point.add(point3);

    point point4 = new point();
    point4.setX(Integer.valueOf(point_x4_list.get(position)));
    point4.setY(Integer.valueOf(point_y4_list.get(position)));

    list_point.add(point4);

    // for(point p:list_point){
    for(int i=0;i<list_point.size();i++){

        point p =list_point.get(i);
        bitmap=   draw_point(bitmap,p.getX(),p.getY(),i,list_point);
        imageView.setImageBitmap(bitmap);
    }

}






    }


    public Bitmap draw_point(Bitmap src,int x,int y,int count,ArrayList<point> list_point){


        Bitmap tempBitmap = Bitmap.createBitmap(src.getWidth(),src.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);

//Draw the image bitmap into the cavas
        tempCanvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //myPaint.setColor(Color.TRANSPARENT);
        // myPaint.
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(15);
//Draw everything else you want into the canvas, in this example a rectangle with rounded edges
        // tempCanvas.drawRoundRect(new RectF(40,40,400,400), 50, 50, paint);
        tempCanvas.drawPoint(x,y,paint);


        if(count>0){
            Paint paint_line = new Paint(Paint.ANTI_ALIAS_FLAG);
            //myPaint.setColor(Color.TRANSPARENT);
            // myPaint.
            paint_line.setStyle(Paint.Style.STROKE);
            paint_line.setColor(Color.RED);
            paint_line.setStrokeWidth(15);

            point po1=list_point.get(count);
            point po2=list_point.get(count-1);
            tempCanvas.drawLine(po1.getX(),po1.getY(),po2.getX(),po2.getY(),paint_line);

        }

        if(count==3){
            Paint paint_line = new Paint(Paint.ANTI_ALIAS_FLAG);
            //myPaint.setColor(Color.TRANSPARENT);
            // myPaint.
            paint_line.setStyle(Paint.Style.STROKE);
            paint_line.setColor(Color.RED);
            paint_line.setStrokeWidth(15);

            point po1=list_point.get(0);
            point po2=list_point.get(3);
            tempCanvas.drawLine(po1.getX(),po1.getY(),po2.getX(),po2.getY(),paint_line);

        }



//Attach the canvas to the ImageView

        return  tempBitmap;


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

