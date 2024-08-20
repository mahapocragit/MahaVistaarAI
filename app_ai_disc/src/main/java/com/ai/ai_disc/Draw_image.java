package com.ai.ai_disc;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;


import java.util.ArrayList;


public class Draw_image extends AppCompatActivity {

    ImageView image;
    ArrayList<point> list_point ;
    String url="";
    Bitmap bitmap1;
    InternetReceiver internet = new InternetReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        image=(ImageView)findViewById(R.id.image);

        System.out.println(" inside ---------------------------------------checking");

        list_point=Data1.list_point;

        save_point();

        Intent intent=getIntent();
        Bundle data=  intent.getExtras();
        url= data.getString("url");
        System.out.println("url "+url);



        GlideApp.with(Draw_image.this)
                .load(url)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                       // int width = resource.getWidth();
                       // int height = resource.getHeight();

                       // bitmap1=resource;
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                        bitmap1 = bitmapDrawable.getBitmap();
                        // original_bitmap=resource;

                        image.setImageBitmap(bitmap1);

                        // draw_point(bitmap1)


                       // System.out.println(" width:"+width);
                       // System.out.println(" height:"+height);
                        drawing();
                    }
                });

        /*
        Glide.with(Draw_image.this)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        System.out.println(" bitmap is ready");

                        int width = resource.getWidth();
                        int height = resource.getHeight();

                        bitmap1=resource;
                       // original_bitmap=resource;

                        image.setImageBitmap(bitmap1);

                       // draw_point(bitmap1)


                        System.out.println(" width:"+width);
                        System.out.println(" height:"+height);
                        drawing();
                    }
                });

         */



    }

    public void drawing(){

        for(point o:list_point){

            System.out.println(" X 1 is :"+o.getX()+" Y 1 is:"+o.getY());

           bitmap1= draw_point(bitmap1,o.getX(),o.getY());
           image.setImageBitmap(bitmap1);
        // System.out.println(" Y is :"+o.getY());
        }

    }

    public Bitmap draw_point(Bitmap src,int x,int y){

      //  point po = new point();
      //  po.setX(x);
       // po.setY(y);

       // list_point.add(po);



        Bitmap tempBitmap = Bitmap.createBitmap(src.getWidth(),src.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);

//Draw the image bitmap into the cavas
        tempCanvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //myPaint.setColor(Color.TRANSPARENT);
        // myPaint.
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(30);
//Draw everything else you want into the canvas, in this example a rectangle with rounded edges
        // tempCanvas.drawRoundRect(new RectF(40,40,400,400), 50, 50, paint);
        tempCanvas.drawPoint(x,y,paint);
        /*

        if(count>0){
            Paint paint_line = new Paint(Paint.ANTI_ALIAS_FLAG);
            //myPaint.setColor(Color.TRANSPARENT);
            // myPaint.
            paint_line.setStyle(Paint.Style.STROKE);
            paint_line.setColor(Color.RED);
            paint_line.setStrokeWidth(30);

            point po1=list_point.get(count);
            point po2=list_point.get(count-1);
            tempCanvas.drawLine(po1.getX(),po1.getY(),po2.getX(),po2.getY(),paint_line);

        }


         */




//Attach the canvas to the ImageView

        return  tempBitmap;

        // image.setImageBitmap(tempBitmap);
    }

    public void save_point(){

        for(point o:list_point){

            System.out.println(" X 1 is :"+o.getX()+" Y 1 is:"+o.getY());
            // System.out.println(" Y is :"+o.getY());
        }

        Toast.makeText(Draw_image.this,"Checking.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet,intentFilter);

        // InternetReceiver internet = new InternetReceiver();


    }


    @Override
    public void onStop(){
        super.onStop();
        unregisterReceiver(internet);

    }

}
