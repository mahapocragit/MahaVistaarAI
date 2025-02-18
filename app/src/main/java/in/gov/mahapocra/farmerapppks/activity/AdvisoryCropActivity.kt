package `in`.gov.mahapocra.farmerapppks.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import `in`.gov.mahapocra.farmerapppks.AppPreferenceManager
import `in`.gov.mahapocra.farmerapppks.adapter.StageAdvisoryAdapter
import `in`.gov.mahapocra.farmerapppks.adapter.StageAdvisoryDetailAdaptr
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityAdvisoryCropBinding
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class AdvisoryCropActivity : AppCompatActivity(), OnMultiRecyclerItemClickListener,
    ApiCallbackCode {

    private lateinit var binding: ActivityAdvisoryCropBinding
    private var cropAdvisoryDetailsJSONArray: JSONArray? = null
    private var cropAdvisoryJSONArray: JSONArray? = null
    var cropId: Int? = 0
    private var cropName: String? = null
    private var farmerId: Int = 0
    private var villageID: Int = 0
    lateinit var languageToLoad: String
    private var sowingDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvisoryCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setting up values for language
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@AdvisoryCropActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }

        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageMenushow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }
        //fetching values
        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        sowingDate = intent.getStringExtra("sowingDate").toString()
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)

        val cropStageResponseString = AppPreferenceManager(this).getString("CROP_STAGE_RESPONSE")
        binding.sowingInfoLayout.cropNameTextView.text = cropName
        binding.sowingInfoLayout.sowingDateTextView.text = sowingDate
        binding.relativeLayoutTopBar.textViewHeaderTitle.text = "Crop Advisory"
        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        if (!cropStageResponseString.equals("default_value")){
            initializeDataFromJson()
        }else{
            getCropStagesAndAdvisory()
        }
    }

    private fun initializeDataFromJson(){
        try {
            val cropStageResponseString = AppPreferenceManager(this).getString("CROP_STAGE_RESPONSE")
            val jSONObject = JSONObject(cropStageResponseString)
            val response = ResponseModel(jSONObject)
            if (response.status) {
                sowingDate = jSONObject.getString("sowing_date")
                binding.sowingInfoLayout.sowingDateTextView.text = jSONObject.getString("sowing_date")
                cropAdvisoryDetailsJSONArray = response.getdataArray()
                Log.d("RESPONSE_TAG", "onResponse: $cropAdvisoryDetailsJSONArray")

                if (cropAdvisoryDetailsJSONArray?.length()!! > 0) {
                    val stagesAdvisoryAdapter =
                        StageAdvisoryAdapter(this, this, cropAdvisoryDetailsJSONArray)
                    binding.cropStagesRecyclerView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.cropStagesRecyclerView.adapter = stagesAdvisoryAdapter
                    stagesAdvisoryAdapter.notifyDataSetChanged()
                }
            } else {
                UIToastMessage.show(this, response.response)
            }
        }catch (_:Exception){

        }

    }

    private fun getCropStagesAndAdvisory() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("crop_id", cropId)
            jsonObject.put("farmer_id", farmerId)
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
            val retrofit: Retrofit = api.retrofitInstance
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getCropStagesAndAdvisory(requestBody)
            api.postRequest(responseCall, this, 1)
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=$e")
            e.printStackTrace()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        Log.d("TAG", "onFailure: ${th.toString()}")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                val response = ResponseModel(jSONObject)
                if (response.status) {
                    cropAdvisoryDetailsJSONArray = response.getdataArray()
                    if (cropAdvisoryDetailsJSONArray?.length()!! > 0) {
                        val stagesAdvisoryAdapter =
                            StageAdvisoryAdapter(this, this, cropAdvisoryDetailsJSONArray)
                        binding.cropStagesRecyclerView.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.cropStagesRecyclerView.adapter = stagesAdvisoryAdapter
                        stagesAdvisoryAdapter.notifyDataSetChanged()
                    }
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 1) {
            val cropDetail: JSONObject = obj as JSONObject
            cropAdvisoryJSONArray = cropDetail.getJSONArray("advisory")
            if (cropAdvisoryJSONArray?.length() == 0) {
                Toast.makeText(
                    this,
                    "Advisory is not available for current stage",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val stageAdvisoryDetailAdapter = StageAdvisoryDetailAdaptr(
                this,
                this,
                cropAdvisoryJSONArray as JSONArray,
                languageToLoad,
                cropId.toString(),
                villageID.toString()
            )
            stageAdvisoryDetailAdapter.notifyDataSetChanged()
        }
        if (i == 2) {
            binding.relativeLayoutTopBar.relativeLayoutToolbar.visibility = View.GONE
        }
    }
}