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
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.ResponseModel
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class ForgetPassword : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode {
    lateinit var textViewHeaderTitle: TextView
    lateinit var imageBackArrow: ImageView
    lateinit var mobileEditText: EditText
    lateinit var sendOTPEditText: EditText
    lateinit var verification: TextView
    lateinit var verify_Layout: LinearLayout
    lateinit var sendOTPButton: Button
    lateinit var resendOTPButton: Button
    lateinit var mob: String
    private var userPass: String = ""
    lateinit var sentOTP: String
    lateinit var enteredOTP: String
    lateinit var FarmerRegstredID: String
    private lateinit var dialog: Dialog
    private var loginOption: Int = 0
    var farmerRigisterID: Int = 0
    var languageToLoad: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@ForgetPassword).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_forget_password)
        init()
        onClick()
        imageBackArrow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.forgot_password)
    }
    private fun init()
    {
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle);
        imageBackArrow=findViewById(R.id.imgBackArrow);
        mobileEditText = findViewById(R.id.mobileEditText)
        sendOTPEditText = findViewById(R.id.sendOTPEditText)
        resendOTPButton = findViewById(R.id.resendOTPButton)
        sendOTPButton = findViewById(R.id.sendOTPButton)
        verification = findViewById(R.id.verifytext)
        verify_Layout = findViewById(R.id.verify_Layout)

    }
    private fun onClick()
    {
        sendOTPButton.setOnClickListener {
            mob = mobileEditText.text.toString()
            userPass = ""
           // userLoginAPI(mob, userPass)
            sendOTP()
        }
        imageBackArrow.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
    private fun sendOTP() {
        mob = mobileEditText.text.toString()
        // verification.setTextColor(Color.parseColor("#000000"))
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
                    AppInventorApi(
                        this,
                        APIServices.DBT,
                        "",
                        AppString(this).getkMSG_WAIT(),
                        true
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getOTPRequest(requestBody)
                DebugLog.getInstance().d("param1=" + responseCall.request().toString())
                DebugLog.getInstance()
                    .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
                api.postRequest(responseCall, this, 2)
                DebugLog.getInstance().d("param=" + responseCall.request().toString())
                DebugLog.getInstance()
                    .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
                e.printStackTrace()
            }
        }
    }
    private fun userLoginAPI(mobileNo: String, userPass: String) {
        if (mobileNo.isEmpty()) {
            mobileEditText.error = resources.getString(R.string.lgn_register_phone_error)
            mobileEditText.requestFocus()
        } else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)
                jsonObject.put("MobileNo", mobileNo.trim { it <= ' ' })
                jsonObject.put("Password", userPass)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppInventorApi(
                        this,
                        APIServices.DBT,
                        "",
                        AppString(this).getkMSG_WAIT(),
                        true
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getUserLogin(requestBody)
                api.postRequest(responseCall, this, 3)
                DebugLog.getInstance().d("param=" + responseCall.request().toString())
                DebugLog.getInstance()
                    .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
                e.printStackTrace()
            }
        }
    }
    private fun addVerificationDialog(sentOTP: String) {
        Log.d("sentOTP", sentOTP)
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        Log.d("sentOTP111", sentOTP)
        dialog.setContentView(R.layout.dialog_activity_verification)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.setText("Enter OTP")
        Log.d("sentOTP22222", sentOTP)

        val revcieveOTPEditText = dialog.findViewById<EditText>(R.id.OptEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resentOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)

        cancelButton.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        submitButton.setOnClickListener {
            var enteredOTP: String = revcieveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                revcieveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                revcieveOTPEditText.requestFocus()
            } else {
                userVerification(enteredOTP)
            }
        }
        resendOTP.setOnClickListener {
            dialog.dismiss()
            //sendOTP()
            userValidateAndLogin()
        }
        dialog.show()
    }
    private fun userVerification(enteredOTP: String) {

        Log.d("shdfhsdf", enteredOTP)
        Log.d("testshvm1", this.sentOTP)
        if (enteredOTP.equals(this.sentOTP)) {
            Toast.makeText(this,"Thank you...", Toast.LENGTH_LONG).show();
            val intent = Intent(this, ConfirmPassword::class.java)
            intent.putExtra("MobileNo",mob)
            startActivity(intent)
//            textViewVerify.setText("Verified")
//            textViewVerify.setTextColor(Color.parseColor("#1d6b08"))
            dialog.dismiss()
        } else {
            Toast.makeText(this, R.string.wrong_OTP, Toast.LENGTH_LONG).show();
        }
    }
    private fun userValidateAndLogin() {
        if (loginOption == 1) {
            mob = mobileEditText.text.toString()
            userPass = ""
            userLoginAPI(mob, userPass)
        }
    }
    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 2) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response =
                    ResponseModel(jSONObject)
                if (response.getStatus()) {
                    var notifiCountValue: String = jSONObject.getString("Message")
                    Toast.makeText(this, notifiCountValue, Toast.LENGTH_LONG).show()
                    //UIToastMessage.showShortDuration(this, notifiCountValue)
                    sentOTP = jSONObject.getString("OTP")
                    addVerificationDialog(sentOTP)
                }
            }
        }
        if (i == 3) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response =
                    ResponseModel(jSONObject)
                if (response.getStatus()) {
                    if(loginOption == 1) {
                        var message: String = jSONObject.getString("Message")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        //UIToastMessage.showShortDuration(this, notifiCountValue)
                        sentOTP = jSONObject.getString("OTP")
                        farmerRigisterID = jSONObject.getInt("FAAPRegistrationID")
                        addVerificationDialog(sentOTP)
                    }else{
                        var message: String = jSONObject.getString("Message")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        //UIToastMessage.showShortDuration(this, notifiCountValue)
                        sentOTP = jSONObject.getString("OTP")
                        farmerRigisterID = jSONObject.getInt("FAAPRegistrationID")
                        AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, farmerRigisterID)
                        val intent = Intent(this, DashboardScreen::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        var getfarmerId: Int = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
                        Log.d("getfarmerIdValue123",getfarmerId.toString())
                    }
                }else{
                    var message: String = jSONObject.getString("Message")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }

        }
    }
    override fun onFailure(th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$th")
    }
    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$obj")
    }
}