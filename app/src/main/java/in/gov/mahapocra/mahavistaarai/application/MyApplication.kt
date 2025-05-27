package `in`.gov.mahapocra.mahavistaarai.application

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import org.osmdroid.config.Configuration
import java.io.File

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this // Initialize instance here

        AppSettings.getInstance().initAppSettings(AppConstants.kSHARED_PREF)
        DebugLog.getInstance().initLoggingEnabled(true)
        AndroidNetworking.initialize(applicationContext)
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Set osmdroid cache directory
        Configuration.getInstance().osmdroidBasePath = File(cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidTileCache = File(cacheDir, "osmdroid/tiles")

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        // Set in-app defaults
        val remoteConfigDefaults: MutableMap<String, Any> = HashMap()
        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults)

        firebaseRemoteConfig.fetch(60) // Fetch every minute
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    firebaseRemoteConfig.activate()
                }
            }
    }

    companion object {
        var instance: MyApplication? = null
            private set
    }
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(configureLocale(base, "fr"))
    }

}
