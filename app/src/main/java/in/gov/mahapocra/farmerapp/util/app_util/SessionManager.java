package in.gov.mahapocra.farmerapp.util.app_util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

        private static final String PREF_NAME = "SessionPrefs";
        private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
        private SharedPreferences preferences;
        private SharedPreferences.Editor editor;

        public SessionManager(Context context) {
            preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }

        public void setLoggedIn(boolean isLoggedIn) {
            editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
            editor.apply();
        }

        public boolean isLoggedIn() {
            return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
        }
    }

