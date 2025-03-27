package `in`.gov.mahapocra.farmerapppks.ui.screens.authentication

import android.app.Dialog
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
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityLoginScreenTempBinding
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class LoginScreen : AppCompatActivity(), ApiCallbackCode {

    private lateinit var binding: ActivityLoginScreenTempBinding
    private lateinit var refreshToken: String
    private lateinit var mobileNo: String
    private lateinit var dialog: Dialog
    private var userPass = ""
    private lateinit var sentOTP: String
    var languageToLoad = "hi"
    private var farmerRegisteredID: Int = 0
    private var loginOption: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppSettings.getLanguage(this@LoginScreen).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        binding = ActivityLoginScreenTempBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClick()
    }

    private fun onClick() {
        binding.signInButton.setOnClickListener {
            userValidateAndLogin()
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
            if (binding.passwordEditText.text.toString().length < 6) {
                Toast.makeText(
                    this,
                    "Please enter at least 6 character in password",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun userValidateAndLogin() {
        if (loginOption == 1) {
            mobileNo = binding.userIdEditText.text.toString()
            userPass = ""
            callRefreshTokenAPI(mobileNo, userPass)
        } else {
            mobileNo = binding.userIdEditText.text.toString()
            userPass = binding.passwordEditText.text.toString()
            if (userPass.isEmpty()) {
                binding.passwordEditText.error = resources.getString(R.string.password_error)
                binding.passwordEditText.requestFocus()
            } else {
                callRefreshTokenAPI(mobileNo, userPass)
            }
        }
    }

    private fun callRefreshTokenAPI(mobileNo: String, userPass: String) {
        if (mobileNo.isEmpty()) {
            binding.userIdEditText.error = resources.getString(R.string.lgn_register_phone_error)
            binding.userIdEditText.requestFocus()
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
                        APIServices.FARMER,
                        "",
                        AppString(this).getkMSG_WAIT(),
                        true
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getRefreshTokenLogin(requestBody)
                api.postRequest(responseCall, this, 4)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun callLoginAPI(strToken: String) {
        if (mobileNo.isEmpty()) {
            binding.userIdEditText.error = resources.getString(R.string.lgn_register_phone_error)
            binding.userIdEditText.requestFocus()
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
                        APIServices.FARMER,
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
                        loginOption = 0
                    }

                R.id.radioButtonOtp ->
                    if (checked) {
                        binding.passwordTextInput.visibility = View.GONE
                        binding.passwordEditText.visibility = View.GONE
                        binding.signInButton.visibility = View.VISIBLE
                        binding.signInButton.setText(R.string.sendOtp)
                        loginOption = 1
                    }
            }
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        th?.printStackTrace()
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {

        if (i == 4) {
            if (jSONObject != null) {
                if (jSONObject.optInt("status") == 200) {
                    refreshToken = jSONObject.getString("refresh_token")
                    callLoginAPI(refreshToken)
                } else {
                    val message: String = jSONObject.getString("response")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }
        if (i == 2) {
            if (jSONObject != null) {
                Log.d("TAGGER", "onResponse: $jSONObject")
                if (jSONObject.optInt("status") == 200) {
                    if (loginOption == 1) {
                        val message: String = jSONObject.getString("Message")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        sentOTP = jSONObject.getString("OTP")
                        farmerRegisteredID = jSONObject.getInt("FAAPRegistrationID")
                        addVerificationDialog(sentOTP)
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

    private fun addVerificationDialog(sentOTP: String) {
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

            val enteredOTP: String = receiveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                receiveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                receiveOTPEditText.requestFocus()
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

    private fun userVerification(enteredOTP: String) {
        if (enteredOTP == this.sentOTP) {
            Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show()
            AppSettings.getInstance()
                .setIntValue(this, AppConstants.fREGISTER_ID, farmerRegisteredID)
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            dialog.dismiss()
        } else {
            Toast.makeText(this, R.string.wrong_OTP, Toast.LENGTH_LONG).show()
        }
    }
}