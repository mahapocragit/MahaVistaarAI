package com.ai.ai_disc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;



public class annotation_adapter extends RecyclerView.Adapter<annotation_adapter.MyViewHolder> {

    Context context;
    ArrayList<String> point_x1_list;
    ArrayList<String> point_y1_list;
    ArrayList<String> point_x2_list;
    ArrayList<String> point_y2_list;
    ArrayList<String> point_x3_list;
    ArrayList<String> point_y3_list;
    ArrayList<String> point_x4_list;
    ArrayList<String> point_y4_list;
    ArrayList<String> message_list;
    Bitmap bitmap;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView message;
        TextView number;
        public MyViewHolder(View view) {
            super(view);
            imageView=(ImageView)view.findViewById(R.id.image);
             message=(TextView)view.findViewById(R.id.message);
            number=(TextView)view.findViewById(R.id.number);
        }
    }


    public annotation_adapter(Context context,
                         ArrayList<String> point_x1_list,
                         ArrayList<String> point_y1_list,
                         ArrayList<String> point_x2_list,
                         ArrayList<String> point_y2_list,
                         ArrayList<String> point_x3_list,
                         ArrayList<String> point_y3_list,
                         ArrayList<String> point_x4_list,
                         ArrayList<String> point_y4_list,
                              Bitmap bitmap,
                         ArrayList<String> message_list
    ) {
        this.context=context;
        this.point_x1_list=point_x1_list;
        this.point_x2_list=point_x2_list;
        this.point_x3_list=point_x3_list;
        this.point_x4_list=point_x4_list;
        this.point_y1_list=point_y1_list;
        this.point_y2_list=point_y2_list;
        this.point_y3_list=point_y3_list;
        this.point_y4_list=point_y4_list;
        this.bitmap=bitmap;
        this.message_list=message_list;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.annotation_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        holder.message.setText("Message :"+message_list.get(position));
        holder.number.setText(" "+(position+1));

        Bitmap bitmap1=bitmap;

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





        for(int i=0;i<list_point.size();i++){

            point p =list_point.get(i);
            bitmap1=   draw_point(bitmap1,p.getX(),p.getY(),i,list_point);
            holder.imageView.setImageBitmap(bitmap1);
        }


    }

    @Override
    public int getItemCount() {
        return  point_x1_list.size();
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
}

