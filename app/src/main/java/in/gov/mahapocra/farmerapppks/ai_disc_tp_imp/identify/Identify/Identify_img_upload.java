package in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.Identify;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;


import android.view.View;
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
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.detect_ins;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.dis_predct_model;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.user;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.util.Cam;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.util.IntReceiver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Identify_img_upload extends AppCompatActivity {

    private static final String TAG = "Identify_disease";
    String crop = "";
    String user_id = "";

    String farmerIdentificationString="";
    String type="";
    String url="";
    Bitmap bitmap;
    LinearLayout lin1;
    LinearLayout headview;
    IntReceiver internet;
    String selected_disease = "";
    TextView title_severity, warning,warnbox;
    LinearLayout severity_layout,disease_layout;
    TextView title_disease;
    Button take_photo, select_from_gallery,another;
    ImageView image;////////////////////////////////////////
    ProgressDialog progressDialog;
    Button upload;
    String file_path = "";
    String file_path_string = "";
    String url_model = "";
    CardView img_cv;
    private String urls;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }
    String selected_severity_level = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_img_upload);

        setTitle("AI-DISC");
     //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img_cv = findViewById(R.id.img_card_view);

        internet = new IntReceiver();

        lin1 = (LinearLayout) findViewById(R.id.linlay1);
        headview=findViewById(R.id.heading1);
        TextView crppp=findViewById(R.id.crppp);
        warnbox=findViewById(R.id.warning);
        warnbox.setVisibility(View.GONE);

        try{
            Intent intent = getIntent();
            Bundle data = intent.getExtras();
            crop = data.getString("crop");
            farmerIdentificationString=data.getString("farmerIdentification");
            type=data.getString("type");
            url=data.getString("url");
            crppp.setText("Crop : "+crop);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                crop = detect_ins.getInstance().getcrop();
                type=detect_ins.getInstance().gettype();
                url=detect_ins.getInstance().geturl();
                farmerIdentificationString = detect_ins.getInstance().getdetection();
                crppp.setText("Crop : "+crop);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        if(farmerIdentificationString!=null )
        {

        }
        take_photo = (Button) findViewById(R.id.button);
        select_from_gallery = (Button) findViewById(R.id.button1);
        another=findViewById(R.id.tryanother);
        another.setVisibility(View.GONE);
        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotopage =new Intent(Identify_img_upload.this, Identify_img_upload.class);
                startActivity(gotopage);
                finish();

            }
        });
        warning=findViewById(R.id.warningmsg);
        warning.setVisibility(View.GONE);
        image = (ImageView) findViewById(R.id.image);


        upload = (Button) findViewById(R.id.upload);
        progressDialog=new ProgressDialog(Identify_img_upload.this);
        user_id = user.getInstance().getUser_id();
        upload.setVisibility(View.GONE);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        upload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {

                progressDialog.setCancelable(false);
                progressDialog.setMessage("Uploading..");
                progressDialog.show();
                upload.setEnabled(false);
                ///////////////// to add google identifier of crop

                url_model="https://aidisc.krishimegh.in:32517/"+url;
                Log.d(TAG, "upload onClick: "+url_model);

                Observable.fromCallable(() -> {

                            // System.out.println(" inside from callable is:" + Thread.currentThread().getName());
                            // upload_image_data(file_path,upload,progressBar);

                            converting_string(file_path);

                            return false;
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((result) ->
                        {
                            if (bitmap!=null){
                                InputImage image = InputImage.fromBitmap(bitmap,0 );
                                ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
                                labeler.process(image).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                                            @Override
                                            public void onSuccess(List<ImageLabel> imageLabels) {
                                                //Log.d("Google Model Output:",imageLabels.getClass().getCanonicalName());
                                                // Log.d("Google Model Output:",imageLabels.toString());
                                                ArrayList<String> obj=new ArrayList<>();
                                                Map<String,Float> myMap = new HashMap<>();
                                                ArrayList<Float> scores = new ArrayList<>();
                                                for (ImageLabel label : imageLabels) {
                                                    String text = label.getText();
                                                    Float conf = label.getConfidence();
                                                    System.out.println(text+" : "+ conf);
                                                    myMap.put(text,conf);
                                                    obj.add(text);
                                                }
                                                // Log.d("Google Model Output:",obj.toString());
                                                if (!obj.isEmpty()) {
                                                    //Log.d(TAG, "not empty");
                                                    if (obj.contains("Plant") || obj.contains("Vegetable") || obj.contains("Fruit") || obj.contains("Insect")) {
                                                        System.out.println("confidence: "+myMap.get("Plant"));
                                                        if (obj.contains("Forest") || obj.contains("Jungle") || obj.contains("Garden") || obj.contains("Field")) {
                                                            //Log.d(TAG,obj.toString());
                                                            progressDialog.cancel();
                                                            afterJungleForestimage();
                                                        } else {
                                                            upload_image_data(url_model, user_id, selected_disease, selected_severity_level, type);
                                                        }
                                                    } else {
                                                        progressDialog.cancel();
                                                        afterfalseimage();

                                                    }
                                                } else {
                                                    progressDialog.cancel();
                                                    afterNoObj();
                                                }

                                            }
                                        })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Toast.makeText(farmerdiseasepestIdentify.this, "Error !!! ", Toast.LENGTH_LONG).show();

                                                progressDialog.cancel();
                                                afterfalseimage();
                                            }
                                        });
                            }
                            //Use result for something
                            //
                        }


                        );
               // progressDialog.cancel();
            }
        });

        take_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {
                    Intent intent = new Intent(Identify_img_upload.this, Cam.class);
                    startActivityForResult(intent, 4);
                } else {
                    if (checking_permision()) {
                        Intent intent = new Intent(Identify_img_upload.this, Cam.class);
                        startActivityForResult(intent, 4);

                    }
                }
            }
        });


        select_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), 1);
                } else {
                    if (ContextCompat.checkSelfPermission(Identify_img_upload.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Identify_img_upload.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), 1);
                    }


                }}
        });

    }


    private void afterfalseimage(){
        upload.setVisibility(View.GONE);
        lin1.setVisibility(View.GONE);
        headview.setVisibility(View.GONE);
        warning.setVisibility(View.GONE);
        warnbox.setVisibility(View.GONE);
        another.setVisibility(View.GONE);

        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(Identify_img_upload.this);
        popDialog1.setIcon(R.drawable.error_1);
        popDialog1.setTitle("WARNING!!");
        popDialog1.setMessage("Your image doesn't contain any plant parts. Please try again with appropriate images");
        popDialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gotopage =new Intent(Identify_img_upload.this, Identify_img_upload.class);
                startActivity(gotopage);
                finish();

            }
        }).show();
    }

    private void afterJungleForestimage(){

        upload.setVisibility(View.GONE);
        lin1.setVisibility(View.GONE);
        headview.setVisibility(View.GONE);
        warning.setVisibility(View.GONE);
        warnbox.setVisibility(View.GONE);
        another.setVisibility(View.GONE);
        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(Identify_img_upload.this);
        popDialog1.setIcon(R.drawable.error_1);
        popDialog1.setTitle("WARNING!!");
        popDialog1.setMessage("Objects in the image are too far. Try to capture the symptomatic region of crop within the rectangular box");
        popDialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gotopage =new Intent(Identify_img_upload.this, Identify_img_upload.class);
                startActivity(gotopage);
                finish();

            }
        }).show();
    }

    private void afterNoObj(){
        upload.setVisibility(View.GONE);
        lin1.setVisibility(View.GONE);
        headview.setVisibility(View.GONE);
        warning.setVisibility(View.GONE);
        warnbox.setVisibility(View.GONE);
        another.setVisibility(View.GONE);
        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(Identify_img_upload.this);
        popDialog1.setIcon(R.drawable.error_1);
        popDialog1.setTitle("WARNING!!");
        popDialog1.setMessage("Objects in the image are too close. Try to capture the symptomatic region of crop within the rectangular box");
        popDialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gotopage =new Intent(Identify_img_upload.this, Identify_img_upload.class);
                startActivity(gotopage);
                finish();

            }
        }).show();
    }

    public boolean checking_permision() {
        if ((ContextCompat.checkSelfPermission(Identify_img_upload.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(Identify_img_upload.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Identify_img_upload.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return false;

        }
        return true;
    }

    public void converting_string(String file_path) {

        if (!file_path.isEmpty()) {
            Uri address_to_convert = Uri.parse(file_path);
            System.out.println("Uri is:" + address_to_convert);
            try {

                InputStream inputstream = null;
                inputstream = getContentResolver().openInputStream(address_to_convert);

                System.out.println(" input stream is :");
                int length_image = 0;

                length_image = inputstream.available();
                byte[] data_in_byte_image = new byte[length_image];
                inputstream.read(data_in_byte_image);
                inputstream.close();
                System.out.println(" after inputstream close");
                file_path_string = "";
                file_path_string = Base64.encodeToString(data_in_byte_image, Base64.DEFAULT);
                // System.out.println("image in string :" +file_path_string);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Identify_img_upload.this, "Error in converting ", Toast.LENGTH_LONG).show();
                file_path_string = "";
            }
        } else {
           // System.out.println("...");
        }
    }

    private void upload_image_data(String url, String user_id, String selected_disease, String selected_severity_level, String type)
    {
        urls=url;
        if (file_path_string.isEmpty())
        {
            upload.setEnabled(true);
            progressDialog.cancel();
            return;
        }

        String image_proper = file_path_string.replaceAll("\n", "");
        System.out.println("imageFile "  + "  " + image_proper);
        String paths="";

        JSONObject object = new JSONObject();
        try {
           /* object.put("image_file", image_proper);
            object.put("crop_name", crop);*/

            object.put("crop_name", crop);
            object.put("image_file", image_proper);
            object.put("user_id", "9010");
            object.put("lat", "17.40");
            object.put("long", "19.30");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("https://aidisc.krishimegh.in:32517/detect_crops")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        upload.setEnabled(true);
                        String paths;
                        progressDialog.cancel();
                        try {
                            paths=response.optString("image_path");
                            if (paths.matches("")){
                                Toast.makeText(Identify_img_upload.this, "Failed at Crop Detection", Toast.LENGTH_LONG).show();
                            }else {
                                if (response.optBoolean("result")) {
                                    upload_image_data_aftercrop(urls, user_id, selected_disease, selected_severity_level, type, paths);
                                } else {
                                    String crops = response.optString("crop_name");
                                    final int[] checkedItem = {-1};
                                    final String[] selectedText = {""};
                                    final String[] listItems = new String[]{ "YES","NO" };
                                    ArrayList<Integer> slist = new ArrayList();

                                    final AlertDialog.Builder popDialog = new AlertDialog.Builder(Identify_img_upload.this);
                                    popDialog.setIcon(R.drawable.errro_2);
                                    popDialog.setTitle("Warning ! The image may not contain "+crop+" crop");
                                    popDialog.setMessage("Proceeding with the selected crop may give wrong detection result.\nStill if you want to proceed press YES or to try with another image press NO. ");
                                    popDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            upload_image_data_aftercrop(urls, user_id, selected_disease, selected_severity_level, type, paths);
                                        }
                                    });
                                    popDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent gotopage =new Intent(Identify_img_upload.this, Identify_img_upload.class);
                                            startActivity(gotopage);
                                            finish();
                                        }
                                    });
                                    popDialog.show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.cancel();
                        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(Identify_img_upload.this);
                        popDialog1.setIcon(R.drawable.error_1);
                        popDialog1.setTitle("Sorry!!!");
                        popDialog1.setMessage("Some error occurred");
                        popDialog1.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent gotopage =new Intent(Identify_img_upload.this, Identify_img_upload.class);
                                startActivity(gotopage);
                                finish();
                            }
                        }).show();
                    }
                });
    }

    private void upload_image_data_aftercrop(String url_model, String user_id, String selected_disease, String selected_severity_level, String type,String path) {
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            /*object.put("image_path", path);
            object.put("user_id", user_id);
            object.put("disease_name", selected_disease);
            object.put("severity", selected_severity_level);*/

            object.put("image_path", path);
            object.put("user_id", "9010");
            object.put("lat", "17.400");
            object.put("long", "19.30");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        AndroidNetworking.post(url_model)
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        upload.setEnabled(true);
                        progressDialog.cancel();
                        if (type.equals("pest")){
                            pestCalling(response);
                        }
                        else
                        {

                            dis_predct_model model = new dis_predct_model();
                            model.setError(false);
                            model.setDisease_name(selected_disease);
                            model.setImage_url(file_path);
                            model.setMessage(response.optString("message"));
                            model.setResult(response.optBoolean("result"));

                            if(response.optBoolean("result")){
                                model.setCropId(response.optString("c_id"));
                                model.setCropName(response.optString("c_name"));
                                model.setIdentificationCode(response.optString("ds_id"));
                                model.setIdentificationType(response.optString("identification_type"));
                                model.setConfidence(response.optString("probability1"));
                                System.out.println("result_crop" + response.toString());
                            }
                            else {
                                model.setErrorval(response.optString("error_msg"));
                                model.setMessage(response.optString("message"));


                            }
                            detect_ins.getInstance().setpath(path);
                            Gson gson = new Gson();
                            String myJson = gson.toJson(model);

                            Intent intent = new Intent(Identify_img_upload.this, Result_identified.class);//Show result
                            Bundle data1 = new Bundle();
                            data1.putString("farmerIdentification",farmerIdentificationString);


                            data1.putString("crop",crop);
                            data1.putString("type",type);
                            data1.putString("url",url);
                            data1.putString("path",path);
                            intent.putExtras(data1);
                            intent.putExtra("result", myJson);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        System.out.println("Category: " + type);
                        progressDialog.cancel();
                        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(Identify_img_upload.this);
                        popDialog1.setIcon(R.drawable.error_1);
                        popDialog1.setTitle("Sorry!!!");
                        popDialog1.setMessage("No model for this crop");
                        popDialog1.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent gotopage =new Intent(Identify_img_upload.this, Identify_img_upload.class);
                                startActivity(gotopage);
                                finish();

                            }
                        }).show();


                        // Log.d(TAG, "onError: ");


                    }
                });

    }

    public void pestCalling(JSONObject response)
    {
        dis_predct_model model = new dis_predct_model();
        model.setResult(response.optBoolean("result"));
        model.setMessage(response.optString("message"));
        model.setDisease_code(response.optString("result"));
        model.setHas_model_variable(response.has("model"));
        model.setPest_present(response.has("one"));
        model.setOne(response.optInt("one"));
        model.setTwo(response.optInt("two"));
        model.setThree(response.optInt("three"));
        model.setFour(response.optInt("four"));
        model.setCropName(response.optString("c_name"));
        model.setCropId(response.optString("c_id"));
        model.setIdentificationType(response.optString("identification_type"));
        model.setInstId(response.optString("inst_id"));

        model.setError(false);
        model.setDisease_name(selected_disease);
        model.setImage_url(file_path);

        Gson gson = new Gson();
        String myJson = gson.toJson(model);

        System.out.println("datafound" + myJson);
//        Intent intent = new Intent(Identify_img_upload.this, Result_identified.class);
//        Bundle data1 = new Bundle();
//        data1.putString("farmerIdentification", farmerIdentificationString);
//        intent.putExtras(data1);
//        intent.putExtra("result", myJson);
//        intent.putExtra("crop",crop);
//        intent.putExtra("type",type);
//        intent.putExtra("url",url);
//        intent.putExtra("type",type);
//        startActivity(intent);

    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(progressDialog!=null)
        {
            progressDialog.cancel();
            upload.setEnabled(true);
        }

    }

    @Override
    public void onBackPressed() {
        Intent gotopage =new Intent(Identify_img_upload.this, Identify_dashboard.class);
        startActivity(gotopage);
    }


    @Override
    public void onActivityResult(int requestcode, int responsecode, Intent data) {
        super.onActivityResult(requestcode, responsecode, data);
        switch (requestcode) {

            case 1:

                if (responsecode == RESULT_OK && data != null) {

                    final Uri address = data.getData();
                    System.out.println(" address 1:" + address);

                    image.setImageURI(address);
                    try {
                        bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lin1.setVisibility(View.GONE);
                    headview.setVisibility(View.GONE);
                    img_cv.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.VISIBLE);
                    file_path = String.valueOf(address);


                } else {
                    Toast.makeText(Identify_img_upload.this, "File is not selected.", Toast.LENGTH_LONG).show();
                }

                break;


            case 4:
                if (responsecode == RESULT_OK) {
                    String url = data.getStringExtra("url");
                    System.out.println(" url is:" + url);
                    image.setImageURI(Uri.parse(url));
                    try {
                        bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(url));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lin1.setVisibility(View.GONE);
                    headview.setVisibility(View.GONE);
                    img_cv.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.VISIBLE);
                    file_path = url;
                } else {
                    Toast.makeText(Identify_img_upload.this, "No image captured", Toast.LENGTH_LONG).show();
                }

                break;


            default:
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        progressDialog.dismiss();
    }

    public void show_dialog(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Identify_img_upload.this);
        alertDialogBuilder.setTitle("Result");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {//yes
                    public void onClick(final DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}