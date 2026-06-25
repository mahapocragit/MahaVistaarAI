package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityWeatherHomeTempBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.TemperatureAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.WEATHER_POINT
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AppHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DraggableTouchListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.FarmerHelper.containsFarmerId
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherHomeTempBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private var recyclerAdapter = TemperatureAdapter(JSONArray())
    private var jsonArrayForecast = JSONArray()
    private var jsonArrayPrevious = JSONArray()
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@WeatherActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityWeatherHomeTempBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        setUpToolbar()
        observeResponse()
        setUpListeners()
        AnimationHelper.shrinkLeftToCenter(binding.bubbleIconImageView)
        lifecycleScope.launch {
            delay(5000) // 5 seconds
            binding.bubbleIconImageView.animate()
                .alpha(0f)
                .setDuration(500) // animation duration in ms
                .withEndAction {
                    binding.bubbleIconImageView.visibility = View.GONE
                    binding.bubbleIconImageView.alpha = 1f // reset alpha in case you show it again
                }
                .start()
        }
        if (containsFarmerId(this)) {
            leaderboardViewModel.updateUserPoints(this, WEATHER_POINT)
        }
        val talukaCode =
            CryptoHelper.decryptField(AppPreferenceManager(this).getString(AppConstants.TALUKA_CODE))
                ?: "0"
        farmerViewModel.fetchWeatherDetails(talukaCode.toInt(), languageToLoad)
        binding.tabLayout.visibility = View.GONE
        binding.viewPager.visibility = View.GONE
        binding.timestampTV.text = getFormattedTimestamp()
    }

    private fun setUpListeners() {
        binding.chatbotIcon.setOnTouchListener(DraggableTouchListener {
            startActivity(Intent(this@WeatherActivity, ChatbotActivity::class.java))
        })
        binding.weatherTalukaTV.text =
            CryptoHelper.decryptField(AppPreferenceManager(this).getString(AppConstants.TALUKA_NAME))
                .toString()
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
            setRecyclerViewUsingArray(jsonArrayPrevious)
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun setUpToolbar() {
        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.weather_title)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            AppHelper(this).redirectToHome()
        }
        binding.relativeLayoutTopBar.relativeLayoutToolbar.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.gradient_top_figma
            )
        )
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AppHelper(this@WeatherActivity).redirectToHome()
            }
        })
    }

    private fun setRecyclerViewUsingArray(jsonArray: JSONArray) {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Set adapter
        recyclerAdapter = TemperatureAdapter(jsonArray)
        binding.recyclerView.adapter = recyclerAdapter
    }

    private fun getFormattedTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy | HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }

    private fun observeResponse() {
        farmerViewModel.weatherResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jSONObject = JSONObject(state.data.toString())
                    val response = ResponseModel(jSONObject)
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

                        setRecyclerViewUsingArray(jsonArrayForecast)
                        recyclerAdapter.notifyDataSetChanged()
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        leaderboardViewModel.responseUpdateUserPoints.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                if (status == 200) {
                    ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
                }
            }
        }
    }
}