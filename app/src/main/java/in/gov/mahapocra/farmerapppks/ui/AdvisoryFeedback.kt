package `in`.gov.mahapocra.farmerapppks.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
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
import `in`.gov.mahapocra.farmerapppks.data.ResponseModel
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class AdvisoryFeedback : Fragment(), ApiCallbackCode {

    private var ratingbar: RatingBar? = null
    private var ratingbar2: RatingBar? = null
    private lateinit var edtFeedbk1: EditText
    private lateinit var edtFeedbk2: EditText
    private lateinit var submitFeedback: TextView
    lateinit var textViewHeaderTitle: TextView
    private var imageMenushow: ImageView? = null
    var languageToLoad: String? = null
    private var rating1: Int = 0
    private var rating2: Int = 0
    private var advisoryId: String? = null
    private var advisoryIdCropsap: String? = null
    private var farmerId: Int = 0
    lateinit var cropId: String
    private lateinit var villageId: String
    lateinit var talukaID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_advisory_feedback, container, false)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(activity).equals("1", ignoreCase = true)) {
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

        cropId = arguments?.getString("cropId").toString()
        villageId = arguments?.getString("villageId").toString()
        advisoryIdCropsap = arguments?.getString("cropsapadvisoryCropId").toString()
        advisoryId = arguments?.getString("advisoryCropId").toString()

        textViewHeaderTitle.text = resources.getString(R.string.crop_advisory_feedback)
        imageMenushow?.visibility = View.VISIBLE
    }

    private fun onClick() {
        submitFeedback.setOnClickListener {
            submitAdvisoryFeedback()
        }
        imageMenushow?.setOnClickListener {
            val i = Intent(activity, DashboardScreen::class.java)
            startActivity(i)
            (activity as Activity?)!!.overridePendingTransition(0, 0)
        }
    }

    private fun submitAdvisoryFeedback() {
        rating1 = ratingbar!!.rating.toInt()
        rating2 = ratingbar2!!.rating.toInt()
        val feedback1: String = edtFeedbk1.text.toString()
        val feedback2: String = edtFeedbk2.text.toString()
        farmerId = AppSettings.getInstance().getIntValue(activity, AppConstants.fREGISTER_ID, 0)
        if (rating1 == 0) {
            UIToastMessage.show(activity, resources.getString(R.string.feedback1_rating_error))
        } else if (rating2 == 0) {
            UIToastMessage.show(activity, resources.getString(R.string.feedback2_rating_error))
        } else {
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
                    AppInventorApi(
                        activity,
                        APIServices.SSO,
                        "",
                        AppString(activity).getkMSG_WAIT(),
                        true
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.submitAdvisoryFeedback(requestBody)
                api.postRequest(responseCall, this, 1)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }


    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response =
                ResponseModel(jSONObject)
            if (response.status) {
                UIToastMessage.show(activity, response.response)
            } else {
                UIToastMessage.show(activity, response.response)
            }
        }
    }

    fun onBackPressed() {
        val i = Intent(activity, CropStageAdvisory::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(0, 0)
    }
}




