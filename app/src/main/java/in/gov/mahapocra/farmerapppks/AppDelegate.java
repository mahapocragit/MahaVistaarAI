package in.gov.mahapocra.farmerapppks;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.mahapocra.farmerapppks.app_util.AppConstants;
import in.gov.mahapocra.farmerapppks.webServices.ForceUpdateChecker;

import static android.content.ContentValues.TAG;

public class AppDelegate extends Application {
   // private final String DATABASE_NAME = "pocra_ffs_off";
    //private AppDatabase appDatabase;
    private static Context mContext;

    private static AppDelegate mInstance = null;
    public static AppDelegate getInstance(Context context) {
        if (mInstance == null) {
            mContext = context;
            mInstance = new AppDelegate();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppSettings.getInstance().initAppSettings(AppConstants.kSHARED_PREF);
        DebugLog.getInstance().initLoggingEnabled(true);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String PACKAGE_NAME = getPackageName();
        String versionName = BuildConfig.VERSION_NAME;
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, versionName);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL, "https://play.google.com/store/apps/details?id="+ PACKAGE_NAME);
        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            Log.d("1213234234",firebaseRemoteConfig.getString("force_update_current_version"));
                            firebaseRemoteConfig.activate();

                        }
                    }
                });
    }



    }


