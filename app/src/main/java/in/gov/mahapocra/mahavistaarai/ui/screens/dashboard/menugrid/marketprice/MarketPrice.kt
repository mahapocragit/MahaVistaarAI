package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.marketprice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityMarketPriceBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.MarketPriceAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.GeoViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.MarketPriceViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.MARKET_PRICE_POINT
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper
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

class MarketPrice : AppCompatActivity(), AlertListEventListener {

    private lateinit var marketPriceAdapter: MarketPriceAdapter
    private lateinit var binding: ActivityMarketPriceBinding
    private val marketViewModel: MarketPriceViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private val geoViewModel: GeoViewModel by viewModels()
    private var districtJSONArray: JSONArray? = null
    private var marketJSONArray: JSONArray? = null
    private var marketPriceDetailsJSONArray: JSONArray = JSONArray()
    private lateinit var districtName: String
    private var districtID: Int = 0
    private lateinit var talukaName: String
    private var talukaID: Int = 0
    private var marketName: String? = null
    private lateinit var languageToLoad: String
    private var marketPriceDate: String = ""
    private var cDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@MarketPrice).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        LocalCustom.switchLanguage(this, languageToLoad)
        binding = ActivityMarketPriceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalCustom.uiResponsive(binding.root)

        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.marketprice)
        binding.tvSourceInformation.text = getString(R.string.source_info_market)
        binding.textViewMarket.text = getString(R.string.farmer_select_market)
        setupObservers()
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
        AnimationHelper.shrinkLeftToCenter(binding.bubbleIconImageView)
        binding.recyclerViewMarketPriceList.setHasFixedSize(true)
        val myLayoutManager = LinearLayoutManager(this)
        myLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerViewMarketPriceList.setLayoutManager(myLayoutManager)

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@MarketPrice, DashboardScreen::class.java))
            }
        })
        onClick()
        setConfiguration()
    }

    private fun setConfiguration() {
        districtName = getLocalizedValue(
            AppConstants.uDISTMR,
            AppConstants.uDIST,
            getString(R.string.farmer_select_district)
        )
        talukaName = getLocalizedValue(
            AppConstants.uTALUKAMR,
            AppConstants.uTALUKA,
            getString(R.string.farmer_select_taluka)
        )
        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
        geoViewModel.getDistrictData(this, languageToLoad)
        marketViewModel.getMarketAndMarketName(this@MarketPrice, districtID, languageToLoad)
    }

    private fun getLocalizedValue(mrKey: String, enKey: String, default: String): String {
        val key = if (languageToLoad == "mr") mrKey else enKey
        return AppSettings.getInstance().getValue(this, key, default)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onClick() {

        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
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
            marketPriceDate = ""
            marketViewModel.fetchMarketList(this, languageToLoad, districtID)
            ProgressHelper.showProgressDialog(this)
        }

        binding.searchEditText.setOnClickListener {
            marketPriceDetailsJSONArray.let { LocalCustom.extractUniqueCommNames(it) }.let {
                LocalCustom.showCommNameDialog(
                    this,
                    it
                ) { selectedName ->
                    marketPriceAdapter.filter(selectedName)
                    binding.searchEditText.text = selectedName
                    if (containsFarmerId(this)) {
                        leaderboardViewModel.updateUserPoints(this, MARKET_PRICE_POINT)
                    }
                }
            }
        }
        binding.chatbotIcon.setOnTouchListener(DraggableTouchListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        })
    }

    private fun setupObservers() {

        leaderboardViewModel.responseUpdateUserPoints.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                if (status == 200) {
                    ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
                }
            }
        }

        // Observe market list only once per lifecycle
        marketViewModel.responseMarketList.observe(this) { responseStr ->
            ProgressHelper.disableProgressDialog()
            if (responseStr != null) {
                try {
                    val jSONObject = JSONObject(responseStr.toString())
                    val response = ResponseModel(jSONObject)

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

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_LONG).show()
                }
            }
        }

        geoViewModel.districtIdResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
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
                        Log.e(TAG, "districtJSONArray could not be cast to JSONArray")
                    }
                } else {
                    Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
                }
            }
        }

        marketViewModel.getMarketPriceDetailsResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.status) {
                    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
                    marketPriceDate = simpleDateFormat.format(Date())
                    cDate = SimpleDateFormat("dd-MM-yyyy").parse(marketPriceDate)
                    marketPriceDetailsJSONArray = response.getdataArray()

                    // tvMarketDate.text = marketPreceDate
                    binding.tvMarketDetails.visibility = View.VISIBLE
                    if (marketName == null) {
                        binding.tvMarketDetails.text = (buildString {
                            append(districtName)
                            append(", ")
                            append(resources.getString(R.string.market_c_price))
                        })
                    } else {
                        binding.tvMarketDetails.text = (buildString {
                            append(districtName)
                            append(", ")
                            append(marketName)
                            append(" ")
                            append(resources.getString(R.string.market_c_price))
                        })
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
                    Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
                }
            }
        }

        marketViewModel.getMarketAndMarketNameResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())

                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.status) {

                    val marketPriceAndMarketName = response.getData()
                    val obj = JSONObject(marketPriceAndMarketName)
                    marketPriceDetailsJSONArray =
                        AppUtility.getInstance().sanitizeArrayJSONObj(obj, "details")
                    binding.tvMarketDetails.visibility = View.VISIBLE
                    if (marketName == null) {
                        binding.tvMarketDetails.text = buildString {
                            append(districtName)
                            append(", ")
                            append(resources.getString(R.string.market_c_price))
                        }
                    } else {
                        binding.tvMarketDetails.text = buildString {
                            append(resources.getString(R.string.market_state))
                            append("")
                            append(resources.getString(R.string.market_c_price))
                        }
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

                    marketJSONArray =
                        AppUtility.getInstance().sanitizeArrayJSONObj(obj, "markets")
                } else {
                    Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Handle errors
        marketViewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
            Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDistrict() {
        if (districtJSONArray == null) {
            geoViewModel.getDistrictData(this, languageToLoad)
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

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {
        if (i == 1) {
            districtID = s1!!.toInt()
            if (s != null) {
                districtName = s
            }
            binding.textViewDistrict.text = s
            if (districtID > 0) {
                marketViewModel.getMarketAndMarketName(this@MarketPrice, districtID, languageToLoad)
            }
            marketPriceDetailsJSONArray = JSONArray()
            talukaID = 0
            binding.tvMarketDetails.visibility = View.VISIBLE
            binding.tvMarketDetails.text = buildString {
                append(districtName)
                append(", ")
                append(resources.getString(R.string.market_c_price))
            }
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
            }

            binding.textViewMarket.text = ""
            binding.textViewMarket.hint = resources.getString(R.string.farmer_select_market)
            binding.textViewMarket.setHintTextColor(Color.GRAY)

        }

        if (i == 3) {
            if (s != null) {
                binding.searchEditText.text = getString(R.string.search_by_crop_name)
                marketName = s
                marketPriceDate = binding.tvMarketDate.text.toString()
                val apmcID = JSONObject(s1).optInt("apmc_id")
                marketViewModel.getMarketPriceDetails(this, apmcID, languageToLoad)
            }
            binding.textViewMarket.text = s
        }
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext =
            LocalCustom.configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }
}