package in.gov.mahapocra.farmerapppks.notification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.activity.DashboardScreen;
import in.gov.mahapocra.farmerapppks.activity.LoginScreen;
import in.gov.mahapocra.farmerapppks.activity.SplashScreen;
import in.gov.mahapocra.farmerapppks.api.APIRequest;
import in.gov.mahapocra.farmerapppks.api.APIServices;
import in.gov.mahapocra.farmerapppks.app_util.AppConstants;
import in.gov.mahapocra.farmerapppks.models.response.ResponseModel;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class ReadNotificationActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {


    private TextView tittleTextView,textViewHeaderTitle;
    private TextView messageTextView,dateTextView,readTextView,urlInfoTextView;
    private ImageView checkImageView,imageMenushow,climateImage;
    String notificationstr,notificationPayloadData,data,notification_data,is_read,created_at,createdatetime;
    String title,body,urlLink,notificationId,user_action,loginType,userId;
    //private AppSession session;
     private int farmerId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_notification);
        initComponents();
        try {
            setConfiguration();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                //loginType = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kLOGIN_TYPE);
                farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0);
                if (farmerId > 0) {
                    startActivity(new Intent(this, DashboardScreen.class));
                    finish();
//                } else if (loginType.equalsIgnoreCase(ApConstants.kCA_LOGIN)) {
//                    Intent intent = new Intent(this, SdaoVillageActivity.class);
//                    intent.putExtra("caId", userId);
//                    intent.putExtra("viewType", "dashboard");
//                    startActivity(intent);
//                    finish();
//                } else if (loginType.equalsIgnoreCase(ApConstants.kSDAO_LOGIN)) {
//                    startActivity(new Intent(this, SdaoDashbordActivity.class));
//                    finish();
//                } else if (loginType.equalsIgnoreCase(ApConstants.kPMU_TYPE)) {
//                    startActivity(new Intent(this, PmuDashboardActivity.class));
//                    finish();
                } else {
                    startActivity(new Intent(this, LoginScreen.class));
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initComponents() {

        //session = new AppSession(this);
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle);
        imageMenushow=findViewById(R.id.imgBackArrow);
        climateImage=findViewById(R.id.climate_images);
        tittleTextView = findViewById(R.id.tittleTextView);
        messageTextView = findViewById(R.id.messageTextView);
        dateTextView = findViewById(R.id.dateTextView);
        urlInfoTextView = findViewById(R.id.urlInfoTextView);
    }

    private void setConfiguration() throws JSONException {
       // loginType = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kLOGIN_TYPE);//amruta
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageMenushow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.news_details);
        imageMenushow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadNotificationActivity.this, NotificationListActivity.class);
                startActivity(intent);
            }
        });


        if (farmerId > 0) {
            if (getIntent() != null) {
                String mData = getIntent().getStringExtra("noticationData");
                if (!(null == mData)) {
                    JSONObject jsonObject = new JSONObject(mData);
                    NotificationNewsModel model = new NotificationNewsModel(jsonObject);
                    title = model.getNewsHeading();
                    body = model.getNews();
                    urlLink = model.getHeaderImage();
//                    try {
//                        JSONObject jsonObject = new JSONObject(mData);
//                        NotificationListmodel model = new NotificationListmodel(jsonObject);
//                        Log.d("array", jsonObject.toString());
//                        notification_data = model.getNotification_data();
//                        //is_read =model.getIs_read();
//                        //created_at =model.getCreated_at();
//                        String unixcreatedTimeStamp = model.getCreated_at();
//                        if (!(unixcreatedTimeStamp == null)) {
//                            try {
//                                // convert seconds to milliseconds
//                                Date date = new java.util.Date(Long.parseLong(unixcreatedTimeStamp) * 1000L);
//                                // the format of your date
//                                SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
//                                // give a timezone reference for formatting (see comment at the bottom)
//                                //sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
//                                created_at = sdf.format(date);
//                                Log.d("createdatetime", created_at);
//                            } catch (NumberFormatException e) {
//                                System.out.println("not a number");
//                            }
//                        }
//                        notificationId = model.getId();
//
//                        try {
//                            JSONObject json = new JSONObject(notification_data);
//                            notificationstr = json.getString("notification");
//                            JSONObject notification = new JSONObject(notificationstr);
//                            title = notification.getString("title");
//                            body = notification.getString("body");
//                            notificationPayloadData = json.getString("data");
//                            JSONObject notificationData = new JSONObject(notificationPayloadData);
//                            urlLink = notificationData.getString("urlLink");
//                            Log.d("shshshsh", title + "" + " " + body + " " + urlLink);
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                } else {
                    Bundle bundle = getIntent().getExtras();// add these lines of code to get data from notification
                    if (bundle != null) {
                        title = bundle.getString("title");
                        body = bundle.getString("body");
                        urlLink = bundle.getString("urlLink");
                        String unixcreatedTimeStamp = bundle.getString("createdate");
                        if (!(unixcreatedTimeStamp == null)) {
                            try {
                                // convert seconds to milliseconds
                                Date date = new java.util.Date(Long.parseLong(unixcreatedTimeStamp) * 1000L);
                                // the format of your date
                                SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                                // give a timezone reference for formatting (see comment at the bottom)
                                //sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
                                created_at = sdf.format(date);
                                Log.d("createdatetime", created_at);
                            } catch (NumberFormatException e) {
                                System.out.println("not a number");
                            }
                        }
                       // notificationId = bundle.getString("notificationId");
                        Log.d("noticationData", title + " " + body + " " + urlLink + " " + notificationId + " " + created_at);
                    }
                }
            }
//            if (Utility.checkConnection(this)) {
//                readNotication();
//            } else {
//                UIToastMessage.show(ReadNotificationActivity.this, "No internet connection");
//            }

            if (urlLink == null || urlLink.equalsIgnoreCase("")) {
                urlInfoTextView.setVisibility(View.GONE);
                climateImage.setVisibility(View.GONE);
                tittleTextView.setText("" + title);
                messageTextView.setText("Message   : " + body);
                dateTextView.setText(created_at);
            } else {
                tittleTextView.setText("" + title);
                messageTextView.setText("Message : " + body);
                dateTextView.setText(created_at);
                urlInfoTextView.setTextColor(Color.parseColor("#000080"));
               // urlInfoTextView.setVisibility(View.VISIBLE);
               // urlInfoTextView.setText(urlLink);
                climateImage.setVisibility(View.VISIBLE);
               // loadImage(climateImage,urlLink);
                Glide.with(this)
                        .load(urlLink)
                        .into(climateImage);
                urlInfoTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (urlLink.contains("http")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink));
                            startActivity(browserIntent);
                        }
                    }
                });
            }
           // String loginData = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
           // JSONObject loginJson = new JSONObject(loginData);
            userId = "6289";
        }else{
            startActivity(new Intent(this, SplashScreen.class));
            finish();
        }
    }

    public static void loadImage(ImageView climateImage, String image_url) {
        try {
            Picasso.get().invalidate(image_url);
            Picasso.get()
                    .load(image_url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .placeholder(R.drawable.ic_thumbnail)
                    .resize(1000, 1000)
                    .centerCrop()
                    .error(R.drawable.ic_thumbnail)
                    .into(climateImage);
        } catch (Exception ex) {
            ex.toString();
        }
    }

    private void readNotication() {
        try {
            user_action = "Read";
            int note= Integer.parseInt(notificationId);
            List<Integer> list = new ArrayList<Integer>();
            list.add(note);

            JSONArray array = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                array.put(list.get(i));
            }
            JSONObject obj = new JSONObject();
            try {
                obj.put("result", array);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d("dsfgghsdfgsdf",obj.toString());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_action", user_action);
            jsonObject.put("notification_ids", obj);


            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.SDAO_BASE_URL, "", AppConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.readfirebaseNotification(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        Log.d("jsonObjectData", jsonObject.toString());
        if (jsonObject != null) {
            ResponseModel response = new ResponseModel(jsonObject);

            if (i == 1) {
                if (response.getStatus()) {
                    //  UIToastMessage.show(this, response.getResponse());
                } else {
                    UIToastMessage.show(this, response.getResponse());
                }
            }


        }

    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

    }

}