package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import org.json.JSONObject

class FirstFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WindAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first, container, false)

        // Initialize RecyclerView and set LayoutManager
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        val dataFromWeather = AppPreferenceManager(requireContext()).getString("WEATHER_HOURLY_DATA_24")
        val jsonObject = JSONObject(dataFromWeather)
        val jsonArray = jsonObject.optJSONArray("data")
        // Set adapter
        adapter = jsonArray?.let { WindAdapter(it, "temp") }!!
        recyclerView.adapter = adapter

        return view
    }

}