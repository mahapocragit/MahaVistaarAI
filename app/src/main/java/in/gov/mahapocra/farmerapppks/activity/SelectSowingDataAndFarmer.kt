package `in`.gov.mahapocra.farmerapppks.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.fragment.advisory.rating2
import `in`.gov.mahapocra.farmerapppks.models.response.CropsCategName
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.Date
import java.util.Objects


class SelectSowingDataAndFarmer : AppCompatActivity(), DatePickerRequestListener, ApiCallbackCode {

    lateinit var textViewSowingDate: TextView
    lateinit var calenderForSowingDate: LinearLayout
    private lateinit var farmName: EditText
    lateinit var saveTextBt: Button
    var sowingDate: String = ""
    val date = Date()
    var cropId: Int? = 0
    var wotrCropId:  String? =null
    var mName: String? =null
    var mUrl: String? =null
    var selectedCropData: String? =null
    var editCrop: String? =null
    lateinit var selectedCropList: ArrayList<CropsCategName>
    lateinit var textViewHeaderTitle: TextView

    private  var farmerId:Int=0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_sowing_data_and_farmer)

        textViewSowingDate = findViewById(R.id.textViewSowingDate)
        calenderForSowingDate = findViewById(R.id.calenderForSowingDate)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        saveTextBt = findViewById(R.id.saveFarm)
        //farmName = findViewById(R.id.farmName)


        cropId = intent.getIntExtra("id",0)
       wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")
        mName = intent.getStringExtra("mName")
        editCrop = intent.getStringExtra("editCrop")

        textViewHeaderTitle.setText(resources.getString(R.string.select_sowing_date))

//        calenderForSowingDate.setOnClickListener {
//            AppUtility.getInstance().showDisabledFutureDatePicker(
//                this,
//                date,
//                1,
//                this
//            )
//        }

        AppUtility.getInstance().showDisabledFutureDatePicker(
            this,
            date,
            1,
            this
        )







        saveTextBt.setOnClickListener {
          //  var farmNamr: String = farmName.text.toString()

        saveFarmerSelectedCrop()


        }
    }

    private fun saveFarmerSelectedCrop() {
        val jsonObject = JSONObject()
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        if (sowingDate.equals("")){
            UIToastMessage.show(this, resources.getString(R.string.farmer_select_date))
        }else {
            try {
                jsonObject.put("farmer_id", farmerId)
                jsonObject.put("sowing_date", sowingDate)
                jsonObject.put("crop_id", cropId)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.kSaveFarmerSelectedCrop(requestBody)
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
    }


    override fun onDateSelected(i: Int, day: Int, month: Int, year: Int) {
                if (i == 1) { 
            Log.d("i2", day.toString())
            Log.d("i3", month.toString())
            Log.d("i4", year.toString())
            sowingDate = "" + year + "-" + month + "-" + day
            textViewSowingDate.text = sowingDate
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                if (response.response.equals("crop saved")){
                    var selectedArrayLists : List<Objects>? = null
                    selectedArrayLists =  AppSettings.getInstance()?.getList(this, AppConstants.kFarmerCrop) as?  List<Objects>
                    if (!(selectedArrayLists == null)) {
                        Log.d("selectedArrayListsss", selectedArrayLists.toString())
                        selectedCropList = ArrayList<CropsCategName>()
                        if (selectedArrayLists.equals("selectedArrayLists")) {
                            selectedCropList.add(CropsCategName(cropId!!, mName, mUrl,wotrCropId))
                        } else {
                            val gson = Gson()
                            val element = gson.toJson(
                                selectedArrayLists,
                                object : TypeToken<java.util.ArrayList<CropsCategName?>?>() {}.type
                            )
                            val listType =
                                object : TypeToken<java.util.ArrayList<CropsCategName?>?>() {}.type
                            selectedCropList = Gson().fromJson(element, listType)
                            selectedCropList.add(CropsCategName(cropId!!, mName, mUrl,wotrCropId))
                            Log.d("selectedCropList", selectedCropList.toString())
                            AppSettings.getInstance().setList(this, AppConstants.kFarmerCrop,
                                selectedCropList as List<CropsCategName>?
                            )
                        }
                    }else {
                        selectedCropList = ArrayList<CropsCategName>()
                        selectedCropList.add(CropsCategName(cropId!!, mName, mUrl,wotrCropId))
                        AppSettings.getInstance().setList(
                            this, AppConstants.kFarmerCrop,
                            selectedCropList as List<CropsCategName>?
                        )
                    }
            Toast.makeText(this, response.response, Toast.LENGTH_SHORT)
                .show()
                    if(editCrop.equals("EditCrop")){
                        val intent = Intent(this, CropStageAdvisory::class.java)
                        intent.putExtra("id", cropId)
                        intent.putExtra("mUrl", mUrl)
                        intent.putExtra("mName",mName)
                        intent.putExtra("wotrCropId",wotrCropId)
                        startActivity(intent)
                    }else {
                        val intent = Intent(this, DashboardScreen::class.java)
                        startActivity(intent)
                    }
                }else{
                    UIToastMessage.show(this, response.response)
                }

            } else {
                UIToastMessage.show(this, response.response)
            }
        }
    }

}