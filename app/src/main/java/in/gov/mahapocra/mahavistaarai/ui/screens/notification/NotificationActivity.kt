package `in`.gov.mahapocra.mahavistaarai.ui.screens.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityNotificationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.NotificationAdapter
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
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import org.json.JSONObject

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var languageToLoad: String
    private lateinit var farmerViewModel: FarmerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@NotificationActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.my_notification)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
        farmerViewModel.getNotificationList(this)
        farmerViewModel.getNotificationResponse.observe(this) {
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val notificationJsonArray = jsonObject.getJSONArray("notifications")
                binding.notificationRecyclerView.apply {
                    hasFixedSize()
                    layoutManager = LinearLayoutManager(this@NotificationActivity)
                    adapter = NotificationAdapter(notificationJsonArray) { jsonObject ->
                        val page = jsonObject.optString("page")
                        val notificationMessage = jsonObject.optString("body")
                        val notificationDate = jsonObject.optString("notification_date")
                        Log.d("TAGGER", "onCreate: $jsonObject")
                        redirectToScreen(page)
                    }
                }
            }
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

            "pestDisease" -> Intent(this, PestsAndDiseasesStages::class.java).apply {
                putExtra("ROUTE", "NOTIFICATION_LIST")
            }

            "weather" -> Intent(this, WeatherActivity::class.java)
            "soilCard" -> Intent(this, HealthCardActivity::class.java)
            "climateTech" -> Intent(this, ClimateResilientTechnology::class.java)
            "marketPrice" -> Intent(this, MarketPrice::class.java)
            "shetishala" -> Intent(this, ShetishalaActivity::class.java)
            "warehouse" -> Intent(this, Warehouse::class.java)
            "customHire" -> Intent(this, CHCenterActivity::class.java)
            "videos" -> Intent(this, VideosActivity::class.java)
            "dbtSchemes" -> Intent(this, DBTActivity::class.java)
            "dashboard" -> Intent(this, DashboardScreen::class.java)
            else -> Intent(this, DashboardScreen::class.java) // default fallback
        }.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
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