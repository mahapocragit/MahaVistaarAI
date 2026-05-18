package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject

object DashboardCache {

    private const val PREF_NAME = "dashboard_pref"
    private const val KEY_DASHBOARD = "dashboard_response"

    fun saveDashboard(context: Context, jsonObject: JsonObject) {

        val pref = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        pref.edit()
            .putString(KEY_DASHBOARD, jsonObject.toString())
            .apply()
    }

    fun getDashboard(context: Context): JsonObject? {

        val pref = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val data = pref.getString(KEY_DASHBOARD, null)

        return if (data != null) {
            Gson().fromJson(data, JsonObject::class.java)
        } else {
            null
        }
    }
}