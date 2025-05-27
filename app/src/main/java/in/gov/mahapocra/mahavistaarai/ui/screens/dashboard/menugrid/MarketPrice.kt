package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIKeys
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityMarketPriceBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.MarketPriceAdapter
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Date


class MarketPrice : AppCompatActivity(), OnMultiRecyclerItemClickListener, ApiCallbackCode,
    AlertListEventListener, ApiJSONObjCallback {

    private lateinit var marketPriceAdapter: MarketPriceAdapter
    lateinit var binding: ActivityMarketPriceBinding
    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null
    private var marketJSONArray: JSONArray? = null
    private var marketPriceDetailsJSONArray: JSONArray? = null

    lateinit var districtName: String
    private var districtID: Int = 0
    lateinit var talukaName: String
    private var talukaID: Int = 0
    private var marketName: String? = null
    lateinit var languageToLoad: String
    private var marketPreceDate: String = ""
    private var cDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@MarketPrice).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@MarketPrice))
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityMarketPriceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.marketprice)
        binding.tvSourceInformation.text = getString(R.string.source_info_market)
        binding.textViewMarket.text = getString(R.string.farmer_select_market)
        setConfiguration()
        binding.recyclerViewMarketPriceList.setHasFixedSize(true)
        val myLayoutManager = LinearLayoutManager(this)
        myLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerViewMarketPriceList.setLayoutManager(myLayoutManager)
        onClick()

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                marketPriceAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setConfiguration() {
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@MarketPrice).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@MarketPrice))
            languageToLoad = "en"
        }
        districtName =
            AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
        talukaName =
            AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
        getDistrictData()
        fetchMarketPriceAndName()
    }

    private fun onClick() {
        binding.relativeLayoutTopBar.imageMenushow.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        binding.textViewDistrict.setOnClickListener {
            showDistrict()
        }

        binding.textViewMarket.setOnClickListener {
            binding.calenderLayout.visibility = View.GONE
            binding.tvMarketDate.text = ""
            marketPreceDate = ""
            getMarkets()
        }
    }

    private fun getMarkets() {
        getMarketName(languageToLoad, districtID)
    }

    private fun getMarketPriceDetails(apmcId: Int) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("api_key", APIKeys.SSO_PROD)
            jsonObject.put("apmc_id", apmcId)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getmarketPriceDetails(requestBody)
            api.postRequest(responseCall, this, 4)
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
                "code",
                this,
                this
            )
        }
    }

    private fun getDistrictData() {
        val jsonObject = JSONObject()
        try {

            jsonObject.put("lang", languageToLoad)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    AppEnvironment.FARMER.baseUrl,
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

    private fun fetchMarketPriceAndName() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("api_key", APIKeys.SSO_PROD)
            jsonObject.put("district_code", districtID)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getMarketAndMarketName(requestBody)
            api.postRequest(responseCall, this, 5)
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=$e")
            e.printStackTrace()
        }
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {

        if (i == 1) {
            Log.d("TAGGER", "didSelectListItem: $s1")
            districtID = s1!!.toInt()
            if (s != null) {
                districtName = s
            }
            binding.textViewDistrict.text = s
            if (districtID > 0) {
                fetchMarketPriceAndName()
            }
            marketPriceDetailsJSONArray = null
            talukaID = 0
            binding.tvMarketDetails.visibility = View.VISIBLE
            binding.tvMarketDetails.text =
                ("$districtName, ${resources.getString(R.string.market_c_price)}")
            binding.tvMarketDate.text = ""
            binding.tvMarketDate.hint = resources.getString(R.string.farmer_select_date)
            binding.tvMarketDate.setHintTextColor(Color.GRAY)
            binding.textViewMarket.text = ""
            binding.textViewMarket.hint = resources.getString(R.string.farmer_select_market)
            binding.textViewMarket.setHintTextColor(Color.GRAY)
        }
        if (i == 2) {
            talukaID = s1!!.toInt()
            if (s != null) {
                talukaName = s
                getMarkets()
            }

            binding.textViewMarket.text = ""
            binding.textViewMarket.hint = resources.getString(R.string.farmer_select_market)
            binding.textViewMarket.setHintTextColor(Color.GRAY)

        }

        if (i == 3) {
            if (s != null) {
                marketName = s.toString()
                marketPreceDate = binding.tvMarketDate.text.toString()
                val apmcID = JSONObject(s1).optInt("apmc_id")
                Log.d("TAGGER", "didSelectListItem: $apmcID")
                getMarketPriceDetails(apmcID)
            }
            binding.textViewMarket.text = s
        }
    }

    private fun getMarketName(languageToLoad: String, districtCode: Int) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("api_key", APIKeys.SSO_PROD)
            jsonObject.put("district_code", districtCode)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getMarketList(requestBody)
            api.postRequest(responseCall, this, 3)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        Toast.makeText(this, "onFailure:Data Not Found", Toast.LENGTH_LONG).show()
    }

    override fun onFailure(th: Throwable?, i: Int) {
        Toast.makeText(this, "onFailure:Data Not Found", Toast.LENGTH_LONG).show()
    }


    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                districtJSONArray = response.getdataArray()
                districtJSONArray?.let {
                    for (j in 0 until it.length()) {
                        val districtObject = it.getJSONObject(j)
                        val id = districtObject.getInt("code")
                        val name = districtObject.getString("name")

                        // Check if the current id matches districtID
                        if (id == districtID) {
                            // Set the text in textViewDistrict if a match is found
                            binding.textViewDistrict.text = name
                            break // No need to continue looping once the matching district is found
                        }
                    }
                } ?: run {
                    Log.e("TAGGER", "districtJSONArray could not be cast to JSONArray")
                }
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 2 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                talukaJSONArray = response.getdataArray()
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 3 && jSONObject != null) {
            Log.d("TAGGER", "onResponse: $jSONObject")
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                marketJSONArray = response.getdataArray()
                AppUtility.getInstance().showListDialogMarketIndex(
                    marketJSONArray,
                    3,
                    getString(R.string.farmer_select_market),
                    "apmc_name",
                    this,
                    this
                )
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 4 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )
            if (response.status) {
//                binding.calenderLayout.visibility = View.VISIBLE
                val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
                marketPreceDate = simpleDateFormat.format(Date())
                cDate = SimpleDateFormat("dd-MM-yyyy").parse(marketPreceDate)
                marketPriceDetailsJSONArray = response.getdataArray()

                // tvMarketDate.text = marketPreceDate
                if (marketPriceDetailsJSONArray !== null) {
                    binding.tvMarketDetails.visibility = View.VISIBLE
                    if (marketName == null) {
                        binding.tvMarketDetails.text = (districtName
                                + ", " + resources.getString(R.string.market_c_price))
                    } else {
                        binding.tvMarketDetails.text = (districtName + ", "
                                + marketName + " " + resources.getString(R.string.market_c_price))
                    }
                    marketPriceAdapter =
                        MarketPriceAdapter(
                            this,
                            marketPriceDetailsJSONArray
                        )
                    binding.recyclerViewMarketPriceList.setLayoutManager(
                        LinearLayoutManager(
                            this,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    )
                    binding.recyclerViewMarketPriceList.adapter = marketPriceAdapter
                    marketPriceAdapter.notifyDataSetChanged()
                } else {
                    binding.tvMarketDetails.visibility = View.VISIBLE
                    if (marketName == null) {
                        binding.tvMarketDetails.text = (districtName
                                + ", " + resources.getString(R.string.market_c_price))

                    } else {
                        binding.tvMarketDetails.text = (districtName + ", "
                                + marketName + " " + resources.getString(R.string.market_c_price))
                    }
                    Toast.makeText(
                        this,
                        "Data Not Available on $marketPreceDate for $marketName",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 5 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )
            if (response.status) {

                val marketPriceAndMarketName = response.getData()
                val obj = JSONObject(marketPriceAndMarketName)
                marketPriceDetailsJSONArray =
                    AppUtility.getInstance().sanitizeArrayJSONObj(obj, "details")
                if (marketPriceDetailsJSONArray !== null) {
                    binding.tvMarketDetails.visibility = View.VISIBLE
                    if (marketName == null) {
                        binding.tvMarketDetails.text =
                            districtName + ", " + resources.getString(R.string.market_c_price)
                    } else {
                        binding.tvMarketDetails.text = (resources.getString(R.string.market_state)
                                + "" + resources.getString(R.string.market_c_price))
                    }
                    marketPriceAdapter =
                        MarketPriceAdapter(
                            this,
                            marketPriceDetailsJSONArray
                        )
                    binding.recyclerViewMarketPriceList.setLayoutManager(
                        LinearLayoutManager(
                            this,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    )
                    binding.recyclerViewMarketPriceList.adapter = marketPriceAdapter
                    marketPriceAdapter.notifyDataSetChanged()

                    marketJSONArray = AppUtility.getInstance().sanitizeArrayJSONObj(obj, "markets")
                } else {
                    binding.tvMarketDetails.visibility = View.VISIBLE
                    binding.tvMarketDetails.text =
                        districtName + ", " + marketName + " " + resources.getString(R.string.market_c_price)
                    Toast.makeText(
                        this,
                        "Data Not Available on $marketPreceDate for $marketName",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
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
}





