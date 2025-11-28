package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiConstants
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class LoginViewModel : ViewModel() {

    private val _sendOtpToMobileResponse = MutableLiveData<JsonObject>()
    val sendOtpToMobileResponse: LiveData<JsonObject> = _sendOtpToMobileResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun sendOtpToMobile(context: Context, mobile: String) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)

                val response = api.sendOtpToMobile(mobile, requestBody)

                ProgressHelper.disableProgressDialog()

                if (response.isSuccessful) {
                    _sendOtpToMobileResponse.value = response.body()
                } else {
                    val errorBodyString = response.errorBody()?.string()

                    val json = JSONObject(errorBodyString ?: "{}")
                    val serverMessage = json.optString("response", "Too many attempts. Please try later.")

                    _error.value = serverMessage
                }

            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _error.value = message
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}