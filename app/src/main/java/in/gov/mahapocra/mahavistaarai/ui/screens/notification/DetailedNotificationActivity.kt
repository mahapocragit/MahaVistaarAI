package `in`.gov.mahapocra.mahavistaarai.ui.screens.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDetailedNotificationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc.CHCenterActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.FertilizerCalculatorActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.MarketPrice
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.SOPActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.Warehouse
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.advisory.AdvisoryCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate.ClimateResilientTechnology
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.DBTActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.PestsAndDiseasesStages
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard.HealthCardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala.ShetishalaActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather.WeatherActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import org.json.JSONArray
import org.json.JSONObject

class DetailedNotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedNotificationBinding
    private lateinit var languageToLoad: String
    private val farmerViewModel: FarmerViewModel by viewModels()
    val flatCropsJsonArray = JSONArray()
    var cropId: Int? = 0
    private var cropName: String? = null
    private var wotrCropId: String? = null
    private var mUrl: String? = null
    private var sowingDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DetailedNotificationActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityDetailedNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        val notificationObject = intent.getStringExtra("notificationObject")
        if (notificationObject != null) {
            val jsonObject = JSONObject(notificationObject)
            val id = jsonObject.optLong("id")
            val flatCropId = jsonObject.optInt("crop")
            farmerViewModel.getNotificationDetails(this, id)
            farmerViewModel.getNotificationDetailedResponse.observe(this) {
                if (it != null) {
                    Log.d("TAGGER", "onCreate: $it")
                    val jsonObject = JSONObject(it.toString())
                    val notificationObject = jsonObject.optJSONObject("notifications")
                    setUpPageContent(notificationObject, id)
                }
            }
            fetchCropList(flatCropId)
        } else {
            val id = intent.getLongExtra("id", 0L)
            farmerViewModel.getNotificationDetails(this, id)
            farmerViewModel.getNotificationDetailedResponse.observe(this) {
                if (it != null) {
                    val jsonObject = JSONObject(it.toString())
                    Log.d("TAGGER", "getNotificationDetails: $jsonObject")
                    val notificationObject = jsonObject.optJSONObject("notifications")
                    val flatCropId = notificationObject?.optInt("crop")
                    Log.d("TAGGER", "onCreate: $flatCropId")
                    setUpPageContent(notificationObject, id)
                    fetchCropList(flatCropId)
                }
            }
        }


        binding.relativeLayoutTopBar.textViewHeaderTitle.text = "अधिक माहिती"
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun fetchCropList(flatCropId:Int?) {
        farmerViewModel.getCropCategoriesAndCropDetails(this, languageToLoad)
        farmerViewModel.cropCategoryResponse.observe(this) { response ->
            if (response != null) {
                try {
                    val jsonObject = JSONObject(response.toString())
                    val dataArray = jsonObject.optJSONArray("data")

                    if (dataArray != null) {
                        for (i in 0 until dataArray.length()) {
                            val categoryObject = dataArray.optJSONObject(i)
                            val crops = categoryObject.optJSONArray("crops")

                            if (crops != null) {
                                for (j in 0 until crops.length()) {
                                    val crop = crops.optJSONObject(j)
                                    flatCropsJsonArray.put(crop)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (flatCropsJsonArray!=null && flatCropsJsonArray.length()>0) {
                for (i in 0 until flatCropsJsonArray.length()) {
                    val jsonObject = flatCropsJsonArray.getJSONObject(i)
                    val id = jsonObject.optInt("id")
                    if (id == flatCropId) {
                        Log.d("TAGGER", "checkCropIdAndFetchJson: $jsonObject")
                        cropId = jsonObject.optInt("id")
                        cropName = jsonObject.optString("name")
                        sowingDate = jsonObject.optString("sowing_date")
                        wotrCropId = jsonObject.optString("wotr_crop_id")
                        mUrl = jsonObject.optString("mUrl")
                    }
                }
            }
        }
    }

    private fun setUpPageContent(jsonObject: JSONObject, notificationId: Long) {
        binding.notificationInfoLayout.visibility = View.VISIBLE
        val page = jsonObject.optString("page")
        val title = jsonObject.optString("title")
        val shortDescription = jsonObject.optString("body")
        val longDescription = jsonObject.optString("description")
        val notificationDate =
            LocalCustom.convertDateFormat(jsonObject.optString("notification_date"))
        val redirectionText = jsonObject.optString("redirection_text")

        binding.titleTextView.text = title
        binding.shortDescriptionTextView.text = shortDescription
        binding.longDescriptionTextView.text = longDescription
        binding.dateTextView.text = notificationDate
        binding.redirectTextView.text = redirectionText ?: "अधिक माहितीसाठी येथे क्लिक करा."
        binding.redirectTextView.setOnClickListener { redirectToScreen(page) }
        farmerViewModel.updateNotificationStatus(this, notificationId)
        farmerViewModel.updateNotificationStatusResponse.observe(this) {

        }
    }

    private fun redirectToScreen(testValue: String) {
        val targetIntent = when (testValue.lowercase()) {
            "advisory" -> Intent(this, AdvisoryCropActivity::class.java).apply {
                putExtra("id", cropId)
                putExtra("wotr_crop_id", wotrCropId)
                putExtra("mUrl", mUrl)
                putExtra("sowingDate", sowingDate)
                putExtra("mName", cropName)
            }

            "sop" -> Intent(this, SOPActivity::class.java).apply {
                putExtra("id", cropId)
                putExtra("wotr_crop_id", wotrCropId)
                putExtra("mUrl", mUrl)
                putExtra("sowingDate", sowingDate)
                putExtra("mName", cropName)
            }

            "fertilizer" -> Intent(this, FertilizerCalculatorActivity::class.java).apply {
                putExtra("id", cropId)
                putExtra("wotr_crop_id", wotrCropId)
                putExtra("mUrl", mUrl)
                putExtra("sowingDate", sowingDate)
                putExtra("mName", cropName)
            }

            "pestdisease" -> Intent(this, PestsAndDiseasesStages::class.java).apply {
                putExtra("id", cropId)
                putExtra("wotr_crop_id", wotrCropId)
                putExtra("mUrl", mUrl)
                putExtra("sowingDate", sowingDate)
                putExtra("mName", cropName)
            }

            "weather" -> Intent(this, WeatherActivity::class.java)
            "soilcard" -> Intent(this, HealthCardActivity::class.java)
            "climatetech" -> Intent(this, ClimateResilientTechnology::class.java)
            "marketprice" -> Intent(this, MarketPrice::class.java)
            "shetishala" -> Intent(this, ShetishalaActivity::class.java)
            "warehouse" -> Intent(this, Warehouse::class.java)
            "customhire" -> Intent(this, CHCenterActivity::class.java)
            "videos" -> Intent(this, VideosActivity::class.java)
            "dbtschemes" -> Intent(this, DBTActivity::class.java)
            "dashboard" -> Intent(this, DashboardScreen::class.java)
            else -> Intent(this, DashboardScreen::class.java)
        }
        startActivity(targetIntent)
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