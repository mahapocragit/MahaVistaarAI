package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.getRatingImageResource
import org.json.JSONArray
import org.json.JSONObject

class SoilTestResultAdapter(private val jsonArray: JSONArray) : RecyclerView.Adapter<SoilTestResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_soil_health_result, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val testParameterTextView: TextView = itemView.findViewById(R.id.testParameterTextView)
        val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)
        val unitTextView: TextView = itemView.findViewById(R.id.unitTextView)
        val ratingImageView: ImageView = itemView.findViewById(R.id.ratingImageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: JSONObject = jsonArray.getJSONObject(position)
        val testParameterName = item.optString("name") ?: ""
        val testValue = item.optString("value") ?: ""
        val unit = item.optString("unit") ?: ""
        val rating = item.optString("rating") ?: ""
        holder.testParameterTextView.text = testParameterName
        holder.valueTextView.text = testValue
        holder.unitTextView.text = unit
        holder.ratingImageView.setImageResource(getRatingImageResource(rating))
    }

}