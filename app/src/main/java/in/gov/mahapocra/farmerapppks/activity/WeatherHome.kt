package `in`.gov.mahapocra.farmerapppks.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.WeatherAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class WeatherHome : AppCompatActivity(), ApiCallbackCode, AlertListEventListener, OnMultiRecyclerItemClickListener {

    lateinit var textViewHeaderTitle: TextView
    lateinit var imageMenushow: ImageView
    lateinit var textViewDistrict: TextView
    lateinit var textViewTaluka: TextView
    lateinit var tvRainfall: TextView
    lateinit var tvMinValue: TextView
    lateinit var tvMaxValue: TextView
    lateinit var tvWindValue: TextView
    lateinit var tvHumidityValue: TextView
    lateinit var tvAgroMetAdvisory: TextView
    lateinit var tvWindDirection: TextView
    lateinit var tvCloudCover: TextView
    lateinit var btnText: TextView
    lateinit var tableRow1: TableRow

    lateinit var btnForecastNext: TextView
    lateinit var btnPreviousWeather: TextView
    lateinit var pikSallaTv: LinearLayout

    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null

    lateinit var districtName: String
    private var districtID: Int = 0
    lateinit var talukaName: String
    private var talukaID: Int = 0
    private var weatherForecastJSONArray: JSONArray? = null
    private var weatherPrevioustJSONArray: JSONArray? = null
    lateinit var rainfall: String
    lateinit var minValue: String
    lateinit var maxValue: String
    lateinit var windValue: String
    lateinit var humidityValue: String
    lateinit var languageToLoad: String

    lateinit var weatherRecyclerView: RecyclerView
    lateinit var forcast_view: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@WeatherHome).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_weather_home)
        init()
        setConfiguration()
        onClick()
    }
    // Initialization
    private fun init() {
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenushow = findViewById(R.id.imageMenushow)
        textViewDistrict = findViewById(R.id.textViewDistrict)
        textViewTaluka = findViewById(R.id.textViewTaluka)
        tvRainfall = findViewById(R.id.tvRainfall)
        tvMinValue = findViewById(R.id.tvMintemp)
        tvMaxValue = findViewById(R.id.tvMaxtemp)
        tvWindValue = findViewById(R.id.tvWindtemp)
        tvHumidityValue = findViewById(R.id.tvHumiditytemp)
        tvWindDirection = findViewById(R.id.tv_wind_direction)
        tvCloudCover = findViewById(R.id.tv_cloud_cover)
        tvAgroMetAdvisory = findViewById(R.id.tvAgroMetAdvisory)
        btnForecastNext = findViewById(R.id.btnForecastNext)
        pikSallaTv = findViewById(R.id.pikSallaTv)
        btnPreviousWeather = findViewById(R.id.btnPreviousWeather)
        weatherRecyclerView = findViewById(R.id.weatherRecyclerView)
        forcast_view = findViewById(R.id.forcast_view)
        btnText = findViewById(R.id.btnText)
        tableRow1 = findViewById(R.id.tableRow1)

    }
    private fun setConfiguration() {
        imageMenushow.visibility = View.VISIBLE
        textViewHeaderTitle.setText(R.string.weather)
        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
        getDistrictData()
        fetchTalukaMasterData()
        weatherDetails()
    }

    private fun onClick() {
        imageMenushow.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        textViewDistrict.setOnClickListener {
            showDistrict()
        }
        textViewTaluka.setOnClickListener {
            showTaluka()
        }

        btnForecastNext.setOnClickListener {
            forcast_view.visibility = View.VISIBLE
            tvWindDirection.visibility = View.VISIBLE
            tvCloudCover.visibility = View.VISIBLE
            btnText.visibility = View.VISIBLE
            pikSallaTv.visibility = View.VISIBLE
            btnText.setText(R.string.next_days)
            btnForecastNext.background = ContextCompat.getDrawable(this, R.drawable.green_bg_gradient)
            btnPreviousWeather.background = ContextCompat.getDrawable(this, R.drawable.layout_button_bg)
            forecastData()
        }
        btnPreviousWeather.setOnClickListener {
            forcast_view.visibility = View.VISIBLE
            tvWindDirection.visibility = View.GONE
            tvCloudCover.visibility = View.GONE
            btnText.visibility = View.VISIBLE
            pikSallaTv.visibility = View.GONE
            btnText.setText(R.string.previous_days)
            btnForecastNext.background = ContextCompat.getDrawable(this, R.drawable.layout_button_bg)
            btnPreviousWeather.background = ContextCompat.getDrawable(this, R.drawable.green_bg_gradient)
            previousCastData()
        }
    }

    private fun previousCastData() {
        if(weatherPrevioustJSONArray!=null) {
            if (weatherPrevioustJSONArray?.length()!! > 0) {
                val weatherAdapter =
                    WeatherAdapter(
                        this,
                        this,
                        weatherPrevioustJSONArray, "previousWeather"
                    )
                weatherRecyclerView.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                )
                weatherRecyclerView.adapter = weatherAdapter
                weatherAdapter.notifyDataSetChanged()
            }
        }else
        {
            Toast.makeText(this@WeatherHome, "Data not Found...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun forecastData() {
        if(weatherForecastJSONArray!=null) {
            if (weatherForecastJSONArray?.length()!! > 0) {
                val weatherAdapter =
                    WeatherAdapter(
                        this,
                        this,
                        weatherForecastJSONArray, "forecastWeather"
                    )
                weatherRecyclerView.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                )
                weatherRecyclerView.adapter = weatherAdapter
                weatherAdapter.notifyDataSetChanged()
            }
        } else
        {
            Toast.makeText(this@WeatherHome, "Data not Found...", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getDistrictData() {
        val jsonObject = JSONObject()
        try {
            //jsonObject.put("SecurityKey", APIServices.SSO_KEY)
            jsonObject.put("lang", languageToLoad)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getDistrictList(requestBody)
            api.postRequest(responseCall, this, 1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun fetchTalukaMasterData() {
        val jsonObject = JSONObject()
        try {
           // jsonObject.put("SecurityKey", APIServices.SSO_KEY)
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("district_id", districtID)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getTalukaList(requestBody)
            api.postRequest(responseCall, this, 2)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun weatherDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("taluka", talukaID)
            jsonObject.put("lang", languageToLoad)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getWeatherDetails(requestBody)
            api.postRequest(responseCall, this, 3)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showDistrict() {
        if (districtJSONArray == null) {
            getDistrictData()
        } else {
            AppUtility.getInstance().showListDialogIndex(
                districtJSONArray,
                1,
                getString(R.string.farmer_select_district),
                "name",
                "id",
                this,
                this
            )
        }
    }
    private fun showTaluka() {
        if (talukaJSONArray == null) {
            if (districtID > 0) {
                fetchTalukaMasterData()
            } else {
                UIToastMessage.show(
                    this,
                    resources.getString(R.string.error_farmer_select_district)
                )
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(
                    talukaJSONArray,
                    2,
                    getString(R.string.farmer_select_taluka),
                    "name",
                    "id",
                    this,
                    this
                )
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        th?.printStackTrace()
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                districtJSONArray = response.getdataArray()
                districtJSONArray?.let {
                    for (j in 0 until it.length()) {
                        val districtObject = it.getJSONObject(j)
                        val id = districtObject.getInt("id")
                        val name = districtObject.getString("name")

                        // Check if the current id matches districtID
                        if (id == districtID) {
                            // Set the text in textViewDistrict if a match is found
                            textViewDistrict.text = name
                            break // No need to continue looping once the matching district is found
                        }
                    }
                } ?: run {
                    Log.e("TAGGER", "districtJSONArray could not be cast to JSONArray")
                }
            } else {
                UIToastMessage.show(this, response.response)
            }
        }

        if (i == 2 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                talukaJSONArray = response.getdataArray()
                talukaJSONArray?.let {
                    for (j in 0 until it.length()) {
                        val talukaObject = it.getJSONObject(j)
                        val id = talukaObject.getInt("id")
                        val name = talukaObject.getString("name")

                        // Check if the current id matches districtID
                        if (id == talukaID) {
                            // Set the text in textViewDistrict if a match is found
                            textViewTaluka.text = name
                            break // No need to continue looping once the matching district is found
                        }
                    }
                } ?: run {
                    Log.e("TAGGER", "districtJSONArray could not be cast to JSONArray")
                }
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
        if (i == 3 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {

                val strAgroData = jSONObject.getString("AgroMetAdvisory")
                val jSONObject = jSONObject.getJSONObject("Temperature")

                rainfall = jSONObject.getString("rainfall").toString()
                minValue = jSONObject.getString("min").toString()
                maxValue = jSONObject.getString("max").toString()
                windValue = jSONObject.getString("wind").toString()
                humidityValue = jSONObject.getString("humidity").toString()

                tvRainfall.setTypeface(null, Typeface.BOLD)
                tvMinValue.setTypeface(null, Typeface.BOLD)
                tvMaxValue.setTypeface(null, Typeface.BOLD)
                tvWindValue.setTypeface(null, Typeface.BOLD)
                tvHumidityValue.setTypeface(null, Typeface.BOLD)

                tvRainfall.text = "" + rainfall + resources.getString(R.string.rainfallunits)
                tvMinValue.text = "" + minValue + "°C"
                tvMaxValue.text = "" + maxValue + "°C"
                tvWindValue.text = "" + windValue + resources.getString(R.string.windunits)
                tvHumidityValue.text = "" + humidityValue + "%"
                tvAgroMetAdvisory.text = "" + strAgroData
                if(tvMinValue!=null) {
                    tableRow1.visibility = View.VISIBLE
                }
                weatherForecastJSONArray = response.getForcastDataArray()

                weatherPrevioustJSONArray = response.getPriviousWeatherDataArray()
                forcast_view.visibility = View.VISIBLE
                tvWindDirection.visibility = View.VISIBLE
                tvCloudCover.visibility = View.VISIBLE
                btnText.visibility = View.VISIBLE
                btnText.setText(R.string.next_days)
                forecastData()
            }
        }
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {
        if (i == 1) {

            districtID = s1!!.toInt()
            if (s != null) {
                districtName = s
            }
            textViewDistrict.text = s
            if (districtID > 0) {
                fetchTalukaMasterData()
            }
            weatherForecastJSONArray = null
            weatherPrevioustJSONArray = null
            talukaID = 0
            textViewTaluka.text = ""
            textViewTaluka.hint = resources.getString(R.string.farmer_select_taluka)
            textViewTaluka.setHintTextColor(Color.GRAY)
        }
        if (i == 2) {
            try {
                talukaID = s1!!.toInt()
                if (s != null) {
                    talukaName = s
                    weatherDetails()
                }
                textViewTaluka.text = s
            } catch (ex: NumberFormatException) {
                System.err.println("Invalid string in argument")
                Toast.makeText(this@WeatherHome, "Data not Found...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }
}