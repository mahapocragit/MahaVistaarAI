package com.ai.ai_disc;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Upload_activity extends AppCompatActivity {

    private static final String TAG = "Upload_activity";
    @BindView(R.id.grid_view)
    RecyclerView gvGallery;

    @BindView(R.id.image)
    Button select_image;

    @BindView(R.id.take_photo)
    Button take_photo;

    // GalleryAdapter galleryAdapter;
    GalleryAdapter1 galleryAdapter1;
    InternetReceiver internet;

    @BindView(R.id.simpleViewAnimator)
    ViewAnimator simpleViewAnimator;

    @BindView(R.id.add_image)
    ImageView add_image;

    @BindView(R.id.layout_bottomsheet)
    LinearLayout layout_bottom_sheet;

    BottomSheetBehavior sheetBehavior;
    int SELECT_IMAGE_CODE = 1;
    int TAKE_IMAGE_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC");
        internet = new InternetReceiver();
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       // Log.i(TAG, "value is 1" + simpleViewAnimator.getDisplayedChild());
        // System.out.println("v :"+simpleViewAnimator.getDisplayedChild());

        simpleViewAnimator.showNext();


        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

            }
        });

        sheetBehavior = BottomSheetBehavior.from(layout_bottom_sheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(Upload_activity.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                // permission is granted, open the camera

                               // Log.i(TAG, "onPermissionGranted: ");
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE_CODE);
                            //    Toast.makeText(Upload_activity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                // check for permanent denial of permission
                               // Log.i(TAG, "onPermissionDenied 1: ");
                                // if (response.isPermanentlyDenied()) {
                                // navigate user to app settings
                               // Log.i(TAG, "onPermissionDenied: ");
                                Toast.makeText(Upload_activity.this, "Permission is required.", Toast.LENGTH_LONG).show();
                                // }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(Upload_activity.this)
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    // do you work now

                                    Intent intent = new Intent(Upload_activity.this, CAM2.class);
                                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                                } else {
                                    Toast.makeText(Upload_activity.this, "Permission is required.", Toast.LENGTH_LONG).show();
                                }


                                // check for permanent denial of any permission
                             //   if (report.isAnyPermissionPermanentlyDenied()) {
                                    // permission is denied permenantly, navigate user to app settings
                              //  }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .check();

            }
        });


    }


    /*
    public boolean checking_permision(){


        if ((ContextCompat.checkSelfPermission(Upload_activity.this, Manifest.permission.CAMERA)  != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(Upload_activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Upload_activity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return false;

        }



        return  true;
    }

     */


    @Override
    public void onActivityResult(int requestcode, int responsecode, Intent data) {
        super.onActivityResult(requestcode, responsecode, data);
        switch (requestcode) {

            case 1:

                if (requestcode == SELECT_IMAGE_CODE && responsecode == RESULT_OK && data != null) {


                    if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }

                    //  System.out.println("value is:"+simpleViewAnimator.getDisplayedChild());

                    if (simpleViewAnimator.getDisplayedChild() == 1) {
                        simpleViewAnimator.setDisplayedChild(0);
                    }


                    if (data.getData() != null) {

                        Uri mImageUri = data.getData();


                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        mArrayUri.add(mImageUri);


                        galleryAdapter1 = new GalleryAdapter1(this, mArrayUri);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        gvGallery.setLayoutManager(mLayoutManager);
                        gvGallery.setItemAnimator(new DefaultItemAnimator());
                        gvGallery.setAdapter(galleryAdapter1);


                    } else {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri);


                            }

                            galleryAdapter1 = new GalleryAdapter1(this, mArrayUri);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            gvGallery.setLayoutManager(mLayoutManager);
                            gvGallery.setItemAnimator(new DefaultItemAnimator());
                            gvGallery.setAdapter(galleryAdapter1);


                        }
                    }

                } else {
                    Toast.makeText(Upload_activity.this, "File is not selected. ", Toast.LENGTH_LONG).show();
                }

                break;


            case 4:


                if (requestcode == TAKE_IMAGE_CODE && responsecode == RESULT_OK && data != null) {


                    if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }

                    System.out.println("value is:" + simpleViewAnimator.getDisplayedChild());

                    if (simpleViewAnimator.getDisplayedChild() == 1) {
                        simpleViewAnimator.setDisplayedChild(0);
                    }

                    Uri mImageUri = Uri.parse(data.getStringExtra("url"));

                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    mArrayUri.add(mImageUri);


                    galleryAdapter1 = new GalleryAdapter1(this, mArrayUri);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    gvGallery.setLayoutManager(mLayoutManager);
                    gvGallery.setItemAnimator(new DefaultItemAnimator());
                    gvGallery.setAdapter(galleryAdapter1);


                } else {
                    Toast.makeText(Upload_activity.this, "Some Error.", Toast.LENGTH_LONG).show();

                }


                break;


            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu_add_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_image:


                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                break;

            case android.R.id.home:


                finish();


                break;

            default:
                break;

        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);


    }


    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(internet);

    }


}
