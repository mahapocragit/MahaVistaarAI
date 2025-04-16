package in.gov.mahapocra.farmerapp.ai_disc_tp_imp.identify;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.media.RingtoneManager.getDefaultUri;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.BulletSpan;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
//import com.hsalf.smileyrating.SmileyRating;


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

import java.util.List;
import java.util.Locale;
import java.util.Random;
import in.gov.mahapocra.farmerapp.R;

import in.gov.mahapocra.farmerapp.ai_disc_tp_imp.identify.model_identify.Management_Practices_Disease;
import in.gov.mahapocra.farmerapp.ai_disc_tp_imp.identify.model_identify.disease_prediction_model1;


public class Result_identify extends AppCompatActivity {

    private static final String TAG = "Result_identify";
    int pageHeight = 2700;
    int pagewidth = 792;
    String selected_disease="";
    Bitmap bmp, scaledbmp,imgs,sacledimg,scaledlogo;
    List<Management_Practices_Disease> management_practices_diseaseList;
    private static final int PERMISSION_REQUEST_CODE = 200;
    LinearLayout mlb_layout, tlb_layout, blsb_layout, expertInputLayout,
            remarksLayout, modelPrediction, casual_Organism_Layout,
            prevalance_Layout, predisposingFunction_Layout, Symptoms_Layout, ManagementPractices_Layout, Diseases_Layout_main,pest_Layout_main;
    TextView disease_result, disease_user_text, disease_user,
            prediction_status_text,
            prediction_status1,cultural_pest_Text,
            scientificName_pest_Text,chemical_pest_Text,bilogical_pest_Text,symptoms_pest_Text, head_1, head_2;
    InternetReceiver internet;
    TextView prediction_status;
    ImageView image;
    String crop,url,path;
    Management_Practices_Disease model;
    String id ;
    String farmerIdentification, type = "";
    TextView disease_user_value, result_identify, casual_Organism_Text,
            prevalance_Text, predisposingFunction_Text, symptoms_Text, cultural_Text, chemical_Text, biological_Text;
    Button next;
    String identificationType = "", identificationId = "", crop_id = "",insectId="", message="";
    CardView casual_Organism_cv, prevalance_cv, predisposingFunction_cv, disease_Symptoms_cv, disease_ManagementPractices_cv, disease_CC_cv, disease_CHC_cv, disease_BC_cv;
    Button rate;
    public static final String  no_info= "Currently, we don't have any information regarding this";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_identify);

        setTitle("AI-DISC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        internet = new InternetReceiver();
        path="";
        id="";
        try{
            Intent intent = getIntent();
            Bundle data = intent.getExtras();
            farmerIdentification = data.getString("farmerIdentification");
            type = data.getString("type");
            crop=data.getString("crop");
            url=data.getString("url");
            path=data.getString("path");
        } catch (Exception e) {
            e.printStackTrace();
            try{
                crop = detection_instance.getInstance().getcrop();
                type=detection_instance.getInstance().gettype();
                url=detection_instance.getInstance().geturl();
                farmerIdentification = detection_instance.getInstance().getdetection();
                path=detection_instance.getInstance().getpath();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        getid();
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logocut);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 120, 120, false);
        scaledlogo = BitmapFactory.decodeResource(getResources(), R.drawable.logos);
        scaledlogo = Bitmap.createScaledBitmap(scaledlogo, 300, 120, false);
         rate=findViewById(R.id.feed);
        Button download=findViewById(R.id.download);
//        rate.setVisibility(View.GONE);
        management_practices_diseaseList = new ArrayList<>();
        Gson gson = new Gson();
        disease_prediction_model1 ob = gson.fromJson(getIntent().getStringExtra("result"), disease_prediction_model1.class);
        //Log.d("Result Page", ob.toString());
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
        result_identify = findViewById(R.id.result_identify);
        disease_result = (TextView) findViewById(R.id.disease);
        prediction_status = findViewById(R.id.prediction_status);
        image = findViewById(R.id.image);
        head_1 = findViewById(R.id.Heading);
//        disease_user_value = findViewById(R.id.disease_user);
        next = findViewById(R.id.next);
//        disease_user_text = findViewById(R.id.disease_user_text);
//        disease_user = findViewById(R.id.disease_user);
        prediction_status_text = findViewById(R.id.prediction_status_text);
        prediction_status1 = findViewById(R.id.prediction_status);
        scientificName_pest_Text= findViewById(R.id.scientificName_pest_Text);
        cultural_pest_Text= findViewById(R.id.cultural_pest_Text);
        chemical_pest_Text= findViewById(R.id.chemical_pest_Text);
        bilogical_pest_Text= findViewById(R.id.bilogical_pest_Text);
        symptoms_pest_Text= findViewById(R.id.dynamicSymptom_pest_Text);

//        head_1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_icon_2,0,0,0);

        //reading returned variables
        if(type.equals("disease")) {
            System.out.println("Category: " + type);
            pest_Layout_main.setVisibility(View.GONE);
//            get image path
            try {
                if (Uri.parse(ob.getImage_url()) != null)
                {
                    image.setImageURI(Uri.parse(ob.getImage_url()));
                    try {
                        sacledimg= MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(ob.getImage_url()));
                        sacledimg = Bitmap.createScaledBitmap(sacledimg, 400, 400, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                } else {
                    Toast.makeText(Result_identify.this, "Not found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e)
            {
                Toast.makeText(Result_identify.this, "Please upload valid image", Toast.LENGTH_LONG).show();
            }
            if (!ob.isError()) {
                System.out.println("No error");
                message = ob.getMessage();
                boolean result = ob.isResult();
                if(result){
                    System.out.println("result is true");
                    crop_id = ob.getCropId();
                    String crop_name = ob.getCropName();
                    identificationType = ob.getIdentificationType();
                    System.out.println("id type" + identificationType);
//                    ##### Disease type
                    if(identificationType.equals("Disease")){
                        String disease_id = ob.getIdentificationCode();
                        System.out.println("Disease ID: " + disease_id);
                        Diseases_Layout_main.setVisibility(View.VISIBLE);
                        result_identify.setText(message);
                        get_Management_Practices_Disease(disease_id, crop_id);

                    }
//                    ##### Healthy type
                    else if(identificationType.equals("Healthy")){
                        System.out.println("id type" + identificationType);
                        Diseases_Layout_main.setVisibility(View.GONE);
                        final android.app.AlertDialog.Builder popDialog1 = new android.app.AlertDialog.Builder(Result_identify.this);
                        popDialog1.setIcon(R.drawable.sucess_1);
                        popDialog1.setTitle("congratulations");
                        popDialog1.setMessage("Your crop looks healthy");
                        popDialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                Intent gotopage =new Intent(Result_identify.this, farmerdiseasepestIdentify.class);
//                                startActivity(gotopage);
//                                finish();

                            }
                        }).show();
                        result_identify.setText(message);

                    }
                    else if(identificationType.equals("Unidentified")){
                        System.out.println("id type" + identificationType);
                        Diseases_Layout_main.setVisibility(View.GONE);
//                        result_identify.setText("Model is not capable");
                        result_identify.setVisibility(View.GONE);
                        final android.app.AlertDialog.Builder popDialog1 = new android.app.AlertDialog.Builder(Result_identify.this);
                        popDialog1.setIcon(R.drawable.error_1);
                        popDialog1.setTitle("Sorry!!!");
                        popDialog1.setMessage("Failed to identify the disease, please try with different image");
                        popDialog1.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent gotopage =new Intent(Result_identify.this, FarmerPestDiseaseIdentify.class);
                                startActivity(gotopage);
                                finish();

                            }
                        }).show();
                    }

                }
                else {
                    Diseases_Layout_main.setVisibility(View.GONE);
//                    result_identify.setText("Sorry!! Failed to identify. Please try with another image");
                    result_identify.setVisibility(View.GONE);
                    final android.app.AlertDialog.Builder popDialog1 = new android.app.AlertDialog.Builder(Result_identify.this);
                    popDialog1.setIcon(R.drawable.error_1);
                    popDialog1.setTitle("Sorry!!!");
                    popDialog1.setMessage("Some error occurred");
                    popDialog1.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent gotopage =new Intent(Result_identify.this, FarmerPestDiseaseIdentify.class);
                            startActivity(gotopage);
                            finish();

                        }
                    }).show();
                }


            }
            else{
                final android.app.AlertDialog.Builder popDialog1 = new android.app.AlertDialog.Builder(Result_identify.this);
                popDialog1.setIcon(R.drawable.error_1);
                popDialog1.setTitle("Sorry!!!");
                popDialog1.setMessage("Some error occured");
                popDialog1.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent gotopage =new Intent(Result_identify.this, FarmerPestDiseaseIdentify.class);
                        startActivity(gotopage);
                        finish();

                    }
                }).show();

            }
        }
        else if(type.equals("pest")){
            Diseases_Layout_main.setVisibility(View.GONE);
            if(!message.isEmpty() && !String.valueOf(ob.getOne()).equals("NA"))
            {
                pest_Layout_main.setVisibility(View.VISIBLE);
                result_identify.setText(message);
                get_Management_Practices_Pest(insectId, crop_id);

/*
                            BitmapDrawable image_bitmap = (BitmapDrawable) image.getDrawable();
                            Bitmap image_bitmap1 = image_bitmap.getBitmap();
                            point a = new point();

                            a.setX(ob.getOne());
                            a.setY(ob.getTwo());
                            point b = new point();
                            b.setX(ob.getThree());
                            b.setY(ob.getTwo());


                            point c = new point();
                            c.setX(ob.getThree());
                            c.setY(ob.getFour());


                            point d = new point();
                            d.setX(ob.getOne());
                            d.setY(ob.getFour());

                            Bitmap modified1 = draw_point(image_bitmap1, a, b);
                            image.setImageBitmap(modified1);

                            Bitmap modified2 = draw_point(modified1, b, c);
                            image.setImageBitmap(modified2);

                            Bitmap modified3 = draw_point(modified2, c, d);
                            image.setImageBitmap(modified3);

                            Bitmap modified4 = draw_point(modified3, d, a);
                            image.setImageBitmap(modified4);*/
            }

            //message is available but pest is not present
            else if(!message.isEmpty() && String.valueOf(ob.getOne()).equals("NA"))
            {
                result_identify.setText(message);
                pest_Layout_main.setVisibility(View.VISIBLE);
                get_Management_Practices_Pest(insectId, crop_id);

            }

        }
        else
            {
                Diseases_Layout_main.setVisibility(View.GONE);
                pest_Layout_main.setVisibility(View.GONE);
                result_identify.setText("Sorry!! Failed to identify. Please try with another image");

        }

    /*    mlb_layout.setVisibility(View.GONE);
        tlb_layout.setVisibility(View.GONE);
        blsb_layout.setVisibility(View.GONE);*/
/*
        casual_Organism_Layout.setVisibility(View.GONE);
        prevalance_Layout.setVisibility(View.GONE);
        predisposingFunction_Layout.setVisibility(View.GONE);
        Symptoms_Layout.setVisibility(View.GONE);
        ManagementPractices_Layout.setVisibility(View.GONE);*/
//        try {
//            if (Uri.parse(ob.getImage_url()) != null)
//            {
//                image.setImageURI(Uri.parse(ob.getImage_url()));
//            } else {
//                Toast.makeText(Result_identify.this, "Not found", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e)
//        {
//            Toast.makeText(Result_identify.this, "Please upload valid image", Toast.LENGTH_LONG).show();
//        }

    rate.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        final android.app.AlertDialog.Builder popDialog = new android.app.AlertDialog.Builder(Result_identify.this);
//        SmileyRating sml=new SmileyRating(Result_identify.this);
//        LinearLayout linearLayout = new LinearLayout(Result_identify.this);
//
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        sml.setLayoutParams(lp);
//        linearLayout.addView(sml);
//        linearLayout.setGravity(Gravity.CENTER);

//
//                popDialog.setIcon(android.R.drawable.gallery_thumb);
        popDialog.setTitle("How useful is it to you?");

        //add linearLayout to dailog
//        popDialog.setView(linearLayout);
        popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                SmileyRating.Type smiley = sml.getSelectedSmiley();
// You can compare it with rating Type

                // You can get the user rating too
                // rating will between 1 to 5, but -1 is none selected
//                int rating = smiley.getRating();
//                if (rating!=-1){
//
//                ratings(rating,id);}
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


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent gotopage=new Intent(Result_identify.this, FarmerPestDiseaseIdentify.class);

                startActivity(gotopage);
                finish();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
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
                }}
                catch(Exception e){}
            }
        });

//        boolean result = ob.isResult();
//        String message = ob.getMessage();
//        insectId=ob.getInstId();
//
//
//      try
//      {
//          if(identificationType!=null)
//          {
//              identificationType = ob.getIdentificationType().toLowerCase();
//          }
//          else
//          {
//              identificationType = null;
//
//          }
//
//          //  identificationType = ob.getIdentificationType().toLowerCase();
//          identificationId = ob.getIdentificationCode();
//      }
//      catch (NullPointerException e)
//      {
//
//      }
//
//        //  String disease_code1 = ob.getDisease_code();
//        cropId = ob.getCropId();
//        boolean has_model_variable = ob.isHas_model_variable();
//
//        System.out.println("message " + message);
//
//        System.out.println("inectId123" + identificationType + insectId +  cropId);
//
//
//       /* if(type.equals("pest"))
//        {
//
//        }
//        else if(type.equals("disease"))
//        {
//            get_Management_Practices_Disease();
//        }
//*/
//
//        if (!ob.isError()) {
//
//
//            if (result) {
//
//                //  reset();
//
//                if (has_model_variable)
//                {
//
//                    result_identify.setText("Model is not present.Image is sent to expert");
//                } else {
//
//                    //message is empty and pest is not available
//                    if (message.isEmpty() && !ob.isPest_present()) {
//                        // show_dialog(message);
//
//                        //  disease_result.setText("Healthy");
//                        result_identify.setText("No suitable match found");
//
//
//                    }
//
//                    else if (identificationType.equals("healthy"))
//                    {
//
//                          /*  disease_user_text.setVisibility(View.GONE);
//                            disease_user.setVisibility(View.GONE);
//                            prediction_status_text.setVisibility(View.GONE);
//                            prediction_status1.setVisibility(View.GONE);
//                            modelPrediction.setVisibility(View.GONE);*/
//                        Diseases_Layout_main.setVisibility(View.GONE);
//                        result_identify.setText(message);
//
//
//
//                    }
//
//
//                    else if (identificationType.equals("disease"))
//                    {
//
//                          /*  disease_user_text.setVisibility(View.GONE);
//                            disease_user.setVisibility(View.GONE);
//                            prediction_status_text.setVisibility(View.GONE);
//                            prediction_status1.setVisibility(View.GONE);
//                            modelPrediction.setVisibility(View.GONE);*/
//                        Diseases_Layout_main.setVisibility(View.VISIBLE);
//                        result_identify.setText(message);
//
//
//                        get_Management_Practices_Disease(identificationId, cropId);
//
//                    }
//
//                    else if(identificationType.equals("pest"))
//                    {
//                        //Both message and pest are available
//                        if(!message.isEmpty() && !String.valueOf(ob.getOne()).equals("NA"))
//                        {
//                            pest_Layout_main.setVisibility(View.VISIBLE);
//                            result_identify.setText(message);
//                            get_Management_Practices_Pest(insectId, cropId);
//
///*
//                            BitmapDrawable image_bitmap = (BitmapDrawable) image.getDrawable();
//                            Bitmap image_bitmap1 = image_bitmap.getBitmap();
//                            point a = new point();
//
//                            a.setX(ob.getOne());
//                            a.setY(ob.getTwo());
//                            point b = new point();
//                            b.setX(ob.getThree());
//                            b.setY(ob.getTwo());
//
//
//                            point c = new point();
//                            c.setX(ob.getThree());
//                            c.setY(ob.getFour());
//
//
//                            point d = new point();
//                            d.setX(ob.getOne());
//                            d.setY(ob.getFour());
//
//                            Bitmap modified1 = draw_point(image_bitmap1, a, b);
//                            image.setImageBitmap(modified1);
//
//                            Bitmap modified2 = draw_point(modified1, b, c);
//                            image.setImageBitmap(modified2);
//
//                            Bitmap modified3 = draw_point(modified2, c, d);
//                            image.setImageBitmap(modified3);
//
//                            Bitmap modified4 = draw_point(modified3, d, a);
//                            image.setImageBitmap(modified4);*/
//                        }
//
//                        //message is available but pest is not present
//                        else if(!message.isEmpty() && String.valueOf(ob.getOne()).equals("NA"))
//                        {
//                            result_identify.setText(message);
//                            pest_Layout_main.setVisibility(View.VISIBLE);
//                            get_Management_Practices_Pest(insectId, cropId);
//
//                        }
//                    }
///*
//                        //message is available but pest is not present
//
//                        else if (!message.isEmpty() && !ob.isPest_present())
//                        {
//                            disease_result.setText(message);
//
//
//                            if (message.toLowerCase().equals(ob.getDisease_name().toLowerCase())) {
//                                prediction_status.setText("Prediction correct");
//
//
//                            } else {
//
//                                prediction_status.setText("Prediction not correct");
//
//                            }
//
//
//                            if (message.equalsIgnoreCase(Identify_disease.mlb)) {
//                                mlb_layout.setVisibility(View.VISIBLE);
//
//                            } else if (message.equalsIgnoreCase(Identify_disease.tlb)) {
//                                tlb_layout.setVisibility(View.VISIBLE);
//
//                            } else if (message.equalsIgnoreCase(Identify_disease.blsb)) {
//                                blsb_layout.setVisibility(View.VISIBLE);
//
//                            }
//                        }
//
//                        //message is empty and pest is available
//
//                        else if (message.isEmpty() && ob.isPest_present())
//                        {
//
//                            Log.d(TAG, "onCreate: inside ");
//
//                            BitmapDrawable image_bitmap = (BitmapDrawable) image.getDrawable();
//                            Bitmap image_bitmap1 = image_bitmap.getBitmap();
//                            point a = new point();
//
//                            a.setX(ob.getOne());
//                            a.setY(ob.getTwo());
//                            point b = new point();
//                            b.setX(ob.getThree());
//                            b.setY(ob.getTwo());
//
//
//                            point c = new point();
//                            c.setX(ob.getThree());
//                            c.setY(ob.getFour());
//
//
//                            point d = new point();
//                            d.setX(ob.getOne());
//                            d.setY(ob.getFour());
//
//                            Bitmap modified1 = draw_point(image_bitmap1, a, b);
//                            image.setImageBitmap(modified1);
//
//                            Bitmap modified2 = draw_point(modified1, b, c);
//                            image.setImageBitmap(modified2);
//
//                            Bitmap modified3 = draw_point(modified2, c, d);
//                            image.setImageBitmap(modified3);
//
//                            Bitmap modified4 = draw_point(modified3, d, a);
//                            image.setImageBitmap(modified4);
//
//                        }
//
//
//                    //message and pest is available
//
//                    else if (!message.isEmpty() && ob.isPest_present()) {
//
//                        Log.d(TAG, "onCreate: inside ");
//
//                        BitmapDrawable image_bitmap = (BitmapDrawable) image.getDrawable();
//                        Bitmap image_bitmap1 = image_bitmap.getBitmap();
//                        point a = new point();
//
//                        a.setX(ob.getOne());
//                        a.setY(ob.getTwo());
//                        point b = new point();
//                        b.setX(ob.getThree());
//                        b.setY(ob.getTwo());
//
//
//                        point c = new point();
//                        c.setX(ob.getThree());
//                        c.setY(ob.getFour());
//
//
//                        point d = new point();
//                        d.setX(ob.getOne());
//                        d.setY(ob.getFour());
//
//                        Bitmap modified1 = draw_point(image_bitmap1, a, b);
//                        image.setImageBitmap(modified1);
//
//                        Bitmap modified2 = draw_point(modified1, b, c);
//                        image.setImageBitmap(modified2);
//
//                        Bitmap modified3 = draw_point(modified2, c, d);
//                        image.setImageBitmap(modified3);
//
//                        Bitmap modified4 = draw_point(modified3, d, a);
//                        image.setImageBitmap(modified4);
//
//                    }*/
//
//
//                }
//
//// For farmer
//               /* if (farmerIdentification != null)
//                {
//                    disease_user_text.setVisibility(View.GONE);
//                    disease_user.setVisibility(View.GONE);
//                    prediction_status_text.setVisibility(View.GONE);
//                    prediction_status1.setVisibility(View.GONE);
//                     modelPrediction.setVisibility(View.GONE);
//*//*
//                    //For type disease
//                    if (!message.isEmpty() && type.equals("disease")) {
//                        result_identify.setText(message *//**//*+ " " +  " disease detected"*//**//*);
//                    }
//
//                    //For type Pest
//
//                    else if (!message.isEmpty() && type.equals("pest")) {
//                        result_identify.setText(message *//**//*+ " " +  " pest detected"*//**//*);
//                    }*//*
//
//
//
//
//                }*/
//
//
//            } else {
//                //   Toast.makeText(MainActivity.this, "username  and password is not correct.", Toast.LENGTH_LONG).show();
//                // Log.d(TAG, "onResponse: identification failed");
//                //  show_dialog("Identification failed.");
//                result_identify.setText("Sorry!! The disease could not be identified. You can try again or consult with Experts");
//                if (farmerIdentification != null) {
////                    disease_user_text.setVisibility(View.GONE);
////                    disease_user.setVisibility(View.GONE);
////                    prediction_status_text.setVisibility(View.GONE);
////                    prediction_status1.setVisibility(View.GONE);
////                    Diseases_Layout_main.setVisibility(View.GONE);
////                    expertInputLayout.setVisibility(View.GONE);
////                    remarksLayout.setVisibility(View.GONE);
//
//                }
//            }
//
//        } else {
//
//            result_identify.setText("Sorry!! Failed to identify. Please try with another image");
//        }


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
       // title.setColor(ContextCompat.getColor(this, R.color.colorMaroon));


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
        canvas.drawText(crop+" ", 300, writ, infoline);
        writ+=35;
        canvas.drawText("Disease Name", 10, writ, infoheader);
        canvas.drawText(selected_disease+" ", 300, writ, infoline);
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
        String filc="ai-disc_"+crop+formattedDate+"_"+String.valueOf(df);

        String imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + folderName;
        imageFile = new File(imagesDir);


        if (!imageFile.exists()) {
            imageFile.mkdir();

        }
        imageFile = new File(imagesDir, filc + ".pdf");
        String imn=imagesDir+"/"+filc + ".pdf";


        try {

            pdfDocument.writeTo(new FileOutputStream(imageFile));

            Toast.makeText(Result_identify.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
            setNotification("Your report has been generated ! Find at Downloads/AI-DISC/",imageFile);
        } catch (IOException e) {

            e.printStackTrace();
        }

        pdfDocument.close();
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
                android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("File Access Permission");
                alertBuilder.setMessage("Permission needed to access files");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(Result_identify.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                android.app.AlertDialog alert = alertBuilder.create();
                alert.show();
               // Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(Result_identify.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            //generatePDF();
        }
    }
    private void ratings(int ratd,String id){
        JSONObject object1 = new JSONObject();
        try {
            object1.put("id", id);
            object1.put("rate", ratd);

        } catch (JSONException e) {
            e.printStackTrace();
        }
      //  Log.d("kkkk",object1.toString());
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/setrate")
                .addJSONObjectBody(object1)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        boolean res=false;

                        res=jsonObject.optBoolean("result");
                        if (res){
                            Toast.makeText(Result_identify.this, "Thank you for your feedback.", Toast.LENGTH_SHORT).show();
                            rate.setVisibility(View.GONE);

                        }


                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });


    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder poy=new AlertDialog.Builder(Result_identify.this);
        poy.setTitle("Do you want to go back ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent gotopage=new Intent(Result_identify.this,farmersprofile_fragment1.class);
//
//                        startActivity(gotopage);
//                        finish();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();



       /* Intent intent=new Intent(Result_identify.this, Identify_disease.class);
        startActivity(intent);*/
    }
    void getid(){
        String[] sty=path.split("/");

        JSONObject jo = new JSONObject();
        try {

            jo.put("inout", sty[sty.length-1]);


        } catch (JSONException E) {

        }
       // Log.d("kkkk1",jo.toString());
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_identification")
                .addJSONObjectBody(jo)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                             id=response.getString("inout");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
//    public Bitmap draw_point(Bitmap src, point from, point to) {
//
//
//        Bitmap tempBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.RGB_565);
//        Canvas tempCanvas = new Canvas(tempBitmap);
//
////Draw the image bitmap into the cavas
//        tempCanvas.drawBitmap(src, 0, 0, null);
//
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        //myPaint.setColor(Color.TRANSPARENT);
//        // myPaint.
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(15);
////Draw everything else you want into the canvas, in this example a rectangle with rounded edges
//        // tempCanvas.drawRoundRect(new RectF(40,40,400,400), 50, 50, paint);
//        // tempCanvas.drawPoint(x, y, paint);
//
//
//        Paint paint_line = new Paint(Paint.ANTI_ALIAS_FLAG);
//        //myPaint.setColor(Color.TRANSPARENT);
//        // myPaint.
//        paint_line.setStyle(Paint.Style.STROKE);
//        paint_line.setColor(Color.RED);
//        paint_line.setStrokeWidth(15);
//
//        point po1 = from;
//        point po2 = to;
//        tempCanvas.drawLine(po1.getX(), po1.getY(), po2.getX(), po2.getY(), paint_line);
//
//
//        return tempBitmap;
//
//
//    }


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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                break;
            default:
                break;

        }

        return true;
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

                                System.out.println("responseDataaaa" + response.toString());


                            }

                            System.out.println("sizzzeeeee" + management_practices_diseaseList.size());
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
                                        scientificName_pest_Text.setText(no_info);
                                    }


                                    if (management_practices_diseaseList.get(i).getDamage_symptoms() != null && !management_practices_diseaseList.get(i).getDamage_symptoms().equals("NA") && !management_practices_diseaseList.get(i).getDamage_symptoms().isEmpty()) {
                                        symptoms_pest_Text.setText(management_practices_diseaseList.get(i).getDamage_symptoms());

                                    } else {
                                        symptoms_pest_Text.setText(no_info);
                                    }


                                    if (management_practices_diseaseList.get(i).getCultural_Text() != null && !management_practices_diseaseList.get(i).getCultural_Text().equals("NA") && !management_practices_diseaseList.get(i).getCultural_Text().isEmpty()) {
                                        cultural_pest_Text.setText(management_practices_diseaseList.get(i).getCultural_Text());

                                    } else {
                                        cultural_pest_Text.setText(no_info);
                                    }
                                    if (management_practices_diseaseList.get(i).getChemical_Text() != null && !management_practices_diseaseList.get(i).getChemical_Text().equals("NA") && !management_practices_diseaseList.get(i).getChemical_Text().isEmpty()) {
                                        chemical_pest_Text.setText(management_practices_diseaseList.get(i).getChemical_Text());

                                    } else {
                                        chemical_pest_Text.setText(no_info);
                                    }
                                    if (management_practices_diseaseList.get(i).getBiological_Text() != null && !management_practices_diseaseList.get(i).getBiological_Text().equals("NA")&& !management_practices_diseaseList.get(i).getBiological_Text().isEmpty()) {
                                        bilogical_pest_Text.setText(management_practices_diseaseList.get(i).getBiological_Text());

                                    } else {
                                        bilogical_pest_Text.setText(no_info);
                                    }


                                }
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(Result_identify.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void get_Management_Practices_Disease(String identificationId, String cropId)
    {
        //selected_disease=identificationId;
        management_practices_diseaseList.clear();
        JSONObject jo = new JSONObject();
        try {

            jo.put("cropId", cropId);
            jo.put("diseaseId", identificationId);

            System.out.println("jsonnnnn" + jo.toString());

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
                                model.setdisease(object.optString("disease_name"));
                                selected_disease=object.optString("disease_name");

                                model.setcrop(object.optString("crop_name"));
                                crop=object.optString("crop_name");
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
                                    casual_Organism_Text.setText(no_info);
                                }


                                if (management_practices_diseaseList.get(i).getPrevalance_Text() != null && !management_practices_diseaseList.get(i).getPrevalance_Text().equals("NA")) {
                                    prevalance_Text.setText(management_practices_diseaseList.get(i).getPrevalance_Text());

                                } else {
                                    prevalance_Text.setText(no_info);
                                }

                                if (management_practices_diseaseList.get(i).getPredisposingFunction_Text() != null) {
                                    if (management_practices_diseaseList.get(i).getPredisposingFunction_Text().equals("NA")) {
                                        predisposingFunction_Text.setText(no_info);


                                    } else {
                                        predisposingFunction_Text.setText(management_practices_diseaseList.get(i).getPredisposingFunction_Text());
                                    }

                                } else {
                                    predisposingFunction_Text.setText(no_info);
                                }
                                if (management_practices_diseaseList.get(i).getSymptoms() != null && !management_practices_diseaseList.get(i).getSymptoms().equals("NA")) {
                                    symptoms_Text.setText(splitmerge(management_practices_diseaseList.get(i).getSymptoms()));

                                    System.out.println("rtrttr" + management_practices_diseaseList.get(i).getSymptoms());

                                } else {
                                    symptoms_Text.setText(no_info);
                                }
                                if (management_practices_diseaseList.get(i).getCultural_Text() != null && !management_practices_diseaseList.get(i).getCultural_Text().equals("NA")) {
                                    cultural_Text.setText(splitmerge(management_practices_diseaseList.get(i).getCultural_Text()));

                                } else {
                                    cultural_Text.setText(no_info);
                                }
                                if (management_practices_diseaseList.get(i).getChemical_Text() != null && !management_practices_diseaseList.get(i).getChemical_Text().equals("NA")) {
                                    chemical_Text.setText(splitmerge(management_practices_diseaseList.get(i).getChemical_Text()));

                                } else {
                                    chemical_Text.setText(no_info);
                                }
                                if (management_practices_diseaseList.get(i).getBiological_Text() != null && !management_practices_diseaseList.get(i).getBiological_Text().equals("NA")) {
                                    biological_Text.setText(splitmerge(management_practices_diseaseList.get(i).getBiological_Text()));

                                } else {
                                    biological_Text.setText(no_info);
                                }

                            }

                            if(management_practices_diseaseList.size()==0)
                            {
                                Diseases_Layout_main.setVisibility(View.GONE);
                                Toast.makeText(Result_identify.this, "No management practices found for this disease", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        final android.app.AlertDialog.Builder popDialog1 = new android.app.AlertDialog.Builder(Result_identify.this);
                        popDialog1.setIcon(R.drawable.error_1);
                        popDialog1.setTitle("Sorry!!!");
                        popDialog1.setMessage("Some error occurred, please try again");
                        popDialog1.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent gotopage =new Intent(Result_identify.this, FarmerPestDiseaseIdentify.class);
                                startActivity(gotopage);
                                finish();

                            }
                        }).show();

                        Toast.makeText(Result_identify.this, "Error", Toast.LENGTH_LONG).show();
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
            if(i==sp.length-1){rt.append(String.valueOf(i + 1)).append(". ").append(sp[i].trim());
            }else{
            rt.append(String.valueOf(i + 1)).append(". ").append(sp[i].trim()).append("\n\n");}
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
    private void setNotification(String notificationMessage,File imagesDir) {

        String CHANNEL_ID = "my_channel_01nov";

//**add this line**
        int requestID = (int) System.currentTimeMillis();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(history_page.NOTIFICATION_SERVICE);

        //Intent notificationIntent = new Intent(getApplicationContext(), NotificationActivity2.class);
//        String imagesDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES).toString() + File.separator + "ai_disc_/"+ts;

//        File file=imagesDir;
//        Uri photoURI = FileProvider.getUriForFile(Result_identify.this, this.getApplicationContext().getPackageName() + ".provider", file);
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        String ext=file.getName().substring(file.getName().indexOf(".")+1);
//        //Log.d("ssssss",ext);
//        String type = mime.getMimeTypeFromExtension(ext);
//        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
//        notificationIntent.setDataAndType(photoURI, type);
//        notificationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
///////////////////////////
        File file=imagesDir;
        Intent notificationIntent;
        Uri photoURI ;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext=file.getName().substring(file.getName().indexOf(".")+1);

        String type = mime.getMimeTypeFromExtension(ext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            photoURI = FileProvider.getUriForFile(Result_identify.this, BuildConfig.APPLICATION_ID + ".provider", file);
//            notificationIntent = new Intent(Intent.ACTION_VIEW,Uri.fromFile(file));
//            notificationIntent.setDataAndType(photoURI, type);
//            notificationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else{
            photoURI=Uri.fromFile(file);
            notificationIntent = new Intent(Intent.ACTION_VIEW);
            notificationIntent.setDataAndType(photoURI, type);
            notificationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        //Uri uri = Uri.parse(imagesDir);
        //Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        //notificationIntent.setDataAndType(uri, "*/*");
//**add this line**
      //  notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

//**edit this line to put requestID as requestCode**

//        PendingIntent contentIntent ;//= PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_ONE_SHOT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            contentIntent = PendingIntent.getActivity
//                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT  );
//
//        }
//        else
//        {
//            contentIntent = PendingIntent.getActivity
//                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT );
//
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.app_name);
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
//            assert mNotificationManager != null;
//            mNotificationManager.createNotificationChannel(mChannel);
//        }
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("AI-DISC Identification Report")
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(notificationMessage))
//                .setContentText(notificationMessage).setAutoCancel(true);
//        mBuilder.setSound(alarmSound);
//        mBuilder.setChannelId(CHANNEL_ID);
//        mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(0, mBuilder.build());

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case android.R.id.home:
//
//                finish();
//
//
//                break;
//            case R.id.logout:
//
//                Intent intent = new Intent(Result_identify.this, Login.class);
//                startActivity(intent);
//                finish();
//
//                shared_pref.remove_shared_preference(Result_identify.this);
//
//                break;
//
//            default:
//                break;
//
//        }
//
//        return true;
//    }
}