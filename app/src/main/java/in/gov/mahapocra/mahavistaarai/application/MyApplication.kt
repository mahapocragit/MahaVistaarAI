package `in`.gov.mahapocra.mahavistaarai.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.androidnetworking.AndroidNetworking
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.TokenSessionManager
import org.osmdroid.config.Configuration
import java.io.File

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        TokenSessionManager.init(this)
        initAppSettings()
        initNetworking()
        initFirebase()
        initNotificationChannel()
        initOsmdroid()
        initRemoteConfig()
    }

    // -------------------- Initializers --------------------

    private fun initAppSettings() {
        AppSettings.getInstance().initAppSettings(AppConstants.kSHARED_PREF)
        DebugLog.getInstance().initLoggingEnabled(true)
    }

    private fun initNetworking() {
        AndroidNetworking.initialize(applicationContext)
    }
    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel_id"
            val channelName = "Default Channel"

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for FCM notifications"
                    enableVibration(true)
                    lightColor = Color.GREEN
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun initOsmdroid() {
        Configuration.getInstance().osmdroidBasePath = File(cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidTileCache = File(cacheDir, "osmdroid/tiles")
    }

    private fun initRemoteConfig() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setDefaultsAsync(emptyMap())

        remoteConfig.fetch(60)
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    remoteConfig.activate()
                }
            }
    }

    // -------------------- Locale --------------------

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(configureLocale(base, "fr"))
    }

    // -------------------- Singleton --------------------

    companion object {
        @Volatile
        lateinit var instance: MyApplication   // ✅ NON-NULL
            private set
    }
}
