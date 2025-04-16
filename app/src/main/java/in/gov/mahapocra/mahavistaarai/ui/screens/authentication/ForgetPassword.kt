package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityForgetPasswordTempBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class ForgetPassword : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode {

    lateinit var binding: ActivityForgetPasswordTempBinding
    private lateinit var mob: String
    private var userPass: String = ""
    private lateinit var sentOTP: String
    private lateinit var dialog: Dialog
    private var loginOption: Int = 0
    private var farmerRegistrationId: Int = 0
    var languageToLoad: String = "mr"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppSettings.getLanguage(this@ForgetPassword).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        binding = ActivityForgetPasswordTempBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClick()
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
                jsonObject.put("MobileNo", mob.trim { it <= ' ' })
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)

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
                val responseCall: Call<JsonObject> = apiRequest.getOTPRequest(requestBody)
                api.postRequest(responseCall, this, 2)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun userLoginAPI(mobileNo: String, userPass: String) {
        if (mobileNo.isEmpty()) {
            binding.mobileEditText.error = resources.getString(R.string.lgn_register_phone_error)
            binding.mobileEditText.requestFocus()
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
                val responseCall: Call<JsonObject> = apiRequest.getUserLogin(requestBody)
                api.postRequest(responseCall, this, 3)
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
        dialogTitle.text = getString(R.string.enter_otp)
        val receiveOTPEditText = dialog.findViewById<EditText>(R.id.OptEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resentOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)

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

    private fun userVerification(enteredOTP: String) {
        if (enteredOTP == this.sentOTP) {
            Toast.makeText(this, "Thank you...", Toast.LENGTH_LONG).show()
            val intent = Intent(this, ConfirmPassword::class.java)
            intent.putExtra("MobileNo", mob)
            startActivity(intent)
            dialog.dismiss()
        } else {
            Toast.makeText(this, R.string.wrong_OTP, Toast.LENGTH_LONG).show()
        }
    }

    private fun userValidateAndLogin() {
        if (loginOption == 1) {
            mob = binding.mobileEditText.text.toString()
            userPass = ""
            userLoginAPI(mob, userPass)
        }
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (jSONObject != null) {
            if (jSONObject.optInt("status") == 200) {
                val response: String = jSONObject.getString("response")
                Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                sentOTP = jSONObject.optInt("otp").toString()
                addVerificationDialog()
            }
        }
        if (i == 3) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.getStatus()) {
                    if (loginOption == 1) {
                        val message: String = jSONObject.getString("Message")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        sentOTP = jSONObject.getString("OTP")
                        farmerRegistrationId = jSONObject.getInt("FAAPRegistrationID")
                        addVerificationDialog()
                    } else {
                        val message: String = jSONObject.getString("Message")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        sentOTP = jSONObject.getString("OTP")
                        farmerRegistrationId = jSONObject.getInt("FAAPRegistrationID")
                        AppSettings.getInstance()
                            .setIntValue(this, AppConstants.fREGISTER_ID, farmerRegistrationId)
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

    override fun onFailure(th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$th")
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$obj")
    }
}