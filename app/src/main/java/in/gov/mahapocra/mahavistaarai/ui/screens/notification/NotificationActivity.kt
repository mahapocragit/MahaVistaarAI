package `in`.gov.mahapocra.mahavistaarai.ui.screens.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityNotificationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.NotificationAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.NetworkUtils
import org.json.JSONObject

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var languageToLoad: String
    private val farmerViewModel: FarmerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@NotificationActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.my_notification)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }
        if (NetworkUtils.isInternetAvailable(this)){
            farmerViewModel.getNotificationList(this)
        }else{
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
        }
        farmerViewModel.getNotificationResponse.observe(this) {
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val notificationJsonArray = jsonObject.getJSONArray("notifications")
                if (notificationJsonArray.length() > 0) {
                    binding.notificationRecyclerView.visibility = View.VISIBLE
                    binding.notificationNotFoundLayout.visibility = View.GONE
                    binding.notificationRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = LinearLayoutManager(this@NotificationActivity)
                        adapter = NotificationAdapter(notificationJsonArray) { jsonObject ->
                            startActivity(
                                Intent(
                                    this@NotificationActivity,
                                    DetailedNotificationActivity::class.java
                                ).apply {
                                    putExtra("notificationObject", jsonObject.toString())
                                })
                        }
                    }
                } else {
                    binding.notificationRecyclerView.visibility = View.GONE
                    binding.notificationNotFoundLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (NetworkUtils.isInternetAvailable(this)){
            farmerViewModel.getNotificationList(this)
        }else{
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
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
}