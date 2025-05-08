package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.TemperatureAdapter.ViewHolder
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
        val ratingTextView: TextView = itemView.findViewById(R.id.ratingTextView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: JSONObject = jsonArray.getJSONObject(position)
        val testparamname = item.optString("testparamname") ?: ""
        val testvalue = item.optString("testvalue") ?: ""
        val unit = item.optString("unit") ?: ""
        val rating = item.optString("rating") ?: ""
        holder.testParameterTextView.text = testparamname
        holder.valueTextView.text = testvalue
        holder.unitTextView.text = unit
        holder.ratingTextView.text = rating
    }
}