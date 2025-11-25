package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityHealthCardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.SoilHealthCardAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.RegistrationViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DraggableTouchListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call

class SoilHealthCardActivity : AppCompatActivity(), ApiCallbackCode, AlertListEventListener {

    private lateinit var languageToLoad: String
    private lateinit var binding: ActivityHealthCardBinding
    private val registrationViewModel: RegistrationViewModel by viewModels()
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
    private val farmerViewModel: FarmerViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@SoilHealthCardActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityHealthCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        setUpObservers()
        setUpListeners()

        districtName = getLocalizedValue(
            AppConstants.uDISTMR,
            AppConstants.uDIST,
            getString(R.string.farmer_select_district)
        )
        talukaName = getLocalizedValue(
            AppConstants.uTALUKAMR,
            AppConstants.uTALUKA,
            getString(R.string.farmer_select_taluka)
        )
        villageName = getLocalizedValue(
            AppConstants.uVILLAGEMR,
            AppConstants.uVILLAGE,
            getString(R.string.farmer_select_village)
        )

        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)

        binding.textViewDist.text =
            if (districtName == "USER_DIST") getString(R.string.farmer_select_district) else districtName
        binding.textViewTaluka.text =
            if (talukaName == "USER_TALUKA") getString(R.string.farmer_select_taluka) else talukaName
        binding.textViewVillage.text =
            if (villageName == "uVILLAGE") getString(R.string.farmer_select_village) else villageName

        binding.relativeLayoutToolbar.textViewHeaderTitle.text =
            getString(R.string.soil_health_card)
        binding.relativeLayoutToolbar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutToolbar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@SoilHealthCardActivity, DashboardScreen::class.java))
            }
        })

        AnimationHelper.shrinkLeftToCenter(binding.bubbleIconImageView)
        lifecycleScope.launch {
            delay(5000) // 5 seconds
            binding.bubbleIconImageView.animate()
                .alpha(0f)
                .setDuration(500) // animation duration in ms
                .withEndAction {
                    binding.bubbleIconImageView.visibility = View.GONE
                    binding.bubbleIconImageView.alpha = 1f // reset alpha in case you show it again
                }
                .start()
        }
        farmerViewModel.getDistrictData(this, languageToLoad)
    }

    private fun getLocalizedValue(mrKey: String, enKey: String, default: String): String {
        val key = if (languageToLoad == "mr") mrKey else enKey
        return AppSettings.getInstance().getValue(this, key, default)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpListeners() {
        binding.chatbotIcon.setOnTouchListener(DraggableTouchListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        })

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
    }

    private fun setUpObservers() {
        farmerViewModel.districtIdResponse.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
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
        }

        farmerViewModel.talukaList.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val response = ResponseModel(jSONObject)
                if (response.status) {
                    talukaJSONArray = response.getdataArray()
                    registrationViewModel.getVillageList(this, languageToLoad, talukaID)
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }

        registrationViewModel.getVillageListResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
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
    }

    private fun fetchData(surveyNumber: Int) {
        val api = AppInventorApi(
            this, AppEnvironment.GIS.baseUrl, "",
            AppString(this).getkMSG_WAIT(), true
        )
        val apiRequest = api.getRetrofitInstance().create(ApiService::class.java)
        val jsonObject = JSONObject().apply {
            put("vincode", villageID)
            put("survey_number", surveyNumber)
        }
        val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
        val responseCall: Call<JsonObject> = apiRequest.fetchFarmerListForSHC(requestBody)
        api.postRequest(responseCall, this, 3)
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        Log.e("SoilHealthCardActivity", "API $i call failed: ${th?.localizedMessage}", th)
        // Optionally show a message to the user
        UIToastMessage.show(this, "Failed to load data. Please check your connection.")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {

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
            farmerViewModel.getDistrictData(this, languageToLoad)
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
                registrationViewModel.getVillageList(this, languageToLoad, talukaID)
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
                registrationViewModel.getVillageList(this, languageToLoad, talukaID)
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