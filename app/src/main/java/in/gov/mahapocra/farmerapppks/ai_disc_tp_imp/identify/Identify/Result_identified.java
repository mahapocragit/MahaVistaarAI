package in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.Identify;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.InternetReceiver;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.detection_instance;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.Management_Practices_Disease;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.disease_prediction_model1;
import in.gov.mahapocra.farmerapppks.databinding.ActivityResultIdentifiedBinding;

public class Result_identified extends AppCompatActivity {

    private static final String TAG = "Result_identify";
    private ActivityResultIdentifiedBinding binding;
    int pageHeight = 2700;
    int pagewidth = 792;
    String selected_disease="";
    Bitmap bmp, scaledbmp,imgs,sacledimg,scaledlogo;
    List<Management_Practices_Disease> management_practices_diseaseList;
    private static final int PERMISSION_REQUEST_CODE = 200;
    LinearLayout casual_Organism_Layout,
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
    Button next,next2;
    String identificationType = "", identificationId = "", crop_id = "",insectId="", message="";
    Button rate;
    public static final String  no_info= "Currently, we don't have any information regarding this";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultIdentifiedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setDataInViews();
        setUpClickEvents();
    }

    private void setDataInViews() {
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        Log.d(TAG, "setDataInViews: "+result);
        try {
            if (result != null) {
                JSONObject response = new JSONObject(result);
                String message = response.optString("message");
                binding.resultIdentify.setText(message);
                Log.d(TAG, "setDataInViews: "+ response);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpClickEvents() {
        binding.next.setOnClickListener(view -> {
            Intent intent=new Intent(Result_identified.this, Identify_img_upload.class);
            startActivity(intent);
            finish();
        });

        binding.next2.setOnClickListener(view -> {
            Intent intent=new Intent(Result_identified.this, Identify_dashboard.class);
            startActivity(intent);
            finish();
        });
    }

//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_result_identified);
//
//        setTitle("AI-DISC");
//      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        internet = new InternetReceiver();
//        path="";
//        id="";
//        try{
//            Intent intent = getIntent();
//            Bundle data = intent.getExtras();
//            farmerIdentification = data.getString("farmerIdentification");
//            type = data.getString("type");
//            crop=data.getString("crop");
//            url=data.getString("url");
//            path=data.getString("path");
//        } catch (Exception e) {
//            e.printStackTrace();
//            try{
//                crop = detection_instance.getInstance().getcrop();
//                type=detection_instance.getInstance().gettype();
//                url=detection_instance.getInstance().geturl();
//                farmerIdentification = detection_instance.getInstance().getdetection();
//                path=detection_instance.getInstance().getpath();
//            } catch (Exception exception) {
//                exception.printStackTrace();
//            }
//        }
//        getid();
//        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logocut);
//        scaledbmp = Bitmap.createScaledBitmap(bmp, 120, 120, false);
//        scaledlogo = BitmapFactory.decodeResource(getResources(), R.drawable.logos);
//        scaledlogo = Bitmap.createScaledBitmap(scaledlogo, 300, 120, false);
//       // rate=findViewById(R.id.feed);
//        Button download=findViewById(R.id.download);
////        rate.setVisibility(View.GONE);
//        management_practices_diseaseList = new ArrayList<>();
//        Gson gson = new Gson();
//        disease_prediction_model1 ob = gson.fromJson(getIntent().getStringExtra("result"), disease_prediction_model1.class);
//        //Log.d("Result Page", ob.toString());
//        Diseases_Layout_main = findViewById(R.id.Diseases_Layout_main);
//        pest_Layout_main=findViewById(R.id.pest_Layout_main);
//        casual_Organism_Layout = findViewById(R.id.casual_Organism_Layout);
//        prevalance_Layout = findViewById(R.id.prevalance_Layout);
//        predisposingFunction_Layout = findViewById(R.id.predisposingFunction_Layout);
//        Symptoms_Layout = findViewById(R.id.Symptoms_Layout);
//        ManagementPractices_Layout = findViewById(R.id.ManagementPractices_Layout);
//        casual_Organism_Text = findViewById(R.id.casual_Organism_Text);
//        prevalance_Text = findViewById(R.id.prevalance_Text);
//        predisposingFunction_Text = findViewById(R.id.predisposingFunction_Text);
//        symptoms_Text = findViewById(R.id.symptoms_Text);
//        cultural_Text = findViewById(R.id.cultural_Text);
//        chemical_Text = findViewById(R.id.chemical_Text);
//        biological_Text = findViewById(R.id.biological_Text);
//        result_identify = findViewById(R.id.result_identify);
//        disease_result = (TextView) findViewById(R.id.disease);
//        prediction_status = findViewById(R.id.prediction_status);
//        image = findViewById(R.id.image);
//        head_1 = findViewById(R.id.Heading);
//        next = findViewById(R.id.next);
//        next2 = findViewById(R.id.next2);
//        prediction_status_text = findViewById(R.id.prediction_status_text);
//        prediction_status1 = findViewById(R.id.prediction_status);
//        scientificName_pest_Text= findViewById(R.id.scientificName_pest_Text);
//        cultural_pest_Text= findViewById(R.id.cultural_pest_Text);
//        chemical_pest_Text= findViewById(R.id.chemical_pest_Text);
//        bilogical_pest_Text= findViewById(R.id.bilogical_pest_Text);
//        symptoms_pest_Text= findViewById(R.id.dynamicSymptom_pest_Text);
//
//        //reading returned variables
//        if(type.equals("disease")) {
//            System.out.println("Category: " + type);
//            pest_Layout_main.setVisibility(View.GONE);
//            // get image path
//            try {
//                if (Uri.parse(ob.getImage_url()) != null)
//                {
//                    image.setImageURI(Uri.parse(ob.getImage_url()));
//                    try {
//                        sacledimg= MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(ob.getImage_url()));
//                        sacledimg = Bitmap.createScaledBitmap(sacledimg, 400, 400, false);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(Result_identified.this, "Not found", Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e)
//            {
//                Toast.makeText(Result_identified.this, "Please upload valid image", Toast.LENGTH_LONG).show();
//            }
//            if (!ob.isError()) {
//                System.out.println("No error");
//                message = ob.getMessage();
//                boolean result = ob.isResult();
//                if(result){
//                    System.out.println("result is true");
//                    crop_id = ob.getCropId();
//                    String crop_name = ob.getCropName();
//                    identificationType = ob.getIdentificationType();
//                    System.out.println("id type" + identificationType);
//                    //  ##### Disease type
//                    if(identificationType.equals("Disease")){
//                        String disease_id = ob.getIdentificationCode();
//                        System.out.println("Disease ID: " + disease_id);
//                        Diseases_Layout_main.setVisibility(View.VISIBLE);
//                        result_identify.setText(message);
//                        get_Management_Practices_Disease(disease_id, crop_id);
//
//                    }
//                    // ##### Healthy type
//                    else if(identificationType.equals("Healthy")){
//                        System.out.println("id type" + identificationType);
//                        Diseases_Layout_main.setVisibility(View.GONE);
//                        final android.app.AlertDialog.Builder popDialog1 = new android.app.AlertDialog.Builder(Result_identified.this);
//                        popDialog1.setIcon(R.drawable.sucess_1);
//                        popDialog1.setTitle("congratulations");
//                        popDialog1.setMessage("Your crop looks healthy");
//                        popDialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).show();
//                        result_identify.setText(message);
//
//                    }
//                    else if(identificationType.equals("Unidentified")){
//                        System.out.println("id type" + identificationType);
//                        Diseases_Layout_main.setVisibility(View.GONE);
//                        result_identify.setVisibility(View.GONE);
//                        final android.app.AlertDialog.Builder popDialog1 = new android.app.AlertDialog.Builder(Result_identified.this);
//                        popDialog1.setIcon(R.drawable.error_1);
//                        popDialog1.setTitle("Sorry!!!");
//                        popDialog1.setMessage("Failed to identify the disease, please try with different image");
//                        popDialog1.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent gotopage =new Intent(Result_identified.this, Identify_img_upload.class);
//                                startActivity(gotopage);
//                                finish();
//
//                            }
//                        }).show();
//                    }
//
//                }
//                else {
//                    Diseases_Layout_main.setVisibility(View.GONE);
//                    result_identify.setVisibility(View.GONE);
//                    final android.app.AlertDialog.Builder popDialog1 = new android.app.AlertDialog.Builder(Result_identified.this);
//                    popDialog1.setIcon(R.drawable.error_1);
//                    popDialog1.setTitle("Sorry!!!");
//                    popDialog1.setMessage("Some error occurred");
//                    popDialog1.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent gotopage =new Intent(Result_identified.this, Identify_img_upload.class);
//                            startActivity(gotopage);
//                            finish();
//
//                        }
//                    }).show();
//                }
//
//
//            }
//            else{
//                final android.app.AlertDialog.Builder popDialog1 = new android.app.AlertDialog.Builder(Result_identified.this);
//                popDialog1.setIcon(R.drawable.error_1);
//                popDialog1.setTitle("Sorry!!!");
//                popDialog1.setMessage("Some error occured");
//                popDialog1.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent gotopage =new Intent(Result_identified.this, Identify_img_upload.class);
//                        startActivity(gotopage);
//                        finish();
//
//                    }
//                }).show();
//
//            }
//        }
//        else if(type.equals("pest")){
//            Diseases_Layout_main.setVisibility(View.GONE);
//            if(!message.isEmpty() && !String.valueOf(ob.getOne()).equals("NA"))
//            {
//                pest_Layout_main.setVisibility(View.VISIBLE);
//                result_identify.setText(message);
//                get_Management_Practices_Pest(insectId, crop_id);
//            }
//
//            //message is available but pest is not present
//            else if(!message.isEmpty() && String.valueOf(ob.getOne()).equals("NA"))
//            {
//                result_identify.setText(message);
//                pest_Layout_main.setVisibility(View.VISIBLE);
//                get_Management_Practices_Pest(insectId, crop_id);
//
//            }
//
//        }
//        else
//        {
//            Diseases_Layout_main.setVisibility(View.GONE);
//            pest_Layout_main.setVisibility(View.GONE);
//            result_identify.setText("Sorry!! Failed to identify. Please try with another image");
//
//        }
//
//
//
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Intent gotopage=new Intent(Result_identified.this, Identify_img_upload.class);
//                startActivity(gotopage);
//                finish();
//            }
//        });
//
//        next2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Intent gotopage=new Intent(Result_identified.this, Identify_dashboard.class);
//                startActivity(gotopage);
//                finish();
//            }
//        });
//
//
//    }

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

    public void get_Management_Practices_Pest(String pest_id, String cropId)
    {
        management_practices_diseaseList.clear();
        JSONObject jo = new JSONObject();
        try {
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

                                //System.out.println("responseDataaaa" + response.toString());
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

                        Toast.makeText(Result_identified.this, "Error", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(Result_identified.this, "No management practices found for this disease", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        final android.app.AlertDialog.Builder popDialog1 = new android.app.AlertDialog.Builder(Result_identified.this);
                        popDialog1.setIcon(R.drawable.error_1);
                        popDialog1.setTitle("Sorry!!!");
                        popDialog1.setMessage("Some error occurred, please try again");
                        popDialog1.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent gotopage =new Intent(Result_identified.this, Identify_img_upload.class);
                                startActivity(gotopage);
                                finish();

                            }
                        }).show();

                        Toast.makeText(Result_identified.this, "Error", Toast.LENGTH_LONG).show();
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
            for (int i = 0; i < sp.length; i += 1) {
                int indext=concat.indexOf(sp[i].trim());
                BulletSpan bullet = new BulletSpan(30, Color.BLUE);
                strings.setSpan(bullet, indext, indext+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return strings;
        }else{
            return new SpannableString(rt.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);
    }

}