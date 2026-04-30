package `in`.gov.mahapocra.mahavistaarai.ui.screens.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIKeys
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.NewDashboardMainActivity
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage

class SplashScreenActivity : AppCompatActivity() {

    private var farmerId: Int = 0
    private lateinit var languageToLoad: String
    private lateinit var appVersionText: TextView
    private lateinit var appPreferenceManager: AppPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@SplashScreenActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        setContentView(R.layout.activity_splash_screen)
        appPreferenceManager = AppPreferenceManager(this)
        val config = ClarityConfig(
            projectId = APIKeys.CLARITY_PROD,
            logLevel = LogLevel.Verbose
        )
        Clarity.initialize(applicationContext, config)
        appPreferenceManager.saveBoolean("SHOW_PROMO_DIALOG", true)
        appVersionText = findViewById(R.id.appVersionText)
        appVersionText.text = buildString {
            append(getString(R.string.app_version))
            append(" ")
            append(LocalCustom.getVersionName(this@SplashScreenActivity))
        }
        // Get Farmer ID
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        val accessToken = AppPreferenceManager(this).getString(AppConstants.ACCESS_TOKEN)?:""
        // Navigate to the appropriate screen after delay
        Handler(Looper.getMainLooper()).postDelayed({
            val targetActivity = if (accessToken.isNotEmpty()) {
                NewDashboardMainActivity::class.java
            } else {
                LoginScreen::class.java
            }

            startActivity(Intent(this, targetActivity).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            finish()
        }, 2000)
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