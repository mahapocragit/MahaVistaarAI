package in.gov.mahapocra.mahavistaarai.sma;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;

import in.gov.mahapocra.mahavistaarai.R;
import in.gov.mahapocra.mahavistaarai.data.model.ResponseModel;
import in.gov.mahapocra.mahavistaarai.util.app_util.AppString;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class KTReportActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private RecyclerView recyclerView;
    private LinearLayout linLayoutKT;
    private CardView cardView;
    private TextView presentDayTextView,tv_meetings,tv_trainings,tv_visits;
    private TextView absentDayTextView;
    private TextView hrTextView,jdatetxt;

    private AppSession session;
    private String year = "";
    private String monthName = "";
    private String year1 = "";
    private String monthName1 = "";
    private CalendarFilterModel calendarFilterModel;
    private JSONArray monthJsonArray;
    private String joining_date_app = null;
    AlertDialog.Builder alertDialogBuilder;
    int currentYear,priviousYear,rollId;
    private String username,ca_UserName;
    private Button btnFeedback;
    private  Dialog feedbackDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ktreport);
        initComponents();
        setConfiguration();
    }
    private void initComponents() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new AppSession(this);

        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        cardView = findViewById(R.id.cardView);
        linLayoutKT = findViewById(R.id.linLayoutKT);
        tv_meetings = findViewById(R.id.tv_meetings);
        tv_trainings = findViewById(R.id.tv_trainings);
        tv_visits = findViewById(R.id.tv_visits);
        btnFeedback = findViewById(R.id.btnFeedback);
        presentDayTextView = findViewById(R.id.presentDayTextView);
        absentDayTextView = findViewById(R.id.absentDayTextView);
//        hrTextView = findViewById(R.id.hrTextView);
        jdatetxt= findViewById(R.id.jdatetxt);
        username = AppSettings.getInstance().getValue(this, AppConstants.kUSERNAME, AppConstants.kUSERNAME);
        Log.d("MAYU","username="+username);
        rollId = session.getProfileModel().getRole_id();
        Log.d("MAYU","rollId="+rollId);

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFeedbackDialog();
            }
        });
    }

    private void setConfiguration() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        ca_UserName = getIntent().getStringExtra("ca_user");
//        Log.d("MAYU","ca_user1="+ca_UserName);


//        if (getIntent().getStringExtra("year") != null) {
//            year = getIntent().getStringExtra("year");
//        }
//
//        if (getIntent().getStringExtra("monthName") != null) {
//            monthName = getIntent().getStringExtra("monthName");
//            Log.d("tesr3434",monthName);
//        }
        monthJsonArray = AppHelper.getInstance().getMonthFilterArray();
        Log.d("monthJsonArray", String.valueOf(monthJsonArray));
        //get current month n year by amruta
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
        Date date = new Date();
        monthName=dateFormat.format(date);
        year = String.valueOf(calendar.get(Calendar.YEAR));
        String outputMonthEn=monthName+" "+year;
        Log.d("MAYU",""+outputMonthEn);
        String outputMonthMr=AppHelper.getInstance().convertToMarathiMonth(outputMonthEn);
        setTitle("विस्तार कार्य अहवाल "+outputMonthMr);
        Log.d("MAYU",""+outputMonthMr);
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("-- वर्ष निवडा --");
        fetchData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ProfileModel profileModel = session.getProfileModel();

//        if (profileModel != null && (profileModel.getRole_id() == AppRole.CA.id())) {
//            DebugLog.getInstance().d("Own user");
//        }  else {
        getMenuInflater().inflate(R.menu.ca_calendar_filter, menu);
//        }
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_call:
                dialPhoneCall();
                return true;

            case R.id.action_filter:
                showYearFilterDialog();
                return true;

            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showYearFilterDialog(){
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        priviousYear = currentYear-1;
        String mCurrentYear = String.valueOf(currentYear);
        String mPriviousYear = String.valueOf(priviousYear);
        Log.d("MAYU","CurrentYear="+mCurrentYear);
        Log.d("MAYU","PreviousYear="+mPriviousYear);

        alertDialogBuilder.setPositiveButton(mCurrentYear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                monthJsonArray = AppHelper.getInstance().getcurrentPriviousMonthFilterArray(mCurrentYear);
                Log.d("monthJsonArray", String.valueOf(monthJsonArray));
                showFilterDialog();
            }
        });

        alertDialogBuilder.setNegativeButton(mPriviousYear,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                monthJsonArray = AppHelper.getInstance().getcurrentPriviousMonthFilterArray(mPriviousYear);
                Log.d("monthJsonArray", String.valueOf(monthJsonArray));
                showFilterDialog();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showFilterDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_calendar_filter);

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final CalendarFilterAdapterKT filterAdapter = new CalendarFilterAdapterKT(this, this, monthJsonArray);
        recyclerView.setAdapter(filterAdapter);

        TextView applyButton = dialog.findViewById(R.id.applyButton);
        applyButton.setText("लागू करा");
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterAdapter.mSelectedIndex == -1) {
                    UIToastMessage.show(KTReportActivity.this, " Please select the month");
                } else {
                    dialog.dismiss();
                    monthName = calendarFilterModel.getName();
///chnge by amruta 29 dec
                    String str = String.valueOf(calendarFilterModel.getIs_selected());
                    Log.d("str12121212",str);
                    if (str.equalsIgnoreCase("1")){
                        monthName1 = calendarFilterModel.getName();
                        year1 = String.valueOf(calendarFilterModel.getYear());
                        String  momthyear=monthName1;
                        Log.d("str13131313",momthyear);
                        AppSettings.getInstance().setValue(KTReportActivity.this, AppConstants.PMOnth, momthyear);
                        String outputMonthMr=AppHelper.getInstance().convertToMarathiMonth(monthName1);
                        setTitle("विस्तार कार्य अहवाल "+outputMonthMr);

                        fetchFilteredData();
                    }else{
                        Calendar calendar = Calendar.getInstance();
                        DateFormat dateFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
                        Date date = new Date();
                        monthName=AppSettings.getInstance().getValue(KTReportActivity.this, AppConstants.PMOnth, "");

                        year = String.valueOf(calendar.get(Calendar.YEAR));
                        fetchData();
                        String outputMonthMr=AppHelper.getInstance().convertToMarathiMonth(monthName);
                        setTitle("विस्तार कार्य अहवाल "+outputMonthMr);
                    }

                }
            }
        });

        TextView cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setText("हटवा");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {
        JSONObject jsonObject = (JSONObject) o;
        DebugLog.getInstance().d("" + jsonObject.toString());

        if (i == 1) {
            CalendarModelKT calendarModel = new CalendarModelKT(jsonObject);

            if (calendarModel.getIs_holiday() == 1) {
                String strHDetails = calendarModel.getHoliday_details();
                Toast.makeText(this, "This is holiday", Toast.LENGTH_SHORT).show();
//                UIToastMessage.showOnTop(this, calendarModel.getHoliday_details());
            }
            if (calendarModel.getIs_present() == 1) {
                String details = getIntent().getStringExtra("mKTDetails");
                String strdate=calendarModel.getDate();
                Log.d("MAYU","strdateKT="+strdate);
//                Toast.makeText(this, "Comming soon....", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, KTReportDetailsActivity.class);
                intent.putExtra("mDetails", jsonObject.toString());
                Log.d("MAYU","abr="+jsonObject.toString());
                Log.d("MAYU","abr2="+details);
                intent.putExtra("mKTDetails", details);
                intent.putExtra("strdateKT", strdate);
                startActivity(intent);
            }
        }

        if (i == 2) {
            calendarFilterModel = new CalendarFilterModel(jsonObject);
            DebugLog.getInstance().d("CalendarFilterModel=" + calendarFilterModel.getName());
        }

    }


    private void fetchData() {
        try {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.ENGLISH);
            String month_name = month_date.format(calendar.getTime());

            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            Log.d("MAYU","APIParam Uname22="+username);

//            if(rollId == 22)
//            {
//                jsonObject.put("ca_username", username);
//                Log.d("MAYU","APIParam Uname22="+username);
//            }
//            if (rollId == 19){
//
//                jsonObject.put("ca_username", ca_UserName);
//                Log.d("MAYU","APIParam Uname19="+ca_UserName);
//            }
//            jsonObject.put("ca_username", username);

            //:TODO SDAO checking from approve attendance and month name will be always string full name

            if (monthName.length() > 0 && year.length() > 0) {
                jsonObject.put("month", monthName);
//                jsonObject.put("year", year);
                jsonObject.put("year", "2025");

            } else { // Current month report
                jsonObject.put("month", monthName1);
                jsonObject.put("year", year1);
            }

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.fetchCalendarMonthReportKT(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void submitKTFeedback(String strFeedback) {
        try {
            JSONObject jsonObject = new JSONObject();
            Log.d("MAYU222","submitKTFeedback==="+strFeedback);
            jsonObject.put("kt_username", username);
            jsonObject.put("kt_feedback", strFeedback);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.addKTFeedBackRequest(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void fetchFilteredData() {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("username", username);
            Log.d("MAYU","APIParam Uname22="+username);
//            if (rollId == 19){
//                jsonObject.put("ca_username", ca_UserName);
//                Log.d("MAYU","APIParam Uname19="+ca_UserName);
//            }
            jsonObject.put("month", monthName1);
            jsonObject.put("year", year1);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_API_CA, session.getToken(), new AppString(this).getkMSG_WAIT(), true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.fetchCalendarMonthReportKT(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {
            if (jsonObject != null) {

                Log.d("final121212",jsonObject.toString());
                ResponseModel response = new ResponseModel(jsonObject);
                String strTotalMeetings=response.getTotalMeetings();
                String strTotalTrainings=response.getTotalTrainings();
                String strTotalVisits=response.getTotalVisits();
                Log.d("MAYU111","MTV=="+strTotalMeetings+strTotalTrainings+strTotalVisits);

                if (i == 1) {
                    if (response.getStatus()) {
                        Log.d("final121212",jsonObject.toString());
//                        joining_date_app = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "joining_date_app");
//                        Log.d("fdsfsdfsdfsd", joining_date_app);

//                        cardView.setVisibility(View.VISIBLE);
                        linLayoutKT.setVisibility(View.VISIBLE);
                        tv_meetings.setText("• बैठका –  "+strTotalMeetings);
                        tv_trainings.setText("• प्रशिक्षण – "+strTotalTrainings);
                        tv_visits.setText("• भेट – "+strTotalVisits);
                        JSONArray jsonArray = response.getDataArray();

                        JSONArray jsonArray1 = new JSONArray();

                        boolean isFutureDate = false;
                        boolean ispostdate = false;

                        for (int k = 0; k < jsonArray.length(); k++) {
                            try {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(k);

                                CalendarModelKT calendarModel = new CalendarModelKT(jsonObject1);
                                Log.d("test53543353",calendarModel.getDate()+" "+AppHelper.getInstance().getCurrentDateYYYYMMDD());


                                if (calendarModel.getDate().equals(AppHelper.getInstance().getCurrentDateYYYYMMDD())) {
                                    isFutureDate = true;

                                } else {

                                    if (isFutureDate) {
                                        jsonObject1.put("is_present", 3);
                                    }

                                }
                                jsonArray1.put(jsonObject1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        DebugLog.getInstance().d("jsonArray1" + jsonArray1.toString());

                        KTCalendarReportAdapter adapter = new KTCalendarReportAdapter(this, this, jsonArray1);
                        recyclerView.setAdapter(adapter);

                        if (jsonArray.length() > 0) {
                            try {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                CalendarModelKT calendarModel = new CalendarModelKT(jsonObject1);

                                String date = AppHelper.getInstance().getAttendanceMonth(calendarModel.getDate());



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        calculateTotalPresentDay(jsonArray);

                    } else {
                        UIToastMessage.show(this, response.getResponse());
                    }
                }
                if (i == 2) {
                    if (response.getStatus()) {
                        Log.d("Amruta21232343",response.toString());
                        JSONArray jsonArray = response.getDataArray();
                        if (jsonArray.length() > 0) {
                            try {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                int monthId = jsonObject1.getInt("id");
                                monthName = jsonObject1.getString("month");
                                String outputMonthMr=AppHelper.getInstance().convertToMarathiMonth(monthName);
                                setTitle("विस्तार कार्य अहवाल "+outputMonthMr);
                                Log.d("newdate4444",monthName);
                                year = jsonObject1.getString("year");
                                fetchData( );


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if(i == 3)
                {
                    if (jsonObject != null) {
                        DebugLog.getInstance().d("onResponse=" + jsonObject);
                        ResponseModel response1 = new ResponseModel(jsonObject);
                        if (response1.getStatus()) {
//                            Toast.makeText(this, "" + response1.getResponse(), Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(KTReportActivity.this)
                                    .setTitle("धन्यवाद...!!!")
                                    .setMessage("तुमचा मौल्यवान अभिप्राय आम्हाला प्राप्त झाला आहे.")
                                    .setPositiveButton("ठीक आहे", (dialogInterface, which) -> {
                                        dialogInterface.dismiss();
                                        feedbackDialog.dismiss();// closes success alert
                                    })
                                    .show();

                        } else {
                            Toast.makeText(this, "" + response1.getResponse(), Toast.LENGTH_SHORT).show();
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


    private void calculateTotalPresentDay(JSONArray jsonArray) {
        Log.d("dfdfbjdbjdbgndfb",jsonArray.toString());
        List<CalendarModelKT> totalP = new ArrayList<>();
        List<CalendarModelKT> totalA = new ArrayList<>();

        long totalHrs = 0;
        long totalMins = 0;

        boolean isJoiningDate = false;
        //   String joinDate = AppHelper.getInstance().getJoiningDate(joining_date_app);
        Log.d("fffffffff","bvbncbnvbn");
        if (joining_date_app != null) {
            Log.d("fffffffff",joining_date_app);
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d("fffffffff","joinDate");
                try {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.d("teststs12121",jsonObject.toString());
                    CalendarModelKT calendarModel = new CalendarModelKT(jsonObject);
                    Log.d("teststs",calendarModel.getDate()+" "+joining_date_app);
                    if (calendarModel.getDate().equals(joining_date_app)) {
                        isJoiningDate = true;
                    }

                    if (isJoiningDate) {
                        if ((calendarModel.getIs_present() == 0) && (calendarModel.getIs_holiday() == 0)) {
                            totalA.add(calendarModel);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CalendarModelKT calendarModel = new CalendarModelKT(jsonObject);

                if (calendarModel.getIs_present() == 1 && calendarModel.getIs_holiday() == 0) {

                    totalP.add(calendarModel);

//                    long hrs = AppHelper.getInstance().getCalculatedTotalHours(calendarModel.getReport_time(), calendarModel.getOut_time());
//
//                    totalMins += AppHelper.getInstance().getCalculatedTotalMins(calendarModel.getReport_time(), calendarModel.getOut_time());
//
//                    totalHrs += AppHelper.getInstance().getCalculatedTotalHours(calendarModel.getReport_time(), calendarModel.getOut_time());


                } else {

                    if (!isJoiningDate) {
                        DebugLog.getInstance().d("joining_date");
                        if ((calendarModel.getIs_present() == 0) && (calendarModel.getIs_holiday() == 0)) {
                            totalA.add(calendarModel);
                        }
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        double minToHr = totalMins / 60;
        double hrs = totalHrs + minToHr;
        Log.d("date222",minToHr+" "+totalHrs+" "+hrs);
        presentDayTextView.setText(String.valueOf(totalP.size()));
        absentDayTextView.setText(String.valueOf(totalA.size()));
//        hrTextView.setText(String.format(Locale.getDefault(), "%.2f", hrs));

    }


    private void dialPhoneCall() {

        if (Build.VERSION.SDK_INT < 23) {
            //Do not need to check the permission
            DebugLog.getInstance().d("No need to check the permission");
            dialPhone();

        } else {

            if (checkPermissionsIsEnabledOrNot()) {
                //If you have already permitted the permission
                dialPhone();

            } else {
                requestMultiplePermission();
            }

        }

    }


    private void dialPhone() {

        ProfileModel profileModel = session.getProfileModel();

        if (profileModel != null && (profileModel.getRole_id() == AppRole.CA.id())) {
            if (profileModel.getMobile().length() > 0) {
                AppUtility.getInstance().makeAPhoneCall(this, profileModel.getMobile());
            }

        } else {
            String details = getIntent().getStringExtra("mCADetails");
            try {
                JSONObject jsonObject = new JSONObject(details);
                CAModel model = new CAModel(jsonObject);
                if (model.getMobile().length() > 0) {
                    AppUtility.getInstance().makeAPhoneCall(this, model.getMobile());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    private boolean checkPermissionsIsEnabledOrNot() {

        int permissionPhoneCall = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        return permissionPhoneCall == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMultiplePermission() {

        int permissionPhoneCall = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionPhoneCall != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

        ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        dialPhone();
                    } else {
                        dialPhone();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }
    private void openFeedbackDialog() {

        feedbackDialog = new Dialog(this);
        feedbackDialog.setContentView(R.layout.dialog_feedback_form);
        feedbackDialog.getWindow().setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        EditText etFeedback = feedbackDialog.findViewById(R.id.etFeedback);
        Button btnSubmit = feedbackDialog.findViewById(R.id.btnSubmit);
        ImageView btnClose = feedbackDialog.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(v -> feedbackDialog.dismiss());
        btnSubmit.setOnClickListener(v -> {

            String strFeedback = etFeedback.getText().toString().trim();
            Log.d("MAYU222","etFeedback==="+strFeedback);
            if (strFeedback.isEmpty()) {
//                Toast.makeText(this, "कृपया अभिप्राय लिहा", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(KTReportActivity.this) .setTitle("अभिप्राय आवश्यक") .setMessage("कृपया तुमचा अभिप्राय लिहा.") .setPositiveButton("ठीक आहे", null) .show();
            }
            else {
                submitKTFeedback(strFeedback);
            }

            //submitFeedback(feedback, dialog);
        });

        feedbackDialog.show();
    }

}