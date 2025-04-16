package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
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
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
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
    private lateinit var villageName: String
  //  lateinit var villageCode: String
    private lateinit  var pdfUrl :String
    lateinit var languageToLoad: String

    lateinit var textViewHeaderTitle: TextView
    private lateinit var imageBackArrow: ImageView

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
        textViewVillage = findViewById(R.id.textViewVillage)
        submitButton = findViewById(R.id.submitButton)

        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle)
        imageBackArrow=findViewById(R.id.imgBackArrow)

    }

    private fun setConfiguration() {

        imageBackArrow.visibility = View.VISIBLE
        textViewHeaderTitle.text = resources.getString(R.string.village_profile)

        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        try {
            getDistrictData()
            fetchTalukaMasterData()
            getVillageAgainstTaluka()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun onClick()
    {
//        imageMenushow.setOnClickListener(View.OnClickListener {
//
//        })
        imageBackArrow.setOnClickListener {
            finish()
        }
        textViewDistrict.setOnClickListener {
            showDistrict()
        }

        textViewTaluka.setOnClickListener {
            showTaluka()
        }
        textViewVillage.setOnClickListener {
            showVillage()
        }

        submitButton.setOnClickListener {
            getMyProfileVillageDetails()
        }
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
            api.postRequest(responseCall, this, 4)
        } catch (e: JSONException) {
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
            textViewDistrict.text = s
            if (districtID > 0) {
                fetchTalukaMasterData()
            }
            //  warehouseAvailabilityJSONArray = null
            talukaID = 0
            textViewTaluka.text = ""
            textViewTaluka.hint = resources.getString(R.string.farmer_select_taluka)
            textViewTaluka.setHintTextColor(Color.GRAY)

            villageID = 0
            textViewVillage.text = ""
            textViewVillage.hint = resources.getString(R.string.farmer_select_village)
            textViewVillage.setHintTextColor(Color.GRAY)
        }
        if (i == 2) {
            talukaID = s1!!.toInt()
            if (s != null) {
                talukaName = s
                getVillageAgainstTaluka()
            }
            textViewTaluka.text = s

            villageID = 0
            textViewVillage.text = ""
            textViewVillage.hint = resources.getString(R.string.farmer_select_village)
            textViewVillage.setHintTextColor(Color.GRAY)

        }

        if (i == 3) {
            villageID = s1!!.toInt()
            villageName = s.toString()
            textViewVillage.text = s
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
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                districtJSONArray = response.getdataArray()
                districtJSONArray?.let {
                    for (j in 0 until it.length()) {
                        val districtObject = it.getJSONObject(j)
                        val id = districtObject.getInt("id")
                        val name = districtObject.getString("name")

                        // Check if the current id matches districtID
                        if (id == districtID) {
                            // Set the text in textViewDistrict if a match is found
                            textViewDistrict.text = name
                            break // No need to continue looping once the matching district is found
                        }
                    }
                } ?: run {
                    Log.e("TAGGER", "districtJSONArray could not be cast to JSONArray")
                }

            } else {

                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 2 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                talukaJSONArray = response.getdataArray()
                talukaJSONArray?.let {
                    for (j in 0 until it.length()) {
                        val talukaObject = it.getJSONObject(j)
                        val id = talukaObject.getInt("id")
                        val name = talukaObject.getString("name")

                        // Check if the current id matches districtID
                        if (id == talukaID) {
                            // Set the text in textViewDistrict if a match is found
                            textViewTaluka.text = name
                            break // No need to continue looping once the matching district is found
                        }
                    }
                } ?: run {
                    Log.e("TAGGER", "districtJSONArray could not be cast to JSONArray")
                }
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 3 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                villJSONArray = response.getdataArray()
                Log.d("TAGGER", "onResponse: $villageID & $villJSONArray")
                villJSONArray?.let {
                    for (j in 0 until it.length()) {
                        val villageObject = it.getJSONObject(j)
                        val id = villageObject.getInt("code")
                        val name = villageObject.getString("name")

                        // Check if the current id matches districtID
                        if (id == villageID) {
                            // Set the text in textViewDistrict if a match is found
                            textViewVillage.text = name
                            break // No need to continue looping once the matching district is found
                        }
                    }
                } ?: run {
                    Log.e("TAGGER", "districtJSONArray could not be cast to JSONArray")
                }
            }
            else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 4 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
               pdfUrl = jSONObject.getString("data")
               // Log.d("talukaJSONArray",talukaJSONArray.toString())

//                var browserIntent = Intent(Intent.ACTION_VIEW, uri.parse(pdfUrl))
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                Log.d("mmmm","browserIntent="+pdfUrl)
                startActivity(browserIntent)

            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

    }

}