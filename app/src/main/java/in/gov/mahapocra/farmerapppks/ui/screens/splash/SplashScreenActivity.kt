package `in`.gov.mahapocra.farmerapppks.ui.screens.splash

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppSession
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
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