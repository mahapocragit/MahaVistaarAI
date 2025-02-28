package `in`.gov.mahapocra.farmerapppks.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.AppPreferenceManager
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.TemperatureAdapter
import `in`.gov.mahapocra.farmerapppks.adapter.ViewPagerAdapter
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityWeatherHomeTempBinding
import `in`.gov.mahapocra.farmerapppks.data.ResponseModel
import `in`.gov.mahapocra.farmerapppks.ui.weather.Item
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherTempActivity : AppCompatActivity() {

    lateinit var binding: ActivityWeatherHomeTempBinding
    private lateinit var recyclerAdapter: TemperatureAdapter
    private lateinit var itemList: List<Item>
    private lateinit var jsonArrayForecast: JSONArray
    private lateinit var jsonArrayPrevious: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherHomeTempBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        binding.relativeLayoutTopBar.relativeLayoutToolbar.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.gradient_top_figma
            )
        )
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val weatherResponse = AppPreferenceManager(this).getString(AppConstants.WEATHER_RESPONSE)
        val talukaName: String = AppSettings.getInstance().getSavedValue(this, AppConstants.uTALUKA)
        binding.weatherTalukaTV.text = talukaName
        if (!weatherResponse.equals(AppConstants.WEATHER_RESPONSE)) {
            val jSONObject = JSONObject(weatherResponse)
            val response =
                ResponseModel(jSONObject)
            if (response.status) {
                val advisory = jSONObject.optString("AgroMetAdvisory")
                jsonArrayForecast = jSONObject.optJSONArray("Forcast")
                jsonArrayPrevious = jSONObject.optJSONArray("Previous")
                val temperatureObject = jSONObject.optJSONObject("Temperature")
                val tempMin: Int = temperatureObject.optInt("min")
                val tempMax: Int = temperatureObject.optInt("max")
                val temperature = "$tempMin°C / $tempMax°C"
                binding.temperatureTextView.text = temperature
                binding.tvAgroMetAdvisory.text = advisory
            }
        }

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = "Weather"

        binding.previousSevenDayTV.setOnClickListener {
            binding.previousSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(
                        this@WeatherTempActivity,
                        R.drawable.shape_right_green
                    )
                setTextColor(Color.WHITE)
            }
            binding.nextSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(this@WeatherTempActivity, R.drawable.shape_left_white)
                setTextColor(Color.BLACK)
            }
            setRecyclerViewUsingArray(jsonArrayPrevious)
            recyclerAdapter.notifyDataSetChanged()
        }

        binding.timestampTV.text = getFormattedTimestamp()

        binding.nextSevenDayTV.setOnClickListener {
            binding.nextSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(
                        this@WeatherTempActivity,
                        R.drawable.shape_left
                    )
                setTextColor(Color.WHITE)
            }
            binding.previousSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(this@WeatherTempActivity, R.drawable.shape_right)
                setTextColor(Color.BLACK)
            }
            setRecyclerViewUsingArray(jsonArrayForecast)
            recyclerAdapter.notifyDataSetChanged()
        }

        // Connect TabLayout and ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Temp"
                1 -> tab.text = "Rain"
                2 -> tab.text = "Humidity"
                3 -> tab.text = "Wind"
            }
        }.attach()


        setRecyclerViewUsingArray(jsonArrayForecast)
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun setRecyclerViewUsingArray(jsonArray:JSONArray){
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Set adapter
        recyclerAdapter = TemperatureAdapter(jsonArray)
        binding.recyclerView.adapter = recyclerAdapter
    }

    fun getFormattedTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy | HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}