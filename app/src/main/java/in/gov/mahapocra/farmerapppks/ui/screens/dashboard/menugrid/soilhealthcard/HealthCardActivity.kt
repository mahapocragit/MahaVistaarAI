package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.soilhealthcard

import `in`.co.appinventor.services_api.settings.AppSettings
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.ui.adapters.FarmerAdapter
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityHealthCardBinding
import org.json.JSONObject
import retrofit2.Call

class HealthCardActivity : AppCompatActivity(), ApiCallbackCode {

    var languageToLoad: String? = null
    private lateinit var binding: ActivityHealthCardBinding
    private lateinit var districtName: String
    private var districtID: Int = 0
    private lateinit var talukaName: String
    private var talukaID: Int = 0
    private lateinit var villageName: String
    private lateinit var farmerAdapter: FarmerAdapter
    private var villageID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHealthCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@HealthCardActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }

        //Loading URL in webView
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        districtName =
            AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
        talukaName =
            AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
        villageName = AppSettings.getInstance()
            .getValue(this, AppConstants.uVILLAGE, AppConstants.uVILLAGE)

        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)

        binding.textViewDist.text = districtName
        binding.textViewTaluka.text = talukaName
        binding.textViewVillage.text = villageName

        binding.relativeLayoutToolbar.textViewHeaderTitle.text = getString(R.string.soil_health_card)
        binding.relativeLayoutToolbar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutToolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
        api.postRequest(responseCall, this, 1)
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (jSONObject != null) {
            val farmerJsonArray = jSONObject.optJSONArray("data")
            if (farmerJsonArray!=null) {
                binding.farmerRecyclerView.visibility = View.VISIBLE
                binding.noDataFoundText.visibility = View.GONE
                binding.noDataFoundImageView.visibility = View.GONE
                farmerAdapter = FarmerAdapter(farmerJsonArray)
                binding.farmerRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.farmerRecyclerView.adapter = farmerAdapter
            }else{
                binding.farmerRecyclerView.visibility = View.GONE
                binding.noDataFoundText.visibility = View.VISIBLE
                binding.noDataFoundImageView.visibility = View.VISIBLE
            }
        }
    }
}