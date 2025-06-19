package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityShetishalaBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosDetailedActivity
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage

class ShetishalaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShetishalaBinding
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ShetishalaActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityShetishalaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.shetishala)

        binding.scheduleCardView.setOnClickListener {
            startActivity(
                Intent(
                    this@ShetishalaActivity,
                    ShetishalaScheduleActivity::class.java
                )
            )
        }

        binding.shetishalaCardView.setOnClickListener {
            startActivity(
                Intent(
                    this@ShetishalaActivity,
                    VideosDetailedActivity::class.java
                )
            )
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