package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import com.microsoft.clarity.Clarity
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class MahavistaarViewModel : ViewModel(){

    private val _responseUrlForChatBot = MutableLiveData<JsonObject>()
    val responseUrlForChatBot: LiveData<JsonObject> = _responseUrlForChatBot

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun requestUrlForChatBot(context: Context) {

        val username = AppSettings.getInstance().getValue( context, AppConstants.uName, AppConstants.uName)
        val mobileNumber = AppSettings.getInstance().getValue( context, AppConstants.uMobileNo, AppConstants.uMobileNo)

        viewModelScope.launch {
            try {
                ProgressHelper.showProgressDialog(context)
                val jsonObject = JSONObject().apply {
                    put("mobile", mobileNumber)
                    put("name", username)
                    put("role", "public")
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.VISTAAR.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                // Retrofit suspend call
                Clarity.sendCustomEvent("JWT_SESSION_START")
                val response = apiRequest.requestForChatBotURL(requestBody)
                ProgressHelper.disableProgressDialog()
                _responseUrlForChatBot.value = response

            }catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
                Clarity.sendCustomEvent("JWT_SESSION_STOPPED")
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