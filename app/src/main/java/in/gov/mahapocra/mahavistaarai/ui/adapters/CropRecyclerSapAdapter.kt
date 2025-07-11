package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject

class CropRecyclerSapAdapter(
    private val jsonArray: JSONArray
) : RecyclerView.Adapter<CropRecyclerSapAdapter.ViewHolder>() {

    // ViewHolder class
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cropNameETLTextView: TextView = itemView.findViewById(R.id.cropNameETLTextView)
        val cropAdvisoryETLTextView: TextView = itemView.findViewById(R.id.cropAdvisoryETLTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_etl, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jsonObject = jsonArray[position] as JSONObject
        val cropNameETL = jsonObject.optString("crop_name_regional")
        val cropAdvisoryETL = jsonObject.optString("cropsap_advisory")
        holder.cropNameETLTextView.text = cropNameETL
        holder.cropAdvisoryETLTextView.text = cropAdvisoryETL
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }
}
