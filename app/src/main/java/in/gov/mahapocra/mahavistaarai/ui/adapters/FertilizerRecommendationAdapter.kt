package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject

class FertilizerRecommendationAdapter (private val jsonArray: JSONArray) : RecyclerView.Adapter<FertilizerRecommendationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_fertilizer_recommendation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cropVarietyTextView: TextView = itemView.findViewById(R.id.cropVarietyTextView)
        val organicFertilizerTextView: TextView = itemView.findViewById(R.id.organicFertilizerTextView)
        val bioFertilizerTextView: TextView = itemView.findViewById(R.id.bioFertilizerTextView)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: JSONObject = jsonArray.getJSONObject(position)
        val cropVariety = item.optString("crop_variety") ?: ""
        val organicFertilizer = item.optString("organic_fert") ?: ""
        val bioFertilizer = item.optString("bio_fert") ?: ""
        val quantity = item.optString("bio_fert_qty") ?: ""
        holder.cropVarietyTextView.text = cropVariety
        holder.organicFertilizerTextView.text = organicFertilizer
        holder.bioFertilizerTextView.text = bioFertilizer
        holder.quantityTextView.text = quantity
    }
}