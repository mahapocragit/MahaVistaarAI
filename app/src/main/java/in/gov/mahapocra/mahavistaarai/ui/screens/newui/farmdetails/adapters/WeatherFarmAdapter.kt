package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R

class WeatherFarmAdapter(
    private val list: List<WeatherFarmModel>,
    private val onItemClick: (WeatherFarmModel) -> Unit
) : RecyclerView.Adapter<WeatherFarmAdapter.AdvisoryViewHolder>() {

    inner class AdvisoryViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val weatherTimeText: TextView = itemView.findViewById(R.id.weatherTimeText)
        private val rainPercentageText: TextView = itemView.findViewById(R.id.rainPercentageText)
        private val humidityPercentageText: TextView = itemView.findViewById(R.id.humidityPercentageText)
        private val windSpeedText: TextView = itemView.findViewById(R.id.windSpeedText)

        fun bind(item: WeatherFarmModel) {

            weatherTimeText.text = item.time
            rainPercentageText.text = item.rainfall
            humidityPercentageText.text = item.humidity
            windSpeedText.text = item.wind

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdvisoryViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_farm_weather, parent, false)

        return AdvisoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AdvisoryViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}