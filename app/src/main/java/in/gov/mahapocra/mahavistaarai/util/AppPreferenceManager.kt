package `in`.gov.mahapocra.mahavistaarai.util

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

    fun saveBoolean(key:String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply() // or editor.commit();
    }

    fun getBoolean(key:String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getString(key:String): String? {
        return sharedPreferences.getString(key, "default_value")
    }

    fun saveInt(key:String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply() // or editor.commit();
    }

    fun getInt(key:String): Int {
        return sharedPreferences.getInt(key, 0)
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