package `in`.gov.mahapocra.farmerapppks.fragment.advisory

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.activity.CropStageAdvisory
import `in`.gov.mahapocra.farmerapppks.activity.DashboardScreen
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit


var ratingbar: RatingBar? = null
var ratingbar2: RatingBar? = null
private lateinit var edtFeedbk1: EditText
private lateinit var edtFeedbk2: EditText
lateinit var submitFeedback: TextView
lateinit var textViewHeaderTitle: TextView
private var imageMenushow: ImageView? = null
var languageToLoad: String? = null
var rating1:Int=0
var rating2:Int=0
var advisoryId:String? = null
var advisoryIdCropsap:String? = null
private  var farmerId:Int=0
lateinit var cropId:String
lateinit var villageId:String

lateinit var cropID: String
lateinit var talukaID: String

class AdvisoryFeedback : Fragment(), ApiCallbackCode {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater!!.inflate(R.layout.fragment_advisory_feedback, container, false)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(activity).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        initial(view)
        onClick()
        return view
    }

    private fun initial(view: View) {
        submitFeedback = view.findViewById(R.id.submitFeedback)
        textViewHeaderTitle = view.findViewById(R.id.textViewHeaderTitle)
        imageMenushow = view.findViewById(R.id.imageMenushow)
        ratingbar = view.findViewById(R.id.ratingBar)
        ratingbar2 = view.findViewById(R.id.ratingBar2)
        edtFeedbk1 = view.findViewById(R.id.edtFeedbk1)
        edtFeedbk2 = view.findViewById(R.id.edtFeedbk2)

        cropId = getArguments()?.getString("cropId").toString()
        villageId = getArguments()?.getString("villageId").toString()
        advisoryIdCropsap = getArguments()?.getString("cropsapadvisoryCropId").toString()
        advisoryId = getArguments()?.getString("advisoryCropId").toString()

        textViewHeaderTitle.setText( resources.getString(R.string.crop_advisory_feedback))
        imageMenushow?.setVisibility(View.VISIBLE)

    }

    private fun onClick() {
        submitFeedback.setOnClickListener { view ->
            submitAdvisoryFeedback()
        }
        imageMenushow?.setOnClickListener(View.OnClickListener {
            val i = Intent(activity, DashboardScreen::class.java)
            startActivity(i)
            (activity as Activity?)!!.overridePendingTransition(0, 0)
        })
    }

    private fun submitAdvisoryFeedback() {
        rating1 = ratingbar!!.rating.toInt()
         rating2 = ratingbar2!!.rating.toInt()
        var feedback1: String = edtFeedbk1.text.toString()
        var feedback2: String = edtFeedbk2.text.toString()
        farmerId = AppSettings.getInstance().getIntValue(activity, AppConstants.fREGISTER_ID, 0)
        if(rating1==0){
            UIToastMessage.show(activity, resources.getString(R.string.feedback1_rating_error))
        }else if (rating2==0){
            UIToastMessage.show(activity, resources.getString(R.string.feedback2_rating_error))
        }else
        {
        val jsonObject = JSONObject()
        try {

            jsonObject.put("farmer_id", farmerId)
            jsonObject.put("crop_id", cropId)
            jsonObject.put("village_id", villageId)
            jsonObject.put("advisory_id", advisoryId)
            jsonObject.put("advisory_id_cropsap", advisoryIdCropsap)
            jsonObject.put("first_questn_rating", rating1)
            jsonObject.put("sec_questn_rating", rating2)
            jsonObject.put("first_comment", feedback1)
            jsonObject.put("sec_comment", feedback2)
            jsonObject.put("advisory_type", "wotr")

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(activity, APIServices.SSO, "", AppString(activity).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.submitAdvisoryFeedback(requestBody)
            DebugLog.getInstance().d("AdvisoryFeedback::param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d(
                    "AdvisoryFeedback::param2=" + AppUtility.getInstance()
                        .bodyToString(responseCall.request())
                )
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("AdvisoryFeedback::param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }}

        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdvisoryFeedback.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdvisoryFeedback().apply {
                arguments = Bundle().apply {

                }
            }
    }


    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)
            if (response.status ) {
                UIToastMessage.show(activity, response.response)
            } else {
                UIToastMessage.show(activity, response.response)
            }
        }
    }

    fun onBackPressed() {
        Log.d("onBackPressed","onBackPressed")
        val i = Intent(activity, CropStageAdvisory::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(0, 0)
    }


}




