package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDiseaseInformationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.DiseasesInformationImgAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale


class DiseaseInformation : AppCompatActivity() {

    private lateinit var binding: ActivityDiseaseInformationBinding
    private lateinit var farmerViewModel: FarmerViewModel
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
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imgBackArrow = findViewById(R.id.imgBackArrow)

        imgBackArrow.visibility = View.VISIBLE
        imgBackArrow.setOnClickListener {
            finish()
        }

        cropName = intent.getStringExtra("name").toString()
        id = intent.getIntExtra("id", 0)
        textViewHeaderTitle.text = cropName
        observePestDiseaseDetails()
        farmerViewModel.showPestDiseaseDetails(this, id)
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

    private fun observePestDiseaseDetails() {
        ProgressHelper.showProgressDialog(this)
        farmerViewModel.getPestDiseaseDetailsResponse.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val response =
                    ResponseModel(
                        it
                    )
                if (response.getStatus()) {
                    binding.symptomsList.visibility = View.VISIBLE
                    pestAndDiseaseDetailsJson = response.getJSONObject()
                    symptomDescriptionList = pestAndDiseaseDetailsJson.getString("symptoms")
                    preventiveMeasures =
                        pestAndDiseaseDetailsJson.getString("preventive_measures")
                    curativeMeasures = pestAndDiseaseDetailsJson.getString("curative_measures")
                    val diseaseImages: JSONArray =
                        pestAndDiseaseDetailsJson.getJSONArray("image")
                    for (j in 0 until diseaseImages.length()) {
                        val diseaseImagesJsonObject: JSONObject =
                            diseaseImages.get(j) as JSONObject
                        val img: String = diseaseImagesJsonObject.get("img") as String
                        diseasesImages.add(img)
                    }
                    binding.viewPager.adapter =
                        DiseasesInformationImgAdapter(this, diseasesImages)
                    binding.indicator.setupWithViewPager(binding.viewPager, true)

                    setPreventControlMeasureWebView(preventiveMeasures)
                    setSymptomsWebView(symptomDescriptionList)
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
        farmerViewModel.error.observe(this){
            ProgressHelper.disableProgressDialog()
        }
    }

    private fun setPreventControlMeasureWebView(param: String) {
        binding.preventCntrlMeasur.loadDataWithBaseURL(
            "file:///android_asset/",
            param, "text/html", "utf-8", null
        )
    }

    private fun setSymptomsWebView(param: String) {
        binding.symptomsList.loadDataWithBaseURL(
            "file:///android_asset/",
            param, "text/html", "utf-8", null
        )
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