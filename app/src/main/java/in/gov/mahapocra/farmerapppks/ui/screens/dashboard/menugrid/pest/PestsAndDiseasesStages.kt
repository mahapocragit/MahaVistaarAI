package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.pest

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.data.model.DiseaseStages
import `in`.gov.mahapocra.farmerapppks.data.model.DiseasesDetails
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityPestsAndDiseasesLibraryBinding
import `in`.gov.mahapocra.farmerapppks.ui.adapters.ParticularStagesDiseasesAdpater
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.AddCropActivity
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.farmerapppks.util.AppPreferenceManager
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
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
        binding = ActivityPestsAndDiseasesLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.pests_n_diseases)
        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE

        binding.sowingInfoLayout.textView7.text = "Selected Crop"
        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageMenushow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

        binding.relativeLayoutTopBar.imageMenushow.setOnClickListener {
            val intent = Intent(this, CropStageAdvisory::class.java)
            intent.putExtra("id", cropId)
            intent.putExtra("mUrl", mUrl)
            intent.putExtra("mName", cropName)
            intent.putExtra("wotr_crop_id", wotrCropId)
            intent.putExtra("dataSavedInLocal", "dataSavedInLocal")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        if (cropId!! > 0) {
            getCropStages()
        } else {
            val args = intent.getBundleExtra("BUNDLE")
            diseasesDetails =
                args?.getSerializable("diseasesDetails") as ArrayList<DiseasesDetails>?
            showParticularStagesByList()
        }
        binding.sowingInfoLayout.cropNameTextView.text = "$cropName"
    }

    private fun showParticularStagesByList() {
        val particularDiseasesAdpter =
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
        binding.diseasesByStage.setAdapter(particularDiseasesAdpter)
        particularDiseasesAdpter.notifyDataSetChanged()
    }

    private fun getCropStages() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("crop_id", cropId)
            jsonObject.put("lang", languageToLoad)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(
                this,
                APIServices.SSO,
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
        if (n == 1) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.getStatus()) {
                    stageJsonArray = response.getdataArray()
                    diseaseStages = ArrayList()
                    for (i in 0 until stageJsonArray.length()) {
                        val stagesJsonObject: JSONObject = stageJsonArray.get(i) as JSONObject
                        val stageName: String = stagesJsonObject.getString("stage")
                        val pestAndDiseasesJsonArray: JSONArray =
                            stagesJsonObject.getJSONArray("pest_and_diseases")
                        diseasesDetails = ArrayList()
                        for (j in 0 until pestAndDiseasesJsonArray.length()) {
                            val pestAndDiseasesJsonObject: JSONObject =
                                pestAndDiseasesJsonArray.get(j) as JSONObject
                            val peastAndDiseaseId: Int = pestAndDiseasesJsonObject.get("id") as Int
                            val stageTitle: String =
                                pestAndDiseasesJsonObject.get("title") as String
                            val stagesubtitle: String =
                                pestAndDiseasesJsonObject.get("subtitle") as String
                            val type: String = pestAndDiseasesJsonObject.get("type") as String
                            var cropsImgUrl: String? = ""
                            cropsImgUrl = pestAndDiseasesJsonObject.get("image").toString()
                            if (cropsImgUrl == null) {
                                cropsImgUrl =
                                    "https://c1.wallpaperflare.com/preview/1015/28/863/leaf-maple-disease-pest.jpg"
                            }
                            diseasesDetails!!.add(
                                DiseasesDetails(
                                    peastAndDiseaseId,
                                    stageTitle,
                                    stagesubtitle,
                                    cropsImgUrl,
                                    type
                                )
                            )
                        }
                        diseaseStages.add(DiseaseStages(i, stageName, diseasesDetails!!))
                    }
                    showParticularStagesByList()
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }
}