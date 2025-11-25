package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiConstants
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityForgetPasswordTempBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.OtpRateLimiter.provideValidEncryptedString
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class ForgetPassword : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode {

    private lateinit var binding: ActivityForgetPasswordTempBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private lateinit var mob: String
    private var userPass: String = ""
    private lateinit var dialog: Dialog
    var languageToLoad: String = "mr"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ForgetPassword)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }

        switchLanguage(this, languageToLoad)
        binding = ActivityForgetPasswordTempBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        onClick()

        val farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        if (farmerId!=0){
            if (languageToLoad == "en") {
                binding.forgetHeadingText1.text = "Change"
                binding.forgetHeadingText2.text = "Password"
            } else {
                binding.forgetHeadingText1.text = "पासवर्ड"
                binding.forgetHeadingText2.text = "बदला"
            }
        }
    }

    private fun onClick() {
        binding.sendOTPButton.setOnClickListener {
            mob = binding.mobileEditText.text.toString()
            userPass = ""
            sendOTP()
        }

        binding.backPressIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun sendOTP() {
        mob = binding.mobileEditText.text.toString()
        if (mob.isEmpty()) {
            binding.mobileEditText.error = resources.getString(R.string.login_mob_err)
            binding.mobileEditText.requestFocus()
        } else if (!AppUtility.getInstance().isValidPhoneNumber(mob)) {
            binding.mobileEditText.error = resources.getString(R.string.login_mob_valid_err)
            binding.mobileEditText.requestFocus()
        } else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
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
                val responseCall: Call<JsonObject> =
                    apiRequest.getOTPRequest(mob.trim { it <= ' ' }, requestBody)
                api.postRequest(responseCall, this, 2)
            } catch (e: JSONException) {
                e.printStackTrace()
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
        dialogTitle.text = getString(R.string.enterOtp)
        val receiveOTPEditText = dialog.findViewById<EditText>(R.id.OptEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resendOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)

        cancelButton.setOnClickListener { dialog.dismiss() }
        submitButton.setOnClickListener {
            val timestamp = System.currentTimeMillis()
            val enteredOTP: String = receiveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                receiveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                receiveOTPEditText.requestFocus()
            } else {
                farmerViewModel.compareOtp(this, timestamp,binding.mobileEditText.text.toString(), enteredOTP)
            }
            farmerViewModel.compareOtpResponse.observe(this) {
                if (it != null) {
                    val calculatedResponse = provideValidEncryptedString(timestamp)
                    val jSONObject = JSONObject(it.toString())
                    val response = jSONObject.optString("response")
                    if (calculatedResponse != response) {
                        userVerification()
                    } else {
                        Toast.makeText(this, R.string.wrong_OTP, Toast.LENGTH_LONG).show()
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

    private fun userVerification() {
        Toast.makeText(this, "Thank you...", Toast.LENGTH_LONG).show()
        val intent = Intent(this, ConfirmPassword::class.java)
        intent.putExtra("MobileNo", mob)
        startActivity(intent)
        dialog.dismiss()
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (jSONObject != null) {
            if (jSONObject.optInt("status") == 200) {
                val response: String = jSONObject.getString("response")
                Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                addVerificationDialog()
            }
        }
    }


    override fun onFailure(th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$th")
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$obj")
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