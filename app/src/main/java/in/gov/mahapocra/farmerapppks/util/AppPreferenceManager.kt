package `in`.gov.mahapocra.farmerapppks.util

import android.content.Context


class AppPreferenceManager(val context: Context) {

    private val preferenceName: String = "MyAppPreferences"
    private val sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

    // Method to save a string to SharedPreferences
    fun saveString(key:String, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply() // or editor.commit();
    }

    fun getString(key:String): String? {
        return sharedPreferences.getString(key, "default_value")
    }

    fun clearPreference(key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply() // or editor.commit();
    }

    fun clearAll() {
        val editor = sharedPreferences.edit()
        editor.clear() // Clear all data
        editor.apply() // or editor.commit();
    }
}