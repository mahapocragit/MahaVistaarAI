package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.advisory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.pest.SelectSowingDataAndFarmer
import `in`.gov.mahapocra.farmerapppks.util.AppPreferenceManager
import `in`.gov.mahapocra.farmerapppks.ui.adapters.StageAdvisoryAdapter
import `in`.gov.mahapocra.farmerapppks.ui.adapters.StageAdvisoryDetailAdaptr
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityAdvisoryCropBinding
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.AddCropActivity
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
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
    private val googleDriveView: String = "https://mozilla.github.io/pdf.js/web/viewer.html?file="
    var cropId: Int? = 0
    private var cropName: String? = null
    private var farmerId: Int = 0
    private var villageID: Int = 0
    private var wotrCropId: String? = null
    private var mUrl: String? = null
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

        binding.stageWiseTV.setOnClickListener {
            binding.stageWiseTV.apply {
                background = ContextCompat.getDrawable(this@AdvisoryCropActivity, R.drawable.shape_right_green)
                setTextColor(ContextCompat.getColor(this@AdvisoryCropActivity, R.color.white))
            }
            binding.taskWiseTV.apply {
                background = ContextCompat.getDrawable(this@AdvisoryCropActivity, R.drawable.shape_left_white)
                setTextColor(ContextCompat.getColor(this@AdvisoryCropActivity, R.color.black))
            }
            binding.webView.visibility = View.GONE
            binding.cropStagesRecyclerView.visibility = View.VISIBLE
        }
        binding.taskWiseTV.setOnClickListener {
            binding.stageWiseTV.apply {
                background = ContextCompat.getDrawable(this@AdvisoryCropActivity, R.drawable.shape_right)
                setTextColor(ContextCompat.getColor(this@AdvisoryCropActivity, R.color.black))
            }
            binding.taskWiseTV.apply {
                background = ContextCompat.getDrawable(this@AdvisoryCropActivity, R.drawable.shape_left)
                setTextColor(ContextCompat.getColor(this@AdvisoryCropActivity, R.color.white))
            }
            binding.webView.visibility = View.VISIBLE
            binding.cropStagesRecyclerView.visibility = View.GONE
        }

        binding.sowingInfoLayout.cropInfoCardView.setOnClickListener {
            val sharing = Intent(this, AddCropActivity::class.java)
            AppPreferenceManager(this).saveString(
                AppConstants.ACTION_FROM_DASHBOARD,
                AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD
            )
            startActivity(sharing)
        }

        //fetching values
        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        sowingDate = intent.getStringExtra("sowingDate").toString()
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)

        binding.sowingInfoLayout.editSowingDateIcon.setOnClickListener {
            val sharing = Intent(this, SelectSowingDataAndFarmer::class.java)
            sharing.putExtra("id", cropId)
            sharing.putExtra("mName", cropName)
            sharing.putExtra("wotr_crop_id", wotrCropId)
            sharing.putExtra("mUrl", wotrCropId)
            AppPreferenceManager(this).saveString(
                AppConstants.ACTION_FROM_DASHBOARD,
                AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD
            )
            startActivity(sharing)
        }

        binding.sowingInfoLayout.cropNameTextView.text = cropName
        binding.sowingInfoLayout.sowingDateTextView.text = sowingDate
        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.crop_advisory)
        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }
        getCropStagesAndAdvisory()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardScreen::class.java))
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
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.status) {
                    if (jSONObject.getString("sowing_date").isNotEmpty()) {
                        binding.sowingInfoLayout.sowingDateTextView.text =
                            jSONObject.getString("sowing_date")
                    }
                    if (jSONObject.getString("advisory_pdf_url").isNotEmpty()) {
                        Log.d("TAGGER", "onResponse pdf: ${jSONObject.getString("advisory_pdf_url")}")
                        setupWebView(jSONObject.optString("advisory_pdf_url"))
                    }
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

    private fun setupWebView(pdfUrl:String) {
        val settings: WebSettings = binding.webView.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false

        binding.webView.webViewClient = WebViewClient()
        if (pdfUrl!=null) {
            binding.webView.loadUrl(googleDriveView + pdfUrl)
        }else{
            Toast.makeText(this@AdvisoryCropActivity, "PDF not available", Toast.LENGTH_SHORT).show()
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