package `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.menugrid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapp.R
import `in`.gov.mahapocra.farmerapp.ui.adapters.WarehouseAvailabilityAdapter
import `in`.gov.mahapocra.farmerapp.data.api.APIRequest
import `in`.gov.mahapocra.farmerapp.data.api.APIServices
import `in`.gov.mahapocra.farmerapp.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapp.util.app_util.AppString
import `in`.gov.mahapocra.farmerapp.databinding.ActivityWarehouseBinding
import `in`.gov.mahapocra.farmerapp.data.model.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class Warehouse : AppCompatActivity(), ApiCallbackCode,
    AlertListEventListener, OnMultiRecyclerItemClickListener {
    lateinit var binding: ActivityWarehouseBinding

    private var warehouseAvailabilityJSONArray: JSONArray? = null
    private var districtJSONArray: JSONArray? = null

    lateinit var districtName: String
    private var districtID: Int = 0
    lateinit var talukaName: String
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
        binding = ActivityWarehouseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        init()
        onClick()
        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.wareHouse)
    }

    private fun init() {
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@Warehouse).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }

        binding.wareHousereport.setHasFixedSize(false)
        binding.wareHousereport.isNestedScrollingEnabled = true
        getDistrictData()
        wareHouseDetails()
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

    private fun getDistrictData() {
        val jsonObject = JSONObject()
        try {
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

    private fun wareHouseDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("district_id", districtID)
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
            val responseCall: Call<JsonObject> = apiRequest.getWareHouseDetails(requestBody)
            api.postRequest(responseCall, this, 3)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
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
                        val id = districtObject.getInt("id")
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
                UIToastMessage.show(this, response.response)
            }
        }
        if (i == 3 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )
            if (response.status) {
                warehouseAvailabilityJSONArray = response.dataArrays
                totalWareHouse = response.total_available_capacity()
                totalAvailableWareHouse = response.getTotalAvailableWareHouse()
                binding.textTotalWarehouse.text =
                    resources.getString(R.string.total_warehouse) + " " + totalWareHouse
                binding.textAvailableCapacity.text =
                    resources.getString(R.string.total_available_capacity) + " " + totalAvailableWareHouse + " " + resources.getString(
                        R.string.tonnes
                    )
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
                }
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {
        if (i == 1) {
            districtID = s1!!.toInt()
            if (s != null) {
                districtName = s
                wareHouseDetails()
            }
            binding.textViewDistrict.text = s
            warehouseAvailabilityJSONArray = null
            talukaID = 0
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
    }
}