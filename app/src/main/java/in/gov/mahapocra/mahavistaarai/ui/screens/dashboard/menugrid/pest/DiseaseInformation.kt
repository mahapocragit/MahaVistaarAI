package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.DiseasesInformationImgAdapter
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDiseaseInformationBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.Locale


class DiseaseInformation : AppCompatActivity(), ApiCallbackCode {

    private lateinit var binding: ActivityDiseaseInformationBinding
    var id: Int = 0
    private lateinit var imgBackArrow: ImageView
    private lateinit var textViewHeaderTitle: TextView
    private lateinit var cropName: String
    private lateinit var mainCropName: String
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
        switchLanguage(this, languageToLoad)
        binding = ActivityDiseaseInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imgBackArrow = findViewById(R.id.imgBackArrow)

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
        binding.preventionMsr.setOnClickListener {
            binding.preventionMsr.background =
                ContextCompat.getDrawable(this, R.drawable.shape_right_green) //enabled
            binding.controlMsr.background =
                ContextCompat.getDrawable(this, R.drawable.shape_left_white)
            binding.preventionMsr.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.controlMsr.setTextColor(ContextCompat.getColor(this, R.color.black))
            setPreventControlMeasureWebView(preventiveMeasures)
        }
        binding.controlMsr.setOnClickListener {
            binding.preventionMsr.background =
                ContextCompat.getDrawable(this, R.drawable.shape_right)
            binding.controlMsr.background = ContextCompat.getDrawable(this, R.drawable.shape_left)
            binding.preventionMsr.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.controlMsr.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.preventionMsr
            binding.controlMsr
            setPreventControlMeasureWebView(curativeMeasures)
        }

        binding.tvCropName.text = "$mainCropName "
    }

    private fun showPestDiseaseDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("pdid", id)
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
            val responseCall: Call<JsonObject> = apiRequest.getPestDiseaseDetails(requestBody)
            api.postRequest(responseCall, this, 1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setPreventControlMeasureWebView(param: String) {
        binding.preventCntrlMeasur.loadDataWithBaseURL(
            "file:///android_asset/",
            param, "text/html", "utf-8", null
        )
        changeLocalLang()
    }

    private fun changeLocalLang() {
        if (languageToLoad == "mr") {
            val languageToLoad = "mr"

            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
            AppSettings.setLanguage(this@DiseaseInformation, "2")

            binding.preventionMsr.setText(R.string.preventive_measure)
            binding.controlMsr.setText(R.string.control_measures)
        }
    }

    private fun setSymptomsWebView(param: String) {
        binding.symptomsList.loadDataWithBaseURL(
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
                    binding.symptomsList.visibility = View.VISIBLE
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
                    binding.viewPager.adapter = DiseasesInformationImgAdapter(this, diseasesImages)
                    binding.indicator.setupWithViewPager(binding.viewPager, true)

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