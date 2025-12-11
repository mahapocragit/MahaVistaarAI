package in.gov.mahapocra.mahavistaarai.sma;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.image.AIImageCompressionAsyncTasks;
import in.co.appinventor.services_api.image.AIImageLoadingUtils;
import in.co.appinventor.services_api.listener.AIImgAsyncResponse;
import in.co.appinventor.services_api.listener.AlertListEventListener;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIAlertView;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.data.model.ResponseModel;
import in.gov.mahapocra.mahavistaarai.util.app_util.ApUtil;
import in.gov.mahapocra.mahavistaarai.util.app_util.AppString;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class KTActivity extends AppCompatActivity implements ApiJSONObjCallback, AIImgAsyncResponse, ApiCallbackCode , AlertListEventListener {

    private AppSession session;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_CODE = 22;
    private static final int CAMERA_PERMISSION_CODE = 3;
    private TextView txtKTName; //
    private ImageView technologyImage;
    private EditText edtRemark,unitEditText,categoryOtherEditText,farmerGroupOtherEditText;
    private LinearLayout linLayoutCategory3,linlayoutCategory4,linLayoutUnit,linLayoutCategoryOther,linLayoutFarmerGroupOther;
    private Button btnSubmitKTAttendance;
    private ProfileModel profileModel;
    private String strLongitude;
    //private double longitude;
    private String strLatitude;
    //private double latitude;
    private FusedLocationProviderClient mFusedLocationClient;
    private int PERMISSION_ID = 44;
    String currentPhotoPath;
    private File imgFile;
    private File photoFile;
    private String fileNames = null;
    private String strRemark,strCategoryOther,strFarmerGrpOther;
    private TextView reasonDropTextView,categoryDropTextView,subCategoryDropTextView,farmerListDropTextView,farmerGrpListDropTextView,discussionTopicDropTextView;
    private JSONArray reasonJSONArray = null;
    private JSONArray farmerListJSONArray = null;
    private JSONArray categoryJSONArray = null;
    private JSONArray subCategoryJSONArray = null;
    private JSONArray farmerGrpListJSONArray = null;
    private JSONArray discussionTopicJSONArray = null;
    private String username,observationNote;
    private int villageCensuscode;
    private int reason_id = 0;
    private String farmer_id = "";
    private int categoryTypeId = 0;
    private int subCategoryTypeId = 0;
    private int discussionTopicTypeId = 0;
    private int farmerGrpTypeId = 0;
    private int farmerSelectedType = 1;
    private int ktWorkType = 1;
    private int technologySelectedType = 1;
    private RadioGroup radioGroupFarmer,radioGroupTechnology,radioGroupOptions;
    private TextView tooltipRemark;
    private RadioButton farmerRBtnOne, farmerRBtnTwo,technologyRBtnOne,technologyRBtnTwo,rb1_Baithak,rb2_Prashikshan,rb3_Bhet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ktactivity);

        initComponant();
        session = new AppSession(this);
        profileModel = session.getProfileModel();
        String name = profileModel.getFirst_name() + " " + profileModel.getMiddle_name() + " " + profileModel.getLast_name();
        txtKTName.setText(name); //

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        username = AppSettings.getInstance().getValue(this, AppConstants.kUSERNAME, AppConstants.kUSERNAME);
        villageCensuscode = AppSettings.getInstance().getIntValue(this, AppConstants.kVillageCensus, 0);
        Log.d("MAYU1","Censuscode=="+villageCensuscode);
        //  fetchAbsentReason();
        //  fetchDbtFarmerList();
        //  fetchFarmerGroupName();
        //  fetchDiscussionTopic();
        technologyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utility.checkConnection(KTActivity.this)) {
                    onImageAction();
                } else {
                    UIAlertView.getOurInstance().show(KTActivity.this, "Please turn on your Mobile data/Wi-fi for your attendance");
                }
            }
        });
        reasonDropTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateReason();
            }
        });
        farmerGrpListDropTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateFarmerGrpList();
            }
        });
//        discussionTopicDropTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                populateDiscussionTopic();
//            }
//        });
        categoryDropTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                populateCategoryType();
            }
        });
        subCategoryDropTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                populateSubCategoryType();
            }
        });
        farmerListDropTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                populateFarmerList();
            }
        });

        radioGroupOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb1_Baithak) {
                    ktWorkType = 1; //Baithak
                } else if (i == R.id.rb2_Prashikshan) {
                    ktWorkType = 2; //Prashikshan
                } else if (i == R.id.rb3_Bhet)
                {
                    ktWorkType = 5;//Bhet
                }
            }
        });
        Log.d("Mayu111", "onCreate categoryTypeID===" + categoryTypeId);
        radioGroupFarmer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.farmerRBtnOne) {
                    farmerSelectedType = 1; //Individual
                    technologyRBtnTwo.setVisibility(View.VISIBLE);
                } else {
                    farmerSelectedType = 2; //FPC
                    technologyRBtnTwo.setVisibility(View.GONE);
                }
            }
        });
        radioGroupTechnology.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.technologyRBtnOne) {
                    technologySelectedType = 1; //Demonstration
//                    Log.d("Mayu111", "onCreate technologySelectedType1===" + technologySelectedType);
                    linLayoutUnit.setVisibility(View.GONE);
                } else {
                    technologySelectedType = 2; //Adoption
                    linLayoutUnit.setVisibility(View.VISIBLE);
//                    Log.d("Mayu111", "onCreate technologySelectedType2===" + technologySelectedType);
                }
            }
        });

        tooltipRemark = findViewById(R.id.tooltipRemark);

        Handler handler = new Handler();
//        Runnable hideTooltipRunnable = () -> tooltipRemark.setVisibility(View.GONE);

        Runnable hideTooltipRunnable = () -> {
            tooltipRemark.animate()
                    .alpha(0f)
                    .setDuration(400)
                    .withEndAction(() -> {
                        tooltipRemark.setVisibility(View.GONE);
                        tooltipRemark.setAlpha(1f);
                    })
                    .start();
        };

        edtRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show tooltip when user types something
                if (s.length() > 0) {
                    tooltipRemark.setVisibility(View.GONE);
                    // Reset the 10-second timer
                    handler.removeCallbacks(hideTooltipRunnable);
                    handler.postDelayed(hideTooltipRunnable, 6000); // 5 seconds
                } else {
                    // Hide immediately when cleared
                    tooltipRemark.setVisibility(View.GONE);
                    handler.removeCallbacks(hideTooltipRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmitKTAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utility.checkConnection(KTActivity.this)) {

                    String strRemark = edtRemark.getText().toString();

//                        if (strRemark.equalsIgnoreCase("")) {
//                            Toast.makeText(KTActivity.this, "कृपया निरीक्षण लिहा. ", Toast.LENGTH_SHORT).show();
//                        } else
//                        if (ktWorkType == 0) {
//                            Toast.makeText(KTActivity.this, "कृपया कामाचे स्वरूप निवडा. ", Toast.LENGTH_SHORT).show();
//                        }
//                        else if (categoryTypeId == 0) {
//                            Toast.makeText(KTActivity.this, "कृपया कामाचा प्रकार निवडा. ", Toast.LENGTH_SHORT).show();
//                        } else if (categoryTypeId == 3 && farmer_id.equalsIgnoreCase("")) {
//                            Toast.makeText(KTActivity.this, "कृपया शेतकरी नाव निवडा. ", Toast.LENGTH_SHORT).show();
//                        } else if (categoryTypeId == 3 && subCategoryTypeId == 0) {
//                            Toast.makeText(KTActivity.this, "कृपया हवामान अनुकूल तंत्रज्ञान प्रकार निवडा. ", Toast.LENGTH_SHORT).show();
//                        } else if ((categoryTypeId == 4 || categoryTypeId == 7 || categoryTypeId == 9) && farmerGrpTypeId == 0) {
//                            Toast.makeText(KTActivity.this, "कृपया शेतकरी गटाचे निवडा.", Toast.LENGTH_SHORT).show();
//                        }
                    if (fileNames == null) {
                        //UIToastMessage.show(KTActivity.this, "Please take photo to mark attendance");
                        Toast.makeText(KTActivity.this, "कृपया फोटो अपलोड करा. ", Toast.LENGTH_SHORT).show();
                    } else {
                        submitKTAttendace(fileNames);
                    }

                } else {
                    UIAlertView.getOurInstance().show(KTActivity.this, "Please turn on your Mobile data/Wi-fi for your attendance");
                }
            }
        });
    }

    private void initComponant() {

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        txtKTName = findViewById(R.id.KT_Name); //
        technologyImage = findViewById(R.id.techImage);
        edtRemark = findViewById(R.id.Remark);
        unitEditText = findViewById(R.id.unitEditText);
        categoryOtherEditText = findViewById(R.id.categoryOtherEditText);
        farmerGroupOtherEditText = findViewById(R.id.farmerGroupOtherEditText);
        btnSubmitKTAttendance = findViewById(R.id.btnSubmitKTAttendance);
        reasonDropTextView = findViewById(R.id.reasonDropTextView);
        categoryDropTextView = findViewById(R.id.categoryDropTextView);
        subCategoryDropTextView = findViewById(R.id.subCategoryDropTextView);
        farmerListDropTextView = findViewById(R.id.farmerListDropTextView);
        farmerGrpListDropTextView = findViewById(R.id.farmerGrpListDropTextView);
        discussionTopicDropTextView = findViewById(R.id.discussionTopicDropTextView);

        radioGroupFarmer = findViewById(R.id.radioGroupFarmer);
        radioGroupTechnology = findViewById(R.id.radioGroupTechnology);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        farmerRBtnOne = radioGroupFarmer.findViewById(R.id.farmerRBtnOne);
        farmerRBtnTwo = radioGroupFarmer.findViewById(R.id.farmerRBtnTwo);
        rb1_Baithak = radioGroupFarmer.findViewById(R.id.rb1_Baithak);
        rb2_Prashikshan = radioGroupFarmer.findViewById(R.id.rb2_Prashikshan);
        rb3_Bhet = radioGroupFarmer.findViewById(R.id.rb3_Bhet);
        technologyRBtnOne = radioGroupTechnology.findViewById(R.id.technologyRBtnOne);
        technologyRBtnTwo = radioGroupTechnology.findViewById(R.id.technologyRBtnTwo);

        linLayoutUnit = findViewById(R.id.linLayoutUnit);
        linLayoutCategoryOther = findViewById(R.id.linLayoutCategoryOther);
        linLayoutFarmerGroupOther = findViewById(R.id.linLayoutFarmerGroupOther);
        linLayoutCategory3 = findViewById(R.id.linLayoutCategory3);
        linlayoutCategory4 = findViewById(R.id.linlayoutCategory4);

    }

    private void fetchAbsentReason() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("role_id", session.getUserRoleId());

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.absentReasonSyncDownRequest(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 2);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fetchFarmerGroupName() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("category_type", 4);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.farmerGroupListKTRequest(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 8);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fetchDiscussionTopic() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("category_type", 4);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.farmerDiscussionListKTRequest(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 9);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fetchDbtFarmerList() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("village_code", villageCensuscode);
//            jsonObject.put("role_id", session.getUserRoleId());

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.dbtFarmerListKTRequest(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 6);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fetchKTCategory(int reason_id) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("category_type", reason_id);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.categoryListKTRequest(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 4);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fetchKTSubCategory(int categoryTypeId) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("category_type", reason_id);
            jsonObject.put("category_id", categoryTypeId);

            Log.d("MAYU111","fetchKTSubCategoryRID="+reason_id);
            Log.d("MAYU111","fetchKTSubCategory2CID="+categoryTypeId);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.subCategoryListKTRequest(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 5);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void submitKTDetails(String fName) {
        try {
            String strUnit = "0";
            String strRemark = "";
            String strCategoryOther = "";
            String strFarmerGroupOther = "";
            strUnit= unitEditText.getText().toString();
            if (strUnit == null || strUnit.trim().length() == 0) {
                strUnit = "0";
            }
            strCategoryOther = categoryOtherEditText.getText().toString();
            strFarmerGroupOther = farmerGroupOtherEditText.getText().toString();
            strRemark=edtRemark.getText().toString();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("username", username);
//            jsonObject.put("category_type", reason_id);
            jsonObject.put("category_type", ktWorkType);
            jsonObject.put("category_id", categoryTypeId);
            jsonObject.put("subcategory_id", subCategoryTypeId);
            jsonObject.put("subcategory_unit", strUnit);
            jsonObject.put("description", strRemark);
            jsonObject.put("latitude", strLatitude);
            jsonObject.put("longitude", strLongitude);
            jsonObject.put("filename", fName);
            jsonObject.put("farmer_id", farmer_id);
            jsonObject.put("farmer_type", farmerSelectedType);
            jsonObject.put("technology_type", technologySelectedType);
            jsonObject.put("farmer_group_id", farmerGrpTypeId);
//            jsonObject.put("discussion_id", discussionTopicTypeId);
            jsonObject.put("farmer_group_other_text", strFarmerGroupOther);
            jsonObject.put("category_other_text", strCategoryOther);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.submitDetailsKTRequest(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 7);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void populateReason() {
        if (reasonJSONArray != null && reasonJSONArray.length() > 0) {
//            AppUtility.getInstance().showListDialogIndex(reasonJSONArray, 2, "कामाचे स्वरूप निवडा", "name", "id", this, this);
            ApUtil.getInstance().showListDialogIndex(reasonJSONArray, 2, "कामाचे स्वरूप निवडा", "name_mr", "id", this, this);
        }
    }
    private void populateFarmerGrpList() {
        if (farmerGrpListJSONArray != null && farmerGrpListJSONArray.length() > 0) {
            ApUtil.getInstance().showListDialogIndex(farmerGrpListJSONArray, 8, "शेतकरी गटाचे नाव निवडा", "name", "id", this, this);
        }
    }
    //    private void populateDiscussionTopic() {
//        if (discussionTopicJSONArray != null && discussionTopicJSONArray.length() > 0) {
//            AppUtility.getInstance().showListDialogIndex(discussionTopicJSONArray, 9, "चर्चेचा विषय निवडा", "name", "id", this, this);
//        }
//    }
    private void populateFarmerList() {
        if (farmerListJSONArray != null && farmerListJSONArray.length() > 0) {
            ApUtil.getInstance().showListDialogIndex(farmerListJSONArray, 6, "शेतकरी नाव निवडा", "register_name", "adhar_vaultid", this, this);
        }
    }
    private void populateCategoryType() {
        if (categoryJSONArray == null) {
            fetchKTCategory(reason_id);

        }else {
//        if (absentJSONArray != null && absentJSONArray.length() > 0) {
            ApUtil.getInstance().showListDialogIndex(categoryJSONArray, 4, "कामाचा प्रकार निवडा", "category_shortname", "id", this, this);
        }
    }
    private void populateSubCategoryType() {
        if (subCategoryJSONArray == null) {
            fetchKTSubCategory(categoryTypeId);
        }else {
//        if (absentJSONArray != null && absentJSONArray.length() > 0) {
            ApUtil.getInstance().showListDialogIndex(subCategoryJSONArray, 5, "हवामान अनुकूल तंत्रज्ञान प्रकार", "subcategory_name", "id", this, this);
        }
    }

    @Override
    public void onFailure(Throwable th, int i) {

    }

    @Override
    public void onFailure(Object obj, Throwable th, int i) {

    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (i == 3) {
                ResponseModel response = new ResponseModel(jsonObject);

                if (response.getStatus()) {
                    // UIToastMessage.show(this, response.getResponse());
                    JSONObject jsonObject1 = response.getJSONObject();
                    fileNames = AppUtility.getInstance().sanitizeJSONObj(jsonObject1, "file_name");
                    Log.d("Mayu111", "response 3==" + fileNames);
                    submitKTDetails(fileNames);

                } else {
                    Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                }
            }
            if (i == 2) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);
                    if (response.getStatus()) {
                        reasonJSONArray = response.getDataArray();
                        for (int m = 0; m < reasonJSONArray.length(); m++) {
                            JSONObject actor = reasonJSONArray.getJSONObject(m);
                        }
                    } else {
                        UIToastMessage.show(this, response.getResponse());
                    }
                }
            }
            if (i == 6) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);
                    if (response.getStatus()) {
                        farmerListJSONArray = response.getDataArray();
                        for (int m = 0; m < farmerListJSONArray.length(); m++) {
                            JSONObject actor = farmerListJSONArray.getJSONObject(m);
                        }
                    } else {
                        UIToastMessage.show(this, response.getResponse());
                    }
                }
            }
            if (i == 4) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);
                    if (response.getStatus()) {
                        categoryJSONArray = response.getDataArray();
                        for (int m = 0; m < categoryJSONArray.length(); m++) {
                            JSONObject actor = categoryJSONArray.getJSONObject(m);
                            observationNote = actor.optString("observation_note");
                        }
                    } else {
                        UIToastMessage.show(this, response.getResponse());
                    }
                }
            }
            if (i == 5) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);
//                    if (response.getStatus()) {
//                        subCategoryJSONArray = response.getDataArray();
//                        for (int m = 0; m < subCategoryJSONArray.length(); m++) {
//                            JSONObject actor = subCategoryJSONArray.getJSONObject(m);
//                        }
//                    } else {
//                        UIToastMessage.show(this, response.getResponse());
//                    }
                    if (response.getStatus()) {
                        subCategoryJSONArray = response.getDataArray();

                        // ✅ Handle null or empty data properly
                        if (subCategoryJSONArray == null || subCategoryJSONArray.length() == 0) {
                            subCategoryJSONArray = new JSONArray(); // clear previous list
                            UIToastMessage.show(this, "No sub-category found");
                        } else {
                            for (int m = 0; m < subCategoryJSONArray.length(); m++) {
                                JSONObject actor = subCategoryJSONArray.getJSONObject(m);
                                // your existing logic
                            }

                        }

                    } else {
                        subCategoryJSONArray = new JSONArray(); // ✅ clear if API returns false
                        UIToastMessage.show(this, response.getResponse());
                    }
                }
            }
            if (i == 7) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);
                    if (response.getStatus()) {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                        Intent newIntent= new Intent(KTActivity.this, KTDashboardActivity.class);
                        startActivity(newIntent);
                        Log.d("MAYU111","RefreshToken==="+response.getResponse());
                    } else {
                        Toast.makeText(this, "" + response.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (i == 8) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response = new ResponseModel(jsonObject);
                    if (response.getStatus()) {
                        farmerGrpListJSONArray = response.getDataArray();
                        for (int m = 0; m < farmerGrpListJSONArray.length(); m++) {
                            JSONObject actor = farmerGrpListJSONArray.getJSONObject(m);
                        }
                    } else {
                        UIToastMessage.show(this, response.getResponse());
                    }
                }
            }
//            if (i == 9) {
//                if (jsonObject != null) {
//                    DebugLog.getInstance().d("onResponse=" + jsonObject);
//                    ResponseModel response = new ResponseModel(jsonObject);
//                    if (response.getStatus()) {
//                        discussionTopicJSONArray = response.getDataArray();
//                        for (int m = 0; m < discussionTopicJSONArray.length(); m++) {
//                            JSONObject actor = discussionTopicJSONArray.getJSONObject(m);
//                        }
//                    } else {
//                        UIToastMessage.show(this, response.getResponse());
//                    }
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                //showInfoDialog();
                return true;

            case R.id.action_refresh:
                // syncDownMasterData();
                return true;

            case android.R.id.home:
                if (getIntent().getStringExtra("coming_from") != null) {
                    String coming_from = getIntent().getStringExtra("coming_from");
                    if (coming_from.equalsIgnoreCase("data_synced")) {
                        Intent nextScreen=new Intent(KTActivity.this,KTDashboardActivity.class);
                        startActivity(nextScreen);
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onImageAction() {

//        if (Build.VERSION.SDK_INT < 23) {
//            //Do not need to check the permission
//            DebugLog.getInstance().d("No need to check the permission");
//            captureCamera();
//        } else {
//
//            if (checkPermissionsIsEnabledOrNot()) {
//                //If you have already permitted the permission
//                captureCamera();
//            } else {
//                requestMultiplePermission();
//            }
//
//        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }
        } else {
            dispatchTakePictureIntent();
        }
    }
    private void captureCamera() {
        dispatchTakePictureIntent();
    }

    //    private void dispatchTakePictureIntent() {
//
//        AIImageLoadingUtil aiImageLoadingUtil = new AIImageLoadingUtil(this);
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//
//            try {
//                photoFile = aiImageLoadingUtil.createImageFile(ImageTypes.ATTENDANCE.id());
//            } catch (Exception ex) {
//                // Error occurred while creating the File
//                ex.printStackTrace();
//            }
//
//            // Continue only if the File was successfully created
//
//            if (photoFile != null) {
//                Uri mImgURI;
//
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//                    mImgURI = Uri.fromFile(photoFile);
//                } else {
    ////                    mImgURI = FileProvider.getUriForFile(getApplication().getApplicationContext(),
    ////                            "ibas.provider", photoFile);
//                    mImgURI = FileProvider.getUriForFile(this,
//                            "in.gov.mahapocra.sma.android.fileprovider",
//                            photoFile);
//                }
//
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImgURI);
//                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
//                PackageManager packageManager = getPackageManager();
//
//                List<ResolveInfo> listCam = packageManager.queryIntentActivities(takePictureIntent, 0);
//
//                ResolveInfo res = listCam.get(0);
//                String packageName = res.activityInfo.packageName;
//
//                Intent intent = new Intent(takePictureIntent);
//                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//                intent.setPackage(packageName);
//
//                startActivityForResult(intent, CAMERA_CODE);
//
//                DebugLog.getInstance().d("Camera Package Name=" + packageName);
//                DebugLog.getInstance().d("mImgURI=" + mImgURI);
//
//            }
//
//        }
//    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoFile = createImageFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            Uri mImgURI;

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                mImgURI = Uri.fromFile(photoFile);
            } else {
                mImgURI = FileProvider.getUriForFile(this,
                        "in.gov.mahapocra.mahavistaarai.android.fileprovider",
                        photoFile);
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImgURI);

            startActivityForResult(takePictureIntent, CAMERA_CODE);

            DebugLog.getInstance().d("Camera Package Name=" + takePictureIntent);
            DebugLog.getInstance().d("mImgURI=" + mImgURI);

        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("image_path", currentPhotoPath);
        edit.commit();
        Log.e("SET Image Path", "setImagePic: " + currentPhotoPath);

        return image;
    }
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

            AIImageLoadingUtils aiImageLoadingUtil = new AIImageLoadingUtils(this);

            AIImageCompressionAsyncTasks asyncTask = new AIImageCompressionAsyncTasks(aiImageLoadingUtil, photoFile, this);
            asyncTask.execute("");

        }
    }


    @Override
    public void asyncImgProcessFinish(Object output) {

        String result = (String) output;
        try {
            imgFile = new File(result);
            Picasso.get()
                    .load(imgFile)
                    .fit()
                    .into(technologyImage);

            fileNames = imgFile.getAbsolutePath();
            Log.d("Mayu111","filename==="+fileNames);
            // syncUpImageRequest(fileNames, "", 0);
            // submitKTAttendace(fileNames, workTypeFlag, villageID);

//            AppSettings.getInstance().setValue(this, AppConstants.kTC_ATTENDANCE, fileNames);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void submitKTAttendace(String filePath ) {

        try {
//            String userId = String.valueOf(session.getUserId());
//            Log.d("Mayu111","userId=="+userId);
            strRemark=edtRemark.getText().toString();
            Log.d("Mayu111","Remark=="+strRemark);
            AppSession session = new AppSession(this);
            MultipartBody.Part partBody = null;
            DebugLog.getInstance().d("imgName=" + filePath);

            Map<String, RequestBody> params = new HashMap<>();
            params.put("username", AppinventorIncAPI.toRequestBody(String.valueOf(username)));
            params.put("timestamp", AppinventorIncAPI.toRequestBody(String.valueOf(session.getTimeStamp())));

            DebugLog.getInstance().d("params=" + params);

            //creating a file
            File file = new File(filePath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            partBody = MultipartBody.Part.createFormData("fileToUpload", file.getName(), reqFile);

            Log.d("Mayu11111","username=="+username);
            Log.d("Mayu11111","timestamp=="+session.getTimeStamp());
            Log.d("Mayu11111","fileToUpload=="+file.getName());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();

            //creating our api
            APIRequest apiRequest = retrofit.create(APIRequest.class);

            Call<JsonObject> responseCall = apiRequest.uploadImagesKTActivity(partBody, params);
            api.postRequest(responseCall, this, 3);
            Log.v("param submitKTAtd",responseCall.request().url().toString());

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            //latitudeTextView.setText(location.getLatitude() + "");
                            // longitTextView.setText(location.getLongitude() + "");
                            strLatitude= String.valueOf(location.getLatitude());
                            strLongitude= String.valueOf(location.getLongitude());
                            Log.d("Mayu112",""+strLatitude);
                            Log.d("Mayu112",""+strLongitude);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
//            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
//            String strLat= String.valueOf(mLastLocation.getLatitude());
//            String strLong= String.valueOf(mLastLocation.getLongitude());
//            Log.d("Mayu111",""+strLat);
//            Log.d("Mayu111",""+strLong);
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        captureCamera();
                    } else {
                        captureCamera();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
    @Override
    public void didSelectListItem(int i, String s, String s1) {

        if (i == 2) {
            reason_id = Integer.parseInt(s1);
            reasonDropTextView.setText(s);
            Log.d("Mayu111", "ReasonID===" + reason_id);
            Log.d("Mayu111", "txtSelectReason===" + s);
//            fetchKTCategory(reason_id);
//            categoryDropTextView.setText("कामाचा प्रकार निवडा");
//            categoryTypeId = 0;
//            subCategoryDropTextView.setText("हवामान अनुकूल तंत्रज्ञान प्रकार");
//            subCategoryTypeId = 0;
//            farmerGrpListDropTextView.setText("शेतकरी गटाचे नाव निवडा");
//            farmerGrpTypeId = 0;
//            farmerListDropTextView.setText("शेतकरी नाव निवडा");
//            farmer_id = "";
//            linLayoutCategory3.setVisibility(View.GONE);
//            linlayoutCategory4.setVisibility(View.GONE);
//            linLayoutCategoryOther.setVisibility(View.GONE);
//            linLayoutFarmerGroupOther.setVisibility(View.GONE);
//            tooltipRemark.setText("\uD83D\uDC49 "+observationNote);
        }
        if (i == 4) {
            categoryTypeId = Integer.parseInt(s1);
            categoryDropTextView.setText(s);
            Log.d("Mayu111", "categoryTypeID===" + categoryTypeId);
            Log.d("Mayu111", "txtSelectCategory===" + s);
            fetchKTCategory(reason_id);
            fetchKTSubCategory(categoryTypeId);
            subCategoryDropTextView.setText("हवामान अनुकूल तंत्रज्ञान प्रकार");
            subCategoryTypeId = 0;
            farmerGrpListDropTextView.setText("शेतकरी गटाचे नाव निवडा");
            farmerGrpTypeId = 0;
            farmerListDropTextView.setText("शेतकरी नाव निवडा");
            farmer_id = "";
            tooltipRemark.setText("\uD83D\uDC49 "+observationNote);
            // Always start by hiding both
            linLayoutCategory3.setVisibility(View.GONE);
            linlayoutCategory4.setVisibility(View.GONE);
            linLayoutFarmerGroupOther.setVisibility(View.GONE);

// Then decide which to show
            if (categoryTypeId == 3) {
                linLayoutCategory3.setVisibility(View.VISIBLE);
                Log.d("Mayu111", "Showing linLayoutCategory3");
                tooltipRemark.setText("\uD83D\uDC49 भेट देऊन प्रात्यक्षिक कोणत्या पिकाकरिता आहे?, पिकाच्या वाढीची अवस्था, शेतकऱ्यांचे अभिप्राय, प्रश्न, शेतकरी गट/ बचत गटातील किती शेतकरी तंत्रज्ञान अवलंब करण्यास उत्सुक आहेत? इ. विषयी माहिती थोडक्यात लिहावी.");

            } else if (categoryTypeId == 4 || categoryTypeId == 7 || categoryTypeId == 9 ) {
                linlayoutCategory4.setVisibility(View.VISIBLE);
                Log.d("Mayu111", "Showing linlayoutCategory4");
            } else {
                subCategoryDropTextView.setText("हवामान अनुकूल तंत्रज्ञान प्रकार");
                linLayoutCategory3.setVisibility(View.GONE);
                linlayoutCategory4.setVisibility(View.GONE);

                Log.d("Mayu111", "Hiding both for other categoryTypeId: " + categoryTypeId);
            }
            if(s.equalsIgnoreCase("इतर"))
            {
                linLayoutCategoryOther.setVisibility(View.VISIBLE);
            }else
            {
                linLayoutCategoryOther.setVisibility(View.GONE);
            }
        }
        if (i == 5) {
            subCategoryTypeId = Integer.parseInt(s1);
            subCategoryDropTextView.setText(s);
            Log.d("Mayu111", "subCategoryTypeId===" + subCategoryTypeId);
            Log.d("Mayu111", "txtSelectCategory===" + s);
        }
        if (i == 6) {
//            farmer_id = Integer.parseInt(s1);
            farmer_id = s1;
            farmerListDropTextView.setText(s);
            Log.d("Mayu111", "farmer_id===" + farmer_id);
            Log.d("Mayu111", "txtSelectFarmer===" + s);
        }
        if (i == 8) {
            farmerGrpTypeId = Integer.parseInt(s1);
            farmerGrpListDropTextView.setText(s);
            Log.d("Mayu111", "farmerGrpTypeId===" + farmerGrpTypeId);
            Log.d("Mayu111", "txtSelectfarmerGrp===" + s);
            if(s.equalsIgnoreCase("Others"))
            {
                linLayoutFarmerGroupOther.setVisibility(View.VISIBLE);
            }else
            {
                linLayoutFarmerGroupOther.setVisibility(View.GONE);
            }
        }
//        if (i == 9) {
//            discussionTopicTypeId = Integer.parseInt(s1);
//            discussionTopicDropTextView.setText(s);
//            Log.d("Mayu111", "discussionTopicTypeId===" + discussionTopicTypeId);
//            Log.d("Mayu111", "txtSelectDiscussuion===" + s);
//        }
    }
}