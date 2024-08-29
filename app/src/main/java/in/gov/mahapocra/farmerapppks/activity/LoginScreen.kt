package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit


class LoginScreen : AppCompatActivity(), ApiCallbackCode {


    lateinit var signInButton: Button
    lateinit var sendOtpButton: Button
    lateinit var forgotPassword: TextView
    lateinit var registerTextView: TextView
    lateinit var radioButtonPwd: RadioButton
    lateinit var radioButtonOtp: RadioButton
    lateinit var passwordTextInput: TextInputLayout
    lateinit var otpTextInput: TextInputLayout
    lateinit var passwordEditText: EditText
    lateinit var otpEditText: EditText
    lateinit var userId: EditText
    lateinit var mobileNo: String
    private var userPass: String = ""
    lateinit var sentOTP: String
    lateinit var refresh_token: String
    var farmerRigisterID: Int = 0
    var languageToLoad: String? = null
    private lateinit var dialog: Dialog
    private var loginOption: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@LoginScreen).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_login_screen)
        init()
        onClick()
    }
    private fun init() {
        signInButton = findViewById(R.id.signInButton);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        registerTextView = findViewById(R.id.registerTextView);
        radioButtonPwd = findViewById(R.id.radioButtonPwd);
        radioButtonOtp = findViewById(R.id.radioButtonOtp);
        passwordTextInput = findViewById(R.id.passwordTextInput);
        otpTextInput = findViewById(R.id.otpTextInput);
        passwordEditText = findViewById(R.id.password);
        otpEditText = findViewById(R.id.otpEditText);
        userId = findViewById(R.id.userIdEditText);
    }
    private fun onClick() {
        signInButton.setOnClickListener(View.OnClickListener {

//            Toast.makeText(this,"Succesfully Login....", Toast.LENGTH_LONG).show();
            userValidateAndLogin()
        })
        forgotPassword.setOnClickListener(View.OnClickListener {
           // Toast.makeText(this, "Comming Soon.....", Toast.LENGTH_LONG).show();
            val intent2 = Intent(applicationContext, ForgetPassword::class.java)
            startActivity(intent2)
        })
        registerTextView.setOnClickListener(View.OnClickListener {
            val intent2 = Intent(applicationContext, Registration::class.java)
            startActivity(intent2)
        })
        passwordEditText.setOnClickListener(View.OnClickListener {
            if (passwordEditText.getText().toString().length < 6) {
                Toast.makeText(
                    this,
                    "Please enter atleast 6 character in password",
                    Toast.LENGTH_LONG
                ).show();
            }
        })
        sendOtpButton.setOnClickListener(View.OnClickListener {

//            otpTextInput.setVisibility(View.VISIBLE);
//            otpEditText.setVisibility(View.VISIBLE);
//            object : CountDownTimer(30000, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
//                    sendOtpButton.setText("seconds remaining: " + millisUntilFinished / 1000)
//                    //here you can have your logic to set text to edittext
//                }
//                override fun onFinish() {
//                    sendOtpButton.setText("Resend OTP")
//                }
//            }.start()
        })
    }

    private fun userValidateAndLogin() {

        if (loginOption == 1) {
            mobileNo = userId.text.toString()
            userPass = ""
            callRefreshTokenAPI(mobileNo, userPass)
        } else {
            mobileNo = userId.text.toString()
            userPass = passwordEditText.text.toString()
            if (userPass.isEmpty()) {
                passwordEditText.error = resources.getString(R.string.password_error)
                passwordEditText.requestFocus()
            } else {
                callRefreshTokenAPI(mobileNo, userPass)
            }
        }
    }
    private fun callRefreshTokenAPI(mobileNo: String, userPass: String) {
        if (mobileNo.isEmpty()) {
            userId.error = resources.getString(R.string.lgn_register_phone_error)
            userId.requestFocus()
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
                val responseCall: Call<JsonObject> = apiRequest.getRefreshTokenLogin(requestBody)
                api.postRequest(responseCall, this, 4)
                DebugLog.getInstance().d("param=" + responseCall.request().toString())
                DebugLog.getInstance()
                    .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
                e.printStackTrace()
            }
        }
    }
    private fun callLoginAPI(strToken: String) {
        if (mobileNo.isEmpty()) {
            userId.error = resources.getString(R.string.lgn_register_phone_error)
            userId.requestFocus()
        } else {

            val jsonObject = JSONObject()
            try {
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)
                jsonObject.put("MobileNo", mobileNo.trim { it <= ' ' })
                jsonObject.put("Password", userPass)
                jsonObject.put("refresh_token", strToken)

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
    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radioButtonPwd ->
                    if (checked) {
                        passwordTextInput.setVisibility(View.VISIBLE);
                        passwordEditText.setVisibility(View.VISIBLE);
                        signInButton.setVisibility(View.VISIBLE);
                        signInButton.setText(R.string.login)
                        sendOtpButton.setVisibility(View.GONE);
                        otpTextInput.setVisibility(View.GONE);
                        otpEditText.setVisibility(View.GONE);
                        Log.d("LoginScreen ", "onRadioButtonClicked::radioButtonPwd")
                        Toast.makeText(this, "Enter your password ", Toast.LENGTH_LONG).show()
                        loginOption = 0
                    }
                R.id.radioButtonOtp ->
                    if (checked) {
                        passwordTextInput.setVisibility(View.GONE);
                        passwordEditText.setVisibility(View.GONE);
                        signInButton.setVisibility(View.VISIBLE);
                        signInButton.setText(R.string.sendOtp)
                        sendOtpButton.setVisibility(View.GONE);
                        otpTextInput.setVisibility(View.GONE);
                        otpEditText.setVisibility(View.GONE);
                        Log.d("LoginScreen ", "onRadioButtonClicked::radioButtonPwd")
                        loginOption = 1
                    }
            }
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$th" + i)
    }
    override fun onResponse(jSONObject: JSONObject?, i: Int) {

        if(i==4) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    var message: String = jSONObject.getString("Message")
                    Log.d("message","message="+message)
                    refresh_token = jSONObject.getString("refresh_token")
                    callLoginAPI(refresh_token)
                    Log.d("refresh_token","refresh_token="+refresh_token)
                } else {
                    var message: String = jSONObject.getString("Message")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }
        if (i == 2) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
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
        dialogTitle.setText(resources.getString(R.string.enterOtp))
        Log.d("sentOTP22222", sentOTP)

        val revcieveOTPEditText = dialog.findViewById<EditText>(R.id.OptEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resentOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)
        otpVerification(resendOTP)
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
            userValidateAndLogin()
        }
        dialog.show()
    }
    private fun otpVerification(resendOTP: Button)
    {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resendOTP.setText(resources.getString(R.string.Time)+":"+ millisUntilFinished / 1000)
                //here you can have your logic to set text to edittext
            }
            override fun onFinish() {
                resendOTP.setText(resources.getString(R.string.Resend_OTP))
            }
        }.start()
    }
    private fun userVerification(enteredOTP: String) {

        Log.d("shdfhsdf", enteredOTP)
        Log.d("testshvm1", this.sentOTP)
        if (enteredOTP.equals(this.sentOTP)) {
            Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show();
            // moblNumberStatus = true
            AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, farmerRigisterID)
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            dialog.dismiss()
        } else {
            Toast.makeText(this, R.string.wrong_OTP, Toast.LENGTH_LONG).show();
        }
    }
}