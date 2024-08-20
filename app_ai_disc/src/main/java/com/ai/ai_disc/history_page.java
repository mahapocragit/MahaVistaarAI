package com.ai.ai_disc;


import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.media.RingtoneManager.getDefaultUri;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.BulletSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ai.ai_disc.model.Management_Practices_Disease;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hsalf.smileyrating.SmileyRating;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class history_page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    InternetReceiver internet;
    TextView loct;
    int PICK_PDF_FILE=500;
    List<Management_Practices_Disease> management_practices_diseaseList;

    LinearLayout mlb_layout, tlb_layout, blsb_layout, expertInputLayout,
            remarksLayout, modelPrediction, casual_Organism_Layout,
            prevalance_Layout, predisposingFunction_Layout, Symptoms_Layout, ManagementPractices_Layout, Diseases_Layout_main,pest_Layout_main;
    TextView disease_result, disease_user_text, disease_user,
            prediction_status_text,
            prediction_status1,cultural_pest_Text,
            scientificName_pest_Text,chemical_pest_Text,bilogical_pest_Text,symptoms_pest_Text;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id_notify = 1;
    TextView prediction_status,score;
    ImageView image;
    String crop,url;
    int pageHeight = 2700;
    int pagewidth = 792;
    private static final int PERMISSION_REQUEST_CODE = 200;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id_firebase = "";
    // creating a bitmap variable
    // for storing our images
    Bitmap bmp, scaledbmp,imgs,sacledimg,scaledlogo;
    String id ;
    String dp_id ;
    String dp_type ;
    String crop_id ;
    String dpname ;
    String cp ;
    String dat ;
    Management_Practices_Disease model;
    String farmerIdentification, type = "";
    TextView disease_user_value, result_identify, casual_Organism_Text,
            prevalance_Text, predisposingFunction_Text, symptoms_Text, cultural_Text, chemical_Text, biological_Text;
    Button next,download;
    String identificationType = "", identificationId = "", cropId = "",insectId="";
    CardView casual_Organism_cv, prevalance_cv, predisposingFunction_cv, disease_Symptoms_cv, disease_ManagementPractices_cv, disease_CC_cv, disease_CHC_cv, disease_BC_cv;
ImageView rt;
int ratedvalue=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);
         id = getIntent().getExtras().getString("id");
         dp_id = getIntent().getExtras().getString("dp_id");
         dp_type = getIntent().getExtras().getString("dp_type");
         crop_id = getIntent().getExtras().getString("crop_id");
         dpname = getIntent().getExtras().getString("dpname");
         cp = getIntent().getExtras().getString("crop");
         dat = getIntent().getExtras().getString("dat");
        result_identify=findViewById(R.id.result_identify);
        result_identify.setText(cp+" : "+dpname);
        TextView datd = findViewById(R.id.dat);
        datd.setText(dat);
        rt=findViewById(R.id.rate);
        score=findViewById(R.id.score);
        download=findViewById(R.id.download);
        ratedvalue=Integer.parseInt(getIntent().getExtras().getString("rate"));
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logocut);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 120, 120, false);
        scaledlogo = BitmapFactory.decodeResource(getResources(), R.drawable.logos);
        scaledlogo = Bitmap.createScaledBitmap(scaledlogo, 300, 120, false);

        // below code is used for
        // checking our permissions.
//        if (checkPermission()) {
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//        } else {
//            requestPermission();
//        }

        switch (ratedvalue){
            case 1:{
                score.setText("1/5");
                rt.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sentiment_very_dissatisfied_24, getTheme()));break;
            }
            case 2:{
                score.setText("2/5");
                rt.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sentiment_dissatisfied_24, getTheme()));break;
            }
            case 3:{
                score.setText("3/5");
                rt.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sentiment_satisfied_24, getTheme()));break;
            }
            case 4:{
                score.setText("4/5");
                rt.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sentiment_satisfied_alt_24, getTheme()));break;
            }
            case 5:{
                score.setText("5/5");
                rt.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sentiment_very_satisfied_24, getTheme()));break;
            }

        }

        Diseases_Layout_main = findViewById(R.id.Diseases_Layout_main);
        pest_Layout_main=findViewById(R.id.pest_Layout_main);
        casual_Organism_Layout = findViewById(R.id.casual_Organism_Layout);
        prevalance_Layout = findViewById(R.id.prevalance_Layout);
        predisposingFunction_Layout = findViewById(R.id.predisposingFunction_Layout);
        Symptoms_Layout = findViewById(R.id.Symptoms_Layout);
        ManagementPractices_Layout = findViewById(R.id.ManagementPractices_Layout);
        casual_Organism_Text = findViewById(R.id.casual_Organism_Text);
        prevalance_Text = findViewById(R.id.prevalance_Text);
        predisposingFunction_Text = findViewById(R.id.predisposingFunction_Text);
        symptoms_Text = findViewById(R.id.symptoms_Text);
        cultural_Text = findViewById(R.id.cultural_Text);
        chemical_Text = findViewById(R.id.chemical_Text);
        biological_Text = findViewById(R.id.biological_Text);
        management_practices_diseaseList = new ArrayList<>();

        result_identify = findViewById(R.id.result_identify);
//        modelPrediction = findViewById(R.id.modelPrediction);
//        expertInputLayout = findViewById(R.id.expertInputLayout);
//        remarksLayout = findViewById(R.id.remarksLayout);
        disease_result = (TextView) findViewById(R.id.disease);
        prediction_status = findViewById(R.id.prediction_status);
        image = findViewById(R.id.image);
//        disease_user_value = findViewById(R.id.disease_user);

//        disease_user_text = findViewById(R.id.disease_user_text);
//        disease_user = findViewById(R.id.disease_user);
        prediction_status_text = findViewById(R.id.prediction_status_text);
        prediction_status1 = findViewById(R.id.prediction_status);
        scientificName_pest_Text= findViewById(R.id.scientificName_pest_Text);
        cultural_pest_Text= findViewById(R.id.cultural_pest_Text);
        chemical_pest_Text= findViewById(R.id.chemical_pest_Text);
        bilogical_pest_Text= findViewById(R.id.bilogical_pest_Text);
        symptoms_pest_Text= findViewById(R.id.dynamicSymptom_pest_Text);
        //Toast.makeText(history_page.this,dpname+cp+dp_type.toString(),Toast.LENGTH_SHORT).show();

        ImageView img = findViewById(R.id.image);
        next= findViewById(R.id.next);
        setNavigationViewListener();
        drawerLayout = findViewById(R.id.my_drawer_layout2);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        internet=new InternetReceiver();
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer2);
        View head=navigationView.getHeaderView(0);

        TextView userd=head.findViewById(R.id.name_dataentry);
        TextView acc=head.findViewById(R.id.type_nav);
        loct=head.findViewById(R.id.loctfff);
        //loct.setText(addressed);
        try{userd.setText(user_singleton.getInstance().getfname()+" "+user_singleton.getInstance().getMname()+" "+user_singleton.getInstance().getlname());
            acc.setText(user_singleton.getInstance().getUser_type()+" Account");
            loct.setText(user_singleton.getInstance().getloct());
            //Log.d("ggg",head.findViewById(R.id.vsr).toString());


        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("error",e.toString());
        }

        JSONObject object = new JSONObject();
        try {
            object.put("image_id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (ratedvalue!=0){
            next.setVisibility(View.GONE);
        }
        download.setEnabled(false);
        AndroidNetworking.post("https://aidisc.krishimegh.in:32517/get_images")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String nb = jsonObject.getString("encoded_string");
                            //Log.d("guuuu",nb);
                            byte[] decodedString = Base64.decode(nb, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            img.setImageBitmap(decodedByte);

                            sacledimg = Bitmap.createScaledBitmap(decodedByte, 400, 400, false);
                            download.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            download.setEnabled(false);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                       // Log.d("gggg","errorr");
                        download.setEnabled(false);
                    }
                });




        if(dp_type.matches("1")){
            get_Management_Practices_Disease(dp_id,crop_id);
            Diseases_Layout_main.setVisibility(View.VISIBLE);

        }
        if(dp_type.matches("2")){
            get_Management_Practices_Pest(dp_id,crop_id);
            pest_Layout_main.setVisibility(View.VISIBLE);
        }
        TextView heal=findViewById(R.id.heal);
        if (dp_type.matches("0")){
            heal.setVisibility(View.VISIBLE);
            download.setVisibility(View.GONE);
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder popDialog = new AlertDialog.Builder(history_page.this);
                SmileyRating sml=new SmileyRating(history_page.this);
                LinearLayout linearLayout = new LinearLayout(history_page.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                sml.setLayoutParams(lp);
                linearLayout.addView(sml);
                linearLayout.setGravity(Gravity.CENTER);

//
//                popDialog.setIcon(android.R.drawable.gallery_thumb);
                popDialog.setTitle("How useful is it to you?");

                //add linearLayout to dailog
                popDialog.setView(linearLayout);
                popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SmileyRating.Type smiley = sml.getSelectedSmiley();
// You can compare it with rating Type

                        // You can get the user rating too
                        // rating will between 1 to 5, but -1 is none selected
                        int rating = smiley.getRating();

                        rating(rating,id);
                    }
                });
                popDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                popDialog.show();




            }


        });

        rt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder popDialog = new AlertDialog.Builder(history_page.this);

                SmileyRating sml=new SmileyRating(history_page.this);
                LinearLayout linearLayout = new LinearLayout(history_page.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                sml.setLayoutParams(lp);
                linearLayout.addView(sml);
                linearLayout.setGravity(Gravity.CENTER);

//
//                popDialog.setIcon(android.R.drawable.gallery_thumb);
                popDialog.setTitle("How useful is it to you?");

                //add linearLayout to dailog
                popDialog.setView(linearLayout);
                popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SmileyRating.Type smiley = sml.getSelectedSmiley();
// You can compare it with rating Type

                        // You can get the user rating too
                        // rating will between 1 to 5, but -1 is none selected
                        int rating = smiley.getRating();

                        rating(rating,id);
                    }
                });
                popDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                popDialog.show();

            }
        });


        //download.setVisibility(View.GONE);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (!checkPermission()) {
                        //Toast.makeText(history_page.this, "hhhhh", Toast.LENGTH_SHORT).show();
                        generatePDF();
                    } else {
                        if (checkPermission()) {
                            requestPermissionAndContinue();
                        } else {
                            //Toast.makeText(history_page.this, "hhhhh1", Toast.LENGTH_SHORT).show();

                            generatePDF();

                        }
                    }
                }catch (Exception e){}
            }
        });

    }


    private void generatePDF() {
        // creating an object variable
        // for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();
        Paint title1 = new Paint();
        Paint title11 = new Paint();
        Paint infoheader = new Paint();
        Paint infoline = new Paint();
        Paint infoline1 = new Paint();
        Paint infoline1d = new Paint();
        TextPaint mTextPaint=new TextPaint();


        //mTextPaint.setAntiAlias(true);
        //mTextPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));




        title.setTextAlign(Paint.Align.CENTER);
        title1.setTextAlign(Paint.Align.CENTER);
        infoline1d.setTextAlign(Paint.Align.CENTER);
        title11.setTextAlign(Paint.Align.CENTER);
        int xPos = (pagewidth / 2);
        infoheader.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));
        infoheader.setTextSize(25);
        infoheader.setColor(ContextCompat.getColor(this, R.color.heading));

        infoline.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        infoline.setTextSize(22);

        infoline.setColor(ContextCompat.getColor(this, R.color.black));

        infoline1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        infoline1.setTextSize(22);

        infoline1.setColor(ContextCompat.getColor(this, R.color.red));
        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 2).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.
        float centerX = (pagewidth  - scaledbmp.getWidth()) * 0.5f;
        float centerX1 = (pagewidth  - sacledimg.getWidth()) * 0.5f;
        float centerX11 = (pagewidth  - scaledlogo.getWidth()) * 0.5f;
        infoline1d.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        infoline1d.setTextSize(15);
        infoline1d.setColor(ContextCompat.getColor(this, R.color.black));
        title1.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        title1.setTextSize(22);
        title1.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        title11.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        title11.setTextSize(22);
        title11.setColor(ContextCompat.getColor(this, R.color.greendark));
        title.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        title.setTextSize(30);
        title.setColor(ContextCompat.getColor(this, R.color.colorMaroon));


        int writ=80;
        canvas.drawBitmap(scaledlogo, 50, writ, paint);
        canvas.drawBitmap(scaledbmp, pagewidth-50-120, writ, paint);
        int rightp=writ+120+30;
        int leftp=writ+120+30;

        canvas.drawText("https://nahep.icar.gov.in/", 50+150, leftp, infoline1d);
        canvas.drawText("http://bitly.ws/vEAp", pagewidth-50-60, rightp, infoline1d);

        writ=30+rightp;

        //writ+=scaledbmp.getHeight()+30;
        canvas.drawText("AI-DISC", xPos, writ, title);
        writ+=35;
        canvas.drawText("Identification Report", xPos, writ, title);
        writ+=35;

        canvas.drawLine(10,writ,792-10,writ,paint);
         writ+=35;

        canvas.drawBitmap(sacledimg, centerX1, writ, paint);
        writ+=35+sacledimg.getHeight()+35;

        canvas.drawText("Crop Name", 10, writ, infoheader);
        canvas.drawText(cp+" ", 300, writ, infoline);
        writ+=35;
        canvas.drawText("Disease Name", 10, writ, infoheader);
        canvas.drawText(dpname+" ", 300, writ, infoline);
        writ+=35;
        canvas.drawLine(10,writ,792-10,writ,paint);

        writ+=35;
        canvas.drawText("About the Disease", xPos, writ, title1);
//        writ+=35;
//        canvas.drawLine(xPos-150,writ,xPos+150,writ,paint);

        writ+=45;
        canvas.drawText("Causal Organism", 10, writ, infoheader);

        canvas.drawText(management_practices_diseaseList.get(0).getCasual_Organism_static(), 300, writ, infoline1);
        writ+=45;

        if (!management_practices_diseaseList.get(0).getPrevalance_Text().matches("")){

        canvas.drawText("Prevalance", 10, writ+30, infoheader);
        //writ+=15;
        mTextPaint.setTextSize(22);
            StaticLayout mTextLayout = new StaticLayout(management_practices_diseaseList.get(0).getPrevalance_Text(), mTextPaint, canvas.getWidth()-10-310, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();

            canvas.translate(300,writ);
            mTextLayout.draw(canvas);
            canvas.restore();
            writ+=45+mTextLayout.getHeight();
            mTextPaint.reset();
            //writ+=45+getTextHeight(management_practices_diseaseList.get(0).getPrevalance_Text(),mTextPaint) ;
        }
        //canvas.drawText(management_practices_diseaseList.get(0).getPrevalance_Text(), 10, writ, infoline);}

/////////////////////////////////////////////////
        if (!management_practices_diseaseList.get(0).getPredisposingFunction_Text().matches("")&& !management_practices_diseaseList.get(0).getPredisposingFunction_Text().equals("NA")){

            canvas.drawText("Predisposing Factors", 10, writ+30, infoheader);

            mTextPaint.setTextSize(22);
            StaticLayout mTextLayout = new StaticLayout(management_practices_diseaseList.get(0).getPredisposingFunction_Text(), mTextPaint, canvas.getWidth()-320, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();

            canvas.translate(300,writ);
            mTextLayout.draw(canvas);
            canvas.restore();
            writ+=45+mTextLayout.getHeight();
            mTextPaint.reset();
            //writ+=45+getTextHeight(management_practices_diseaseList.get(0).getPredisposingFunction_Text(),mTextPaint) ;
            //canvas.drawText(management_practices_diseaseList.get(0).getPredisposingFunction_Text(), 10, writ, infoline);
            }

        if (!management_practices_diseaseList.get(0).getSymptoms().matches("")&& !management_practices_diseaseList.get(0).getSymptoms().equals("NA")){
            canvas.drawText("Symptoms", 10, writ+30, infoheader);
            mTextPaint.setTextSize(22);
            StaticLayout mTextLayout = new StaticLayout(splitmerge(management_practices_diseaseList.get(0).getSymptoms()), mTextPaint, canvas.getWidth()-320, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();

            canvas.translate(300,writ);
            mTextLayout.draw(canvas);
            canvas.restore();
            writ+=45+mTextLayout.getHeight();
            mTextPaint.reset();
            //writ+=45+getTextHeight(management_practices_diseaseList.get(0).getSymptoms(),mTextPaint);
            //canvas.drawText(management_practices_diseaseList.get(0).getSymptoms(), 10, writ, infoline);
        }


        canvas.drawLine(10,writ,792-10,writ,paint);
        writ+=35;
        canvas.drawText("Management Practices of the Disease", xPos, writ, title1);
        writ+=45;

        if (!management_practices_diseaseList.get(0).getCultural_Text().matches("")&& !management_practices_diseaseList.get(0).getCultural_Text().equals("NA")){

            canvas.drawText("Cultural Control", 10, writ+30, infoheader);

            mTextPaint.setTextSize(22);
            StaticLayout mTextLayout = new StaticLayout(splitmerge(management_practices_diseaseList.get(0).getCultural_Text()), mTextPaint, canvas.getWidth()-320, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();

            canvas.translate(300,writ);
            mTextLayout.draw(canvas);
            canvas.restore();
            writ+=45+mTextLayout.getHeight();
            mTextPaint.reset();
            //writ+=45+getTextHeight(management_practices_diseaseList.get(0).getCultural_Text(),mTextPaint) ;
            //canvas.drawText(management_practices_diseaseList.get(0).getCultural_Text(), 10, writ, infoline);
        }


        if (!management_practices_diseaseList.get(0).getChemical_Text().matches("")&& !management_practices_diseaseList.get(0).getChemical_Text().equals("NA")){

            canvas.drawText("Chemical Control", 10, writ+30, infoheader);
            mTextPaint.setTextSize(22);
            StaticLayout mTextLayout = new StaticLayout(splitmerge(management_practices_diseaseList.get(0).getChemical_Text()), mTextPaint, canvas.getWidth()-320, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();

            canvas.translate(300,writ);
            mTextLayout.draw(canvas);
            canvas.restore();
            writ+=45+mTextLayout.getHeight();
            mTextPaint.reset();
            //writ+=45+getTextHeight(management_practices_diseaseList.get(0).getChemical_Text(),mTextPaint) ;
            }

        if (!management_practices_diseaseList.get(0).getBiological_Text().matches("")&& !management_practices_diseaseList.get(0).getBiological_Text().equals("NA")){

            canvas.drawText("Biological Control", 10, writ+30, infoheader);
            mTextPaint.setTextSize(22);
            StaticLayout mTextLayout = new StaticLayout(splitmerge(management_practices_diseaseList.get(0).getBiological_Text()), mTextPaint, canvas.getWidth()-320, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();

            canvas.translate(300,writ);
            mTextLayout.draw(canvas);
            canvas.restore();
            writ+=45+mTextLayout.getHeight();
            mTextPaint.reset();
            //writ+=45+getTextHeight(management_practices_diseaseList.get(0).getBiological_Text(),mTextPaint) ;
        }


        canvas.drawLine(10,writ,792-10,writ,paint);
//        writ+=20;




        writ+=40;
        canvas.drawText("Designed and developed by", xPos, writ, infoline1d);
        writ+=30;
        canvas.drawText("Division of Computer Application", xPos, writ, title1);
        writ+=30;
        canvas.drawText("ICAR-Indian Agricultural Statistics Research Institute", xPos, writ, title1);

        writ+=30;
        canvas.drawText("https://iasri.icar.gov.in/", xPos, writ, infoline1d);
        writ+=30;
        mTextPaint.setTextSize(22);
        StaticLayout mTextLayout = new StaticLayout("Disclaimer : This report is a system generated report. The Artificial Intelligence-based identification may be incorrect sometimes, so as the corresponding information. Therefore, we suggest you to kindly verify this content with the domain experts before taking any action", mTextPaint, canvas.getWidth()-40, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();

        canvas.translate(20,writ);
        mTextLayout.draw(canvas);
        canvas.restore();
        //writ+=45+mTextLayout.getHeight();
        mTextPaint.reset();
        writ+=30;
        Date dtyy=new Date();
        SimpleDateFormat tms = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yyyy");
        canvas.drawText("Generated on,  "+sdfs.format(dtyy)+", "+tms.format(dtyy), xPos, writ+mTextLayout.getHeight(), infoline1d);

//        canvas.drawText("Download AI-DISC Mobile APP", xPos, writ, title);
//
//        writ+=30;
//        canvas.drawText("https://play.google.com/store/apps/details?id=com.ai.ai_disc", xPos, writ, title1);//http://bitly.ws/vEAp

        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.
        String folderName = "AI-DISC";
        OutputStream fos = null;
        File imageFile = null;

        int df= new Random().nextInt(10000) ;
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat dfd = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = dfd.format(c);
        String filc="ai-disc_"+cp+formattedDate+"_"+String.valueOf(df);

        String imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + folderName;
        imageFile = new File(imagesDir);


        if (!imageFile.exists()) {
            imageFile.mkdir();

        }
        imageFile = new File(imagesDir, filc + ".pdf");
        String imn=imagesDir+"/"+filc + ".pdf";
//        Log.d("imageFile",imn);
//        Log.d("imageFile1",imagesDir);

        try {

            pdfDocument.writeTo(new FileOutputStream(imageFile));

            //Toast.makeText(history_page.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
            setNotification("Your report has been generated ! Find at Downloads/AI-DISC/",imageFile);
        } catch (IOException e) {

            e.printStackTrace();
           // Log.d("err+pdf",e.toString());

        }

        pdfDocument.close();
    }
    private void rating(int ratd,String id){
        JSONObject object1 = new JSONObject();
        try {
            object1.put("id", id);
            object1.put("rate", ratd);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/setrate")
                .addJSONObjectBody(object1)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        boolean res=false;

                            res=jsonObject.optBoolean("result");
                            if (res){
                                Toast.makeText(history_page.this, "Thank you for your feedback.", Toast.LENGTH_SHORT).show();
                                switch (ratd){
                                    case 1:{
                                        score.setText("1/5");
                                        rt.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sentiment_very_dissatisfied_24, getTheme()));break;
                                    }
                                    case 2:{
                                        score.setText("2/5");
                                        rt.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sentiment_dissatisfied_24, getTheme()));break;
                                    }
                                    case 3:{
                                        score.setText("3/5");
                                        rt.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sentiment_satisfied_24, getTheme()));break;
                                    }
                                    case 4:{
                                        score.setText("4/5");
                                        rt.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sentiment_satisfied_alt_24, getTheme()));break;
                                    }
                                    case 5:{
                                        score.setText("5/5");
                                        rt.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sentiment_very_satisfied_24, getTheme()));break;
                                    }


                                }

                            }next.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });


    }


    public void get_Management_Practices_Pest(String pest_id, String cropId)
    {
        management_practices_diseaseList.clear();
        JSONObject jo = new JSONObject();
        try {

           /* jo.put("cropId", "69");
            jo.put("pestId", "190");*/
            jo.put("cropId", cropId);
            jo.put("pestId", pest_id);

        } catch (JSONException E) {

        }

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Managment_Practices_pest")
                .addJSONObjectBody(jo)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            for (int i = 0; i < response.length(); i++) {


                                JSONObject object = (JSONObject) response.get(i);

                                model = new Management_Practices_Disease();
                                model.setCropId(object.optString("crop_id"));
                                model.setInsectId(object.optString("insect_id"));
                                model.setScientific_Name(object.optString("scientific_Name"));
                                model.setDamage_symptoms(object.optString("damage_symptoms"));
                                model.setCultural_Text(object.optString("Cultural_control"));
                                model.setChemical_Text(object.optString("Chemical_control"));
                                model.setBiological_Text(object.optString("Biological_control"));
                                management_practices_diseaseList.add(model);

                            }

                            //System.out.println("sizzzeeeee" + management_practices_diseaseList.size());
                            if(management_practices_diseaseList.size()==0)
                            {
                                scientificName_pest_Text.setText("Not available");
                                symptoms_pest_Text.setText("Not available");
                                cultural_pest_Text.setText("Not available");
                                chemical_pest_Text.setText("Not available");
                                bilogical_pest_Text.setText("Not available");
                            }
                            else
                            {
                                for (int i = 0; i < management_practices_diseaseList.size(); i++)
                                {




                                    if (management_practices_diseaseList.get(i).getScientific_Name() != null && !management_practices_diseaseList.get(i).getScientific_Name().equals("NA") && !management_practices_diseaseList.get(i).getScientific_Name().isEmpty()) {
                                        scientificName_pest_Text.setText(management_practices_diseaseList.get(i).getScientific_Name());

                                    } else {
                                        scientificName_pest_Text.setText("Not available");
                                    }


                                    if (management_practices_diseaseList.get(i).getDamage_symptoms() != null && !management_practices_diseaseList.get(i).getDamage_symptoms().equals("NA") && !management_practices_diseaseList.get(i).getDamage_symptoms().isEmpty()) {
                                        symptoms_pest_Text.setText(management_practices_diseaseList.get(i).getDamage_symptoms());

                                    } else {
                                        symptoms_pest_Text.setText("Not available");
                                    }


                                    if (management_practices_diseaseList.get(i).getCultural_Text() != null && !management_practices_diseaseList.get(i).getCultural_Text().equals("NA") && !management_practices_diseaseList.get(i).getCultural_Text().isEmpty()) {
                                        cultural_pest_Text.setText(management_practices_diseaseList.get(i).getCultural_Text());

                                    } else {
                                        cultural_pest_Text.setText("Not available");
                                    }
                                    if (management_practices_diseaseList.get(i).getChemical_Text() != null && !management_practices_diseaseList.get(i).getChemical_Text().equals("NA") && !management_practices_diseaseList.get(i).getChemical_Text().isEmpty()) {
                                        chemical_pest_Text.setText(management_practices_diseaseList.get(i).getChemical_Text());

                                    } else {
                                        chemical_pest_Text.setText("Not available");
                                    }
                                    if (management_practices_diseaseList.get(i).getBiological_Text() != null && !management_practices_diseaseList.get(i).getBiological_Text().equals("NA")&& !management_practices_diseaseList.get(i).getBiological_Text().isEmpty()) {
                                        bilogical_pest_Text.setText(management_practices_diseaseList.get(i).getBiological_Text());

                                    } else {
                                        bilogical_pest_Text.setText("Not available");
                                    }


                                }
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(history_page.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void get_Management_Practices_Disease(String identificationId, String cropId)
    {

        management_practices_diseaseList.clear();
        JSONObject jo = new JSONObject();
        try {

            jo.put("cropId", cropId);
            jo.put("diseaseId", identificationId);

            //System.out.println("jsonnnnn" + jo.toString());

        } catch (JSONException E) {

        }

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Managment_Practices_diseases")
                .addJSONObjectBody(jo)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            for (int i = 0; i < response.length(); i++)
                            {


                                JSONObject object = (JSONObject) response.get(i);

                                model = new Management_Practices_Disease();
                                model.setCropId(object.optString("crop_id"));
                                model.setDiseaseId(object.optString("disease_id"));
                                model.setCasual_Organism_static(object.optString("scientific_name"));
                                model.setPrevalance_Text(object.optString("Prevalance"));
                                model.setPredisposingFunction_Text(object.optString("Predisposing_Factors"));
                                model.setSymptoms(object.optString("Symptoms"));
                                model.setCultural_Text(object.optString("Cultural_control"));
                                model.setChemical_Text(object.optString("Chemical_control"));
                                model.setBiological_Text(object.optString("Biological_control"));
                                management_practices_diseaseList.add(model);


/*


                                System.out.println("Disease_name" + object.optString("Disease_name"));
                                System.out.println("response" + response.toString());
                                System.out.println("prevalance" + object.optString("Prevalance"));
                                System.out.println("Predisposing_Factors" + object.optString("Predisposing_Factors"));
                                System.out.println("Cultural_control" + object.optString("Cultural_control"));
                                System.out.println("Chemical_control" + object.optString("Chemical_control"));
                                System.out.println("Biological_control" + object.optString("Biological_control"));
*/



                            }


                            for (int i = 0; i < management_practices_diseaseList.size(); i++)
                            {

                                if (management_practices_diseaseList.get(i).getCasual_Organism_static() != null && !management_practices_diseaseList.get(i).getCasual_Organism_static().equals("NA")) {
                                    casual_Organism_Text.setText(management_practices_diseaseList.get(i).getCasual_Organism_static());

                                } else {
                                    casual_Organism_Text.setText("Not available");
                                }


                                if (management_practices_diseaseList.get(i).getPrevalance_Text() != null && !management_practices_diseaseList.get(i).getPrevalance_Text().equals("NA")) {

                                    prevalance_Text.setText(management_practices_diseaseList.get(i).getPrevalance_Text());

                                } else {
                                    prevalance_Text.setText("Not available");
                                }

                                if (management_practices_diseaseList.get(i).getPredisposingFunction_Text() != null) {
                                    if (management_practices_diseaseList.get(i).getPredisposingFunction_Text().equals("NA")) {
                                        predisposingFunction_Text.setText("Not available");


                                    } else {
                                        predisposingFunction_Text.setText(management_practices_diseaseList.get(i).getPredisposingFunction_Text());
                                    }

                                } else {
                                    predisposingFunction_Text.setText("Not available");
                                }
                                if (management_practices_diseaseList.get(i).getSymptoms() != null && !management_practices_diseaseList.get(i).getSymptoms().equals("NA")) {


                                    symptoms_Text.setText(splitmerge( management_practices_diseaseList.get(i).getSymptoms()));

                                    //System.out.println("rtrttr" + management_practices_diseaseList.get(i).getSymptoms());

                                } else {
                                    symptoms_Text.setText("Not available");
                                }
                                if (management_practices_diseaseList.get(i).getCultural_Text() != null && !management_practices_diseaseList.get(i).getCultural_Text().equals("NA")) {
                                    cultural_Text.setText(splitmerge( management_practices_diseaseList.get(i).getCultural_Text()));

                                } else {
                                    cultural_Text.setText("Not available");
                                }
                                if (management_practices_diseaseList.get(i).getChemical_Text() != null && !management_practices_diseaseList.get(i).getChemical_Text().equals("NA")) {
                                    chemical_Text.setText(splitmerge( management_practices_diseaseList.get(i).getChemical_Text()));

                                } else {
                                    chemical_Text.setText("Not available");
                                }
                                if (management_practices_diseaseList.get(i).getBiological_Text() != null && !management_practices_diseaseList.get(i).getBiological_Text().equals("NA")) {
                                    biological_Text.setText(splitmerge( management_practices_diseaseList.get(i).getBiological_Text()));

                                } else {
                                    biological_Text.setText("Not available");
                                }

                            }

                            if(management_practices_diseaseList.size()==0)
                            {
                                Diseases_Layout_main.setVisibility(View.GONE);
                                Toast.makeText(history_page.this, "No management practices found for this disease", Toast.LENGTH_SHORT).show();

                                    download.setVisibility(View.GONE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(history_page.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });


    }
    SpannableString splitmerge(String val){
        String[] sp=val.split(";");
        StringBuilder rt= new StringBuilder();
        StringBuilder rt1= new StringBuilder();
        SpannableString strings ;
        String sep = System.lineSeparator();
        for (int i = 0; i < sp.length; i += 1) {
            rt.append(String.valueOf(i + 1)).append(". ").append(sp[i].trim()).append("\n\n");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            for (int i = 0; i < sp.length; i += 1) {
                if(i==sp.length-1){
                    rt1.append(sp[i].trim());
                }else{
                rt1.append(sp[i].trim()).append(sep).append(sep);}

            }

            String concat=rt1.toString();
            strings=new SpannableString(concat);
//            SpannableStringBuilder cg = new SpannableStringBuilder();
            for (int i = 0; i < sp.length; i += 1) {
                int indext=concat.indexOf(sp[i].trim());

                // You can change the attributes as you need ... I just added a bit of color and formating
                BulletSpan bullet = new BulletSpan(30, Color.BLUE);
                strings.setSpan(bullet, indext, indext+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            return strings;
        }else{

            return new SpannableString(rt.toString());
        }

    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer2);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void sign_out() {


        DocumentReference docRef = db.collection("loginaidisc").document(user_singleton.getInstance().getfb_id());

// Remove the 'capital' field from the document
        Map<String, Object> updates = new HashMap<>();
        updates.put("token", FieldValue.delete());

        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d("fff", "DocumentSnapshot successfully written!");
                        Toast.makeText(history_page.this, "Signed out", Toast.LENGTH_LONG).show();
                        shared_pref.remove_shared_preference(history_page.this);
                        Intent intent = new Intent(history_page.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("fff", "Error writing document", e);
                        Toast.makeText(history_page.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(history_page.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(history_page.this, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(history_page.this);
                    Intent intent = new Intent(history_page.this, Login.class);
                    startActivity(intent);
                    finish();
                }else{
                    sign_out();}



            }
        });
        opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        opt.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.about_us1:

                Intent intent = new Intent(history_page.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.disclaimer:

                Intent intent1q = new Intent(history_page.this, disclaimer.class);
                startActivity(intent1q);



                break;
            case R.id.help1:

                Intent intent1 = new Intent(history_page.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.home:

                Intent ibtwd;
                if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("expert")){
                    ibtwd=new Intent(history_page.this, expertprofile_fragment.class);
                }else{ ibtwd=new Intent(history_page.this, farmersprofile_fragment1.class);}
                startActivity(ibtwd);

                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.editme:

                Intent ibt=new Intent(history_page.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(history_page.this, contributor.class);
                startActivity(ibt1);

                break;

            case R.id.history:

                Intent ibtw=new Intent(history_page.this, history.class);
                startActivity(ibtw);

                break;
            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onStart() {

        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }



//    @Override
//    public void onStop() {
//        super.onStop();
//        unregisterReceiver(internet);
//
//    }

    @Override
    public void onBackPressed() {
                super.onBackPressed();


    }
    private float getTextHeight(String text, Paint paint) {

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }
    private PdfDocument designpdf(){
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();
        Paint heading = new Paint();
        TextPaint write = new TextPaint();

        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        Canvas canvas = myPage.getCanvas();

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.greendark));
        String text="Uploaded Image";
        canvas.drawBitmap(scaledbmp, 361, 80, paint);
        canvas.drawText(text,5,text.length(),361,320,paint);
        canvas.drawBitmap(sacledimg, 361, 120, paint);







        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);
        return pdfDocument;
    }




//    private void generatePDF() {
//        // creating an object variable
//        // for our PDF document.
//        PdfDocument pdfDocument=designpdf();
//
//
//
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if(Environment.isExternalStorageManager())
//            {
//                File internal = new File("/sdcard");
//                //internalContents = internal.listFiles();
//                //Log.d("path",internal.toString());
//            }
//            else
//            {
//                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivity(permissionIntent);
//            }
//        }
//
//        String imagesDir = Environment.getExternalStoragePublicDirectory(
//                                        Environment.DIRECTORY_PICTURES).toString() + File.separator + "ai_disc_";
//                                File imageFile = new File(imagesDir);
//                                if (!imageFile.exists()) {
//                                    imageFile.mkdir();
//
//                                }
//        long tsLong = System.currentTimeMillis()/1000;
//        String ts = "ai_disc_"+ Long.toString(tsLong) +".pdf";
//                                imageFile = new File(imagesDir, ts);
//                                //fos = new FileOutputStream(imageFile);
//                                //File file = new File(sdCardRoot, "GFG.pdf");
//                                //Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpeg";
//                                //Log.d("path",file.toString());
//
//                                try {
//                                    // after creating a file name we will
//                                    // write our PDF file to that location.
//                                    pdfDocument.writeTo(new FileOutputStream(imageFile));
//
//                                    // below line is to print toast message
//                                    // on completion of PDF generation.
//                                    //Toast.makeText(history_page.this, "PDF file generated successfully."+imageFile.toString(), Toast.LENGTH_SHORT).show();
////                                    mBuilder.setContentText("Download completed")
////                                            // Removes the progress bar
////                                            .setProgress(0,0,false);
////                                    mNotifyManager.notify(id_notify, mBuilder.build());
//                                    setNotification("Download Completed, Tap to see",ts);
//
//                                } catch (IOException e) {
//                                    // below line is used
//                                    // to handle error
//                                    e.printStackTrace();
////                                    mBuilder.setContentText("Download Failed")
////                                            // Removes the progress bar
////                                            .setProgress(0,0,false);
////                                    mNotifyManager.notify(id_notify, mBuilder.build());
//                                }
//                                // after storing our pdf to that
//                                // location we are closing our PDF file.
//                                pdfDocument.close();
//
//
//
//
//    }
    private void addNotification(String msg,String imagesDir) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.done) //set icon for notification
                        .setContentTitle("AI-DISC Identification ") //set title of notification
                        .setContentText(msg)//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


//        String imagesDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES).toString() + File.separator + "ai_disc_";
        Uri uri = Uri.parse(imagesDir);
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setDataAndType(uri, "*/*");
        //startActivity(notificationIntent);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", "This is a notification message");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(history_page.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
    private void setNotification(String notificationMessage,File imagesDir) {

        String CHANNEL_ID = "my_channel_01_nott";

//**add this line**
        int requestID = (int) System.currentTimeMillis();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(history_page.NOTIFICATION_SERVICE);

        //Intent notificationIntent = new Intent(getApplicationContext(), NotificationActivity2.class);
//        String imagesDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES).toString() + File.separator + "ai_disc_/"+ts;

        File file=imagesDir;
        Intent notificationIntent;
        Uri photoURI ;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext=file.getName().substring(file.getName().indexOf(".")+1);

        String type = mime.getMimeTypeFromExtension(ext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            photoURI = FileProvider.getUriForFile(history_page.this, BuildConfig.APPLICATION_ID + ".provider", file);
             notificationIntent = new Intent(Intent.ACTION_VIEW,Uri.fromFile(file));
            notificationIntent.setDataAndType(photoURI, type);
            notificationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else{
            photoURI=Uri.fromFile(file);
            notificationIntent = new Intent(Intent.ACTION_VIEW);
            notificationIntent.setDataAndType(photoURI, type);
            notificationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }


        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);


        //Uri uri = Uri.parse(imagesDir);
        //Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        //notificationIntent.setDataAndType(uri, "*/*");
//**add this line**
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

//**edit this line to put requestID as requestCode**

        PendingIntent contentIntent ;//= PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT  );

        }
        else
        {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("AI-DISC Identification Report")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }



    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("File Access Permission");
                alertBuilder.setMessage("Permission needed to access files");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(history_page.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
               // Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(history_page.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            //generatePDF();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    //generatePDF();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}