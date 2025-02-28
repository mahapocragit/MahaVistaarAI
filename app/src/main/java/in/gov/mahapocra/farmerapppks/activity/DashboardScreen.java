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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

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
import in.gov.mahapocra.farmerapppks.databinding.ActivityDashboardScreenBinding;
import in.gov.mahapocra.farmerapppks.models.response.CropsCategName;
import in.gov.mahapocra.farmerapppks.models.response.ResponseModel;
import in.gov.mahapocra.farmerapppks.notification.NotificationListActivity;
import in.gov.mahapocra.farmerapppks.webServices.ForceUpdateChecker;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class DashboardScreen extends AppCompatActivity implements ApiCallbackCode, AdapterView.OnItemClickListener, ForceUpdateChecker.OnUpdateNeededListener, OnMultiRecyclerItemClickListener {
    private ActivityDashboardScreenBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextView nav_user_name;
    private TextView nav_user_phone;
    String languageToLoad;
    private int farmerId = 0;
    private int cropId = 0;
    private String savedCropName = "";
    private int savedCropId;
    private String savedCropSowingDate;
    private String savedCropWoTRId;
    private String savedCropImageUrl;
    private AppPreferenceManager appPreferenceManager;
    private JSONArray jsonArray;
    private boolean showToast = true;

    private static final String[] arrayCategory = new String[]{
            "Crop Advisory", "Soil Health Card", "Fertilizer Calculator", "Climate Resilent Technology", "Pest and Diseases", "Identify Pest/Disease",
            "Market Price", "Warehouse Availabilities", "DBT Schemes"
    };

    private static final String[] arrayCategoryMarathi = new String[]{
            "पीक सल्ला", "मृदा आरोग्य पत्रिका", "खत मात्रा गणक (कॅलक्यूलेटर)", "हवामान अनुकूल तंत्रज्ञान", "कीड व रोग", "किटक/रोग ओळखा",
            "बाजारभाव", " गोदाम उपलब्धता", "थेट लाभ हस्तांतरण योजना"

    };

    static int[] arrayCategoryImg = new int[]{
            R.drawable.ecology, R.drawable.soil, R.drawable.fertilizer, R.drawable.climate_change, R.drawable.ladybug, R.drawable.pest,
            R.drawable.commodity, R.drawable.warehouse, R.drawable.ic_dbt
    };

    ArrayList<CropsCategName> selectedCropList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageToLoad = "en";
        if (AppSettings.getLanguage(DashboardScreen.this).equalsIgnoreCase("2")) {
            languageToLoad = "hi";
        }
        showToast = true;
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        binding = ActivityDashboardScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appPreferenceManager = new AppPreferenceManager(this);
        init();

        binding.appBarMain.dashboardScreen.deleteCropImageView.setOnClickListener(view -> {
            cropId = savedCropId;
            deleteDialog();
        });

        binding.appBarMain.dashboardScreen.timestampTextView.setText(getFormattedTimestamp());
        binding.appBarMain.dashboardScreen.temperatureLayout.setOnClickListener(view -> {
            Intent weather = new Intent(DashboardScreen.this, WeatherTempActivity.class);
            startActivity(weather);
        });

        getFirebaseTokenFromServer();
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        setConfiguration();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout1);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerSlideAnimationEnabled(true);

        // Set Data
        String userName = AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName);
        String userNumber = AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo);
        View hView = binding.navView.getHeaderView(0);
        nav_user_name = hView.findViewById(R.id.tv_farmerName);
        nav_user_phone = hView.findViewById(R.id.tv_famerPhoneNumber);
        if (!userName.equals("USER_NAME")) {
            String capitalizeStrName = ApUtil.getCamelCaseStreing(userName);
            nav_user_name.setText(!capitalizeStrName.isEmpty() ? capitalizeStrName : userName);
            nav_user_phone.setText(userNumber);
        }

        // navigationView.setNavigationItemSelectedListener(this);
        binding.appBarMain.dashboardScreen.gridViewDashboard.setColumnWidth(GridView.STRETCH_COLUMN_WIDTH);
        if (languageToLoad.equalsIgnoreCase("en")) {
            binding.appBarMain.dashboardScreen.gridViewDashboard.setAdapter(new DashboardAdapter(this, arrayCategory, arrayCategoryImg, "single_item_grid"));
        } else if (languageToLoad.equalsIgnoreCase("hi")) {
            binding.appBarMain.dashboardScreen.gridViewDashboard.setAdapter(new DashboardAdapter(this, arrayCategoryMarathi, arrayCategoryImg, "single_item_grid"));
        }

        appPreferenceManager.clearAll();
        dashboardGridItemsLayoutSetup();
        binding.appBarMain.imgLangChange.setOnClickListener(v -> openChangeLangPopup());
        binding.appBarMain.imgNotification.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardScreen.this, ComingSoonActivity.class);
            startActivity(intent);
        });
        binding.appBarMain.imgCallIcon.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                callingFun();
            }
        });

        binding.appBarMain.dashboardScreen.addCropCardView.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardScreen.this, AddCropActivity.class);
            appPreferenceManager.clearPreference(AppConstants.ACTION_FROM_DASHBOARD);
            startActivity(intent);
        });
        setVersion();
        getFarmerSelectedCrop(languageToLoad);
        requestingPermissions();
        int talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0);
        String talukaName = AppSettings.getInstance().getSavedValue(this, AppConstants.uTALUKA);

        if (talukaID != 0) {
            callForWeatherApi(talukaID);
            binding.appBarMain.dashboardScreen.weatherTalukaTV.setText(talukaName);
        }

        binding.appBarMain.dashboardScreen.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(DashboardScreen.this, DashboardScreen.class));
                    break;
                case  R.id.nav_about:
                    startActivity(new Intent(DashboardScreen.this, TempDashboardActivity.class));
                    break;
                case R.id.nav_chat:
                    startActivity(new Intent(DashboardScreen.this, TempDashboardActivity.class));
                    break;
                case  R.id.nav_help:
                    startActivity(new Intent(DashboardScreen.this, TempDashboardActivity.class));
                    break;
                case R.id.nav_profile:
                    startActivity(new Intent(DashboardScreen.this, Registration.class));
                    break;
            }
            return false;
        });
    }

    private void dashboardGridItemsLayoutSetup() {
        binding.appBarMain.dashboardScreen.gridViewDashboard.setOnItemClickListener((parent, v, position, id) -> {
            switch (position) {
                case 0:

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

                case 1:
                    Intent healthIntent = new Intent(DashboardScreen.this, HealthCardActivity.class);
                    startActivity(healthIntent);
                    break;
                case 2:
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
                case 3:
                    Intent addPeople = new Intent(DashboardScreen.this, ClimateResilintTechnology.class);
                    startActivity(addPeople);
                    break;

                case 4:
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

                case 5:
                    Intent identify = new Intent(getApplicationContext(), Identify_dashboard.class);
                    startActivity(identify);
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
    }

    public static String getFormattedTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy | HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }


    private void getFirebaseTokenFromServer() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                task.getException().printStackTrace();
            }
            String token = task.getResult();
            appPreferenceManager.saveString("FCM_TOKEN", token);
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
    }

    private void setConfiguration() {
        try {
            if (languageToLoad.equalsIgnoreCase("en")) {
                jsonArray = AppHelper.getInstance().getMenuOption();
            } else if (languageToLoad.equalsIgnoreCase("hi")) {
                jsonArray = AppHelper.getInstance().getMenuOptionMarathi();
            }
            DrawerMenuAdapter menuAdapter = new DrawerMenuAdapter(this, jsonArray, farmerId);
            binding.menuListView.setAdapter(menuAdapter);
            binding.menuListView.setOnItemClickListener(this);

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
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = pinfo.versionName;
        if (APIServices.DBT.equalsIgnoreCase("https://ilab-sso.mahapocra.gov.in/")) {
            binding.appVerTextView.setText("App Version " + versionName + " S");
        } else {
            binding.appVerTextView.setText("App Version " + versionName);
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
                            View hView = binding.navView.getHeaderView(0);
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

                            callForWeatherApi(strTalukaId);
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
                                    binding.appBarMain.dashboardScreen.addChangeCropTV.setText(R.string.change_Crop);
                                    binding.appBarMain.dashboardScreen.addChangeCropIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_swap));
                                    binding.appBarMain.dashboardScreen.savedCropNameCardView.setVisibility(View.VISIBLE);
                                    binding.appBarMain.dashboardScreen.savedCropNameTextView.setText(savedCropName);
                                    Picasso.get().load(savedCropImageUrl).fit().centerCrop().into(binding.appBarMain.dashboardScreen.savedCropNameImageView);
                                    binding.appBarMain.dashboardScreen.yourCropTv.setVisibility(View.GONE);
                                    getCropStagesAndAdvisory();
                                    selectedCropList.add(new CropsCategName(selectedCrop.getInt("crop_id"), selectedCrop.getString("name"), selectedCrop.getString("image"), selectedCrop.getString("wotr_crop_id")));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            AppSettings.getInstance().setList(
                                    this, AppConstants.kFarmerCrop,
                                    Arrays.asList(selectedCropList.toArray()));
                        } else {
                            binding.appBarMain.dashboardScreen.savedCropNameCardView.setVisibility(View.GONE);
                            binding.appBarMain.dashboardScreen.yourCropTv.setVisibility(View.VISIBLE);
                            binding.appBarMain.dashboardScreen.yourCropTv.setText(R.string.no_crops_added);
                            binding.appBarMain.dashboardScreen.addChangeCropTV.setText(R.string.add_Crop);
                            binding.appBarMain.dashboardScreen.addChangeCropIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_add_24));
                        }
                        if (selectedCropList != null) {
                            showCropList(selectedCropList);
                        } else {
                            binding.appBarMain.dashboardScreen.selectedCropRecyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        UIToastMessage.show(this, farmersSelectedCropResponse.getResponse());
                    }
                    updateSavedCropDetails();
                    break;
                case 3:
                    ResponseModel deleteSelectedCropResponse = new ResponseModel(jSONObject);
                    if (deleteSelectedCropResponse.getStatus()) {
                        if (showToast) {
                            UIToastMessage.show(this, deleteSelectedCropResponse.getResponse());
                        }
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
                case 5:
                    if (jSONObject != null) {
                        appPreferenceManager.saveString(AppConstants.WEATHER_RESPONSE, jSONObject.toString());
                        ResponseModel response = new ResponseModel(jSONObject);
                        if (response.getStatus()) {
                            JSONObject temperatureObject = jSONObject.optJSONObject("Temperature");
                            int tempMin = temperatureObject.optInt("min");
                            int tempMax = temperatureObject.optInt("max");
                            String temperature = tempMin + "°C / " + tempMax + "°C";
                            binding.appBarMain.dashboardScreen.temperatureTextView.setText(temperature);
                        }
                    }
                    break;
            }
        }
    }

    private void callForWeatherApi(int talukaID) {
        JSONObject jsonObject = new JSONObject();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                jsonObject.put("taluka", talukaID);
                jsonObject.put("lang", languageToLoad);

                RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
                AppInventorApi api = new AppInventorApi(
                        this,
                        APIServices.SSO,
                        "",
                        new AppString(this).getkMSG_WAIT(),
                        false
                );

                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.getWeatherDetails(requestBody);

                api.postRequest(responseCall, this, 5);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateSavedCropDetails() {
        if (selectedCropList != null) {
            if (selectedCropList.size() > 1) {
                for (int i = 0; i < selectedCropList.size(); i++) {
                    if (selectedCropList.get(i).getmName().equals(savedCropName)) {
                        cropId = selectedCropList.get(i).getId();
                        showToast = false;
                        deleteFarmerSelectedCrop();
                    }
                }
            }
        }
    }

    private void showCropList(ArrayList<CropsCategName> selectedCropList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.appBarMain.dashboardScreen.selectedCropRecyclerView.setLayoutManager(layoutManager);
        binding.appBarMain.dashboardScreen.selectedCropRecyclerView.setHasFixedSize(true);
        binding.appBarMain.dashboardScreen.selectedCropRecyclerView.setItemAnimator(new DefaultItemAnimator());
        VideosImageDetailsAdapter adapter = new VideosImageDetailsAdapter(this, selectedCropList, this, "dashboardScreen");
        binding.appBarMain.dashboardScreen.selectedCropRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    void deleteDialog() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.delete_crop_title)
                .setMessage(R.string.delete_crop_message)
                .setPositiveButton(R.string.delete_crop_yes, (dialogInterface, i) -> deleteFarmerSelectedCrop())
                .setNegativeButton(R.string.delete_crop_no, (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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