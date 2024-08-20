package com.ai.ai_disc;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Bundle;

import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    Button take_photo, select_from_gallery;
    ImageView image;
    Uri uriSavedImage;
    Button upload;
    String file_path = "";
    String file_path_string="";


    String crop= "";
    String disease= "";
    String part= "";
    String latitude="";
    String longitude="";
    ProgressDialog progress;
    InternetReceiver internet = new InternetReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC");

        take_photo = (Button) findViewById(R.id.button);
        select_from_gallery = (Button) findViewById(R.id.button1);
        image = (ImageView) findViewById(R.id.image);
        upload = (Button) findViewById(R.id.upload);
        upload.setVisibility(View.GONE);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                upload_image_data(file_path,Data1.record_id);
            }
        });

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(checking_permision()){


                     Intent intent = new Intent(MainActivity.this,CAM2.class);
                     startActivityForResult(intent,4);

                }
   }
        });


        select_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                } else {

                    /*
                      Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
                     */

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
                }


            }
        });

    }


    @Override
    public void onActivityResult(int requestcode, int responsecode, Intent data) {

        switch (requestcode) {

            case 1:

                if (requestcode == 1 && responsecode == RESULT_OK && data != null) {

                    final Uri address = data.getData();

                    System.out.println(" address 1:" + address);

                    image.setImageURI(address);
                    image.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.VISIBLE);
                    file_path = String.valueOf(address);


/*
 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    mArrayUri.add(mImageUri);
                    galleryAdapter = new GalleryAdapter(getApplicationContext(),mArrayUri);
                    gvGallery.setAdapter(galleryAdapter);
                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                            .getLayoutParams();
                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(getApplicationContext(),mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
 */




                } else {
                    Toast.makeText(MainActivity.this, "File is not selected. ", Toast.LENGTH_LONG).show();
                }

                break;



            case 4:


                if (requestcode == 4  && responsecode == RESULT_OK) {


                    String url=data.getStringExtra("url");
                  //  System.out.println(" url is:"+url);
                    image.setImageURI(Uri.parse(url));
                    image.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.VISIBLE);
                    file_path = url;



                } else {
                    Toast.makeText(MainActivity.this, "Some Error.", Toast.LENGTH_LONG).show();


                }

                break;




            default:
                break;
        }
    }







    private void upload_image_data(String address,String record_id) {

        upload.setEnabled(false);

        progress = new ProgressDialog(MainActivity.this);
        progress.setCancelable(false);
        progress.setMessage("Uploading.Please wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        converting_string(address);

        System.out.println(" address:"+address);

        if(file_path_string.isEmpty()){

            progress.cancel();
            show_dialog("Image is not added.");

            return;
        }

       String image_proper= file_path_string.replaceAll("\n","");
        JSONObject object = new JSONObject();
        try {
            object.put("record_id",record_id);
            object.put("image_path",image_proper);



/*
{
  "record_id": "sample string 1",
  "image_path": "sample string 2"
}
 */

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Add_image_data")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progress.cancel();

                        upload.setEnabled(true);

                        System.out.print(" response :"+response);






                            /*
                            {
{
  "message": "sample string 1",
  "result": true
}
                             */


                        try {


                            boolean result = response.getBoolean("result");
                            String message=response.getString("message");



                            if(result){

                                reset();

                                show_dialog("Image is added successfully.");

                            }else {
                                //   Toast.makeText(MainActivity.this, "username  and password is not correct.", Toast.LENGTH_LONG).show();

                                show_dialog("Image is not added.");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            show_dialog(" Image is not added. Error occured");
                        }




                    }
                    @Override
                    public void onError(ANError error) {

                        progress.cancel();
                        upload.setEnabled(true);
                        show_dialog("Error in adding image.");
                       // Toast.makeText(MainActivity.this,"There is error in adding image.",Toast.LENGTH_LONG).show();
                    }
                });






    }





    public boolean checking_permision(){


        if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)  != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return false;

        }



        return  true;
    }


    public void show_dialog(String message){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);


        alertDialogBuilder.setTitle("Response");


        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {//yes
                    public void onClick(final DialogInterface dialog, int id) {

                        dialog.cancel();

                        // Intent intent = new Intent(Traps_list.this,Damage_list.class);
                        //finish();
                        //startActivity(intent);

                    }
                });



        AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();
    }

    @Override
    public   boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflator= getMenuInflater();
        inflator.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.logout:

                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);

                shared_pref.remove_shared_preference(MainActivity.this);

                break;

            default:
                break;

        }

        return true;
    }
    public void converting_string(String file_path){

        if(!file_path.isEmpty()){


            Uri address_to_convert=Uri.parse(file_path);
            System.out.println("Uri is:"+address_to_convert);

            try {

                InputStream inputstream=null;
                inputstream = getContentResolver().openInputStream(address_to_convert);
              //  inputstream = new


                System.out.println(" input stream is :");
                int length_image = 0;

                length_image = inputstream.available();
                System.out.println("  length:" + length_image);
                byte[] data_in_byte_image = new byte[length_image];
                System.out.println("  length of data in byte " + data_in_byte_image.length);
                System.out.println(" data in byte " + data_in_byte_image.length);
                inputstream.read(data_in_byte_image);
                inputstream.close();
                System.out.println(" after inputstream close");
                file_path_string="";
                file_path_string = Base64.encodeToString(data_in_byte_image,Base64.DEFAULT);
                System.out.println(" image in string :" +file_path_string);




            }catch(IOException e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this,"Error in converting ",Toast.LENGTH_LONG).show();
                file_path_string="";
            }
        }else{

            System.out.println(" inside else ");
        }
    }

    public void reset(){


        image.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);
        file_path = "";
        file_path_string="";
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
