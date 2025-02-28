package in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.SparseIntArray;
import android.view.MenuItem;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.navigation.NavigationView;
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

import java.util.Map;
import java.util.Objects;

import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.disease_prediction_model1;
import in.gov.mahapocra.farmerapppks.util.app_util.AppConstants;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class FarmerPestDiseaseIdentify extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Identify_disease";
    String crop = "";
    String user_id = "";
    String farmerIdentificationString="";
    String type="";
    String url="";
    Bitmap bitmap;
    LinearLayout lin1;
    LinearLayout headview;
    InternetReceiver internet;
    String selected_disease = "";
    TextView warning,warnbox;
    Button take_photo, select_from_gallery,another;
    ImageView image;
    ProgressDialog progressDialog;
    Button upload;
    String file_path = "";
    String file_path_string = "";
    String url_model = "";
    TextView loct;
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
        setContentView(R.layout.activity_identify_disease);
        requestCameraPermission();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("AI-DISC");
        img_cv = findViewById(R.id.img_card_view);

        internet = new InternetReceiver();
        setNavigationViewListener();
        drawerLayout = findViewById(R.id.my_drawer_layout2);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.navigation_drawer2);
        View head=navigationView.getHeaderView(0);


        //Location set in navigation drawer
        TextView userd=head.findViewById(R.id.name_dataentry);
        TextView acc=head.findViewById(R.id.type_nav);
        loct=head.findViewById(R.id.loctfff);
        try{userd.setText(user_singleton.getInstance().getfname()+" "+user_singleton.getInstance().getMname()+" "+user_singleton.getInstance().getlname());
            acc.setText(user_singleton.getInstance().getUser_type()+" Account");
            loct.setText(user_singleton.getInstance().getloct());
        } catch (Exception e) {
            e.printStackTrace();
        }
        lin1 = findViewById(R.id.linlay1);
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

                crop = detection_instance.getInstance().getcrop();
                type=detection_instance.getInstance().gettype();
                url=detection_instance.getInstance().geturl();
                farmerIdentificationString = detection_instance.getInstance().getdetection();
                crppp.setText("Crop : "+crop);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        take_photo = findViewById(R.id.button);
        select_from_gallery = findViewById(R.id.button1);
        another=findViewById(R.id.tryanother);
        another.setVisibility(View.GONE);
        another.setOnClickListener(view -> {
            Intent gotoPage =new Intent(FarmerPestDiseaseIdentify.this, FarmerPestDiseaseIdentify.class);
            startActivity(gotoPage);
            finish();
        });
        warning=findViewById(R.id.warningmsg);
        warning.setVisibility(View.GONE);
        image = findViewById(R.id.image);


        upload = findViewById(R.id.upload);
        progressDialog=new ProgressDialog(FarmerPestDiseaseIdentify.this);
        user_id = user_singleton.getInstance().getUser_id();
        upload.setVisibility(View.GONE);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        upload.setOnClickListener(v -> {

            progressDialog.setCancelable(false);
            progressDialog.setMessage("Uploading..");
            progressDialog.show();
            upload.setEnabled(false);
            // To add google identifier of crop

            url_model="https://aidisc.krishimegh.in:32517/"+url;

            Observable.fromCallable(() -> {
                converting_string(file_path);
                return false;
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) ->
                    {
                        if (bitmap!=null){

                            InputImage image = InputImage.fromBitmap(bitmap,0 );
                            ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
                            labeler.process(image).addOnSuccessListener(imageLabels -> {
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
//                                                   // Log.d("Google Model Output:",obj.toString());

                                if (!obj.isEmpty()) {
                                    //Log.d(TAG, "not empty");

                                    if (obj.contains("Plant") || obj.contains("Vegetable") || obj.contains("Fruit") || obj.contains("Insect")) {
                                        System.out.println("confidence: "+myMap.get("Plant"));
                                        if (obj.contains("Forest") || obj.contains("Jungle") || obj.contains("Garden") || obj.contains("Field")) {
                                            //Log.d(TAG,obj.toString());
                                            progressDialog.cancel();
                                            afterJungleForestImage();
                                        } else {
                                            upload_image_data(url_model, user_id, selected_disease, selected_severity_level, type);
//                                                            if(myMap.get("Plant")>0.75 || myMap.get("Vegetable")>0.75 || myMap.get("Fruit")>0.75 || myMap.get("Fruit")>0.75){
//                                                                upload_image_data(url_model, user_id, selected_disease, selected_severity_level, type);
//                                                                //upload_image_data_for_crop(url_model, user_id, selected_disease, selected_severity_level, type);
//                                                            }
//                                                            else
//                                                            {
//                                                                progressDialog.cancel();
//                                                                afterJungleForestimage();
//                                                            }

                                        }

                                    } else {
                                        //Toast.makeText(farmerdiseasepestIdentify.this, "Error !!! No plant objects...", Toast.LENGTH_LONG).show();
                                        progressDialog.cancel();
                                        afterfalseimage();

                                    }
                                } else {
                                    progressDialog.cancel();
                                    afterNoObj();
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
                    });
        });

        take_photo.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {
                Intent intent = new Intent(FarmerPestDiseaseIdentify.this, CAM2.class);
                startActivityForResult(intent, 4);
            } else {
                if (checking_permision()) {
                    Intent intent = new Intent(FarmerPestDiseaseIdentify.this, CAM2.class);
                    startActivityForResult(intent, 4);
                }
            }
        });


        select_from_gallery.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), 1);
            } else {
            if (ContextCompat.checkSelfPermission(FarmerPestDiseaseIdentify.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(FarmerPestDiseaseIdentify.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), 1);
            }
        }});
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    AppConstants.CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void upload_image_data_aftercrop(String url_model, String user_id, String selected_disease, String selected_severity_level, String type,String path) {
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("image_path", path);
            object.put("user_id", user_id);
            object.put("disease_name", selected_disease);
            object.put("severity", selected_severity_level);
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
                            disease_prediction_model1 model = new disease_prediction_model1();
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
                                System.out.println("result_crop" + response);
                            }
                            else {
                                model.setErrorval(response.optString("error_msg"));
                                model.setMessage(response.optString("message"));
                            }
                            detection_instance.getInstance().setpath(path);
                            Gson gson = new Gson();
                            String myJson = gson.toJson(model);
                            Intent intent = new Intent(FarmerPestDiseaseIdentify.this, Result_identify.class);
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
                        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(FarmerPestDiseaseIdentify.this);
                        popDialog1.setIcon(R.drawable.error_1);
                        popDialog1.setTitle("Sorry!!!");
                        popDialog1.setMessage("No model for this crop");
                        popDialog1.setPositiveButton("Try again", (dialog, which) -> {
                            Intent gotopage =new Intent(FarmerPestDiseaseIdentify.this, FarmerPestDiseaseIdentify.class);
                            startActivity(gotopage);
                            finish();
                        }).show();
                    }
                });
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigation_drawer2);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void afterfalseimage(){
        upload.setVisibility(View.GONE);
        lin1.setVisibility(View.GONE);
        headview.setVisibility(View.GONE);
        warning.setVisibility(View.GONE);
        warnbox.setVisibility(View.GONE);
        another.setVisibility(View.GONE);

        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(FarmerPestDiseaseIdentify.this);
        popDialog1.setIcon(R.drawable.error_1);
        popDialog1.setTitle("WARNING!!");
        popDialog1.setMessage("Your image doesn't contain any plant parts. Please try again with appropriate images");
        popDialog1.setPositiveButton("OK", (dialog, which) -> {
                Intent gotoPage =new Intent(FarmerPestDiseaseIdentify.this, FarmerPestDiseaseIdentify.class);
                startActivity(gotoPage);
                finish();
        }).show();
    }

    private void afterJungleForestImage(){
        upload.setVisibility(View.GONE);
        lin1.setVisibility(View.GONE);
        headview.setVisibility(View.GONE);
        warning.setVisibility(View.GONE);
        warnbox.setVisibility(View.GONE);
        another.setVisibility(View.GONE);
        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(FarmerPestDiseaseIdentify.this);
        popDialog1.setIcon(R.drawable.error_1);
        popDialog1.setTitle("WARNING!!");
        popDialog1.setMessage("Objects in the image are too far. Try to capture the symptomatic region of crop within the rectangular box");
        popDialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gotopage =new Intent(FarmerPestDiseaseIdentify.this, FarmerPestDiseaseIdentify.class);
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
        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(FarmerPestDiseaseIdentify.this);
        popDialog1.setIcon(R.drawable.error_1);
        popDialog1.setTitle("WARNING!!");
        popDialog1.setMessage("Objects in the image are too close. Try to capture the symptomatic region of crop within the rectangular box");
        popDialog1.setPositiveButton("OK", (dialog, which) -> {
            Intent gotoPage =new Intent(FarmerPestDiseaseIdentify.this, FarmerPestDiseaseIdentify.class);
            startActivity(gotoPage);
            finish();
        }).show();
    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(() -> {
            int timeToBlink = 500;    //in milissegunds
            try{Thread.sleep(timeToBlink);}catch (Exception e) {}
            handler.post(new Runnable() {
                @Override
                public void run() {
                        if(warning.getVisibility() == View.VISIBLE){
                            warning.setVisibility(View.INVISIBLE);
                        }else{
                            warning.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
            });
        }).start();
    }

    public boolean checking_permision() {
        if ((ContextCompat.checkSelfPermission(FarmerPestDiseaseIdentify.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(FarmerPestDiseaseIdentify.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(FarmerPestDiseaseIdentify.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(FarmerPestDiseaseIdentify.this, "Error in converting ", Toast.LENGTH_LONG).show();
                file_path_string = "";
            }
        } else {
            System.out.println(" inside else ");
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
        JSONObject object = new JSONObject();
        try {
            object.put("image_file", image_proper);
            object.put("crop_name", crop);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //python flask api
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
                                Toast.makeText(FarmerPestDiseaseIdentify.this, "Failed at Crop Detection", Toast.LENGTH_LONG).show();
                            }else {
                                if (response.optBoolean("result")) {

                                    upload_image_data_aftercrop(urls, user_id, selected_disease, selected_severity_level, type, paths);
                                } else {
                                    final AlertDialog.Builder popDialog = new AlertDialog.Builder(FarmerPestDiseaseIdentify.this);
                                    popDialog.setIcon(R.drawable.errro_2);
                                    popDialog.setTitle("Warning ! The image may not contain "+crop+" crop");
                                    popDialog.setMessage("Proceeding with the selected crop may give wrong detection result.\nStill if you want to proceed press YES or to try with another image press NO. ");
                                    popDialog.setPositiveButton("YES", (dialog, which) -> upload_image_data_aftercrop(urls, user_id, selected_disease, selected_severity_level, type, paths));
                                    popDialog.setNegativeButton("NO", (dialog, which) -> {
                                        Intent gotoPage =new Intent(FarmerPestDiseaseIdentify.this, FarmerPestDiseaseIdentify.class);
                                                         startActivity(gotoPage);
                                                   finish();

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
                        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(FarmerPestDiseaseIdentify.this);
                        popDialog1.setIcon(R.drawable.error_1);
                        popDialog1.setTitle("Sorry!!!");
                        popDialog1.setMessage("Some error occurred");
                        popDialog1.setPositiveButton("Try again", (dialog, which) -> {
                            Intent gotoPage =new Intent(FarmerPestDiseaseIdentify.this, FarmerPestDiseaseIdentify.class);
                            startActivity(gotoPage);
                            finish();
                        }).show();
                    }
                });


    }

    public void pestCalling(JSONObject response)
    {
        disease_prediction_model1 model = new disease_prediction_model1();
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
        Intent intent = new Intent(FarmerPestDiseaseIdentify.this, Result_identify.class);
        Bundle data1 = new Bundle();
        data1.putString("farmerIdentification",farmerIdentificationString);
        intent.putExtras(data1);
        intent.putExtra("result", myJson);
        intent.putExtra("crop",crop);
        intent.putExtra("type",type);
        intent.putExtra("url",url);
        intent.putExtra("type",type);
        startActivity(intent);

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
        if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("farmer")){

//        Intent gotopage =new Intent(farmerdiseasepestIdentify.this, farmersprofile_fragment1.class);
//        startActivity(gotopage);
        }
        else{
//            Intent gotopage =new Intent(farmerdiseasepestIdentify.this, expertprofile_fragment.class);
//            startActivity(gotopage);
        }

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
                    Toast.makeText(FarmerPestDiseaseIdentify.this, "File is not selected.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(FarmerPestDiseaseIdentify.this, "No image captured", Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmerPestDiseaseIdentify.this);
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

//    public void sign_out() {
//
//
//        DocumentReference docRef = db.collection("loginaidisc").document(user_singleton.getInstance().getfb_id());
//
//// Remove the 'capital' field from the document
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("token", FieldValue.delete());
//
//        docRef.update(updates)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                       // Log.d("fff", "DocumentSnapshot successfully written!");
//                        Toast.makeText(farmerdiseasepestIdentify.this, "Signed out", Toast.LENGTH_LONG).show();
//                        shared_pref.remove_shared_preference(farmerdiseasepestIdentify.this);
//                        Intent intent = new Intent(farmerdiseasepestIdentify.this, Login.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                       // Log.w("fff", "Error writing document", e);
//                        Toast.makeText(farmerdiseasepestIdentify.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
//                    }
//                });
//    }

//    private void signout() {
//        AlertDialog.Builder opt = new AlertDialog.Builder(farmerdiseasepestIdentify.this);
//        opt.setTitle("Are you sure ?");
//        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if (user_singleton.getInstance().getfb_id().toString().matches("")){
//                    Toast.makeText(farmerdiseasepestIdentify.this, "Signed out", Toast.LENGTH_LONG).show();
//                    shared_pref.remove_shared_preference(farmerdiseasepestIdentify.this);
//                    Intent intent = new Intent(farmerdiseasepestIdentify.this, Login.class);
//                    startActivity(intent);
//                    finish();
//                }else{
//                    sign_out();}
//
//            }
//        });
//        opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//        opt.show();
//    }

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

//            case R.id.about_us1:
//
//                Intent intent = new Intent(farmerdiseasepestIdentify.this, About_us1.class);
//                startActivity(intent);
//
//
//
//                break;
//            case R.id.help1:
//
//                Intent intent1 = new Intent(farmerdiseasepestIdentify.this, needHelp.class);
//                startActivity(intent1);
//
//
//
//                break;
//            case R.id.logout1:
//
//                signout();
//
//                break;
//            case R.id.disclaimer:
//
//                Intent intent1q = new Intent(farmerdiseasepestIdentify.this, disclaimer.class);
//                startActivity(intent1q);
//
//
//
//                break;
//            case R.id.editme:
//
//                Intent ibt=new Intent(farmerdiseasepestIdentify.this, editprofile.class);
//                startActivity(ibt);
//
//                break;
//            case R.id.contributor:
//
//                Intent ibt1=new Intent(farmerdiseasepestIdentify.this, contributor.class);
//                startActivity(ibt1);
//
//                break;
//
//            case R.id.history:
//
//                Intent ibtw=new Intent(farmerdiseasepestIdentify.this, history.class);
//                startActivity(ibtw);
//
//                break;
//            case R.id.home:
//
//                Intent ibtwd;
//                if (user_singleton.getInstance().getUser_type().trim().toLowerCase().matches("expert")){
//                    ibtwd=new Intent(farmerdiseasepestIdentify.this, expertprofile_fragment.class);
//                }else{ ibtwd=new Intent(farmerdiseasepestIdentify.this, farmersprofile_fragment1.class);}
//                startActivity(ibtwd);
//
//                break;
//            default:
//                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}