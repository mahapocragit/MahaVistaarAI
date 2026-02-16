package in.gov.mahapocra.mahavistaarai.sma.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.data.model.ResponseModel;
import in.gov.mahapocra.mahavistaarai.sma.domain.APIRequest;
import in.gov.mahapocra.mahavistaarai.sma.data.constants.APIServices;
import in.gov.mahapocra.mahavistaarai.sma.data.constants.AppConstants;
import in.gov.mahapocra.mahavistaarai.sma.data.helper.AppSession;
import in.gov.mahapocra.mahavistaarai.sma.data.models.KTReportDetailsModel;
import in.gov.mahapocra.mahavistaarai.util.app_util.AppString;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class KTDetailsHistory extends AppCompatActivity implements ApiCallbackCode {

    private AppSession session;
    private TextView tvCategoryType,tvCategoryName,tvSubcategoryName,tvFarmerGroupName,tvFarmerGroupOtherText,tvCategoryNameOther,tvDate,tvDiscussionName,tvTechnologyType,tvFarmerType,tvDescription,tvRegisterName,tvMobileNumber;
    private ImageView imgPhoto;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ktdetails_history);
        initComponant();
    }

    private void initComponant()
    {
        session = new AppSession(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        username = AppSettings.getInstance().getValue(this, AppConstants.kUSERNAME, AppConstants.kUSERNAME);
        String selectedId = getIntent().getStringExtra("id");
        String selectedCategoryId = getIntent().getStringExtra("category_id");
        String selectedDateKT = getIntent().getStringExtra("selectedDateKT");
        Log.d("MAYU","selectedId=="+selectedId);
        Log.d("MAYU","selectedCategoryId=="+selectedCategoryId);
        fetchKTReportsDetails(selectedId,selectedCategoryId,selectedDateKT);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        imgPhoto = findViewById(R.id.imgPhoto);
        tvCategoryType = findViewById(R.id.tvCategoryType);
        tvSubcategoryName = findViewById(R.id.tvSubcategoryName);
        tvFarmerGroupName = findViewById(R.id.tvFarmerGroupName);
        tvDate = findViewById(R.id.tvDate);
        tvFarmerGroupOtherText = findViewById(R.id.tvFarmerGroupOtherText);
        tvCategoryNameOther = findViewById(R.id.tvCategoryNameOther);
        tvDiscussionName = findViewById(R.id.tvDiscussionName);
        tvTechnologyType = findViewById(R.id.tvTechnologyType);
        tvFarmerType = findViewById(R.id.tvFarmerType);
        tvDescription = findViewById(R.id.tvDescription);
        tvRegisterName = findViewById(R.id.tvRegisterName);
        tvMobileNumber = findViewById(R.id.tvMobileNumber);
    }
    private void fetchKTReportsDetails(String selectedId, String selectedCategoryId,String selectedDateKT) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
//            jsonObject.put("date", selectedDateKT);
//            jsonObject.put("username", "KT529762");
            jsonObject.put("date", selectedDateKT);
            jsonObject.put("id", selectedId);
//            jsonObject.put("id", 2);
            jsonObject.put("category_id", selectedCategoryId);
//            jsonObject.put("category_id", 3);

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
    public void onFailure(Object obj, Throwable th, int i) {

    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {
            if (jsonObject != null && i == 1) {

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
                                    String farmer_group_other_text = item.optString("farmer_group_other_text");
                                    String category_other_text = item.optString("category_other_text");

                                    String imageUrl = item.optString("image");
                                    Log.d("MAYU","category_name="+category_name);
                                    Log.d("MAYU","subcategory_name="+subcategory_name);
                                    Log.d("MAYU","image="+imageUrl);
                                    // ✅ New fields for farmer details
                                    String registerName = "";
                                    String mobileNumber = "";
                                    if (imageUrl != null && !imageUrl.isEmpty()) {

                                        Picasso.get()
                                                .load(imageUrl)
                                                .fit()
                                                .into(imgPhoto);
                                    }
                                    imgPhoto.setOnClickListener(new View.OnClickListener() {   // To view image fullsize
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(KTDetailsHistory.this, ImageViewerActivity.class);
                                            intent.putExtra("mURL", imageUrl);
                                            startActivity(intent);
                                        }
                                    });
                                    tvCategoryType.setText("कामाचे स्वरूप -   "+categoryTypeMr);
                                    if(category_name.equalsIgnoreCase("इतर"))
                                    {
                                        tvCategoryNameOther.setVisibility(View.VISIBLE);
                                        tvCategoryName.setVisibility(View.GONE);
                                        tvCategoryNameOther.setText("कामाचा प्रकार -   "+category_other_text);
                                    }else {
                                        tvCategoryName.setText("कामाचा प्रकार -   " + category_name);
                                        tvCategoryNameOther.setVisibility(View.GONE);
                                    }
                                    tvDate.setText("दिनांक : "+strDate);
                                    Log.d("MMM","dddd=="+description);
                                    if(description.equalsIgnoreCase("")) {
                                        tvDescription.setVisibility(View.GONE);
                                    }else {
                                        tvDescription.setVisibility(View.VISIBLE);
                                        tvDescription.setText(" निरीक्षण -   " + description);
                                    }

                                    if(category_id.equalsIgnoreCase("4") || category_id.equalsIgnoreCase("7") || category_id.equalsIgnoreCase("9"))
                                    {
                                        if(farmer_group_name.equalsIgnoreCase("Others"))
                                        {
                                            tvFarmerGroupName.setVisibility(View.VISIBLE);
                                            tvFarmerGroupName.setText("शेतकरी गट - " + farmer_group_name);
                                            tvFarmerGroupOtherText.setVisibility(View.VISIBLE);
                                            tvFarmerGroupOtherText.setText("शेतकरी गटाचे नाव - "+farmer_group_other_text);
                                        }else {
                                            tvFarmerGroupName.setVisibility(View.VISIBLE);
//                                        tvDiscussionName.setVisibility(View.VISIBLE);
                                            tvFarmerGroupName.setText("शेतकरी गट - " + farmer_group_name);
//                                        tvDiscussionName.setText("चर्चेचा विषय - "+discussion_name);
                                        }
                                    }else if(category_id.equalsIgnoreCase("3"))
                                    {
                                        tvFarmerType.setVisibility(View.VISIBLE);
                                        tvTechnologyType.setVisibility(View.VISIBLE);
                                        tvSubcategoryName.setVisibility(View.VISIBLE);
                                        tvRegisterName.setVisibility(View.VISIBLE);
                                        tvMobileNumber.setVisibility(View.VISIBLE);
                                        if(farmer_type.equalsIgnoreCase("1")) {
                                            tvFarmerType.setText("शेतकरी प्रकार - "+"वैयक्तिक शेतकरी");
                                        }else
                                        {
                                            tvFarmerType.setText("शेतकरी प्रकार - "+"शेतकरी गट");
                                        }
                                        if(technology_type.equalsIgnoreCase("1")) {
                                            tvTechnologyType.setText("तंत्रज्ञान प्रकार - "+" तंत्रज्ञान प्रात्यक्षिक ");
                                        }else
                                        {
                                            tvTechnologyType.setText("तंत्रज्ञान प्रकार -"+" तंत्रज्ञान अवलंब ");
                                        }
                                        tvSubcategoryName.setText("प्रक्षेत्र काम - "+subcategory_name);
                                        // Check for farmer_details array
                                        JSONArray farmerDetailsArray = item.optJSONArray("farmer_details");
                                        if (farmerDetailsArray != null && farmerDetailsArray.length() > 0) {
                                            JSONObject farmerObj = farmerDetailsArray.getJSONObject(0); // usually single farmer
                                            registerName = farmerObj.optString("register_name", "");
                                            mobileNumber = farmerObj.optString("mobile_number", "");
                                            Log.d("MAYU","register_name="+registerName);
                                            Log.d("MAYU","mobile_number="+mobileNumber);

                                            tvRegisterName.setText("Farmer Name -  "+registerName);
                                            tvMobileNumber.setText("Mobile Number -  "+mobileNumber);
                                        }
                                    }

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
                                            imageUrl,
                                            categoryType,
                                            registerName,     // new
                                            mobileNumber,     //new
                                            categoryTypeMr,    // new
                                            strDate
                                    );
                                }
                            }
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