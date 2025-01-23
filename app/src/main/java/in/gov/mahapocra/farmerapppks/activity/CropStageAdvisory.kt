package `in`.gov.mahapocra.farmerapppks.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.DashboardAdapter
import `in`.gov.mahapocra.farmerapppks.adapter.StageAdvisoryAdapter
import `in`.gov.mahapocra.farmerapppks.adapter.StageAdvisoryDetailAdaptr
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityCropStageAdvisoryBinding
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class CropStageAdvisory : AppCompatActivity(), ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private lateinit var binding: ActivityCropStageAdvisoryBinding

    private var cropAdvisoryDetailsJSONArray: JSONArray? = null
    private var cropAdvisoryJSONArray: JSONArray? = null

    var cropId: Int? = 0
    private var wotrCropId: String? = "0"
    private var cropName: String? = null
    private var mUrl: String? = null
    private var farmerId: Int = 0
    private var villageID: Int = 0
    private var sowingDate: String = ""
    lateinit var languageToLoad: String
    private val arrayCategory =
        arrayOf("Crop Advisory", "Fertilizer Calculator", "Crop Management", "Pests and Diseases")
    private val arrayCategoryMarathi =
        arrayOf("पीक सल्ला", "खत मात्रा गणक (कॅलक्यूलेटर)", "पीक व्यवस्थापन", "कीड व रोग")
    private var arrayCategoryImg = intArrayOf(
        R.drawable.crop_advsry,
        R.drawable.fertilizer_calculator,
        R.drawable.pest_management,
        R.drawable.pets_n_disease_img
    )

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropStageAdvisoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@CropStageAdvisory).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        binding.gridViewJobs.columnWidth = GridView.AUTO_FIT
        setConfiguration()

        cropId = intent.getIntExtra("id", 0)
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")
        cropName = intent.getStringExtra("mName")
        binding.cropNameTextView.text = cropName
        val dataSavedInLocal = intent.getStringExtra("dataSavedInLocal")
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        if (dataSavedInLocal.equals("dataSavedInLocal") && cropId == 0) {
            cropId = AppSettings.getInstance().getIntValue(this, AppConstants.tmpCROPID, 0)
            wotrCropId = AppSettings.getInstance()
                .getValue(this, AppConstants.tmpWOTRID, AppConstants.tmpWOTRID)
            sowingDate = AppSettings.getInstance()
                .getValue(this, AppConstants.tmpSOWINGDATE, AppConstants.tmpSOWINGDATE)
            mUrl =
                AppSettings.getInstance().getValue(this, AppConstants.tmpMURL, AppConstants.tmpMURL)
            cropName = AppSettings.getInstance()
                .getValue(this, AppConstants.tmpCROPNAME, AppConstants.tmpCROPNAME)
        }

        if (languageToLoad.equals("en", ignoreCase = true)) {
            binding.gridViewJobs.adapter =
                DashboardAdapter(
                    this,
                    arrayCategory,
                    arrayCategoryImg,
                    "stage_single_item_grid"
                )
            Log.d("", "")
        } else if (languageToLoad.equals("mr", ignoreCase = true)) {
            binding.gridViewJobs.adapter =
                DashboardAdapter(
                    this,
                    arrayCategoryMarathi,
                    arrayCategoryImg,
                    "stage_single_item_grid"
                )
        }

        binding.gridViewJobs.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                when (position) {
                    0 -> {
                        binding.cropAdvisoryLinearLayout.visibility = View.VISIBLE
                        binding.comingSoonLinearLayout.visibility = View.GONE
                    }

                    1 -> {
                        val intent = Intent(this, FertilizerCalculatorActivity::class.java)
                        intent.putExtra("id", cropId)
                        intent.putExtra("mName", cropName)
                        intent.putExtra("wotr_crop_id", wotrCropId)
                        intent.putExtra("sowingDate", sowingDate)
                        startActivity(intent)
                    }

                    2 -> {
                        binding.cropAdvisoryLinearLayout.visibility = View.GONE
                        binding.comingSoonLinearLayout.visibility = View.VISIBLE
                    }

                    3 -> {
                        val intent = Intent(this, PestsAndDiseasesStages::class.java)
                        intent.putExtra("cropId", cropId)
                        intent.putExtra("wotr_crop_id", wotrCropId)
                        intent.putExtra("sowingDate", sowingDate)
                        intent.putExtra("mUrl", mUrl)
                        intent.putExtra("mName", cropName)
                        startActivity(intent)
                    }
                }

                AppSettings.getInstance().setIntValue(this, AppConstants.tmpCROPID, cropId!!)
                AppSettings.getInstance().setValue(this, AppConstants.tmpWOTRID, wotrCropId)
                AppSettings.getInstance().setValue(this, AppConstants.tmpSOWINGDATE, sowingDate)
                AppSettings.getInstance().setValue(this, AppConstants.tmpMURL, mUrl)
                AppSettings.getInstance().setValue(this, AppConstants.tmpCROPNAME, cropName)
            }


        binding.relativeLayoutTopBar.textViewHeaderTitle.text = cropName
        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageMenushow.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }

        binding.editCropImageView.setOnClickListener {
            val intent = Intent(this, SelectSowingDataAndFarmer::class.java)
            intent.putExtra("id", cropId)
            intent.putExtra("mUrl", mUrl)
            intent.putExtra("mName", cropName)
            intent.putExtra("editCrop", "EditCrop")
            startActivity(intent)
        }

        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        getCropStagesAndAdvisory()
        binding.addCropImageView.setOnClickListener {
            val intent = Intent(this@CropStageAdvisory, AddCropActivity::class.java)
            intent.putExtra("SOWING_DATE", sowingDate)
            intent.putExtra("NO_NEED_TO_ADD_SOWING_DATE", "NO_NEED_TO_ADD_SOWING_DATE")
            startActivity(intent)
        }
    }

    private fun setConfiguration() {
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@CropStageAdvisory).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@CropStageAdvisory))
            languageToLoad = "en"
        }
    }

    private fun getCropStagesAndAdvisory() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("crop_id", cropId)
            jsonObject.put("farmer_id", farmerId)
            jsonObject.put("lang", languageToLoad)
            Log.d("RESPONSE_TAG", "getCropStagesAndAdvisory: " + jsonObject)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.retrofitInstance
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getCropStagesAndAdvisory(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=$e")
            e.printStackTrace()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.status) {
                    sowingDate = jSONObject.getString("sowing_date")
                    binding.sowingDateTextView.text = jSONObject.getString("sowing_date")
                    cropAdvisoryDetailsJSONArray = response.getdataArray()
                    Log.d("RESPONSE_TAG", "onResponse: $cropAdvisoryDetailsJSONArray")

                    if (cropAdvisoryDetailsJSONArray?.length()!! > 0) {
                        val stagesAdvisoryAdapter =
                            StageAdvisoryAdapter(this, this, cropAdvisoryDetailsJSONArray)
                        binding.cropStagesRecyclerView.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        binding.cropStagesRecyclerView.adapter = stagesAdvisoryAdapter
                        stagesAdvisoryAdapter.notifyDataSetChanged()
                    }
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 1) {
            val cropDetail: JSONObject = obj as JSONObject
            val status =
                if (cropDetail.getString("status").equals("pending")) "pending" else "No data"
            binding.comingSoonLinearLayout1.visibility = View.GONE
            binding.cropStagesInfoRecyclerView.visibility = View.VISIBLE
            cropAdvisoryJSONArray = cropDetail.getJSONArray("advisory")
            if (cropAdvisoryJSONArray?.length() ==0){
                Toast.makeText(this, "Advisory is not available for current stage", Toast.LENGTH_SHORT).show()
            }
            val stageAdvisoryDetailAdapter = StageAdvisoryDetailAdaptr(
                this,
                this,
                cropAdvisoryJSONArray as JSONArray,
                languageToLoad,
                cropId.toString(),
                villageID.toString()
            )
            binding.cropStagesInfoRecyclerView.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.cropStagesInfoRecyclerView.adapter = stageAdvisoryDetailAdapter
            stageAdvisoryDetailAdapter.notifyDataSetChanged()
        }
        if (i == 2) {
            binding.relativeLayoutTopBar.relativeLayoutToolbar.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardScreen::class.java)
        intent.putExtra("dataSavedInLocal", "dataSavedInLocal")
        startActivity(intent)
    }
}