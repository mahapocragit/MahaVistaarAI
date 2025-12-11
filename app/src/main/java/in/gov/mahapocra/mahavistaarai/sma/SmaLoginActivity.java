package in.gov.mahapocra.mahavistaarai.sma;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.co.appinventor.serviceslibrary.BuildConfig;
import in.gov.mahapocra.mahavistaarai.R;


import in.gov.mahapocra.mahavistaarai.data.model.ResponseModel;
import in.gov.mahapocra.mahavistaarai.util.app_util.AppString;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class SmaLoginActivity extends AppCompatActivity implements ApiCallbackCode {
    private AppSession session;
    Context context;
    private EditText usernameEditText;
    private EditText passEditText;
    String username, versionName, token, machineId,strAddress;
    int appID, userID;
    ProfileModel profileModel = null;
    JSONArray array2;
    private String strToken = null;
    double latitude, longitude;
    String publicIP = null;
    int versionCode,sdkVersion;
    private String strAppVersion = null;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sma_login);
        initComponents();
        setDefaultConfig();
//        forceUpdateRequest();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    private void initComponents() {

        usernameEditText = findViewById(R.id.usernameEditText);
        passEditText = findViewById(R.id.passEditText);
        usernameEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });
//        findViewById(R.id.forgotTextView).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navigateForgotActivity();
//            }
//        });
        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonAction();
            }
        });
    }

    private void setDefaultConfig() {

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;
        versionName = pinfo.versionName;

//        token = FirebaseInstanceId.getInstance().getToken();
//        Log.d("token12345", "" + token);
        session = new AppSession(this);
    }
    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            // Use latitude and longitude
//                            Toast.makeText(SmaLoginActivity.this,
//                                    "Latitude: " + latitude + ", Longitude: " + longitude,
//                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void forceUpdateRequest() {
        try {
            // No request body required for this POST
            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL_FORCE_UPDATE, "", AppConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);

            Call<JsonObject> responseCall = apiRequest.forceUpdateRequest();

            DebugLog.getInstance().d("param forceUpdateRequest=" + responseCall.request().toString());
            DebugLog.getInstance().d("param forceUpdateRequest=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            // Using your custom postRequest method
            api.postRequest(responseCall, this, 7);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void showCADashboard() {
//        Intent intent = new Intent(this, CADashboardActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
    private void showKTDashboard() {
        Intent intent = new Intent(this, KTDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void onButtonAction() {
        requestDataValidation();
    }


    private void requestDataValidation() {

        String user = usernameEditText.getText().toString().trim();
        String pass = passEditText.getText().toString().trim();

        if (user.isEmpty()) {
            UIToastMessage.show(this, "Enter Username");
            usernameEditText.requestFocus();
        } else if (pass.isEmpty()) {
            UIToastMessage.show(this, "Enter Password");
            usernameEditText.requestFocus();
        } else {
            fetchOauthRefreshToken(user,pass);
//            JSONObject jsonObject = new JSONObject();
//
//            try {
//                jsonObject.put("mob", mob.trim());
//                jsonObject.put("pass", pass.trim());
//                jsonObject.put("secret", APIServices.SSO_KEY);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
//            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);
//            Retrofit retrofit = api.getRetrofitInstance();
//            APIRequest apiRequest = retrofit.create(APIRequest.class);
//            Call<JsonObject> responseCall = apiRequest.oauthTokenRequest(requestBody);
//
//            DebugLog.getInstance().d("param=" + responseCall.request().toString());
//            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
//
//            api.postRequest(responseCall, this, 1);
//
//            passEditText.setText("");
        }
    }
    private void fetchOauthRefreshToken(String username,String pass) {
        try {
//            String mob = mobileEditText.getText().toString();
//            String pass = passEditText.getText().toString();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("username", username.trim());
            jsonObject.put("password", pass.trim());
            jsonObject.put("secret", APIServices.SSO_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.oauthRefreshToken(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 4);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//    private void fetchLogin(String strRefreshToken)
//    {
//        String mob = usernameEditText.getText().toString();
//        String pass = passEditText.getText().toString();
//        Log.d("MAYU111","updated token=="+strToken);
//
//        String deviceName = Build.MODEL;
//        String deviceName2 = Build.BRAND;
//        publicIP = getLocalIPv4Address();
//        versionCode = getVersionCode(this);
//        getAddressFromLocation(this, latitude, longitude);
//        sdkVersion = Build.VERSION.SDK_INT;
//        strAddress = getAddressFromLocation(this, latitude, longitude);
//        Log.d("MAYU111","MODEL===="+deviceName);
//        Log.d("MAYU111","BRAND===="+deviceName2);
//        Log.d("MAYU111","sdkVersion===="+sdkVersion);
//        Log.d("MAYU111","VERSION_CODES===="+versionCode);
//        Log.d("MAYU111","strAddress===="+strAddress);
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//
//            JSONArray user_agent_JsonArray = new JSONArray();
//
//            JSONObject jsonObject1 = new JSONObject();
//            jsonObject1.put("model",deviceName );
//            jsonObject1.put("sdk",sdkVersion );
//            jsonObject1.put("brand", deviceName2);
//            jsonObject1.put("version_code",versionCode );
//            user_agent_JsonArray.put(jsonObject1);
//
//            jsonObject.put("username", mob.trim());
//            jsonObject.put("password", pass.trim());
//            jsonObject.put("secret", APIServices.SSO_KEY);
//            jsonObject.put("ip", publicIP);
//            jsonObject.put("is_dashboard", false);
//            jsonObject.put("device", "mobile");
//            jsonObject.put("user_agent", user_agent_JsonArray);
//            jsonObject.put("lat", latitude);
//            jsonObject.put("long", longitude);
//            jsonObject.put("address", strAddress);
//            Log.d("MAYU111","publicIP===="+publicIP);
//            Log.d("Mayu111","latitude===="+latitude);
//            Log.d("Mayu111","longitude===="+longitude);
//            jsonObject.put("refresh_token", strRefreshToken);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
//
////            RuntimeAPI api = new RuntimeAPI(this, APIServices.BASE_API, "", AppConstants.kMSG_WAIT, true);
//        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);
//        Retrofit retrofit = api.getRetrofitInstance();
//        APIRequest apiRequest = retrofit.create(APIRequest.class);
//        Call<JsonObject> responseCall = apiRequest.oauthLoginRequest(requestBody);
//        DebugLog.getInstance().d("param=" + responseCall.request().toString());
//        DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
//        api.postRequest(responseCall, this, 1);
//
//    }

    public static String getLocalIPv4Address() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // Check if it's not an IPv6 address and is not loopback address
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().contains(".")) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // Return -1 or handle the exception accordingly
            return -1;
        }
    }
    public static String getAddressFromLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder addressStringBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressStringBuilder.append(address.getAddressLine(i)).append("\n");
                }
                return addressStringBuilder.toString().trim();
            } else {
                return "Address not found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        DebugLog.getInstance().d("onResponse=" + jsonObject);
        if (jsonObject != null) {
            ResponseModel response = new ResponseModel(jsonObject);
            if (i == 1) {
                if (response.getStatus()) {
                    usernameEditText.setText("");

                    ProfileModel profileModel = null;
                    JSONArray array2 = new JSONArray();
                    try {
                        profileModel = new ProfileModel(jsonObject.getJSONObject("data"));
//                        saveTokenNcheckActiveDeactive(profileModel.getId(), profileModel.getUsername());
                        // Get "data" object
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        JSONArray villagesArray = dataObject.getJSONArray("villages");
                        if (villagesArray.length() > 0) {
                            JSONObject villageObj = villagesArray.getJSONObject(0);
                            // Extract "village" and "village_code"
                            String villageName = villageObj.getString("village");
                            int villageCode = villageObj.getInt("village_code");
                            AppSettings.getInstance().setIntValue(this, AppConstants.kVillageCensus, villageCode);
                            AppSettings.getInstance().setValue(this, AppConstants.kVillageName, villageName);

                            // Print in log or show in TextView
                            Log.d("VillageInfo", "Village: " + villageName + ", Code: " + villageCode);

                            // Example: display in Toast
//                            Toast.makeText(this, "Village: " + villageName + "\nCode: " + villageCode, Toast.LENGTH_LONG).show();
                        }
                        try {
                            array2 = profileModel.getRolearray();
                        } catch (Exception e) {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (profileModel.getPassword_change_requires() == 1) {
                        AppSettings.getInstance().setIntValue(this, AppConstants.kROLE_ID, profileModel.getRole_id());
                        AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
                        AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
                        AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
                        AppSettings.getInstance().setValue(this, AppConstants.kDesignation, profileModel.getDesignation());
                        AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_LOGGED_IN, false);
                        AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
                        AppSettings.getInstance().setValue(this, AppConstants.kSwitch, "no");
//                        Intent intent = new Intent(this, ChangePasswordActivity.class);
//                        startActivity(intent);
                    }
                    //  else if (array2.length()<=1) {
                    //CA
//                    if (profileModel != null && (profileModel.getRole_id() == AppRole.CA.id())) {
//
//
//                        AppSettings.getInstance().setIntValue(this, AppConstants.kROLE_ID, profileModel.getRole_id());
//                        AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
//                        AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
//                        AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
//                        AppSettings.getInstance().setValue(this, AppConstants.kLevel, profileModel.getLevel());
//                        AppSettings.getInstance().setValue(this, AppConstants.kDesignation, profileModel.getDesignation());
//                        AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_LOGGED_IN, true);
//                        AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
//                        AppSettings.getInstance().setValue(this, AppConstants.kSwitch, "no");
////                        showCADashboard();
//
//                    }
                    //KT-Krushi Tai
                    if (profileModel != null && (profileModel.getRole_id() == AppRole.KT.id())) {

                        AppSettings.getInstance().setIntValue(this, AppConstants.kROLE_ID, profileModel.getRole_id());
                        AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
                        AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
                        AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
                        AppSettings.getInstance().setValue(this, AppConstants.kLevel, profileModel.getLevel());
                        AppSettings.getInstance().setValue(this, AppConstants.kDesignation, profileModel.getDesignation());
                        AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_LOGGED_IN, true);
                        AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
                        AppSettings.getInstance().setValue(this, AppConstants.kSwitch, "no");
                        showKTDashboard();

                    }


                } else {
                    UIToastMessage.show(this, response.getResponse());
//                    Toast.makeText(context, "Access Denied...", Toast.LENGTH_SHORT).show();

//                    Intent ii = new Intent(SmaLoginActivity.this, SelectoRoleActivity.class);
//                    startActivity(ii);
//                    AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
//                    AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
//                    AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
//                    AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
                }
            }

//            else {
//                UIToastMessage.show(this, response.getResponse());
//            }

            if (i == 11) {
                ResponseModel responseModel = new ResponseModel(jsonObject);
                if (responseModel.getStatus()) {

//                    if (profileModel.getPassword_change_requires() == 1) {
//                        AppSettings.getInstance().setIntValue(this, AppConstants.kROLE_ID, profileModel.getRole_id());
//                        AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
//                        AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
//                        AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
//                        AppSettings.getInstance().setValue(this, AppConstants.kDesignation, profileModel.getDesignation());
//                        AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_LOGGED_IN, false);
//                        AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
//                        AppSettings.getInstance().setValue(this, AppConstants.kSwitch, "no");
//                        Intent intent = new Intent(this, ChangePasswordActivity.class);
//                        startActivity(intent);
//                    } else if (array2.length() <= 1) {
//                        //CA
//                        if (profileModel != null && (profileModel.getRole_id() == AppRole.CA.id())) {
//
//                            AppSettings.getInstance().setIntValue(this, AppConstants.kROLE_ID, profileModel.getRole_id());
//                            AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
//                            AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
//                            AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
//                            AppSettings.getInstance().setValue(this, AppConstants.kDesignation, profileModel.getDesignation());
//                            AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_LOGGED_IN, true);
//                            AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
//                            AppSettings.getInstance().setValue(this, AppConstants.kSwitch, "no");
//                            showCADashboard();
//                        }
//
//
//                        //T&M Service
//                        if (profileModel != null && (profileModel.getRole_id() == AppRole.SP.id())) {
//
//                            AppSettings.getInstance().setIntValue(this, AppConstants.kROLE_ID, profileModel.getRole_id());
//                            AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
//                            AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
//                            AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
//                            AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_LOGGED_IN, true);
//                            AppSettings.getInstance().setValue(this, AppConstants.kLevel, profileModel.getLevel());
//                            AppSettings.getInstance().setValue(this, AppConstants.kDesignation, profileModel.getDesignation());
//                            AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
//                            AppSettings.getInstance().setValue(this, AppConstants.kSwitch, "no");
//                            showTMDashboard();
//                        }
//
//
//                        //SDAO
//                        if (profileModel != null && (profileModel.getRole_id() == AppRole.SDAO.id())) {
//
//
//                            AppSettings.getInstance().setIntValue(this, AppConstants.kROLE_ID, profileModel.getRole_id());
//                            AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
//                            AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
//                            AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
//                            AppSettings.getInstance().setValue(this, AppConstants.kLevel, profileModel.getLevel());
//                            AppSettings.getInstance().setValue(this, AppConstants.kDesignation, profileModel.getDesignation());
//                            AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_LOGGED_IN, true);
//                            AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
//                            AppSettings.getInstance().setValue(this, AppConstants.kSwitch, "no");
//                            showSDAODashboard();
//
//                        }
//
//
//                        //DSAO
//                        if (profileModel != null && (profileModel.getRole_id() == AppRole.DSAO.id())) {
//
//                            AppSettings.getInstance().setIntValue(this, AppConstants.kROLE_ID, profileModel.getRole_id());
//                            AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
//                            AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
//                            AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
//                            AppSettings.getInstance().setValue(this, AppConstants.kLevel, profileModel.getLevel());
//                            AppSettings.getInstance().setValue(this, AppConstants.kDesignation, profileModel.getDesignation());
//                            AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_LOGGED_IN, true);
//                            AppSettings.getInstance().setValue(this, AppConstants.kSwitch, "no");
//                            AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
//                            showDSAODashboard();
//                        }
//                        // Log.d("profileModel", profileModel.getLevel() + "  " + SSORole.PMU.role());
//                        //PMU
//                        //if (profileModel != null && (profileModel.getRole_id() == AppRole.PMU.id())) {
//                        if (profileModel != null && (profileModel.getLevel().equalsIgnoreCase(SSORole.PMU.role()))) {
//                            AppSettings.getInstance().setIntValue(this, AppConstants.kROLE_ID, profileModel.getRole_id());
//                            AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
//                            AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
//                            AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
//                            AppSettings.getInstance().setValue(this, AppConstants.kLevel, profileModel.getLevel());
//                            AppSettings.getInstance().setValue(this, AppConstants.kDesignation, profileModel.getDesignation());
//                            AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_LOGGED_IN, true);
//                            AppSettings.getInstance().setValue(this, AppConstants.kSwitch, "no");
//                            AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
//                            showPMUDashboard();
//                        }
//                    } else {
//                        Intent ii = new Intent(SmaLoginActivity.this, SelectoRoleActivity.class);
//                        startActivity(ii);
//                        AppSettings.getInstance().setValue(this, AppConstants.kTOKEN, response.getToken());
//                        AppSettings.getInstance().setIntValue(this, AppConstants.kUSER_ID, profileModel.getId());
//                        AppSettings.getInstance().setValue(this, AppConstants.kUSERNAME, profileModel.getUsername());
//                        AppSettings.getInstance().setValue(this, AppConstants.kLOGIN_DATA, response.getData().toString());
//                    }

                    DebugLog.getInstance().d("active_deactive_response=" + jsonObject);
                }
            }
            if (i == 4) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response1 = new ResponseModel(jsonObject);

                    if (response1.getStatus()) {
                        strToken=response1.getRefreshToken();
                        Log.d("MAYU111","RefreshToken==="+strToken);
//                        fetchLogin(strToken);
                    } else {
                        Toast.makeText(this, "" + response1.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (i == 7) {
                if (jsonObject != null) {
                    DebugLog.getInstance().d("onResponse=" + jsonObject);
                    ResponseModel response1 = new ResponseModel(jsonObject);
                    strAppVersion = response1.getForceUpdateAppVersion();
                    int serverVersion = Integer.parseInt(strAppVersion);
                    Log.d("MAYU111", "serverVersion===" + serverVersion);
                    int versionCode = BuildConfig.VERSION_CODE;
                    Log.d("MAYU111", "versionCode===" + versionCode);
                    if (serverVersion > versionCode) {
                        // Show update popup
                        Log.d("MAYU111", "serverVersion > versionCode =" + serverVersion + " > " + versionCode);
                        showUpdateDialog();
                    }
                }
            }
        }
    }
    private void showUpdateDialog() {
        SpannableString title = new SpannableString("    Update Available");
        title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setSpan(new ForegroundColorSpan(Color.parseColor("#22a7ff")), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Use your desired color
        // Use your desired color
        new AlertDialog.Builder(SmaLoginActivity.this)
                .setTitle(title)
                .setMessage("A new version of the app is available. Please update to continue.")
                .setIcon(R.drawable.icon_update)
                .setPositiveButton("Update", (dialog, which) -> {
                    // Open Play Store or URL
                    String url1 = "https://mahapocra.gov.in/mr/applications";
                    openLinkInChrome(url1);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Dismiss the dialog
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
    private void openLinkInChrome(String url) {
        try {
            // Create the intent to open the link
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setPackage("com.android.chrome"); // Specify Chrome as the target app

            // Start Chrome browser
            startActivity(intent);
        } catch (Exception e) {
            // Fallback: Open in the default browser if Chrome is not installed
            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW);
            fallbackIntent.setData(Uri.parse(url));
            startActivity(fallbackIntent);
        }
    }
    private void saveTokenNcheckActiveDeactive(int userId, String UserName) {

        username = UserName;
        userID = userId;
        appID = session.getAppId();
        machineId = getMachineId();
        Log.d("appVersionLoggedDetails", " username=" + username + "  app_id=" + appID + "  versionName=" + versionName + " token=" + token + " user_id=" + userID + " device_ID=" + machineId);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("app_id", appID);
            jsonObject.put("version_number", versionName);
            jsonObject.put("fcm_token", token);
            jsonObject.put("user_id", userID);
            jsonObject.put("device_id", machineId);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.checkActivateDeactivateUser(requestBody);
            api.postRequest(responseCall, this, 11);
            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getMachineId() {

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;

    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }

}
