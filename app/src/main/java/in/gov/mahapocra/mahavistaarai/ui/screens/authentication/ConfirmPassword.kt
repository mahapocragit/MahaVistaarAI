package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityChangePwdTempBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.isStrongPassword
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class ConfirmPassword : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode {

    private lateinit var binding: ActivityChangePwdTempBinding
    private lateinit var newPwd: String
    private lateinit var confirmPwd: String
    private lateinit var userMobileNo: String
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ConfirmPassword)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }

        switchLanguage(this, languageToLoad)
        binding = ActivityChangePwdTempBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userMobileNo = intent.getStringExtra("MobileNo").toString()
        onClick()
    }

    private fun onClick() {
        binding.backPressIcon.setOnClickListener {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
        }
        binding.submitButton.setOnClickListener {
            requestDataValidation()
        }
    }

    private fun requestDataValidation() {
        newPwd = binding.newPasswordEditText.text.toString()
        confirmPwd = binding.confirmPasswordEditText.text.toString()
        if (newPwd.isEmpty()) {
            binding.newPasswordEditText.error = resources.getString(R.string.new_pwd_err)
            binding.newPasswordEditText.requestFocus()
        } else if (confirmPwd.isEmpty()) {
            binding.confirmPasswordEditText.error = resources.getString(R.string.new_pwd_err)
            binding.confirmPasswordEditText.requestFocus()
        } else if (newPwd != confirmPwd) {
            binding.confirmPasswordEditText.error =
                resources.getString(R.string.pass_equals_confirmpass)
            binding.confirmPasswordEditText.requestFocus()
        } else if (!isStrongPassword(binding.confirmPasswordEditText.text.toString())) {
            binding.passwordErrorTextView.visibility = View.VISIBLE
            UIToastMessage.show(this, resources.getString(R.string.weak_password))
        } else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)
                jsonObject.put("MobileNo", userMobileNo.trim { it <= ' ' })
                jsonObject.put("Password", newPwd)

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
                val responseCall: Call<JsonObject> = apiRequest.getNewPassword(requestBody)
                api.postRequest(responseCall, this, 1)
            } catch (e: JSONException) {
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
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.getStatus()) {
                    val notificationCountValue: String = jSONObject.getString("response")
                    Toast.makeText(this, notificationCountValue, Toast.LENGTH_LONG).show();
                    val intent = Intent(this, LoginScreen::class.java)
                    startActivity(intent)
                } else {
                    val notificationCountValue: String = jSONObject.getString("response")
                    Toast.makeText(this, notificationCountValue, Toast.LENGTH_LONG).show();
                }
            }
        }
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