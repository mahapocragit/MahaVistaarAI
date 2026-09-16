package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.WeatherFarmAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.WeatherFarmModel

class HourlyWeatherFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var weatherFarmAdapter: WeatherFarmAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_hourly_weather,
            container,
            false
        )

        recyclerView = view.findViewById(R.id.weatherFarmRecyclerView)
        val weatherList = List(24) { hour ->
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }

            val amPm = if (hour < 12) "AM" else "PM"

            WeatherFarmModel(
                rainfall = "${(0..100).random()}%",
                time = String.format("%02d:00 %s", displayHour, amPm),
                humidity = "${(40..95).random()}%",
                wind = "${(5..30).random()} km/r"
            )
        }

        weatherFarmAdapter = WeatherFarmAdapter(
            weatherList
        ) { selectedItem ->

            // Item clicked
//            submitSelectedItem(selectedItem)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = weatherFarmAdapter
            setHasFixedSize(true)
        }

        return view
    }
}