package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.convertDateFormat
import org.json.JSONArray
import org.json.JSONObject

class CropRecyclerSapAdapter(
    private val jsonArray: JSONArray
) : RecyclerView.Adapter<CropRecyclerSapAdapter.ViewHolder>() {

    // ViewHolder class
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cropNameETLTextView: TextView = itemView.findViewById(R.id.cropNameETLTextView)
        val cropAdvisoryETLTextView: TextView = itemView.findViewById(R.id.cropAdvisoryETLTextView)
        val etlAdvisoryDateTextView: TextView = itemView.findViewById(R.id.etlAdvisoryDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_etl, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = getCurrentLanguage(holder.itemView.context)
        val jsonObject = jsonArray[position] as JSONObject
        val cropNameETL = jsonObject.optString("crop_name")
        val cropNameMarathi = jsonObject.optString("crop_name_regional")
        val cropAdvisoryETL = jsonObject.optString("cropsap_advisory")
        val cropSapAdvisoryEng = jsonObject.optString("cropsap_advisory_eng")
        val etlAdvisoryDate = jsonObject.optString("cropsap_advisory_date")
        holder.cropNameETLTextView.text = if (language!="en") cropNameMarathi else cropNameETL
        holder.cropAdvisoryETLTextView.text = if (language!="en") cropAdvisoryETL else cropSapAdvisoryEng
        holder.etlAdvisoryDateTextView.text =
            buildString {
                append(holder.cropNameETLTextView.context.getString(R.string.date))
                append(" ")
                append(convertDateFormat(etlAdvisoryDate))
            }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    fun getCurrentLanguage(context: Context): String{
        var languageToLoad = "mr"
        if (AppSettings.getLanguage(context).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        return languageToLoad
    }
}
