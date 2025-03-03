package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.sidenavigation.training

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.ui.adapters.AdaptorComingEvent
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class UpcomingEvents : AppCompatActivity(), ApiCallbackCode, ApiJSONObjCallback,
    OnMultiRecyclerItemClickListener {

    private var districtID: Int = 0
    private var disId: Int = 0

    private var searchEText: EditText? = null
    private var searchRLayout: RelativeLayout? = null
    private var tmsComingEventRView: RecyclerView? = null

    private var scheduleJSONArray: JSONArray? = null

    private var textViewHeaderTitle: TextView? = null
    private var imgBackArrow: ImageView? = null
    var languageToLoad: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@UpcomingEvents).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_upcoming_events_details)
        init()
        setConfiguration()

    }

    private fun init() {
        searchEText = findViewById(R.id.searchEText)
        searchRLayout = findViewById(R.id.searchRLayout)
        tmsComingEventRView = findViewById<View>(R.id.psComingEventRView) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tmsComingEventRView!!.setLayoutManager(linearLayoutManager)
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle);
        imgBackArrow=findViewById(R.id.imgBackArrow);

    }

    private fun setConfiguration() {

        imgBackArrow?.setVisibility(View.VISIBLE)
        textViewHeaderTitle?.setText(R.string.upcoming_events_location)

        if (intent.extras != null) {
            disId = intent.extras!!.getInt("distID")
            }



       // districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)

        getAllSubDivScheduledEventList(disId)

        imgBackArrow?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, TrainingLocationSelection::class.java)
            startActivity(intent)
            finish()
        })

    }

    private fun getAllSubDivScheduledEventList(
        subDivisionId: Int
    ) {
        val jsonObject = JSONObject()
        try {
//            jsonObject.put("schedule_id", "")
//            jsonObject.put("user_id", userID)
//            jsonObject.put("role_id", roleId)
//            jsonObject.put("level", center)
//            jsonObject.put("district_id", distId)
            jsonObject.put("dist_id", subDivisionId)
            //jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val requestBody: RequestBody =
            AppUtility.getInstance().getRequestBody(jsonObject.toString())
        val api = AppInventorApi(
            this,
            APIServices.TMS,
            "",
            AppString(this).getkMSG_WAIT(),
            true
        )
        val retrofit: Retrofit = api.getRetrofitInstance()
        val apiRequest: APIRequest = retrofit.create(
            APIRequest::class.java)
        val responseCall: Call<JsonObject> = apiRequest.getSubdivisionUpcomingList(requestBody)
        api.postRequest(responseCall, this, 2)
        DebugLog.getInstance()
            .d("get_Scheduled_by_sub_div_param=" + responseCall.request().toString())
        DebugLog.getInstance().d(
            "get_Scheduled_by_sub_div_param=" + AppUtility.getInstance()
                .bodyToString(responseCall.request())
        )
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 2 && jSONObject != null) {
            val responseModel =
                ResponseModel(
                    jSONObject
                )
            if (responseModel.status) {
                if (jSONObject != null) {
                    scheduleJSONArray = responseModel.getdataArray()
                }

                Log.d("scheduleJSONArray", scheduleJSONArray.toString())
               val  adaptorComingEvent = AdaptorComingEvent(this, scheduleJSONArray, false, this)

                tmsComingEventRView?.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                )
                tmsComingEventRView?.setAdapter(adaptorComingEvent)
                adaptorComingEvent.notifyDataSetChanged()
            } else {
               // adaptorPsComingEvent = AdaptorPsComingEvent(this, JSONArray(), false, this)
              //  psComingEventRView.setAdapter(adaptorPsComingEvent)
                UIToastMessage.show(this, responseModel.getMsg())
            }
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onFailure(th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 2) {
            val jsonObject = obj as JSONObject
            val evntSchId: String =jsonObject.getString("schedule_event_id")
            Log.d("evntSchId", evntSchId)
            val intent = Intent(this, UpcomingEventsDetails::class.java)
            intent.putExtra("id_value", evntSchId)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, TrainingLocationSelection::class.java)
        startActivity(intent)
        finish()
    }


}