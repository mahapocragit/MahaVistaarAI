package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.TemperatureAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ViewPagerAdapter
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityWeatherHomeTempBinding
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherActivity : AppCompatActivity(), ApiCallbackCode, OnMultiRecyclerItemClickListener {

    lateinit var binding: ActivityWeatherHomeTempBinding
    private lateinit var recyclerAdapter: TemperatureAdapter
    private var vinCode : Int = 0
    private lateinit var jsonArrayForecast: JSONArray
    private lateinit var jsonArrayPrevious: JSONArray
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@WeatherActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        configureLocale(baseContext, languageToLoad)
        binding = ActivityWeatherHomeTempBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.tabLayout.visibility = View.GONE
        binding.viewPager.visibility = View.GONE

        val weatherResponse = AppPreferenceManager(this).getString(AppConstants.WEATHER_RESPONSE)
        val talukaName: String = AppSettings.getInstance().getSavedValue(this, AppConstants.uTALUKA)
        binding.weatherTalukaTV.text = talukaName
        if (!weatherResponse.equals(AppConstants.WEATHER_RESPONSE)) {
            val jSONObject = JSONObject(weatherResponse)
            val response =
                ResponseModel(
                    jSONObject
                )
            if (response.status) {
                val advisory = jSONObject.optString("AgroMetAdvisory")
                jsonArrayForecast = jSONObject.optJSONArray("Forcast")
                jsonArrayPrevious = jSONObject.optJSONArray("Previous")
                val temperatureObject = jSONObject.optJSONObject("Temperature")
                val tempMin: String = temperatureObject.optString("min")
                val tempMax: String = temperatureObject.optString("max")
                val rainfall: String = temperatureObject.optString("rainfall")
                val humidity: String = temperatureObject.optString("humidity")
                val wind: String = temperatureObject.optString("wind")
                binding.tvAgroMetAdvisory.text = advisory
                val temperature = "$tempMin°C / $tempMax°C"
                binding.temperatureTextView.text = temperature
                binding.rainTextView.text = "$rainfall mm"
                binding.humidityTextView.text = "$humidity %"
                binding.windTextView.text = "$wind Km/h"
            }
        }

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.weather_title)

        binding.previousSevenDayTV.setOnClickListener {
            binding.tabLayout.visibility = View.GONE
            binding.viewPager.visibility = View.GONE
            binding.previousSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(
                        this@WeatherActivity,
                        R.drawable.shape_right_green
                    )
                setTextColor(Color.WHITE)
            }
            binding.nextSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(this@WeatherActivity, R.drawable.shape_left_white)
                setTextColor(Color.BLACK)
            }
            makeAPICallForPreviousData()
            recyclerAdapter.notifyDataSetChanged()
        }

        binding.timestampTV.text = getFormattedTimestamp()

        binding.nextSevenDayTV.setOnClickListener {
            binding.tabLayout.visibility = View.GONE
            binding.viewPager.visibility = View.GONE
            binding.nextSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(
                        this@WeatherActivity,
                        R.drawable.shape_left
                    )
                setTextColor(Color.WHITE)
            }
            binding.previousSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(this@WeatherActivity, R.drawable.shape_right)
                setTextColor(Color.BLACK)
            }
            setRecyclerViewUsingArray(jsonArrayForecast)
            recyclerAdapter.notifyDataSetChanged()
        }

        setRecyclerViewUsingArray(jsonArrayForecast)
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun makeAPICallForPreviousData() {
        vinCode = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        val jsonObject = JSONObject()
        jsonObject.put("for_date", getCurrentDate()) // "2024-10-18"
        val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
        val api = AppInventorApi(this, APIServices.GIS, "", AppString(this).getkMSG_WAIT(), true)
        val apiRequest = api.getRetrofitInstance().create(APIRequest::class.java)
        val responseCall: Call<JsonObject> = apiRequest.getPreviousDates(requestBody)
        api.postRequest(responseCall, this, 1)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun setRecyclerViewUsingArray(jsonArray:JSONArray){
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Set adapter
        recyclerAdapter = TemperatureAdapter(jsonArray, this)
        binding.recyclerView.adapter = recyclerAdapter
    }

    private fun getFormattedTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy | HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1){
            val jsonArray = jSONObject?.optJSONArray("data")
            jsonArray?.let { setRecyclerViewUsingArray(it) }
        }else if (i == 2){
            val jsonArray = jSONObject?.optJSONArray("data")
            Log.d("TAGGER", "onResponse: $jsonArray")
            binding
            if (jsonArray!=null){
                binding.tabLayout.visibility = View.VISIBLE
                binding.viewPager.visibility = View.VISIBLE
                AppPreferenceManager(this).saveString( "WEATHER_HOURLY_DATA_24", jSONObject.toString())
                val viewPagerAdapter = ViewPagerAdapter(this)
                binding.viewPager.adapter = viewPagerAdapter
                binding.viewPager.isUserInputEnabled = false
                // Connect TabLayout and ViewPager2
                TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = "Temp"
                        1 -> tab.text = "Rain"
                        2 -> tab.text = "Humidity"
                        3 -> tab.text = "Wind"
                    }
                }.attach()
            }else{
                binding.tabLayout.visibility = View.GONE
                binding.viewPager.visibility = View.GONE
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (obj != null) {
            val jSONObject = obj as JSONObject
            val date = jSONObject.optString("for_date")
            val jsonObject = JSONObject()
            jsonObject.put("for_date", date) //  "2024-10-18"
            jsonObject.put("vincode", vinCode) //525878
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(this, APIServices.GIS, "",
                AppString(this).getkMSG_WAIT(), true)
            val apiRequest = api.getRetrofitInstance().create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getHourlyData(requestBody)
            api.postRequest(responseCall, this, 2)
        }
    }
}