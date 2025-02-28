package in.gov.mahapocra.farmerapppks.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppInventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.activity.DashboardScreen;
import in.gov.mahapocra.farmerapppks.api.APIRequest;
import in.gov.mahapocra.farmerapppks.api.APIServices;
import in.gov.mahapocra.farmerapppks.app_util.AppConstants;
import in.gov.mahapocra.farmerapppks.data.ResponseModel;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class NotificationListActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {
    private RecyclerView recyclerView;
    // private AppSession session;
    private int ca_id;
    private JSONArray mDataArray;
    int appID;
    String userID;
    private TextView textViewHeaderTitle;
    private ImageView imageMenushow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        initComponents();
        try {
            setConfiguration();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {

        // session = new AppSession(this);
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle);
        imageMenushow=findViewById(R.id.imageMenushow);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setConfiguration() throws JSONException {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageMenushow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.news);
        imageMenushow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationListActivity.this, DashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        if (Utility.checkConnection(this)) {
            getnoticationList();
        } else {
            UIToastMessage.show(NotificationListActivity.this, "No internet connection");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utility.checkConnection(this)) {
            try {
                getnoticationList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            UIToastMessage.show(NotificationListActivity.this, "No internet connection");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.ca_vill_board, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

//            case R.id.action_villa_sync:
//
//                if (Utility.checkConnection(this)) {
//                    syncDownClusterVillages();
//                } else {
//                    UIAlertView.getOurInstance().show(this, new AppString(this).getkNETWORK());
//                }

            // return true;

            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        JSONObject jsonObject = (JSONObject)o;

        Intent intent = new Intent(this, ReadNotificationActivity.class);
        intent.putExtra("noticationData", jsonObject.toString());
        startActivity(intent);
    }

    synchronized void getnoticationList()  {
       // String loginData = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
        int loginData =  AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0);
        try {
           //JSONObject loginJson = new JSONObject(loginData);
           // userID = loginJson.getString("id");

           // userID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
            JSONObject jsonObject = new JSONObject();
            //        return AppSettings.getInstance().getIntValue(mContext, AppConstants.kUSER_ID, 0);

            // appID =  session.getAppId();
//            jsonObject.put("user_id", "6289");
//            jsonObject.put("app_id", "7");
            jsonObject.put("SecurityKey", APIServices.SSO_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppInventorApi api = new AppInventorApi(this, APIServices.DBT, "", AppConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getNewsList(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        if (jsonObject != null) {
            ResponseModel responseModel = new ResponseModel(jsonObject);

            if (i == 1) {

                if (responseModel.getStatus()) {
                    JSONArray jsonArray = responseModel.getNewsArray();
                    if (jsonArray.length() > 0) {
                        NotificationMsgListAdapter adapter = new NotificationMsgListAdapter(this, this, jsonArray);
                        recyclerView.setAdapter(adapter);
                    }

                } else {
                    UIToastMessage.show(this, responseModel.getMsg());
                }
            }

        }
    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }

}