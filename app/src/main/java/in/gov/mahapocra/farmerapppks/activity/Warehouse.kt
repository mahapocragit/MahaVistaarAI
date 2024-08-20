package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.WarehouseAvailabilityAdapterreport
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class Warehouse : AppCompatActivity(), ApiCallbackCode,
    AlertListEventListener, OnMultiRecyclerItemClickListener {
    lateinit var textViewHeaderTitle: TextView
    lateinit var textViewDistrict: TextView
    //lateinit var textViewTaluka: TextView
    lateinit var textTotalWarehouse: TextView
    lateinit var textAvailableCapacity: TextView
    lateinit var wareHouseEmptyTextView: TextView
    lateinit var imageMenushow: ImageView
    lateinit var wareHousereport:RecyclerView

    private var warehouseAvailabilityJSONArray : JSONArray? = null
    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null

    lateinit var districtName: String
    private  var districtID: Int = 0
    lateinit var talukaName: String
    private var talukaID: Int = 0
    lateinit var totalWareHouse: String
    lateinit var totalAvailableWareHouse: String
    lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@Warehouse).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@Warehouse))
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_warehouse)
        init()
        onClick()
        imageMenushow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.wareHouse)
    }

    private fun init()
    {
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@Warehouse).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@Warehouse))
            languageToLoad = "en"
        }
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle)
        imageMenushow=findViewById(R.id.imageMenushow)
        textViewDistrict=findViewById(R.id.textViewDistrict)
      //  textViewTaluka=findViewById(R.id.textViewTaluka)
        wareHouseEmptyTextView=findViewById(R.id.wareHouseEmptyTextView)
        textTotalWarehouse=findViewById(R.id.textTotalWarehouse)
        textAvailableCapacity=findViewById(R.id.textAvailableCapacity)
        wareHousereport=findViewById(R.id.wareHousereport)

        wareHousereport.setHasFixedSize(false)
        wareHousereport.setNestedScrollingEnabled(true)

        //districtName  = AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
       // talukaName = AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)

//        if (!districtName.equals("USER_DIST") ) {
//            textViewDistrict.setText(districtName)
//           // textViewTaluka.setText(talukaName)
//            Log.d("districtNamewr:talukawr", districtName )
//            wareHouseDetails()
//        }else{
//            getDistrictData()
//        }

        getDistrictData()
    }
    private fun onClick()
    {
        imageMenushow.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        })

        textViewDistrict.setOnClickListener {
            showDistrict()
        }

//        textViewTaluka.setOnClickListener {
//            showTaluka()
//        }
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
                    .showListDialogIndex(talukaJSONArray, 2, getString(R.string.farmer_select_taluka), "Test", "Value", this, this)
        }
    }

    private fun fetchTalukaMasterData() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("SecurityKey", APIServices.SSO_KEY)
            jsonObject.put("DistrictID", districtID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                    AppinventorApi(this, APIServices.DBT, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getTalukaData(requestBody)
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

    private fun getDistrictData() {
        val jsonObject = JSONObject()
        try {
           // jsonObject.put("SecurityKey", APIServices.SSO_KEY)
            jsonObject.put("lang", languageToLoad)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                    AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
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


    private fun wareHouseDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("district_id", districtID)
            jsonObject.put("lang", languageToLoad)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getWareHouseDetails(requestBody)
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

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {

        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                districtJSONArray = response.getdataArray()
                Log.d("districtJSONArray11111",districtJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }

        if (i == 2 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                talukaJSONArray = response.dataArrays
                Log.d("talukaJSONArray",talukaJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
        if (i == 3 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                warehouseAvailabilityJSONArray = response.dataArrays
                Log.d("warehouseAvailability",warehouseAvailabilityJSONArray.toString())
                totalWareHouse = response.total_available_capacity()
                totalAvailableWareHouse = response.getTotalAvailableWareHouse()

                textTotalWarehouse.setText(resources.getString(R.string.total_warehouse)+" "+totalWareHouse)
                textAvailableCapacity.setText(resources.getString(R.string.total_available_capacity)+" "+totalAvailableWareHouse + " "+resources.getString(R.string.tonnes))

                    if (warehouseAvailabilityJSONArray !== null) {
                        if (warehouseAvailabilityJSONArray?.length()!! > 0) {
                            val adaptorWaterBudgetReport =
                                WarehouseAvailabilityAdapterreport(
                                    this,
                                    this,
                                    warehouseAvailabilityJSONArray
                                )
                            wareHousereport.setLayoutManager(
                                LinearLayoutManager(
                                    this,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            )
                            wareHousereport!!.adapter = adaptorWaterBudgetReport
                            adaptorWaterBudgetReport!!.notifyDataSetChanged()
                        }
                    }else{
                        wareHousereport!!.visibility = View.GONE
                        wareHouseEmptyTextView!!.visibility = View.VISIBLE
                    }

            } else {
                UIToastMessage.show(this, response.response)
            }
        }
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {

        if (i == 1) {

            districtID = s1!!.toInt()
            if (s != null) {
                districtName = s
                wareHouseDetails()
            }
            textViewDistrict.setText(s)
//            if (districtID > 0) {
//                fetchTalukaMasterData()
//            }
              warehouseAvailabilityJSONArray = null
            talukaID = 0

           // textViewTaluka.setText("")
           // textViewTaluka.setHint(resources.getString(R.string.farmer_select_taluka))
        }
        if (i == 2) {

            try {
                talukaID = s1!!.toInt()
                if (s != null) {
                    talukaName = s
                    wareHouseDetails()
                }
               // textViewTaluka.setText(s)
            } catch (ex: NumberFormatException) {
                System.err.println("Invalid string in argumment")
                Toast.makeText(this@Warehouse, "Data not Found...", Toast.LENGTH_SHORT).show();
                //request for well-formatted string
            }
        }
    }
    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }
}