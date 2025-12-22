package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.chms.RandomObservationDashboardCA
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiConstants
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.FirebaseHelper
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityLoginScreenBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.toSHA512
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.OtpRateLimiter
import `in`.gov.mahapocra.mahavistaarai.util.OtpRateLimiter.provideValidEncryptedString
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

private var timestamp: Long = 0

class LoginScreen : AppCompatActivity(), ApiCallbackCode {
    private lateinit var binding: ActivityLoginScreenBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private lateinit var refreshToken: String
    private lateinit var mobileNo: String
    private lateinit var dialog: Dialog
    private var userPass = ""
    var languageToLoad = "mr"
    private var farmerRegisteredID: Int = 0
    private var loginOption: Int = 1
    private var mobile = ""
    private var enteredOTP = ""
    private val PASSWORD_VERIFY = 0
    private val OTP_VERIFY = 1
    private var agriStackMobile = ""
    private var fcmToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AppSettings.getLanguage(this@LoginScreen)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        FirebaseHelper(this)

        binding.changeLanguageImageView.setOnClickListener {
            openChangeLangPopup()
        }

        AppSettings.getInstance().clearIntValue(this, AppConstants.fREGISTER_ID)
        if (intent.getStringExtra("from") != "dashboard") {
            AppSettings.getInstance().setBooleanValue(this, AppConstants.IS_USER_GUEST, false)
        }
        authenticationOperations()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result
                Log.d("FCM Token", "onCreate: ${fcmToken}")
            } else {
                Log.e("FCM Token", "Fetching token failed", task.exception)
            }
        }

        binding.mobileNoOption.setOnClickListener {
            agriStackMobile = ""
            binding.mobileNoOption.background =
                ContextCompat.getDrawable(this, R.drawable.shape_left) //enabled
            binding.farmerIdOption.background =
                ContextCompat.getDrawable(this, R.drawable.shape_right)
            binding.mobileNoOption.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.farmerIdOption.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.mobileLoginLayout.visibility = View.VISIBLE
            binding.farmerLoginLayout.visibility = View.GONE
        }
        binding.farmerIdOption.setOnClickListener {
            binding.mobileNoOption.background =
                ContextCompat.getDrawable(this, R.drawable.shape_left_white)
            binding.farmerIdOption.background =
                ContextCompat.getDrawable(this, R.drawable.shape_right_green)
            binding.mobileNoOption.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.farmerIdOption.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.mobileNoOption
            binding.farmerIdOption
            binding.mobileLoginLayout.visibility = View.GONE
            binding.farmerLoginLayout.visibility = View.VISIBLE
        }

        farmerIdLayoutValidation()
    }

    private fun farmerIdLayoutValidation() {
        binding.sendFarmerIdOTPButton.setOnClickListener {
            val farmerId = binding.farmerIdEditText.text.toString()
            if (farmerId.length != 11 && farmerId.isEmpty()) {
                UIToastMessage.show(this, "Please enter valid Farmer ID")
            } else {
                farmerViewModel.farmerIdBasedLogin(this, farmerId)
            }
        }

        farmerViewModel.agristackLoginResponse.observe(this) {
            Log.d(TAG, "farmerIdLayoutValidation: $it")
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                if (jsonObject.optInt("status") == 200) {
                    agriStackMobile = jsonObject.optString("mobile")
                    addVerificationDialogForFarmer()
                }
            }
        }

        farmerViewModel.error.observe(this)
        {
            Log.d(TAG, "farmerIdLayoutValidation error: $it")
        }
    }

    private fun openChangeLangPopup() {
        val dialog = Dialog(this@LoginScreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_language_selector)

        val tvEnglish = dialog.findViewById<TextView>(R.id.tv_eng)
        val tvMarathi = dialog.findViewById<TextView>(R.id.tv_mar)

        tvEnglish.setOnClickListener {
            val languageToLoad = "en"
            configureLocale(baseContext, languageToLoad)
            AppSettings.setLanguage(this@LoginScreen, "1")
            finish()
            startActivity(intent)
            dialog.dismiss()
        }

        tvMarathi.setOnClickListener {
            val languageToLoad = "mr"
            configureLocale(baseContext, languageToLoad)
            AppSettings.setLanguage(this@LoginScreen, "2")
            finish()
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun authenticationOperations() {
        binding.signInButton.setOnClickListener {
            AppSettings.getInstance().setBooleanValue(this, AppConstants.IS_USER_GUEST, false)
            if (loginOption == PASSWORD_VERIFY) {
                userValidateAndLogin()
            } else {
                sendOTP()
            }
        }

        binding.forgotPassword.setOnClickListener {
            val intent2 = Intent(applicationContext, ForgetPassword::class.java)
            startActivity(intent2)
        }

        binding.registerTextView.setOnClickListener {
            val intent2 = Intent(applicationContext, PreRegistrationActivity::class.java)
            startActivity(intent2)
        }

        binding.passwordEditText.setOnClickListener {
            if (binding.passwordEditText.text.toString().length < 8) {
                Toast.makeText(
                    this,
                    "Please enter at least 8 character in password",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.guestModeCardView.setOnClickListener {
            // CAPTCHA was successfully verified
            val userId = LocalCustom.generateRandom10DigitNumber()
            AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, userId)
            AppSettings.getInstance()
                .setBooleanValue(this, AppConstants.IS_USER_GUEST, true)
            startActivity(Intent(this, RandomObservationDashboardCA::class.java))
        }
    }

    private fun sendOTP() {
        mobile = binding.userIdEditText.text.toString()

        if (mobile.isEmpty()) {
            binding.userIdEditText.error = resources.getString(R.string.login_mob_err)
            binding.userIdEditText.requestFocus()
            return
        }

        if (!AppUtility.getInstance().isValidPhoneNumber(mobile)) {
            binding.userIdEditText.error = resources.getString(R.string.login_mob_valid_err)
            binding.userIdEditText.requestFocus()
            return
        }

        // Check OTP Rate Limit before proceeding
        if (!OtpRateLimiter.canSendOtp(mobile)) {
            val timeLeftMillis = OtpRateLimiter.getBlockedTimeLeft(mobile)
            val minutesLeft = (timeLeftMillis / 60000).toInt()
            val secondsLeft = ((timeLeftMillis % 60000) / 1000).toInt()
            UIToastMessage.show(
                this,
                "OTP limit reached. Try again in ${minutesLeft}m ${secondsLeft}s."
            )
            return
        }

        val jsonObject = JSONObject()
        try {
            jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(
                this,
                AppEnvironment.FARMER.baseUrl,
                "",
                AppString(this).getkMSG_WAIT(),
                true
            )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(ApiService::class.java)
            val responseCall: Call<JsonObject> =
                apiRequest.getOTPRequest(mobile.trim(), requestBody)
            api.postRequest(responseCall, this, 1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun userValidateAndLogin() {
        mobileNo = binding.userIdEditText.text.toString()
        userPass = binding.passwordEditText.text.toString()
        if (userPass.isEmpty()) {
            binding.passwordEditText.error = resources.getString(R.string.password_error)
            binding.passwordEditText.requestFocus()
        } else {
            callRefreshTokenAPI(mobileNo, userPass)
        }
    }
    private fun callRefreshTokenAPI(mobileNo: String, userPass: String = "", otp: String = "") {
        if (mobileNo.isEmpty()) {
            binding.userIdEditText.error = resources.getString(R.string.lgn_register_phone_error)
            binding.userIdEditText.requestFocus()
        } else {
            // CAPTCHA was successfully verified
            val jsonObject = JSONObject()
            try {
                jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
                val requestBody =
                    AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppInventorApi(
                        this,
                        AppEnvironment.FARMER.baseUrl,
                        "",
                        AppString(this).getkMSG_WAIT(),
                        true
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)
                if (otp != "") {
                    val responseCall: Call<JsonObject> =
                        apiRequest.getRefreshTokenLoginViaOTP(
                            mobileNo.trim { it <= ' ' },
                            otp,
                            fcmToken,
                            requestBody
                        )
                    api.postRequest(responseCall, this, 4)
                } else {
                    val responseCall: Call<JsonObject> =
                        apiRequest.getRefreshTokenLoginViaPassword(
                            mobileNo.trim { it <= ' ' },
                            toSHA512(userPass),
                            fcmToken,
                            requestBody
                        )
                    api.postRequest(responseCall, this, 4)
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun callLoginAPI(strToken: String, otp: String) {
        if (mobileNo.isEmpty()) {
            binding.userIdEditText.error = resources.getString(R.string.lgn_register_phone_error)
            binding.userIdEditText.requestFocus()
        } else {
            if (otp != "") {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
                    jsonObject.put("refresh_token", strToken)

                    val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                    val api =
                        AppInventorApi(
                            this,
                            AppEnvironment.FARMER.baseUrl,
                            "",
                            AppString(this).getkMSG_WAIT(),
                            true
                        )
                    val retrofit: Retrofit = api.getRetrofitInstance()
                    val apiRequest = retrofit.create(ApiService::class.java)
                    Log.d(TAG, "callLoginAPI: true")
                    val responseCall: Call<JsonObject> =
                        apiRequest.getUserLoginOTP(mobileNo.trim { it <= ' ' }, otp, requestBody)
                    api.postRequest(responseCall, this, 2)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
                    jsonObject.put("refresh_token", strToken)

                    val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                    val api =
                        AppInventorApi(
                            this,
                            AppEnvironment.FARMER.baseUrl,
                            "",
                            AppString(this).getkMSG_WAIT(),
                            true
                        )
                    val retrofit: Retrofit = api.getRetrofitInstance()
                    val apiRequest = retrofit.create(ApiService::class.java)
                    Log.d(TAG, "callLoginAPI: true")
                    val responseCall: Call<JsonObject> =
                        apiRequest.getUserLoginPassword(
                            mobileNo.trim { it <= ' ' },
                            toSHA512(userPass),
                            requestBody
                        )
                    api.postRequest(responseCall, this, 2)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun callLoginAPIForFarmer(strToken: String, otp: String = "") {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
            jsonObject.put("refresh_token", strToken)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(ApiService::class.java)
            Log.d(TAG, "callLoginAPIForFarmer: true")
            val responseCall: Call<JsonObject> =
                apiRequest.getUserLoginOTP(agriStackMobile.trim { it <= ' ' }, otp, requestBody)
            api.postRequest(responseCall, this, 2)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.id) {
                R.id.radioButtonPwd ->
                    if (checked) {
                        binding.passwordTextInput.visibility = View.VISIBLE
                        binding.passwordEditText.visibility = View.VISIBLE
                        binding.signInButton.visibility = View.VISIBLE
                        binding.signInButton.setText(R.string.login)
                        loginOption = PASSWORD_VERIFY
                    }

                R.id.radioButtonOtp ->
                    if (checked) {
                        binding.passwordTextInput.visibility = View.GONE
                        binding.passwordEditText.visibility = View.GONE
                        binding.signInButton.visibility = View.VISIBLE
                        binding.signInButton.setText(R.string.sendOtp)
                        loginOption = OTP_VERIFY
                    }
            }
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        th?.printStackTrace()
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            Log.d(TAG, "onResponse: $jSONObject")
            if (jSONObject != null) {
                if (jSONObject.optInt("status") == 200) {
                    val response: String = jSONObject.getString("response")
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    addVerificationDialog()
                } else if (jSONObject.optInt("status") == 201) {
                    Toast.makeText(this, R.string.mobile_otp_error_text, Toast.LENGTH_LONG).show()
                } else if (jSONObject.optInt("status") == 429) {
                    Toast.makeText(this, jSONObject.optString("response"), Toast.LENGTH_LONG).show()
                } else{
                    Toast.makeText(this, jSONObject.optString("response"), Toast.LENGTH_LONG).show()
                }
            }
        }
        if (i == 4) {
            if (jSONObject != null) {
                if (jSONObject.optInt("status") == 200) {
                    refreshToken = jSONObject.getString("refresh_token")
                    if (agriStackMobile != "") {
                        callLoginAPIForFarmer(refreshToken, enteredOTP)
                    } else {
                        callLoginAPI(refreshToken, enteredOTP)
                    }
                } else {
                    val message: String = jSONObject.getString("response")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }
        if (i == 2) {
            if (jSONObject != null) {
                if (jSONObject.optInt("status") == 200) {
                    AppPreferenceManager(this).saveBoolean("show_overlay", true)
                    if (loginOption == OTP_VERIFY) {
                        val message: String = jSONObject.getString("response")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        farmerRegisteredID = jSONObject.getInt("FAAPRegistrationID")
                        AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, farmerRegisteredID)
                        Log.d("REGISTER_ID Login", "fREGISTER_ID = " + farmerRegisteredID)
                        val intent = Intent(this, RandomObservationDashboardCA::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        val message: String = jSONObject.getString("response")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        farmerRegisteredID = jSONObject.getInt("FAAPRegistrationID")
                        AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, farmerRegisteredID)
                        Log.d("REGISTER_ID Login", "fREGISTER_ID = " + farmerRegisteredID)
                        val intent = Intent(this, RandomObservationDashboardCA::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val message: String = jSONObject.getString("Message")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun addVerificationDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_activity_verification)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.text = resources.getString(R.string.enterOtp)
        val receiveOTPEditText = dialog.findViewById<EditText>(R.id.OptEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resendOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)
        otpVerification(resendOTP)
        cancelButton.setOnClickListener { dialog.dismiss() }
        submitButton.setOnClickListener {

            enteredOTP = receiveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                receiveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                receiveOTPEditText.requestFocus()
            } else {
                timestamp = System.currentTimeMillis()
                farmerViewModel.compareOtp(this, timestamp, mobile, enteredOTP)
                farmerViewModel.compareOtpResponse.observe(this) {
                    if (it != null) {
                        val calculatedResponse = provideValidEncryptedString(timestamp)
                        val jSONObject = JSONObject(it.toString())
                        val response = jSONObject.optString("response")
                        if (calculatedResponse != response) {
                            mobileNo = binding.userIdEditText.text.toString()
                            callRefreshTokenAPI(mobileNo, userPass, enteredOTP)
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_LONG).show()
                        }
                        dialog.dismiss()
                    }
                }
            }

        }

        resendOTP.setOnClickListener {
            dialog.dismiss()
            sendOTP()
        }
        dialog.show()
    }

    private fun addVerificationDialogForFarmer() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_activity_verification)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.text = resources.getString(R.string.enterOtp)
        val receiveOTPEditText = dialog.findViewById<EditText>(R.id.OptEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resendOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)
        otpVerification(resendOTP)
        cancelButton.setOnClickListener { dialog.dismiss() }
        submitButton.setOnClickListener {

            enteredOTP = receiveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                receiveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                receiveOTPEditText.requestFocus()
            } else {
                timestamp = System.currentTimeMillis()
                farmerViewModel.compareOtp(this, timestamp, agriStackMobile, enteredOTP)
                farmerViewModel.compareOtpResponse.observe(this) {
                    if (it != null) {
                        val calculatedResponse = provideValidEncryptedString(timestamp)
                        val jSONObject = JSONObject(it.toString())
                        val response = jSONObject.optString("response")
                        if (calculatedResponse != response) {
                            callRefreshTokenAPI(mobileNo = agriStackMobile, otp = enteredOTP)
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_LONG).show()
                        }
                        dialog.dismiss()
                    }
                }
            }

        }

        resendOTP.setOnClickListener {
            dialog.dismiss()
            if (agriStackMobile.isNotEmpty()) {
                val farmerId = binding.farmerIdEditText.text.toString()
                farmerViewModel.farmerIdBasedLogin(this, farmerId)
            } else {
                userValidateAndLogin()
            }
        }
        dialog.show()
    }

    private fun otpVerification(resendOTP: Button) {
        // Disable and gray out the button before starting timer
        resendOTP.isEnabled = false
        resendOTP.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        resendOTP.setTextColor(ContextCompat.getColor(this, R.color.white))

        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resendOTP.text =
                    resources.getString(R.string.Time) + ":" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                // Enable and reset button appearance
                resendOTP.isEnabled = true
                resendOTP.text = resources.getString(R.string.Resend_OTP)
                resendOTP.setBackgroundColor(
                    ContextCompat.getColor(
                        this@LoginScreen,
                        R.color.actionbar_color_figma
                    )
                )
                resendOTP.setTextColor(ContextCompat.getColor(this@LoginScreen, R.color.white))
            }
        }.start()
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }
}