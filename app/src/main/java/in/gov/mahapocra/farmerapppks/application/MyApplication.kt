package in.gov.mahapocra.farmerapppks.application;

import android.app.Application;
import android.util.Log;

import com.google.firebase.BuildConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.mahapocra.farmerapppks.util.app_util.AppConstants;
import in.gov.mahapocra.farmerapppks.util.ForceUpdateChecker;

import static android.content.ContentValues.TAG;

public class AppDelegate extends Application {

    private static AppDelegate mInstance;

    public static AppDelegate getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this; // Initialize instance here

        AppSettings.getInstance().initAppSettings(AppConstants.kSHARED_PREF);
        DebugLog.getInstance().initLoggingEnabled(true);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String PACKAGE_NAME = getPackageName();
        String versionName = BuildConfig.VERSION_NAME;

        // Set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap<>();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, versionName);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL, "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME);
        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);

        firebaseRemoteConfig.fetch(60) // Fetch every minute
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Remote config is fetched.");
                        firebaseRemoteConfig.activate();
                    }
                });
    }
}
