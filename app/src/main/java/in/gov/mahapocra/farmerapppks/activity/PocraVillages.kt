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
import `in`.gov.mahapocra.farmerapppks.ui.adapters.villageAgainsttalukaAdapter
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class PocraVillages : AppCompatActivity(), ApiCallbackCode, ApiJSONObjCallback,
    AlertListEventListener {

    private var textViewDistrict: TextView? = null
    private var textViewTaluka: TextView? = null
    lateinit var submitButton: Button
    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null
    private var villJSONArray: JSONArray? = null
    private var villageAdapter: RecyclerView? = null
    lateinit var districtName: String
    private var districtID: Int = 0
    lateinit var talukaName: String
    private var talukaID: Int = 0
    lateinit var languageToLoad: String
    lateinit var textViewHeaderTitle: TextView
    private lateinit var imageBackArrow: ImageView
    lateinit var imageMenushow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@PocraVillages).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@PocraVillages))
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_pocra_villages)
        init()
        setConfiguration()
        onClick()
    }

    private fun init() {
        textViewDistrict = findViewById(R.id.textViewDistrict)
        textViewTaluka = findViewById(R.id.textViewTaluka)
        submitButton = findViewById(R.id.submitButton)
        villageAdapter = findViewById(R.id.villageAdapter)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageBackArrow = findViewById(R.id.imgBackArrow)
        imageMenushow = findViewById(R.id.imageMenushow)
    }

    private fun setConfiguration() {

        imageBackArrow.visibility = View.VISIBLE
        textViewHeaderTitle.text = resources.getText(R.string.pocra_village)
        districtName =
            AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
        talukaName =
            AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)

        if (!districtName.equals("USER_DIST") && !talukaName.equals("USER_TALUKA")) {
            textViewDistrict?.text = districtName
            textViewTaluka?.text = talukaName
            getVillageAgainstTaluka()
        }
        getDistrictData()
    }

    private fun onClick() {
        imageMenushow.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }

        textViewDistrict?.setOnClickListener {
            showDistrict()
        }

        textViewTaluka?.setOnClickListener {
            if (districtID > 0) {
                showTaluka()
            } else {
                textViewTaluka?.hint = resources.getString(R.string.error_farmer_select_district)
                textViewTaluka?.setHintTextColor(Color.GRAY)
            }
        }

        submitButton.setOnClickListener {
            getVillageAgainstTaluka()
        }

        imageBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

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
            api.postRequest(responseCall, this, 1)
        } catch (e: JSONException) {
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
            api.postRequest(responseCall, this, 2)
        } catch (e: JSONException) {
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
            api.postRequest(responseCall, this, 3)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showTaluka() {
        if (talukaJSONArray == null) {
            if (districtID > 0) {
                fetchTalukaMasterData()
            } else {
                UIToastMessage.show(
                    this,
                    resources.getString(R.string.error_farmer_select_district)
                )
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(
                    talukaJSONArray, 2, getString(R.string.farmer_select_taluka), "name",
                    "id", this, this
                )
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
            textViewDistrict?.text = s
            if (districtID > 0) {
                fetchTalukaMasterData()
            }
            talukaID = 0
            textViewTaluka?.text = ""
            textViewTaluka?.hint = resources.getString(R.string.farmer_select_taluka)
            textViewTaluka?.setHintTextColor(Color.GRAY)
        }
        if (i == 2) {
            talukaID = s1!!.toInt()
            if (s != null) {
                talukaName = s
                getVillageAgainstTaluka()
            }
            textViewTaluka?.text = s
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        Toast.makeText(this, "onFailure:Data Not Found", Toast.LENGTH_LONG).show()
    }

    override fun onFailure(th: Throwable?, i: Int) {
        Toast.makeText(this, "onFailure:Data Not Found", Toast.LENGTH_LONG).show()
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {

        if (i == 1 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                districtJSONArray = response.getdataArray()
                Log.d("districtJSONArray11111", districtJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }

        if (i == 2 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                talukaJSONArray = response.getdataArray()
                Log.d("talukaJSONArray", talukaJSONArray.toString())
            }
        }

        if (i == 3 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                villJSONArray = response.getdataArray()
                Log.d("villJSONArray", villJSONArray.toString())

                val adaptorWaterBudgetReport =
                    villageAgainsttalukaAdapter(
                        this,
                        villJSONArray
                    )
                villageAdapter?.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                )
                villageAdapter!!.adapter = adaptorWaterBudgetReport
                adaptorWaterBudgetReport.notifyDataSetChanged()
            }
        }
    }
}