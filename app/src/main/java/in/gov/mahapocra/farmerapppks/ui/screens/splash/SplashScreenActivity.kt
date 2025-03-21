package `in`.gov.mahapocra.farmerapppks.ui.screens.splash

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import java.util.Locale

class SplashScreenActivity : AppCompatActivity() {

    private var farmerId: Int = 0
    private var versionName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Set Language Configuration
        val languageToLoad = if (AppSettings.getLanguage(this) == "1") "en" else "hi"
        Locale.setDefault(Locale(languageToLoad))
        resources.updateConfiguration(
            Configuration().apply { locale = Locale(languageToLoad) },
            resources.displayMetrics
        )

        // Get Version Name
        versionName = packageManager.getPackageInfo(packageName, 0)?.versionName

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
}