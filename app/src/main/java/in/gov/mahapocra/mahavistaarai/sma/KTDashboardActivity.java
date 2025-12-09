package in.gov.mahapocra.mahavistaarai.sma;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;

import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.data.model.ResponseModel;
import in.gov.mahapocra.mahavistaarai.util.app_util.AppString;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class KTDashboardActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode  {

    private ListView mMenuListView;
    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView designationTextView;
    private ImageView ivNotificationCount;
    private TextView tv_notification_count;
    private RecyclerView recyclerView;
    String versionName;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    TextView appVersionTextView;
    TextView switchtxt;
    int appID,userID,notifiCountValue;
    JSONArray array2 = new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ktdashboard);

        initComponents();
        setConfiguration();
    }
    private void initComponents() {

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
        appVersionTextView = findViewById(R.id.appVerTextView);
        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        designationTextView = findViewById(R.id.designationTextView);
        recyclerView = findViewById(R.id.recyclerView);
        mMenuListView = findViewById(R.id.menuListView);
        ivNotificationCount = findViewById(R.id.iv_notification_image);
        tv_notification_count = findViewById(R.id.tv_notification_count);
    }

    private void setConfiguration() {

        // updateConfig();
        findViewById(R.id.logoutImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askUserPermission();
            }
        });

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;
        versionName = pinfo.versionName;
        if(APIServices.BASE_API.equalsIgnoreCase("https://ilab-sma-api.mahapocra.gov.in/v22/")){
            appVersionTextView.setText("App Version " + versionName+"S");}
        else{
            {
                appVersionTextView.setText("App Version " + versionName);}
        }
        JSONArray jsonArray = AppHelper.getInstance().getKTDashboardMenu();

        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        KTDashboardAdapter adapter = new KTDashboardAdapter(this, this, jsonArray);
        recyclerView.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
                nestedScrollView.scrollTo(0, 0);
            }
        }, 200);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(KTDashboardActivity.this, MyProfileOthersActivity.class).putExtra("role","KT");;
//                startActivity(intent);
            }
        });

        /*DrawerMenuAdapter menuAdapter = new DrawerMenuAdapter(this, jsonArray);
        mMenuListView.setAdapter(menuAdapter);

        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_menu_footer, null, false);
        mMenuListView.addFooterView(footerView);

        TextView appVersion = footerView.findViewById(R.id.appVersionTextView);

        String appVer = "App Version : " + BuildConfig.VERSION_NAME;
        appVersion.setText(appVer);*/
        requestDataValidation();
        getUnreadNotificationCount();
        dataBinding();


//        ivNotificationCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(KTDashboardActivity.this, NotificationListActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();

//        getUnreadNotificationCount();
        requestDataValidation();
    }

    private void getUnreadNotificationCount() {
        AppSession session = new AppSession(this);
        appID =  session.getAppId();
        userID = AppSettings.getInstance().getIntValue(this, AppConstants.kUSER_ID, 0);
        String appId = String.valueOf(appID);
        String userId = String.valueOf(userID);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_id", appId);
            jsonObject.put("user_id", userId);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorIncAPI api = new AppinventorIncAPI(this,  APIServices.BASE_API, session.getToken(), "Please Wait...", true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getFirebaseUnreadNotificationCount(requestBody);

            api.postRequest(responseCall, this, 1);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {

        AppString appString = new AppString(this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do You Want To Exit SMA App ?");
        alertDialogBuilder.setPositiveButton(appString.getNO(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.setNegativeButton(appString.getYES(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cadashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {
        try {
            DebugLog.getInstance().d("");

            JSONObject jsonObject = (JSONObject) o;

            int id = jsonObject.getInt("id");

            if (id == 0) {
//                    Toast.makeText(this, "Comming Soon...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, KTProjectInformation.class);
                intent.putExtra("KTMode", CAClusterMode.MANAGE);
                startActivity(intent);
            }
            if (id == 1) {

                Intent intent = new Intent(this, KTActivity.class);
                intent.putExtra("KTMode", CAClusterMode.MANAGE);
                startActivity(intent);
            }
            if (id == 2) {
                Intent intent = new Intent(this, KTReportActivity.class);
                intent.putExtra("KTMode", CAClusterMode.MANAGE);
                startActivity(intent);

            }
//            if (id == 3) {
//                Intent intent = new Intent(this, MyProfileOthersActivity.class).putExtra("role","KT");;
//                startActivity(intent);
//
//                /*Intent intent = new Intent(this, CAListActivity.class);
//                intent.putExtra("CAClusterMode", CAClusterMode.ASSIGN);
//                startActivity(intent);*/
//            }

//                if (id == 2) {
//
//                    AppSession session = new AppSession(this);
//                    ProfileModel model = session.getProfileModel();
//
//                    Intent intent = new Intent(this, SubDivisionActivity.class);
//                    intent.putExtra("district_id", model.getDistrict_id());
//                    startActivity(intent);
//
////                Intent intent = new Intent(this, SubDivisionActivity.class);
////                intent.putExtra("CAClusterMode", CAClusterMode.REMOVE);
////                startActivity(intent);
//                }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void dataBinding() {
        try {

            AppSession session = new AppSession(this);

            ProfileModel profileModel = session.getProfileModel();

            if (profileModel.getProfile_pic().length() > 0) {
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(3)
                        .cornerRadiusDp(40)
                        .oval(false)
                        .build();

                Picasso.get()
                        .load(profileModel.getProfile_pic())
                        .transform(transformation)
                        .resize(150, 150)
                        .centerCrop()
                        .into(profileImageView);

            } else {
                profileImageView.setImageResource(R.mipmap.ic_profile);
            }

            String name = profileModel.getFirst_name()+" "+profileModel.getMiddle_name()+" "+profileModel.getLast_name() +"-"+AppSettings.getInstance().getValue(this, AppConstants.kDesignation, AppConstants.kDesignation);
            nameTextView.setText(name);
            String strVillName = AppSettings.getInstance().getValue(this, AppConstants.kVillageName, " ");
            int strVillCensus =AppSettings.getInstance().getIntValue(this, AppConstants.kVillageCensus, 0);

            designationTextView.setText(strVillName+" ("+strVillCensus+") ");

//            districtTextView.setText(profileModel.getDistrict_name());
//            switchtxt= findViewById(R.id.switchtxt);
//            switchtxt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent ii=new Intent(KTDashboardActivity.this, SelectoRoleActivity.class);
//                    startActivity(ii);
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestDataValidation() {
        int userID = AppSettings.getInstance().getIntValue(this, AppConstants.kUSER_ID, 0);
        String id1= String.valueOf(userID);
        // print actual String
        System.out.println("Sample String:\n"
                + id1);

        // Encode into Base64 format
        String BasicBase64format
                = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            BasicBase64format = Base64.getEncoder()
                    .encodeToString(id1.getBytes());
        }
        String id=BasicBase64format;
        // print encoded String
        System.out.println("Encoded String:\n"
                + id);
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("user_id", id);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getroledata(requestBody);
            api.postRequest(responseCall, this, 2);
            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void askUserPermission() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.logout_alert_mr));
        alertDialogBuilder.setPositiveButton("नाही",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.setNegativeButton("होय",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logoutFromApplication();
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void logoutFromApplication() {

        new AppSession(this).logoutFromApplication();

        Intent intent = new Intent(this, SmaLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {
            if (jsonObject != null) {

                if(i == 1){
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    DebugLog.getInstance().d("kNotificationCount1111=" + jsonObject);
                    if (responseModel.getStatus()) {

                        //AppSettings.getInstance().setIntValue(this, AppConstants.kNotificationCount, Integer.parseInt(jsonObject.getString("data")));
                        notifiCountValue=Integer.parseInt(jsonObject.getString("data"));
                        if(notifiCountValue >= 1){
                            Log.d("notificationCount", String.valueOf(notifiCountValue));
                            tv_notification_count.setText(""+notifiCountValue);
                        }else{
                            tv_notification_count.setVisibility(View.GONE);
                        }
                    }
                }
                if(i == 2){
                    ResponseModel response = new ResponseModel(jsonObject);
                    if (response.getStatus()) {


                        ProfileModel profileModel = null;

                        try {
                            profileModel = new ProfileModel(jsonObject.getJSONObject("data"));

                            array2 = profileModel.getRolearray();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (array2.length()>1){
                            switchtxt.setVisibility(View.VISIBLE);
                        }else{
                            switchtxt.setVisibility(View.GONE);
                            ///Toast.makeText(this, "You Don't have any additional charges ", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}

