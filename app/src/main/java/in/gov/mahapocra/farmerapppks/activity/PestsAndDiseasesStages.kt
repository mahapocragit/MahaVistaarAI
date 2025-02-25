package `in`.gov.mahapocra.farmerapppks.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.AppPreferenceManager
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.ParticularStagesDiseasesAdpater
import `in`.gov.mahapocra.farmerapppks.adapter.PestAndDiseasesAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.DiseaseStages
import `in`.gov.mahapocra.farmerapppks.models.response.DiseasesDetails
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit


class PestsAndDiseasesStages : AppCompatActivity(), ApiCallbackCode {

    private var textViewHeaderTitle: TextView? = null
    private var tvAgroMetAdvisory: TextView? = null
    private var cropNameTextView: TextView? = null
    private lateinit var diseaseStageTv: TextView
    private lateinit var sowingDateTextView: TextView
    private lateinit var sowingDateLabel: TextView
    private var imageMenushow: ImageView? = null
    private lateinit var imageViewHeaderBack: ImageView
    private lateinit var editSowingDateIcon: ImageView
    private lateinit var imageHome: ImageView
    private lateinit var cropInfoCardView: CardView

    private var diseasesByStage: RecyclerView? = null
    private var diseasesDetails: ArrayList<DiseasesDetails>? = null
    private lateinit var diseaseStages: ArrayList<DiseaseStages>

    private var particularStagesDiseases: String = "AllStagesDiseases"
    private var stagesId: Int = 0
    var cropId: Int? = 0
    private var wotrCropId: String? = null
    private var sowingDate: String? = null
    private var mUrl: String? = null
    var cropName: String? = null
    private var stagesName: String = ""
    private lateinit var stageJsonArray: JSONArray
    lateinit var languageToLoad: String

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@PestsAndDiseasesStages).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@PestsAndDiseasesStages))
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_pests_and_diseases_activitry)

        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        cropInfoCardView = findViewById(R.id.cropInfoCardView)
        cropNameTextView = findViewById(R.id.cropNameTextView)
        tvAgroMetAdvisory = findViewById(R.id.tvAgroMetAdvisory)
        imageViewHeaderBack = findViewById(R.id.imageViewHeaderBack)
        diseaseStageTv = findViewById(R.id.disease_stage)
        sowingDateTextView = findViewById(R.id.sowingDateTextView)
        sowingDateLabel = findViewById(R.id.textView7)
        imageMenushow = findViewById(R.id.imgBackArrow)
        editSowingDateIcon = findViewById(R.id.editSowingDateIcon)
        imageHome = findViewById(R.id.imageMenushow)
        diseasesByStage = findViewById(R.id.diseasesByStage)

        textViewHeaderTitle?.setText(R.string.pests_n_diseases)
        imageMenushow?.visibility = View.VISIBLE

        sowingDateTextView.visibility = View.GONE
        sowingDateLabel.text = "Selected Crop"
        imageHome.visibility = View.VISIBLE
        imageHome.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        editSowingDateIcon.visibility = View.GONE
        imageViewHeaderBack.visibility = View.VISIBLE
        imageViewHeaderBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        cropId = intent.getIntExtra("cropId", 0)
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        sowingDate = intent.getStringExtra("sowingDate")
        mUrl = intent.getStringExtra("mUrl")
        cropName = intent.getStringExtra("mName")
        stagesName = intent.getStringExtra("name").toString()
        particularStagesDiseases = intent.getStringExtra("ParticularStagesDiseases").toString()
        stagesId = intent.getIntExtra("id", 0)
        AppSettings.getInstance()
            .setValue(this, AppConstants.tmpCROPNAME, cropName)

        cropInfoCardView.setOnClickListener {
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

        imageMenushow?.setOnClickListener {
            val intent = Intent(this, CropStageAdvisory::class.java)
            //here id means cropID not stageID
            intent.putExtra("id", cropId)
            intent.putExtra("mUrl", mUrl)
            intent.putExtra("mName", cropName)
            intent.putExtra("wotr_crop_id", wotrCropId)
            intent.putExtra("dataSavedInLocal", "dataSavedInLocal")
            //  intent.putExtra("name",stagesName)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


        if (cropId!! > 0) {
//            diseaseStageTv.visibility = View.VISIBLE
            getCropStages()
        } else {
//            diseaseStageTv.visibility = View.GONE
            val args = intent.getBundleExtra("BUNDLE")
            diseasesDetails =
                args?.getSerializable("diseasesDetails") as ArrayList<DiseasesDetails>?
            showParticularStagesByList()
        }

        cropNameTextView?.text = "$cropName"

    }

    private fun showParticularStagesByList() {
            diseaseStageTv.text = stagesName
            val particularDiseasesAdpter =
                ParticularStagesDiseasesAdpater(
                    this,
                    diseasesDetails!!
                )
            diseasesByStage?.setLayoutManager(
                LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
            diseasesByStage?.setAdapter(particularDiseasesAdpter)
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
                val response = ResponseModel(jSONObject)
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
//                    showStages(diseaseStages)
                    showParticularStagesByList()
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
    }

    private fun showStages(diseaseStages: ArrayList<DiseaseStages>) {

        val pestAndDiseasesAdapter =
            PestAndDiseasesAdapter(
                this,
                diseaseStages
            )

        diseasesByStage?.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        diseasesByStage?.setAdapter(pestAndDiseasesAdapter)
        pestAndDiseasesAdapter.notifyDataSetChanged()
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }


}