package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.model.DiseaseStages
import `in`.gov.mahapocra.mahavistaarai.data.model.DiseasesDetails
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityPestsAndDiseasesLibraryBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ParticularStagesDiseasesAdpater
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.AddCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class PestsAndDiseasesStages : AppCompatActivity(), ApiCallbackCode {

    private lateinit var binding: ActivityPestsAndDiseasesLibraryBinding
    private var diseasesDetails: ArrayList<DiseasesDetails>? = null
    private lateinit var diseaseStages: ArrayList<DiseaseStages>

    var cropId: Int? = 0
    private var stagesId: Int = 0
    private var mUrl: String? = null
    lateinit var languageToLoad: String
    private var cropName: String? = null
    private var wotrCropId: String? = null
    private lateinit var stageJsonArray: JSONArray
    private var particularStagesDiseases: String = "AllStagesDiseases"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@PestsAndDiseasesStages).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityPestsAndDiseasesLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.pests_n_diseases)
        binding.sowingInfoLayout.textView7.text = getString(R.string.selected_crop)
        binding.tvAgroMetAdvisory.text = getString(R.string.different_stages_text)

        binding.sowingInfoLayout.sowingDateTextView.visibility = View.GONE
        binding.sowingInfoLayout.editSowingDateIcon.visibility = View.GONE

        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            startActivity(Intent(this@PestsAndDiseasesStages, DashboardScreen::class.java))
        }

        cropId = intent.getIntExtra("cropId", 0)
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")
        cropName = intent.getStringExtra("mName")
        particularStagesDiseases = intent.getStringExtra("ParticularStagesDiseases").toString()
        stagesId = intent.getIntExtra("id", 0)
        AppSettings.getInstance()
            .setValue(this, AppConstants.tmpCROPNAME, cropName)

        binding.sowingInfoLayout.cropInfoCardView.setOnClickListener {
            val sharing = Intent(this, AddCropActivity::class.java)
            sharing.putExtra("id", cropId)
            sharing.putExtra("mName", cropName)
            sharing.putExtra("wotr_crop_id", wotrCropId)
            sharing.putExtra("mUrl", mUrl)
            AppPreferenceManager(this).saveString(
                AppConstants.ACTION_FROM_DASHBOARD,
                AppConstants.PEST_AND_DISEASES_STAGES
            )
            startActivity(sharing)
        }

        if (cropName.isNullOrBlank()) {
            cropName = AppSettings.getInstance()
                .getValue(this, AppConstants.tmpCROPNAME, AppConstants.tmpCROPNAME)
        }

        if (cropId!! > 0) {
            getCropStages()
        } else {
            Toast.makeText(this, "No Crops Added", Toast.LENGTH_SHORT).show()
        }
        binding.sowingInfoLayout.cropNameTextView.text = "$cropName"
    }

    private fun showParticularStagesByList() {
        val particularDiseasesAdapter =
            ParticularStagesDiseasesAdpater(
                this,
                diseasesDetails!!
            )
        binding.diseasesByStage.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        binding.diseasesByStage.setAdapter(particularDiseasesAdapter)
        particularDiseasesAdapter.notifyDataSetChanged()
    }

    private fun getCropStages() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("crop_id", cropId)
            jsonObject.put("lang", languageToLoad)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(
                this,
                APIServices.FARMER,
                "",
                AppString(this).getkMSG_WAIT(),
                true
            )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getCropStages(requestBody)
            api.postRequest(responseCall, this, 1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onResponse(jSONObject: JSONObject?, n: Int) {
        if (n == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.getStatus()) {
                stageJsonArray = response.getdataArray()
                diseaseStages = ArrayList()
                try {
                    if (stageJsonArray != null && stageJsonArray.length() != 0) {
                        val stagesJsonObject = stageJsonArray.getJSONObject(0)
                        // Handle missing "stage"
                        val stageName: String = stagesJsonObject.optString("stage", "Unknown Stage")
                        // Handle missing "pest_and_diseases"
                        val pestAndDiseasesJsonArray: JSONArray =
                            stagesJsonObject.optJSONArray("pest_and_diseases") ?: JSONArray()

                        diseasesDetails = ArrayList()

                        for (i in 0 until pestAndDiseasesJsonArray.length()) {
                            val pestAndDiseasesJsonObject =
                                pestAndDiseasesJsonArray.getJSONObject(i)

                            val pestAndDiseaseId = pestAndDiseasesJsonObject.optInt("id", -1)
                            val stageTitle =
                                pestAndDiseasesJsonObject.optString("title", "Unknown Title")
                            val stageSubtitle =
                                pestAndDiseasesJsonObject.optString("subtitle", "No Subtitle")
                            val type = pestAndDiseasesJsonObject.optString("type", "Unknown Type")

                            var cropsImgUrl = pestAndDiseasesJsonObject.optString("image", "")
                            if (cropsImgUrl.isEmpty()) {
                                cropsImgUrl =
                                    "https://c1.wallpaperflare.com/preview/1015/28/863/leaf-maple-disease-pest.jpg"
                            }

                            diseasesDetails!!.add(
                                DiseasesDetails(
                                    pestAndDiseaseId,
                                    stageTitle,
                                    stageSubtitle,
                                    cropsImgUrl,
                                    type
                                )
                            )
                        }
                        diseaseStages.add(DiseaseStages(0, stageName, diseasesDetails!!))
                        showParticularStagesByList()
                    }
                } catch (e: JSONException) {
                    Log.e("TAGGER", "JSON parsing error at index $0", e)
                }
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
    }


    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        Log.d("TAGGER", "onFailure: ${th?.message}")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@PestsAndDiseasesStages, DashboardScreen::class.java))
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

}