package `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.farmerapp.R
import org.json.JSONArray
import org.json.JSONObject

class WindAdapter(private val jsonArray: JSONArray, private val actName:String) : RecyclerView.Adapter<WindAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = jsonArray[position] as JSONObject
        holder.titleTextView.text = item.optString("for_hour")
        when(actName){
            "temp" -> {
                holder.imageView.setImageResource(R.drawable.weather_ic)
                val temp = item.optInt("temp")
                val unit = item.optString("temp_unit")
                holder.valueForWeather.text = "$temp $unit"
            }
            "rain" -> {
                holder.imageView.setImageResource(R.drawable.rain_ic)
                val rainfall = item.optInt("rain")
                val unit = item.optString("rain_unit")
                holder.valueForWeather.text = "$rainfall $unit"
            }
            "humidity" -> {
                holder.imageView.setImageResource(R.drawable.humidity_ic)
                val humidity = item.optDouble("humidity")
                val unit = item.optString("humidity_unit")
                holder.valueForWeather.text = "$humidity $unit"
            }
            "wind" -> {
                holder.imageView.setImageResource(R.drawable.wind_ic)
                val wind = item.optDouble("wind")
                val unit = item.optString("wind_unit")
                holder.valueForWeather.text = "$wind $unit"
            }
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val valueForWeather: TextView = itemView.findViewById(R.id.valueForWeather)
        val imageView: ImageView = itemView.findViewById(R.id.imageView11)
    }
}
