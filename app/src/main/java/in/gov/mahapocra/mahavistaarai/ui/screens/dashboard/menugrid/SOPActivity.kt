package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivitySopactivityBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.SOPAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class SOPActivity : AppCompatActivity(), ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private lateinit var binding: ActivitySopactivityBinding
    private lateinit var farmerViewModel: FarmerViewModel
    var cropId: Int? = 0
    private var cropName: String? = null
    private var farmerId: Int = 0
    private var villageID: Int = 0
    private var wotrCropId: String? = null
    private var mUrl: String? = null
    private var sowingDate: String = ""
    private lateinit var languageToLoad: String
    private val googleDriveView: String = "https://mozilla.github.io/pdf.js/web/viewer.html?file="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@SOPActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivitySopactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        farmerViewModel= ViewModelProvider(this)[FarmerViewModel::class.java]

        //fetching values
        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        sowingDate = intent.getStringExtra("sowingDate").toString()
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        getCropStagesAndAdvisory()

        binding.sowingInfoLayout.sowingDateTextView.visibility = View.GONE
        binding.sowingInfoLayout.editSowingDateIcon.visibility = View.GONE
        binding.sowingInfoLayout.textView7.text = getString(R.string.selected_crop)
        binding.sowingInfoLayout.cropNameTextView.text = cropName
        binding.floatingActionButton.setOnClickListener {
            mUrl?.let { it1 -> LocalCustom.downloadPdf(this, it1) }
        }

        cropId?.let { farmerViewModel.fetchSOPDate(this, it) }

        binding.sowingInfoLayout.cropInfoCardView.setOnClickListener {
            val sharing = Intent(this, AddCropActivity::class.java)
            sharing.putExtra("id", cropId)
            sharing.putExtra("mName", cropName)
            sharing.putExtra("wotr_crop_id", wotrCropId)
            sharing.putExtra("mUrl", mUrl)
            AppPreferenceManager(this).saveString(
                AppConstants.ACTION_FROM_DASHBOARD,
                AppConstants.SOP_FROM_DASHBOARD
            )
            startActivity(sharing)
        }

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener { startActivity(Intent(this, DashboardScreen::class.java)) }
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.sop_title)
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
            jsonObject.put("sowing_date", "23-03-2025")
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.FARMER,
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

        farmerViewModel.sopResponse.observe(this){
            Log.d("TAGGER", "getCropStagesAndAdvisory: $it")
            if (it!=null){
                val jsonObject = JSONObject(it.toString())
                val dataArray = jsonObject.getJSONArray("data")
                binding.sopRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.sopRecyclerView.adapter = SOPAdapter(this, this, dataArray)
            }
        }
    }

    private fun setupWebView(pdfUrl: String) {
        val settings: WebSettings = binding.webView.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false

        binding.webView.webViewClient = WebViewClient()
        if (pdfUrl != null) {
            val viewOnlyUrl = "$googleDriveView$pdfUrl"
            binding.webView.loadUrl(viewOnlyUrl)
        } else {
            Toast.makeText(this@SOPActivity, "PDF not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            Log.d("TAGGER", "onResponse: $jSONObject")
            if (jSONObject != null) {
                val response = ResponseModel(jSONObject)
                if (response.status) {
                    if (jSONObject.getString("advisory_pdf_url").isNotEmpty()) {
                        Log.d(
                            "TAGGER",
                            "onResponse pdf: ${jSONObject.getString("advisory_pdf_url")}"
                        )
                        val pdfUrl = jSONObject.getString("advisory_pdf_url")
                        if (pdfUrl != "null") {
                            binding.noDataFoundImageView.visibility = View.GONE
                            binding.webView.visibility = View.GONE
                            setupWebView(pdfUrl)
                        } else {
                            binding.noDataFoundImageView.visibility = View.VISIBLE
                            binding.webView.visibility = View.GONE
                            UIToastMessage.show(this, "PDF not available")
                        }
                    }
                } else {
                    UIToastMessage.show(this, response.response)
                }
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

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i==2){
            val jsonObject = obj as JSONObject
            val webViewUrl = jsonObject.optString("url")
            val intent = Intent(this, SOPWebViewActivity::class.java)
            val b = Bundle()
            b.putSerializable("webUrl", webViewUrl)
            intent.putExtras(b)
            startActivity(intent)
        }
    }
}