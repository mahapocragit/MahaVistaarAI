package `in`.gov.mahapocra.mahavistaarai.ui.screens.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
import org.json.JSONObject

class DetailedNotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedNotificationBinding
    private lateinit var languageToLoad: String
    private lateinit var farmerViewModel: FarmerViewModel

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
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]

        val id = intent.getLongExtra("id", 0L)
        Log.d("TAGGER", "onCreate: $id")
        farmerViewModel.getNotificationDetails(this, id)
        farmerViewModel.getNotificationDetailedResponse.observe(this) {
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val notificationObject = jsonObject.optJSONObject("notifications")
                setUpPageContent(notificationObject, id)
            }
        }

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = "अधिक माहिती"
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setUpPageContent(jsonObject: JSONObject, notificationId: Long) {
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
        farmerViewModel.updateNotificationStatusResponse.observe(this){

        }
    }

    private fun redirectToScreen(testValue: String) {
        val targetIntent = when (testValue.lowercase()) {
            "advisory" -> Intent(this, AdvisoryCropActivity::class.java).apply {
                putExtra("ROUTE", "NOTIFICATION_LIST")
            }

            "sop" -> Intent(this, SOPActivity::class.java).apply {
                putExtra("ROUTE", "NOTIFICATION_LIST")
            }

            "fertilizer" -> Intent(this, FertilizerCalculatorActivity::class.java).apply {
                putExtra("ROUTE", "NOTIFICATION_LIST")
            }

            "pestdisease" -> Intent(this, PestsAndDiseasesStages::class.java).apply {
                putExtra("ROUTE", "NOTIFICATION_LIST")
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