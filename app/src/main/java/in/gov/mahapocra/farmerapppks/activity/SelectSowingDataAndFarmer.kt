package `in`.gov.mahapocra.farmerapppks.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.AppPreferenceManager
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.databinding.ActivitySelectSowingDataAndFarmerBinding
import `in`.gov.mahapocra.farmerapppks.models.response.CropsCategName
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.Date
import java.util.Objects


class SelectSowingDataAndFarmer : AppCompatActivity(), DatePickerRequestListener, ApiCallbackCode {

    private lateinit var binding: ActivitySelectSowingDataAndFarmerBinding
    private var sowingDate: String = ""
    val date = Date()
    var cropId: Int? = 0
    private var farmerId: Int = 0
    private var wotrCropId: String? = null
    private var mName: String? = null
    private var mUrl: String? = null
    private var editCrop: String? = null
    private lateinit var selectedCropList: ArrayList<CropsCategName>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectSowingDataAndFarmerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cropId = intent.getIntExtra("id", 0)
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")
        mName = intent.getStringExtra("mName")
        editCrop = intent.getStringExtra("editCrop")


        binding.relativeLayoutTopBar.textViewHeaderTitle.text =
            resources.getString(R.string.select_sowing_date)
        AppUtility.getInstance().showDisabledFutureDatePicker(
            this,
            date,
            1,
            this
        )
        val source = AppPreferenceManager(this).getString(AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD)
        binding.saveDateForFarmButton.setOnClickListener {
            if (source.equals(AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD)) {
                val intent = Intent(this, CropStageAdvisory::class.java)
                intent.putExtra("dataSavedInLocal", "dataSavedInLocal")
                AppSettings.getInstance().setIntValue(this, AppConstants.tmpCROPID, cropId!!)
                AppSettings.getInstance().setValue(this, AppConstants.tmpWOTRID, wotrCropId)
                AppSettings.getInstance().setValue(this, AppConstants.tmpSOWINGDATE, sowingDate)
                AppSettings.getInstance().setValue(this, AppConstants.tmpMURL, mUrl)
                AppSettings.getInstance().setValue(this, AppConstants.tmpCROPNAME, mName)
                intent.putExtra("editCrop", editCrop)
                startActivity(intent)
            }else if (source.equals(AppConstants.FERTILIZER_CALCULATOR_FROM_DASHBOARD)) {
                val intent = Intent(this, FertilizerCalculatorActivity::class.java)
                intent.putExtra("id", cropId)
                intent.putExtra("wotr_crop_id", wotrCropId)
                intent.putExtra("mUrl", mUrl)
                intent.putExtra("mName", mName)
                intent.putExtra("sowingDate", sowingDate)
                intent.putExtra("editCrop", editCrop)
                startActivity(intent)
            } else {
                saveFarmerSelectedCrop()
            }
        }
        binding.calenderForSowingDate.setOnClickListener {
            AppUtility.getInstance().showDisabledFutureDatePicker(
                this,
                date,
                1,
                this
            )
        }
    }

    private fun saveFarmerSelectedCrop() {
        val jsonObject = JSONObject()
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        if (sowingDate.isEmpty()) {
            UIToastMessage.show(this, resources.getString(R.string.farmer_select_date))
        } else {
            try {
                jsonObject.put("farmer_id", farmerId)
                jsonObject.put("sowing_date", sowingDate)
                jsonObject.put("crop_id", cropId)

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
                val responseCall: Call<JsonObject> = apiRequest.kSaveFarmerSelectedCrop(requestBody)
                api.postRequest(responseCall, this, 1)
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=$e")
                e.printStackTrace()
            }
        }
    }


    override fun onDateSelected(i: Int, day: Int, month: Int, year: Int) {
        if (i == 1) {
            Log.d("i2", day.toString())
            Log.d("i3", month.toString())
            Log.d("i4", year.toString())
            sowingDate = "$day-$month-$year"
            binding.textViewSowingDate.text = sowingDate
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                if (response.response.equals("crop saved")) {
                    var selectedArrayLists: List<Objects>? = null
                    selectedArrayLists = AppSettings.getInstance()
                        ?.getList(this, AppConstants.kFarmerCrop) as? List<Objects>
                    if (selectedArrayLists != null) {
                        selectedCropList = ArrayList()
                        if (selectedArrayLists.equals("selectedArrayLists")) {
                            selectedCropList.add(CropsCategName(cropId!!, mName, mUrl, wotrCropId))
                        } else {
                            val gson = Gson()
                            val element = gson.toJson(
                                selectedArrayLists,
                                object : TypeToken<java.util.ArrayList<CropsCategName?>?>() {}.type
                            )
                            val listType =
                                object : TypeToken<java.util.ArrayList<CropsCategName?>?>() {}.type
                            selectedCropList = Gson().fromJson(element, listType)
                            selectedCropList.add(CropsCategName(cropId!!, mName, mUrl, wotrCropId))
                            AppSettings.getInstance().setList(
                                this, AppConstants.kFarmerCrop,
                                selectedCropList as List<CropsCategName>?
                            )
                        }
                    } else {
                        selectedCropList = ArrayList()
                        selectedCropList.add(CropsCategName(cropId!!, mName, mUrl, wotrCropId))
                        AppSettings.getInstance().setList(
                            this, AppConstants.kFarmerCrop,
                            selectedCropList as List<CropsCategName>?
                        )
                    }
                    Toast.makeText(this, response.response, Toast.LENGTH_SHORT)
                        .show()
                    if (editCrop.equals("EditCrop")) {
                        val intent = Intent(this, CropStageAdvisory::class.java)
                        intent.putExtra("id", cropId)
                        intent.putExtra("mUrl", mUrl)
                        intent.putExtra("mName", mName)
                        intent.putExtra("wotrCropId", wotrCropId)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, DashboardScreen::class.java)
                        startActivity(intent)
                    }
                } else {
                    UIToastMessage.show(this, response.response)
                }

            } else {
                UIToastMessage.show(this, response.response)
            }
        }
    }
}