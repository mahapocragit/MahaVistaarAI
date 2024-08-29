package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class MyVillageProfilePdf : AppCompatActivity(), ApiCallbackCode,
        AlertListEventListener, ApiJSONObjCallback {

    private lateinit var textViewDistrict: TextView
    private lateinit var textViewTaluka: TextView
    private lateinit var textViewVillage: TextView
    lateinit var submitButton: Button


    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null
    private var villJSONArray: JSONArray? = null

    lateinit var districtName: String
    private  var districtID: Int = 0
    lateinit var talukaName: String
    private var talukaID: Int = 0
    private var villageID: Int = 0
    lateinit var villageName: String
  //  lateinit var villageCode: String
    lateinit  var pdfUrl :String
    lateinit var languageToLoad: String

    lateinit var textViewHeaderTitle: TextView
    lateinit var imageBackArrow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@MyVillageProfilePdf).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@MyVillageProfilePdf))
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_my_village_profile_pdf)
        init()
        setConfiguration()
        onClick()
    }
    private fun init()
    {

        textViewDistrict=findViewById(R.id.textViewDistrict)
        textViewTaluka=findViewById(R.id.textViewTaluka)
        textViewVillage = findViewById(R.id.textViewVillage);
        submitButton = findViewById(R.id.submitButton)

        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle);
        imageBackArrow=findViewById(R.id.imgBackArrow);

    }

    private fun setConfiguration() {

        imageBackArrow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(resources.getString(R.string.village_profile))

//        districtName = AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
//        talukaName = AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
//        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
//        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
//
//        if (!districtName.equals("USER_DIST") && !talukaName.equals("USER_TALUKA")) {
//            textViewDistrict.setText(districtName)
//            textViewTaluka.setText(talukaName)
//            Log.d("districtNamewr:talukawr", districtName + " ::" + talukaName)
//            fetchTalukaMasterData()
//            getMyProfileVillageDetails()
//        }
        getDistrictData()
    }

    private fun onClick()
    {
//        imageMenushow.setOnClickListener(View.OnClickListener {
//
//        })
        imageBackArrow.setOnClickListener(View.OnClickListener {
            finish()
        })
        textViewDistrict.setOnClickListener {
            showDistrict()
        }

        textViewTaluka.setOnClickListener {
            showTaluka()
        }
        textViewVillage.setOnClickListener {
            showVillage()
        }

        submitButton.setOnClickListener(View.OnClickListener {
            getMyProfileVillageDetails()
        })
    }

    private fun getMyProfileVillageDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("census_code", villageID)
            //jsonObject.put("DistrictID", districtID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getMyVillageProfileDetails(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 4)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun showVillage() {
        if (villJSONArray == null) {
            if (talukaID > 0) {
                getVillageAgainstTaluka()
            } else {
                UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_taluka))
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(villJSONArray, 3, getString(R.string.farmer_select_village), "name",
                        "code",this, this)
        }
    }


    private fun showTaluka() {
        if (talukaJSONArray == null) {
            if (districtID > 0) {
                fetchTalukaMasterData()
            } else {
                UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_district))
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(talukaJSONArray, 2, getString(R.string.farmer_select_taluka), "name", "id", this, this)
        }
    }

//    private fun getDistrictData() {
//        val api = AppinventorIncAPI(this, APIServices.SSO, APIServices.SSO_KEY, "", true)
//        api.getRequestData(APIServices.kgetPocraDistrict, this, 1)
//    }

//    private fun fetchTalukaMasterData() {
//        var url:String=APIServices.kgetPocraTaluka+districtID
//        val api = AppinventorIncAPI(this, APIServices.SSO, APIServices.SSO_KEY, "", true)
//        api.getRequestData(url, this, 2)
//    }

//    private fun getVillageAgainstTaluka() {
//        var url:String=APIServices.kgetPocraVillage+talukaID
//        val api = AppinventorIncAPI(this, APIServices.SSO, APIServices.SSO_KEY, "", true)
//        api.getRequestData(url, this, 5)
//    }


    private fun getDistrictData() {
        val jsonObject = JSONObject()
        try {

            jsonObject.put("lang", languageToLoad)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getDistrictList(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }


    private fun fetchTalukaMasterData() {
        val jsonObject = JSONObject()
        try {

            jsonObject.put("lang", languageToLoad)
            jsonObject.put("district_id", districtID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getTalukaList(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 2)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun getVillageAgainstTaluka() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("taluka_id", talukaID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.kGetVillageList(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 3)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun showDistrict() {
        if (districtJSONArray == null) {
            getDistrictData()
        } else {
            AppUtility.getInstance().showListDialogIndex(
                districtJSONArray,
                1,
                getString(R.string.farmer_select_district),
                "name",
                "id",
                this,
                this
            )
        }
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {

        if (i == 1) {

            districtID = s1!!.toInt()
            if (s != null) {
                districtName = s
            }
            textViewDistrict.setText(s)
            if (districtID > 0) {
                fetchTalukaMasterData()
            }
            //  warehouseAvailabilityJSONArray = null
            talukaID = 0
            textViewTaluka.setText("")
            textViewTaluka.setHint(resources.getString(R.string.farmer_select_taluka))
            textViewTaluka.setHintTextColor(Color.GRAY)

            villageID = 0
            textViewVillage.setText("")
            textViewVillage.setHint(resources.getString(R.string.farmer_select_village))
            textViewVillage.setHintTextColor(Color.GRAY)
        }
        if (i == 2) {
            talukaID = s1!!.toInt()
            if (s != null) {
                talukaName = s
                getVillageAgainstTaluka()
            }
            textViewTaluka.setText(s)

            villageID = 0
            textViewVillage.setText("")
            textViewVillage.setHint(resources.getString(R.string.farmer_select_village))
            textViewVillage.setHintTextColor(Color.GRAY)

        }

        if (i == 3) {
            villageID = s1!!.toInt()
            villageName = s.toString()
            textViewVillage.setText(s)
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        Log.d("obj", obj.toString())
    }

    override fun onFailure(th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {

        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                districtJSONArray = response.getdataArray()
                Log.d("districtJSONArray11111", districtJSONArray.toString())
            } else {

                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 2 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                talukaJSONArray = response.getdataArray()
                Log.d("talukaJSONArray", talukaJSONArray.toString())
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 3 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                villJSONArray = response.getdataArray()
                Log.d("villJSONArray", villJSONArray.toString())
            }
            else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 4 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
               pdfUrl = jSONObject.getString("data")
               // Log.d("talukaJSONArray",talukaJSONArray.toString())

//                var browserIntent = Intent(Intent.ACTION_VIEW, uri.parse(pdfUrl))
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                Log.d("mmmm","browserIntent="+pdfUrl);
                startActivity(browserIntent)

            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

    }

}