package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.WeatherAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
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

    lateinit var weatherAdapter: RecyclerView
    lateinit var forcast_view: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@WeatherHome).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@WeatherHome))
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_weather_home)
        init()
        setConfiguration()
        onClick()
    }
    // Initialization
    private fun init() {
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle);
        imageMenushow = findViewById(R.id.imageMenushow);
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
        weatherAdapter = findViewById(R.id.weatherAdapter)
        forcast_view = findViewById(R.id.forcast_view)
        btnText = findViewById(R.id.btnText)
        tableRow1 = findViewById(R.id.tableRow1)

    }
    private fun setConfiguration() {

        imageMenushow.setVisibility(View.VISIBLE)
        textViewHeaderTitle.setText(R.string.weather)

//        districtName = AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
//        talukaName = AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
//        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
//        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
//        if (!districtName.equals("USER_DIST") && !talukaName.equals("USER_TALUKA")) {
//            textViewDistrict.setText(districtName)
//            textViewTaluka.setText(talukaName)
//            Log.d("districtNamewr:talukawr", districtName + " ::" + talukaName)
//            fetchTalukaMasterData()
//            weatherDetails()
//        }
        getDistrictData()
    }

    private fun onClick() {
        imageMenushow.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        })
        textViewDistrict.setOnClickListener {
            showDistrict()
        }
        textViewTaluka.setOnClickListener {
            showTaluka()
        }

        btnForecastNext.setOnClickListener {
//            val weather = Intent(this@WeatherHome, ForecastWeather::class.java)
//            startActivity(weather)
            forcast_view.setVisibility(View.VISIBLE)
            tvWindDirection.setVisibility(View.VISIBLE)
            tvCloudCover.setVisibility(View.VISIBLE)
            btnText.setVisibility(View.VISIBLE)
            pikSallaTv.setVisibility(View.VISIBLE)
            btnText.setText(R.string.next_days)
            btnForecastNext.setBackground(ContextCompat.getDrawable(this, R.drawable.green_bg_gradient))
            btnPreviousWeather.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_button_bg))
            forcastData()
        }
        btnPreviousWeather.setOnClickListener {
//            val weather = Intent(this@WeatherHome, PreviousWeather::class.java)
//            startActivity(weather)
            forcast_view.setVisibility(View.VISIBLE)
            tvWindDirection.setVisibility(View.GONE)
            tvCloudCover.setVisibility(View.GONE)
            btnText.setVisibility(View.VISIBLE)
            pikSallaTv.setVisibility(View.GONE)
            btnText.setText(R.string.previous_days)
            btnForecastNext.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_button_bg))
            btnPreviousWeather.setBackground(ContextCompat.getDrawable(this, R.drawable.green_bg_gradient))
        previouscastData()
        }
    }

    private fun previouscastData() {
        if(weatherPrevioustJSONArray!=null) {
            if (weatherPrevioustJSONArray?.length()!! > 0) {
                val weatherAdaptr =
                    WeatherAdapter(
                        this,
                        this,
                        weatherPrevioustJSONArray, "previousWeather"
                    )
                weatherAdapter.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                )
                weatherAdapter!!.adapter = weatherAdaptr
                weatherAdaptr!!.notifyDataSetChanged()
            }
        }else
        {
            Toast.makeText(this@WeatherHome, "Data not Found...", Toast.LENGTH_SHORT).show();
        }
    }

    private fun forcastData() {
        if(weatherForecastJSONArray!=null) {
            if (weatherForecastJSONArray?.length()!! > 0) {
                val weatherAdaptr =
                    WeatherAdapter(
                        this,
                        this,
                        weatherForecastJSONArray, "forecastWeather"
                    )
                weatherAdapter.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                )
                weatherAdapter!!.adapter = weatherAdaptr
                weatherAdaptr!!.notifyDataSetChanged()
            }
        } else
        {
            Toast.makeText(this@WeatherHome, "Data not Found...", Toast.LENGTH_SHORT).show();
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
            DebugLog.getInstance().d("Weather::param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param2=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
            api.postRequest(responseCall, this, 2)
            DebugLog.getInstance().d("Weather::param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
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
            DebugLog.getInstance().d("Weather::param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param2=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
            api.postRequest(responseCall, this, 3)
            DebugLog.getInstance().d("Weather::param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
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
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                districtJSONArray = response.getdataArray()
                Log.d("Weather::districtArray", districtJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }

        if (i == 2 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                talukaJSONArray = response.getdataArray()
                Log.d("Weather::talukaArray", talukaJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
        if (i == 3 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {

                val strAgroData = jSONObject.getString("AgroMetAdvisory")
                Log.d("Weather::AgroData", strAgroData.toString())
                val jSONObject = jSONObject.getJSONObject("Temperature")
                Log.d("Weather::JsonOb", jSONObject.toString())

                rainfall = jSONObject.getString("rainfall").toString()
                minValue = jSONObject.getString("min").toString()
                maxValue = jSONObject.getString("max").toString()
                windValue = jSONObject.getString("wind").toString()
                humidityValue = jSONObject.getString("humidity").toString()
                Log.d("Weather::minValue", minValue)
                Log.d("Weather::maxValue", maxValue)
                Log.d("Weather::wind", windValue)
                Log.d("Weather::humidity", humidityValue)

                tvRainfall.setTypeface(null, Typeface.BOLD);
                tvMinValue.setTypeface(null, Typeface.BOLD);
                tvMaxValue.setTypeface(null, Typeface.BOLD);
                tvWindValue.setTypeface(null, Typeface.BOLD);
                tvHumidityValue.setTypeface(null, Typeface.BOLD);

                tvRainfall!!.text = "" + rainfall + resources.getString(R.string.rainfallunits)
                tvMinValue!!.text = "" + minValue + "°C"
                tvMaxValue!!.text = "" + maxValue + "°C"
                tvWindValue!!.text = "" + windValue + resources.getString(R.string.windunits)
                tvHumidityValue!!.text = "" + humidityValue + "%"
                tvAgroMetAdvisory!!.text = "" + strAgroData
                if(tvMinValue!=null) {
                    tableRow1.setVisibility(View.VISIBLE)
                }
                weatherForecastJSONArray = response.getForcastDataArray()
                Log.d("warehouseAvailability", weatherForecastJSONArray.toString())

                weatherPrevioustJSONArray = response.getPriviousWeatherDataArray()
                Log.d("warehouseAvailability", weatherPrevioustJSONArray.toString())
                forcast_view.setVisibility(View.VISIBLE)
                tvWindDirection.setVisibility(View.VISIBLE)
                tvCloudCover.setVisibility(View.VISIBLE)
                btnText.setVisibility(View.VISIBLE)
                btnText.setText(R.string.next_days)
                forcastData()
               // previouscastData()
            } else {
                // UIToastMessage.show(this, response.response)
            }
        }
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {
        if (i == 1) {

            districtID = s1!!.toInt()
            if (s != null) {
                districtName = s
            }
            textViewDistrict.setText(s)
            if (districtID > 0) {
                fetchTalukaMasterData()
            }
            weatherForecastJSONArray = null
            weatherPrevioustJSONArray = null
            talukaID = 0
            textViewTaluka.setText("")
            textViewTaluka.setHint(resources.getString(R.string.farmer_select_taluka))
            textViewTaluka.setHintTextColor(Color.GRAY)
        }
        if (i == 2) {
            try {
                talukaID = s1!!.toInt()
                if (s != null) {
                    talukaName = s
                    weatherDetails()
                }
                textViewTaluka.setText(s)
            } catch (ex: NumberFormatException) {
                System.err.println("Invalid string in argumment")
                Toast.makeText(this@WeatherHome, "Data not Found...", Toast.LENGTH_SHORT).show();
                //request for well-formatted string
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }
}