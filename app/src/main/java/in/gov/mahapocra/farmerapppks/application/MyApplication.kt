package `in`.gov.mahapocra.farmerapppks.application

import android.app.Application
import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.util.ForceUpdateChecker
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this // Initialize instance here

        AppSettings.getInstance().initAppSettings(AppConstants.kSHARED_PREF)
        DebugLog.getInstance().initLoggingEnabled(true)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val PACKAGE_NAME = packageName
        val versionName = BuildConfig.VERSION_NAME

        // Set in-app defaults
        val remoteConfigDefaults: MutableMap<String, Any> = HashMap()
        remoteConfigDefaults[ForceUpdateChecker.KEY_UPDATE_REQUIRED] = false
        remoteConfigDefaults[ForceUpdateChecker.KEY_CURRENT_VERSION] = versionName
        remoteConfigDefaults[ForceUpdateChecker.KEY_UPDATE_URL] =
            "https://play.google.com/store/apps/details?id=$PACKAGE_NAME"
        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults)

        firebaseRemoteConfig.fetch(60) // Fetch every minute
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "Remote config is fetched.")
                    firebaseRemoteConfig.activate()
                }
            }
    }

    companion object {
        var instance: MyApplication? = null
            private set
    }
}
