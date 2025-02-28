package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.sidenavigation

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppHelper
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import `in`.gov.mahapocra.farmerapppks.activity.UpcomingEvents
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class TrainingLocationSelection : AppCompatActivity(), OnMultiRecyclerItemClickListener,
    AlertListEventListener, ApiCallbackCode {

    private var locationLLayout: LinearLayout? = null
    private var locationJsonArray: JSONArray? = null
    private var locationTView: TextView? = null

    private var textViewHeaderTitle: TextView? = null
    private var imageMenushow: ImageView? = null

    lateinit var locationName: String
    private var locationID: Int = 0

    lateinit var subDivisionName: String
    private var subdivisionID: Int = 0


    private var districtID: Int = 0

    private var districtJSONArray: JSONArray? = null

    lateinit var districtName: String
    lateinit var languageToLoad: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@TrainingLocationSelection).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@TrainingLocationSelection))
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_training_location_selection)
        init()
        setConfiguration()
    }

    private fun init() {
        locationLLayout = findViewById(R.id.locationLLayout) as LinearLayout
        // dbtActivityGrp.setHasFixedSize(false)
        locationTView=findViewById(R.id.locationTView)
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle);
        imageMenushow=findViewById(R.id.imageMenushow);
    }

    private fun setConfiguration() {
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@TrainingLocationSelection).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@TrainingLocationSelection))
            languageToLoad = "en"
        }
        locationJsonArray = AppHelper.getInstance().getDistMemLocationJsonArray()
        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        imageMenushow?.setVisibility(View.VISIBLE)
        textViewHeaderTitle?.setText(R.string.upcoming_events_location)

        Log.d("locationJsonArray",locationJsonArray.toString())
        locationTView?.setOnClickListener(View.OnClickListener {
//            if (locationJsonArray != null) {
//                AppUtility.getInstance().showListDialogIndex(
//                    locationJsonArray,
//                    1,
//                    "Select Location",
//                    "name",
//                    "id",
//                    this,
//                    this
//                )
//            }

            showDistrict()
        })

        getDistrictData()

        imageMenushow?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)

        })
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
           // jsonObject.put("SecurityKey", APIServices.SSO_KEY)
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
                Log.d("Weather::districtArray", districtJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {
//        if (i == 1) {
//            locationID = s1!!.toInt()
////            if (s != null) {
////                locationName = s
////            }
//
//            if (locationID == 2 ){
//            val intent=Intent(this,UpcomingEvents::class.java)
//                startActivity(intent)
//            }
//            else{
//
//            }
//        }

        if (i == 1) {

            districtID = s1!!.toInt()
            if (s != null) {
                districtName = s
            }

            val intent=Intent(this, UpcomingEvents::class.java)
            intent.putExtra("distID",districtID)
                startActivity(intent)

          //  textViewDistrict.setText(s)
//            if (districtID > 0) {
//                fetchTalukaMasterData()
//            }
//            weatherForecastJSONArray = null
//            weatherPrevioustJSONArray = null
//            talukaID = 0
//            textViewTaluka.setText("")
//            textViewTaluka.setHint(resources.getString(R.string.farmer_select_taluka))
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
      //  finish()
    }


}