package in.gov.mahapocra.mahavistaarai.chms;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.AlertListEventListener;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.listener.AsyncResponse;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.sma.APIRequest;
import in.gov.mahapocra.mahavistaarai.sma.APIServices;
import in.gov.mahapocra.mahavistaarai.sma.AppSession;
import in.gov.mahapocra.mahavistaarai.sma.ProfileModel;
import in.gov.mahapocra.mahavistaarai.util.AppConstants;
import in.gov.mahapocra.mahavistaarai.util.app_util.AppString;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import okhttp3.MediaType;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CropImageRandomForCA extends AppCompatActivity implements ApiJSONObjCallback, AlertListEventListener, ApiCallbackCode, OnMultiRecyclerItemClickListener, AsyncResponse {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private LinearLayout imageContainer;
    private Button addButton, removeButton, btnSave, submitButton,btnImageCount;
    private List<View> dynamicViews;
    private ImageButton cameraButton;
    private TextView cuurentDateText;
    private EditText edtSurveyNumber;
    private EditText edtArea;
    private RadioGroup radioGroup, radioGroup2;
    private RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    private int isFlagSetCategory = 0;
    private int isFlagSetDisease = 0;
    private RelativeLayout relPestType, relDiseaseType;
    private RecyclerView recyclerViewCropList;
    private AppSession session;
    private ProfileModel profileModel;
    private TextView txtSelectVillage,txtSelectDist,txtSelectTaluka;
    private JSONArray mVillageArray;
    private int districtID = 0;
    private int talukaID = 0;
    private int villageID = 0;
    private String currentDate;
    private TextView txtSelectCropStage;
    private JSONArray mCropStageArray;
    private int cropStageId = 0;
    private TextView txtSelectCropPest;
    private JSONArray mCropPestArray;
    private JSONArray mDistArray;
    private JSONArray mTalukaArray;
    private int cropPestId = 0;
    private TextView txtSelectCropList;
    private JSONArray mCropArray;
    private int cropId = 0;
    private TextView txtSelectCropDisease;
    private JSONArray mCropDieaseArray;
    private int cropDiseaseId = 0;
    private EditText edtFarmerName;
    //    private JSONArray mFarmerListArray;
//    private int farmerId = 0;
    private String formNo = "0";
    private JSONArray mImageListArray;
    private TextView txtSelectSection;
    private JSONArray mImageSectionArray;
    private int sectionId = 0;
    private double latitude = 0;
    private double longitude = 0;
    private int croppingSystem = CroppingSystem.SOLE.id();
    private ArrayList<Bitmap> capturedImages;
    private List<String> categoryImgOptions;
    private ArrayList<String> selectedCategories;
    private ArrayList<File> imageFiles;
    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private File photoFile = null;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_CODE = 22;
    private File imgFile = null;
    private File file = null;
    private int fRegisterId;
//    public AppLocationManager appLocationManager;

    //    private TextView txtSelectImgSection;
//    private JSONArray mCropImgSection;
//    private int cropImgSectionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image_random_for_ca);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        init();
        session = new AppSession(this);
        profileModel = session.getProfileModel();
//        userId = String.valueOf(profileModel.getId());
//        Log.d("Mayu111", "userId==" + userId);
        currentDate = getCurrentDate();
        cuurentDateText.setText(currentDate);
        dynamicViews = new ArrayList<>();
        Log.d("MAYU111", "dynamicViews" + dynamicViews.size());
        capturedImages = new ArrayList<>();
        imageFiles = new ArrayList<>();
        fRegisterId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0);
        Log.d("REGISTER_ID", "fREGISTER_ID = " + fRegisterId);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dynamicViews.size() >= 1) {
                    Toast.makeText(CropImageRandomForCA.this, "Please Save your Data ", Toast.LENGTH_SHORT).show();
                    return;
                }
                addNewItem();

            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLastItem();
            }
        });
        radioButton1.setChecked(true);
        radioButton3.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioButton1.getId()) {
                    // RadioButton 1 is selected
                    radioGroup2.setVisibility(View.VISIBLE);
                    relPestType.setVisibility(View.VISIBLE);
                    // relDiseaseType.setVisibility(View.VISIBLE);
                    isFlagSetCategory = 0;
                    Toast.makeText(CropImageRandomForCA.this, "Infected Crop selected", Toast.LENGTH_SHORT).show();
                } else if (checkedId == radioButton2.getId()) {
                    // RadioButton 2 is selected
                    radioGroup2.setVisibility(View.GONE);
                    relPestType.setVisibility(View.GONE);
                    relDiseaseType.setVisibility(View.GONE);
                    isFlagSetCategory = 1;
                    Toast.makeText(CropImageRandomForCA.this, "Normal Crop selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioButton3.getId()) {
                    // RadioButton 3 is selected
                    relPestType.setVisibility(View.VISIBLE);
                    relDiseaseType.setVisibility(View.GONE);
                    isFlagSetDisease = 0;
                    Toast.makeText(CropImageRandomForCA.this, "Pest selected", Toast.LENGTH_SHORT).show();
                } else if (checkedId == radioButton4.getId()) {
                    // RadioButton 4 is selected
                    relPestType.setVisibility(View.GONE);
                    relDiseaseType.setVisibility(View.VISIBLE);
                    isFlagSetDisease = 1;
                    Toast.makeText(CropImageRandomForCA.this, "Disease selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtSelectDist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDistrict();
            }
        });
        txtSelectTaluka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTaluka();
            }
        });
        txtSelectVillage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVillage();
            }
        });
        txtSelectCropStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCropStage();
            }
        });
        txtSelectCropList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCrops();
            }
        });
        txtSelectCropPest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCropPest();
            }
        });
        txtSelectCropDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCropDisease();
            }
        });
//                txtSelectFarmer.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showVillageFarmerList();
//                    }
//                });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strFarmer = edtFarmerName.getText().toString();
                String strSurveyNumber = edtSurveyNumber.getText().toString();
//                String strArea = edtArea.getText().toString();
                if (districtID == 0) {
                    Toast.makeText(CropImageRandomForCA.this, "Please Select District ", Toast.LENGTH_SHORT).show();
                }
                if (talukaID == 0) {
                    Toast.makeText(CropImageRandomForCA.this, "Please Select Taluka ", Toast.LENGTH_SHORT).show();
                }
                if (villageID == 0) {
                    Toast.makeText(CropImageRandomForCA.this, "Please Select Village ", Toast.LENGTH_SHORT).show();
                }
                else if(strFarmer.isEmpty() )
                {
                    Toast.makeText(CropImageRandomForCA.this, "Please Enter Farmer Name ", Toast.LENGTH_SHORT).show();
                }
                else if(strSurveyNumber.isEmpty() )
                {
                    Toast.makeText(CropImageRandomForCA.this, "Please Enter Survey Number ", Toast.LENGTH_SHORT).show();
                }
//                else if(strArea.isEmpty() )
//                {
//                    Toast.makeText(CropImageRandomForCA.this, "Please Enter Area ", Toast.LENGTH_SHORT).show();
//                }
                else if (cropId == 0) {
                    Toast.makeText(CropImageRandomForCA.this, "Please Select Crop ", Toast.LENGTH_SHORT).show();
                }
                else if (cropStageId == 0) {
                    Toast.makeText(CropImageRandomForCA.this, "Please Select Crop Stage ", Toast.LENGTH_SHORT).show();
                }else if (capturedImages.isEmpty() ) {
                    Toast.makeText(CropImageRandomForCA.this, "Please Add Image ", Toast.LENGTH_SHORT).show();
                }
//                    else if (file ==  null ) {
//                        Toast.makeText(CropImageRandomForCA.this, "Please Add Image  ", Toast.LENGTH_SHORT).show();
//                    }

                else if (isFlagSetCategory == 0 && isFlagSetDisease == 0 && cropPestId == 0) {
                    Toast.makeText(CropImageRandomForCA.this, "Please Select Pest Type ", Toast.LENGTH_SHORT).show();
                } else if (isFlagSetCategory == 0 && isFlagSetDisease == 1 && cropDiseaseId == 0){
                    Toast.makeText(CropImageRandomForCA.this, "Please Select Disease Type ", Toast.LENGTH_SHORT).show();
                } else if(sectionId == 0){
                    Toast.makeText(CropImageRandomForCA.this, "Please Select section ", Toast.LENGTH_SHORT).show();
                } else {
                    imageUploadRequest(selectedCategories, capturedImages);
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {   // For Multiple Images
            @Override
            public void onClick(View v) {
                fetchUpdateFarmerStatus(formNo);
//                if (capturedImages.isEmpty()) {
//                    Toast.makeText(CropImage.this, "No images captured", Toast.LENGTH_SHORT).show();
//                } else {
////                    selectedCategories.add(categorySpinner.getSelectedItem().toString());
//                    //postImages(selectedCategories, capturedImages);
//                    Intent intent = new Intent (CropImage.this, DashboardActivity.class);
//                    startActivity (intent);
//                  // String selectedCategory = categorySpinner.getSelectedItem().toString();
//                  //  Log.d("MAYU111","selectedCategory="+selectedCategory);
//                    Toast.makeText(CropImage.this, "Data Saved Succesfully", Toast.LENGTH_SHORT).show();
////                    for (Bitmap bitmap : capturedImages) {
////                        File imageFile = saveBitmapToFile(CropImage.this, bitmap);
////                        if (imageFile != null) {
////                            imageFiles.add(imageFile);
////                        }
////                    }
//                    Log.d("MAYU111","selectedCategories Size="+selectedCategories.size());
//                    Log.d("MAYU111","selectedCategories List="+selectedCategories);
//                    Log.d("MAYU111","capturedImages Size="+capturedImages.size());
//                    Log.d("MAYU111","imageFiles List="+imageFiles);
//                   // selectedCategories.clear();
//                  //  postImages(selectedCategory, capturedImages);
//                   // postImages2(selectedCategories, imageFiles);
//                   // imageUploadRequest(selectedCategories, imageFiles);
//                   // imageUploadRequest(selectedCategories, capturedImages);  // correct one
//                }
            }
        });
    }

    private void onImageAction() {

        if (Build.VERSION.SDK_INT < 23) {
            //Do not need to check the permission
            DebugLog.getInstance().d("No need to check the permission");
            captureCamera();
        } else {

            if (checkPermissionsIsEnabledOrNot()) {
                //If you have already permitted the permission
                captureCamera();
            } else {
                requestMultiplePermission();
            }
        }
    }

    private void captureCamera() {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {

        AIImageLoadingUtil aiImageLoadingUtil = new AIImageLoadingUtil(this);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = aiImageLoadingUtil.createImageFile2(ImageTypes.CHMSIMG.id());
            } catch (Exception ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created

            if (photoFile != null) {
                Uri mImgURI;

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    mImgURI = Uri.fromFile(photoFile);
                } else {
//                    mImgURI = FileProvider.getUriForFile(getApplication().getApplicationContext(),
//                            "ibas.provider", photoFile);
                    mImgURI = FileProvider.getUriForFile(this,
                            "in.gov.mahapocra.mahavistaarai.android.fileprovider",
                            photoFile);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImgURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> listCam = packageManager.queryIntentActivities(takePictureIntent, 0);
                ResolveInfo res = listCam.get(0);
                String packageName = res.activityInfo.packageName;
                Intent intent = new Intent(takePictureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(packageName);
                startActivityForResult(intent, CAMERA_CODE);
                DebugLog.getInstance().d("Camera Package Name=" + packageName);
                DebugLog.getInstance().d("mImgURI=" + mImgURI);
            }
        }
    }

    private boolean checkPermissionsIsEnabledOrNot() {


        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return permissionCamera == PackageManager.PERMISSION_GRANTED &&
                permissionWrite == PackageManager.PERMISSION_GRANTED;
    }
    private void requestMultiplePermission() {

        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_CODE);
    }

    private void init() {
        cuurentDateText = findViewById(R.id.cuurentDate);
        edtFarmerName = findViewById(R.id.edtFarmerName);
        edtArea = findViewById(R.id.edtArea);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup2 = findViewById(R.id.radioGroup2);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        relPestType = findViewById(R.id.relativeLayout_PestType);
        relDiseaseType = findViewById(R.id.relativeLayout_DiseaseType);
        txtSelectDist = findViewById(R.id.tvSelectDist);
        txtSelectTaluka = findViewById(R.id.tvSelectTaluka);
        txtSelectVillage = findViewById(R.id.tvSelectVillage);
        txtSelectCropStage = findViewById(R.id.tvSelectCropStage);
        txtSelectCropList = findViewById(R.id.tvSelectCrops);
        txtSelectCropPest = findViewById(R.id.tvSelectPestType);
        txtSelectCropDisease = findViewById(R.id.tvSelectDiseaseType);
        imageContainer = findViewById(R.id.imageContainer);
        addButton = findViewById(R.id.addImageButton);
        removeButton = findViewById(R.id.removeImageButton);
        btnImageCount = findViewById(R.id.btnImageCount);
        edtSurveyNumber = findViewById(R.id.edtSurveyNumber);
        submitButton = findViewById(R.id.btnSubmit);
        recyclerViewCropList = findViewById(R.id.recyclerViewCropList);
        btnSave = findViewById(R.id.btnSave);
    }
//    public void performAction(View view) {
//        if (isFlagSetPest) {
//            // Perform action when flag is set
//            Toast.makeText(CropTest.this, "Flag is set", Toast.LENGTH_SHORT).show();
//        } else {
//            // Perform action when flag is not set
//            Toast.makeText(CropTest.this, "Flag is not set", Toast.LENGTH_SHORT).show();
//        }
//    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {  // For multiple Images
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private String getCurrentDate() {
        // Create a SimpleDateFormat object with the desired date format
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        // Get the current date
        Date currentDate = new Date();
        // Format the current date as per the SimpleDateFormat object
        return sdf.format(currentDate);
    }

//    public void showVillage() {
//        if (mVillageArray == null) {
////            fetchVillage(Integer.parseInt(userId));
//            fetchVillage();
//        } else {
//            AppUtility.getInstance().showListDialogIndex(mVillageArray, 1, getString(R.string.TcVillage), "name", "id", this, this);
//        }
//    }

//    private void fetchVillage() {
//
//        String url = "https://sso-ndksp.mahapocra.gov.in/v23/masterService/get-village-on-taluka/4015";
////                APIServices.kCAId + userID;
//        Log.d("Mayu111", "URL===" + url);
//        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), false);
//        Log.d("Mayu111", "URL===" +APIServices.SSO+url);
//        //  AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);
//        //  AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API, new AppSession(this).getToken(), new AppString(this).getkMSG_WAIT(), true);
//        api.getRequestData(url, this, 1);
//        DebugLog.getInstance().d(url);
//    }

    private void fetchAllCrops() {

        String url = APIServices.BASE_URL_CHMS + APIServices.kAllCrops;
        Log.d("Mayu111", "URL===" + url);
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_CHMS, "", new AppString(this).getkMSG_WAIT(), false);
        Log.d("Mayu111", "URL===" +APIServices.BASE_URL_CHMS);
        //  AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);
        //  AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API, new AppSession(this).getToken(), new AppString(this).getkMSG_WAIT(), true);
        api.getRequestData(url, this, 3);
        DebugLog.getInstance().d(url);
    }
    public void showCrops() {
        if (mCropArray == null) {
//            fetchCrops();
            fetchAllCrops();
        } else {
            AppUtility.getInstance().showListDialogIndex(mCropArray, 3, getString(R.string.Cropname), "name", "id", this, this);
        }
    }

    public void showDistrict() {
        if (mDistArray == null) {
            fetchDistrict();
        } else {
            AppUtility.getInstance().showListDialogIndex(mDistArray, 11, getString(R.string.Alldistrict), "name", "code", this, this);
        }
    }
    private void fetchDistrict() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lang", "en");
            Log.d("Mayu111","croppingSystemId=="+croppingSystem);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_CHMS, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<com.google.gson.JsonObject> responseCall = apiRequest.fetchDistrict(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 11);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void showTaluka() {
        if (mTalukaArray == null) {
            fetchTaluka(String.valueOf(districtID));
        } else {
            AppUtility.getInstance().showListDialogIndex(mTalukaArray, 12, getString(R.string.Alltaluka), "name", "code", this, this);
        }
    }
    private void fetchTaluka(String districtID) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lang", "en");
            jsonObject.put("district_code", districtID);
            Log.d("Mayu111","districtID=="+districtID);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_CHMS, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<com.google.gson.JsonObject> responseCall = apiRequest.fetchTaluka(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 12);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void showVillage() {
        if (mVillageArray == null) {
            fetchVillage(String.valueOf(talukaID));
        } else {
            AppUtility.getInstance().showListDialogIndex(mVillageArray, 13, getString(R.string.TcVillage), "name", "code", this, this);
        }
    }
    private void fetchVillage(String talukaID) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lang", "en");
            jsonObject.put("taluka_code", talukaID);
            Log.d("Mayu111","talukaID=="+talukaID);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_CHMS, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<com.google.gson.JsonObject> responseCall = apiRequest.fetchVillage(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 13);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showCropStage() {
        if (mCropStageArray == null) {
            fetchCropStage();
        } else {
            AppUtility.getInstance().showListDialogIndex(mCropStageArray, 2, getString(R.string.CropStage), "name", "id", this, this);
        }
    }

    private void fetchCropStage() {

        String url = APIServices.BASE_URL_CHMS + APIServices.kCropStages;
        Log.d("Mayu111", "URL===" + url);
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_CHMS, new AppSession(this).getToken(), new AppString(this).getkMSG_WAIT(), true);
        api.getRequestData(url, this, 2);
        DebugLog.getInstance().d(url);
    }

    public void showCropPest() {
        if (mCropPestArray == null) {
            fetchCropPest(cropId);
        } else {
            AppUtility.getInstance().showListDialogIndex(mCropPestArray, 4, getString(R.string.CropPest), "name", "id", this, this);
        }
    }

    private void fetchCropPest(int strCropId) {
        try {
            JSONObject jsonObject = new JSONObject();
            // Log.d("Mayu111","croppingSystemId=="+croppingSystem);
            Log.d("Mayu111", "fetchCropPest crop_id==" + cropId);
            jsonObject.put("crop_id", strCropId);
            // jsonObject.put("cropping_system_id", croppingSystem);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_CHMS, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<com.google.gson.JsonObject> responseCall = apiRequest.fetchCropsPestForCA(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 4);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showCGMImageSection() {
        if (mImageSectionArray == null) {
            fetchCGMImageSectionType(cropStageId, cropId);
        } else {
            AppUtility.getInstance().showListDialogIndex(mImageSectionArray, 10, getString(R.string.CropPest), "name", "id", this, this);
        }
    }

    private void fetchCGMImageSectionType(int strcropStageId, int strCropId) {
        try {
            JSONObject jsonObject = new JSONObject();
            // Log.d("Mayu111","croppingSystemId=="+croppingSystem);
            Log.d("Mayu111", "fetchCGMImageSectionType stage_id==" + strcropStageId);
            Log.d("Mayu111", "fetchCGMImageSectionType crop_id==" + cropId);
            jsonObject.put("stage_id", strcropStageId);
            jsonObject.put("crop_id", strCropId);
            // jsonObject.put("cropping_system_id", croppingSystem);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_CHMS, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<com.google.gson.JsonObject> responseCall = apiRequest.fetchkImageSectionForCA(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 10);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showCropDisease() {
        if (mCropDieaseArray == null) {
            fetchCropDisease(cropId);
        } else {
            AppUtility.getInstance().showListDialogIndex(mCropDieaseArray, 5, getString(R.string.Cropdisease), "name", "id", this, this);
        }
    }

    private void fetchCropDisease(int cropId) {

        try {
            JSONObject jsonObject = new JSONObject();
            // Log.d("Mayu111","croppingSystemId=="+croppingSystem);
            Log.d("Mayu111", "fetchCropDisease crop_id==" + cropId);
            jsonObject.put("crop_id", cropId);
            // jsonObject.put("cropping_system_id", croppingSystem);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_CHMS, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<com.google.gson.JsonObject> responseCall = apiRequest.fetchCropsDieaseForCA(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 5);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//            public void showVillageFarmerList() {
//                if (mFarmerListArray == null) {
//                    fetchVillageFarmerList(villageID);
//                } else {
//                    AppUtility.getInstance().showListDialogIndex(mFarmerListArray, 7, getString(R.string.farmerFromCAVillages), "name", "id", this, this);
//                }
//            }

//    private void fetchVillageFarmerList(int villageID) {
//
//        try {
//            JSONObject jsonObject = new JSONObject();
//            // Log.d("Mayu111","croppingSystemId=="+croppingSystem);
//            Log.d("Mayu111", "fetchCropDisease village_id==" + villageID);
//            jsonObject.put("village_id", villageID);
//            // jsonObject.put("cropping_system_id", croppingSystem);
//            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
//
//            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
//            Retrofit retrofit = api.getRetrofitInstance();
//            APIRequest apiRequest = retrofit.create(APIRequest.class);
//            Call<com.google.gson.JsonObject> responseCall = apiRequest.fetchFarmerListForCA(requestBody);
//
//            DebugLog.getInstance().d("param=" + responseCall.request().toString());
//            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
//
//            api.postRequest(responseCall, this, 7);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void fetchImageList(String formNoId) {

        try {
            JSONObject jsonObject = new JSONObject();
            Log.d("Mayu111", "fetchCropDisease formNoId==" + formNoId);
            jsonObject.put("form_no", formNoId);
            // jsonObject.put("cropping_system_id", croppingSystem);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<com.google.gson.JsonObject> responseCall = apiRequest.fetchkCHMSFarmerCropListForTC(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 8);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchUpdateFarmerStatus(String formNoId) {

        try {
            JSONObject jsonObject = new JSONObject();
            Log.d("Mayu111", "fetchCropDisease fRegisterId==" + fRegisterId);
//            jsonObject.put("form_no", formNoId);
            jsonObject.put("user_id", fRegisterId);
            // jsonObject.put("cropping_system_id", croppingSystem);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_CHMS, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<com.google.gson.JsonObject> responseCall = apiRequest.fetchUpdatefarmerStatusForTC(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 9);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//            private void fetchRegisterdfarmerData() {
//                try {
//                    JSONObject jsonObject = new JSONObject();
//                    // Log.d("Mayu111","fetchCropDisease village_id=="+villageID);
//                    jsonObject.put("village_id", villageID);
//                    jsonObject.put("farmer_id", farmerId);
//                    // jsonObject.put("cropping_system_id", croppingSystem);
//                    RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
//
//                    AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
//                    Retrofit retrofit = api.getRetrofitInstance();
//                    APIRequest apiRequest = retrofit.create(APIRequest.class);
//                    Call<JsonObject> responseCall = apiRequest.fetchRegisterdfarmerDataForCA(requestBody);
//
//                    DebugLog.getInstance().d("param=" + responseCall.request().toString());
//                    DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
//
//                    api.postRequest(responseCall, this, 3);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

    private void addNewItem() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.dynamic_view, imageContainer, false);
        // categorySpinner = itemView.findViewById(R.id.categorySpinner);
        txtSelectSection = itemView.findViewById(R.id.tvSelectSection);
        cameraButton = itemView.findViewById(R.id.cameraButton);

        selectedCategories = new ArrayList<>();
        txtSelectSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fetchCGMImageSectionType(cropStageId,cropId);
                showCGMImageSection();
            }
        });

//        selectedCategories = new ArrayList<>();

//        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedValue = parent.getItemAtPosition(position).toString();
////                selectedValues.add(selectedValue);
//
//                selectedCategories.add(selectedValue);
//                Log.d("MAYU111","categorySpinner selectedCategories="+selectedCategories);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Do nothing
//            }
//        });
        removeButton.setVisibility(View.GONE);
//        selectedCategories.add(categorySpinner.getSelectedItem().toString());
//        Log.d("MAYU111","addNewItem selectedCategories="+selectedCategories);
        // Handle camera button click
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //captureImage();
                if (checkCameraPermission()) {
                    //captureImage(); - working
                    addImage();  // for multiple images
//                        onImageAction();
                } else {
                    requestCameraPermission();
                }
            }
        });
        imageContainer.addView(itemView);
        dynamicViews.add(itemView);
    }

    private void removeLastItem() {
        if (!dynamicViews.isEmpty()) {
            View lastView = dynamicViews.get(dynamicViews.size() - 1);
            imageContainer.removeView(lastView);
            dynamicViews.remove(lastView);
        } else {
            Toast.makeText(this, "No views to remove", Toast.LENGTH_SHORT).show();
        }
    }

    //    private void removeAllDynamicItems() {
//
//        if (!dynamicViews.isEmpty()) {
//        int itemCount = dynamicViews.size();
//         if (itemCount > 0) {
//            dynamicViews.clear();
//          }
//        } else {
//            Toast.makeText(this, "No views to remove", Toast.LENGTH_SHORT).show();
//        }
//    }
    private void removeAllDynamicViews() {
        if (!dynamicViews.isEmpty()) {
            for (View dynamicView : dynamicViews) {
                imageContainer.removeView(dynamicView);
            }
            dynamicViews.clear();
        } else {
            Toast.makeText(this, "No views to remove", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkCameraPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

//                    Uri imageUri = data.getData();
//
//                    // Use the imageUri to access the captured image file
//                    File imageFile = new File(imageUri.getPath());
//                    cameraButton.setImageURI(imageUri);

            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    capturedImages.add(imageBitmap);// for multiple images
                    cameraButton.setImageBitmap(imageBitmap);
                    // requestLocationUpdates();
                    captureLatLong();
                }
            }
        } else {
            Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
        }
    }


//        @Override
//        protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
//
//            super.onActivityResult(requestCode, resultCode, data);
//            if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
//
//          /*  Bitmap photo = (Bitmap) data.getExtras().get("data");
//            attendanceImageView.setImageBitmap(photo);
//            Uri tempUri = getImageUri(getApplicationContext(), photo);
//
//            imgFile = new File(getRealPathFromURI(tempUri));
//            imageUploadRequest();
//            DebugLog.getInstance().d("fileNameIs" + imgFile.getAbsolutePath());
//
//            String latLong = String.valueOf(appLocationManager.getLatitude())+"_"+String.valueOf(appLocationManager.getLongitude());
//            AppSettings.getInstance().setValue(this, AppConstants.kATTENDANCE_FILE_LOC, latLong);*/
//
//                AIImageLoadingUtils aiImageLoadingUtil = new AIImageLoadingUtils(this);
//
//                AIImageCompressionClearImg asyncTask = new AIImageCompressionClearImg(aiImageLoadingUtil, photoFile, this);
//                asyncTask.execute("");
//                captureLatLong();
//
//            }
//        }

    private void addImage() {   // For multiple images
        if (capturedImages.size() >= 5) {
            Toast.makeText(this, "Maximum number of images reached", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkCameraPermission()) {
            captureImage();
        } else {
            requestCameraPermission();
        }
    }

    // Convert Bitmap to File and save it as a temporary image file
    private File saveBitmapToFile(Context context, Bitmap bitmap) {
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outStream = new FileOutputStream(file);

            int targetWidth = 800; // Set your desired width
            int targetHeight = 600; // Set your desired height
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
            outStream.flush();
            outStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert ArrayList of File to MultipartBody.Part list
    private ArrayList<MultipartBody.Part> convertFilesToMultipart(ArrayList<File> fileList) {
        ArrayList<MultipartBody.Part> parts = new ArrayList<>();
        for (File file : fileList) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
            parts.add(part);
        }
        return parts;
    }


    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void imageUploadRequest(ArrayList<String> categories, ArrayList<Bitmap> capturedImages) {
        try {
            AppSession session = new AppSession(this);
            String stredtFarmerName = edtFarmerName.getText().toString();
            String stredtSurveyNo = edtSurveyNumber.getText().toString();
//            String stredtArea = edtArea.getText().toString();
            Log.d("MAYU111", "edtSurveyNumber=" + stredtSurveyNo);
//            Log.d("MAYU111", "edtArea=" + stredtArea);

            MultipartBody.Part partBody = null;
            MultipartBody.Part partBody2 = null;

            Map<String, RequestBody> params = new HashMap<>();
            String userId = String.valueOf(session.getUserId());
            File imageFile = null;
            for (Bitmap bitmap : capturedImages) {
                imageFile = saveBitmapToFile(CropImageRandomForCA.this, bitmap);
                if (imageFile != null) {
                    imageFiles.add(imageFile);
                }
            }
            params.put("date", AppinventorIncAPI.toRequestBody(currentDate));
            //   params.put("farmer_id", AppinventorApi.toRequestBody(String.valueOf(farmerId)));
            params.put("category", AppinventorIncAPI.toRequestBody(String.valueOf(isFlagSetCategory)));
//            params.put("district", AppinventorIncAPI.toRequestBody(String.valueOf(districtID))); // new added
//            params.put("taluka", AppinventorIncAPI.toRequestBody(String.valueOf(talukaID))); // new added
            params.put("village", AppinventorIncAPI.toRequestBody(String.valueOf(villageID)));
            params.put("farmer_name", AppinventorIncAPI.toRequestBody(stredtFarmerName));
            params.put("survey_no", AppinventorIncAPI.toRequestBody(stredtSurveyNo));
            params.put("area", AppinventorIncAPI.toRequestBody("0"));
            params.put("user_id", AppinventorIncAPI.toRequestBody(String.valueOf(fRegisterId)));
            params.put("crop", AppinventorIncAPI.toRequestBody(String.valueOf(cropId)));
            params.put("stage", AppinventorIncAPI.toRequestBody(String.valueOf(cropStageId)));
            if (isFlagSetCategory == 0) {
                if (isFlagSetDisease == 0) {
                    params.put("pest", AppinventorIncAPI.toRequestBody(String.valueOf(cropPestId)));
                    Log.d("MAYU111", "imageUploadRequest cropPestId==" + cropPestId);
                    //params.put("disease", AppinventorApi.toRequestBody("0"));
                } else {
                    //params.put("pest", AppinventorApi.toRequestBody("0"));
                    params.put("disease", AppinventorIncAPI.toRequestBody(String.valueOf(cropDiseaseId)));
                    Log.d("MAYU111", "imageUploadRequest cropDiseaseId==" + cropDiseaseId);
                }
            }
            params.put("lat", AppinventorIncAPI.toRequestBody(String.valueOf(latitude)));
            params.put("long", AppinventorIncAPI.toRequestBody(String.valueOf(longitude)));
            params.put("section", AppinventorIncAPI.toRequestBody(String.valueOf(sectionId)));
//            params.put("form_no", AppinventorIncAPI.toRequestBody(formNo));
            // params.put("section", AppinventorApi.toRequestBody(String.valueOf(categories)));
            Log.d("MAYU111", "imageUploadRequest param==" + params);
            Log.d("MAYU111", "imageUploadRequest categories==" + categories);
            //   Log.d("MAYU111", "imageUploadRequest farmerId==" + farmerId);
//            Log.d("MAYU111", "imageUploadRequest formNo==" + formNo);
            Log.d("MAYU111", "imageUploadRequest lat==" + latitude);
            Log.d("MAYU111", "imageUploadRequest long==" + longitude);
            Log.d("MAYU111", "imageUploadRequest userId==" + fRegisterId);
//            Log.d("MAYU111", "imageUploadRequest Area==" + stredtArea);
            Log.d("MAYU111", "imageUploadRequest param==" + "date=" + currentDate + "category=" + isFlagSetCategory + "survey_no=" + stredtSurveyNo + "village=" + villageID + "farmer_name=" + stredtFarmerName + "crop=" + cropId + "stage=" + cropStageId);
            Log.d("MAYU111", "imageUploadRequest param==" + "lat=" + latitude + "long=" + longitude +"user_id=" + fRegisterId + "image=" + imageFile + "section=" + sectionId + "pest=" + cropPestId + "disease=" + cropDiseaseId);

//                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), capturedImages);
//                partBody = MultipartBody.Part.createFormData("image_name", String.valueOf(capturedImages), reqFile);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            partBody = MultipartBody.Part.createFormData("image", String.valueOf(imageFile), reqFile);


            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_CHMS, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();

            //creating our api
            APIRequest apiRequest = retrofit.create(APIRequest.class);

            //creating a call and calling the upload image method
            Call<JsonObject> responseCall = apiRequest.uploadImagesRequestImgCropTC(partBody,params);
            api.postRequest(responseCall, this, 6);
            Log.d("MAYU111", "url: " + responseCall.request().url().toString());
            DebugLog.getInstance().d("MAYU111 param=" + responseCall.request().toString());
            DebugLog.getInstance().d("MAYU111 param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object obj) {

    }

    @Override
    public void asyncProcessFinish(Object output) {
        String result = (String) output;
        imgFile = new File(result);
        Picasso.get()
                .load(imgFile)
                .fit()
                .into(cameraButton);
        file = new File(imgFile.getPath());
    }

    public static class ImageJsonConverter {

        public static JSONObject createJsonFromImageList(ArrayList<String> imagePaths) {
            JSONObject jsonObject = new JSONObject();
            try {
                // Convert the ArrayList to a JSONArray
                JSONArray jsonArray = new JSONArray(imagePaths);

                // Add the JSONArray to the JSONObject
                jsonObject.put("image_name", jsonArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();

            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void didSelectListItem(int i, String str, String str2) {

        if (i == 11) {
            districtID = Integer.parseInt(str2);
            txtSelectDist.setText(str);
            Log.d("Mayu111", "didSelectListItem txtSelectDist===" + districtID);
            Log.d("Mayu111", "txtSelectDist===" + str);
            fetchTaluka(String.valueOf(districtID));
        }
        if (i == 12) {
            talukaID = Integer.parseInt(str2);
            txtSelectTaluka.setText(str);
            Log.d("Mayu111", "didSelectListItem txtSelectTaluka===" + talukaID);
            Log.d("Mayu111", "txtSelectTaluka===" + str);
            fetchVillage(String.valueOf(talukaID));
        }
        if (i == 13) {
            villageID = Integer.parseInt(str2);
            txtSelectVillage.setText(str);
            Log.d("Mayu111", "didSelectListItem villageID===" + villageID);
            Log.d("Mayu111", "txtSelectVillage===" + str);
//            fetchVillageFarmerList(villageID);
        }
        if (i == 2) {
            cropStageId = Integer.parseInt(str2);
            txtSelectCropStage.setText(str);
            Log.d("Mayu111", "didSelectListItem cropStageId===" + cropStageId);
            Log.d("Mayu111", "txtSelectCropStage===" + str);
            fetchCGMImageSectionType(cropStageId,cropId);

        }
        if (i == 3) {
            cropId = Integer.parseInt(str2);
            txtSelectCropList.setText(str);
            Log.d("Mayu111", "didSelectListItem cropId===" + villageID);
            Log.d("Mayu111", "txtSelectCropList===" + str);
            fetchCropStage();
        }

        if (i == 4) {
            cropPestId = Integer.parseInt(str2);
            txtSelectCropPest.setText(str);
            Log.d("Mayu111", "didSelectListItem cropPestId===" + cropPestId);
            Log.d("Mayu111", "txtSelectCropPest===" + str);

        }
        if (i == 5) {
            cropDiseaseId = Integer.parseInt(str2);
            txtSelectCropDisease.setText(str);
            Log.d("Mayu111", "didSelectListItem cropDiseaseId===" + cropDiseaseId);
            Log.d("Mayu111", "txtSelectCropDisease===" + str);

        }
//                if (i == 7) {
//                   // farmerId = Integer.parseInt(str2);
//                    txtSelectFarmer.setText(str);
//                    Log.d("Mayu111", "didSelectListItem farmerId===" + farmerId);
//                    Log.d("Mayu111", "txtSelectFarmer===" + str);
//                   // fetchRegisterdfarmerData();
//                }
        if (i == 10) {
            sectionId = Integer.parseInt(str2);
            txtSelectSection.setText(str);
            Log.d("Mayu111", "didSelectListItem sectionId===" + sectionId);
            Log.d("Mayu111", "txtSelectSection===" + str);
            selectedCategories.add(String.valueOf(sectionId));
            Log.d("MAYU111", "categorySpinner selectedCategories=" + selectedCategories);
        }
    }

    @Override
    public void onFailure(Object obj, Throwable th, int i) {

    }

    @Override
    public void onFailure(Throwable th, int i) {

    }

    public ArrayList<MultipartBody.Part> createStringArrayFormData(ArrayList<String> stringArray) {
        ArrayList<MultipartBody.Part> parts = new ArrayList<>();

        for (int i = 0; i < stringArray.size(); i++) {
            String stringData = stringArray.get(i);

            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), stringData);
            MultipartBody.Part part = MultipartBody.Part.createFormData("section", null, requestBody);
//            MultipartBody.Part part = MultipartBody.Part.createFormData("section" + i, null, requestBody);
            parts.add(part);

        }
        Log.d("MAYU111", "StringArrayFormData parts" + parts);
        return parts;
    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {
            if (i == 2) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);

                    if (response.getStatus()) {
                        mCropStageArray = response.getDataArray();
                        for (int m = 0; m < mCropStageArray.length(); m++) {
                            JSONObject actor = mCropStageArray.getJSONObject(m);
                        }

                    } else {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (i == 3) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);

                    if (response.getStatus()) {
                        mCropArray = response.getDataArray();
                        for (int m = 0; m < mCropArray.length(); m++) {
                            JSONObject actor = mCropArray.getJSONObject(m);
                        }

                    } else {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
//                    if (i == 3) {
//                        if (jsonObject != null) {
//                            DebugLog.getInstance().d("onResponse=" + jsonObject);
//                            ResponseModel response = new ResponseModel(jsonObject);
//
//                            if (response.getStatus()) {
//                                JSONObject mj = response.getData();
//                                try {
//                                    Log.d("MAYU111", " Crop Name 3" + mj.getString("crop"));
//                                    Log.d("MAYU111", " crop_id 3" + mj.getString("crop_id"));
//                                    txtSelectCropList.setText("Crop : " + mj.getString("crop"));
//                                    tvSurveyNumber.setText("SurveyNo : " + mj.getString("survey_number"));
//                                    tvSelectArea.setText("Area : " + mj.getString("area_under_crop"));
//                                    cropId = Integer.parseInt((mj.getString("crop_id")));
//
//                                    fetchCropPest(cropId);
//                                    fetchCropDisease(cropId);
//                                } catch (JSONException jsonException) {
//                                    jsonException.printStackTrace();
//                                }
//
//                            } else {
//                                Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
            if (i == 4) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);

                    if (response.getStatus()) {
                        mCropPestArray = response.getDataArray();
                        for (int m = 0; m < mCropPestArray.length(); m++) {
                            JSONObject actor = mCropPestArray.getJSONObject(m);
                        }

                    } else {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (i == 5) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);

                    if (response.getStatus()) {
                        mCropDieaseArray = response.getDataArray();
                        for (int m = 0; m < mCropDieaseArray.length(); m++) {
                            JSONObject actor = mCropDieaseArray.getJSONObject(m);
                        }
                    } else {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (i == 11) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);

                    if (response.getStatus()) {
                        mDistArray = response.getDataArray();
                        for (int m = 0; m < mDistArray.length(); m++) {
                            JSONObject actor = mDistArray.getJSONObject(m);
                        }

                    } else {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (i == 12) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);

                    if (response.getStatus()) {
                        mTalukaArray = response.getDataArray();
                        for (int m = 0; m < mTalukaArray.length(); m++) {
                            JSONObject actor = mTalukaArray.getJSONObject(m);
                        }

                    } else {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (i == 13) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);

                    if (response.getStatus()) {
                        mVillageArray = response.getDataArray();
                        for (int m = 0; m < mVillageArray.length(); m++) {
                            JSONObject actor = mVillageArray.getJSONObject(m);
                        }

                    } else {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
//                    if (i == 7) {
//                        if (jsonObject != null) {
//                            DebugLog.getInstance().d("onResponse=" + jsonObject);
//                            ResponseModel response = new ResponseModel(jsonObject);
//
//                            if (response.getStatus()) {
//                                mFarmerListArray = response.getDataArray();
//                                for (int m = 0; m < mFarmerListArray.length(); m++) {
//                                    JSONObject actor = mFarmerListArray.getJSONObject(m);
//                                }
//                            } else {
//                                mFarmerListArray.remove(0);
//                                Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
            if (i == 8) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);

                    if (response.getStatus()) {
                        mImageListArray = response.getDataArray();
//                        for (int m = 0; m < mFarmerListArray.length(); m++) {
//                            JSONObject actor = mFarmerListArray.getJSONObject(m);
//                        }
//
                        Log.d("MAYU111=", "mImageListArrayJSONArray=" + mImageListArray.toString());
                        ImageListCHMS imageAdapter = new ImageListCHMS(this, this, mImageListArray);
                        recyclerViewCropList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                        recyclerViewCropList.setAdapter(imageAdapter);
                        imageAdapter.notifyDataSetChanged();
                        String strCount=response.getCount();
                        btnImageCount.setVisibility(View.VISIBLE);
                        btnImageCount.setText("Image Count : "+strCount);

                    } else {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (i == 10) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);
                    if (response.getStatus()) {
                        mImageSectionArray = response.getDataArray();
                        for (int m = 0; m < mImageSectionArray.length(); m++) {
                            JSONObject actor = mImageSectionArray.getJSONObject(m);

                        }
                    } else {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if (i == 6 && jsonObject != null) {
                DebugLog.getInstance().d("onResponse=" + jsonObject.toString());
                ResponseModel response = new ResponseModel(jsonObject);
                Log.d("MAYU111", "onResponse=" + response.getStatus());
                if (response.getStatus()) {
                    sectionId = 0;
                    // file = null;
                    capturedImages.clear();
                    selectedCategories.clear();
                    removeAllDynamicViews();
                    JSONObject mj = response.getData();
                    try {
                        Log.d("MAYU111", "form_no Before==" + mj.getString("form_no"));
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_LONG).show();
                        formNo = ((mj.getString("form_no")));
                        Log.d("MAYU111", "form_no After==" + formNo);
                        fetchImageList(formNo);

                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                } else {
                    //  UIToastMessage.show(this, response.getResponse());
                    // fetchImageList();
                    capturedImages.clear();
                    selectedCategories.clear();
                    //  Toast.makeText(this, "Images submitted", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_LONG).show();
                }
            }
            if (i == 9 && jsonObject != null) {
                DebugLog.getInstance().d("onResponse=" + jsonObject.toString());
                ResponseModel response = new ResponseModel(jsonObject);
                Log.d("MAYU111", "onResponse=" + response.getStatus());
                if (response.getStatus()) {

                    Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent (CropImageRandomForCA.this, RandomObservationDashboardCA.class);
                    startActivity (intent);
                } else {
                    Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (mImageListArray == null ) {
            finish();
        } else {
            // Create the AlertDialog.Builder
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            // Set the title and message for the dialog
            alertDialogBuilder.setTitle("Crop Image");
            alertDialogBuilder.setMessage("Your data not saved please submit your data ");

            // Set a positive button with a click listener
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Code to perform when the user clicks the Yes button
                    dialog.dismiss();; // Close the activity and exit the application
                }
            });

//        // Set a negative button with a click listener
//        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Code to perform when the user clicks the No button
//                dialog.dismiss(); // Close the dialog and stay in the activity
//            }
//        });

            // Create and show the AlertDialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
    private String extractNumberFromString(String inputString) {
        // Regular expression pattern to match any continuous sequence of digits
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(inputString);

        // Find the first occurrence of a numeric sequence in the input string
        if (matcher.find()) {
            return matcher.group(); // Return the matched numeric sequence
        } else {
            return ""; // Return empty string if no numeric sequence is found
        }
    }

    private void captureLatLong()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.d("MAYU111","latitude="+latitude);
                    Log.d("MAYU111","longitude="+longitude);
                    // Now you have the latitude and longitude
                }
            }
        };

        // Check and request location permissions (if needed)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        } else {
            requestLocationUpdates();
        }
    }
    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // locationRequest.setInterval(5000); // Update interval in milliseconds (5 seconds)
        // locationRequest.setFastestInterval(2000); // Fastest update interval in milliseconds (2 seconds)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
        );
    }
}
