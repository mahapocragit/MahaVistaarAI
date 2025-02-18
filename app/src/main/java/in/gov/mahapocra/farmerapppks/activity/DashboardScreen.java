package in.gov.mahapocra.farmerapppks.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import in.co.appinventor.services_api.api.AppInventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.mahapocra.farmerapppks.AppPreferenceManager;
import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.adapter.DashboardAdapter;
import in.gov.mahapocra.farmerapppks.adapter.DrawerMenuAdapter;
import in.gov.mahapocra.farmerapppks.adapter.VideosImageDetailsAdapter;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.Identify.Identify_dashboard;
import in.gov.mahapocra.farmerapppks.api.APIRequest;
import in.gov.mahapocra.farmerapppks.api.APIServices;
import in.gov.mahapocra.farmerapppks.app_util.ApUtil;
import in.gov.mahapocra.farmerapppks.app_util.AppConstants;
import in.gov.mahapocra.farmerapppks.app_util.AppHelper;
import in.gov.mahapocra.farmerapppks.app_util.AppString;
import in.gov.mahapocra.farmerapppks.models.response.CropsCategName;
import in.gov.mahapocra.farmerapppks.models.response.ResponseModel;
import in.gov.mahapocra.farmerapppks.notification.NotificationListActivity;
import in.gov.mahapocra.farmerapppks.webServices.ForceUpdateChecker;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class DashboardScreen extends AppCompatActivity implements ApiCallbackCode, AdapterView.OnItemClickListener, ForceUpdateChecker.OnUpdateNeededListener, OnMultiRecyclerItemClickListener {
    private NavigationView navigationView;
    private String TAG = "DashboardScreenTAG";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private GridView gridView;
    private CardView addCrop;
    private TextView nav_user_name;
    private TextView nav_user_phone;
    private ImageView imgLangChange;
    private ImageView imgNotification;
    private ImageView imgCallIcon;
    private ListView menuListView;
    private FloatingActionButton floatingActionButton;
    private RecyclerView selectedCropListRecyc;
    String languageToLoad;
    private int farmerId = 0;
    private int cropId = 0;
    private String savedCropName = "";
    private int savedCropId;
    private String savedCropSowingDate;
    private String savedCropWoTRId;
    private String savedCropImageUrl;
    TextView alertHeadingTv;
    private AppPreferenceManager appPreferenceManager;
    private JSONArray jsonArray;

    private static final String[] arrayCategory = new String[]{
            "Pest and Diseases", "Identify Pest/Disease", "Crop Advisory", "Fertilizer Calculator", "Climate Resilent Technology", "Soil Health Card",
            "Market Price", "Warehouse Availabilities", "DBT Schemes"

    };

    private static final String[] arrayCategoryMarathi = new String[]{
            "कीड व रोग", "किटक/रोग ओळखा", "पीक सल्ला", "खत मात्रा गणक (कॅलक्यूलेटर)", "हवामान अनुकूल तंत्रज्ञान", "मृदा आरोग्य पत्रिका",
            "बाजारभाव", " गोदाम उपलब्धता", "थेट लाभ हस्तांतरण योजना"

    };

    static int[] arrayCategoryImg = new int[]{
            R.drawable.ladybug, R.drawable.pest, R.drawable.ecology, R.drawable.fertilizer, R.drawable.climate_change, R.drawable.soil,
            R.drawable.commodity, R.drawable.warehouse, R.drawable.world_news
    };

    ArrayList<CropsCategName> selectedCropList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageToLoad = "en";
        if (AppSettings.getLanguage(DashboardScreen.this).equalsIgnoreCase("2")) {
            languageToLoad = "hi";
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_dashboard_screen);
        appPreferenceManager = new AppPreferenceManager(this);
        init();
        LinearLayout temperatureLayout = findViewById(R.id.temperatureLayout);
        temperatureLayout.setOnClickListener(view -> {
            Intent weather = new Intent(DashboardScreen.this, WeatherTempActivity.class);
            startActivity(weather);
        });

        floatingActionButton.setOnClickListener(view -> {
            startActivity(new Intent(DashboardScreen.this, TempDashboardActivity.class));
        });

        getFirebaseTokenFromServer();
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        setConfiguration();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout1);


        // navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerSlideAnimationEnabled(true);

        // Set Data
        String userName = AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName);
        String userNumber = AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo);
        navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        nav_user_name = hView.findViewById(R.id.tv_farmerName);
        nav_user_phone = hView.findViewById(R.id.tv_famerPhoneNumber);
        if (!userName.equals("USER_NAME")) {
            String capitalizeStrName = ApUtil.getCamelCaseStreing(userName);
            nav_user_name.setText(!capitalizeStrName.isEmpty() ? capitalizeStrName : userName);
            nav_user_phone.setText(userNumber);
        }

        // navigationView.setNavigationItemSelectedListener(this);
        gridView = findViewById(R.id.gridViewJobs);
        gridView.setColumnWidth(GridView.STRETCH_COLUMN_WIDTH);
        if (languageToLoad.equalsIgnoreCase("en")) {
            gridView.setAdapter(new DashboardAdapter(this, arrayCategory, arrayCategoryImg, "single_item_grid"));
        } else if (languageToLoad.equalsIgnoreCase("hi")) {
            gridView.setAdapter(new DashboardAdapter(this, arrayCategoryMarathi, arrayCategoryImg, "single_item_grid"));
        }

        appPreferenceManager.clearAll();
        gridView.setOnItemClickListener((parent, v, position, id) -> {
            switch (position) {
                case 0:
                    if (savedCropName.isEmpty()) {
                        Intent sharing = new Intent(DashboardScreen.this, AddCropActivity.class);
                        appPreferenceManager.saveString(AppConstants.ACTION_FROM_DASHBOARD, AppConstants.PEST_AND_DISEASES_STAGES);
                        startActivity(sharing);
                    } else {
                        Intent intentPestsAndDiseases = new Intent(this, PestsAndDiseasesStages.class);
                        intentPestsAndDiseases.putExtra("cropId", savedCropId);
                        intentPestsAndDiseases.putExtra("wotr_crop_id", savedCropWoTRId);
                        intentPestsAndDiseases.putExtra("sowingDate", savedCropSowingDate);
                        intentPestsAndDiseases.putExtra("mUrl", savedCropImageUrl);
                        intentPestsAndDiseases.putExtra("mName", savedCropName);
                        startActivity(intentPestsAndDiseases);
                    }
                    break;
                case 1:
                    Intent identify = new Intent(getApplicationContext(), Identify_dashboard.class);
                    startActivity(identify);
                    break;
                case 2:
                    if (savedCropName.isEmpty()) {
                        Intent sharing = new Intent(DashboardScreen.this, AddCropActivity.class);
                        appPreferenceManager.saveString(AppConstants.ACTION_FROM_DASHBOARD, AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD);
                        startActivity(sharing);
                    } else {
                        Intent intent = new Intent(this, AdvisoryCropActivity.class);
                        intent.putExtra("id", savedCropId);
                        intent.putExtra("wotr_crop_id", savedCropWoTRId);
                        intent.putExtra("mUrl", savedCropImageUrl);
                        intent.putExtra("mName", savedCropName);
                        startActivity(intent);
                    }
                    break;

                case 3:
                    if (savedCropName.isEmpty()) {
                        Intent comingSoonIntent = new Intent(DashboardScreen.this, AddCropActivity.class);
                        appPreferenceManager.saveString(AppConstants.ACTION_FROM_DASHBOARD, AppConstants.FERTILIZER_CALCULATOR_FROM_DASHBOARD);
                        startActivity(comingSoonIntent);
                    } else {
                        Intent intent = new Intent(this, FertilizerCalculatorActivity.class);
                        intent.putExtra("id", savedCropId);
                        intent.putExtra("wotr_crop_id", savedCropWoTRId);
                        intent.putExtra("mUrl", savedCropImageUrl);
                        intent.putExtra("mName", savedCropName);
                        intent.putExtra("sowingDate", savedCropSowingDate);
                        startActivity(intent);
                    }
                    break;
                case 4:
                    Intent addPeople = new Intent(DashboardScreen.this, ClimateResilintTechnology.class);
                    startActivity(addPeople);
                    break;
                case 5:
                    Intent healthIntent = new Intent(DashboardScreen.this, HealthCardActivity.class);
                    startActivity(healthIntent);
                    break;
                case 6:
                    Intent marketIntent = new Intent(DashboardScreen.this, MarketPrice.class);
                    startActivity(marketIntent);
                    break;
                case 7:
                    Intent warehouseIntent = new Intent(DashboardScreen.this, Warehouse.class);
                    startActivity(warehouseIntent);
                    break;
                case 8:
                    Intent dbtIntent = new Intent(DashboardScreen.this, DbtSchemes.class);
                    startActivity(dbtIntent);
                    break;
            }
        });
        imgLangChange.setOnClickListener(v -> openChangeLangPopup());
        imgNotification.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardScreen.this, ComingSoonActivity.class);
            intent.putExtra("notification", "mayu");
            startActivity(intent);
        });
        imgCallIcon.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                callingFun();
            }
        });

        addCrop.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardScreen.this, AddCropActivity.class);
            appPreferenceManager.clearPreference(AppConstants.ACTION_FROM_DASHBOARD);
            startActivity(intent);
        });
        setVersion();
        getFarmerSelectedCrop(languageToLoad);
        requestingPermissions();
    }

    private void getFirebaseTokenFromServer() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("firebaseMessage", "Fetching FCM registration token failed", task.getException());
            }
            String token = task.getResult();
            Log.d("firebaseMessage", "FCM Token: " + token);
        });
    }

    private void requestingPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        }

        // Check which permissions are not granted
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        // Request the permissions that are not granted
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }


    public void init() {
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0);
        if (farmerId > 0) {
            getUserDetails();
        }
        addCrop = findViewById(R.id.AddCropTv);
        selectedCropListRecyc = findViewById(R.id.selectedCrops);
        gridView = findViewById(R.id.gridViewJobs);
        imgLangChange = findViewById(R.id.imgLangChange);
        imgNotification = findViewById(R.id.imgNotification);
        imgCallIcon = findViewById(R.id.imgCallIcon);
        menuListView = findViewById(R.id.menuListView1);
        alertHeadingTv = findViewById(R.id.textView5);
        floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    private void setConfiguration() {
        try {
            if (languageToLoad.equalsIgnoreCase("en")) {
                jsonArray = AppHelper.getInstance().getMenuOption();
            } else if (languageToLoad.equalsIgnoreCase("hi")) {
                jsonArray = AppHelper.getInstance().getMenuOptionMarathi();
            }
            DrawerMenuAdapter menuAdapter = new DrawerMenuAdapter(this, jsonArray, farmerId);
            menuListView.setAdapter(menuAdapter);
            menuListView.setOnItemClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //API FOR GETTING SOWING DATE INFORMATION
    private void getCropStagesAndAdvisory() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("crop_id", savedCropId);
            jsonObject.put("farmer_id", farmerId);
            jsonObject.put("lang", languageToLoad);
            Log.d("RESPONSE_TAG", "getCropStagesAndAdvisory: " + jsonObject);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppInventorApi api = new AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    new AppString(this).getkMSG_WAIT(),
                    true
            );

            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getCropStagesAndAdvisory(requestBody);

            api.postRequest(responseCall, this, 4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setVersion() {
        TextView appVersionTextView = findViewById(R.id.appVerTextView);
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = pinfo.versionName;
        if (APIServices.DBT.equalsIgnoreCase("https://ilab-sso.mahapocra.gov.in/")) {
            appVersionTextView.setText("App Version " + versionName + " S");
        } else {
            appVersionTextView.setText("App Version " + versionName);
        }
        AppSettings.getInstance().setValue(DashboardScreen.this, AppConstants.kAPP_BUILD_VERSION, versionName);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void callingFun() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:02222163352"));
        startActivity(intent);
    }


    private void getUserDetails() {
        boolean isLogin = AppSettings.getInstance().getBooleanValue(this, AppConstants.userDataSaved, false);
        if (!isLogin) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("SecurityKey", APIServices.SSO_KEY);
                jsonObject.put("FAAPRegistrationID", farmerId);

                RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
                AppInventorApi api = new AppInventorApi(this, APIServices.DBT, "", new AppString(this).getkMSG_WAIT(), true);

                Handler handler = new Handler();
                Runnable runnable = () -> {
                    Retrofit retrofit = api.getRetrofitInstance();
                    APIRequest apiRequest = retrofit.create(APIRequest.class);
                    Call<JsonObject> responseCall = apiRequest.getGetRegistration(requestBody);
                    api.postRequest(responseCall, this, 1);
                };
                handler.post(runnable);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getFarmerSelectedCrop(String language) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lang", language);
            jsonObject.put("farmer_id", farmerId);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppInventorApi api = new AppInventorApi(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);

            Handler handler = new Handler();
            Runnable runnable = () -> {
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.getFarmersSelectedCrop(requestBody);
                api.postRequest(responseCall, this, 2);
            };
            handler.post(runnable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteFarmerSelectedCrop() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("crop_id", cropId);
            jsonObject.put("farmer_id", farmerId);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppInventorApi api = new AppInventorApi(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);

            Handler handler = new Handler();
            Runnable runnable = () -> {
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.deleteSelectedCrop(requestBody);
                api.postRequest(responseCall, this, 3);
            };
            handler.post(runnable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openChangeLangPopup() {
        final Dialog dialog = new Dialog(DashboardScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_language_selector);

        TextView tvEnglish = dialog.findViewById(R.id.tv_eng);
        TextView tvMarathi = dialog.findViewById(R.id.tv_mar);

        tvEnglish.setOnClickListener(view -> {

            String languageToLoad = "en";
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

            AppSettings.setLanguage(DashboardScreen.this, "1");

            finish();
            startActivity(getIntent());

            dialog.dismiss();
            getFarmerSelectedCrop(languageToLoad);

        });

        tvMarathi.setOnClickListener(view -> {

            String languageToLoad = "hi";

            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

            AppSettings.setLanguage(DashboardScreen.this, "2");

            finish();
            startActivity(getIntent());

            dialog.dismiss();
            getFarmerSelectedCrop(languageToLoad);
        });

        dialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    // Perform the related action (e.g., accessing the camera)
                    UIToastMessage.show(DashboardScreen.this, "Access Permission granted");
                } else {
                    // Permission was denied
                    // Notify the user and handle the situation gracefully
                    UIToastMessage.show(DashboardScreen.this, "Access Permission denied");
                }
            }
        }
    }

    @Override
    public void onFailure(Object obj, Throwable th, int i) {
    }

    @Override
    public void onResponse(JSONObject jSONObject, int i) {
        if (jSONObject != null) {
            switch (i) {
                case 1:
                    ResponseModel registrationResponse = new ResponseModel(jSONObject);
                    if (registrationResponse.getStatus()) {
                        try {
                            String strName = jSONObject.getString("Name");
                            String strMobNo = jSONObject.getString("MobileNo");
                            String strEmailId = jSONObject.getString("EmailId");
                            int strFFAReg = jSONObject.getInt("FAAPRegistrationID");
                            String strDistName = jSONObject.getString("DistrictName");
                            int strDistId = jSONObject.getInt("DistrictID");
                            String strTalukaName = jSONObject.getString("TalukaName");
                            int strTalukaId = jSONObject.getInt("TalukaID");
                            int strVillageId = jSONObject.getInt("VillageID");
                            String strVillageName = jSONObject.getString("VillageName");
                            AppSettings.getInstance().setValue(this, AppConstants.uName, strName);
                            AppSettings.getInstance().setValue(this, AppConstants.uMobileNo, strMobNo);
                            AppSettings.getInstance().setValue(this, AppConstants.uEmail, strEmailId);
                            AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, strFFAReg);
                            AppSettings.getInstance().setValue(this, AppConstants.uDIST, strDistName);
                            AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, strDistId);
                            AppSettings.getInstance().setValue(this, AppConstants.uTALUKA, strTalukaName);
                            AppSettings.getInstance().setIntValue(this, AppConstants.uTALUKAID, strTalukaId);
                            AppSettings.getInstance().setValue(this, AppConstants.uVILLAGE, strVillageName);
                            AppSettings.getInstance().setIntValue(this, AppConstants.uVILLAGEID, strVillageId);
                            AppSettings.getInstance().setBooleanValue(this, AppConstants.userDataSaved, true);

                            String userName = AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName);
                            String userNumber = AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo);
                            navigationView = findViewById(R.id.nav_view);
                            View hView = navigationView.getHeaderView(0);
                            nav_user_name = hView.findViewById(R.id.tv_farmerName);
                            nav_user_phone = hView.findViewById(R.id.tv_famerPhoneNumber);
                            if (!userName.equals("USER_NAME")) {
                                try {
                                    String capitalizeStrName = ApUtil.getCamelCaseStreing(userName);
                                    nav_user_name.setText(!capitalizeStrName.isEmpty() ? capitalizeStrName : userName);
                                    nav_user_phone.setText(userNumber);
                                } catch (StringIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    ResponseModel farmersSelectedCropResponse = new ResponseModel(jSONObject);
                    if (farmersSelectedCropResponse.getStatus()) {
                        JSONArray selectedCrops = farmersSelectedCropResponse.getDataArrays();
                        if (selectedCrops.length() > 0) {
                            selectedCropList = new ArrayList<>();
                            for (int j = 0; j < selectedCrops.length(); j++) {
                                try {
                                    JSONObject selectedCrop = selectedCrops.getJSONObject(j);
                                    savedCropId = selectedCrop.getInt("crop_id");
                                    savedCropName = selectedCrop.getString("name");
                                    savedCropImageUrl = selectedCrop.getString("image");
                                    savedCropWoTRId = selectedCrop.getString("wotr_crop_id");
                                    getCropStagesAndAdvisory();
                                    selectedCropList.add(new CropsCategName(selectedCrop.getInt("crop_id"), selectedCrop.getString("name"), selectedCrop.getString("image"), selectedCrop.getString("wotr_crop_id")));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            AppSettings.getInstance().setList(
                                    this, AppConstants.kFarmerCrop,
                                    Arrays.asList(selectedCropList.toArray()));
                            Log.d(TAG, "onResponse: is Not Empty");
                        } else {
                            savedCropName = "";
                            Log.d(TAG, "onResponse: is Empty");
                        }
                        if (selectedCropList != null) {
                            alertHeadingTv.setVisibility(View.GONE);
                            showCropList(selectedCropList);
                        } else {
                            alertHeadingTv.setVisibility(View.VISIBLE);
                            selectedCropListRecyc.setVisibility(View.GONE);
                        }
                    } else {
                        UIToastMessage.show(this, farmersSelectedCropResponse.getResponse());
                    }
                    break;
                case 3:
                    ResponseModel deleteSelectedCropResponse = new ResponseModel(jSONObject);
                    if (deleteSelectedCropResponse.getStatus()) {
                        UIToastMessage.show(this, deleteSelectedCropResponse.getResponse());
                        AppSettings.getInstance().setList(this, AppConstants.kFarmerCrop, null);
                        selectedCropList.clear();
                        getFarmerSelectedCrop(languageToLoad);
                    } else {
                        UIToastMessage.show(this, deleteSelectedCropResponse.getResponse());
                    }
                    break;
                case 4:
                    if (jSONObject != null) {
                        new AppPreferenceManager(this).saveString("CROP_STAGE_RESPONSE", jSONObject.toString());

                        ResponseModel response = new ResponseModel(jSONObject);
                        if (response.getStatus()) {
                            try {
                                savedCropSowingDate = jSONObject.getString("sowing_date");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            UIToastMessage.show(this, response.getResponse());
                        }
                    }
                    break;
            }
        }
    }

    private void showCropList(ArrayList<CropsCategName> selectedCropList) {
        selectedCropListRecyc.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        selectedCropListRecyc.setLayoutManager(layoutManager);
        selectedCropListRecyc.setHasFixedSize(true);
        selectedCropListRecyc.setItemAnimator(new DefaultItemAnimator());
        VideosImageDetailsAdapter adapter = new VideosImageDetailsAdapter(this, selectedCropList, this, "dashboardScreen");
        selectedCropListRecyc.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            int id = jsonObject.getInt("id");
            switch (id) {
                case 0:
                    Intent intent = new Intent(DashboardScreen.this, Registration.class);
                    intent.putExtra("FAAPRegistrationID", farmerId);
                    startActivity(intent);
                    break;
                case 1:
                    if (farmerId > 0) {
                        Intent sharing = new Intent(DashboardScreen.this, BandavarActivity.class);
                        startActivity(sharing);
                    } else {
                        UIToastMessage.show(DashboardScreen.this, "Please Login First...");
                    }
                    break;
                case 2:
                    Intent dbtStatusIntent = new Intent(DashboardScreen.this, DbtStatus.class);
                    dbtStatusIntent.putExtra("userPhoneNumber", String.valueOf(nav_user_phone));
                    startActivity(dbtStatusIntent);
                    break;
                case 3:
                    Intent notificationIntent = new Intent(DashboardScreen.this, NotificationListActivity.class);
                    startActivity(notificationIntent);
                    break;
                case 4:
                    Intent gisIntent = new Intent(DashboardScreen.this, GisActivity.class);
                    startActivity(gisIntent);
                    break;
                case 5:
                    Intent trainingLocationIntent = new Intent(DashboardScreen.this, TrainingLocationSelection.class);
                    startActivity(trainingLocationIntent);
                    break;
                case 6:
                    Intent loginIntent = new Intent(DashboardScreen.this, LoginScreen.class);
                    startActivity(loginIntent);
                    break;
                case 7:
                    logoutFromApp();
                    break;
                case 10:
                    startActivity(new Intent(DashboardScreen.this, AboutPocra.class));
                    break;
                case 11:
                    startActivity(new Intent(DashboardScreen.this, MyVillageProfilePdf.class));
                    break;
                case 12:
                    startActivity(new Intent(DashboardScreen.this, DbtStatus.class));
                    break;
                case 13:
                    if (farmerId > 0) {
                        Intent grievanceIntent = new Intent(DashboardScreen.this, Grievances.class);
                        grievanceIntent.putExtra("FAAPRegistrationID", farmerId);
                        startActivity(grievanceIntent);
                    } else {
                        UIToastMessage.show(DashboardScreen.this, "Please Login First...");
                    }
                    break;
            }
            DrawerLayout drawer = findViewById(R.id.drawer_layout1);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logoutFromApp() {

        AppSettings.getInstance().setValue(this, AppConstants.uName, AppConstants.uName);
        AppSettings.getInstance().setValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo);
        AppSettings.getInstance().setValue(this, AppConstants.uEmail, AppConstants.uEmail);
        AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, 0);
        AppSettings.getInstance().setValue(this, AppConstants.uDIST, AppConstants.uDIST);
        AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, 0);
        AppSettings.getInstance().setValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA);
        AppSettings.getInstance().setIntValue(this, AppConstants.uTALUKAID, 0);
        AppSettings.getInstance().setValue(this, AppConstants.uVILLAGE, AppConstants.uVILLAGE);
        AppSettings.getInstance().setIntValue(this, AppConstants.uVILLAGEID, 0);
        AppSettings.getInstance().setList(this, AppConstants.kFarmerCrop, null);
        AppUtility.getInstance().clearAppSharedPrefData(this, AppConstants.kSHARED_PREF);
        AppSettings.getInstance().setBooleanValue(this, AppConstants.userDataSaved, false);
        Intent intent = new Intent(DashboardScreen.this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUpdateNeeded(String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue reposting.")
                .setPositiveButton("Update",
                        (dialog1, which) -> redirectStore(updateUrl)).setNegativeButton("No, thanks",
                        (dialog12, which) -> finish()).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object id) {
        if (i == 2) {
            cropId = ((int) id);
            deleteFarmerSelectedCrop();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}