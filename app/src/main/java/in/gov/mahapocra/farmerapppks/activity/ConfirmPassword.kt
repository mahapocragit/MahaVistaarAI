package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.ResponseModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class ConfirmPassword : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode {

    lateinit var textViewHeaderTitle: TextView
    lateinit var imageBackArrow: ImageView
    lateinit var btnSubmit: Button
    lateinit var newPasswordEditText: EditText
    lateinit var confirmPasswordEditText: EditText
    lateinit var newPwd: String
    lateinit var confirmPwd: String
    lateinit var userMobileNo: String
    var languageToLoad: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@ConfirmPassword).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_change_pwd)
        init()
        onClick()
        imageBackArrow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.forgot_password)
    }
    private fun init()
    {
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle);
        imageBackArrow=findViewById(R.id.imgBackArrow);
        btnSubmit = findViewById(R.id.submitButton)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        userMobileNo = intent.getStringExtra("MobileNo").toString()
       // userMobileNo  = AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
        Log.d("ConfirmPassword","Mobile No: "+userMobileNo)
    }
    private fun onClick()
    {
        imageBackArrow.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
        })
        btnSubmit.setOnClickListener {
            requestDataValidation()
        }
    }
    private fun requestDataValidation() {
        newPwd = newPasswordEditText.text.toString()
        confirmPwd=confirmPasswordEditText.text.toString()
        Log.d("ConfirmPassword::","Validation::newPwd="+newPwd)
        Log.d("ConfirmPassword::","Validation::confirmPwd="+confirmPwd)
        if (newPwd.isEmpty()) {
            newPasswordEditText.error = resources.getString(R.string.new_pwd_err)
            newPasswordEditText.requestFocus()
        }else if (confirmPwd.isEmpty()) {
            confirmPasswordEditText.error = resources.getString(R.string.new_pwd_err)
            confirmPasswordEditText.requestFocus()
        }else if (!newPwd.equals(confirmPwd)) {
            confirmPasswordEditText.error = resources.getString(R.string.pass_equals_confirmpass)
            confirmPasswordEditText.requestFocus()
        } else {
            val jsonObject = JSONObject()
            try {
                Log.d("ConfirmPassword::","Validation::MobileNo="+userMobileNo)
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)
                jsonObject.put("MobileNo", userMobileNo.trim { it <= ' ' })
                jsonObject.put("Password",newPwd)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    this,
                    APIServices.DBT,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getNewPassword(requestBody)
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
        TODO("Not yet implemented")
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }
    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response =
                    ResponseModel(jSONObject)
                if (response.getStatus()) {
                    var notifiCountValue: String =  jSONObject.getString("Message")
                    Toast.makeText(this,notifiCountValue, Toast.LENGTH_LONG).show();
                    val intent = Intent(this, LoginScreen::class.java)
                    startActivity(intent)
                    //UIToastMessage.showShortDuration(this, notifiCountValue)
                    //sentOTP = jSONObject.getString("OTP")
                    //FarmerRegstredID = jSONObject.getString("FAAPRegistrationID")
                    //FarmerRegstredID = "3"
                }
                else
                {
                    var notifiCountValue: String =  jSONObject.getString("Message")
                    Toast.makeText(this,notifiCountValue, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}