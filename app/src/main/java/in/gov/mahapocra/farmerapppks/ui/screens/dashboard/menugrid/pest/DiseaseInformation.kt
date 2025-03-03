package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.pest

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.ui.adapters.DiseasesInformationImgAdapter
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.Locale


class DiseaseInformation : AppCompatActivity(), ApiCallbackCode {

    var id: Int = 0
    private var viewPager: ViewPager? = null
    private var symptomDescriptionListWebView: WebView? = null
    private var preventControlMeasure: WebView? = null
    private lateinit var prevention: TextView
    private lateinit var controlMeasures: TextView
    private lateinit var txtCropName: TextView
    private lateinit var imgBackArrow: ImageView
    private lateinit var textViewHeaderTitle: TextView
    private lateinit var cropName: String
    private lateinit var mainCropName: String
    private var indicator: TabLayout? = null
    private var symptomDescriptionList: String = ""
    private var preventiveMeasures: String = ""
    private var curativeMeasures: String = ""
    lateinit var languageToLoad: String
    private lateinit var pestAndDiseaseDetailsJson: JSONObject
    private var diseasesImages: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DiseaseInformation).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_disease_information)
        viewPager = findViewById<View>(R.id.view_pager) as ViewPager
        symptomDescriptionListWebView = findViewById<View>(R.id.symptomsList) as WebView
        preventControlMeasure = findViewById<View>(R.id.prevent_cntrl_measur) as WebView
        prevention = findViewById(R.id.prevention_msr)
        controlMeasures = findViewById(R.id.control_msr)
        txtCropName = findViewById(R.id.tvCropName)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imgBackArrow = findViewById(R.id.imgBackArrow)
        indicator = findViewById<View>(R.id.indicator) as TabLayout

        imgBackArrow.visibility = View.VISIBLE
        imgBackArrow.setOnClickListener {
            finish()
        }

        cropName = intent.getStringExtra("name").toString()
        id = intent.getIntExtra("id", 0)
        textViewHeaderTitle.text = cropName
        showPestDiseaseDetails()

        mainCropName = AppSettings.getInstance()
            .getValue(this, AppConstants.tmpCROPNAME, AppConstants.tmpCROPNAME)
        prevention.setOnClickListener {
            prevention.background = ContextCompat.getDrawable(this, R.drawable.dashboard_button)
            controlMeasures.background =
                ContextCompat.getDrawable(this, R.drawable.layout_button_bg)
            setPreventControlMeasureWebView(preventiveMeasures)
        }
        controlMeasures.setOnClickListener {

            controlMeasures.background =
                ContextCompat.getDrawable(this, R.drawable.dashboard_button)
            prevention.background = ContextCompat.getDrawable(this, R.drawable.layout_button_bg)
            setPreventControlMeasureWebView(curativeMeasures)
        }

        txtCropName.text = mainCropName + " "
    }

    private fun showPestDiseaseDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("pdid", id)
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
            val responseCall: Call<JsonObject> = apiRequest.getPestDiseaseDetails(requestBody)
            api.postRequest(responseCall, this, 1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setPreventControlMeasureWebView(param: String) {
        preventControlMeasure!!.loadDataWithBaseURL(
            "file:///android_asset/",
            param, "text/html", "utf-8", null
        )
        changeLocalLang()
    }

    private fun changeLocalLang() {
        if (languageToLoad == "mr") {
            val languageToLoad = "hi"

            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
            AppSettings.setLanguage(this@DiseaseInformation, "2")

            prevention.setText(R.string.preventive_measure)
            controlMeasures.setText(R.string.control_measures)
        }
    }

    private fun setSymptomsWebView(param: String) {
        symptomDescriptionListWebView!!.loadDataWithBaseURL(
            "file:///android_asset/",
            param, "text/html", "utf-8", null
        )
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.getStatus()) {
                    symptomDescriptionListWebView?.visibility = View.VISIBLE
                    pestAndDiseaseDetailsJson = response.getJSONObject()
                    symptomDescriptionList = pestAndDiseaseDetailsJson.getString("symptoms")
                    preventiveMeasures = pestAndDiseaseDetailsJson.getString("preventive_measures")
                    curativeMeasures = pestAndDiseaseDetailsJson.getString("curative_measures")
                    val diseaseImages: JSONArray = pestAndDiseaseDetailsJson.getJSONArray("image")
                    for (j in 0 until diseaseImages.length()) {
                        val diseaseImagesJsonObject: JSONObject = diseaseImages.get(j) as JSONObject
                        val img: String = diseaseImagesJsonObject.get("img") as String
                        diseasesImages.add(img)
                    }
                    viewPager!!.adapter = DiseasesInformationImgAdapter(this, diseasesImages)
                    indicator!!.setupWithViewPager(viewPager, true)

                    setPreventControlMeasureWebView(preventiveMeasures)
                    setSymptomsWebView(symptomDescriptionList)

                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        th?.printStackTrace()
    }

}