package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class BandavarActivity : AppCompatActivity() , ApiCallbackCode {
    lateinit var textViewHeaderTitle: TextView
    lateinit var imageMenushow: ImageView
    lateinit var edtFarmerSuccessStory: EditText
    lateinit var tvPostStory: TextView
    lateinit var tvViewStory: TextView
    lateinit var userName: String
    lateinit var userMobileNo: String
//    lateinit var userRegId: String
    var userRegId : Int ? = 0
    lateinit var strFarmerStory: String
    var languageToLoad: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@BandavarActivity).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_bandavar)
        init()

        onClick()
        imageMenushow.setVisibility(View.VISIBLE)
        textViewHeaderTitle.setText(resources.getString(R.string.badhvarText))

        userName = AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName)
        userMobileNo = AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
        userRegId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        Log.d("BandavarActivity=","userName="+userName)
        Log.d("BandavarActivity=","userMobileNo="+userMobileNo)
        Log.d("BandavarActivity=","userRegId="+userRegId)
    }
    private fun init() {
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle);
        imageMenushow = findViewById(R.id.imageMenushow);
        edtFarmerSuccessStory = findViewById(R.id.tv_farmer_success_story);
        tvPostStory = findViewById(R.id.tv_post_story);
        tvViewStory = findViewById(R.id.tv_view_story);
    }
    private fun onClick()
    {
        imageMenushow.setOnClickListener(View.OnClickListener {
        val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
       })
        tvPostStory.setOnClickListener(View.OnClickListener {
            setBandavarSuccessStory()
        })
        tvViewStory.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ViewStoriesBandavar::class.java)
            startActivity(intent)
        })
    }
    private fun setBandavarSuccessStory()
    {
        strFarmerStory=edtFarmerSuccessStory.getText().toString();
        Log.d("BandavarActivity","FarmerStory="+strFarmerStory)
        val jsonObject = JSONObject()
        if(userRegId!! > 0) {
            try {
                jsonObject.put("name", userName)
                jsonObject.put("mobile_no", userMobileNo.trim { it <= ' ' })
                jsonObject.put("FAAPRegistrationID", userRegId)
                jsonObject.put("bandhar_description", strFarmerStory)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.postStoryBandhavar(requestBody)
                api.postRequest(responseCall, this, 1)
                DebugLog.getInstance().d("param=" + responseCall.request().toString())
                DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
                e.printStackTrace()
            }
        }else
        {
            UIToastMessage.show(this@BandavarActivity, "Please Login First...")
        }
    }
    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }
    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                        var message: String = jSONObject.getString("response")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        //UIToastMessage.showShortDuration(this, notifiCountValue)
                }else {
                    var message: String = jSONObject.getString("response")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}