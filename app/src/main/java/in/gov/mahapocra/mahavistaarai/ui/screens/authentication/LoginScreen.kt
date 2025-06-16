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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.FirebaseHelper
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityLoginScreenTempBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.showCaptchaDialog
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.OtpRateLimiter
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class LoginScreen : AppCompatActivity(), ApiCallbackCode {

    private lateinit var binding: ActivityLoginScreenTempBinding
    private lateinit var farmerViewModel: FarmerViewModel
    private lateinit var refreshToken: String
    private lateinit var mobileNo: String
    private lateinit var dialog: Dialog
    private var userPass = ""
    var languageToLoad = "mr"
    private var farmerRegisteredID: Int = 0
    private var loginOption: Int = 0
    private var mobile = ""
    private var enteredOTP = ""
    private val PASSWORD_VERIFY = 0
    private val OTP_VERIFY = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@LoginScreen)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityLoginScreenTempBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseHelper(this)
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
        binding.changeLanguageImageView.setOnClickListener {
            openChangeLangPopup()
        }

        AppSettings.getInstance().clearIntValue(this, AppConstants.fREGISTER_ID)
        AppSettings.getInstance().setBooleanValue(this, AppConstants.IS_USER_GUEST, false)
        authenticationOperations()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", token) // Copy this exact token
            } else {
                Log.e("FCM Token", "Fetching token failed", task.exception)
            }
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
            showCaptchaDialog(this) {
                if (it) {
                    if (loginOption == PASSWORD_VERIFY) {
                        userValidateAndLogin()
                    } else {
                        sendOTP()
                    }
                } else {
                    UIToastMessage.show(this, "CAPTCHA verification Failed!!")
                }
            }

        }

        binding.forgotPassword.setOnClickListener {
            val intent2 = Intent(applicationContext, ForgetPassword::class.java)
            startActivity(intent2)
        }

        binding.registerTextView.setOnClickListener {
            val intent2 = Intent(applicationContext, Registration::class.java)
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
            showCaptchaDialog(this) { verified ->
                if (verified) {
                    // CAPTCHA was successfully verified
                    val userId = LocalCustom.generateRandom10DigitNumber()
                    AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, userId)
                    AppSettings.getInstance()
                        .setBooleanValue(this, AppConstants.IS_USER_GUEST, true)
                    startActivity(Intent(this, DashboardScreen::class.java))
                    // Do something here
                } else {
                    // CAPTCHA failed or dialog canceled
                    UIToastMessage.show(this, "CAPTCHA verification Failed!!")
                }
            }
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
            jsonObject.put("MobileNo", mobile.trim())
            jsonObject.put("SecurityKey", APIServices.SSO_KEY)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(
                this,
                AppEnvironment.FARMER.baseUrl,
                "",
                AppString(this).getkMSG_WAIT(),
                true
            )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getOTPRequest(requestBody)
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
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)
                jsonObject.put("MobileNo", mobileNo.trim { it <= ' ' })
                if (otp.isNotEmpty()) {
                    jsonObject.put("otp", otp)
                } else {
                    jsonObject.put("Password", userPass)
                }

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
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> =
                    apiRequest.getRefreshTokenLogin(requestBody)
                api.postRequest(responseCall, this, 4)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun callLoginAPI(strToken: String, otp: String = "") {
        if (mobileNo.isEmpty()) {
            binding.userIdEditText.error = resources.getString(R.string.lgn_register_phone_error)
            binding.userIdEditText.requestFocus()
        } else {

            val jsonObject = JSONObject()
            try {
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)
                jsonObject.put("MobileNo", mobileNo.trim { it <= ' ' })
                if (otp.isNotEmpty()) {
                    jsonObject.put("otp", otp)
                } else {
                    jsonObject.put("Password", userPass)
                }
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
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getUserLogin(requestBody)
                api.postRequest(responseCall, this, 2)
            } catch (e: JSONException) {
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
                        binding.passwordTextInput.visibility = View.VISIBLE
                        binding.passwordEditText.visibility = View.VISIBLE
                        binding.signInButton.visibility = View.VISIBLE
                        binding.signInButton.setText(R.string.login)
                        Toast.makeText(this, "Enter your password ", Toast.LENGTH_LONG).show()
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
            if (jSONObject != null) {
                if (jSONObject.optInt("status") == 200) {
                    val response: String = jSONObject.getString("response")
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    addVerificationDialog()
                } else if (jSONObject.optInt("status") == 201) {
                    Toast.makeText(this, R.string.mobile_otp_error_text, Toast.LENGTH_LONG).show()
                }
            }
        }
        if (i == 4) {
            if (jSONObject != null) {
                if (jSONObject.optInt("status") == 200) {
                    refreshToken = jSONObject.getString("refresh_token")
                    callLoginAPI(refreshToken, enteredOTP)
                } else {
                    val message: String = jSONObject.getString("response")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }
        if (i == 2) {
            if (jSONObject != null) {
                if (jSONObject.optInt("status") == 200) {
                    if (loginOption == OTP_VERIFY) {
                        val message: String = jSONObject.getString("response")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        farmerRegisteredID = jSONObject.getInt("FAAPRegistrationID")
                        AppSettings.getInstance()
                            .setIntValue(this, AppConstants.fREGISTER_ID, farmerRegisteredID)
                        val intent = Intent(this, DashboardScreen::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        val message: String = jSONObject.getString("response")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        farmerRegisteredID = jSONObject.getInt("FAAPRegistrationID")
                        AppSettings.getInstance()
                            .setIntValue(this, AppConstants.fREGISTER_ID, farmerRegisteredID)
                        val intent = Intent(this, DashboardScreen::class.java)
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
        val resendOTP = dialog.findViewById<Button>(R.id.resentOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)
        otpVerification(resendOTP)
        cancelButton.setOnClickListener { dialog.dismiss() }
        submitButton.setOnClickListener {

            enteredOTP = receiveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                receiveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                receiveOTPEditText.requestFocus()
            } else {
                farmerViewModel.compareOtp(this, mobile, enteredOTP)
                farmerViewModel.compareOtpResponse.observe(this) {
                    if (it != null) {
                        val jSONObject = JSONObject(it.toString())
                        if (jSONObject.optInt("status") == 200) {
                            mobileNo = binding.userIdEditText.text.toString()
                            callRefreshTokenAPI(mobileNo, userPass, enteredOTP)
                            dialog.dismiss()
                        } else {
                            UIToastMessage.show(this, getString(R.string.wrong_OTP))
                            dialog.dismiss()
                        }
                        dialog.dismiss()
                    }
                }
            }

        }

        resendOTP.setOnClickListener {
            dialog.dismiss()
            userValidateAndLogin()
        }
        dialog.show()
    }

    private fun otpVerification(resendOTP: Button) {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resendOTP.text =
                    resources.getString(R.string.Time) + ":" + millisUntilFinished / 1000
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                resendOTP.text = resources.getString(R.string.Resend_OTP)
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