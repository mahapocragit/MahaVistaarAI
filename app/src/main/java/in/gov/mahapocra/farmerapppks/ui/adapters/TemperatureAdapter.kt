package `in`.gov.mahapocra.farmerapppks.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.farmerapppks.R
import org.json.JSONArray
import org.json.JSONObject

class TemperatureAdapter(private val jsonArray: JSONArray) :
    RecyclerView.Adapter<TemperatureAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureTextView)
        val windSpeedTV: TextView = itemView.findViewById(R.id.windSpeedTV)
        val imageView: ImageView = itemView.findViewById(R.id.imageView11)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_temp, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: JSONObject = jsonArray.getJSONObject(position)
        val minTemp = item.optString("temp_min")?:""
        val maxTemp = item.optString("temp_max")?:""
        val windSpeed = item.optString("wind_speed")?:""
        val windSpeedTemp = "$windSpeed km/h"
        val temperature = "$minTemp°C / $maxTemp°C"
        holder.titleTextView.text = item.getString("date")
        holder.windSpeedTV.text = windSpeedTemp
        holder.temperatureTextView.text = temperature
        holder.imageView.setImageResource(R.drawable.weather_ic)
    }
}