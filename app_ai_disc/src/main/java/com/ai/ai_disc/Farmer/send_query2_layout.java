package com.ai.ai_disc.Farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.About_us1;
import com.ai.ai_disc.Login;
import com.ai.ai_disc.MultiChat_FarmerExpertQuery_Activity;
import com.ai.ai_disc.R;
import com.ai.ai_disc.contributor;
import com.ai.ai_disc.create_report;
import com.ai.ai_disc.editprofile;
import com.ai.ai_disc.farmersprofile_fragment1;
import com.ai.ai_disc.history;
import com.ai.ai_disc.history_page;
import com.ai.ai_disc.model.MultiChatFarmerExpertQuery_Model;
import com.ai.ai_disc.my_reports;
import com.ai.ai_disc.needHelp;
import com.ai.ai_disc.shared_pref;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class send_query2_layout extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String cropId="",cropName="";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    String user_id;
    String organization_id;
    Spinner commodity_type, commodity_name, problem_area, translationSpinner;
    EditText description, translated_Language_ediText;
    Button select_picture, select_video, select_audio, submit, cancelTranslation_Button;
    String problem_area_code;
    String commodity_name_code;
    String commodity_id,chooseCropId;
    ArrayList<Model_DashboardContent> model_level_creations;
    ArrayList<String> cropListCodeList,cropListNameList;
    LinearLayout translateLayout;
    String crop_Name="",crop_Id="";
    int number_image = 0;
    int number_video = 0;
    int number_audio = 0;
    LinearLayout layout_add_image_etc;
    String problem = "";
    int transClicked = 0;
    String getUser_id="";
    //  ExposureVideoPlayer thumb_view;
    TextView loct;
    boolean onoff;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    String[] image_list = new String[1];
    String[] video_list = new String[3];
    String[] audio_list = new String[3];

    static String[] image_data = new String[1];
    static String[] video_data = new String[3];
    static String[] audio_data = new String[3];

    InputStream inputstream = null;
    InputStream inputstream2 = null;

    ProgressDialog progressDialog_at_send_query;
    Spinner chooseCrop_Spinner;


    TextView send_query_text_view;

  //  internet_receiver internet = new internet_receiver();

    ArrayAdapter commodity_type_adapter, commodity_name_adapter, problem_area_adapter, translation_adapter,choose_Crop_Adapter;

    ArrayList<String> commodity_type_name_list;
    ArrayList<String> commodity_type_code_list;

    ArrayList<String> commodity_name_name_list;
    ArrayList<String> commodity_name_code_list;

    ArrayList<String> problem_area_name_list;
    ArrayList<String> problem_area_code_list;
    ArrayList<String> translation_language_List;

    LinearLayout image_new_layout, video_new_layout, audio_new_layout;

    TextView description_text_view, field_mandatory_text_view;
    TextView attachment_text_view;
    TextView image_text_view, video_text_view, audio_text_view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_query2_layout);
        getUser_id= user_singleton.getInstance().getUser_id();

        setNavigationViewListener();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
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
            Log.d("error",e.toString());
        }

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
     /*   cropId=data.getString("cropID");
        cropName=data.getString("cropName");*/
        user_id=user_singleton.getInstance().getUser_id();
        cropListCodeList=new ArrayList<>();
        cropListNameList=new ArrayList<>();
        image_list[0] = "";
        /*image_list[1] = "";
        image_list[2] = "";*/
        field_mandatory_text_view = (TextView) findViewById(R.id.text_view_condition);
        send_query_text_view = (TextView) findViewById(R.id.textView12);
        commodity_type = (Spinner) findViewById(R.id.spinner17);
        model_level_creations =new ArrayList<>();
        commodity_name = (Spinner) findViewById(R.id.spinner20);
        problem_area = (Spinner) findViewById(R.id.spinner21);
        chooseCrop_Spinner=(Spinner)findViewById(R.id.chooseCrop_Spinner);
      //  translationSpinner = (Spinner) findViewById(R.id.translation_spinner);
        description = (EditText) findViewById(R.id.description_problem);
       // translated_Language_ediText = (EditText) findViewById(R.id.translated_Language);
        attachment_text_view = (TextView) findViewById(R.id.text_attachment);

        image_text_view = (TextView) findViewById(R.id.new_textview1);
        video_text_view = (TextView) findViewById(R.id.new_textview2);
        audio_text_view = (TextView) findViewById(R.id.new_textview3);

       // cancelTranslation_Button = (Button) findViewById(R.id.cancelButton);
        submit = (Button) findViewById(R.id.submit_query);
       // translateLayout = (LinearLayout) findViewById(R.id.translateLayout);
        layout_add_image_etc = (LinearLayout) findViewById(R.id.linear_layout_image_video_audio);
        description_text_view = (TextView) findViewById(R.id.description);
        getWindow().setSoftInputMode(1);

        commodity_type_name_list = new ArrayList<String>();
        commodity_type_code_list = new ArrayList<String>();

        commodity_name_name_list = new ArrayList<String>();
        commodity_name_code_list = new ArrayList<String>();

        problem_area_name_list = new ArrayList<String>();
        problem_area_code_list = new ArrayList<String>();
        translation_language_List = new ArrayList<String>();

        commodity_type_name_list.add("select commodity type ");
        commodity_name_name_list.add("select commodity name");
        problem_area_name_list.add("select problem area");
        translation_language_List.add("Translate to");
        translation_language_List.add("Bengali");

        commodity_type_code_list.add("");
        commodity_name_code_list.add("");
        problem_area_code_list.add("");
        getting_list_crop();

        choose_Crop_Adapter= new ArrayAdapter(send_query2_layout.this, android.R.layout.simple_list_item_1, cropListNameList);
        commodity_type_adapter = new ArrayAdapter(send_query2_layout.this, android.R.layout.simple_list_item_1, commodity_type_name_list);
        commodity_name_adapter = new ArrayAdapter(send_query2_layout.this, android.R.layout.simple_list_item_1, commodity_name_name_list);
        problem_area_adapter = new ArrayAdapter(send_query2_layout.this, android.R.layout.simple_list_item_1, problem_area_name_list);
        //  translation_adapter=new ArrayAdapter(send_query2_layout.this,android.R.layout.simple_list_item_1,translation_language_List);
        chooseCrop_Spinner.setAdapter(choose_Crop_Adapter);
        commodity_type.setAdapter(commodity_type_adapter);
        commodity_name.setAdapter(commodity_name_adapter);
        problem_area.setAdapter(problem_area_adapter);
//        translationSpinner.setAdapter(translation_adapter);

        image_new_layout = (LinearLayout) findViewById(R.id.new_layout1);
        video_new_layout = (LinearLayout) findViewById(R.id.new_layout2);
        audio_new_layout = (LinearLayout) findViewById(R.id.new_layout3);


        chooseCrop_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                chooseCropId = cropListCodeList.get(position);

                System.out.println("cropID " + chooseCropId);
/*

                if (!chooseCropId.isEmpty())
                {

                    get_commodity_name(commodity_id);
                    get_problem_area(commodity_id);
                }
*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        image_new_layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //  Toast.makeText(send_query2_layout.this,"This is image",Toast.LENGTH_LONG).show();

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
                if (check_permissions()){
                    if (number_image < 1)
                {
                    // Toast.makeText(send_query2_layout.this, "select picture", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    // System.out.println("q.3.2");
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    // System.out.println("q.3.3");
                    startActivityForResult(Intent.createChooser(intent, "select picture"), 1);

                } else
                {
                    Toast.makeText(send_query2_layout.this, "Please rise new query", Toast.LENGTH_LONG).show();
                }
                }

                }else{
                    if (number_image < 1)
                    {
                        // Toast.makeText(send_query2_layout.this, "select picture", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        // System.out.println("q.3.2");
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        // System.out.println("q.3.3");
                        startActivityForResult(Intent.createChooser(intent, "select picture"), 1);

                    } else
                    {
                        Toast.makeText(send_query2_layout.this, "Please rise new query", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Toast.makeText(send_query2_layout.this, to_be_used[5],Toast.LENGTH_LONG).show();

                submit.setEnabled(false);
                progressDialog_at_send_query = new ProgressDialog(send_query2_layout.this);
                progressDialog_at_send_query.setCancelable(false);
                progressDialog_at_send_query.setMessage("Send Query");
                progressDialog_at_send_query.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog_at_send_query.show();

                if (!description.getText().toString().isEmpty() && !chooseCropId.isEmpty())
                {

                    problem = description.getText().toString();

                    for (int i = 0; i < image_list.length; i++)
                    {
                        System.out.println("image is:" + image_list[i]);
                        if (!image_list[i].isEmpty())
                        {


                            Uri address_to_convert = Uri.parse(image_list[i]);


                            try {
                                inputstream = getContentResolver().openInputStream(address_to_convert);

                                int length_image = 0;

                                length_image = inputstream.available();
                                System.out.println("  length:" + length_image);
                                byte[] data_in_byte_image = new byte[length_image];
                                inputstream.read(data_in_byte_image);
                                inputstream.close();
                                String image_in_string = Base64.encodeToString(data_in_byte_image, Base64.DEFAULT);
                                image_data[i] = image_in_string;
                            } catch (IOException e) {
                                e.printStackTrace();


                              //  Toast.makeText(send_query2_layout.this, to_be_used[15], Toast.LENGTH_LONG).show();
                            }
                        } else {
                        }


                    }



                    shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);

                    String token_firebase = shared_pref.sp.getString("token", "");

                    // System.out.println("toke firebase in submit query  is:"+token_firebase);


                    json_new_submit_query_asyntask json_new_submit = new json_new_submit_query_asyntask(send_query2_layout.this, progressDialog_at_send_query, submit);
                    json_new_submit.execute(user_id, problem, chooseCropId, token_firebase);
                } else
                    {
                        if(chooseCropId.isEmpty())
                        {
                            Toast.makeText(send_query2_layout.this, "Please choose the crop", Toast.LENGTH_LONG).show();

                        }
                         else if(description.getText().toString().isEmpty())
                        {
                            Toast.makeText(send_query2_layout.this, "Please write the query", Toast.LENGTH_LONG).show();

                        }

                    progressDialog_at_send_query.dismiss();
                    submit.setEnabled(true);
                }
            }
        });
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onActivityResult(int requestcode, int responsecode, Intent data)
    {

        super.onActivityResult(requestcode, responsecode, data);
        switch (requestcode) {

            case 1:

                if (requestcode == 1 && responsecode == RESULT_OK && data != null) {

                    final Uri address = data.getData();
                    long file_length = check_length(address);

                    if (file_length < 8050)
                    {


                        LayoutInflater layoutinflater2 = (LayoutInflater) send_query2_layout.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View parent2 = layoutinflater2.inflate(R.layout.query_image_layout, null);
                        ImageView image = (ImageView) parent2.findViewById(R.id.image);
                        ImageButton remove_button = (ImageButton) parent2.findViewById(R.id.cancel);


                        Glide.with(send_query2_layout.this)
                                .load(address)
                                .into(image);


                        layout_add_image_etc.addView(parent2);


                        number_image = number_image + 1;
                        add_image_address(address.toString());

                        remove_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layout_add_image_etc.removeView(parent2);
                                number_image = number_image - 1;
                                remove_image_address(address.toString());

                            }
                        });


                    } else {
                        // System.out.println(" more than 2 MB :");

                        Toast.makeText(send_query2_layout.this, "please choose less then 8mb size", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(send_query2_layout.this,"error", Toast.LENGTH_LONG).show();
                }

                break;





            default:
                break;
        }
    }



    public  void getting_list_crop()
    {



        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/list_crop")

                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {



                        try {


                           // model_level_creations.clear();
                            cropListCodeList.clear();
                            cropListNameList.clear();
                            cropListNameList.add(0,"Select your crop");
                            cropListCodeList.add(0,"");

                            JSONArray array = response.getJSONArray("cropreport_list");

                            for(int i=0;i<array.length();i++)
                            {
                               // Model_DashboardContent model_dashboardContent=new Model_DashboardContent();
                                JSONObject object = (JSONObject) array.get(i);

                                crop_Id= object.getString("id");
                                crop_Name= object.getString("name");
                                cropListCodeList.add(i+1, crop_Id);
                                cropListNameList.add(i+1,crop_Name);


                              //  model_dashboardContent.setGetGridText(crop_Name);
                               // model_dashboardContent.setCropId(crop_Id);

                               // model_level_creations.add(model_dashboardContent);

                            }

                     choose_Crop_Adapter.notifyDataSetChanged();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                    }
                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(send_query2_layout.this,"Error in list of crop.",Toast.LENGTH_LONG).show();
                    }
                });
    }




    public void add_image_address(String address_to_add) {

        //   System.out.println("inside  adding image ");

        // System.out.println(" value at 0:"+image_list[0]);
        // System.out.println(" value at 1:"+image_list[1]);
        // System.out.println(" value at 2:"+image_list[2]);

        for (int i = 0; i < image_list.length; i++) {
            if (image_list[i].isEmpty()) {
                //            System.out.println(" empty:"+i);
                image_list[i] = address_to_add;
                //          System.out.println(" not empty,now is:"+image_list[i]);
                return;
            }
        }


    }


    public void remove_image_address(String address_to_remove) {

        // System.out.println("inside  removing image ");
        // System.out.println(" value at 0:"+image_list[0]);
        // System.out.println(" value at 1:"+image_list[1]);
        //System.out.println(" value at 2:"+image_list[2]);

        for (int i = 0; i < image_list.length; i++) {
            if (image_list[i].equals(address_to_remove)) {
                //      System.out.println(" matching:"+i);
                image_list[i] = "";
                //    System.out.println(" set to empty :"+image_list[i]);
                return;
            }
        }

    }
    public boolean check_permissions() {

        if (ContextCompat.checkSelfPermission(send_query2_layout.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(send_query2_layout.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(send_query2_layout.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(send_query2_layout.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;

        }


        return true;


    }


    public long check_length(Uri address_of_file)
    {
        long length_file = 0;


        try {
            inputstream2 = getContentResolver().openInputStream(address_of_file);
            //  Toast.makeText(send_query2_layout.this,"checking size  ",Toast.LENGTH_LONG).show();


            length_file = inputstream2.available();
            //   System.out.println(" size is 1:"+length_file);
            length_file = length_file / 1024;
            // System.out.println(" size is  2:"+length_file);
            inputstream2.close();
            // return

        } catch (FileNotFoundException e) {
            //System.out.println(" file not found exception:");
            e.printStackTrace();
        } catch (IOException e) {
            // System.out.println(" io exception :");
            e.printStackTrace();
        }
        return length_file;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        Toast.makeText(send_query2_layout.this, "Signed out", Toast.LENGTH_LONG).show();
                        shared_pref.remove_shared_preference(send_query2_layout.this);
                        Intent intent = new Intent(send_query2_layout.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w("fff", "Error writing document", e);
                        Toast.makeText(send_query2_layout.this, "Token not Deleted.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void signout() {
        android.app.AlertDialog.Builder opt = new AlertDialog.Builder(send_query2_layout.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user_singleton.getInstance().getfb_id().toString().matches("")){
                    Toast.makeText(send_query2_layout.this, "Signed out", Toast.LENGTH_LONG).show();
                    shared_pref.remove_shared_preference(send_query2_layout.this);
                    Intent intent = new Intent(send_query2_layout.this, Login.class);
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

//    private void signout() {
//        AlertDialog.Builder opt = new AlertDialog.Builder(send_query2_layout.this);
//        opt.setTitle("Are you sure ?");
//        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                shared_pref.remove_shared_preference(send_query2_layout.this);
//                Intent intent = new Intent(send_query2_layout.this, Login.class);
//                startActivity(intent);
//                finish();
//
//
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.about_us1:

                Intent intent = new Intent(send_query2_layout.this, About_us1.class);
                startActivity(intent);



                break;
            case R.id.help1:

                Intent intent1 = new Intent(send_query2_layout.this, needHelp.class);
                startActivity(intent1);



                break;
            case R.id.logout1:

                signout();

                break;
            case R.id.editme:

                Intent ibt=new Intent(send_query2_layout.this, editprofile.class);
                startActivity(ibt);

                break;
            case R.id.contributor:

                Intent ibt1=new Intent(send_query2_layout.this, contributor.class);
                startActivity(ibt1);

                break;
            case R.id.history:

                Intent ibt1q=new Intent(send_query2_layout.this, history.class);
                startActivity(ibt1q);

                break;
            case R.id.home:

                Intent ibtwd=new Intent(send_query2_layout.this, farmersprofile_fragment1.class);
                startActivity(ibtwd);

                break;
            default:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public class Json_new_submit_query extends AsyncTask<String,Void,String>
    {

        String value_returned;
        String id_returned="";
        Context context1;
        String user_id;
        String organization_id;
        ProgressDialog progressDialog1;
        Button submit1;
        String image1_proper;
        JSONObject obj;
        public Json_new_submit_query(Context context, ProgressDialog progressDialog){
            context1=context;
            progressDialog1=progressDialog;

        }


        @Override
        protected String doInBackground(String... param)
        {


            try {
                user_id=param[0];
                //organization_id=param[1];
                String description=param[1];
                String crop_id=param[2];
                String firebase_token="";

                obj= new JSONObject();

                String image_1= send_query2_layout.image_data[0];
          /*  String image_2= send_query2_layout.image_data[1];
            String image_3= send_query2_layout.image_data[2];*/
         /*   String video_1= send_query2_layout.video_data[0];
            String video_2= send_query2_layout.video_data[1];
            String video_3= send_query2_layout.video_data[2];
            String audio_1= send_query2_layout.audio_data[0];
            String audio_2= send_query2_layout.audio_data[1];
            String audio_3= send_query2_layout.audio_data[2];*/
                if(image_1!=null)
                {
                    if(!image_1.isEmpty())
                    {
                        image1_proper=image_1.replaceAll("\n","");

                    }
                    else
                    {
                        image1_proper="";
                    }

                }
                else
                {
                    image1_proper="";
                }

        /*    String image2_proper=image_2.replaceAll("\n","");
            String image3_proper=image_3.replaceAll("\n","");*/
       /*     String video1_proper=video_1.replaceAll("\n","");
            String video2_proper=video_2.replaceAll("\n","");
            String video3_proper=video_3.replaceAll("\n","");
            String audio1_proper=audio_1.replaceAll("\n","");
            String audio2_proper=audio_2.replaceAll("\n","");
            String audio3_proper=audio_3.replaceAll("\n","");*/
                // String pdf_proper=pdf.replaceAll("\n","");


                obj.put("image_path",image1_proper);
       /*     obj.put("image_2",image2_proper);
            obj.put("image_3",image3_proper);*/
                obj.put("query_description",description);
                obj.put("crop_id",crop_id);
                obj.put("farmer_id",user_id);
                obj.put("query_status","0");
                obj.put("language_type","English");

                Log.d("ImagePath",image1_proper);


                AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/farmersendquery")
                        .addJSONObjectBody(obj)
                        .build()
                        .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener() {
                            @Override
                            public void onResponse(Response okHttpResponse, String response) {

                                if(!response.isEmpty()){

                                    if(!response.equals("0"))
                                    {
                                        Toast.makeText(context1, "Your Query has been submitted successfully!", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(context1, farmersprofile_fragment1.class);
//                                        intent.putExtra("user_id",user_id);
//                                        // intent.putExtra("organization_id",organization_id);
//                                        context1.startActivity(intent);
                                        send_query2_layout.super.onBackPressed();
                                        finish();
                                    }
                                }else{
                                    Toast.makeText(context1,"query not submitted",Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onError(ANError anError) {
                                //  System.out.println("inside android  error , in send query");

                                Toast.makeText(context1,"query not submitted",Toast.LENGTH_LONG).show();

                            }
                        });


          /*  obj.put("video_1",video1_proper);
            obj.put("video_2",video2_proper);
            obj.put("video_3",video3_proper);
            obj.put("audio_1",audio1_proper);
            obj.put("audio_2",audio2_proper);
            obj.put("audio_3",audio3_proper);*/
                System.out.println("userId" + user_id);
                System.out.println("cropId" + crop_id);
                System.out.println("desc" + description);
                System.out.println("imagepath" +image1_proper );
        /*    obj.put("item_id",commodity_name_code);
            obj.put("main_problem_id",commodity_type_code);
            obj.put("problem_area_id",problem_area_code);
            obj.put("pdf_file","");
            obj.put("language","english");
            obj.put("desc",description);
            obj.put("kvk_id",organization_id);
            obj.put("user_id",user_id);
            obj.put("token_id",firebase_token);*/

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return value_returned;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1.show();
        }

        @Override
        protected void onPostExecute(String result)
        {

            //ca.notifyDataSetChanged();
            progressDialog1.dismiss();
            //   submit1.setEnabled(true);



        }

    }
}