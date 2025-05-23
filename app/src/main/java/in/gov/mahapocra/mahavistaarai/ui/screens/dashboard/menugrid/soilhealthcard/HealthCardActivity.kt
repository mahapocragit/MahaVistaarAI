package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.content.Context
import android.graphics.Color
import `in`.co.appinventor.services_api.settings.AppSettings
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityHealthCardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class HealthCardActivity : AppCompatActivity(), ApiCallbackCode, AlertListEventListener {

    private lateinit var languageToLoad: String
    private lateinit var binding: ActivityHealthCardBinding
    private lateinit var districtName: String
    private var districtID: Int = 0
    private lateinit var talukaName: String
    private var talukaID: Int = 0
    private lateinit var villageName: String
    private lateinit var farmerAdapter: SoilHealthCardAdapter
    private var villageID: Int = 0
    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null
    private var villageJSONArray: JSONArray? = null
    private lateinit var farmerViewModel: FarmerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@HealthCardActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityHealthCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]


        //Loading URL in webView
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        districtName =
            AppSettings.getInstance().getValue(this, AppConstants.uDIST, getString(R.string.farmer_select_district))
        talukaName =
            AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, getString(R.string.farmer_select_taluka))
        villageName = AppSettings.getInstance()
            .getValue(this, AppConstants.uVILLAGE, getString(R.string.farmer_select_village))

        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)

        binding.textViewDist.text = if (districtName == "USER_DIST") getString(R.string.farmer_select_district) else districtName
        binding.textViewTaluka.text = if (talukaName == "USER_TALUKA") getString(R.string.farmer_select_taluka) else talukaName
        binding.textViewVillage.text = if (villageName == "uVILLAGE") getString(R.string.farmer_select_village) else villageName

        binding.relativeLayoutToolbar.textViewHeaderTitle.text =
            getString(R.string.soil_health_card)
        binding.relativeLayoutToolbar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutToolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        getDistrictData()


        binding.textViewDist.setOnClickListener {
            showDistrict()
        }

        binding.textViewTaluka.setOnClickListener {
            showTaluka()
        }

        binding.textViewVillage.setOnClickListener {
            showVillage()
        }

        binding.submitButton.setOnClickListener {
            val surveyNo = binding.edtSurveyNo.text.toString()
            if (villageID != null) {
                if (surveyNo.isNotEmpty()) {
                    fetchData(surveyNo.toInt())
                } else {
                    Toast.makeText(this, "Please select survey number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select village", Toast.LENGTH_SHORT).show()
            }
        }

        observeResponse()
    }

    private fun fetchData(surveyNumber: Int) {
        val api = AppInventorApi(
            this, APIServices.GIS, "",
            AppString(this).getkMSG_WAIT(), true
        )
        val apiRequest = api.getRetrofitInstance().create(APIRequest::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("vincode", villageID)
        jsonObject.put("survey_number", surveyNumber)
        val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
        val responseCall: Call<JsonObject> = apiRequest.fetchFarmerListForSHC(requestBody)
        api.postRequest(responseCall, this, 3)
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        Log.e("HealthCardActivity", "API call failed: ${th?.localizedMessage}", th)
        // Optionally show a message to the user
        UIToastMessage.show(this, "Failed to load data. Please check your connection.")
    }

    private fun observeResponse() {
        farmerViewModel.talukaList.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val response = ResponseModel(jSONObject)
                if (response.status) {
                    talukaJSONArray = response.getdataArray()
                    getVillageAgainstTaluka()
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )
            if (response.status) {
                districtJSONArray = response.getdataArray()
                farmerViewModel.fetchTalukaMasterData(this, languageToLoad)
            } else {
                UIToastMessage.show(this, response.response)
            }
        }

        if (i == 3 && jSONObject != null) {
            val farmerJsonArray = jSONObject.optJSONArray("data")
            if (farmerJsonArray != null) {
                binding.farmerRecyclerView.visibility = View.VISIBLE
                binding.noDataFoundText.visibility = View.GONE
                binding.noDataFoundImageView.visibility = View.GONE
                farmerAdapter = SoilHealthCardAdapter(farmerJsonArray)
                binding.farmerRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.farmerRecyclerView.adapter = farmerAdapter
            } else {
                binding.farmerRecyclerView.visibility = View.GONE
                binding.noDataFoundText.visibility = View.VISIBLE
                binding.noDataFoundImageView.visibility = View.VISIBLE
            }
        }

        if (i == 5 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )
            if (response.status) {
                villageJSONArray = response.getdataArray()
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
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
                "code",
                this,
                this
            )
        }
    }

    private fun showTaluka() {
        if (talukaJSONArray == null) {
            if (districtID > 0) {
                farmerViewModel.fetchTalukaMasterData(this, languageToLoad)
            } else {
                UIToastMessage.show(
                    this,
                    resources.getString(R.string.error_farmer_select_district)
                )
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(
                    talukaJSONArray,
                    2,
                    getString(R.string.farmer_select_taluka),
                    "name",
                    "code",
                    this,
                    this
                )
        }
    }

    private fun showVillage() {
        if (villageJSONArray == null) {
            if (talukaID > 0) {
                getVillageAgainstTaluka()
            } else {
                UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_taluka))
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(
                    villageJSONArray,
                    3,
                    getString(R.string.farmer_select_village),
                    "name",
                    "code",
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
                AppInventorApi(
                    this,
                    APIServices.FARMER,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            CoroutineScope(Dispatchers.IO).launch {
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getDistrictList(requestBody)
                api.postRequest(responseCall, this@HealthCardActivity, 1)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getVillageAgainstTaluka() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("taluka_code", talukaID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(
                this,
                APIServices.FARMER,
                "",
                AppString(this).getkMSG_WAIT(),
                true
            )

            CoroutineScope(Dispatchers.IO).launch {
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.kGetVillageList(requestBody)
                api.postRequest(responseCall, this@HealthCardActivity, 5)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {

        if (i == 1) {
            if (s1 != null) {
                districtID = s1.toInt()
            }

            if (s != null) {
                districtName = s
            }
            binding.textViewDist.text = s
            if (districtID > 0) {
                AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, districtID)
                farmerViewModel.fetchTalukaMasterData(this, languageToLoad)
            }
            talukaID = 0
            binding.textViewTaluka.text = ""
            binding.textViewTaluka.hint = resources.getString(R.string.farmer_select_taluka)
            binding.textViewTaluka.setHintTextColor(Color.GRAY)

            villageID = 0
            binding.textViewVillage.text = ""
            binding.textViewVillage.hint = resources.getString(R.string.farmer_select_village)
            binding.textViewVillage.setHintTextColor(Color.GRAY)
        }


        if (i == 2) {
            if (s1 != "") {
                talukaID = s1!!.toInt()
            }
            if (s != null) {
                talukaName = s
            }
            binding.textViewTaluka.text = s
            villageJSONArray = null
            if (talukaID > 0) {
                getVillageAgainstTaluka()
            }
            villageID = 0
            binding.textViewVillage.text = ""
            binding.textViewVillage.hint = resources.getString(R.string.farmer_select_village)
            binding.textViewVillage.setHintTextColor(Color.GRAY)
        }

        if (i == 3) {
            if (s1 != "") {
                villageID = s1!!.toInt()
            }

            villageName = s.toString()
            binding.textViewVillage.text = s
        }

    }
}