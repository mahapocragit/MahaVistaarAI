package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.sidenavigation.training

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.ui.adapters.AdaptorSelectedCoCoord
import `in`.gov.mahapocra.farmerapppks.ui.adapters.AdaptorSelectedCoordinator
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.ApUtil
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import `in`.gov.mahapocra.farmerapppks.data.model.TrainingDetailModel
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class UpcomingEventsDetails : AppCompatActivity(), ApiCallbackCode, ApiJSONObjCallback,
    OnMultiRecyclerItemClickListener {

    private var textViewHeaderTitle: TextView? = null
    private var imgBackArrow: ImageView? = null

    private val progress: ProgressDialog? = null
    private val actionType = "View"

    private var schId = ""

    private var eventTypeLLayout: LinearLayout? = null
    private var eventTypeTextView: TextView? = null
    private var eventTypeId = ""

    private var eventSubTypeLLayout: LinearLayout? = null
    private var eventSubTypeTView: TextView? = null
    private var eventSubTypeId = ""
    private var eventSubType = ""

    private var eventTitleLLayout: LinearLayout? = null
    private var eventStartDateTextView: TextView? = null
    private var eventStartDate = ""
    private var eventEndDateTextView: TextView? = null
    private var eventEndDate = ""
    private var eventStartTimeTextView: TextView? = null
    private var eventEndTimeTextView: TextView? = null
    private var eventStartTime: String? = null
    private var eventEndTime: String? = null

    private var eventReportingDateTView: TextView? = null
    private var eventReportingDate = ""

    private var eventReportingTimeTView: TextView? = null
    private var eventReportingTime: String? = null

    // For session
    private var sessionData: JSONArray? = null

    // VCRMC (GP)
    private var selectVCRMCRLayout: RelativeLayout? = null
    private var sledVCRMCCountTView: TextView? = null
    private var sledVCRMCMoreTView: TextView? = null

    private var sledVCRMCTView: TextView? = null
    private var sledGPLLayout: LinearLayout? = null // Selected VCRMC
    private var sledGPRView: RecyclerView? = null

    // Farmer
    private var selectFarmerRLayout: RelativeLayout? = null
    private var sledFarmerCountTView: TextView? = null
    private var sledFarmerMoreTView: TextView? = null

    private var sledFarmerLLayout: LinearLayout? = null // Selected Farmer
    private var sledFarmerRView: RecyclerView? = null
    private var sledFarmerTView: TextView? = null

    // Pocra official
    private var selectPOMRLayout: RelativeLayout? = null
    private var sledPOMCountTView: TextView? = null
    private var sledPOMMoreTView: TextView? = null

    private var sledPOMemberLLayout: LinearLayout? = null // Selected Facilitator
    private var sledPOMemberRView: RecyclerView? = null
    private var sledPOMemberTView: TextView? = null

    // CA Resource_person
    private var selectCaResourceRLayout: RelativeLayout? = null
    private var selectCaResourceCountTView: TextView? = null
    private var selectCaResourceMoreTView: TextView? = null

    // Other Participants
    private var selectOtherRLayout: RelativeLayout? = null
    private var sledOtherCountTView: TextView? = null
    private var sledOtherMoreTView: TextView? = null

    private var sledOthParticipantLLayout: LinearLayout? = null // Selected Facilitator
    private var sledOthParticipantRView: RecyclerView? = null
    private var sledOthParticipantTView: TextView? = null

    private var participantsEditText: TextView? = null
    private var memCount: String? = null
    private var oVenueLocationTView: TextView? = null
    private var vDistLLayout: LinearLayout? = null
    private var vDistTView: TextView? = null

    private var vDistId = ""
    private var venueLLayout: LinearLayout? = null
    private var venueLocationTView: TextView? = null
    private var eventVenueId = ""
    private var eventVenue = ""
    private var eventOtherVenue: String? = null
    private var venueELLayout: LinearLayout? = null

    private var kvkLLayout: LinearLayout? = null
    private var kvkTView: TextView? = null
    private var desigLLayout: LinearLayout? = null

    // Coordinator
    private var coordinatorLLayout: LinearLayout? = null
    private var sledCordLLayout: LinearLayout? = null
    private var sledCordRView: RecyclerView? = null
    private var coordinatorTextView: TextView? = null
    private var sledCordJSONArray: JSONArray? = null
    private var adaptorSelectedCoordinator: AdaptorSelectedCoordinator? = null
    private var sledCordId: JSONArray? = null

    private var coCordLinearLayout: LinearLayout? = null
    private var sledCoCoordLLayout: LinearLayout? = null
    private var sledCoCoordRView: RecyclerView? = null
    private var coCordTextView: TextView? = null
    private var sledCoCoordJSONArray: JSONArray? = null
    private var adaptorSelectedCoCoord: AdaptorSelectedCoCoord? = null
    private var sledCoCoordinatorId: JSONArray? = null
    private var sledCoCoordinatorArray: JSONArray? = null

    private var selectshgRLayout: RelativeLayout? = null
    private var sledshgCountTView: TextView? = null
    private var sledshgMoreTView: TextView? = null
    private var sledShgTView: TextView? = null
    private var sledshgLLayout: LinearLayout? = null
    private var sledshgRView: RecyclerView? = null
    private var selectfpcRLayout: RelativeLayout? = null
    private var sledfpcCountTView: TextView? = null
    private var sledfpcMoreTView: TextView? = null
    private var sledFpcTView: TextView? = null
    private var sledFpcLLayout: LinearLayout? = null
    private var sledFpcRView: RecyclerView? = null
    private var selectFGRLayout: RelativeLayout? = null
    private var sledFGCountTView: TextView? = null
    private var sledFGMoreTView: TextView? = null
    private var sledFGTView: TextView? = null
    private var sledFGLLayout: LinearLayout? = null
    private var sledFGRView: RecyclerView? = null
    var languageToLoad: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upcoming_events_details2)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@UpcomingEventsDetails).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        init()
        setConfiguration()
    }

    private fun init() {

        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imgBackArrow = findViewById(R.id.imgBackArrow)
        // Initialization of VIEWs
        eventTypeLLayout = findViewById<View>(R.id.eventTypeLinearLayout) as LinearLayout
        eventTypeTextView = findViewById<View>(R.id.eventTypeTextView) as TextView
        eventSubTypeLLayout = findViewById<View>(R.id.eventSubTypeLLayout) as LinearLayout
        eventSubTypeTView = findViewById<View>(R.id.eventSubTypeTView) as TextView
        eventTitleLLayout = findViewById<View>(R.id.eventTitleLLayout) as LinearLayout
        eventStartDateTextView = findViewById<View>(R.id.eventStartDateTextView) as TextView
        eventEndDateTextView = findViewById<View>(R.id.eventEndDateTextView) as TextView
        eventStartTimeTextView = findViewById<View>(R.id.eventStartTimeTextView) as TextView
        eventEndTimeTextView = findViewById<View>(R.id.eventEndTimeTextView) as TextView
        eventReportingDateTView = findViewById<View>(R.id.eventReportingDateTView) as TextView
        eventReportingTimeTView = findViewById<View>(R.id.eventReportingTimeTView) as TextView
        // VCRMC (GP)
        selectVCRMCRLayout = findViewById<View>(R.id.selectVCRMCRLayout) as RelativeLayout
        sledVCRMCCountTView = findViewById<View>(R.id.sledVCRMCCountTView) as TextView
        sledVCRMCMoreTView = findViewById<View>(R.id.sledVCRMCMoreTView) as TextView
        sledVCRMCTView = findViewById<View>(R.id.sledVCRMCTView) as TextView
        sledGPLLayout = findViewById<View>(R.id.sledGPLLayout) as LinearLayout
        sledGPRView = findViewById<View>(R.id.sledGPRView) as RecyclerView
        val sledGPLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sledGPRView!!.setLayoutManager(sledGPLayoutManager)
        // Farmer
        selectFarmerRLayout = findViewById<View>(R.id.selectFarmerRLayout) as RelativeLayout
        sledFarmerCountTView = findViewById<View>(R.id.sledFarmerCountTView) as TextView
        sledFarmerMoreTView = findViewById<View>(R.id.sledFarmerMoreTView) as TextView
        sledFarmerLLayout = findViewById<View>(R.id.sledFarmerLLayout) as LinearLayout
        sledFarmerTView = findViewById<View>(R.id.sledFarmerTView) as TextView
        sledFarmerRView = findViewById<View>(R.id.sledFarmerRView) as RecyclerView
        val sledFarmerLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sledFarmerRView!!.setLayoutManager(sledFarmerLayoutManager)
        // SHG
        selectshgRLayout = findViewById<View>(R.id.selectshgRLayout) as RelativeLayout
        sledshgCountTView = findViewById<View>(R.id.sledshgCountTView) as TextView
        sledshgMoreTView = findViewById<View>(R.id.sledshgMoreTView) as TextView
        sledShgTView = findViewById<View>(R.id.sledShgTView) as TextView
        sledshgLLayout = findViewById<View>(R.id.sledshgLLayout) as LinearLayout
        sledshgRView = findViewById<View>(R.id.sledshgRView) as RecyclerView
        val shgLL = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sledshgRView!!.setLayoutManager(shgLL)
        // FPC
        selectfpcRLayout = findViewById<View>(R.id.selectfpcRLayout) as RelativeLayout
        sledfpcCountTView = findViewById<View>(R.id.sledfpcCountTView) as TextView
        sledfpcMoreTView = findViewById<View>(R.id.sledfpcMoreTView) as TextView
        sledFpcTView = findViewById<View>(R.id.sledFpcTView) as TextView
        sledFpcLLayout = findViewById<View>(R.id.sledFpcLLayout) as LinearLayout
        sledFpcRView = findViewById<View>(R.id.sledFpcRView) as RecyclerView
        val fpcLL = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sledFpcRView!!.setLayoutManager(fpcLL)
        // FG
        selectFGRLayout = findViewById<View>(R.id.selectFGRLayout) as RelativeLayout
        sledFGCountTView = findViewById<View>(R.id.sledFGCountTView) as TextView
        sledFGMoreTView = findViewById<View>(R.id.sledFGMoreTView) as TextView
        sledFGTView = findViewById<View>(R.id.sledFGTView) as TextView
        sledFGLLayout = findViewById<View>(R.id.sledFGLLayout) as LinearLayout
        sledFGRView = findViewById<View>(R.id.sledFGRView) as RecyclerView
        val FGLL = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sledFGRView!!.setLayoutManager(FGLL)
        // PoCRA Official Member
        selectPOMRLayout = findViewById<View>(R.id.selectPOMRLayout) as RelativeLayout
        sledPOMCountTView = findViewById<View>(R.id.sledPOMCountTView) as TextView
        sledPOMMoreTView = findViewById<View>(R.id.sledPOMMoreTView) as TextView
        sledPOMemberLLayout = findViewById<View>(R.id.sledPOMemberLLayout) as LinearLayout
        sledPOMemberTView = findViewById<View>(R.id.sledPOMemberTView) as TextView
        sledPOMemberRView = findViewById<View>(R.id.sledPOMemberRView) as RecyclerView
        val sledPOMemberLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sledPOMemberRView!!.setLayoutManager(sledPOMemberLayoutManager)
        // CA Resource Person
        selectCaResourceRLayout = findViewById<View>(R.id.selectCaResourceRLayout) as RelativeLayout
        selectCaResourceCountTView = findViewById<View>(R.id.selectCaResourceCountTView) as TextView
        selectCaResourceMoreTView = findViewById<View>(R.id.selectCaResourceMoreTView) as TextView
        // Other Participants
        selectOtherRLayout = findViewById<View>(R.id.selectOtherRLayout) as RelativeLayout
        sledOtherCountTView = findViewById<View>(R.id.sledOtherCountTView) as TextView
        sledOtherMoreTView = findViewById<View>(R.id.sledOtherMoreTView) as TextView
        sledOthParticipantLLayout =
            findViewById<View>(R.id.sledOthParticipantLLayout) as LinearLayout
        sledOthParticipantTView = findViewById<View>(R.id.sledOthParticipantTView) as TextView
        sledOthParticipantRView = findViewById<View>(R.id.sledOthParticipantRView) as RecyclerView
        val sledOthParticipantLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sledOthParticipantRView!!.setLayoutManager(sledOthParticipantLayoutManager)

        // For Participants Count
        participantsEditText = findViewById<View>(R.id.participantsEditText) as EditText
        desigLLayout = findViewById<View>(R.id.desigLLayout) as LinearLayout
        // For Selected Coordinator
        coordinatorLLayout = findViewById<View>(R.id.coordinatorLLayout) as LinearLayout
        sledCordLLayout = findViewById<View>(R.id.sledCordLLayout) as LinearLayout
        sledCordRView = findViewById<View>(R.id.sledCordRView) as RecyclerView
        val sledCordLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sledCordRView!!.setLayoutManager(sledCordLayoutManager)
        coordinatorTextView = findViewById<View>(R.id.coordinatorTextView) as TextView
        // For Selected Co-coordinator
        coCordLinearLayout = findViewById<View>(R.id.coCordLinearLayout) as LinearLayout
        sledCoCoordLLayout = findViewById<View>(R.id.sledCoCoordLLayout) as LinearLayout
        sledCoCoordRView = findViewById<View>(R.id.sledCoCoordRView) as RecyclerView
        val sledCoCordLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sledCoCoordRView!!.setLayoutManager(sledCoCordLayoutManager)
        coCordTextView = findViewById<View>(R.id.coCordTextView) as TextView
        // For Venue District
        vDistLLayout = findViewById<View>(R.id.vDistLLayout) as LinearLayout
        vDistTView = findViewById<View>(R.id.vDistTView) as TextView
        // For Venue
        venueLLayout = findViewById<View>(R.id.venueLLayout) as LinearLayout
        venueLocationTView = findViewById<View>(R.id.venueLocationTView) as TextView
        kvkLLayout = findViewById<View>(R.id.kvkLLayout) as LinearLayout
        kvkTView = findViewById<View>(R.id.kvkTView) as TextView
        venueELLayout = findViewById<View>(R.id.venueELLayout) as LinearLayout
        oVenueLocationTView = findViewById<View>(R.id.oVenueLocationTView) as TextView
    }

    private fun setConfiguration() {
        imgBackArrow?.visibility = View.VISIBLE
        textViewHeaderTitle?.setText(R.string.event_details)
        imgBackArrow?.setOnClickListener {
            val intent = Intent(this, TrainingLocationSelection::class.java)
            startActivity(intent)
            finish()
        }

        val schId: String? = intent.getStringExtra("id_value")
        if (schId != null) {
            getScheduledEventBySchId(schId)
        }
    }

    private fun getScheduledEventBySchId(schId: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("schedule_id", schId)
            jsonObject.put("user_id", "")
            jsonObject.put("role_id", "")
            jsonObject.put("level", "")
            jsonObject.put("api_key", "a910d2ba49ef2e4a74f8e0056749b10d")
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
        val responseCall: Call<JsonObject> = apiRequest.psGetEventDetailRequest(requestBody)
        api.postRequest(responseCall, this, 5)
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 5 && jSONObject != null) {
            // get Event detail Response
            val responseModel =
                ResponseModel(
                    jSONObject
                )
            if (responseModel.status) {
                val eventJSONArray: JSONArray = responseModel.getdataArray()
                setScheduledEventData(eventJSONArray)
            } else {
                // UIToastMessage.show(this, responseModel.msg)
            }
        }
    }

    private fun setScheduledEventData(eventJsonArray: JSONArray) {
        // Getting Data
        try {
            val eventDetailJson = eventJsonArray.getJSONObject(0)
            val trDModel = TrainingDetailModel(eventDetailJson)
            schId = trDModel.id
            eventTypeId = trDModel.event_type
            val event_type_name: String = trDModel.event_type_name
            eventSubTypeLLayout!!.visibility = View.VISIBLE
            eventSubTypeId = trDModel.event_sub_type_id
            eventSubType = trDModel.event_sub_type_name
            if (eventSubType.equals("Others", ignoreCase = true)) {
                eventTitleLLayout!!.visibility = View.VISIBLE
            } else {
                eventTitleLLayout!!.visibility = View.GONE
            }
            eventSubTypeTView!!.text = eventSubType
            val title: String = trDModel.title
            memCount = trDModel.participints
            eventStartTime = trDModel.start_date
            eventStartDate = ApUtil.getDateYMDByTimeStamp(eventStartTime)
            eventEndTime = trDModel.end_date
            eventEndDate = ApUtil.getDateYMDByTimeStamp(eventEndTime)
            eventStartTimeTextView?.text = trDModel.event_start_time
            eventEndTimeTextView?.text = trDModel.event_end_time
            val eventReportDate: String = trDModel.reporting_date
            if (!eventReportDate.equals("", ignoreCase = true)) {
                eventReportingDate = ApUtil.getDateYMDByTimeStamp(eventReportDate)
                val dispReportDate: String = ApUtil.getDateByTimeStamp(eventReportDate)
                eventReportingDateTView!!.text = dispReportDate
            }
            eventReportingTime = trDModel.reporting_time
            eventReportingTimeTView!!.text = eventReportingTime
            vDistId = trDModel.district_id
            val districtName: String = trDModel.district_name
            vDistTView!!.text = districtName
            eventVenueId = trDModel.venue
            eventVenue = trDModel.venue_name
            eventOtherVenue = trDModel.other_venue
            if (eventVenueId.equals("1", ignoreCase = true) || eventVenueId.equals(
                    "64",
                    ignoreCase = true
                )
            ) {
                venueELLayout!!.visibility = View.VISIBLE
                oVenueLocationTView!!.text = eventOtherVenue
                kvkLLayout!!.visibility = View.GONE
            } else if (eventVenueId.equals("2", ignoreCase = true)) {
                kvkLLayout!!.visibility = View.VISIBLE
                kvkTView!!.text = eventOtherVenue
                venueELLayout!!.visibility = View.GONE
            } else {
                kvkLLayout!!.visibility = View.GONE
                venueELLayout!!.visibility = View.GONE
            }
            venueLocationTView!!.text = eventVenue
            eventTypeTextView!!.text = event_type_name
//            eventTitleEditText!!.text = title
            val dispEventStartDate: String = ApUtil.getDateByTimeStamp(eventStartTime)
            eventStartDateTextView!!.text = dispEventStartDate
            val dispEventEndDate: String = ApUtil.getDateByTimeStamp(eventEndTime)
            eventEndDateTextView!!.text = dispEventEndDate
            participantsEditText!!.text = memCount

            // For Session Detail
            sessionData = trDModel.session


            // For Co-coordinator
            var coCoordinatorArray: JSONArray? = null
            val coCoordArray: JSONArray = trDModel.co_coordinators
            val othCoCoordArray: JSONArray = trDModel.resource_person
            if (othCoCoordArray != null && othCoCoordArray.length() > 0) {
                coCoordinatorArray = othCoCoordArray
                if (coCoordinatorArray != null) {
                    val coCoordSled = JSONArray()
                    for (ri in 0 until coCoordinatorArray.length()) {
                        val coCoJSON = JSONObject()
                        val cCJosn = coCoordinatorArray.getJSONObject(ri)
                        val cCId = cCJosn.getString("rp_id")
                        val cCFName = cCJosn.getString("first_name")
                        val cCMName = cCJosn.getString("middle_name")
                        val cCLName = cCJosn.getString("last_name")
                        coCoJSON.put("id", cCId)
                        coCoJSON.put("role_id", "0")
                        coCoJSON.put("first_name", cCFName)
                        coCoJSON.put("middle_name", cCMName)
                        coCoJSON.put("last_name", cCLName)
                        coCoJSON.put("is_selected", "1")
                        coCoordSled.put(coCoJSON)
                    }
                    AppSettings.getInstance()
                        .setValue(this, AppConstants.kS_CO_COORDINATOR, coCoordSled.toString())
                }
            } else if (coCoordArray != null) {
                coCoordinatorArray = coCoordArray
                if (coCoordinatorArray != null) {
                    val coCoordSled = JSONArray()
                    for (ri in 0 until coCoordinatorArray.length()) {
                        val coCoJSON = JSONObject()
                        val cCJosn = coCoordinatorArray.getJSONObject(ri)
                        val cCId = cCJosn.getString("id")
                        val cCRoleId = cCJosn.getString("role_id")
                        val cCFName = cCJosn.getString("first_name")
                        val cCMName = cCJosn.getString("middle_name")
                        val cCLName = cCJosn.getString("last_name")
                        val mobile = cCJosn.getString("mobile")
                        coCoJSON.put("id", cCId)
                        coCoJSON.put("role_id", cCRoleId)
                        coCoJSON.put("first_name", cCFName)
                        coCoJSON.put("middle_name", cCMName)
                        coCoJSON.put("last_name", cCLName)
                        coCoJSON.put("mobile", mobile)
                        coCoJSON.put("is_selected", "1")
                        coCoordSled.put(coCoJSON)
                    }
                    AppSettings.getInstance()
                        .setValue(this, AppConstants.kS_CO_COORDINATOR, coCoordSled.toString())
                }
            }


            // For Coordinator Person
            val coordinators: JSONArray = trDModel.coordinators
            if (coordinators != null) {
                val cordA = JSONArray()
                for (ri in 0 until coordinators.length()) {
                    val cordJosn = coordinators.getJSONObject(ri)
                    cordJosn.put("is_selected", "1")
                    cordA.put(cordJosn)
                }
                AppSettings.getInstance()
                    .setValue(this, AppConstants.kS_COORDINATOR, cordA.toString())
            }
            setSelectedCoordinator()
            setSelectedCoCoord()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // Coordinator
    private fun setSelectedCoordinator() {
        val sledCoordinator = AppSettings.getInstance()
            .getValue(this, AppConstants.kS_COORDINATOR, AppConstants.kS_COORDINATOR)
        try {
            if (!sledCoordinator.equals("kS_COORDINATOR", ignoreCase = true)) {
                sledCordJSONArray = JSONArray(sledCoordinator)
                if (sledCordJSONArray!!.length() > 0) {
                    sledCordLLayout!!.visibility = View.VISIBLE
                    adaptorSelectedCoordinator = AdaptorSelectedCoordinator(
                        this@UpcomingEventsDetails,
                        sledCordJSONArray,
                        actionType,
                        this@UpcomingEventsDetails
                    )
                    sledCordRView!!.adapter = adaptorSelectedCoordinator
                } else {
                    sledCordJSONArray = null
                    sledCordLLayout!!.visibility = View.GONE
                }
                if (sledCordJSONArray != null) {
                    sledCordId = JSONArray()
                    for (i in 0 until sledCordJSONArray!!.length()) {
                        val cordDetail = sledCordJSONArray!!.getJSONObject(i)
                        val sledCord = JSONObject()
                        sledCord.put("coordinator_id", cordDetail.getString("id"))
                        sledCord.put("role_id", cordDetail.getString("role_id"))
                        sledCordId!!.put(sledCord)
                    }
                    coordinatorTextView!!.error = null
                }
            } else {
                sledCordJSONArray = null
                sledCordLLayout!!.visibility = View.GONE
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // Resource Person
    private fun setSelectedCoCoord() {
        val sledCoCoord = AppSettings.getInstance()
            .getValue(this, AppConstants.kS_CO_COORDINATOR, AppConstants.kS_CO_COORDINATOR)
        try {
            if (!sledCoCoord.equals("kS_CO_COORDINATOR", ignoreCase = true)) {
                sledCoCoordJSONArray = JSONArray(sledCoCoord)
                if (sledCoCoordJSONArray!!.length() > 0) {
                    sledCoCoordLLayout!!.visibility = View.VISIBLE
                    adaptorSelectedCoCoord = AdaptorSelectedCoCoord(
                        this@UpcomingEventsDetails,
                        sledCoCoordJSONArray,
                        actionType,
                        this@UpcomingEventsDetails
                    )
                    sledCoCoordRView!!.adapter = adaptorSelectedCoCoord
                } else {
                    sledCoCoordJSONArray = null
                    sledCoCoordLLayout!!.visibility = View.GONE
                }
                if (sledCoCoordJSONArray != null) {
                    sledCoCoordinatorId = JSONArray()
                    sledCoCoordinatorArray = JSONArray()
                    for (i in 0 until sledCoCoordJSONArray!!.length()) {
                        val resPerDetail = sledCoCoordJSONArray!!.getJSONObject(i)
                        val co_coardRoleId = resPerDetail.getString("role_id")
                        if (co_coardRoleId.equals("0", ignoreCase = true)) {
                            val resPerCord = JSONObject()
                            resPerCord.put("rp_id", resPerDetail.getString("id"))
                            sledCoCoordinatorId!!.put(resPerCord)
                        } else {
                            val resPerCord = JSONObject()
                            resPerCord.put("coordinator_id", resPerDetail.getString("id"))
                            resPerCord.put("role_id", resPerDetail.getString("role_id"))
                            sledCoCoordinatorArray!!.put(resPerCord)
                        }
                    }
                    coCordTextView!!.error = null
                }
            } else {
                sledCoCoordJSONArray = null
                sledCoCoordLLayout!!.visibility = View.GONE
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        th?.printStackTrace()
    }

    override fun onFailure(th: Throwable?, i: Int) {
        th?.printStackTrace()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {

    }
}