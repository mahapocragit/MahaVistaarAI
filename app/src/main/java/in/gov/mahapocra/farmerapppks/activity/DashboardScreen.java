package in.gov.mahapocra.farmerapppks.activity;

import static android.app.PendingIntent.getActivity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.adapter.DashboardAdapter;
import in.gov.mahapocra.farmerapppks.adapter.DrawerMenuAdapter;
import in.gov.mahapocra.farmerapppks.adapter.VideosImageDetailsAdpter;
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
    private DrawerLayout drawer;
    private GridView gridView;
    private TextView nameTextView;
    private TextView mobileNoTextView;

    private TextView addCrop;
    private TextView yourCrop;
    private TextView textNewUpdates;
    private TextView aboutPocraText1;
    private TextView aboutPocraText2;
    private TextView nav_user_name;
    private TextView nav_user_phone;
    private ImageView imgLangChange;
    private ImageView imgNotification;
    private ImageView imgCallIcon;
    private LinearLayout linearLayoutForum;
    private LinearLayout linearLayoutSupport;
    private LinearLayout linearLayoutProfile;
    private ListView menuListView;
    private RecyclerView selectedCropListRecyc;
    String languageToLoad;
    private String newupdateFlag;
    private int farmerId = 0;
    private int cropId = 0;

    private JSONArray jsonArray;

    private static final String[] arrayCategery = new String[]{
            "Weather", "Identify Pest/Disease","Pests and Diseases","Fertilizer Calculator", "Climate Resilent Technology", "Soil Health Card",
            "Market Price", "Warehouse Availabilities", "DBT Schemes"

    };

    private static final String[] arrayCategeryMarathi = new String[]{
            "हवामान", "किटक/रोग ओळखा","कीड व रोग","खत मात्रा गणक (कॅलक्यूलेटर)", "हवामान अनुकूल तंत्रज्ञान", "मृदा आरोग्य पत्रिका",
            "बाजारभाव", " गोदाम उपलब्धता","थेट लाभ हस्तांतरण योजना"

    };

    static int[] arrayCategeryImg = new int[]{
            R.drawable.weather_icon, R.drawable.identify_disease_icon,R.drawable.pets_n_disease_img, R.drawable.fertilizer_calculator, R.drawable.climate_resilent_technology_icon, R.drawable.soil_health_card_icon,
            R.drawable.market_price_icon, R.drawable.warehouse_availability_icon, R.drawable.dbt_schemes_icon
    };

    ArrayList<CropsCategName> selectedCropList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //          String  languageToLoad = "hi";

        languageToLoad = "en";
        if (AppSettings.getLanguage(DashboardScreen.this).equalsIgnoreCase("2")) {
            languageToLoad = "hi";
        }
        Log.d("languageToLoad121212=", languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_dashboard_screen);
        init();


        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        setConfiguration();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout1);


        // navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerSlideAnimationEnabled(true);

        // Set Data
        String userNeme = AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName);
        String userNumber = AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo);
        Log.d("getStrName=", userNeme);
        navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        nav_user_name = hView.findViewById(R.id.tv_farmerName);
        nav_user_phone = hView.findViewById(R.id.tv_famerPhoneNumber);
        if (!userNeme.equals("USER_NAME")) {
            String capitalizeStrName = ApUtil.getCamelCaseStreing(userNeme);
            nav_user_name.setText(capitalizeStrName);
            nav_user_phone.setText(userNumber);
        }

        // navigationView.setNavigationItemSelectedListener(this);
        gridView = findViewById(R.id.gridViewJobs);
        gridView.setColumnWidth(GridView.AUTO_FIT);
        if (languageToLoad.equalsIgnoreCase("en")) {
            gridView.setAdapter(new DashboardAdapter(this, arrayCategery, arrayCategeryImg,"single_item_grid"));
            Log.d("", "");
        } else if (languageToLoad.equalsIgnoreCase("hi")) {
            gridView.setAdapter(new DashboardAdapter(this, arrayCategeryMarathi, arrayCategeryImg,"single_item_grid"));
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView txtLable = v.findViewById(R.id.grid_item_label);
                //Toast.makeText(DashboardScreen.this, txtLable.getText(), Toast.LENGTH_SHORT).show();
                Log.d("gridView", "Pos=" + position);

                if (position == 0) {
                    Intent weather = new Intent(DashboardScreen.this, WeatherHome.class);
                    startActivity(weather);
                } else if (position == 1) {

                    Intent i = new Intent(getApplicationContext(), Identify_dashboard.class);
                    startActivity(i);

                } else if (position == 2) {
                    Intent sharing = new Intent(DashboardScreen.this, CommingSoonActivity.class);
                    startActivity(sharing);
                }
                else if (position == 3) {
                    Intent sharing = new Intent(DashboardScreen.this, CommingSoonActivity.class);
                    startActivity(sharing);

                } else if (position == 4) {

                    Intent addPeople = new Intent(DashboardScreen.this, ClimateResilintTechnology.class);
                    startActivity(addPeople);
                } else if (position == 5) {
                    Intent addPeople = new Intent(DashboardScreen.this, HealthCardActivity.class);
                    startActivity(addPeople);
                } else if (position == 6) {
                    Intent sharing = new Intent(DashboardScreen.this, MarketPrice.class);
                    startActivity(sharing);
                }
                else if (position == 7) {
                    Intent sharing = new Intent(DashboardScreen.this, Warehouse.class);
                    startActivity(sharing);
                } else if (position == 8) {
                   // Intent sharing = new Intent(DashboardScreen.this, CropAdvisory.class);
                    Intent sharing = new Intent(DashboardScreen.this, DbtSchemes.class);
                    startActivity(sharing);
                }
//                else if (position == 9) {
//                    Intent sharing = new Intent(DashboardScreen.this, CropAdvisory.class);
//                    startActivity(sharing);
//                }

                // Intent intent = new Intent(JobsMenuGridListScreen.this, JobFoundList.class);
                // startActivity(intent);
                //  txtLable.setBackgroundColor(Color.parseColor("#F21B92"));
            }
        });




//        selectedCropList = new ArrayList<CropsCategName>();
//        List<Object> selectedArrayLists;
//        selectedArrayLists = AppSettings.getInstance().getList(this, AppConstants.kFarmerCrop);
//        if (!(selectedArrayLists == null)  && selectedArrayLists.size() > 0) {
//            if (languageToLoad.equalsIgnoreCase("en")) {
//                yourCrop.setVisibility(View.VISIBLE);
//                Log.d("selectedArrayListsss", selectedArrayLists.toString());
//                Gson gson = new Gson();
//                String element = gson.toJson(
//                        selectedArrayLists,
//                        new TypeToken<ArrayList<CropsCategName>>() {
//                        }.getType());
//                Type listType = new TypeToken<ArrayList<CropsCategName>>() {
//                }.getType();
//                selectedCropList = new Gson().fromJson(element, listType);
//                showCropList(selectedCropList);
//            } else if (languageToLoad.equalsIgnoreCase("hi")) {
//                getFarmerSelectedCrop();
//            }
//        } else {
//            getFarmerSelectedCrop();
//        }


        aboutPocraText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardScreen.this, AboutPocra.class);
                startActivity(intent);
            }
        });
        aboutPocraText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardScreen.this, MyVillageProfilePdf.class);
                startActivity(intent);
            }
        });
        imgLangChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeLangPopup();
            }
        });
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardScreen.this, CommingSoonActivity.class);
                intent.putExtra("notification", "mayu");
                startActivity(intent);
            }
        });
        linearLayoutForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardScreen.this, DbtStatus.class);
                startActivity(intent);
            }
        });
        linearLayoutSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (farmerId > 0) {
                    Intent intent = new Intent(DashboardScreen.this, Grievances.class);
                    intent.putExtra("FAAPRegistrationID", farmerId);
                    startActivity(intent);
                } else {
                    UIToastMessage.show(DashboardScreen.this, "Please Login First...");
                }
            }
        });
        linearLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (farmerId > 0) {
                    Intent intent = new Intent(DashboardScreen.this, Registration.class);
                    intent.putExtra("FAAPRegistrationID", farmerId);
                    startActivity(intent);
                } else {
                    UIToastMessage.show(DashboardScreen.this, "Please Login First...");
                }

            }
        });
        imgCallIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    callingFun();
                }
            }
        });
        textNewUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // climateResilientGroupList();
                Intent intent = new Intent(DashboardScreen.this, NewUpdateKharip.class);

                startActivity(intent);
            }
        });

        addCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardScreen.this, AddCropActivity.class);
                // Intent intent = new Intent(DashboardScreen.this, SelectSowingDataAndFarmer.class);
                startActivity(intent);
            }
        });
        setVersion();
        getFarmerSelectedCrop(languageToLoad);
//        UserDetailsModel userDetailsModel=new UserDetailsModel();
//        String strName=userDetailsModel.getName();
//        String strMobNo=userDetailsModel.getMobileNo();
//        Log.d("DashboardScreen::","Name:"+strName);
//        Log.d("DashboardScreen::","MobNo:"+strMobNo);
//        if(nameTextView.equals(null)) {
//            Toast.makeText(this, "Null Name", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            nameTextView.setText(strName);
//            mobileNoTextView.setText(strMobNo);
//        }

    }


    public void init() {
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0);
        if (farmerId > 0) {
            getUserDetails();
        }
        nameTextView = findViewById(R.id.nameTextView);
        addCrop = findViewById(R.id.AddCropTv);
        yourCrop = findViewById(R.id.yourCropTv);
        selectedCropListRecyc = findViewById(R.id.selectedCrops);
        textNewUpdates = findViewById(R.id.textNewUpdates);
        mobileNoTextView = findViewById(R.id.mobNoTextView);
        gridView = findViewById(R.id.gridViewJobs);
        aboutPocraText1 = findViewById(R.id.textAboutPocra);
        aboutPocraText2 = findViewById(R.id.textMyvillage);
        imgLangChange = findViewById(R.id.imgLangChange);
        imgNotification = findViewById(R.id.imgNotification);
        imgCallIcon = findViewById(R.id.imgCallIcon);
        linearLayoutForum = findViewById(R.id.forum);
        linearLayoutSupport = findViewById(R.id.support);
        linearLayoutProfile = findViewById(R.id.profile);
        menuListView = findViewById(R.id.menuListView1);
        ObjectAnimator animator = ObjectAnimator.ofInt(textNewUpdates, "backgroundColor", Color.BLUE, Color.RED, Color.GREEN);
        // duration of one color
        animator.setDuration(500);
        animator.setEvaluator(new ArgbEvaluator());
        // color will be show in reverse manner
        animator.setRepeatCount(Animation.REVERSE);
        // It will be repeated up to infinite time
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }

    private void setConfiguration() {
        try {
            if (languageToLoad.equalsIgnoreCase("en")) {
                jsonArray = AppHelper.getInstance().getMenuOption();
            } else if (languageToLoad.equalsIgnoreCase("hi")) {
                jsonArray = AppHelper.getInstance().getMenuOptionMarathi();
            }
            Log.d("jsonArray111", jsonArray.toString());
            Log.d("farmerIdfarmerId", String.valueOf(farmerId));
            DrawerMenuAdapter menuAdapter = new DrawerMenuAdapter(this, jsonArray, farmerId);
            menuListView.setAdapter(menuAdapter);
            menuListView.setOnItemClickListener(this);

        } catch (Exception e) {
            Log.d("JSONException", e.toString());
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
        int versionNumber = pinfo.versionCode;
        String versionName = pinfo.versionName;
        if (APIServices.DBT.equalsIgnoreCase("https://ilab-sso.mahapocra.gov.in/")) {
            appVersionTextView.setText("App Version " + versionName + " S");
        } else {
            appVersionTextView.setText("App Version " + versionName);
        }

        Log.d("gfghfghfghfgh", versionName);
        AppSettings.getInstance().setValue(DashboardScreen.this, AppConstants.kAPP_BUILD_VERSION, versionName);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void callingFun() {
//        String number = "2222163352";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:02222163352"));
        startActivity(intent);
    }


    private void getUserDetails() {
        JSONObject jsonObject = new JSONObject();
        //var farmerId: Int = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        Log.d("farmerId11111", String.valueOf(farmerId));
        try {
            jsonObject.put("SecurityKey", APIServices.SSO_KEY);
            jsonObject.put("FAAPRegistrationID", farmerId);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.DBT, "", new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getGetRegistration(requestBody);
            DebugLog.getInstance().d("param1=" + responseCall.request());
            DebugLog.getInstance().d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 1);

            DebugLog.getInstance().d("param=" + responseCall.request());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getFarmerSelectedCrop(String language) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lang", language);
            jsonObject.put("farmer_id", farmerId);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getFarmersSelectedCrop(requestBody);
            DebugLog.getInstance().d("param1=" + responseCall.request());
            DebugLog.getInstance().d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 2);

            DebugLog.getInstance().d("param=" + responseCall.request());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
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
            AppinventorApi api = new AppinventorApi(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.deleteSelectedCrop(requestBody);
            DebugLog.getInstance().d("param1=" + responseCall.request());
            DebugLog.getInstance().d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 3);

            DebugLog.getInstance().d("param=" + responseCall.request());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
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

        tvMarathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            }
        });

        dialog.show();

    }

//    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//           item.setIcon(R.drawable.warehouse_availabilities);
//            Intent i=new Intent(DashboardScreen.this, Registration.class);
//            startActivity(i);
//        } else if (id == R.id.nav_login) {
//
//            Intent i=new Intent(DashboardScreen.this, LoginScreen.class);
//            startActivity(i);
//        } else if (id == R.id.nav_gallery) {
//
//            Intent i=new Intent(DashboardScreen.this, LoginScreen.class);
//            startActivity(i);
//        } else if (id == R.id.nav_slideshow) {
//            Intent i=new Intent(DashboardScreen.this, Warehouse.class);
//            startActivity(i);
//        } else if (id == R.id.nav_my_farm_survay) {
//            Intent i=new Intent(DashboardScreen.this, AboutPocra.class);
//            startActivity(i);
//
//        } else if (id == R.id.nav_crop_Registration) {
//            Toast.makeText(this, "Comming soon.......", Toast.LENGTH_SHORT).show();
//
//        } else if (id == R.id.nav_Trainings_Workshops_Events_Exposures) {
//            AppUtility.getInstance().clearAppSharedPrefData(this, AppConstants.kSHARED_PREF);
//            Intent intent =new  Intent(DashboardScreen.this, SplashScreen.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//        }
//        //DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    @Override
    public void onFailure(Object obj, Throwable th, int i) {
    }
    @Override
    public void onResponse(JSONObject jSONObject, int i) {

        if (i == 1) {
            if (jSONObject != null) {
                Log.d("jSONObject232dacsboard", jSONObject.toString());
                DebugLog.getInstance().d("onResponse=$jSONObject");
                ResponseModel response = new ResponseModel(jSONObject);
                if (response.getStatus()) {
                    String strName = null;
                    try {
                        strName = jSONObject.getString("Name");
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

                        String districtName = AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST);
                        String taluka = AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA);

                        String userNeme = AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName);
                        String userNumber = AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo);
                        navigationView = findViewById(R.id.nav_view);
                        View hView = navigationView.getHeaderView(0);
                        nav_user_name = hView.findViewById(R.id.tv_farmerName);
                        nav_user_phone = hView.findViewById(R.id.tv_famerPhoneNumber);
                        if (!userNeme.equals("USER_NAME")) {
                            try {
                                String capitalizeStrName = ApUtil.getCamelCaseStreing(userNeme);
                                nav_user_name.setText(capitalizeStrName);
                                nav_user_phone.setText(userNumber);
                            } catch (StringIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        if (i == 2 && jSONObject != null) {
            ResponseModel response = new ResponseModel(jSONObject);
            if (response.getStatus()) {
                JSONArray selectedCrops = response.getDataArrays();
                yourCrop.setVisibility(View.INVISIBLE);
                if (selectedCrops.length() > 0) {
                    yourCrop.setVisibility(View.VISIBLE);
                    selectedCropList = new ArrayList<>();
                    for (int j = 0; j < selectedCrops.length(); j++) {
                        try {
                            JSONObject selectedCrop = selectedCrops.getJSONObject(j);
                            selectedCropList.add(new CropsCategName(selectedCrop.getInt("crop_id"), selectedCrop.getString("name"), selectedCrop.getString("image"), selectedCrop.getString("wotr_crop_id")));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    AppSettings.getInstance().setList(
                            this, AppConstants.kFarmerCrop,
                            Arrays.asList(selectedCropList.toArray()));
                }
                if(!(selectedCropList == null)){
                    showCropList(selectedCropList);
                }
            }else {
                UIToastMessage.show(this, response.getResponse());
            }
        }
        if (i == 3 && jSONObject != null) {
            ResponseModel response = new ResponseModel(jSONObject);
            if (response.getStatus()) {
                UIToastMessage.show(this, response.getResponse());
                AppSettings.getInstance().setList(this, AppConstants.kFarmerCrop, null);
                selectedCropList.clear();
                getFarmerSelectedCrop(languageToLoad);
            } else {
                UIToastMessage.show(this, response.getResponse());
            }
        }

    }

    private void showCropList(ArrayList<CropsCategName> selectedCropList) {
        selectedCropListRecyc.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        selectedCropListRecyc.setLayoutManager(layoutManager);
        selectedCropListRecyc.setHasFixedSize(true);
        selectedCropListRecyc.setItemAnimator(new DefaultItemAnimator());
        VideosImageDetailsAdpter adapter = new VideosImageDetailsAdpter(this, selectedCropList, this, "dashboardScreen");
        selectedCropListRecyc.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            int id = jsonObject.getInt("id");
            Log.d("id1111", String.valueOf(id));
            if (id == 0) {
                Intent intent = new Intent(DashboardScreen.this, Registration.class);
                intent.putExtra("FAAPRegistrationID", farmerId);
                startActivity(intent);
            }
            if (id == 1) { //Intent intent = new Intent(this, FinancialDetailsActivity.class);
                if (farmerId > 0) {
                    Intent sharing = new Intent(DashboardScreen.this, BandavarActivity.class);
                    startActivity(sharing);
                } else {
                    UIToastMessage.show(DashboardScreen.this, "Please Login First...");
                }
            }
            if (id == 2) {
                Intent intent = new Intent(DashboardScreen.this, DbtStatus.class);
                intent.putExtra("userPhoneNumber", String.valueOf(nav_user_phone));
                startActivity(intent);
            }
            if (id == 3) {
                Intent intent = new Intent(DashboardScreen.this, NotificationListActivity.class);
                startActivity(intent);
            }
            if (id == 4) {
                Intent intent = new Intent(DashboardScreen.this, Gis.class);
                startActivity(intent);
            }
            if (id == 5) {
                Intent intent = new Intent(DashboardScreen.this, TrainingLocationSelection.class);
                startActivity(intent);
            }
            if (id == 6) {
                Intent intent = new Intent(DashboardScreen.this, LoginScreen.class);
                startActivity(intent);
            }
            if (id == 7) {
                //AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_OFFLINE, false)
                logoutFromApp();
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
        Intent intent = new Intent(DashboardScreen.this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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