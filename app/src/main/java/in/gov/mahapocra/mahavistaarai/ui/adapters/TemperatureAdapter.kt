package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class TemperatureAdapter(private val jsonArray: JSONArray) :
    RecyclerView.Adapter<TemperatureAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val weatherCardView: CardView = itemView.findViewById(R.id.weatherCardView)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureTextView)
        val windSpeedTV: TextView = itemView.findViewById(R.id.windSpeedTV)
        val humidityTV: TextView = itemView.findViewById(R.id.humidityTV)
        val rainTV: TextView = itemView.findViewById(R.id.rainTV)
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
        val humidity = item.optString("humidity_1")?:""
        val rain = item.optString("rain")?:""
        val windSpeedTemp = "${holder.weatherCardView.context.getString(R.string.wind)}: $windSpeed km/h"
        val humidityTemp = "${holder.weatherCardView.context.getString(R.string.humidity)}: $humidity %"
        val rainTemp = "${holder.weatherCardView.context.getString(R.string.rain)}: $rain %"
        val temperature = "$minTemp°C / $maxTemp°C"
        try {
            holder.titleTextView.text = item.getString("date")
        }catch (e:Exception){
            holder.titleTextView.text = formatDate(item.getString("for_date"))
        }
        holder.windSpeedTV.text = windSpeedTemp
        holder.humidityTV.text = humidityTemp
        holder.rainTV.text = rainTemp
        holder.temperatureTextView.text = temperature
        holder.imageView.setImageResource(R.drawable.weather_ic)
    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val date = inputFormat.parse(inputDate) // Parse input string to Date
        return outputFormat.format(date!!) // Convert Date to output string
    }
}