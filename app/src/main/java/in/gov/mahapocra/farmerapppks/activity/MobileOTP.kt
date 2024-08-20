package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit


class MobileOTP : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode {

    lateinit var mobileEditText: EditText
    lateinit var sendOTPEditText: EditText
    lateinit var verification: TextView
    lateinit var verify_Layout: LinearLayout
    lateinit var sendOTPButton: Button
    lateinit var resendOTPButton: Button
    lateinit var mob: String
    lateinit var sentOTP: String
    lateinit var enteredOTP: String
    lateinit var FarmerRegstredID: String
    lateinit var languageToLoad: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@MobileOTP).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_mobile_otp)
        setConfiguration()
        listner()
    }
    private fun setConfiguration() {
        mobileEditText = findViewById(R.id.mobileEditText)
        sendOTPEditText = findViewById(R.id.sendOTPEditText)
        resendOTPButton = findViewById(R.id.resendOTPButton)
        sendOTPButton = findViewById(R.id.sendOTPButton)
        verification = findViewById(R.id.verifytext)
        verify_Layout = findViewById(R.id.verify_Layout)
    }

    private fun listner() {
        sendOTPButton.setOnClickListener {
            requestDataValidation()
        }
        verification.setOnClickListener {
            userVerification()
        }
        resendOTPButton.setOnClickListener {
            requestDataValidation()
        }
    }
    private fun userVerification() {
        enteredOTP = sendOTPEditText.text.toString()
        Log.d("shdfhsdf",enteredOTP)
        Log.d("testshvm1",sentOTP)
        if (enteredOTP.equals(sentOTP)){
            verification.setTextColor(Color.parseColor("#FF88EA8C"))
            if (!FarmerRegstredID.equals("")){
               // dashboard()
                Toast.makeText(this,"callDashboard()", Toast.LENGTH_LONG).show();
                val intent = Intent(this, DashboardScreen::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }else{
                //registration()
                Toast.makeText(this,"callRegistration()", Toast.LENGTH_LONG).show();
                val intent = Intent(this, Registration::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun requestDataValidation() {
        mob = mobileEditText.text.toString()
        verification.setTextColor(Color.parseColor("#000000"))
        if (mob.isEmpty()) {
            mobileEditText.error = resources.getString(R.string.login_mob_err)
            mobileEditText.requestFocus()
        } else if (!AppUtility.getInstance().isValidPhoneNumber(mob)) {
            mobileEditText.error = resources.getString(R.string.login_mob_valid_err)
            mobileEditText.requestFocus()
        } else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("MobileNo", mob.trim { it <= ' ' })
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppinventorApi(this, APIServices.DBT, "", AppString(this).getkMSG_WAIT(), true)
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getOTPRequest(requestBody)
                DebugLog.getInstance().d("param1=" + responseCall.request().toString())
                DebugLog.getInstance().d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
                api.postRequest(responseCall, this, 1)
                DebugLog.getInstance().d("param=" + responseCall.request().toString())
                DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
                e.printStackTrace()
            }
        }
    }
    override fun onFailure(th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$th")
    }
    override fun onResponse(jSONObject: JSONObject?, i: Int) {

        if (i == 1) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    verify_Layout.visibility = View.VISIBLE
                    resendOTPButton.visibility = View.VISIBLE
                    var notifiCountValue: String =  jSONObject.getString("Message")
                    Toast.makeText(this,notifiCountValue, Toast.LENGTH_LONG).show();
                    //UIToastMessage.showShortDuration(this, notifiCountValue)
                    sentOTP = jSONObject.getString("OTP")
                    FarmerRegstredID = jSONObject.getString("FAAPRegistrationID")
                    //FarmerRegstredID = "3"
                    }
                }
            }
        }
    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$obj")
    }
}