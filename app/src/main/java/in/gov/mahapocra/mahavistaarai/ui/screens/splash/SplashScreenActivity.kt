package `in`.gov.mahapocra.mahavistaarai.ui.screens.splash

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import java.util.Locale

class SplashScreenActivity : AppCompatActivity() {

    private var farmerId: Int = 0
    private lateinit var languageToLoad: String
    private lateinit var appVersionText:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@SplashScreenActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        setContentView(R.layout.activity_splash_screen)

        // Set Language Configuration
        val languageToLoad = if (AppSettings.getLanguage(this) == "1") "en" else "mr"
        Locale.setDefault(Locale(languageToLoad))
        resources.updateConfiguration(
            Configuration().apply { locale = Locale(languageToLoad) },
            resources.displayMetrics
        )

        appVersionText = findViewById(R.id.appVersionText)
        appVersionText.text = "${getString(R.string.app_version)} ${LocalCustom.getVersionName(this)}"
        // Get Farmer ID
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)

        // Navigate to the appropriate screen after delay
        Handler(Looper.getMainLooper()).postDelayed({
            val targetActivity =
                if (farmerId > 0) DashboardScreen::class.java else LoginScreen::class.java
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