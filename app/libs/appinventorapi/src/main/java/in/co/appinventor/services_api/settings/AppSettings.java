package in.co.appinventor.services_api.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* renamed from: in.co.appinventor.services_api.settings.AppSettings */
public class AppSettings {
    public static final String PREF_BANNER_DATA = "PREF_BANNER_DATA";
    public static final String PREF_CHANNEL_DATA = "PREF_CHANNEL_DATA";
    public static final String PREF_CHANNEL_USER_DATA = "PREF_CHANNEL_USER_DATA";
    public static final String PREF_DEVICE_TOKEN = "PREF_DEVICE_TOKEN";
    public static final String PREF_GCM_TOKEN = "PREF_GCM_TOKEN";
    public static final String PREF_IS_LOGIN_DONE = "PREF_IS_LOGIN_DONE";
    public static final String PREF_LOGIN_DATA = "PREF_LOGIN_DATA";
    public static final String PREF_PAGE_TOKEN = "PREF_PAGE_TOKEN";
    public static final String PREF_SOCIAL_DATA = "PREF_SOCIAL_DATA";
    public static final String PREF_SPLASH_BANNER_DATA = "PREF_SPLASH_BANNER_DATA";
    public static final String PREF_USER_ACTION_TYPE = "PREF_USER_ACTION_TYPE";
    public static final String PREF_USER_ID = "PREF_USER_ID";
    public static final String PREF_USER_MOBILE = "PREF_USER_MOBILE";
    public static final String PREF_USER_PROFILE_PIC = "PREF_USER_PROFILE_PIC";
    public static final String PREF_USER_PROFILE_PIC500 = "PREF_USER_PROFILE_PIC500";
    public static final String PREF_USER_PROFILE_PIC80 = "PREF_USER_PROFILE_PIC80";
    private static final AppSettings ourInstance = new AppSettings();
    private static SharedPreferences prefs = null;
    private String APP_SHARED_PREFERENCE_NAME = "in.co.appinventor";

    public static final String USER_NAME = "USER_NAME";
    public static final String USER_MOBILE = "USER_MOBILE";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_FAAPRegistrationID = "USER_FAAPRegistrationID";
    public static final String USER_DIST = "USER_DIST";
    public static final String USER_DISTId = "USER_DISTId";
    public static final String USER_TALUKA = "USER_TALUKA";
    public static final String USER_TALUKAID = "USER_TALUKAID";

    public static String CHOOSE_LANGUAGE = "ChooseLanguage";
    public static String LANGUAGE_ENG = "en";
    public static String LANGUAGE_MAR = "mr";
    public static String LANGUAGE_ENG_ID = "1"; //--- for english
    public static String LANGUAGE_MAR_ID = "2"; //--- for marathi
    private static String PREFERENCE_NAME = "UserInfo";


    private AppSettings() {
    }
    public static AppSettings getInstance() {
        return ourInstance;
    }
    public void initAppSettings(String sharedPreferenceName) {
        this.APP_SHARED_PREFERENCE_NAME = "in.co.appinventor" + sharedPreferenceName + "sharedpreference";
    }

    public String getValue(Context context, String key, String defaultValue) {
        isAppSettingInit();
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        return prefs.getString(key, defaultValue);
    }

    public SharedPreferences getAppSharedPreference(Context context) {
        isAppSettingInit();
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        return prefs;
    }

    //Language
    public static void setLanguage(Context c, String lang)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Language", lang);
        editor.commit();
    }

    public static String getLanguage(Context c)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getString("Language", "2");
    }

    public void clearAppSharedData(Context context) {
        isAppSettingInit();
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        prefs.edit().clear().commit();
    }

    public void setValue(Context context, String key, String value) {
        isAppSettingInit();
        if (value != null && context != null) {
            prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }
    public int getIntValue(Context context, String key, int defaultValue) {
        isAppSettingInit();
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        return prefs.getInt(key, defaultValue);
    }
    public void setIntValue(Context context, String key, int value) {
        isAppSettingInit();
        if (context != null) {
            prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    public void clearIntValue(Context context, String key) {
        isAppSettingInit();
        if (context != null) {
            prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(key, 0);  // Set to default "cleared" value
            editor.apply();
        }
    }

    public long getLongValue(Context context, String key, long defaultValue) {
        isAppSettingInit();
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        return prefs.getLong(key, defaultValue);
    }

    public void setLongValue(Context context, String key, long value) {
        isAppSettingInit();
        if (context != null) {
            prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(key, value);
            editor.apply();
        }
    }

    public float getFloatValue(Context context, String key, float defaultValue) {
        isAppSettingInit();
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        return prefs.getFloat(key, defaultValue);
    }

    public void setFloatValue(Context context, String key, float value) {
        isAppSettingInit();
        if (context != null) {
            prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(key, value);
            editor.apply();
        }
    }

    public boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        isAppSettingInit();
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        return prefs.getBoolean(key, defaultValue);
    }

    public void setBooleanValue(Context context, String key, boolean value) {
        isAppSettingInit();
        if (context != null) {
            prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public void setList(Context context, String key, List<Object> value) {
        isAppSettingInit();
        if (context != null) {
            prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, new Gson().toJson(value));
            editor.apply();
        }
    }

    public List<Object> getList(Context context, String key) {
        isAppSettingInit();
        List<Object> productFromShared = new ArrayList<>();
        if (context == null) {
            return productFromShared;
        }
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        Gson gson = new Gson();
        String jsonPreferences = prefs.getString(key, "");
        if (jsonPreferences.length() > 0) {
            return (List) gson.fromJson(jsonPreferences, new TypeToken<List<Object>>() {
            }.getType());
        }
        return productFromShared;
    }

    public void setListDates(Context context, String key, List<Date> value) {
        isAppSettingInit();
        if (context != null) {
            prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, new Gson().toJson(value));
            editor.apply();
        }
    }

    public List<Date> getListDates(Context context, String key) {
        isAppSettingInit();
        List<Date> productFromShared = new ArrayList<>();
        if (context == null) {
            return productFromShared;
        }
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        Gson gson = new Gson();
        String jsonPreferences = prefs.getString(key, "");
        if (jsonPreferences.length() > 0) {
            return (List) gson.fromJson(jsonPreferences, new TypeToken<List<Date>>() {
            }.getType());
        }
        return productFromShared;
    }

    private void isAppSettingInit() {
        if (this.APP_SHARED_PREFERENCE_NAME.equalsIgnoreCase("in.co.appinventor")) {
            try {
                throw new Exception("Please use initAppSettings in application class, Before using the method.  Add your app unique shared preference name");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getSavedValue(Context context, String key) {
        isAppSettingInit();
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        return prefs.getString(key, key);
    }

    public Set<String> getAutoCompletionData(Context context, String key) {
        isAppSettingInit();
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        return prefs.getStringSet(key, new HashSet());
    }

    public void setAutoCompletionData(Context context, String key, Set<String> history) {
        isAppSettingInit();
        prefs = context.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, history);
        editor.apply();
    }

    public String getUserId(Context mContext) {
        isAppSettingInit();
        prefs = mContext.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        return prefs.getString(PREF_USER_ID, PREF_USER_ID);
    }

    public String getUserMobile(Context mContext) {
        isAppSettingInit();
        prefs = mContext.getSharedPreferences(this.APP_SHARED_PREFERENCE_NAME, 0);
        return prefs.getString(PREF_USER_MOBILE, PREF_USER_MOBILE);
    }
}
