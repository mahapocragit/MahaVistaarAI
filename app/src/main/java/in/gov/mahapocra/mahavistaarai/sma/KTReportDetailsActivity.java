package in.gov.mahapocra.mahavistaarai.sma;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.data.model.ResponseModel;
import in.gov.mahapocra.mahavistaarai.util.app_util.AppString;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class KTReportDetailsActivity extends AppCompatActivity implements ApiCallbackCode {

    private AppSession session;
    private RecyclerView rvFarmFieldSchool;
    private RecyclerView rvTraining;
    private RecyclerView rvMeeting;
    private RecyclerView rvVisit;
    private String username;
    private boolean hoverVisible = true;
    private ArrayList<KTReportDetailsModel> KTReportDetailsList;
    private ArrayList<KTReportDetailsModel> KTReportDetailsList1;
    private ArrayList<KTReportDetailsModel> KTReportDetailsList2;
    private ArrayList<KTReportDetailsModel> KTReportDetailsList3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ktreport_details);
        initComponents();
        fetchKTReportsDetails();
    }
    private void initComponents() {

        session = new AppSession(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ImageView downloadIcon = findViewById(R.id.downloadIcon);
        TextView hoverMessage = findViewById(R.id.hoverMessage);
        downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the Toast message when the download icon is clicked
                Toast.makeText(KTReportDetailsActivity.this, "Comming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        //ImageView downloadIcon = findViewById(R.id.downloadIcon);
        LinearLayout hoverLayout = findViewById(R.id.hoverLayout);

        // Show hover on activity start
        hoverLayout.setVisibility(View.VISIBLE);

        // Blink animation between two colors
        int colorFrom = Color.parseColor("#AA000000"); // dark
        int colorTo = Color.parseColor("#FFFFC107");   // amber
        ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnim.setDuration(600);
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.addUpdateListener(animation ->
                hoverLayout.setBackgroundColor((int) animation.getAnimatedValue()));
        colorAnim.start();

        View.OnClickListener hideHoverAndToast = v -> {
            if (hoverVisible) {
                hoverLayout.setVisibility(View.GONE);
                if (colorAnim != null && colorAnim.isRunning()) {
                    colorAnim.cancel();
                }
                hoverVisible = false;
            }
            Toast.makeText(KTReportDetailsActivity.this, "Comming Soon", Toast.LENGTH_SHORT).show();
        };
        // Hide hover message and show toast on icon click
        downloadIcon.setOnClickListener(hideHoverAndToast);
        // Hide hover message and show toast on hover click
        hoverLayout.setOnClickListener(hideHoverAndToast);

        rvFarmFieldSchool = findViewById(R.id.rvFarmFieldSchool);
        rvFarmFieldSchool.setHasFixedSize(false);
        rvFarmFieldSchool.setNestedScrollingEnabled(true);
        KTReportDetailsList = new ArrayList<>();

        rvTraining = findViewById(R.id.rvTraining);
        rvTraining.setHasFixedSize(false);
        rvTraining.setNestedScrollingEnabled(true);
        KTReportDetailsList1 = new ArrayList<>();

        rvMeeting = findViewById(R.id.rvMeeting);
        rvMeeting.setHasFixedSize(false);
        rvMeeting.setNestedScrollingEnabled(true);
        KTReportDetailsList2 = new ArrayList<>();

        rvVisit = findViewById(R.id.rvVisit);
        rvVisit.setHasFixedSize(false);
        rvVisit.setNestedScrollingEnabled(true);
        KTReportDetailsList3 = new ArrayList<>();
        username = AppSettings.getInstance().getValue(this, AppConstants.kUSERNAME, AppConstants.kUSERNAME);

    }
private void setTrainingDetails(ArrayList<KTReportDetailsModel> list, RecyclerView rvTraining2 ,String flag) {
    KTReportDetailsAdapter mainAdapter = new KTReportDetailsAdapter(
            KTReportDetailsActivity.this,
            list,
            flag,
            model -> {
                // When an item is clicked
                String selectedDateKT = getIntent().getStringExtra("strdateKT");
                Log.d("MAYU","setTrainingDetails selecteddateKT=="+selectedDateKT);
                Intent intent = new Intent(KTReportDetailsActivity.this, KTDetailsHistory.class);
                intent.putExtra("id", model.getId());
                intent.putExtra("category_id", model.getCategoryId());
                intent.putExtra("selectedDateKT", selectedDateKT);
                Log.d("MAYU_T",""+model.getId());
                Log.d("MAYU_T",""+model.getCategoryId());
                startActivity(intent);
            }
    );
    rvTraining2.setLayoutManager(new LinearLayoutManager(KTReportDetailsActivity.this));
    rvTraining2.setAdapter(mainAdapter);
    rvTraining2.setHasFixedSize(true);
}
private void setMeetingDetails(ArrayList<KTReportDetailsModel> list, RecyclerView rvMeeting2, String flag) {
    KTReportDetailsAdapter mainAdapter = new KTReportDetailsAdapter(
            KTReportDetailsActivity.this,
            list,
            flag,
            model -> {
                // When an item is clicked
                String selectedDateKT = getIntent().getStringExtra("strdateKT");
                Log.d("MAYU","setMeetingDetails selecteddateKT=="+selectedDateKT);
                Intent intent = new Intent(KTReportDetailsActivity.this, KTDetailsHistory.class);
                intent.putExtra("id", model.getId());
                intent.putExtra("category_id", model.getCategoryId());
                intent.putExtra("selectedDateKT", selectedDateKT);
                Log.d("MAYU_M",""+model.getId());
                Log.d("MAYU_M",""+model.getCategoryId());
                startActivity(intent);
            }
    );

    rvMeeting2.setLayoutManager(new LinearLayoutManager(KTReportDetailsActivity.this));
    rvMeeting2.setAdapter(mainAdapter);
    rvMeeting2.setHasFixedSize(true);
}
    private void setVisitDetails(ArrayList<KTReportDetailsModel> list, RecyclerView rvVisit2 ,String flag) {
        KTReportDetailsAdapter mainAdapter = new KTReportDetailsAdapter(
                KTReportDetailsActivity.this,
                list,
                flag,
                model -> {
                    // When an item is clicked
                    String selectedDateKT = getIntent().getStringExtra("strdateKT");
                    Log.d("MAYU","setVisitDetails selecteddateKT=="+selectedDateKT);
                    Intent intent = new Intent(KTReportDetailsActivity.this, KTDetailsHistory.class);
                    intent.putExtra("id", model.getId());
                    intent.putExtra("category_id", model.getCategoryId());
                    intent.putExtra("selectedDateKT", selectedDateKT);
                    Log.d("MAYU_T",""+model.getId());
                    Log.d("MAYU_T",""+model.getCategoryId());
                    startActivity(intent);
                }
        );

        rvVisit2.setLayoutManager(new LinearLayoutManager(KTReportDetailsActivity.this));
        rvVisit2.setAdapter(mainAdapter);
        rvVisit2.setHasFixedSize(true);
    }
    private void fetchKTReportsDetails() {
        try {
            String selectedDateKT = getIntent().getStringExtra("strdateKT");
            Log.d("MAYU","selecteddateKT=="+selectedDateKT);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("date", selectedDateKT);
//            jsonObject.put("username", "KT529762");
//            jsonObject.put("date", "2025-10-29");

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.fetchWorkDetailsKT(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Finish the current activity
    }

    @Override
    public void onFailure(Object obj, Throwable th, int i) {

    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {
            if (jsonObject != null && i == 1) {
                KTReportDetailsList.clear();
                KTReportDetailsList1.clear();
                KTReportDetailsList2.clear();
                KTReportDetailsList3.clear();

                ResponseModel responseModel = new ResponseModel(jsonObject);
                if (responseModel.getStatus()) {

                    JSONArray dataArray = responseModel.getDataArray();
                    if (dataArray.length() > 0) {
                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject categoryObj = dataArray.getJSONObject(j);
                            String categoryType = categoryObj.optString("category_type");
                            String categoryTypeMr = categoryObj.optString("category_type_mr");
                            String strDate = categoryObj.optString("date");
                            JSONArray itemsArray = categoryObj.optJSONArray("items");

                            if (itemsArray != null) {
                                for (int k = 0; k < itemsArray.length(); k++) {
                                    JSONObject item = itemsArray.getJSONObject(k);

                                    String id = item.optString("id");
                                    String category_id = item.optString("category_id");
                                    String category_name = item.optString("category_name");
                                    String subcategory_name = item.optString("subcategory_name");
                                    String farmer_type = item.optString("farmer_type");
                                    String farmer_id = item.optString("farmer_id");
                                    String technology_type = item.optString("technology_type");
                                    String farmer_group_name = item.optString("farmer_group_name");
                                    String discussion_name = item.optString("discussion_name");
                                    String description = item.optString("description");
                                    String image = item.optString("image");

                                    // Use your KTReportDetailsModel constructor accordingly
                                    KTReportDetailsModel model = new KTReportDetailsModel(
                                            id,
                                            category_id,
                                            category_name,
                                            subcategory_name,
                                            farmer_type,
                                            farmer_id,
                                            technology_type,
                                            farmer_group_name,
                                            discussion_name,
                                            description,
                                            image,
                                            categoryType,
                                            categoryTypeMr,
                                            strDate
                                    );

                                    // Sort by category type
//                                    If you also want to show "FFS" data:
                                    if (categoryType.equalsIgnoreCase("Farm Field School")) {
                                        KTReportDetailsList.add(model);
                                    } else if (categoryType.equalsIgnoreCase("Meeting")) {
                                        KTReportDetailsList1.add(model);
                                    } else if (categoryType.equalsIgnoreCase("Training")) {
                                        KTReportDetailsList2.add(model);
                                    }
//                                     If you also want to show "Visit" data:
                                     else if (categoryType.equalsIgnoreCase("Visit"))
                                     {
                                         KTReportDetailsList3.add(model);
                                     }
                                }
                            }
                        }

                        String flag = "KT";
                        // Show only if data available
                        if (KTReportDetailsList.size() > 0) {
                            rvFarmFieldSchool.setVisibility(View.VISIBLE);
                            setMeetingDetails(KTReportDetailsList, rvFarmFieldSchool, flag);
                        } else {
                            rvFarmFieldSchool.setVisibility(View.GONE);
                        }
                        if (KTReportDetailsList1.size() > 0) {
                            rvMeeting.setVisibility(View.VISIBLE);
                            setMeetingDetails(KTReportDetailsList1, rvMeeting, flag);
                        } else {
                            rvMeeting.setVisibility(View.GONE);
                        }

                        if (KTReportDetailsList2.size() > 0) {
                            rvTraining.setVisibility(View.VISIBLE);
                            setTrainingDetails(KTReportDetailsList2, rvTraining, flag);
                        } else {
                            rvTraining.setVisibility(View.GONE);
                        }

                        if (KTReportDetailsList3.size() > 0) {
                            rvVisit.setVisibility(View.VISIBLE);
                            setVisitDetails(KTReportDetailsList3, rvVisit, flag);
                        } else {
                            rvVisit.setVisibility(View.GONE);
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("KTReport", "Error parsing response: " + e.getMessage());
        }
    }
}