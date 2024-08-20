package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.api.AppinventorIncAPI
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.*
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.CropAdvisoryAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.fragments.CropAdvisoryFragment
import `in`.gov.mahapocra.farmerapppks.fragments.advisory.CropAdvisorySelection
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
import java.util.*


class CropAdvisory : AppCompatActivity(), ApiCallbackCode, ApiJSONObjCallback, OnMultiRecyclerItemClickListener,AlertListEventListener,
    DatePickerRequestListener {

    lateinit var textViewHeaderTitle: TextView
    lateinit var imageMenushow: ImageView
    lateinit var relativeLayoutTopBar: RelativeLayout
    private var recyclerViewCropList: RecyclerView? = null
    private var cropAdArray: JSONArray? = null
    var languageToLoad: String? = null

    lateinit var textViewDistrict: TextView
    lateinit var textViewTaluka: TextView
    lateinit var textViewCrop: TextView
    lateinit var textViewSowingDate: TextView
    lateinit var nextButton: TextView

    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null
    private var cropJSONArray: JSONArray? = null

    private var districtID: Int = 0
    lateinit var talukaName: String
    private var talukaID: Int = 0
    lateinit var districtName: String
    private var cropID: Int = 0
    lateinit var cropName: String
    var sowingDate: String = ""
    val date = Date()

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_advisory)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@CropAdvisory).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        init()
        onClick()
        imageMenushow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.crop_advisory)


    }
    private fun init()
    {
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
       imageMenushow = findViewById(R.id.imageMenushow)
        relativeLayoutTopBar = findViewById(R.id.relativeLayoutTopBar)
//        textViewDistrict = findViewById(R.id.textViewDistrict)
//        textViewTaluka = findViewById(R.id.textViewTaluka)
//        textViewCrop = findViewById(R.id.textViewCrop)
//        textViewSowingDate = findViewById(R.id.textViewSowingDate)
     //  nextButton = findViewById(R.id.nextButton)
        getDistrictData()

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, CropAdvisorySelection())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun onClick()
    {
        imageMenushow.setOnClickListener(View.OnClickListener {
//            val intent = Intent(this, DashboardScreen::class.java)
//            startActivity(intent)
        })
//        textViewDistrict.setOnClickListener {
//            showDistrict()
//        }
//        textViewTaluka.setOnClickListener {
//            showTaluka()
//        }
//        textViewCrop.setOnClickListener {
//            showCrop()
//        }
//        textViewSowingDate.setOnClickListener {
//            AppUtility.getInstance().showDisabledFutureDatePicker(
//                this@CropAdvisory,
//                date,
//                1,
//                this@CropAdvisory
//            )
//        }
//        nextButton.setOnClickListener {
////            val cropAdvisoryDetails = Intent(this@CropAdvisory, CropAdvisoryDetaitsAndFeedback::class.java)
////            startActivity(cropAdvisoryDetails)
//
//
//        }
    }
    private fun getDistrictData() {
        val jsonObject = JSONObject()
        try {
            //jsonObject.put("SecurityKey", APIServices.SSO_KEY)
            jsonObject.put("lang", languageToLoad)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getDistrictList(requestBody)
            DebugLog.getInstance().d("Weather::param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param2=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("Weather::param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
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
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
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

    private fun fetchCropMasterData() {
        try {
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getCropList()
            DebugLog.getInstance().d("Weather::param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "Weather::param2=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
            api.postRequest(responseCall, this, 4)
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

    private fun getCropList(talukaID : Int ) {
       // var url: String = APIServices.kCropAdvisory
        cropAdArray = null
        var url: String = APIServices.kCropAdvisoryNew+talukaID
//        var url: String = "https://ilab-ffs-api.mahapocra.gov.in/v23/farmService/get-all-crops"

        Log.d("param", APIServices.FFS_BASE_URL + url)
        val api = AppinventorIncAPI(this, APIServices.FFS_BASE_URL, APIServices.SSO_KEY, "", true)
        api.getRequestData(url, this, 3)
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

    private fun showCrop() {
        if (cropJSONArray == null) {
            if (talukaID > 0) {
                fetchCropMasterData()
            } else {
                UIToastMessage.show(
                    this,
                    resources.getString(R.string.farmer_select_taluka)
                )
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(
                    cropJSONArray,
                    3,
                    getString(R.string.select_crop),
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

    override fun onFailure(th: Throwable?, i: Int) {
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

        if (i == 4 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status) {
                cropJSONArray = response.getdataArray()
                Log.d("Weather::talukaArray", cropJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
//        if (i == 3 && jSONObject != null) {
//            val response = ResponseModel(jSONObject)
//            if (response.status) {
//                cropAdArray = response.getdataArray()
//                Log.d("CropAdvisory=","cropjsonArray="+ cropAdArray.toString())
//                val adaptorDbtActivityGrp = CropAdvisoryAdapter(this, this, cropAdArray,languageToLoad)
//                recyclerViewCropList?.setLayoutManager(
//                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//                )
//                recyclerViewCropList?.setAdapter(adaptorDbtActivityGrp)
//                adaptorDbtActivityGrp.notifyDataSetChanged()
//            } else {
//                UIToastMessage.show(this, response.response)
//            }
//        }
        if (i == 3) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    recyclerViewCropList?.setVisibility(View.VISIBLE)
                    cropAdArray = response.getdataArray()
                    Log.d("CropAdvisory=","cropjsonArray="+ cropAdArray.toString())

                    val adaptorDbtActivityGrp = CropAdvisoryAdapter(this, this, cropAdArray,languageToLoad)
                    recyclerViewCropList?.setLayoutManager(
                        LinearLayoutManager(
                            this,
                            LinearLayoutManager.VERTICAL,
                            false))
                    recyclerViewCropList?.setAdapter(adaptorDbtActivityGrp)
                    adaptorDbtActivityGrp.notifyDataSetChanged()
                }
                else {
                    recyclerViewCropList?.setVisibility(View.GONE)
                UIToastMessage.show(this, response.response)
               }
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

            talukaID = 0
            textViewTaluka.setText("")
            textViewTaluka.setHint(resources.getString(R.string.farmer_select_taluka))
            textViewTaluka.setHintTextColor(Color.GRAY)
            cropJSONArray = null
            cropID = 0
            textViewCrop.setText("")
            textViewCrop.setHint(resources.getString(R.string.select_crop))
            textViewCrop.setHintTextColor(Color.GRAY)
        }
        if (i == 2) {
            try {
                talukaID = s1!!.toInt()
                if (s != null) {
                    talukaName = s
                    fetchCropMasterData()
                }
                textViewTaluka.setText(s)
                cropID = 0
                textViewCrop.setText("")
                textViewCrop.setHint(resources.getString(R.string.select_crop))
                textViewCrop.setHintTextColor(Color.GRAY)
            } catch (ex: NumberFormatException) {
                System.err.println("Invalid string in argumment")
                Toast.makeText(this@CropAdvisory, "Data not Found...", Toast.LENGTH_SHORT).show();
            }
        }
        if (i == 3) {
            try {
                cropID = s1!!.toInt()
                if (s != null) {
                    cropName = s
                }
                textViewCrop.setText(s)

            } catch (ex: NumberFormatException) {
                Toast.makeText(this@CropAdvisory, "Data not Found...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {

        val jsonObject = obj as JSONObject
        val cropId: String =jsonObject.getString("id")
        val cropNameEn: String =jsonObject.getString("name")
        val cropNameMr: String =jsonObject.getString("name_mr")
        val cropSap: String =jsonObject.getString("cropsap")
        Log.d("CropId page 1", cropId)
        Log.d("talukaID page 1", talukaID.toString())
        UIToastMessage.show(this,"You selected "+cropNameEn)
            val intent = Intent(this, CropAdvisoryDetails::class.java)
            intent.putExtra("CropId", cropId)
            intent.putExtra("CropNameEn", cropNameEn)
            intent.putExtra("CropNameMr", cropNameMr)
            intent.putExtra("CropSap", cropSap)
            intent.putExtra("TalukaId", talukaID.toString())
            startActivity(intent)
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
}