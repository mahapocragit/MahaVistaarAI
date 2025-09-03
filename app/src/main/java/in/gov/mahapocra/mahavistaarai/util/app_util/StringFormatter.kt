package `in`.gov.mahapocra.mahavistaarai.util.app_util

import org.json.JSONArray
import org.json.JSONObject

object StringFormatter {

    fun extractCropObjects(jsonArray: JSONArray): List<JSONObject> {
        val list = mutableListOf<JSONObject>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.optJSONObject(i)
            if (obj != null) list.add(obj)
        }
        return list
    }
}