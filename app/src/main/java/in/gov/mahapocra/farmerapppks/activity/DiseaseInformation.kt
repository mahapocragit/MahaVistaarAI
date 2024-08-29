package `in`.gov.mahapocra.farmerapppks.activity

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import `in`.gov.mahapocra.farmerapppks.adapter.DiseasesInformationImgAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.Locale


class DiseaseInformation : AppCompatActivity() , ApiCallbackCode {

    private var viewPager: ViewPager? = null
    private var symptomDescrpListWebView: WebView? = null
    private var preventCntrlMeasur: WebView? = null
    private lateinit var prevention: TextView
    private lateinit var controlMeasures: TextView
    private lateinit var txtCropName: TextView
    private lateinit var imgBackArrow: ImageView
    private lateinit var textViewHeaderTitle:TextView
    private lateinit var cropName: String
    private lateinit var mainCropName: String
    var id: Int = 0
    private var indicator: TabLayout? = null
     var symptomDescriptionList:String = ""
     var preventiveMeasures: String =""
    var curativeMeasures: String=""
    lateinit var pestAndDiseaseDetailsJson: JSONObject
    lateinit var languageToLoad: String


    var diseasesImages: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DiseaseInformation).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@DiseaseInformation))
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_disease_information)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DiseaseInformation).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@DiseaseInformation))
            languageToLoad = "en"
        }
        Log.d("languageToLoad3",languageToLoad)
        viewPager = findViewById<View>(R.id.view_pager) as ViewPager
        symptomDescrpListWebView = findViewById<View>(R.id.symptomsList) as WebView
        preventCntrlMeasur = findViewById<View>(R.id.prevent_cntrl_measur) as WebView
        prevention = findViewById(R.id.prevention_msr) as TextView
        controlMeasures = findViewById(R.id.control_msr) as TextView
        txtCropName = findViewById(R.id.tvCropName) as TextView
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imgBackArrow = findViewById(R.id.imgBackArrow) as ImageView
        indicator = findViewById<View>(R.id.indicator) as TabLayout


        imgBackArrow?.setVisibility(View.VISIBLE)
        imgBackArrow.setOnClickListener(View.OnClickListener {
            finish()
        })

        cropName = intent.getStringExtra("name").toString()
        id = intent.getIntExtra("id",0)
        textViewHeaderTitle?.setText(cropName)
        Log.d("languageToLoad2",languageToLoad)
        showPestDiseaseDetails()

        mainCropName = AppSettings.getInstance().getValue(this, AppConstants.tmpCROPNAME, AppConstants.tmpCROPNAME)

//        diseasesImages.add("https://farmprecise-app-static-assets.s3.ap-south-1.amazonaws.com/plants_diseases_prevention/29072020175336Greenmould-onion-1.jpg")
//        diseasesImages.add("https://c1.wallpaperflare.com/preview/1015/28/863/leaf-maple-disease-pest.jpg")
//        diseasesImages.add("https://secure.caes.uga.edu/extension/publications/files/html/C960/images/Fig%203b%20stripe%20rust%20symptoms%205%20.jpg")
//        diseasesImages.add("https://farmprecise-app-static-assets.s3.ap-south-1.amazonaws.com/plants_diseases_prevention/29072020175336Greenmould-onion-1.jpg")


//        symptomDescriptionList =
//            "<ul><li style=\\\"font-weight: 400;\\\"><span style=\\\"font-weight: 400;\\\">या रोगाचा प्रादुर्भाव कांद्याच्या वरच्या बाजूस होऊन पूर्ण पाने पक्वतेच्या अगोदरच मरतात.</span></li><li style=\\\"font-weight: 400;\\\"><span style=\\\"font-weight: 400;\\\">प्रादुर्भाव ग्रस्त कांद्यावर हिरवी बुरशी वाढून कांदे हिरवे दिसायला लागतात.</span></li><li style=\\\"font-weight: 400;\\\"><span style=\\\"font-weight: 400;\\\">नंतर प्रादुर्भाव कांद्याच्या मानेपासून आतील गाभ्या पर्यंत वाढतो</span></li></ul>"

        prevention.setOnClickListener(View.OnClickListener {
            prevention.setBackground(ContextCompat.getDrawable(this, R.drawable.dashboard_button))
            controlMeasures.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_button_bg))
            setPreventCntrlMeasurWebView(preventiveMeasures)
        })
        controlMeasures.setOnClickListener(View.OnClickListener {

            controlMeasures.setBackground(ContextCompat.getDrawable(this, R.drawable.dashboard_button))
            prevention.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_button_bg))
            setPreventCntrlMeasurWebView(curativeMeasures)
        })

        txtCropName.setText(mainCropName+" ")
    }

    private fun showPestDiseaseDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("pdid",id)
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
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun setPreventCntrlMeasurWebView(param: String) {
        preventCntrlMeasur!!.loadDataWithBaseURL(
            "file:///android_asset/",
            param, "text/html", "utf-8", null
        )
        Log.d("languageToLoad4",languageToLoad)
        changeLocalLang()
    }

    private fun changeLocalLang() {
        if (languageToLoad.equals("mr")){
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
        symptomDescrpListWebView!!.loadDataWithBaseURL(
            "file:///android_asset/",
            param, "text/html", "utf-8", null
        )
    }
    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    Log.d("languageToLoad1",languageToLoad)
                    symptomDescrpListWebView?.visibility=View.VISIBLE
                    pestAndDiseaseDetailsJson = response.getJSONObject()
                    symptomDescriptionList = pestAndDiseaseDetailsJson.getString("symptoms")
                    preventiveMeasures = pestAndDiseaseDetailsJson.getString("preventive_measures")
                    curativeMeasures = pestAndDiseaseDetailsJson.getString("curative_measures")
                    var diseaseImages: JSONArray = pestAndDiseaseDetailsJson.getJSONArray("image")
                    for (j in 0 until diseaseImages.length()){
                        val diseaseImagesJsonObject:JSONObject = diseaseImages.get(j) as JSONObject
                        var img :String = diseaseImagesJsonObject.get("img") as String
                        diseasesImages.add(img)
                    }
                    viewPager!!.adapter = DiseasesInformationImgAdapter(this, diseasesImages)
                    indicator!!.setupWithViewPager(viewPager, true)


                    setPreventCntrlMeasurWebView(preventiveMeasures)
                    setSymptomsWebView(symptomDescriptionList)

                }else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
    }
    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

}