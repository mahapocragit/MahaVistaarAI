package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.api.AppinventorIncAPI
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.*
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.MarketPriceAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.MarketPriceModel
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.*

class MarketPrice : AppCompatActivity(), OnMultiRecyclerItemClickListener, ApiCallbackCode,
    AlertListEventListener, ApiJSONObjCallback, DatePickerRequestListener {

    lateinit var textViewHeaderTitle: TextView
    lateinit var imageMenushow: ImageView
    private lateinit var textViewDistrict: TextView
   // private lateinit var textViewTaluka: TextView
    private lateinit var textViewMarket: TextView
    private lateinit var tv_market_details: TextView
    private lateinit var tvMarketDate: TextView
    lateinit var recyclerViewMarketPriceList: RecyclerView
    lateinit var calenderLayout: RelativeLayout

    lateinit var rvMarketPriceList: RecyclerView

    private val jobListItems: ArrayList<MarketPriceModel> = ArrayList<MarketPriceModel>()

    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null
    private var marketJSONArray: JSONArray? = null
    private var marketPriceDetailsJSONArray: JSONArray? = null

    private var marketPriceAndMarketName: JSONObject? = null


    lateinit var districtName: String
    private var districtID: Int = 0
    lateinit var talukaName: String
    private var talukaID: Int = 0
    private var marketName: String? = null
    lateinit var languageToLoad: String
    var marketPreceDate: String = ""
    var cDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@MarketPrice).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@MarketPrice))
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_market_price)
        init()
        imageMenushow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.marketprice)
        setConfiguration()
        rvMarketPriceList.setHasFixedSize(true)
        val MyLayoutManager = LinearLayoutManager(this)
        MyLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvMarketPriceList.setLayoutManager(MyLayoutManager)
        onClick()
    }
    private fun init() {
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle);
        imageMenushow = findViewById(R.id.imageMenushow);
        rvMarketPriceList = findViewById(R.id.recyclerViewMarketPriceList)
        textViewDistrict = findViewById(R.id.textViewDistrict)
      //  textViewTaluka = findViewById(R.id.textViewTaluka)
        textViewMarket = findViewById(R.id.textViewMarket)
        tv_market_details = findViewById(R.id.tv_market_details)
        tvMarketDate = findViewById(R.id.tvMarketDate)
        recyclerViewMarketPriceList = findViewById(R.id.recyclerViewMarketPriceList)
        calenderLayout = findViewById(R.id.calenderLayout)
    }

    private fun setConfiguration() {
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@MarketPrice).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@MarketPrice))
            languageToLoad = "en"
        }
        districtName = AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
        talukaName = AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)

//        if (!districtName.equals("USER_DIST") && !talukaName.equals("USER_TALUKA")) {
//            textViewDistrict.setText(districtName)
//            textViewTaluka.setText(talukaName)
//            Log.d("districtNamewr:talukawr", districtName + " ::" + talukaName)
//            fetchTalukaMasterData()
//            getMarketPriceDetails()
//        }
        getDistrictData()
       // getMarketName()

        // val c = Calendar.getInstance()
        // c.time = cDate
        //c.add(Calendar.DATE, 0);  // for current day date  //  test
        // dayPlushOne = c.time
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

//        textViewTaluka.setOnClickListener {
//            showTaluka()
//        }

        textViewMarket.setOnClickListener {
            calenderLayout.visibility=View.GONE
            tvMarketDate.text= ""
            marketPreceDate = ""
            getMarkets()
        }
        tvMarketDate.setOnClickListener {
            AppUtility.getInstance().showDisabledFutureDatePicker(
                this@MarketPrice,
                cDate,
                1,
                this@MarketPrice
            )
        }
    }

    private fun getMarkets() {
        if (marketJSONArray == null) {
            getMarketName()
        } else {
            AppUtility.getInstance().showListDialogMarketIndex(
                marketJSONArray,
                3,
                getString(R.string.farmer_select_market),
                "apmc_name",
                this,
                this
            )
        }
    }

    private fun getMarketPriceDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("date", marketPreceDate)
            jsonObject.put("market", marketName)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getmarketPriceDetails(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 4)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
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
               // fetchTalukaMasterData()
            } else {
                UIToastMessage.show(
                    this,
                    resources.getString(R.string.error_farmer_select_district)
                )
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(talukaJSONArray, 2, getString(R.string.farmer_select_taluka), "name", "id", this, this)
        }
    }


    private fun getDistrictData() {
        val jsonObject = JSONObject()
        try {

            jsonObject.put("lang", languageToLoad)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getDistrictList(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun fetchMarketPriceAndName() {
        val jsonObject = JSONObject()
        try {
           // jsonObject.put("lang", languageToLoad)
            jsonObject.put("dist_id", districtID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getMarketAndMarketName(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 5)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
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
                fetchMarketPriceAndName()
            }
            marketPriceDetailsJSONArray = null
            talukaID = 0
            tv_market_details.visibility = View.VISIBLE
            if (marketName == null) {
                tv_market_details.setText(
                    districtName
                            + "," + "\n" + resources.getString(R.string.market_c_price)
                )
            }else{
                tv_market_details.setText(
                    districtName
                            + "," + "\n" + resources.getString(R.string.market_c_price)
                )
            }

          //  textViewTaluka.setText("")
          //  textViewTaluka.setHint(resources.getString(R.string.farmer_select_taluka))
            tvMarketDate.setText("")
            tvMarketDate.setHint(resources.getString(R.string.farmer_select_date))
            tvMarketDate.setHintTextColor(Color.GRAY)
            textViewMarket.setText("")
            textViewMarket.setHint(resources.getString(R.string.farmer_select_market))
            textViewMarket.setHintTextColor(Color.GRAY)
        }
        if (i == 2) {
            talukaID = s1!!.toInt()
            if (s != null) {
                talukaName = s
                getMarketAgainstTaluka()
            }
          //  textViewTaluka.setText(s)


            textViewMarket.setText("")
            textViewMarket.setHint(resources.getString(R.string.farmer_select_market))
            textViewMarket.setHintTextColor(Color.GRAY)

        }

        if (i == 3) {
            if (s != null) {
                marketName = s.toString()
                marketPreceDate= tvMarketDate.text.toString()
                getMarketPriceDetails()
            }
            textViewMarket.setText(s)
        }
    }

    private fun getMarketName() {
        try {
            var url: String = APIServices.kGetMarketData
            Log.d("TalukaUrl", url)
            Log.d("param",APIServices.SSO + url)
            val api = AppinventorIncAPI(this, APIServices.SSO, APIServices.SSO_KEY, "", true)
            api.getRequestData(url, this, 3)
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun getMarketAgainstTaluka() {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("taluka", talukaID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getmarketList(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 3)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
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
            val response = ResponseModel(jSONObject)

            if (response.status) {
                districtJSONArray = response.getdataArray()
                Log.d("districtJSONArray11111", districtJSONArray.toString())
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 2 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                talukaJSONArray = response.getdataArray()
                Log.d("talukaJSONArray", talukaJSONArray.toString())
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 3 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                marketJSONArray = response.getdataArray()
                Log.d("talukaJSONArray", marketJSONArray.toString())
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 4 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                calenderLayout.visibility=View.VISIBLE
                val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
                marketPreceDate = simpleDateFormat.format(Date())
                cDate = SimpleDateFormat("dd-MM-yyyy").parse(marketPreceDate)

                marketPriceDetailsJSONArray = response.getdataArray()
                Log.d("MPriceDetailsJSONArray", marketPriceDetailsJSONArray.toString())

               // tvMarketDate.text = marketPreceDate
                if(marketPriceDetailsJSONArray!== null) {
                    tv_market_details.visibility = View.VISIBLE
                    if (marketName == null) {
                        tv_market_details.setText(
                           districtName
                                    + "," + "\n" + resources.getString(R.string.market_c_price)
                        )
                    }else{
                        tv_market_details.setText(
                            districtName+","
                                    + marketName + "\n" + resources.getString(R.string.market_c_price)
                        )
                    }
                    val marketPriceAdapter =
                        MarketPriceAdapter(
                            this,
                            marketPriceDetailsJSONArray
                        )
                    recyclerViewMarketPriceList.setLayoutManager(
                        LinearLayoutManager(
                            this,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    )
                    recyclerViewMarketPriceList!!.adapter = marketPriceAdapter
                    marketPriceAdapter!!.notifyDataSetChanged()
                }else{
                    tv_market_details.visibility = View.VISIBLE
                    if (marketName == null) {
                        tv_market_details.setText(
                            districtName
                                    + "," + "\n" + resources.getString(R.string.market_c_price)
                        )

                    }else{
                        tv_market_details.setText(
                            districtName+","
                                    + marketName + "\n" + resources.getString(R.string.market_c_price)
                        )
                    }
                    Toast.makeText(this, "Data Not Available on $marketPreceDate for $marketName", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }

        if (i == 5 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {

               val marketPriceAndMarketName = response.getData()
                var obj = JSONObject(marketPriceAndMarketName)
                marketPriceDetailsJSONArray = AppUtility.getInstance().sanitizeArrayJSONObj(obj, "details")
                Log.d("MPriceDetailsJSONArray", marketPriceDetailsJSONArray.toString())

                // tvMarketDate.text = marketPreceDate
                if(marketPriceDetailsJSONArray!== null) {
                    tv_market_details.visibility = View.VISIBLE
                    if (marketName == null) {
                        tv_market_details.setText(
                           districtName + "," + "\n" + resources.getString(R.string.market_c_price)
                        )
                    }else{
                        tv_market_details.setText(
                            resources.getString(R.string.market_state)
                                    + "" + "\n" + resources.getString(R.string.market_c_price)
                        )
                    }
                    val marketPriceAdapter =
                        MarketPriceAdapter(
                            this,
                            marketPriceDetailsJSONArray
                        )
                    recyclerViewMarketPriceList.setLayoutManager(
                        LinearLayoutManager(
                            this,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    )
                    recyclerViewMarketPriceList!!.adapter = marketPriceAdapter
                    marketPriceAdapter!!.notifyDataSetChanged()

                    marketJSONArray = AppUtility.getInstance().sanitizeArrayJSONObj(obj, "markets")
                }else{
                    tv_market_details.visibility = View.VISIBLE
                    tv_market_details.setText(
                       districtName+"," + marketName + "\n" + resources.getString(R.string.market_c_price)
                    )
                    Toast.makeText(this, "Data Not Available on $marketPreceDate for $marketName", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }

    override fun onDateSelected(i: Int, day: Int, month: Int, year: Int) {
        if (i == 1) {
            Log.d("i2", day.toString())
            Log.d("i3", month.toString())
            Log.d("i4", year.toString())
            marketPreceDate = "" + year + "-" + month + "-" + day
            tvMarketDate.text = marketPreceDate
            Log.d("marketPreceDate", marketPreceDate)
            if (!marketName.isNullOrEmpty()) {
                getMarketPriceDetails()
            } else {
                Toast.makeText(this, "Please Select Your Desire Market", Toast.LENGTH_LONG).show()
            }
        }
    }
}





