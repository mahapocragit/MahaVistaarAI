package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityWarehouseBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.WarehouseAvailabilityAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.WAREHOUSE_POINT
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DraggableTouchListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class Warehouse : AppCompatActivity(), AlertListEventListener, OnMultiRecyclerItemClickListener {

    lateinit var binding: ActivityWarehouseBinding
    private var warehouseAvailabilityJSONArray: JSONArray? = null
    private var districtJSONArray: JSONArray? = null
    private val farmerViewModel: FarmerViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private lateinit var districtName: String
    private var districtID: Int = 0
    private var talukaID: Int = 0
    private lateinit var totalWareHouse: String
    private lateinit var totalAvailableWareHouse: String
    lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@Warehouse).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityWarehouseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        setUpObserver()
        init()
        onClick()
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
        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.tvSourceInformation.text = getString(R.string.source_info_market)
        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.wareHouse)
        binding.textViewDistrict.text = getString(R.string.farmer_select_district)

        binding.tvWareHouseName.text = getString(R.string.warehouse_details)
        binding.tvTotalAvailableCapacity.text = getString(R.string.total_available_capacity)
        binding.tvRecordDate.text = getString(R.string.record_date)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.wareHousereport.setHasFixedSize(false)
        binding.wareHousereport.isNestedScrollingEnabled = true
        farmerViewModel.getDistrictData(this, languageToLoad)
        farmerViewModel.fetchWarehouseData(this, districtID, languageToLoad)
        binding.chatbotIcon.setOnTouchListener(DraggableTouchListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        })
    }

    private fun setUpObserver() {

        leaderboardViewModel.responseUpdateUserPoints.observe(this){ response->
            if (response!=null){
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                if (status==200){
                    ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
                }
            }
        }

        farmerViewModel.districtIdResponse.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
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
                    UIToastMessage.show(this, response.response)
                }
            }
        }

        farmerViewModel.warehouseDetailsResponse.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.status) {
                    warehouseAvailabilityJSONArray = response.dataArrays
                    if (warehouseAvailabilityJSONArray?.length() != 0) {
                        binding.wareHousereport.visibility = View.VISIBLE
                        binding.wareHouseEmptyTextView.visibility = View.GONE
                        totalWareHouse = response.total_available_capacity()
                        totalAvailableWareHouse = response.getTotalAvailableWareHouse()
                        binding.textTotalWarehouse.text =
                            buildString {
                                append(resources.getString(R.string.total_warehouse))
                                append(" ")
                                append(totalWareHouse)
                            }
                        binding.textAvailableCapacity.text =
                            buildString {
                                append(resources.getString(R.string.total_available_capacity))
                                append(" ")
                                append(totalAvailableWareHouse)
                                append(" ")
                                append(
                                    resources.getString(
                                        R.string.tonnes
                                    )
                                )
                            }
                        if (warehouseAvailabilityJSONArray !== null) {
                            if (warehouseAvailabilityJSONArray?.length()!! > 0) {
                                val adaptorWaterBudgetReport =
                                    WarehouseAvailabilityAdapter(
                                        this,
                                        this,
                                        warehouseAvailabilityJSONArray
                                    )
                                binding.wareHousereport.setLayoutManager(
                                    LinearLayoutManager(
                                        this,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                )
                                binding.wareHousereport.adapter = adaptorWaterBudgetReport
                                adaptorWaterBudgetReport.notifyDataSetChanged()
                            }
                        } else {
                            binding.wareHousereport.visibility = View.GONE
                            binding.wareHouseEmptyTextView.visibility = View.VISIBLE
                            resetValuesForWareHouse()
                        }
                    } else {
                        binding.wareHousereport.visibility = View.GONE
                        binding.wareHouseEmptyTextView.visibility = View.VISIBLE
                        resetValuesForWareHouse()
                    }
                } else {
                    UIToastMessage.show(this, response.response)
                }
                leaderboardViewModel.updateUserPoints(this, WAREHOUSE_POINT)
            }
        }
    }

    private fun onClick() {
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                startActivity(Intent(this@Warehouse, DashboardScreen::class.java))
            }
        })

        binding.textViewDistrict.setOnClickListener {
            showDistrict()
        }

    }

    private fun showDistrict() {
        if (districtJSONArray == null) {
            farmerViewModel.getDistrictData(this, languageToLoad)
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

    private fun resetValuesForWareHouse() {
        binding.textTotalWarehouse.text =
            buildString {
                append(resources.getString(R.string.total_warehouse))
                append(" ")
                append(0)
            }
        binding.textAvailableCapacity.text =
            buildString {
                append(resources.getString(R.string.total_available_capacity))
                append(" ")
                append(0)
            }
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {
        if (i == 1) {
            districtID = s1!!.toInt()
            if (s != null) {
                districtName = s
                farmerViewModel.fetchWarehouseData(this, districtID, languageToLoad)
            }
            binding.textViewDistrict.text = s
            warehouseAvailabilityJSONArray = null
            talukaID = 0
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
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